/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                      Actividad contenedora de los fragments normales
:*
:* Archivo:      ActivityMain.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        21-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 21/05/2020       Franco, Carranza, Castillo      Creación del archivo
:*==========================================================================================*/

package gps.gmv.akista.actividades;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.ActivityMainBinding;
import gps.gmv.akista.fragments.FragmentMain;

public class ActivityMain extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        FirebaseAuth.getInstance().addAuthStateListener(this);

        getSupportFragmentManager()
        .beginTransaction()
        .replace(binding.main.getId(), new FragmentMain())
        .commit();
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (!(firebaseAuth.getCurrentUser() != null && !firebaseAuth.getCurrentUser().isAnonymous())) {
            startActivity(new Intent(this, ActivityLogin.class));
            finish();
        }
    }
}
