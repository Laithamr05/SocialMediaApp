import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.control.TabPane;
import java.io.File;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.*;
import javafx.scene.paint.Color;

public class WelcomePage {
	private TabPane tabPane;
	private CircularDoublyLinkedList<UserManager> users;
	private CircularDoublyLinkedList<PostManager> posts;

	public WelcomePage(TabPane tabPane) {
		this.tabPane = tabPane;
		this.users = new CircularDoublyLinkedList<>();
		this.posts = new CircularDoublyLinkedList<>();
	}

	public VBox createWelcomePage() {
		VBox welcomeContent = new VBox(20);
		welcomeContent.setPadding(new Insets(20));
		welcomeContent.setAlignment(Pos.CENTER);

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

		Label welcomeLabel = new Label("Welcome to Palestine Social Network");
		welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

		HBox buttonBox = new HBox(10);
		buttonBox.setAlignment(Pos.CENTER);
		
		Button usersButton = new Button("Manage Users");
		usersButton.setOnAction(e -> {
			if (tabPane != null) {
				tabPane.getSelectionModel().select(1);
			}
		});
		
		Button postsButton = new Button("Manage Posts");
		postsButton.setOnAction(e -> {
			if (tabPane != null) {
				tabPane.getSelectionModel().select(2);
			}
		});
		
		Button friendsButton = new Button("Manage Friendships");
		friendsButton.setOnAction(e -> {
			if (tabPane != null) {
				tabPane.getSelectionModel().select(3);
			}
		});
		
		Button reportsButton = new Button("Reports & Statistics");
		reportsButton.setOnAction(e -> {
			if (tabPane != null) {
				tabPane.getSelectionModel().select(4);
			}
		});
		
		buttonBox.getChildren().addAll(usersButton, postsButton, friendsButton, reportsButton);

		welcomeContent.getChildren().addAll(logoView, welcomeLabel, buttonBox);

		return welcomeContent;
	}

	public void setUsers(CircularDoublyLinkedList<UserManager> users) {
		this.users = users;
	}

	public CircularDoublyLinkedList<UserManager> getUsers() {
		return users;
	}

	public void setPosts(CircularDoublyLinkedList<PostManager> posts) {
		this.posts = posts;
	}

	public CircularDoublyLinkedList<PostManager> getPosts() {
		return posts;
	}

	public Tab createWelcomeTab() {
		VBox welcomeContent = new VBox(30);
		welcomeContent.setAlignment(Pos.CENTER);
		welcomeContent.setPadding(new Insets(50, 20, 20, 20));
		
		try {
			Image logo = new Image(getClass().getResourceAsStream("/PalestineSocial.png"));
			ImageView logoView = new ImageView(logo);
			logoView.setFitWidth(300);
			logoView.setPreserveRatio(true);
			
			welcomeContent.getChildren().add(logoView);
		} catch (Exception e) {
			// Failed to load logo - silently ignored
		}
		
		Text welcomeText = new Text("Welcome to Palestine Social Network");
		welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 24));
		welcomeText.setFill(Color.GREEN);
		
		Text instructionText = new Text("Please use the tabs above to navigate the application.");
		instructionText.setFont(Font.font("Arial", 16));
		
		welcomeContent.getChildren().addAll(welcomeText, instructionText);
		
		Tab welcomeTab = new Tab("Welcome", welcomeContent);
		welcomeTab.setClosable(false);
		return welcomeTab;
	}
}
