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
	private static String fileSavePath;// 文件下载下来保存的位置
	private int threadNumber;// 启用线程数目
	private DownThreads[] threads;// 线程类,线程对象
	private long fileSize;// 文件大小,以字节为单位
	private String fileName;// 文件的名字

	public FileDownMessages fileDownMessages;

	// public int numSize = 0;

	public MutiDownloads(String fileurl, String fileSavePath, int threadNumber, String fileName, Long fileSize) {
		this.fileurl = fileurl;
		this.fileSavePath = fileSavePath;
		this.threadNumber = threadNumber;
		this.fileName = fileName;
		this.fileSize = fileSize;
		threads = new DownThreads[threadNumber];// 初始化线程数组
	}

	public void download(FileDownMessages fileDownMessages) throws IOException {
		URL url = new URL(fileurl);// url connection
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5 * 1000);// 超时5秒重连
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

		// 获取响应吗
		int urlcode = connection.getResponseCode();
		System.out.println("code is : " + urlcode);

		// 获取文件名字
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

		// 获取文件大小
		fileSize = connection.getContentLengthLong();
		connection.disconnect();
		System.out.println("文件大小为 : " + fileSize + " bytes , 分为  " + threadNumber + " 部分下载 .");

		// 计算出当前线程需要下载的块的大小
		int currentPartSize = (int) (fileSize / threadNumber + 1);
		RandomAccessFile file = new RandomAccessFile(fileSavePath + "\\" + fileName, "rw");

		// 设置本地文件的大小
		// file.setLength(fileSize);
		file.close();

		boolean status = false;
		// 根据线程数目,初始化线程数组,创建线程
		for (int i = 0; i < threadNumber; i++) {

			// 设置本地配置文件的信息,用于标志断点,每个线程一个文件
			File configFile = new File(fileSavePath + "\\" + "part" + i + "_" + fileName + ".txt");

			// System.out.println("file configfilename : " + fileSavePath + "\\" + "part" +
			// i + "_" + fileName);
			int startPos = i * currentPartSize;// 第i个线程读取文件的开始指针位置
			// 每个线程要下载的部分,用randomaccessfile表示
			RandomAccessFile currentFilePart = new RandomAccessFile(fileSavePath + "\\" + fileName, "rw");
			// 文件指针定位到该线程下载的位置
			currentFilePart.seek(startPos);
			// 创建下载线程
			if (i == threadNumber - 1) {
				status = true;// 表明这是最后一块
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

	private int startPos;// 当前线程下载的开始位置
	// private int endPos;// 当前线程下载的开始位置
	private int currentPartSize;// 当前线程下载块的大小
	private RandomAccessFile currentFilePart;// 当前线程下载块
	public int downLengh;// 当前线程已经下载的大小
	public String fileurl;// 资源的url
	public File configFile;// 断点配置文件的位置
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
			connection.setConnectTimeout(5 * 1000);// 超时5秒重连
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

			// 判断这个线程下载的文件是否断点过
			if (configFile.exists()) {
				RandomAccessFile randomAccessFile = new RandomAccessFile(configFile, "rw");
				randomAccessFile.seek(0);
				startPos = randomAccessFile.readInt();
				currentPartSize = currentPartSize - startPos;
				currentFilePart.seek(startPos);
				System.out.println("config文件存在,存在断点 ,当前断点位置为:  " + startPos);
				randomAccessFile.close();
				// configFile.delete();
			}
			// connection.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);

			// 写入文件系统
			InputStream inputStream = connection.getInputStream();
			inputStream.skip(this.startPos);
			// 设立缓冲区,大小为1024字节
			byte[] byteBuffer = new byte[1024 * 10];
			// 每次inputstream读取到的字节数,返回一个int
			int readNum = 0;
			// 读取网络文件数据,写入本地文件

			RandomAccessFile randomAccessFile = new RandomAccessFile(configFile, "rw");
			System.out.println(this.getName() + "文件下载中. . . " + downLengh + "  " + startPos + "  " + "part size : "
					+ currentPartSize);
			while (downLengh < currentPartSize && (readNum = inputStream.read(byteBuffer)) != -1) {
				// 写入文件
				currentFilePart.write(byteBuffer, 0, readNum);
				// 累计已经下载的总字节数
				downLengh = downLengh + readNum;
				// 写入config文件,记录文件指针位置,以防断点
				randomAccessFile.seek(0);
				randomAccessFile.writeInt(startPos + downLengh);
				randomAccessFile.seek(0);
				// System.out.println("线程已经保存的字节 : " + randomAccessFile.readInt());
			}
			System.out.println(this.getName() + " 负责的文件下载完成! 一共 : " + downLengh);
			System.out.println("线程已经保存的字节 : " + randomAccessFile.readInt());
			randomAccessFile.close();
			currentFilePart.close();
			inputStream.close();
			configFile.delete();// 当前线程下载完成 ,删除配置文件

			// 全部下载完成
			if (status) {
				FileWriter saveMessages = new FileWriter("I:\\JavaSE程序设计\\Microclouds\\messages\\config.txt", true);
				BufferedWriter out = new BufferedWriter(saveMessages);
				String messages = fileDownMessages.getFileName() + "*" + fileDownMessages.getFileSize() + "*" + "下载完成"
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
