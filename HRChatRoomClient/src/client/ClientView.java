package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import client.entity.Record;
import util.GoUrl;
import util.Message;
import util.MessageType;
import util.Translate;

public class ClientView {

	private DatagramSocket clientSocket; // �ͻ����׽���
	private Message msg; // ��Ϣ����
	private byte[] data = new byte[8096];

	private JFrame frame;
	private RevMessage client;
	private JList<String> userList;
	private JTextArea textArea, txt_msg;
	private JTextField txt_port, txt_hostIP, txt_name;
	private JButton btn_start, btn_stop, btn_send, btn_sendFile, btn_goNew, btn_goGame;
	private JPanel northPanel, eastPanel, southPanel, panel;
	private JScrollPane rightScroll, leftScroll, msgScroll;
	private JSplitPane centerSplit, rightSplit;
	private DefaultListModel<String> listModel;
	private String currentUser, chatUser;
	private boolean isGroup;

	// private List<User> onlineUsers;

	private static String[] DEFAULT_FONT = new String[] { "Table.font", "TableHeader.font", "CheckBox.font",
			"Tree.font", "Viewport.font", "ProgressBar.font", "RadioButtonMenuItem.font", "ToolBar.font",
			"ColorChooser.font", "ToggleButton.font", "Panel.font", "TextArea.font", "Menu.font", "TableHeader.font",
			"OptionPane.font", "MenuBar.font", "Button.font", "Label.font", "PasswordField.font", "ScrollPane.font",
			"MenuItem.font", "ToolTip.font", "List.font", "EditorPane.font", "Table.font", "TabbedPane.font",
			"RadioButton.font", "CheckBoxMenuItem.font", "TextPane.font", "PopupMenu.font", "TitledBorder.font",
			"ComboBox.font" };

	public ClientView(DatagramSocket clientSocket, Message msg) {
		this();
		this.clientSocket = clientSocket;
		this.msg = msg;
		initConfig();
		// �����ͻ�����Ϣ���ܺʹ����߳�
		client = new RevMessage(clientSocket, this);
		client.start();
	}

	public ClientView() {
		initialGUI();
		addListeners();
	}

	public void initConfig() {
		// ��Ϣ��ʼ��
		this.currentUser = msg.getUserId();
		this.chatUser = "HR������";
		this.listModel.addElement(chatUser);
		this.userList.setSelectedIndex(0);
		this.isGroup = true;
	}

	private void formWindowClosing(WindowEvent event) {
		try {
			msg.setType(MessageType.M_QUIT);
			msg.setText(null);
			data = Translate.ObjectToByte(msg);
			// ����
			DatagramPacket packet = new DatagramPacket(data, data.length, msg.getToAddr(), msg.getToPort());
			clientSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (clientSocket != null)
				clientSocket.close();
		}

	}
	
	private void sendFileListener(ActionEvent e) {
		// TODO Auto-generated method stub
		JFileChooser fd = new JFileChooser();
		fd.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fd.showOpenDialog(null);
		// ��ȡѡ�е��ļ�
		File f = fd.getSelectedFile();
		SwingWorker<List<String>, String> sender = new SendFile(f, msg, this);
		sender.execute();
	}

