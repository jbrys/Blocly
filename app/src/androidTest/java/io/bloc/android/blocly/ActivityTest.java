package io.bloc.android.blocly;

import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;

import io.bloc.android.blocly.ui.activity.BloclyActivity;

/**
 * Created by jeffbrys on 11/22/15.
 */
public class ActivityTest extends ActivityInstrumentationTestCase2<BloclyActivity> {

    private BloclyActivity mActivity;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Fragment mFragment;

    public ActivityTest(){
        super(BloclyActivity.class);

    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();

        mActivity = getActivity();
        mRecyclerView = (RecyclerView) mActivity.findViewById(R.id.rv_fragment_rss_list);
        mSwipeRefreshLayout = (SwipeRefreshLayout) mActivity.findViewById(R.id.srl_fragment_rss_list);
        mFragment = getActivity().getFragmentManager().findFragmentById(R.id.fl_activity_blocly);

        BloclyApplication.TEST_MODE = true;


    }

    public void testPreconditions() {
        assertNotNull("mActivity is null", mActivity);
        assertNotNull("mRecyclerView is null", mRecyclerView);
        assertNotNull("mFragment is null", mFragment);
    }

    public void testItemHasFavoriteCheckbox() {

        assertNotNull(mRecyclerView.getChildAt(0).findViewById(R.id.cb_rss_item_favorite_star));
    }

}
