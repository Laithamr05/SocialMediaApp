import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class UserManagerUI {
    private CircularDoublyLinkedList<UserManager> users;
    private FileManager fileManager;
    private WelcomePage welcomePage;
    private UserTableManager tableManager;
    private UserDialogManager dialogManager;
    private ComboBox<String> sortOrderComboBox;

    public UserManagerUI(CircularDoublyLinkedList<UserManager> users, FileManager fileManager, WelcomePage welcomePage) {
        this.users = users;
        this.fileManager = fileManager;
        this.welcomePage = welcomePage;
        this.tableManager = new UserTableManager(users);
        this.dialogManager = new UserDialogManager(users, tableManager);
    }

    public Tab createUserTab() {
        VBox userContent = new VBox(20);
        userContent.setPadding(new Insets(20));
        userContent.setAlignment(Pos.CENTER);

        // Add search box
        HBox searchBox = createSearchBox();

        // Get table from manager
        TableView<UserManager> userTable = tableManager.getUserTable();

        // Navigate buttons
        HBox tableNavBox = createNavigationButtons();

        // Action buttons
        HBox actionButtonsBox = createActionButtons();

        userContent.getChildren().addAll(searchBox, userTable, tableNavBox, actionButtonsBox);

        Tab userTab = new Tab("Users", userContent);
        userTab.setClosable(false);
        return userTab;
    }

    private HBox createSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        
        ComboBox<String> searchTypeComboBox = new ComboBox<>();
        searchTypeComboBox.getItems().addAll("ID", "Name");
        searchTypeComboBox.setValue("Name");
        searchTypeComboBox.setPrefWidth(100);
        
        TextField searchField = new TextField();
        searchField.setPromptText("Enter search term");
        searchField.setPrefWidth(200);
        
        Button searchButton = new Button("Search");
        searchButton.setPrefWidth(100);
        searchButton.setOnAction(e -> searchForUser(searchTypeComboBox.getValue(), searchField.getText()));
        
        searchBox.getChildren().addAll(new Label("Search by:"), searchTypeComboBox, searchField, searchButton);
        return searchBox;
    }

    private HBox createNavigationButtons() {
        HBox tableNavBox = new HBox(10);
        tableNavBox.setAlignment(Pos.CENTER);
        
        Button prevButton = new Button("◀ Previous");
        prevButton.setPrefWidth(100);
        prevButton.setOnAction(e -> navigateToPreviousUser());
        
        Button nextButton = new Button("Next ▶");
        nextButton.setPrefWidth(100);
        nextButton.setOnAction(e -> navigateToNextUser());
        
        tableNavBox.getChildren().addAll(prevButton, nextButton);
        return tableNavBox;
    }

    private HBox createActionButtons() {
        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.setAlignment(Pos.CENTER);
        
        Button addUserButton = new Button("Add User");
        addUserButton.setPrefWidth(100);
        addUserButton.setOnAction(e -> dialogManager.showAddUserDialog());
        
        Button editUserButton = new Button("Edit User");
        editUserButton.setPrefWidth(100);
        editUserButton.setOnAction(e -> dialogManager.showEditUserDialog());
        
        Button deleteUserButton = new Button("Delete User");
        deleteUserButton.setPrefWidth(100);
        deleteUserButton.setOnAction(e -> deleteSelectedUser());
        
        Button uploadUsersButton = new Button("Upload Users");
        uploadUsersButton.setPrefWidth(100);
        uploadUsersButton.setOnAction(e -> {
            handleUserFileUpload();
            refreshUserTable();
        });
        
        actionButtonsBox.getChildren().addAll(addUserButton, editUserButton, deleteUserButton, uploadUsersButton);
        return actionButtonsBox;
    }

    private void searchForUser(String searchType, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Warning", null, "Please enter a search term.");
            return;
        }

        boolean searchById = "ID".equals(searchType);
        UserManager foundUser = tableManager.findUser(searchTerm, searchById);

        if (foundUser == null) {
            String message = searchById 
                ? "No user found with ID: " + searchTerm 
                : "No user found with name: " + searchTerm;
            showAlert(Alert.AlertType.INFORMATION, "Search Result", null, message);
            return;
        }

        // Select the found user in the table
        tableManager.getUserTable().getSelectionModel().select(foundUser);
        tableManager.getUserTable().scrollTo(foundUser);
        
        // Show the user details
        String details = "User Found:\n\n" +
                         "ID: " + foundUser.getUserID() + "\n" +
                         "Name: " + foundUser.getName() + "\n" +
                         "Age: " + foundUser.getAge();
        
        showAlert(Alert.AlertType.INFORMATION, "Search Result", "User Found", details);
    }

    private void navigateToPreviousUser() {
        TableView<UserManager> table = tableManager.getUserTable();
        int currentIndex = table.getSelectionModel().getSelectedIndex();
        
        if (currentIndex > 0) {
            table.getSelectionModel().select(currentIndex - 1);
            table.scrollTo(currentIndex - 1);
        } else if (currentIndex == -1 && !table.getItems().isEmpty()) {
            // No selection, select the first item
            table.getSelectionModel().select(0);
            table.scrollTo(0);
        }
    }

    private void navigateToNextUser() {
        TableView<UserManager> table = tableManager.getUserTable();
        int currentIndex = table.getSelectionModel().getSelectedIndex();
        int lastIndex = table.getItems().size() - 1;
        
        if (currentIndex < lastIndex) {
            table.getSelectionModel().select(currentIndex + 1);
            table.scrollTo(currentIndex + 1);
        } else if (currentIndex == -1 && !table.getItems().isEmpty()) {
            // No selection, select the first item
            table.getSelectionModel().select(0);
            table.scrollTo(0);
        }
    }

    private void deleteSelectedUser() {
        UserManager selectedUser = tableManager.getUserTable().getSelectionModel().getSelectedItem();
        
        if (dialogManager.confirmDeleteUser(selectedUser)) {
            users.delete(selectedUser);
            refreshUserTable();
            showAlert(Alert.AlertType.INFORMATION, "Success", null, 
                      "User '" + selectedUser.getName() + "' deleted successfully.");
        }
    }

    private void handleUserFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select User Data File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                fileManager.loadUsers(selectedFile.getAbsolutePath(), users);
                
                int duplicatesRemoved = removeDuplicateUsers();
                
                refreshUserTable();
                
                String successMessage = "Users loaded successfully.";
                if (duplicatesRemoved > 0) {
                    successMessage += "\nRemoved " + duplicatesRemoved + " duplicate user(s).";
                }
                
                showAlert(Alert.AlertType.INFORMATION, "Success", null, successMessage);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", "Upload Failed", 
                         "Error loading user data: " + e.getMessage());
            }
        }
    }
    
    private int removeDuplicateUsers() {
        int duplicatesRemoved = 0;
        
        // Keep track of user IDs we've already seen
        ArrayList<String> seenUserIds = new ArrayList<>();
        ArrayList<UserManager> uniqueUsers = new ArrayList<>();
        
        // First collect all unique users (keeping only first occurrence of each ID)
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                String userId = user.getUserID();
                
                if (!seenUserIds.contains(userId)) {
                    // First time seeing this ID - keep it
                    seenUserIds.add(userId);
                    uniqueUsers.add(user);
                } else {
                    // This is a duplicate - don't keep it
                    duplicatesRemoved++;
                }
            }
        }
        
        // Clear the original list
        users.clear();
        
        // Rebuild the list with only unique users
        for (UserManager user : uniqueUsers) {
            users.insertLast(user);
        }
        
        return duplicatesRemoved;
    }

    public void refreshUserTable() {
        tableManager.refreshTable();
    }

    public TableView<UserManager> getUserTable() {
        return tableManager.getUserTable();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 
