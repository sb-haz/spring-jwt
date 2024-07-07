package com.hasan.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.mail.javamail.MimeMessageHelper.MULTIPART_MODE_MIXED;

@Service
@Slf4j
@RequiredArgsConstructor

// This class is responsible for sending emails
public class EmailService {

    private final JavaMailSender mailSender; // This class helps us send emails
    private final SpringTemplateEngine templateEngine; // This class helps us use Thymeleaf templates

    @Async // This annotation tells Spring to run this method in a separate thread
    public void sendEmail(
            String to,
            String username,
            EmailTemplateName emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {

        // If the email template is null, we use the default template
        String templateName;
        if (emailTemplate == null) templateName = "confirm_email";
        else templateName = emailTemplate.name();

        // Create a new MimeMessage, which helps us create a new email
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MULTIPART_MODE_MIXED,
                UTF_8.name()
        );

        // Create a map with the properties we want to use in the email template
        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmationUrl", confirmationUrl);
        properties.put("activation_code", activationCode);

        // Create a new context and set the properties, so we can use them in the email template
        Context context = new Context();
        context.setVariables(properties);

        // Set the email properties
        helper.setFrom("contact@booknetwork.com");
        helper.setTo(to);
        helper.setSubject(subject);

        // Process the email template with the templateName and context
        // templateName is the name of the Thymeleaf template we want to use
        String template = templateEngine.process(templateName, context);

        // Set the email content to the processed template
        helper.setText(template, true);

        // Send the email
        mailSender.send(mimeMessage);
    }
}