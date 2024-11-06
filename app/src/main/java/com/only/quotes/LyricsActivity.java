package com.only.quotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class LyricsActivity extends AppCompatActivity {

    private ListView lyricsListView;
    private ArrayAdapter<String> lyricsAdapter;
    private String[] lyrics = {
            "Line 1 of the song", "Line 2 of the song", "Line 3 of the song", "Line 4 of the song"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);

        lyricsListView = findViewById(R.id.lyricsListView);
        lyricsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lyrics);
        lyricsListView.setAdapter(lyricsAdapter);

        lyricsListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedLyrics = lyrics[position];
            Intent resultIntent = new Intent();
            resultIntent.putExtra("selectedLyrics", selectedLyrics);
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }
}
