package org.jboss.seam.mail.ui;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * JSF component for rendering the body
 * Supports plain text, html bodies and setting an alternative
 * (text) part using an alternative facet
 *
 */
public class UIBody extends MailComponent
{
   
   public static final String HTML="html";
   public static final String PLAIN = "plain";
   
   private String type = HTML;
   
   @Override
   public void encodeChildren(FacesContext facesContext) throws IOException
   {
     try
     {
        String body = encode(facesContext);
        MimeMessage mimeMessage = findMimeMessage();
        if (PLAIN.equals(type)) 
        {
          mimeMessage.setText(body);
        }
        else if (HTML.equals(type)) 
        {
           UIComponent alternative = getFacet("alternative");
           if (alternative != null)
           {
              BodyPart text = new MimeBodyPart();
              text.setText(encode(facesContext,alternative));
              BodyPart html = new MimeBodyPart();
              html.setContent(body, "text/html");
              Multipart multipart = new MimeMultipart("alternative");
              multipart.addBodyPart(html);
              multipart.addBodyPart(text);
              mimeMessage.setContent(multipart);
           }
           else
           {   
              mimeMessage.setContent(body, "text/html");
           }
        }
      }
      catch (Exception e)
      {
        throw new FacesException(e);
      }
   }
   
   public void setType(String type)
   {
      this.type = type;
   }
   
   /**
    * The type of the body - plain or html
    */
   public String getType()
   {
      if (type == null) 
      {
         return getString("type");
      }
      return type;
   }

}
