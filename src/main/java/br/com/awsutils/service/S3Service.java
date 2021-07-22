package br.com.awsutils.service;

import br.com.awsutils.Constants;
import br.com.awsutils.domain.AWSUtilsException;
import com.amazonaws.AmazonClientException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Date;

@Service
public class S3Service {

    private static final long URL_EXPIRATION_TIME = 1000 * 60 * 60;

    public void transferImage(String sourceName) throws AWSUtilsException {

        final AmazonS3 amazonS3 = AmazonS3Client.builder().withRegion(Regions.SA_EAST_1)
                .withPathStyleAccessEnabled(true).build();

        try {

            String destinationName = sourceName;

            if (destinationName.contains("-")) {
                destinationName = destinationName.replace("-", "/");
            }

            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                    Constants.BUCKET_MOBILE, sourceName, Constants.BUCKET_DOCUMENTS, destinationName);

            amazonS3.copyObject(copyObjRequest);

            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(Constants.BUCKET_MOBILE, sourceName);

            amazonS3.deleteObject(deleteObjectRequest);

        } catch (AmazonClientException ase) {

            throw new AWSUtilsException(ase.getMessage());

        }

    }

    public void uploadFile(byte[] file, String filename, String contentType, String bucketName)
            throws AWSUtilsException {

        final AmazonS3 amazonS3 = AmazonS3Client.builder().withRegion(Regions.SA_EAST_1)
                .withPathStyleAccessEnabled(true).build();

        try {

            S3Object s3Object = new S3Object();

            ObjectMetadata omd = new ObjectMetadata();
            omd.setContentType(contentType);
            omd.setContentLength(file.length);
            omd.setHeader("filename", filename);

            ByteArrayInputStream bis = new ByteArrayInputStream(file);

            s3Object.setObjectContent(bis);
            amazonS3.putObject(new PutObjectRequest(bucketName, filename, bis, omd));
            s3Object.close();

        } catch (Exception ase) {

            throw new AWSUtilsException(ase.getMessage());

        }

    }

    public String getObject(String objectId, String bucketName) throws AWSUtilsException {

        try {

            final AmazonS3 amazonS3 = AmazonS3Client.builder().withRegion(Regions.SA_EAST_1)
                    .withPathStyleAccessEnabled(true).build();

            Date currentDate = new Date();
            long msec = currentDate.getTime();
            msec += URL_EXPIRATION_TIME;
            currentDate.setTime(msec);

            GeneratePresignedUrlRequest generatePresignedUrlRequest =
                    new GeneratePresignedUrlRequest(bucketName, objectId);
            generatePresignedUrlRequest.setExpiration(new Date(new Date().getTime() + URL_EXPIRATION_TIME));

            URL s = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

            return s.toString();

        } catch (Exception ase) {

            throw new AWSUtilsException(ase.getMessage());

        }

    }

}