package de.dhjmf.mail;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import de.dhjmf.Utils;


public class EmailUtil {

	protected static Session authenticate() {
		Properties props = new Properties();
		props.put("mail.smtp.host", Utils.EMAIL_SMTP_HOST);
		props.put("mail.smtp.port", Utils.EMAIL_TLS_PORT);
		props.put("mail.smtp.auth", Utils.EMAIL_ENABLE_AUTHENTICATION);
		props.put("mail.smtp.starttls.enable", Utils.EMAIL_ENABLE_STARTTLS);
		
	    // create Authenticator object to pass in Session.getInstance argument
		Authenticator auth = new Authenticator() {
			//override the getPasswordAuthentication method
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Utils.EMAIL_FROM, Utils.EMAIL_PASSWORD);
			}
		};
		return Session.getInstance(props, auth);
	}
	
	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	protected static void sendEmail(Session session, String toEmail, String subject, String body, String filename, String filepath, Boolean attachment) {
		try
	    {
	      MimeMessage msg = new MimeMessage(session);
	      //set message headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(new InternetAddress(Utils.EMAIL_FROM, "NoReply"));
	      msg.setReplyTo(InternetAddress.parse(Utils.EMAIL_FROM, false));

	      msg.setSubject(subject, Utils.CHARSET);
	      msg.setSentDate(new Date());
	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
	      
	      if(!attachment) {
		      msg.setText(body, Utils.CHARSET);
	      } else {
	         // Create the message body part
	         BodyPart messageBodyPart = new MimeBodyPart();

	         // Fill the message
	         messageBodyPart.setText(body);
	         
	         // Create a multipart message for attachment
	         Multipart multipart = new MimeMultipart();

	         // Set text message part
	         multipart.addBodyPart(messageBodyPart);

	         // Second part is attachment
	         messageBodyPart = new MimeBodyPart();
	         DataSource source = new FileDataSource(filepath);
	         messageBodyPart.setDataHandler(new DataHandler(source));
	         messageBodyPart.setFileName(filename);
	         multipart.addBodyPart(messageBodyPart);

	         // Send the complete message parts
	         msg.setContent(multipart);
	      }
	            
    	  Transport.send(msg);  

    	  Utils.LOGGER.log(Level.INFO, "eMail sent successfully!");
	    }
	    catch (MessagingException e) {
	    	Utils.LOGGER.log(Level.SEVERE, "eMail was not sent! Reason: " + e.toString());
	    }
	    catch (UnsupportedEncodingException e) {
	    	Utils.LOGGER.log(Level.SEVERE, "eMail was not sent! Reason: " + e.toString());
	    }
	    catch (Exception e) {
	    	Utils.LOGGER.log(Level.SEVERE, "eMail was not sent! Reason: " + e.toString());
		}
	}

}