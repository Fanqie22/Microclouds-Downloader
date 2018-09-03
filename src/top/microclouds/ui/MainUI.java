package top.microclouds.ui;

import java.awt.Button;
import java.awt.CardLayout;

import java.awt.Dimension;

import java.awt.Font;

import java.awt.GridLayout;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

import com.microclos.uitl.FileDownMessages;

import top.microclouds.download.MutiDownloads;

public class MainUI implements ActionListener {

	protected static String title = "Microclouds�����";// ���������ڵı���
	protected static JFrame jFrame;// �����ڿ��
	protected static CardLayout cardLayout = new CardLayout();// ��Ƭʽ����,�����л�downPanle ��cloudpanle
	protected static JPanel panel = new JPanel();// ���cloudpanel��downpanel����
	protected static JPanel cloudPanel = new JPanel();// �������
	protected static JPanel downPanel = new JPanel();// �������

	protected static JButton cloudBtn = new JButton("�ҵ�����");// ������л�����
	protected static JButton downBtn = new JButton("��������");// �������������İ�ť

	protected static Vector rowData;// ��ӵ����������б�
	protected static DefaultTableModel defaultTableModel;// ���ģ��
	protected static JTable jTable;// ���
	protected static JScrollPane jScrollPane;// ��������

	protected static FileDownMessages fileDownMessages;

	protected static MutiDownloads mutiDownloads;

	protected static int rowNum = 0;

	public MainUI() {
	}

	public MainUI(int i) throws IOException {

		jFrame = new JFrame(title);
		jFrame.setResizable(false);
		jFrame.setBounds(90, 30, 1200, 660);
		jFrame.setLayout(null);

		panel.setLayout(cardLayout);
		panel.setBounds(0, 60, 1200, 550);
		panel.setBorder(new TitledBorder("�� �� �� ��"));

		panel.add(cloudPanel, "cloud");
		panel.add(downPanel, "down");

		this.cloudPanelComponent();
		this.downPanelComponent();
		this.topComponent();
		this.CardTopComponent();
		cardLayout.show(panel, "down");

		jFrame.add(panel);
		jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
		jFrame.setIconImage(new ImageIcon("images/cloud.png").getImage());
		jFrame.setVisible(true);
	}

	public void setColumnSize(JTable table, int i, int preferedWidth, int maxWidth, int minWidth) {
		// ������ģ��
		TableColumnModel cm = table.getColumnModel();
		// �õ���i���ж���
		TableColumn column = cm.getColumn(i);
		column.setPreferredWidth(preferedWidth);
		column.setMaxWidth(maxWidth);
		column.setMinWidth(minWidth);
	}

	public void setTableAllRowHeight(JTable table, int height) {
		table.setRowHeight(height);
	}

