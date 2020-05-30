/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                Fragment que muestra el registro de asistencia de un tutorado
:*
:* Archivo:      FragmentAsistencia.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        08-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 08/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 28/05/2020       Franco, Carranza, Castillo      Integración del adaptador para la info
:*                                                  de la asistencia
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

import java.util.ArrayList;
import java.util.HashMap;

import gps.gmv.akista.R;
import gps.gmv.akista.adaptadores.FragmentAsistenciaAdapter;
import gps.gmv.akista.databinding.FragmentAsistenciaBinding;
import gps.gmv.akista.entidades.Alumno;
import gps.gmv.akista.entidades.Asistencia;
import gps.gmv.akista.entidades.Relacion;

public class FragmentAsistencia extends Fragment {

    private FragmentAsistenciaAdapter adapter;
    private FragmentAsistenciaBinding binding;

    private ArrayList<Alumno> data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_asistencia, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        data = new ArrayList<>();
        adapter = new FragmentAsistenciaAdapter(getContext(), data);
        binding.listView.setAdapter(adapter);
        downloadData();
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());
    }

    private void downloadData() {
        binding.top.setVisibility(View.GONE);
        binding.listView.setVisibility(View.GONE);
        binding.working.setVisibility(View.VISIBLE);

        ArrayList<String> aluIds = new ArrayList<>();
        HashMap<String, Integer> faltasHM = new HashMap<>(),
                                 justifHM = new HashMap<>();

        FirebaseDatabase.getInstance().
        getReference("relacion")
        .orderByChild("idTutor")
        .equalTo(FirebaseAuth.getInstance().getUid())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        aluIds.add(snapshot.getValue(Relacion.class).getIdAlumno());

                    getAsist(aluIds, faltasHM, justifHM);
                } else {
                    Snackbar.make(getView(), "No tiene relación con ningún alumno", Snackbar.LENGTH_LONG).show();
                    binding.working.setVisibility(View.GONE);
                    binding.clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.working.setVisibility(View.GONE);
                binding.clear.setVisibility(View.VISIBLE);
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
        /*

        */
    }

    private void getAsist(ArrayList<String> aluIds, HashMap<String, Integer> faltasHM, HashMap<String, Integer> justifHM) {
        for (String str : aluIds)
            FirebaseDatabase.getInstance()
            .getReference("alumno")
            .orderByChild("curp")
            .equalTo(str)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            data.add(snapshot.getValue(Alumno.class));

                        getReport(aluIds, faltasHM, justifHM);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    binding.working.setVisibility(View.GONE);
                    binding.top.setVisibility(View.VISIBLE);
                    binding.clear.setVisibility(View.VISIBLE);
                    Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
    }

    private void getReport(ArrayList<String> aluIds, HashMap<String, Integer> faltasHM, HashMap<String, Integer> justifHM) {
        for (String str : aluIds)
            FirebaseDatabase.getInstance()
            .getReference("asistencia")
            .orderByChild("idAlumno")
            .equalTo(str)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int faltas = 0, justificaciones = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Asistencia a = snapshot.getValue(Asistencia.class);
                            if (a.getAsistencia() == Asistencia.FALTA)
                                faltas++;
                            else if (a.getAsistencia() == Asistencia.JUSTIF)
                                justificaciones++;
                        }

                        faltasHM.put(str, faltas);
                        justifHM.put(str, justificaciones);


                    } else {
                        faltasHM.put(str, 0);
                        justifHM.put(str, 0);
                    }

                    adapter.setData(faltasHM, justifHM);

                    binding.top.setVisibility(View.VISIBLE);
                    binding.working.setVisibility(View.GONE);
                    binding.listView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    binding.working.setVisibility(View.GONE);
                    binding.top.setVisibility(View.VISIBLE);
                    binding.clear.setVisibility(View.VISIBLE);
                    Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            });
    }
}
