package myviewholders;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tuhin.myapplication.ActivityResponse;
import com.example.tuhin.myapplication.R;
import com.google.firebase.firestore.DocumentSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;


//  acts as a parent for all viewholders
//  initializes elements declared in post_q.xml and com_n_like.xml
public class BaseHomeFeedHolder extends RecyclerView.ViewHolder {

    public BaseHomeFeedHolder(@NonNull View v) {
        super(v);
    }
    public void bindTo(final Context context, final DocumentSnapshot activity) {
        Log.i("entering", "binder"+this.getClass().toString());
    }

}
