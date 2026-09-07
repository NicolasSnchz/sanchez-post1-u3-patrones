package com.universidad.confudes.certificados;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MejorasCertificadoTest {

    private final SolicitudCertificado solicitud =
        new SolicitudCertificado("EVT-001", "PART-123", "Ana Rios", "ana@correo.com");

    private ServicioCertificados nuevaFachada() {
        return new FachadaEmisionCertificados(new ValidadorAsistencia(), new GeneradorCertificadoPDF(),
                new FirmaDigitalService(), new EnvioCorreoService());
    }

    @Test
    void emiteSinNingunaMejoraActivada() {
        ServicioCertificados base = nuevaFachada();
        assertDoesNotThrow(() -> base.emitir(solicitud));
    }

    @Test
    void combinaLasTresMejorasSinCrearUnaClaseNueva() {
        ServicioCertificados conTodo =
                new MejoraTraduccionIngles(
                    new MejoraCodigoQR(
                        new MejoraMarcaDeAgua(nuevaFachada())));
        assertDoesNotThrow(() -> conTodo.emitir(solicitud));
    }

    @Test
    void unaSolaMejoraFuncionaDeFormaIndependiente() {
        ServicioCertificados soloMarcaDeAgua = new MejoraMarcaDeAgua(nuevaFachada());
        assertDoesNotThrow(() -> soloMarcaDeAgua.emitir(solicitud));
    }

    @Test
    void elOrdenDeLasMejorasEsIntercambiable() {
        ServicioCertificados ordenA = new MejoraMarcaDeAgua(new MejoraTraduccionIngles(nuevaFachada()));
        ServicioCertificados ordenB = new MejoraTraduccionIngles(new MejoraMarcaDeAgua(nuevaFachada()));
        assertDoesNotThrow(() -> ordenA.emitir(solicitud));
        assertDoesNotThrow(() -> ordenB.emitir(solicitud));
    }
}
