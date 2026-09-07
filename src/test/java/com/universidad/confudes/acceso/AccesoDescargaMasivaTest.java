package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccesoDescargaMasivaTest {

    @AfterEach
    void limpiarRol() {
        System.clearProperty("confudes.rol");
    }

    private ServicioCertificados nuevoControlado(ServicioCertificados real) {
        return new ProxyControlAccesoCertificados(real);
    }

    private ServicioCertificados nuevaFachada() {
        return new FachadaEmisionCertificados(new ValidadorAsistencia(), new GeneradorCertificadoPDF(),
                new FirmaDigitalService(), new EnvioCorreoService());
    }

    @Test
    void rechazaAParticipanteSinLlegarAEmitir() {
        System.setProperty("confudes.rol", "PARTICIPANTE");
        ServicioCertificados controlado = nuevoControlado(nuevaFachada());
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertThrows(SecurityException.class, () -> controlado.emitir(solicitud));
    }

    @Test
    void permiteAOrganizador() {
        System.setProperty("confudes.rol", "ORGANIZADOR");
        ServicioCertificados controlado = nuevoControlado(nuevaFachada());
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertDoesNotThrow(() -> controlado.emitir(solicitud));
    }

    @Test
    void noEjecutaLaOperacionCostosaCuandoElRolNoEstaAutorizado() {
        System.setProperty("confudes.rol", "PARTICIPANTE");
        final boolean[] seEjecuto = {false};
        ServicioCertificados espia = solicitud -> {
            seEjecuto[0] = true;
            return new byte[0];
        };
        ServicioCertificados controlado = nuevoControlado(espia);
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-001", "PART-123", "Ana", "ana@correo.com");
        assertThrows(SecurityException.class, () -> controlado.emitir(solicitud));
        assertFalse(seEjecuto[0]);
    }

    @Test
    void permiteAAdmin() {
        System.setProperty("confudes.rol", "ADMIN");
        ServicioCertificados controlado = nuevoControlado(nuevaFachada());
        SolicitudCertificado solicitud = new SolicitudCertificado("EVT-002", "PART-456", "Luis", "luis@correo.com");
        assertDoesNotThrow(() -> controlado.emitir(solicitud));
    }
}
