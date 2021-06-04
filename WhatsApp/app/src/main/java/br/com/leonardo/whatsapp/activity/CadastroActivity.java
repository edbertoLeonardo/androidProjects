package br.com.leonardo.whatsapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.leonardo.whatsapp.R;
import br.com.leonardo.whatsapp.config.ConfiguracaoFirebase;
import br.com.leonardo.whatsapp.helper.Base64Custom;
import br.com.leonardo.whatsapp.helper.UsuarioFirebase;
import br.com.leonardo.whatsapp.model.Usuario;

public class CadastroActivity extends AppCompatActivity {

    private TextInputEditText campoNome, campoSenha, campoEmail;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        campoNome = findViewById(R.id.editNome);
        campoSenha = findViewById(R.id.editLoginSenha);
        campoEmail = findViewById(R.id.editLoginEmail);
    }

    public void cadastrarUsuario(Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha()).addOnCompleteListener(this, task -> {

            if (task.isSuccessful()){
                Toast.makeText(CadastroActivity.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_SHORT).show();
                UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());
                finish();

                try {
                    String idUsuario = Base64Custom.codificarBase64(usuario.getEmail());
                    usuario.setId(idUsuario);
                    usuario.salvar();


                }catch (Exception e){
                    e.printStackTrace();
                }

            }else {

                String excecao = "";
                try {
                    throw task.getException();
                } catch (FirebaseAuthWeakPasswordException e) {
                    excecao = "Digite uma senha mais forte!";
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    excecao = "Por favor, digite um e-mail válido!";
                } catch (FirebaseAuthUserCollisionException e) {
                    excecao = "E-mail já cadastrado!";
                } catch (Exception e) {
                    excecao = "Erro ao cadastrar usuário: " + e.getMessage();
                    e.printStackTrace();
                }
                Toast.makeText(CadastroActivity.this, excecao, Toast.LENGTH_SHORT).show();

            }

        });
    }

    public void validarCadastroUsuario(View view){

        //Recuperar os textos dos campos
        String textoNome = campoNome.getText().toString();
        String textoEmail = campoEmail.getText().toString();
        String textoSenha = campoSenha.getText().toString();

        if ( !textoNome.isEmpty() ){
             if ( !textoEmail.isEmpty() ){
                 if ( !textoSenha.isEmpty() ){

                     Usuario usuario = new Usuario();
                     usuario.setNome(textoNome);
                     usuario.setEmail(textoEmail);
                     usuario.setSenha(textoSenha);

                     cadastrarUsuario(usuario);

                 }else {
                     Toast.makeText(CadastroActivity.this, "Preencha a senha", Toast.LENGTH_SHORT).show();
                 }

             }else {
                 Toast.makeText(CadastroActivity.this, "Preencha o email", Toast.LENGTH_SHORT).show();
             }

        }else {
            Toast.makeText(CadastroActivity.this, "Preencha o nome", Toast.LENGTH_SHORT).show();

        }

    }
}