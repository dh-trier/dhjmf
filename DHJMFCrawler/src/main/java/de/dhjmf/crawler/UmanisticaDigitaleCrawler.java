package de.dhjmf.crawler;

import java.io.IOException;

import de.dhjmf.Utils;

public class UmanisticaDigitaleCrawler extends AbstractRSSCrawler {
		
	public UmanisticaDigitaleCrawler() throws IOException {
		super(Utils.UMANISTICADIGITALE_NAME, Utils.UMANISTICADIGITALE_FOLDER, Utils.UMANISTICADIGITALE_NEWLINKS_FOLDER, 
				Utils.UMANISTICADIGITALE_RSS, Utils.UMANISTICADIGITALE_DOC, Utils.UMANISTICADIGITALE_TMP, Utils.UMANISTICADIGITALE_OUT);
	}
	
}