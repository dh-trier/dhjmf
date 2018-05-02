package de.dhjmf.rss;

import de.dhjmf.Utils;

/*
 * Represents one RSS message
 */
public class FeedMessage {

    String title;
    String link;
    String pubdate;
    
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    @Override
    public String toString() {
        // return "FeedMessage [title=" + title + ", link=" + link + ", pubdate=" + pubdate + "]";
    	return pubdate + Utils.DELIMITER + link + Utils.DELIMITER + title + Utils.EOL;
    }

}