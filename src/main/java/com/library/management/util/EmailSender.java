package com.library.management.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Utility class for sending emails from the Library Management System.
 * <p>
 * This class uses the JavaMail API to send verification codes to users' email addresses.
 * It configures SMTP properties for Gmail and sends simple text-based messages.
 * </p>
 *
 * <p><b>Security Warning:</b> Hardcoding email credentials in source code is a security risk.
 * In production environments, always store sensitive credentials in environment variables or secure configuration files.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * EmailSender.sendEmail("user@example.com", "123456");
 * }</pre>
 *
 * <p><b>Dependencies:</b> JavaMail API must be available in the classpath (e.g., `javax.mail.jar` or using Maven dependency).</p>
 *
 * @author Fardaws Jawad
 */
public class EmailSender {

    //-----------------------------------------------------------------------
    /**
     * Sends a verification email containing the provided code to the specified recipient.
     *
     * @param recipientEmail   the email address of the recipient
     * @param verificationCode the verification code to be sent in the email body
     */
    public static void sendEmail(String recipientEmail, String verificationCode) {
        String host = "smtp.gmail.com";
        String from = "arashjawad95@gmail.com";
        String password = "kssabyjdbsyvwuef"; // Move this to a secure location in production

        // Set mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        // Create a mail session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create the email message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Email Verification Code");
            message.setText("Your verification code is: " + verificationCode);

            // Send the message
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace(); // Optionally replace with logging
        }
    }

}
