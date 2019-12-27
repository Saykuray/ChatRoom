package client.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.xml.bind.DatatypeConverter;

/**
 * ��װһЩͨ�õļ����㷨
 * getHash�����ַ������ļ���ժҪ����Ҫ����������ܴ���ͽ�������ժҪ��ʽ���������ݿ�
 * generateNewKey������AES�Գ���Կ
 * @author sakura
 *
 */
public class Cryptography {
	private static final int BUFSIZE=8192;  //��������С
	
	public static String getHash(String plainText, String hashType) {
		try {
			MessageDigest md = MessageDigest.getInstance(hashType);
			byte[] encrytString = md.digest(plainText.getBytes("UTF-8"));
			return DatatypeConverter.printHexBinary(encrytString);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return null;
		}
	}
	
	public static SecretKey generateNewKey() {
		try {
			KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
			keyGenerator.init(128);
			SecretKey secretKey = keyGenerator.generateKey();
			return secretKey;
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	private Cryptography() {}
}
