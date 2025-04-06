import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TabPane;

public class MenuBarManager {
    private TabPane tabPane;

    public MenuBarManager(TabPane tabPane) {
        this.tabPane = tabPane;
    }

    public MenuBar createMenuBar() {
        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().add(exitItem);

        Menu viewMenu = new Menu("View");
        MenuItem welcomeItem = new MenuItem("Welcome Page");
        welcomeItem.setOnAction(e -> tabPane.getSelectionModel().select(0));

        MenuItem usersItem = new MenuItem("Users");
        usersItem.setOnAction(e -> tabPane.getSelectionModel().select(1));

        MenuItem postsItem = new MenuItem("Posts");
        postsItem.setOnAction(e -> tabPane.getSelectionModel().select(2));

        viewMenu.getItems().addAll(welcomeItem, usersItem, postsItem);

        Menu userMenu = new Menu("User Management");
        MenuItem addUserItem = new MenuItem("Add User");
        addUserItem.setOnAction(e -> showAddUserDialog());

        MenuItem editUserItem = new MenuItem("Edit User");
        editUserItem.setOnAction(e -> showEditUserDialog());

        userMenu.getItems().addAll(addUserItem, editUserItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, viewMenu, userMenu);
        return menuBar;
    }

    private void showAddUserDialog() {
    }

    private void showEditUserDialog() {
    }
} 
