package de.dhjmf.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dhjmf.Utils;


/**
 * Abstract class for all digital humanities journal crawlers.
 * 
 * @author Andreas Lüschow
 * @since 2018-05-22
 */
public abstract class AbstractCrawler {
	
	protected final Pattern URL_PATTERN = Pattern.compile("\t(https?:[^\t]*)\t");
	protected int duplicatesCount;
	protected int newEntriesCount;
	protected File docfile;
	protected File tmpfile;
	protected File outfile;
	protected final File newlinks;
	protected String journal;
	
	
	/** 
	 * Specifies the pattern for the journal's identifier; usually a URL inside the docfile.
	 */
	protected AbstractCrawler() throws IOException {
		this.duplicatesCount = 0;
		this.newEntriesCount = 0;
		/* create folder for all journals if it does not already exist */
		new File(Utils.JOURNAL_FOLDER).mkdir(); 
	    this.newlinks = new File(Utils.NEWLINKS); 
	    /* delete old file and create new one */
	    this.newlinks.delete(); this.newlinks.createNewFile(); 
	}
    
	/** 
	 * Start crawling
	 */
	public abstract void start() throws FileNotFoundException, IOException;
	
	/** 
	 * Get the entries that were already crawled and added to Bibsonomy
	 * from the file that belongs to this crawler and put them in 
	 * a temporary file.
	 * 
	 * @param docfile Docfile for the specific journal
	 * @param tmpfile Temporary file for the specific journal
	 * @return A List with Strings that contains all identifiers from the docfile 
	 */
	protected List<String> getPreviousEntries(File docfile, File tmpfile) throws FileNotFoundException, IOException {
		List<String> preventries = new ArrayList<String>();
        FileWriter fwTmp = new FileWriter(tmpfile);
        BufferedWriter writerTmp = new BufferedWriter(fwTmp); 
        try {
            Scanner scanner = new Scanner(docfile);
            // read the file line by line...
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                // if pattern matches, get the link of the entry
                Matcher matcher = URL_PATTERN.matcher(line);
                if(matcher.find()) { 
                    preventries.add(matcher.group(1));
                }
                /* copy line to docfile */
                writerTmp.write(line + Utils.EOL);
            }
            scanner.close(); writerTmp.close();
        } catch(FileNotFoundException e) { 
        	Utils.LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
        }
        /* the docfile was copied to the temporary file so it can be deleted */
        docfile.delete();
        return preventries;
	}	
	
	/** 
	 * Combine new entries in the docfile with old entries from the temporary file.
	 * 
	 * @param docwriter A BufferedWriter for the journal's docfile
	 * @param tmp A temporary file 
	 * @return nothing 
	 */
	protected void combineFiles(BufferedWriter docwriter, File tmp) throws FileNotFoundException, IOException {
        try {
            Scanner scanner = new Scanner(tmp);
            // read the file line by line...
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                docwriter.write(line + Utils.EOL);
            }
            scanner.close(); docwriter.close();
        } catch(FileNotFoundException e) { 
        	Utils.LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
        }
        /* delete the temporary file */
        tmp.delete();
	}
}