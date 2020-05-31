/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                              Adaptador para ListView
:*
:* Archivo:      FragmentMensajeAdapter.java
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentMensajeAdapterBinding;
import gps.gmv.akista.entidades.Mensaje;
import gps.gmv.akista.otros.Singleton;

public class FragmentMensajeAdapter extends ArrayAdapter<Mensaje> {

    private String otr; // otr es la cadena que corresponde al nombre
                        // del otro usuario de la conversación

    public FragmentMensajeAdapter(Context context, List<Mensaje> mensajes, String otr) {
        super(context, 0, mensajes);
        this.otr = otr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from((parent.getContext()));
        FragmentMensajeAdapterBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_mensaje_adapter,null,false);

        Mensaje m = getItem(position);

        binding.setMsj(m);
        binding.setFecha(Singleton.getInstance().parseDate(m.getFecha()));

        // Si el ID del remitente es diferente del ID del usuario que usa la app
        // el nombre que aparecerá en el mensaje es el del otro usuario
        if (!m.getIdRemitente().equals(FirebaseAuth.getInstance().getUid()))
            binding.setRemitente(otr);

        return binding.getRoot();
    }
}
