import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.beans.property.SimpleStringProperty;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import java.util.Calendar;

public class DataManagementUI {
    private CircularDoublyLinkedList<UserManager> userList;
    private CircularDoublyLinkedList<PostManager> postList;
    private FileManager dataManager;
    private ComboBox<String> sortSelector;
    private TableView<PostManager> postTableView;
    private TableView<UserManager> userTableView;

    public DataManagementUI(CircularDoublyLinkedList<UserManager> userList, CircularDoublyLinkedList<PostManager> postList, FileManager dataManager) {
        this.userList = userList;
        this.postList = postList;
        this.dataManager = dataManager;
    }

    public Tab createDataManagementTab() {
        Tab mainTab = new Tab("Data Management");
        mainTab.setClosable(false);

        VBox contentBox = new VBox(15);
        contentBox.setPadding(new Insets(20));
        contentBox.setAlignment(Pos.TOP_CENTER);

        // Title
        Label sectionTitle = new Label("Data Management");
        sectionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Sort order selection
        HBox sortOptionsBox = new HBox(10);
        sortOptionsBox.setAlignment(Pos.CENTER);
        
        Label sortByLabel = new Label("Sort Order:");
        sortSelector = new ComboBox<>();
        sortSelector.getItems().addAll("Unsorted", "Ascending by Username", "Descending by Username");
        sortSelector.setValue("Unsorted");
        sortSelector.setPrefWidth(200);
        
        sortOptionsBox.getChildren().addAll(sortByLabel, sortSelector);

        // Save Reports Section
        VBox reportOptionsBox = new VBox(10);
        reportOptionsBox.setAlignment(Pos.CENTER);
        reportOptionsBox.setPadding(new Insets(10));
        reportOptionsBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        Label reportSectionLabel = new Label("Save Reports");
        reportSectionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Button saveCreatedPostsButton = new Button("Save Posts Created Report");
        saveCreatedPostsButton.setPrefWidth(250);
        saveCreatedPostsButton.setOnAction(e -> generateCreatedPostsReport());
        
        Button saveSharedPostsButton = new Button("Save Posts Shared Report");
        saveSharedPostsButton.setPrefWidth(250);
        saveSharedPostsButton.setOnAction(e -> generateSharedPostsReport());
        
        reportOptionsBox.getChildren().addAll(reportSectionLabel, saveCreatedPostsButton, saveSharedPostsButton);

        // Save Data Section
        VBox dataOptionsBox = new VBox(10);
        dataOptionsBox.setAlignment(Pos.CENTER);
        dataOptionsBox.setPadding(new Insets(10));
        dataOptionsBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5;");
        
        Label dataExportLabel = new Label("Save Updated Data");
        dataExportLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Button exportUsersButton = new Button("Save Updated Users");
        exportUsersButton.setPrefWidth(250);
        exportUsersButton.setOnAction(e -> exportUserData());
        
        Button exportFriendsButton = new Button("Save Updated Friendships");
        exportFriendsButton.setPrefWidth(250);
        exportFriendsButton.setOnAction(e -> exportFriendshipData());
        
        Button exportPostsButton = new Button("Save Updated Posts");
        exportPostsButton.setPrefWidth(250);
        exportPostsButton.setOnAction(e -> exportPostData());
        
        dataOptionsBox.getChildren().addAll(dataExportLabel, exportUsersButton, exportFriendsButton, exportPostsButton);

        contentBox.getChildren().addAll(
            sectionTitle,
            new Separator(),
            sortOptionsBox,
            new Separator(),
            reportOptionsBox,
            dataOptionsBox
        );

        mainTab.setContent(contentBox);
        return mainTab;
    }

