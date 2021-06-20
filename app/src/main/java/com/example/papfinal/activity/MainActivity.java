package com.example.papfinal.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.papfinal.R;
import com.example.papfinal.fragment.InicioFragment;
import com.example.papfinal.fragment.JogosFragment;
import com.example.papfinal.fragment.PerfilFragment;
import com.example.papfinal.fragment.PesquisaFragment;
import com.example.papfinal.fragment.PostarFragment;
import com.example.papfinal.helper.ConfiguracaoFirebase;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbarPrincipal);
        toolbar.setTitle("JustFriends");
        setSupportActionBar(toolbar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        configuraBottomNavigationView();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.viewPager, new InicioFragment()).commit();

    }

    private void configuraBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavigation);
        bottomNavigationViewEx.enableAnimation(true);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);

        habilitarNavegacao(bottomNavigationViewEx);

    }

    private void  habilitarNavegacao(BottomNavigationViewEx viewEx){

        viewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem Item) {

                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (Item.getItemId()){
                    case R.id.ic_home:
                        fragmentTransaction.replace(R.id.viewPager, new InicioFragment()).commit();
                        return true;

                    case R.id.ic_pesquisa:
                        fragmentTransaction.replace(R.id.viewPager, new PesquisaFragment()).commit();
                        return true;

                    case R.id.ic_postar:
                        fragmentTransaction.replace(R.id.viewPager, new PostarFragment()).commit();
                        return true;

                    case R.id.ic_games:
                        fragmentTransaction.replace(R.id.viewPager, new JogosFragment()).commit();
                        return true;

                    case R.id.ic_perfil:
                        fragmentTransaction.replace(R.id.viewPager, new PerfilFragment()).commit();
                        return true;
                }

                return false;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_sair:
                deslogarUsuario();
                startActivity(new Intent(getApplicationContext(), loginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    private void deslogarUsuario(){
        try{
            autenticacao.signOut();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
