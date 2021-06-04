package br.com.leonardo.pokedex;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    //public static final String BASE_URL = "http://pokeapi.co/";
    //public static final String BASE_URL = "https://pokeapi.co/api/v2/";
    //public static final String BASE_URL = "https://pokeapi.co/api/v1/";

    public static final String BASE_URL = "http://pokeapi.co";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if(retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}