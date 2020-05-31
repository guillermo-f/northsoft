/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*              Fragment de calendario para eventos programados por la escuela
:*
:* Archivo:      FragmentCalendarioEventos.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        13-05-2020
:* Compilador:   JDK 8
:* Ultima modif: 25-05-2020
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 13/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 25/05/2020       Franco, Carranza, Castillo      Implementación de la funcionalidad
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import gps.gmv.akista.R;
import gps.gmv.akista.adaptadores.FragmentCalendarioEventosAdapter;
import gps.gmv.akista.databinding.FragmentCalendarioEventosBinding;
import gps.gmv.akista.entidades.Evento;

public class FragmentCalendarioEventos extends Fragment {

    private FragmentCalendarioEventosAdapter adapter;
    private FragmentCalendarioEventosBinding binding;

    private ArrayList<Evento> _events; // Aquí se guarda la información de los eventos almacenados
                                       // en el día seleccionado dentro del calendario

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendario_eventos, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        init();
        setListeners();
    }

    private void init() {
        _events = new ArrayList<>();
        adapter = new FragmentCalendarioEventosAdapter(getContext(), _events);
        binding.listView.setAdapter(adapter);
        binding.calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        binding.calendar.setUseThreeLetterAbbreviation(true);
        downloadData();
        setMonth(Calendar.getInstance().get(Calendar.MONTH)); // Se establece el mes en el layout
                                                              // mediante el metodo setMonth
                                                              // ya que no se muestra en la vista
    }

    private void setListeners() {
        binding.btRegresar.setOnClickListener(v -> getFragmentManager().popBackStack());
        binding.calendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date date) {
                displayEvents(date); // Cuando se selecciona un día se muestran los eventos
                                     // almacenados en el
            }

            @Override public void onMonthScroll(Date firstDayOfNewMonth) {
                // Al hacer scroll entre meses se actualiza tanto el nombre del mes mostrado
                // como los eventos mostrados en la vista ya que al hacer scroll se selecciona
                // el día primero de cada mes
                setMonth(firstDayOfNewMonth.getMonth());
                displayEvents(firstDayOfNewMonth);
            }
        });
    }

    private void downloadData() {
        changeVisibility(false);

        FirebaseDatabase.getInstance()
        .getReference("evento")
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    // Se baja desde el espacio de "evento" todos los eventos
                    // y se almacenan en el calendario para posteriormente mostrarlos
                    // al hacer clic en algún día
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Evento e = snapshot.getValue(Evento.class);
                        binding.calendar.addEvent(new Event(Color.WHITE, e.getFechaHora(), e));
                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Snackbar.make(getView(), databaseError.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

        changeVisibility(true);
    }

    // Método para esconder los elementos de la vista y mostrar un elemento que alude a un "cargando"
    private void changeVisibility(boolean visible) {
        binding.dummy.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.calendar.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.listView.setVisibility(visible ? View.VISIBLE : View.GONE);
        binding.working.setVisibility(visible ? View.GONE : View.VISIBLE);
    }

    // Según el valor recibido se regresa una cadena
    private void setMonth(int m) {
        switch (m) {
            case 0:
                binding.setMes("Enero");
                break;
            case 1:
                binding.setMes("Febrero");
                break;
            case 2:
                binding.setMes("Marzo");
                break;
            case 3:
                binding.setMes("Abril");
                break;
            case 4:
                binding.setMes("Mayo");
                break;
            case 5:
                binding.setMes("Junio");
                break;
            case 6:
                binding.setMes("Julio");
                break;
            case 7:
                binding.setMes("Agosto");
                break;
            case 8:
                binding.setMes("Septiembre");
                break;
            case 9:
                binding.setMes("Octubre");
                break;
            case 10:
                binding.setMes("Noviembre");
                break;
            case 11:
                binding.setMes("Diciembre");
                break;
        }
    }

    // Actualiza los eventos a mostrar y se obtienen del calendario a través del día seleccionado
    private void displayEvents(Date date) {
        List<Event> events = binding.calendar.getEvents(date);
        _events.clear();

        for (Event e : events)
            _events.add((Evento) e.getData());

        adapter.notifyDataSetChanged();
    }
}
