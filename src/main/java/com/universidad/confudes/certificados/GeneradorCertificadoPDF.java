package com.universidad.confudes.certificados;

public class GeneradorCertificadoPDF {
    public byte[] iniciarDocumento(String plantillaId) {
        System.out.println("Iniciando documento con plantilla " + plantillaId);
        return new byte[0];
    }
    public void insertarDatosParticipante(byte[] documentoBase, String nombre, String evento, String fecha) {
        System.out.println("Insertando datos de " + nombre + " en el documento");
    }
    public byte[] finalizarDocumento() {
        System.out.println("Documento finalizado");
        return "PDF-CONTENIDO".getBytes();
    }
}
