import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Comparator;

public class ReportDisplayManager {
    private CircularDoublyLinkedList<UserManager> userDatabase;
    private CircularDoublyLinkedList<PostManager> postDatabase;
    private ReportManager analytics;
    private ReportDialogManager dialogManager;
    
    public ReportDisplayManager(CircularDoublyLinkedList<UserManager> userDatabase, 
                              CircularDoublyLinkedList<PostManager> postDatabase,
                              ReportManager analytics,
                              ReportDialogManager dialogManager) {
        this.userDatabase = userDatabase;
        this.postDatabase = postDatabase;
        this.analytics = analytics;
        this.dialogManager = dialogManager;
    }
    
    public void showCreatedPostsByUser(UserManager user) {
        if (user == null) {
            dialogManager.showNotification("No user selected");
            return;
        }
        
        ObservableList<PostManager> userPosts = analytics.getPostsByUser(user);
        
        StringBuilder resultText = new StringBuilder();
        resultText.append("=== POSTS CREATED BY ").append(user.getName().toUpperCase()).append(" ===\n\n");
        
        if (userPosts.isEmpty()) {
            resultText.append("No posts found for this user.");
        } else {
            resultText.append("Total posts: ").append(userPosts.size()).append("\n\n");
            
            for (int i = 0; i < userPosts.size(); i++) {
                PostManager post = userPosts.get(i);
                resultText.append("POST #").append(i + 1).append("\n");
                resultText.append("ID: ").append(post.getPostID()).append("\n");
                resultText.append("Content: ").append(post.getContent()).append("\n");
                resultText.append("Created on: ").append(dialogManager.formatDateString(post.getCreationDate())).append("\n");
                resultText.append("-----------------------------------\n");
            }
        }
        
        dialogManager.showNotification("Posts Created", resultText.toString());
    }

    public void showSharedPostsWithUser(UserManager user) {
        if (user == null) {
            dialogManager.showNotification("No user selected");
            return;
        }
        
        ObservableList<PostManager> sharedPosts = analytics.getPostsSharedWithUser(user);
        
        StringBuilder resultText = new StringBuilder();
        resultText.append("=== POSTS SHARED WITH ").append(user.getName().toUpperCase()).append(" ===\n\n");
        
        if (sharedPosts.isEmpty()) {
            resultText.append("No posts shared with this user.");
        } else {
            resultText.append("Total shared posts: ").append(sharedPosts.size()).append("\n\n");
            
            for (int i = 0; i < sharedPosts.size(); i++) {
                PostManager post = sharedPosts.get(i);
                resultText.append("POST #").append(i + 1).append("\n");
                resultText.append("ID: ").append(post.getPostID()).append("\n");
                resultText.append("Content: ").append(post.getContent()).append("\n");
                resultText.append("Created by: ").append(post.getCreator().getName()).append("\n");
                resultText.append("Created on: ").append(dialogManager.formatDateString(post.getCreationDate())).append("\n");
                resultText.append("-----------------------------------\n");
            }
        }
        
        dialogManager.showNotification("Posts Shared", resultText.toString());
    }

    public void showEngagementStats(UserManager user) {
        if (user == null) {
            dialogManager.showNotification("No user selected");
            return;
        }
        
        ObservableList<PostManager> createdPosts = analytics.getPostsByUser(user);
        ObservableList<PostManager> sharedPosts = analytics.getPostsSharedWithUser(user);
        
        String resultText = user.getName() + " has created " + createdPosts.size() + 
                          " post" + (createdPosts.size() != 1 ? "s" : "") + 
                          " and has " + sharedPosts.size() + 
                          " post" + (sharedPosts.size() != 1 ? "s" : "") + 
                          " shared with them";
        
        dialogManager.showNotification("Engagement Stats", resultText);
    }
    
    public void generateReport(String reportType, int limit) {
        switch (reportType) {
            case "Active Users":
                if (postDatabase.isEmpty()) {
                    dialogManager.showNotification("No Posts Available", "There are no posts in the system to generate this report.");
                    return;
                }
                String activeUsersReport = analytics.generateMostActiveUsersReport(limit);
                dialogManager.showNotification("Most Active Users Report", activeUsersReport);
                break;
                
            case "Users Active in Last 3 Weeks":
                ObservableList<UserManager> recentUsers = analytics.getUsersActiveInLastThreeWeeks(limit);
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
                        recentActivityReport.append("   Last active: ").append(dialogManager.formatDateString(analytics.getLastActivityDate(user))).append("\n");
                        recentActivityReport.append("   Posts: ").append(analytics.countPostsByUser(user)).append("\n");
                        if (i < recentUsers.size() - 1) {
                            recentActivityReport.append("\n");
                        }
                    }
                }
                dialogManager.showNotification("Recent Activity Report", recentActivityReport.toString());
                break;
        }
    }
    
    public void displayEngagementStatsReport(int limit) {
        ObservableList<UserManager> sortedUsers = getSortedUserList();
        if (sortedUsers.isEmpty()) {
            dialogManager.showNotification("No users available");
            return;
        }

        StringBuilder resultText = new StringBuilder("=== ENGAGEMENT STATS REPORT ===\n\n");
        int count = 0;
        
        for (int i = 0; i < sortedUsers.size() && count < limit; i++) {
            UserManager user = sortedUsers.get(i);
            
            ObservableList<PostManager> createdPosts = analytics.getPostsByUser(user);
            ObservableList<PostManager> sharedPosts = analytics.getPostsSharedWithUser(user);
            
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
        
        dialogManager.showNotification("Engagement Stats Report", resultText.toString());
    }
    
    public void exportCreatedPostsReport() {
        ObservableList<UserManager> sortedUserList = getSortedUserList();
        
        if (sortedUserList.isEmpty()) {
            dialogManager.showNotification("No users available to generate report");
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
                            
                            reportWriter.println("- Post ID: " + post.getPostID() + 
                                        ", Content: " + post.getContent() + 
                                        ", " + dialogManager.formatDateString(post.getCreationDate()) + 
                                        ", Shared With: " + (sharedWith.length() > 0 ? sharedWith : "None"));
                        }
                    }
                    reportWriter.println();
                }
                
                dialogManager.showNotification("Report Saved", "Posts Created Report has been successfully saved to " + outputFile.getName());
            } catch (IOException e) {
                dialogManager.showNotification("Error saving report: " + e.getMessage());
            }
        }
    }
    
    public void exportSharedPostsReport() {
        ObservableList<UserManager> sortedUserList = getSortedUserList();
        
        if (sortedUserList.isEmpty()) {
            dialogManager.showNotification("No users available to generate report");
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
                            reportWriter.println("- Post ID: " + post.getPostID() + 
                                        ", Content: " + post.getContent() + 
                                        ", " + dialogManager.formatDateString(post.getCreationDate()) + 
                                        ", Creator: " + post.getCreator().getName());
                        }
                    }
                    reportWriter.println();
                }
                
                dialogManager.showNotification("Report Saved", "Posts Shared Report has been successfully saved to " + outputFile.getName());
            } catch (IOException e) {
                dialogManager.showNotification("Error saving report: " + e.getMessage());
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
} 