package io.bloc.android.blocly.api;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.BuildConfig;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.database.DatabaseOpenHelper;
import io.bloc.android.blocly.api.database.table.RssFeedTable;
import io.bloc.android.blocly.api.database.table.RssItemTable;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.api.network.GetFeedsNetworkRequest;

/**
 * Created by jeffbrys on 10/26/15.
 */
public class DataSource {

    private DatabaseOpenHelper databaseOpenHelper;
    private RssFeedTable rssFeedTable;
    private RssItemTable rssItemTable;
    private List<RssFeed> feeds;
    private List<RssItem> items;
    private List<GetFeedsNetworkRequest.FeedResponse> feedResponse;

    public DataSource() {

        rssFeedTable = new RssFeedTable();
        rssItemTable = new RssItemTable();
        databaseOpenHelper = new DatabaseOpenHelper(BloclyApplication.getSharedInstance(),
                rssFeedTable, rssItemTable);

        feeds = new ArrayList<RssFeed>();
        items = new ArrayList<RssItem>();
        createFakeData();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG && false) {
                    BloclyApplication.getSharedInstance().deleteDatabase("blocly_db");
                }
                SQLiteDatabase writeableDatabase = databaseOpenHelper.getWritableDatabase();
                feedResponse = new GetFeedsNetworkRequest("http://feeds.feedburner.com/androidcentral?format=xml").performRequest();

                GetFeedsNetworkRequest.FeedResponse feed = feedResponse.get(0);

                ContentValues feedValues = new ContentValues();
                feedValues.put(rssFeedTable.COLUMN_ID, 0);
                feedValues.put(rssFeedTable.COLUMN_FEED_URL, feed.channelURL);
                feedValues.put(rssFeedTable.COLUMN_TITLE, feed.channelTitle);
                feedValues.put(rssFeedTable.COLUMN_DESCRIPTION, feed.channelDescription);
                feedValues.put(rssFeedTable.COLUMN_FEED_URL, feed.channelFeedURL);


                writeableDatabase.insert(rssFeedTable.getName(), null, feedValues);

                int itemIndex = 0;
                for (GetFeedsNetworkRequest.ItemResponse item : feed.channelItems){

                    long pubDate = System.currentTimeMillis();
                    SimpleDateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
                    try {
                        pubDate = f.parse(item.itemPubDate).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    ContentValues itemValues = new ContentValues();
                    itemValues.put(rssItemTable.COLUMN_ID, itemIndex);
                    itemValues.put(rssItemTable.COLUMN_LINK, item.itemURL);
                    itemValues.put(rssItemTable.COLUMN_TITLE, item.itemTitle);
                    itemValues.put(rssItemTable.COLUMN_DESCRIPTION, item.itemDescription);
                    itemValues.put(rssItemTable.COLUMN_GUID, item.itemGUID);
                    itemValues.put(rssItemTable.COLUMN_PUB_DATE, pubDate);
                    itemValues.put(rssItemTable.COLUMN_ENCLOSURE, item.itemEnclosureURL);
                    itemValues.put(rssItemTable.COLUMN_MIME_TYPE, item.itemEnclosureMIMEType);
                    itemValues.put(rssItemTable.COLUMN_RSS_FEED, 0);
                    itemValues.put(rssItemTable.COLUMN_FAVORITE, 0);
                    itemValues.put(rssItemTable.COLUMN_ARCHIVED, 0);

                    writeableDatabase.insert(rssItemTable.getName(), null, itemValues);
                    itemIndex++;
                }


            }
        }).start();
    }



    public List<RssFeed> getFeeds() {
        return feeds;
    }

    public List<RssItem> getItems() {
        return items;
    }

    void createFakeData() {
        feeds.add(new RssFeed("My Favorite Feed",
                "This feed is just incredible, I can't even begin to tell you…",
                "http://favoritefeed.net", "http://feeds.feedburner.com/favorite_feed?format=xml"));
        for (int i = 0; i < 10; i++) {
            items.add(new RssItem(String.valueOf(i),
                    BloclyApplication.getSharedInstance().getString(R.string.placeholder_headline) + " " + i,
                    BloclyApplication.getSharedInstance().getString(R.string.placeholder_content),
                    "http://favoritefeed.net?story_id=an-incredible-news-story",
                    // Had to replace this image since the orginal was returning 404
                    //"http://rs1img.memecdn.com/silly-dog_o_511213.jpg",
                    "http://i.kinja-img.com/gawker-media/image/upload/s--gnSWo1nI--/japbcvpavbzau9dbuaxf.jpg",
                    0, System.currentTimeMillis(), false, false, false));
        }
    }
}
