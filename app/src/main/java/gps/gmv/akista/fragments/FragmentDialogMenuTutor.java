/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                DialogFragment que la hace de menú de la app para los
:*                             padres/tutores
:*
:* Archivo:      FragmentDialogMenuAdmin.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        21-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 21/05/2020       Franco, Carranza, Castillo      Creación del archivo
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
import gps.gmv.akista.databinding.FragmentDialogMenuTutorBinding;
import gps.gmv.akista.otros.Singleton;

public class FragmentDialogMenuTutor extends DialogFragment {

    private FragmentDialogMenuTutorBinding binding;

    private FragmentMain parent;

    public FragmentDialogMenuTutor(FragmentMain parent) {
        this.parent = parent;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_menu_tutor, container, false);
        binding.setUsuario(Singleton.getInstance().getUsuario());
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.btAsistencia.setOnClickListener(parent::verAsistencia);
        binding.btCalificaciones.setOnClickListener(parent::verCalificaciones);
        binding.btCalendario.setOnClickListener(parent::verCalendario);
        binding.btRegistraHijo.setOnClickListener(parent::registraHijo);

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
