// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.util.Calendar;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.util.Iterator;

// Handles post data and sharing functionality in the social network application
public class PostManager implements Comparable<PostManager> {
    // Post identification and content information
    private String postID;
    private UserManager creator;
    private String content;
    private Calendar creationDate;
    
    // List of users this post is shared with
    private CircularDoublyLinkedList<UserManager> sharedUsers;
    
    // Properties for table display
    private SimpleStringProperty postIDProperty;
    private SimpleStringProperty contentProperty;
    private SimpleStringProperty dateProperty;
    private SimpleStringProperty authorProperty;

    // Creates a new post with the given information
    public PostManager(String postID, UserManager creator, String content, Calendar creationDate) {
        this.postID = postID;
        this.creator = creator;
        this.content = content;
        this.creationDate = creationDate;
        this.sharedUsers = new CircularDoublyLinkedList<UserManager>();
        
        // Initialize table display properties
        this.postIDProperty = new SimpleStringProperty(postID);
        this.contentProperty = new SimpleStringProperty(content);
        this.dateProperty = new SimpleStringProperty(formatDate(creationDate));
        
        // Set author name for display
        String authorName = creator != null ? creator.getName() : "Unknown";
        this.authorProperty = new SimpleStringProperty(authorName);
    }
    
    // Formats a Calendar date into a readable string (DD.MM.YYYY)
    private String formatDate(Calendar date) {
        if (date == null) return "";
        return String.format("%02d.%02d.%d", 
            date.get(Calendar.DAY_OF_MONTH),
            date.get(Calendar.MONTH) + 1,
            date.get(Calendar.YEAR));
    }

    // Creates a new post and optionally shares it with all friends
    public static PostManager createPost(String postID, UserManager creator, String content, Calendar creationDate, boolean shareWithAllFriends) {
        PostManager newPost = new PostManager(postID, creator, content, creationDate);
        if (shareWithAllFriends && creator != null) {
            newPost.shareWithAllFriends();
        }
        return newPost;
    }

    // Basic getters and setters for post properties
    public String getPostID() { return postID; }
    public void setPostID(String postID) { 
        this.postID = postID;
        this.postIDProperty.set(postID);
    }
    
    public UserManager getCreator() { return creator; }
    public void setCreator(UserManager creator) { 
        this.creator = creator;
        this.authorProperty.set(creator != null ? creator.getName() : "Unknown");
    }
    
    public String getContent() { return content; }
    public void setContent(String content) { 
        this.content = content;
        this.contentProperty.set(content);
    }
    
    public Calendar getCreationDate() { return creationDate; }
    public void setCreationDate(Calendar creationDate) { 
        this.creationDate = creationDate;
        this.dateProperty.set(formatDate(creationDate));
    }

    // Returns the list of users this post is shared with
    public CircularDoublyLinkedList<UserManager> getSharedUsers() { return sharedUsers; }

    // Shares this post with a specific user
    public void shareWith(UserManager user) {
        if (!sharedUsers.contains(user)) {
            sharedUsers.insertLast(user);
        }
    }
    
    // Shares this post with all friends of the creator
    public void shareWithAllFriends() {
        if (creator == null) return;
        CircularDoublyLinkedList<UserManager> friends = creator.getFriends();
        if (friends == null) return;
        
        Iterator<UserManager> iterator = friends.iterator();
        while (iterator.hasNext()) {
            UserManager friend = iterator.next();
            if (friend != null) {
                shareWith(friend);
            }
        }
    }

    // Returns a string representation of the post
    @Override
    public String toString() {
        String creatorName = creator != null ? creator.getName() : "Unknown";
        String postContent = content != null ? content : "";
        return "Post ID: " + postID + ", Creator: " + creatorName + ", Content: " + postContent;
    }

    // Compares posts by their creation dates for sorting
    @Override
    public int compareTo(PostManager other) {
        if (this == other) return 0;
        if (other == null) return 1;
        if (creationDate == null && other.creationDate == null) return 0;
        if (creationDate == null) return -1;
        if (other.creationDate == null) return 1;
        return other.creationDate.compareTo(creationDate);
    }

    // Getter methods for table display properties
    public SimpleStringProperty postIDProperty() { return postIDProperty; }
    public SimpleStringProperty contentProperty() { return contentProperty; }
    public SimpleStringProperty dateProperty() { return dateProperty; }
    public SimpleStringProperty authorProperty() { return authorProperty; }
} 
