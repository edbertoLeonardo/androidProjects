package br.com.leonardo.myuber.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.leonardo.myuber.R;
import br.com.leonardo.myuber.model.Requisicao;
import br.com.leonardo.myuber.model.Usuario;

public class RequisicoesAdapter extends RecyclerView.Adapter<RequisicoesAdapter.MyViewHolder> {

    public RequisicoesAdapter(List<Requisicao> requisicoes, Context context, Usuario motorista) {
        this.requisicoes = requisicoes;
        this.context = context;
        this.motorista = motorista;
    }

    private List<Requisicao> requisicoes;
    private Context context;
    private Usuario motorista;



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_requisicoes, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Requisicao requisicao = requisicoes.get(position);
        Usuario passgeiro = requisicao.getPassageiro();

//        motorista.getLatitude();
//        motorista.getLatitude();

        holder.nome.setText(passgeiro.getNome());
        holder.distancia.setText("1 Km - aproximadamente");

    }

    @Override
    public int getItemCount() {
        return requisicoes.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, distancia;

        public MyViewHolder(View itemView){
            super(itemView);

            nome = itemView.findViewById(R.id.textRequisicaoNome);
            distancia = itemView.findViewById(R.id.textRequisicaoDistancia);
        }
    }
}
