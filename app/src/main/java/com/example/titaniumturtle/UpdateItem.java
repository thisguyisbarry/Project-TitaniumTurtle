package com.example.titaniumturtle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateItem extends AppCompatActivity {
    public static final int CAMERA_PERMISSION = 101;
    public static final int CAMERA_REQUEST = 102;
    public static final int GALLERY_REQUEST = 103;
    ImageButton cameraButton, galleryButton;
    EditText item_name, item_note;
    ImageView item_pic;
    Button update_button, delete_button;
    String id, name, note, currentPhotoPath, savePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_item);

        item_name = findViewById(R.id.updateName);
        item_note = findViewById(R.id.updateNote);
        item_pic = findViewById(R.id.updateImage);
        update_button = findViewById(R.id.updateBtn);
        delete_button = findViewById(R.id.deleteBtn);
        cameraButton = findViewById(R.id.cameraButton);
        galleryButton = findViewById(R.id.galleryButton);

        getData();

        cameraButton.setOnClickListener(v -> askCameraPermission());

        galleryButton.setOnClickListener(v ->  {

            Intent gallery = new Intent(Intent.ACTION_OPEN_DOCUMENT,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST);

        });


        update_button.setOnClickListener(v -> {
            Database myDB = new Database(UpdateItem.this);
            name = item_name.getText().toString().trim();
            note = item_note.getText().toString().trim();
            myDB.updateItem(id, name, note, savePhoto);
        });

        delete_button.setOnClickListener(v ->{
            Database myDB = new Database(UpdateItem.this);
            myDB.deleteItem(id);
            finish();
        });
    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }else {
            dispatchTakePictureIntent();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERMISSION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                dispatchTakePictureIntent();
            }else {
                Toast.makeText(this, R.string.denied_camera, Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            File f = new File(currentPhotoPath);
            item_pic.setImageURI(Uri.fromFile(f));
            savePhoto = Uri.fromFile(f).toString();
            Log.d("tag", "Camera Uri " + Uri.fromFile(f));
            Log.d("tag", "Camera Uri string: " + savePhoto);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);

        }

        if (requestCode == GALLERY_REQUEST && resultCode == Activity.RESULT_OK) {

            Uri contentUri = data.getData();
            Log.d("tag", "Gallery Uri:  " + contentUri);
            item_pic.setImageURI(contentUri);
            savePhoto = contentUri.toString();
            Log.d("tag", "Gallery Uri string:  " + savePhoto);

        }

    }



    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    // Reference: The following code is from Android example https://developer.android.com/training/camera/photobasics#TaskGallery
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.titaniumturtle.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    void getData(){
        if(getIntent().hasExtra("id") && getIntent().hasExtra("name") && getIntent().hasExtra("note") && getIntent().hasExtra("pic")){
             id = getIntent().getStringExtra("id");
             name = getIntent().getStringExtra("name");
             note = getIntent().getStringExtra("note");
             savePhoto = getIntent().getStringExtra("pic");

            item_name.setText(name);
            item_note.setText(note);
            item_pic.setImageURI(Uri.parse(String.valueOf(savePhoto)));

        }else{
            Toast.makeText(this, "No data", Toast.LENGTH_LONG).show();
        }
    }

}