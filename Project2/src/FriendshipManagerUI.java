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

public class FriendshipManagerUI {
    private TableView<UserManager> friendshipTable;
    private CircularDoublyLinkedList<UserManager> users;
    private FileManager fileManager;

    public FriendshipManagerUI(CircularDoublyLinkedList<UserManager> users, FileManager fileManager) {
        this.users = users;
        this.fileManager = fileManager;
        this.friendshipTable = createFriendshipTable();
    }

    public Tab createFriendshipTab() {
        VBox friendshipContent = new VBox(10);
        friendshipContent.setPadding(new Insets(20));

        friendshipTable.setPrefHeight(400);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);

        Button addFriendButton = new Button("Add Friend");
        addFriendButton.setOnAction(e -> showAddFriendDialog());

        Button removeFriendButton = new Button("Remove Friend");
        removeFriendButton.setOnAction(e -> showRemoveFriendDialog());

        Button uploadFriendshipsButton = new Button("Upload Friendships");
        uploadFriendshipsButton.setOnAction(e -> handleFriendshipFileUpload());

        buttonBar.getChildren().addAll(addFriendButton, removeFriendButton, uploadFriendshipsButton);

        friendshipContent.getChildren().addAll(friendshipTable, buttonBar);

        Tab friendshipTab = new Tab("Friendships", friendshipContent);
        friendshipTab.setClosable(false);
        return friendshipTab;
    }

    private void showAddFriendDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Friend");
        dialog.setHeaderText("Enter User IDs to Create Friendship");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField user1IdField = new TextField();
        TextField user2IdField = new TextField();
        user1IdField.setPromptText("First User ID");
        user2IdField.setPromptText("Second User ID");

        grid.add(new Label("First User ID:"), 0, 0);
        grid.add(user1IdField, 1, 0);
        grid.add(new Label("Second User ID:"), 0, 1);
        grid.add(user2IdField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Pair<>(user1IdField.getText(), user2IdField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ids -> {
            if (!ids.getKey().isEmpty() && !ids.getValue().isEmpty()) {
                UserManager user1 = findUserById(ids.getKey());
                UserManager user2 = findUserById(ids.getValue());

                if (user1 == null || user2 == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid User ID", 
                            "One or both user IDs do not exist.");
                    return;
                }

                if (user1.contains(user2)) {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Already Friends", 
                            "These users are already friends.");
                    return;
                }

                user1.addFriend(user2);
                user2.addFriend(user1);
                updateFriendshipTable();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Friendship Added", 
                        "Friendship has been created successfully.");
            }
        });
    }

    private void showRemoveFriendDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Remove Friend");
        dialog.setHeaderText("Enter User IDs to Remove Friendship");

        ButtonType removeButtonType = new ButtonType("Remove", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(removeButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField user1IdField = new TextField();
        TextField user2IdField = new TextField();
        user1IdField.setPromptText("First User ID");
        user2IdField.setPromptText("Second User ID");

        grid.add(new Label("First User ID:"), 0, 0);
        grid.add(user1IdField, 1, 0);
        grid.add(new Label("Second User ID:"), 0, 1);
        grid.add(user2IdField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == removeButtonType) {
                return new Pair<>(user1IdField.getText(), user2IdField.getText());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(ids -> {
            if (!ids.getKey().isEmpty() && !ids.getValue().isEmpty()) {
                UserManager user1 = findUserById(ids.getKey());
                UserManager user2 = findUserById(ids.getValue());

                if (user1 == null || user2 == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid User ID", 
                            "One or both user IDs do not exist.");
                    return;
                }

                if (!user1.contains(user2)) {
                    showAlert(Alert.AlertType.WARNING, "Warning", "Not Friends", 
                            "These users are not friends.");
                    return;
                }

                user1.removeFriend(user2);
                user2.removeFriend(user1);
                updateFriendshipTable();
                showAlert(Alert.AlertType.INFORMATION, "Success", "Friendship Removed", 
                        "Friendship has been removed successfully.");
            }
        });
    }

    private UserManager findUserById(String id) {
        Node<UserManager> current = users.dummy.next;
        while (current != users.dummy) {
            if (current.data.getUserID().equals(id)) {
                return current.data;
            }
            current = current.next;
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
        TableView<UserManager> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserID()));
        idColumn.setPrefWidth(100);

        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(200);

        TableColumn<UserManager, String> friendsColumn = new TableColumn<>("Friends");
        friendsColumn.setCellValueFactory(cellData -> {
            StringBuilder friends = new StringBuilder();
            CircularDoublyLinkedList<UserManager> friendsList = cellData.getValue().getFriends();
            Node<UserManager> current = friendsList.dummy.next;
            
            System.out.println("Friend list for " + cellData.getValue().getName() + ":");
            
            while (current != friendsList.dummy) {
                if (friends.length() > 0) {
                    friends.append(", ");
                }
                friends.append(current.data.getName() + " (ID: " + current.data.getUserID() + ")");
                System.out.println("- " + current.data.getName() + " (ID: " + current.data.getUserID() + ")");
                current = current.next;
            }
            
            if (friends.length() == 0) {
                return new SimpleStringProperty("No friends");
            }
            return new SimpleStringProperty(friends.toString());
        });
        friendsColumn.setPrefWidth(300);

        table.getColumns().addAll(idColumn, nameColumn, friendsColumn);
        return table;
    }

    private void handleFriendshipFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Friendship Data File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                fileManager.readFriendships(selectedFile.getAbsolutePath(), users);
                updateFriendshipTable();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("File Uploaded");
                alert.setContentText("Friendships have been successfully uploaded from the file.");
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

    public void updateFriendshipTable() {
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Node<UserManager> current = users.dummy.next;
        while (current != users.dummy) {
            userList.add(current.data);
            current = current.next;
        }
        friendshipTable.setItems(userList);
        friendshipTable.refresh();
        System.out.println("Updated friendship table with " + userList.size() + " users");
    }

    public TableView<UserManager> getFriendshipTable() {
        return friendshipTable;
    }
} 
