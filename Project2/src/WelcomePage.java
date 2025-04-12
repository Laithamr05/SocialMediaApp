// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import java.io.File;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

// Manages the welcome page and navigation of the social network application
public class WelcomePage {
	// Main navigation container
	private TabPane tabPane;
	// Data structures for storing users and posts
	private CircularDoublyLinkedList<UserManager> users;
	private CircularDoublyLinkedList<PostManager> posts;
	// Welcome page content container
	private VBox welcomeContent;

	// Creates a new welcome page with the given tab pane
	public WelcomePage(TabPane tabPane) {
		this.tabPane = tabPane;
		this.users = new CircularDoublyLinkedList<>();
		this.posts = new CircularDoublyLinkedList<>();

		welcomeContent = new VBox(20);
		welcomeContent.setPadding(new Insets(20));
		welcomeContent.setAlignment(Pos.CENTER);
	}

	// Returns the welcome page content container
	public VBox getWelcomeContent() {
		return welcomeContent;
	}

	// Creates and returns the welcome page layout with logo and navigation buttons
	public VBox createWelcomePage() {
		// Load and display the application logo
		ImageView logoView = new ImageView();
		try {
			Image logo = new Image(new File("src/PalestineSocial.png").toURI().toString());
			logoView.setImage(logo);
			logoView.setFitWidth(300);
			logoView.setFitHeight(200);
			logoView.setPreserveRatio(true);
		} catch (Exception e) {
			// Error loading logo - silently ignored
		}

		// Create and style the welcome label
		Label welcomeLabel = new Label("Welcome to Palestine Social Network");
		welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		// Create a container for navigation buttons
		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		
		// Create and configure the Users button
		Button usersButton = new Button("Manage Users");
		usersButton.setOnAction(e -> {
			if (tabPane != null) {
				tabPane.getSelectionModel().select(1);
			}
		});
		
		// Create and configure the Friendships button
		Button friendsButton = new Button("Manage Friendships");
		friendsButton.setOnAction(e -> {
			if (tabPane != null) {
				tabPane.getSelectionModel().select(2);
			}
		});
		
		// Create and configure the Posts button
		Button postsButton = new Button("Manage Posts");
		postsButton.setOnAction(e -> {
			if (tabPane != null) {
				tabPane.getSelectionModel().select(3);
			}
		});
		
		// Create and configure the Reports button
		Button reportsButton = new Button("Reports & Statistics");
		reportsButton.setOnAction(e -> {
			if (tabPane != null) {
				tabPane.getSelectionModel().select(4);
			}
		});
		
		// Create and configure the Data Management button
		Button dataManagementButton = new Button("Data Management");
		dataManagementButton.setOnAction(e -> {
			if (tabPane != null) {
				tabPane.getSelectionModel().select(5);
			}
		});
		
		// Add all buttons to the button container
		buttonBox.getChildren().addAll(usersButton, friendsButton, postsButton, reportsButton, dataManagementButton);

		// Add all components to the welcome page
		welcomeContent.getChildren().addAll(logoView, welcomeLabel, buttonBox);

		return welcomeContent;
	}

	// Sets the list of users
	public void setUsers(CircularDoublyLinkedList<UserManager> users) {
		this.users = users;
	}

	// Returns the list of users
	public CircularDoublyLinkedList<UserManager> getUsers() {
		return users;
	}

	// Sets the list of posts
	public void setPosts(CircularDoublyLinkedList<PostManager> posts) {
		this.posts = posts;
	}

	// Returns the list of posts
	public CircularDoublyLinkedList<PostManager> getPosts() {
		return posts;
	}

	// Returns the main tab pane
	public TabPane getTabPane() {
		return tabPane;
	}
}
