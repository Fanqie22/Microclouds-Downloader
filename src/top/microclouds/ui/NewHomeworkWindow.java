package top.microclouds.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.microclos.uitl.FileDownMessages;

import top.microclouds.download.MutiDownloads;

public class NewHomeworkWindow extends JFrame implements ActionListener {
	protected static JLabel urlAddressLabel = new JLabel("���ص�ַ  :");
	protected static JTextField urlAddress = new JTextField(20);
	protected static JButton testURLButton = new JButton("����URL ");

	protected static JLabel savePathLabel = new JLabel("����Ŀ¼  :");
	protected static JTextField savePath = new JTextField("C:\\Users\\Administrator\\Desktop\\�����ļ���\\test\\", 30);

	protected static JLabel saveFileNameLabel = new JLabel("�� �� ��    :");
	protected static JTextField saveFileName = new JTextField(20);

	protected static JButton filebutton = new JButton("���Ŀ¼");

	protected static JLabel threadNumLbael = new JLabel("���������߳��� :");
	protected static JComboBox<Integer> jComboBox = new JComboBox();

	protected static JLabel fileSizeLabel = new JLabel("�ļ���С :");
	protected static JLabel fileSize = new JLabel("δ֪��С");

	protected static JButton submitbutton = new JButton("ȷ������");

	protected static boolean urlTest = false;

	protected static HttpURLConnection httpURLConnection;

	// protected MainUI mainUI;

	// public static void closeCurrentWindlw() {
	//
	// }

