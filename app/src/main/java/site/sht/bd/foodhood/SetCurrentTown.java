package site.sht.bd.foodhood;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import myapp.utils.NullStrings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import site.sht.bd.foodhood.R;

public class SetCurrentTown extends AppCompatActivity {

    String selectedTown;
    Toolbar toolbar;
    Spinner townSpinner;
    TextView skipOrNext;
    Bundle personDataBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_current_town);

        personDataBundle = getIntent().getExtras();

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        townSpinner = findViewById(R.id.town_spinner);
        skipOrNext = findViewById(R.id.skip_or_next);

        ArrayAdapter<CharSequence> townAdapter = ArrayAdapter.createFromResource(this,
                R.array.towns_bd, android.R.layout.simple_spinner_item);
        townAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        townSpinner.setAdapter(townAdapter);
        townSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("town", parent.getItemAtPosition(position).toString());
                selectedTown = parent.getItemAtPosition(position).toString();
                if(selectedTown.equals(NullStrings.NULL_CURRENT_TOWN_STRING)){
                    skipOrNext.setText("Skip");
                }else{
                    skipOrNext.setText("Next");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        skipOrNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("skip_next", "clicked");
                Intent intent = new Intent(SetCurrentTown.this, ProfileSetup.class);
                if(((TextView)v).getText() == "Next"){
                    Log.i("selected_town", selectedTown);
                    personDataBundle.putString("current_town", selectedTown);
                }
                intent.putExtras(personDataBundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
