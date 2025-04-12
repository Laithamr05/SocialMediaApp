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
	private ReportManager reportManager;
	private Tab statisticsTab;
	private ComboBox<String> reportTypeSelector;
	private TextField userLimitField;
	private Label selectedUserLabel;

	public ReportManagerUI(CircularDoublyLinkedList<UserManager> userDatabase,
			CircularDoublyLinkedList<PostManager> postDatabase) {
		this.userDatabase = userDatabase;
		this.postDatabase = postDatabase;
		this.selectedUserLabel = new Label("No users available");
		this.reportManager = new ReportManager(userDatabase, postDatabase, selectedUserLabel);
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
		prevButton.setOnAction(e -> reportManager.selectPreviousUser());

		Button nextButton = new Button("Next ▶");
		nextButton.setPrefWidth(100);
		nextButton.setOnAction(e -> reportManager.selectNextUser());

		selectedUserLabel.setStyle("-fx-font-weight: bold;");

		userNavBox.getChildren().addAll(prevButton, selectedUserLabel, nextButton);

		return userNavBox;
	}

	public HBox createActionButtons() {
		HBox actionButtonsBox = new HBox(10);
		actionButtonsBox.setAlignment(Pos.CENTER);

		Button viewCreatedButton = new Button("View Created Posts");
		viewCreatedButton.setPrefWidth(150);
		viewCreatedButton.setOnAction(e -> reportManager.showCreatedPostsByUser(reportManager.getCurrentUser()));

		Button viewSharedButton = new Button("View Shared Posts");
		viewSharedButton.setPrefWidth(150);
		viewSharedButton.setOnAction(e -> reportManager.showSharedPostsWithUser(reportManager.getCurrentUser()));

		Button viewStatsButton = new Button("View Engagement Stats");
		viewStatsButton.setPrefWidth(150);
		viewStatsButton.setOnAction(e -> reportManager.showEngagementStats(reportManager.getCurrentUser()));

		actionButtonsBox.getChildren().addAll(viewCreatedButton, viewSharedButton, viewStatsButton);

		return actionButtonsBox;
	}

	public void generateReport() {
		String reportType = reportTypeSelector.getValue();
		if (reportType == null || reportType.isEmpty()) {
			reportManager.showNotification("No report type selected", "Please select a report type.");
			return;
		}

		int limit;
		try {
			limit = Integer.parseInt(userLimitField.getText());
			if (limit <= 0) {
				reportManager.showNotification("Invalid limit", "Please enter a positive number for the user limit.");
				return;
			}
		} catch (NumberFormatException e) {
			reportManager.showNotification("Invalid input", "Please enter a valid number for the user limit.");
			return;
		}

		reportManager.generateReport(reportType, limit);
	}

	public void showCreatedPostsByUser() {
		UserManager selectedUser = reportManager.getCurrentUser();
		if (selectedUser == null) {
			showNotification("No users available");
			return;
		}

		ObservableList<PostManager> userPosts = reportManager.getPostsByUser(selectedUser);

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
		UserManager selectedUser = reportManager.getCurrentUser();
		if (selectedUser == null) {
			showNotification("No users available");
			return;
		}

		ObservableList<PostManager> sharedPosts = reportManager.getPostsSharedWithUser(selectedUser);

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
				resultText += "Creator: " + post.getCreator().getName() + "\n";
				resultText += "Created on: " + formatDateString(post.getCreationDate()) + "\n";
				resultText += "-----------------------------------\n";
			}
		}

		showNotification("Shared Posts", resultText);
	}

	public void showEngagementStats() {
		UserManager selectedUser = reportManager.getCurrentUser();
		if (selectedUser == null) {
			showNotification("No users available");
			return;
		}

		int postCount = reportManager.countPostsByUser(selectedUser);
		ObservableList<PostManager> sharedPosts = reportManager.getPostsSharedWithUser(selectedUser);
		int shareCount = sharedPosts.size();

		String resultText = "";
		resultText += "=== ENGAGEMENT STATS FOR " + selectedUser.getName().toUpperCase() + " ===\n\n";
		resultText += "Posts created: " + postCount + "\n";
		resultText += "Posts shared: " + shareCount + "\n";
		resultText += "Total engagement: " + (postCount + shareCount) + "\n";

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
			reportText = reportManager.generateMostActiveUsersReport(userLimit);
		} else {
			reportText = reportManager.generateRecentActivityReport(userLimit);
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

					ObservableList<PostManager> userPosts = reportManager.getPostsByUser(user);
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

					ObservableList<PostManager> sharedPosts = reportManager.getPostsSharedWithUser(user);
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

	private void showUserActivityReport(UserManager user) {
		if (user == null) {
			showNotification("Error", "Please select a user first");
			return;
		}

		CircularDoublyLinkedList<PostManager> createdPosts = new CircularDoublyLinkedList<>();
		CircularDoublyLinkedList<PostManager> sharedPosts = new CircularDoublyLinkedList<>();

		Iterator<PostManager> iterator = postDatabase.iterator();
		while (iterator.hasNext()) {
			PostManager post = iterator.next();
			if (post != null) {
				if (post.getCreator().equals(user)) {
					createdPosts.insertLast(post);
				}
				if (post.getSharedUsers().contains(user)) {
					sharedPosts.insertLast(post);
				}
			}
		}

		String pluralSuffixCreated;
		if (createdPosts.size() != 1) {
			pluralSuffixCreated = "s";
		} else {
			pluralSuffixCreated = "";
		}

		String pluralSuffixShared;
		if (sharedPosts.size() != 1) {
			pluralSuffixShared = "s";
		} else {
			pluralSuffixShared = "";
		}

		String reportText = user.getName() + " has created " + createdPosts.size() + " post" + pluralSuffixCreated + 
						  " and has " + sharedPosts.size() + " post" + pluralSuffixShared + " shared with him";

		showNotification("User Activity Report", reportText);
	}

	private void showEngagementStatsReport() {
		String reportText = "=== ENGAGEMENT STATS REPORT ===\n\n";

		Iterator<UserManager> userIterator = userDatabase.iterator();
		while (userIterator.hasNext()) {
			UserManager user = userIterator.next();
			if (user != null) {
				int postCount = 0;
				int shareCount = 0;

				Iterator<PostManager> postIterator = postDatabase.iterator();
				while (postIterator.hasNext()) {
					PostManager post = postIterator.next();
					if (post != null) {
						if (post.getCreator().equals(user)) {
							postCount++;
						}
						if (post.getSharedUsers().contains(user)) {
							shareCount++;
						}
					}
				}

				String pluralSuffixPosts;
				if (postCount != 1) {
					pluralSuffixPosts = "s";
				} else {
					pluralSuffixPosts = "";
				}

				String pluralSuffixShares;
				if (shareCount != 1) {
					pluralSuffixShares = "s";
				} else {
					pluralSuffixShares = "";
				}

				reportText += user.getName() + ":\n";
				reportText += "- Created " + postCount + " post" + pluralSuffixPosts + "\n";
				reportText += "- Shared " + shareCount + " post" + pluralSuffixShares + "\n\n";
			}
		}

		showNotification("Engagement Stats Report", reportText);
	}
}