	private void addListeners() {

		// ��Ӵ����¼�������
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				formWindowClosing(e);
			}
		});

		// ������Ϣ
		btn_send.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String txt = txt_msg.getText();
				System.out.println(isGroup);
				// ˽��
				if (!txt.isEmpty() && !isGroup) {
					sendMessage(MessageType.M_MSG, txt, chatUser);
					receiveMessage("��", txt);
					client.addChatRecords(MessageType.M_MSG, chatUser, "��", txt);
					txt_msg.setText("");
					// Ⱥ��
				} else if (!txt.isEmpty() && isGroup) {
					sendMessage(MessageType.M_GROUP, txt, "");
					receiveMessage("��", txt);
					client.addChatRecords(MessageType.M_MSG, chatUser, "��", txt);
					txt_msg.setText("");
				} else {
					JOptionPane.showMessageDialog(frame, "��Ϣ����Ϊ��!", "", JOptionPane.WARNING_MESSAGE);
				}
			}
		});

		// �����ļ�
		btn_sendFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sendFileListener(e);
			}
		});

		// �л�����
		userList.addMouseListener(new MouseAdapter() {

			// �û��л����ڣ����������¼
			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				// ��ȡѡ�е��б�ֵ
				String content = (String) userList.getSelectedValue();
				// ��ȡѡ�е�����
				int i = userList.getSelectedIndex();
				System.out.println("�л�����" + content);
				// ����������Ϣ��
				if (content != null && content.contains("(New Message)")) {
					chatUser = content.substring(0, content.indexOf('('));
					listModel.add(i, chatUser);
					listModel.remove(i + 1);
				} else {
					chatUser = content;
				}
				// �ж��Ƿ�Ⱥ��
				if (chatUser.contains("HR������")) {
					isGroup = true;
				} else {
					isGroup = false;
				}
				textArea.setText("");
				ArrayList<Record> chatRecords = client.getChatRecords(chatUser);
				for (int j = 0; j < chatRecords.size(); j++) {
					// ��������Ϣչʾ���������
					receiveMessage(chatRecords.get(j).getSender(), chatRecords.get(j).getText());
				}
			}
		});
		
		// ����������
		btn_goNew.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GoUrl.openURL("http://news.sise.com.cn/");
			}
		});
		
		// ����Ϸ
		btn_goGame.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				GoUrl.openURL("https://2048game.com/");
			}
		});
	}

	private void initialGUI() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		UIManager.put("RootPane.setupButtonVisible", false);

		// ����Ĭ������
		for (int i = 0; i < DEFAULT_FONT.length; i++)
			UIManager.put(DEFAULT_FONT[i], new Font("Microsoft YaHei UI", Font.PLAIN, 15));

		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setForeground(Color.gray);

		txt_msg = new JTextArea();
		txt_port = new JTextField("8080");
		txt_hostIP = new JTextField();
		txt_name = new JTextField("");
		btn_start = new JButton("����");
		btn_stop = new JButton("�˳�");
		btn_send = new JButton("����");
		btn_sendFile = new JButton("�����ļ�");
		btn_goNew = new JButton("����");
		btn_goGame = new JButton("��Ϸ");

		listModel = new DefaultListModel<String>();
		userList = new JList<String>(listModel);

		northPanel = new JPanel();

		GridBagLayout gridBagLayout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();

		northPanel.setLayout(gridBagLayout);

		constraints.insets = new Insets(0, 5, 0, 5);
		constraints.fill = GridBagConstraints.BOTH;

		JLabel label;

		constraints.weightx = 1.0;
		label = new JLabel("�˿�");
		gridBagLayout.setConstraints(label, constraints);
		northPanel.add(label);

		constraints.weightx = 3.0;
		gridBagLayout.setConstraints(txt_port, constraints);
		northPanel.add(txt_port);

		constraints.weightx = 1.0;
		label = new JLabel("������IP");
		gridBagLayout.setConstraints(label, constraints);
		northPanel.add(label);

		constraints.weightx = 3.0;
		gridBagLayout.setConstraints(txt_hostIP, constraints);
		northPanel.add(txt_hostIP);

		constraints.weightx = 1.0;
		label = new JLabel("����");
		gridBagLayout.setConstraints(label, constraints);
		northPanel.add(label);

		constraints.weightx = 3.0;
		gridBagLayout.setConstraints(txt_name, constraints);
		northPanel.add(txt_name);
		gridBagLayout.setConstraints(btn_start, constraints);
		northPanel.add(btn_start);
		gridBagLayout.setConstraints(btn_stop, constraints);
		northPanel.add(btn_stop);

		// northPanel.setBorder(new TitledBorder("������Ϣ"));

		rightScroll = new JScrollPane(textArea);
		rightScroll.setBorder(new TitledBorder("������Ϣ"));

		leftScroll = new JScrollPane(userList);
		leftScroll.setBorder(new TitledBorder("�����û�"));

		msgScroll = new JScrollPane(txt_msg);

		southPanel = new JPanel(new BorderLayout());
		southPanel.add(msgScroll, "Center");

		panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		btn_send.setMargin(new Insets(5, 20, 5, 20));
		btn_sendFile.setMargin(new Insets(5, 20, 5, 20));
		btn_goNew.setMargin(new Insets(5, 20, 5, 20));
		btn_goGame.setMargin(new Insets(5, 20, 5, 20));
		panel.add(btn_goGame);
		panel.add(btn_goNew);
		panel.add(btn_sendFile);
		panel.add(btn_send);

		southPanel.add(panel, "South");
		southPanel.setBorder(new TitledBorder("����"));

		rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightScroll, southPanel);
		rightSplit.setDividerLocation(400);

		eastPanel = new JPanel(new BorderLayout());
		eastPanel.add(rightSplit, "Center");

		centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, eastPanel);
		centerSplit.setDividerLocation(200);

		frame = new JFrame();
		frame.setSize(800, 600);

		frame.setLayout(new BorderLayout());
		frame.add(centerSplit, "Center");

		int screenWidth = toolkit.getScreenSize().width;
		int screenHeight = toolkit.getScreenSize().height;

		frame.setLocation((screenWidth - frame.getWidth()) / 2, (screenHeight - frame.getHeight()) / 2);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * ���ͱ���
	 * 
	 * @param type     ����Э������
	 * @param text     ��Ϣ����
	 * @param targetId Ŀ��
	 */
	public void sendMessage(int type, String text, String targetId) {
		try {
			msg.setType(type);
			msg.setText(text);
			msg.setTargetId(targetId);
			data = Translate.ObjectToByte(msg);
			DatagramPacket packet = new DatagramPacket(data, data.length, msg.getToAddr(), msg.getToPort());
			clientSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * д����Ϣ����Ϣ�б�
	 * 
	 * @param user
	 * @param text
	 */
	public void receiveMessage(String user, String text) {
		textArea.append(user + " : " + text);
		textArea.append("\r\n\r\n");
	}

	/**
	 * ���ݱ���Э�����ͣ������û��б���ʾ e.g �յ�Ⱥ������Ϣ������ǰ��JList��������Ⱥ���ϣ���Ⱥ�����ƺ���ʾ(New Message)
	 * 
	 * @param type
	 * @param sender
	 * @param text
	 */
	public void updateGUI(int type, String sender, String text) {
		if (type == MessageType.M_GROUP) {
			if (chatUser.equals("HR������")) {
				receiveMessage(sender, text);
			} else {
				String name = (String) listModel.elementAt(0);
				listModel.add(0, name + "(New Message)");
				listModel.remove(1);
			}
		} else if (type == MessageType.M_MSG) {
			if (chatUser.equals(sender)) {
				receiveMessage(sender, text);
			} else {
				for (int i = 0; i < listModel.size(); i++) {
					String name = (String) listModel.elementAt(i);
					if (name.contains(sender)) {
						listModel.remove(i);
						listModel.add(i, name + "(New Message)");
						break;
					}
				}
			}
		} else if (type == MessageType.M_LOGIN || type == MessageType.M_ACK) {
			listModel.addElement(sender);
		} else if (type == MessageType.M_QUIT) {
			for (int i = 0; i < listModel.size(); i++) {
				String name = (String) listModel.elementAt(i);
				if (name.contains(sender)) {
					listModel.remove(i);
					return;
				}
			}
		} else if (type == MessageType.M_FILE) {
			JOptionPane.showMessageDialog(frame, text, "ϵͳ��Ϣ", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void setTitle(String name) {
		frame.setTitle(name);
	}
	
	public static void main(String[] args) {
		new ClientView();
	}
}
