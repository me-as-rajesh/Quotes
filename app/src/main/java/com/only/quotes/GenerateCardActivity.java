package com.only.quotes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.text.TextWatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GenerateCardActivity extends AppCompatActivity {

    private EditText searchBar;
    private Button searchButton;
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrls;

    private static final String API_URL = "https://lexica.art/api/infinite-prompts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_card);

        searchBar = findViewById(R.id.searchBar);
        searchButton = findViewById(R.id.searchButton);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        imageUrls = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUrls, this::onImageSelected);
        recyclerView.setAdapter(imageAdapter);

        searchBar.setHint("Search for images");
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No need to implement this
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int after) {
                if (charSequence.length() == 0) {
                    clearImages();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        searchButton.setOnClickListener(v -> {
            String searchTerm = searchBar.getText().toString().trim();
            if (!searchTerm.isEmpty()) {
                new Thread(() -> fetchImages(searchTerm)).start();
            } else {
                Toast.makeText(this, "Please enter a search term!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchImages(String searchTerm) {
        try {
            // Prepare JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("text", searchTerm);
            requestBody.put("model", "lexica-aperture-v3.5");
            requestBody.put("searchMode", "images");
            requestBody.put("source", "search");
            requestBody.put("cursor", 0);

            // Create connection
            HttpURLConnection connection = (HttpURLConnection) new URL(API_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send request body
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.toString().getBytes());
                os.flush();
            }

            // Check response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                // Parse JSON response
                List<String> urls = parseImageUrlsFromJson(response.toString());
                runOnUiThread(() -> {
                    imageUrls.clear();
                    imageUrls.addAll(urls);
                    imageAdapter.notifyDataSetChanged();
                });
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Failed to fetch images. Code: " + responseCode, Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "An error occurred: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private List<String> parseImageUrlsFromJson(String jsonResponse) {
        List<String> urls = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray prompts = jsonObject.getJSONArray("prompts");

            for (int i = 0; i < prompts.length(); i++) {
                JSONObject prompt = prompts.getJSONObject(i);
                JSONArray images = prompt.getJSONArray("images");

                for (int j = 0; j < images.length(); j++) {
                    JSONObject image = images.getJSONObject(j);
                    String imageId = image.getString("id");
                    String link = "https://image.lexica.art/full_webp/" + imageId;
                    urls.add(link);
                }
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
    private void clearImages() {
        imageUrls.clear();
        imageAdapter.notifyDataSetChanged();
        searchBar.setHint("Search glass");
    }
}
