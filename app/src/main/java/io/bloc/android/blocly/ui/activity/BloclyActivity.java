package io.bloc.android.blocly.ui.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import io.bloc.android.blocly.R;
import io.bloc.android.blocly.ui.adapter.ItemAdapter;

/**
 * Created by jeffbrys on 10/24/15.
 */
public class BloclyActivity extends Activity implements ImageLoadingListener {

    private ItemAdapter itemAdapter;
    ImageView recyclerViewBackgroundImage;
    String imageURL = "https://i.imgur.com/PcC50MI.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocly);

        ImageLoader.getInstance().loadImage(imageURL, this);
        itemAdapter = new ItemAdapter();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_activity_blocly);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter);
    }

    @Override
    public void onLoadingStarted(String imageUri, View view){}

    @Override
    public void onLoadingFailed(String imageUri, View view, FailReason failReason){}

    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage){
        recyclerViewBackgroundImage = (ImageView) findViewById(R.id.iv_rv_background);

        if (imageUri.equals(imageURL)) {
            recyclerViewBackgroundImage.setImageBitmap(loadedImage);
        }
    }

    @Override
    public void onLoadingCancelled(String imageUri, View view) {}
}
