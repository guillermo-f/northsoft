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

    private ArrayList<Alumno> data; // Aquí se almacena la información de los alumnos

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

    // Se inicializan valores
    private void init() {
        data = new ArrayList<>();
        adapter = new FragmentAsistenciaAdapter(getContext(), data);
        binding.listView.setAdapter(adapter);
        downloadData(); // se descarga la información desde Firebase Database
    }

    // Funcionalidad a los botones
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

        // Se busca en el espacio de "relacion" a los alumnos que tengan relación alguna con el usuario
        // que usa la app
        FirebaseDatabase.getInstance()
        .getReference("relacion")
        .orderByChild("idTutor")                      // mediante el campo idTutor el cual tiene que
        .equalTo(FirebaseAuth.getInstance().getUid()) // coincidir con el id del usuario
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) { // Si se encuentran relaciones se guardan los id de los alumnos
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        aluIds.add(snapshot.getValue(Relacion.class).getIdAlumno());

                    getAsist(aluIds, faltasHM, justifHM); // Se inicia la busqueda de los alumnos
                } else {
                    // Éste codigo se ejecuta si no se encuentran alumnos relacionados con el usuario
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
    }

    private void getAsist(ArrayList<String> aluIds, HashMap<String, Integer> faltasHM, HashMap<String, Integer> justifHM) {
        for (String str : aluIds)
            // Por cada id contenido en la lista se buscará al alumno en la base de datos
            FirebaseDatabase.getInstance()
            .getReference("alumno")
            .orderByChild("curp") // La "llave principal" de los alumnos en la app es el CURP
            .equalTo(str)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren())
                            // Y se guardará en el arreglo principal del fragment
                            data.add(snapshot.getValue(Alumno.class));

                        getReport(aluIds, faltasHM, justifHM); // Se buscan los datos de la asistencia
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
            // Por cada id contenido en la lista se buscan todos los datos de asistencia
            // que pertenezcan al alumno que le corresponda el id
            FirebaseDatabase.getInstance()
            .getReference("asistencia")
            .orderByChild("idAlumno")
            .equalTo(str)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        int faltas = 0, justificaciones = 0; // Se establecen contadores para las
                                                             // incidencias de falta o justificacion
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Asistencia a = snapshot.getValue(Asistencia.class);

                            // Dependiendo del valor de la asistencia se incrementa el contador específico
                            if (a.getAsistencia() == Asistencia.FALTA)
                                faltas++;
                            else if (a.getAsistencia() == Asistencia.JUSTIF)
                                justificaciones++;
                        }

                        // Se insertan los valores en HashMap's
                        faltasHM.put(str, faltas);
                        justifHM.put(str, justificaciones);


                    } else {
                        // De no haber información de asistencia que pertenezca al alumno
                        // se guarda el valor de 0
                        faltasHM.put(str, 0);
                        justifHM.put(str, 0);
                    }

                    // Se envían los HashMap's al adaptador
                    adapter.setData(faltasHM, justifHM);

                    // Se reestablece la vista del fragment y se actualiza el adaptador del listView
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
