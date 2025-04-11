import java.util.Calendar;

public class PostManager implements Comparable<PostManager> {
    private String postID;
    private UserManager creator;
    private String content;
    private Calendar creationDate;
    private CircularDoublyLinkedList<UserManager> sharedUsers;

    public PostManager(String postID, UserManager creator, String content, Calendar creationDate) {
        this.postID = postID;
        this.creator = creator;
        this.content = content;
        this.creationDate = creationDate;
        this.sharedUsers = new CircularDoublyLinkedList<UserManager>();
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public UserManager getCreator() {
        return creator;
    }

    public String getContent() {
        return content;
    }

    public Calendar getCreationDate() {
        return creationDate;
    }

    public CircularDoublyLinkedList<UserManager> getSharedUsers() {
        return sharedUsers;
    }

    public void addSharedUser(UserManager user) {
        if (!sharedUsers.contains(user)) {
            sharedUsers.insertLast(user);
        }
    }

    public boolean isSharedWith(UserManager user) {
        return sharedUsers.contains(user);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PostManager other = (PostManager) obj;
        if (postID == null && other.postID == null) return true;
        if (postID == null || other.postID == null) return false;
        return postID.equals(other.postID);
    }

    @Override
    public String toString() {
        String creatorName = "";
        if (creator != null) {
            creatorName = creator.getName();
        } else {
            creatorName = "Unknown";
        }
        
        String postContent = "";
        if (content != null) {
            postContent = content;
        } else {
            postContent = "";
        }
        
        return "Post ID: " + postID + ", Creator: " + creatorName + ", Content: " + postContent;
    }

    @Override
    public int compareTo(PostManager other) {
        if (this == other) return 0;
        if (other == null) return 1;
        if (creationDate == null && other.creationDate == null) return 0;
        if (creationDate == null) return -1;
        if (other.creationDate == null) return 1;
        return other.creationDate.compareTo(creationDate);
    }
} 
