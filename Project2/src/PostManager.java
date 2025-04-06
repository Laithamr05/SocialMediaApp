import java.time.LocalDate;

public class PostManager {
    private String postID;
    private UserManager creator;
    private String content;
    private LocalDate creationDate;
    private CircularDoublyLinkedList<UserManager> sharedUsers;

    public PostManager(String postID, UserManager creator, String content, LocalDate creationDate) {
        this.postID = postID;
        this.creator = creator;
        this.content = content;
        this.creationDate = creationDate;
        this.sharedUsers = new CircularDoublyLinkedList<>();
    }

    public String getPostID() {
        return postID;
    }

    public UserManager getCreator() {
        return creator;
    }

    public String getContent() {
        return content;
    }

    public LocalDate getCreationDate() {
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
        return postID.equals(other.postID);
    }

    @Override
    public String toString() {
        return "Post ID: " + postID + ", Creator: " + creator.getName() + ", Content: " + content;
    }
} 
