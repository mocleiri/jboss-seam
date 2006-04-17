//$Id$
package org.jboss.seam.example.messages;

import static org.jboss.seam.ScopeType.SESSION;
import static javax.persistence.PersistenceContextType.EXTENDED;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Interceptors;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ejb.SeamInterceptor;

@Stateful
@Scope(SESSION)
@Name("messageManager")
public class MessageManagerBean implements Serializable, MessageManager
{

   @DataModel
   private List<Message> messageList;
   
   @DataModelSelection
   @Out(required=false)
   private Message message;
   
   @PersistenceContext(type=EXTENDED)
   private EntityManager em;
   
   @Factory("messageList")
   public void findMessages()
   {
      messageList = em.createQuery("from Message msg order by msg.datetime desc").getResultList();
   }
   
   public String select()
   {
      message.setRead(true);
      return "selected";
   }
   
   public String delete()
   {
      messageList.remove(message);
      em.remove(message);
      message=null;
      return "deleted";
   }
   
   @Remove @Destroy
   public void destroy() {}

}
