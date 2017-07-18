package com.zencode.tripledescryptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 * TripleDES decorator class, adapted for use with Spring Boot
 * 
 * @author fvalenzuela
 *
 */
public class TripleDESDecorator implements InitializingBean {

	private static TripleDESDecorator instance;

	private final static Logger logger = org.slf4j.LoggerFactory.getLogger(TripleDES.class);

	@Value("${keyPath}")
	private String keyPath;

	@Value("${ivPath}")
	private String ivPath;

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
	}

	public static TripleDESDecorator get() {
		return instance;
	}

	/**
	 * Read a file and return it's bytes
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
		}
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			logger.error("Could not completely read file " + file.getName());
			is.close();
			throw new IOException("Could not completely read file " + file.getName());
		}
		is.close();
		return bytes;
	}

	/**
	 * Decrypt a byte[] using vector and key file.
	 * 
	 * @param key
	 * @param vector
	 * @param cadena
	 * @return
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private byte[] decryptBytesUsingFiles(File key, File vector, byte[] cadena) throws InvalidKeyException,
			FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException,
			IOException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cadena);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		if (vector != null) {
			TripleDES.decrypt(TripleDES.readKey(key), getBytesFromFile(vector), byteArrayInputStream,
					byteArrayOutputStream);
		} else {
			TripleDES.decrypt(TripleDES.readKey(key), byteArrayInputStream, byteArrayOutputStream);
		}
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Encrypt the provided text in plain text
	 * 
	 * @param plainText
	 * @return
	 */
	public byte[] encrypt(String plainText) {
		if (keyPath == null || ivPath == null) {
			logger.error("Wrong key filenames!!");
			logger.error("Please specify the values --keyPath=key2.bg --ivPath=iv2.bg");
			System.exit(0);
		}
		byte[] result = null;
		try {
			result = this.encryptUsingFiles(new File(keyPath), new File(ivPath), plainText.getBytes("UTF-8"));
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException
				| InvalidAlgorithmParameterException e) {
			logger.error(e.getMessage());
			logger.debug(e.getStackTrace().toString());
			System.exit(0);
		} catch (IOException e) {
			logger.error("Error reading key files " + keyPath + " or " + ivPath + " please check");
			logger.error(e.getMessage());
			System.exit(0);
		}
		return result;
	}

	/**
	 * Encrypt the provided text in plain text using key files
	 * 
	 * @param key
	 * @param vector
	 * @param cadena
	 * @return
	 * @throws InvalidKeyException
	 * @throws FileNotFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 * @throws InvalidAlgorithmParameterException
	 */
	private byte[] encryptUsingFiles(File key, File vector, byte[] cadena)
			throws InvalidKeyException, FileNotFoundException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeySpecException, IOException, InvalidAlgorithmParameterException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(cadena);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		if (vector != null) {
			TripleDES.encrypt(TripleDES.readKey(key), getBytesFromFile(vector), byteArrayInputStream,
					byteArrayOutputStream);
		} else {
			TripleDES.encrypt(TripleDES.readKey(key), byteArrayInputStream, byteArrayOutputStream);
		}
		return byteArrayOutputStream.toByteArray();
	}

	/**
	 * Decrypt previous encrypted text
	 * 
	 * @param bytes
	 * @return
	 */
	public byte[] decrypt(byte[] text) {
		byte[] result = null;

		try {
			result = this.decryptBytesUsingFiles(new File(keyPath), new File(ivPath), text);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
			logger.error("Error decrypting key");
			logger.debug(e.getMessage());
		} catch (IOException e) {
			logger.error("Error reading key files");
		}
		return result;

	}

}
