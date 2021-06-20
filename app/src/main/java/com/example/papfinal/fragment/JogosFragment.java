package com.example.papfinal.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.papfinal.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class JogosFragment extends Fragment {

    private Button CaraOuCoroa;
    private Button FlappyBird;
    private Button PedraPapelTesoura;

    public JogosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jogos, container, false);

        FlappyBird = view.findViewById(R.id.FlappyBird);
        CaraOuCoroa = view.findViewById(R.id.CaraouCoroa);
        PedraPapelTesoura = view.findViewById(R.id.pedrapapeltesoura);

        FlappyBird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.example.flappybird","com.example.flappybird.AndroidLauncher"));
                startActivity(intent);
            }
        });

        CaraOuCoroa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.example.caraoucoroa","com.example.caraoucoroa.MainActivity"));
                startActivity(intent);
            }
        });

        PedraPapelTesoura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.example.pedrapapeltesoura","com.example.pedrapapeltesoura.MainActivity"));
                startActivity(intent);
            }
        });

        return view;
    }


    private PackageManager getPackageManager() {
        return null;
    }
    }

