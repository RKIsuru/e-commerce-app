package com.rkisuru.notification.email;

import com.rkisuru.notification.kafka.order.Product;
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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendPaymentSuccessEmail(
            String receiverEmail,
            String customerName,
            BigDecimal amount,
            String orderReference) throws MessagingException
    {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, UTF_8.name());
        mimeMessageHelper.setFrom("iaravinda33@gmail.com");

        final String templateName = EmailTemplates.PAYMENT_CONFIRMATION.getTemplate();

        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("amount", amount);
        variables.put("orderReference", orderReference);

        Context context = new Context();
        context.setVariables(variables);
        mimeMessageHelper.setSubject(EmailTemplates.PAYMENT_CONFIRMATION.getSubject());

        try {
            String html = templateEngine.process(templateName, context);
            mimeMessageHelper.setText(html, true);

            mimeMessageHelper.setTo(receiverEmail);
            mailSender.send(mimeMessage);
            log.info(String.format("Email successfully sent to %s", receiverEmail));
        } catch (MessagingException e) {
            log.warn("Cannot send email to {}", receiverEmail);
        }
    }

    @Async
    public void sendOrderConfirmationEmail(
            String receiverEmail,
            String customerName,
            BigDecimal amount,
            String orderReference,
            List<Product> products
            ) throws MessagingException
    {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, UTF_8.name());
        mimeMessageHelper.setFrom("iaravinda33@gmail.com");

        final String templateName = EmailTemplates.ORDER_CONFIRMATION.getTemplate();

        Map<String, Object> variables = new HashMap<>();
        variables.put("customerName", customerName);
        variables.put("totalAmount", amount);
        variables.put("orderReference", orderReference);
        variables.put("products", products);

        Context context = new Context();
        context.setVariables(variables);
        mimeMessageHelper.setSubject(EmailTemplates.ORDER_CONFIRMATION.getSubject());

        try {
            String html = templateEngine.process(templateName, context);
            mimeMessageHelper.setText(html, true);

            mimeMessageHelper.setTo(receiverEmail);
            mailSender.send(mimeMessage);
            log.info(String.format("Email successfully sent to %s", receiverEmail));
        } catch (MessagingException e) {
            log.warn("Cannot send email to {}", receiverEmail);
        }
    }

}