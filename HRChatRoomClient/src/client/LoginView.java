package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import client.security.Cryptography;
import util.ClientInfo;
import util.Message;
import util.MessageType;
import util.Translate;

public class LoginView extends JFrame {
	private Container container;
	private JPanel panel_1, panel_2, panel_3;
	private JLabel lbId, lbPassword;
	private JTextField txId;
	private JPasswordField txPassword;
	private JButton btnLogin, btnRegist;
	private InetAddress remoteAddr = null;
	private DatagramSocket clientSocket = null;
	private int remotePort;

	public LoginView() {
		initComponent();
		addListener();
		addClientSocket();
	}

	private void addClientSocket() {
		try {
			// ��ȡ��������ַ�Ͷ˿�
			remoteAddr = InetAddress.getByName(ClientInfo.getServerAddr());
			remotePort = ClientInfo.getServerPort();
			// ����UDP�׽���
			clientSocket = new DatagramSocket();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initComponent() {
		container = this.getContentPane();
		container.setLayout(new BorderLayout());

		panel_1 = new JPanel();
		panel_2 = new JPanel();
		panel_3 = new JPanel();
		lbId = new JLabel("�û���");
		lbPassword = new JLabel("����");
		txId = new JTextField(10);
		txPassword = new JPasswordField(10);
		btnLogin = new JButton("��¼");
		btnRegist = new JButton("ע��");

		// ���� north
		panel_1.add(new JLabel("HR������"));
		// ���� center
		lbId.setBounds(100, 20, 50, 20);
		txId.setBounds(160, 20, 120, 20);
		lbPassword.setBounds(100, 60, 50, 20);
		txPassword.setBounds(160, 60, 120, 20);
		panel_2.setLayout(null);
		panel_2.add(lbId);
		panel_2.add(txId);
		panel_2.add(lbPassword);
		panel_2.add(txPassword);
		// ��ť
		panel_3.setLayout(new FlowLayout());
		panel_3.add(btnLogin);
		panel_3.add(btnRegist);

		container.add(panel_1, "North");
		container.add(panel_2, "Center");
		container.add(panel_3, "South");

		this.setSize(400, 230);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	private void addListener() {
		btnLogin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LoginActionPerformed(e);
			}
		});

		btnRegist.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				RegistActionPerformed(e);
			}
		});
	}

	private void LoginActionPerformed(ActionEvent e) {
		try {
			String id = txId.getText();
			String password = String.valueOf(txPassword.getPassword());
			if (id.equals("") || password.equals("")) {
				JOptionPane.showMessageDialog(null, "�ʺŻ����벻��Ϊ�գ�", "������ʾ", JOptionPane.ERROR_MESSAGE);
				return;
			}
			clientSocket.setSoTimeout(3000);// ���ó�ʱʱ��
			// �����û���¼��Ϣ
			Message msg = new Message();
			msg.setUserId(id);// ��¼��
			msg.setPassword(Cryptography.getHash(password, "SHA-256")); // ����

			msg.setType(MessageType.M_LOGIN); // ��¼��Ϣ����
			msg.setToAddr(remoteAddr); // Ŀ���ַ
			msg.setToPort(remotePort); // Ŀ��˿�
			byte[] data = Translate.ObjectToByte(msg); // ��Ϣ�������л�
			// �����¼����
			DatagramPacket packet = new DatagramPacket(data, data.length, remoteAddr, remotePort);
			// ���͵�¼����
			clientSocket.send(packet);

			// ���շ��������͵ı���
			DatagramPacket backPacket = new DatagramPacket(data, data.length);
			clientSocket.receive(backPacket);

			clientSocket.setSoTimeout(0);// ȡ����ʱʱ��
			Message backMsg = (Message) Translate.ByteToObject(data);
			// �����¼���
			if (backMsg.getType() == MessageType.M_SUCCESS) { // ��¼�ɹ�
				System.out.println("�û�" + msg.getUserId() + "��¼�ɹ�");
				this.dispose(); // �رյ�¼�Ի���
				ClientView client = new ClientView(clientSocket, msg); // �����ͻ�������
				client.setTitle(msg.getUserId()); // ���ñ���
			} else { // ��¼ʧ��
				System.out.println("�û�" + msg.getUserId() + "��¼ʧ��");
				JOptionPane.showMessageDialog(null, "�û�ID���������\n\n��¼ʧ�ܣ�\n", "��¼ʧ��", JOptionPane.ERROR_MESSAGE);
			}

		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "��¼����", JOptionPane.ERROR_MESSAGE);
		} // end try
	}

	private void RegistActionPerformed(ActionEvent e) {
		try {
			String id = txId.getText();
			String password = String.valueOf(txPassword.getPassword());
			if (id.equals("") || password.equals("")) {
				JOptionPane.showMessageDialog(null, "�ʺŻ����벻��Ϊ�գ�", "������ʾ", JOptionPane.ERROR_MESSAGE);
				return;
			}
			clientSocket.setSoTimeout(3000);// ���ó�ʱʱ��
			// �����û���¼��Ϣ
			Message msg = new Message();
			msg.setUserId(id);// ��¼��
			msg.setPassword(Cryptography.getHash(password, "SHA-256")); // ����

			msg.setType(MessageType.M_REGIST); // ��¼��Ϣ����
			msg.setToAddr(remoteAddr); // Ŀ���ַ
			msg.setToPort(remotePort); // Ŀ��˿�
			byte[] data = Translate.ObjectToByte(msg); // ��Ϣ�������л�
			// �����¼����
			DatagramPacket packet = new DatagramPacket(data, data.length, remoteAddr, remotePort);
			// ���͵�¼����
			clientSocket.send(packet);

			// ���շ��������͵ı���
			DatagramPacket backPacket = new DatagramPacket(data, data.length);
			clientSocket.receive(backPacket);

			clientSocket.setSoTimeout(0);// ȡ����ʱʱ��
			Message backMsg = (Message) Translate.ByteToObject(data);
			System.out.println(backMsg.getType());
			// ����ע����
			if (backMsg.getType() == MessageType.M_SUCCESS) { // ��¼�ɹ�
				// ���ҳ�棬���������˻�����
				txId.setText("");
				txPassword.setText("");
				System.out.println("�û�" + msg.getUserId() + "ע��ɹ�");
			} else { // ע��ʧ��
				// ���ҳ�棬���������˻�����
				txId.setText("");
				txPassword.setText("");
				System.out.println("�û�" + msg.getUserId() + "ע��ʧ��");
				JOptionPane.showMessageDialog(null, "�û�ID�Ѵ��ڣ�\n\nע��ʧ�ܣ�\n", "ע��ʧ��", JOptionPane.ERROR_MESSAGE);
			}
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "ע�����", JOptionPane.ERROR_MESSAGE);
		} // end try
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				new LoginView();
			}
		});
	}
}
