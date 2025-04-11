// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GUI extends Application {
	private CircularDoublyLinkedList<UserManager> users;
	private CircularDoublyLinkedList<PostManager> posts;
	private FileManager fileManager;
	private UserManagerUI userManagerUI;
	private PostManagerUI postManagerUI;
	private FriendshipManagerUI friendshipManagerUI;
	private ReportManagerUI reportManagerUI;
	private DataManagementUI dataManagementUI;
	private WelcomePage welcomePage;
	private TabPane tabPane;
	private BorderPane mainLayout;
	private MenuUIManager menuUIManager;
	private DataLoadingManager dataLoadingManager;

	@Override
	public void start(Stage primaryStage) {
		// Initialize data structures
		users = new CircularDoublyLinkedList<>();
		posts = new CircularDoublyLinkedList<>();
		fileManager = new FileManager();

		// Initialize managers
		dataLoadingManager = new DataLoadingManager(users, posts, fileManager);
		
		// Set up main layout
		mainLayout = new BorderPane();
		mainLayout.setPadding(new Insets(10));

		// Initialize TabPane
		tabPane = new TabPane();
		tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

		// Initialize UI components
		welcomePage = new WelcomePage(tabPane);
		welcomePage.setUsers(users);
		welcomePage.setPosts(posts);
		userManagerUI = new UserManagerUI(users, fileManager, welcomePage);
		postManagerUI = new PostManagerUI(posts, fileManager, users, welcomePage);
		friendshipManagerUI = new FriendshipManagerUI(users, fileManager);
		reportManagerUI = new ReportManagerUI(users, posts);
		dataManagementUI = new DataManagementUI(users, posts, fileManager);
		menuUIManager = new MenuUIManager(tabPane, users);

		// Add tabs to TabPane
		Tab welcomeTab = new Tab("Welcome", welcomePage.createWelcomePage());
		welcomeTab.setClosable(false);
		tabPane.getTabs().add(welcomeTab);

		tabPane.getTabs().add(userManagerUI.createUserTab());
		tabPane.getTabs().add(friendshipManagerUI.createFriendshipTab());
		tabPane.getTabs().add(postManagerUI.createPostTab());
		tabPane.getTabs().add(reportManagerUI.createReportsTab());
		tabPane.getTabs().add(dataManagementUI.createDataManagementTab());

		// Set up layout
		mainLayout.setCenter(tabPane);
		mainLayout.setTop(menuUIManager.createMenuBar());

		// Create scene and show stage
		Scene scene = new Scene(mainLayout, 800, 600);
		primaryStage.setTitle("Social Network Application");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
