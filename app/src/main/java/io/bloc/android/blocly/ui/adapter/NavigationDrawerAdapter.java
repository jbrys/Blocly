package io.bloc.android.blocly.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import io.bloc.android.blocly.BloclyApplication;
import io.bloc.android.blocly.R;
import io.bloc.android.blocly.api.model.RssFeed;

/**
 * Created by jeffbrys on 11/7/15.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    public enum NavigationOption {
        NAVIGATION_OPTION_INBOX,
        NAVIGATION_OPTION_FAVORITES,
        NAVIGATION_OPTION_ARCHIVED
    }

    public static interface  DrawerClickListener {
        public void didSelectDrawerOption(NavigationDrawerAdapter adapter);

    }

    WeakReference<DrawerClickListener> drawerClickListener;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.navigation_item, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RssFeed rssFeed = null;

        if (position >= NavigationOption.values().length) {
            int feedPosition = position - NavigationOption.values().length;
            rssFeed = BloclyApplication.getSharedDataSource().getFeeds().get(feedPosition);
        }
        holder.update(position, rssFeed);
    }

    @Override
    public int getItemCount() {
        return NavigationOption.values().length
                + BloclyApplication.getSharedDataSource().getFeeds().size();
    }

    public DrawerClickListener getDrawerClickListener(){
        if (drawerClickListener == null){
            return null;
        }
        return drawerClickListener.get();
    }

    public void setDrawerClickListener(DrawerClickListener listener){
        this.drawerClickListener = new WeakReference<DrawerClickListener>(listener);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View topPadding;
        TextView title;
        View bottomPadding;
        View divider;

        public ViewHolder(View itemView) {
            super(itemView);
            topPadding = itemView.findViewById(R.id.v_nav_item_top_padding);
            title = (TextView) itemView.findViewById(R.id.tv_nav_item_title);
            bottomPadding = itemView.findViewById(R.id.v_nav_item_bottom_padding);
            divider = itemView.findViewById(R.id.v_nav_item_divider);
            itemView.setOnClickListener(this);
        }

        void update(int position, RssFeed rssFeed) {
            boolean shouldShowTopPadding = position == NavigationOption.NAVIGATION_OPTION_INBOX.ordinal()
                    || position == NavigationOption.values().length;
            topPadding.setVisibility(shouldShowTopPadding ? View.VISIBLE : View.GONE);

            boolean shouldShowBottomPadding = position == NavigationOption.NAVIGATION_OPTION_ARCHIVED.ordinal()
                    || position == getItemCount() - 1;
            bottomPadding.setVisibility(shouldShowBottomPadding ? View.VISIBLE : View.GONE);

            divider.setVisibility(position == NavigationOption.NAVIGATION_OPTION_ARCHIVED.ordinal()
                    ? View.VISIBLE : View.GONE);

            if (position < NavigationOption.values().length) {

                int[] titleTexts = new int[] {R.string.navigation_option_inbox,
                    R.string.navigation_option_favorites,
                    R.string.navigation_option_archived};
                title.setText(titleTexts[position]);
            } else {
                title.setText(rssFeed.getTitle());
            }
        }

        /*
        * OnClickListener
        */

        @Override
        public void onClick(View view) {

            if (getDrawerClickListener() == null){
                return;
            }

            getDrawerClickListener().didSelectDrawerOption(NavigationDrawerAdapter.this);
//            Toast.makeText(view.getContext(), "Nothing...yet!", Toast.LENGTH_SHORT).show();

        }
    }
}
