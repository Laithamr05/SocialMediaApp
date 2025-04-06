import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;

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
					System.out.println("Added user: " + name + " (ID: " + id + ")");
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading users file: " + e.getMessage());
			e.printStackTrace();
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
									System.out.println("Added friendship: " + user.getName() + " - " + friend.getName());
								}
								
								if (!friend.contains(user)) {
									friend.addFriend(user);
								}
							} else {
								System.out.println("Friend ID not found: " + friendId);
							}
						}
					} else {
						System.out.println("User ID not found: " + userId);
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Error reading friendships file: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void readPosts(String filePath, CircularDoublyLinkedList<PostManager> posts, CircularDoublyLinkedList<UserManager> users) {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				processPostLine(line, posts, users);
			}
		} catch (IOException e) {
			System.out.println("Error reading posts file: " + e.getMessage());
			e.printStackTrace();
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
				System.out.println("Error parsing date: " + dateStr + " - " + e.getMessage());
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
				System.out.println("Added post: " + postId + " by " + creator.getName());
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
	}
}
