package com.example.itsmetyping;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.auth.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.MyViewHolder> {

    private Activity context;
    private List<Users> usersList;
    private List<Comments> commentsList;

    public CommentsAdapter(Activity context, List<Comments> commentsList, List<Users> usersList){
        this.context = context;
        this.commentsList = commentsList;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_row,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Comments comments = commentsList.get(position);
        holder.setMy_comment(comments.getComment());

        Users users = usersList.get(position);
        holder.setTxtUsername(users.getName());
        holder.setCircleImageView(users.getImage());
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView my_comment, txtUsername;
        View my_view;
        CircleImageView circleImageView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            my_view = itemView;
        }
        public void setMy_comment(String user_comment){
            my_comment = my_view.findViewById(R.id.comment_description);
            my_comment.setText(user_comment);
        }

        public void setTxtUsername(String username){
            txtUsername = my_view.findViewById(R.id.txtUsername);
            txtUsername.setText(username);
        }

        public void setCircleImageView(String  user_dp){
            circleImageView = my_view.findViewById(R.id.user_pic);
            Glide.with(context).load(user_dp).into(circleImageView);

        }
    }
}
