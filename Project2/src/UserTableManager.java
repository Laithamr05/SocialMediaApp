// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import java.util.Iterator;

public class UserTableManager { // class to manage user table display
    private TableView<UserManager> userTable; // main table to display users
    private CircularDoublyLinkedList<UserManager> users; // list of users in the system
    
    public UserTableManager(CircularDoublyLinkedList<UserManager> users) { // constructor to initialize the manager
        this.users = users; // store the user list
        this.userTable = createUserTable(); // create the user table
    }
    
    public TableView<UserManager> getUserTable() { // getter for the user table
        return userTable;
    }
    
    public TableView<UserManager> createUserTable() { // method to create the user table
        TableView<UserManager> table = new TableView<>(); // create new table view
        
        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID"); // column for user ids
        idColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue(); // get user from the table row
            SimpleStringProperty result; // property to return
            if (user != null) { // check if user exists
                result = new SimpleStringProperty(user.getUserID()); // set property to user id
            } else {
                result = new SimpleStringProperty(""); // empty string if no user
            }
            return result;
        });
        idColumn.setPrefWidth(100); // set column width
        
        idColumn.setComparator(new java.util.Comparator<String>() { // set custom comparator for sorting
            @Override
            public int compare(String id1, String id2) { // compare method
                try {
                    return Integer.compare(Integer.parseInt(id1), Integer.parseInt(id2)); // try numeric comparison
                } catch (NumberFormatException e) {
                    return id1.compareTo(id2); // fallback to string comparison
                }
            }
        });
        
        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name"); // column for user names
        nameColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue(); // get user from the table row
            SimpleStringProperty result; // property to return
            if (user != null) { // check if user exists
                result = new SimpleStringProperty(user.getName()); // set property to user name
            } else {
                result = new SimpleStringProperty(""); // empty string if no user
            }
            return result;
        });
        nameColumn.setPrefWidth(200); // set column width
        
        TableColumn<UserManager, String> ageColumn = new TableColumn<>("Age"); // column for user age
        ageColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue(); // get user from the table row
            SimpleStringProperty result; // property to return
            if (user != null) { // check if user exists
                result = new SimpleStringProperty(user.getAge()); // set property to user age
            } else {
                result = new SimpleStringProperty(""); // empty string if no user
            }
            return result;
        });
        ageColumn.setPrefWidth(100); // set column width
        
        table.getColumns().addAll(idColumn, nameColumn, ageColumn); // add columns to table
        table.setPrefHeight(400); // set preferred height
        table.setPlaceholder(new Label("No content in table")); // set placeholder when table is empty
        
        refreshTable(table); // load initial data
        
        return table; // return the created table
    }
    
    public void refreshTable(TableView<UserManager> table) { // method to refresh table with current data
        ObservableList<UserManager> userList = FXCollections.observableArrayList(); // create observable list
        
        // Create a list of users in the exact order they appear in the linked list
        Iterator<UserManager> iterator = users.iterator(); // get iterator for users
        while (iterator.hasNext()) { // iterate through users
            UserManager user = iterator.next(); // get next user
            if (user != null) { // check if user exists
                userList.add(user); // add to list
            }
        }
        
        // Clear and set the items
        table.getItems().clear(); // clear existing items
        table.setItems(userList); // set new items
        table.refresh(); // refresh table display
    }
    
    public void refreshTable() { // convenience method to refresh the main table
        refreshTable(userTable); // refresh the user table
    }
    
    public UserManager findUser(String searchTerm, boolean searchById) { // method to find user by name or id
        Iterator<UserManager> iterator = users.iterator(); // get iterator for users
        while (iterator.hasNext()) { // iterate through users
            UserManager user = iterator.next(); // get next user
            if (user != null) { // check if user exists
                if (searchById) { // if searching by id
                    if (user.getUserID().equals(searchTerm)) { // check if id matches
                        return user; // return user if found
                    }
                } else { // if searching by name
                    if (user.getName().equalsIgnoreCase(searchTerm)) { // check if name matches (case insensitive)
                        return user; // return user if found
                    }
                }
            }
        }
        return null; // return null if not found
    }
} 
