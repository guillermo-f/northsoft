/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                          Fragment para elegir la hora de un evento
:*
:* Archivo:      FragmentEligeFecha.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        27-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 27/05/2020       Franco, Carranza, Castillo      Creación del archivo
:*==========================================================================================*/

package gps.gmv.akista.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class FragmentEligeHora extends DialogFragment {

    FragmentRegistroEventos parent;

    public FragmentEligeHora(FragmentRegistroEventos parent) {
        this.parent = parent;
    }

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        Calendar cal = Calendar.getInstance();
        return new TimePickerDialog(
            getContext(),
            parent,
            cal.get(Calendar.HOUR),
            cal.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(getContext())
        );
    }
}
