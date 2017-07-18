/*
 * Copyright (c) 2000 David Flanagan.  All rights reserved.
 * This code is from the book Java Examples in a Nutshell, 2nd Edition.
 * It is provided AS-IS, WITHOUT ANY WARRANTY either expressed or implied.
 * You may study, use, and modify it for any non-commercial purpose.
 * You may distribute it non-commercially as long as you retain this notice.
 * For a commercial use license, or to purchase the book (recommended),
 * visit http://www.davidflanagan.com/javaexamples2.
 */
package com.zencode.tripledescryptor;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * This class defines methods for encrypting and decrypting using the Triple DES
 * algorithm and for generating, reading and writing Triple DES keys. It also
 * defines a main() method that allows these methods to be used from the command
 * line.
 */
@Component
@Service
public class TripleDES {

	/** Generate a secret TripleDES encryption/decryption key */
	public static SecretKey generateKey() throws NoSuchAlgorithmException {
		// Get a key generator for Triple DES (a.k.a DESede)
		KeyGenerator keygen = KeyGenerator.getInstance("DESede");

		// Use it to generate a key
		return keygen.generateKey();
	}

	public static SecretKey generateKey(byte[] key) throws Exception {
		DESedeKeySpec keyspec = new DESedeKeySpec(key);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		return keyfactory.generateSecret(keyspec);
	}

	/** Save the specified TripleDES SecretKey to the specified file */
	public static void writeKey(SecretKey key, File f)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// Convert the secret key to an array of bytes like this
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		DESedeKeySpec keyspec = (DESedeKeySpec) keyfactory.getKeySpec(key, DESedeKeySpec.class);
		byte[] rawkey = keyspec.getKey();

		// Write the raw key to the file
		FileOutputStream out = new FileOutputStream(f);
		out.write(rawkey);
		out.close();
	}

	/** Read a TripleDES secret key from the specified file */
	public static SecretKey readKey(File f)
			throws IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
		// Read the raw bytes from the keyfile
		DataInputStream in = new DataInputStream(new FileInputStream(f));
		byte[] rawkey = new byte[(int) f.length()];
		in.readFully(rawkey);
		in.close();

		// Convert the raw bytes to a secret key like this
		DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		SecretKey key = keyfactory.generateSecret(keyspec);
		return key;
	}

	public static SecretKey convertBytesToSecretKey(byte[] bytes)
			throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException {
		if (bytes == null || bytes.length == 0)
			return null;
		DESedeKeySpec keyspec = new DESedeKeySpec(bytes);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		return keyfactory.generateSecret(keyspec);
	}

	/**
	 * Use the specified TripleDES key to encrypt bytes from the input stream
	 * and write them to the output stream. This method uses CipherOutputStream
	 * to perform the encryption and write bytes at the same time.
	 */
	public static void encrypt(SecretKey key, InputStream in, OutputStream out) throws NoSuchAlgorithmException,
			InvalidKeyException, NoSuchPaddingException, IOException, InvalidAlgorithmParameterException {
		encrypt(key, null, in, out);
	}

	public static void encrypt(SecretKey key, byte[] vector, InputStream in, OutputStream out)
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IOException,
			InvalidAlgorithmParameterException {
		// Create and initialize the encryption engine
		Cipher cipher;

		if (vector != null && vector.length > 0) {
			cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(vector);
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		} else {
			cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.ENCRYPT_MODE, key);
		}

		// Create a special output stream to do the work for us
		CipherOutputStream cos = new CipherOutputStream(out, cipher);

		// Read from the input and write to the encrypting output stream
		byte[] buffer = new byte[2048];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			cos.write(buffer, 0, bytesRead);
		}
		cos.close();

		// For extra security, don't leave any plaintext hanging around memory.
		Arrays.fill(buffer, (byte) 0);
	}

	/**
	 * Use the specified TripleDES key to decrypt bytes ready from the input
	 * stream and write them to the output stream. This method uses uses Cipher
	 * directly to show how it can be done without CipherInputStream and
	 * CipherOutputStream.
	 */
	public static void decrypt(SecretKey key, InputStream in, OutputStream out)
			throws NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException {
		decrypt(key, null, in, out);
	}

	public static void decrypt(SecretKey key, byte[] vector, InputStream in, OutputStream out)
			throws NoSuchAlgorithmException, InvalidKeyException, IOException, IllegalBlockSizeException,
			NoSuchPaddingException, InvalidAlgorithmParameterException, BadPaddingException {
		// Create and initialize the decryption engine
		Cipher cipher;
		if (vector != null && vector.length > 0) {
			cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			IvParameterSpec iv = new IvParameterSpec(vector);
			cipher.init(Cipher.DECRYPT_MODE, key, iv);
		} else {
			cipher = Cipher.getInstance("DESede");
			cipher.init(Cipher.DECRYPT_MODE, key);
		}

		// Read bytes, decrypt, and write them out.
		byte[] buffer = new byte[2048];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(cipher.update(buffer, 0, bytesRead));
		}

		// Write out the final bunch of decrypted bytes
		out.write(cipher.doFinal());
		out.flush();
	}

}