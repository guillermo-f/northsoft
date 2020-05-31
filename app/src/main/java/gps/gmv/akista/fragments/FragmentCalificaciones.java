/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                Fragment que muestra el registro de calificaciones de un tutorado
:*
:* Archivo:      FragmentCalificaciones.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        13-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 13/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 28/05/2020       Franco, Carranza, Castillo      Implementación de la funcionalidad
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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

import java.util.ArrayList;

import gps.gmv.akista.R;
import gps.gmv.akista.adaptadores.FragmentCalificacionesAdapter;
import gps.gmv.akista.adaptadores.SpinnerAdapter;
import gps.gmv.akista.databinding.FragmentCalificacionesBinding;
import gps.gmv.akista.entidades.Calificacion;
import gps.gmv.akista.entidades.Relacion;

public class FragmentCalificaciones extends Fragment {

    private FragmentCalificacionesAdapter adapter;
    private FragmentCalificacionesBinding binding;

    private ArrayList<Calificacion> data; // Información de calificaciones para el adaptador
    private ArrayList<String> relacionesId; // Se guardan los id de los alumnos

    private int selectedItem; // Se usa para específicar a un alumno dentro del spinner

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calificaciones, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    // Se inicializan campos
    private void init() {
        data = new ArrayList<>();
        relacionesId = new ArrayList<>();
        adapter = new FragmentCalificacionesAdapter(getContext(), data);
        binding.listView.setAdapter(adapter);

        changeVisibility(false);
        downloadRelData(); // Descarga de los id de los alumnos con los que se tiene relación
        selectedItem = 0;
    }

    private void setListeners() {
        // Los siguientes 5 listeners se accionan al presionar el botón correspondiente del número de
        // unidad para ver las calificaciones que correspondan
        binding.u1.setOnClickListener(v -> downloadData(relacionesId.get(selectedItem), "1"));
        binding.u2.setOnClickListener(v -> downloadData(relacionesId.get(selectedItem), "2"));
        binding.u3.setOnClickListener(v -> downloadData(relacionesId.get(selectedItem), "3"));
        binding.u4.setOnClickListener(v -> downloadData(relacionesId.get(selectedItem), "4"));
        binding.u5.setOnClickListener(v -> downloadData(relacionesId.get(selectedItem), "5"));

        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());

        // Al cambiar el elemento seleccionado en el spinner se almacena el valor del índice
        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedItem = i;
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void downloadRelData() {
        // Se buscan relaciones existences con alumnos
        FirebaseDatabase.getInstance()
        .getReference("relacion")
        .orderByChild("idTutor")
        .equalTo(FirebaseAuth.getInstance().getUid())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // De existir se guardan los id
                    SpinnerAdapter<String> adapter = new SpinnerAdapter<>(getContext(), relacionesId);
                    binding.spinner.setAdapter(adapter);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        relacionesId.add(snapshot.getValue(Relacion.class).getIdAlumno());

                    // Por defecto al iniciar se solicitaran los datos de calificaciones para la unidad 1
                    downloadData(relacionesId.get(0), "1");
                    adapter.notifyDataSetChanged(); // Se actualiza el spinner de nombres de los usuarios
                } else {
                    changeVisibility(true);
                    // De no haber relaciones existentes se inhabilitan los botones de selección
                    // de unidad y el spinner
                    Snackbar.make(getView(), "No tiene relación con ningún alumno", Snackbar.LENGTH_LONG).show();

                    binding.u1.setEnabled(false);
                    binding.u2.setEnabled(false);
                    binding.u3.setEnabled(false);
                    binding.u4.setEnabled(false);
                    binding.u5.setEnabled(false);

                    binding.spinner.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                changeVisibility(true);
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    // Descarga de la información de las calificaciones
    private void downloadData(String alumnoId, String unidad) {
        changeVisibility(false);
        data.clear(); // Se eliminan los datos guardados existentes

        // Se buscan las calificaciones del alumno en base a su id
        FirebaseDatabase.getInstance()
        .getReference("calificacion")
        .orderByChild("alumnoId")
        .equalTo(alumnoId)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        // Y se filtran de acuerdo a la unidad seleccionada
                        if (snapshot.getValue(Calificacion.class).getUnidadMateria().equals(unidad))
                            data.add(snapshot.getValue(Calificacion.class));

                adapter.notifyDataSetChanged();
                changeVisibility(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                changeVisibility(true);
            }
        });
    }

    private void changeVisibility(boolean visible) {
        binding.body.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
}