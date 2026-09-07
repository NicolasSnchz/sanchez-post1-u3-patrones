package com.universidad.confudes.certificados;

public class SolicitudCertificado {
    private final String eventoId;
    private final String participanteId;
    private final String nombre;
    private final String correoDestino;

    public SolicitudCertificado(String eventoId, String participanteId, String nombre, String correoDestino) {
        this.eventoId = eventoId;
        this.participanteId = participanteId;
        this.nombre = nombre;
        this.correoDestino = correoDestino;
    }
    public String getEventoId() { return eventoId; }
    public String getParticipanteId() { return participanteId; }
    public String getNombre() { return nombre; }
    public String getCorreoDestino() { return correoDestino; }
}
