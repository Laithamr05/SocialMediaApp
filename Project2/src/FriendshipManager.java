// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

public class FriendshipManager { // class to manage friendship relationships
	public FriendshipManager() { // default constructor
	}

	public UserManager search(String value, CircularDoublyLinkedList<UserManager> users) { // method to search for a user
		Node<UserManager> current = users.dummy.next; // start at the first user
		while (current != users.dummy) { // loop through all users
			if (current.data.getUserID().equals(value) || current.data.getName().equalsIgnoreCase(value)) { // match by id or name
				return current.data; // return matching user
			}
			current = current.next; // move to next user
		}
		return null; // return null if no match found
	}

	public boolean addFriend(UserManager user1, UserManager user2) { // method to add friendship between two users
		if (user1 == null || user2 == null) // check if either user is null
			return false;
		if (user1.isFriend(user2)) // check if already friends
			return false;
		user1.getFriends().insertLast(user2); // add user2 to user1's friends
		user2.getFriends().insertLast(user1); // add user1 to user2's friends
		return true; // friendship added successfully
	}

	public boolean removeFriend(UserManager user1, UserManager user2) { // method to remove friendship between two users
		if (user1 == null || user2 == null) // check if either user is null
			return false;
		if (!user1.isFriend(user2)) // check if they are not friends
			return false;
		user1.getFriends().delete(user2); // remove user2 from user1's friends
		user2.getFriends().delete(user1); // remove user1 from user2's friends
		return true; // friendship removed successfully
	}
}
