package com.universidad.confudes.certificados;

public class MejoraMarcaDeAgua extends MejoraCertificado {

    private static final String TEXTO_POR_DEFECTO = "Universidad de Santander - UDES";

    private final String texto;

    public MejoraMarcaDeAgua(ServicioCertificados envuelto) {
        this(envuelto, TEXTO_POR_DEFECTO);
    }

    public MejoraMarcaDeAgua(ServicioCertificados envuelto, String texto) {
        super(envuelto);
        this.texto = texto;
    }

    @Override
    protected byte[] aplicarMejora(byte[] documento, SolicitudCertificado solicitud) {
        return UtilidadesPDF.aplicarMarcaDeAgua(documento, texto);
    }
}
