// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.scene.control.*;

public class MenuUIManager { // class to manage the application menu bar UI
    private TabPane tabPane; // reference to the main tab pane for navigation
    private CircularDoublyLinkedList<UserManager> users; // list of users in the system
    
    public MenuUIManager(TabPane tabPane, CircularDoublyLinkedList<UserManager> users) { // constructor to initialize the manager
        this.tabPane = tabPane; // store the tab pane reference
        this.users = users; // store the user list
    }
    
    public MenuBar createMenuBar() { // method to create the application menu bar
        MenuBar menuBar = new MenuBar(); // create menu bar
        
        Menu fileMenu = new Menu("File"); // create file menu
        MenuItem exitItem = new MenuItem("Exit"); // create exit menu item
        exitItem.setOnAction(e -> System.exit(0)); // set action to exit application
        fileMenu.getItems().add(exitItem); // add exit item to menu

        Menu viewMenu = new Menu("View"); // create view menu
        MenuItem welcomeItem = new MenuItem("Welcome Page"); // create welcome page menu item
        welcomeItem.setOnAction(e -> tabPane.getSelectionModel().select(0)); // set action to switch to welcome tab

        MenuItem usersItem = new MenuItem("Users"); // create users menu item
        usersItem.setOnAction(e -> tabPane.getSelectionModel().select(1)); // set action to switch to users tab

        MenuItem friendshipItem = new MenuItem("Friendship"); // create friendship menu item
        friendshipItem.setOnAction(e -> tabPane.getSelectionModel().select(2)); // set action to switch to friendship tab
        
        MenuItem postsItem = new MenuItem("Posts"); // create posts menu item
        postsItem.setOnAction(e -> tabPane.getSelectionModel().select(3)); // set action to switch to posts tab
        
        MenuItem reportsItem = new MenuItem("Reports & Statistics"); // create reports menu item
        reportsItem.setOnAction(e -> tabPane.getSelectionModel().select(4)); // set action to switch to reports tab
        
        MenuItem dataManagementItem = new MenuItem("Data Management"); // create data management menu item
        dataManagementItem.setOnAction(e -> tabPane.getSelectionModel().select(5)); // set action to switch to data management tab

        viewMenu.getItems().addAll(welcomeItem, usersItem, friendshipItem, postsItem, reportsItem, dataManagementItem); // add all items to view menu

        menuBar.getMenus().addAll(fileMenu, viewMenu); // add menus to menu bar
        return menuBar; // return the created menu bar
    }
} 