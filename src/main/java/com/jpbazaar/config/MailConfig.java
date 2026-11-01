package com.jpbazaar.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Customizer for JavaMailSender to handle common cloud environment configurations,
 * such as auto-correcting when an email address (e.g. user@gmail.com) is mistakenly
 * supplied as the SMTP hostname instead of 'smtp.gmail.com'.
 */
@Slf4j
@Component
public class MailConfig implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof JavaMailSenderImpl mailSender) {
            String host = mailSender.getHost();
            if (StringUtils.hasText(host) && host.contains("@")) {
                log.warn("Detected email address '{}' provided as mail host. Automatically remapping to 'smtp.gmail.com'.", host);
                if (!StringUtils.hasText(mailSender.getUsername())) {
                    mailSender.setUsername(host);
                }
                mailSender.setHost("smtp.gmail.com");
            } else if (!StringUtils.hasText(host)) {
                log.info("No mail host specified. Defaulting to 'smtp.gmail.com'.");
                mailSender.setHost("smtp.gmail.com");
            }
        }
        return bean;
    }
}
