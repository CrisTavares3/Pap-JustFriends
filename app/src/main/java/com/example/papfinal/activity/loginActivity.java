package com.example.papfinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.papfinal.R;
import com.example.papfinal.helper.ConfiguracaoFirebase;
import com.example.papfinal.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.view.View.*;

public class loginActivity extends AppCompatActivity {

    RelativeLayout rellay1, rellay2;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            rellay1.setVisibility(VISIBLE);
            rellay2.setVisibility(VISIBLE);

        }
    };

    private EditText campoEmail, campoSenha;
    private Button botaoEntrar;
    private Usuario usuario;
    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rellay1 = findViewById(R.id.rellay1);
        rellay2 = findViewById(R.id.rellay2);
        handler.postDelayed(runnable, 2000);

        verificarUsuarioLogado();
        inicializarComponentes();
        //login
        botaoEntrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoEmail = campoEmail.getText().toString();
                String textoSenha = campoSenha.getText().toString();

                if( !textoEmail.isEmpty() ){
                    if( !textoSenha.isEmpty() ){

                        usuario = new Usuario();
                        usuario.setEmail(textoEmail);
                        usuario.setSenha(textoSenha);
                        validarLogin(usuario);

                    }else{
                        Toast.makeText(loginActivity.this, "Preencha a Senha!", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(loginActivity.this, "Preencha o E-mail!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void verificarUsuarioLogado(){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        if(autenticacao.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void validarLogin(Usuario usuario){

    autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    autenticacao.signInWithEmailAndPassword(usuario.getEmail(),usuario.getSenha()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }else{
                Toast.makeText(loginActivity.this, "Erro ao fazer login!", Toast.LENGTH_SHORT).show();

            }
        }
    });
    }

    public void abrirRegistro(View view){
        Intent i = new Intent(loginActivity.this, RegistroActivity.class);
        startActivity(i);
    }

    public void inicializarComponentes(){
        campoEmail = findViewById(R.id.editLoginEmail);
        campoSenha = findViewById(R.id.editLoginSenha);
        botaoEntrar = findViewById(R.id.botaoEntrar);

    }



}
