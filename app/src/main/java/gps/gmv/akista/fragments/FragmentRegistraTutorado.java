/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                            Fragment para registrar a un alumno
:*
:* Archivo:      FragmentRegistroAviso.java
:* Autor:        Guillermo Franco Alemán           16130804
:*               Miguel Angel Carranza Esquivel    16130790
:*               Victor Alberto Castillo Rivera    17130016
:*
:* Fecha:        18-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 18/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 27/05/2020       Franco, Carranza, Castillo      Implementación de funcionalidad
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gps.gmv.akista.R;
import gps.gmv.akista.adaptadores.SpinnerAdapter;
import gps.gmv.akista.databinding.FragmentRegistraTutoradoBinding;
import gps.gmv.akista.entidades.Alumno;
import gps.gmv.akista.entidades.Grupo;

public class FragmentRegistraTutorado extends Fragment {

    private FragmentRegistraTutoradoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registra_tutorado, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        changeVisibility(false);

        binding.setAlumno(new Alumno());

        List<String> spinnerData = new ArrayList<>();

        // Al registrar un alumno se registra también a que grupo pertenece por lo que se debe
        // descargar primero la lista de grupos disponibles
        FirebaseDatabase.getInstance()
        .getReference("grupo")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Si hay grupos disponibles entonces se guardan y se muestran en un spinner
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        spinnerData.add(snapshot.getValue(Grupo.class).getId());

                    SpinnerAdapter<String> adapter = new SpinnerAdapter<>(getContext(), spinnerData);
                    binding.spinner.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    changeVisibility(true);
                } else {
                    // Si no hay grupos se impide el registro de cualquier alumno y se desactiva el botón de registro
                    binding.btFin.setEnabled(false);

                    Snackbar.make(getView(), "Imposible registrar alumno: no hay grupos", Snackbar.LENGTH_LONG)
                    .setAction("Regresar", v -> getFragmentManager().popBackStack()).show();

                    changeVisibility(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                changeVisibility(true);
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void changeVisibility(boolean visible) {
        binding.top.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.scroll.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btVolver.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btFin.setOnClickListener(v -> registro());
    }

    private void registro() {
        changeVisibility(false);
        HashMap<Integer, String> checks = check(); // Se manda a llamar al metodo check
                                                   // para la comprobación de valores inválidos

        // Si el HashMap devuelto está vacío entonces el registro procede
        if (checks.isEmpty()) {
            // Se asigna el grupo al alumno de acuerdo al elegido por el spinner
            binding.getAlumno().setGrupo((String) binding.spinner.getSelectedItem());

            // Se inserta el Alumno en la base de datos
            FirebaseDatabase.getInstance()
            .getReference("alumno")
            .child(binding.getAlumno().getCurp())
            .setValue(binding.getAlumno())
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    binding.working.setVisibility(View.GONE);
                    binding.success.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(getView(), "Registro fallido", Snackbar.LENGTH_LONG).show();
                    changeVisibility(true);
                }
            });
        } else {
            // Si la lista de errores no está vacía se activan los errores específicos.
            // Es posible que solo exista un error o que todos los errores estén presentes por lo
            // si el valor de alguna llave es null entonces el error visualmente se desactiva
            changeVisibility(true);

            binding.etNom.setError(checks.get(1));
            binding.etDir.setError(checks.get(2));
            binding.etCP.setError(checks.get(3));
            binding.etCURP.setError(checks.get(4));
            binding.etClaveEsc.setError(checks.get(5));
            binding.etMatr.setError(checks.get(6));
        }
    }

    // Aquí se comprueban los valores introducidos en los campos del registro
    private HashMap<Integer, String> check() {
        HashMap<Integer, String> values = new HashMap<>();

        if (binding.getAlumno().getNombre().isEmpty())
            values.put(1, "Escriba el nombre del alumno");

        if (binding.getAlumno().getDireccion().isEmpty())
            values.put(2, "Escriba la dirección");

        if (binding.getAlumno().getCodigoPostal().isEmpty())
            values.put(3, "Escriba el código postal");
        else if (binding.getAlumno().getCodigoPostal().length() < 5)
            values.put(3, "Escriba un código postal válido");

        if (binding.getAlumno().getCurp().isEmpty())
            values.put(4, "Escriba el CURP");
        else if (binding.getAlumno().getCurp().length() < 18)
            values.put(4, "CURP inválido");

        if (binding.getAlumno().getClave().isEmpty())
            values.put(5, "Es necesaria la clave escolar del alumno");

        if (binding.getAlumno().getMatricula().isEmpty())
            values.put(6, "Escriba la matrícula del alumno");

        return values;
    }
}
