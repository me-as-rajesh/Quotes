package com.only.quotes;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GenerateCardActivity extends AppCompatActivity {

    private EditText searchBar;
    private Button searchButton;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrls;

    private static final String API_URL = "https://lexica.art/api/v1/search?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_card);

        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        imageUrls = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUrls, this::onImageSelected);
        recyclerView.setAdapter(imageAdapter);

        searchButton.setOnClickListener(v -> {
            String searchTerm = searchBar.getText().toString();
            if (!searchTerm.isEmpty()) {
                new Thread(() -> searchForImages(searchTerm)).start();
            } else {
                Toast.makeText(this, "Please enter a search term!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchForImages(String searchTerm) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_URL + searchTerm)
                .build();

        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                List<String> urls = parseImageUrlsFromJson(responseBody);

                runOnUiThread(() -> {
                    imageUrls.clear();
                    imageUrls.addAll(urls);
                    imageAdapter.notifyDataSetChanged();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch images. Response code: " + response.code(), Toast.LENGTH_SHORT).show());
            }
        } catch (IOException e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private List<String> parseImageUrlsFromJson(String jsonResponse) {
        List<String> urls = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray imagesArray = jsonObject.getJSONArray("images");
            for (int i = 0; i < imagesArray.length(); i++) {
                JSONObject item = imagesArray.getJSONObject(i);
                String url = item.getString("srcSmall");
                urls.add(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    private void onImageSelected(String imageUrl) {
        Intent intent = new Intent(this, CreateCardActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }
}
