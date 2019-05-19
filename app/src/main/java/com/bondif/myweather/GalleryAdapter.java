package com.bondif.myweather;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

public class GalleryAdapter extends ArrayAdapter<PhotoModel> {

    private List<PhotoModel> photosList;
    private int resource;

    public GalleryAdapter(@NonNull Context context, int resource, List<PhotoModel> photosList) {
        super(context, resource, photosList);
        this.photosList = photosList;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }
        ImageView ivPhoto = listItem.findViewById(R.id.ivPhoto);
        TextView tvPlaceName = listItem.findViewById(R.id.tvPlaceName);
        Button btnViewOnMap = listItem.findViewById(R.id.btnViewOnMap);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ivPhoto.setAdjustViewBounds(true);
        ivPhoto.setScaleType(ImageView.ScaleType.FIT_START);
        ivPhoto.setLayoutParams(params);

        try {
            ivPhoto.setImageBitmap(MediaStore.Images.Media.getBitmap(parent.getContext().getContentResolver(), Uri.parse(photosList.get(position).photo.data)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        tvPlaceName.setText(String.valueOf(photosList.get(position).photo.placeName));

        return listItem;
    }
}
