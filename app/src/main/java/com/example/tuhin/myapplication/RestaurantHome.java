package com.example.tuhin.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import myapp.utils.SourceHomePage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantHome extends AppCompatActivity {

    private int source;

    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;

    private int[] drawables_unselected = {
            R.drawable.outline_home_black_24dp,
            R.drawable.outline_rate_review_black_24dp,
            R.drawable.baseline_notifications_none_black_24dp,
            R.drawable.baseline_fastfood_black_24dp,
            R.drawable.outline_menu_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_home);

        source = getIntent().getIntExtra("source", SourceHomePage.UNKNOWN);

        setCurrentUserNameLocal();
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        toolbar.setTitle("Food Frenzy");
        setSupportActionBar(toolbar);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        // maybe not needed: IDK
        adapter.notifyDataSetChanged();
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        // adapter.notifyDataSetChanged();
        for(int i=0; i<tabLayout.getTabCount(); i++){
            tabLayout.getTabAt(i).setIcon(drawables_unselected[i]);
        }

        showMyDialog(source);
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
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, MainActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fm){
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }


        @NonNull
        @Override
        public Fragment getItem(int i) {
            switch(i){
                case 0:
                    return new RestHomeFeedFragment();
                case 1:
                    return new PeoplePosts();
                case 2:
                    return new AllNotificationsFragment();
                case 3:
                    return new RestaurantSelfDishesFragment();
                default:
                    return new RestMoreOptionsFragment();

            }
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    private void setCurrentUserNameLocal(){
        SharedPreferences sPref = getSharedPreferences(
                getString(R.string.account_type),
                Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sPref.edit();
        FirebaseFirestore.getInstance()
                .collection("rest_vital")
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

    private void showMyDialog(int source){
        if(source==SourceHomePage.UNKNOWN) return;
        String message = source==SourceHomePage.POST_CREATION_SUCCESSFUL ?
                "Review added successfully": "Adding review failed";
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialogBuilder.create().show();
    }
}
