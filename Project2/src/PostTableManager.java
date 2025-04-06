import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.collections.ListChangeListener;

public class PostTableManager {
    public TableView<Post> createPostTable() {
        TableView<Post> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<Post, String> postIDColumn = new TableColumn<>("Post ID");
        postIDColumn.setCellValueFactory(cellData -> cellData.getValue().postIDProperty());
        postIDColumn.setPrefWidth(100);

        TableColumn<Post, String> creatorColumn = new TableColumn<>("Creator");
        creatorColumn.setCellValueFactory(cellData -> {
            Post post = cellData.getValue();
            return new SimpleStringProperty(post != null && post.getCreator() != null ? post.getCreator().getUserID() : "");
        });
        creatorColumn.setPrefWidth(100);

        TableColumn<Post, String> contentColumn = new TableColumn<>("Content");
        contentColumn.setCellValueFactory(cellData -> cellData.getValue().contentProperty());
        contentColumn.setPrefWidth(300);

        TableColumn<Post, String> dateColumn = new TableColumn<>("Creation Date");
        dateColumn.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
        dateColumn.setPrefWidth(150);

        table.getColumns().addAll(postIDColumn, creatorColumn, contentColumn, dateColumn);

        table.getItems().addListener((ListChangeListener<Post>) change -> {
            while (change.next()) {
                if (change.wasAdded() || change.wasRemoved() || change.wasUpdated()) {
                    table.refresh();
                }
            }
        });

        return table;
    }
} 
