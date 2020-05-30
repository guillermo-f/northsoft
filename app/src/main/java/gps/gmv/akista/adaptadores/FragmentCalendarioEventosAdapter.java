/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                              Adaptador para ListView
:*
:* Archivo:      FragmentCalendarioEventosAdapter.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        25-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 25/05/2020       Franco, Carranza, Castillo      Creación del archivo
:*==========================================================================================*/

package gps.gmv.akista.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.databinding.DataBindingUtil;

import java.util.List;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentCalendarioEventosAdapterBinding;
import gps.gmv.akista.entidades.Evento;
import gps.gmv.akista.otros.Singleton;

public class FragmentCalendarioEventosAdapter extends ArrayAdapter<Evento> {

    public FragmentCalendarioEventosAdapter(Context context, List<Evento> eventos) {
        super(context, 0, eventos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from((parent.getContext()));
        FragmentCalendarioEventosAdapterBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendario_eventos_adapter,null,false);

        binding.setEvento(getItem(position));
        binding.setFecha(Singleton.getInstance().parseDate(getItem(position).getFechaHora()));

        return binding.getRoot();
    }
}
