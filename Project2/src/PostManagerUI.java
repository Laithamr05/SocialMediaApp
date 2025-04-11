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

public class PostManagerUI {
    private TableView<PostManager> postTable;
    private CircularDoublyLinkedList<PostManager> posts;
    private FileManager fileManager;
    private CircularDoublyLinkedList<UserManager> users;
    private WelcomePage welcomePage;

    public PostManagerUI(CircularDoublyLinkedList<PostManager> posts, FileManager fileManager, 
                        CircularDoublyLinkedList<UserManager> users, WelcomePage welcomePage) {
        this.posts = posts;
        this.fileManager = fileManager;
        this.users = users;
        this.welcomePage = welcomePage;
        this.postTable = createPostTable();
    }

    public Tab createPostTab() {
        VBox postContent = new VBox(10);
        postContent.setPadding(new Insets(20));

        postTable.setPrefHeight(400);

        HBox navButtonBar = new HBox(10);
        navButtonBar.setAlignment(Pos.CENTER);
        
        Button prevButton = new Button("◀ Previous");
        prevButton.setOnAction(e -> navigateToPreviousPost());
        
        Button nextButton = new Button("Next ▶");
        nextButton.setOnAction(e -> navigateToNextPost());
        
        navButtonBar.getChildren().addAll(prevButton, nextButton);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);

        Button createPostButton = new Button("Create Post");
        createPostButton.setOnAction(e -> showAddPostDialog());

        Button uploadPostButton = new Button("Upload Posts");
        uploadPostButton.setOnAction(e -> handlePostFileUpload());

        buttonBar.getChildren().addAll(createPostButton, uploadPostButton);

        postContent.getChildren().addAll(postTable, navButtonBar, buttonBar);

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

