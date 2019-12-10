package myapp.utils;

import android.util.Log;
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

    public static boolean validatePhone(String phoneNumber){
        phoneNumber = phoneNumber.replaceAll("[\\s-]", "");
        Log.i("phone", phoneNumber);
        String phoneValidationRegex = getPhoneValidationRegex(true);
        return phoneNumber.matches(phoneValidationRegex);
    }

    public static boolean confirmPassword(String password, String passwordAgain){
        return password.equals(passwordAgain);
    }

    private static String getPhoneValidationRegex(boolean onlyCellNumber){
        if(onlyCellNumber){
            return "(\\+88)?01[^02\\D][0-9]{8}";
        }else{
            return "(\\+88)?0" +
                    // mobile
                    "(1[^02\\D][0-9]{8}|" +
                    // btcl-dhaka
                    "2[0-9]{7,9}|" +
                    // btcl-others
                    "[3-9][0-9]{1,2}[0-9]{6,8}|" +
                    // ip telephony
                    "96[0-9]{2}[0-9]{6})";
        }
    }
}
