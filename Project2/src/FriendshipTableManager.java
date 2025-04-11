// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class FriendshipTableManager { // class to manage friendship table display
    private TableView<UserManager> friendshipTable; // main table to display friendships
    private CircularDoublyLinkedList<UserManager> users; // list of users in the system
    
    public FriendshipTableManager(CircularDoublyLinkedList<UserManager> users) { // constructor to initialize the manager
        this.users = users; // store the user list
        this.friendshipTable = createFriendshipTable(); // create the friendship table
    }
    
    public TableView<UserManager> getFriendshipTable() { // getter for the friendship table
        return friendshipTable;
    }
    
    public TableView<UserManager> createFriendshipTable() { // method to create the friendship table
        TableView<UserManager> tableView = new TableView<>(); // create new table view
        tableView.setPrefHeight(400); // set preferred height

        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name"); // column for user names
        nameColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue(); // get user from the table row
            SimpleStringProperty property = new SimpleStringProperty(""); // default empty property
            if (user != null) { // check if user exists
                property = new SimpleStringProperty(user.getName()); // set property to user name
            }
            return property;
        });
        nameColumn.setPrefWidth(150); // set column width

        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID"); // column for user ids
        idColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue(); // get user from the table row
            SimpleStringProperty property = new SimpleStringProperty(""); // default empty property
            if (user != null) { // check if user exists
                property = new SimpleStringProperty(user.getUserID()); // set property to user id
            }
            return property;
        });
        idColumn.setPrefWidth(100); // set column width

        TableColumn<UserManager, String> friendsColumn = new TableColumn<>("Friends"); // column to show friends
        friendsColumn.setPrefWidth(400); // set column width
        friendsColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue(); // get user from the table row
            String friends = ""; // start with empty string
            if (user != null) { // check if user exists
                Iterator<UserManager> iterator = user.getFriends().iterator(); // get iterator for friends
                while (iterator.hasNext()) { // iterate through friends
                    UserManager friend = iterator.next(); // get next friend
                    if (friend != null) { // check if friend exists
                        if (!friends.equals("")) { // add comma if not first friend
                            friends += ", ";
                        }
                        friends += friend.getName(); // add friend name
                    }
                }
            }

            SimpleStringProperty property; // property to return
            if (friends.equals("")) { // check if no friends
                property = new SimpleStringProperty("No friends"); // show no friends message
            } else {
                property = new SimpleStringProperty(friends); // show list of friends
            }
            return property;
        });

        tableView.getColumns().addAll(nameColumn, idColumn, friendsColumn); // add columns to table
        refreshTable(tableView); // load initial data
        
        return tableView; // return the created table
    }
    
    public void refreshTable(TableView<UserManager> table) { // method to refresh table with current data
        ObservableList<UserManager> userList = FXCollections.observableArrayList(); // create observable list
        Iterator<UserManager> iterator = users.iterator(); // get iterator for users
        while (iterator.hasNext()) { // iterate through users
            UserManager user = iterator.next(); // get next user
            if (user != null) { // check if user exists
                userList.add(user); // add to list
            }
        }
        table.setItems(userList); // set items to table
        table.refresh(); // refresh table display
    }
    
    public void refreshTable() { // convenience method to refresh the main table
        refreshTable(friendshipTable); // refresh the main friendship table
    }
    
    public ObservableList<UserManager> getSortedUserList(String sortOrder) { // method to get sorted list of users
        ObservableList<UserManager> userList = FXCollections.observableArrayList(); // create observable list
        Iterator<UserManager> iterator = users.iterator(); // get iterator for users
        while (iterator.hasNext()) { // iterate through users
            UserManager user = iterator.next(); // get next user
            if (user != null) { // check if user exists
                userList.add(user); // add to list
            }
        }
        
        if (sortOrder.equals("Ascending by Username")) { // sort ascending by name
            Collections.sort(userList, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager u1, UserManager u2) { // compare method
                    return u1.getName().compareTo(u2.getName()); // compare by name
                }
            });
        } else if (sortOrder.equals("Descending by Username")) { // sort descending by name
            Collections.sort(userList, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager u1, UserManager u2) { // compare method
                    return u2.getName().compareTo(u1.getName()); // compare by name in reverse
                }
            });
        }
        
        return userList; // return sorted list
    }
    
    public UserManager findUserById(String id) { // method to find user by id
        Iterator<UserManager> iterator = users.iterator(); // get iterator for users
        while (iterator.hasNext()) { // iterate through users
            UserManager user = iterator.next(); // get next user
            if (user != null && user.getUserID().equals(id)) { // check if user exists and id matches
                return user; // return user if found
            }
        }
        return null; // return null if not found
    }
} 