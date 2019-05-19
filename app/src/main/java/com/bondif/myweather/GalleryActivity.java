package com.bondif.myweather;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.bondif.myweather.room.AppDatabase;
import com.bondif.myweather.room.Photo;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static AppDatabase appDatabase;
    private Button btnOpenCamera;
    private String placeName;
    private FusedLocationProviderClient fusedLocationClient;
    private Activity _this;
    private List<PhotoModel> photoModels = new LinkedList<>();
    private GalleryAdapter galleryAdapter;
    private ListView lvPhotosList;
    private String currentPhotoPath;
    private Bitmap imageBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        _this = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setResourceReferences();
        appDatabase = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "photos").allowMainThreadQueries().build();

        galleryAdapter = new GalleryAdapter(getApplicationContext(), R.layout.layout_image_card, photoModels);
        lvPhotosList.setAdapter(galleryAdapter);

        if (!canAccessLocation() && !canAccessExternalStorage()) {
            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    },
                    1340);
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnOpenCamera.setOnClickListener(event -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.i("MyLog", ex.getMessage());
                }

                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(
                            getApplicationContext(),
                            "com.bondif.myweather.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        Photo[] photos = appDatabase.photoDao().findAll();
        for (Photo dbPhoto : photos) {
            Log.i("MyLog", dbPhoto.toString());
            photoModels.add(new PhotoModel(dbPhoto));
            galleryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(currentPhotoPath));
            } catch (IOException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Place Name");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);
            builder.setCancelable(false);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    placeName = input.getText().toString();
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(_this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        Photo photo = new Photo();
                                        photo.name = "";
                                        photo.data = currentPhotoPath;

                                        photo.latitude = location.getLongitude() + "";
                                        photo.longitude = location.getLatitude() + "";
                                        photo.placeName = placeName;

                                        appDatabase.photoDao().insertPhoto(photo);
                                        photoModels.add(new PhotoModel(photo));
                                        galleryAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), "You must fill the place name field", Toast.LENGTH_SHORT).show();
                }
            });

            builder.show();
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        currentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private boolean canAccessLocation() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean canAccessExternalStorage() {
        return ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void setResourceReferences() {
        btnOpenCamera = findViewById(R.id.btnOpenCamera);
        lvPhotosList = findViewById(R.id.photosList);
    }
}
