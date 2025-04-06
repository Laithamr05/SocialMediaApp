import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import java.time.LocalDate;

public class Post {
	private SimpleStringProperty postID;
	private UserManager creator;
	private SimpleStringProperty content;
	private SimpleStringProperty date;
	private CircularDoublyLinkedList<UserManager> sharedWith;
	private SimpleStringProperty author;

	public Post() {
		this.postID = new SimpleStringProperty();
		this.content = new SimpleStringProperty();
		this.date = new SimpleStringProperty();
		this.sharedWith = new CircularDoublyLinkedList<>();
		this.author = new SimpleStringProperty();
	}

	public Post(String postID, UserManager creator, String content, String date, CircularDoublyLinkedList<UserManager> sharedWith) {
		this.postID = new SimpleStringProperty(postID);
		this.creator = creator;
		this.content = new SimpleStringProperty(content);
		this.date = new SimpleStringProperty(date);
		this.sharedWith = sharedWith;
		this.author = new SimpleStringProperty();
	}

	public String getPostID() {
		return postID.get();
	}

	public void setPostID(String postID) {
		this.postID.set(postID);
	}

	public StringProperty postIDProperty() {
		return postID;
	}

	public UserManager getCreator() {
		return creator;
	}

	public void setCreator(UserManager creator) {
		this.creator = creator;
	}

	public String getContent() {
		return content.get();
	}

	public void setContent(String content) {
		this.content.set(content);
	}

	public StringProperty contentProperty() {
		return content;
	}

	public String getDate() {
		return date.get();
	}

	public void setDate(String date) {
		this.date.set(date);
	}

	public StringProperty dateProperty() {
		return date;
	}

	public CircularDoublyLinkedList<UserManager> getSharedWith() {
		return sharedWith;
	}

	public void setSharedWith(CircularDoublyLinkedList<UserManager> sharedWith) {
		this.sharedWith = sharedWith;
	}

	public String getAuthor() {
		return author.get();
	}

	public void setAuthor(String author) {
		if (author == null || author.isEmpty()) {
			throw new CustomException("Author cannot be null or empty");
		}
		this.author.set(author);
	}

	@Override
	public String toString() {
		return "Post [postID=" + postID.get() + ", creator=" + creator.getName() + 
			   ", content=" + content.get() + ", date=" + date.get() + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Post other = (Post) obj;
		return postID.get().equals(other.postID.get());
	}

	public void shareWith(UserManager user) {
		sharedWith.insertLast(user);
	}

	public void shareWithAllFriends() {
		CircularDoublyLinkedList<UserManager> friends = creator.getFriends();
		Node<UserManager> current = friends.dummy.next;

		while (current != friends.dummy) {
			sharedWith.insertLast(current.data);
			current = current.next;
		}
	}

}
