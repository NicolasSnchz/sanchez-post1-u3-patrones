package com.universidad.confudes.asistencia;

import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import com.universidad.confudes.externo.qrcheck.QRCheckRequest;
import com.universidad.confudes.externo.qrcheck.QRCheckResponse;
import org.springframework.stereotype.Service;

/**
 * Necesidad 1 - Adapter.
 *
 * Traduce el contrato interno ServicioAsistencia (el unico que conoce
 * ControladorCheckIn y el modulo de reportes) al contrato del SDK externo
 * QRCheckAPI. Ni el SDK ni el contrato interno pueden modificarse, asi que
 * toda la incompatibilidad se resuelve aqui:
 *
 *   - eventoId llega como String y QRCheckRequest exige un long.
 *   - credencialQR debe viajar como payload con el prefijo que espera el proveedor.
 *   - el codigo numerico de respuesta (200 / 401 / cualquier otro) se traduce
 *     a un ResultadoCheckIn, que es lo que el controlador sabe interpretar.
 */
@Service
public class AdaptadorQRCheck implements ServicioAsistencia {

    private static final String PREFIJO_PROVEEDOR = "QR-";

    private final QRCheckClient clienteExterno;

    public AdaptadorQRCheck(QRCheckClient clienteExterno) {
        this.clienteExterno = clienteExterno;
    }

    @Override
    public ResultadoCheckIn registrarAsistencia(String eventoId, String participanteId, String credencialQR) {
        if (credencialQR == null || credencialQR.isBlank()) {
            return new ResultadoCheckIn(false, "No se recibio una credencial QR para el participante " + participanteId);
        }

        QRCheckRequest peticion = new QRCheckRequest(
                normalizarPayload(credencialQR),
                convertirIdEvento(eventoId));

        QRCheckResponse respuesta = clienteExterno.validar(peticion);
        return traducirRespuesta(respuesta, participanteId);
    }

    /**
     * El proveedor exige que el payload empiece con "QR-". El adaptador normaliza
     * el prefijo cuando la credencial interna ya lo trae en cualquier combinacion
     * de mayusculas y minusculas, pero nunca lo inventa: una cadena que no es una
     * credencial QR debe seguir siendo rechazada por el proveedor.
     */
    private String normalizarPayload(String credencialQR) {
        String limpia = credencialQR.trim();
        if (limpia.regionMatches(true, 0, PREFIJO_PROVEEDOR, 0, PREFIJO_PROVEEDOR.length())) {
            return PREFIJO_PROVEEDOR + limpia.substring(PREFIJO_PROVEEDOR.length());
        }
        return limpia;
    }

    /**
     * Los identificadores internos tienen forma "EVT-001" y el SDK espera un long.
     * Se extraen los digitos del identificador; si no hay ninguno se envia 0,
     * valor que el proveedor trata como evento no identificado.
     */
    private long convertirIdEvento(String eventoId) {
        if (eventoId == null) {
            return 0L;
        }
        String digitos = eventoId.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(digitos);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    private ResultadoCheckIn traducirRespuesta(QRCheckResponse respuesta, String participanteId) {
        int codigo = respuesta.getCodigoRespuesta();
        if (codigo == 200) {
            return new ResultadoCheckIn(true, "Asistencia registrada para " + participanteId
                    + ". " + respuesta.getDetalle());
        }
        if (codigo == 401) {
            return new ResultadoCheckIn(false, "Credencial invalida. " + respuesta.getDetalle());
        }
        return new ResultadoCheckIn(false, "Respuesta inesperada del proveedor (codigo " + codigo
                + "). " + respuesta.getDetalle());
    }
}
