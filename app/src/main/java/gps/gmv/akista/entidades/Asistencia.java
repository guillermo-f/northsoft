/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                          Entidad que modela a una asistencia
:*
:* Archivo:      Asistencia.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        21-05-2020
:* Compilador:   JDK 8
:* Ultima modif: -
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 21/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 25/05/2020       Franco, Carranza, Castillo      Añadido el campo 'grupo'
:*==========================================================================================*/

package gps.gmv.akista.entidades;

import gps.gmv.akista.otros.Singleton;

public class Asistencia {

    private String  idAlumno,
                    grupo;
    private int     asistencia;
    private long    fecha;

    public static final int ASISTIO = 0,
                            FALTA = 1,
                            JUSTIF = 2;

    public Asistencia() {
        idAlumno = "";
        grupo = "";
        asistencia = ASISTIO;
        fecha = System.currentTimeMillis();
    }

    public String getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(String idAlumno) {
        this.idAlumno = idAlumno;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public int getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(int asistencia) {
        this.asistencia = asistencia;
    }

    public long getFecha() {
        return fecha;
    }

    public void setFecha(long fecha) {
        this.fecha = fecha;
    }
}
