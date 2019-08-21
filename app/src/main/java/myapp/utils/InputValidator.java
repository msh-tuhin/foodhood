package myapp.utils;

import android.util.Patterns;

public class InputValidator {
    public static boolean validateName(String name){
        return !name.trim().isEmpty();
    }

    public static boolean validateEmail(String email){
        // TODO maybe use a custom regex for email validation
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    public static boolean validatePassword(String password){
        if(password.length() < 6){
            return false;
        }
        return true;
    }

    public static boolean validatePhone(){
        return true;
    }

    public static boolean confirmPassword(String password, String passwordAgain){
        return password.equals(passwordAgain);
    }
}
