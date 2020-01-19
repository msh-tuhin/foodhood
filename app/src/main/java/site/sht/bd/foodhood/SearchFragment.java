package site.sht.bd.foodhood;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import myapp.utils.AlgoliaAttributeNames;
import myapp.utils.AlgoliaIndexNames;
import myapp.utils.SearchHitBinder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.algolia.instantsearch.core.helpers.Searcher;
import com.algolia.instantsearch.ui.helpers.InstantSearch;
import com.algolia.instantsearch.ui.utils.ItemClickSupport;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import site.sht.bd.foodhood.R;

import org.json.JSONException;

public class SearchFragment extends Fragment {

    InstantSearch instantSearch;
    Searcher searcher;
    SearchBox searchBox;
    Hits hits;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchBox = view.findViewById(R.id.searchBox);
        hits = view.findViewById(R.id.search_hits);

//        searcher = Searcher.create(AlgoliaCredentials.ALGOLIA_APP_ID,
//                AlgoliaCredentials.ALGOLIA_SEARCH_API_KEY, AlgoliaIndexNames.INDEX_MAIN);
//
//        instantSearch = new InstantSearch(getActivity(), searcher);
//        instantSearch.search();
//        // instantSearch.setSearchOnEmptyString(false);
        initiateSearch();

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL);
        hits.addItemDecoration(dividerItemDecoration);

        hits.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                SearchHitBinder.refreshView(view);
                int position = hits.getChildAdapterPosition(view);
                Log.i("position", Integer.toString(position));

                SearchHitBinder searchHitBinder = new SearchHitBinder(hits, position, view);
                searchHitBinder.bind(true, true, true, true, true,
                        true, true, true);
                // setParentLayoutOnClickListener(hits, position, view);
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });

        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                try{
                    String id = hits.get(position).getString(AlgoliaAttributeNames.ID);
                    int type = hits.get(position).getInt(AlgoliaAttributeNames.TYPE);
                    Intent intent;
                    switch (type){
                        case 0:
                            intent = new Intent(SearchFragment.this.getActivity(), PersonDetail.class);
                            intent.putExtra("personLink", id);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(SearchFragment.this.getActivity(), DishDetail.class);
                            intent.putExtra("dishLink", id);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(SearchFragment.this.getActivity(), RestDetail.class);
                            intent.putExtra("restaurantLink", id);
                            startActivity(intent);
                            break;
                    }
                }catch (JSONException e){
                    Log.i("object_id", "no value found");
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        if(searcher!=null){
            searcher.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.alter_timeline).setVisible(false);
    }

    private void setParentLayoutOnClickListener(final Hits hits, final int position, final View view){
        LinearLayout parentLayout = view.findViewById(R.id.parent_layout);
        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String id = hits.get(position).getString(AlgoliaAttributeNames.ID);
                    int type = hits.get(position).getInt(AlgoliaAttributeNames.TYPE);
                    Intent intent;
                    switch (type){
                        case 0:
                            intent = new Intent(SearchFragment.this.getActivity(), PersonDetail.class);
                            intent.putExtra("personLink", id);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(SearchFragment.this.getActivity(), DishDetail.class);
                            intent.putExtra("dishLink", id);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(SearchFragment.this.getActivity(), RestDetail.class);
                            intent.putExtra("restaurantLink", id);
                            startActivity(intent);
                            break;
                    }
                }catch (JSONException e){
                    Log.i("object_id", "no value found");
                }
            }
        });
    }

    private void initiateSearch(){
        FirebaseFirestore.getInstance().collection("acr")
                .document("a").get()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            searcher = Searcher.create(documentSnapshot.getString("id"),
                                    documentSnapshot.getString("k"),
                                    AlgoliaIndexNames.INDEX_MAIN);
                            instantSearch = new InstantSearch(getActivity(), searcher);
                            instantSearch.search();
                            // instantSearch.setSearchOnEmptyString(false);
                        }
                    }
                });
    }
}
