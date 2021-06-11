package br.com.awsutils.service;

import br.com.awsutils.Constants;
import br.com.awsutils.domain.AWSUtilsUnsafeContentException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.rekognition.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.awsutils.Constants.BUCKET_DOCUMENTS;

@Service
public class RekognitionService {

    private static final Float DETECT_TEXT_THRESHOLD = 50F;
    private static final Float FACE_COMPARE_SIMILARITY_THRESHOLD = 60F;
    private static final Float UNSAFE_THRESHOLD = 70F;

    public double faceMatch(String sourceUrl, String targetUrl) {

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        Image sourceImage = new Image().withS3Object(new S3Object().withBucket(BUCKET_DOCUMENTS)
                .withName(sourceUrl));

        Image targetImage = new Image().withS3Object(new S3Object().withBucket(BUCKET_DOCUMENTS)
                .withName(targetUrl));

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(sourceImage)
                .withTargetImage(targetImage)
                .withSimilarityThreshold(FACE_COMPARE_SIMILARITY_THRESHOLD);

        CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);

        List <CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();

        if (faceDetails.size() > 0) {
            return faceDetails.get(0).getSimilarity().doubleValue();
        } else {
            return 0;
        }

    }

    public boolean compareFaces(String sourceUrl, String targetUrl, boolean detectUnsafe) throws AWSUtilsUnsafeContentException {

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        Image sourceImage = new Image().withS3Object(new S3Object().withBucket(Constants.BUCKET_MOBILE)
                        .withName(sourceUrl));

        if (detectUnsafe) {

            DetectModerationLabelsRequest requestModeration = new DetectModerationLabelsRequest()
                    .withImage(sourceImage).withMinConfidence(UNSAFE_THRESHOLD);

            try {

                DetectModerationLabelsResult result = rekognitionClient.detectModerationLabels(requestModeration);
                List<ModerationLabel> labels = result.getModerationLabels();
                for (ModerationLabel label : labels) {
                    if (label.getName().toLowerCase().contains("nudity")) {
                        throw new AWSUtilsUnsafeContentException(Constants.UNSAFE_CONTENT);
                    }
                }
            } catch (AmazonRekognitionException e) {
                e.printStackTrace();
            }

        }

        Image targetImage = new Image().withS3Object(new S3Object().withBucket(Constants.BUCKET_MOBILE)
                        .withName(targetUrl));

        CompareFacesRequest request = new CompareFacesRequest()
                .withSourceImage(sourceImage)
                .withTargetImage(targetImage)
                .withSimilarityThreshold(FACE_COMPARE_SIMILARITY_THRESHOLD);

        CompareFacesResult compareFacesResult=rekognitionClient.compareFaces(request);

        List <CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();

        return faceDetails.size() > 0;

    }

    public String extractText(String objectId) {

        StringBuilder data = new StringBuilder();

        AmazonRekognition rekognitionClient = AmazonRekognitionClientBuilder
                .standard()
                .withRegion(Regions.US_EAST_1)
                .build();

        DetectTextRequest request = new DetectTextRequest()
                .withImage(new Image()
                        .withS3Object(new S3Object()
                                .withName(objectId)
                                .withBucket(BUCKET_DOCUMENTS)));

        DetectTextResult result = rekognitionClient.detectText(request);
        List<TextDetection> textDetections = result.getTextDetections();

        for (TextDetection text: textDetections) {

            if (text.getConfidence() > DETECT_TEXT_THRESHOLD) {
                data.append(text.getDetectedText());
            }
        }

        return data.toString();

    }

}
