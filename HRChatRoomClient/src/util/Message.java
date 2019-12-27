package util;

import java.io.Serializable;
import java.net.InetAddress;

public class Message implements Serializable {

	private String userId = null; // �û�id
	private String password = null; // ����
	private String text = null; // ��Ϣ��
	private InetAddress toAddr = null; // Ŀ���û���ַ
	private int type; // ��Ϣ���ͣ�M_LOGIN:�û���¼��Ϣ��M_SUCCESS:��¼�ɹ���M_FAULURE:��¼ʧ�ܣ�M_ACK:�������Ե�¼�û��Ļ�Ӧ��Ϣ��M_MSG:�Ự��Ϣ��M_QUIT:�û��˳���Ϣ
	private int toPort; // Ŀ���û��˿�
	private String targetId = null; // Ŀ���û�id

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public InetAddress getToAddr() {
		return toAddr;
	}

	public void setToAddr(InetAddress toAddr) {
		this.toAddr = toAddr;
	}

	public int getToPort() {
		return toPort;
	}

	public void setToPort(int toPort) {
		this.toPort = toPort;
	}

	public String getTargetId() {
		return targetId;
	}

	public void setTargetId(String targetId) {
		this.targetId = targetId;
	}
}