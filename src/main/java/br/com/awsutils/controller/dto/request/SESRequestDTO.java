package br.com.awsutils.controller.dto.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SESRequestDTO {

    private String fromAddress;
    private String toAddress;
    private String subject;
    private String textBody;
    private String htmlBody;
    private List<String> attachments = new ArrayList<>();

}
