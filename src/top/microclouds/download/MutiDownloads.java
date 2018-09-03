package top.microclouds.download;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.microclos.uitl.FileDownMessages;

import top.microclouds.ui.MainUI;

public class MutiDownloads {

	private String fileurl;// url
	private static String fileSavePath;// �ļ��������������λ��
	private int threadNumber;// �����߳���Ŀ
	private DownThreads[] threads;// �߳���,�̶߳���
	private long fileSize;// �ļ���С,���ֽ�Ϊ��λ
	private String fileName;// �ļ�������

	public FileDownMessages fileDownMessages;

	// public int numSize = 0;

	public MutiDownloads(String fileurl, String fileSavePath, int threadNumber, String fileName, Long fileSize) {
		this.fileurl = fileurl;
		this.fileSavePath = fileSavePath;
		this.threadNumber = threadNumber;
		this.fileName = fileName;
		this.fileSize = fileSize;
		threads = new DownThreads[threadNumber];// ��ʼ���߳�����
	}

	public void download(FileDownMessages fileDownMessages) throws IOException {
		URL url = new URL(fileurl);// url connection
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5 * 1000);// ��ʱ5������
		connection.setRequestMethod("GET");
		connection.setRequestProperty("Accept",
				"image/gif,image/jpeg,image/pjpeg,image/pjpeg," + "application/x-shockwave-flash,application/xaml+xml,"
						+ "application/vnd.ms-xpsdocument,application/x-ms-xbap"
						+ "application/x-ms-application,application/vnd.ms-excel,"
						+ "application/vnd.ms-powerpoint,application/msword,*/*");
		connection.setRequestProperty("Accept-Language", "zh-CN");
		connection.setRequestProperty("Charset", "UTF-8");
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("user-apent", "Mozilla/4.0 (compatible;MISE 6.0; Windows NT 5.1;SV1)");
		connection.connect();

		// ��ȡ��Ӧ��
		int urlcode = connection.getResponseCode();
		System.out.println("code is : " + urlcode);

		// ��ȡ�ļ�����
		// String fileNameString = connection.getHeaderField("Content-Disposition");
		// if (!("".equals(fileNameString))) {
		// fileName = fileNameString.substring(21);
		// System.out.println("filenamestring is : " + fileName);
		// } else {
		// if (fileurl.indexOf("/") != -1) {
		// this.fileName = fileurl.substring(fileurl.lastIndexOf("/") + 1,
		// fileurl.length());
		// System.out.println("substring filename is : " + fileName);
		// } else {
		// this.fileName = "myfile";
		// }
		// }
		// System.out.println("getheader : " + fileNameString);

		// ��ȡ�ļ���С
		fileSize = connection.getContentLengthLong();
		connection.disconnect();
		System.out.println("�ļ���СΪ : " + fileSize + " bytes , ��Ϊ  " + threadNumber + " �������� .");

		// �������ǰ�߳���Ҫ���صĿ�Ĵ�С
		int currentPartSize = (int) (fileSize / threadNumber + 1);
		RandomAccessFile file = new RandomAccessFile(fileSavePath + "\\" + fileName, "rw");

		// ���ñ����ļ��Ĵ�С
		// file.setLength(fileSize);
		file.close();

		boolean status = false;
		// �����߳���Ŀ,��ʼ���߳�����,�����߳�
		for (int i = 0; i < threadNumber; i++) {

			// ���ñ��������ļ�����Ϣ,���ڱ�־�ϵ�,ÿ���߳�һ���ļ�
			File configFile = new File(fileSavePath + "\\" + "part" + i + "_" + fileName + ".txt");

			// System.out.println("file configfilename : " + fileSavePath + "\\" + "part" +
			// i + "_" + fileName);
			int startPos = i * currentPartSize;// ��i���̶߳�ȡ�ļ��Ŀ�ʼָ��λ��
			// ÿ���߳�Ҫ���صĲ���,��randomaccessfile��ʾ
			RandomAccessFile currentFilePart = new RandomAccessFile(fileSavePath + "\\" + fileName, "rw");
			// �ļ�ָ�붨λ�����߳����ص�λ��
			currentFilePart.seek(startPos);
			// ���������߳�
			if (i == threadNumber - 1) {
				status = true;// �����������һ��
			}
			threads[i] = new DownThreads(startPos, currentPartSize, currentFilePart, fileurl, configFile, status,
					fileDownMessages);
			threads[i].start();
		}

	}

	public double getProgress() {

		int sumSize = 0;
		for (int i = 0; i < threadNumber; i++) {
			sumSize = sumSize + threads[i].downLengh;
		}
		System.out.println("getpro sumsize : " + sumSize + "     filelen : " + fileSize);
		System.out.println("result : " + (sumSize * 1.0 / fileSize) * 100);
		return (sumSize * 1.0 / fileSize) * 100;
	}

	public int getSpeed() {
		int sumSize = 0;
		for (int i = 0; i < threadNumber; i++) {
			sumSize = sumSize + threads[i].downLengh;
		}
		return sumSize / 1024;
	}
}

