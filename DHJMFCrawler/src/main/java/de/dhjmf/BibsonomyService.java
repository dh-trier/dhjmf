package de.dhjmf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;

import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.User;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.model.util.GroupUtils;
import org.bibsonomy.rest.client.*;
import org.bibsonomy.bibtex.parser.*;

import de.dhjmf.Utils;

/**
 * Class for uploading records to the BibSonomy publication sharing system
 *
 * @author Andreas Lüschow
 * @since 2018-09-14
 */
public class BibsonomyService {
	
	private static int success = 0;  // count successfully uploaded items
    private static int noSuccess = 0;  // count items that couldn't be uploaded
    private static List<String> successURLs = new ArrayList<String>();
    
    /**
     * Method for starting the scraping and upload process.
     * 
     * @throws FileNotFoundException
     * @throws IOException
     */
	public void upload() throws FileNotFoundException, IOException {
		/* create basic connection with the Bibsonomy API */
		final RestLogicFactory rlf = new RestLogicFactory();
		final LogicInterface logic = rlf.getLogicAccess(Utils.USERNAME, Utils.API_KEY);
		readInputfile(logic);
	}


	/**
	 * Method for scraping articles on journal websites and writing them to a BibTeX file.
	 * 
	 * @param logic
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static void readInputfile(LogicInterface logic) throws FileNotFoundException, IOException  { 
		/* create file with new articles */
		new File(Utils.BIBTEX_FOLDER).mkdir();
		File bibtex = new File(Utils.BIBTEX_FILE);
		FileWriter fw = new FileWriter(bibtex);
		BufferedWriter writerBibtex = new BufferedWriter(fw);
		/* read file with new links */
        FileReader reader = new FileReader(Utils.NEWLINKS);
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
                
        /* start scraping */
        while(line != null) {
        	UrlListScraper urlListScraper = new UrlListScraper(); // Initialize UrlListScraper Object
        	System.out.println("URL aus RSS-Feed: " + line);
        	/* call the Bibsonomy scraper */
        	try {
        		String url = "";
        		if (line.contains("dx.doi")) {
	        		URL urlobj = new URL(line);
	        		URLConnection conn = urlobj.openConnection();
        			url = conn.getHeaderField("Link").split(";")[0].replace("<", "").replace(">", ""); // get original URL if dx.doi.org was used
        			System.out.println("Verwendete (Original-)URL: " + url);
        		} else {
        			url = line;  // use URL from file in all other cases
        		}
        		
        		// Create Post and get BibTex
        		String responseBody = urlListScraper.writePublications(url);
        	    Post<BibTex> post = new Post<BibTex>();
        	    PostBibTeXParser pbp = new PostBibTeXParser();
        	    post = pbp.parseBibTeXPost(responseBody);
        	    
        	    writerBibtex.write(responseBody + Utils.EOL + Utils.EOL);  // create a blank line between two entries
        	    uploadPost(logic, post, line);  // upload scraped post
        	} catch (NoSuchElementException e) {
        		String errMsg = line + " does not point to a scrapable URL, please check this! Reason: " + e.toString();
        		Utils.LOGGER.log(Level.SEVERE, errMsg);
        		noSuccess++;
        	} catch (Exception e) {
        		String errMsg = "Unknow Error: " + e + " für URL " + line;
        		Utils.LOGGER.log(Level.SEVERE, errMsg);
        		noSuccess++;
        	}
        	
        	/* get next line in file */
        	line = br.readLine();
        }
        writerBibtex.close();
        br.close();
        /* Log successfully uploaded URLs */
        if(successURLs.size() > 0) {
        	Utils.LOGGER.log(Level.INFO, "Successfully uploaded:");
            for (String successURL : successURLs) {
            	Utils.LOGGER.log(Level.FINEST, Utils.DELIMITER + successURL);
            	success++;
            }
	        Utils.LOGGER.log(Level.INFO, "Successful: " + Integer.toString(success) + " || Unsuccessful: " + Integer.toString(noSuccess));
	        Utils.LOGGER.log(Level.INFO, "In total: " + Integer.toString(success + noSuccess));
        } else {
        	Utils.LOGGER.log(Level.INFO, "No new datasets available!");
        }
	}
	
	/**
	 * Method for uploading posts via the BibSonomy API.
	 * 
	 * @param logic
	 * @param post A new BibSonomy post
	 * @param line
	 */
	private static void uploadPost(LogicInterface logic, Post<BibTex> post, String line) {
		
		/* make post private or public */
		if (Utils.VISIBILITY.equals("private")) {
			post.setGroups(Collections.singleton(GroupUtils.buildPrivateGroup()));
		} else if (Utils.VISIBILITY.equals("public")) {
			post.setGroups(Collections.singleton(GroupUtils.buildPublicGroup()));
		} else {
			String errMsg = "Invalid visibility parameter, must be either 'private' or 'public'!";
			Utils.LOGGER.log(Level.SEVERE, errMsg);
		}
		/* set user */
		post.setUser(new User(Utils.USERNAME));
		/* set default tags */
		post.addTag(Utils.DEFAULT_TAGS);
		
		/* create new BibTeX publication */
		final BibTex publication = new BibTex();

		/* get values from post */ 
		publication.setTitle(post.getResource().getTitle());
		publication.setAuthor(post.getResource().getAuthor());
		publication.setYear(post.getResource().getYear());
		publication.setEntrytype(post.getResource().getEntrytype());
		publication.setBibtexKey(post.getResource().getBibtexKey());
		publication.setAbstract(post.getResource().getAbstract());
		publication.setJournal(post.getResource().getJournal());
		publication.setUrl(post.getResource().getUrl());
		// TODO: Add DOI
		
		/* add BibTex elements to the Bibsonomy post */
		post.setResource(publication);

		try {
			logic.createPosts(Collections.<Post<? extends Resource>>singletonList(post));
    	    successURLs.add(line);
		} catch (Exception e) {
			String errMsg = post.getResource().getUrl() + " couldn't be uploaded to Bibsonomy! Reason: " + e.toString();
			noSuccess++;
			Utils.LOGGER.log(Level.SEVERE, errMsg);
		}
		
	}

}