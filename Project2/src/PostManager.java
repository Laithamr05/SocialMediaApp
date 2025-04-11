// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.util.Calendar;

public class PostManager implements Comparable<PostManager> { // class for managing post data and operations
    private String postID; // unique identifier for the post
    private UserManager creator; // user who created the post
    private String content; // content of the post
    private Calendar creationDate; // date when the post was created
    private CircularDoublyLinkedList<UserManager> sharedUsers; // list of users the post is shared with

    public PostManager(String postID, UserManager creator, String content, Calendar creationDate) { // constructor to initialize post
        this.postID = postID; // set post id
        this.creator = creator; // set creator
        this.content = content; // set content
        this.creationDate = creationDate; // set creation date
        this.sharedUsers = new CircularDoublyLinkedList<UserManager>(); // initialize shared users list
    }

    public String getPostID() { // getter for post id
        return postID;
    }

    public void setPostID(String postID) { // setter for post id
        this.postID = postID;
    }

    public UserManager getCreator() { // getter for creator
        return creator;
    }

    public String getContent() { // getter for content
        return content;
    }
    
    public void setContent(String content) { // setter for content
        this.content = content;
    }

    public Calendar getCreationDate() { // getter for creation date
        return creationDate;
    }

    public CircularDoublyLinkedList<UserManager> getSharedUsers() { // getter for shared users list
        return sharedUsers;
    }

    public void addSharedUser(UserManager user) { // method to add a user to shared users
        if (!sharedUsers.contains(user)) { // check if user is already in shared list
            sharedUsers.insertLast(user); // add user to the end of shared list
        }
    }
    
    public void shareWith(UserManager user) { // method to share post with a user
        addSharedUser(user); // add user to shared users
    }
    
    public void clearSharedUsers() { // method to clear all shared users
        // Create a new empty list rather than trying to clear the existing one
        this.sharedUsers = new CircularDoublyLinkedList<UserManager>(); // reset shared users list
    }

    public boolean isSharedWith(UserManager user) { // method to check if post is shared with a user
        return sharedUsers.contains(user); // check if user is in shared list
    }

    @Override
    public boolean equals(Object obj) { // method to check if two posts are equal
        if (this == obj) return true; // check if same object
        if (obj == null || getClass() != obj.getClass()) return false; // check if null or different class
        PostManager other = (PostManager) obj; // cast to PostManager
        if (postID == null && other.postID == null) return true; // both IDs null, consider equal
        if (postID == null || other.postID == null) return false; // one ID null, not equal
        return postID.equals(other.postID); // compare post IDs
    }

    @Override
    public String toString() { // method to convert post to string representation
        String creatorName = ""; // initialize creator name
        if (creator != null) { // check if creator exists
            creatorName = creator.getName(); // get creator name
        } else {
            creatorName = "Unknown"; // use "Unknown" if no creator
        }
        
        String postContent = ""; // initialize post content
        if (content != null) { // check if content exists
            postContent = content; // use actual content
        } else {
            postContent = ""; // use empty string if no content
        }
        
        return "Post ID: " + postID + ", Creator: " + creatorName + ", Content: " + postContent; // return formatted string
    }

    @Override
    public int compareTo(PostManager other) { // method to compare posts by date
        if (this == other) return 0; // same object, equal
        if (other == null) return 1; // null comes before this
        if (creationDate == null && other.creationDate == null) return 0; // both dates null, equal
        if (creationDate == null) return -1; // null date comes before non-null
        if (other.creationDate == null) return 1; // non-null date comes after null
        return other.creationDate.compareTo(creationDate); // compare by creation date (reverse order)
    }
} 
