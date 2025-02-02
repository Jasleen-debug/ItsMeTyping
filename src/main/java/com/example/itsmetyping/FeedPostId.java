package com.example.itsmetyping;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

public class FeedPostId {
    @Exclude
    public String FeedPostId;

    public <T extends FeedPostId> T withId (@NonNull final String id){
        this.FeedPostId = id;
        return (T) this;
    }
}
