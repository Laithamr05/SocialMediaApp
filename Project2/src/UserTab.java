import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TabPane;

public class UserTab {
    private TabPane tabPane;
    private TableView<UserManager> userTable;

    public UserTab(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public Tab createUserTab() {
        VBox userContent = new VBox(10);
        userContent.setPadding(new Insets(20));

        userTable = createUserTable();

        Button addUserButton = new Button("Add User");
        addUserButton.setOnAction(e -> showAddUserDialog());

        Button editUserButton = new Button("Edit User");
        editUserButton.setOnAction(e -> showEditUserDialog());

        Button uploadUserButton = new Button("Upload Users");
        uploadUserButton.setOnAction(e -> handleUserFileUpload());

        Button backButton = new Button("Back to Welcome");
        backButton.setOnAction(e -> tabPane.getSelectionModel().select(0));

        HBox buttonBar = new HBox(10);
        buttonBar.getChildren().addAll(addUserButton, editUserButton, uploadUserButton, backButton);

        userContent.getChildren().addAll(userTable, buttonBar);

        Tab userTab = new Tab("Users", userContent);
        userTab.setClosable(false);
        return userTab;
    }

    private TableView<UserManager> createUserTable() {
        TableView<UserManager> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");

        table.getColumns().add(idColumn);
        return table;
    }

    private void showAddUserDialog() {
    }

    private void showEditUserDialog() {
    }

    private void handleUserFileUpload() {
    }
} 
