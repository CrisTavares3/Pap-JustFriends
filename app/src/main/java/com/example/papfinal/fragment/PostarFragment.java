package com.example.papfinal.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.papfinal.R;
import com.example.papfinal.activity.FiltroActivity;
import com.example.papfinal.helper.Permissao;

import java.io.ByteArrayOutputStream;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostarFragment extends Fragment {

    private Button buttonAbrirGaleria, buttonAbrirCamera;
    private static final int selecao_camera = 100;
    private static final int selecao_galeria = 200;

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    public PostarFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_postar, container, false);

        Permissao.validarPermissoes(permissoesNecessarias, getActivity(), 1);

        buttonAbrirCamera = view.findViewById(R.id.buttonAbrirCamera);
        buttonAbrirGaleria = view.findViewById(R.id.buttonAbrirGaleria);

        buttonAbrirCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(i.resolveActivity(getActivity().getPackageManager()) != null){
                    startActivityForResult(i, selecao_camera);
                }
            }
        });


        buttonAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(i, selecao_galeria);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == getActivity().RESULT_OK){

            Bitmap imagem = null;

           try {
               switch (requestCode){
                   case selecao_camera:
                       imagem = (Bitmap) data.getExtras().get("data");
                       break;
                   case selecao_galeria:
                       Uri localImagemSelecionada = data.getData();
                       imagem = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localImagemSelecionada);
                        break;
               }

               if(imagem != null){
                   ByteArrayOutputStream baos = new ByteArrayOutputStream();
                   imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                   byte[] dadosImagem = baos.toByteArray();
                   Intent i = new Intent(getActivity(), FiltroActivity.class);
                   i.putExtra("fotoEscolhida", dadosImagem);
                   startActivity(i);
               }

           }catch (Exception e){
               e.printStackTrace();
           }

        }
    }
}
