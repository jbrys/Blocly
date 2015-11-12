package io.bloc.android.blocly.api.database.table;

/**
 * Created by jeffbrys on 11/10/15.
 */
public class RssFeedTable extends Table {

    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_FEED_URL = "feed_url";


    @Override
    public String getName() {
        return "rss_feeds";
    }

    @Override
    public String getCreateStatement() {
        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_LINK + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_FEED_URL + " TEXT)";
    }

}
