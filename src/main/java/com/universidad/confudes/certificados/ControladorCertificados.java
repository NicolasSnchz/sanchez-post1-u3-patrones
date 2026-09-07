package com.universidad.confudes.certificados;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

// Refactorizado: depende de un unico colaborador (ServicioCertificados) y ya no
// conoce al validador, al generador, al firmador ni al servicio de correo.
@RestController
@RequestMapping("/api/certificados")
public class ControladorCertificados {

    private final ServicioCertificados servicioCertificados;

    public ControladorCertificados(ServicioCertificados servicioCertificados) {
        this.servicioCertificados = servicioCertificados;
    }

    @PostMapping("/{eventoId}/{participanteId}")
    public ResponseEntity<String> emitir(@PathVariable String eventoId, @PathVariable String participanteId,
                                         @RequestParam String nombre, @RequestParam String correoDestino) {
        try {
            servicioCertificados.emitir(
                    new SolicitudCertificado(eventoId, participanteId, nombre, correoDestino));
            return ResponseEntity.ok("Certificado emitido y enviado");
        } catch (AsistenciaInsuficienteException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }
}
