package com.universidad.confudes.asistencia;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

// Ya en produccion - no modificar. Depende unicamente de ServicioAsistencia.
@RestController
@RequestMapping("/api/checkin")
public class ControladorCheckIn {
    private final ServicioAsistencia servicioAsistencia;

    public ControladorCheckIn(ServicioAsistencia servicioAsistencia) {
        this.servicioAsistencia = servicioAsistencia;
    }

    @PostMapping
    public ResponseEntity<ResultadoCheckIn> registrar(@RequestParam String eventoId,
                                                      @RequestParam String participanteId,
                                                      @RequestParam String credencialQR) {
        ResultadoCheckIn resultado =
            servicioAsistencia.registrarAsistencia(eventoId, participanteId, credencialQR);
        return resultado.isExitoso() ? ResponseEntity.ok(resultado) : ResponseEntity.status(422).body(resultado);
    }
}
