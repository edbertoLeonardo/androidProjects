package br.com.leonardo.outratentativapokemon;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import br.com.leonardo.outratentativapokemon.model.Pokemon;
import br.com.leonardo.outratentativapokemon.model.PokemonResponse;
import br.com.leonardo.outratentativapokemon.pokeapi.PokeApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "POKEDEX";

 private Retrofit retrofit;
 private RecyclerView recyclerView;
 private ListaPokemonAdapter listaPokemonAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        listaPokemonAdapter = new ListaPokemonAdapter();
        recyclerView.setAdapter(listaPokemonAdapter);
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);

        retrofit = new Retrofit.Builder()
                .baseUrl("http://pokeapi.co/api/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        obterDados();

    }

    private void obterDados() {
        PokeApiService service = retrofit.create(PokeApiService.class);
        Call<PokemonResponse> pokemonResponseCall =  service.obterListPokemon();

        pokemonResponseCall.enqueue(new Callback<PokemonResponse>() {
            @Override
            public void onResponse(Call<PokemonResponse> call, Response<PokemonResponse> response) {
                if (response.isSuccessful()){

                    PokemonResponse pokemonResponse = response.body();
                    ArrayList<Pokemon> pokemonList = pokemonResponse.getResults();

                    listaPokemonAdapter.addListPokemon(pokemonList);

                }else {
                    Log.e(TAG,"resposta " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PokemonResponse> call, Throwable t) {
                Log.e(TAG,"erro" + t.getMessage());
            }
        });
    }
}