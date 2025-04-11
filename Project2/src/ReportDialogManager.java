// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Alert.AlertType;
import java.util.Calendar;
import java.time.LocalDate;

public class ReportDialogManager { // class to manage dialog boxes for reports
    
    public void showNotification(String message) { // method to show simple notification with message
        Alert alert = new Alert(AlertType.INFORMATION); // create information alert
        alert.setTitle("Information"); // set alert title
        alert.setHeaderText(null); // no header text
        alert.setContentText(message); // set alert message
        alert.showAndWait(); // show alert and wait for user action
    }
    
    public void showNotification(String title, String message) { // method to show notification with custom title and scrollable message
        Alert dialog = new Alert(AlertType.INFORMATION); // create information alert
        dialog.setTitle(title); // set custom title
        dialog.setHeaderText(null); // no header text
        
        // Create a scrollable text area for the message
        TextArea textArea = new TextArea(message); // create text area with message
        textArea.setEditable(false); // make text area read-only
        textArea.setWrapText(true); // enable text wrapping
        textArea.setPrefHeight(300); // set preferred height
        textArea.setPrefWidth(500); // set preferred width
        
        // Add the text area to a scrollable pane
        ScrollPane scrollPane = new ScrollPane(textArea); // create scroll pane with text area
        scrollPane.setFitToWidth(true); // make content fit to width
        scrollPane.setFitToHeight(true); // make content fit to height
        
        // Add the scroll pane to the dialog
        dialog.getDialogPane().setContent(scrollPane); // set dialog content to scroll pane
        dialog.getDialogPane().setPrefSize(550, 350); // set dialog size
        dialog.setResizable(true); // allow resizing
        
        dialog.showAndWait(); // show dialog and wait for user action
    }

    public void showSimpleNotification(String title, String message) { // method to show simple notification with title and message
        Alert dialog = new Alert(AlertType.INFORMATION); // create information alert
        dialog.setTitle(title); // set custom title
        dialog.setHeaderText(null); // no header text
        dialog.setContentText(message); // set message
        
        dialog.getDialogPane().setPrefSize(400, 150); // set smaller dialog size
        
        dialog.showAndWait(); // show dialog and wait for user action
    }
    
    public String formatDateString(Calendar date) { // method to format Calendar date to string
        int day = date.get(Calendar.DAY_OF_MONTH); // get day
        int month = date.get(Calendar.MONTH) + 1; // get month (add 1 as Calendar months are zero-based)
        int year = date.get(Calendar.YEAR); // get year
        
        String dayStr; // string to hold formatted day
        if (day < 10) { // add leading zero if day is single digit
            dayStr = "0" + day;
        } else {
            dayStr = String.valueOf(day);
        }
        
        String monthStr; // string to hold formatted month
        if (month < 10) { // add leading zero if month is single digit
            monthStr = "0" + month;
        } else {
            monthStr = String.valueOf(month);
        }
        
        return dayStr + "." + monthStr + "." + year; // return formatted date string
    }
    
    public String formatDateString(LocalDate date) { // method to format LocalDate to string
        int day = date.getDayOfMonth(); // get day
        int month = date.getMonthValue(); // get month
        int year = date.getYear(); // get year
        
        String dayStr; // string to hold formatted day
        if (day < 10) { // add leading zero if day is single digit
            dayStr = "0" + day;
        } else {
            dayStr = String.valueOf(day);
        }
        
        String monthStr; // string to hold formatted month
        if (month < 10) { // add leading zero if month is single digit
            monthStr = "0" + month;
        } else {
            monthStr = String.valueOf(month);
        }
        
        return dayStr + "." + monthStr + "." + year; // return formatted date string
    }
} 