package myviewholders;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.example.tuhin.myapplication.EditPersonProfileForm;
import com.example.tuhin.myapplication.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EditPersonProfileHeaderHolder extends RecyclerView.ViewHolder {

    private Context mContext;
    private String mPersonLink;

    private Button editProfileButton;
    public EditPersonProfileHeaderHolder(@NonNull View v) {
        super(v);
        editProfileButton = v.findViewById(R.id.edit_profile_button);
    }

    public void bindTo(final Context context, final String personLink){
        mContext = context;
        mPersonLink = personLink;
        setEditProfileButtonOnClickListener();
    }

    private void setEditProfileButtonOnClickListener(){
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditPersonProfileForm.class);
                intent.putExtra("personLink", mPersonLink);
                mContext.startActivity(intent);
            }
        });
    }
}
