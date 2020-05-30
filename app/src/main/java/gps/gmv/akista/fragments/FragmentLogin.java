/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                              Fragment de login de la app
:*
:* Archivo:      FragmentLogin.java
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
:* 21/05/2020       Franco, Carranza, Castillo      Implementación del login
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentLoginBinding;

public class FragmentLogin extends Fragment {

    private FragmentLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        binding.setCorreoElectronico("");
        binding.setContrasena("");
    }

    private void setListeners() {
        binding.tvReg.setOnClickListener(v ->
            getActivity()
            .getSupportFragmentManager()
            .beginTransaction()
            .replace(getId(), new FragmentRegistroTutor())
            .addToBackStack(null)
            .commit()
        );

        binding.btLogin.setOnClickListener(v -> {
            if (Patterns.EMAIL_ADDRESS.matcher(binding.getCorreoElectronico()).matches()) {
                if (binding.getCorreoElectronico().length() >= 8) {
                    if (!binding.getContrasena().isEmpty() && !binding.getCorreoElectronico().isEmpty()) {
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(binding.getCorreoElectronico(), binding.getContrasena())
                        .addOnCompleteListener(getActivity(), task -> {
                            if (!task.isSuccessful())
                                Snackbar.make(binding.main, "No se encontró una cuenta con esas credenciales.\nVerifique sus datos por favor.", Snackbar.LENGTH_LONG).show();
                        });
                    }
                }
                else
                    Snackbar.make(binding.main, "Verifique su contraseña.", Snackbar.LENGTH_LONG).show();
            }
            else
                Snackbar.make(binding.main, "Introduzca un correo electrónico válido.", Snackbar.LENGTH_LONG).show();
        });
    }
}
