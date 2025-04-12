// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

// Main application class that initializes and launches the social network manager
public class Main extends Application {
    // Initializes the JavaFX application and sets up the main window
    @Override
    public void start(Stage primaryStage) {
        // Create the main tab pane for navigation
        TabPane tabPane = new TabPane();
        Scene scene = new Scene(tabPane, 800, 600);

        // Initialize the welcome page and add it as the first tab
        WelcomePage welcomePage = new WelcomePage(tabPane);
        Tab welcomeTab = new Tab("Welcome", welcomePage.createWelcomePage());
        welcomeTab.setClosable(false);
        tabPane.getTabs().add(welcomeTab);

        // Configure and display the main window
        primaryStage.setTitle("Social Network Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Application entry point
    public static void main(String[] args) {
        launch(args);
    }
} 