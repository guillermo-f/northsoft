/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                              Pantalla principal de la app
:*
:* Archivo:      FragmentMain.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        08-05-2020
:* Compilador:   JDK 8
:* Ultima modif: 29/05/2020
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 08/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 21/05/2020       Franco, Carranza, Castillo      Implementación de funcionalidad
:* 28/05/2020       Franco, Carranza, Castillo      ListView en pantalla principal
:* 29/05/2020       Franco, Carranza, Castillo      Listener de avisos agregado
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
import androidx.fragment.app.DialogFragment;
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
import gps.gmv.akista.adaptadores.FragmentMainAdapter;
import gps.gmv.akista.databinding.FragmentMainBinding;
import gps.gmv.akista.entidades.Alumno;
import gps.gmv.akista.entidades.Aviso;
import gps.gmv.akista.entidades.Relacion;
import gps.gmv.akista.entidades.Usuario;
import gps.gmv.akista.otros.Singleton;

public class FragmentMain extends Fragment {

    private FragmentMainAdapter adapter;
    private FragmentMainBinding binding;
    private DialogFragment fd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        List<Alumno> source = new ArrayList<>();
        adapter = new FragmentMainAdapter(getContext(), source);
        binding.listView.setAdapter(adapter);

        FirebaseDatabase.getInstance()
        .getReference("relacion")
        .orderByChild("idTutor")
        .equalTo(FirebaseAuth.getInstance().getUid())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FirebaseDatabase.getInstance()
                        .getReference("alumno")
                        .child(snapshot.getValue(Relacion.class).getIdAlumno())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot local) {
                                source.add(local.getValue(Alumno.class));
                                adapter.notifyDataSetChanged();
                            }

                            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
                        });
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        FirebaseDatabase.getInstance()
        .getReference("aviso")
        .orderByChild("destinatario")
        .equalTo(FirebaseAuth.getInstance().getUid())
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Aviso a = snapshot.getValue(Aviso.class);

                        new AlertDialog.Builder(getActivity())
                        .setTitle(a.getMotivo())
                        .setMessage(a.getMensaje() + "\n\n" + Singleton.getInstance().parseDate(a.getFechaMensaje()))
                        .setCancelable(false)
                        .setNeutralButton("OK", ((dialogInterface, i) -> {
                            FirebaseDatabase.getInstance()
                            .getReference("aviso/" + a.getId())
                            .removeValue();

                            dialogInterface.dismiss();
                        }))
                        .create()
                        .show();
                    }
            }

            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void setListeners() {
        binding.btMenu.setOnClickListener(v -> {
            if (Singleton.getInstance().getUsuario().getTipoUsuario() == 1) {
                fd = new FragmentDialogMenuTutor(this);
                fd.show(getActivity().getSupportFragmentManager(), null);
            } else {
                fd = new FragmentDialogMenuAdmin(this);
                fd.show(getActivity().getSupportFragmentManager(), null);
            }
        });

        binding.btMsj.setOnClickListener(v -> setFragment(new FragmentMensajes()));
    }

    void verAsistencia(View view) {
        fd.dismiss();

        if (Singleton.getInstance().getUsuario().getTipoUsuario() == Usuario.TUTOR)
            setFragment(new FragmentAsistencia());
        else
            setFragment(new FragmentPaseListaManual());
    }

    void verCalificaciones(View view) {
        fd.dismiss();

        if (Singleton.getInstance().getUsuario().getTipoUsuario() == Usuario.TUTOR)
            setFragment(new FragmentCalificaciones());
        else
            setFragment(new FragmentRegistraCalificaciones());
    }

    void verCalendario(View view) {
        fd.dismiss();

        if (Singleton.getInstance().getUsuario().getTipoUsuario() == Usuario.TUTOR)
            setFragment(new FragmentCalendarioEventos());
        else
            setFragment(new FragmentRegistroEventos());
    }

    private void registraAlumno() {
        fd.dismiss();
        setFragment(new FragmentRegistraTutorado());
    }

    void nuevoAviso(View view) {
        fd.dismiss();
        setFragment(new FragmentRegistroAviso());
    }

    void registraHijo(View view) {
        fd.dismiss();
        setFragment(new FragmentRelacionaTutorado());
    }

    private void registroGrupo() {
        fd.dismiss();
        setFragment(new FragmentRegistroGrupo());
    }

    void comprobar(String pass, int go) {
        FirebaseDatabase.getInstance()
        .getReference("CLAVE_GENERAL")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(String.class).equals(pass)) {
                    ((FragmentDialogMenuAdmin)fd).dialogContrasena.dismiss();

                    if (go == 2)
                        registraAlumno();
                    else
                        registroGrupo();
                } else {
                    new AlertDialog.Builder(getActivity())
                    .setTitle("Error")
                    .setMessage("Contraseña incorrecta")
                    .setNeutralButton("OK", ((dialogInterface, i) -> {
                        ((FragmentDialogMenuAdmin) fd).dialogContrasena.dismiss();
                        dialogInterface.dismiss();
                    }))
                    .create().show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new AlertDialog.Builder(getActivity())
                .setTitle("Error")
                .setMessage(databaseError.getMessage())
                .setNeutralButton("OK", ((dialogInterface, i) -> dialogInterface.dismiss()))
                .create().show();
            }
        });
    }

    private void setFragment(Fragment fragment) {
        getActivity()
        .getSupportFragmentManager()
        .beginTransaction()
        .replace(getId(), fragment)
        .addToBackStack(null)
        .commit();
    }
}
