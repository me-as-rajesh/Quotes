package com.only.quotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

import java.io.FileOutputStream;
import java.io.IOException;

public class PreviewActivity extends AppCompatActivity {

    private ImageView previewImage;
    private Bitmap cardBitmap;
    private Button shareButton, saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        previewImage = findViewById(R.id.previewImage);
        shareButton = findViewById(R.id.shareButton);
        saveButton = findViewById(R.id.saveButton);

        String imageUrl = getIntent().getStringExtra("imageUrl");

        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(previewImage);

        previewImage.post(() -> {
            // Capture the loaded image as a bitmap for sharing and saving
            previewImage.buildDrawingCache();
            cardBitmap = previewImage.getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
            previewImage.setImageBitmap(cardBitmap);
        });

        setupButtons();
    }

    private void setupButtons() {
        shareButton.setOnClickListener(v -> shareImage(null));

        saveButton.setOnClickListener(v -> saveImage(cardBitmap));
    }

    private void shareImage(String packageName) {
        if (cardBitmap == null) return;

        String path = saveImage(cardBitmap);
        Uri uri = Uri.parse(path);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        if (packageName != null) {
            shareIntent.setPackage(packageName);
        }

        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }

    private String saveImage(Bitmap bitmap) {
        String path = getExternalFilesDir(null) + "/shared_image.png";

        try (FileOutputStream out = new FileOutputStream(path)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }
}
