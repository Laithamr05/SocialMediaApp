import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;

public class FriendshipManagerUI {
    private TableView<UserManager> friendshipTable;
    private CircularDoublyLinkedList<UserManager> users;
    private FileManager fileManager;
    private ComboBox<String> sortOrderComboBox;
    private ComboBox<UserManager> userComboBox;

    public FriendshipManagerUI(CircularDoublyLinkedList<UserManager> users, FileManager fileManager) {
        this.users = users;
        this.fileManager = fileManager;
        this.friendshipTable = createFriendshipTable();
    }

    public Tab createFriendshipTab() {
        VBox mainContent = new VBox();
        mainContent.setSpacing(10);
        mainContent.setPadding(new Insets(10));

        VBox friendshipContent = new VBox(10);
        friendshipContent.setPadding(new Insets(20));

        friendshipTable.setPrefHeight(400);

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

        HBox navButtonBar = new HBox(10);
        navButtonBar.setAlignment(Pos.CENTER);
        
        Button prevButton = new Button("◀ Previous");
        prevButton.setOnAction(e -> navigateToPreviousFriend());
        
        Button nextButton = new Button("Next ▶");
        nextButton.setOnAction(e -> navigateToNextFriend());
        
        navButtonBar.getChildren().addAll(prevButton, nextButton);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);

        Button addFriendButton = new Button("Add Friend");
        addFriendButton.setOnAction(e -> showAddFriendDialog());

        Button removeFriendButton = new Button("Remove Friend");
        removeFriendButton.setOnAction(e -> showRemoveFriendDialog());

        Button uploadFriendshipsButton = new Button("Upload Friendships");
        uploadFriendshipsButton.setOnAction(e -> handleFriendshipFileUpload());

        buttonBar.getChildren().addAll(addFriendButton, removeFriendButton, uploadFriendshipsButton);

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

