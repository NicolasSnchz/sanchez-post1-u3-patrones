package com.universidad.confudes.certificados;

import java.time.LocalDate;

/**
 * Necesidad 2 - Facade.
 *
 * Unico colaborador que ControladorCertificados necesita conocer. Recibe los
 * cuatro servicios existentes (que siguen siendo usados directamente por otros
 * modulos y por eso no se fusionan ni se modifican) y encapsula la secuencia
 * completa validar -> generar -> firmar -> enviar detras de una sola operacion.
 */
public class FachadaEmisionCertificados implements ServicioCertificados {

    private static final String PLANTILLA = "plantilla-2026";
    private static final String CERTIFICADO_INSTITUCIONAL = "cert-udes-2026.pfx";
    private static final double ASISTENCIA_MINIMA = 0.8;

    private final ValidadorAsistencia validador;
    private final GeneradorCertificadoPDF generador;
    private final FirmaDigitalService firma;
    private final EnvioCorreoService correo;

    public FachadaEmisionCertificados(ValidadorAsistencia validador,
                                      GeneradorCertificadoPDF generador,
                                      FirmaDigitalService firma,
                                      EnvioCorreoService correo) {
        this.validador = validador;
        this.generador = generador;
        this.firma = firma;
        this.correo = correo;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        if (!validador.tieneAsistenciaMinima(solicitud.getParticipanteId(),
                                             solicitud.getEventoId(), ASISTENCIA_MINIMA)) {
            throw new AsistenciaInsuficienteException(
                    "Asistencia insuficiente para el participante " + solicitud.getParticipanteId());
        }

        byte[] documentoFinal = generarDocumento(solicitud);
        byte[] documentoFirmado = firmarDocumento(documentoFinal);
        enviarPorCorreo(solicitud, documentoFirmado);
        return documentoFirmado;
    }

    private byte[] generarDocumento(SolicitudCertificado solicitud) {
        byte[] base = generador.iniciarDocumento(PLANTILLA);
        generador.insertarDatosParticipante(base, solicitud.getNombre(),
                solicitud.getEventoId(), LocalDate.now().toString());
        return generador.finalizarDocumento();
    }

    private byte[] firmarDocumento(byte[] documento) {
        FirmaDigitalService.Sesion sesion = firma.abrirSesion(CERTIFICADO_INSTITUCIONAL);
        try {
            return firma.firmar(sesion, documento);
        } finally {
            firma.cerrarSesion(sesion);
        }
    }

    private void enviarPorCorreo(SolicitudCertificado solicitud, byte[] documentoFirmado) {
        correo.adjuntarArchivo(solicitud.getCorreoDestino(), documentoFirmado,
                "certificado-" + solicitud.getParticipanteId() + ".pdf");
        correo.enviar("Su certificado de participacion",
                "Adjunto encontrara su certificado del evento " + solicitud.getEventoId() + ".");
    }
}
