package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import com.example.tuhin.myapplication.FullPostForRestaurant;
import com.google.firebase.firestore.DocumentSnapshot;

import androidx.annotation.NonNull;
import myapp.utils.EntryPoints;
import myapp.utils.PostBuilder;

public class HalfPostHolderForRestaurant extends HalfPostHolder{

    public HalfPostHolderForRestaurant(@NonNull View v) {
        super(v);
    }

    // TODO
    // might receive a PostModel object if in HalfPostHolder postsnapshot is
    // converted to a PostModel object for binding purposes
    public void bindTo(Context context, DocumentSnapshot postSnapshot){
        mContext = context;
        mPostLink = postSnapshot.getId();
        mPostBuilder = new PostBuilder(context, postSnapshot);
        mPostSnapShot = postSnapshot;

        bindValuesIndependent();
        setOnClickListenersIndependent();
        bindValuesDependentOnPostDownload();
        setOnClickListenersDependentOnPostDownload();
    }

    @Override
    public void setGoToFullOnClickListener() {
        goToFull.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FullPostForRestaurant.class);
                intent.putExtra("entry_point", EntryPoints.CLICKED_GO_TO_FULL_POST);
                intent.putExtra("postLink", mPostLink);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public void bindLikeIcon() {

    }

    @Override
    public void setLikeIconOnClickListener() {

    }

    @Override
    public void setCommentIconOnClickListener() {

    }
}
