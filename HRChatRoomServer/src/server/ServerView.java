package server;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import util.ServerInfo;

/**
 * ������������
 * 
 * @author sakura
 *
 */
public class ServerView extends JFrame {
	private static final long serialVersionUID = 1L;
	int onlineCount = 0;
	RevMessage server;
	DefaultListModel<String> model = new DefaultListModel<String>();

	// ��������Ϣ���
	JPanel pnlServer, pnlServerInfo;
	JLabel lblStatus, lblNumber, lblMax, lblServerName, lblIP, lblPort, lblLog;
	JTextField txtStatus, txtNumber, txtMax, txtServerName, txtIP, txtPort;
	JButton btnStart, btnStop, btnSaveLog;
	TextArea taLog;
	JTabbedPane tpServer;
	TextArea taMessage;

	// �û���Ϣ���
	JPanel pnlUser;
	JLabel lblMessage, lblUser, lblNotice, lblUserCount;
	JList<String> lstUser;
	JScrollPane spUser;
	JTextField txtNotice;
	JButton btnSend, btnKick;
	String ti = "";

	String serverAddr;
	int serverPort;

	public ServerView() {
		init();
		addListeners();
	}

	public void init() {
		// ��ʼ������
		serverAddr = ServerInfo.getServerAddr();
		serverPort = ServerInfo.getServerPort();

		// ����������
		setTitle("HR���������");
		setSize(550, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLocationRelativeTo(null);// ������ʾ

		// ==========��������Ϣ���=========================
		pnlServer = new JPanel();
		pnlServer.setLayout(null);
		pnlServer.setBackground(new Color(60, 150, 100));

		pnlServerInfo = new JPanel(new GridLayout(14, 1));
		pnlServerInfo.setBackground(new Color(60, 150, 100));
		pnlServerInfo.setFont(new Font("����", 0, 12));
		pnlServerInfo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""),
				BorderFactory.createEmptyBorder(1, 1, 1, 1)));

		lblStatus = new JLabel("��ǰ״̬:");
		lblStatus.setForeground(Color.YELLOW);
		lblStatus.setFont(new Font("����", 0, 12));
		txtStatus = new JTextField("δ����", 10);
		txtStatus.setBackground(Color.decode("#d6f4f2"));
		txtStatus.setFont(new Font("����", 0, 12));
		txtStatus.setEditable(false);

		lblNumber = new JLabel("��ǰ��������:");
		lblNumber.setForeground(Color.YELLOW);
		lblNumber.setFont(new Font("����", 0, 12));
		txtNumber = new JTextField("0 ��", 10);
		txtNumber.setBackground(Color.decode("#d6f4f2"));
		txtNumber.setFont(new Font("����", 0, 12));
		txtNumber.setEditable(false);

		lblMax = new JLabel("�����������:");
		lblMax.setForeground(Color.YELLOW);
		lblMax.setFont(new Font("����", 0, 12));
		txtMax = new JTextField("20 ��", 10);
		txtMax.setBackground(Color.decode("#d6f4f2"));
		txtMax.setFont(new Font("����", 0, 12));
		txtMax.setEditable(false);

		lblServerName = new JLabel("����������:");
		lblServerName.setForeground(Color.YELLOW);
		lblServerName.setFont(new Font("����", 0, 12));
		txtServerName = new JTextField("HR���������", 10);
		txtServerName.setBackground(Color.decode("#d6f4f2"));
		txtServerName.setFont(new Font("����", 0, 12));
		txtServerName.setEditable(false);

		lblIP = new JLabel("������IP:");
		lblIP.setForeground(Color.YELLOW);
		lblIP.setFont(new Font("����", 0, 12));
		txtIP = new JTextField(serverAddr, 10);
		txtIP.setBackground(Color.decode("#d6f4f2"));
		txtIP.setFont(new Font("����", 0, 12));
		txtIP.setEditable(false);

		lblPort = new JLabel("�������˿�:");
		lblPort.setForeground(Color.YELLOW);
		lblPort.setFont(new Font("����", 0, 12));
		txtPort = new JTextField(Integer.toString(serverPort), 10);
		txtPort.setBackground(Color.decode("#d6f4f2"));
		txtPort.setFont(new Font("����", 0, 12));
		txtPort.setEditable(false);

		btnStart = new JButton("����������");
		btnStart.setBackground(Color.lightGray);
		btnStart.setFont(new Font("����", 0, 12));

		btnStop = new JButton("�رշ�����");
		btnStop.setBackground(Color.lightGray);
		btnStop.setFont(new Font("����", 0, 12));

		lblLog = new JLabel("[��������־]");
		lblLog.setForeground(Color.YELLOW);
		lblLog.setFont(new Font("����", 0, 12));

		taLog = new TextArea(20, 50);
		taLog.setFont(new Font("����", 0, 12));
		btnSaveLog = new JButton("������־");
		btnSaveLog.setBackground(Color.lightGray);
		btnSaveLog.setFont(new Font("����", 0, 12));

		pnlServerInfo.add(lblStatus);
		pnlServerInfo.add(txtStatus);
		pnlServerInfo.add(lblNumber);
		pnlServerInfo.add(txtNumber);
		pnlServerInfo.add(lblMax);
		pnlServerInfo.add(txtMax);
		pnlServerInfo.add(lblServerName);
		pnlServerInfo.add(txtServerName);
		pnlServerInfo.add(lblIP);
		pnlServerInfo.add(txtIP);
		pnlServerInfo.add(lblPort);
		pnlServerInfo.add(txtPort);

		pnlServerInfo.setBounds(5, 5, 100, 400);
		lblLog.setBounds(110, 5, 100, 30);
		taLog.setBounds(110, 35, 400, 370);
		btnStart.setBounds(120, 410, 120, 30);
		btnStop.setBounds(240, 410, 120, 30);
		btnSaveLog.setBounds(360, 410, 120, 30);
		pnlServer.add(pnlServerInfo);
		pnlServer.add(lblLog);
		pnlServer.add(taLog);
		pnlServer.add(btnStart);
		pnlServer.add(btnStop);
		pnlServer.add(btnSaveLog);

		// ===========�����û����====================
		pnlUser = new JPanel();
		pnlUser.setLayout(null);
		pnlUser.setBackground(new Color(60, 150, 100));
		pnlUser.setFont(new Font("����", 0, 12));
		lblMessage = new JLabel("[�û���Ϣ]");
		lblMessage.setFont(new Font("����", 0, 12));
		lblMessage.setForeground(Color.YELLOW);
		taMessage = new TextArea(20, 20);
		taMessage.setFont(new Font("����", 0, 12));
		lblNotice = new JLabel("֪ͨ��");
		lblNotice.setFont(new Font("����", 0, 12));
		txtNotice = new JTextField(20);
		txtNotice.setFont(new Font("����", 0, 12));
		btnSend = new JButton("����");
		btnSend.setBackground(Color.lightGray);
		btnSend.setFont(new Font("����", 0, 12));
		btnSend.setEnabled(true);

		lblUserCount = new JLabel("���������� 0 ��");
		lblUserCount.setFont(new Font("����", 0, 12));

		btnKick = new JButton("����");
		btnKick.setBackground(Color.lightGray);
		btnKick.setFont(new Font("����", 0, 12));
		lblUser = new JLabel("[�����û��б�]");
		lblUser.setFont(new Font("����", 0, 12));
		lblUser.setForeground(Color.YELLOW);

		lstUser = new JList<String>(model);
		lstUser.setFont(new Font("����", 0, 12));
		lstUser.setVisibleRowCount(17);
		lstUser.setFixedCellWidth(180);
		lstUser.setFixedCellHeight(18);

		spUser = new JScrollPane();
		spUser.setBackground(Color.decode("#15c7f3"));
		spUser.setFont(new Font("����", 0, 12));
		// spUser.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		spUser.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		spUser.getViewport().setView(lstUser);

		lblMessage.setBounds(5, 5, 100, 25);
		taMessage.setBounds(5, 35, 300, 360);
		taMessage.setEditable(false);
		lblUser.setBounds(310, 5, 100, 25);
		spUser.setBounds(310, 35, 220, 360);
		lblNotice.setBounds(5, 410, 40, 25);
		txtNotice.setBounds(50, 410, 160, 25);
		btnSend.setBounds(210, 410, 80, 25);
		lblUserCount.setBounds(320, 410, 100, 25);
		btnKick.setBounds(440, 410, 80, 25);

		pnlUser.add(lblMessage);
		pnlUser.add(taMessage);
		pnlUser.add(lblUser);
		pnlUser.add(spUser);

		pnlUser.add(lblNotice);
		pnlUser.add(txtNotice);
		pnlUser.add(btnSend);
		pnlUser.add(lblUserCount);
		pnlUser.add(btnKick);

		// ============����ǩ���========================

		btnStop.setEnabled(false);
		tpServer = new JTabbedPane(JTabbedPane.TOP);
		tpServer.setBackground(Color.decode("#0ea276"));
		tpServer.setFont(new Font("����", 0, 12));
		tpServer.add("����������", pnlServer);
		tpServer.add("����������", pnlUser);
		this.getContentPane().add(tpServer);
		setVisible(true);
	}

	private void addListeners() {
		// ����������
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				btnStartActionPerformed(event);
			}
		});

		// �رշ�����
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnCloseServer();
			}
		});

		// ������־
		btnSaveLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSaveLog();
			}
		});

		// ����֪ͨ
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSendNotice();
			}
		});

		// ����
		btnKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnTiRen();
			}
		});
	}

	/**
	 * ����Ա��֪ͨ
	 */
	private void btnSendNotice() {
		String text = txtNotice.getText();
		if (!text.isEmpty()) {
			txtNotice.setText("");
			System.out.println(text);

		} else {
			JOptionPane.showMessageDialog(getParent(), "��Ϣ����Ϊ��!");
		}
	}

	private void btnSaveLog() {
		try {
			FileOutputStream fileoutput = new FileOutputStream("log/log.txt", true);
			String temp = taLog.getText();
			// System.out.println(temp);
			fileoutput.write(temp.getBytes());
			fileoutput.close();
			JOptionPane.showMessageDialog(null, "��¼������log.txt");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void btnTiRen() {
		// TODO �Զ����ɷ������

	}

	private void btnCloseServer() {
		// TODO �Զ����ɷ������
		server.stopServer();
		this.dispose();
	}

	// ������������ť
	private void btnStartActionPerformed(ActionEvent event) {
		// ����UDP���ݱ��׽���,��ָ���˿�����
		try {
			InetAddress sa = InetAddress.getByName(serverAddr);
			DatagramSocket serverSocket = new DatagramSocket(serverPort, sa);
			updateLog("������" + sa.getHostAddress() + ":" + serverSocket.getLocalPort() + "��ʼ����...\n");
			// ����ͨ�����߳�
			server = new RevMessage(serverSocket, this);
			server.start();
			txtStatus.setText("����");
			
			// �����ļ������߳�
			FileThread fileThread = new FileThread();
			fileThread.start();
		}
		catch (SocketException e1) {
			e1.printStackTrace();
		} catch (UnknownHostException e2) {
			e2.printStackTrace();
		}
		
		btnStart.setEnabled(false);
		btnStop.setEnabled(true);
	}

	// ���·�������־
	public void updateLog(String msg) {
		taLog.append(ServerInfo.getCurrentTime() + " : " + msg + "\n");
	}

	// ������Ϣ�б�
	public void updateMessageList(String msg) {
		taMessage.append(msg + "\n");
	}

	// �����û��б�
	public void updateUserList(boolean type, String id) {
		// ��������û�
		if (type) {
			model.addElement(id);
			onlineCount++;
			lblUserCount.setText("���������� " + onlineCount + " ��");
			txtNumber.setText(Integer.toString(onlineCount));
		} else { // �Ƴ��û�
			if (model.contains(id)) {
				model.removeElement(id);
				onlineCount--;
				lblUserCount.setText("���������� " + onlineCount + " ��");
				txtNumber.setText(Integer.toString(onlineCount));
			}
		}

	}

	public static void main(String[] args) {
		new ServerView();
	}

	private class FileThread extends Thread {
		@Override
		public void run() {
			try {
				// ��ȡ��Կ������
				String serverPass = ServerInfo.getServerPass();
				String myserverPass = ServerInfo.getMyserverPass();
				// ��ȡ��Կ��·��
				String serverPath = ServerInfo.getServerPath();
				String myserverPath = ServerInfo.getMyserverPath();

				// ��ȡ֤���
				InputStream key = new FileInputStream(serverPath);// ˽Կ��
				InputStream tkey = new FileInputStream(myserverPath);// ��Կ��

				SSLContext ctx = SSLContext.getInstance("SSL");// SSL������
				KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				// ��������Կ��
				KeyStore ks = KeyStore.getInstance("JKS");
				// �����ε���Կ��
				KeyStore tks = KeyStore.getInstance("JKS");
				// ����˽Կ֤���
				ks.load(key, serverPass.toCharArray());
				// ���ع�Կ֤���
				tks.load(tkey, myserverPass.toCharArray());
				kmf.init(ks, serverPass.toCharArray());
				tmf.init(tks);
				ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
				// ������������ȫ����
				SSLServerSocket sslListenSocket = (SSLServerSocket) ctx.getServerSocketFactory()
						.createServerSocket(ServerInfo.getServerPort());
				int processors = Runtime.getRuntime().availableProcessors();// CPU��
				ExecutorService fixedPool = Executors.newFixedThreadPool(processors * 2);// �����̶���С�̳߳�
				while (true) { // �������пͻ�������
					SSLSocket fileSocket = (SSLSocket) sslListenSocket.accept();// ��������ӣ�������������������Ӳ������µĻỰ�׽���
					// �ļ������߳�ΪSwingWorker���͵ĺ�̨�����߳�
					SwingWorker<Integer, Object> recver = new RevFile(fileSocket, ServerView.this, tks, ks); // �����ͻ��߳�
					fixedPool.execute(recver); // ���̳߳ص��ȿͻ��߳�����
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null, ex.getMessage(), "������ʾ", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
}
