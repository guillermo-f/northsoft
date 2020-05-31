/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                DialogFragment que la hace de menú de la app para los
:*                             administrativos o profesores
:*
:* Archivo:      FragmentDialogMenuAdmin.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        19-05-2020
:* Compilador:   JDK 8
:* Ultima modif: 26-05-2020
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 19/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 26/05/2020       Franco, Carranza, Castillo      Funcionalidad de los botones
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

import com.google.firebase.auth.FirebaseAuth;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentDialogMenuAdminBinding;
import gps.gmv.akista.otros.Singleton;

// DialogFragment que contiene botones para diversas acciones, en este caso para un usuario docente
public class FragmentDialogMenuAdmin extends DialogFragment {

    private FragmentDialogMenuAdminBinding binding;

    private FragmentMain parent;

    FragmentDialogContrasena dialogContrasena;

    public FragmentDialogMenuAdmin(FragmentMain parent) {
        this.parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_menu_admin, container, false);
        binding.setUsuario(Singleton.getInstance().getUsuario());
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.btAsistencia.setOnClickListener(parent::verAsistencia);
        binding.btCalificaciones.setOnClickListener(parent::verCalificaciones);
        binding.btEvento.setOnClickListener(parent::verCalendario);
        binding.btAviso.setOnClickListener(parent::nuevoAviso);

        binding.btRegistroAlumno.setOnClickListener(v -> {
            dialogContrasena = new FragmentDialogContrasena(parent, 2);
            dialogContrasena.show(getFragmentManager(), null);
        });

        binding.btRegistroGrupo.setOnClickListener(v -> {
            dialogContrasena = new FragmentDialogContrasena(parent, 1);
            dialogContrasena.show(getFragmentManager(), null);
        });

        binding.btCerrarSesion.setOnClickListener(v ->
            new AlertDialog.Builder(getContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Está seguro de salir?")
            .setCancelable(false)
            .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
            .setPositiveButton("Si", (dialogInterface, i) -> {
                Singleton.getInstance().setUsuario(null);
                FirebaseAuth.getInstance().signOut();
            })
            .create()
            .show()
        );
    }
}
