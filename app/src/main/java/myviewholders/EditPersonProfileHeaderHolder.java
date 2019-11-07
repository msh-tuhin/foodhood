package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tuhin.myapplication.EditPersonProfile;
import com.example.tuhin.myapplication.EditPersonProfileForm;
import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EditPersonProfileHeaderHolder extends RecyclerView.ViewHolder {
    private String[] months = {"Jan", "Feb", "March", "April", "May", "June",
            "July", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private Context mContext;
    private String mPersonLink;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private Button editProfileButton;
    private TextView nameTV;
    private TextView bioTV;
    private TextView birthdateTV;
    private TextView currentTownTV;
    private TextView homeTownTV;
    private TextView phoneTV;
    public EditPersonProfileHeaderHolder(@NonNull View v) {
        super(v);
        editProfileButton = v.findViewById(R.id.edit_profile_button);
        nameTV = v.findViewById(R.id.name);
        bioTV = v.findViewById(R.id.bio);
        birthdateTV = v.findViewById(R.id.birthdate);
        currentTownTV = v.findViewById(R.id.current_location);
        homeTownTV = v.findViewById(R.id.hometown);
        phoneTV = v.findViewById(R.id.phone);
    }

    public void bindTo(final Context context, final String personLink){
        mContext = context;
        mPersonLink = personLink;
        setEditProfileButtonOnClickListener();

        db.collection("person_vital")
                .document(mPersonLink).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot personVitalSnapshot) {
                        if(personVitalSnapshot.exists()){
                            setCollapsedTitle(personVitalSnapshot);
                            bindName(personVitalSnapshot);
                            bindBio(personVitalSnapshot);
                            bindPhone(personVitalSnapshot);
                            bindBirthdate(personVitalSnapshot);
                            bindCurrentTown(personVitalSnapshot);
                            bindHomeTown(personVitalSnapshot);
                        }
                    }
                });
    }

    private void setEditProfileButtonOnClickListener(){
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditPersonProfileForm.class);
                intent.putExtra("personLink", mPersonLink);
                mContext.startActivity(intent);
            }
        });
    }

    private void bindName(DocumentSnapshot personVitalSnapshot){
        String personName = personVitalSnapshot.getString("n");
        if(personName != null){
            nameTV.setText(personName);
        }
    }

    private void bindBio(DocumentSnapshot personVitalSnapshot){
        String bio = personVitalSnapshot.getString("bio");
        if(bio != null){
            bioTV.setText(bio);
        }
    }

    private void bindPhone(DocumentSnapshot personVitalSnapshot){
        String personPhone = personVitalSnapshot.getString("p");
        if(personPhone != null){
            phoneTV.setText(personPhone);
        }
    }

    private void bindBirthdate(DocumentSnapshot personVitalSnapshot){
        Timestamp personBirthdateTS = personVitalSnapshot.getTimestamp("b");
        String birthdate = getBirthDateString(personBirthdateTS);
        if(birthdate != null){
            String fullText = "Born on: " + birthdate;
            SpannableStringBuilder ssb = getSpannedText(fullText, "Born on: ");
            birthdateTV.setText(ssb);
        }
    }

    private void bindCurrentTown(DocumentSnapshot personVitalSnapshot){
        String personCurrentTown = personVitalSnapshot.getString("ct");
        if(personCurrentTown != null){
            currentTownTV.setText(personCurrentTown);
        }
    }

    private void bindHomeTown(DocumentSnapshot personVitalSnapshot){
        String personHomeTown = personVitalSnapshot.getString("ht");
        if(personHomeTown != null){
            homeTownTV.setText(personHomeTown);
        }
    }

    private String getBirthDateString(Timestamp birthDate){
        if(birthDate == null) return null;
        Date dateObj = birthDate.toDate();
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

    private SpannableStringBuilder getSpannedText(String fullText, String spannable){
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(fullText);
        int start = fullText.indexOf(spannable);
        int end = start + spannable.length();
        spannableStringBuilder.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableStringBuilder;
    }

    private void setCollapsedTitle(DocumentSnapshot personVitalSnapshot){
        final String name = personVitalSnapshot.getString("n");
        if(name == null) return;
        ((EditPersonProfile)mContext).appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    ((EditPersonProfile)mContext).collapsingToolbarLayout.setTitle(name);
                    isShow = true;
                } else if(isShow) {
                    ((EditPersonProfile)mContext).collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }
}
