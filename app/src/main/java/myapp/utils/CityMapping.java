package myapp.utils;

import java.util.HashMap;
import java.util.Map;

public class CityMapping {

    // <city, division>
    private Map<String, String> division = new HashMap<>();
    private Map<String, String> district = new HashMap<>();
    private String divisionCtg = "Chittagong";
    private String districtCumilla = "Comilla";

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

        // Comilla district
        division.put("Debidwar", divisionCtg);
        division.put("Barura", divisionCtg);
        division.put("Brahmanpara", divisionCtg);
        division.put("Chandina", divisionCtg);
        division.put("Chauddagram", divisionCtg);
        division.put("Daudkandi", divisionCtg);
        division.put("Homna", divisionCtg);
        division.put("Laksam", divisionCtg);
        division.put("Muradnagar", divisionCtg);
        division.put("Nangalkot", divisionCtg);
        division.put("Comilla Sadar", divisionCtg);
        division.put("Meghna", divisionCtg);
        division.put("Monohargonj", divisionCtg);
        division.put("Sadarsouth", divisionCtg);
        division.put("Titas", divisionCtg);
        division.put("Burichang", divisionCtg);
        division.put("Lalmai", divisionCtg);
        division.put("Comilla", divisionCtg);

        // Feni district
        division.put("Chhagalnaiya", divisionCtg);
        division.put("Feni Sadar", divisionCtg);
        division.put("Sonagazi", divisionCtg);
        division.put("Fulgazi", divisionCtg);
        division.put("Parshuram", divisionCtg);
        division.put("Daganbhuiyan", divisionCtg);

        // Brahmanbaria district
        division.put("Brahmanbaria Sadar", divisionCtg);
        division.put("Kasba", divisionCtg);
        division.put("Nasirnagar", divisionCtg);
        division.put("Sarail", divisionCtg);
        division.put("Ashuganj", divisionCtg);
        division.put("Akhaura", divisionCtg);
        division.put("Nabinagar", divisionCtg);
        division.put("Bancharampur", divisionCtg);
        division.put("Bijoynagar", divisionCtg);

        // Rangamati district
        division.put("Rangamati Sadar", divisionCtg);
        division.put("Kaptai", divisionCtg);
        division.put("Kawkhali", divisionCtg);
        division.put("Baghaichari", divisionCtg);
        division.put("Barkal", divisionCtg);
        division.put("Langadu", divisionCtg);
        division.put("Rajasthali", divisionCtg);
        division.put("Belaichari", divisionCtg);
        division.put("Juraichari", divisionCtg);
        division.put("Naniarchar", divisionCtg);

        // Ctg District
        division.put("Rangunia", divisionCtg);
        division.put("Sitakunda", divisionCtg);
        division.put("Mirsharai", divisionCtg);
        division.put("Patiya", divisionCtg);
        division.put("Sandwip", divisionCtg);
        division.put("Banshkhali", divisionCtg);
        division.put("Boalkhali", divisionCtg);
        division.put("Anwara", divisionCtg);
        division.put("Chandanaish", divisionCtg);
        division.put("Satkania", divisionCtg);
        division.put("Lohagara", divisionCtg);
        division.put("Hathazari", divisionCtg);
        division.put("Fatikchhari", divisionCtg);
        division.put("Raozan", divisionCtg);
        division.put("Karnafuli", divisionCtg);
        division.put("Chittagong", divisionCtg);
    }

    private void setDistrict(){
        // Ctg division
        division.put("Debidwar", districtCumilla);
        division.put("Barura", districtCumilla);
        division.put("Brahmanpara", districtCumilla);
        division.put("Chandina", districtCumilla);
        division.put("Chauddagram", districtCumilla);
        division.put("Daudkandi", districtCumilla);
        division.put("Homna", districtCumilla);
        division.put("Laksam", districtCumilla);
        division.put("Muradnagar", districtCumilla);
        division.put("Nangalkot", districtCumilla);
        division.put("Comilla Sadar", districtCumilla);
        division.put("Meghna", districtCumilla);
        division.put("Monohargonj", districtCumilla);
        division.put("Sadarsouth", districtCumilla);
        division.put("Titas", districtCumilla);
        division.put("Burichang", districtCumilla);
        division.put("Lalmai", districtCumilla);
        division.put("Comilla", districtCumilla);

        division.put("Chhagalnaiya", "Feni");
        division.put("Feni Sadar", "Feni");
        division.put("Sonagazi", "Feni");
        division.put("Fulgazi", "Feni");
        division.put("Parshuram", "Feni");
        division.put("Daganbhuiyan", "Feni");

        division.put("Brahmanbaria Sadar", "Brahmanbaria");
        division.put("Kasba", "Brahmanbaria");
        division.put("Nasirnagar", "Brahmanbaria");
        division.put("Sarail", "Brahmanbaria");
        division.put("Ashuganj", "Brahmanbaria");
        division.put("Akhaura", "Brahmanbaria");
        division.put("Nabinagar", "Brahmanbaria");
        division.put("Bancharampur", "Brahmanbaria");
        division.put("Bijoynagar", "Brahmanbaria");

        division.put("Rangamati Sadar", "Rangamati");
        division.put("Kaptai", "Rangamati");
        division.put("Kawkhali", "Rangamati");
        division.put("Baghaichari", "Rangamati");
        division.put("Barkal", "Rangamati");
        division.put("Langadu", "Rangamati");
        division.put("Rajasthali", "Rangamati");
        division.put("Belaichari", "Rangamati");
        division.put("Juraichari", "Rangamati");
        division.put("Naniarchar", "Rangamati");

        division.put("Rangunia", "Chittagong");
        division.put("Sitakunda", "Chittagong");
        division.put("Mirsharai", "Chittagong");
        division.put("Patiya", "Chittagong");
        division.put("Sandwip", "Chittagong");
        division.put("Banshkhali", "Chittagong");
        division.put("Boalkhali", "Chittagong");
        division.put("Anwara", "Chittagong");
        division.put("Chandanaish", "Chittagong");
        division.put("Satkania", "Chittagong");
        division.put("Lohagara", "Chittagong");
        division.put("Hathazari", "Chittagong");
        division.put("Fatikchhari", "Chittagong");
        division.put("Raozan", "Chittagong");
        division.put("Karnafuli", "Chittagong");
        division.put("Chittagong", "Chittagong");
    }
}
