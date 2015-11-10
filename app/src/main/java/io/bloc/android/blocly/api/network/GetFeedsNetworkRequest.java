package io.bloc.android.blocly.api.network;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jeffbrys on 11/10/15.
 */
public class GetFeedsNetworkRequest extends NetworkRequest {

    final static String TAG = GetFeedsNetworkRequest.class.getSimpleName();

    String[] feedUrls;
    int entries = 0;


    public GetFeedsNetworkRequest(String... feedUrls) {
        this.feedUrls = feedUrls;
    }

    @Override
    public Object performRequest() {
        for (String feedUrlString : feedUrls) {
            InputStream inputStream = openStream(feedUrlString);


            if (inputStream == null) {
                return null;
            }
            try {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(inputStream, "UTF_8");

                boolean insideItem = false;

                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("item")) {
                            insideItem = true;
                            entries++;
                        } else if (xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem)
                                Log.v(TAG, xpp.nextText());
                        } else if (xpp.getName().equalsIgnoreCase("link")) {
                            if (insideItem)
                                Log.v(TAG, xpp.nextText());
                        }
                    } else if (eventType == XmlPullParser.END_TAG &&
                            xpp.getName().equalsIgnoreCase("item")) {
                        insideItem = false;
                    }

                    eventType = xpp.next();
                }

            } catch (IOException e) {
                e.printStackTrace();
                setErrorCode(ERROR_IO);
                return null;
            } catch (XmlPullParserException e) {
                e.printStackTrace();;
                setErrorCode(ERROR_MALFORMED_XML);
                return null;
            }

            Log.v(TAG, "Entry count: " + entries);
        }
        return null;
    }



}
