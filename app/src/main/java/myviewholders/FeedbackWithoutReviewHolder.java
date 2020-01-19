package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import models.FeedbackModel;
import site.sht.bd.foodhood.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FeedbackWithoutReviewHolder extends RecyclerView.ViewHolder {

    CircleImageView avatar;
    TextView personName, rating;

    public FeedbackWithoutReviewHolder(@NonNull View v) {
        super(v);
        avatar = v.findViewById(R.id.person_avatar);
        personName = v.findViewById(R.id.name);
        rating = v.findViewById(R.id.rating);
    }

    public void bindTo(FeedbackModel feedback){
        rating.setText(Double.toString(feedback.getRating()));
    }
}