    public ObservableList<UserManager> getSortedUserList() {
        ObservableList<UserManager> sortedUserList = FXCollections.observableArrayList();
        
        Iterator<UserManager> userIterator = userList.iterator();
        while (userIterator.hasNext()) {
            UserManager user = userIterator.next();
            sortedUserList.add(user);
        }
        
        String sortOption = sortSelector.getValue();
        if (sortOption.equals("Ascending by Username")) {
            Collections.sort(sortedUserList, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager user1, UserManager user2) {
                    return user1.getName().compareTo(user2.getName());
                }
            });
        } else if (sortOption.equals("Descending by Username")) {
            Collections.sort(sortedUserList, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager user1, UserManager user2) {
                    return user2.getName().compareTo(user1.getName());
                }
            });
        }
        
        return sortedUserList;
    }

    public void generateCreatedPostsReport() {
        ObservableList<UserManager> sortedUserList = getSortedUserList();
        
        if (sortedUserList.isEmpty()) {
            showNotification("No users available to generate report");
            return;
        }
        
        FileChooser filePicker = new FileChooser();
        filePicker.setTitle("Save Posts Created Report");
        filePicker.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        filePicker.setInitialFileName("posts_created.txt");
        
        File outputFile = filePicker.showSaveDialog(null);
        if (outputFile != null) {
            try (PrintWriter fileWriter = new PrintWriter(new FileWriter(outputFile))) {
                fileWriter.println("Posts Created Report");
                fileWriter.println("-------------------");
                fileWriter.println();
                
                int userCount = sortedUserList.size();
                for (int i = 0; i < userCount; i++) {
                    UserManager currentUser = sortedUserList.get(i);
                    fileWriter.println("User: " + currentUser.getName());
                    
                    Iterator<PostManager> postIterator = postList.iterator();
                    boolean hasUserPosts = false;
                    
                    while (postIterator.hasNext()) {
                        PostManager currentPost = postIterator.next();
                        if (currentPost.getCreator().equals(currentUser)) {
                            hasUserPosts = true;
                            
                            String sharedWithUsers = "";
                            CircularDoublyLinkedList<UserManager> sharedUserList = currentPost.getSharedUsers();
                            if (sharedUserList != null) {
                                Iterator<UserManager> sharedIterator = sharedUserList.iterator();
                                while (sharedIterator.hasNext()) {
                                    UserManager sharedUser = sharedIterator.next();
                                    if (!sharedWithUsers.equals("")) {
                                        sharedWithUsers += ", ";
                                    }
                                    sharedWithUsers += sharedUser.getName();
                                }
                            }
                            
                            fileWriter.println("- Post ID: " + currentPost.getPostID() + 
                                        ", Content: " + currentPost.getContent() + 
                                        ", " + formatDateString(currentPost.getCreationDate()) + 
                                        ", Shared With: " + (sharedWithUsers.equals("") ? "None" : sharedWithUsers));
                        }
                    }
                    
                    if (!hasUserPosts) {
                        fileWriter.println("No posts created by this user.");
                    }
                    
                    fileWriter.println();
                }
                
                showNotification("Report Saved", "Posts Created Report has been saved to " + outputFile.getName());
            } catch (IOException e) {
                showNotification("Error saving report: " + e.getMessage());
            }
        }
    }

    public void generateSharedPostsReport() {
        ObservableList<UserManager> sortedUserList = getSortedUserList();
        
        if (sortedUserList.isEmpty()) {
            showNotification("No users available to generate report");
            return;
        }
        
        FileChooser filePicker = new FileChooser();
        filePicker.setTitle("Save Posts Shared Report");
        filePicker.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        filePicker.setInitialFileName("posts_shared.txt");
        
        File outputFile = filePicker.showSaveDialog(null);
        if (outputFile != null) {
            try (PrintWriter fileWriter = new PrintWriter(new FileWriter(outputFile))) {
                fileWriter.println("Posts Shared With User Report");
                fileWriter.println("---------------------------");
                fileWriter.println();
                
                int userCount = sortedUserList.size();
                for (int i = 0; i < userCount; i++) {
                    UserManager currentUser = sortedUserList.get(i);
                    fileWriter.println("User: " + currentUser.getName());
                    
                    Iterator<PostManager> postIterator = postList.iterator();
                    boolean hasSharedPosts = false;
                    
                    while (postIterator.hasNext()) {
                        PostManager currentPost = postIterator.next();
                        CircularDoublyLinkedList<UserManager> sharedUserList = currentPost.getSharedUsers();
                        
                        if (sharedUserList != null) {
                            Iterator<UserManager> sharedIterator = sharedUserList.iterator();
                            while (sharedIterator.hasNext()) {
                                UserManager sharedUser = sharedIterator.next();
                                if (sharedUser.equals(currentUser)) {
                                    hasSharedPosts = true;
                                    fileWriter.println("- Post ID: " + currentPost.getPostID() + 
                                                ", Content: " + currentPost.getContent() + 
                                                ", " + formatDateString(currentPost.getCreationDate()) + 
                                                ", Creator: " + currentPost.getCreator().getName());
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (!hasSharedPosts) {
                        fileWriter.println("No posts shared with this user.");
                    }
                    
                    fileWriter.println();
                }
                
                showNotification("Report Saved", "Posts Shared Report has been saved to " + outputFile.getName());
            } catch (IOException e) {
                showNotification("Error saving report: " + e.getMessage());
            }
        }
    }

    public void exportUserData() {
        FileChooser filePicker = new FileChooser();
        filePicker.setTitle("Save Updated Users");
        filePicker.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        filePicker.setInitialFileName("updated_users.txt");
        
        File outputFile = filePicker.showSaveDialog(null);
        if (outputFile != null) {
            try {
                dataManager.saveUsers(outputFile.getAbsolutePath(), getSortedUserList());
                showNotification("Success", "Updated users data has been saved to " + outputFile.getName());
            } catch (Exception e) {
                showNotification("Error saving users data: " + e.getMessage());
            }
        }
    }

    public void exportFriendshipData() {
        FileChooser filePicker = new FileChooser();
        filePicker.setTitle("Save Updated Friendships");
        filePicker.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        filePicker.setInitialFileName("updated_friendships.txt");
        
        File outputFile = filePicker.showSaveDialog(null);
        if (outputFile != null) {
            try {
                dataManager.saveFriendships(outputFile.getAbsolutePath(), getSortedUserList());
                showNotification("Success", "Updated friendships data has been saved to " + outputFile.getName());
            } catch (Exception e) {
                showNotification("Error saving friendships data: " + e.getMessage());
            }
        }
    }

    public void exportPostData() {
        FileChooser filePicker = new FileChooser();
        filePicker.setTitle("Save Updated Posts");
        filePicker.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        filePicker.setInitialFileName("updated_posts.txt");
        
        File outputFile = filePicker.showSaveDialog(null);
        if (outputFile != null) {
            try {
                dataManager.savePosts(outputFile.getAbsolutePath(), postList, getSortedUserList());
                showNotification("Success", "Updated posts data has been saved to " + outputFile.getName());
            } catch (Exception e) {
                showNotification("Error saving posts data: " + e.getMessage());
            }
        }
    }

    public String formatDateString(Calendar date) {
        int day = date.get(Calendar.DAY_OF_MONTH);
        int month = date.get(Calendar.MONTH) + 1; // Calendar months are 0-based
        int year = date.get(Calendar.YEAR);
        
        String dayStr;
        if (day < 10) {
            dayStr = "0" + day;
        } else {
            dayStr = String.valueOf(day);
        }
        
        String monthStr;
        if (month < 10) {
            monthStr = "0" + month;
        } else {
            monthStr = String.valueOf(month);
        }
        
        return dayStr + "." + monthStr + "." + year;
    }

    public void showNotification(String message) {
        Alert popup = new Alert(Alert.AlertType.INFORMATION);
        popup.setTitle("Information");
        popup.setHeaderText(null);
        popup.setContentText(message);
        popup.showAndWait();
    }

    public void showNotification(String title, String message) {
        Alert popup = new Alert(Alert.AlertType.INFORMATION);
        popup.setTitle(title);
        popup.setHeaderText(null);
        popup.setContentText(message);
        popup.showAndWait();
    }

    public void refreshPostTable() {
        ObservableList<PostManager> displayPosts = FXCollections.observableArrayList();
        Iterator<PostManager> iterator = postList.iterator();
        while (iterator.hasNext()) {
            displayPosts.add(iterator.next());
        }
        postTableView.setItems(displayPosts);
    }

    public void refreshUserTable() {
        ObservableList<UserManager> displayUsers = FXCollections.observableArrayList();
        Iterator<UserManager> iterator = userList.iterator();
        while (iterator.hasNext()) {
            displayUsers.add(iterator.next());
        }
        userTableView.setItems(displayUsers);
    }

    public TableColumn<PostManager, String> createSharedUsersColumn() {
        TableColumn<PostManager, String> friendsColumn = new TableColumn<>("Shared With");
        friendsColumn.setCellValueFactory(cellData -> {
            String displayText = "";
            CircularDoublyLinkedList<UserManager> sharedFriends = cellData.getValue().getSharedUsers();
            Iterator<UserManager> iterator = sharedFriends.iterator();
            while (iterator.hasNext()) {
                UserManager friend = iterator.next();
                if (!displayText.isEmpty()) {
                    displayText += ", ";
                }
                displayText += friend.getName();
            }
            
            SimpleStringProperty result;
            if (displayText.isEmpty()) {
                result = new SimpleStringProperty("Not shared");
            } else {
                result = new SimpleStringProperty(displayText);
            }
            return result;
        });
        return friendsColumn;
    }
} 