// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.util.ArrayList;
import java.util.Iterator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Collections;
import java.util.Comparator;
import java.util.Calendar;
import javafx.collections.ListChangeListener;
import javafx.beans.value.ObservableValue;

// Manages the display and interaction with data tables in the application
public class TableManager {
    // Lists to store the data for each table
    private ObservableList<UserManager> userList;
    private ObservableList<PostManager> postList;
    private ObservableList<UserManager> friendshipList;
    
    // Source data lists
    private CircularDoublyLinkedList<UserManager> users;
    private CircularDoublyLinkedList<PostManager> posts;

    // Table views for displaying data
    private TableView<UserManager> userTable;
    private TableView<PostManager> postTable;
    private TableView<UserManager> friendshipTable;

    // Creates a new TableManager with empty lists
    public TableManager(CircularDoublyLinkedList<UserManager> users, CircularDoublyLinkedList<PostManager> posts) {
        this.users = users;
        this.posts = posts;
        userList = FXCollections.observableArrayList();
        postList = FXCollections.observableArrayList();
        friendshipList = FXCollections.observableArrayList();
    }

    // Creates and configures the user table with appropriate columns
    public TableView<UserManager> createUserTable() {
        userTable = new TableView<>();
        
        // Configure columns for user data
        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        
        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<UserManager, String> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        
        // Add columns to the table
        userTable.getColumns().addAll(idColumn, nameColumn, ageColumn);
        userTable.setItems(userList);
        
        return userTable;
    }

    // Creates and configures the post table with appropriate columns
    public TableView<PostManager> createPostTable() {
        postTable = new TableView<>();
        
        // Configure columns for post data
        TableColumn<PostManager, String> idColumn = new TableColumn<>("Post ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("postID"));
        
        TableColumn<PostManager, String> contentColumn = new TableColumn<>("Content");
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        
        TableColumn<PostManager, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<PostManager, String> authorColumn = new TableColumn<>("Author");
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        
        // Add columns to the table
        postTable.getColumns().addAll(idColumn, contentColumn, dateColumn, authorColumn);
        postTable.setItems(postList);
        
        return postTable;
    }

    // Returns the post table
    public TableView<PostManager> getPostTable() {
        return postTable;
    }

    // Returns the user table
    public TableView<UserManager> getUserTable() {
        return userTable;
    }

    // Returns the friendship table
    public TableView<UserManager> getFriendshipTable() {
        return friendshipTable;
    }

    // Creates and configures the friendship table with appropriate columns
    public TableView<UserManager> createFriendshipTable() {
        friendshipTable = new TableView<>();
        
        // Configure columns for friendship data
        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userID"));
        
        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<UserManager, String> friendsColumn = new TableColumn<>("Friends");
        friendsColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            StringBuilder friendsList = new StringBuilder();
            
            // Get the friends list
            CircularDoublyLinkedList<UserManager> friends = user.getFriends();
            if (friends != null && !friends.isEmpty()) {
                Iterator<UserManager> iterator = friends.iterator();
                boolean first = true;
                
                // Use the iterator to get all friends
                while (iterator.hasNext()) {
                    UserManager friend = iterator.next();
                    if (friend != null) {
                        if (!first) {
                            friendsList.append(", ");
                        }
                        friendsList.append(friend.getName());
                        first = false;
                    }
                }
            }
            
            return new SimpleStringProperty(friendsList.toString());
        });
        
        // Add columns to the table
        friendshipTable.getColumns().addAll(idColumn, nameColumn, friendsColumn);
        friendshipTable.setItems(friendshipList);
        
        return friendshipTable;
    }

    // Refreshes the user table with new data
    public void refreshUserTable(CircularDoublyLinkedList<UserManager> users) {
        this.users = users;
        userList.clear();
        
        // First collect all users
        ArrayList<UserManager> sortedUsers = new ArrayList<>();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                sortedUsers.add(user);
            }
        }
        
        // Sort users by name
        Collections.sort(sortedUsers, (u1, u2) -> u1.getName().compareTo(u2.getName()));
        
        // Add sorted users to the table
        userList.addAll(sortedUsers);
    }

    // Refreshes the post table with new data
    public void refreshPostTable(CircularDoublyLinkedList<PostManager> posts) {
        this.posts = posts;
        postList.clear();
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            postList.add(iterator.next());
        }
    }

    // Refreshes the friendship table with new data
    public void refreshFriendshipTable(CircularDoublyLinkedList<UserManager> users) {
        this.users = users;
        friendshipList.clear();
        
        // First collect all users
        ArrayList<UserManager> sortedUsers = new ArrayList<>();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                sortedUsers.add(user);
            }
        }
        
        // Sort users by name
        Collections.sort(sortedUsers, (u1, u2) -> u1.getName().compareTo(u2.getName()));
        
        // Add sorted users to the table
        friendshipList.addAll(sortedUsers);
    }

    // Returns the currently selected user from the user table
    public UserManager getSelectedUser() {
        return userTable.getSelectionModel().getSelectedItem();
    }

    // Returns the currently selected post from the post table
    public PostManager getSelectedPost() {
        return postTable.getSelectionModel().getSelectedItem();
    }

    // Returns the currently selected user from the friendship table
    public UserManager getSelectedFriendship() {
        return friendshipTable.getSelectionModel().getSelectedItem();
    }

    public String formatDate(Calendar date) {
        if (date == null) return "";
        
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1;
        int year = date.get(Calendar.YEAR);
        
        return String.format("%02d.%02d.%d", day, month, year);
    }
    
    public ObservableList<UserManager> getSortedUserList(String sortOrder) {
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                userList.add(user);
            }
        }
        
        if (sortOrder.equals("Ascending by Username")) {
            Collections.sort(userList, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager u1, UserManager u2) {
                    return u1.getName().compareTo(u2.getName());
                }
            });
        } else if (sortOrder.equals("Descending by Username")) {
            Collections.sort(userList, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager u1, UserManager u2) {
                    return u2.getName().compareTo(u1.getName());
                }
            });
        }
        
        return userList;
    }
    
    public UserManager findUser(String searchTerm, boolean searchById) {
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                if (searchById && user.getUserID().equals(searchTerm)) {
                    return user;
                } else if (!searchById && user.getName().equals(searchTerm)) {
                    return user;
                }
            }
        }
        return null;
    }
    
    public PostManager findPost(String searchTerm, boolean searchById) {
        Iterator<PostManager> iterator = posts.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null) {
                if (searchById && post.getPostID().equals(searchTerm)) {
                    return post;
                } else if (!searchById && post.getContent().equals(searchTerm)) {
                    return post;
                }
            }
        }
        return null;
    }
    
    public void selectUser(UserManager user) {
        userTable.getSelectionModel().select(user);
        userTable.scrollTo(user);
    }
    
    public void selectPost(PostManager post) {
        postTable.getSelectionModel().select(post);
        postTable.scrollTo(post);
    }
    
    public void selectFriendship(UserManager user) {
        friendshipTable.getSelectionModel().select(user);
        friendshipTable.scrollTo(user);
    }
} 