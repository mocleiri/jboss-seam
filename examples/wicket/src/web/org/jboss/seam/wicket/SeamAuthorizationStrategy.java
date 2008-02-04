package org.jboss.seam.wicket;

import org.apache.wicket.Component;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authorization.Action;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.NotLoggedInException;
import org.jboss.seam.util.Strings;

public class SeamAuthorizationStrategy implements IAuthorizationStrategy
{
   
   private LogProvider log = Logging.getLogProvider(SeamAuthorizationStrategy.class);
   private Class loginPage;
   
   public SeamAuthorizationStrategy()
   {
      this(null);
   }

   public SeamAuthorizationStrategy(final Class loginPage)
   {
      this.loginPage = loginPage;
   }
   
   // TODO Use permission schemes for this?
   public boolean isActionAuthorized(Component component, Action action)
   {
      return isInstantiationAuthorized(component.getClass());
   }

   public boolean isInstantiationAuthorized(Class componentClass)
   {
      Restrict restrict = (Restrict) componentClass.getAnnotation(Restrict.class);
      if ( restrict != null && Identity.isSecurityEnabled() )
      {
         String expr = !Strings.isEmpty( restrict.value() ) ? restrict.value() : "#{identity.loggedIn}";
         try
         {
            Identity.instance().checkRestriction(expr);
         }
         catch (NotLoggedInException e) 
         {
            log.error("Unauthorized access to " + componentClass.getName() + ", user not logged in", e);
            return handleException(componentClass);
         }
         catch (org.jboss.seam.security.AuthorizationException e) 
         {
            log.error("Unauthorized access to " + componentClass.getName(), e);
            return handleException(componentClass);
         }
      }
      return true;
   }

   private boolean handleException(Class componentClass)
   {
      if (Page.class.isAssignableFrom(componentClass))
      {
         // Redirect to page to let the user sign in
         throw new RestartResponseAtInterceptPageException(loginPage);
      }
      return false;
   }
   
   
}
