package gps.gmv.akista.otros;

import gps.gmv.akista.entidades.Usuario;

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
}
