package server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import server.dao.Manager;
import server.entity.User;
import util.Message;
import util.MessageType;
import util.Translate;

/**
 * ������������Ϣ�ʹ�����Ϣ���߳���
 * 
 * @author sakura
 *
 */
public class RevMessage extends Thread {

	private DatagramSocket serverSocket = null; // �������׽���
	private DatagramPacket packet = null; // ͨ�ű���
	private ServerView parentUI = null; // ����
	private List<User> userList = new ArrayList<User>(); // �����û��б�
	private Manager manager = null;
	private boolean isRunnable = true;
	private byte[] data = new byte[8096]; // 8k

	/**
	 * ���캯��
	 * 
	 * @param serverSocket �������׽���
	 * @param ParentUI     ��������������ʾ
	 */
	public RevMessage(DatagramSocket serverSocket, ServerView parentUI) {
		this.serverSocket = serverSocket;
		this.parentUI = parentUI;
		manager = new Manager();
	}

	public void stopServer() {
		isRunnable = false;
	}

	@Override
	public void run() {
		int type = -1;
		// ѭ�������յ��ĸ�����Ϣ
		while (isRunnable) {
			try {
				packet = new DatagramPacket(data, data.length); // ���ձ���
				serverSocket.receive(packet); // ��������
				// ���յ�����Ϣת��Ϊ����
				Message msg = (Message) Translate.ByteToObject(packet.getData());
				// ��ȡ��Ϣ����
				// ��Ϣ�����е�¼��ע�ᣬ����Ϣ��ȷ�ϣ��ɹ���ʧ��
				type = msg.getType();
				parentUI.updateLog("���ܵ��ͻ�������:" + type);

				switch (type) {
				case MessageType.M_REGIST:
					registHandle(msg.getUserId(), msg.getPassword());
					break;

				case MessageType.M_LOGIN:
					loginHandle(msg.getUserId(), msg.getPassword());
					break;

				case MessageType.M_GROUP:
					groupMessageHandle(msg.getUserId(), msg.getText());
					break;

				case MessageType.M_MSG:
					messageHandle(msg.getUserId(), msg.getTargetId());
					break;

				case MessageType.M_QUIT:
					logoutHandle(msg.getUserId());
					break;

				default:
					break;
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ����ע��
	 * @throws IOException 
	 */
	public void registHandle(String userId, String password) throws IOException {
		Message backMsg = new Message();
		// ע��ɹ�
		if (manager.register(userId, password)) {
			backMsg.setType(MessageType.M_SUCCESS);
			byte[] buf = Translate.ObjectToByte(backMsg);
			DatagramPacket backPacket = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
			serverSocket.send(backPacket);
		// ע��ʧ��
		} else {
			backMsg.setType(MessageType.M_FAILURE);
			byte[] buf = Translate.ObjectToByte(backMsg);
			DatagramPacket backPacket = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
			serverSocket.send(backPacket);
		}
	}

	/**
	 * �����¼
	 * @param userId
	 * @throws IOException
	 */
	public void loginHandle(String userId, String password) throws IOException {
		Message backMsg = new Message();
		// ��¼�ɹ�
		// if ("1000".equals(userId) || "2000".equals(userId) || "4000".equals(userId)) {
		if (manager.login(userId, password)) {
			// ���ͳɹ�����
			backMsg.setType(MessageType.M_SUCCESS);
			byte[] buf = Translate.ObjectToByte(backMsg);
			DatagramPacket backPacket = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
			serverSocket.send(backPacket);

			User user = new User();
			user.setId(userId); // �����û���
			user.setPacket(packet); // �����յ��ı���
			userList.add(user);
			// ���·�������־
			parentUI.updateLog(userId + "��¼");
			// ���������û��б�
			parentUI.updateUserList(true, userId);

			// �������û�����M_LOGIN��Ϣ�������û����������û��б�
			DatagramPacket oldPacket, newPacket;
			Message current = new Message();
			for (int i = 0; i < userList.size(); i++) {
				// ���������û��б�
				// if ���û� �������û���Ϣ
				// �����������û���Ϣ if ���û� ������
				// �����û�����M_LOGIN��Ϣ���˳���data�����û����͹��������ݵĻ��棬�����������M_LOGIN
				if (!userId.equalsIgnoreCase(userList.get(i).getId())) {
					oldPacket = userList.get(i).getPacket(); // ���û��ı���
					newPacket = new DatagramPacket(data, data.length, oldPacket.getAddress(), // �����͵��±���
							oldPacket.getPort());
					serverSocket.send(newPacket);
					// ��ǰ�û�����M_ACK��Ϣ-�����û��б�(�����Լ�)
					// �����û����͵���Ϣ
					System.out.println("���͵���ϢACK��" + userId + userList.get(i).getId());
					current.setUserId(userList.get(i).getId()); // ���÷��ͱ��ĵ��û�idΪ���û�id
					current.setType(MessageType.M_ACK);
					byte[] buffer = Translate.ObjectToByte(current);
					newPacket = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
					serverSocket.send(newPacket);
				}
			}
		} else { // ��¼ʧ��
			backMsg.setType(MessageType.M_FAILURE);
			byte[] buf = Translate.ObjectToByte(backMsg);
			DatagramPacket backPacket = new DatagramPacket(buf, buf.length, packet.getAddress(), packet.getPort());
			serverSocket.send(backPacket);
		}
	}

	/**
	 * 
	 * @param userId
	 * @param text
	 * @throws IOException
	 */
	public void groupMessageHandle(String userId, String text) throws IOException {
		// ���·�������Ϣ�б� public
		parentUI.updateMessageList(userId + ": " + text);

		// ���������û�������Ϣ
		DatagramPacket oldPacket, newPacket;
		for (int i = 0; i < userList.size(); i++) {
			if (!userList.get(i).getId().equalsIgnoreCase(userId)) {
				oldPacket = userList.get(i).getPacket();
				newPacket = new DatagramPacket(data, data.length, oldPacket.getAddress(), oldPacket.getPort());
				serverSocket.send(newPacket);
			}
		}
	}

	/**
	 * 
	 * @param userId   ��Դ�û�id
	 * @param targetId Ŀ���û�id
	 * @param text     ���͵�����
	 * @throws IOException
	 */
	public void messageHandle(String userId, String targetId) throws IOException {
		// ת����Ŀ���û� �����б�ȡ��Ŀ���û��ı���
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getId().equalsIgnoreCase(targetId)) {
				DatagramPacket oldPacket = userList.get(i).getPacket();
				DatagramPacket newPacket = new DatagramPacket(data, data.length, oldPacket.getAddress(),
						oldPacket.getPort());
				serverSocket.send(newPacket);
				return;
			}
		}
	}

	/**
	 * 
	 * @param userId
	 * @throws IOException
	 */
	public void logoutHandle(String userId) throws IOException {
		// �������û��б��Ƴ����û�
		for (int i = 0; i < userList.size(); i++) {
			if (userList.get(i).getId().equalsIgnoreCase(userId)) {
				userList.remove(i);
				break;
			}
		}
		// �ӷ���������Ƴ��û���������־
		parentUI.updateUserList(false, userId);
		parentUI.updateLog(userId + "����");
		// �������û�ת��������Ϣ
		DatagramPacket oldPacket, newPacket;
		for (int i = 0; i < userList.size(); i++) {
			oldPacket = userList.get(i).getPacket();
			newPacket = new DatagramPacket(data, data.length, oldPacket.getAddress(), oldPacket.getPort());
			serverSocket.send(newPacket);
		}
	}
}
