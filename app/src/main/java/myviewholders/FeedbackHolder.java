package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import models.FeedbackModel;
import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedbackHolder extends RecyclerView.ViewHolder {

    CircleImageView avatar;
    TextView personName, rating, review;

    public FeedbackHolder(@NonNull View v) {
        super(v);
        avatar = v.findViewById(R.id.person_avatar);
        personName = v.findViewById(R.id.name);
        rating = v.findViewById(R.id.rating);
        review = v.findViewById(R.id.review);
    }

    public void bindTo(FeedbackModel feedback){
        rating.setText(Double.toString(feedback.getRating()));
        review.setText(feedback.getReview());
    }
}
