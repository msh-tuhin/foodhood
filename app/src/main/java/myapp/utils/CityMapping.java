package myapp.utils;

import java.util.HashMap;
import java.util.Map;

public class CityMapping {

    // <district, division>
    private Map<String, String> division = new HashMap<>();
    // <city, district>
    private Map<String, String> district = new HashMap<>();
    private String divisionCtg = "Chittagong";
    private String districtCumilla = "Comilla";
    private String districtCtg = "Chittagong";
    private String cityCtg = "Chattogram City";
    private String cityCumilla = "Cumilla City";

    public CityMapping(){
        setDivision();
        setDistrict();
    }

    public String getDivision(String city){
        return division.get(district.get(city));
    }
    public String getDistrict(String city){
        return district.get(city);
    }

    private void setDivision(){

        division.put(districtCumilla, divisionCtg);
        division.put("Feni", divisionCtg);
        division.put("Brahmanbaria", divisionCtg);
        division.put("Rangamati", divisionCtg);
        division.put(districtCtg, divisionCtg);
        division.put("Noakhali", divisionCtg);
        division.put("Chandpur", divisionCtg);
        division.put("Lakshmipur", divisionCtg);
        division.put("Coxsbazar", divisionCtg);
        division.put("Khagrachhari", divisionCtg);
        division.put("Bandarban", divisionCtg);

        division.put("Sirajganj", "Rajshahi");
        division.put("Pabna", "Rajshahi");
        division.put("Bogra", "Rajshahi");
        division.put("Rajshahi", "Rajshahi");
        division.put("Natore", "Rajshahi");
        division.put("Joypurhat", "Rajshahi");
        division.put("Chapainawabganj", "Rajshahi");
        division.put("Naogaon", "Rajshahi");

        division.put("Jessore", "Khulna");
        division.put("Satkhira", "Khulna");
        division.put("Meherpur", "Khulna");
        division.put("Narail", "Khulna");
        division.put("Chuadanga", "Khulna");
        division.put("Kushtia", "Khulna");
        division.put("Magura", "Khulna");
        division.put("Khulna", "Khulna");
        division.put("Bagerhat", "Khulna");
        division.put("Jhenaidah", "Khulna");

        division.put("Jhalakathi", "Barisal");
        division.put("Patuakhali", "Barisal");
        division.put("Pirojpur", "Barisal");
        division.put("Barisal", "Barisal");
        division.put("Bhola", "Barisal");
        division.put("Barguna", "Barisal");

        division.put("Sylhet", "Sylhet");
        division.put("Moulvibazar", "Sylhet");
        division.put("Habiganj", "Sylhet");
        division.put("Sunamganj", "Sylhet");

        division.put("Narsingdi", "Dhaka");
        division.put("Gazipur", "Dhaka");
        division.put("Shariatpur", "Dhaka");
        division.put("Narayanganj", "Dhaka");
        division.put("Tangail", "Dhaka");
        division.put("Kishoreganj", "Dhaka");
        division.put("Manikganj", "Dhaka");
        division.put("Dhaka", "Dhaka");
        division.put("Munshiganj", "Dhaka");
        division.put("Rajbari", "Dhaka");
        division.put("Madaripur", "Dhaka");
        division.put("Gopalganj", "Dhaka");
        division.put("Faridpur", "Dhaka");

        division.put("Panchagarh", "Rangpur");
        division.put("Dinajpur", "Rangpur");
        division.put("Lalmonirhat", "Rangpur");
        division.put("Nilphamari", "Rangpur");
        division.put("Gaibandha", "Rangpur");
        division.put("Thakurgaon", "Rangpur");
        division.put("Rangpur", "Rangpur");
        division.put("Kurigram", "Rangpur");

        division.put("Sherpur", "Mymensingh");
        division.put("Mymensingh", "Mymensingh");
        division.put("Jamalpur", "Mymensingh");
        division.put("Netrokona", "Mymensingh");
    }

