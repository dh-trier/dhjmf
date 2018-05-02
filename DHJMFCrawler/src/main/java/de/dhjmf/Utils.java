package de.dhjmf;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.dhjmf.logging.DHJMFFormatter;


@SuppressWarnings("serial")
public class Utils implements Serializable {
    
	public static String TODAY = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
	public static String TODAY_LONG = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
	
	public static Logger LOGGER;
    public static String LOGFILE;
    public static String LOGFILE_MAIL;
    public static String LOGFOLDER;
    
    public static String EMAIL_FROM;
    public static String EMAIL_PASSWORD;
    public static String EMAIL_TO;
    public static String EMAIL_SMTP_HOST;
    public static String EMAIL_TLS_PORT;
    public static String EMAIL_ENABLE_AUTHENTICATION;
    public static String EMAIL_ENABLE_STARTTLS;
    public static String EMAIL_SUBJECT;
    public static String EMAIL_BODY;

    public static String USERNAME;
    public static String API_KEY;
    public static String VISIBILITY;
    public static String BIBSCRAPER_BASE_URL;
    public static String BIBSCRAPER_FORMAT;
    public static String CHARSET;
    public static String NEWLINKS;
    public static String BIBTEX_FOLDER;
    public static String BIBTEX_FILE;
    public static String DEFAULT_TAGS;
    public static Boolean VERBOSE;
    
    public static String WORKDIR;
    
    public static String REMOVE_NAMESPACE;
    
    public static String JOURNAL_FOLDER;
    
    public static String ZFDG_NAME;
    public static String ZFDG_FOLDER;
    public static String ZFDG_NEWLINKS_FOLDER;
    public static String ZFDG_RSS;
    public static String ZFDG_DOC;
    public static String ZFDG_TMP;
    public static String ZFDG_OUT;
    
    public static String UMANISTICADIGITALE_NAME;
    public static String UMANISTICADIGITALE_FOLDER;
    public static String UMANISTICADIGITALE_NEWLINKS_FOLDER;
    public static String UMANISTICADIGITALE_RSS;
    public static String UMANISTICADIGITALE_DOC;
    public static String UMANISTICADIGITALE_TMP;
    public static String UMANISTICADIGITALE_OUT;
    
    public static String EOL;
    public static String DELIMITER;

    public static ResourceBundle config;


    static {
        init();
    }

