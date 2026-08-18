package com.lucasmaciel404.pdv_api.service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final Resend resend;

    public void sendPasswordResetEmail(
            String email,
            String resetToken
    ) {

        String resetLink = "https://seu-front.com/reset-password?token=" + resetToken;

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from("PDV <onboarding@resend.dev>")
                .to(email)
                .subject("Recuperação de senha")
                .html("""
                        <h2>Recuperação de senha</h2>

                        <p>Recebemos uma solicitação para alterar sua senha.</p>

                        <p>
                            <a href="%s">Clique aqui para redefinir sua senha</a>
                        </p>

                        <p>Este link expira em 30 minutos.</p>

                        <p>Se você não solicitou essa alteração, ignore este e-mail.</p>
                        """.formatted(resetLink))
                .build();

        try {
            resend.emails().send(params);
        } catch (ResendException e) {
            throw new RuntimeException("Erro ao enviar e-mail de recuperação", e);
        }
    }
}