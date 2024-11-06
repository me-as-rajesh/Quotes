package com.only.quotes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class CreateCardActivity extends AppCompatActivity {

    private EditText editTextCard;
    private SeekBar seekBarOpacity;
    private TextView opacityLabel;
    private Button btnPaste;
    private Button btnLyrics;
    private Button btnPreview;
    private ImageView imageView;
    private static final int REQUEST_CODE_LYRICS = 1;
    private String imageUrl;
    private float currentOpacity = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        editTextCard = findViewById(R.id.editTextCard);
        btnPaste = findViewById(R.id.btnPaste);
        btnLyrics = findViewById(R.id.btnlyrics);
        btnPreview = findViewById(R.id.btnPreview);
        opacityLabel = findViewById(R.id.opacityLabel);
        seekBarOpacity = findViewById(R.id.seekBarOpacity);
        imageView = findViewById(R.id.imageView);

        imageUrl = getIntent().getStringExtra("imageUrl");
        loadImage(imageUrl);

        btnPaste.setOnClickListener(v -> pasteFromClipboard());

        btnLyrics.setOnClickListener(v -> {
            Intent intent = new Intent(CreateCardActivity.this, LyricsActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LYRICS);
        });

        setupOpacitySeekBar();

        btnPreview.setOnClickListener(v -> openPreview());
    }

    private void loadImage(String url) {
        if (url != null) {
            Glide.with(this)
                    .load(url)
                    .apply(new RequestOptions().placeholder(R.drawable.ic_launcher_background))
                    .into(imageView);
        }
    }

    private void pasteFromClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData clip = clipboard.getPrimaryClip();
            if (clip != null && clip.getItemCount() > 0) {
                editTextCard.setText(clip.getItemAt(0).getText());
            }
        }
    }

    private void setupOpacitySeekBar() {
        seekBarOpacity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                currentOpacity = progress / 100f;
                opacityLabel.setText("Opacity: " + progress + "%");
                imageView.setAlpha(currentOpacity);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void openPreview() {
        Intent intent = new Intent(CreateCardActivity.this, PreviewActivity.class);
        intent.putExtra("imageUrl", imageUrl);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LYRICS && resultCode == RESULT_OK && data != null) {
            String selectedLyrics = data.getStringExtra("selectedLyrics");
            if (selectedLyrics != null) {
                editTextCard.setText(selectedLyrics);
            }
        }
    }
}
