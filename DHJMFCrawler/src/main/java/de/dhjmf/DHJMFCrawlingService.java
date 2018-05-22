package de.dhjmf;

import java.util.logging.Level;

import de.dhjmf.BibsonomyService;
import de.dhjmf.Utils;
import de.dhjmf.crawler.*;
import de.dhjmf.mail.Mail;

/**
 * Main class for starting the DHJMF crawling service.
 *
 * @author Andreas Lüschow
 * @since 2018-05-22
 */
public final class DHJMFCrawlingService {
	
	
	public static void main(String[] args) {
		try {
			Utils.LOGGER.log(Level.INFO, "##### " + Utils.TODAY_LONG + " #####");
			Utils.LOGGER.log(Level.INFO, "DHJMFCrawlingService started." + Utils.EOL);
			start();
		} catch (Exception e) {
			Utils.LOGGER.log(Level.SEVERE, e.toString());
		}
	}
	
	private static final void start() throws Exception {
				
		/*
		 * Add new crawlers here
		 */
		final ZfdGCrawler zfdg = new ZfdGCrawler();
		final UmanisticaDigitaleCrawler umanisticaDigitale = new UmanisticaDigitaleCrawler();
		/* ... */		
		
		/* RSS feeds */
		Utils.LOGGER.log(Level.INFO, "Starting to crawl RSS feeds ...");
		zfdg.start();
		umanisticaDigitale.start();
		/* ... */
		
		Utils.LOGGER.log(Level.INFO, "Crawling of RSS feeds completed!" + Utils.EOL);
		
		/* ------------------------- */
		
		final BibsonomyService bibservice = new BibsonomyService();
		
		/* Upload to Bibsonomy*/
		Utils.LOGGER.log(Level.INFO, "Starting upload to Bibsonomy ...");
		bibservice.upload();
		Utils.LOGGER.log(Level.INFO, "Upload completed!" + Utils.EOL);
		
		/* ------------------------- */
		
		Mail.packFiles();
		// Mail.send(Utils.LOGFILE_MAIL, Utils.LOGFILE);
		Mail.send(Utils.ZIPNAME_MAIL, Utils.ZIPNAME);
		
		/* ------------------------- */
		
		Utils.LOGGER.log(Level.INFO, "DHJMFCrawlingService stopped.");
	}


}