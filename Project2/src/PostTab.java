import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TabPane;

public class PostTab {
	private TabPane tabPane;
	private TableView<Post> postTable;

	public PostTab(TabPane tabPane) {
		this.tabPane = tabPane;
	}

	public Tab createPostTab() {
		VBox postContent = new VBox(10);
		postContent.setPadding(new Insets(20));

		postTable = createPostTable();

		Button uploadPostButton = new Button("Upload Posts");
		uploadPostButton.setOnAction(e -> handlePostFileUpload());

		Button backButton = new Button("Back to Welcome");
		backButton.setOnAction(e -> tabPane.getSelectionModel().select(0));

		HBox buttonBar = new HBox(10);
		buttonBar.getChildren().addAll(uploadPostButton, backButton);

		postContent.getChildren().addAll(postTable, buttonBar);

		Tab postTab = new Tab("Posts", postContent);
		postTab.setClosable(false);
		return postTab;
	}

	private TableView<Post> createPostTable() {
		TableView<Post> table = new TableView<>();
		table.setPrefHeight(400);

		TableColumn<Post, String> idColumn = new TableColumn<>("Post ID");

		table.getColumns().add(idColumn);
		return table;
	}

	private void handlePostFileUpload() {
	}
}
