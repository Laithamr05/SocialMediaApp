import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDate;
import java.time.Period;

public class UserManagerUI {
    private TableView<UserManager> userTable;
    private CircularDoublyLinkedList<UserManager> users;
    private FileManager fileManager;
    private WelcomePage welcomePage;

    public UserManagerUI(CircularDoublyLinkedList<UserManager> users, FileManager fileManager, WelcomePage welcomePage) {
        this.users = users;
        this.fileManager = fileManager;
        this.welcomePage = welcomePage;
        this.userTable = createUserTable();
    }

    public Tab createUserTab() {
        VBox userContent = new VBox(10);
        userContent.setPadding(new Insets(20));

        userTable.setPrefHeight(400);

        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);

        Button addUserButton = new Button("Add User");
        addUserButton.setOnAction(e -> showAddUserDialog());

        Button editUserButton = new Button("Edit User");
        editUserButton.setOnAction(e -> showEditUserDialog());

        Button uploadUserButton = new Button("Upload Users");
        uploadUserButton.setOnAction(e -> handleUserFileUpload());

        buttonBar.getChildren().addAll(addUserButton, editUserButton, uploadUserButton);

        userContent.getChildren().addAll(userTable, buttonBar);

        Tab userTab = new Tab("Users", userContent);
        userTab.setClosable(false);
        return userTab;
    }

    private TableView<UserManager> createUserTable() {
        TableView<UserManager> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserID()));
        idColumn.setPrefWidth(100);

        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(200);

        TableColumn<UserManager, String> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAge()));
        ageColumn.setPrefWidth(100);

        table.getColumns().addAll(idColumn, nameColumn, ageColumn);
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

                LocalDate birthDate = birthDatePicker.getValue();
                LocalDate now = LocalDate.now();
                int age = Period.between(birthDate, now).getYears();

                if (birthDate.isAfter(now)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Birth Date");
                    alert.setContentText("Birth date cannot be in the future.");
                    alert.showAndWait();
                    return null;
                }

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
        
        int age = Integer.parseInt(selectedUser.getAge());
        LocalDate birthDate = LocalDate.now().minusYears(age);
        
        DatePicker birthDatePicker = new DatePicker(birthDate);

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

                LocalDate newBirthDate = birthDatePicker.getValue();
                LocalDate now = LocalDate.now();

                if (newBirthDate.isAfter(now)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Birth Date");
                    alert.setContentText("Birth date cannot be in the future.");
                    alert.showAndWait();
                    return null;
                }

                int newAge = Period.between(newBirthDate, now).getYears();

                if (newAge < 16) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Age");
                    alert.setContentText("User must be at least 16 years old.");
                    alert.showAndWait();
                    return null;
                }

                selectedUser.setName(nameField.getText());
                selectedUser.setAge(String.valueOf(newAge));
                updateUserTable();
                return selectedUser;
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleUserFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select User Data File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                fileManager.readUsers(selectedFile.getAbsolutePath(), users);
                updateUserTable();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText("File Uploaded");
                alert.setContentText("Users have been successfully uploaded from the file.");
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

    public void updateUserTable() {
        ObservableList<UserManager> userList = FXCollections.observableArrayList();
        Node<UserManager> current = users.dummy.next;
        while (current != users.dummy) {
            userList.add(current.data);
            current = current.next;
        }
        userTable.setItems(userList);
        
       
    }

    public TableView<UserManager> getUserTable() {
        return userTable;
    }
} 
