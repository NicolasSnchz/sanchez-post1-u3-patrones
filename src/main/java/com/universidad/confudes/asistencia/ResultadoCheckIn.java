package com.universidad.confudes.asistencia;

public class ResultadoCheckIn {
    private final boolean exitoso;
    private final String mensaje;

    public ResultadoCheckIn(boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }
    public boolean isExitoso() { return exitoso; }
    public String getMensaje() { return mensaje; }
}
