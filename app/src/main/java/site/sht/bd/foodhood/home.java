package site.sht.bd.foodhood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import myapp.utils.OrphanUtilityMethods;
import myapp.utils.SourceHomePage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class home extends AppCompatActivity {

    private int source;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener mAuthStateListener;

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    private int[] drawables_unselected = {
            R.drawable.outline_home_black_24dp,
            R.drawable.baseline_notifications_none_black_24dp,
            R.drawable.outline_search_black_24dp,
            //R.drawable.outline_insert_invitation_black_24dp,
            R.drawable.outline_menu_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        OrphanUtilityMethods.shouldCheckUpdateOptional(this);

        source = getIntent().getIntExtra("source", SourceHomePage.UNKNOWN);

        setCurrentUserNameLocal();
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // maybe not needed: IDK
        adapter.notifyDataSetChanged();
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // adapter.notifyDataSetChanged();
        for(int i=0; i<tabLayout.getTabCount(); i++){
            tabLayout.getTabAt(i).setIcon(drawables_unselected[i]);
        }

        showMyDialog(source);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user==null){
                    Intent intent = new Intent(home.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        OrphanUtilityMethods.checkUpdateMust(this);
        OrphanUtilityMethods.checkMaintenanceBreak(this);
        // OrphanUtilityMethods.shouldCheckUpdateOptional(this);
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                mAuth.signOut();
                return true;
            case R.id.alter_timeline:
                return false;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter{

        ViewPagerAdapter(FragmentManager fm){
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }


        @NonNull
        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return new HomeFeedFragment();
                case 1:
                    return new AllNotificationsFragment();
                case 2:
                    return new SearchFragment();
                //case 3:
                //    return new InvitationsFragment();
                default:
                    return new MoreOptionsFragment();

            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return null;
//            return super.getPageTitle(position);
//            switch (position){
//                case 0:
//                    return "Home";
//                case 1:
//                    return "Notifications";
//                case 2:
//                    return "Search";
//                case 3:
//                    return "Invitations";
//                default:
//                    return "More";
//
//            }
        }
    }

    private void setCurrentUserNameLocal(){
        SharedPreferences sPref = getSharedPreferences(
                getString(R.string.vital_info),
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sPref.edit();
        FirebaseFirestore.getInstance()
                .collection("person_vital")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            String name = documentSnapshot.getString("n");
                            editor.putString("name", name);
                            editor.apply();
                        }
                    }
                });
    }

    private void showMyDialog(final int source){
        if(source==SourceHomePage.UNKNOWN) return;
        String message = "";
        switch (source){
            case SourceHomePage.POST_CREATION_SUCCESSFUL:
                message = "Review added successfully";
                break;
            case SourceHomePage.POST_CREATION_FAILED:
                message = "Adding review failed";
                break;
            case SourceHomePage.PHOTO_UPLOAD_FAILED:
                message = "Couldn't upload profile picture";
                break;
        }
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                if(source==SourceHomePage.PHOTO_UPLOAD_FAILED) return;
                Toast.makeText(home.this,
                        "It might take some time to show your" +
                        " review in your timeline! Please refresh in a few moments.",
                        Toast.LENGTH_LONG).show();
            }
        });
        dialogBuilder.create().show();
    }

}
