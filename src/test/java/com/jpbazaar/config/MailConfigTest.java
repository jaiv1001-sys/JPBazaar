package com.jpbazaar.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.assertj.core.api.Assertions.assertThat;

class MailConfigTest {

    private final MailConfig mailConfig = new MailConfig();

    @Test
    @DisplayName("Should remap email address accidentally passed as mail host to smtp.gmail.com")
    void shouldRemapEmailHostToSmtpGmail() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("jaiv4134@gmail.com");

        mailConfig.postProcessBeforeInitialization(mailSender, "mailSender");

        assertThat(mailSender.getHost()).isEqualTo("smtp.gmail.com");
        assertThat(mailSender.getUsername()).isEqualTo("jaiv4134@gmail.com");
    }

    @Test
    @DisplayName("Should preserve valid SMTP host")
    void shouldPreserveValidSmtpHost() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.resend.com");
        mailSender.setUsername("resend_user");

        mailConfig.postProcessBeforeInitialization(mailSender, "mailSender");

        assertThat(mailSender.getHost()).isEqualTo("smtp.resend.com");
        assertThat(mailSender.getUsername()).isEqualTo("resend_user");
    }

    @Test
    @DisplayName("Should default blank host to smtp.gmail.com")
    void shouldDefaultBlankHostToGmail() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("");

        mailConfig.postProcessBeforeInitialization(mailSender, "mailSender");

        assertThat(mailSender.getHost()).isEqualTo("smtp.gmail.com");
    }
}
