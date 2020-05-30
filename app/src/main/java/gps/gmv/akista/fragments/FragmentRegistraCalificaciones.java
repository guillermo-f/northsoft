/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                         Fragment de registro de calificaciones
:*
:* Archivo:      FragmentRegistraCalificaciones.java
:* Autor:        Guillermo Franco Alemán           16130804
:*               Miguel Angel Carranza Esquivel    16130790
:*               Victor Alberto Castillo Rivera    17130016
:*
:* Fecha:        14-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 14/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 28/05/2020       Franco, Carranza, Castillo      Implementación de funcionalidad
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import gps.gmv.akista.R;
import gps.gmv.akista.adaptadores.SpinnerAdapter;
import gps.gmv.akista.databinding.FragmentRegistraCalificacionesBinding;
import gps.gmv.akista.entidades.Alumno;
import gps.gmv.akista.entidades.Calificacion;
import gps.gmv.akista.entidades.Grupo;

public class FragmentRegistraCalificaciones extends Fragment {

    private FragmentRegistraCalificacionesBinding binding;

    private List<Alumno>    spinnerAlumnoData;
    private List<Grupo>     grupoSpinnerData;

    private SpinnerAdapter<Alumno> alumnoSpinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registra_calificaciones, container, false);
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

        binding.setCalif("");

        spinnerAlumnoData = new ArrayList<>();
        grupoSpinnerData = new ArrayList<>();

        alumnoSpinnerAdapter = new SpinnerAdapter<>(getContext(), spinnerAlumnoData);
        binding.spAlumno.setAdapter(alumnoSpinnerAdapter);

        List<String> unidadSpinnerData = new ArrayList<>();
        unidadSpinnerData.add("1");
        unidadSpinnerData.add("2");
        unidadSpinnerData.add("3");
        unidadSpinnerData.add("4");
        unidadSpinnerData.add("5");

        SpinnerAdapter<String> unidadSpinnerAdapter = new SpinnerAdapter<>(getContext(), unidadSpinnerData);
        binding.spUnidad.setAdapter(unidadSpinnerAdapter);
        unidadSpinnerAdapter.notifyDataSetChanged();

        FirebaseDatabase.getInstance()
        .getReference("grupo")
        .orderByChild("idDocente")
        .equalTo(FirebaseAuth.getInstance().getUid())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        grupoSpinnerData.add(snapshot.getValue(Grupo.class));

                    set();
                } else {
                    binding.btSend.setEnabled(false);
                    changeVisibility(true);
                    Snackbar.make(getView(), "Imposible registrar calificaciones: no tiene grupos asignados", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void set() {
        List<String> materiaSpinnerData = new ArrayList<>();

        FirebaseDatabase.getInstance()
        .getReference("materia")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        materiaSpinnerData.add(snapshot.getValue(String.class));
                    SpinnerAdapter<String> materiaSpinnerAdapter = new SpinnerAdapter<>(getContext(), materiaSpinnerData);
                    binding.spMateria.setAdapter(materiaSpinnerAdapter);
                    materiaSpinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        SpinnerAdapter<Grupo> grupoSpinnerAdapter = new SpinnerAdapter<>(getContext(), grupoSpinnerData);
        binding.spGpo.setAdapter(grupoSpinnerAdapter);
        grupoSpinnerAdapter.notifyDataSetChanged();

        if (!spinnerAlumnoData.isEmpty())
            downloadAlu(spinnerAlumnoData.get(0).getGrupo());

        changeVisibility(true);
    }

    private void setListeners() {
        binding.spGpo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                downloadAlu(grupoSpinnerData.get(i).getId());
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btSend.setOnClickListener(v -> enviar());
    }

    private void downloadAlu(String grupo) {
        changeVisibility(false);
        spinnerAlumnoData.clear();

        FirebaseDatabase.getInstance()
        .getReference("alumno")
        .orderByChild("grupo")
        .equalTo(grupo)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        spinnerAlumnoData.add(snapshot.getValue(Alumno.class));
                    alumnoSpinnerAdapter.notifyDataSetChanged();
                    changeVisibility(true);
                } else {
                    binding.btSend.setEnabled(false);
                    Snackbar.make(getView(), "Imposible registrar calificaciones: grupo sin alumnos", Snackbar.LENGTH_LONG).show();
                    changeVisibility(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                changeVisibility(true);
            }
        });
    }

    private void changeVisibility(boolean visible) {
        binding.top.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.body.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    private void enviar() {
        Double califDouble;

        try {
            califDouble = Double.parseDouble(binding.getCalif());
        } catch (Exception ex) {
            Snackbar.make(getView(), "Ingrese una calificación correcta", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (califDouble < 0.0 || califDouble > 100.0) {
            Snackbar.make(getView(), "Ingrese una calificación correcta", Snackbar.LENGTH_LONG).show();
            return;
        }

        Calificacion c = new Calificacion();
        Alumno a = (Alumno) binding.spAlumno.getSelectedItem();
        c.setAlumnoId(a.getCurp());
        c.setCalif(califDouble);
        c.setGrupo(((Grupo) binding.spGpo.getSelectedItem()).getId());
        c.setNombreMateria((String) binding.spMateria.getSelectedItem());
        c.setUnidadMateria((String) binding.spUnidad.getSelectedItem());

        new AlertDialog.Builder(getActivity())
        .setTitle("Confirmación")
        .setMessage("Confirma que desea asignar la calificación de " + c.getCalif() + " a " + a.getNombre() + " en la unidad " + c.getUnidadMateria())
        .setCancelable(false)
        .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
        .setPositiveButton("Si", ((dialogInterface, i) -> {
            changeVisibility(false);

            FirebaseDatabase.getInstance()
            .getReference("calificacion")
            .child(c.getId())
            .setValue(c)
            .addOnCompleteListener(task -> {
                changeVisibility(true);

                if (task.isSuccessful())
                    Snackbar.make(getView(), "Calificación enviada", Snackbar.LENGTH_LONG).show();
                else
                    Snackbar.make(getView(), "Ocurrió un error", Snackbar.LENGTH_LONG).show();

                binding.setCalif("");
            });
        }))
        .create()
        .show();
    }
}
