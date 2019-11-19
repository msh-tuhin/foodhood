package myapp.utils;

import android.view.View;
import android.widget.ImageView;

import com.example.tuhin.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class PictureBinder {
    public static void bindProfilePicture(ImageView view, DocumentSnapshot documentSnapshot){
        if(documentSnapshot == null) return;
        String profilePictureLink = documentSnapshot.getString("pp");
        if(profilePictureLink != null && !profilePictureLink.equals("")){
            Picasso.get().load(profilePictureLink)
                    .placeholder(R.drawable.ltgray)
                    .error(R.drawable.ltgray)
                    .into(view);
        }
    }

    public static void bindCoverPicture(ImageView view, DocumentSnapshot documentSnapshot){
        if(documentSnapshot == null) return;
        String profilePictureLink = documentSnapshot.getString("cp");
        if(profilePictureLink != null && !profilePictureLink.equals("")){
            Picasso.get().load(profilePictureLink)
                    .placeholder(R.drawable.ltgray)
                    .error(R.drawable.ltgray)
                    .into(view);
        }
    }
}
