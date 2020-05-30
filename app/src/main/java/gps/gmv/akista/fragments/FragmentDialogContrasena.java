/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                      DialogFragment para comprobación de contraseña
:*
:* Archivo:      FragmentDialogContrasena.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        30-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 19/05/2020       Franco, Carranza, Castillo      Creación del archivo, funcionalidad
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentDialogContrasenaBinding;

public class FragmentDialogContrasena extends DialogFragment {

    FragmentDialogContrasenaBinding binding;

    private FragmentMain parent;
    private int t;

    public FragmentDialogContrasena(FragmentMain parent, int t) {
        this.parent = parent;
        this.t = t;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_contrasena, container, false);
        binding.setContrasena("");
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.comprobar.setOnClickListener(v -> parent.comprobar(binding.getContrasena(), t));
    }
}
