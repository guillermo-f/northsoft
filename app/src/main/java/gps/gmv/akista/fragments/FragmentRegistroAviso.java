/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                            Fragment donde se registra un aviso
:*                            a alguien en específico o en general
:*
:* Archivo:      FragmentRegistroAviso.java
:* Autor:        Guillermo Franco Alemán           16130804
:*               Miguel Angel Carranza Esquivel    16130790
:*               Victor Alberto Castillo Rivera    17130016
:*
:* Fecha:        08-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 08/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 27/05/2020       Franco, Carranza, Castillo      Implementación de la funcionalidad
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
import gps.gmv.akista.databinding.FragmentRegistroAvisoBinding;
import gps.gmv.akista.entidades.Aviso;
import gps.gmv.akista.entidades.Usuario;

public class FragmentRegistroAviso extends Fragment {

    private FragmentRegistroAvisoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_aviso, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        binding.setAviso(new Aviso());
        downloadData();
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btVolver.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btEnviar.setOnClickListener(v -> registro());
    }

    private void downloadData() {
        changeVisibility(false);

        List<Usuario> spinnerData = new ArrayList<>();

        FirebaseDatabase.getInstance()
        .getReference("usuario")
        .orderByChild("tipoUsuario")
        .equalTo(Usuario.TUTOR)
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren())
                        spinnerData.add(snapshot.getValue(Usuario.class));

                    SpinnerAdapter<Usuario> adapter = new SpinnerAdapter<>(getContext(), spinnerData);
                    binding.spinner.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(getView(), "No hay usuarios disponibles", Snackbar.LENGTH_LONG).show();
                    binding.spinner.setEnabled(false);
                    binding.btEnviar.setEnabled(false);
                }

                changeVisibility(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
                binding.spinner.setEnabled(false);
                binding.btEnviar.setEnabled(false);

                changeVisibility(true);
            }
        });
    }

    private void registro() {
        HashMap<Integer, String> checks = check();

        if (checks.isEmpty()) {
            changeVisibility(false);

            binding.getAviso().setDestinatario(((Usuario) binding.spinner.getSelectedItem()).getId());

            FirebaseDatabase.getInstance()
            .getReference("aviso")
            .child(binding.getAviso().getId())
            .setValue(binding.getAviso())
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    binding.working.setVisibility(View.GONE);
                    binding.success.setVisibility(View.VISIBLE);
                } else {
                    changeVisibility(true);
                    Snackbar.make(getView(), "Registro de evento fallido", Snackbar.LENGTH_LONG).show();
                }
            });
        } else {
            binding.etMotivo.setError(checks.get(1));
            binding.etMsj.setError(checks.get(2));
        }
    }

    private HashMap<Integer, String> check() {
        HashMap<Integer, String> values = new HashMap<>();

        if (binding.getAviso().getMotivo().isEmpty())
            values.put(1, "Ingrese el motivo del aviso");

        if (binding.getAviso().getMensaje().isEmpty())
            values.put(2, "Tiene que escribir una aviso");

        return values;
    }

    private void changeVisibility(boolean visible) {
        binding.top.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.scroll.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
}
