/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                         Actividad de registro de calificaciones
:*
:* Archivo:      ActivityRegistraCalificaciones.java
:* Autor:        Guillermo Franco Alemán           16130804
:*               Miguel Angel Carranza Esquivel    16130790
:*               Victor Alberto Castillo Rivera    17130016
:*
:* Fecha:        14-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 14/05/2020       Franco, Carranza, Castillo      Creación del archivo
:*==========================================================================================*/

package gps.gmv.akista.actividades;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import gps.gmv.akista.R;
import gps.gmv.akista.databinding.ActivityRegistraCalificacionesBinding;

public class ActivityRegistraCalificaciones extends AppCompatActivity {

    private ActivityRegistraCalificacionesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_registra_calificaciones);
    }
}
