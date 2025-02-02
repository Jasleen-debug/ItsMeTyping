package com.example.itsmetyping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class AddPostActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private EditText etTitle;
    private EditText etDescription;
    private Button submitButton;

    private Uri imageUri = null;

    private StorageReference storageReference;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String current_user_id;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        //Directs to root directory of firebase storage
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        progress = new ProgressDialog(this);
        submitButton = findViewById(R.id.button_submit);
        submitButton.setOnClickListener(submitButtonClickListener);

        imageButton = findViewById(R.id.imageButton);
        imageButton.setOnClickListener(imageButtonClickListener);

    }

    private View.OnClickListener imageButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMinCropResultSize(512,512)
                    .setAspectRatio(1,1)
                    .start(AddPostActivity.this);
        }
    };

    private View.OnClickListener submitButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startBlogPosting();
        }
    };

    private void startBlogPosting() {

        progress.setMessage("Posting to feed...");
        progress.show();
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description) && imageUri != null){

            final String randomName = UUID.randomUUID().toString();

            StorageReference filepath = storageReference.child("post_images").child(randomName + ".jpg");
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri dlUri = uri;

                            Map<String, Object> postMap = new HashMap<>();
                            postMap.put("image", dlUri.toString());
                            postMap.put("title",title);
                            postMap.put("description",description);
                            postMap.put("user_id",current_user_id);
                            postMap.put("timestamp",FieldValue.serverTimestamp());

                            firebaseFirestore.collection("UserPosts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(AddPostActivity.this, "New Post added!", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(AddPostActivity.this, MainActivity.class));
                                        finish();
                                    }else{
                                        String error = task.getException().getMessage();
                                        Toast.makeText(AddPostActivity.this, "Error: "+error, Toast.LENGTH_LONG).show();
                                    }
                                    progress.dismiss();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddPostActivity.this, "Image Error: " + e.toString(), Toast.LENGTH_LONG).show();
                    progress.dismiss();
                }
            });
        }else{
            Toast.makeText(AddPostActivity.this, "All the fields are required!!", Toast.LENGTH_LONG).show();
            progress.dismiss();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                imageUri = result.getUri();
                imageButton.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
            }
        }
    }

}