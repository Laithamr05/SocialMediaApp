import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import java.util.Calendar;
import java.util.Iterator;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.BooleanProperty;
import javafx.util.StringConverter;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;

public class PostManagerUI {
    private TableView<PostManager> postTable;
    private CircularDoublyLinkedList<PostManager> posts;
    private FileManager fileManager;
    private CircularDoublyLinkedList<UserManager> users;
    private WelcomePage welcomePage;
    private TableManager tableManager;
    private DialogManager dialogManager;
    private ComboBox<String> sortOrderComboBox;

    public PostManagerUI(CircularDoublyLinkedList<PostManager> posts, FileManager fileManager, 
                        CircularDoublyLinkedList<UserManager> users, WelcomePage welcomePage) {
        this.posts = posts;
        this.fileManager = fileManager;
        this.users = users;
        this.welcomePage = welcomePage;
        this.tableManager = new TableManager(users, posts);
        this.postTable = tableManager.createPostTable();
        this.dialogManager = new DialogManager(users, posts, tableManager);
    }

    public Tab createPostTab() {
        VBox postContent = new VBox(20);
        postContent.setPadding(new Insets(20));
        postContent.setAlignment(Pos.CENTER);

        HBox tableNavBox = createNavigationButtons();
        HBox actionButtonsBox = createActionButtons();

        postContent.getChildren().addAll(postTable, tableNavBox, actionButtonsBox);

        Tab postTab = new Tab("Posts", postContent);
        postTab.setClosable(false);
        return postTab;
    }

