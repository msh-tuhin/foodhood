package models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;

@IgnoreExtraProperties
public class PersonInfo {
    @PropertyName("e")
    private String email;

    @PropertyName("p")
    private String phone;

    public PersonInfo(){

    }

    @PropertyName("e")
    public String getEmail() {
        return email;
    }

    @PropertyName("e")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("p")
    public String getPhone() {
        return phone;
    }

    @PropertyName("p")
    public void setPhone(String phone) {
        this.phone = phone;
    }
}
