package com.shellmonger.apps.familyphotos.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shellmonger.apps.familyphotos.R;
import com.shellmonger.apps.familyphotos.models.Photo;
import com.shellmonger.apps.familyphotos.repositories.Repository;
import com.shellmonger.apps.familyphotos.repositories.RepositoryChange;
import com.shellmonger.apps.familyphotos.repositories.RepositoryException;
import com.shellmonger.apps.familyphotos.repositories.RepositoryFactory;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ViewHolder> implements Observer {
    private static final String TAG = "PhotoListAdapter";

    /**
     * The list of photos
     */
    private Repository<Photo> photos;

    /**
     * Create a new PhotoListAdapter
     */
    public PhotoListAdapter() throws RepositoryException {
        photos = RepositoryFactory.getPhotosRepository();
        photos.addObserver(this);
    }

    /**
     * Called when the RecyclerView creates an element
     * @param parent the parent RecyclerView
     * @param viewType the type of view
     * @return the ViewHolder for the element
     */
    @NonNull
    @Override
    public PhotoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_photos, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Called to bind the data in an element with the view
     * @param holder the view holder
     * @param position the position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull PhotoListAdapter.ViewHolder holder, int position) {
        try {
            holder.setModel(photos.getItem(position));
        } catch (RepositoryException err) {
            Log.e(TAG, "RepositoryException:", err);
            throw new RuntimeException("RepositoryException", err);
        }
    }

    /**
     * Gets the number of elements in the list
     * @return the number of elements in the list
     */
    @Override
    public int getItemCount() {
        try {
            return photos.getLength();
        } catch (RepositoryException err) {
            Log.e(TAG, "RepositoryException:", err);
            throw new RuntimeException("RepositoryException", err);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        RepositoryChange change = (RepositoryChange) arg;
        switch (change.getOperation()) {
            case ADD:
                notifyItemInserted(change.getPosition());
                break;
            case CHANGE:
                notifyItemChanged(change.getPosition());
                break;
            case REMOVE:
                notifyItemRemoved(change.getPosition());
                break;
        }
    }

    /**
     * View Holder for the adapter
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View mView;

        private TextView mCaption, mAlbum, mLocation, mTags;
        private ImageView mImage, mLocationIcon, mTagsIcon;

        public ViewHolder(View v) {
            super(v);
            mView = v;

            mCaption = v.findViewById(R.id.text_caption);
            mAlbum = v.findViewById(R.id.text_album);
            mLocation = v.findViewById(R.id.text_location);
            mTags = v.findViewById(R.id.text_tags);

            mImage = v.findViewById(R.id.image_picture);
            mLocationIcon = v.findViewById(R.id.icon_location);
            mTagsIcon = v.findViewById(R.id.icon_tags);
        }

        public void setModel(Photo model) {
            // Set the caption (if available)
            if (model.getCaption() != null) {
                mCaption.setText(model.getCaption());
                mCaption.setVisibility(View.VISIBLE);
            } else {
                mCaption.setVisibility(View.INVISIBLE);
            }

            // Set the image
            mImage.setImageBitmap(model.getPicture());
            mImage.setAdjustViewBounds(true);

            // Set the album name
            if (model.getAlbumId() != null) {
                mAlbum.setText(model.getAlbumId());
            } else {
                mAlbum.setText(R.string.default_album_name);
            }

            // Set the Location - location is not available yet
            mLocationIcon.setVisibility(View.INVISIBLE);
            mLocation.setVisibility(View.INVISIBLE);

            // Set the tags
            List<String> tags = model.getTags();
            if (tags == null || tags.size() == 0) {
                mTagsIcon.setVisibility(View.INVISIBLE);
                mTags.setVisibility(View.INVISIBLE);
            } else {
                mTagsIcon.setVisibility(View.VISIBLE);
                mTags.setVisibility(View.VISIBLE);
                mTags.setText(TextUtils.join(", ", tags));
            }
        }
    }

}
