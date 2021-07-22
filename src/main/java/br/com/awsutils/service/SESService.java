package br.com.awsutils.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.URLDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Properties;

@Service
public class SESService {

    private final S3Service s3Service;

    @Value("${awsutils.config.mail.attachments.bucket}")
    private String bucketAttachments;

    @Value("${awsutils.config.mail.encoding}")
    private String defaultEncoding;

    @Value("${awsutils.config.mail.charset}")
    private String defaultCharset;

    public SESService(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    public void sendEmail(String fromAddress,
                          String toAddress,
                          String subject,
                          String textBody,
                          String htmlBody,
                          List<String> attachments) {

        try {

            Session session = Session.getDefaultInstance(new Properties());

            MimeMessage message = new MimeMessage(session);

            message.setSubject(subject, defaultCharset);
            message.setFrom(new InternetAddress(fromAddress));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));

            MimeMultipart messageBody = new MimeMultipart("alternative");

            MimeBodyPart wrap = new MimeBodyPart();

            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(textBody, defaultEncoding);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlBody,defaultEncoding);

            messageBody.addBodyPart(textPart);
            messageBody.addBodyPart(htmlPart);

            wrap.setContent(messageBody);

            MimeMultipart mimeMultipart = new MimeMultipart("mixed");

            message.setContent(mimeMultipart);

            mimeMultipart.addBodyPart(wrap);

            String attachmentUrl = s3Service.getObject(attachments.get(0), bucketAttachments);
            MimeBodyPart attachment = new MimeBodyPart();
            DataSource dataSource = new URLDataSource(new URL(attachmentUrl));
            attachment.setDataHandler(new DataHandler(dataSource));
            attachment.setFileName(dataSource.getName());

            mimeMultipart.addBodyPart(attachment);

            try {

                AmazonSimpleEmailService client =
                        AmazonSimpleEmailServiceClientBuilder.standard()
                                .withRegion(Regions.SA_EAST_1).build();

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                message.writeTo(outputStream);
                RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));

                SendRawEmailRequest rawEmailRequest = new SendRawEmailRequest(rawMessage);

                client.sendRawEmail(rawEmailRequest);

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            System.out.println("email sent!");

        } catch (Exception ex) {
            System.out.println("the email was not sent. Error message: " + ex.getMessage());
        }
    }

}
