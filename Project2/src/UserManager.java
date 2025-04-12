// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.beans.property.SimpleStringProperty;
import java.util.ArrayList;
import java.util.Iterator;

// Manages user data and friendships in the social network application
public class UserManager implements Comparable<UserManager> {
	// User identification and personal information
	private String userID;
	private String name;
	private int age;
	
	// List of friends for this user
	private CircularDoublyLinkedList<UserManager> friends;
	
	// Properties for table display
	private SimpleStringProperty userIDProperty;
	private SimpleStringProperty nameProperty;
	private SimpleStringProperty ageProperty;
	private SimpleStringProperty friendsCountProperty;

	// Creates a new user with the given information
	public UserManager(String userID, String name, int age) {
		this.userID = userID;
		this.name = name;
		this.age = age;
		this.friends = new CircularDoublyLinkedList<>();
		
		// Initialize table display properties
		this.userIDProperty = new SimpleStringProperty(userID);
		this.nameProperty = new SimpleStringProperty(name);
		this.ageProperty = new SimpleStringProperty(String.valueOf(age));
		this.friendsCountProperty = new SimpleStringProperty("0");
	}

	// looks for a user by their id or name
	public static UserManager search(String value, CircularDoublyLinkedList<UserManager> users) {
		if (users == null || value == null) {
			return null;
		}
		Iterator<UserManager> iterator = users.iterator();
		while (iterator.hasNext()) {
			UserManager user = iterator.next();
			if (user != null && (user.getUserID().equals(value) || user.getName().equals(value))) {
				return user;
			}
		}
		return null;
	}

	// manages friendships between users
	public static boolean addFriendship(UserManager user1, UserManager user2) {
		if (user1 == null || user2 == null)
			return false;
		if (user1.isFriend(user2))
			return false;
		user1.getFriends().insertLast(user2);
		user2.getFriends().insertLast(user1);
		return true;
	}

	// removes friendship between users
	public static boolean removeFriendship(UserManager user1, UserManager user2) {
		if (user1 == null || user2 == null)
			return false;
		if (!user1.isFriend(user2))
			return false;
		user1.getFriends().delete(user2);
		user2.getFriends().delete(user1);
		return true;
	}

	// basic getters and setters
	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
		this.userIDProperty.set(userID);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.nameProperty.set(name);
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
		this.ageProperty.set(String.valueOf(age));
	}

	public CircularDoublyLinkedList<UserManager> getFriends() {
		return friends;
	}

	public void setFriends(CircularDoublyLinkedList<UserManager> friends) {
		this.friends = friends;
	}

	// manages friend list
	public void addFriend(UserManager friend) {
		if (friend != null && !isFriend(friend)) {
			friends.insertLast(friend);
		}
	}

	public void removeFriend(UserManager friend) {
		if (friend != null && isFriend(friend)) {
			friends.delete(friend);
		}
	}

	// string representation of user
	@Override
	public String toString() {
		return "User ID: " + userID + ", Name: " + name + ", Age: " + age;
	}

	// checks if two users are the same
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		UserManager other = (UserManager) obj;
		String thisID = userID;
		String otherID = other.userID;
		if (thisID == null && otherID == null)
			return true;
		if (thisID == null || otherID == null)
			return false;
		return thisID.equals(otherID);
	}

	// makes a special number for the user
	@Override
	public int hashCode() {
		return userID.hashCode();
	}

	// checks if someone is already a friend
	public boolean isFriend(UserManager user) {
		if (user == null || friends == null) return false;
		Iterator<UserManager> iterator = friends.iterator();
		while (iterator.hasNext()) {
			UserManager friend = iterator.next();
			if (friend != null && friend.equals(user)) {
				return true;
			}
		}
		return false;
	}

	// updates a friend's info
	public boolean update(UserManager user, String ID, String name, String age) {
		if (!friends.contains(user)) {
			return false;
		}
		user.setUserID(ID);
		user.setName(name);
		user.setAge(Integer.parseInt(age));
		return true;
	}

	// finds a user by their ID
	public static UserManager searchByID(CircularDoublyLinkedList<UserManager> users, String userID) {
		if (users == null || userID == null) return null;
		Iterator<UserManager> iterator = users.iterator();
		while (iterator.hasNext()) {
			UserManager user = iterator.next();
			if (user != null && user.getUserID().equals(userID)) {
				return user;
			}
		}
		return null;
	}

	// finds a user by their name
	public UserManager searchByName(CircularDoublyLinkedList<UserManager> users, String name) {
		if (users == null || name == null) {
			return null;
		}
		Iterator<UserManager> iterator = users.iterator();
		while (iterator.hasNext()) {
			UserManager user = iterator.next();
			if (user != null && user.getName().equals(name)) {
				return user;
			}
		}
		return null;
	}

	// adds a user to the friends list
	public void insertUser(UserManager user) {
		friends.insertLast(user);
	}

	// removes a user from the friends list
	public boolean deleteUser(UserManager user) {
		if (user == null) {
			return false;
		}
		friends.delete(user);
		return true;
	}

	// checks if a user is in the friends list
	public boolean contains(UserManager user) {
		if (friends == null || user == null) {
			return false;
		}
		Iterator<UserManager> iterator = friends.iterator();
		while (iterator.hasNext()) {
			UserManager friend = iterator.next();
			if (friend != null && friend.equals(user)) {
				return true;
			}
		}
		return false;
	}

	// compares two users by their names
	@Override
	public int compareTo(UserManager other) {
		if (this == other) return 0;
		if (other == null) return 1;
		String thisName = name;
		String otherName = other.name;
		if (thisName == null && otherName == null) return 0;
		if (thisName == null) return -1;
		if (otherName == null) return 1;
		return thisName.compareToIgnoreCase(otherName);
	}

	// Getter methods for table display properties
	public SimpleStringProperty userIDProperty() { return userIDProperty; }
	public SimpleStringProperty nameProperty() { return nameProperty; }
	public SimpleStringProperty ageProperty() { return ageProperty; }
	public SimpleStringProperty friendsCountProperty() { return friendsCountProperty; }
}
