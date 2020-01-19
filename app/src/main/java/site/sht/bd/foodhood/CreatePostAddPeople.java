package site.sht.bd.foodhood;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.core.model.NumericRefinement;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

import org.json.JSONException;

import java.util.ArrayList;

import models.DishFeedback;
import models.RestaurantFeedback;
import models.SelectedPerson;
import myapp.utils.AlgoliaAttributeNames;
import myapp.utils.AlgoliaIndexNames;
import myapp.utils.SearchHitBinder;

// receives explicit intent with bundle extra
// the keys =>
//     "caption": str
//     "imageSringUris": ArrayList<String>
//     "restaurantFeedback": RestaurantFeedback
//     "dishFeedbacks": ArrayList<DishFeedback>
public class CreatePostAddPeople extends AppCompatActivity {

    ArrayList<String> addedPeople = new ArrayList<>();
    ArrayList<SelectedPerson> addedPeopleList = new ArrayList<>();

//    SelectedPerson selectedPerson = new SelectedPerson();
    Bundle post;
    Toolbar toolbar;
    Searcher searcher;
    InstantSearch instantSearch;
    SearchBox searchBox;
    Hits hits;
    LinearLayout selectedPeopleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post_add_people);

        post = getIntent().getExtras();

        // checking if data received correctly
        ArrayList<DishFeedback> dishFeedbacks = (ArrayList<DishFeedback>) post.getSerializable("dishFeedbacks");
        RestaurantFeedback restaurantFeedback = (RestaurantFeedback) post.getSerializable("restaurantFeedback");
        Log.i("number_dishes", Integer.toString(dishFeedbacks.size()));
        Log.i("rest-rating", Float.toString(restaurantFeedback.rating));
        Log.i("rest-review", restaurantFeedback.review);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Create Post");
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // not sure about this
        // actionBar.setDisplayShowHomeEnabled(true);

        searchBox = findViewById(R.id.searchBox);
        hits = findViewById(R.id.search_hits);
        selectedPeopleLayout = findViewById(R.id.selected_people_layout);

//        searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID, AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY,
//                AlgoliaIndexNames.INDEX_MAIN);
//        searcher.addNumericRefinement(new NumericRefinement(AlgoliaAttributeNames.TYPE, 2, 0));
//        instantSearch = new InstantSearch(this, searcher);
//        // instantSearch.search();
//        instantSearch.setSearchOnEmptyString(false);
        initiateSearch();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        hits.addItemDecoration(dividerItemDecoration);

        hits.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                SearchHitBinder.refreshView(view);
                int position = hits.getChildAdapterPosition(view);
                Log.i("position", Integer.toString(position));
                SearchHitBinder searchHitBinder = new SearchHitBinder(hits, position, view);
                searchHitBinder.bind(true, true, false, false,
                        false, false, false, false);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });

        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
//                invalidateOptionsMenu();
                final SelectedPerson selectedPerson = new SelectedPerson();
                try{
                    // throws JSONException
                    selectedPerson.setFromJSONObject(hits.get(position));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                if(!addedPeople.contains(selectedPerson.id)){
                    addedPeopleList.add(selectedPerson);
                    addedPeople.add(selectedPerson.id);
                    printPeople(addedPeople);
                    final View view = LayoutInflater.from(CreatePostAddPeople.this).inflate(R.layout.selected_people, null);
                    ((TextView)view.findViewById(R.id.person_name)).setText(selectedPerson.name);
                    view.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedPeopleLayout.removeView(view);
                            addedPeople.remove(selectedPerson.id);
                            Log.i("removing", selectedPerson.name);
                            printPeople(addedPeople);
                            addedPeopleList.remove(selectedPerson);
                        }
                    });
                    selectedPeopleLayout.addView(view, 0);
                }else{
                    String toastMsg = selectedPerson.name + " already exixts";
                    Toast.makeText(CreatePostAddPeople.this, toastMsg, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        if(searcher!=null){
            searcher.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_post_add_people_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.next:
                Log.i("Menu", "working");
                Intent intent = new Intent(CreatePostAddPeople.this, CreatePostPreview.class);
//                post.putStringArrayList("taggedPeople", addedPeople);
                post.putSerializable("taggedPeople", addedPeopleList);
                intent.putExtras(post);
                startActivity(intent);
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // dont have to disable the next menu item for this page
//        if(!addedPeople.isEmpty())
//            menu.findItem(R.id.next).setEnabled(true);
        return super.onPrepareOptionsMenu(menu);
    }

    private void printPeople(ArrayList<String> people){
        int i = 0;
        for(String person:people){
            Log.i("Person "+Integer.toString(i), person);
            i++;
        }
    }

    private void initiateSearch(){
        FirebaseFirestore.getInstance().collection("acr")
                .document("a").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            searcher = Searcher.create(documentSnapshot.getString("id"),
                                    documentSnapshot.getString("k"),
                                    AlgoliaIndexNames.INDEX_RATING_DESC);
                            searcher.addNumericRefinement(new NumericRefinement(AlgoliaAttributeNames.TYPE, 2, 0));
                            instantSearch = new InstantSearch(CreatePostAddPeople.this, searcher);
                            // instantSearch.search();
                            instantSearch.setSearchOnEmptyString(false);
                        }
                    }
                });
    }
}
