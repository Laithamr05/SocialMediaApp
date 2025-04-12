// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.util.Pair;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

// Manages dialog windows for user input and confirmation
public class DialogManager {
    private CircularDoublyLinkedList<UserManager> users;
    private CircularDoublyLinkedList<PostManager> posts;
    private TableManager tableManager;
    
    // Share options for combo box
    private final String SHARE_ALL_FRIENDS = "Share with all friends";
    private final String SHARE_SPECIFIC_FRIENDS = "Share with specific friends";
    
    public DialogManager(CircularDoublyLinkedList<UserManager> users, 
                        CircularDoublyLinkedList<PostManager> posts,
                        TableManager tableManager) {
        this.users = users;
        this.posts = posts;
        this.tableManager = tableManager;
    }
    
    // Shows a dialog for adding or editing a user
    public void showAddUserDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add User");
        dialog.setHeaderText("Enter user details");

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField ageField = new TextField();

        GridPane grid = new GridPane();
        grid.add(new Label("User ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageField, 1, 2);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                return new Pair<>(idField.getText(), nameField.getText() + "," + ageField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            String id = pair.getKey();
            String[] nameAge = pair.getValue().split(",");
            if (nameAge.length == 2) {
                String name = nameAge[0];
                try {
                    int age = Integer.parseInt(nameAge[1]);
                    UserManager newUser = new UserManager(id, name, age);
                    users.add(newUser);
                    tableManager.refreshUserTable(users);
                } catch (NumberFormatException e) {
                    showErrorDialog("Invalid Input", "Invalid Age", "Please enter a valid number for age.");
                }
            }
        });
    }

    // Shows a dialog for adding or editing a post
    public void showAddPostDialog() {
        Dialog<PostManager> dialog = new Dialog<>();
        dialog.setTitle("Add New Post");
        dialog.setHeaderText("Enter post details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea contentField = new TextArea();
        contentField.setPromptText("Post content");
        contentField.setPrefRowCount(3);

        ComboBox<UserManager> creatorComboBox = new ComboBox<>();
        creatorComboBox.setPromptText("Select creator");
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                userList.add(user);
            }
        }
        creatorComboBox.setItems(userList);

        ListView<UserManager> sharedUsersListView = new ListView<>();
        sharedUsersListView.setPrefHeight(100);
        sharedUsersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        grid.add(new Label("Content:"), 0, 0);
        grid.add(contentField, 1, 0);
        grid.add(new Label("Creator:"), 0, 1);
        grid.add(creatorComboBox, 1, 1);
        grid.add(new Label("Share with:"), 0, 2);
        grid.add(sharedUsersListView, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String content = contentField.getText();
                UserManager creator = creatorComboBox.getValue();
                
                if (content.isEmpty() || creator == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Missing Information", "Please fill in all required fields.");
                    return null;
                }

                PostManager newPost = new PostManager(String.valueOf(posts.size() + 1), creator, content, Calendar.getInstance());
                
                ObservableList<UserManager> selectedUsers = sharedUsersListView.getSelectionModel().getSelectedItems();
                for (int i = 0; i < selectedUsers.size(); i++) {
                    UserManager user = selectedUsers.get(i);
                    newPost.shareWith(user);
                }
                
                return newPost;
            }
            return null;
        });

        Optional<PostManager> result = dialog.showAndWait();
        result.ifPresent(post -> {
            posts.insertLast(post);
            tableManager.refreshPostTable(posts);
        });
    }

    // Shows a dialog for adding or editing a friendship
    public void showAddFriendshipDialog() {
        Dialog<Pair<UserManager, UserManager>> dialog = new Dialog<>();
        dialog.setTitle("Add New Friendship");
        dialog.setHeaderText("Select two users to create a friendship");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<UserManager> user1ComboBox = new ComboBox<>();
        user1ComboBox.setPromptText("Select first user");
        ObservableList<UserManager> userList1 = FXCollections.observableArrayList();
        Iterator<UserManager> iterator1 = users.iterator();
        while (iterator1.hasNext()) {
            UserManager user = iterator1.next();
            if (user != null) {
                userList1.add(user);
            }
        }
        user1ComboBox.setItems(userList1);

        ComboBox<UserManager> user2ComboBox = new ComboBox<>();
        user2ComboBox.setPromptText("Select second user");
        user2ComboBox.setItems(userList1);

        grid.add(new Label("First User:"), 0, 0);
        grid.add(user1ComboBox, 1, 0);
        grid.add(new Label("Second User:"), 0, 1);
        grid.add(user2ComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                UserManager user1 = user1ComboBox.getValue();
                UserManager user2 = user2ComboBox.getValue();
                
                if (user1 == null || user2 == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Missing Information", "Please select both users.");
                    return null;
                }

                if (user1.equals(user2)) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Selection", "Cannot create friendship with the same user.");
                    return null;
                }

                return new Pair<>(user1, user2);
            }
            return null;
        });

        Optional<Pair<UserManager, UserManager>> result = dialog.showAndWait();
        result.ifPresent(pair -> {
            UserManager user1 = pair.getKey();
            UserManager user2 = pair.getValue();
            
            if (!user1.getFriends().contains(user2)) {
                user1.getFriends().insertLast(user2);
                user2.getFriends().insertLast(user1);
                tableManager.refreshFriendshipTable(users);
            }
        });
    }

    // Shows a confirmation dialog
    public static boolean showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // Shows an error dialog
    public static void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Shows an information dialog
    public static void showInformationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // User Dialog Methods
    public void showEditUserDialog() {
        UserManager selectedUser = tableManager.getSelectedUser();
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No Selection", "Please select a user to edit");
            return;
        }
        
        Dialog<UserManager> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit user details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonType.OK.getButtonData());
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField(selectedUser.getUserID());
        TextField nameField = new TextField(selectedUser.getName());
        TextField ageField = new TextField(String.valueOf(selectedUser.getAge()));

        grid.add(new Label("ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String id = idField.getText();
                    String name = nameField.getText();
                    int age = Integer.parseInt(ageField.getText());

                    if (id.isEmpty() || name.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "ID and Name cannot be empty");
                        return null;
                    }

                    if (age <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Age must be a positive number");
                        return null;
                    }

                    selectedUser.setUserID(id);
                    selectedUser.setName(name);
                    selectedUser.setAge(age);
                    tableManager.refreshUserTable(users);
                    return selectedUser;
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Age must be a number");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }
    
    public void showSearchUserDialog() {
        Dialog<UserManager> dialog = new Dialog<>();
        dialog.setTitle("Search User");
        dialog.setHeaderText("Enter search criteria:");

        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        ComboBox<String> searchTypeComboBox = new ComboBox<>();
        searchTypeComboBox.getItems().addAll("ID", "Name");
        searchTypeComboBox.setValue("Name");

        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term");

        grid.add(new Label("Search by:"), 0, 0);
        grid.add(searchTypeComboBox, 1, 0);
        grid.add(new Label("Search term:"), 0, 1);
        grid.add(searchField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                String searchTerm = searchField.getText().trim();
                if (searchTerm.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", null, "Please enter a search term");
                    return null;
                }

                boolean searchById = "ID".equals(searchTypeComboBox.getValue());
                UserManager foundUser = tableManager.findUser(searchTerm, searchById);

                if (foundUser == null) {
                    String message;
                    if (searchById) {
                        message = "No user found with ID: " + searchTerm;
                    } else {
                        message = "No user found with name: " + searchTerm;
                    }
                    showAlert(Alert.AlertType.INFORMATION, "Search Result", null, message);
                    return null;
                }

                return foundUser;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(foundUser -> {
            if (foundUser != null) {
                tableManager.selectUser(foundUser);
            }
        });
    }
    
    public void showEditUserDetailsDialog(UserManager user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit User Details");
        dialog.setHeaderText("Edit details for user: " + user.getName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Fields with current values
        TextField idField = new TextField(user.getUserID());
        TextField nameField = new TextField(user.getName());
        TextField ageField = new TextField(String.valueOf(user.getAge()));

        // Add fields to grid
        grid.add(new Label("User ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == saveButtonType) {
            String newID = idField.getText().trim();
            String newName = nameField.getText().trim();
            String newAge = ageField.getText().trim();
            
            if (newID.isEmpty() || newName.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Empty Fields", 
                        "User ID and Name cannot be empty.");
                return;
            }
            
            // Check for duplicate ID if changed
            if (!newID.equals(user.getUserID()) && isDuplicateID(newID)) {
                String uniqueID = generateUniqueID(newID);
                showAlert(Alert.AlertType.WARNING, "Duplicate ID", "ID Already Exists", 
                        "The ID '" + newID + "' is already in use. Using '" + uniqueID + "' instead.");
                newID = uniqueID;
            }
            
            // Update user details
            user.setUserID(newID);
            user.setName(newName);
            user.setAge(Integer.parseInt(newAge));
            
            // Refresh table to show changes
            tableManager.refreshUserTable(users);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "User Updated", 
                    "User details have been updated successfully.");
        }
    }
    
    public boolean confirmDeleteUser(UserManager user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete user: " + user.getName() + "?");

        return alert.showAndWait().filter(response -> response == ButtonType.OK).isPresent();
    }
    
    // Post Dialog Methods
    public void showCreatePostDialog() {
        Dialog<PostManager> dialog = new Dialog<>();
        dialog.setTitle("Create Post");
        dialog.setHeaderText("Enter post details");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextArea contentArea = new TextArea();
        contentArea.setPromptText("Post Content");
        contentArea.setPrefHeight(100);
        contentArea.setPrefWidth(300);
        contentArea.setWrapText(true);

        ComboBox<UserManager> creatorComboBox = new ComboBox<>();
        ObservableList<UserManager> userList = FXCollections.observableArrayList();

        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                userList.add(user);
            }
        }

        creatorComboBox.setItems(userList);
        
        // Share options combo box
        ComboBox<String> shareOptionsComboBox = new ComboBox<>();
        shareOptionsComboBox.getItems().addAll(SHARE_ALL_FRIENDS, SHARE_SPECIFIC_FRIENDS);
        shareOptionsComboBox.setValue(SHARE_SPECIFIC_FRIENDS);
        
        // Share with users section
        Label shareLabel = new Label("Share with:");
        ListView<UserManager> shareListView = new ListView<>();
        shareListView.setPrefHeight(150);
        
        // Share option behavior
        shareOptionsComboBox.setOnAction(e -> {
            String selectedOption = shareOptionsComboBox.getValue();
            if (selectedOption.equals(SHARE_ALL_FRIENDS)) {
                selectAllFriends(shareListView, true);
            }
        });
        
        // Add a listener to update the share list when creator is selected
        creatorComboBox.setOnAction(e -> {
            UserManager selectedCreator = creatorComboBox.getValue();
            if (selectedCreator != null) {
                updateShareList(shareListView, selectedCreator);
                
                // Apply the current share option
                if (shareOptionsComboBox.getValue().equals(SHARE_ALL_FRIENDS)) {
                    selectAllFriends(shareListView, true);
                }
            }
        });
        
        grid.add(new Label("Content:"), 0, 0);
        grid.add(contentArea, 1, 0);
        grid.add(new Label("Creator:"), 0, 1);
        grid.add(creatorComboBox, 1, 1);
        grid.add(new Label("Share Option:"), 0, 2);
        grid.add(shareOptionsComboBox, 1, 2);
        grid.add(shareLabel, 0, 3);
        grid.add(shareListView, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                UserManager creator = creatorComboBox.getValue();
                String content = contentArea.getText();
                
                if (content.isEmpty() || creator == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Missing Information", "Please fill in all required fields.");
                    return null;
                }

                // Generate a new post ID
                String postID = String.valueOf(posts.size() + 1);
                
                // Create post with current date
                Calendar currentDate = Calendar.getInstance();
                PostManager newPost = new PostManager(postID, creator, content, currentDate);
                
                // Get selected friends to share with
                if (shareOptionsComboBox.getValue().equals(SHARE_ALL_FRIENDS)) {
                    // Share with all friends
                    for (int i = 0; i < shareListView.getItems().size(); i++) {
                        UserManager friend = shareListView.getItems().get(i);
                        if (friend != null && !friend.equals(creator)) {
                            newPost.shareWith(friend);
                        }
                    }
                } else {
                    // Share with specifically selected friends
                    ArrayList<UserManager> selectedUsers = new ArrayList<>();
                    for (int i = 0; i < shareListView.getItems().size(); i++) {
                        CheckBox cb = getCheckBox(shareListView, i);
                        if (cb != null && cb.isSelected()) {
                            selectedUsers.add(shareListView.getItems().get(i));
                        }
                    }
                    
                    for (UserManager user : selectedUsers) {
                        if (user != null && !user.equals(creator)) {
                            newPost.shareWith(user);
                        }
                    }
                }
                
                return newPost;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(post -> {
            // Handle duplicate post ID
            if (isDuplicatePostID(post.getPostID())) {
                String newID = generateUniquePostID(post.getPostID());
                post.setPostID(newID);
            }
            
            posts.insertLast(post);
            tableManager.refreshPostTable(posts);
            
            showAlert(Alert.AlertType.INFORMATION, "Success", null, 
                     "Post created successfully!");
        });
    }
    
    public void showEditPostDialog() {
        PostManager selectedPost = tableManager.getSelectedPost();
        if (selectedPost == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No Post Selected", 
                      "Please select a post to edit.");
            return;
        }
        
        Dialog<PostManager> dialog = new Dialog<>();
        dialog.setTitle("Edit Post");
        dialog.setHeaderText("Edit post details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField postIDField = new TextField(selectedPost.getPostID());
        postIDField.setEditable(false); // Post ID cannot be changed
        
        TextArea contentArea = new TextArea(selectedPost.getContent());
        contentArea.setPrefHeight(100);
        contentArea.setWrapText(true);

        ComboBox<UserManager> creatorComboBox = new ComboBox<>();
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                userList.add(user);
            }
        }
        creatorComboBox.setItems(userList);
        creatorComboBox.setValue(selectedPost.getCreator());

        // Share options combo box
        ComboBox<String> shareOptionsComboBox = new ComboBox<>();
        shareOptionsComboBox.getItems().addAll(SHARE_ALL_FRIENDS, SHARE_SPECIFIC_FRIENDS);
        shareOptionsComboBox.setValue(SHARE_SPECIFIC_FRIENDS);
        
        // Share with users section
        Label shareLabel = new Label("Share with:");
        ListView<UserManager> shareListView = new ListView<>();
        shareListView.setPrefHeight(150);
        
        // Initialize share list with current creator's friends
        updateShareList(shareListView, selectedPost.getCreator());
        
        // Pre-select currently shared users
        Iterator<UserManager> sharedUsersIterator = selectedPost.getSharedUsers().iterator();
        while (sharedUsersIterator.hasNext()) {
            UserManager sharedUser = sharedUsersIterator.next();
            if (sharedUser != null) {
                int index = shareListView.getItems().indexOf(sharedUser);
                if (index >= 0) {
                    CheckBox cb = getCheckBox(shareListView, index);
                    if (cb != null) {
                        cb.setSelected(true);
                    }
                }
            }
        }
        
        // Share option behavior
        shareOptionsComboBox.setOnAction(e -> {
            String selectedOption = shareOptionsComboBox.getValue();
            if (selectedOption.equals(SHARE_ALL_FRIENDS)) {
                selectAllFriends(shareListView, true);
            }
        });
        
        // Update share list when creator changes
        creatorComboBox.setOnAction(e -> {
            UserManager selectedCreator = creatorComboBox.getValue();
            if (selectedCreator != null) {
                updateShareList(shareListView, selectedCreator);
                
                // Apply the current share option
                if (shareOptionsComboBox.getValue().equals(SHARE_ALL_FRIENDS)) {
                    selectAllFriends(shareListView, true);
                }
            }
        });

        grid.add(new Label("Post ID:"), 0, 0);
        grid.add(postIDField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentArea, 1, 1);
        grid.add(new Label("Creator:"), 0, 2);
        grid.add(creatorComboBox, 1, 2);
        grid.add(new Label("Share Option:"), 0, 3);
        grid.add(shareOptionsComboBox, 1, 3);
        grid.add(shareLabel, 0, 4);
        grid.add(shareListView, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String content = contentArea.getText();
                UserManager creator = creatorComboBox.getValue();
                
                if (content.isEmpty() || creator == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", 
                              "Please fill in all required fields and select a creator.");
                    return null;
                }

                // Update post details
                selectedPost.setContent(content);
                selectedPost.setCreator(creator);
                
                // Clear existing shared users
                selectedPost.getSharedUsers().clear();
                
                // Update shared users based on selection
                if (shareOptionsComboBox.getValue().equals(SHARE_ALL_FRIENDS)) {
                    // Share with all friends
                    for (int i = 0; i < shareListView.getItems().size(); i++) {
                        UserManager friend = shareListView.getItems().get(i);
                        if (friend != null && !friend.equals(creator)) {
                            selectedPost.shareWith(friend);
                        }
                    }
                } else {
                    // Share with specifically selected friends
                    for (int i = 0; i < shareListView.getItems().size(); i++) {
                        CheckBox cb = getCheckBox(shareListView, i);
                        if (cb != null && cb.isSelected()) {
                            UserManager friend = shareListView.getItems().get(i);
                            if (friend != null && !friend.equals(creator)) {
                                selectedPost.shareWith(friend);
                            }
                        }
                    }
                }
                
                return selectedPost;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(post -> {
            tableManager.refreshPostTable(posts);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Post Updated", 
                     "Post has been updated successfully.");
        });
    }
    
    public boolean confirmDeletePost(PostManager post) {
        if (post == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No Post Selected", 
                      "Please select a post to delete.");
            return false;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Post");
        confirmAlert.setContentText("Are you sure you want to delete this post?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    // Friendship Dialog Methods
    public void showAddFriendDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Friend");
        dialog.setHeaderText("Enter Users to Create Friendship");

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
            UserManager user = iterator.next();
            if (user != null) {
                userList.add(user);
            }
        }

        user1ComboBox.setItems(userList);
        user2ComboBox.setItems(userList);

        user1ComboBox.setPrefWidth(200);
        user2ComboBox.setPrefWidth(200);

        grid.add(new Label("First User:"), 0, 0);
        grid.add(user1ComboBox, 1, 0);
        grid.add(new Label("Second User:"), 0, 1);
        grid.add(user2ComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                UserManager user1 = user1ComboBox.getValue();
                UserManager user2 = user2ComboBox.getValue();

                if (user1 != null && user2 != null) {
                    return new Pair<>(user1.getUserID(), user2.getUserID());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ids -> {
            if (ids != null && !ids.getKey().isEmpty() && !ids.getValue().isEmpty()) {
                UserManager user1 = tableManager.findUser(ids.getKey(), true);
                UserManager user2 = tableManager.findUser(ids.getValue(), true);

                if (user1 == null || user2 == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid User Selection",
                            "One or both users do not exist.");
                    return;
                }

                if (user1.equals(user2)) {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Invalid Selection",
                            "You cannot add a user as their own friend.");
                    return;
                }

                if (user1.contains(user2)) {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Already Friends",
                            user1.getName() + " and " + user2.getName() + " are already friends.");
                    return;
                }

                user1.addFriend(user2);
                user2.addFriend(user1);

                tableManager.refreshFriendshipTable(users);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Friendship Created",
                        user1.getName() + " and " + user2.getName() + " are now friends.");
            }
        });
    }

    public void showRemoveFriendDialog() {
        Dialog<Pair<UserManager, UserManager>> dialog = new Dialog<>();
        dialog.setTitle("Remove Friend");
        dialog.setHeaderText("Select Users to Remove Friendship");

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
            UserManager user = iterator.next();
            if (user != null) {
                userList.add(user);
            }
        }

        user1ComboBox.setItems(userList);

        user1ComboBox.setOnAction(e -> {
            UserManager selectedUser = user1ComboBox.getValue();
            if (selectedUser != null) {
                ObservableList<UserManager> friendsList = FXCollections.observableArrayList();
                Iterator<UserManager> friendsIterator = selectedUser.getFriends().iterator();
                while (friendsIterator.hasNext()) {
                    UserManager friend = friendsIterator.next();
                    if (friend != null) {
                        friendsList.add(friend);
                    }
                }
                user2ComboBox.setItems(friendsList);
            }
        });

        user1ComboBox.setPrefWidth(200);
        user2ComboBox.setPrefWidth(200);

        grid.add(new Label("First User:"), 0, 0);
        grid.add(user1ComboBox, 1, 0);
        grid.add(new Label("Friend to Remove:"), 0, 1);
        grid.add(user2ComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == removeButtonType) {
                UserManager user1 = user1ComboBox.getValue();
                UserManager user2 = user2ComboBox.getValue();

                if (user1 != null && user2 != null) {
                    return new Pair<>(user1, user2);
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(userPair -> {
            if (userPair != null) {
                UserManager user1 = userPair.getKey();
                UserManager user2 = userPair.getValue();

                if (user1 == null || user2 == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Selection",
                            "Please select valid users to remove friendship.");
                    return;
                }

                if (!user1.contains(user2)) {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Not Friends",
                            user1.getName() + " and " + user2.getName() + " are not friends.");
                    return;
                }

                user1.getFriends().delete(user2);
                user2.getFriends().delete(user1);

                tableManager.refreshFriendshipTable(users);

                showAlert(Alert.AlertType.INFORMATION, "Success", "Friendship Removed",
                        user1.getName() + " and " + user2.getName() + " are no longer friends.");
            }
        });
    }
    
    // Utility Methods
    private void selectAllFriends(ListView<UserManager> listView, boolean select) {
        for (int i = 0; i < listView.getItems().size(); i++) {
            CheckBox cb = getCheckBox(listView, i);
            if (cb != null) {
                cb.setSelected(select);
            }
        }
    }
    
    private void updateShareList(ListView<UserManager> shareListView, UserManager creator) {
        // Clear the current items
        ObservableList<UserManager> friendsList = FXCollections.observableArrayList();
        
        // Get the creator's friends
        if (creator != null && creator.getFriends() != null) {
            Iterator<UserManager> friendsIterator = creator.getFriends().iterator();
            while (friendsIterator.hasNext()) {
                UserManager friend = friendsIterator.next();
                if (friend != null) {
                    friendsList.add(friend);
                }
            }
        }
        
        // Update the share list with friends
        shareListView.setItems(friendsList);
        
        // Set up the checkboxes for selection
        shareListView.setCellFactory(CheckBoxListCell.forListView(item -> {
            return new SimpleBooleanProperty(false);
        }));
    }
    
    private CheckBox getCheckBox(ListView<UserManager> listView, int index) {
        // This is a workaround since directly accessing child nodes is not visible
        try {
            ListCell<UserManager> cell = (ListCell<UserManager>)listView.lookup(".list-cell[index=\"" + index + "\"]");
            if (cell != null) {
                // Look for the checkbox in the cell
                for (javafx.scene.Node node : cell.getChildrenUnmodifiable()) {
                    if (node instanceof CheckBox) {
                        return (CheckBox)node;
                    }
                }
            }
        } catch (Exception e) {
            // In case of error, return null
        }
        return null;
    }
    
    private boolean isDuplicateID(String id) {
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user.getUserID().equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    private String generateUniqueID(String baseID) {
        int counter = 1;
        String newID = baseID + "_" + counter;
        
        while (isDuplicateID(newID)) {
            counter++;
            newID = baseID + "_" + counter;
        }
        
        return newID;
    }
    
    private boolean isDuplicatePostID(String postID) {
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post.getPostID().equals(postID)) {
                return true;
            }
        }
        return false;
    }
    
    private String generateUniquePostID(String baseID) {
        int counter = 1;
        String newID = baseID + "_" + counter;
        
        while (isDuplicatePostID(newID)) {
            counter++;
            newID = baseID + "_" + counter;
        }
        
        return newID;
    }
    
    public void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
