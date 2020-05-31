/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                Fragment donde se relaciona a un tutorado/alumno con un padre/tutor
:*
:* Archivo:      FragmentRelacionaTutorado.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        18-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 18/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 26/05/2020       Franco, Carranza, Castillo      Implementación de la funcionalidad
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentRelacionaTutoradoBinding;
import gps.gmv.akista.entidades.Alumno;
import gps.gmv.akista.entidades.Relacion;

public class FragmentRelacionaTutorado extends Fragment {

    private FragmentRelacionaTutoradoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_relaciona_tutorado, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        // Se setean los valores a cadenas vacías para prevenir errores por valores nulos
        binding.setClaveAlumno("");
        binding.setCurpAlumno("");
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btVolver.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btFin.setOnClickListener(v -> registro());
    }

    // Proceso de registro
    private void registro() {
        // Se comprueba que el campo del CURP no esté vacío
        if (binding.getCurpAlumno().isEmpty()) {
            binding.etCURPAlumno.setError("Ingrese el CURP del alumno");
            return;
            // Y que tiene la longitud correcta
        } else if (binding.getCurpAlumno().length() < 18) {
            binding.etCURPAlumno.setError("CURP incorrecto, verifique");
            return;
        }

        // Aquí se escribe la clave escolar del alumno
        if (binding.getClaveAlumno().isEmpty()) {
            binding.etClave.setError("Escriba la clave escolar");
            return;
        }

        changeVisibility(false);

        // Se busca el alumno existente al que le pertenezca el CURP introducido
        FirebaseDatabase.getInstance()
        .getReference("alumno")
        .child(binding.getCurpAlumno())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Si lo encuentra...
                if (dataSnapshot.exists()) {
                    Alumno a = dataSnapshot.getValue(Alumno.class);

                    // Compara la clave del alumno con la introducida; si coincide...
                    if (a.getClave().equals(binding.getClaveAlumno())) {
                        // Se crea el objeto Relacion
                        Relacion r = new Relacion();

                        r.setIdAlumno(a.getCurp());
                        r.setIdTutor(FirebaseAuth.getInstance().getUid());

                        // Y se inserta en la base de datos
                        FirebaseDatabase.getInstance()
                        .getReference("relacion")
                        .child(binding.getCurpAlumno() + '-' + FirebaseAuth.getInstance().getUid())
                        .setValue(r)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                binding.working.setVisibility(View.GONE);
                                binding.success.setVisibility(View.VISIBLE);
                            } else {
                                changeVisibility(true);
                                Snackbar.make(getView(), "Registro fallido", Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        // Esta parte se ejecuta si se encuentra el alumno pero no coincide la contraseña
                        changeVisibility(true);
                        Snackbar.make(getView(), "Alumno enccontrado, clave incorrecta", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    // Esta parte se ejecuta si no se encontró al Alumno con el CURP introducido
                    changeVisibility(true);
                    Snackbar.make(getView(), "Las credenciales no coinciden con ningún alumno", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void changeVisibility(boolean visible) {
        binding.top.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.scroll.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
}
