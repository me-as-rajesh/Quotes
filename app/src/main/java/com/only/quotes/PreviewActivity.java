package com.only.quotes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;

public class PreviewActivity extends AppCompatActivity {

    private ImageView previewImage;
    private Bitmap cardBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        previewImage = (ImageView) findViewById(R.id.previewImage);

        String imageUrl = getIntent().getStringExtra("imageUrl");
        String cardText = getIntent().getStringExtra("cardText");

        // Load the image using Glide
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(previewImage);

        previewImage.post(() -> generateCardWithText(cardText));
    }

    private void generateCardWithText(String cardText) {
        // Get the bitmap from the ImageView
        previewImage.buildDrawingCache();
        Bitmap bitmap = previewImage.getDrawingCache();

        // Create a new bitmap with text drawn on it
        Bitmap cardBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(cardBitmap);

        // Set up the text paint
        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40);
        textPaint.setAntiAlias(true);

        // Draw the text on the canvas
        canvas.drawText(cardText, 50, cardBitmap.getHeight() - 100, textPaint);

        // Display the new card with text
        previewImage.setImageBitmap(cardBitmap);
    }
    private void setupSocialMediaButtons() {
        Button shareInsta = findViewById(R.id.shareInsta);
        Button shareFacebook = findViewById(R.id.shareFacebook);
        Button shareThreads = findViewById(R.id.shareThreads);
        Button shareWhatsApp = findViewById(R.id.shareWhatsApp);
        Button shareMore = findViewById(R.id.shareMore);

        shareInsta.setOnClickListener(v -> shareImage("com.instagram.android"));
        shareFacebook.setOnClickListener(v -> shareImage("com.facebook.katana"));
        shareThreads.setOnClickListener(v -> shareImage("com.threads.app"));
        shareWhatsApp.setOnClickListener(v -> shareImage("com.whatsapp"));
        shareMore.setOnClickListener(v -> shareImage(null)); // Share to any other app
    }

    private void shareImage(String packageName) {
        String path = saveImage(cardBitmap);  // Save the image and get its path
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
        // Logic to save the image and return the file path
        return "";  // Replace with actual file path
    }
}
