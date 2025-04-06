import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class UserTableManager {
    public TableView<UserManager> createUserTable() {
        TableView<UserManager> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<UserManager, String> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserID()));
        idColumn.setPrefWidth(150);

        TableColumn<UserManager, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(200);

        TableColumn<UserManager, String> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAge()));
        ageColumn.setPrefWidth(100);

        table.getColumns().addAll(idColumn, nameColumn, ageColumn);
        return table;
    }
} 
