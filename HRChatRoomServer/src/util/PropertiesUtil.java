package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * �û������ļ�������
 * @author sakura
 *
 */
public class PropertiesUtil {

	private static String path = "config/userinfo.properties";
	private static Properties prop = new Properties();
	
	static {
		try {
			prop.load(new FileInputStream(path));
		} catch (FileNotFoundException e) {
			System.out.println("�Ҳ��������������ļ�");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ͨ��key��ȡvalue
	 * 
	 * @param key
	 * @return
	 */
	public static String get(String key) {
		return prop.getProperty(key);
	}

	/**
	 * �޸Ļ�������key
	 * 
	 * @param key
	 * @param value
	 */
	public static void update(String key, String value) {
		prop.setProperty(key, value);
		FileOutputStream fo = null;
		try {
			fo = new FileOutputStream(path);
			// ��Properties�е������б�����Ԫ�ضԣ�д�������
			prop.store(fo, "");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ͨ��keyɾ��value
	 * 
	 * @param key
	 */
	public static void delete(String key) {
		prop.remove(key);
		FileOutputStream oFile = null;
		try {
			oFile = new FileOutputStream(path);
			prop.store(oFile, "");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				oFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ѭ������key value
	 */
	public static void list() {
		Enumeration en = prop.propertyNames(); // �õ������ļ�������
		while (en.hasMoreElements()) {
			String strKey = (String) en.nextElement();
			String strValue = prop.getProperty(strKey);
			System.out.println(strKey + "=" + strValue);
		}
	}
}