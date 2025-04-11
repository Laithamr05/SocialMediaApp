import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.io.File;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class UserManagerUI {
    private TableView<UserManager> userTable;
    private CircularDoublyLinkedList<UserManager> users;
    private FileManager fileManager;
    private WelcomePage welcomePage;
    private ComboBox<String> sortOrderComboBox;

    public UserManagerUI(CircularDoublyLinkedList<UserManager> users, FileManager fileManager, WelcomePage welcomePage) {
        this.users = users;
        this.fileManager = fileManager;
        this.welcomePage = welcomePage;
        this.userTable = new TableView<>();
    }

    public Tab createUserTab() {
        VBox userContent = new VBox(20);
        userContent.setPadding(new Insets(20));
        userContent.setAlignment(Pos.CENTER);

        userTable = new TableView<>();
        userTable.setPrefHeight(400);
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            return user != null ? new SimpleStringProperty(user.getUserID()) : new SimpleStringProperty("");
        });
        idColumn.setPrefWidth(100);
        
        idColumn.setComparator((id1, id2) -> {
            try {
                return Integer.compare(Integer.parseInt(id1), Integer.parseInt(id2));
            } catch (NumberFormatException e) {
                return id1.compareTo(id2);
            }
        });

        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            return user != null ? new SimpleStringProperty(user.getName()) : new SimpleStringProperty("");
        });
        nameColumn.setPrefWidth(200);

        TableColumn<UserManager, String> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            return user != null ? new SimpleStringProperty(user.getAge()) : new SimpleStringProperty("");
        });
        ageColumn.setPrefWidth(100);

        userTable.getColumns().addAll(idColumn, nameColumn, ageColumn);

        userTable.setPlaceholder(new Label("No content in table"));

        refreshUserTable();

        HBox tableNavBox = new HBox(10);
        tableNavBox.setAlignment(Pos.CENTER);
        Button prevButton = new Button("◀ Previous");
        prevButton.setPrefWidth(100);
        prevButton.setOnAction(e -> navigateToPreviousUser());
        Button nextButton = new Button("Next ▶");
        nextButton.setPrefWidth(100);
        nextButton.setOnAction(e -> navigateToNextUser());
        tableNavBox.getChildren().addAll(prevButton, nextButton);

        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.setAlignment(Pos.CENTER);
        Button addUserButton = new Button("Add User");
        addUserButton.setPrefWidth(100);
        addUserButton.setOnAction(e -> showAddUserDialog());
        Button editUserButton = new Button("Edit User");
        editUserButton.setPrefWidth(100);
        editUserButton.setOnAction(e -> showEditUserDialog());
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

        userContent.getChildren().addAll(userTable, tableNavBox, actionButtonsBox);

        Tab userTab = new Tab("Users", userContent);
        userTab.setClosable(false);
        return userTab;
    }

    private TableView<UserManager> createUserTable() {
        TableView<UserManager> table = new TableView<>();
        
        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            return user != null ? new SimpleStringProperty(user.getUserID()) : new SimpleStringProperty("");
        });
        idColumn.setPrefWidth(100);
        
        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            return user != null ? new SimpleStringProperty(user.getName()) : new SimpleStringProperty("");
        });
        nameColumn.setPrefWidth(200);
        
        TableColumn<UserManager, String> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData -> {
            UserManager user = cellData.getValue();
            return user != null ? new SimpleStringProperty(user.getAge()) : new SimpleStringProperty("");
        });
        ageColumn.setPrefWidth(100);
        
        table.getColumns().addAll(idColumn, nameColumn, ageColumn);
        
        refreshUserTable();
        
        return table;
    }

    void showAddUserDialog() {
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
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();
                    return null;
                }

                try {
                    LocalDate birthDate = birthDatePicker.getValue();
                    LocalDate now = LocalDate.now();
                    
                    int age = Period.between(birthDate, now).getYears();
                    
                    if (age < 16) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Invalid Age");
                        alert.setContentText("User must be at least 16 years old.");
                        alert.showAndWait();
                        return null;
                    }

                    String newID = String.valueOf(users.size() + 1);
                    String name = nameField.getText();
                    String ageStr = String.valueOf(age);

                    UserManager newUser = new UserManager(newID, name, ageStr);
                    users.insertLast(newUser);
                    updateUserTable();
                    return newUser;
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please enter valid information: " + e.getMessage());
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait();
    }

    void showEditUserDialog() {
        UserManager selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No User Selected");
            alert.setContentText("Please select a user to edit.");
            alert.showAndWait();
            return;
        }

        Dialog<UserManager> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit User Details");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selectedUser.getName());
        
        DatePicker birthDatePicker = new DatePicker();
        birthDatePicker.setPromptText("Birth Date");
        
        int currentAge = Integer.parseInt(selectedUser.getAge());
        LocalDate approximateBirthDate = LocalDate.now().minusYears(currentAge);
        birthDatePicker.setValue(approximateBirthDate);
        
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
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please fill in all fields.");
                    alert.showAndWait();
                    return null;
                }
                
                try {
                    LocalDate birthDate = birthDatePicker.getValue();
                    LocalDate now = LocalDate.now();
                    
                    int age = Period.between(birthDate, now).getYears();
                    
                    if (age < 16) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText("Invalid Age");
                        alert.setContentText("User must be at least 16 years old.");
                        alert.showAndWait();
                        return null;
                    }
                
                    selectedUser.setName(nameField.getText());
                    selectedUser.setAge(String.valueOf(age));
                    return selectedUser;
                } catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Input");
                    alert.setContentText("Please enter valid information: " + e.getMessage());
                    alert.showAndWait();
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            Alert confirmAlert = new Alert(Alert.AlertType.INFORMATION);
            confirmAlert.setTitle("Success");
            confirmAlert.setHeaderText("User Updated");
            confirmAlert.setContentText("User " + user.getName() + " has been successfully updated.");
            confirmAlert.showAndWait();
            
            userTable.refresh();
            
            if (welcomePage != null) {
                TabPane tabPane = welcomePage.getTabPane();
                if (tabPane != null) {
                    int tabCount = tabPane.getTabs().size();
                    for (int i = 0; i < tabCount; i++) {
                        Tab tab = tabPane.getTabs().get(i);
                        if (tab.getText().equals("Friendships")) {
                            javafx.scene.Node content = tab.getContent();
                            if (content instanceof VBox) {
                                VBox vbox = (VBox) content;
                                int childCount = vbox.getChildren().size();
                                for (int j = 0; j < childCount; j++) {
                                    javafx.scene.Node node = vbox.getChildren().get(j);
                                    if (node instanceof TableView) {
                                        TableView<UserManager> friendshipTable = (TableView<UserManager>) node;
                                        friendshipTable.refresh();
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        });
    }

    private void handleUserFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select User Data File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                fileManager.loadUsers(selectedFile.getAbsolutePath(), users);
                
                int renamedUsers = removeDuplicateUsers();
                
                updateUserTable();
                
                String successMessage = "Users have been successfully uploaded from the file.";
                if (renamedUsers > 0) {
                    successMessage += "\nAssigned new IDs to " + renamedUsers + " duplicate user(s).";
                }
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("File Uploaded");
                alert.setContentText(successMessage);
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Upload Failed");
                alert.setContentText("Error reading the file: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    private int removeDuplicateUsers() {
        int duplicatesRenamed = 0;
        
        java.util.ArrayList<String> seenUserIds = new java.util.ArrayList<String>();
        java.util.ArrayList<UserManager> usersToRename = new java.util.ArrayList<UserManager>();
        
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                String userId = user.getUserID();
                
                if (seenUserIds.contains(userId)) {
                    usersToRename.add(user);
                    duplicatesRenamed++;
                } else {
                    seenUserIds.add(userId);
                }
            }
        }
        
        int highestId = 0;
        iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            if (user != null) {
                try {
                    int id = Integer.parseInt(user.getUserID());
                    if (id > highestId) {
                        highestId = id;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        
        for (int i = 0; i < usersToRename.size(); i++) {
            UserManager userToRename = usersToRename.get(i);
            String newId = String.valueOf(highestId + i + 1);
            
            userToRename.setUserID(newId);
            
            seenUserIds.add(newId);
        }
        
        return duplicatesRenamed;
    }

    public void updateUserTable() {
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            userList.add(user);
        }
        
        userTable.setItems(userList);
    }

    public TableView<UserManager> getUserTable() {
        return userTable;
    }

    private void navigateToPreviousUser() {
        int currentIndex = userTable.getSelectionModel().getSelectedIndex();
        if (currentIndex > 0) {
            userTable.getSelectionModel().select(currentIndex - 1);
            userTable.scrollTo(currentIndex - 1);
        } else if (userTable.getItems().size() > 0) {
            userTable.getSelectionModel().select(userTable.getItems().size() - 1);
            userTable.scrollTo(userTable.getItems().size() - 1);
        }
    }
    
    private void navigateToNextUser() {
        int currentIndex = userTable.getSelectionModel().getSelectedIndex();
        if (currentIndex < userTable.getItems().size() - 1) {
            userTable.getSelectionModel().select(currentIndex + 1);
            userTable.scrollTo(currentIndex + 1);
        } else if (userTable.getItems().size() > 0) {
            userTable.getSelectionModel().select(0);
            userTable.scrollTo(0);
        }
    }

    private void deleteSelectedUser() {
        UserManager selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Selection");
            alert.setHeaderText("No User Selected");
            alert.setContentText("Please select a user to delete.");
            alert.showAndWait();
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete User: " + selectedUser.getName());
        confirmAlert.setContentText("Are you sure you want to delete this user? This action cannot be undone.");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            users.delete(selectedUser);
            
            updateUserTable();
            
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("User Deleted");
            successAlert.setHeaderText(null);
            successAlert.setContentText("User " + selectedUser.getName() + " has been successfully deleted.");
            successAlert.showAndWait();
        }
    }

    private void saveUsers() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Users Data");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("updated_users.txt");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ObservableList<UserManager> userList = getUsersWithSortOrder();
                
                fileManager.saveUsers(file.getAbsolutePath(), userList);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Data Saved");
                alert.setContentText("Users data has been successfully saved to " + file.getName());
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Save Failed");
                alert.setContentText("Error saving the data: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    private void savePosts() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Posts Data");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("updated_posts.txt");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                CircularDoublyLinkedList<PostManager> posts = welcomePage.getPosts();
                
                fileManager.savePosts(file.getAbsolutePath(), posts, getUsersWithSortOrder());
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Data Saved");
                alert.setContentText("Posts data has been successfully saved to " + file.getName());
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Save Failed");
                alert.setContentText("Error saving the data: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    private void saveFriendships() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Friendships Data");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("updated_friendships.txt");
        
        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            try {
                ObservableList<UserManager> userList = getUsersWithSortOrder();
                
                fileManager.saveFriendships(file.getAbsolutePath(), userList);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("Data Saved");
                alert.setContentText("Friendships data has been successfully saved to " + file.getName());
                alert.showAndWait();
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Save Failed");
                alert.setContentText("Error saving the data: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    private ObservableList<UserManager> getUsersWithSortOrder() {
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            userList.add(user);
        }
        
        String sortSelection = sortOrderComboBox.getValue();
        
        if (sortSelection.equals("Ascending by Username")) {
            Collections.sort(userList, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager u1, UserManager u2) {
                    return u1.getName().compareTo(u2.getName());
                }
            });
        } else if (sortSelection.equals("Descending by Username")) {
            Collections.sort(userList, new Comparator<UserManager>() {
                @Override
                public int compare(UserManager u1, UserManager u2) {
                    return u2.getName().compareTo(u1.getName());
                }
            });
        }
        
        return userList;
    }

    private void refreshUserTable() {
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Iterator<UserManager> iterator = users.iterator();
        while (iterator.hasNext()) {
            userList.add(iterator.next());
        }
        userTable.setItems(userList);
    }
} 
