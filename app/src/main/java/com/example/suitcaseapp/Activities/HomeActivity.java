package com.example.suitcaseapp.Activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.suitcaseapp.R;
import com.example.suitcaseapp.Upload;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    // Constants for different actions
    private static final int GALLERY_CODE = 1;
    private static final int CAMERA_CODE = 2;

    private static final int REQUEST_CODE = 101;

    private static final int RESULT_OK = -1;

    TextView userEmail;
    EditText tobuyItem, itemPrice, todoDescription;
    Button logoutBtn, saveTodo, viewTodo;

    private ImageView addPhotoButton;
    private ImageView addFileButton;
    private ImageView imageView;

    private Uri imageUri;

    // Firebase Variables
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef;
    StorageReference StorageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.example.suitcaseapp.R.layout.activity_home);
        // Matching the textViews with the IDs

        userEmail = findViewById(com.example.suitcaseapp.R.id.userEmail);
        tobuyItem = findViewById(com.example.suitcaseapp.R.id.item_name);
        itemPrice = findViewById(com.example.suitcaseapp.R.id.item_price);
        todoDescription = findViewById(com.example.suitcaseapp.R.id.new_description);

        saveTodo = findViewById(com.example.suitcaseapp.R.id.saveTodo);
        saveTodo.setOnClickListener(this);

        logoutBtn = findViewById(com.example.suitcaseapp.R.id.logoutBtn);
        logoutBtn.setOnClickListener(this);

        viewTodo = findViewById(com.example.suitcaseapp.R.id.viewTodo);
        viewTodo.setOnClickListener(this);

        addPhotoButton = findViewById(com.example.suitcaseapp.R.id.cameraButton);
        addFileButton = findViewById(com.example.suitcaseapp.R.id.fileButton);

        imageView = findViewById(com.example.suitcaseapp.R.id.post_imageView);

        addFileButton.setOnClickListener(this);
        addPhotoButton.setOnClickListener(this);



        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mRef = FirebaseDatabase.getInstance().getReference("Photo Uploads");
        StorageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        try {
            // Welcome message for entering the page
            int index = user.getEmail().indexOf("@");
            userEmail.setText("Hello, " + user.getEmail().substring(0, index) + "!");
        }
        catch (NullPointerException e)
        {
            userEmail.setText(R.string.welcome_text);
            Log.d("FSLog", "User is not logged in");
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == com.example.suitcaseapp.R.id.logoutBtn){
            mAuth.signOut();

            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else if(id == com.example.suitcaseapp.R.id.saveTodo) {
            // Saves the data to the database
            SaveData();
        }
        else if(id == com.example.suitcaseapp.R.id.viewTodo) {
            Intent intent = new Intent(HomeActivity.this, viewToDoActivity.class);
            startActivity(intent);

        }
        else if(id == com.example.suitcaseapp.R.id.cameraButton)
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, CAMERA_CODE);

        }

        else if(id == R.id.fileButton)
        {
            Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, REQUEST_CODE);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            imageView.setImageURI(imageUri);
        }

    }


    private void SaveData()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();



        String title = tobuyItem.getText().toString().trim();
        String price = itemPrice.getText().toString().trim();
        String description = todoDescription.getText().toString().trim();
        Boolean purchased = false;


        if(title.isEmpty() || title.length() < 3)
        {
            showError(tobuyItem, "Item is not valid");
        }
        else if (price.isEmpty() || !price.startsWith("$"))
        {
            showError(itemPrice, "Price is not valid" );
        }
        else if (description.isEmpty())
        {
            showError(todoDescription, "Please enter a description for the item" );
        }
        else if (imageUri == null)
        {
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show();
        }
        else
        {

            Map<String, Object> holiday_item = new HashMap<>();

            holiday_item.put("title", title);
            holiday_item.put("price", price);
            holiday_item.put("desc", description);
            holiday_item.put("timeAdded", new Timestamp(new Date()));
            holiday_item.put("email", user.getEmail());
            holiday_item.put("image", imageUri.toString());
            holiday_item.put("purchased", purchased);

            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            // Get the Uri of the file to upload
            Uri fileUri = imageUri;

            // Create a StorageReference with a unique name (e.g., using timestamp)
            StorageReference fileRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");


            fileRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // File successfully uploaded
                        // You can get the download URL or perform other actions here
                        fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = imageUri.toString();
                            Upload upload = new Upload(title.trim(), downloadUrl);
                            //upload.setUri(imageUri);
                            upload.setImageUrl(downloadUrl);
                            String uploadId = mRef.push().getKey();
                            mRef.child(uploadId).setValue(upload);
                        }).addOnFailureListener(exception -> {

                        });
                    })
                    .addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                    });


            // Upload file to Firebase Storage
            fileRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(HomeActivity.this, "New Holiday Item added!", Toast.LENGTH_LONG).show();
                    String new_url = taskSnapshot.getUploadSessionUri().toString();
                    Upload upload = new Upload(title.trim(), new_url);
                    //upload.setUri(imageUri);
                    upload.setImageUrl(new_url);
                    String uploadId = mRef.push().getKey();
                    mRef.child(uploadId).setValue(upload);
                }
            }).addOnFailureListener(exception -> {

            });


            // Add a new document with a generated ID
            db.collection("holiday_items")
                    .add(holiday_item)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(HomeActivity.this, "New Holiday Item added!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(HomeActivity.this, viewToDoActivity.class);
                            startActivity(intent);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                            Toast.makeText(HomeActivity.this, "Error adding item to list", Toast.LENGTH_LONG).show();
                        }
                    });

                  {

            }

        }
    }

    private void showError(EditText input, String s)
    {
        input.setError(s);
        input.requestFocus();
    }
}