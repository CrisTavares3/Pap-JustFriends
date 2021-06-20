package com.example.papfinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.papfinal.R;
import com.example.papfinal.helper.ConfiguracaoFirebase;
import com.example.papfinal.helper.UsuarioFirebase;
import com.example.papfinal.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegistroActivity extends AppCompatActivity {

    private EditText campoNome, campoEmail, campoSenha;
    private Button botaoCadastrar;
    private Usuario usuario;

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        inicializar();
        
        botaoCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoNome = campoNome.getText().toString();
                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();


                if( !textoNome.isEmpty() ){
                    if( !textoEmail.isEmpty() ){
                        if( !textoSenha.isEmpty() ){

                            usuario = new Usuario();
                            usuario.setNome(textoNome);
                            usuario.setEmail(textoEmail);
                            usuario.setSenha(textoSenha);
                            cad(usuario);

                        }else{
                            Toast.makeText(RegistroActivity.this, "Preencha a Senha!", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(RegistroActivity.this, "Preencha o E-mail!", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(RegistroActivity.this, "Preencha o Nome!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public void cad(final Usuario usuario){

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(
                this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){

                            try{
                                //Salvar dados no Firebase
                                String idUsuario = task.getResult().getUser().getUid();
                                usuario.setId(idUsuario);
                                usuario.salvar();

                                //salvar dados no profile do Firebase
                                UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());

                                Toast.makeText(RegistroActivity.this, "Registrado com sucesso!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }else{

                            String erroExcecao = "";
                            try{
                                throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                erroExcecao = "Digite uma senha mais forte!";
                            }catch (FirebaseAuthInvalidCredentialsException e){
                                erroExcecao = "Digite um e-mail válido!";
                            }catch (FirebaseAuthUserCollisionException e){
                                erroExcecao = "Esta conta já está registrada!";
                            }catch (Exception e){
                                erroExcecao = "ao cadastrar usúario: " + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(RegistroActivity.this, "Erro: " + erroExcecao , Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );

    }


    public void inicializar(){

        campoNome = findViewById(R.id.editCadastroNome);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        botaoCadastrar = findViewById(R.id.botaoCadastrar);

    }

    public void abrirLogin(View view){
        Intent i = new Intent(RegistroActivity.this, loginActivity.class);
        startActivity(i);
    }

}
