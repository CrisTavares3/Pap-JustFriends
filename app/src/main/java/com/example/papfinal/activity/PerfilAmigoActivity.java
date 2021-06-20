package com.example.papfinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.papfinal.R;
import com.example.papfinal.adapter.AdapterGrid;
import com.example.papfinal.helper.ConfiguracaoFirebase;
import com.example.papfinal.helper.UsuarioFirebase;
import com.example.papfinal.model.Postagem;
import com.example.papfinal.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PerfilAmigoActivity extends AppCompatActivity {

    private Usuario usuarioSelecionado;
    private Button buttonAcaoPerfil;
    private CircleImageView imagePerfil;

    private DatabaseReference usuariosRef;
    private DatabaseReference usuarioAmigoRef;
    private DatabaseReference seguidoresRef;
    private DatabaseReference usuarioLogadoRef;
    private DatabaseReference firebaseRef;
    private ValueEventListener valueEventListenerPerfilAmigo;
    private TextView textPublicacoes, textSeguidores, textSeguindo;
    private String idUsuarioLogado;
    private Usuario usuarioLogado;
    private DatabaseReference postagensUsuarioRef;
    private GridView gridViewPerfil;
    private AdapterGrid adapterGrid;
    private List<Postagem> postagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_amigo);

        firebaseRef = ConfiguracaoFirebase.getFirebase();
        usuariosRef = firebaseRef.child("usuarios");
        seguidoresRef = firebaseRef.child("seguidores");
        idUsuarioLogado = UsuarioFirebase.getIdentificadorUsuario();

        inicializarComponentes();

        //editar tollbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_black_24dp);

        Bundle bundle = getIntent().getExtras();
        if( bundle != null ){
            usuarioSelecionado = (Usuario) bundle.getSerializable("usuarioSelecionado");

            //Configurar referencia postagens usuario
            postagensUsuarioRef = ConfiguracaoFirebase.getFirebase()
                    .child("postagens")
                    .child( usuarioSelecionado.getId() );

            //Configura nome do usuário na toolbar
            getSupportActionBar().setTitle( usuarioSelecionado.getNome() );

            //Recuperar foto do usuário
            String caminhoFoto = usuarioSelecionado.getCaminhoFoto();
            if( caminhoFoto != null ){
                Uri url = Uri.parse( caminhoFoto );
                Glide.with(PerfilAmigoActivity.this)
                        .load( url )
                        .into( imagePerfil );
            }
        }

        inicializarImageLoader();
        carregarFotosPostagem();

        gridViewPerfil.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Postagem postagem = postagens.get(position);
                Intent i = new Intent(getApplicationContext(), VisualizarPostagemActivity.class);

                i.putExtra("postagem", postagem);
                i.putExtra("usuario", usuarioSelecionado);

                startActivity(i);
            }
        });


    }

    public void inicializarImageLoader(){
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(this)
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
                .build();
        ImageLoader.getInstance().init(config);

    }

    public void carregarFotosPostagem(){

        //Recupera as fotos postadas pelo usuario
        postagens = new ArrayList<>();
        postagensUsuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Configurar o tamanho do grid
                int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
                int tamanhoImagem = tamanhoGrid / 3;
                gridViewPerfil.setColumnWidth( tamanhoImagem );

                List<String> urlFotos = new ArrayList<>();
                for( DataSnapshot ds: dataSnapshot.getChildren() ){
                    Postagem postagem = ds.getValue( Postagem.class );
                    postagens.add( postagem );
                    urlFotos.add( postagem.getCaminhoFoto() );
                }

                //Configurar adapter
                adapterGrid = new AdapterGrid(getApplicationContext(), R.layout.grid_postagem, urlFotos );
                gridViewPerfil.setAdapter( adapterGrid );

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void recuperarDadosUsuarioLogado(){
        usuarioLogadoRef = usuariosRef.child(idUsuarioLogado);
        usuarioLogadoRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        usuarioLogado = dataSnapshot.getValue(Usuario.class);
                        verificaSegueUsuarioAmigo();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }



    private void verificaSegueUsuarioAmigo(){
        DatabaseReference seguidorRef =seguidoresRef
                .child(usuarioSelecionado.getId())
                .child(idUsuarioLogado);
        seguidorRef.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() ){
                            Log.i("dadosUsuario", ":Seguindo");
                            habilitarBotaoSeguir(true);
                        }else{
                            Log.i("dadosUsuario", ": seguir" );
                            habilitarBotaoSeguir( false );
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );
    }

    private void habilitarBotaoSeguir(boolean segueUsuario){
        if(segueUsuario){
            buttonAcaoPerfil.setText("Seguindo");
            // buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
            //   @Override
            //   public void onClick(View v) {
            //       apagarseguidor(usuarioLogado,usuarioSelecionado);
            //    }
            //  });
        }else{
            buttonAcaoPerfil.setText("Seguir");
           /* buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        salvarSeguidor(usuarioLogado, usuarioSelecionado);

                }
            });*/
            buttonAcaoPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Salvar seguidor
                    salvarSeguidor(usuarioLogado, usuarioSelecionado);
                }
            });
        }
    }



    private void salvarSeguidor(Usuario uLogado, Usuario uAmigo){
        HashMap<String, Object> dadosUsuarioLogado = new HashMap<>();
        dadosUsuarioLogado.put("nome",uLogado.getNome());
        dadosUsuarioLogado.put("caminhoFoto",uLogado.getCaminhoFoto());
        DatabaseReference seguidorRef = seguidoresRef
                .child(uAmigo.getId())
                .child(uLogado.getId());
        seguidorRef.setValue(dadosUsuarioLogado);

        buttonAcaoPerfil.setText("Seguindo");

        //Incrementar seguindo do usuario logado
        int seguindo = uLogado.getSeguindo() + 1;
        HashMap<String, Object> dadosSeguindo = new HashMap<>();
        dadosSeguindo.put("seguindo",seguindo);
        DatabaseReference usuarioSeguindo=usuariosRef
                .child(uLogado.getId());
        usuarioSeguindo.updateChildren(dadosSeguindo);

        //incrementar seguidores do amigo
        int seguidores = uAmigo.getSeguidores() + 1;
        HashMap<String, Object> dadosSeguidores = new HashMap<>();
        dadosSeguidores.put("seguidores",seguidores);
        DatabaseReference usuarioSeguidores=usuariosRef
                .child(uAmigo.getId());
        usuarioSeguidores.updateChildren(dadosSeguidores);

    }

    private void apagarseguidor(final Usuario uLogado, final Usuario uAmigo){
        seguidoresRef.limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ulogado = dataSnapshot.getKey();
                seguidoresRef.child(idUsuarioLogado)
                        .child(uAmigo.getId()).removeValue();

                buttonAcaoPerfil.setText("Seguir");

                //Incrementar seguindo do usuario Logado
                int seguindo = uLogado.getSeguindo() - 1;
                HashMap<String, Object> dadosSeguindo = new HashMap<>();
                dadosSeguindo.put("seguindo", seguindo);
                DatabaseReference usuarioSeguindo = usuariosRef
                        .child(uLogado.getId());
                usuarioSeguindo.updateChildren(dadosSeguindo);

                //Incrementar seguidores do amigo
                int seguidores = uAmigo.getSeguidores() - 1;
                HashMap<String, Object> dadosSeguidores = new HashMap<>();
                dadosSeguidores.put("seguidores", seguidores);
                DatabaseReference usuarioSeguidores = usuariosRef
                        .child( uAmigo.getId() );
                usuarioSeguidores.updateChildren( dadosSeguidores );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperarDadosUsuarioLogado();
        recuperarDadosPerfilAmigo();
    }

    @Override
    protected void onStop() {
        super.onStop();
        usuarioAmigoRef.removeEventListener(valueEventListenerPerfilAmigo);
    }

    private void recuperarDadosPerfilAmigo(){

        usuarioAmigoRef = usuariosRef.child(usuarioSelecionado.getId());
        valueEventListenerPerfilAmigo = usuarioAmigoRef.addValueEventListener(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Usuario usuario = dataSnapshot.getValue(Usuario.class);


                        String postagens = String.valueOf(usuario.getPostagens());
                        String seguindo = String.valueOf(usuario.getSeguindo());
                        String seguidores = String.valueOf(usuario.getSeguidores());

                        textPublicacoes.setText(postagens);
                        textSeguidores.setText(seguidores);
                        textSeguindo.setText(seguindo);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }
        );

    }

    private void inicializarComponentes(){
        imagePerfil=findViewById(R.id.imagePerfil);
        buttonAcaoPerfil = findViewById(R.id.buttonAcaoPerfil);
        buttonAcaoPerfil.setText("Seguir");
        textPublicacoes = findViewById(R.id.textPublicacoes);
        textSeguidores = findViewById(R.id.textSeguidores);
        textSeguindo = findViewById(R.id.textSeguindo);
        gridViewPerfil = findViewById(R.id.gridViewPerfil);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}
