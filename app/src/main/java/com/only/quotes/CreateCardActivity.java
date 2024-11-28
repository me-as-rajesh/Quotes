package com.only.quotes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;

public class CreateCardActivity extends AppCompatActivity implements View.OnClickListener, StickerBSFragment.StickerListener {

    ImageView imagePhotoEditBack, imagePhotoEditUndo, imagePhotoEditRedo,
            imagePhotoEditCrop, imagePhotoEditSticker, imagePhotoEditText, imagePhotoEditPaint;
    VerticalSlideColorPicker colorPickerView;
    PhotoEditorView photoEditorView;
    FloatingActionButton fabPhotoDone;

    private PhotoEditor mPhotoEditor;
    private StickerBSFragment mStickerBSFragment;
    private int mSelectedColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_card);

        imagePhotoEditBack = findViewById(R.id.img_photo_edit_back);
        imagePhotoEditUndo = findViewById(R.id.img_photo_edit_undo);
        imagePhotoEditRedo = findViewById(R.id.img_photo_edit_redo);
        imagePhotoEditCrop = findViewById(R.id.img_photo_edit_crop);
        imagePhotoEditSticker = findViewById(R.id.img_photo_edit_stickers);
        imagePhotoEditText = findViewById(R.id.img_photo_edit_text);
        imagePhotoEditPaint = findViewById(R.id.img_photo_edit_paint);

        photoEditorView = findViewById(R.id.photo_editor_view);
        fabPhotoDone = findViewById(R.id.fab_photo_done);

        mPhotoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .build();

        colorPickerView = findViewById(R.id.color_picker_view);
        colorPickerView.setOnColorChangeListener(selectedColor -> {
            mSelectedColor = selectedColor;
            if (colorPickerView.getVisibility() == View.VISIBLE) {
                imagePhotoEditPaint.setBackgroundColor(selectedColor);
                mPhotoEditor.setBrushColor(selectedColor);
            }
        });

        // Set OnClickListeners
        imagePhotoEditBack.setOnClickListener(this);
        imagePhotoEditUndo.setOnClickListener(this);
        imagePhotoEditRedo.setOnClickListener(this);
        imagePhotoEditCrop.setOnClickListener(this);
        imagePhotoEditSticker.setOnClickListener(this);
        imagePhotoEditText.setOnClickListener(this);
        imagePhotoEditPaint.setOnClickListener(this);
        fabPhotoDone.setOnClickListener(this);

        mStickerBSFragment = new StickerBSFragment();
        mStickerBSFragment.setStickerListener(this);

        String imageUrl = getIntent().getStringExtra("imageUrl");
        Glide.with(this)
                .load(imageUrl)
                .into(photoEditorView.getSource());
/*        try {

            Uri imageUri = Uri.parse(imageUrl);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            photoEditorView.getSource().setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_photo_edit_back) {
            finish();
        } else if (id == R.id.img_photo_edit_undo) {
            mPhotoEditor.undo();
        } else if (id == R.id.img_photo_edit_redo) {
            mPhotoEditor.redo();
        } else if (id == R.id.img_photo_edit_crop) {
            // Handle crop action
        } else if (id == R.id.img_photo_edit_stickers) {
            ShowBrush(false);
            mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
        } else if (id == R.id.img_photo_edit_text) {
            ShowBrush(false);
            TextEditorDialogFragment textEditorDialogFragment = TextEditorDialogFragment.show(this);
            textEditorDialogFragment.setOnTextEditorListener((inputText, colorCode) -> {
                mPhotoEditor.addText(inputText, colorCode);
            });
        } else if (id == R.id.img_photo_edit_paint) {
            if (colorPickerView.getVisibility() == View.VISIBLE) {
                ShowBrush(false);
            } else {
                ShowBrush(true);
            }
        } else if (id == R.id.fab_photo_done) {
            saveImage();
        }
    }


    @Override
    public void onStickerClick(Bitmap bitmap) {
        mPhotoEditor.addImage(bitmap);
    }

    private void ShowBrush(boolean enableBrush) {
        if (enableBrush) {
            mPhotoEditor.setBrushColor(mSelectedColor);
            imagePhotoEditPaint.setBackgroundColor(mSelectedColor);
            mPhotoEditor.setBrushDrawingMode(true);
            colorPickerView.setVisibility(View.VISIBLE);
        } else {
            imagePhotoEditPaint.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
            mPhotoEditor.setBrushDrawingMode(false);
            colorPickerView.setVisibility(View.INVISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        File folder = new File(Environment.getExternalStorageDirectory() + "/PhotoEditing");
        if (!folder.isDirectory()) {
            folder.mkdirs();
        }

        File file = new File(folder, System.currentTimeMillis() + ".png");

        try {
            file.createNewFile();

            SaveSettings saveSettings = new SaveSettings.Builder()
                    .setClearViewsEnabled(true)
                    .setTransparencyEnabled(true)
                    .build();

            mPhotoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                @Override
                public void onSuccess(@NonNull String imagePath) {
                    Toast.makeText(CreateCardActivity.this, "Image Saved Successfully", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK, new Intent().putExtra("imagePath", imagePath));
                    finish();
                }

                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(CreateCardActivity.this, "Failed to save Image", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}
