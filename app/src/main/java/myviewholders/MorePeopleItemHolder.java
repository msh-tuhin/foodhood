package myviewholders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.PictureBinder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.tuhin.myapplication.DishDetail;
import com.example.tuhin.myapplication.PersonDetail;
import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MorePeopleItemHolder extends RecyclerView.ViewHolder {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String mCurrentUserUid;

    CircleImageView avatar;
    TextView nameTV;
    Button followButton;

    public MorePeopleItemHolder(@NonNull View v) {
        super(v);
        avatar = v.findViewById(R.id.person_avatar);
        nameTV = v.findViewById(R.id.person_name);
        followButton = v.findViewById(R.id.follow);
    }

    public void bindTo(final Context context, final String personLink){
        mCurrentUserUid = mAuth.getCurrentUser().getUid();
        followButton.setVisibility(View.INVISIBLE);
        db.collection("person_vital")
                .document(personLink)
                .get()
                .addOnSuccessListener((Activity) context, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot personVitalSnapshot) {
                        bindAvatar(personVitalSnapshot);
                        setAvatarOnClickListener(context, personLink);
                        bindName(personVitalSnapshot);
                        setNameOnClickListener(context, personLink);
                    }
                });
        bindFollowButton(context, personLink);
    }

    private void bindAvatar(DocumentSnapshot personVitalSnapshot){
        PictureBinder.bindCoverPicture(avatar, personVitalSnapshot);
    }

    private void setAvatarOnClickListener(final Context context, final String personLink){
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonDetail.class);
                intent.putExtra("personLink", personLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindName(DocumentSnapshot personVitalSnapshot){
        String name = personVitalSnapshot.getString("n");
        if(name==null || name.equals("")) return;
        nameTV.setText(name);
    }

    private void setNameOnClickListener(final Context context, final String personLink){
        nameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PersonDetail.class);
                intent.putExtra("personLink", personLink);
                context.startActivity(intent);
            }
        });
    }

    private void bindFollowButton(final Context context, final String personLink){
        db.collection("followings")
                .document(mCurrentUserUid)
                .get()
                .addOnSuccessListener((Activity)context, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            try{
                                ArrayList<String> followings = (ArrayList) documentSnapshot.get("a");
                                if(followings.contains(personLink)){
                                    followButton.setText("UNFOLLOW");
                                }
                            } catch (NullPointerException e){
                                Log.i("Error", e.getMessage());
                            }
                        }
                        followButton.setVisibility(View.VISIBLE);
                        followButton.setOnClickListener(getFollowPersonOnClickListener(personLink));
                    }
                })
                .addOnFailureListener((Activity)context, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        followButton.setVisibility(View.VISIBLE);
                        followButton.setOnClickListener(getFollowPersonOnClickListener(personLink));
                    }
                });
    }

    private View.OnClickListener getFollowPersonOnClickListener(final String personLink){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference followingRef = db.collection("followings")
                        .document(mCurrentUserUid);
                DocumentReference followerRef = db.collection("followers")
                        .document(personLink);
                DocumentReference followerVitalRef = db.collection("person_vital")
                        .document(mCurrentUserUid);
                DocumentReference followedVitalRef = db.collection("person_vital")
                        .document(personLink);
                switch (((Button)v).getText().toString()){
                    case "FOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followButton.setText("UNFOLLOW");
                        followingRef.update("a", FieldValue.arrayUnion(personLink));
                        followerRef.update("a", FieldValue.arrayUnion(mCurrentUserUid));
                        followerVitalRef.update("nf", FieldValue.increment(1));
                        followedVitalRef.update("nfb", FieldValue.increment(1));
                        break;
                    case "UNFOLLOW":
                        // TODO: this should be done if and only if the updates are successful
                        followButton.setText("FOLLOW");
                        followingRef.update("a", FieldValue.arrayRemove(personLink));
                        followerRef.update("a", FieldValue.arrayRemove(mCurrentUserUid));
                        followerVitalRef.update("nf", FieldValue.increment(-1));
                        followedVitalRef.update("nfb", FieldValue.increment(-1));
                        break;
                }
            }
        };
    }
}
