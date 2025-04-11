// laith amro
// 1230018
// dr. mamoun nawahda
// section 7

public class CustomException extends RuntimeException { // custom exception class for application-specific errors
    public CustomException(String message) { // constructor that takes an error message
        super(message); // pass message to parent constructor
    }
} 
