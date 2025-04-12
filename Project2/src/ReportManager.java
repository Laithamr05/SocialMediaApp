import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;

// Manages report generation and analytics for the social network application
public class ReportManager {
    // Data structures for storing users and posts
    private CircularDoublyLinkedList<UserManager> userDatabase;
    private CircularDoublyLinkedList<PostManager> postDatabase;
    private Label selectedUserLabel;
    private int currentUserIndex = 0;

    // Creates a new report manager with the given user and post data
    public ReportManager(CircularDoublyLinkedList<UserManager> userDatabase, CircularDoublyLinkedList<PostManager> postDatabase) {
        this.userDatabase = userDatabase;
        this.postDatabase = postDatabase;
    }
    
    public ReportManager(CircularDoublyLinkedList<UserManager> userDatabase, CircularDoublyLinkedList<PostManager> postDatabase, Label selectedUserLabel) {
        this.userDatabase = userDatabase;
        this.postDatabase = postDatabase;
        this.selectedUserLabel = selectedUserLabel;
        initializeUserDisplay();
    }

    // ===== Report Dialog Methods (from ReportDialogManager) =====
    
    public void showNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    public void showSimpleNotification(String title, String message) {
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        
        dialog.getDialogPane().setPrefSize(400, 150);
        
        dialog.showAndWait();
    }
    
