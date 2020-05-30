/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                              Adaptador para ListView
:*
:* Archivo:      FragmentCalificacionesAdapter.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        26-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 26/05/2020       Franco, Carranza, Castillo      Creación del archivo
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
import gps.gmv.akista.databinding.FragmentCalificacionesAdapterBinding;
import gps.gmv.akista.entidades.Calificacion;

public class FragmentCalificacionesAdapter extends ArrayAdapter<Calificacion> {

    public FragmentCalificacionesAdapter(Context context, List calificaciones) {
        super(context, 0, calificaciones);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from((parent.getContext()));
        FragmentCalificacionesAdapterBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calificaciones_adapter,null,false);

        binding.setCalificacion(getItem(position));

        return binding.getRoot();
    }
}
