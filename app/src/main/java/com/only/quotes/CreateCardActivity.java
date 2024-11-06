package com.only.quotes;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class CreateCardActivity extends AppCompatActivity {

    private EditText editTextCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        editTextCard = findViewById(R.id.editTextCard);
        Button btnPaste = findViewById(R.id.btnPaste);
        Button btnCreateCard = findViewById(R.id.btnCreateCard);


        btnPaste.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                ClipData clip = clipboard.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    editTextCard.setText(clip.getItemAt(0).getText());
                }
            }
        });

        // Create card and move to next page
        btnCreateCard.setOnClickListener(v -> {
            Intent intent = new Intent(CreateCardActivity.this, GenerateCardActivity.class);
            intent.putExtra("cardText", editTextCard.getText().toString());
            startActivity(intent);
        });
    }
}
