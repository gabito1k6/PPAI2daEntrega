package com.utn.bolsines.servicios;

public class MailServerService {

    private static MailServerService instancia;

    private MailServerService() {}

    public static MailServerService getInstancia() {
        if (instancia == null) {
            instancia = new MailServerService();
        }
        return instancia;
    }

    /**
     * Envía un correo electrónico de notificación.
     * @param destinatario dirección de email del destinatario
     * @param asunto       asunto del correo
     * @param cuerpo       cuerpo del correo
     */
    public void enviarMail(String destinatario, String asunto, String cuerpo) {
        // TODO: integrar con servidor de mail real (ej. SMTP, SendGrid, etc.)
        System.out.println("Enviando mail a: " + destinatario);
        System.out.println("Asunto: " + asunto);
        System.out.println("Cuerpo: " + cuerpo);
    }
}
