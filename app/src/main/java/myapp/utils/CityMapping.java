package myapp.utils;

import java.util.HashMap;
import java.util.Map;

public class CityMapping {

    // <city, division>
    private Map<String, String> division = new HashMap<>();
    private Map<String, String> district = new HashMap<>();

    public CityMapping(){
        setDivision();
        setDistrict();
    }

    public String getDivision(String city){
        return division.get(city);
    }
    public String getDistrict(String city){
        return district.get(city);
    }

    private void setDivision(){
        division.put("Chattogram", "Chattogram");
        division.put("Dhaka", "Dhaka");
        division.put("Cumilla", "Cumilla");
        division.put("Sylhet", "Sylhet");
        division.put("Rajshahi", "Rajshahi");
        division.put("Barishal", "Barishal");
    }

    private void setDistrict(){
        division.put("Chattogram", "Chattogram");
        division.put("Dhaka", "Dhaka");
        division.put("Cumilla", "Cumilla");
        division.put("Sylhet", "Sylhet");
        division.put("Rajshahi", "Rajshahi");
        division.put("Barishal", "Barishal");
    }
}