    public String formatDateString(Calendar date) {
        if (date == null) {
            return "N/A";
        }
        
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
    
    // ===== Report Navigation Methods (from ReportNavigationManager) =====
    
    public void initializeUserDisplay() {
        if (selectedUserLabel != null) {
            selectedUserLabel.setText("No users available");
        }
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
        if (currentUser != null && selectedUserLabel != null) {
            selectedUserLabel.setText(currentUser.getName());
        } else if (selectedUserLabel != null) {
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
    
    public Label getSelectedUserLabel() {
        return selectedUserLabel;
    }
    
    public void setSelectedUserLabel(Label label) {
        this.selectedUserLabel = label;
    }
    
    // ===== Report Display Methods (from ReportDisplayManager) =====
    
    public void showCreatedPostsByUser(UserManager user) {
        if (user == null) {
            showNotification("No user selected");
            return;
        }
        
        ObservableList<PostManager> userPosts = getPostsByUser(user);
        String resultText = "=== POSTS CREATED BY " + user.getName().toUpperCase() + " ===\n\n";
        
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

    public void showSharedPostsWithUser(UserManager user) {
        if (user == null) {
            showNotification("No user selected");
            return;
        }
        
        ObservableList<PostManager> sharedPosts = getPostsSharedWithUser(user);
        String resultText = "=== POSTS SHARED WITH " + user.getName().toUpperCase() + " ===\n\n";
        
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

    public void showEngagementStats(UserManager user) {
        if (user == null) {
            showNotification("No user selected");
            return;
        }
        
        ObservableList<PostManager> createdPosts = getPostsByUser(user);
        ObservableList<PostManager> sharedPosts = getPostsSharedWithUser(user);
        
        String resultText = user.getName() + " has created " + createdPosts.size() + 
                          " post" + (createdPosts.size() != 1 ? "s" : "") + 
                          " and has " + sharedPosts.size() + 
                          " post" + (sharedPosts.size() != 1 ? "s" : "") + 
                          " shared with them";
        
        showNotification("Engagement Stats", resultText);
    }
    
    public void generateReport(String reportType, int limit) {
        switch (reportType) {
            case "Active Users":
                if (postDatabase.isEmpty()) {
                    showNotification("No Posts Available", "There are no posts in the system to generate this report.");
                    return;
                }
                String activeUsersReport = generateMostActiveUsersReport(limit);
                showNotification("Most Active Users Report", activeUsersReport);
                break;
                
            case "Users Active in Last 3 Weeks":
                ObservableList<UserManager> recentUsers = getUsersActiveInLastThreeWeeks(limit);
                StringBuilder recentActivityReport = new StringBuilder();
                recentActivityReport.append("USERS ACTIVE IN LAST 3 WEEKS\n");
                recentActivityReport.append("---------------------------\n\n");
                
                if (recentUsers.isEmpty()) {
                    recentActivityReport.append("No users have been active in the last 3 weeks.");
                } else {
                    recentActivityReport.append("Top ").append(recentUsers.size()).append(" recently active users:\n\n");
                    for (int i = 0; i < recentUsers.size(); i++) {
                        UserManager user = recentUsers.get(i);
                        recentActivityReport.append(i + 1).append(". ").append(user.getName()).append("\n");
                        recentActivityReport.append("   User ID: ").append(user.getUserID()).append("\n");
                        recentActivityReport.append("   Last active: ").append(formatDateString(getLastActivityDate(user))).append("\n");
                        recentActivityReport.append("   Posts: ").append(countPostsByUser(user)).append("\n");
                        if (i < recentUsers.size() - 1) {
                            recentActivityReport.append("\n");
                        }
                    }
                }
                showNotification("Recent Activity Report", recentActivityReport.toString());
                break;
        }
    }
    
    public void displayEngagementStatsReport(int limit) {
        ObservableList<UserManager> sortedUsers = getSortedUserList();
        if (sortedUsers.isEmpty()) {
            showNotification("No users available");
            return;
        }

        StringBuilder resultText = new StringBuilder("=== ENGAGEMENT STATS REPORT ===\n\n");
        int count = 0;
        
        for (int i = 0; i < sortedUsers.size() && count < limit; i++) {
            UserManager user = sortedUsers.get(i);
            
            ObservableList<PostManager> createdPosts = getPostsByUser(user);
            ObservableList<PostManager> sharedPosts = getPostsSharedWithUser(user);
            
            resultText.append(user.getName()).append(":\n");
            resultText.append("  - Created ").append(createdPosts.size()).append(" post");
            if (createdPosts.size() != 1) {
                resultText.append("s");
            }
            resultText.append("\n");
            resultText.append("  - Has ").append(sharedPosts.size()).append(" post");
            if (sharedPosts.size() != 1) {
                resultText.append("s");
            }
            resultText.append(" shared with them\n");
            resultText.append("-----------------------------------\n");
            count++;
        }
        
        showNotification("Engagement Stats Report", resultText.toString());
    }
    
    public void exportCreatedPostsReport() {
        ObservableList<UserManager> sortedUserList = getSortedUserList();
        
        if (sortedUserList.isEmpty()) {
            showNotification("No users available to generate report");
            return;
        }
        
        FileChooser fileSelector = new FileChooser();
        fileSelector.setTitle("Save Posts Created Report");
        fileSelector.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
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
                    
                    ObservableList<PostManager> userPosts = getPostsByUser(user);
                    reportWriter.println("Total Posts: " + userPosts.size());
                    
                    if (!userPosts.isEmpty()) {
                        reportWriter.println();
                        for (int j = 0; j < userPosts.size(); j++) {
                            PostManager post = userPosts.get(j);
                            reportWriter.println("  Post #" + (j + 1) + ":");
                            reportWriter.println("  - ID: " + post.getPostID());
                            reportWriter.println("  - Content: " + post.getContent());
                            reportWriter.println("  - Created on: " + formatDateString(post.getCreationDate()));
                            reportWriter.println();
                        }
                    }
                    
                    reportWriter.println("-----------------------------------");
                    reportWriter.println();
                }
                
                showNotification("Success", "Posts created report was exported successfully to:\n" + outputFile.getAbsolutePath());
            } catch (IOException e) {
                showNotification("Error", "Failed to export posts report: " + e.getMessage());
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
        fileSelector.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileSelector.setInitialFileName("posts_shared.txt");
        
        File outputFile = fileSelector.showSaveDialog(null);
        if (outputFile != null) {
            try (PrintWriter reportWriter = new PrintWriter(new FileWriter(outputFile))) {
                reportWriter.println("Posts Shared Report");
                reportWriter.println("------------------");
                reportWriter.println();
                
                for (int i = 0; i < sortedUserList.size(); i++) {
                    UserManager user = sortedUserList.get(i);
                    reportWriter.println("User: " + user.getName());
                    
                    ObservableList<PostManager> sharedPosts = getPostsSharedWithUser(user);
                    reportWriter.println("Total Shared Posts: " + sharedPosts.size());
                    
                    if (!sharedPosts.isEmpty()) {
                        reportWriter.println();
                        for (int j = 0; j < sharedPosts.size(); j++) {
                            PostManager post = sharedPosts.get(j);
                            reportWriter.println("  Post #" + (j + 1) + ":");
                            reportWriter.println("  - ID: " + post.getPostID());
                            reportWriter.println("  - Content: " + post.getContent());
                            reportWriter.println("  - Created by: " + post.getCreator().getName());
                            reportWriter.println("  - Created on: " + formatDateString(post.getCreationDate()));
                            reportWriter.println();
                        }
                    }
                    
                    reportWriter.println("-----------------------------------");
                    reportWriter.println();
                }
                
                showNotification("Success", "Posts shared report was exported successfully to:\n" + outputFile.getAbsolutePath());
            } catch (IOException e) {
                showNotification("Error", "Failed to export posts report: " + e.getMessage());
            }
        }
    }
    
    public ObservableList<UserManager> getSortedUserList() {
        ObservableList<UserManager> sortedUserList = FXCollections.observableArrayList();
        
        Iterator<UserManager> iterator = userDatabase.iterator();
        while (iterator.hasNext()) {
            sortedUserList.add(iterator.next());
        }
        
        sortedUserList.sort((user1, user2) -> user1.getName().compareTo(user2.getName()));
        
        return sortedUserList;
    }

    // ===== Original ReportManager methods =====

    public void showReport(String title, String content) {
        showNotification(title, content);
    }

    public ObservableList<PostManager> getPostsByUser(UserManager user) {
        ObservableList<PostManager> userPosts = FXCollections.observableArrayList();
        
        if (postDatabase == null || postDatabase.isEmpty()) {
            return userPosts;
        }
        
        Iterator<PostManager> iterator = postDatabase.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post.getCreator() != null && post.getCreator().getUserID() == user.getUserID()) {
                userPosts.add(post);
            }
        }
        
        return userPosts;
    }
    
    public ObservableList<PostManager> getPostsSharedWithUser(UserManager user) {
        ObservableList<PostManager> sharedPosts = FXCollections.observableArrayList();
        
        if (postDatabase == null || postDatabase.isEmpty()) {
            return sharedPosts;
        }
        
        Iterator<PostManager> iterator = postDatabase.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            
            // Skip posts created by this user (since we only want ones shared WITH them)
            if (post.getCreator() != null && post.getCreator().getUserID() == user.getUserID()) {
                continue;
            }
            
            // Check if post is shared with this user
            CircularDoublyLinkedList<UserManager> sharedUsers = post.getSharedUsers();
            if (sharedUsers != null && !sharedUsers.isEmpty()) {
                Iterator<UserManager> sharedIterator = sharedUsers.iterator();
                while (sharedIterator.hasNext()) {
                    UserManager sharedUser = sharedIterator.next();
                    if (sharedUser.getUserID() == user.getUserID()) {
                        sharedPosts.add(post);
                        break;
                    }
                }
            }
        }
        
        return sharedPosts;
    }

    public ObservableList<UserManager> getMostActiveUserWithMostPosts() {
        ObservableList<UserManager> result = FXCollections.observableArrayList();
        
        if (userDatabase.isEmpty() || postDatabase.isEmpty()) {
            return result;
        }
        
        UserManager mostActiveUser = null;
        int maxPostCount = 0;
        
        Iterator<UserManager> iterator = userDatabase.iterator();
        while (iterator.hasNext()) {
            UserManager user = iterator.next();
            int postCount = countPostsByUser(user);
            
            if (postCount > maxPostCount) {
                maxPostCount = postCount;
                mostActiveUser = user;
            }
        }
        
        if (mostActiveUser != null && maxPostCount > 0) {
            result.add(mostActiveUser);
        }
        
        return result;
    }
    
    public ObservableList<UserManager> getMostActiveUsersWithMostPosts(int limit) {
        ObservableList<UserManager> sortedUsers = FXCollections.observableArrayList();
        
        if (userDatabase == null || userDatabase.isEmpty()) {
            return sortedUsers;
        }
        
        // First collect all users
        Iterator<UserManager> iterator = userDatabase.iterator();
        while (iterator.hasNext()) {
            sortedUsers.add(iterator.next());
        }
        
        // Sort them by post count (descending)
        sortedUsers.sort((user1, user2) -> {
            int postCount1 = countPostsByUser(user1);
            int postCount2 = countPostsByUser(user2);
            return Integer.compare(postCount2, postCount1); // Descending order
        });
        
        // Limit the results
        if (limit > 0 && limit < sortedUsers.size()) {
            return FXCollections.observableArrayList(sortedUsers.subList(0, limit));
        }
        
        return sortedUsers;
    }
    
    public int countPostsByUser(UserManager user) {
        if (user == null) {
            return 0;
        }
        
        return getPostsByUser(user).size();
    }
    
    public ObservableList<UserManager> getUsersActiveInLastThreeWeeks() {
        return getUsersActiveInLastThreeWeeks(Integer.MAX_VALUE);
    }
    
    public ObservableList<UserManager> getUsersActiveInLastThreeWeeks(int limit) {
        ObservableList<UserManager> activeUsers = FXCollections.observableArrayList();
        
        if (userDatabase == null || userDatabase.isEmpty() || postDatabase == null || postDatabase.isEmpty()) {
            return activeUsers;
        }
        
        // Calculate date 3 weeks ago
        Calendar threeWeeksAgo = Calendar.getInstance();
        threeWeeksAgo.add(Calendar.WEEK_OF_YEAR, -3);
        
        // Track users who have posted recently
        Iterator<UserManager> userIterator = userDatabase.iterator();
        while (userIterator.hasNext()) {
            UserManager user = userIterator.next();
            if (hasPostedSince(user, threeWeeksAgo)) {
                activeUsers.add(user);
            }
        }
        
        // Sort by most recent activity
        activeUsers.sort((user1, user2) -> {
            Calendar lastActivity1 = getLastActivityDate(user1);
            Calendar lastActivity2 = getLastActivityDate(user2);
            if (lastActivity1 == null) return 1;
            if (lastActivity2 == null) return -1;
            return lastActivity2.compareTo(lastActivity1); // Most recent first
        });
        
        // Limit the results
        if (limit > 0 && limit < activeUsers.size()) {
            return FXCollections.observableArrayList(activeUsers.subList(0, limit));
        }
        
        return activeUsers;
    }
    
    public String getUserEngagementMetrics(UserManager user) {
        if (user == null) {
            return "No user selected";
        }
        
        int postsCreated = getPostsByUser(user).size();
        int postsReceived = getPostsSharedWithUser(user).size();
        int totalFriends = countUserFriends(user);
        
        return "User: " + user.getName() + "\n" +
               "Posts created: " + postsCreated + "\n" +
               "Posts received: " + postsReceived + "\n" +
               "Friends: " + totalFriends;
    }
    
    public int countUserFriends(UserManager user) {
        if (user == null) {
            return 0;
        }
        
        CircularDoublyLinkedList<UserManager> friends = user.getFriends();
        if (friends == null) {
            return 0;
        }
        
        return friends.size();
    }
    
    public boolean hasPostedSince(UserManager user, Calendar date) {
        if (user == null || date == null) {
            return false;
        }
        
        ObservableList<PostManager> userPosts = getPostsByUser(user);
        for (int i = 0; i < userPosts.size(); i++) {
            PostManager post = userPosts.get(i);
            if (post.getCreationDate().after(date)) {
                return true;
            }
        }
        
        return false;
    }
    
    public Calendar getLastActivityDate(UserManager user) {
        if (user == null) {
            return null;
        }
        
        Calendar mostRecent = null;
        ObservableList<PostManager> userPosts = getPostsByUser(user);
        
        for (int i = 0; i < userPosts.size(); i++) {
            PostManager post = userPosts.get(i);
            Calendar postDate = post.getCreationDate();
            if (mostRecent == null || postDate.after(mostRecent)) {
                mostRecent = postDate;
            }
        }
        
        return mostRecent;
    }
    
    public String generateMostActiveUsersReport(int limit) {
        ObservableList<UserManager> activeUsers = getMostActiveUsersWithMostPosts(limit);
        
        String report = "MOST ACTIVE USERS REPORT\n";
        report += "-----------------------\n\n";
        
        if (activeUsers.isEmpty()) {
            report += "No active users found.";
        } else {
            report += "Top " + activeUsers.size() + " most active users:\n\n";
            for (int i = 0; i < activeUsers.size(); i++) {
                UserManager user = activeUsers.get(i);
                int postCount = countPostsByUser(user);
                report += (i + 1) + ". " + user.getName() + "\n";
                report += "   User ID: " + user.getUserID() + "\n";
                report += "   Posts: " + postCount + "\n";
                if (i < activeUsers.size() - 1) {
                    report += "\n";
                }
            }
        }
        
        return report;
    }
    
    public String generateRecentActivityReport(int limit) {
        ObservableList<UserManager> recentUsers = getUsersActiveInLastThreeWeeks(limit);
        
        String report = "RECENT ACTIVITY REPORT\n";
        report += "---------------------\n\n";
        
        if (recentUsers.isEmpty()) {
            report += "No users have been active in the last 3 weeks.";
        } else {
            report += "Top " + recentUsers.size() + " recently active users:\n\n";
            for (int i = 0; i < recentUsers.size(); i++) {
                UserManager user = recentUsers.get(i);
                report += (i + 1) + ". " + user.getName() + "\n";
                report += "   User ID: " + user.getUserID() + "\n";
                report += "   Last active: " + formatDateString(getLastActivityDate(user)) + "\n";
                report += "   Posts: " + countPostsByUser(user) + "\n";
                if (i < recentUsers.size() - 1) {
                    report += "\n";
                }
            }
        }
        
        return report;
    }

    public String generateUserActivityReport(UserManager user) {
        if (user == null) {
            return "Invalid user";
        }

        String resultText = "=== USER ACTIVITY REPORT ===\n\n";
        resultText += "User: " + user.getName() + " (ID: " + user.getUserID() + ")\n\n";

        CircularDoublyLinkedList<PostManager> createdPosts = new CircularDoublyLinkedList<>();
        CircularDoublyLinkedList<PostManager> sharedPosts = new CircularDoublyLinkedList<>();

        Iterator<PostManager> iterator = postDatabase.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post != null && post.getCreator().equals(user)) {
                createdPosts.insertLast(post);
            }
            if (post != null && post.getSharedUsers().contains(user)) {
                sharedPosts.insertLast(post);
            }
        }

