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
        Menu fileMenu = new Menu("Exit");
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
        
        MenuItem dataManagementItem = new MenuItem("Data Management");
        dataManagementItem.setOnAction(e -> tabPane.getSelectionModel().select(5));

        viewMenu.getItems().addAll(welcomeItem, usersItem, postsItem, friendshipItem, reportsItem, dataManagementItem);

        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, viewMenu);
        return menuBar;
    }
} 
