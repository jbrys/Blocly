package io.bloc.android.blocly.ui.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.DataSource;
import io.bloc.android.blocly.api.model.RssItem;

/**
 * Created by jeffbrys on 11/18/15.
 */
public class RssItemDetailFragment extends Fragment implements ImageLoadingListener, android.support.v7.widget.Toolbar.OnMenuItemClickListener {

    private static final String BUNDLE_EXTRA_RSS_ITEM = RssItemDetailFragment.class.getCanonicalName().concat(".EXTRA_RSS_ITEM");

    public static final RssItemDetailFragment detailFragmentForRssItem(RssItem rssItem) {
        Bundle arguments = new Bundle();
        arguments.putLong(BUNDLE_EXTRA_RSS_ITEM, rssItem.getRowId());
        RssItemDetailFragment rssItemDetailFragment = new RssItemDetailFragment();
        rssItemDetailFragment.setArguments(arguments);
        return rssItemDetailFragment;
    }

    ImageView headerImage;
    TextView title;
    TextView content;
    ProgressBar progressBar;
    Toolbar toolbar;
    RssItem sidePanelItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            long rssItemId = arguments.getLong(BUNDLE_EXTRA_RSS_ITEM);
            BloclyApplication.getSharedDataSource().fetchRSSItemWithId(rssItemId, new DataSource.Callback<RssItem>() {
                @Override
                public void onSuccess(RssItem rssItem) {
                    if (getActivity() == null) {
                        return;
                    }
                    sidePanelItem = rssItem;
                    title.setText(rssItem.getTitle());
                    content.setText(rssItem.getDescription());
                    ImageLoader.getInstance().loadImage(rssItem.getImageUrl(), RssItemDetailFragment.this);
                }

                @Override
                public void onError(String errorMessage) {

                }
            });
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_rss_item_detail, container, false);
        headerImage = (ImageView) inflate.findViewById(R.id.iv_fragment_rss_item_detail_header);
        progressBar = (ProgressBar) inflate.findViewById(R.id.pb_fragment_rss_item_detail_header);
        title = (TextView) inflate.findViewById(R.id.tv_fragment_rss_item_detail_title);
        content = (TextView) inflate.findViewById(R.id.tv_fragment_rss_item_detail_content);
        toolbar = (Toolbar) getActivity().findViewById(R.id.tb_activity_blocly_right_pane);
        toolbar.setOnMenuItemClickListener(this);
        return inflate;
    }

    /*
     * ImageLoadingListener
     */

    @Override
    public void onLoadingStarted(String imageUri, View view) {
        progressBar.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        headerImage.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();

    }

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
        progressBar.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
    }

    @Override
    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
        progressBar.animate()
                .alpha(0f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();
        headerImage.setImageBitmap(loadedImage);
        headerImage.animate()
                .alpha(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime))
                .start();

    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {

    }



    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (sidePanelItem == null) {return false;}

        if (menuItem.getItemId() == R.id.action_visit_page) {
            RssItem itemToView = sidePanelItem;

            Intent visitIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(itemToView.getUrl()));
            startActivity(visitIntent);
        }
        if (menuItem.getItemId() == R.id.action_share) {
            RssItem itemToShare = sidePanelItem;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    String.format("%s (%s)", itemToShare.getTitle(), itemToShare.getUrl()));

            shareIntent.setType("text/plain");
            Intent chooser = Intent.createChooser(shareIntent, getString(R.string.share_chooser_title));
            startActivity(chooser);
        }

        return true;
    }
}
