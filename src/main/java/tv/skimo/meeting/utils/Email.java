package tv.skimo.meeting.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {
	
	public static boolean send(String to, String text) 
	{    
	      String from = "no-reply@skimo.tv";
	      Properties properties = System.getProperties();
	      properties.setProperty("mail.user", "vasu@skimo.tv");
	      properties.setProperty("mail.password", "@rcher15$");
	      properties.put("mail.smtp.host", "smtp.gmail.com");
	      properties.put("mail.smtp.port", "465");
	      properties.put("mail.smtp.ssl.enable", "true");
	      properties.put("mail.smtp.auth", "true");

	      Session session = Session.getInstance(properties, new javax.mail.Authenticator() 
	      {
	            protected PasswordAuthentication getPasswordAuthentication() 
	            {
	                return new PasswordAuthentication("vasu@skimo.tv", "@rcher15$");
	            }

	        });

	      try 
	      {
	         MimeMessage message = new MimeMessage(session);

	         message.setFrom(new InternetAddress(from, "No Reply"));
	    	 message.setReplyTo(InternetAddress.parse("noreply@gmail.com", false));
	         message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

	         message.setSubject("One time passcode for registering Skimo Smart Meeting Recorder");
	         message.setText("Here is the one time passcode " + text);
	         Transport.send(message);
	      } 
	      catch (Exception mex) 
	      {
	    	  mex.printStackTrace();
	    	  return false;
	      }
	      return true;
	}
	
	public static void main(String[] args)
	{
		Email.send("vasusrini@yahoo.com", "asdfasfasdfAdadfasdf");
		
	}

}
