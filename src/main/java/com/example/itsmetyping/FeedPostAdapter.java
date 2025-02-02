package com.example.itsmetyping;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedPostAdapter extends RecyclerView.Adapter<FeedPostAdapter.MyViewHolder> {

    ArrayList<FeedPost> feedPostList;
    Context context;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;

    public FeedPostAdapter(Context context, ArrayList<FeedPost> feedPostList){
        this.feedPostList = feedPostList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.feed_row, parent,false);
        firebaseFirestore  = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new MyViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FeedPost feedPost = feedPostList.get(position);
        holder.title.setText(feedPost.getTitle());
        holder.description.setText(feedPost.getDescription());
        Glide.with(context).load(feedPostList.get(position).getImage()).into(holder.imageView);

        String user_id = feedPostList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName = task.getResult().getString("name");
                    String userImage = task.getResult().getString("image");
                    holder.setUserData(userName, userImage);
                }else{
                    Log.d("JK", "There is some error!");
                }
            }
        });

        long millisecond = feedPost.getTimestamp().getTime();
        Log.d("JK", String.valueOf(millisecond));
        String dateString = DateFormat.format("MMMM dd, yyyy", new Date(millisecond)).toString();

        holder.date.setText(dateString);

        // Like Pictures
        String postId = feedPost.FeedPostId;
        String current_user_id = firebaseAuth.getCurrentUser().getUid();
        holder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseFirestore.collection("UserPosts/" + postId + "/Likes").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firebaseFirestore.collection("UserPosts/" + postId + "/Likes").document(current_user_id).set(likesMap);
                        }else{
                            firebaseFirestore.collection("UserPosts/" + postId + "/Likes").document(current_user_id).delete();
                        }
                    }
                });
            }
        });

        // Like color change
        firebaseFirestore.collection("UserPosts/"+postId+"/Likes").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    if(value.exists()){
                        holder.imgLike.setImageDrawable(context.getDrawable(R.mipmap.outline_favorite_black_24));
                    }else{
                        holder.imgLike.setImageDrawable(context.getDrawable(R.mipmap.outline_favorite_border_black_24));
                    }
                }
            }
        });

        // Like Count
        firebaseFirestore.collection("UserPosts/"+postId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error == null){
                    if(!value.isEmpty()){
                        int count = value.size();
                        holder.setPostLikes(count);
                    }else{
                        holder.setPostLikes(0);
                    }
                }
            }
        });

      // Comments
       holder.imgComment.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            Intent intent = new Intent(context, CommentsActivity.class);
              intent.putExtra("post_id",postId);
              context.startActivity(intent);
         }
      });
    }

    @Override
    public int getItemCount() {
        return feedPostList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView, imgLike, imgComment;
        TextView title, txtLikeCount;
        TextView description;

        TextView username;
        CircleImageView circleImageView;

        TextView date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assign variables
            imageView = itemView.findViewById(R.id.post_image);
            title = itemView.findViewById(R.id.post_title);
            description = itemView.findViewById(R.id.post_description);

            username = itemView.findViewById(R.id.txtUsername);
            circleImageView = itemView.findViewById(R.id.user_thumbnail);
            date = (TextView) itemView.findViewById(R.id.txtTimestamp);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgComment = itemView.findViewById(R.id.imgComment);

        }

        public void setPostLikes(int count){
            txtLikeCount = itemView.findViewById(R.id.txtLikeCount);
            if(count == 1){
                txtLikeCount.setText(count + " Like ");
            }else{
                txtLikeCount.setText(count + " Likes");
            }

        }

        public void setUserData(String name, String image){
            username.setText(name);
            Glide.with(context).load(image).into(circleImageView);
        }
    }
}