    private void updateUserComboBox() {
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                userList.add(user);
            }
        }
        
        userComboBox.getItems().clear();
        userComboBox.setItems(userList);
        
        userComboBox.setValue(null);
    }

    private void updateFriendshipDisplay() {
        UserManager selectedUser = userComboBox.getValue();
        if (selectedUser == null) {
            return;
        }

        ArrayList<UserManager> friends = new ArrayList<UserManager>();
        CircularDoublyLinkedList<UserManager> friendsList = selectedUser.getFriends();
        Iterator<UserManager> iterator = friendsList.iterator();
        while (iterator.hasNext()) {
            friends.add(iterator.next());
        }

        String sortOrder = sortOrderComboBox.getValue();
        if (sortOrder.equals("Ascending by Username")) {
            Collections.sort(friends, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager u1, UserManager u2) {
                    return u1.getName().compareTo(u2.getName());
                }
            });
        } else if (sortOrder.equals("Descending by Username")) {
            Collections.sort(friends, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager u1, UserManager u2) {
                    return u2.getName().compareTo(u1.getName());
                }
            });
        }

        String friendsList_str = "";
        if (friends.isEmpty()) {
            friendsList_str = "No friends found.";
        } else {
            Iterator<UserManager> friendsIterator = friends.iterator();
            boolean first = true;
            while (friendsIterator.hasNext()) {
                if (!first) {
                    friendsList_str += "\n";
                }
                friendsList_str += friendsIterator.next().getName();
                first = false;
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Friends List");
        alert.setHeaderText("Friends of " + selectedUser.getName());
        alert.setContentText(friendsList_str);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    private void showAddFriendDialog() {
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
            userList.add(iterator.next());
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
            if (!ids.getKey().isEmpty() && !ids.getValue().isEmpty()) {
                UserManager user1 = findUserById(ids.getKey());
                UserManager user2 = findUserById(ids.getValue());

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

                updateFriendshipTable();
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Friendship Created", 
                        user1.getName() + " and " + user2.getName() + " are now friends.");
            }
        });
    }

    private void showRemoveFriendDialog() {
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
            userList.add(iterator.next());
        }
        
        user1ComboBox.setItems(userList);
        
        user1ComboBox.setOnAction(e -> {
            UserManager selectedUser = user1ComboBox.getValue();
            if (selectedUser != null) {
                ObservableList<UserManager> friendsList = FXCollections.observableArrayList();
                Iterator<UserManager> friendsIterator = selectedUser.getFriends().iterator();
                while (friendsIterator.hasNext()) {
                    friendsList.add(friendsIterator.next());
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
            UserManager user1 = userPair.getKey();
            UserManager user2 = userPair.getValue();
            
            if (!user1.contains(user2)) {
                showAlert(Alert.AlertType.WARNING, "Warning", "Not Friends", 
                        user1.getName() + " and " + user2.getName() + " are not friends.");
                return;
            }
            
            user1.getFriends().delete(user2);
            user2.getFriends().delete(user1);
            
            updateFriendshipTable();
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "Friendship Removed", 
                    user1.getName() + " and " + user2.getName() + " are no longer friends.");
        });
    }

    private UserManager findUserById(String id) {
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user.getUserID().equals(id)) {
                return user;
            }
        }
        return null;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private TableView<UserManager> createFriendshipTable() {
        TableView<UserManager> tableView = new TableView<>();

        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            SimpleStringProperty property = new SimpleStringProperty("");
            if (user != null) {
                property = new SimpleStringProperty(user.getName());
            }
            return property;
        });

        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            SimpleStringProperty property = new SimpleStringProperty("");
            if (user != null) {
                property = new SimpleStringProperty(user.getUserID());
            }
            return property;
        });

        TableColumn<UserManager, String> friendsColumn = new TableColumn<>("Friends");
        friendsColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            String friends = "";
            
            if (user != null) {
                Iterator<UserManager> iterator = user.getFriends().iterator();
                while (iterator.hasNext()) {
                    UserManager friend = iterator.next();
                    if (!friends.equals("")) {
                        friends += ", ";
                    }
                    friends += friend.getName();
                }
            }
            
            SimpleStringProperty property = new SimpleStringProperty("");
            if (friends.equals("")) {
                property = new SimpleStringProperty("No friends");
            } else {
                property = new SimpleStringProperty(friends);
            }
            return property;
        });

        tableView.getColumns().addAll(nameColumn, idColumn, friendsColumn);
        return tableView;
    }

    private void handleFriendshipFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Friendship Data File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                fileManager.loadFriendships(selectedFile.getAbsolutePath(), users);
                
                int renamedUsers = removeDuplicateUsers();
                
                updateFriendshipTable();
                updateUserComboBox();
                
                String successMessage = "Friendships have been successfully loaded.";
                if (renamedUsers > 0) {
                    successMessage += "\nAssigned new IDs to " + renamedUsers + " duplicate user(s).";
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("File Uploaded");
                alert.setContentText(successMessage);
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Upload Failed");
                alert.setContentText("Error reading the file: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    private int removeDuplicateUsers() {
        int duplicatesRenamed = 0;
        
        ArrayList<String> seenUserIds = new ArrayList<String>();
        ArrayList<UserManager> usersToRename = new ArrayList<UserManager>();
        
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                String userId = user.getUserID();
                
                if (seenUserIds.contains(userId)) {
                    usersToRename.add(user);
                    duplicatesRenamed++;
                } else {
                    seenUserIds.add(userId);
                }
            }
        }
        
        int highestId = 0;
        iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                try {
                    int id = Integer.parseInt(user.getUserID());
                    if (id > highestId) {
                        highestId = id;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        
        for (int i = 0; i < usersToRename.size(); i++) {
            UserManager userToRename = usersToRename.get(i);
            String newId = String.valueOf(highestId + i + 1);
            
            userToRename.setUserID(newId);
            
            seenUserIds.add(newId);
        }
        
        return duplicatesRenamed;
    }

    public void updateFriendshipTable() {
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                userList.add(user);
            }
        }
        
        friendshipTable.getItems().clear();
        
        friendshipTable.setItems(userList);
        friendshipTable.refresh();
    }

    public TableView<UserManager> getFriendshipTable() {
        return friendshipTable;
    }

    private void navigateToPreviousFriend() {
        int currentIndex = friendshipTable.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            friendshipTable.getSelectionModel().select(currentIndex - 1);
            friendshipTable.scrollTo(currentIndex - 1);
        } else if (friendshipTable.getItems().size() > 0) {
            friendshipTable.getSelectionModel().select(friendshipTable.getItems().size() - 1);
            friendshipTable.scrollTo(friendshipTable.getItems().size() - 1);
        }
    }
    
    private void navigateToNextFriend() {
        int currentIndex = friendshipTable.getSelectionModel().getSelectedIndex();
        if (currentIndex < friendshipTable.getItems().size() - 1) {
            friendshipTable.getSelectionModel().select(currentIndex + 1);
            friendshipTable.scrollTo(currentIndex + 1);
        } else if (friendshipTable.getItems().size() > 0) {
            friendshipTable.getSelectionModel().select(0);
            friendshipTable.scrollTo(0);
        }
    }

    private void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void showFriendshipsForUser(UserManager user) {
        if (user == null) {
            showNotification("No user selected");
            return;
        }

        CircularDoublyLinkedList<UserManager> friends = user.getFriends();
        if (friends.isEmpty()) {
            showNotification(user.getName() + " has no friends yet.");
            return;
        }

        String resultText = "=== FRIENDS OF " + user.getName().toUpperCase() + " ===\n\n";
        resultText += "Total friends: " + friends.size() + "\n\n";
        
        Iterator<UserManager> iterator = friends.iterator();
        int friendCount = 1;
        while (iterator.hasNext()) {
            UserManager friend = iterator.next();
            resultText += "FRIEND #" + friendCount + "\n";
            resultText += "ID: " + friend.getUserID() + "\n";
            resultText += "Name: " + friend.getName() + "\n";
            resultText += "Age: " + friend.getAge() + "\n";
            resultText += "-----------------------------------\n";
            friendCount++;
        }
        
        showNotification("Friends List", resultText);
    }

    public void showMutualFriends(UserManager user1, UserManager user2) {
        if (user1 == null || user2 == null) {
            showNotification("Please select two users first");
            return;
        }

        if (user1.equals(user2)) {
            showNotification("Please select two different users");
            return;
        }

        CircularDoublyLinkedList<UserManager> mutualFriends = new CircularDoublyLinkedList<>();
        Iterator<UserManager> user1Friends = user1.getFriends().iterator();
        
        while (user1Friends.hasNext()) {
            UserManager friend = user1Friends.next();
            if (user2.getFriends().contains(friend)) {
                mutualFriends.insertLast(friend);
            }
        }

        if (mutualFriends.isEmpty()) {
            showNotification("No mutual friends found between " + user1.getName() + " and " + user2.getName());
            return;
        }

        String resultText = "=== MUTUAL FRIENDS BETWEEN " + user1.getName().toUpperCase() + 
                          " AND " + user2.getName().toUpperCase() + " ===\n\n";
        resultText += "Total mutual friends: " + mutualFriends.size() + "\n\n";
        
        Iterator<UserManager> iterator = mutualFriends.iterator();
        int friendCount = 1;
        while (iterator.hasNext()) {
            UserManager friend = iterator.next();
            resultText += "FRIEND #" + friendCount + "\n";
            resultText += "ID: " + friend.getUserID() + "\n";
            resultText += "Name: " + friend.getName() + "\n";
            resultText += "Age: " + friend.getAge() + "\n";
            resultText += "-----------------------------------\n";
            friendCount++;
        }
        
        showNotification("Mutual Friends", resultText);
    }

    public void showSuggestedFriends(UserManager user) {
        if (user == null) {
            showNotification("No user selected");
            return;
        }

        if (user.getFriends().isEmpty()) {
            showNotification(user.getName() + " has no friends yet. Cannot suggest new connections.");
            return;
        }

        CircularDoublyLinkedList<UserManager> suggestedFriends = new CircularDoublyLinkedList<>();
        
        Iterator<UserManager> friendsIterator = user.getFriends().iterator();
        while (friendsIterator.hasNext()) {
            UserManager friend = friendsIterator.next();
            
            Iterator<UserManager> friendOfFriendIterator = friend.getFriends().iterator();
            while (friendOfFriendIterator.hasNext()) {
                UserManager potential = friendOfFriendIterator.next();
                
                if (!potential.equals(user) && !user.contains(potential) && !suggestedFriends.contains(potential)) {
                    suggestedFriends.insertLast(potential);
                }
            }
        }

        if (suggestedFriends.isEmpty()) {
            showNotification("No friend suggestions found for " + user.getName());
            return;
        }

        String resultText = "=== SUGGESTED FRIENDS FOR " + user.getName().toUpperCase() + " ===\n\n";
        resultText += "Total suggestions: " + suggestedFriends.size() + "\n\n";
        
        Iterator<UserManager> iterator = suggestedFriends.iterator();
        int friendCount = 1;
        while (iterator.hasNext()) {
            UserManager suggestion = iterator.next();
            resultText += "SUGGESTION #" + friendCount + "\n";
            resultText += "ID: " + suggestion.getUserID() + "\n";
            resultText += "Name: " + suggestion.getName() + "\n";
            resultText += "Age: " + suggestion.getAge() + "\n";
            resultText += "-----------------------------------\n";
            friendCount++;
        }
        
        showNotification("Friend Suggestions", resultText);
    }
} 
