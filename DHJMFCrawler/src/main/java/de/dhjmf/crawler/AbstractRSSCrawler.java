package de.dhjmf.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import de.dhjmf.Utils;
import de.dhjmf.rss.*;


/**
 * Abstract class for all digital humanities journal crawlers that use RSS feeds
 * 
 * @author Andreas Lüschow
 * @since 2018-05-22
 */
public abstract class AbstractRSSCrawler extends AbstractCrawler {
	
	protected final RSSFeedParser parser;  
	protected Feed feed;
	
	/** 
	 * Initialize the RSSCrawler. Parameters are specified in a separate Crawler class for every journal.
	 */
	protected AbstractRSSCrawler(String journalName, String folder, String newlinksFolder, String feedURL, String doc, String tmp, String out) throws IOException {
		this.journal = journalName;
		this.parser = new RSSFeedParser(feedURL);  
		this.feed = parser.readFeed();
		/* create journal folder and files if not existent */
		new File(folder).mkdir(); new File(newlinksFolder).mkdir();
	    this.docfile = new File(doc); this.docfile.createNewFile();
	    this.tmpfile = new File(tmp); this.tmpfile.createNewFile();
	    this.outfile = new File(out); this.outfile.createNewFile();
	}

	@Override
	public void start() throws FileNotFoundException, IOException {
				
        /* get an Array with the previous entries and move all previous entries to a temporary file */
        List<String> prev = getPreviousEntries(docfile, tmpfile);
        
        /* write new entries from the RSS feed to the docfile */
        try {
            FileWriter fw = new FileWriter(docfile);
            BufferedWriter writerDocfile = new BufferedWriter(fw);
            FileWriter fw2 = new FileWriter(outfile);
            BufferedWriter writerOutfile = new BufferedWriter(fw2);
            FileWriter fw3 = new FileWriter(newlinks, true);  // true == appends to the file's content
            BufferedWriter writerNewlinks = new BufferedWriter(fw3);
           
        	writerDocfile.write("##### " + Utils.TODAY + " #####\n");  // use as a separator for single days in the documentation file
            for (FeedMessage message : feed.getMessages()) {
                // System.out.println(message);
            	if (!prev.contains(message.getLink())) {
            		String msg = message.toString();
            		while (msg.contains("  ")) {
            			msg = msg.replaceAll("\\s\\s", " "); // replaces unnecessary whitespaces from the RSS entries
            		} 
            		writerDocfile.write(msg); writerOutfile.write(message.getLink() + Utils.EOL); 
            		writerNewlinks.write(message.getLink() + Utils.EOL);
            		newEntriesCount++;
            	} else {
            		// System.out.println("Der Eintrag mit dem Link " + message.getLink() + " ist bereits verarbeitet worden!");
            		duplicatesCount++;
            	}
            	
            }
            // append content from the temporary file to the new docfile
            combineFiles(writerDocfile, tmpfile);
            writerDocfile.close(); writerOutfile.close(); writerNewlinks.close();
        } catch (IOException e) {
        	Utils.LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
        }
                
        Utils.LOGGER.log(Level.INFO, "Journal: " + this.journal + Utils.EOL + Utils.DELIMITER + 
        		"Datasets in RSS feed: " + Integer.toString(duplicatesCount + newEntriesCount) + Utils.EOL + Utils.DELIMITER + 
        		"Alread crawled: " + Integer.toString(duplicatesCount) + Utils.EOL + Utils.DELIMITER + 
        		"New datasets: " + Integer.toString(newEntriesCount));
        // TODO: Make sure, that there are no entries outside the RSS feed that were forgotten!
        if (prev.size() == 0) {
        	String errMsg = "All items from the " + this.journal + " RSS feed were added, maybe some entries are missing!";
        	Utils.LOGGER.log(Level.WARNING, errMsg);
        }
	}
}