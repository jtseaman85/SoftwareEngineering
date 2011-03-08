import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;

class NetworkUI extends JFrame implements Runnable {

	public static JTextArea chatArea;
	public static JTextArea ipField;
	public static JTextField textField;
	public static JLabel statusTitle;
	public static JLabel status;
	public static JButton send;
	public static JButton connect;
	public static JButton listen;
	private static StringBuffer toAppend;
	private static StringBuffer toSend;
	private static SocketConnection socket;

	public NetworkUI() {
		setTitle("Client Chat");
		createContents();
	}

	private void createContents() {
		JLabel title = new JLabel("Socket Test");
		JTextField ipField = new JTextField(10);
		connect = new JButton("connect");
		connect.addActionListener(new ActionAdapter() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO insert things to do when connect button is clicked
			}
		} );
		listen = new JButton("listen");
		listen.addActionListener(new ActionAdapter() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO insert things to do when connect button is clicked
			}
		} );
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.add(title);
		titlePanel.add(ipField);
		titlePanel.add(connect);
		titlePanel.add(listen);
		
		statusTitle = new JLabel("Status: ");
		status = new JLabel("");
		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		statusPanel.add(statusTitle);
		statusPanel.add(status);

		chatArea = new JTextArea();
		chatArea.setLineWrap(true);
		chatArea.setEditable(false);
		chatArea.setPreferredSize(new Dimension(300, 150));

		textField = new JTextField();
		textField.setPreferredSize(new Dimension(250, 25));
		send = new JButton("Send");
		send.addActionListener(new ActionAdapter() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO insert things to do when send button is clicked
			}
		} );

		JPanel panel = new JPanel();
		JPanel northPanel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

		northPanel.add("North", titlePanel);
		northPanel.add("Center", statusPanel);
		northPanel.setSize(new Dimension(300, 60));
		centerPanel.add(chatArea);
		southPanel.add(textField);
		southPanel.add(send);

		panel.setLayout(new BorderLayout());
		panel.setBackground(Color.black);
		panel.add("North", northPanel);
		panel.add("Center", centerPanel);
		panel.add("South", southPanel);

		getContentPane().add(panel);
		pack();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	private static void appendToHistory(String s) {
		synchronized (toAppend) {
			toAppend.append(s);
		}
	}
	
	private static void sendOut(String s) {
		synchronized (toSend) {
			toSend.append(s);
		}
	}
	
	public static void main(String[] args) {
		NetworkUI frame = new NetworkUI();
		frame.setTitle("TCP Connection");
		WindowListener l = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		};
		frame.addWindowListener(l);
		frame.setVisible(true);
		
		socket = new SocketConnection();
		
		while(true) {
			if (!socket.isConnected()) {
				
			}
		}
		
	}

	
}

class ActionAdapter implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
	}
}