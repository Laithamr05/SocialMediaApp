import javafx.beans.property.SimpleStringProperty;


public class UserManager implements Comparable<UserManager> {
	private SimpleStringProperty userID;
	private SimpleStringProperty name;
	private SimpleStringProperty age;
	private CircularDoublyLinkedList<UserManager> friends;

	public UserManager() {
		this.userID = new SimpleStringProperty();
		this.name = new SimpleStringProperty();
		this.age = new SimpleStringProperty();
		this.friends = new CircularDoublyLinkedList<UserManager>();
	}

	public UserManager(String userID, String name, String age) {
		this.userID = new SimpleStringProperty(userID);
		this.name = new SimpleStringProperty(name);
		this.age = new SimpleStringProperty(age);
		this.friends = new CircularDoublyLinkedList<UserManager>();
	}

	public String getUserID() {
		return userID.get();
	}

	public void setUserID(String userID) {
		this.userID.set(userID);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String getAge() {
		return age.get();
	}

	public void setAge(String age) {
		this.age.set(age);
	}

	public CircularDoublyLinkedList<UserManager> getFriends() {
		return friends;
	}

	public void setFriends(CircularDoublyLinkedList<UserManager> friends) {
		this.friends = friends;
	}

	public void addFriend(UserManager friend) {
		Node<UserManager> current = friends.dummy.next;
		while (current != friends.dummy) {
			if (current.data.getUserID().equals(friend.getUserID())) {
				return;
			}
			current = current.next;
		}
		friends.insertLast(friend);
	}

	public void removeFriend(UserManager friend) {
		if (friends.contains(friend)) {
			Node<UserManager> current = friends.dummy.next;
			while (current != friends.dummy) {
				if (current.data.equals(friend)) {
					current.previous.next = current.next;
					current.next.previous = current.previous;
					current.next = null;
					current.previous = null;
					return;
				}
				current = current.next;
			}
		}
	}

	@Override
	public String toString() {
		return name.get() + " (ID: " + userID.get() + ")";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		UserManager other = (UserManager) obj;
		String thisID = userID.get();
		String otherID = other.userID.get();
		if (thisID == null && otherID == null)
			return true;
		if (thisID == null || otherID == null)
			return false;
		return thisID.equals(otherID);
	}

	public boolean isFriend(UserManager user) {
		return friends.contains(user);
	}

	public boolean update(UserManager user, String ID, String name, String age) {
		if (!friends.contains(user)) {
			return false;
		}
		user.setUserID(ID);
		user.setName(name);
		user.setAge(age);
		return true;
	}

	public UserManager searchByID(CircularDoublyLinkedList<UserManager> users, String userID) {
		Node<UserManager> current = users.dummy.next;
		while (current != users.dummy) {
			if (current.data.getUserID().equals(userID)) {
				return current.data;
			}
			current = current.next;
		}
		return null;
	}

	public UserManager searchByName(CircularDoublyLinkedList<UserManager> users, String name) {
		Node<UserManager> current = users.dummy.next;
		while (current != users.dummy) {
			if (current.data.getName().trim().equalsIgnoreCase(name)) {
				return current.data;
			}
			current = current.next;
		}
		return null;
	}

	public void insertUser(UserManager user) {
		friends.insertLast(user);
	}

	public boolean deleteUser(UserManager user) {
		if (friends.contains(user)) {
			Node<UserManager> current = friends.dummy.next;
			while (current != friends.dummy) {
				if (current.data.equals(user)) {
					current.previous.next = current.next;
					current.next.previous = current.previous;
					current.next = null;
					current.previous = null;
					return true;
				}
				current = current.next;
			}
		}
		return false;
	}

	public boolean contains(UserManager user) {
		Node<UserManager> current = friends.dummy.next;
		while (current != friends.dummy) {
			if (current.data.equals(user)) {
				return true;
			}
			current = current.next;
		}
		return false;
	}

	@Override
	public int compareTo(UserManager other) {
		if (this == other) return 0;
		if (other == null) return 1;
		String thisName = name.get();
		String otherName = other.name.get();
		if (thisName == null && otherName == null) return 0;
		if (thisName == null) return -1;
		if (otherName == null) return 1;
		return thisName.compareToIgnoreCase(otherName);
	}
}
