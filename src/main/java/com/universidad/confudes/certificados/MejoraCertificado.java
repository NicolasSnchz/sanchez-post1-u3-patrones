package com.universidad.confudes.certificados;

/**
 * Necesidad 3 - Decorator (clase base).
 *
 * Cada mejora implementa el mismo contrato ServicioCertificados y envuelve a otro
 * ServicioCertificados, que puede ser la fachada base o una mejora ya aplicada.
 * Eso permite apilarlas en cualquier orden y en cualquier combinacion sin crear
 * una clase por combinacion y sin tocar la fachada de la Necesidad 2.
 */
public abstract class MejoraCertificado implements ServicioCertificados {

    protected final ServicioCertificados envuelto;

    protected MejoraCertificado(ServicioCertificados envuelto) {
        if (envuelto == null) {
            throw new IllegalArgumentException("Una mejora siempre envuelve a otro ServicioCertificados");
        }
        this.envuelto = envuelto;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        byte[] documento = envuelto.emitir(solicitud);
        return aplicarMejora(documento, solicitud);
    }

    protected abstract byte[] aplicarMejora(byte[] documento, SolicitudCertificado solicitud);
}
