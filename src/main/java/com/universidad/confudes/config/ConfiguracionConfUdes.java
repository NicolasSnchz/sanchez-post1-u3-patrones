package com.universidad.confudes.config;

import com.universidad.confudes.acceso.ProxyControlAccesoCertificados;
import com.universidad.confudes.certificados.*;
import com.universidad.confudes.externo.qrcheck.QRCheckClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Registra como beans las clases dadas (que no pueden anotarse porque no se
 * modifican) y arma las composiciones de las Necesidades 3 y 4.
 */
@Configuration
public class ConfiguracionConfUdes {

    @Bean
    public QRCheckClient qrCheckClient() {
        return new QRCheckClient();
    }

    @Bean
    public ValidadorAsistencia validadorAsistencia() {
        return new ValidadorAsistencia();
    }

    @Bean
    public GeneradorCertificadoPDF generadorCertificadoPDF() {
        return new GeneradorCertificadoPDF();
    }

    @Bean
    public FirmaDigitalService firmaDigitalService() {
        return new FirmaDigitalService();
    }

    @Bean
    public EnvioCorreoService envioCorreoService() {
        return new EnvioCorreoService();
    }

    @Bean
    public FachadaEmisionCertificados fachadaEmisionCertificados(ValidadorAsistencia validador,
                                                                 GeneradorCertificadoPDF generador,
                                                                 FirmaDigitalService firma,
                                                                 EnvioCorreoService correo) {
        return new FachadaEmisionCertificados(validador, generador, firma, correo);
    }

    /** Emision individual: la fachada sin mejoras, que es lo que usan los participantes. */
    @Bean
    @Primary
    public ServicioCertificados servicioCertificados(FachadaEmisionCertificados fachada) {
        return fachada;
    }

    /** Ejemplo de evento con las tres mejoras activadas y apiladas en un orden concreto. */
    @Bean("servicioCertificadosMejorado")
    public ServicioCertificados servicioCertificadosMejorado(FachadaEmisionCertificados fachada) {
        return new MejoraTraduccionIngles(
                   new MejoraCodigoQR(
                       new MejoraMarcaDeAgua(fachada)));
    }

    /** Descarga masiva: la misma cadena de mejoras, protegida por el proxy de roles. */
    @Bean("servicioDescargaMasiva")
    public ServicioCertificados servicioDescargaMasiva(
            @Qualifier("servicioCertificadosMejorado") ServicioCertificados mejorado) {
        return new ProxyControlAccesoCertificados(mejorado);
    }
}