    private void setDistrict(){
        // Ctg division
        district.put("Debidwar", districtCumilla);
        district.put("Barura", districtCumilla);
        district.put("Brahmanpara", districtCumilla);
        district.put("Chandina", districtCumilla);
        district.put("Chauddagram", districtCumilla);
        district.put("Daudkandi", districtCumilla);
        district.put("Homna", districtCumilla);
        district.put("Laksam", districtCumilla);
        district.put("Muradnagar", districtCumilla);
        district.put("Nangalkot", districtCumilla);
        district.put("Comilla Sadar", districtCumilla);
        district.put("Meghna", districtCumilla);
        district.put("Monohargonj", districtCumilla);
        district.put("Sadarsouth", districtCumilla);
        district.put("Titas", districtCumilla);
        district.put("Burichang", districtCumilla);
        district.put("Lalmai", districtCumilla);
        district.put(cityCumilla, districtCumilla);

        district.put("Chhagalnaiya", "Feni");
        district.put("Feni Sadar", "Feni");
        district.put("Sonagazi", "Feni");
        district.put("Fulgazi", "Feni");
        district.put("Parshuram", "Feni");
        district.put("Daganbhuiyan", "Feni");

        district.put("Brahmanbaria Sadar", "Brahmanbaria");
        district.put("Kasba", "Brahmanbaria");
        district.put("Nasirnagar", "Brahmanbaria");
        district.put("Sarail", "Brahmanbaria");
        district.put("Ashuganj", "Brahmanbaria");
        district.put("Akhaura", "Brahmanbaria");
        district.put("Nabinagar", "Brahmanbaria");
        district.put("Bancharampur", "Brahmanbaria");
        district.put("Bijoynagar", "Brahmanbaria");

        district.put("Rangamati Sadar", "Rangamati");
        district.put("Kaptai", "Rangamati");
        district.put("Kawkhali(Rangamati)", "Rangamati");
        district.put("Baghaichari", "Rangamati");
        district.put("Barkal", "Rangamati");
        district.put("Langadu", "Rangamati");
        district.put("Rajasthali", "Rangamati");
        district.put("Belaichari", "Rangamati");
        district.put("Juraichari", "Rangamati");
        district.put("Naniarchar", "Rangamati");

        district.put("Rangunia", districtCtg);
        district.put("Sitakunda", districtCtg);
        district.put("Mirsharai", districtCtg);
        district.put("Patiya", districtCtg);
        district.put("Sandwip", districtCtg);
        district.put("Banshkhali", districtCtg);
        district.put("Boalkhali", districtCtg);
        district.put("Anwara", districtCtg);
        district.put("Chandanaish", districtCtg);
        district.put("Satkania", districtCtg);
        district.put("Lohagara("+districtCtg+")", districtCtg);
        district.put("Hathazari", districtCtg);
        district.put("Fatikchhari", districtCtg);
        district.put("Raozan", districtCtg);
        district.put("Karnafuli", districtCtg);
        district.put(cityCtg, districtCtg);

        district.put("Noakhali Sadar", "Noakhali");
        district.put("Companiganj(Noakhali)", "Noakhali");
        district.put("Begumganj", "Noakhali");
        district.put("Hatia", "Noakhali");
        district.put("Subarnachar", "Noakhali");
        district.put("Kabirhat", "Noakhali");
        district.put("Senbug", "Noakhali");
        district.put("Chatkhil", "Noakhali");
        district.put("Sonaimuri", "Noakhali");

        district.put("Haimchar", "Chandpur");
        district.put("Kachua(Chandpur)", "Chandpur");
        district.put("Shahrasti", "Chandpur");
        district.put("Chandpur Sadar", "Chandpur");
        district.put("Matlabsouth", "Chandpur");
        district.put("Hajiganj", "Chandpur");
        district.put("Matlabnorth", "Chandpur");
        district.put("Faridgonj", "Chandpur");

        district.put("Lakshmipur Sadar", "Lakshmipur");
        district.put("Kamalnagar", "Lakshmipur");
        district.put("Raipur", "Lakshmipur");
        district.put("Ramgati", "Lakshmipur");
        district.put("Ramganj", "Lakshmipur");

        district.put("Coxsbazar Sadar", "Coxsbazar");
        district.put("Chakaria", "Coxsbazar");
        district.put("Kutubdia", "Coxsbazar");
        district.put("Ukhiya", "Coxsbazar");
        district.put("Moheshkhali", "Coxsbazar");
        district.put("Pekua", "Coxsbazar");
        district.put("Ramu", "Coxsbazar");
        district.put("Teknaf", "Coxsbazar");

        district.put("Khagrachhari Sadar", "Khagrachhari");
        district.put("Dighinala", "Khagrachhari");
        district.put("Panchari", "Khagrachhari");
        district.put("Laxmichhari", "Khagrachhari");
        district.put("Mohalchari", "Khagrachhari");
        district.put("Manikchari", "Khagrachhari");
        district.put("Ramgarh", "Khagrachhari");
        district.put("Matiranga", "Khagrachhari");
        district.put("Guimara", "Khagrachhari");

        district.put("Bandarban Sadar", "Bandarban");
        district.put("Alikadam", "Bandarban");
        district.put("Naikhongchhari", "Bandarban");
        district.put("Rowangchhari", "Bandarban");
        district.put("Lama", "Bandarban");
        district.put("Ruma", "Bandarban");
        district.put("Thanchi", "Bandarban");

        // Rajshahi division
        district.put("Belkuchi", "Sirajganj");
        district.put("Chauhali", "Sirajganj");
        district.put("Kamarkhand", "Sirajganj");
        district.put("Kazipur", "Sirajganj");
        district.put("Raigonj", "Sirajganj");
        district.put("Shahjadpur", "Sirajganj");
        district.put("Sirajganj Sadar", "Sirajganj");
        district.put("Ullapara", "Sirajganj");

        district.put("Sujanagar", "Pabna");
        district.put("Ishurdi", "Pabna");
        district.put("Bhangura", "Pabna");
        district.put("Pabna Sadar", "Pabna");
        district.put("Bera", "Pabna");
        district.put("Atghoria", "Pabna");
        district.put("Chatmohar", "Pabna");
        district.put("Santhia", "Pabna");
        district.put("Faridpur", "Pabna");

        district.put("Kahaloo", "Bogra");
        district.put("Bogra Sadar", "Bogra");
        district.put("Shariakandi", "Bogra");
        district.put("Shajahanpur", "Bogra");
        district.put("Dupchanchia", "Bogra");
        district.put("Adamdighi", "Bogra");
        district.put("Nondigram", "Bogra");
        district.put("Sonatala", "Bogra");
        district.put("Dhunot", "Bogra");
        district.put("Gabtali", "Bogra");
        district.put("Sherpur", "Bogra");
        district.put("Shibganj(Bogra)", "Bogra");

        district.put("Paba", "Rajshahi");
        district.put("Durgapur(Rajshahi)", "Rajshahi");
        district.put("Mohonpur", "Rajshahi");
        district.put("Charghat", "Rajshahi");
        district.put("Puthia", "Rajshahi");
        district.put("Bagha", "Rajshahi");
        district.put("Godagari", "Rajshahi");
        district.put("Tanore", "Rajshahi");
        district.put("Bagmara", "Rajshahi");
        district.put("Rajshahi City", "Rajshahi");

        district.put("Natore Sadar", "Natore");
        district.put("Singra", "Natore");
        district.put("Baraigram", "Natore");
        district.put("Bagatipara", "Natore");
        district.put("Lalpur", "Natore");
        district.put("Gurudaspur", "Natore");
        district.put("Naldanga", "Natore");

        district.put("Akkelpur", "Joypurhat");
        district.put("Kalai", "Joypurhat");
        district.put("Khetlal", "Joypurhat");
        district.put("Panchbibi", "Joypurhat");
        district.put("Joypurhat Sadar", "Joypurhat");

        district.put("Chapainawabganj Sadar", "Chapainawabganj");
        district.put("Gomostapur", "Chapainawabganj");
        district.put("Nachol", "Chapainawabganj");
        district.put("Bholahat", "Chapainawabganj");
        district.put("Shibganj(Chapainawabganj)", "Chapainawabganj");

        district.put("Mohadevpur", "Naogaon");
        district.put("Badalgachi", "Naogaon");
        district.put("Patnitala", "Naogaon");
        district.put("Dhamoirhat", "Naogaon");
        district.put("Niamatpur", "Naogaon");
        district.put("Manda", "Naogaon");
        district.put("Atrai", "Naogaon");
        district.put("Raninagar", "Naogaon");
        district.put("Naogaon Sadar", "Naogaon");
        district.put("Porsha", "Naogaon");
        district.put("Sapahar", "Naogaon");

        // Khulna division
        district.put("Manirampur", "Jessore");
        district.put("Abhaynagar", "Jessore");
        district.put("Bagherpara", "Jessore");
        district.put("Chougachha", "Jessore");
        district.put("Jhikargacha", "Jessore");
        district.put("Keshabpur", "Jessore");
        district.put("Jessore Sadar", "Jessore");
        district.put("Sharsha", "Jessore");

        district.put("Assasuni", "Satkhira");
        district.put("Debhata", "Satkhira");
        district.put("Kalaroa", "Satkhira");
        district.put("Satkhira Sadar", "Satkhira");
        district.put("Shyamnagar", "Satkhira");
        district.put("Tala", "Satkhira");
        district.put("Kaliganj(Satkhira)", "Satkhira");

        district.put("Mujibnagar", "Meherpur");
        district.put("Meherpur Sadar", "Meherpur");
        district.put("Gangni", "Meherpur");

        district.put("Narail Sadar", "Narail");
        district.put("Lohagara(Narail)", "Narail");
        district.put("Kalia", "Narail");

        district.put("Chuadanga Sadar", "Chuadanga");
        district.put("Alamdanga", "Chuadanga");
        district.put("Damurhuda", "Chuadanga");
        district.put("Jibannagar", "Chuadanga");

        district.put("Kushtia Sadar", "Kushtia");
        district.put("Kumarkhali", "Kushtia");
        district.put("Khoksa", "Kushtia");
        district.put("Mirpurkushtia", "Kushtia");
        district.put("Daulatpur", "Kushtia");
        district.put("Bheramara", "Kushtia");

        district.put("Shalikha", "Magura");
        district.put("Sreepur(Magura)", "Magura");
        district.put("Magura Sadar", "Magura");
        district.put("Mohammadpur", "Magura");

        district.put("Paikgasa", "Khulna");
        district.put("Fultola", "Khulna");
        district.put("Digholia", "Khulna");
        district.put("Rupsha", "Khulna");
        district.put("Terokhada", "Khulna");
        district.put("Dumuria", "Khulna");
        district.put("Botiaghata", "Khulna");
        district.put("Dakop", "Khulna");
        district.put("Koyra", "Khulna");
        district.put("Khulna City", "Khulna");

        district.put("Fakirhat", "Bagerhat");
        district.put("Bagerhat Sadar", "Bagerhat");
        district.put("Mollahat", "Bagerhat");
        district.put("Sarankhola", "Bagerhat");
        district.put("Rampal", "Bagerhat");
        district.put("Morrelganj", "Bagerhat");
        district.put("Kachua(Bagerhat)", "Bagerhat");
        district.put("Mongla", "Bagerhat");
        district.put("Chitalmari", "Bagerhat");

        district.put("Jhenaidah Sadar", "Jhenaidah");
        district.put("Shailkupa", "Jhenaidah");
        district.put("Harinakundu", "Jhenaidah");
        district.put("Kaliganj(Jhenaidah)", "Jhenaidah");
        district.put("Kotchandpur", "Jhenaidah");
        district.put("Moheshpur", "Jhenaidah");

        // Barisal division
        district.put("Jhalakathi Sadar", "Jhalakathi");
        district.put("Kathalia", "Jhalakathi");
        district.put("Nalchity", "Jhalakathi");
        district.put("Rajapur", "Jhalakathi");

        district.put("Bauphal", "Patuakhali");
        district.put("Patuakhali Sadar", "Patuakhali");
        district.put("Dumki", "Patuakhali");
        district.put("Dashmina", "Patuakhali");
        district.put("Kalapara", "Patuakhali");
        district.put("Mirzaganj", "Patuakhali");
        district.put("Galachipa", "Patuakhali");
        district.put("Rangabali", "Patuakhali");

        district.put("Pirojpur Sadar", "Pirojpur");
        district.put("Nazirpur", "Pirojpur");
        district.put("Kawkhali(Pirojpur)", "Pirojpur");
        district.put("Bhandaria", "Pirojpur");
        district.put("Mathbaria", "Pirojpur");
        district.put("Nesarabad", "Pirojpur");
        district.put("Indurkani", "Pirojpur");

        district.put("Barisal Sadar", "Barisal");
        district.put("Bakerganj", "Barisal");
        district.put("Babuganj", "Barisal");
        district.put("Wazirpur", "Barisal");
        district.put("Banaripara", "Barisal");
        district.put("Gournadi", "Barisal");
        district.put("Agailjhara", "Barisal");
        district.put("Mehendiganj", "Barisal");
        district.put("Muladi", "Barisal");
        district.put("Hizla", "Barisal");
        district.put("Barisal City", "Barisal");

        district.put("Bhola Sadar", "Bhola");
        district.put("Borhanuddin", "Bhola");
        district.put("Charfesson", "Bhola ");
        district.put("Doulatkhan", "Bhola");
        district.put("Monpura", "Bhola");
        district.put("Tazumuddin", "Bhola");
        district.put("Lalmohan", "Bhola");

        district.put("Amtali", "Barguna");
        district.put("Barguna Sadar", "Barguna");
        district.put("Betagi", "Barguna");
        district.put("Bamna", "Barguna");
        district.put("Pathorghata", "Barguna");
        district.put("Taltali", "Barguna");

        // Sylhet division
        district.put("Balaganj", "Sylhet");
        district.put("Beanibazar", "Sylhet");
        district.put("Bishwanath", "Sylhet");
        district.put("Companiganj(Sylhet)", "Sylhet");
        district.put("Fenchuganj", "Sylhet");
        district.put("Golapganj", "Sylhet");
        district.put("Gowainghat", "Sylhet");
        district.put("Jaintiapur", "Sylhet");
        district.put("Kanaighat", "Sylhet");
        district.put("Sylhet Sadar", "Sylhet");
        district.put("Zakiganj", "Sylhet");
        district.put("Dakshinsurma", "Sylhet");
        district.put("Osmaninagar", "Sylhet");
        district.put("Sylhet City", "Sylhet");

        district.put("Barlekha", "Moulvibazar");
        district.put("Kamolganj", "Moulvibazar");
        district.put("Kulaura", "Moulvibazar");
        district.put("Moulvibazar Sadar", "Moulvibazar");
        district.put("Rajnagar", "Moulvibazar");
        district.put("Sreemangal", "Moulvibazar");
        district.put("Juri", "Moulvibazar");

        district.put("Nabiganj", "Habiganj");
        district.put("Bahubal", "Habiganj");
        district.put("Ajmiriganj", "Habiganj");
        district.put("Baniachong", "Habiganj");
        district.put("Lakhai", "Habiganj");
        district.put("Chunarughat", "Habiganj");
        district.put("Habiganj Sadar", "Habiganj");
        district.put("Madhabpur", "Habiganj");
        district.put("Shayestaganj", "Habiganj");

        district.put("Sunamganj Sadar", "Sunamganj");
        district.put("Southsunamganj", "Sunamganj");
        district.put("Bishwambarpur", "Sunamganj");
        district.put("Chhatak", "Sunamganj");
        district.put("Jagannathpur", "Sunamganj");
        district.put("Dowarabazar", "Sunamganj");
        district.put("Tahirpur", "Sunamganj");
        district.put("Dharmapasha", "Sunamganj");
        district.put("Jamalganj", "Sunamganj");
        district.put("Shalla", "Sunamganj");
        district.put("Derai", "Sunamganj");

        // Dhaka division
        district.put("Belabo", "Narsingdi");
        district.put("Monohardi", "Narsingdi");
        district.put("Narsingdi Sadar", "Narsingdi");
        district.put("Palash", "Narsingdi");
        district.put("Raipura", "Narsingdi");
        district.put("Shibpur", "Narsingdi");

        district.put("Kaliganj(Gazipur)", "Gazipur");
        district.put("Kaliakair", "Gazipur");
        district.put("Kapasia", "Gazipur");
        district.put("Gazipur Sadar", "Gazipur");
        district.put("Sreepur(Gazipur)", "Gazipur");
        district.put("Gazipur City", "Gazipur");

        district.put("Shariatpur Sadar", "Shariatpur");
        district.put("Naria", "Shariatpur");
        district.put("Zajira", "Shariatpur");
        district.put("Gosairhat", "Shariatpur");
        district.put("Bhedarganj", "Shariatpur");
        district.put("Damudya", "Shariatpur");

        district.put("Araihazar", "Narayanganj");
        district.put("Bandar", "Narayanganj");
        district.put("Narayanganj Sadar", "Narayanganj");
        district.put("Rupganj", "Narayanganj");
        district.put("Sonargaon", "Narayanganj");
        district.put("Narayanganj City", "Narayanganj");

        district.put("Basail", "Tangail");
        district.put("Bhuapur", "Tangail");
        district.put("Delduar", "Tangail");
        district.put("Ghatail", "Tangail");
        district.put("Gopalpur", "Tangail");
        district.put("Madhupur", "Tangail");
        district.put("Mirzapur", "Tangail");
        district.put("Nagarpur", "Tangail");
        district.put("Sakhipur", "Tangail");
        district.put("Tangail Sadar", "Tangail");
        district.put("Kalihati", "Tangail");
        district.put("Dhanbari", "Tangail");

        district.put("Itna", "Kishoreganj");
        district.put("Katiadi", "Kishoreganj");
        district.put("Bhairab", "Kishoreganj");
        district.put("Tarail", "Kishoreganj");
        district.put("Hossainpur", "Kishoreganj");
        district.put("Pakundia", "Kishoreganj");
        district.put("Kuliarchar", "Kishoreganj");
        district.put("Kishoreganj Sadar", "Kishoreganj");
        district.put("Karimgonj", "Kishoreganj");
        district.put("Bajitpur", "Kishoreganj");
        district.put("Austagram", "Kishoreganj");
        district.put("Mithamoin", "Kishoreganj");
        district.put("Nikli", "Kishoreganj");

        district.put("Harirampur", "Manikganj");
        district.put("Saturia", "Manikganj");
        district.put("Manikganj Sadar", "Manikganj");
        district.put("Gior", "Manikganj");
        district.put("Shibaloy", "Manikganj");
        district.put("Doulatpur", "Manikganj");
        district.put("Singiar", "Manikganj");

        district.put("Savar", "Dhaka");
        district.put("Dhamrai", "Dhaka");
        district.put("Keraniganj", "Dhaka");
        district.put("Nawabganj(Dhaka)", "Dhaka");
        district.put("Dohar", "Dhaka");
        district.put("Dhaka City", "Dhaka");

        district.put("Munshiganj Sadar", "Munshiganj");
        district.put("Sreenagar", "Munshiganj");
        district.put("Sirajdikhan", "Munshiganj");
        district.put("Louhajanj", "Munshiganj");
        district.put("Gajaria", "Munshiganj");
        district.put("Tongibari", "Munshiganj");

        district.put("Rajbari Sadar", "Rajbari");
        district.put("Goalanda", "Rajbari");
        district.put("Pangsa", "Rajbari");
        district.put("Baliakandi", "Rajbari");
        district.put("Kalukhali", "Rajbari");

        district.put("Madaripur Sadar", "Madaripur");
        district.put("Shibchar", "Madaripur");
        district.put("Kalkini", "Madaripur");
        district.put("Rajoir", "Madaripur");

        district.put("Gopalganj Sadar", "Gopalganj");
        district.put("Kashiani", "Gopalganj");
        district.put("Tungipara", "Gopalganj");
        district.put("Kotalipara", "Gopalganj");
        district.put("Muksudpur", "Gopalganj");

        district.put("Faridpur Sadar", "Faridpur");
        district.put("Alfadanga", "Faridpur");
        district.put("Boalmari", "Faridpur");
        district.put("Sadarpur", "Faridpur");
        district.put("Nagarkanda", "Faridpur");
        district.put("Bhanga", "Faridpur");
        district.put("Charbhadrasan", "Faridpur");
        district.put("Madhukhali", "Faridpur");
        district.put("Saltha", "Faridpur");

        // Rangpur division
        district.put("Panchagarh Sadar", "Panchagarh");
        district.put("Debiganj", "Panchagarh");
        district.put("Boda", "Panchagarh");
        district.put("Atwari", "Panchagarh");
        district.put("Tetulia", "Panchagarh");

        district.put("Nawabganj(Dinajpur)", "Dinajpur");
        district.put("Birganj", "Dinajpur");
        district.put("Ghoraghat", "Dinajpur");
        district.put("Birampur", "Dinajpur");
        district.put("Parbatipur", "Dinajpur");
        district.put("Bochaganj", "Dinajpur");
        district.put("Kaharol", "Dinajpur");
        district.put("Fulbari", "Dinajpur");
        district.put("Dinajpur Sadar", "Dinajpur");
        district.put("Hakimpur", "Dinajpur");
        district.put("Khansama", "Dinajpur");
        district.put("Birol", "Dinajpur");
        district.put("Chirirbandar", "Dinajpur");

        district.put("Lalmonirhat Sadar", "Lalmonirhat");
        district.put("Kaliganj(Lalmonirhat)", "Lalmonirhat");
        district.put("Hatibandha", "Lalmonirhat");
        district.put("Patgram", "Lalmonirhat");
        district.put("Aditmari", "Lalmonirhat");

        district.put("Syedpur", "Nilphamari");
        district.put("Domar", "Nilphamari");
        district.put("Dimla", "Nilphamari");
        district.put("Jaldhaka", "Nilphamari");
        district.put("Kishorganj", "Nilphamari");
        district.put("Nilphamari Sadar", "Nilphamari");

        district.put("Sadullapur", "Gaibandha");
        district.put("Gaibandha Sadar", "Gaibandha");
        district.put("Palashbari", "Gaibandha");
        district.put("Saghata", "Gaibandha");
        district.put("Gobindaganj", "Gaibandha");
        district.put("Sundarganj", "Gaibandha");
        district.put("Phulchari", "Gaibandha");

        district.put("Thakurgaon Sadar", "Thakurgaon");
        district.put("Pirganj", "Thakurgaon");
        district.put("Ranisankail", "Thakurgaon");
        district.put("Haripur", "Thakurgaon");
        district.put("Baliadangi", "Thakurgaon");

        district.put("Rangpur Sadar", "Rangpur");
        district.put("Gangachara", "Rangpur");
        district.put("Taragonj", "Rangpur");
        district.put("Badargonj", "Rangpur");
        district.put("Mithapukur", "Rangpur");
        district.put("Pirgonj", "Rangpur");
        district.put("Kaunia", "Rangpur");
        district.put("Pirgacha", "Rangpur");
        district.put("Rangpur City", "Rangpur");

        district.put("Kurigram Sadar", "Kurigram");
        district.put("Nageshwari", "Kurigram");
        district.put("Bhurungamari", "Kurigram");
        district.put("Phulbari", "Kurigram");
        district.put("Rajarhat", "Kurigram");
        district.put("Ulipur", "Kurigram");
        district.put("Chilmari", "Kurigram");
        district.put("Rowmari", "Kurigram");
        district.put("Charrajibpur", "Kurigram");

        // Mymensingh division
        district.put("Sherpur Sadar", "Sherpur");
        district.put("Nalitabari", "Sherpur");
        district.put("Sreebordi", "Sherpur");
        district.put("Nokla", "Sherpur");
        district.put("Jhenaigati", "Sherpur");

        district.put("Fulbaria", "Mymensingh");
        district.put("Trishal", "Mymensingh");
        district.put("Bhaluka", "Mymensingh");
        district.put("Muktagacha", "Mymensingh");
        district.put("Mymensingh Sadar", "Mymensingh");
        district.put("Dhobaura", "Mymensingh");
        district.put("Phulpur", "Mymensingh");
        district.put("Haluaghat", "Mymensingh");
        district.put("Gouripur", "Mymensingh");
        district.put("Gafargaon", "Mymensingh");
        district.put("Iswarganj", "Mymensingh");
        district.put("Nandail", "Mymensingh");
        district.put("Tarakanda", "Mymensingh");
        district.put("Mymensingh City", "Mymensingh");

        district.put("Jamalpur Sadar", "Jamalpur");
        district.put("Melandah", "Jamalpur");
        district.put("Islampur", "Jamalpur");
        district.put("Dewangonj", "Jamalpur");
        district.put("Sarishabari", "Jamalpur ");
        district.put("Madarganj", "Jamalpur ");
        district.put("Bokshiganj", "Jamalpur ");

        district.put("Barhatta", "Netrokona");
        district.put("Durgapur(Netrokona)", "Netrokona");
        district.put("Kendua", "Netrokona");
        district.put("Atpara", "Netrokona");
        district.put("Madan", "Netrokona");
        district.put("Khaliajuri", "Netrokona");
        district.put("Kalmakanda", "Netrokona");
        district.put("Mohongonj", "Netrokona");
        district.put("Purbadhala", "Netrokona");
        district.put("Netrokona Sadar", "Netrokona");

    }
}