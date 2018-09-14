package de.dhjmf;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.bibsonomy.bibtex.parser.SimpleBibTeXParser;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.util.BibTexUtils;
import org.bibsonomy.scraper.KDEUrlCompositeScraper;
import org.bibsonomy.scraper.Scraper;
import org.bibsonomy.scraper.ScrapingContext;
import org.bibsonomy.scraper.exceptions.ScrapingException;

import bibtex.parser.ParseException;

/**
 * Reads a list of URLs and tries to gather publication metadata from them using
 * BibSonomy's screen scrapers.
 * 
 * Taken from https://bitbucket.org/bibsonomy/bibsonomy-tools/src/default/src/main/java/org/bibsonomy/tools/UrlListScraper.java?fileviewer=file-view-default
 * and adapted for DHJMFCrawlingService by Andreas Lüschow
 */
public class UrlListScraper {

	private final Scraper scraper;
	private final SimpleBibTeXParser parser;

	public UrlListScraper() {
		this.scraper = new KDEUrlCompositeScraper();
		this.parser = new SimpleBibTeXParser();
	}


	/**
	 * Each supplied argument is regarded as a text file which contains one URL
	 * per line. If no arguments are given, URLs are read from standard input,
	 * one per line.
	 * 
	 * Results are written in BibTeX format to standard output.
	 * 
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws ScrapingException 
	 */
	public static void main(final String[] args) throws IOException {
		final UrlListScraper urlListScraper = new UrlListScraper();
		if (args.length == 0) {
			urlListScraper.writePublications(System.in, System.out);
		} else {
			for (final String arg : args) {
				urlListScraper.writePublications(new FileInputStream(arg), System.out);
			}
		}
	}


	public void writePublications(final InputStream in, final PrintStream out) throws UnsupportedEncodingException, IOException, MalformedURLException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			final URL url = new URL(line.trim());
			final BibTex publication = getPublication(url);
			if (publication != null) {
				out.println(BibTexUtils.toBibtexString(publication));
			} else {
				System.err.println("Could not get metadata for " + url);
			}
		}
		reader.close();
	}
	
	
	public String writePublications(final String line) throws UnsupportedEncodingException, IOException, MalformedURLException {
		final URL url = new URL(line.trim());
		final BibTex publication = getPublication(url);
		if (publication != null) {
			return BibTexUtils.toBibtexString(publication);
		}
		return null;
	}
	

	public BibTex getPublication(final URL url) {
		final ScrapingContext sc = new ScrapingContext(url);

		try {
			if (this.scraper.scrape(sc)) {
				return this.parser.parseBibTeX(sc.getBibtexResult());
			}
		} catch (final Exception e) {

		}
		return null;
	}

}