package br.com.leonardo.organizze.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.leonardo.organizze.R;
import br.com.leonardo.organizze.TermosDeUsoActivity;
import br.com.leonardo.organizze.config.ConfiguracaoFirebase;
import br.com.leonardo.organizze.helper.Base64Custom;
import br.com.leonardo.organizze.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private EditText nome, email,senha;
    private TextView termosUso;
    private Button buttonCadastar;
    private FirebaseAuth autenticacao;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        getSupportActionBar().setTitle("Cadastre-se no Organizze");

        nome = findViewById(R.id.editNome);
        email = findViewById(R.id.editEmail);
        senha = findViewById(R.id.editSenha);
        buttonCadastar = findViewById(R.id.buttonCadastrar);
        termosUso = findViewById(R.id.textTermosDeUso);

        buttonCadastar.setOnClickListener(v -> {

            String textoNome = nome.getText().toString();
            String textoEmail = email.getText().toString();
            String textoSenha= senha.getText().toString();

            if ( !textoNome.isEmpty() ){
                if ( !textoEmail.isEmpty() ){
                    if ( !textoSenha.isEmpty() ){

                        usuario = new Usuario();
                        usuario.setNome(textoNome);
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        cadastrarUsuario();

                    }else {
                        Toast.makeText(CadastroActivity.this, "Preenhca a senha",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this, "Preenhca o email",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(CadastroActivity.this, "Preenhca o nome",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cadastrarUsuario(){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()
        ).addOnCompleteListener(this, task -> {
            if ( task.isSuccessful()){

                String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                usuario.setIdUsuario(idUsuario);
                usuario.salvarUsiario();
                finish();

            }else {

                String excecao = "";
                try {
                    throw task.getException();
                }catch (FirebaseAuthWeakPasswordException e){
                    excecao = "Digite uma senha mais forte!";
                }catch (FirebaseAuthInvalidCredentialsException e){
                    excecao = "Digite um e-mail válido";
                }catch (FirebaseAuthUserCollisionException e){
                    excecao = "Esse e-mail já foi cadastrado";
                }catch (Exception e){
                    excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                    e.printStackTrace();
                }

                Toast.makeText(CadastroActivity.this, excecao ,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void abrirTermosDeUso(View view){

        termosUso.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), TermosDeUsoActivity.class);
            startActivity(intent);
        });

    }
}