package main.GoogleAPI.util;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class GmailUtil {

	public static MimeMessage createSimpleEmail(String to, String from, String subject, String bodyText) throws MessagingException {
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		MimeMessage email = new MimeMessage(session);
		
		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
		
		return email;
	}
	
	public MimeMessage createEmailWithAttachment(String to, String from, String subject, String bodyText, File file) throws MessagingException, IOException {
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
			
		MimeMessage email = new MimeMessage(session);
			
		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject);
			
		Multipart multipart = new MimeMultipart();

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(bodyText, "text/plain");
		multipart.addBodyPart(mimeBodyPart);
			
		DataSource source = new FileDataSource(file);	
		mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setDataHandler(new DataHandler(source));
		mimeBodyPart.setFileName(file.getName());
		multipart.addBodyPart(mimeBodyPart);
				
		email.setContent(multipart);
			
		return email;
	}
}
