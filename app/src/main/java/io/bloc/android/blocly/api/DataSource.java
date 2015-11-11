package io.bloc.android.blocly.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.model.RssFeed;
import io.bloc.android.blocly.api.model.RssItem;
import io.bloc.android.blocly.api.network.GetFeedsNetworkRequest;

/**
 * Created by jeffbrys on 10/26/15.
 */
public class DataSource {


    private List<RssFeed> feeds;
    private List<RssItem> items;
    List<GetFeedsNetworkRequest.FeedResponse> feedResponse;


    public DataSource() {
        feeds = new ArrayList<RssFeed>();
        items = new ArrayList<RssItem>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                feedResponse = new GetFeedsNetworkRequest("http://www.npr.org/rss/rss.php?id=1001").performRequest();
                createTestData(feedResponse);
            }
        }).start();

//        createFakeData();


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

    void createTestData(List<GetFeedsNetworkRequest.FeedResponse> feedResponse) {

        GetFeedsNetworkRequest.FeedResponse feed = feedResponse.get(0);


        int itemIndex = 0;
        for (GetFeedsNetworkRequest.ItemResponse item : feed.channelItems) {
            long pubDate = System.currentTimeMillis();
            SimpleDateFormat f = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss z", Locale.ENGLISH);
            try {
                pubDate = f.parse(item.itemPubDate).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            items.add(new RssItem(item.itemGUID,
                    item.itemTitle,
                    item.itemDescription,
                    item.itemURL,
                    item.itemEnclosureURL,
                    itemIndex,
                    pubDate,
                    false, false, false
            ));
            itemIndex++;
        }
        feeds.add(new RssFeed(feed.channelTitle,
                feed.channelDescription,
                feed.channelURL,
                feed.channelFeedURL));

    }


}

