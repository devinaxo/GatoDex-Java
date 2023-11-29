package com.devinaxo.gatodex_java;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity {

    private CatDbHelper dbHelper;
    private CatAdapter catAdapter;
    private TextView emptyView;
    private static final int PICK_IMAGE_REQUEST = 1;
    private String selectedImagePath;
    private ImageView selectedImageView;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int REQUEST_IMAGE_PICKER = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.catToolbar));

        dbHelper = new CatDbHelper(this);
        catAdapter = new CatAdapter(this, dbHelper.getAllCats(), 0, this);

        GridView catGrid = findViewById(R.id.catGrid);
        catGrid.setAdapter(catAdapter);
        emptyView = findViewById(R.id.emptyView);
        updateEmptyViewVisibility();

        catGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showCatDetailDialog(id);
            }
        });

        Button addCatButton = findViewById(R.id.addCatButton);
        addCatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCatDialog();
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        startActivity(new Intent(this, AboutActivity.class));
        return true;
    }

    void showCatDetailDialog(long catId) {
        CatDetailDialogFragment dialogFragment = CatDetailDialogFragment.newInstance(catId);
        dialogFragment.show(getSupportFragmentManager(), "cat_detail_dialog");
    }

    private void showAddCatDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_cat_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextNickname = dialogView.findViewById(R.id.editTextNickname);
        final EditText editTextType = dialogView.findViewById(R.id.editTextType);
        final EditText editTextPlaceMet = dialogView.findViewById(R.id.editTextPlaceMet);
        final EditText editTextDateMet = dialogView.findViewById((R.id.editTextDateMet));
        final Button buttonSelectImage = dialogView.findViewById(R.id.buttonSelectImage);
        selectedImageView = dialogView.findViewById(R.id.selectedImageView);

        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the image gallery to select an image
                chooseImage();
            }
        });

        dialogBuilder.setPositiveButton("Add", (dialog, which) -> {
            // Handle the "Add" button click
            String nickname = editTextNickname.getText().toString();
            String type = editTextType.getText().toString();
            String placeMet = editTextPlaceMet.getText().toString();
            String dateMet = editTextDateMet.getText().toString();

            if(TextUtils.isEmpty(nickname) || TextUtils.isEmpty(type) || TextUtils.isEmpty(placeMet) || TextUtils.isEmpty(dateMet) || TextUtils.isEmpty(selectedImagePath)){
                Toast.makeText(this, "Please fill all fields and/or select an image.", Toast.LENGTH_SHORT).show();
                selectedImagePath = "";
            }else {
                // Add the cat to the database
                dbHelper.addCat(new Cat(nickname, type, placeMet, selectedImagePath, dateMet));

                // Update the adapter with the latest data
                catAdapter.changeCursor(dbHelper.getAllCats());
                updateEmptyViewVisibility();

                selectedImagePath = "";
            }
        });

        dialogBuilder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void showImage(ImageView img, Uri selectedImageUri){
        if(selectedImageUri != null){
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(img);

            img.setVisibility(View.VISIBLE);
        }
    }


    private void chooseImage() {
        // Check if permission is not granted
        if (ContextCompat.checkSelfPermission(this, getReadStoragePermission())
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{getReadStoragePermission()},
                    REQUEST_STORAGE_PERMISSION);

        } else {
            // Permission is already granted, proceed with image selection
            openImagePicker();
        }
    }

    //Checks to see whether old or new permissions get used
    private String getReadStoragePermission() {
        // Check the Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above, use the new storage permissions
            return Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            // For versions below Android 13, use the old storage permission
            return Manifest.permission.READ_EXTERNAL_STORAGE;
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICKER);
    }


    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with image selection
                openImagePicker();
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(this, "Permission denied. Image can't be chosen.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
            // Handle the selected image URI
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                // Use the selectedImageUri as needed
                // You might want to store it for later use in showAddCatDialog
                selectedImagePath = getPathFromUri(selectedImageUri);
                showImage(selectedImageView, selectedImageUri);
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String imagePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            imagePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return imagePath;
    }

    void updateEmptyViewVisibility() {
        emptyView.setVisibility(catAdapter.getCount() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update the adapter with the latest data when the activity is resumed
        catAdapter.changeCursor(dbHelper.getAllCats());
        updateEmptyViewVisibility();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database helper to avoid memory leaks
        dbHelper.close();
    }
    public CatDbHelper getDbHelper() {
        return dbHelper;
    }
}
