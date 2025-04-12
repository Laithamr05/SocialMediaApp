import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private TableManager tableManager;
    private DialogManager dialogManager;
    private ComboBox<String> sortOrderComboBox;
    private TextField searchField;
    private ComboBox<String> searchTypeComboBox;

    public UserManagerUI(CircularDoublyLinkedList<UserManager> users, FileManager fileManager, WelcomePage welcomePage) {
        this.users = users;
        this.fileManager = fileManager;
        this.welcomePage = welcomePage;
        this.tableManager = new TableManager(users, null);
        this.dialogManager = new DialogManager(users, null, tableManager);
        this.searchField = new TextField();
        this.searchTypeComboBox = new ComboBox<>();
    }

    public Tab createUserTab() {
        VBox userContent = new VBox(20);
        userContent.setPadding(new Insets(20));
        userContent.setAlignment(Pos.CENTER);

        HBox searchBox = createSearchBox();

        // Create the user table first
        TableView<UserManager> userTable = tableManager.createUserTable();

        HBox tableNavBox = createNavigationButtons();

        HBox actionButtonsBox = createActionButtons();

        userContent.getChildren().addAll(searchBox, userTable, tableNavBox, actionButtonsBox);

        Tab userTab = new Tab("Users", userContent);
        userTab.setClosable(false);
        return userTab;
    }

    private HBox createSearchBox() {
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);
        
        searchTypeComboBox.getItems().addAll("ID", "Name");
        searchTypeComboBox.setValue("Name");
        searchTypeComboBox.setPrefWidth(100);
        
        searchBox.getChildren().addAll(new Label("Search by:"), searchTypeComboBox, searchField);
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

    private void handleUserSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "Please enter a search term");
            return;
        }

        boolean searchById = searchTerm.matches("\\d+");
        UserManager foundUser = tableManager.findUser(searchTerm, searchById);

        if (foundUser != null) {
            tableManager.getUserTable().getSelectionModel().select(foundUser);
            tableManager.getUserTable().scrollTo(foundUser);
        } else {
            String errorMessage;
            if (searchById) {
                errorMessage = "No user found with ID: " + searchTerm;
            } else {
                errorMessage = "No user found with name: " + searchTerm;
            }
            showAlert(Alert.AlertType.ERROR, "Error", null, errorMessage);
        }
    }

    private void navigateToPreviousUser() {
        TableView<UserManager> table = tableManager.getUserTable();
        int currentIndex = table.getSelectionModel().getSelectedIndex();
        
        if (currentIndex > 0) {
            table.getSelectionModel().select(currentIndex - 1);
            table.scrollTo(currentIndex - 1);
        } else if (currentIndex == -1 && !table.getItems().isEmpty()) {
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
        if (users == null) {
            showAlert(Alert.AlertType.ERROR, "Error", null, "No users loaded - please load users first");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select User Data File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                CircularDoublyLinkedList<UserManager> loadedUsers = fileManager.loadUsers(selectedFile.getAbsolutePath());
                Iterator<UserManager> iterator = loadedUsers.iterator();
                while (iterator.hasNext()) {
                    users.add(iterator.next());
                }
                
                int duplicatesRemoved = removeDuplicateUsers();
                
                refreshUserTable();
                
                String successMessage = "Users loaded successfully.";
                if (duplicatesRemoved > 0) {
                    successMessage += "\nRemoved " + duplicatesRemoved + " duplicate user(s).";
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", null, successMessage);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Error", null, "Failed to load users: " + e.getMessage());
            }
        }
    }
    
    public int removeDuplicateUsers() {
        List<UserManager> uniqueUsers = new ArrayList<>();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            boolean isDuplicate = false;
            for (int i = 0; i < uniqueUsers.size(); i++) {
                if (uniqueUsers.get(i).getUserID().equals(user.getUserID())) {
                    isDuplicate = true;
                    break;
                }
            }
            if (!isDuplicate) {
                uniqueUsers.add(user);
            }
        }
        
        int removedCount = users.size() - uniqueUsers.size();
        users.clear();
        for (int i = 0; i < uniqueUsers.size(); i++) {
            users.insertLast(uniqueUsers.get(i));
        }
        tableManager.refreshUserTable(users);
        return removedCount;
    }

    public void refreshUserTable() {
        tableManager.refreshUserTable(users);
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