    private TableView<PostManager> createPostTable() {
        TableView<PostManager> table = new TableView<>();
        table.setPrefHeight(400);

        table.setPlaceholder(new Label("No posts to display"));

        TableColumn<PostManager, String> idColumn = new TableColumn<>("Post ID");
        idColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            SimpleStringProperty property = new SimpleStringProperty("");
            if (post != null) {
                property = new SimpleStringProperty(post.getPostID());
            }
            return property;
        });
        idColumn.setPrefWidth(100);
        
        idColumn.setComparator((id1, id2) -> {
            try {
                return Integer.compare(Integer.parseInt(id1), Integer.parseInt(id2));
            } catch (NumberFormatException e) {
                return id1.compareTo(id2);
            }
        });

        TableColumn<PostManager, String> creatorColumn = new TableColumn<>("Creator");
        creatorColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            if (post == null) {
                return new SimpleStringProperty("");
            }
            UserManager creator = post.getCreator();
            if (creator == null) {
                return new SimpleStringProperty("Unknown");
            }
            return new SimpleStringProperty(creator.getName());
        });
        creatorColumn.setPrefWidth(150);

        TableColumn<PostManager, String> contentColumn = createContentColumn();
        contentColumn.setPrefWidth(300);

        TableColumn<PostManager, String> dateColumn = createDateColumn();
        dateColumn.setPrefWidth(150);

        TableColumn<PostManager, String> sharedWithColumn = createSharedUsersColumn();
        sharedWithColumn.setPrefWidth(200);

        table.getColumns().addAll(idColumn, creatorColumn, contentColumn, dateColumn, sharedWithColumn);
        return table;
    }

    private TableColumn<PostManager, String> createContentColumn() {
        TableColumn<PostManager, String> contentColumn = new TableColumn<>("Content");
        contentColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            SimpleStringProperty property;
            if (post != null) {
                property = new SimpleStringProperty(post.getContent());
            } else {
                property = new SimpleStringProperty("");
            }
            return property;
        });
        contentColumn.setPrefWidth(300);
        return contentColumn;
    }

    private TableColumn<PostManager, String> createDateColumn() {
        TableColumn<PostManager, String> dateColumn = new TableColumn<>("Creation Date");
        dateColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            String dateString;
            if (post == null) {
                dateString = "";
            } else {
                Calendar date = post.getCreationDate();
                if (date != null) {
                    dateString = formatDate(date);
                } else {
                    dateString = "";
                }
            }
            return new SimpleStringProperty(dateString);
        });
        dateColumn.setPrefWidth(150);
        return dateColumn;
    }

    private TableColumn<PostManager, String> createSharedUsersColumn() {
        TableColumn<PostManager, String> sharedUsersColumn = new TableColumn<>("Shared With");
        sharedUsersColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue() == null) {
                return new SimpleStringProperty("");
            }
            
            PostManager post = cellData.getValue();
            String sharedWithUsers = "";
            
            CircularDoublyLinkedList<UserManager> sharedWith = post.getSharedUsers();
            
            if (sharedWith != null) {
                Iterator<UserManager> iterator = sharedWith.iterator();
                while (iterator.hasNext()) {
                    UserManager user = iterator.next();
                    if (user != null) {
                        if (!sharedWithUsers.equals("")) {
                            sharedWithUsers += ", ";
                        }
                        sharedWithUsers += user.getName();
                    }
                }
            }
            
            SimpleStringProperty property = new SimpleStringProperty("");
            if (sharedWithUsers.equals("")) {
                property = new SimpleStringProperty("None");
            } else {
                property = new SimpleStringProperty(sharedWithUsers);
            }
            return property;
        });
        
        return sharedUsersColumn;
    }

    private HBox createNavigationButtons() {
        HBox tableNavBox = new HBox(10);
        tableNavBox.setAlignment(Pos.CENTER);
        
        Button prevButton = new Button("◀ Previous");
        prevButton.setPrefWidth(100);
        prevButton.setOnAction(e -> navigateToPreviousPost());
        
        Button nextButton = new Button("Next ▶");
        nextButton.setPrefWidth(100);
        nextButton.setOnAction(e -> navigateToNextPost());
        
        tableNavBox.getChildren().addAll(prevButton, nextButton);
        return tableNavBox;
    }

    private HBox createActionButtons() {
        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.setAlignment(Pos.CENTER);
        
        Button addPostButton = new Button("Add Post");
        addPostButton.setOnAction(e -> dialogManager.showCreatePostDialog());
        
        Button editPostButton = new Button("Edit Post");
        editPostButton.setOnAction(e -> dialogManager.showEditPostDialog());
        
        Button deletePostButton = new Button("Delete Post");
        deletePostButton.setOnAction(e -> deleteSelectedPost());
        
        Button uploadPostsButton = new Button("Upload Posts");
        uploadPostsButton.setOnAction(e -> {
            handlePostFileUpload();
            refreshPostTable();
        });
        
        actionButtonsBox.getChildren().addAll(addPostButton, editPostButton, deletePostButton, uploadPostsButton);
        return actionButtonsBox;
    }

    private void navigateToPreviousPost() {
        TableView<PostManager> table = tableManager.getPostTable();
        int currentIndex = table.getSelectionModel().getSelectedIndex();
        
        if (currentIndex > 0) {
            table.getSelectionModel().select(currentIndex - 1);
            table.scrollTo(currentIndex - 1);
        } else if (currentIndex == -1 && !table.getItems().isEmpty()) {
            table.getSelectionModel().select(0);
            table.scrollTo(0);
        }
    }

    private void navigateToNextPost() {
        TableView<PostManager> table = tableManager.getPostTable();
        int currentIndex = table.getSelectionModel().getSelectedIndex();
        int lastIndex = table.getItems().size() - 1;
        
        if (currentIndex < lastIndex) {
            table.getSelectionModel().select(currentIndex + 1);
            table.scrollTo(currentIndex + 1);
        } else if (currentIndex == -1 && !table.getItems().isEmpty()) {
            table.getSelectionModel().select(0);
            table.scrollTo(0);
        }
    }

    private void deleteSelectedPost() {
        PostManager selectedPost = tableManager.getPostTable().getSelectionModel().getSelectedItem();
        
        if (dialogManager.confirmDeletePost(selectedPost)) {
            posts.delete(selectedPost);
            refreshPostTable();
            showAlert(Alert.AlertType.INFORMATION, "Success", null, 
                      "Post '" + selectedPost.getPostID() + "' deleted successfully.");
        }
    }

    private void handlePostFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Post File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            CircularDoublyLinkedList<PostManager> loadedPosts = fileManager.loadPosts(file.getAbsolutePath(), users);
            if (loadedPosts != null) {
                posts = loadedPosts;
                tableManager.refreshPostTable(posts);
            }
        }
    }

    public void refreshPostTable() {
        tableManager.refreshPostTable(posts);
    }

    public TableView<PostManager> getPostTable() {
        return tableManager.getPostTable();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void updateUIAfterPostsChanged() {
        postTable.refresh();
    }

    private void showAddPostDialog() {
        Dialog<PostManager> dialog = new Dialog<>();
        dialog.setTitle("Add New Post");
        dialog.setHeaderText("Enter post details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField contentField = new TextField();
        contentField.setPromptText("Post Content");
        
        ComboBox<UserManager> creatorComboBox = new ComboBox<>();
        creatorComboBox.setPromptText("Select Creator");
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                userList.add(user);
            }
        }
        creatorComboBox.setItems(userList);

        grid.add(new Label("Content:"), 0, 0);
        grid.add(contentField, 1, 0);
        grid.add(new Label("Creator:"), 0, 1);
        grid.add(creatorComboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String content = contentField.getText();
                UserManager creator = creatorComboBox.getValue();
                
                if (content.isEmpty() || creator == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", "Please fill in all fields");
                    return null;
                }

                String postID = generatePostID();
                Calendar creationDate = Calendar.getInstance();
                return new PostManager(postID, creator, content, creationDate);
            }
            return null;
        });

        Optional<PostManager> result = dialog.showAndWait();
        result.ifPresent(post -> {
            posts.insertLast(post);
            tableManager.refreshPostTable(posts);
        });
    }
    
    private String formatDate(Calendar date) {
        if (date == null) {
            return "";
        }
        
        String formattedDate = (date.get(Calendar.MONTH) + 1) + "/" + 
                               date.get(Calendar.DAY_OF_MONTH) + "/" + 
                               date.get(Calendar.YEAR) + " " + 
                               date.get(Calendar.HOUR_OF_DAY) + ":" + 
                               date.get(Calendar.MINUTE) + ":" + 
                               date.get(Calendar.SECOND);
        
        return formattedDate;
    }

    private String generatePostID() {
        int maxID = 0;
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null) {
                try {
                    int id = Integer.parseInt(post.getPostID());
                    maxID = Math.max(maxID, id);
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        return String.valueOf(maxID + 1);
    }

    private boolean checkForFriendships() {
        if (users == null || users.isEmpty()) {
            return false;
        }
        
        Iterator<UserManager> userIterator = users.iterator();
        while (userIterator.hasNext()) {
            UserManager user = userIterator.next();
            if (user != null && user.getFriends() != null && !user.getFriends().isEmpty()) {
                return true;
            }
        }
        
        return false;
    }

    private void showSharePostDialog() {
        PostManager selectedPost = tableManager.getSelectedPost();
        if (selectedPost == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No Post Selected", "Please select a post to share");
            return;
        }

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Share Post");
        dialog.setHeaderText("Select users to share with");

        ButtonType shareButtonType = new ButtonType("Share", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(shareButtonType, ButtonType.CANCEL);

        ListView<UserManager> userListView = new ListView<>();
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null && !user.equals(selectedPost.getCreator())) {
                userList.add(user);
            }
        }
        userListView.setItems(userList);
        userListView.setCellFactory(CheckBoxListCell.forListView(user -> {
            BooleanProperty selected = new SimpleBooleanProperty(selectedPost.getSharedUsers().contains(user));
            selected.addListener((obs, wasSelected, isNowSelected) -> {
                if (isNowSelected) {
                    selectedPost.shareWith(user);
                } else {
                    selectedPost.getSharedUsers().delete(user);
                }
            });
            return selected;
        }));

        dialog.getDialogPane().setContent(userListView);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == shareButtonType) {
                return true;
            }
            return false;
        });

        dialog.showAndWait();
        tableManager.refreshPostTable(posts);
    }
} 
