package com.universidad.confudes.acceso;

// Provisto por el modulo de autenticacion de ConfUDES - no modificar.
// En produccion lee el rol desde el token de la peticion actual.
public class ContextoUsuario {
    public static String rolActual() {
        return System.getProperty("confudes.rol", "PARTICIPANTE");
    }
}
