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
        
        MenuItem friendshipItem = new MenuItem("Friendship");
        friendshipItem.setOnAction(e -> tabPane.getSelectionModel().select(3));
        
        MenuItem reportsItem = new MenuItem("Reports & Statistics");
        reportsItem.setOnAction(e -> tabPane.getSelectionModel().select(4));

        viewMenu.getItems().addAll(welcomeItem, usersItem, postsItem, friendshipItem, reportsItem);

        Menu userMenu = new Menu("User Management");
        MenuItem addUserItem = new MenuItem("Add User");
        addUserItem.setOnAction(e -> showAddUserDialog());

        MenuItem editUserItem = new MenuItem("Edit User");
        editUserItem.setOnAction(e -> showEditUserDialog());

        userMenu.getItems().addAll(addUserItem, editUserItem);
        
        Menu reportMenu = new Menu("Reports");
        MenuItem postsCreatedItem = new MenuItem("Posts Created by User");
        postsCreatedItem.setOnAction(e -> {
            tabPane.getSelectionModel().select(4); // Select the Reports tab
        });
        
        MenuItem postsSharedItem = new MenuItem("Posts Shared with User");
        postsSharedItem.setOnAction(e -> {
            tabPane.getSelectionModel().select(4); // Select the Reports tab
        });
        
        MenuItem mostActiveItem = new MenuItem("Most Active Users");
        mostActiveItem.setOnAction(e -> {
            tabPane.getSelectionModel().select(4); // Select the Reports tab
        });
        
        MenuItem engagementItem = new MenuItem("User Engagement Metrics");
        engagementItem.setOnAction(e -> {
            tabPane.getSelectionModel().select(4); // Select the Reports tab
        });
        
        reportMenu.getItems().addAll(postsCreatedItem, postsSharedItem, mostActiveItem, engagementItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, viewMenu, userMenu, reportMenu);
        return menuBar;
    }

    private void showAddUserDialog() {
    }

    private void showEditUserDialog() {
    }
} 
