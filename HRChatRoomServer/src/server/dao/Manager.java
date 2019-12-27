package server.dao;

import java.util.HashSet;

import util.PropertiesUtil;

/**
 * �����û���ע���¼
 * @author sakura
 *
 */
public class Manager {
	// �û���¼��
	private HashSet<String> userSet = new HashSet<String>(); 
	
	/**
	 * �����û���¼���ж��û������Ƿ���ȷ
	 */
	public boolean login(String userId, String password) {
		String value = PropertiesUtil.get(userId);
		if (value == null) {
			return false;
		}
		return value.equals(password);
	}
	
	
	/**
	 * �����û�ע�ᣬ�ж�id�Ƿ�ע���
	 * ע����˷���flase
	 * û����ע���û�����true
	 */
	public boolean register(String userId, String password) {
		String value = PropertiesUtil.get(userId);
		// ���userid��û��ע���
		if(value == null) {
			// �������ļ�д�û���Ϣ
			PropertiesUtil.update(userId, password);
			return true;
		}
		return false;
	}
	
	
	/**
	 * �ж��û��Ƿ��Ѿ���¼
	 */
	public boolean isLogin() {
		return true;
	}
}
