App Overview

Type: Social Networking App

Core Features:

Users can make posts, which are visible on the main feed.

Users can like and comment on each other's posts.

Profile customization allows users to update their username and profile picture, with changes reflected in their previous posts and comments.

New users can sign up, and after logging in, they are directed to the account setup page to customize their profile.

Technical Features

Authentication: Uses Firebase Authentication with email and password for user sign-in.

User Profile Management: Users can upload and update their profile pictures and names.

Post Creation: Users can make posts that include a timestamp and uploaded images (with a size limit using a cropping library).

Firebase Integration:

Firebase Storage is used to store uploaded images for profile pictures and posts.

Firebase Realtime Database stores post data in collections (e.g., users, posts, comments, likes).

Each post has a unique ID, and related data (like comments and likes) is stored in subcollections for organization.

App Functionality

Main Feed: Displays posts from all users, with the newest posts at the top.

Comments and Likes: Comments include the user's name and profile picture. Each post can be liked or disliked, and the count is tracked.

User Experience:

If the app loses connectivity, users are directed to the account setup page upon reopening instead of the main feed.

Firebase Database Structure

Users Collection: Each user has a unique ID with profile details.

Posts Collection: Each post has a unique ID and includes information like timestamp, image URL, and comments/likes subcollections.

Comments Subcollection: Stores comments on posts, including the user’s ID to fetch their profile data.

Likes: Tracks users who like or dislike posts by their user ID.
