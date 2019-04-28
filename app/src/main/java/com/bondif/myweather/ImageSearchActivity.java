package com.bondif.myweather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class ImageSearchActivity extends AppCompatActivity {
    private EditText etSearch;
    private Button btnSearch;
    private LinearLayout layoutImages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_search);

        setResourceReferences();

        btnSearch.setOnClickListener(event -> {
            if (etSearch.length() == 0) {
                Toast.makeText(getApplicationContext(), "Please enter a city name!", Toast.LENGTH_SHORT).show();
                return;
            }

            String API_KEY = "12318606-bf4fc45171c99a1a04a021106";
            String query = etSearch.getText().toString();
            String url = "https://pixabay.com/api/?key=" + API_KEY + "&q=" + query + "&image_type=photo";

            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest request = new StringRequest(Request.Method.GET, url,
                    response -> {
                        Log.i("MyLog", response);
                        try {
                            JSONObject root = new JSONObject(response);
                            JSONArray hits = root.getJSONArray("hits");
                            JSONObject image;
                            String imgUrl;
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            params.setMargins(0, 2, 0, 2);

                            for (int i = 0; i < hits.length(); i++) {
                                image = hits.getJSONObject(i);
                                imgUrl = image.getString("webformatURL");
                                Log.i("MyLog", imgUrl);

                                ImageView imageView = new ImageView(getApplicationContext());
                                imageView.setLayoutParams(params);
                                Picasso.get().load(imgUrl).into(imageView);
                                layoutImages.addView(imageView);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }, error -> Log.e("MyLog", error.getMessage())
            );
            queue.add(request);
        });
    }

    private void setResourceReferences() {
        etSearch = findViewById(R.id.etSearchImages);
        btnSearch = findViewById(R.id.btnSearchImages);
        layoutImages = findViewById(R.id.layoutImages);
    }
}
