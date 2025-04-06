import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;

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

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);

        Button createPostButton = new Button("Create Post");
        createPostButton.setOnAction(e -> showCreatePostDialog());

        Button uploadPostButton = new Button("Upload Posts");
        uploadPostButton.setOnAction(e -> handlePostFileUpload());

        buttonBar.getChildren().addAll(createPostButton, uploadPostButton);

        postContent.getChildren().addAll(postTable, buttonBar);

        Tab postTab = new Tab("Posts", postContent);
        postTab.setClosable(false);
        return postTab;
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
            return new SimpleStringProperty(post != null && post.getCreator() != null ? 
                post.getCreator().getName() : "");
        });
        creatorColumn.setPrefWidth(150);

        TableColumn<PostManager, String> contentColumn = new TableColumn<>("Content");
        contentColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));
        contentColumn.setPrefWidth(300);

        TableColumn<PostManager, String> dateColumn = new TableColumn<>("Creation Date");
        dateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getCreationDate().toString()));
        dateColumn.setPrefWidth(150);

        TableColumn<PostManager, String> sharedWithColumn = new TableColumn<>("Shared With");
        sharedWithColumn.setCellValueFactory(cellData -> {
            StringBuilder sharedWith = new StringBuilder();
            CircularDoublyLinkedList<UserManager> sharedUsers = cellData.getValue().getSharedUsers();
            Node<UserManager> current = sharedUsers.dummy.next;
            while (current != sharedUsers.dummy) {
                if (sharedWith.length() > 0) {
                    sharedWith.append(", ");
                }
                sharedWith.append(current.data.getName());
                current = current.next;
            }
            return new SimpleStringProperty(sharedWith.toString());
        });
        sharedWithColumn.setPrefWidth(200);

        table.getColumns().addAll(postIDColumn, creatorColumn, contentColumn, dateColumn, sharedWithColumn);
        return table;
    }

    private void handlePostFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Post Data File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                fileManager.readPosts(selectedFile.getAbsolutePath(), posts, users);
                updatePostTable();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("File Uploaded");
                alert.setContentText("Posts have been successfully uploaded from the file.");
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

    public void updatePostTable() {
        ObservableList<PostManager> postList = FXCollections.observableArrayList();
        Node<PostManager> current = posts.dummy.next;
        while (current != posts.dummy) {
            postList.add(current.data);
            current = current.next;
        }
        postTable.setItems(postList);
    }

    private void showCreatePostDialog() {
        Dialog<PostManager> dialog = new Dialog<>();
        dialog.setTitle("Create New Post");
        dialog.setHeaderText("Enter Post Details");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField contentField = new TextField();
        contentField.setPromptText("Content");

        grid.add(new Label("Content:"), 0, 0);
        grid.add(contentField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                if (contentField.getText().isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please enter content for the post.");
                    alert.showAndWait();
                    return null;
                }

                String postID = String.valueOf(posts.size() + 1);
                UserManager creator = users.dummy.next.data;
                PostManager newPost = new PostManager(postID, creator, contentField.getText(), LocalDate.now());
                posts.insertLast(newPost);
                updatePostTable();
                return newPost;
            }
            return null;
        });

        dialog.showAndWait();
    }

    public TableView<PostManager> getPostTable() {
        return postTable;
    }
} 
