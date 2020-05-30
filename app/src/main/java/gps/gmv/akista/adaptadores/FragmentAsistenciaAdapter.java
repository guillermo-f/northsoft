/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                              Adaptador para ListView
:*
:* Archivo:      FragmentAsistenciaAdapter.java
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

import java.util.HashMap;
import java.util.List;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.FragmentAsistenciaAdapterBinding;
import gps.gmv.akista.entidades.Alumno;

public class FragmentAsistenciaAdapter extends ArrayAdapter<Alumno> {

    private HashMap<String, Integer> faltas, justificaciones;

    public FragmentAsistenciaAdapter(Context context, List<Alumno> alumnos) {
        super(context, 0, alumnos);
        faltas = new HashMap<>();
        justificaciones = new HashMap<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from((parent.getContext()));
        FragmentAsistenciaAdapterBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_asistencia_adapter,null,false);


        binding.setAlumno(getItem(position));
        binding.setFaltas(faltas.get(binding.getAlumno().getCurp()) != null ? faltas.get(binding.getAlumno().getCurp()) : 0);
        binding.setJustificaciones(justificaciones.get(binding.getAlumno().getCurp()) != null ? justificaciones.get(binding.getAlumno().getCurp()) : 0);

        return binding.getRoot();
    }

    public void setData(HashMap<String, Integer> faltas, HashMap<String, Integer> justificaciones) {
        this.faltas = faltas;
        this.justificaciones = justificaciones;
    }
}
