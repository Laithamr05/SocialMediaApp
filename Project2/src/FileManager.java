import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

public class FileManager {
	public void loadUsers(String filePath, CircularDoublyLinkedList<UserManager> userDb) {
		try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = fileReader.readLine()) != null) {
				String[] fields = line.split(",");
				if (fields.length >= 3) {
					String id = fields[0].trim();
					String name = fields[1].trim();
					String age = fields[2].trim();
					userDb.insertLast(new UserManager(id, name, age));
				}
			}
		} catch (IOException e) {
		}
	}

	public void loadFriendships(String filePath, CircularDoublyLinkedList<UserManager> userDb) {
		// First check if users exist
		if (userDb == null || userDb.isEmpty()) {
			displayMessage("Error", "Please load users first before loading friendships");
			throw new IllegalStateException("No users loaded - please load users first");
		}

		try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = fileReader.readLine()) != null) {
				String[] fields = line.split(",");
				if (fields.length >= 2) {
					String userId = fields[0].trim();
					UserManager owner = lookupUserById(userDb, userId);
					
					if (owner != null) {
						owner.setFriends(new CircularDoublyLinkedList<>());
						
						for (int i = 1; i < fields.length; i++) {
							String friendId = fields[i].trim();
							UserManager friend = lookupUserById(userDb, friendId);
							
							if (friend != null && !owner.equals(friend)) {
								if (!owner.contains(friend)) {
									owner.addFriend(friend);
								}
								
								if (!friend.contains(owner)) {
									friend.addFriend(owner);
								}
							}
						}
					}
				}
			}
			
			// Validate friendship links to make sure no null entries
			Iterator<UserManager> userIterator = userDb.iterator();
			while (userIterator.hasNext()) {
				UserManager user = userIterator.next();
				if (user != null && user.getFriends() != null) {
					// Check for null entries in the friends list
					CircularDoublyLinkedList<UserManager> friendsList = user.getFriends();
					CircularDoublyLinkedList<UserManager> validFriends = new CircularDoublyLinkedList<>();
					
					Iterator<UserManager> friendsIterator = friendsList.iterator();
					while (friendsIterator.hasNext()) {
						UserManager friend = friendsIterator.next();
						if (friend != null) {
							validFriends.insertLast(friend);
						}
					}
					
					user.setFriends(validFriends);
				}
			}
		} catch (IOException e) {
		}
	}

	public void loadPosts(String filePath, CircularDoublyLinkedList<PostManager> postDb, CircularDoublyLinkedList<UserManager> userDb) {
		// First check if users exist
		if (userDb == null || userDb.isEmpty()) {
			displayMessage("Error", "Please load users first before loading posts");
			throw new IllegalStateException("No users loaded - please load users first");
		}
		
		// Check if any friendships exist
		boolean hasFriendships = false;
		Iterator<UserManager> userIterator = userDb.iterator();
		while (userIterator.hasNext()) {
			UserManager user = userIterator.next();
			if (user != null && user.getFriends() != null && !user.getFriends().isEmpty()) {
				hasFriendships = true;
				break;
			}
		}
		
		if (!hasFriendships) {
			displayMessage("Error", "Please create friendships before loading posts");
			throw new IllegalStateException("No friendships established - please create friendships first");
		}

		try (BufferedReader fileReader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = fileReader.readLine()) != null) {
				processPostRecord(line, postDb, userDb);
			}
		} catch (IOException e) {
		}
	}
	
	public void processPostRecord(String line, CircularDoublyLinkedList<PostManager> postDb, CircularDoublyLinkedList<UserManager> userDb) {
		String[] fields = line.split(",");
		if (fields.length >= 4) {
			String postID = fields[0];
			String creatorID = fields[1];
			String content = fields[2];
			String dateStr = fields[3];
			
			UserManager creator = lookupUserById(userDb, creatorID);
			
			if (creator != null) {
				Calendar date = Calendar.getInstance();
				try {
					String[] dateParts = dateStr.split("\\.");
					if (dateParts.length == 3) {
						int day = Integer.parseInt(dateParts[0]);
						int month = Integer.parseInt(dateParts[1]) - 1;
						int year = Integer.parseInt(dateParts[2]);
						date.set(year, month, day);
					}
				} catch (NumberFormatException e) {
					System.err.println("Error parsing date: " + dateStr);
				}
				
				PostManager post = new PostManager(postID, creator, content, date);
				
				for (int i = 4; i < fields.length; i++) {
					UserManager sharedUser = lookupUserById(userDb, fields[i]);
					if (sharedUser != null) {
						post.addSharedUser(sharedUser);
					}
				}
				
				postDb.insertLast(post);
			}
		}
	}

	public UserManager lookupUserById(CircularDoublyLinkedList<UserManager> userDb, String id) {
		Node<UserManager> current = userDb.dummy.next;
		while (current != userDb.dummy) {
			if (current.data.getUserID().equals(id)) {
				return current.data;
			}
			current = current.next;
		}
		return null;
	}

	public void saveUsers(String filePath, ObservableList<UserManager> userList) {
		if (userList == null || userList.isEmpty()) {
			displayMessage("Error", "No users to save");
			return;
		}

		try (PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath))) {
			for (int i = 0; i < userList.size(); i++) {
				UserManager user = userList.get(i);
				if (user != null) {
					fileWriter.println(user.getUserID() + "," + user.getName() + "," + user.getAge());
				}
			}
		} catch (IOException e) {
			displayMessage("Error", "Failed to save users: " + e.getMessage());
		}
	}

	public void saveFriendships(String filePath, ObservableList<UserManager> userList) {
		if (userList == null || userList.isEmpty()) {
			displayMessage("Error", "No users to save friendships for");
			return;
		}

		try (PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath))) {
			for (UserManager user : userList) {
				String line = user.getUserID();
				
				CircularDoublyLinkedList<UserManager> friendsList = user.getFriends();
				if (friendsList != null && !friendsList.isEmpty()) {
					Iterator<UserManager> friendIterator = friendsList.iterator();
					while (friendIterator.hasNext()) {
						UserManager friend = friendIterator.next();
						if (friend != null) {
							line += "," + friend.getUserID();
						}
					}
				}
				
				fileWriter.println(line);
			}
		} catch (IOException e) {
			displayMessage("Error", "Failed to save friendships: " + e.getMessage());
		}
	}

	public void savePosts(String filePath, CircularDoublyLinkedList<PostManager> posts, ObservableList<UserManager> userList) {
		if (posts == null || posts.isEmpty()) {
			displayMessage("Error", "No posts to save");
			return;
		}

		try (PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath))) {
			ArrayList<PostManager> sortedPosts = new ArrayList<PostManager>();
			Iterator<PostManager> postIterator = posts.iterator();
			while (postIterator.hasNext()) {
				sortedPosts.add(postIterator.next());
			}
			
			// Sort posts by ID using a traditional comparator instead of lambda
			Collections.sort(sortedPosts, new java.util.Comparator<PostManager>() {
				@Override
				public int compare(PostManager p1, PostManager p2) {
					return p1.getPostID().compareTo(p2.getPostID());
				}
			});
			
			for (int i = 0; i < sortedPosts.size(); i++) {
				PostManager post = sortedPosts.get(i);
				if (post != null && post.getCreator() != null) {
					String line = post.getPostID() + "," + post.getCreator().getUserID() + "," + post.getContent() + ",";
					
					Calendar date = post.getCreationDate();
					if (date != null) {
						// Format date without ternary operators
						int day = date.get(Calendar.DAY_OF_MONTH);
						int month = date.get(Calendar.MONTH) + 1;
						int year = date.get(Calendar.YEAR);
						
						String dayStr;
						if (day < 10) {
							dayStr = "0" + day;
						} else {
							dayStr = String.valueOf(day);
						}
						
						String monthStr;
						if (month < 10) {
							monthStr = "0" + month;
						} else {
							monthStr = String.valueOf(month);
						}
						
						line = line + dayStr + "." + monthStr + "." + year;
					} else {
						line = line + "01.01.2023";
					}
					
					line = line + ",";
					CircularDoublyLinkedList<UserManager> sharedUsers = post.getSharedUsers();
					if (sharedUsers != null && !sharedUsers.isEmpty()) {
						boolean isFirst = true;
						Iterator<UserManager> iterator = sharedUsers.iterator();
						while (iterator.hasNext()) {
							UserManager sharedUser = iterator.next();
							if (!isFirst) {
								line = line + ";";
							}
							line = line + sharedUser.getUserID();
							isFirst = false;
						}
					}
					
					fileWriter.println(line);
				}
			}
		} catch (IOException e) {
			displayMessage("Error", "Failed to save posts: " + e.getMessage());
		}
	}
	
	public void displayMessage(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