        resultText += "Created " + createdPosts.size() + " post" + (createdPosts.size() != 1 ? "s" : "") + "\n";
        resultText += "Shared " + sharedPosts.size() + " post" + (sharedPosts.size() != 1 ? "s" : "") + "\n\n";

        resultText += "Created Posts:\n";
        Iterator<PostManager> createdIterator = createdPosts.iterator();
        while (createdIterator.hasNext()) {
            PostManager post = createdIterator.next();
            if (post != null) {
                resultText += "- " + post.getContent() + " (" + formatDateString(post.getCreationDate()) + ")\n";
            }
        }

        resultText += "\nShared Posts:\n";
        Iterator<PostManager> sharedIterator = sharedPosts.iterator();
        while (sharedIterator.hasNext()) {
            PostManager post = sharedIterator.next();
            if (post != null) {
                resultText += "- " + post.getContent() + " by " + post.getCreator().getName() + " (" + formatDateString(post.getCreationDate()) + ")\n";
            }
        }

        return resultText;
    }

    public String generateEngagementStatsReport() {
        String resultText = "=== ENGAGEMENT STATS REPORT ===\n\n";

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

                resultText += user.getName() + ":\n";
                resultText += "- Created " + postCount + " post" + (postCount != 1 ? "s" : "") + "\n";
                resultText += "- Shared " + shareCount + " post" + (shareCount != 1 ? "s" : "") + "\n\n";
            }
        }

        return resultText;
    }

    // laith amro
    // 1230018
    // dr. mamoun nawahda
    // section 7

    // Returns a list of users sorted by post count
    public ArrayList<UserManager> getMostActiveUsers() {
        ArrayList<UserManager> users = new ArrayList<>();
        Iterator<UserManager> userIterator = userDatabase.iterator();
        while (userIterator.hasNext()) {
            users.add(userIterator.next());
        }
        
        Collections.sort(users, (u1, u2) -> {
            int count1 = countPostsByUser(u1);
            int count2 = countPostsByUser(u2);
            return Integer.compare(count2, count1);
        });
        
        return users;
    }

    // Returns a list of users sorted by friend count
    public ArrayList<UserManager> getMostConnectedUsers() {
        ArrayList<UserManager> users = new ArrayList<>();
        Iterator<UserManager> userIterator = userDatabase.iterator();
        while (userIterator.hasNext()) {
            users.add(userIterator.next());
        }
        
        Collections.sort(users, (u1, u2) -> {
            int count1 = u1.getFriends().size();
            int count2 = u2.getFriends().size();
            return Integer.compare(count2, count1);
        });
        
        return users;
    }

    // Returns a list of users sorted by shared post count
    public ArrayList<UserManager> getMostEngagedUsers() {
        ArrayList<UserManager> users = new ArrayList<>();
        Iterator<UserManager> userIterator = userDatabase.iterator();
        while (userIterator.hasNext()) {
            users.add(userIterator.next());
        }
        
        Collections.sort(users, (u1, u2) -> {
            int count1 = getPostsSharedWithUser(u1).size();
            int count2 = getPostsSharedWithUser(u2).size();
            return Integer.compare(count2, count1);
        });
        
        return users;
    }

    // Returns a list of users who have no friends
    public ArrayList<UserManager> getIsolatedUsers() {
        ArrayList<UserManager> isolatedUsers = new ArrayList<>();
        Iterator<UserManager> userIterator = userDatabase.iterator();
        while (userIterator.hasNext()) {
            UserManager user = userIterator.next();
            if (user.getFriends().isEmpty()) {
                isolatedUsers.add(user);
            }
        }
        return isolatedUsers;
    }

    // Returns a list of users who have not created any posts
    public ArrayList<UserManager> getInactiveUsers() {
        ArrayList<UserManager> inactiveUsers = new ArrayList<>();
        Iterator<UserManager> userIterator = userDatabase.iterator();
        while (userIterator.hasNext()) {
            UserManager user = userIterator.next();
            if (countPostsByUser(user) == 0) {
                inactiveUsers.add(user);
            }
        }
        return inactiveUsers;
    }

    // Returns a list of users who have not shared any posts
    public ArrayList<UserManager> getUnengagedUsers() {
        ArrayList<UserManager> unengagedUsers = new ArrayList<>();
        Iterator<UserManager> userIterator = userDatabase.iterator();
        while (userIterator.hasNext()) {
            UserManager user = userIterator.next();
            if (getPostsSharedWithUser(user).isEmpty()) {
                unengagedUsers.add(user);
            }
        }
        return unengagedUsers;
    }

    // Returns a list of users and their mutual friend counts with a specific user
    public ArrayList<UserManager> getMutualFriendCounts(UserManager targetUser) {
        if (targetUser == null) return new ArrayList<>();
        ArrayList<UserManager> users = new ArrayList<>();
        Iterator<UserManager> userIterator = userDatabase.iterator();
        while (userIterator.hasNext()) {
            UserManager user = userIterator.next();
            if (!user.equals(targetUser)) {
                users.add(user);
            }
        }
        
        Collections.sort(users, (u1, u2) -> {
            int count1 = countMutualFriends(u1, targetUser);
            int count2 = countMutualFriends(u2, targetUser);
            return Integer.compare(count2, count1);
        });
        
        return users;
    }

    // Returns the number of mutual friends between two users
    public int countMutualFriends(UserManager user1, UserManager user2) {
        if (user1 == null || user2 == null) return 0;
        int count = 0;
        Iterator<UserManager> friendIterator = user1.getFriends().iterator();
        while (friendIterator.hasNext()) {
            UserManager friend = friendIterator.next();
            if (user2.getFriends().contains(friend)) {
                count++;
            }
        }
        return count;
    }
} 