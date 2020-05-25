/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                          Fragment para registro de un docente
:*
:* Archivo:      FragmentRegistroDocente.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        20-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 20/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 21/05/2020       Franco, Carranza, Castillo      Implementación del proceso de registro
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
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentRegistroDocenteBinding;
import gps.gmv.akista.entidades.Usuario;

public class FragmentRegistroDocente extends Fragment {

    private FragmentRegistroDocenteBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_docente, container, false);
        init();
        setListeners();
        return binding.getRoot();
    }

    private void init() {
        binding.setUsuario(new Usuario());
        binding.getUsuario().setTipoUsuario(2);
        binding.setContrasena("");
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> back());
        binding.btFin.setOnClickListener(v -> registro());
        binding.registroPadreTutor.setOnClickListener(v -> back());
    }

    private void registro() {
        HashMap<Integer, String> checks = check();

        if (checks.isEmpty()) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.getUsuario().getCorreo(), binding.getContrasena())
            .addOnCompleteListener(getActivity(), task -> {
                if (task.isSuccessful()) {
                    FirebaseDatabase.getInstance().
                    getReference("usuario").
                    child(FirebaseAuth.getInstance().getUid()).
                    setValue(binding.getUsuario());
                }
                else
                    Snackbar.make(getView(), task.getException().getMessage(), Snackbar.LENGTH_LONG).show();
            });
        } else {
            binding.etNom.setError(checks.containsKey(1) ? checks.get(1) : null);
            binding.etCorreo.setError(checks.containsKey(2) ? checks.get(2) : null);
            binding.etContra.setError(checks.containsKey(3) ? checks.get(3) : null);
            binding.etTel.setError(checks.containsKey(4) ? checks.get(4) : null);
            binding.etDir.setError(checks.containsKey(5) ? checks.get(5) : null);
            binding.etCP.setError(checks.containsKey(6) ? checks.get(6) : null);
            binding.etClaveDoc.setError(checks.containsKey(7) ? checks.get(7) : null);
        }
    }

    private HashMap<Integer, String> check() {
        HashMap<Integer, String> values = new HashMap<>();

        if (binding.getUsuario().getNombre().isEmpty())
            values.put(1, "Escriba su nombre");

        if (binding.getUsuario().getCorreo().isEmpty())
            values.put(2, "Introduzca un correo electrónico");
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.getUsuario().getCorreo()).matches())
            values.put(2, "Introduzca un correo electrónico válido");

        if (binding.getContrasena().isEmpty())
            values.put(3, "Introduzca una contraseña");
        else if (!validPass())
            values.put(3, "Elija otra contraseña, mínimo 8 caracteres que combiene letras, números y algún signo: )(][}{$@#%");

        if (binding.getUsuario().getTelefono().isEmpty())
            values.put(4, "Introduzca un número de teléfono");
        else if (binding.getUsuario().getTelefono().length() < 10)
            values.put(4, "Introduzca un número de teléfono completo");

        if (binding.getUsuario().getDireccion().isEmpty())
            values.put(5, "Ingrese una dirección");

        if (binding.getUsuario().getCodigoPostal().isEmpty())
            values.put(6, "Escriba su código postal");
        else if (binding.getUsuario().getCodigoPostal().length() < 5)
            values.put(6, "Escriba su código postal completo");

        if (binding.getUsuario().getClaveDocente().isEmpty())
            values.put(7, "Ingrese su clave de docente");

        return values;
    }

    private boolean validPass() {
        String pass = binding.getContrasena();

        if (pass.length() < 8)
            return false;

        int longtd = pass.length(),
                     letras = 0,
                     nums   = 0,
                     signos = 0;

        for (int i = 0; i < longtd; i++) {
            if (Character.isDigit(pass.charAt(i)))
                nums++;
            if (Character.isLetter(pass.charAt(i)))
                letras++;
            if (")(][}{$@#%".indexOf(pass.charAt(i)) >= 0)
                signos++;
        }

        return letras > 0 && nums > 0 && signos > 0;
    }

    private void back() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
