package de.dhjmf.mail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.mail.Session;

import de.dhjmf.Utils;
import de.dhjmf.zip.Packager;

/**
 * Superior class for mail sending.
 * 
 * @author Andreas Lüschow
 * @since 2018-05-22
 */
public class Mail {
	
	/**
	 * Method for sending an email.
	 * 
	 * @param filename Name of the attachment file.
	 * @param filepath Full path to the  attachment file.
	 */
	public static void send(String filename, String filepath) {
		Session session = EmailUtil.authenticate();
		EmailUtil.sendEmail(session, Utils.EMAIL_TO, Utils.EMAIL_SUBJECT, Utils.EMAIL_BODY, filename, filepath, true);
		
	}
	
	/**
	 * Method for packing documentation and log files to a zip archive.
	 */
	public static void packFiles() {
		List<File> sources = new ArrayList<File>();
		sources.add(new File(Utils.BIBTEX_FOLDER));
		sources.add(new File(Utils.LOGFOLDER));
		sources.add(new File(Utils.JOURNAL_FOLDER));
		try {
			Packager.packZip(new File(Utils.ZIPNAME), sources);
		} catch(IOException e) {
			Utils.LOGGER.log(Level.SEVERE, "eMail was not sent due to packaging/zipping problems!");
			System.out.println("eMail not sent due to packaging/zipping problems: " + e.toString());
		}
	}

}