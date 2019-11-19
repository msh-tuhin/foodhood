package myviewholders;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import models.FeedbackModel;

import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.DateTimeExtractor;
import myapp.utils.PictureBinder;

public class FeedbackHolderCommon extends RecyclerView.ViewHolder {
    private String[] months = {"Jan", "Feb", "March", "April", "May", "June",
            "July", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Context mContext;

    CircleImageView avatar;
    TextView timeTV;
    TextView personNameTV;
    TextView ratingTV;
    TextView reviewTV;

    public FeedbackHolderCommon(@NonNull View v) {
        super(v);
        avatar = v.findViewById(R.id.person_avatar);
        timeTV = v.findViewById(R.id.time);
        personNameTV = v.findViewById(R.id.name);
        ratingTV = v.findViewById(R.id.rating);
        reviewTV = v.findViewById(R.id.review);
    }

    public void bindTo(Context context, String feedbackLink){
        mContext = context;
        db.collection("feedbacks").document(feedbackLink)
        .get()
        .addOnSuccessListener((Activity)context, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot feedbackSnapshot) {
                bindValuesDependentOnPersonVitalDownload(feedbackSnapshot);
                bindTime(feedbackSnapshot);
                bindRating(feedbackSnapshot);
                bindReview(feedbackSnapshot);
            }
        });
    }

    private void bindValuesDependentOnPersonVitalDownload(DocumentSnapshot feedbackSnapshot){
        final String personLink = feedbackSnapshot.getString("w");
        db.collection("person_vital").document(personLink)
                .get()
                .addOnSuccessListener((Activity)mContext, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot personVitalSnapshot) {
                        bindName(personVitalSnapshot);
                        setNameOnClickListener(personLink);
                        bindAvatar(personVitalSnapshot);
                        setAvatarOnClickListener(personLink);
                    }
                });
    }

    private void bindTime(DocumentSnapshot feedbackSnapshot){
        Timestamp timestamp = feedbackSnapshot.getTimestamp("ts");
        timeTV.setText(DateTimeExtractor.getDateTimeString(timestamp));
    }

    private void bindAvatar(DocumentSnapshot personVitalSnapshot){
        PictureBinder.bindProfilePicture(avatar, personVitalSnapshot);
    }

    private void setAvatarOnClickListener(final String personLink){
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonDetail.class);
                intent.putExtra("personLink", personLink);
                mContext.startActivity(intent);
            }
        });
    }

    private void bindName(DocumentSnapshot personVitalSnapshot){
        String name = personVitalSnapshot.getString("n");
        personNameTV.setText(name);
    }

    private void setNameOnClickListener(final String personLink){
        personNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PersonDetail.class);
                intent.putExtra("personLink", personLink);
                mContext.startActivity(intent);
            }
        });
    }

    private void bindRating(DocumentSnapshot feedbackSnapshot){
        Double rating = feedbackSnapshot.getDouble("r");
        ratingTV.setText(Double.toString(rating));
    }

    private void bindReview(DocumentSnapshot feedbackSnapshot){
        String review = feedbackSnapshot.getString("re");
        if(review==null) return;
        reviewTV.setText(review);
    }
}