	public NewHomeworkWindow() {
		// this.mainUI = mainUI;
		this.setLayout(null);
		this.setTitle("�½���������");
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(screen.width / 4, screen.height / 4);
		this.setSize(556, 300);

		JPanel jPanel = new JPanel();
		jPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 10));
		jPanel.setBounds(0, 10, 540, 180);
		jPanel.setBorder(BorderFactory.createTitledBorder("�½�����"));

		jPanel.add(urlAddressLabel);
		jPanel.add(urlAddress);
		jPanel.add(testURLButton);
		jPanel.add(savePathLabel);
		jPanel.add(savePath);
		jPanel.add(saveFileNameLabel);
		jPanel.add(saveFileName);
		jPanel.add(filebutton);
		jPanel.add(threadNumLbael);

		jComboBox.addItem(1);
		jComboBox.addItem(2);
		jComboBox.addItem(4);
		jComboBox.addItem(8);
		jComboBox.setPreferredSize(new Dimension(80, 30));

		jPanel.add(jComboBox);
		jPanel.add(fileSizeLabel);
		jPanel.add(fileSize);

		JPanel jPanel2 = new JPanel();
		submitbutton.setPreferredSize(new Dimension(120, 35));
		jPanel2.setLayout(new FlowLayout(FlowLayout.CENTER));
		jPanel2.setBounds(0, 195, 540, 80);
		jPanel2.add(submitbutton);

		savePath.setEditable(false);

		// ��������
		testURLButton.addActionListener(new UrlAddressActionListener());
		filebutton.addActionListener(new FileChooseActionLinstener());
		submitbutton.addActionListener(this);

		this.add(jPanel);
		this.add(jPanel2);
		this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	// �ڲ���,���ڼ����¼�
	static class UrlAddressActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			// JButton jButton = (JButton) e.getSource();
			// jButton.setText("������..");
			String urlString = urlAddress.getText();

			if ("".equals(urlString)) {
				urlTest = false;
				JOptionPane.showMessageDialog(null, "URL �� ַ Ϊ �� !", "�� ʾ", JOptionPane.INFORMATION_MESSAGE);
			} else {

				URL url;
				try {
					url = new URL(urlString);
					httpURLConnection = (HttpURLConnection) url.openConnection();
					httpURLConnection.setConnectTimeout(2000);
					int status = httpURLConnection.getResponseCode();
					fileSize.setText(httpURLConnection.getContentLengthLong() / 1024 + "");
					Pattern FilePattern = Pattern.compile("[\\\\/:*?\"<>|=%&$#~^!@()+]");

					// str==null?null:FilePattern.matcher(str).replaceAll("");

					if (status == 200) {
						urlTest = true;
						String fileNameString = new String();

						if (httpURLConnection.getHeaderField("Content-Disposition") != null) {
							fileNameString = httpURLConnection.getHeaderField("Content-Disposition");
							saveFileName.setText(FilePattern.matcher(fileNameString.substring(21)).replaceAll(""));

						} else {
							if (urlString.indexOf("/") != -1) {

								saveFileName.setText(FilePattern
										.matcher(
												urlString.substring(urlString.lastIndexOf("/") + 1, urlString.length()))
										.replaceAll(""));
							} else {
								saveFileName.setText("myfile");
							}
						}

					} else {
						urlTest = false;
						JOptionPane.showMessageDialog(null, "else �ⲻ��һ����Ч�� URL !", "URL ��������",
								JOptionPane.ERROR_MESSAGE);
						url = null;
					}
				} catch (Exception e1) {
					System.out.println(e1);
					urlTest = false;
					JOptionPane.showMessageDialog(null, "�ⲻ��һ����Ч�� URL !", "URL ��������", JOptionPane.ERROR_MESSAGE);
					url = null;
				}
				// jButton.setText("����URL ");
			}
		}

	}

	static class FileChooseActionLinstener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String DirPath = null;
			JFileChooser jFileChooser = new JFileChooser("C:\\");
			jFileChooser.setFileSelectionMode(jFileChooser.DIRECTORIES_ONLY);
			jFileChooser.setDialogType(jFileChooser.OPEN_DIALOG);

			jFileChooser.showOpenDialog(null);

			if (!"".equals(jFileChooser.getSelectedFile().getAbsolutePath())) {
				DirPath = jFileChooser.getSelectedFile().getAbsolutePath();
			} else {
				DirPath = "C:\\";
			}
			savePath.setText(DirPath);

		}

	}

	public static void main(String[] args) {
		// NewHomeworkWindow newHomeworkWindow = new NewHomeworkWindow();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!urlTest) {
			JOptionPane.showMessageDialog(null, "URL �� ַ Ϊ �� �� �� Ч !", "�� ʾ", JOptionPane.INFORMATION_MESSAGE);
		} else {
			// String fileurl, String fileSavePath,
			// int threadNumber, String fileName
			System.out.println("fileurl  : " + urlAddress.getText() + "   " + "filesavepath  : " + savePath.getText()
					+ "    " + "threadNum  : " + (Integer) jComboBox.getSelectedItem() + "   " + "filename  : "
					+ saveFileName.getText());

			// MutiDownloads mutiDownloads = new MutiDownloads(urlAddress.getText(),
			// savePath.getText(),
			// (Integer) jComboBox.getSelectedItem(), saveFileName.getText());
			// try {
			// mutiDownloads.download();
			//
			//
			//
			// } catch (IOException e1) {
			// e1.printStackTrace();
			// }

			// fileSize.getText().split(" ");

			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ
			FileDownMessages fileDownMessages = new FileDownMessages();
			fileDownMessages.setFileName(saveFileName.getText());
			fileDownMessages.setFileSize(httpURLConnection.getContentLengthLong() / 1024 + " KB");
			fileDownMessages.setDownStatus("�������");
			fileDownMessages.setDownSpeed("-");
			fileDownMessages.setDownProgress("100 %");
			fileDownMessages.setDownStartTime((String) df.format(new Date()));
			fileDownMessages.setFileSavePath(savePath.getText());

			MutiDownloads mutiDownloads = new MutiDownloads(urlAddress.getText(), savePath.getText(),
					(Integer) jComboBox.getSelectedItem(), saveFileName.getText(),
					httpURLConnection.getContentLengthLong() / 1024);
			try {
				mutiDownloads.download(fileDownMessages);
				MainUI mainUI = new MainUI();
				mainUI.updateDwonMessages(saveFileName.getText(), httpURLConnection.getContentLengthLong() / 1024,
						saveFileName.getText(), mutiDownloads);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			this.dispose();// �رյ�ǰ�Ӵ���
		}

	}

}
