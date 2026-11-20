package com.bradesco.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final ObjectProvider<JavaMailSender> mailSender;
    private final String remetente;

    public EmailService(ObjectProvider<JavaMailSender> mailSender,
                        @Value("${app.mail.remetente}") String remetente) {
        this.mailSender = mailSender;
        this.remetente = remetente;
    }

    public void enviar(String para, String assunto, String texto) {
        JavaMailSender sender = mailSender.getIfAvailable();

        if (sender == null) {
            // Modo demonstração: sem servidor de e-mail configurado, só registra no log
            log.info("[E-MAIL SIMULADO] Para: {} | Assunto: {} | Mensagem: {}", para, assunto, texto);
            return;
        }

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(remetente);
        mensagem.setTo(para);
        mensagem.setSubject(assunto);
        mensagem.setText(texto);
        sender.send(mensagem);
    }
}