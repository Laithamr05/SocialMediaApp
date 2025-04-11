import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.Iterator;

public class UserDialogManager {
    private CircularDoublyLinkedList<UserManager> users;
    private UserTableManager tableManager;
    
    public UserDialogManager(CircularDoublyLinkedList<UserManager> users, UserTableManager tableManager) {
        this.users = users;
        this.tableManager = tableManager;
    }
    
    public void showAddUserDialog() {
        Dialog<UserManager> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter User Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setPromptText("Birth Date");
        
        birthDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.isAfter(today));
            }
        });

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Birth Date:"), 0, 1);
        grid.add(birthDatePicker, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                if (nameField.getText().isEmpty() || birthDatePicker.getValue() == null) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Invalid Input", 
                              "Please fill in all fields.");
                    return null;
                }

                String name = nameField.getText();
                LocalDate birthDate = birthDatePicker.getValue();
                LocalDate today = LocalDate.now();
                Period age = Period.between(birthDate, today);
                
                // Generate unique user ID
                String userID = String.valueOf(users.size() + 1);
                
                UserManager newUser = new UserManager(userID, name, String.valueOf(age.getYears()));
                return newUser;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            // Check for duplicate ID
            if (isDuplicateID(user.getUserID())) {
                // Generate a new ID by appending a suffix
                String newID = generateUniqueID(user.getUserID());
                user.setUserID(newID);
                showAlert(Alert.AlertType.INFORMATION, "ID Changed", 
                          "Duplicate ID detected", 
                          "The User ID was changed to " + newID + " to avoid duplication.");
            }
            
            users.insertLast(user);
            tableManager.refreshTable();
        });
    }
    
    public void showEditUserDialog() {
        UserManager selectedUser = tableManager.getUserTable().getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showSearchUserDialog();
            return;
        }
        
        showEditUserDetailsDialog(selectedUser);
    }
    
    public void showSearchUserDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Search User");
        dialog.setHeaderText("Enter search criteria:");

        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField searchTermField = new TextField();
        searchTermField.setPromptText("Search term");

        ComboBox<String> searchTypeSelector = new ComboBox<>();
        searchTypeSelector.getItems().addAll("By Name", "By ID");
        searchTypeSelector.setValue("By Name");

        grid.add(new Label("Search:"), 0, 0);
        grid.add(searchTermField, 1, 0);
        grid.add(new Label("Type:"), 0, 1);
        grid.add(searchTypeSelector, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                return searchTermField.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(searchTerm -> {
            if (searchTerm != null && !searchTerm.trim().isEmpty()) {
                boolean searchById = searchTypeSelector.getValue().equals("By ID");
                UserManager user = tableManager.findUser(searchTerm, searchById);
                
                if (user != null) {
                    showEditUserDetailsDialog(user);
                } else {
                    showAlert(Alert.AlertType.INFORMATION, "Search Result", "User Not Found", 
                            "No user found with " + (searchById ? "ID" : "name") + ": " + searchTerm);
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Empty Search", 
                        "Please enter a search term.");
            }
        });
    }
    
    public void showEditUserDetailsDialog(UserManager user) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit User Details");
        dialog.setHeaderText("Edit details for user: " + user.getName());

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Fields with current values
        TextField idField = new TextField(user.getUserID());
        TextField nameField = new TextField(user.getName());
        TextField ageField = new TextField(user.getAge());

        // Add fields to grid
        grid.add(new Label("User ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Age:"), 0, 2);
        grid.add(ageField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        
        if (result.isPresent() && result.get() == saveButtonType) {
            String newID = idField.getText().trim();
            String newName = nameField.getText().trim();
            String newAge = ageField.getText().trim();
            
            if (newID.isEmpty() || newName.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Invalid Input", "Empty Fields", 
                        "User ID and Name cannot be empty.");
                return;
            }
            
            // Check for duplicate ID if changed
            if (!newID.equals(user.getUserID()) && isDuplicateID(newID)) {
                String uniqueID = generateUniqueID(newID);
                showAlert(Alert.AlertType.WARNING, "Duplicate ID", "ID Already Exists", 
                        "The ID '" + newID + "' is already in use. Using '" + uniqueID + "' instead.");
                newID = uniqueID;
            }
            
            // Update user details
            user.setUserID(newID);
            user.setName(newName);
            user.setAge(newAge);
            
            // Refresh table to show changes
            tableManager.refreshTable();
            
            showAlert(Alert.AlertType.INFORMATION, "Success", "User Updated", 
                    "User details have been updated successfully.");
        }
    }
    
    public boolean confirmDeleteUser(UserManager user) {
        if (user == null) {
            showAlert(Alert.AlertType.WARNING, "Warning", "No User Selected", 
                      "Please select a user to delete.");
            return false;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete User");
        confirmAlert.setContentText("Are you sure you want to delete user " + user.getName() + "?");
        
        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    public boolean isDuplicateID(String id) {
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user.getUserID().equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    public String generateUniqueID(String baseID) {
        int counter = 1;
        String newID = baseID + "_" + counter;
        
        while (isDuplicateID(newID)) {
            counter++;
            newID = baseID + "_" + counter;
        }
        
        return newID;
    }
    
    public void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 