// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Post { // class to represent a post in the social network
	private SimpleStringProperty postID; // unique identifier for the post
	private UserManager creator; // user who created the post
	private SimpleStringProperty content; // content of the post
	private SimpleStringProperty date; // date when the post was created
	private CircularDoublyLinkedList<UserManager> sharedWith; // list of users the post is shared with
	private SimpleStringProperty author; // author name of the post

	public Post() { // default constructor
		this.postID = new SimpleStringProperty(); // initialize postID property
		this.content = new SimpleStringProperty(); // initialize content property
		this.date = new SimpleStringProperty(); // initialize date property
		this.sharedWith = new CircularDoublyLinkedList<>(); // initialize shared users list
		this.author = new SimpleStringProperty(); // initialize author property
	}

	public Post(String postID, UserManager creator, String content, String date, CircularDoublyLinkedList<UserManager> sharedWith) { // constructor with parameters
		this.postID = new SimpleStringProperty(postID); // set postID property
		this.creator = creator; // set creator
		this.content = new SimpleStringProperty(content); // set content property
		this.date = new SimpleStringProperty(date); // set date property
		this.sharedWith = sharedWith; // set shared users list
		this.author = new SimpleStringProperty(); // initialize author property
	}

	public String getPostID() { // getter for postID
		return postID.get();
	}

	public void setPostID(String postID) { // setter for postID
		this.postID.set(postID);
	}

	public StringProperty postIDProperty() { // getter for postID property for JavaFX binding
		return postID;
	}

	public UserManager getCreator() { // getter for creator
		return creator;
	}

	public void setCreator(UserManager creator) { // setter for creator
		this.creator = creator;
	}

	public String getContent() { // getter for content
		return content.get();
	}

	public void setContent(String content) { // setter for content
		this.content.set(content);
	}

	public StringProperty contentProperty() { // getter for content property for JavaFX binding
		return content;
	}

	public String getDate() { // getter for date
		return date.get();
	}

	public void setDate(String date) { // setter for date
		this.date.set(date);
	}

	public StringProperty dateProperty() { // getter for date property for JavaFX binding
		return date;
	}

	public CircularDoublyLinkedList<UserManager> getSharedWith() { // getter for shared users list
		return sharedWith;
	}

	public void setSharedWith(CircularDoublyLinkedList<UserManager> sharedWith) { // setter for shared users list
		this.sharedWith = sharedWith;
	}

	public String getAuthor() { // getter for author
		return author.get();
	}

	public void setAuthor(String author) { // setter for author with validation
		if (author == null || author.isEmpty()) { // check if author is null or empty
			throw new CustomException("Author cannot be null or empty"); // throw exception if invalid
		}
		this.author.set(author); // set author if valid
	}

	@Override
	public String toString() { // method to convert post to string representation
		return "Post [postID=" + postID.get() + ", creator=" + creator.getName() + 
			   ", content=" + content.get() + ", date=" + date.get() + "]"; // formatted string with post details
	}

	@Override
	public boolean equals(Object obj) { // method to check if two posts are equal
		if (this == obj) return true; // check if same object
		if (obj == null || getClass() != obj.getClass()) return false; // check if null or different class
		Post other = (Post) obj; // cast to Post
		return postID.get().equals(other.postID.get()); // compare post IDs
	}

	public void shareWith(UserManager user) { // method to share post with a user
		sharedWith.insertLast(user); // add user to shared list
	}

	public void shareWithAllFriends() { // method to share post with all friends of creator
		CircularDoublyLinkedList<UserManager> friends = creator.getFriends(); // get creator's friends
		Node<UserManager> current = friends.dummy.next; // start at first friend

		while (current != friends.dummy) { // iterate through friends
			sharedWith.insertLast(current.data); // share with each friend
			current = current.next; // move to next friend
		}
	}
}
