package br.com.awsutils.service;

import java.io.IOException;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import org.springframework.stereotype.Service;

@Service
public class SESService {

    private static final String CHARSET_UTF8 = "UTF-8";

    public void sendEmail(String fromAddress,
                          String toAddress,
                          String subject,
                          String textBody,
                          String htmlBody) {

        try {

            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder
                    .standard()
                    .withRegion(Regions.SA_EAST_1)
                    .build();

            SendEmailRequest request = new SendEmailRequest()
                    .withDestination(
                            new Destination().withToAddresses(toAddress))
                    .withMessage(new Message()
                            .withBody(new Body()
                                    .withHtml(new Content()
                                            .withCharset(CHARSET_UTF8).withData(htmlBody))
                                    .withText(new Content()
                                            .withCharset(CHARSET_UTF8).withData(textBody)))
                            .withSubject(new Content()
                                    .withCharset(CHARSET_UTF8).withData(subject)))
                    .withSource(fromAddress);

            client.sendEmail(request);

            System.out.println("Email sent!");

        } catch (Exception ex) {
            System.out.println("The email was not sent. Error message: " + ex.getMessage());
        }
    }

}