    public static void init() {
  
    	String configBaseName = "config";
        config = ResourceBundle.getBundle(configBaseName);

        WORKDIR = config.getString("dhjmf.common.workdir");
        
        EMAIL_FROM = config.getString("dhjmf.mail.address.from");
        EMAIL_PASSWORD = config.getString("dhjmf.mail.address.password");
        EMAIL_TO = config.getString("dhjmf.mail.address.to");
        EMAIL_SMTP_HOST = config.getString("dhjmf.mail.smtphost");
        EMAIL_TLS_PORT = config.getString("dhjmf.mail.tlsport");
        EMAIL_ENABLE_AUTHENTICATION = config.getString("dhjmf.mail.authentication");
        EMAIL_ENABLE_STARTTLS = config.getString("dhjmf.mail.starttls");
        
        try {
        	LOGFOLDER = WORKDIR + config.getString("dhjmf.logger.folder");
        	new File(LOGFOLDER).mkdir();
            LOGGER = Logger.getLogger(Utils.class.getName());
            LOGFILE = LOGFOLDER + "/" + config.getString("dhjmf.logger.file") + "_" + TODAY + ".txt";
            LOGFILE_MAIL = config.getString("dhjmf.logger.file") + "_" + TODAY + ".txt";
	        // This block configure the logger with handler and formatter  
	        FileHandler fh = new FileHandler(LOGFILE);  
	        // SimpleFormatter formatter = new SimpleFormatter();  
	        DHJMFFormatter formatter = new DHJMFFormatter();
	        fh.setFormatter(formatter);
	        LOGGER.addHandler(fh);
        } catch (SecurityException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        switch(config.getString("dhjmf.logger.level")) {
        	case "info":
		    	LOGGER.setLevel(Level.INFO);
		    	break;
        	case "config":
        		LOGGER.setLevel(Level.CONFIG);
		    	break;
        	case "finest":
        		LOGGER.setLevel(Level.FINEST);
		    	break;
        }
        
        USERNAME = config.getString("bibsonomy.api.user");
        API_KEY = config.getString("bibsonomy.api.key");

        VISIBILITY = config.getString("bibsonomy.post.visibility");

        BIBSCRAPER_BASE_URL = config.getString("bibsonomy.url.bibscraper");
        BIBSCRAPER_FORMAT = config.getString("bibsonomy.url.format");
        CHARSET = config.getString("dhjmf.common.charset");
        
        EMAIL_SUBJECT = config.getString("dhjmf.mail.subject");
        EMAIL_BODY = config.getString("dhjmf.mail.body");
        
        // TODO: Mehr als einen Default-Tag erlauben
        DEFAULT_TAGS = config.getString("bibsonomy.post.tags");
                
        EOL = config.getString("dhjmf.files.eol");
        DELIMITER = config.getString("dhjmf.files.delimiter");
        
        NEWLINKS = WORKDIR + config.getString("dhjmf.files.newlinks");
        BIBTEX_FOLDER = WORKDIR + config.getString("dhjmf.files.bibtex_folder") + "/";
        BIBTEX_FILE = BIBTEX_FOLDER + config.getString("dhjmf.files.bibtex_snippets") + "_" + TODAY + ".bib";
        
        VERBOSE = Boolean.parseBoolean(config.getString("dhjmf.debug.verbose"));
        
        REMOVE_NAMESPACE = config.getString("dhjmf.common.remove_namespace");
        
        JOURNAL_FOLDER = WORKDIR + config.getString("dhjmf.files.journal_folder") + "/";
        
        ZFDG_NAME = config.getString("dhjmf.zfdg.name");
        ZFDG_FOLDER = JOURNAL_FOLDER + ZFDG_NAME + "/";
        ZFDG_NEWLINKS_FOLDER = ZFDG_FOLDER + config.getString("dhjmf.common.folder.newlinks") + "/";
        ZFDG_RSS = config.getString("dhjmf.zfdg.feedURL");
        ZFDG_DOC = ZFDG_FOLDER + config.getString("dhjmf.zfdg.files.documentation");
        ZFDG_TMP = ZFDG_FOLDER + config.getString("dhjmf.zfdg.files.documentation_tmp");
        ZFDG_OUT = ZFDG_NEWLINKS_FOLDER + config.getString("dhjmf.zfdg.files.newlinks") + "_" + TODAY + ".txt";
        
        UMANISTICADIGITALE_NAME = config.getString("dhjmf.umanisticadigitale.name");
        UMANISTICADIGITALE_FOLDER = JOURNAL_FOLDER + UMANISTICADIGITALE_NAME + "/";
        UMANISTICADIGITALE_NEWLINKS_FOLDER = UMANISTICADIGITALE_FOLDER + config.getString("dhjmf.common.folder.newlinks") + "/";
        UMANISTICADIGITALE_RSS = config.getString("dhjmf.umanisticadigitale.feedURL");
        UMANISTICADIGITALE_DOC = UMANISTICADIGITALE_FOLDER + config.getString("dhjmf.umanisticadigitale.files.documentation");
        UMANISTICADIGITALE_TMP = UMANISTICADIGITALE_FOLDER + config.getString("dhjmf.umanisticadigitale.files.documentation_tmp");
        UMANISTICADIGITALE_OUT = UMANISTICADIGITALE_NEWLINKS_FOLDER + config.getString("dhjmf.umanisticadigitale.files.newlinks") + "_" + TODAY + ".txt";
        

    }
}