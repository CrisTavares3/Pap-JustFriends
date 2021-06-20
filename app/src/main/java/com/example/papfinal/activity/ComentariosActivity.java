package com.example.papfinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.papfinal.R;
import com.example.papfinal.adapter.AdapterComentario;
import com.example.papfinal.helper.ConfiguracaoFirebase;
import com.example.papfinal.helper.UsuarioFirebase;
import com.example.papfinal.model.Comentario;
import com.example.papfinal.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ComentariosActivity extends AppCompatActivity {

        private EditText editComentario;
        private String idPostagem;
        private Usuario usuario;
        private RecyclerView recyclerComentarios;
        private AdapterComentario adapterComentario;
        private List<Comentario> listaComentarios = new ArrayList<>();

        private DatabaseReference firebaseRef;
        private DatabaseReference comentariosRef;
        private ValueEventListener valueEventListenerComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentarios);

        editComentario = findViewById(R.id.editComentario);
        recyclerComentarios = findViewById(R.id.recyclerComentarios);
        usuario = UsuarioFirebase.getDadosUsuarioLogado();
        firebaseRef = ConfiguracaoFirebase.getFirebase();

        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Comentários");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        adapterComentario = new AdapterComentario(listaComentarios, getApplicationContext());
        recyclerComentarios.setHasFixedSize(true);
        recyclerComentarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerComentarios.setAdapter(adapterComentario);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            idPostagem = bundle.getString("idPostagem");
        }
    }

    private void recuperarComentarios(){
        comentariosRef = firebaseRef.child("comentarios")
                .child(idPostagem);
        valueEventListenerComentarios = comentariosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaComentarios.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    listaComentarios.add(ds. getValue(Comentario.class));
                }
                adapterComentario.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarComentarios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        comentariosRef.removeEventListener(valueEventListenerComentarios);
    }

    public void salvarComentario(View view){
        String textoComentario = editComentario.getText().toString();
        if(textoComentario != null && !textoComentario.equals("")){

            Comentario comentario = new Comentario();
            comentario.setIdPostagem(idPostagem);
            comentario.setIdUsuario(usuario.getId());
            comentario.setNomeUsuario(usuario.getNome());
            comentario.setCaminhoFoto(usuario.getCaminhoFoto());
            comentario.setComentario(textoComentario);

            if(comentario.salvar()){
                Toast.makeText(this, "Comentário salvo com sucesso!", Toast.LENGTH_SHORT).show();
            }

        }else{

            Toast.makeText(this, "Insira o comentário antes de enviar!", Toast.LENGTH_SHORT).show();
        }
        editComentario.setText("");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
