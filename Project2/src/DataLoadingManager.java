// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.io.File;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DataLoadingManager { // class to manage data loading operations
	private CircularDoublyLinkedList<UserManager> users; // list of users in the system
	private CircularDoublyLinkedList<PostManager> posts; // list of posts in the system
	private FileManager fileManager; // file manager for loading data

	public DataLoadingManager(CircularDoublyLinkedList<UserManager> users, CircularDoublyLinkedList<PostManager> posts,
			FileManager fileManager) { // constructor to initialize the manager
		this.users = users; // store the user list
		this.posts = posts; // store the post list
		this.fileManager = fileManager; // store the file manager
	}

	public void handleUserFileUpload(Stage stage) { // method to handle user file uploads
		FileChooser fileChooser = new FileChooser(); // create file chooser dialog
		fileChooser.setTitle("Open User Data File"); // set dialog title
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt")); // only show text files

		File selectedFile = fileChooser.showOpenDialog(stage); // show dialog and get selected file
		if (selectedFile != null) { // check if a file was selected
			try {
				fileManager.loadUsers(selectedFile.getPath(), users); // load users from file
				showAlert("Users loaded successfully!"); // show success message
			} catch (Exception e) {
				showAlert("Error loading user data: " + e.getMessage()); // show error message
			}
		}
	}

	public void handlePostFileUpload(Stage stage) { // method to handle post file uploads
		if (users.isEmpty()) { // check if users are loaded first
			showAlert("Please load users first!"); // show warning if no users
			return;
		}

		FileChooser fileChooser = new FileChooser(); // create file chooser dialog
		fileChooser.setTitle("Open Post Data File"); // set dialog title
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt")); // only show text files

		File selectedFile = fileChooser.showOpenDialog(stage); // show dialog and get selected file
		if (selectedFile != null) { // check if a file was selected
			try {
				fileManager.loadPosts(selectedFile.getPath(), posts, users); // load posts from file
				showAlert("Posts loaded successfully!"); // show success message
			} catch (Exception e) {
				showAlert("Error loading post data: " + e.getMessage()); // show error message
			}
		}
	}

	public void handleFriendshipFileUpload(Stage stage) { // method to handle friendship file uploads
		if (users.isEmpty()) { // check if users are loaded first
			showAlert("Please load users first!"); // show warning if no users
			return;
		}

		FileChooser fileChooser = new FileChooser(); // create file chooser dialog
		fileChooser.setTitle("Open Friendship Data File"); // set dialog title
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt")); // only show text files

		File selectedFile = fileChooser.showOpenDialog(stage); // show dialog and get selected file
		if (selectedFile != null) { // check if a file was selected
			try {
				fileManager.loadFriendships(selectedFile.getPath(), users); // load friendships from file
				showAlert("Friendships loaded successfully!"); // show success message
			} catch (Exception e) {
				showAlert("Error loading friendship data: " + e.getMessage()); // show error message
			}
		}
	}

	private void showAlert(String message) { // method to display alert messages
		Alert alert = new Alert(Alert.AlertType.INFORMATION); // create information alert
		alert.setTitle("Information"); // set alert title
		alert.setHeaderText(null); // no header text
		alert.setContentText(message); // set alert message
		alert.showAndWait(); // show alert and wait for user action
	}
}