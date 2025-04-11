import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import javafx.collections.ListChangeListener;
import java.util.Iterator;
import java.util.Calendar;

public class GUI extends Application {
	private CircularDoublyLinkedList<UserManager> users;
	private CircularDoublyLinkedList<PostManager> posts;
	private FileManager fileManager;
	private UserManagerUI userManagerUI;
	private PostManagerUI postManagerUI;
	private FriendshipManagerUI friendshipManagerUI;
	private ReportManagerUI reportManagerUI;
	private DataManagementUI dataManagementUI;
	private WelcomePage welcomePage;
	private TabPane tabPane;
	private BorderPane mainLayout;

	@Override
	public void start(Stage primaryStage) {
		users = new CircularDoublyLinkedList<>();
		posts = new CircularDoublyLinkedList<>();
		fileManager = new FileManager();

		// Load default data if available
		loadDefaultData();

		mainLayout = new BorderPane();
		mainLayout.setPadding(new Insets(10));

		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		welcomePage = new WelcomePage(tabPane);
		welcomePage.setUsers(users);
		welcomePage.setPosts(posts);
		userManagerUI = new UserManagerUI(users, fileManager, welcomePage);
		postManagerUI = new PostManagerUI(posts, fileManager, users, welcomePage);
		friendshipManagerUI = new FriendshipManagerUI(users, fileManager);
		reportManagerUI = new ReportManagerUI(users, posts);
		dataManagementUI = new DataManagementUI(users, posts, fileManager);

		Tab welcomeTab = new Tab("Welcome", welcomePage.createWelcomePage());
		welcomeTab.setClosable(false);
		tabPane.getTabs().add(welcomeTab);

		tabPane.getTabs().add(userManagerUI.createUserTab());
		tabPane.getTabs().add(postManagerUI.createPostTab());
		tabPane.getTabs().add(friendshipManagerUI.createFriendshipTab());
		tabPane.getTabs().add(reportManagerUI.createReportsTab());
		tabPane.getTabs().add(dataManagementUI.createDataManagementTab());

		mainLayout.setCenter(tabPane);

		MenuBarManager menuBarManager = new MenuBarManager(tabPane);
		mainLayout.setTop(menuBarManager.createMenuBar());

		Scene scene = new Scene(mainLayout, 800, 600);
		primaryStage.setTitle("Social Network Application");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private MenuBar createMenuBar() {
		MenuBar menuBar = new MenuBar();
		
		Menu fileMenu = new Menu("File");
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> System.exit(0));
		fileMenu.getItems().add(exitItem);

		Menu viewMenu = new Menu("View");
		MenuItem welcomeItem = new MenuItem("Welcome Page");
		welcomeItem.setOnAction(e -> tabPane.getSelectionModel().select(0));

		MenuItem usersItem = new MenuItem("Users");
		usersItem.setOnAction(e -> tabPane.getSelectionModel().select(1));

		MenuItem postsItem = new MenuItem("Posts");
		postsItem.setOnAction(e -> tabPane.getSelectionModel().select(2));
		
		MenuItem friendshipItem = new MenuItem("Friendship");
		friendshipItem.setOnAction(e -> tabPane.getSelectionModel().select(3));
		
		MenuItem reportsItem = new MenuItem("Reports & Statistics");
		reportsItem.setOnAction(e -> tabPane.getSelectionModel().select(4));
		
		MenuItem dataManagementItem = new MenuItem("Data Management");
		dataManagementItem.setOnAction(e -> tabPane.getSelectionModel().select(5));

		viewMenu.getItems().addAll(welcomeItem, usersItem, postsItem, friendshipItem, reportsItem, dataManagementItem);

		Menu userMenu = new Menu("User Management");
		MenuItem addUserItem = new MenuItem("Add User");
		addUserItem.setOnAction(e -> {
			Dialog<UserManager> dialog = new Dialog<>();
			dialog.setTitle("Add User");
			dialog.setHeaderText("Enter User Details");

			ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));

			TextField idField = new TextField();
			TextField nameField = new TextField();
			TextField ageField = new TextField();
			
			idField.setPromptText("User ID");
			nameField.setPromptText("Name");
			ageField.setPromptText("Age");

			grid.add(new Label("User ID:"), 0, 0);
			grid.add(idField, 1, 0);
			grid.add(new Label("Name:"), 0, 1);
			grid.add(nameField, 1, 1);
			grid.add(new Label("Age:"), 0, 2);
			grid.add(ageField, 1, 2);

			dialog.getDialogPane().setContent(grid);
			dialog.showAndWait();
		});

		MenuItem editUserItem = new MenuItem("Edit User");
		editUserItem.setOnAction(e -> {
			Dialog<UserManager> dialog = new Dialog<>();
			dialog.setTitle("Edit User");
			dialog.setHeaderText("Edit User Details");

			ButtonType editButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(editButtonType, ButtonType.CANCEL);

			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));

			TextField nameField = new TextField();
			TextField ageField = new TextField();
			
			nameField.setPromptText("Name");
			ageField.setPromptText("Age");

			grid.add(new Label("Name:"), 0, 0);
			grid.add(nameField, 1, 0);
			grid.add(new Label("Age:"), 0, 1);
			grid.add(ageField, 1, 1);

			dialog.getDialogPane().setContent(grid);
			dialog.showAndWait();
		});

		userMenu.getItems().addAll(addUserItem, editUserItem);

		menuBar.getMenus().addAll(fileMenu, viewMenu, userMenu);
		return menuBar;
	}

	private VBox createWelcomePage() {
		VBox welcomePage = new VBox(20);
		welcomePage.setPadding(new Insets(50));
		welcomePage.setAlignment(Pos.CENTER);

		ImageView welcomeImage = new ImageView();
		try {
			welcomeImage.setImage(new Image(new File("src/facebook.png").toURI().toString()));
		} catch (Exception e) {
			// Error loading image silently ignored
		}
		welcomeImage.setFitWidth(400);
		welcomeImage.setFitHeight(300);
		welcomeImage.setPreserveRatio(true);

		Button userButton = new Button("View Users");
		userButton.setOnAction(e -> tabPane.getSelectionModel().select(1));
		userButton.setPrefWidth(200);

		Button postButton = new Button("View Posts");
		postButton.setOnAction(e -> tabPane.getSelectionModel().select(2));
		postButton.setPrefWidth(200);

		welcomePage.getChildren().addAll(welcomeImage, userButton, postButton);
		return welcomePage;
	}

	private Tab createUserTab() {
		VBox userContent = new VBox(10);
		userContent.setPadding(new Insets(20));

		TableView<UserManager> userTable = createUserTable();

		Button addUserButton = new Button("Add User");
		addUserButton.setOnAction(e -> showAddUserDialog());

		Button editUserButton = new Button("Edit User");
		editUserButton.setOnAction(e -> showEditUserDialog());

		Button uploadUserButton = new Button("Upload Users");
		uploadUserButton.setOnAction(e -> handleUserFileUpload());

		Button backButton = new Button("Back to Welcome");
		backButton.setOnAction(e -> tabPane.getSelectionModel().select(0));

		HBox buttonBar = new HBox(10);
		buttonBar.getChildren().addAll(addUserButton, editUserButton, uploadUserButton, backButton);

		userContent.getChildren().addAll(userTable, buttonBar);

		Tab userTab = new Tab("Users", userContent);
		userTab.setClosable(false);
		return userTab;
	}

	private Tab createPostTab() {
		VBox postContent = new VBox(10);
		postContent.setPadding(new Insets(20));

		TableView<PostManager> postTable = createPostTable();

		Button createPostButton = new Button("Create Post");
		createPostButton.setOnAction(e -> showCreatePostDialog());

		Button uploadPostButton = new Button("Upload Posts");
		uploadPostButton.setOnAction(e -> handlePostFileUpload());

		Button backButton = new Button("Back to Welcome");
		backButton.setOnAction(e -> tabPane.getSelectionModel().select(0));

		HBox buttonBar = new HBox(10);
		buttonBar.getChildren().addAll(createPostButton, uploadPostButton, backButton);

		postContent.getChildren().addAll(postTable, buttonBar);

		Tab postTab = new Tab("Posts", postContent);
		postTab.setClosable(false);
		return postTab;
	}

	private Tab createFriendshipTab() {
		VBox friendshipContent = new VBox(10);
		friendshipContent.setPadding(new Insets(20));

		TableView<UserManager> friendshipTable = new TableView<>();

		Button addFriendButton = new Button("Add Friend");
		addFriendButton.setOnAction(e -> showAddFriendDialog());

		Button removeFriendButton = new Button("Remove Friend");
		removeFriendButton.setOnAction(e -> showRemoveFriendDialog());

		HBox buttonBar = new HBox(10);
		buttonBar.getChildren().addAll(addFriendButton, removeFriendButton);

		friendshipContent.getChildren().addAll(friendshipTable, buttonBar);

		Tab friendshipTab = new Tab("Friendships", friendshipContent);
		friendshipTab.setClosable(false);
		return friendshipTab;
	}

	private void showCreatePostDialog() {
		Dialog<PostManager> dialog = new Dialog<>();
		dialog.setTitle("Create Post");
		dialog.setHeaderText("Enter post details");

		ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField contentField = new TextField();
		contentField.setPromptText("Post Content");

		ComboBox<UserManager> creatorComboBox = new ComboBox<>();
		ObservableList<UserManager> userList = FXCollections.observableArrayList();

		Iterator<UserManager> iterator = users.iterator();
		while (iterator.hasNext()) {
			userList.add(iterator.next());
		}

		creatorComboBox.setItems(userList);

		grid.add(new Label("Content:"), 0, 0);
		grid.add(contentField, 1, 0);
		grid.add(new Label("Creator:"), 0, 1);
		grid.add(creatorComboBox, 1, 1);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == createButtonType) {
				UserManager creator = creatorComboBox.getValue();
				if (contentField.getText().isEmpty() || creator == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid Input");
					alert.setContentText("Please fill in all fields and select a creator.");
					alert.showAndWait();
					return null;
				}

				String postID = String.valueOf(posts.size() + 1);
				Calendar currentDate = Calendar.getInstance();
				PostManager newPost = new PostManager(postID, creator, contentField.getText(), currentDate);
				posts.insertLast(newPost);
				updatePostTable();
				return newPost;
			}
			return null;
		});

		dialog.showAndWait();
	}

	private void showAddFriendDialog() {
		Dialog<Boolean> dialog = new Dialog<>();
		dialog.setTitle("Add Friend");
		dialog.setHeaderText("Select users to add as friends");

		ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		ComboBox<UserManager> user1ComboBox = new ComboBox<>();
		ComboBox<UserManager> user2ComboBox = new ComboBox<>();
		ObservableList<UserManager> userList = FXCollections.observableArrayList();

		Iterator<UserManager> iterator = users.iterator();
		while (iterator.hasNext()) {
			userList.add(iterator.next());
		}

		user1ComboBox.setItems(userList);
		user2ComboBox.setItems(userList);

		grid.add(new Label("User 1:"), 0, 0);
		grid.add(user1ComboBox, 1, 0);
		grid.add(new Label("User 2:"), 0, 1);
		grid.add(user2ComboBox, 1, 1);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				UserManager user1 = user1ComboBox.getValue();
				UserManager user2 = user2ComboBox.getValue();

				if (user1 == null || user2 == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid Selection");
					alert.setContentText("Please select both users.");
					alert.showAndWait();
					return false;
				}

				if (user1.equals(user2)) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid Selection");
					alert.setContentText("Cannot add a user as their own friend.");
					alert.showAndWait();
					return false;
				}

				if (user1.isFriend(user2)) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Already Friends");
					alert.setContentText("These users are already friends.");
					alert.showAndWait();
					return false;
				}

				user1.addFriend(user2);
				user2.addFriend(user1);
				updateUserTable();
				return true;
			}
			return false;
		});

		dialog.showAndWait();
	}

	private void showRemoveFriendDialog() {
		Dialog<Boolean> dialog = new Dialog<>();
		dialog.setTitle("Remove Friend");
		dialog.setHeaderText("Select users to remove friendship");

		ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		ComboBox<UserManager> user1ComboBox = new ComboBox<>();
		ComboBox<UserManager> user2ComboBox = new ComboBox<>();
		ObservableList<UserManager> userList = FXCollections.observableArrayList();

		Iterator<UserManager> iterator = users.iterator();
		while (iterator.hasNext()) {
			userList.add(iterator.next());
		}

		user1ComboBox.setItems(userList);
		user2ComboBox.setItems(userList);

		grid.add(new Label("User 1:"), 0, 0);
		grid.add(user1ComboBox, 1, 0);
		grid.add(new Label("User 2:"), 0, 1);
		grid.add(user2ComboBox, 1, 1);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == removeButtonType) {
				UserManager user1 = user1ComboBox.getValue();
				UserManager user2 = user2ComboBox.getValue();

				if (user1 == null || user2 == null) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid Selection");
					alert.setContentText("Please select both users.");
					alert.showAndWait();
					return false;
				}

				if (user1.equals(user2)) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid Selection");
					alert.setContentText("Cannot remove a user from themselves.");
					alert.showAndWait();
					return false;
				}

				if (!user1.isFriend(user2)) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Not Friends");
					alert.setContentText("These users are not friends.");
					alert.showAndWait();
					return false;
				}

				user1.removeFriend(user2);
				user2.removeFriend(user1);
				updateUserTable();
				return true;
			}
			return false;
		});

		dialog.showAndWait();
	}

	private void handleFriendshipFileUpload() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Friendship Data File");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			fileManager.loadFriendships(selectedFile.getAbsolutePath(), users);
			updateFriendshipTable();
		}
	}

	private void updateFriendshipTable() {
		ObservableList<UserManager> userList = FXCollections.observableArrayList();
		Iterator<UserManager> iterator = users.iterator();
		while (iterator.hasNext()) {
			userList.add(iterator.next());
		}
		friendshipManagerUI.getFriendshipTable().setItems(userList);
		friendshipManagerUI.getFriendshipTable().refresh();
	}

	private void setDefaultUser() {
	}

	private TableView<UserManager> createUserTable() {
		TableView<UserManager> table = new TableView<>();
		table.setPrefHeight(400);

		TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");
		idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserID()));
		idColumn.setPrefWidth(150);

		TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
		nameColumn.setPrefWidth(200);

		TableColumn<UserManager, String> ageColumn = new TableColumn<>("Age");
		ageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAge()));
		ageColumn.setPrefWidth(100);

		table.getColumns().addAll(idColumn, nameColumn, ageColumn);
		return table;
	}

	private TableView<PostManager> createPostTable() {
		TableView<PostManager> table = new TableView<>();
		table.setPrefHeight(400);

		TableColumn<PostManager, String> postIDColumn = new TableColumn<>("Post ID");
		postIDColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPostID()));
		postIDColumn.setPrefWidth(100);

		TableColumn<PostManager, String> creatorColumn = new TableColumn<>("Creator");
		creatorColumn.setCellValueFactory(cellData -> {
			PostManager post = cellData.getValue();
			return new SimpleStringProperty(
					post != null && post.getCreator() != null ? post.getCreator().getName() : "");
		});
		creatorColumn.setPrefWidth(100);

		TableColumn<PostManager, String> contentColumn = new TableColumn<>("Content");
		contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));
		contentColumn.setPrefWidth(300);

		TableColumn<PostManager, String> dateColumn = new TableColumn<>("Creation Date");
		dateColumn.setCellValueFactory(
				cellData -> new SimpleStringProperty(formatDate(cellData.getValue().getCreationDate())));
		dateColumn.setPrefWidth(150);

		TableColumn<PostManager, String> sharedWithColumn = createSharedUsersColumn();
		sharedWithColumn.setPrefWidth(200);

		table.getColumns().addAll(postIDColumn, creatorColumn, contentColumn, dateColumn, sharedWithColumn);

		table.getItems().addListener((ListChangeListener<PostManager>) change -> {
			while (change.next()) {
				if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
					table.refresh();
				}
			}
		});

		return table;
	}

	private TableColumn<PostManager, String> createSharedUsersColumn() {
		TableColumn<PostManager, String> sharedUsersColumn = new TableColumn<>("Shared With");
		sharedUsersColumn.setCellValueFactory(cellData -> {
			String sharedUsers = "";
			CircularDoublyLinkedList<UserManager> sharedUsersList = cellData.getValue().getSharedUsers();
			Iterator<UserManager> iterator = sharedUsersList.iterator();
			while (iterator.hasNext()) {
				UserManager user = iterator.next();
				if (!sharedUsers.isEmpty()) {
					sharedUsers += ", ";
				}
				sharedUsers += user.getName();
			}

			SimpleStringProperty result;
			if (sharedUsers.isEmpty()) {
				result = new SimpleStringProperty("Not shared");
			} else {
				result = new SimpleStringProperty(sharedUsers);
			}
			return result;
		});
		return sharedUsersColumn;
	}

	private void showAddUserDialog() {
		Dialog<UserManager> dialog = new Dialog<>();
		dialog.setTitle("Add New User");
		dialog.setHeaderText("Enter User Details");

		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField idField = new TextField();
		idField.setPromptText("User ID");

		TextField nameField = new TextField();
		nameField.setPromptText("Name");

		TextField ageField = new TextField();
		ageField.setPromptText("Age");

		grid.add(new Label("User ID:"), 0, 0);
		grid.add(idField, 1, 0);
		grid.add(new Label("Name:"), 0, 1);
		grid.add(nameField, 1, 1);
		grid.add(new Label("Age:"), 0, 2);
		grid.add(ageField, 1, 2);

		dialog.getDialogPane().setContent(grid);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				if (idField.getText().isEmpty() || nameField.getText().isEmpty() || ageField.getText().isEmpty()) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid Input");
					alert.setContentText("Please fill in all fields.");
					alert.showAndWait();
					return null;
				}

				String newID = idField.getText();
				Node<UserManager> current = users.dummy.next;
				while (current != users.dummy) {
					if (current.data.getUserID().equals(newID)) {
						Alert alert = new Alert(Alert.AlertType.ERROR);
						alert.setTitle("Error");
						alert.setHeaderText("Duplicate ID");
						alert.setContentText("This User ID already exists. Please choose a different one.");
						alert.showAndWait();
						return null;
					}
					current = current.next;
				}

				String name = nameField.getText();
				String age = ageField.getText();

				return new UserManager(newID, name, age);
			}
			return null;
		});

		dialog.showAndWait().ifPresent(newUser -> {
			users.insertLast(newUser);
			updateUserTable();
		});
	}

	private void showEditUserDialog() {
		Dialog<String> searchDialog = new Dialog<>();
		searchDialog.setTitle("Search User");
		searchDialog.setHeaderText("Search User by ID or Name");

		ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
		searchDialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField searchField = new TextField();
		searchField.setPromptText("User ID or Name");

		ToggleGroup searchTypeGroup = new ToggleGroup();
		RadioButton idRadio = new RadioButton("Search by ID");
		idRadio.setToggleGroup(searchTypeGroup);
		idRadio.setSelected(true);

		RadioButton nameRadio = new RadioButton("Search by Name");
		nameRadio.setToggleGroup(searchTypeGroup);

		grid.add(new Label("Search:"), 0, 0);
		grid.add(searchField, 1, 0);
		grid.add(idRadio, 0, 1);
		grid.add(nameRadio, 1, 1);

		searchDialog.getDialogPane().setContent(grid);

		searchField.requestFocus();

		searchDialog.setResultConverter(dialogButton -> {
			if (dialogButton == searchButtonType) {
				return searchField.getText();
			}
			return null;
		});

		searchDialog.showAndWait().ifPresent(searchTerm -> {
			boolean searchById = idRadio.isSelected();
			UserManager foundUser = findUser(searchTerm, searchById);

			if (foundUser != null) {
				showEditUserDetailsDialog(foundUser);
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("User Not Found");
				alert.setHeaderText(null);

				String errorMessage;
				if (searchById) {
					errorMessage = "No user found with the specified ID: " + searchTerm;
				} else {
					errorMessage = "No user found with the specified name: " + searchTerm;
				}
				alert.setContentText(errorMessage);
				alert.showAndWait();
			}
		});
	}

	private UserManager findUser(String searchTerm, boolean searchById) {
		Node<UserManager> current = users.dummy.next;
		while (current != users.dummy) {
			if (searchById) {
				if (current.data.getUserID().equals(searchTerm)) {
					return current.data;
				}
			} else {
				if (current.data.getName().equalsIgnoreCase(searchTerm)) {
					return current.data;
				}
			}
			current = current.next;
		}
		return null;
	}

	private void showEditUserDetailsDialog(UserManager user) {
		Dialog<UserManager> dialog = new Dialog<>();
		dialog.setTitle("Edit User");
		dialog.setHeaderText("Edit User Details");

		ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField userIdField = new TextField(user.getUserID());
		userIdField.setEditable(false); // User ID cannot be changed

		TextField nameField = new TextField(user.getName());

		TextField ageField = new TextField(user.getAge());

		grid.add(new Label("User ID:"), 0, 0);
		grid.add(userIdField, 1, 0);
		grid.add(new Label("Name:"), 0, 1);
		grid.add(nameField, 1, 1);
		grid.add(new Label("Age:"), 0, 2);
		grid.add(ageField, 1, 2);

		dialog.getDialogPane().setContent(grid);

		nameField.requestFocus();

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				if (nameField.getText().isEmpty() || ageField.getText().isEmpty()) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Invalid Input");
					alert.setContentText("Please fill in all fields.");
					alert.showAndWait();
					return null;
				}

				String name = nameField.getText();
				String age = ageField.getText();

				user.setName(name);
				user.setAge(age);

				return user;
			}
			return null;
		});

		dialog.showAndWait().ifPresent(updatedUser -> {
			updateUserTable();
		});
	}

	private void handleUserFileUpload() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select User Data File");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			fileManager.loadUsers(selectedFile.getAbsolutePath(), users);
			updateUserTable();
		}
	}

	private void handlePostFileUpload() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Post Data File");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		File selectedFile = fileChooser.showOpenDialog(null);

		if (selectedFile != null) {
			fileManager.loadPosts(selectedFile.getAbsolutePath(), posts, users);
			updatePostTable();
		}
	}

	private void updateUserTable() {
		ObservableList<UserManager> userList = FXCollections.observableArrayList();
		Iterator<UserManager> iterator = users.iterator();
		while (iterator.hasNext()) {
			userList.add(iterator.next());
		}
		userManagerUI.getUserTable().setItems(userList);
	}

	private void updatePostTable() {
		ObservableList<PostManager> postList = FXCollections.observableArrayList();
		Iterator<PostManager> iterator = posts.iterator();
		while (iterator.hasNext()) {
			postList.add(iterator.next());
		}
		postManagerUI.getPostTable().setItems(postList);
		postManagerUI.getPostTable().refresh();
	}

	private String formatDate(Calendar date) {
		int day = date.get(Calendar.DAY_OF_MONTH);
		int month = date.get(Calendar.MONTH) + 1; // Calendar months are 0-based
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

		return dayStr + "." + monthStr + "." + year;
	}

	private void loadDefaultData() {
		// Try to load users from default file
		File defaultUsersFile = new File("users.txt");
		if (defaultUsersFile.exists()) {
			fileManager.loadUsers(defaultUsersFile.getAbsolutePath(), users);
		}

		// Try to load posts from default file
		File defaultPostsFile = new File("posts.txt");
		if (defaultPostsFile.exists()) {
			fileManager.loadPosts(defaultPostsFile.getAbsolutePath(), posts, users);
		}

		// Try to load friendships from default file
		File defaultFriendshipsFile = new File("friendships.txt");
		if (defaultFriendshipsFile.exists()) {
			fileManager.loadFriendships(defaultFriendshipsFile.getAbsolutePath(), users);
		}
	}
}
