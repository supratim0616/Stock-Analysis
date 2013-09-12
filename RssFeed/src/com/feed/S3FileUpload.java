package com.feed;

import java.io.File;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

public class S3FileUpload {

	File file;
	static Logger log = Logger.getLogger(Initiator.class.getName());

	public S3FileUpload(File file) {
		super();
		this.file = file;
	}

	static void uploadFileonS3(File file) {
		/*
		 * This credentials provider implementation loads your AWS credentials
		 * from a properties file at the root of your classpath.
		 * 
		 * Important: Be sure to fill in your AWS access credentials in the
		 * AwsCredentials.properties file before you try to run this sample.
		 * http://aws.amazon.com/security-credentials
		 */
		AmazonS3 s3 = new AmazonS3Client(
				new ClasspathPropertiesFileCredentialsProvider());
		Region region = Region.getRegion(Regions.US_EAST_1);
		s3.setRegion(region);
		
		String filename = file.getName();

		String bucketName = "stock-news-feed";
		String key = getKey(file.getName());

		log.info("Getting Started with Amazon S3");

		try {

			log.info("Uploading a new object to S3 from a file\n");
			s3.putObject(bucketName, key, file);

		} catch (AmazonClientException ace) {
			log.error("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with S3, "
					+ "such as not being able to access the network.");
			log.error("Error Message: " + ace.getMessage());
		}
	}
	
	/**
	 * Returns a String for aws key. Method is used to create the path for storing file on S3 
	 *
	 * @param  filename
	 * @return  String
	 */
	private static String getKey(String filename)
	{
		String key = "";
		key = filename.split("_")[1].substring(0, 8) + "/" ;
		if (filename.contains("reject"))
			key = key + "REJECT" + "/" + filename ;
			else
				key = key + filename.split("_")[0] + "/" + filename;
		return key;
	}

}
