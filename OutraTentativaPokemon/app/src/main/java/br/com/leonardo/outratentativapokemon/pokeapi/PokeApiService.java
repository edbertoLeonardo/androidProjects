package br.com.leonardo.outratentativapokemon.pokeapi;



import br.com.leonardo.outratentativapokemon.model.PokemonResponse;
import retrofit2.Call;
import retrofit2.http.GET;

public interface PokeApiService {

    @GET("pokemon")
    Call<PokemonResponse> obterListPokemon();
}
