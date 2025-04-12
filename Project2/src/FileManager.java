// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

// Handles all file operations for the application including loading and saving data
public class FileManager {
	// File paths for storing user and post data
	private static final String USERS_FILE = "users.txt";
	private static final String POSTS_FILE = "posts.txt";

	// Loads user data from a file and returns a list of UserManager objects
	public CircularDoublyLinkedList<UserManager> loadUsers(String filePath) {
		CircularDoublyLinkedList<UserManager> users = new CircularDoublyLinkedList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 3) {
					String userID = parts[0].trim();
					String name = parts[1].trim();
					int age = Integer.parseInt(parts[2].trim());
					users.add(new UserManager(userID, name, age));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return users;
	}

	// Loads friendship data from file
	public static void loadFriendships(String filename, CircularDoublyLinkedList<UserManager> users) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 2) {
					// First part is the user ID
					String userID = parts[0].trim();
					UserManager user = UserManager.searchByID(users, userID);
					
					if (user != null) {
						// Add all friends for this user
						for (int i = 1; i < parts.length; i++) {
							String friendID = parts[i].trim();
							UserManager friend = UserManager.searchByID(users, friendID);
							
							if (friend != null && !user.isFriend(friend)) {
								user.getFriends().insertLast(friend);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Error loading friendships: " + e.getMessage());
		}
	}

	// Loads post data from a file and returns a list of PostManager objects
	public CircularDoublyLinkedList<PostManager> loadPosts(String filePath, CircularDoublyLinkedList<UserManager> users) {
		CircularDoublyLinkedList<PostManager> posts = new CircularDoublyLinkedList<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 4) {
					String postID = parts[0].trim();
					String userID = parts[1].trim();
					String content = parts[2].trim();
					String dateStr = parts[3].trim();
					boolean shareWithAllFriends = parts.length > 4 && Boolean.parseBoolean(parts[4].trim());

					UserManager creator = UserManager.searchByID(users, userID);
					if (creator != null) {
						Calendar creationDate = parseDate(dateStr);
						PostManager post = PostManager.createPost(postID, creator, content, creationDate, shareWithAllFriends);
						posts.add(post);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return posts;
	}

	// Saves user data to a file
	public void saveUsers(String filePath, CircularDoublyLinkedList<UserManager> users) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			Iterator<UserManager> iterator = users.iterator();
			while (iterator.hasNext()) {
				UserManager user = iterator.next();
				writer.write(user.getUserID() + "," + user.getName() + "," + user.getAge());
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Saves friendship data to a file
	public void saveFriendships(String filePath, CircularDoublyLinkedList<UserManager> users) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			Iterator<UserManager> iterator = users.iterator();
			while (iterator.hasNext()) {
				UserManager user = iterator.next();
				CircularDoublyLinkedList<UserManager> friends = user.getFriends();
				Iterator<UserManager> friendIterator = friends.iterator();
				while (friendIterator.hasNext()) {
					UserManager friend = friendIterator.next();
					if (user.getUserID().compareTo(friend.getUserID()) < 0) {
						writer.write(user.getUserID() + "," + friend.getUserID());
						writer.newLine();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Saves post data to a file
	public void savePosts(String filePath, CircularDoublyLinkedList<PostManager> posts) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			Iterator<PostManager> iterator = posts.iterator();
			while (iterator.hasNext()) {
				PostManager post = iterator.next();
				String dateStr = formatDate(post.getCreationDate());
				boolean shareWithAllFriends = post.getSharedUsers() != null && !post.getSharedUsers().isEmpty();
				writer.write(post.getPostID() + "," + 
						   post.getCreator().getUserID() + "," + 
						   post.getContent() + "," + 
						   dateStr + "," + 
						   shareWithAllFriends);
				writer.newLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// Converts a date string to a Calendar object
	private Calendar parseDate(String dateStr) {
		String[] parts = dateStr.split("\\.");
		if (parts.length == 3) {
			int day = Integer.parseInt(parts[0]);
			int month = Integer.parseInt(parts[1]) - 1;
			int year = Integer.parseInt(parts[2]);
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month, day);
			return calendar;
		}
		return null;
	}

	// Converts a Calendar object to a formatted date string
	private String formatDate(Calendar date) {
		if (date == null) return "";
		return String.format("%02d.%02d.%d", 
			date.get(Calendar.DAY_OF_MONTH),
			date.get(Calendar.MONTH) + 1,
			date.get(Calendar.YEAR));
	}

	// Searches for a user by their ID in the given user list
	public UserManager lookupUserById(CircularDoublyLinkedList<UserManager> userData, String id) {
		Iterator<UserManager> iterator = userData.iterator();
		while (iterator.hasNext()) {
			UserManager user = iterator.next();
			if (user.getUserID().equals(id)) {
				return user;
			}
		}
		return null;
	}

	// Displays a message dialog with the given title and message
	public void displayMessage(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
