package myviewholders;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.tuhin.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import myapp.utils.ResourceIds;

public class FullRestFeedCommentHolder extends RecyclerView.ViewHolder {

    LinearLayout commentLayout;
    private CircleImageView commenterImage;
    private TextView commenterNameTextView, theCommentTextView, repliesLinkTextView;
    private ImageView likeComment, replyToComment;
    private TextView numberOfLikesTextView, numberOfRepliesTextView;
    FirebaseFirestore db;

    public FullRestFeedCommentHolder(@NonNull View v) {
        super(v);
        commentLayout = v.findViewById(R.id.small_comment_layout);
        commenterImage = v.findViewById(R.id.commenter_image);
        commenterNameTextView = v.findViewById(R.id.commenter_name);
        theCommentTextView = v.findViewById(R.id.the_comment);
        repliesLinkTextView = v.findViewById(R.id.replies_link);
        likeComment = v.findViewById(R.id.like_comment);
        replyToComment = v.findViewById(R.id.reply_to_comment);
        numberOfLikesTextView = v.findViewById(R.id.number_of_likes);
        numberOfRepliesTextView = v.findViewById(R.id.number_of_replies);
        db = FirebaseFirestore.getInstance();
    }
    public void bindTo(final Context context, final String postLink, final String commentLink) {
        Log.i("bindTo", this.getClass().toString());

        final String currentUserLink = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("comments").document(commentLink).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot commentSnapshot = task.getResult();
                            if (commentSnapshot.exists()) {
                                String commenterLink = commentSnapshot.getString("w");

                                db.collection("person_vital").document(commenterLink).get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot personVital) {
                                                if (personVital.exists()) {
                                                    commenterNameTextView.setText(personVital.getString("n"));
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("error", "failed to fetch person name");
                                    }
                                });
                                theCommentTextView.setText(commentSnapshot.getString("te"));
                                int numberOfReplies = ((List<String>) commentSnapshot.get("r")).size();
                                numberOfRepliesTextView.setText(Integer.toString(numberOfReplies));
                                List<String> likers = (List<String>) commentSnapshot.get("l");
                                if (likers.contains(currentUserLink)) {
                                    likeComment.setImageResource(ResourceIds.LIKE_FULL);
                                } else {
                                    likeComment.setImageResource(ResourceIds.LIKE_EMPTY);
                                }
                                int numberofLikes = likers.size();
                                numberOfLikesTextView.setText(Integer.toString(numberofLikes));
                            }
                        }
                    }
                });
    }
}
