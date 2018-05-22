package de.dhjmf.rss;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores an RSS feed
 */
public class Feed {

    final String title;
    final String link;
    final String pubDate;

    final List<FeedMessage> entries = new ArrayList<FeedMessage>();

    public Feed(String title, String link, String pubDate) {
        this.title = title;
        this.link = link;
        this.pubDate = pubDate;
    }

    public List<FeedMessage> getMessages() {
        return entries;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPubDate() {
        return pubDate;
    }

    @Override
    public String toString() {
        return "Feed [link=" + link + ", pubDate=" + pubDate + ", title=" + title + "]";
    }

}