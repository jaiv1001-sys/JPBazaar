package com.jpbazaar.util;

import org.springframework.util.StringUtils;

/**
 * Utility for masking sensitive data (passwords, credit cards, emails, tokens)
 * before logging or diagnostics to prevent sensitive information leakage.
 */
public final class MaskingUtils {

    private MaskingUtils() {
        // Prevent instantiation
    }

    /**
     * Masks an email address (e.g. john.doe@example.com -> j***e@example.com).
     */
    public static String maskEmail(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            return "***";
        }
        int atIndex = email.indexOf('@');
        String name = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        if (name.length() <= 2) {
            return name.charAt(0) + "***" + domain;
        }
        return name.charAt(0) + "***" + name.charAt(name.length() - 1) + domain;
    }

    /**
     * Masks payment card / account number preserving only the last 4 digits.
     */
    public static String maskCardNumber(String cardNumber) {
        if (!StringUtils.hasText(cardNumber)) {
            return "****";
        }
        String sanitized = cardNumber.replaceAll("\\s+", "");
        if (sanitized.length() <= 4) {
            return "****";
        }
        String last4 = sanitized.substring(sanitized.length() - 4);
        return "****-****-****-" + last4;
    }

    /**
     * Completely redacts passwords or secret keys.
     */
    public static String maskSecret(String secret) {
        if (!StringUtils.hasText(secret)) {
            return "[EMPTY]";
        }
        return "[PROTECTED]";
    }

    /**
     * Masks JWT or bearer tokens displaying only prefix and suffix.
     */
    public static String maskToken(String token) {
        if (!StringUtils.hasText(token)) {
            return "[EMPTY]";
        }
        if (token.length() <= 10) {
            return "[MASKED]";
        }
        return token.substring(0, 6) + "..." + token.substring(token.length() - 4);
    }
}
