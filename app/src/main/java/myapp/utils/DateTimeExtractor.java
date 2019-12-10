package myapp.utils;

import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.Date;

public class DateTimeExtractor {
    public static String[] months = {"Jan", "Feb", "March", "April", "May", "June",
            "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static String getDateTimeStringFull(Timestamp ts){
        if(ts == null) return null;
        Date dateObj = ts.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR);
        int minute = cal.get(Calendar.MINUTE);
        int am_pm = cal.get(Calendar.AM_PM);
        hour = (am_pm==1 && hour==0) ? 12 : hour;
        String yearString = Integer.toString(year);
        String monthString = months[month];
        String dateString = Integer.toString(date);
        String hourString = Integer.toString(hour);
        hourString = hourString.length()==1 ? "0"+hourString : hourString;
        String minuteString = Integer.toString(minute);
        minuteString = minuteString.length()==1 ? "0"+minuteString : minuteString;
        String amPmString = am_pm==0 ? "AM":"PM";
        return hourString + ":" + minuteString + " " + amPmString + ", "
                + dateString + " " + monthString + ", " + yearString;
    }

    public static String getDateOrTimeString(Timestamp ts){
        return getDateTimeStringFull(ts);
        //return getDateTimeStringSelective(ts);
    }

    public static String getDateTimeStringSelective(Timestamp ts){
        if(ts == null) return null;
        Date dateObj = ts.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);
        if(isDateToday(dateObj)){
            int hour = cal.get(Calendar.HOUR);
            int minute = cal.get(Calendar.MINUTE);
            int am_pm = cal.get(Calendar.AM_PM);
            hour = (am_pm==1 && hour==0) ? 12 : hour;
            String hourString = Integer.toString(hour);
            hourString = hourString.length()==1 ? "0"+hourString : hourString;
            String minuteString = Integer.toString(minute);
            minuteString = minuteString.length()==1 ? "0"+minuteString : minuteString;
            String amPmString = am_pm==0 ? "AM":"PM";
            return hourString + ":" + minuteString + " " + amPmString;
        }else{
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int date = cal.get(Calendar.DATE);
            String yearString = Integer.toString(year);
            String monthString = months[month];
            String dateString = Integer.toString(date);
            return dateString + " " + monthString + ", " + yearString;
        }
    }

    public static String getDateString(Timestamp ts){
        if(ts == null) return null;
        Date dateObj = ts.toDate();
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateObj);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int date = cal.get(Calendar.DATE);
        String yearString = Integer.toString(year);
        String monthString = months[month];
        String dateString = Integer.toString(date);
        return dateString + " " + monthString + ", " + yearString;
    }

    public static Boolean isDateToday(Date comparable){
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int nowDate = cal.get(Calendar.DATE);
        int nowMonth = cal.get(Calendar.MONTH);
        int nowYear = cal.get(Calendar.YEAR);

        cal.setTime(comparable);
        int comparableDate = cal.get(Calendar.DATE);
        int comparableMonth= cal.get(Calendar.MONTH);
        int comparableYear= cal.get(Calendar.YEAR);

        return nowDate==comparableDate && nowMonth==comparableMonth && nowYear==comparableYear;
    }
}
