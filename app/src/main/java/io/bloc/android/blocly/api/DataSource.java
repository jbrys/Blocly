package io.bloc.android.blocly.api;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
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

    public static final String ACTION_DOWNLOAD_COMPLETED = DataSource.class.getCanonicalName().concat(".ACTION_DOWNLOAD_COMPLETED");

    private DatabaseOpenHelper databaseOpenHelper;
    private RssFeedTable rssFeedTable;
    private RssItemTable rssItemTable;
    private List<RssFeed> feeds;
    private List<RssItem> items;

    public DataSource() {
        rssFeedTable = new RssFeedTable();
        rssItemTable = new RssItemTable();
        databaseOpenHelper = new DatabaseOpenHelper(BloclyApplication.getSharedInstance(),
                rssFeedTable, rssItemTable);

        feeds = new ArrayList<RssFeed>();
        items = new ArrayList<RssItem>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (BuildConfig.DEBUG && true) {
                    BloclyApplication.getSharedInstance().deleteDatabase("blocly_db");
                }
                SQLiteDatabase writableDatabase = databaseOpenHelper.getWritableDatabase();
                List<GetFeedsNetworkRequest.FeedResponse> feedResponses =
                        new GetFeedsNetworkRequest("http://feeds.feedburner.com/androidcentral?format=xml").performRequest();
                GetFeedsNetworkRequest.FeedResponse androidCentral = feedResponses.get(0);

                long androidCentralFeedId = new RssFeedTable.Builder()
                        .setFeedURL(androidCentral.channelFeedURL)
                        .setSiteURL(androidCentral.channelURL)
                        .setTitle(androidCentral.channelTitle)
                        .setDescription(androidCentral.channelDescription)
                        .insert(writableDatabase);

                List<RssItem> newRssItems = new ArrayList<RssItem>();
                for (GetFeedsNetworkRequest.ItemResponse itemResponse : androidCentral.channelItems) {
                    long itemPubDate = System.currentTimeMillis();
                    DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
                    try {
                        itemPubDate = dateFormat.parse(itemResponse.itemPubDate).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    long newItemRowId = new RssItemTable.Builder()
                            .setTitle(itemResponse.itemTitle)
                            .setDescription(itemResponse.itemDescription)
                            .setEnclosure(itemResponse.itemEnclosureURL)
                            .setMIMEType(itemResponse.itemEnclosureMIMEType)
                            .setLink(itemResponse.itemURL)
                            .setGUID(itemResponse.itemGUID)
                            .setPubDate(itemPubDate)
                            .setRSSFeed(androidCentralFeedId)
                            .insert(writableDatabase);

                    Cursor itemCursor = rssItemTable.fetchRow(databaseOpenHelper.getReadableDatabase(), newItemRowId);

                    itemCursor.moveToFirst();
                    RssItem newRssItem = itemFromCursor(itemCursor);
                    newRssItems.add(newRssItem);
                    itemCursor.close();
                }
                Cursor androidCentralCursor = rssFeedTable.fetchRow(databaseOpenHelper.getReadableDatabase(), androidCentralFeedId);
                androidCentralCursor.moveToFirst();
                RssFeed androidCentralRssFeed = feedFromCursor(androidCentralCursor);
                androidCentralCursor.close();
                items.addAll(newRssItems);
                feeds.add(androidCentralRssFeed);
                BloclyApplication.getSharedInstance().sendBroadcast(new Intent(ACTION_DOWNLOAD_COMPLETED));
            }
        }).start();

        RssItemTable table = new RssItemTable();
        table.getAllArchived(databaseOpenHelper.getReadableDatabase());
        table.getAllArchivedFromFeed(databaseOpenHelper.getReadableDatabase(), "0");
        table.getAllFavorited(databaseOpenHelper.getReadableDatabase());
        table.getAllFavoritedFromFeed(databaseOpenHelper.getReadableDatabase(), "0");
        table.getAllFromFeed(databaseOpenHelper.getReadableDatabase(), "0");
        table.getAllFromFeedWithOffsetAndLimit(databaseOpenHelper.getReadableDatabase(), "0", 2, 10);
    }

    public List<RssFeed> getFeeds() {
        return feeds;
    }

    public List<RssItem> getItems() {
        return items;
    }

    static RssFeed feedFromCursor(Cursor cursor) {
        return new RssFeed(RssFeedTable.getTitle(cursor), RssFeedTable.getDescription(cursor),
                RssFeedTable.getSiteURL(cursor), RssFeedTable.getFeedURL(cursor));
    }

    static RssItem itemFromCursor(Cursor cursor) {
        return new RssItem(RssItemTable.getGUID(cursor), RssItemTable.getTitle(cursor),
                RssItemTable.getDescription(cursor), RssItemTable.getLink(cursor),
                RssItemTable.getEnclosure(cursor), RssItemTable.getRssFeedId(cursor),
                RssItemTable.getPubDate(cursor), RssItemTable.getFavorite(cursor),
                RssItemTable.getArchived(cursor));
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
                    0, System.currentTimeMillis(), false, false));
        }
    }
}