class DownThreads extends Thread {

	private int startPos;// ��ǰ�߳����صĿ�ʼλ��
	// private int endPos;// ��ǰ�߳����صĿ�ʼλ��
	private int currentPartSize;// ��ǰ�߳����ؿ�Ĵ�С
	private RandomAccessFile currentFilePart;// ��ǰ�߳����ؿ�
	public int downLengh;// ��ǰ�߳��Ѿ����صĴ�С
	public String fileurl;// ��Դ��url
	public File configFile;// �ϵ������ļ���λ��
	public boolean status;

	public FileDownMessages fileDownMessages;

	public DownThreads(int startPos, int currentPartSize, RandomAccessFile currentFilePart, String fileurl,
			File configFile, boolean status, FileDownMessages fileDownMessages) {
		this.startPos = startPos;
		this.currentPartSize = currentPartSize;
		this.currentFilePart = currentFilePart;
		this.fileurl = fileurl;
		this.configFile = configFile;
		this.status = status;
		this.fileDownMessages = fileDownMessages;
		// endPos = this.startPos + this.currentPartSize;
	}

	@Override
	public void run() {
		try {
			URL url = new URL(fileurl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5 * 1000);// ��ʱ5������
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept",
					"image/gif,image/jpeg,image/pjpeg,image/pjpeg,"
							+ "application/x-shockwave-flash,application/xaml+xml,"
							+ "application/vnd.ms-xpsdocument,application/x-ms-xbap"
							+ "application/x-ms-application,application/vnd.ms-excel,"
							+ "application/vnd.ms-powerpoint,application/msword,*/*");
			connection.setRequestProperty("Accept-Language", "zh-CN");
			connection.setRequestProperty("Charset", "UTF-8");
			connection.setRequestProperty("user-apent", "Mozilla/4.0 (compatible;MISE 6.0; Windows NT 5.1;SV1)");

			// �ж�����߳����ص��ļ��Ƿ�ϵ��
			if (configFile.exists()) {
				RandomAccessFile randomAccessFile = new RandomAccessFile(configFile, "rw");
				randomAccessFile.seek(0);
				startPos = randomAccessFile.readInt();
				currentPartSize = currentPartSize - startPos;
				currentFilePart.seek(startPos);
				System.out.println("config�ļ�����,���ڶϵ� ,��ǰ�ϵ�λ��Ϊ:  " + startPos);
				randomAccessFile.close();
				// configFile.delete();
			}
			// connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);

			// д���ļ�ϵͳ
			InputStream inputStream = connection.getInputStream();
			inputStream.skip(this.startPos);
			// ����������,��СΪ1024�ֽ�
			byte[] byteBuffer = new byte[1024 * 10];
			// ÿ��inputstream��ȡ�����ֽ���,����һ��int
			int readNum = 0;
			// ��ȡ�����ļ�����,д�뱾���ļ�

			RandomAccessFile randomAccessFile = new RandomAccessFile(configFile, "rw");
			System.out.println(this.getName() + "�ļ�������. . . " + downLengh + "  " + startPos + "  " + "part size : "
					+ currentPartSize);
			while (downLengh < currentPartSize && (readNum = inputStream.read(byteBuffer)) != -1) {
				// д���ļ�
				currentFilePart.write(byteBuffer, 0, readNum);
				// �ۼ��Ѿ����ص����ֽ���
				downLengh = downLengh + readNum;
				// д��config�ļ�,��¼�ļ�ָ��λ��,�Է��ϵ�
				randomAccessFile.seek(0);
				randomAccessFile.writeInt(startPos + downLengh);
				randomAccessFile.seek(0);
				// System.out.println("�߳��Ѿ�������ֽ� : " + randomAccessFile.readInt());
			}
			System.out.println(this.getName() + " ������ļ��������! һ�� : " + downLengh);
			System.out.println("�߳��Ѿ�������ֽ� : " + randomAccessFile.readInt());
			randomAccessFile.close();
			currentFilePart.close();
			inputStream.close();
			configFile.delete();// ��ǰ�߳�������� ,ɾ�������ļ�

			// ȫ���������
			if (status) {
				FileWriter saveMessages = new FileWriter("I:\\JavaSE�������\\Microclouds\\messages\\config.txt", true);
				BufferedWriter out = new BufferedWriter(saveMessages);
				String messages = fileDownMessages.getFileName() + "*" + fileDownMessages.getFileSize() + "*" + "�������"
						+ "*" + " - " + "*" + "100 %" + "*" + fileDownMessages.getDownStartTime() + "*"
						+ fileDownMessages.getFileSavePath();
				out.write(messages);
				out.newLine();
				out.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
