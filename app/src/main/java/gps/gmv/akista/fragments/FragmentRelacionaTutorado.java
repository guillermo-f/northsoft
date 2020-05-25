/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                Fragment donde se relaciona a un tutorado/alumno con un padre/tutor
:*
:* Archivo:      FragmentRelacionaTutorado.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        18-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 18/05/2020       Franco, Carranza, Castillo      Creación del archivo
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
import gps.gmv.akista.databinding.FragmentRelacionaTutoradoBinding;

public class FragmentRelacionaTutorado extends Fragment {

    private FragmentRelacionaTutoradoBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_relaciona_tutorado, container, false);
        return binding.getRoot();
    }
}
