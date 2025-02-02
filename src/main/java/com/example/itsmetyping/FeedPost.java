package com.example.itsmetyping;


import java.util.Date;

public class FeedPost extends FeedPostId{
    private String user_id, title, description, image;
    private Date timestamp;

    private FeedPost(){

    }

    public FeedPost(String title, String description, String image, String user_id, Date timestamp) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
