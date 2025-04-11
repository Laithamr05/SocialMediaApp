import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class ReportManagerUI {
	private CircularDoublyLinkedList<UserManager> userDatabase;
	private CircularDoublyLinkedList<PostManager> postDatabase;
	private ReportManager analytics;
	private Tab statisticsTab;
	private ComboBox<String> reportTypeSelector;
	private TextField userLimitField;

	private Label selectedUserLabel;
	private int currentUserIndex = 0;

	private ReportDialogManager dialogManager;
	private ReportDisplayManager displayManager;
	private ReportNavigationManager navigationManager;

	public ReportManagerUI(CircularDoublyLinkedList<UserManager> userDatabase,
			CircularDoublyLinkedList<PostManager> postDatabase) {
		this.userDatabase = userDatabase;
		this.postDatabase = postDatabase;
		this.analytics = new ReportManager(userDatabase, postDatabase);
		this.selectedUserLabel = new Label("No users available");

		// Initialize manager components
		this.dialogManager = new ReportDialogManager();
		this.displayManager = new ReportDisplayManager(userDatabase, postDatabase, analytics, dialogManager);
		this.navigationManager = new ReportNavigationManager(userDatabase, selectedUserLabel, dialogManager);
	}

	public void selectPreviousUser() {
		if (userDatabase.isEmpty()) {
			showNotification("No users available");
			return;
		}

		currentUserIndex--;
		if (currentUserIndex < 0) {
			currentUserIndex = userDatabase.size() - 1;
		}

		updateUserDisplay();
	}

	public void selectNextUser() {
		if (userDatabase.isEmpty()) {
			showNotification("No users available");
			return;
		}

		currentUserIndex++;
		if (currentUserIndex >= userDatabase.size()) {
			currentUserIndex = 0;
		}

		updateUserDisplay();
	}

	public void updateUserDisplay() {
		UserManager currentUser = getCurrentUser();
		if (currentUser != null) {
			selectedUserLabel.setText(currentUser.getName());
		} else {
			selectedUserLabel.setText("No users available");
		}
	}

	public UserManager getCurrentUser() {
		if (userDatabase.isEmpty() || currentUserIndex < 0) {
			return null;
		}

		Iterator<UserManager> iterator = userDatabase.iterator();
		int count = 0;
		while (iterator.hasNext()) {
			UserManager user = iterator.next();
			if (count == currentUserIndex) {
				return user;
			}
			count++;
		}
		return null;
	}

	public Tab createReportsTab() {
		VBox reportsContent = new VBox(10);
		reportsContent.setAlignment(Pos.CENTER);
		reportsContent.setPadding(new Insets(20));

		// Set up report controls
		HBox reportTypeBox = createReportSelectionControls();
		HBox userLimitBox = createUserLimitControls();
		Button generateReportButton = createGenerateReportButton();

		// Set up user navigation
		HBox userNavBox = createUserNavigationControls();

		// Set up action buttons for reports
		HBox actionButtonsBox = createActionButtons();

		reportsContent.getChildren().addAll(reportTypeBox, userLimitBox, generateReportButton, userNavBox,
				actionButtonsBox);

		Tab reportsTab = new Tab("Reports", reportsContent);
		reportsTab.setClosable(false);
		return reportsTab;
	}

	public HBox createReportSelectionControls() {
		HBox reportTypeBox = new HBox(10);
		reportTypeBox.setAlignment(Pos.CENTER);

		Label reportTypeLabel = new Label("Report Type:");
		reportTypeSelector = new ComboBox<>();
		reportTypeSelector.getItems().addAll("Active Users", "Users Active in Last 3 Weeks");
		reportTypeSelector.setPrefWidth(200);

		reportTypeBox.getChildren().addAll(reportTypeLabel, reportTypeSelector);

		return reportTypeBox;
	}

	public HBox createUserLimitControls() {
		HBox userLimitBox = new HBox(10);
		userLimitBox.setAlignment(Pos.CENTER);

		Label userLimitLabel = new Label("User Limit:");
		userLimitField = new TextField();
		userLimitField.setPrefWidth(100);

		userLimitBox.getChildren().addAll(userLimitLabel, userLimitField);

		return userLimitBox;
	}

	public Button createGenerateReportButton() {
		Button generateReportButton = new Button("Generate Report");
		generateReportButton.setPrefWidth(150);
		generateReportButton.setOnAction(e -> generateReport());

		return generateReportButton;
	}

	public HBox createUserNavigationControls() {
		HBox userNavBox = new HBox(10);
		userNavBox.setAlignment(Pos.CENTER);

		Button prevButton = new Button("◀ Previous");
		prevButton.setPrefWidth(100);
		prevButton.setOnAction(e -> navigationManager.selectPreviousUser());

		Button nextButton = new Button("Next ▶");
		nextButton.setPrefWidth(100);
		nextButton.setOnAction(e -> navigationManager.selectNextUser());

		selectedUserLabel.setStyle("-fx-font-weight: bold;");

		userNavBox.getChildren().addAll(prevButton, selectedUserLabel, nextButton);

		return userNavBox;
	}

	public HBox createActionButtons() {
		HBox actionButtonsBox = new HBox(10);
		actionButtonsBox.setAlignment(Pos.CENTER);

		Button viewCreatedButton = new Button("View Created Posts");
		viewCreatedButton.setPrefWidth(150);
		viewCreatedButton.setOnAction(e -> displayManager.showCreatedPostsByUser(navigationManager.getCurrentUser()));

		Button viewSharedButton = new Button("View Shared Posts");
		viewSharedButton.setPrefWidth(150);
		viewSharedButton.setOnAction(e -> displayManager.showSharedPostsWithUser(navigationManager.getCurrentUser()));

		Button viewStatsButton = new Button("View Engagement Stats");
		viewStatsButton.setPrefWidth(150);
		viewStatsButton.setOnAction(e -> displayManager.showEngagementStats(navigationManager.getCurrentUser()));

		actionButtonsBox.getChildren().addAll(viewCreatedButton, viewSharedButton, viewStatsButton);

		return actionButtonsBox;
	}

	public void generateReport() {
		String selectedType = reportTypeSelector.getValue();
		if (selectedType == null) {
			dialogManager.showNotification("Please select a report type");
			return;
		}

		try {
			int limit = Integer.parseInt(userLimitField.getText());
			if (limit <= 0) {
				dialogManager.showNotification("Please enter a positive number for the user limit");
				return;
			}

			displayManager.generateReport(selectedType, limit);
		} catch (NumberFormatException e) {
			dialogManager.showNotification("Please enter a valid number for the user limit");
		}
	}

	public void showCreatedPostsByUser() {
		UserManager selectedUser = getCurrentUser();
		if (selectedUser == null) {
			showNotification("No users available");
			return;
		}

		ObservableList<PostManager> userPosts = analytics.getPostsByUser(selectedUser);

		String resultText = "";
		resultText += "=== POSTS CREATED BY " + selectedUser.getName().toUpperCase() + " ===\n\n";

		if (userPosts.isEmpty()) {
			resultText += "No posts found for this user.";
		} else {
			resultText += "Total posts: " + userPosts.size() + "\n\n";

			for (int i = 0; i < userPosts.size(); i++) {
				PostManager post = userPosts.get(i);
				resultText += "POST #" + (i + 1) + "\n";
				resultText += "ID: " + post.getPostID() + "\n";
				resultText += "Content: " + post.getContent() + "\n";
				resultText += "Created on: " + formatDateString(post.getCreationDate()) + "\n";
				resultText += "-----------------------------------\n";
			}
		}

		showNotification("Posts Created", resultText);
	}

	public void showSharedPostsWithUser() {
		UserManager selectedUser = getCurrentUser();
		if (selectedUser == null) {
			showNotification("No users available");
			return;
		}

		ObservableList<PostManager> sharedPosts = analytics.getPostsSharedWithUser(selectedUser);

		String resultText = "";
		resultText += "=== POSTS SHARED WITH " + selectedUser.getName().toUpperCase() + " ===\n\n";

		if (sharedPosts.isEmpty()) {
			resultText += "No posts shared with this user.";
		} else {
			resultText += "Total shared posts: " + sharedPosts.size() + "\n\n";

			for (int i = 0; i < sharedPosts.size(); i++) {
				PostManager post = sharedPosts.get(i);
				resultText += "POST #" + (i + 1) + "\n";
				resultText += "ID: " + post.getPostID() + "\n";
				resultText += "Content: " + post.getContent() + "\n";
				resultText += "Created by: " + post.getCreator().getName() + "\n";
				resultText += "Created on: " + formatDateString(post.getCreationDate()) + "\n";
				resultText += "-----------------------------------\n";
			}
		}

		showNotification("Posts Shared", resultText);
	}

	public void showEngagementStats() {
		UserManager selectedUser = getCurrentUser();
		if (selectedUser == null) {
			showNotification("No users available");
			return;
		}

		ObservableList<PostManager> createdPosts = analytics.getPostsByUser(selectedUser);
		ObservableList<PostManager> sharedPosts = analytics.getPostsSharedWithUser(selectedUser);

		String resultText = selectedUser.getName() + " has created " + createdPosts.size() + " post"
				+ (createdPosts.size() != 1 ? "s" : "") + " and has " + sharedPosts.size() + " post"
				+ (sharedPosts.size() != 1 ? "s" : "") + " shared with him";

		showNotification("Engagement Stats", resultText);
	}

	public void displayActiveUsersReport() {
		String selectedOption = reportTypeSelector.getValue();
		boolean showMostPosts = selectedOption.equals("User with Most Posts");

		int userLimit = 5;
		try {
			String limitText = userLimitField.getText().trim();
			if (!limitText.isEmpty()) {
				userLimit = Integer.parseInt(limitText);
				userLimit = Math.max(1, userLimit);
			}
		} catch (NumberFormatException e) {
		}

		String reportText;

		if (showMostPosts) {
			if (postDatabase.isEmpty()) {
				showNotification("No Posts Available", "There are no posts in the system to generate this report.");
				return;
			}
			reportText = analytics.generateMostActiveUsersReport(userLimit);
		} else {
			reportText = analytics.generateRecentActivityReport(userLimit);
		}

		showNotification(showMostPosts ? "Most Active Users" : "Recent Activity", reportText);
	}

	public void showNotification(String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public void showSimpleNotification(String title, String message) {
		Alert dialog = new Alert(Alert.AlertType.INFORMATION);
		dialog.setTitle(title);
		dialog.setHeaderText(null);
		dialog.setContentText(message);

		dialog.getDialogPane().setPrefSize(400, 150);

		dialog.showAndWait();
	}

	public void showNotification(String title, String message) {
		Alert dialog = new Alert(Alert.AlertType.INFORMATION);
		dialog.setTitle(title);
		dialog.setHeaderText(null);

		// Create a scrollable text area for the message
		TextArea textArea = new TextArea(message);
		textArea.setEditable(false);
		textArea.setWrapText(true);
		textArea.setPrefHeight(300);
		textArea.setPrefWidth(500);

		// Add the text area to a scrollable pane
		ScrollPane scrollPane = new ScrollPane(textArea);
		scrollPane.setFitToWidth(true);
		scrollPane.setFitToHeight(true);

		// Add the scroll pane to the dialog
		dialog.getDialogPane().setContent(scrollPane);
		dialog.getDialogPane().setPrefSize(550, 350);
		dialog.setResizable(true);

		dialog.showAndWait();
	}

	public void exportCreatedPostsReport() {
		ObservableList<UserManager> sortedUserList = getSortedUserList();

		if (sortedUserList.isEmpty()) {
			showNotification("No users available to generate report");
			return;
		}

		FileChooser fileSelector = new FileChooser();
		fileSelector.setTitle("Save Posts Created Report");
		fileSelector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		fileSelector.setInitialFileName("posts_created.txt");

		File outputFile = fileSelector.showSaveDialog(null);
		if (outputFile != null) {
			try (PrintWriter reportWriter = new PrintWriter(new FileWriter(outputFile))) {
				reportWriter.println("Posts Created Report");
				reportWriter.println("-------------------");
				reportWriter.println();

				for (int i = 0; i < sortedUserList.size(); i++) {
					UserManager user = sortedUserList.get(i);
					reportWriter.println("User: " + user.getName());

					ObservableList<PostManager> userPosts = analytics.getPostsByUser(user);
					if (userPosts.isEmpty()) {
						reportWriter.println("No posts created by this user.");
					} else {
						for (int j = 0; j < userPosts.size(); j++) {
							PostManager post = userPosts.get(j);
							String sharedWith = "";
							CircularDoublyLinkedList<UserManager> sharedUsers = post.getSharedUsers();
							if (sharedUsers != null) {
								Iterator<UserManager> iterator = sharedUsers.iterator();
								while (iterator.hasNext()) {
									UserManager sharedUser = iterator.next();
									if (!sharedWith.isEmpty()) {
										sharedWith += ", ";
									}
									sharedWith += sharedUser.getName();
								}
							}

							reportWriter.println("- Post ID: " + post.getPostID() + ", Content: " + post.getContent()
									+ ", " + formatDateString(post.getCreationDate()) + ", Shared With: "
									+ (sharedWith.length() > 0 ? sharedWith : "None"));
						}
					}
					reportWriter.println();
				}

				showNotification("Report Saved",
						"Posts Created Report has been successfully saved to " + outputFile.getName());
			} catch (IOException e) {
				showNotification("Error saving report: " + e.getMessage());
			}
		}
	}

	public void exportSharedPostsReport() {
		ObservableList<UserManager> sortedUserList = getSortedUserList();

		if (sortedUserList.isEmpty()) {
			showNotification("No users available to generate report");
			return;
		}

		FileChooser fileSelector = new FileChooser();
		fileSelector.setTitle("Save Posts Shared Report");
		fileSelector.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
		fileSelector.setInitialFileName("posts_shared.txt");

		File outputFile = fileSelector.showSaveDialog(null);
		if (outputFile != null) {
			try (PrintWriter reportWriter = new PrintWriter(new FileWriter(outputFile))) {
				reportWriter.println("Posts Shared With User Report");
				reportWriter.println("---------------------------");
				reportWriter.println();

				for (int i = 0; i < sortedUserList.size(); i++) {
					UserManager user = sortedUserList.get(i);
					reportWriter.println("User: " + user.getName());

					ObservableList<PostManager> sharedPosts = analytics.getPostsSharedWithUser(user);
					if (sharedPosts.isEmpty()) {
						reportWriter.println("No posts shared with this user.");
					} else {
						for (int j = 0; j < sharedPosts.size(); j++) {
							PostManager post = sharedPosts.get(j);
							reportWriter.println("- Post ID: " + post.getPostID() + ", Content: " + post.getContent()
									+ ", " + formatDateString(post.getCreationDate()) + ", Creator: "
									+ post.getCreator().getName());
						}
					}
					reportWriter.println();
				}

				showNotification("Report Saved",
						"Posts Shared Report has been successfully saved to " + outputFile.getName());
			} catch (IOException e) {
				showNotification("Error saving report: " + e.getMessage());
			}
		}
	}

	public ObservableList<UserManager> getSortedUserList() {
		ObservableList<UserManager> sortedUsers = FXCollections.observableArrayList();

		Iterator<UserManager> iterator = userDatabase.iterator();
		while (iterator.hasNext()) {
			sortedUsers.add(iterator.next());
		}

		sortedUsers.sort(Comparator.comparing(UserManager::getName));

		return sortedUsers;
	}

	public String formatDateString(Calendar date) {
		int day = date.get(Calendar.DAY_OF_MONTH);
		int month = date.get(Calendar.MONTH) + 1;
		int year = date.get(Calendar.YEAR);

		String dayStr;
		if (day < 10) {
			dayStr = "0" + day;
		} else {
			dayStr = String.valueOf(day);
		}

		String monthStr;
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		return dayStr + "." + monthStr + "." + year;
	}

	public String formatDateString(LocalDate date) {
		int day = date.getDayOfMonth();
		int month = date.getMonthValue();
		int year = date.getYear();

		String dayStr;
		if (day < 10) {
			dayStr = "0" + day;
		} else {
			dayStr = String.valueOf(day);
		}

		String monthStr;
		if (month < 10) {
			monthStr = "0" + month;
		} else {
			monthStr = String.valueOf(month);
		}

		return dayStr + "." + monthStr + "." + year;
	}
}