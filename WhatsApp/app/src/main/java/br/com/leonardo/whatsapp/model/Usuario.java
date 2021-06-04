package br.com.leonardo.whatsapp.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import br.com.leonardo.whatsapp.activity.ConfiguracoesActivity;
import br.com.leonardo.whatsapp.config.ConfiguracaoFirebase;
import br.com.leonardo.whatsapp.helper.UsuarioFirebase;

public class Usuario {

    private String id;
    private String nome;
    private String email;
    private String senha;
    private String foto;


    public Usuario(){

    }

    public void salvar(){

        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuario = databaseRef.child("usuarios").child(getId());
        usuario.setValue(this);
    }

    public void atualizar(){

        String identificadorUsuario = UsuarioFirebase.getIdentificadorUsuario();
        DatabaseReference databaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuariosRef = databaseRef.child("usuarios").child(identificadorUsuario);

        Map<String, Object> valoresUsuario = converterParaMap();
        usuariosRef.updateChildren(valoresUsuario);
    }

    @Exclude
    public Map<String, Object> converterParaMap(){
        HashMap<String, Object> usuarioMap = new HashMap<>();
        usuarioMap.put("email", getEmail());
        usuarioMap.put("nome", getNome());
        usuarioMap.put("foto", getFoto());

        return usuarioMap;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
