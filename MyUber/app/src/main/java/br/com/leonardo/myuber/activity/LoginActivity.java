package br.com.leonardo.myuber.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.leonardo.myuber.R;
import br.com.leonardo.myuber.config.ConfiguracaoFirebase;
import br.com.leonardo.myuber.helper.UsuarioFirebase;
import br.com.leonardo.myuber.model.Usuario;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
    }

    public void validarLoginUsuario(View view){

        //Recuperar textos dos campos
        String textoEmail = campoEmail.getText().toString();
        String textoSenha= campoSenha.getText().toString();

        if (!textoEmail.isEmpty()){
            if (!textoSenha.isEmpty()){

                Usuario usuario = new Usuario();
                usuario.setEmail(textoEmail);
                usuario.setSenha(textoSenha);

                logarUsuario(usuario);

            }else {
                Toast.makeText(LoginActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(LoginActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
        }

    }

    public void logarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAuth();
        autenticacao.signInWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    UsuarioFirebase.redirecionaUsuarioLogado(LoginActivity.this);

                }else {

                }
            }
        });

    }
}