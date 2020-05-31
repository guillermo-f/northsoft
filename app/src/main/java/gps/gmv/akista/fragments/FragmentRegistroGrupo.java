/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                            Fragment donde se registra un grupo
:*
:* Archivo:      FragmentRegistroGrupo.java
:* Autor:        Guillermo Franco Alemán           16130804
:*               Miguel Angel Carranza Esquivel    16130790
:*               Victor Alberto Castillo Rivera    17130016
:*
:* Fecha:        29-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 29/05/2020       Franco, Carranza, Castillo      Creación del archivo, funcionalidad
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
import java.util.List;

import gps.gmv.akista.R;
import gps.gmv.akista.adaptadores.SpinnerAdapter;
import gps.gmv.akista.databinding.FragmentRegistroGrupoBinding;
import gps.gmv.akista.entidades.Grupo;
import gps.gmv.akista.entidades.Usuario;

public class FragmentRegistroGrupo extends Fragment {

    private FragmentRegistroGrupoBinding binding;

    private List<Usuario> docentes;

    private SpinnerAdapter<Usuario> docentesSpinnerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_grupo, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        binding.setGrupo(new Grupo());
        docentes = new ArrayList<>();

        docentesSpinnerAdapter = new SpinnerAdapter<>(getContext(), docentes);
        binding.spDocente.setAdapter(docentesSpinnerAdapter);

        downloadData();
    }

    private void setListeners() {
        binding.btVolver.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btSend.setOnClickListener(v -> registro());
    }

    private void downloadData() {
        changeVisibility(false);

        // Los grupos se asignan a un docente por lo que se necesita la lista de los mismos
        FirebaseDatabase.getInstance()
        .getReference("usuario")
        .orderByChild("tipoUsuario")
        .equalTo(Usuario.PROFE)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Si hay docentes disponibles se guardan
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        docentes.add(snapshot.getValue(Usuario.class));
                    docentesSpinnerAdapter.notifyDataSetChanged();
                } else {
                    // De lo contrario se impide continuar con el registro anulando el boton de terminar
                    Snackbar.make(getView(), "No hay docentes disponibles", Snackbar.LENGTH_LONG).show();
                    binding.spDocente.setEnabled(false);
                    binding.btSend.setEnabled(false);
                }
                changeVisibility(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                binding.spDocente.setEnabled(false);
                binding.btSend.setEnabled(false);
                changeVisibility(true);
            }
        });
    }

    private void changeVisibility(boolean visible) {
        binding.top.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.scroll.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    // Proceso de registro
    private void registro() {
        // La clave del grupo no puede quedar vacía
        if (binding.getGrupo().getId().isEmpty()) {
            binding.etGpo.setError("Necesita agregar un identificador");
            return;
        }

        changeVisibility(false);

        // Se establce el id del docente al que pertenecerá el grupo desde el valor seteado
        // en el spinner
        binding.getGrupo().setIdDocente(((Usuario) binding.spDocente.getSelectedItem()).getId());

        // Se inserta el grupo en la base de datos
        FirebaseDatabase.getInstance()
        .getReference("grupo")
        .child(binding.getGrupo().getId())
        .setValue(binding.getGrupo())
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.working.setVisibility(View.GONE);
                binding.success.setVisibility(View.VISIBLE);
            } else {
                changeVisibility(true);
                Snackbar.make(getView(), "Registro de grupo fallido", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
