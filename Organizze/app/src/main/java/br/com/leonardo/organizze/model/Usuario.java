package br.com.leonardo.organizze.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import br.com.leonardo.organizze.config.ConfiguracaoFirebase;

public class Usuario {

    private String idUsuario;
    private String nome;
    private String email;
    private String senha;
    private Double receitaTotal = 0.0;
    private Double despesaTotal = 0.0;

    public Usuario(){

    }

    public void salvarUsiario(){
        DatabaseReference databaseReference = ConfiguracaoFirebase.getFirebaseDatabase();
        databaseReference.child("usuarios").child(this.idUsuario).setValue(this);
    }

    public Double getReceitaTotal() {
        return receitaTotal;
    }

    public void setReceitaTotal(Double receitaTotal) {
        this.receitaTotal = receitaTotal;
    }

    public Double getDespesaTotal() {
        return despesaTotal;
    }

    public void setDespesaTotal(Double despesaTotal) {
        this.despesaTotal = despesaTotal;
    }

    @Exclude
    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}
