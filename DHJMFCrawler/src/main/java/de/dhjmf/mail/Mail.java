package de.dhjmf.mail;

import javax.mail.Session;

import de.dhjmf.Utils;


public class Mail {
	
	public static void send(String filename, String filepath) {
	
		Session session = EmailUtil.authenticate();
		
		EmailUtil.sendEmail(session, Utils.EMAIL_TO, Utils.EMAIL_SUBJECT, Utils.EMAIL_BODY, filename, filepath, true);
		
	}

}