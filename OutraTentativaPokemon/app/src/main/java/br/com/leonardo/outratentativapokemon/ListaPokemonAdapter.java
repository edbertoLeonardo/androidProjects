package br.com.leonardo.outratentativapokemon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import br.com.leonardo.outratentativapokemon.model.Pokemon;

public class ListaPokemonAdapter extends RecyclerView.Adapter<ListaPokemonAdapter.ViewHolder> {

    private ArrayList<Pokemon> dataset;

    public ListaPokemonAdapter(){
        dataset = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pokemon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pokemon p = dataset.get(position);
        holder.nomeTextView.setText(p.getNome());

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void addListPokemon(ArrayList<Pokemon> pokemonList) {
        dataset.addAll(pokemonList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView fotoImageView;
        private TextView nomeTextView;

        public ViewHolder(View itemView){
            super(itemView);

            fotoImageView = itemView.findViewById(R.id.fotoImageView);
            nomeTextView = itemView.findViewById(R.id.nomeTextView);
        }
    }

}

