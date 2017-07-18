package com.zencode.tripledescryptor;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
@EnableAutoConfiguration
/**
 * Simple application for encrypt plan passwords using TripleDES
 * It receives the parameter -Dpass as text to encrypt and the name of the files
 * containing key and vector keys.
 * @author fvalenzuela
 *
 */
public class TripledescryptorApplication {

	private final static Logger logger = org.slf4j.LoggerFactory.getLogger(TripledescryptorApplication.class);

	@Value("${keyPath}")
	public String keyPath;

	public static void main(String[] args) {
		SpringApplication.run(TripledescryptorApplication.class, args);
		TripleDESDecorator des = TripleDESDecorator.get();
		String plainText = System.getProperty("pass");
		if (plainText == null) {
			logger.error("Error, parameter -Dpass is empty. You must use -Dpass=CLAVEAENCRIPTAR.");
			System.exit(0);
		}
		logger.info("Attemp to encrypt text: " + plainText);
		byte[] ecn = new byte[2];
		ecn = des.encrypt(plainText);
		String b64 = Base64.encodeBase64String(ecn);
		byte[] reverse;
		reverse = des.decrypt(ecn);
		if (new String(reverse).equals(plainText)) {
			logger.info("Text encryption successfully");
			logger.info("Encrypted key is: " + b64);
		} else {
			logger.error("Cannot validate key integrity. Please try again!");
		}

	}

}
