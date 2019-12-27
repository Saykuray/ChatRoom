package client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

import client.entity.Record;
import util.Message;
import util.MessageType;
import util.Translate;

public class RevMessage extends Thread {
	private DatagramSocket clientSocket;
	private ClientView parentUI;
	private String groupName = "HR������";
	// �����û������¼
	private HashMap<String, ArrayList<Record>> chatRecords = new HashMap<String, ArrayList<Record>>();
	private byte[] data = new byte[8096];

	public RevMessage(DatagramSocket clientSocket, ClientView parentUI) {
		this.clientSocket = clientSocket;
		this.parentUI = parentUI;
		chatRecords.put(groupName, new ArrayList<Record>());
	}

	public ArrayList<Record> getChatRecords(String userId) {
		return chatRecords.get(userId);
	}
	
	public void addChatRecords(int type, String userId, String sender, String text) {
		// �ж������¼�����Ƿ��и��û���û����������û�
		if(!chatRecords.containsKey(userId)) {
			chatRecords.put(userId, new ArrayList<Record>());
		}
		Record record = new Record(type, sender, text);
		chatRecords.get(userId).add(record);
	}
	
	@Override
	public void run() {
		while(true) { // ������Ϣ
			try {
				DatagramPacket packet = new DatagramPacket(data, data.length);
				clientSocket.receive(packet); // ���ձ���
				Message msg = (Message) Translate.ByteToObject(data);
				System.out.println("����" + msg.getUserId() + "�û���Э�飺" + msg.getType());
				String userId = msg.getUserId();
				// ͨ��Э����ദ��
				
				// �����û���¼��Ϣ
				if (msg.getType() == MessageType.M_LOGIN || msg.getType() == MessageType.M_ACK) {
					chatRecords.put(msg.getUserId(), new ArrayList<Record>());
					parentUI.updateGUI(msg.getType(), msg.getUserId(), "");
				// ������ȷ����Ϣ
				} else if (msg.getType() == MessageType.M_ACK) {
					parentUI.updateGUI(msg.getType(), msg.getUserId(), "");
				// Ⱥ����Ϣ
				} else if (msg.getType() == MessageType.M_GROUP) {
					addChatRecords(msg.getType(), groupName, msg.getUserId(), msg.getText());
					parentUI.updateGUI(msg.getType(), msg.getUserId(), msg.getText());
				// ��ͨ�Ự��Ϣ
				} else if (msg.getType() == MessageType.M_MSG) {
					addChatRecords(msg.getType(), msg.getUserId(), msg.getUserId(), msg.getText());
					parentUI.updateGUI(msg.getType(), msg.getUserId(), msg.getText());
				// �����û�������Ϣ
				} else if (msg.getType() == MessageType.M_QUIT) {
					parentUI.updateGUI(msg.getType(), msg.getUserId(), "");
				}
			} catch (Exception e) {
				
			}
		}
	}
}