	public void setTableColumnCenter(JTable table) {
		DefaultTableCellRenderer r = new DefaultTableCellRenderer();
		r.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, r);
	}

	public void topComponent() {

		JPanel topPanel = new JPanel();
		topPanel.setSize(280, 50);
		GridLayout gridLayout = new GridLayout(1, 4);
		topPanel.setLayout(gridLayout);

		JButton addBtn, deleteBtn, stopBtn, restartBtn;
		ImageIcon addIcon, deleteIcon, restartIcon, stopIcon;
		addIcon = new ImageIcon("images/add.png");
		deleteIcon = new ImageIcon("images/delete.png");
		restartIcon = new ImageIcon("images/restart.png");
		stopIcon = new ImageIcon("images/stop.png");
		addBtn = new JButton();
		deleteBtn = new JButton();
		stopBtn = new JButton();
		restartBtn = new JButton();

		addBtn.setOpaque(false);
		addBtn.setMargin(new Insets(0, 0, 0, 0));
		addBtn.setFocusPainted(false);
		addBtn.setIcon(addIcon);
		addBtn.setToolTipText("�������");
		addBtn.setContentAreaFilled(false);

		deleteBtn.setOpaque(false);
		deleteBtn.setContentAreaFilled(false);
		deleteBtn.setMargin(new Insets(0, 0, 0, 0));
		deleteBtn.setFocusPainted(false);
		deleteBtn.setIcon(deleteIcon);
		deleteBtn.setToolTipText("ɾ������");

		stopBtn.setOpaque(false);
		stopBtn.setContentAreaFilled(false);
		stopBtn.setMargin(new Insets(0, 0, 0, 0));
		stopBtn.setFocusPainted(false);
		stopBtn.setIcon(stopIcon);
		stopBtn.setToolTipText("��ͣ����");

		restartBtn.setOpaque(false);
		restartBtn.setContentAreaFilled(false);
		restartBtn.setMargin(new Insets(0, 0, 0, 0));
		restartBtn.setFocusPainted(false);
		restartBtn.setIcon(restartIcon);
		restartBtn.setToolTipText("��ʼ����");

		topPanel.add(addBtn);
		topPanel.add(restartBtn);
		topPanel.add(stopBtn);
		topPanel.add(deleteBtn);

		// ��Ӱ�ť�����¼�
		addBtn.addActionListener(this);
		stopBtn.addActionListener(new StopHomeworkListener());
		restartBtn.addActionListener(new RestartHomeworkListener());
		deleteBtn.addActionListener(new DeleteHomeworkListener());

		jFrame.add(topPanel);
	}

	public void CardTopComponent() {
		JPanel cardPanel = new JPanel();
		GridLayout gridLayout = new GridLayout(1, 2);
		cardPanel.setLayout(gridLayout);
		cardPanel.setBounds(308, 0, 240, 50);

		ImageIcon cloudIcon = new ImageIcon("images/clouddisk.png");
		ImageIcon downIcon = new ImageIcon("images/download.png");

		cloudBtn.setIcon(cloudIcon);
		cloudBtn.setOpaque(false);
		downBtn.setOpaque(false);
		downBtn.setIcon(downIcon);
		cloudBtn.setMargin(new Insets(0, 0, 0, 0));
		cloudBtn.setFocusPainted(false);
		cloudBtn.setContentAreaFilled(false);
		downBtn.setMargin(new Insets(0, 0, 0, 0));
		downBtn.setFocusPainted(false);
		downBtn.setContentAreaFilled(false);

		cloudBtn.addActionListener(new MyActionListener());
		downBtn.addActionListener(new MyActionListener());

		cardPanel.add(cloudBtn);
		cardPanel.add(downBtn);

		jFrame.add(cardPanel);
	}

	public void cloudPanelComponent() {
		cloudPanel.setBounds(0, 80, 1056, 500);
		cloudPanel.setBorder(new TitledBorder("cloud panel"));
		Button aButton = new Button("dd");
		cloudPanel.add(aButton);
	}

	public void downPanelComponent() throws IOException {
		// downPanel.setBorder(new TitledBorder("�����б�"));
		String[] col = { "�ļ���", "�ļ���С", "״̬", "�����ٶ�", "����", "��ʼʱ��", "����λ��" };
		String[][] row = { { "�ļ���", "�ļ���С", "״̬", "�����ٶ�", "����", "2018-12-12 12:12", "����λ��" },
				{ "�ļ���", "�ļ���С", "״̬", "�����ٶ�", "����", "2018-12-12 12:12", "����λ��" } };

		String[][] row2 = null;

//		Vector lists = new Vector<>();

		defaultTableModel = new DefaultTableModel(row2, col);

		String string = new String();
		String[] strings = new String[10];

		FileReader readMessages = new FileReader("I:\\JavaSE�������\\Microclouds\\messages\\config.txt");
		BufferedReader bufferedReader = new BufferedReader(readMessages);
		while ((string = bufferedReader.readLine()) != null) {
			Vector lists = new Vector<>();
			strings = string.split("\\*");
			System.out.println("ddddddddddddddddddd");
			for (int i = 0; i < 7; i++) {
				System.out.println(" list : --> " + strings[i]);
				lists.add(strings[i]);
//				System.out.println("list : " + strings[i]);
			}
			defaultTableModel.addRow(lists);
			lists = null;
		}
		bufferedReader.close();

		jTable = new JTable(defaultTableModel);
		jTable.setRowSorter(new TableRowSorter(defaultTableModel));
		// defaultTableModel.addRow(rowData2);
		// defaultTableModel.setValueAt("haha~", 0, 4);

		// ȥ��������
		// jTable.setShowVerticalLines(false);

		jScrollPane = new JScrollPane(jTable);
		jTable.setPreferredScrollableViewportSize(new Dimension(1200, 550));
		jTable.setFillsViewportHeight(true);
		setTableAllRowHeight(jTable, 24);
		setTableColumnCenter(jTable);
		jTable.setFont(new Font("Menu.font", Font.PLAIN, 15));
		setColumnSize(jTable, 0, 250, 312, 300);// filename
		setColumnSize(jTable, 1, 80, 120, 80);// ��С
		setColumnSize(jTable, 2, 80, 100, 80);// ״̬
		setColumnSize(jTable, 3, 80, 120, 80);// �ٶ�
		setColumnSize(jTable, 4, 100, 80, 100);// ����
		setColumnSize(jTable, 5, 100, 160, 100);// time
		setColumnSize(jTable, 6, 300, 320, 300);// λ��

		downPanel.add(jScrollPane);
	}

	private void string(char[] c, int i, int num) {
		// TODO Auto-generated method stub

	}

	public void updateDwonMessages(String fileName, Long fileSize, String fileSavePath, MutiDownloads mutiDownloads) {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// �������ڸ�ʽ

		// fileDownMessages.setFileName(fileName);
		Vector list = new Vector<>();
		list.add(fileName);
		list.add(fileSize + " KB");
		list.add("��������");
		list.add(" K/s");
		list.add(" ");
		list.add((String) df.format(new Date()));
		list.add(fileSavePath);
		defaultTableModel.addRow(list);

		// if (defaultTableModel.getRowCount() != 0) {
		rowNum = defaultTableModel.getRowCount() - 1;
		// System.out.println("--------------->" + rowNum);
		// }

		new Thread(() -> {
			// double progress = mutiDownloads.getProgress();
			DecimalFormat decimalFormat = new DecimalFormat("0.0");
			while (mutiDownloads.getProgress() < 100) {
				// progress = mutiDownloads.getProgress();
				// System.out.println("pro : " + mutiDownloads.getProgress());
				defaultTableModel.setValueAt(decimalFormat.format(mutiDownloads.getProgress()) + " %", rowNum, 4);
				defaultTableModel.setValueAt(mutiDownloads.getSpeed() + " KB/s", rowNum, 3);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			defaultTableModel.setValueAt(100 + " %", 0, 4);
		}).start();
	}

	public static void main(String[] args) throws IOException {
		MainUI mainUI = new MainUI(0);

	}

	// �����л�down��cloud�����¼�
	static class MyActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			if (button == cloudBtn) {
				cardLayout.first(panel);
			} else {
				cardLayout.last(panel);
			}
		}

	}

	// �����½�����İ�ť���¼�
	// static class AddHomeworkListener implements ActionListener {
	//
	// @Override
	// public void actionPerformed(ActionEvent e) {
	// JButton button = (JButton) e.getSource();
	// NewHomeworkWindow newHomeworkWindow = new NewHomeworkWindow();
	// }
	//
	// }

	// ������ͣ����İ�ť���¼�
	static class StopHomeworkListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			button.setEnabled(false);
		}

	}

	// ������ʼ����İ�ť���¼�
	static class RestartHomeworkListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
			button.setEnabled(false);
		}

	}

	// ����ɾ������İ�ť���¼�
	static class DeleteHomeworkListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			JButton button = (JButton) e.getSource();
		}

	}
	// ------------------------->

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton button = (JButton) e.getSource();
		NewHomeworkWindow newHomeworkWindow = new NewHomeworkWindow();

	}
}
