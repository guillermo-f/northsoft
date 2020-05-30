/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                              Fragment de pase de lista manual
:*
:* Archivo:      FragmentPaseListaManual.java
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
:* 29/05/2020       Franco, Carranza, Castillo      Implementacioń de la funcionalidad
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import gps.gmv.akista.R;
import gps.gmv.akista.adaptadores.SpinnerAdapter;
import gps.gmv.akista.databinding.FragmentPaseListaManualBinding;
import gps.gmv.akista.entidades.Alumno;
import gps.gmv.akista.entidades.Asistencia;
import gps.gmv.akista.entidades.Grupo;

public class FragmentPaseListaManual extends Fragment {

    private FragmentPaseListaManualBinding binding;

    private SpinnerAdapter<Alumno>  alumnoSpinnerAdapter;
    private SpinnerAdapter<Grupo>   grupoSpinnerAdapter;

    private List<Alumno>    alumnoSpinnerData;
    private List<Grupo>     grupoSpinnerData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pase_lista_manual, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new AlertDialog.Builder(getActivity())
        .setTitle("Aviso")
        .setMessage("La herramienta de pase de lista es un auxiliar en caso de tener problemas con el reconocimiento.")
        .setCancelable(false)
        .setNeutralButton("OK", ((dialogInterface, i) -> dialogInterface.dismiss()))
        .create()
        .show();

        init();
        setListeners();
    }

    private void init() {
        List<String> asistSpinnerData = new ArrayList<>();
        asistSpinnerData.add("Asistencia");
        asistSpinnerData.add("Falta");
        asistSpinnerData.add("Justificada");

        alumnoSpinnerData = new ArrayList<>();
        grupoSpinnerData = new ArrayList<>();

        alumnoSpinnerAdapter = new SpinnerAdapter<>(getContext(), alumnoSpinnerData);
        binding.spAlumno.setAdapter(alumnoSpinnerAdapter);

        grupoSpinnerAdapter = new SpinnerAdapter<>(getContext(), grupoSpinnerData);
        binding.spGpo.setAdapter(grupoSpinnerAdapter);

        SpinnerAdapter<String> asistSpinnerAdapter = new SpinnerAdapter<>(getContext(), asistSpinnerData);
        binding.spAsistencia.setAdapter(asistSpinnerAdapter);
        asistSpinnerAdapter.notifyDataSetChanged();

        downloadData();
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btSend.setOnClickListener(v -> registro());
    }

    private void downloadData() {
        changeVisibility(false);

        FirebaseDatabase.getInstance()
        .getReference("grupo")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        grupoSpinnerData.add(snapshot.getValue(Grupo.class));
                    grupoSpinnerAdapter.notifyDataSetChanged();

                    enableErrorA(null, false);
                    get(grupoSpinnerData.get(0).getId());
                } else {
                    changeVisibility(true);
                    enableErrorA("Imposible registrar asistencia, no hay grupos", true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                changeVisibility(true);
                enableErrorA(databaseError.getMessage(), true);
            }
        });
    }

    private void enableErrorA(String msg, boolean flag) {
        setFlags(flag);
        binding.spGpo.setEnabled(!flag);
        if (flag)
            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
    }

    private void enableErrorB(String msg, boolean flag) {
        setFlags(flag);
        if (flag)
            Snackbar.make(getView(), msg, Snackbar.LENGTH_LONG).show();
    }

    private void setFlags(boolean flag) {
        binding.btSend.setEnabled(!flag);
        binding.spAlumno.setEnabled(!flag);
        binding.spAsistencia.setEnabled(!flag);
    }

    private void get(String grupo) {
        changeVisibility(false);
        alumnoSpinnerData.clear();

        FirebaseDatabase.getInstance()
        .getReference("alumno")
        .orderByChild("grupo")
        .equalTo(grupo)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        alumnoSpinnerData.add(snapshot.getValue(Alumno.class));
                    enableErrorB(null, false);
                }
                else
                    enableErrorB("Imposible registrar asistencia, grupo vacío", true);

                alumnoSpinnerAdapter.notifyDataSetChanged();
                changeVisibility(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                enableErrorB(databaseError.getMessage(), true);
                changeVisibility(true);
            }
        });
    }

    private void changeVisibility(boolean visible) {
        binding.top.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.scroll.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    private void registro() {
        changeVisibility(false);

        Asistencia a = new Asistencia();
        a.setAsistencia(binding.spAsistencia.getSelectedItemPosition());
        a.setGrupo(binding.spGpo.getSelectedItem().toString());
        a.setIdAlumno(((Alumno) binding.spAlumno.getSelectedItem()).getCurp());

        FirebaseDatabase.getInstance()
        .getReference("asistencia")
        .child(UUID.randomUUID().toString())
        .setValue(a)
        .addOnCompleteListener(task -> {
            if (task.isSuccessful())
                Snackbar.make(getView(), "Asistencia registrada", Snackbar.LENGTH_LONG).show();
            else
                Snackbar.make(getView(), "Ocurrió un error", Snackbar.LENGTH_LONG).show();
        });

        changeVisibility(true);
    }
}
