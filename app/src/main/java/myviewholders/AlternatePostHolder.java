package myviewholders;

import android.content.Context;
import android.view.View;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
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
