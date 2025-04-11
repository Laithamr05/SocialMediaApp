import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Collectors;

public class PostDialogManager {
    private CircularDoublyLinkedList<PostManager> posts;
    private CircularDoublyLinkedList<UserManager> users;
    private PostTableManager tableManager;
    
    // Share options for combo box
    private final String SHARE_ALL_FRIENDS = "Share with all friends";
    private final String SHARE_SPECIFIC_FRIENDS = "Share with specific friends";
    
    public PostDialogManager(CircularDoublyLinkedList<PostManager> posts, 
                            CircularDoublyLinkedList<UserManager> users, 
                            PostTableManager tableManager) {
        this.posts = posts;
        this.users = users;
        this.tableManager = tableManager;
    }
    
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
            } else {
                // Keep current selections when switching to specific mode
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
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", 
                              "Please fill in all required fields and select a creator.");
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
            tableManager.refreshTable();
            
            showAlert(Alert.AlertType.INFORMATION, "Success", null, 
                     "Post created successfully!");
        });
    }
    
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
    
    public void showEditPostDialog() {
        PostManager selectedPost = tableManager.getPostTable().getSelectionModel().getSelectedItem();
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

        // Show creator info but don't allow changes
        TextField creatorField = new TextField(selectedPost.getCreator().getName());
        creatorField.setEditable(false);
        
        // Share options combo box
        ComboBox<String> shareOptionsComboBox = new ComboBox<>();
        shareOptionsComboBox.getItems().addAll(SHARE_ALL_FRIENDS, SHARE_SPECIFIC_FRIENDS);
        shareOptionsComboBox.setValue(SHARE_SPECIFIC_FRIENDS);
        
        // Share with users section
        Label shareLabel = new Label("Share with:");
        ListView<UserManager> shareListView = new ListView<>();
        shareListView.setPrefHeight(150);
        
        // Get only the creator's friends for the share list
        UserManager creator = selectedPost.getCreator();
        ObservableList<UserManager> friendsList = FXCollections.observableArrayList();
        
        if (creator != null && creator.getFriends() != null) {
            Iterator<UserManager> friendsIterator = creator.getFriends().iterator();
            while (friendsIterator.hasNext()) {
                UserManager friend = friendsIterator.next();
                if (friend != null) {
                    friendsList.add(friend);
                }
            }
        }
        
        shareListView.setItems(friendsList);
        
        // Pre-select users already shared with
        CircularDoublyLinkedList<UserManager> sharedUsers = selectedPost.getSharedUsers();
        
        // Determine if all friends are shared with
        boolean allFriendsShared = true;
        for (UserManager friend : friendsList) {
            if (!sharedUsers.contains(friend)) {
                allFriendsShared = false;
                break;
            }
        }
        
        // Set the appropriate share option
        if (allFriendsShared && !friendsList.isEmpty()) {
            shareOptionsComboBox.setValue(SHARE_ALL_FRIENDS);
        }
        
        // Share option behavior
        shareOptionsComboBox.setOnAction(e -> {
            String selectedOption = shareOptionsComboBox.getValue();
            if (selectedOption.equals(SHARE_ALL_FRIENDS)) {
                selectAllFriends(shareListView, true);
            }
            // Keep current selections when switching to specific mode
        });
        
        shareListView.setCellFactory(CheckBoxListCell.forListView(item -> {
            boolean isShared = sharedUsers != null && sharedUsers.contains(item);
            return new SimpleBooleanProperty(isShared);
        }));

        grid.add(new Label("Post ID:"), 0, 0);
        grid.add(postIDField, 1, 0);
        grid.add(new Label("Content:"), 0, 1);
        grid.add(contentArea, 1, 1);
        grid.add(new Label("Creator:"), 0, 2);
        grid.add(creatorField, 1, 2);
        grid.add(new Label("Share Option:"), 0, 3);
        grid.add(shareOptionsComboBox, 1, 3);
        grid.add(shareLabel, 0, 4);
        grid.add(shareListView, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String content = contentArea.getText();
                
                if (content.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", 
                              "Please enter content for the post.");
                    return null;
                }

                // Update post content
                selectedPost.setContent(content);
                
                // Update shared users
                selectedPost.clearSharedUsers();
                
                // Get selected friends to share with
                if (shareOptionsComboBox.getValue().equals(SHARE_ALL_FRIENDS)) {
                    // Share with all friends
                    for (int i = 0; i < shareListView.getItems().size(); i++) {
                        UserManager friend = shareListView.getItems().get(i);
                        if (friend != null) {
                            selectedPost.shareWith(friend);
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
                        if (user != null) {
                            selectedPost.shareWith(user);
                        }
                    }
                }
                
                return selectedPost;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(post -> {
            tableManager.refreshTable();
            showAlert(Alert.AlertType.INFORMATION, "Success", null, "Post updated successfully!");
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
        confirmAlert.setContentText("Are you sure you want to delete this post?\n\n" + 
                                   "Content: " + post.getContent().substring(0, 
                                   Math.min(50, post.getContent().length())) + 
                                   (post.getContent().length() > 50 ? "..." : ""));
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    private boolean isDuplicatePostID(String postID) {
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null && post.getPostID().equals(postID)) {
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
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 