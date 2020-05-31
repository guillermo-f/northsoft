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
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        // Se establecen los valores iniciales
        binding.setUsuario(new Usuario());
        binding.getUsuario().setTipoUsuario(Usuario.PROFE); // Aquí se registran los docentes o administrativos
        binding.setContrasena(""); // Contraseña en cadena vacia para evitar errores por valores nulos
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> {
            getActivity()
            .getSupportFragmentManager()
            .beginTransaction()
            .replace(getId(), new FragmentLogin())
            .commit();
        });
        binding.btFin.setOnClickListener(v -> registro());
        binding.registroPadreTutor.setOnClickListener(v -> back());
    }

    private void registro() {
        // Revisión de errores mediante el metodo check
        HashMap<Integer, String> checks = check();

        // Inexistencia de errores
        if (checks.isEmpty()) {
            changeVisibility(false);

            // Se crea el usuario en Firebase
            FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(binding.getUsuario().getCorreo(), binding.getContrasena())
            .addOnCompleteListener(getActivity(), taskA -> {
                // Si el registro es exitoso>
                if (taskA.isSuccessful()) {
                    // >se le asigna el ID proveniente de Firebase al usuario
                    binding.getUsuario().setId(FirebaseAuth.getInstance().getUid());

                    // Se guarda la información completa del usuario en la base de datos
                    FirebaseDatabase.getInstance().
                    getReference("usuario").
                    child(FirebaseAuth.getInstance().getUid()).
                    setValue(binding.getUsuario())
                    .addOnCompleteListener(taskB -> {
                        if (!taskB.isSuccessful()) {
                            // Si la inserción del usuario falla, se elimina el usuario de Firebase
                            // para que pueda volver a intentarlo sin encontrarse con duplicidad
                            // de cuentas
                            Snackbar.make(getView(), "Registro fallido", Snackbar.LENGTH_LONG).show();
                            changeVisibility(true);
                            FirebaseAuth.getInstance().getCurrentUser().delete();
                        }
                    });
                }
                else
                    Snackbar.make(getView(), taskA.getException().getMessage(), Snackbar.LENGTH_LONG).show();
            });
        } else {
            // Existencia de errores
            binding.etNom.setError(checks.get(1));
            binding.etCorreo.setError(checks.get(2));
            binding.etContra.setError(checks.get(3));
            binding.etTel.setError(checks.get(4));
            binding.etDir.setError(checks.get(5));
            binding.etCP.setError(checks.get(6));
            binding.etClaveDoc.setError(checks.get(7));
        }
    }

    // Comprobación de errores devueltos en un HashMap
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

    // Aquí se comprueba que una contraseña sea válida
    private boolean validPass() {
        String pass = binding.getContrasena();

        // Debe tener al menos 8 caracteres
        if (pass.length() < 8)
            return false;

        int longtd = pass.length(),
            letras = 0, // contador de letras (mayusculas y minusculas)
            nums   = 0, // digitos
            signos = 0; // signos especiales

        for (int i = 0; i < longtd; i++) {
            if (Character.isDigit(pass.charAt(i)))
                nums++;
            if (Character.isLetter(pass.charAt(i)))
                letras++;
            if (")(][}{$@#%".indexOf(pass.charAt(i)) >= 0)
                signos++;
        }

        // Debe haber al menos uno de cada para que la contraseña sea valida pero la
        // longitud mínima es 8 por lo qeu obliga a la existencia de más de uno de al menos un tipo
        return letras > 0 && nums > 0 && signos > 0;
    }

    private void back() {
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private void changeVisibility(boolean visible) {
        binding.top.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.scroll.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
}
