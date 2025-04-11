import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ReportManager {
    private CircularDoublyLinkedList<UserManager> userDatabase;
    private CircularDoublyLinkedList<PostManager> postDatabase;

    public ReportManager(CircularDoublyLinkedList<UserManager> userDatabase, CircularDoublyLinkedList<PostManager> postDatabase) {
        this.userDatabase = userDatabase;
        this.postDatabase = postDatabase;
    }

    private void showReport(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
        if (user == null || user.getFriends() == null) {
            return 0;
        }
        
        int count = 0;
        Iterator<UserManager> iterator = user.getFriends().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        
        return count;
    }
    
    public boolean hasPostedSince(UserManager user, Calendar date) {
        if (user == null || postDatabase == null || postDatabase.isEmpty()) {
            return false;
        }
        
        Iterator<PostManager> iterator = postDatabase.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post.getCreator() != null && post.getCreator().getUserID() == user.getUserID()) {
                if (post.getCreationDate().after(date)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    public Calendar getLastActivityDate(UserManager user) {
        if (user == null || postDatabase == null || postDatabase.isEmpty()) {
            return null;
        }
        
        Calendar lastActivity = null;
        
        Iterator<PostManager> iterator = postDatabase.iterator();
        while (iterator.hasNext()) {
            PostManager post = iterator.next();
            if (post.getCreator() != null && post.getCreator().getUserID() == user.getUserID()) {
                if (lastActivity == null || post.getCreationDate().after(lastActivity)) {
                    lastActivity = post.getCreationDate();
                }
            }
        }
        
        return lastActivity;
    }
    
    public String generateMostActiveUsersReport(int limit) {
        ObservableList<UserManager> activeUsers = getMostActiveUsersWithMostPosts(limit);
        
        String report = "MOST ACTIVE USERS REPORT\n";
        report += "------------------------\n\n";
        
        if (activeUsers.isEmpty()) {
            report += "No active users found.";
            return report;
        }
        
        report += "Top " + activeUsers.size() + " users with most posts:\n\n";
        
        for (int i = 0; i < activeUsers.size(); i++) {
            UserManager user = activeUsers.get(i);
            int postCount = countPostsByUser(user);
            
            report += (i + 1) + ". " + user.getName() + "\n";
            report += "   User ID: " + user.getUserID() + "\n";
            report += "   Posts: " + postCount + "\n";
            report += "   Friends: " + countUserFriends(user) + "\n";
            if (i < activeUsers.size() - 1) {
                report += "\n";
            }
        }
        
        return report;
    }
    
    public String generateRecentActivityReport(int limit) {
        ObservableList<UserManager> recentUsers = getUsersActiveInLastThreeWeeks(limit);
        
        String report = "RECENT USER ACTIVITY REPORT\n";
        report += "--------------------------\n\n";
        
        if (recentUsers.isEmpty()) {
            report += "No users with recent activity found.";
            return report;
        }
        
        report += "Users active in the past 3 weeks:\n\n";
        
        for (int i = 0; i < recentUsers.size(); i++) {
            UserManager user = recentUsers.get(i);
            Calendar lastActivity = getLastActivityDate(user);
            
            report += (i + 1) + ". " + user.getName() + "\n";
            report += "   User ID: " + user.getUserID() + "\n";
            
            if (lastActivity != null) {
                // Format the date
                int day = lastActivity.get(Calendar.DAY_OF_MONTH);
                int month = lastActivity.get(Calendar.MONTH) + 1;
                int year = lastActivity.get(Calendar.YEAR);
                report += "   Last activity: " + day + "/" + month + "/" + year + "\n";
            }
            
            report += "   Posts: " + countPostsByUser(user) + "\n";
            if (i < recentUsers.size() - 1) {
                report += "\n";
            }
        }
        
        return report;
    }
} 