import java.io.File;
import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class FriendshipManagerUI {
	private CircularDoublyLinkedList<UserManager> users;
	private FileManager fileManager;
	private FriendshipTableManager tableManager;
	private FriendshipDialogManager dialogManager;
	private ComboBox<String> sortOrderComboBox;
	private ComboBox<UserManager> userComboBox;

	public FriendshipManagerUI(CircularDoublyLinkedList<UserManager> users, FileManager fileManager) {
		this.users = users;
		this.fileManager = fileManager;
		this.tableManager = new FriendshipTableManager(users);
		this.dialogManager = new FriendshipDialogManager(users, tableManager);
	}

	public Tab createFriendshipTab() {
		VBox mainContent = new VBox();
		mainContent.setSpacing(10);
		mainContent.setPadding(new Insets(10));

		VBox friendshipContent = new VBox(10);
		friendshipContent.setPadding(new Insets(20));

		// Get the table from the table manager
		TableView<UserManager> friendshipTable = tableManager.getFriendshipTable();
		friendshipTable.setPrefHeight(400);

		// Set up user selection and sorting controls
		HBox controlsBox = createControlsBox();

		// Set up navigation buttons
		HBox navButtonBar = createNavigationButtons();

		// Set up action buttons
		HBox buttonBar = createActionButtons();

		friendshipContent.getChildren().addAll(controlsBox, friendshipTable, navButtonBar, buttonBar);
		mainContent.getChildren().add(friendshipContent);

		Tab friendshipTab = new Tab("Friendships", mainContent);
		friendshipTab.setClosable(false);

		friendshipTab.setOnSelectionChanged(e -> {
			if (friendshipTab.isSelected()) {
				updateUserComboBox();
			}
		});

		return friendshipTab;
	}

	private HBox createControlsBox() {
		HBox controlsBox = new HBox(10);
		controlsBox.setAlignment(Pos.CENTER);

		Label userLabel = new Label("Select User:");
		userComboBox = new ComboBox<>();
		userComboBox.setPrefWidth(200);
		userComboBox.setOnAction(e -> updateFriendshipDisplay());

		Label sortLabel = new Label("Sort Order:");
		sortOrderComboBox = new ComboBox<>();
		sortOrderComboBox.getItems().addAll("Unsorted", "Ascending by Username", "Descending by Username");
		sortOrderComboBox.setValue("Unsorted");
		sortOrderComboBox.setPrefWidth(200);
		sortOrderComboBox.setOnAction(e -> updateFriendshipDisplay());

		controlsBox.getChildren().addAll(userLabel, userComboBox, sortLabel, sortOrderComboBox);
		
		return controlsBox;
	}

	private HBox createNavigationButtons() {
		HBox navButtonBar = new HBox(10);
		navButtonBar.setAlignment(Pos.CENTER);

		Button prevButton = new Button("◀ Previous");
		prevButton.setOnAction(e -> navigateToPreviousFriend());

		Button nextButton = new Button("Next ▶");
		nextButton.setOnAction(e -> navigateToNextFriend());

		navButtonBar.getChildren().addAll(prevButton, nextButton);
		
		return navButtonBar;
	}

	private HBox createActionButtons() {
		HBox buttonBar = new HBox(10);
		buttonBar.setAlignment(Pos.CENTER);

		Button addFriendButton = new Button("Add Friend");
		addFriendButton.setOnAction(e -> dialogManager.showAddFriendDialog());

		Button removeFriendButton = new Button("Remove Friend");
		removeFriendButton.setOnAction(e -> dialogManager.showRemoveFriendDialog());

		Button uploadFriendshipsButton = new Button("Upload Friendships");
		uploadFriendshipsButton.setOnAction(e -> handleFriendshipFileUpload());

		buttonBar.getChildren().addAll(addFriendButton, removeFriendButton, uploadFriendshipsButton);
		
		return buttonBar;
	}

	private void updateUserComboBox() {
		userComboBox.getItems().clear();
		userComboBox.setItems(FXCollections.observableArrayList(tableManager.getSortedUserList("Unsorted")));
		userComboBox.setValue(null);
	}

	private void updateFriendshipDisplay() {
		UserManager selectedUser = userComboBox.getValue();
		if (selectedUser == null) {
			return;
		}

		showFriendshipsForUser(selectedUser);
	}

	private void handleFriendshipFileUpload() {
		// Check if users exist and are not empty
		if (users == null || users.isEmpty()) {
			showAlert(Alert.AlertType.ERROR, "Error", null, "No users loaded - please load users first");
			return;
		}
		
		// Verify that users actually contain data
		boolean hasUsers = false;
		Iterator<UserManager> userIterator = users.iterator();
		while (userIterator.hasNext()) {
			UserManager user = userIterator.next();
			if (user != null) {
				hasUsers = true;
				break;
			}
		}
		
		if (!hasUsers) {
			showAlert(Alert.AlertType.ERROR, "Error", null, "No valid users found - please load users first");
			return;
		}
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Friendship Data File");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			try {
				fileManager.loadFriendships(selectedFile.getPath(), users);
				tableManager.refreshTable();
				showAlert(Alert.AlertType.INFORMATION, "Success", null, "Friendships loaded successfully!");
			} catch (Exception e) {
				showAlert(Alert.AlertType.ERROR, "Error", null, "Error loading friendship data: " + e.getMessage());
			}
		}
	}

	private void navigateToPreviousFriend() {
		TableView<UserManager> table = tableManager.getFriendshipTable();
		int currentIndex = table.getSelectionModel().getSelectedIndex();
		
		if (currentIndex > 0) {
			table.getSelectionModel().select(currentIndex - 1);
			table.scrollTo(currentIndex - 1);
		} else if (currentIndex == -1 && !table.getItems().isEmpty()) {
			// No selection, select the first item
			table.getSelectionModel().select(0);
			table.scrollTo(0);
		}
	}

	private void navigateToNextFriend() {
		TableView<UserManager> table = tableManager.getFriendshipTable();
		int currentIndex = table.getSelectionModel().getSelectedIndex();
		int lastIndex = table.getItems().size() - 1;
		
		if (currentIndex < lastIndex) {
			table.getSelectionModel().select(currentIndex + 1);
			table.scrollTo(currentIndex + 1);
		} else if (currentIndex == -1 && !table.getItems().isEmpty()) {
			// No selection, select the first item
			table.getSelectionModel().select(0);
			table.scrollTo(0);
		}
	}

	public void showFriendshipsForUser(UserManager user) {
		if (user == null) {
			showAlert(Alert.AlertType.INFORMATION, "Notification", null, "No user selected");
			return;
		}

		CircularDoublyLinkedList<UserManager> friends = user.getFriends();
		if (friends.isEmpty()) {
			showAlert(Alert.AlertType.INFORMATION, "Notification", null, 
					 user.getName() + " has no friends yet.");
			return;
		}

		StringBuilder resultText = new StringBuilder();
		resultText.append("=== FRIENDS OF ").append(user.getName().toUpperCase()).append(" ===\n\n");
		resultText.append("Total friends: ").append(friends.size()).append("\n\n");

		java.util.Iterator<UserManager> iterator = friends.iterator();
		int friendCount = 1;
		while (iterator.hasNext()) {
			UserManager friend = iterator.next();
			if (friend != null) {
				resultText.append("FRIEND #").append(friendCount).append("\n");
				resultText.append("ID: ").append(friend.getUserID()).append("\n");
				resultText.append("Name: ").append(friend.getName()).append("\n");
				resultText.append("Age: ").append(friend.getAge()).append("\n");
				resultText.append("-----------------------------------\n");
				friendCount++;
			}
		}

		showNotification("Friends List", resultText.toString());
	}

	private void showAlert(Alert.AlertType type, String title, String header, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	private void showNotification(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.getDialogPane().setPrefHeight(500);
		alert.getDialogPane().setPrefWidth(500);
		alert.showAndWait();
	}
}
