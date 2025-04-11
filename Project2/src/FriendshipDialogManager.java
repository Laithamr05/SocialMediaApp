// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import java.util.Iterator;

public class FriendshipDialogManager {
    private CircularDoublyLinkedList<UserManager> users;
    private FriendshipTableManager tableManager;
    
    public FriendshipDialogManager(CircularDoublyLinkedList<UserManager> users, FriendshipTableManager tableManager) {
        this.users = users;
        this.tableManager = tableManager;
    }
    
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
                UserManager user1 = tableManager.findUserById(ids.getKey());
                UserManager user2 = tableManager.findUserById(ids.getValue());

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

                tableManager.refreshTable();

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

                tableManager.refreshTable();

                showAlert(Alert.AlertType.INFORMATION, "Success", "Friendship Removed",
                        user1.getName() + " and " + user2.getName() + " are no longer friends.");
            }
        });
    }
    
    public void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 