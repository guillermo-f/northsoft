/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                              Adaptador para ListView
:*
:* Archivo:      FragmentMensajesAdapter.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        28-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 29/05/2020       Franco, Carranza, Castillo      Creación del archivo
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
import gps.gmv.akista.databinding.FragmentMensajesAdapterBinding;

public class FragmentMensajesAdapter extends ArrayAdapter<String> {

    public FragmentMensajesAdapter(Context context, List<String> remitentes) {
        super(context, 0, remitentes);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from((parent.getContext()));
        FragmentMensajesAdapterBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mensajes_adapter,null,false);

        // Se inserta el nombre del usuario miembro de la conversación mediante databinding
        binding.setRemitente(getItem(position));

        return binding.getRoot();
    }
}
