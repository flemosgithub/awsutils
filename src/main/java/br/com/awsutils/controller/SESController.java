package br.com.awsutils.controller;


import br.com.awsutils.Constants;
import br.com.awsutils.controller.dto.request.SESRequestDTO;
import br.com.awsutils.domain.AWSUtilsResponse;
import br.com.awsutils.service.SESService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@Api(tags = "mail")
@RequestMapping("/mail")
@RestController
public class SESController {

    @Resource
    private SESService sesService;

    @ApiOperation(value = "sendMail",
            nickname = "SES send mail",
            notes = "Sends a simple mail")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "You need permission to access this resource"),
            @ApiResponse(code = 500, message = "An internal server error happend")})
    @RequestMapping(path = "/send",
                    method = RequestMethod.POST,
                    produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> sendMail(@RequestBody SESRequestDTO request) {

        AWSUtilsResponse<String> returnMessage = new AWSUtilsResponse<>();
        returnMessage.setDatetime(Constants.datetimeFormat.format(new Date()));

        long startTime = System.currentTimeMillis();

        try {

            sesService.sendEmail(request.getFromAddress(), request.getToAddress(), request.getSubject(),
                    request.getTextBody(), request.getHtmlBody(), request.getAttachments());

            returnMessage.setStatus(true);
            returnMessage.setMessage(Constants.SUCCESS_MESSAGE);

        } catch (Exception e) {

            returnMessage.setStatus(false);
            returnMessage.setMessage(e.getMessage());

        }

        returnMessage.setDuration(System.currentTimeMillis() - startTime);

        return new ResponseEntity<>(returnMessage, HttpStatus.OK);

    }



}
