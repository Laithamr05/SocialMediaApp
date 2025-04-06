import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import javafx.collections.ObservableList;

public class FileManager {
	public void readUsers(String filePath, CircularDoublyLinkedList<UserManager> users) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 3) {
					String id = parts[0].trim();
					String name = parts[1].trim();
					String age = parts[2].trim();
					users.insertLast(new UserManager(id, name, age));
				}
			}
		} catch (IOException e) {
			// Handle exception silently
		}
	}

	public void readFriendships(String filePath, CircularDoublyLinkedList<UserManager> users) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length >= 2) {
					String userId = parts[0].trim();
					UserManager user = findUserById(users, userId);
					
					if (user != null) {
						user.setFriends(new CircularDoublyLinkedList<>());
						
						for (int i = 1; i < parts.length; i++) {
							String friendId = parts[i].trim();
							UserManager friend = findUserById(users, friendId);
							
							if (friend != null && !user.equals(friend)) {
								if (!user.contains(friend)) {
									user.addFriend(friend);
								}
								
								if (!friend.contains(user)) {
									friend.addFriend(user);
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// Handle exception silently
		}
	}

	public void readPosts(String filePath, CircularDoublyLinkedList<PostManager> posts, CircularDoublyLinkedList<UserManager> users) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				processPostLine(line, posts, users);
			}
		} catch (IOException e) {
			// Handle exception silently
		}
	}
	
	private void processPostLine(String line, CircularDoublyLinkedList<PostManager> posts, CircularDoublyLinkedList<UserManager> users) {
		String[] parts = line.split(",");
		if (parts.length >= 4) {
			String postId = parts[0].trim();
			String creatorId = parts[1].trim();
			String content = parts[2].trim();
			String dateStr = parts[3].trim();
			
			LocalDate date;
			try {
				String[] dateParts = dateStr.split("\\.");
				if (dateParts.length == 3) {
					int day = Integer.parseInt(dateParts[0]);
					int month = Integer.parseInt(dateParts[1]);
					int year = Integer.parseInt(dateParts[2]);
					date = LocalDate.of(year, month, day);
				} else {
					date = LocalDate.parse(dateStr);
				}
			} catch (Exception e) {
				date = LocalDate.now();
			}
			
			UserManager creator = findUserById(users, creatorId);
			if (creator != null) {
				PostManager post = new PostManager(postId, creator, content, date);
				
				for (int i = 4; i < parts.length; i++) {
					String userId = parts[i].trim();
					UserManager sharedUser = findUserById(users, userId);
					if (sharedUser != null) {
						post.addSharedUser(sharedUser);
					}
				}
				
				posts.insertLast(post);
			}
		}
	}

	private UserManager findUserById(CircularDoublyLinkedList<UserManager> users, String id) {
		Node<UserManager> current = users.dummy.next;
		while (current != users.dummy) {
			if (current.data.getUserID().equals(id)) {
				return current.data;
			}
			current = current.next;
		}
		return null;
	}

	public void writeUsers(String filePath, CircularDoublyLinkedList<UserManager> users) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
			Node<UserManager> current = users.dummy.next;
			while (current != users.dummy) {
				UserManager user = current.data;
				writer.println(user.getUserID() + "," + user.getName() + "," + user.getAge());
				current = current.next;
			}
		} catch (IOException e) {
			// Handle exception silently
		}
	}

	public void writeFriendships(String filePath, CircularDoublyLinkedList<UserManager> users) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
			Node<UserManager> current = users.dummy.next;
			while (current != users.dummy) {
				UserManager user = current.data;
				StringBuilder friendIds = new StringBuilder();
				Node<UserManager> friendNode = user.getFriends().dummy.next;
				while (friendNode != user.getFriends().dummy) {
					if (friendIds.length() > 0) {
						friendIds.append(",");
					}
					friendIds.append(friendNode.data.getUserID());
					friendNode = friendNode.next;
				}
				writer.println(user.getUserID() + "," + friendIds);
				current = current.next;
			}
		} catch (IOException e) {
			// Handle exception silently
		}
	}

	public void writePosts(String filePath, CircularDoublyLinkedList<PostManager> posts) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
			Node<PostManager> current = posts.dummy.next;
			while (current != posts.dummy) {
				PostManager post = current.data;
				
				StringBuilder postLine = new StringBuilder();
				postLine.append(post.getPostID()).append(",");
				postLine.append(post.getCreator().getUserID()).append(",");
				postLine.append(post.getContent()).append(",");
				
				LocalDate date = post.getCreationDate();
				String formattedDate = String.format("%d.%d.%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
				postLine.append(formattedDate);
				
				Node<UserManager> sharedNode = post.getSharedUsers().dummy.next;
				while (sharedNode != post.getSharedUsers().dummy) {
					postLine.append(",").append(sharedNode.data.getUserID());
					sharedNode = sharedNode.next;
				}
				
				writer.println(postLine.toString());
				current = current.next;
			}
		} catch (IOException e) {
			// Handle exception silently
		}
	}

	/**
	 * Save users data to file, supporting different sort orders
	 * @param filePath Path to save the file
	 * @param userList List of users, possibly sorted
	 */
	public void saveUsers(String filePath, ObservableList<UserManager> userList) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
			for (UserManager user : userList) {
				writer.println(user.getUserID() + "," + user.getName() + "," + user.getAge());
			}
		} catch (IOException e) {
			throw new RuntimeException("Error saving users: " + e.getMessage());
		}
	}

	/**
	 * Save friendships data to file, supporting different sort orders
	 * @param filePath Path to save the file
	 * @param userList List of users, possibly sorted
	 */
	public void saveFriendships(String filePath, ObservableList<UserManager> userList) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
			for (UserManager user : userList) {
				StringBuilder friendIds = new StringBuilder();
				CircularDoublyLinkedList<UserManager> friends = user.getFriends();
				
				if (friends != null) {
					Node<UserManager> friendNode = friends.dummy.next;
					while (friendNode != friends.dummy) {
						if (friendIds.length() > 0) {
							friendIds.append(",");
						}
						friendIds.append(friendNode.data.getUserID());
						friendNode = friendNode.next;
					}
				}
				
				writer.println(user.getUserID() + "," + friendIds);
			}
		} catch (IOException e) {
			throw new RuntimeException("Error saving friendships: " + e.getMessage());
		}
	}

	/**
	 * Save posts data to file, taking into account user sorting
	 * @param filePath Path to save the file
	 * @param posts List of posts
	 * @param userList Sorted list of users to determine post order
	 */
	public void savePosts(String filePath, CircularDoublyLinkedList<PostManager> posts, ObservableList<UserManager> userList) {
		try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
			// First, save posts by users in the order provided by userList
			for (UserManager user : userList) {
				// Find all posts by this user
				Node<PostManager> current = posts.dummy.next;
				while (current != posts.dummy) {
					PostManager post = current.data;
					if (post.getCreator() != null && post.getCreator().getUserID().equals(user.getUserID())) {
						writePostToFile(writer, post);
					}
					current = current.next;
				}
			}
			
			// Then check if there are any remaining posts (creators not in userList)
			Node<PostManager> current = posts.dummy.next;
			while (current != posts.dummy) {
				PostManager post = current.data;
				boolean creatorInList = false;
				
				if (post.getCreator() != null) {
					for (UserManager user : userList) {
						if (user.getUserID().equals(post.getCreator().getUserID())) {
							creatorInList = true;
							break;
						}
					}
					
					if (!creatorInList) {
						writePostToFile(writer, post);
					}
				}
				
				current = current.next;
			}
		} catch (IOException e) {
			throw new RuntimeException("Error saving posts: " + e.getMessage());
		}
	}
	
	/**
	 * Helper method to write a single post to file
	 */
	private void writePostToFile(PrintWriter writer, PostManager post) {
		StringBuilder postLine = new StringBuilder();
		postLine.append(post.getPostID()).append(",");
		postLine.append(post.getCreator().getUserID()).append(",");
		postLine.append(post.getContent()).append(",");
		
		LocalDate date = post.getCreationDate();
		String formattedDate = String.format("%d.%d.%d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
		postLine.append(formattedDate);
		
		CircularDoublyLinkedList<UserManager> sharedUsers = post.getSharedUsers();
		if (sharedUsers != null) {
			Node<UserManager> sharedNode = sharedUsers.dummy.next;
			while (sharedNode != sharedUsers.dummy) {
				postLine.append(",").append(sharedNode.data.getUserID());
				sharedNode = sharedNode.next;
			}
		}
		
		writer.println(postLine.toString());
	}
}
