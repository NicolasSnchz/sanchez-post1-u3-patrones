package com.universidad.confudes.acceso;

import com.universidad.confudes.certificados.ServicioCertificados;
import com.universidad.confudes.certificados.SolicitudCertificado;

import java.util.Set;

/**
 * Necesidad 4 - Proxy de proteccion.
 *
 * Implementa el mismo contrato ServicioCertificados, asi que el resto del sistema
 * lo inyecta sin enterarse de que existe una verificacion adicional. A diferencia
 * de una mejora de la Necesidad 3, este objeto decide si delega o no: cuando el rol
 * no esta autorizado nunca llega a invocarse la emision real, que es costosa porque
 * consume el limite de 60 llamadas por minuto del proveedor de firma digital.
 */
public class ProxyControlAccesoCertificados implements ServicioCertificados {

    private static final Set<String> ROLES_AUTORIZADOS = Set.of("ORGANIZADOR", "ADMIN");

    private final ServicioCertificados servicioReal;

    public ProxyControlAccesoCertificados(ServicioCertificados servicioReal) {
        this.servicioReal = servicioReal;
    }

    @Override
    public byte[] emitir(SolicitudCertificado solicitud) {
        String rol = ContextoUsuario.rolActual();
        if (rol == null || !ROLES_AUTORIZADOS.contains(rol.trim().toUpperCase())) {
            throw new SecurityException("El rol " + rol
                    + " no puede solicitar la descarga masiva de certificados del evento "
                    + solicitud.getEventoId());
        }
        return servicioReal.emitir(solicitud);
    }
}
