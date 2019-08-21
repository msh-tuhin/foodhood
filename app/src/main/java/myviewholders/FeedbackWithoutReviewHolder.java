package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.tuhin.myapplication.FeedbackModel;
import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
        FirebaseFirestore.getInstance().collection("person_vital").document(feedback.getWho())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot personVital = task.getResult();
                    if(personVital.exists()){
                        String name = personVital.getString("n");
                        personName.setText(name);
                    }
                }
            }
        });
    }
}
