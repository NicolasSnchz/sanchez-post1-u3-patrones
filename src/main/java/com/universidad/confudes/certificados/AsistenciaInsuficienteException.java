package com.universidad.confudes.certificados;

/**
 * Se lanza cuando el participante no alcanza el porcentaje minimo de asistencia
 * exigido para recibir el certificado. Permite que la fachada exponga una sola
 * operacion y que el controlador siga respondiendo 403 sin conocer al validador.
 */
public class AsistenciaInsuficienteException extends RuntimeException {
    public AsistenciaInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
