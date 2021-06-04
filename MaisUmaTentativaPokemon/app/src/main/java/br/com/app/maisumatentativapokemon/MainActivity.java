package br.com.app.maisumatentativapokemon;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.algolia.instantsearch.helpers.InstantSearch;
import com.algolia.instantsearch.helpers.Searcher;

public class MainActivity extends AppCompatActivity {

    Searcher searcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searcher = Searcher.create("G7M8G8OJ78","1706b7d422d6d42168b738c561120050", "pokemon");
        InstantSearch helper = new InstantSearch(this, searcher);
        helper.search();
    }

    @Override
    protected void onDestroy() {
        searcher.destroy();
        super.onDestroy();
    }
}