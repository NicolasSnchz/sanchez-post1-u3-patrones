package com.universidad.confudes.asistencia;

import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckInIntegracionTest {

    private ServicioAsistencia nuevoServicio() {
        return new AdaptadorQRCheck(new QRCheckClient());
    }

    @Test
    void registraAsistenciaConCredencialValida() {
        ServicioAsistencia servicio = nuevoServicio();
        ResultadoCheckIn resultado = servicio.registrarAsistencia("EVT-001", "PART-123", "QR-abc123");
        assertTrue(resultado.isExitoso());
    }

    @Test
    void rechazaCredencialInvalidaSinLanzarExcepcion() {
        ServicioAsistencia servicio = nuevoServicio();
        ResultadoCheckIn resultado = servicio.registrarAsistencia("EVT-001", "PART-999", "no-es-un-qr");
        assertFalse(resultado.isExitoso());
    }

    @Test
    void rechazaCredencialVaciaSinLlamarAlProveedor() {
        ServicioAsistencia servicio = nuevoServicio();
        ResultadoCheckIn resultado = servicio.registrarAsistencia("EVT-001", "PART-777", "   ");
        assertFalse(resultado.isExitoso());
        assertNotNull(resultado.getMensaje());
    }
}