        TableColumn<PostManager, String> contentColumn = new TableColumn<>("Content");
        contentColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            return post != null ? new SimpleStringProperty(post.getContent()) : new SimpleStringProperty("");
        });
        contentColumn.setPrefWidth(300);

        TableColumn<PostManager, String> dateColumn = new TableColumn<>("Creation Date");
        dateColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            if (post == null) {
                return new SimpleStringProperty("");
            }
            Calendar date = post.getCreationDate();
            return new SimpleStringProperty(date != null ? formatDate(date) : "");
        });
        dateColumn.setPrefWidth(150);

        TableColumn<PostManager, String> sharedWithColumn = createSharedUsersColumn();
        sharedWithColumn.setPrefWidth(200);

        table.getColumns().addAll(idColumn, creatorColumn, contentColumn, dateColumn, sharedWithColumn);
        return table;
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

    private void handlePostFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Post Data File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                fileManager.loadPosts(selectedFile.getAbsolutePath(), posts, users);
                
                int renamedPosts = removeDuplicatePosts();
                
                updatePostTable();
                
                String successMessage = "Posts have been successfully uploaded from the file.";
                if (renamedPosts > 0) {
                    successMessage += "\nAssigned new IDs to " + renamedPosts + " duplicate post(s).";
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

    private int removeDuplicatePosts() {
        int duplicatesRenamed = 0;
        
        ArrayList<String> seenPostIds = new ArrayList<String>();
        ArrayList<PostManager> postsToRename = new ArrayList<PostManager>();
        
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null) {
                String postId = post.getPostID();
                
                if (seenPostIds.contains(postId)) {
                    postsToRename.add(post);
                    duplicatesRenamed++;
                } else {
                    seenPostIds.add(postId);
                }
            }
        }
        
        int highestId = 0;
        iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null) {
                try {
                    int id = Integer.parseInt(post.getPostID());
                    if (id > highestId) {
                        highestId = id;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        
        for (int i = 0; i < postsToRename.size(); i++) {
            PostManager postToRename = postsToRename.get(i);
            String newId = String.valueOf(highestId + i + 1);
            
            postToRename.setPostID(newId);
            
            seenPostIds.add(newId);
        }
        
        return duplicatesRenamed;
    }

    public void updatePostTable() {
        ObservableList<PostManager> postList = FXCollections.observableArrayList();
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null) {
                postList.add(post);
            }
        }
        
        postTable.getItems().clear();
        postTable.setItems(postList);
        postTable.refresh();
    }

    private void updateUIAfterPostsChanged() {
        postTable.refresh();
    }

    private void showAddPostDialog() {
        Dialog<PostManager> dialog = new Dialog<>();
        dialog.setTitle("Add New Post");
        dialog.setHeaderText("Create New Post");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

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

        Label friendsLabel = new Label("Share with friends:");
        
        java.util.LinkedHashMap<UserManager, BooleanProperty> friendSelectionMap = new java.util.LinkedHashMap<UserManager, BooleanProperty>();
        
        ListView<UserManager> friendsListView = new ListView<>();
        friendsListView.setPrefHeight(150);
        
        friendsListView.setCellFactory(lv -> {
            CheckBoxListCell<UserManager> cell = new CheckBoxListCell<>(item -> {
                if (!friendSelectionMap.containsKey(item)) {
                    BooleanProperty property = new SimpleBooleanProperty(true);
                    friendSelectionMap.put(item, property);
                }
                return friendSelectionMap.get(item);
            });
            
            cell.setConverter(new StringConverter<UserManager>() {
                @Override
                public String toString(UserManager user) {
                    String name = "";
                    if (user != null) {
                        name = user.getName();
                    }
                    return name;
                }

                @Override
                public UserManager fromString(String string) {
                    return null;
                }
            });
            
            return cell;
        });

        creatorComboBox.setOnAction(e -> {
            UserManager selectedCreator = creatorComboBox.getValue();
            if (selectedCreator != null) {
                ObservableList<UserManager> friendsList = FXCollections.observableArrayList();
                friendSelectionMap.clear();
                
                Iterator<UserManager> friendsIterator = selectedCreator.getFriends().iterator();
                while (friendsIterator.hasNext()) {
                    UserManager friend = friendsIterator.next();
                    friendsList.add(friend);
                    friendSelectionMap.put(friend, new SimpleBooleanProperty(true));
                }
                friendsListView.setItems(friendsList);
            }
        });

        grid.add(new Label("Content:"), 0, 0);
        grid.add(contentField, 1, 0);
        grid.add(new Label("Creator:"), 0, 1);
        grid.add(creatorComboBox, 1, 1);
        grid.add(friendsLabel, 0, 2);
        grid.add(friendsListView, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                UserManager creator = creatorComboBox.getValue();
                if (contentField.getText().isEmpty() || creator == null) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please fill in all fields and select a creator.");
                    alert.showAndWait();
                    return null;
                }

                String postID = generatePostID();
                Calendar currentDate = Calendar.getInstance();
                PostManager newPost = new PostManager(postID, creator, contentField.getText(), currentDate);

                for (Map.Entry<UserManager, BooleanProperty> entry : friendSelectionMap.entrySet()) {
                    if (entry.getValue().get()) {
                        newPost.addSharedUser(entry.getKey());
                    }
                }

                posts.insertLast(newPost);
                updatePostTable();
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Post Created");
                alert.setContentText("New post has been created successfully.");
                alert.showAndWait();
                
                return newPost;
            }
            return null;
        });

        dialog.showAndWait();
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

    public TableView<PostManager> getPostTable() {
        return postTable;
    }

    private void navigateToPreviousPost() {
        int currentIndex = postTable.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            postTable.getSelectionModel().select(currentIndex - 1);
            postTable.scrollTo(currentIndex - 1);
        } else if (postTable.getItems().size() > 0) {
            postTable.getSelectionModel().select(postTable.getItems().size() - 1);
            postTable.scrollTo(postTable.getItems().size() - 1);
        }
    }
    
    private void navigateToNextPost() {
        int currentIndex = postTable.getSelectionModel().getSelectedIndex();
        if (currentIndex < postTable.getItems().size() - 1) {
            postTable.getSelectionModel().select(currentIndex + 1);
            postTable.scrollTo(currentIndex + 1);
        } else if (postTable.getItems().size() > 0) {
            postTable.getSelectionModel().select(0);
            postTable.scrollTo(0);
        }
    }

    private String generatePostID() {
        return String.valueOf(posts.size() + 1);
    }
} 
