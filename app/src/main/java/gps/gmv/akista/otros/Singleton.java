/*==========================================================================================
:*                          INSTITUTO TECNOLOGICO DE LA LAGUNA
:*                         INGENIERIA EN SISTEMAS COMPUTACIONALES
:*                            GESTIÓN DE PROYECTOS DE SOFTWARE
:*
:*                         SEMESTRE: ENE-JUN/2020  HORA: 10-11 HRS
:*
:*                  Clase Singleton para almacenamiento de datos que se
:*                  usarán durante el runtime de la app o métodos varios
:*
:* Archivo:      Singleton.java
:* Autor:        Guillermo Franco Alemán            16130804
:*               Miguel Angel Carranza Esquivel     16130790
:*               Victor Alberto Castillo Rivera     17130016
:*
:* Fecha:        21-05-2020
:* Compilador:   JDK 8
:* Ultima modif: 25-05-2020
:*
:* Fecha            Modificó                        Motivo
:*==========================================================================================
:* 21/05/2020       Franco, Carranza, Castillo      Creación del archivo
:* 25/05/2020       Franco, Carranza, Castillo      Se agregó el método parseDate para
:*                                                  convertir valores del tipo long a fechas
:*==========================================================================================*/

package gps.gmv.akista.otros;

import java.text.SimpleDateFormat;
import java.util.Date;

import gps.gmv.akista.entidades.Usuario;

// En este Singleton se almacena el objeto Usuario perteneciente a quien inicie sesión dentro de la misma
public class Singleton {

    private Usuario usuario;
    private static Singleton instance;

    private Singleton() {}

    public static Singleton getInstance() {
        return instance == null ? (instance = new Singleton()) : instance;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    // Además contiene un metodo que convierte los valores 'long' a su equivalente en una fecha string
    public String parseDate(long fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date resultdate = new Date(fecha);
        return sdf.format(resultdate).toUpperCase();
    }
}
