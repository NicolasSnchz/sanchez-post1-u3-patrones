package com.universidad.confudes.certificados;

// Utilidades de bajo nivel ya probadas - usarlas, no reimplementar la logica
// de PDF. No modificar esta clase.
public final class UtilidadesPDF {
    private UtilidadesPDF() {}

    public static byte[] aplicarMarcaDeAgua(byte[] documento, String texto) {
        System.out.println("Aplicando marca de agua: " + texto);
        return documento; // simulacion
    }
    public static byte[] insertarCodigoQR(byte[] documento, String urlVerificacion) {
        System.out.println("Insertando QR de verificacion: " + urlVerificacion);
        return documento;
    }
    public static byte[] traducirAIngles(byte[] documento) {
        System.out.println("Traduciendo certificado al ingles");
        return documento;
    }
}
