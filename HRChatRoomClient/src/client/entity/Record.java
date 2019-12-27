package client.entity;


/**
 * ��Ϣ��¼��Ŀ
 * �������û���ÿһ����Ϣ
 * @author sakura
 *
 */
public class Record {
	private int type; // ��Ϣ����
	private String sender; // ��Ϣ������
	private String text; // ��Ϣ����
	
	public Record() {
		
	}
	
	public Record(int type, String sender, String text) {
		this.type = type;
		this.sender = sender;
		this.text = text;
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "Record [type=" + type + ", text=" + text + "]";
	}
}
