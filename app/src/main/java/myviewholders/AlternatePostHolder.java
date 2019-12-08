package myviewholders;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import models.PostModel;
import myapp.utils.PictureBinder;
import myapp.utils.PostBuilder;

public class AlternatePostHolder extends HalfPostHolder{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public AlternatePostHolder(@NonNull View v) {
        super(v);
    }

    // TODO
    // might receive a PostModel object if in HalfPostHolder postsnapshot is
    // converted to a PostModel object for binding purposes
    public void bindTo(Context context, DocumentSnapshot postSnapshot){
        refreshHolder();
        mContext = context;
        mPostLink = postSnapshot.getId();
        mPostBuilder = new PostBuilder(context, postSnapshot);
        mPostSnapShot = postSnapshot;

        bindValuesIndependent();
        setOnClickListenersIndependent();
        bindValuesDependentOnPostDownload();
        setOnClickListenersDependentOnPostDownload();
    }
}
