package util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ServerInfo {
	// ���ط����������ļ�
	private static String configPath = "config/project.properties";
	private static Properties prop = new Properties();
	static {
		try {
			prop.load(new FileInputStream(configPath));
		} catch (FileNotFoundException e) {
			System.out.println("�Ҳ��������������ļ�");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String serverAddr = (String)prop.getProperty("serverAddr");
	private static int serverPort = Integer.valueOf((String)prop.getProperty("serverPort"));
	private static String serverPath = (String)prop.getProperty("server_path");
	private static String myserverPath = (String)prop.getProperty("myserver_path");
	private static String serverPass = (String)prop.getProperty("server_pass");
	private static String myserverPass = (String)prop.getProperty("myserver_pass");
	
	// ���ط�������IP
	public static String getServerAddr() {
		return serverAddr;
	}
	
	// ���ط������󶨶˿�
	public static int getServerPort() {
		return serverPort;
	}
	
	// ���ط�������Կ��·��
	public static String getServerPath() {
		return serverPath;
	}

	// ���ط�������������Կ��·��
	public static String getMyserverPath() {
		return myserverPath;
	}

	// ���ط�������Կ������
	public static String getServerPass() {
		return serverPass;
	}

	// ���ط�������������Կ������
	public static String getMyserverPass() {
		return myserverPass;
	}

	// ��ȡ��ǰʱ��
	public static String getCurrentTime() {
		Date date = new Date();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
		return dateFormat.format(date);
	}
	
}
