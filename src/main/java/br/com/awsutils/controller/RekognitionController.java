package br.com.awsutils.controller;


import br.com.awsutils.Constants;
import br.com.awsutils.service.RekognitionService;
import br.com.awsutils.domain.AWSUtilsResponse;
import br.com.awsutils.domain.FaceMatchResult;
import io.swagger.annotations.Api;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@Api(tags = "rekognition")
@RequestMapping("/rekognition")
@RestController
public class RekognitionController {

    @Resource
    private RekognitionService rekognitionService;

    @RequestMapping(path = "/facematch",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> faceMatch(@RequestParam String sourceUrl,
                                                            @RequestParam String targetUrl) {

        AWSUtilsResponse<FaceMatchResult> returnMessage = new AWSUtilsResponse<>();
        returnMessage.setDatetime(Constants.datetimeFormat.format(new Date()));

        long startTime = System.currentTimeMillis();

        try {

            double similarity = rekognitionService.faceMatch(sourceUrl, targetUrl);

            FaceMatchResult result = new FaceMatchResult();
            result.setSimilarity(similarity);

            returnMessage.setStatus(true);
            returnMessage.setMessage(Constants.SUCCESS_MESSAGE);
            returnMessage.setData(result);

        } catch (Exception e) {

            returnMessage.setStatus(false);
            returnMessage.setMessage(e.getMessage());

        }

        returnMessage.setDuration(System.currentTimeMillis() - startTime);

        return new ResponseEntity<>(returnMessage, HttpStatus.OK);

    }

    @RequestMapping(path = "/facecompare",
                    method = RequestMethod.POST,
                    produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> faceCompare(@RequestParam String sourceUrl,
                                      @RequestParam String targetUrl,
                                      @RequestParam boolean detectUnsafe) {

        AWSUtilsResponse<String> returnMessage = new AWSUtilsResponse<>();
        returnMessage.setDatetime(Constants.datetimeFormat.format(new Date()));

        long startTime = System.currentTimeMillis();

        try {

            boolean faceMatch = rekognitionService.compareFaces(sourceUrl, targetUrl, detectUnsafe);
            returnMessage.setStatus(faceMatch);
            if (faceMatch) {
                returnMessage.setMessage(Constants.FACE_MATCH);
            } else {
                returnMessage.setMessage(Constants.FACE_MISMATCH);
            }

        } catch (Exception e) {

            returnMessage.setStatus(false);
            returnMessage.setMessage(e.getMessage());

        }

        returnMessage.setDuration(System.currentTimeMillis() - startTime);

        return new ResponseEntity<>(returnMessage, HttpStatus.OK);

    }

    @RequestMapping(path = "/ocr",
                    method = RequestMethod.POST,
                    produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<?> ocr(@RequestParam String objectId) {

        AWSUtilsResponse<String> returnMessage = new AWSUtilsResponse<>();
        returnMessage.setDatetime(Constants.datetimeFormat.format(new Date()));

        long startTime = System.currentTimeMillis();

        try {

            String data = rekognitionService.extractText(objectId);

            returnMessage.setStatus(true);
            returnMessage.setMessage("text successfully extracted");
            returnMessage.setData(data);

            System.out.println("### TEXT EXTRACTED: " + data);

        } catch (Exception e) {

            returnMessage.setStatus(false);
            returnMessage.setMessage(e.getMessage());

        }

        returnMessage.setDuration(System.currentTimeMillis() - startTime);

        return new ResponseEntity<>(returnMessage, HttpStatus.OK);

    }


}
