package br.com.awsutils.controller;


import br.com.awsutils.Constants;
import br.com.awsutils.service.S3Service;
import br.com.awsutils.domain.AWSUtilsResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;

@Api(tags = "s3")
@RequestMapping("/s3")
@RestController
public class S3Controller {

    @Resource
    private S3Service s3Service;

    @RequestMapping(path = "/transferimage",
                    method = RequestMethod.POST,
                    produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> transferImage(@RequestParam String filename) {

        AWSUtilsResponse<String> returnMessage = new AWSUtilsResponse<>();
        returnMessage.setDatetime(Constants.datetimeFormat.format(new Date()));

        long startTime = System.currentTimeMillis();

        try {

            s3Service.transferImage(filename);

            returnMessage.setStatus(true);
            returnMessage.setMessage(Constants.SUCCESS_MESSAGE);

        } catch (Exception e) {

            returnMessage.setStatus(false);
            returnMessage.setMessage(e.getMessage());

        }

        returnMessage.setDuration(System.currentTimeMillis() - startTime);

        return new ResponseEntity<>(returnMessage, HttpStatus.OK);

    }

    @ApiOperation(value = "uploadFile",
                  nickname = "uploadFileS3",
                  notes = "Uploads a file to S3 bucket")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success"),
            @ApiResponse(code = 403, message = "You need permission to access this resource"),
            @ApiResponse(code = 500, message = "An internal server error happend")})
    @PostMapping(path = "/upload",
                 consumes = "multipart/form-data",
                 produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("filename") String filename,
                                        @RequestParam("contentType") String contentType,
                                        @RequestParam("bucketName") String bucketName) {

        AWSUtilsResponse<String> returnMessage = new AWSUtilsResponse<>();
        returnMessage.setDatetime(Constants.datetimeFormat.format(new Date()));

        long startTime = System.currentTimeMillis();

        try {

            s3Service.uploadFile(file.getBytes(), filename, contentType, bucketName);

            returnMessage.setStatus(true);
            returnMessage.setMessage(Constants.S3_UPLOAD_SUCCESS);

        } catch (Exception e) {

            returnMessage.setStatus(false);
            returnMessage.setMessage(e.getMessage());

        }

        returnMessage.setDuration(System.currentTimeMillis() - startTime);

        return new ResponseEntity<>(returnMessage, HttpStatus.OK);

    }

    @RequestMapping(path = "/object",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> getObject(@RequestParam("objectId") String objectId,
                                       @RequestParam("bucketName") String bucketName) {

        AWSUtilsResponse<String> returnMessage = new AWSUtilsResponse<>();
        returnMessage.setDatetime(Constants.datetimeFormat.format(new Date()));

        long startTime = System.currentTimeMillis();

        try {

            returnMessage.setData(s3Service.getObject(objectId, bucketName));

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
