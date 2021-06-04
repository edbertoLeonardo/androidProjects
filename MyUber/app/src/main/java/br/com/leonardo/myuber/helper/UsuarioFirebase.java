package br.com.leonardo.myuber.helper;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.leonardo.myuber.activity.PassageiroActivity;
import br.com.leonardo.myuber.activity.RequisicoesActivity;
import br.com.leonardo.myuber.config.ConfiguracaoFirebase;
import br.com.leonardo.myuber.model.Usuario;

public class UsuarioFirebase {

    public static FirebaseUser getUsuarioAtual(){

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAuth();
        return usuario.getCurrentUser();
    }

    public static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setId(firebaseUser.getUid());
        usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());

        return usuario;
    }

    public static boolean atualizarNOmeUsuario(String nome){

      try{

          FirebaseUser user = getUsuarioAtual();
          UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(nome).build();
          user.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
              @Override
              public void onComplete(@NonNull Task<Void> task) {
                    if (!task.isSuccessful()){
                        Log.d("Perfil", "Erro ao atualizar nome de perfil.");
                    }
              }
          });

          return true;

      }catch (Exception e){
        e.printStackTrace();
        return false;
      }

    }

    public static void redirecionaUsuarioLogado(Activity activity){

      FirebaseUser user = getUsuarioAtual();
      if (user != null){

          DatabaseReference usuarioRef = ConfiguracaoFirebase.getFirebaseDatabase().child("usuarios").child(getIdentificadorUsuario());
          usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot snapshot) {

                  Usuario usuario = snapshot.getValue(Usuario.class);
                  String tipoUsuario = usuario.getTipo();
                  if (tipoUsuario.equals("M")){
                      activity.startActivity(new Intent(activity, RequisicoesActivity.class));

                  }else {
                      activity.startActivity(new Intent(activity, PassageiroActivity.class));
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError error) {

              }
          });
      }

    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }
}
