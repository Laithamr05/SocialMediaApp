public class FriendshipManager {
	public FriendshipManager() {
	}

	public UserManager search(String value, CircularDoublyLinkedList<UserManager> users) {
		Node<UserManager> current = users.dummy.next;
		while (current != users.dummy) {
			if (current.data.getUserID().equals(value) || current.data.getName().equalsIgnoreCase(value)) {
				return current.data;
			}
			current = current.next;
		}
		return null;
	}

	public boolean addFriend(UserManager user1, UserManager user2) {
		if (user1 == null || user2 == null)
			return false;
		if (user1.isFriend(user2))
			return false;
		user1.getFriends().insertLast(user2);
		user2.getFriends().insertLast(user1);
		return true;
	}

	public boolean removeFriend(UserManager user1, UserManager user2) {
		if (user1 == null || user2 == null)
			return false;
		if (!user1.isFriend(user2))
			return false;
		user1.getFriends().delete(user2);
		user2.getFriends().delete(user1);
		return true;
	}
}
