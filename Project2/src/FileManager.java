import java.io.*;
import java.util.Arrays;
import javafx.collections.ObservableList;
import java.util.Iterator;
import java.util.Calendar;
import javafx.scene.control.Alert;
import javafx.collections.FXCollections;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

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
		} catch (IOException e) {
		}
	}

	public void loadPosts(String filePath, CircularDoublyLinkedList<PostManager> postDb, CircularDoublyLinkedList<UserManager> userDb) {
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

	public void storeUsers(String filePath, CircularDoublyLinkedList<UserManager> userDb) {
		try (PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath))) {
			Iterator<UserManager> iterator = userDb.iterator();
			while (iterator.hasNext()) {
				UserManager user = iterator.next();
				fileWriter.println(user.getUserID() + "," + user.getName());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void storeFriendships(String filePath, CircularDoublyLinkedList<UserManager> userDb) {
		try (PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath))) {
			Iterator<UserManager> iterator = userDb.iterator();
			while (iterator.hasNext()) {
				UserManager user = iterator.next();
				CircularDoublyLinkedList<UserManager> friendsList = user.getFriends();
				Iterator<UserManager> friendIterator = friendsList.iterator();
				while (friendIterator.hasNext()) {
					UserManager friend = friendIterator.next();
					fileWriter.println(user.getUserID() + "," + friend.getUserID());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void storePosts(String filePath, CircularDoublyLinkedList<PostManager> postDb) {
		try (PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath))) {
			Iterator<PostManager> iterator = postDb.iterator();
			while (iterator.hasNext()) {
				PostManager post = iterator.next();
				writePostEntry(fileWriter, post);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveUsers(String filePath, ObservableList<UserManager> userList) {
		if (userList == null || userList.isEmpty()) {
			displayMessage("Error", "No users to save");
			return;
		}

		try (PrintWriter fileWriter = new PrintWriter(new FileWriter(filePath))) {
			fileWriter.println("# Users");
			fileWriter.println("# Format: user_id,name,age");
			
			for (int i = 0; i < userList.size(); i++) {
				UserManager user = userList.get(i);
				if (user != null) {
					fileWriter.println(user.getUserID() + "," + user.getName() + "," + user.getAge());
				}
			}

			fileWriter.println("# Users");
			fileWriter.println("# Format: user_id,name,age");
			
			for (int i = 0; i < userList.size(); i++) {
				UserManager user = userList.get(i);
				String line = user.getUserID() + "," + user.getName() + "," + user.getAge();
				fileWriter.println(line);
			}

			displayMessage("Success", "Users saved to " + filePath);
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
			displayMessage("Success", "Friendships saved to " + filePath);
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
			List<PostManager> sortedPosts = new ArrayList<PostManager>();
			Iterator<PostManager> postIterator = posts.iterator();
			while (postIterator.hasNext()) {
				sortedPosts.add(postIterator.next());
			}
			Collections.sort(sortedPosts, (p1, p2) -> p1.getPostID().compareTo(p2.getPostID()));
			
			fileWriter.println("# Posts");
			fileWriter.println("# Format: post_id,creator_id,content,creation_date,shared_with_ids");
			
			for (int i = 0; i < sortedPosts.size(); i++) {
				PostManager post = sortedPosts.get(i);
				if (post != null && post.getCreator() != null) {
					String line = post.getPostID() + "," + post.getCreator().getUserID() + "," + post.getContent() + ",";
					
					Calendar date = post.getCreationDate();
					if (date != null) {
						line = line + date.get(Calendar.YEAR) + "-" + (date.get(Calendar.MONTH) + 1) + "-" + date.get(Calendar.DAY_OF_MONTH);
					} else {
						line = line + "2023-01-01";
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
			
			displayMessage("Success", "Posts saved to " + filePath);
		} catch (IOException e) {
			displayMessage("Error", "Failed to save posts: " + e.getMessage());
		}
	}
	
	public void writePostEntry(PrintWriter fileWriter, PostManager post) {
		String output = "";
		output += post.getPostID() + ",";
		output += post.getCreator().getUserID() + ",";
		output += post.getContent() + ",";
		
		Calendar date = post.getCreationDate();
		String dateStr = String.format("%d.%d.%d", 
				date.get(Calendar.DAY_OF_MONTH),
				date.get(Calendar.MONTH) + 1,
				date.get(Calendar.YEAR));
		output += dateStr;
		
		CircularDoublyLinkedList<UserManager> sharedUsers = post.getSharedUsers();
		Iterator<UserManager> iterator = sharedUsers.iterator();
		while (iterator.hasNext()) {
			UserManager user = iterator.next();
			output += "," + user.getUserID();
		}
		
		fileWriter.println(output);
	}
	
	public void displayMessage(String title, String message) {
		Alert popup = new Alert(Alert.AlertType.INFORMATION);
		popup.setTitle(title);
		popup.setHeaderText(null);
		popup.setContentText(message);
		popup.showAndWait();
	}
}
