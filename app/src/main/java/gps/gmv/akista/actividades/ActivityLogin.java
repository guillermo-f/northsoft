/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*          Actividad contenedora de los fragments relacinados al login o registro
:*
:* Archivo:      ActivityLogin.java
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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.ActivityLoginBinding;
import gps.gmv.akista.entidades.Usuario;
import gps.gmv.akista.fragments.FragmentLogin;
import gps.gmv.akista.otros.Singleton;

public class ActivityLogin extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        FirebaseAuth.getInstance().addAuthStateListener(this);

        getSupportFragmentManager()
        .beginTransaction()
        .replace(binding.main.getId(), new FragmentLogin())
        .commit();
    }

    // Método de escucha que se lanza cuando se inicia la sesión de la app mediante Firebase
    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            binding.main.setVisibility(View.GONE);
            binding.working.setVisibility(View.VISIBLE);

            // Se descarga la información del usuario desde la base de datos
            FirebaseDatabase.getInstance().
            getReference("usuario").
            child(FirebaseAuth.getInstance().getUid()) // el ID a buscar proviene de Firebase Auth
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Singleton.getInstance().setUsuario(dataSnapshot.getValue(Usuario.class));

                    // Cuando se descarga el usuario se guarda en el objeto Singleton y se
                    // abre el ActivityMain
                    startActivity(new Intent(ActivityLogin.this, ActivityMain.class));
                    finish();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Snackbar.make(binding.main, error.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
        }
    }
}
