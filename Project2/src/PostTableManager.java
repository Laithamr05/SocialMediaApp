import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import java.util.Iterator;
import java.util.Calendar;
import javafx.collections.ListChangeListener;

public class PostTableManager {
    private TableView<PostManager> postTable;
    private CircularDoublyLinkedList<PostManager> posts;
    private CircularDoublyLinkedList<UserManager> users;
    
    public PostTableManager(CircularDoublyLinkedList<PostManager> posts, CircularDoublyLinkedList<UserManager> users) {
        this.posts = posts;
        this.users = users;
        this.postTable = createPostTable();
    }
    
    public TableView<PostManager> getPostTable() {
        return postTable;
    }
    
    public TableView<PostManager> createPostTable() {
        TableView<PostManager> table = new TableView<>();
        table.setPrefHeight(400);
        
        TableColumn<PostManager, String> postIDColumn = new TableColumn<>("Post ID");
        postIDColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            SimpleStringProperty result;
            if (post != null) {
                result = new SimpleStringProperty(post.getPostID());
            } else {
                result = new SimpleStringProperty("");
            }
            return result;
        });
        postIDColumn.setPrefWidth(80);
        
        TableColumn<PostManager, String> creatorColumn = new TableColumn<>("Creator");
        creatorColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            if (post != null && post.getCreator() != null) {
                return new SimpleStringProperty(post.getCreator().getName());
            }
            return new SimpleStringProperty("");
        });
        creatorColumn.setPrefWidth(120);
        
        TableColumn<PostManager, String> contentColumn = new TableColumn<>("Content");
        contentColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            SimpleStringProperty result;
            if (post != null) {
                result = new SimpleStringProperty(post.getContent());
            } else {
                result = new SimpleStringProperty("");
            }
            return result;
        });
        contentColumn.setPrefWidth(300);
        
        TableColumn<PostManager, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            if (post != null && post.getCreationDate() != null) {
                return new SimpleStringProperty(formatDate(post.getCreationDate()));
            }
            return new SimpleStringProperty("");
        });
        dateColumn.setPrefWidth(100);
        
        TableColumn<PostManager, String> sharedWithColumn = new TableColumn<>("Shared With");
        sharedWithColumn.setCellValueFactory(cellData -> {
            PostManager post = cellData.getValue();
            if (post == null) {
                return new SimpleStringProperty("");
            }
            
            CircularDoublyLinkedList<UserManager> sharedUsers = post.getSharedUsers();
            if (sharedUsers == null || sharedUsers.isEmpty()) {
                return new SimpleStringProperty("Not shared");
            }
            
            StringBuilder sb = new StringBuilder();
            Iterator<UserManager> iterator = sharedUsers.iterator();
            while (iterator.hasNext()) {
                UserManager user = iterator.next();
                if (user != null) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(user.getName());
                }
            }
            
            return new SimpleStringProperty(sb.toString());
        });
        sharedWithColumn.setPrefWidth(150);
        
        table.getColumns().addAll(postIDColumn, creatorColumn, contentColumn, dateColumn, sharedWithColumn);
        
        // Add a listener to refresh the table when items change
        table.getItems().addListener(new ListChangeListener<PostManager>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends PostManager> change) {
                while (change.next()) {
                    if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
                        table.refresh();
                    }
                }
            }
        });
        
        refreshTable(table);
        
        return table;
    }
    
    public void refreshTable(TableView<PostManager> table) {
        ObservableList<PostManager> postList = FXCollections.observableArrayList();
        
        // Create a list of posts in the exact order they appear in the linked list
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null) {
                postList.add(post);
            }
        }
        
        // Clear and set the items
        table.getItems().clear();
        table.setItems(postList);
        table.refresh();
    }
    
    public void refreshTable() {
        refreshTable(postTable);
    }
    
    public PostManager findPostById(String postId) {
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null && post.getPostID().equals(postId)) {
                return post;
            }
        }
        return null;
    }
    
    public PostManager findPostByContent(String content) {
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null && post.getContent().contains(content)) {
                return post;
            }
        }
        return null;
    }
    
    public String formatDate(Calendar date) {
        if (date == null) return "";
        
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;
        int year = date.get(Calendar.YEAR);
        
        return String.format("%02d.%02d.%d", day, month, year);
    }
} 
