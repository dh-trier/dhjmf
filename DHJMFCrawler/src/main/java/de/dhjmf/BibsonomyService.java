package de.dhjmf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
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

public class BibsonomyService {
	
	private static int success = 0;
    private static int noSuccess = 0;
    private static List<String> successURLs = new ArrayList<String>();
    
	public void upload() throws FileNotFoundException, IOException {

		
		/* create basic connection with the Bibsonomy API */
		final RestLogicFactory rlf = new RestLogicFactory();
		final LogicInterface logic = rlf.getLogicAccess(Utils.USERNAME, Utils.API_KEY);
		
		readInputfile(logic);

	}


	private static void readInputfile(LogicInterface logic) throws FileNotFoundException, IOException  { 
		/* read file with new articles */
		new File(Utils.BIBTEX_FOLDER).mkdir();
		File bibtex = new File(Utils.BIBTEX_FILE);
		FileWriter fw = new FileWriter(bibtex);
		BufferedWriter writerBibtex = new BufferedWriter(fw);
		
        FileReader reader = new FileReader(Utils.NEWLINKS);
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        
        
 
        while(line != null) {
        	String url = Utils.BIBSCRAPER_BASE_URL + line + Utils.BIBSCRAPER_FORMAT;
        	System.out.println("URL: " + url);
        	/* call the Bibsonomy scraping service */
        	try {
        		InputStream response = new URL(url).openStream();
            	try (Scanner scanner = new Scanner(response)) {
            	    String responseBody = scanner.useDelimiter("\\A").next();
            	    
            	    Post<BibTex> post = new Post<BibTex>();
            	    PostBibTeXParser pbp = new PostBibTeXParser();
            	    post = pbp.parseBibTeXPost(responseBody);
            	    
            	    // System.out.println(responseBody);
            	    writerBibtex.write(responseBody + Utils.EOL + Utils.EOL);  // create a blank line between two entries
            	    uploadPost(logic, post, line);
            	}
        	} catch (NoSuchElementException e) {
        		String errMsg = line + " does not point to a scrapable URL, please check this! Reason: " + e.toString();
        		Utils.LOGGER.log(Level.SEVERE, errMsg);
        		noSuccess++;
        	} catch (Exception e) {
        		String errMsg = "Unknow Error: " + e + "";
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
        	Utils.LOGGER.log(Level.INFO, "Succesfully uploaded:");
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
	
	/* upload post via API */
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
		// publication.setAuthor(PersonNameUtils.discoverPersonNamesIgnoreExceptions("Mustermann, Max"));
		publication.setAuthor(post.getResource().getAuthor());
		publication.setYear(post.getResource().getYear());
		publication.setEntrytype(post.getResource().getEntrytype());
		publication.setBibtexKey(post.getResource().getBibtexKey());
		publication.setAbstract(post.getResource().getAbstract());
		publication.setJournal(post.getResource().getJournal());
		publication.setUrl(post.getResource().getUrl());
		// TODO: DOI hinzufügen!
		
		/* add BibTex elements to the Bibsonomy post */
		post.setResource(publication);

		try {
			// TODO: Exception ,wenn Eintrag bereits in der Bibsonomy-Collection enthalten ist!
			logic.createPosts(Collections.<Post<? extends Resource>>singletonList(post));
    	    successURLs.add(line);
		} catch (Exception e) {
			String errMsg = post.getResource().getUrl() + " couldn't be uploaded to Bibsonomy! Reason: " + e.toString();
			noSuccess++;
			Utils.LOGGER.log(Level.SEVERE, errMsg);
		}
	}
	
	/*
	// Get list of posts
	private static void getPosts(LogicInterface logic) {
		List<Post<BibTex>> publications = logic.getPosts(BibTex.class, GroupingEntity.USER, Utils.USERNAME, null, null, "", null, null, null, null, null, 0, 100);
		for (Post<BibTex> pub: publications) {
			System.out.println(pub);
		}
	}
	*/

}