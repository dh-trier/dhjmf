package de.dhjmf.crawler;

import java.io.IOException;

import de.dhjmf.Utils;

public class ZfdGCrawler extends AbstractRSSCrawler {
	
	public ZfdGCrawler() throws IOException {
		super(Utils.ZFDG_NAME, Utils.ZFDG_FOLDER, Utils.ZFDG_NEWLINKS_FOLDER, Utils.ZFDG_RSS, Utils.ZFDG_DOC, Utils.ZFDG_TMP, Utils.ZFDG_OUT);
	}
	
}