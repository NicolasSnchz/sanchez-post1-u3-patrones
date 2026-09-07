package com.universidad.confudes.asistencia;

// Contrato interno - usado por ControladorCheckIn y por el modulo de
// reportes de asistencia (no incluido en este laboratorio). No modificar.
public interface ServicioAsistencia {
    ResultadoCheckIn registrarAsistencia(String eventoId, String participanteId, String credencialQR);
}
