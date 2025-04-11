// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.beans.property.SimpleStringProperty;


public class UserManager implements Comparable<UserManager> { // class for managing user data and operations
	private SimpleStringProperty userID; // unique identifier for the user
	private SimpleStringProperty name; // user's name
	private SimpleStringProperty age; // user's age
	private CircularDoublyLinkedList<UserManager> friends; // list of user's friends

	public UserManager() { // default constructor
		this.userID = new SimpleStringProperty(); // initialize userID property
		this.name = new SimpleStringProperty(); // initialize name property
		this.age = new SimpleStringProperty(); // initialize age property
		this.friends = new CircularDoublyLinkedList<UserManager>(); // initialize friends list
	}

	public UserManager(String userID, String name, String age) { // constructor with parameters
		this.userID = new SimpleStringProperty(userID); // set userID property
		this.name = new SimpleStringProperty(name); // set name property
		this.age = new SimpleStringProperty(age); // set age property
		this.friends = new CircularDoublyLinkedList<UserManager>(); // initialize friends list
	}

	public String getUserID() { // getter for userID
		return userID.get();
	}

	public void setUserID(String userID) { // setter for userID
		this.userID.set(userID);
	}

	public String getName() { // getter for name
		return name.get();
	}

	public void setName(String name) { // setter for name
		this.name.set(name);
	}

	public String getAge() { // getter for age
		return age.get();
	}

	public void setAge(String age) { // setter for age
		this.age.set(age);
	}

	public CircularDoublyLinkedList<UserManager> getFriends() { // getter for friends list
		return friends;
	}

	public void setFriends(CircularDoublyLinkedList<UserManager> friends) { // setter for friends list
		this.friends = friends;
	}

	public void addFriend(UserManager friend) { // method to add a friend
		Node<UserManager> current = friends.dummy.next; // start at first friend
		while (current != friends.dummy) { // iterate through friends
			if (current.data.getUserID().equals(friend.getUserID())) { // check if already friends
				return;
			}
			current = current.next; // move to next friend
		}
		friends.insertLast(friend); // add friend to end of list
	}

	public void removeFriend(UserManager friend) { // method to remove a friend
		if (friends.contains(friend)) { // check if friend exists
			Node<UserManager> current = friends.dummy.next; // start at first friend
			while (current != friends.dummy) { // iterate through friends
				if (current.data.equals(friend)) { // find the friend to remove
					current.previous.next = current.next; // update previous node's next
					current.next.previous = current.previous; // update next node's previous
					current.next = null; // clear reference to next
					current.previous = null; // clear reference to previous
					return;
				}
				current = current.next; // move to next friend
			}
		}
	}

	@Override
	public String toString() { // method to convert user to string representation
		return name.get() + " (ID: " + userID.get() + ")"; // formatted string with name and ID
	}

	@Override
	public boolean equals(Object obj) { // method to check if two users are equal
		if (this == obj) // check if same object
			return true;
		if (obj == null || getClass() != obj.getClass()) // check if null or different class
			return false;
		UserManager other = (UserManager) obj; // cast to UserManager
		String thisID = userID.get(); // get this user's ID
		String otherID = other.userID.get(); // get other user's ID
		if (thisID == null && otherID == null) // both IDs null, consider equal
			return true;
		if (thisID == null || otherID == null) // one ID null, not equal
			return false;
		return thisID.equals(otherID); // compare user IDs
	}

	public boolean isFriend(UserManager user) { // method to check if a user is a friend
		return friends.contains(user); // check if in friends list
	}

	public boolean update(UserManager user, String ID, String name, String age) { // method to update a friend's info
		if (!friends.contains(user)) { // check if user is a friend
			return false;
		}
		user.setUserID(ID); // update ID
		user.setName(name); // update name
		user.setAge(age); // update age
		return true; // update successful
	}

	public UserManager searchByID(CircularDoublyLinkedList<UserManager> users, String userID) { // method to find user by ID
		Node<UserManager> current = users.dummy.next; // start at first user
		while (current != users.dummy) { // iterate through users
			if (current.data.getUserID().equals(userID)) { // check if ID matches
				return current.data; // return matching user
			}
			current = current.next; // move to next user
		}
		return null; // no match found
	}

	public UserManager searchByName(CircularDoublyLinkedList<UserManager> users, String name) { // method to find user by name
		Node<UserManager> current = users.dummy.next; // start at first user
		while (current != users.dummy) { // iterate through users
			if (current.data.getName().trim().equalsIgnoreCase(name)) { // check if name matches (case insensitive)
				return current.data; // return matching user
			}
			current = current.next; // move to next user
		}
		return null; // no match found
	}

	public void insertUser(UserManager user) { // method to insert a user into friends list
		friends.insertLast(user); // add to end of list
	}

	public boolean deleteUser(UserManager user) { // method to delete a user from friends list
		if (friends.contains(user)) { // check if user exists in list
			Node<UserManager> current = friends.dummy.next; // start at first friend
			while (current != friends.dummy) { // iterate through friends
				if (current.data.equals(user)) { // find user to delete
					current.previous.next = current.next; // update previous node's next
					current.next.previous = current.previous; // update next node's previous
					current.next = null; // clear reference to next
					current.previous = null; // clear reference to previous
					return true; // deletion successful
				}
				current = current.next; // move to next friend
			}
		}
		return false; // user not found
	}

	public boolean contains(UserManager user) { // method to check if user is in friends list
		Node<UserManager> current = friends.dummy.next; // start at first friend
		while (current != friends.dummy) { // iterate through friends
			if (current.data.equals(user)) { // check if user matches
				return true; // user found
			}
			current = current.next; // move to next friend
		}
		return false; // user not found
	}

	@Override
	public int compareTo(UserManager other) { // method to compare users by name
		if (this == other) return 0; // same object, equal
		if (other == null) return 1; // null comes before this
		String thisName = name.get(); // get this user's name
		String otherName = other.name.get(); // get other user's name
		if (thisName == null && otherName == null) return 0; // both names null, equal
		if (thisName == null) return -1; // null name comes before non-null
		if (otherName == null) return 1; // non-null name comes after null
		return thisName.compareToIgnoreCase(otherName); // compare names (case insensitive)
	}
}
