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
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 08/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 21/05/2020       Franco, Carranza, Castillo      Implementación de funcionalidad
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
import gps.gmv.akista.databinding.FragmentMainBinding;
import gps.gmv.akista.otros.Singleton;

public class FragmentMain extends Fragment {

    private FragmentMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        setListeners();
        return binding.getRoot();
    }

    private void setListeners() {
        binding.btMenu.setOnClickListener(v -> {
            if (Singleton.getInstance().getUsuario().getTipoUsuario() == 1)
                new FragmentDialogMenuTutor().show(getActivity().getSupportFragmentManager(), null);
            else
                new FragmentDialogMenuAdmin().show(getActivity().getSupportFragmentManager(), null);
        });

        binding.btMsj.setOnClickListener(v ->
            getActivity()
            .getSupportFragmentManager()
            .beginTransaction()
            .replace(binding.main.getId(), new FragmentMensajes())
            .addToBackStack(null)
            .commit()
        );
    }
}
