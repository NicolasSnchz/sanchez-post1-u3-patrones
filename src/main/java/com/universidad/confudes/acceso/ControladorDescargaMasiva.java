package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.ServicioCertificados;
import com.universidad.confudes.certificados.SolicitudCertificado;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Descarga masiva de los certificados de un evento en un unico .zip.
 * Inyecta un ServicioCertificados normal: no sabe que detras hay un proxy que
 * verifica el rol antes de dejar pasar la operacion.
 */
@RestController
@RequestMapping("/api/certificados/masivo")
public class ControladorDescargaMasiva {

    private final ServicioCertificados servicioCertificados;

    public ControladorDescargaMasiva(
            @Qualifier("servicioDescargaMasiva") ServicioCertificados servicioCertificados) {
        this.servicioCertificados = servicioCertificados;
    }

    @PostMapping("/{eventoId}")
    public ResponseEntity<byte[]> descargar(@PathVariable String eventoId,
                                            @RequestParam List<String> participantes,
                                            @RequestParam String correoDestino) {
        try {
            return ResponseEntity.ok(comprimir(eventoId, participantes, correoDestino));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage().getBytes());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("No fue posible construir el archivo".getBytes());
        }
    }

    private byte[] comprimir(String eventoId, List<String> participantes, String correoDestino)
            throws IOException {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(salida)) {
            for (String participanteId : participantes) {
                byte[] certificado = servicioCertificados.emitir(
                        new SolicitudCertificado(eventoId, participanteId, participanteId, correoDestino));
                zip.putNextEntry(new ZipEntry("certificado-" + participanteId + ".pdf"));
                zip.write(certificado);
                zip.closeEntry();
            }
        }
        return salida.toByteArray();
    }
}
