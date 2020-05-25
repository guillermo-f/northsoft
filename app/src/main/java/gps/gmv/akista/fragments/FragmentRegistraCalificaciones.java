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

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentRegistraCalificacionesBinding;

public class FragmentRegistraCalificaciones extends Fragment {

    private FragmentRegistraCalificacionesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registra_calificaciones, container, false);
        return binding.getRoot();
    }
}
