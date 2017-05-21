package com.flynorc.a08_newsapp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Flynorc on 20-May-17.
 * Class to hold the data about each article (news)
 */

public class Article {
    private String title;
    private String section;
    private Date publishedDate;
    private String authorName;
    private String url;

    /*
     * constructor
     */
    public Article (String title, String section, Date publishedDate, String authorName, String url) {
        this.title = title;
        this.section = section;
        this.publishedDate = publishedDate;
        this.authorName = authorName;
        this.url = url;
    }

    /*
     * getters
     */
    public String getTitle() {
        return title;
    }

    public String getSection() {
        return section;
    }

    public String getDate() {
        //format the date in a more human readable form to use in the app
        return new SimpleDateFormat("dd.MM.yyyy - HH:mm").format(publishedDate);
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getUrl() {
        return url;
    }

    /*
     * helper for debugging
     */
    @Override
    public String toString() {
        return "title: " + title + ", section: " + section + ", date: " + getDate() + ", author: " + authorName + ", url: " + url;
    }
}
