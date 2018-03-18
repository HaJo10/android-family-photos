package com.shellmonger.apps.familyphotos.listeners;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.shellmonger.apps.familyphotos.activities.EditActivity;
import com.shellmonger.apps.familyphotos.models.Photo;
import com.shellmonger.apps.familyphotos.repositories.RepositoryException;
import com.shellmonger.apps.familyphotos.repositories.RepositoryFactory;

/**
 * Click-listener for the photo list items used in the MainActivity
 */
public class PhotoListClickListener implements RecyclerView.OnItemTouchListener {
    private static final String TAG = "PhotoListClickListener";

    private Context mContext;

    /**
     * Create a new click-listener
     * @param context the context of the calling activity
     */
    public PhotoListClickListener(final Context context) {
        mContext = context;
    }

    /**
     * Handle a click on an item in the list
     * @param rv the recyclerview that was clicked
     * @param e the click event
     * @return true if the event was handled
     */
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        try {
            View vItem = rv.findChildViewUnder(e.getX(), e.getY());
            if (vItem != null) {
                int position = rv.getChildAdapterPosition(vItem);
                Photo model = RepositoryFactory.getPhotosRepository().getItem(position);

                Bundle bundle = new Bundle();
                bundle.putString("id", model.getId());
                Intent intent = new Intent(mContext, EditActivity.class);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        } catch (RepositoryException err) {
            Log.e(TAG, "RepositoryException", err);
            throw new RuntimeException("RepositoryException", err);
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
