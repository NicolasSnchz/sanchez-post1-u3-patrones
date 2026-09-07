package com.universidad.confudes.certificados;

public class EnvioCorreoService {
    public void adjuntarArchivo(String destinatario, byte[] archivo, String nombreArchivo) {
        System.out.println("Adjuntando " + nombreArchivo + " para " + destinatario);
    }
    public void enviar(String asunto, String cuerpo) {
        System.out.println("Enviando correo: " + asunto);
    }
}
