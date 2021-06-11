package br.com.awsutils.controller.dto.request;

import lombok.Data;

@Data
public class SESRequestDTO {

    private String fromAddress;
    private String toAddress;
    private String subject;
    private String textBody;
    private String htmlBody;

}
