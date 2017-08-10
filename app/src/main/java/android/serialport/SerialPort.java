/*
 * Copyright 2009 Cedric Priscal
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */

package android.serialport;

import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SerialPort {

	private static final String TAG = "SerialPortNative";

	/*
	 * Do not remove or rename the field mFd: it is used by native method
	 * close();
	 */
	private int fdx = -1;
	private int writelen;
	private String str;
	private static SerialPort serialPort;


	/**
	 * 拿到操作"libserial_port.so"(操作串口)的类！
	 *
	 * @return
	 */
	public static SerialPort getInstance(){
		if(null == serialPort){
			synchronized (SerialPort.class) {
				if(null == serialPort){
					serialPort = new SerialPort();
				}
			}
		}
		return serialPort;
	}

	/*
	 * public SerialPort(String device, int baudrate) throws SecurityException,
	 * IOException {
	 * 
	 * fdx = openport(device, baudrate); if (fdx < 0) { Log.e(TAG,
	 * "native open returns null"); throw new IOException(); } }
	 */
	public SerialPort() {
		// openport_easy(dev,brd);
	}

	public void OpenSerial(String dev, int brd) throws SecurityException,
            IOException {
		// int result = 0;
		fdx = openport_easy(dev, brd);
		if (fdx < 0) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
	}


	/**
	 * 打开串口 -- 设置波特率之类的
	 *
	 * @param device					要打开的设备 -- 串口
	 * @param baudrate					波特率
	 * @param databit					数据位
	 * @param stopbit					停止位
	 * @param crc						校验(不知道是不是这个) == NONE:0; EVEN:1; ODD:2
	 *
	 * @throws SecurityException
	 * @throws IOException
	 */
	public void OpenSerial(String device, int baudrate, int databit, int stopbit, int crc)
			throws SecurityException, IOException {
		// private native int openport(String port,int brd,int bit,int stop,int crc);

		if(fdx < 0){
			closeport(fdx);			// 关闭之前的开的文件描述符
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("open");
		fdx = openport(device, baudrate, databit, stopbit, crc); // 操作.so 打开驱动文件 -- 拿到"文件描述符"

		if (fdx < 0) {
			Log.e(TAG, "native open returns null");
			throw new IOException();
		}
		Log.d(TAG, "open fd=" + fdx);
	}

	/*
	 * public int WriteSerialPort(byte[] data) { if(fdx < 0) { return -1; }
	 * return writeport(fdx, data); }
	 */

	/*
	 * public SerialPort() {
	 * 
	 * }
	 * 
	 * public int OpenSerialPort(String port) { if(fd >= 0) { close(fd); } fd =
	 * openport(port); if(fd < 0) { return -1; } return 0; }
	 *
	 * 获得文件描述符 -- 一开始初始化对应的串口的到的！！！
	 */
	public int getFd() {
		return fdx;
	}


	/**
	 * 通过一开始"初始化的串口--拿到文件描述符",发送数据
	 *
	 * @param fd		文件描述符
	 * @param str		要发送的数据
	 * @return
	 */
	public int WriteSerialByte(int fd, byte[] str) {
		writelen = writeport(fd, str);
		if (writelen >= 0) {
			Log.d(TAG, "write success");
		}
		return writelen;
	}

	public int WriteSerialString(int fd, String str, int len) {
		writelen = writestring(fd, str, len);
		return writelen;
	}


	/**
	 * 通过文件描述符 -- 读取串口的数据 -- 获取的是字节数组
	 *
	 * @param fd				文件描述符
	 * @param len				一次读多少个数据
	 *
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public byte[] ReadSerial(int fd, int len) throws UnsupportedEncodingException {
		byte[] tmp;
		tmp = readport(fd, len, 50);   // 参数三,间隔时间？延时时间？
		if (tmp == null) {
			return null;
		}

		/*
		 * for(byte x : tmp) {
		 * 		Log.w("xxxx", String.format("0x%x", x));
		 * 	}
		 */
		return tmp;
	}

	public String ReadSerialString(int fd, int len) throws UnsupportedEncodingException {

		byte[] tmp;
		tmp = readport(fd, len, 50);
		if (tmp == null) {
			return null;
		}

		if (isUTF8(tmp)) {
			str = new String(tmp, "utf8");
			Log.d(TAG, "is a utf8 string");
		} else {
			str = new String(tmp, "gbk");
			Log.d(TAG, "is a gbk string");
		}

		return str;
	}

	/**
	 * 关闭文件描述符
	 *
	 * @param fd	文件描述符
	 */
	public void CloseSerial(int fd) {
		closeport(fd);
		fdx = -1;
	}

	private boolean isUTF8(byte[] sx) {
		Log.d(TAG, "begian to set codeset");
		for (int i = 0; i < sx.length;) {
			if (sx[i] < 0) {
				if ((sx[i] >>> 5) == 0x7FFFFFE) {
					if (((i + 1) < sx.length)
							&& ((sx[i + 1] >>> 6) == 0x3FFFFFE)) {
						i = i + 2;
					} else {
						return false;
					}
				} else if ((sx[i] >>> 4) == 0xFFFFFFE) {
					if (((i + 2) < sx.length)
							&& ((sx[i + 1] >>> 6) == 0x3FFFFFE)
							&& ((sx[i + 2] >>> 6) == 0x3FFFFFE)) {
						i = i + 3;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				i++;
			}
		}
		return true;
	}

	// JNI
	private native int openport_easy(String port, int brd);


	/**
	 * 校验(不知道是不是这个) == NONE:0; EVEN:1; ODD:2
	 */
	private native int openport(String port, int brd, int bit, int stop, int crc);

	private native void closeport(int fd);

	private native byte[] readport(int fd, int count, int delay);

	private native int writeport(int fd, byte[] buf);

	public native static int writestring(int fd, String wb, int len);

	static {
		System.loadLibrary("serial_port");
	}

}
