package br.com.awsutils;

import java.text.SimpleDateFormat;

public class Constants {

    public static SimpleDateFormat datetimeFormat = new SimpleDateFormat("ddMMyyyyHHmmss");

    public static final String SUCCESS_MESSAGE = "operation successfully executed";

    public static final String S3_UPLOAD_SUCCESS = "image added to amazon s3 directory";

    public static final String FACE_MATCH = "images contains the same person";
    public static final String FACE_MISMATCH = "images does not contains the same person";
    public static final String UNSAFE_CONTENT = "image has unsafe content";

    public static String BUCKET_MOBILE = "multilaser-mobile";
    public static String BUCKET_DOCUMENTS = "multilaser-documents";

}

