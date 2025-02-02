package com.example.itsmetyping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsActivity extends AppCompatActivity {

    private EditText comment;
    private Button postComment;
    private RecyclerView recyclerView;

    private FirebaseFirestore firebaseFirestore;
    private String post_id;
    private String current_user_id;
    private FirebaseAuth firebaseAuth;

    private CommentsAdapter commentsAdapter;
    private List<Comments> myList;
    private List<Users> usersList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        comment = (EditText) findViewById(R.id.txtComment);
        postComment = findViewById(R.id.btnPostComment);
        recyclerView = findViewById(R.id.comments_recycler_view);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();

        myList = new ArrayList<>();
        usersList = new ArrayList<>();

        commentsAdapter = new CommentsAdapter(CommentsActivity.this,myList, usersList);


        post_id = getIntent().getStringExtra("post_id");

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(commentsAdapter);

        firebaseFirestore.collection("UserPosts/"+post_id+"/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.d("JK", "Error:" + error.getMessage());
                }else{
                    for(DocumentChange documentChange : value.getDocumentChanges()){
                        if(documentChange.getType() == DocumentChange.Type.ADDED){
                            Comments comments = documentChange.getDocument().toObject(Comments.class);
                            String userId = documentChange.getDocument().getString("user");

                            firebaseFirestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()){
                                        Users users = task.getResult().toObject(Users.class);
                                        usersList.add(users);
                                        myList.add(comments);
                                        commentsAdapter.notifyDataSetChanged();
                                    }else{
                                        Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });



                        }else{
                            commentsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });



        postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String my_comment = comment.getText().toString();
                if(!my_comment.isEmpty()){
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("comment", my_comment);
                    commentsMap.put("timestamp", FieldValue.serverTimestamp());
                    commentsMap.put("user",current_user_id);
                    firebaseFirestore.collection("UserPosts/"+post_id+"/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CommentsActivity.this,"Comment Added!", Toast.LENGTH_LONG).show();
                            }else{
                                Toast.makeText(CommentsActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(CommentsActivity.this, "Please write a comment!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}