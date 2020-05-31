/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                Fragment para registro de eventos
:*
:* Archivo:      FragmentRegistroEventos.java
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
:* 27/05/2020       Franco, Carranza, Castillo      Implementación de funcionalidad
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentRegistroEventosBinding;
import gps.gmv.akista.entidades.Evento;

public class FragmentRegistroEventos extends Fragment  implements DatePickerDialog.OnDateSetListener,
                                                                  TimePickerDialog.OnTimeSetListener {

    private FragmentRegistroEventosBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_registro_eventos, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        binding.setEvento(new Evento());
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.btVolver.setOnClickListener(v -> getFragmentManager().popBackStack());
        // Para elegir la fecha un evento se abre el FragmentEligeFecha donde se muestra un calendario
        binding.btAbreCalendario.setOnClickListener(v -> new FragmentEligeFecha(this).show(getFragmentManager(), null));
        // Para elegir la hora un evento se abre el FragmentEligeHora donde se muestra un reloj
        binding.btAbreReloj.setOnClickListener(v -> new FragmentEligeHora(this).show(getFragmentManager(), null));
        binding.btEnviar.setOnClickListener(v -> registro());
    }

    private void registro() {
        // Se llama a la comprobación de errores
        HashMap<Integer, String> checks = check();

        if (checks.isEmpty()) {
            // De no haber errores...
            changeVisibility(false);

            binding.getEvento().setFechaHora(parse()); // El valor de la fecha y hora se establece
                                                       // mediante un valor del tipo long por lo que
                                                       // las cadenas de fecha y hora se deben convertir

            // Se inserta el valor en la base de datos
            FirebaseDatabase.getInstance()
            .getReference("evento")
            .child(binding.getEvento().getId())
            .setValue(binding.getEvento())
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    binding.working.setVisibility(View.GONE);
                    binding.success.setVisibility(View.VISIBLE);
                } else {
                    Snackbar.make(getView(), "Registro de evento fallido", Snackbar.LENGTH_LONG).show();
                    changeVisibility(true);
                }
            });
        } else {
            // Existencia de errores
            binding.etNom.setError(checks.get(1));
            binding.btAbreCalendario.setError(checks.get(2));
            binding.btAbreReloj.setError(checks.get(3));
            binding.etMsj.setError(checks.get(4));
        }
    }

    // Comprobación de errores
    private HashMap<Integer, String> check() {
        HashMap<Integer, String> values = new HashMap<>();

        if (binding.getEvento().getNombre().isEmpty())
            values.put(1, "Escriba el nombre del evento");

        if (binding.getFecha() == null)
            values.put(2, "Elija una fecha");

        if (binding.getHora() == null)
            values.put(3, "Elija una hora");

        if (binding.getEvento().getMotivo().isEmpty())
            values.put(4, "Escriba el motivo del evento a realizar");

        return values;
    }

    // Conversión de una fecha en string a long
    private long parse() {
        long longDate = 0L;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            longDate = sdf.parse(binding.getFecha() + " " + binding.getHora()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return longDate;
    }

    // Listener del Dialog Fragment que converte los valores de año, mes y día a una cadena con
    // formato dd/mm/aaaa
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        month++;
        binding.setFecha(
            (day < 10 ? "0" + day : "" + day) + "/" +
            (month < 10 ? "0" + month : "" + month) + "/" +
            year
        );
    }

    // Listener del Dialog Fragment que convierte los valores de la hora a una cadena con
    // formato hh:mm
    @Override
    public void onTimeSet(TimePicker timePicker, int h, int m) {
        binding.setHora(
            (h < 10 ? "0" + h : "" + h) + ":" +
            (m < 10 ? "0" + m : "" + m)
        );
    }

    private void changeVisibility(boolean visible) {
        binding.top.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.scroll.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }
}
