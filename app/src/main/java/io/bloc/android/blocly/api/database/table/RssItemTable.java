package io.bloc.android.blocly.api.database.table;

/**
 * Created by jeffbrys on 11/10/15.
 */
public class RssItemTable extends Table {

    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_GUID = "guid";
    public static final String COLUMN_PUB_DATE = "pub_date";
    public static final String COLUMN_ENCLOSURE = "enclosure";
    public static final String COLUMN_MIME_TYPE = "mime_type";
    public static final String COLUMN_RSS_FEED = "rss_feed";
    public static final String COLUMN_FAVORITE = "is_favorite";
    public static final String COLUMN_ARCHIVED = "is_archived";

    @Override
    public String getName() {
        return "rss_items";
    }

    @Override
    public String getCreateStatement() {
        return "CREATE TABLE " + getName() + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_LINK + " TEXT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_GUID + " TEXT,"
                + COLUMN_PUB_DATE + " INTEGER,"
                + COLUMN_ENCLOSURE + " TEXT,"
                + COLUMN_MIME_TYPE + " TEXT,"
                + COLUMN_RSS_FEED + " INTEGER,"
                + COLUMN_FAVORITE + " INTEGER DEFAULT 0,"
                + COLUMN_ARCHIVED + " INTEGER DEFAULT 0)";
    }

}
