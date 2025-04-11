// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.scene.control.Label;
import java.util.Iterator;

public class ReportNavigationManager {
    private CircularDoublyLinkedList<UserManager> userDatabase;
    private Label selectedUserLabel;
    private int currentUserIndex = 0;
    private ReportDialogManager dialogManager;
    
    public ReportNavigationManager(CircularDoublyLinkedList<UserManager> userDatabase, 
                                  Label selectedUserLabel,
                                  ReportDialogManager dialogManager) {
        this.userDatabase = userDatabase;
        this.selectedUserLabel = selectedUserLabel;
        this.dialogManager = dialogManager;
        initializeUserDisplay();
    }
    
    public void initializeUserDisplay() {
        if (selectedUserLabel != null) {
            selectedUserLabel.setText("No users available");
        }
    }
    
    public void selectPreviousUser() {
        if (userDatabase.isEmpty()) {
            dialogManager.showNotification("No users available");
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
            dialogManager.showNotification("No users available");
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
} 