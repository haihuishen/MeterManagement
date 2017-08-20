package android.serialport;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.zh.metermanagement.utils.LogUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 抄表控制 -- 需要为"红外？Rfid?"上电
 */
public class MeterController {
	public final static String Tag = "MeterControll";
	private final int Pro_Idle = 0;
	private final int Pro_One = 1;
	private final int Pro_Two = 2;

	private static MeterController controll;

	/** 回写--子类实现(将数据暴露给子类) */
	private MeterCallBack callBack;


	//	public String power_off_write = "-wdout106 0";					// 捷宝 -- 红外模块 -- 关
	//	public String power_on_write = "-wdout106 1";					// 捷宝 -- 红外模块 -- 开
	public String power_off_write = "0";								// 深圳蓝畅 -- 红外模块 -- 关
	public String power_on_write = "1";									// 深圳蓝畅 -- 红外模块 -- 开
	//	public String actual_path = "/sys/class/misc/mtgpio/pin";		// 捷宝 -- 红外模块的地址
	public String actual_path = "/proc/gpiocontrol/set_id";				// 深圳蓝畅 -- 红外模块的地址
	// public String RFID_POWER_PATH = "/proc/gpiocontrol/set_id";		// 深圳蓝畅 -- 红外模块的地址



	private ReadThread mReadThread;
	private int currentPro = Pro_Idle;
	private boolean isMeter = true;

	/** 接收串口数据的线程是否开启 -- ture:开启了 false:关闭了 */
	private boolean begin;

	/** 控制串口读取线程状态 ture:接受数据 false:停止接受数据 */
	private boolean isReceive = false;

	private byte[] readAdress_97 = new byte[] { 0x68, (byte) 0xAA, (byte) 0xAA,
			(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, 0x68, 0x01,
			0x02, 0x65, (byte) 0xF3, 0x27, 0x16 };

	private byte[] readAdress_07 = new byte[] { 0x68, (byte) 0xAA, (byte) 0xAA,
			(byte) 0xAA, (byte) 0xAA, (byte) 0xAA, (byte) 0xAA, 0x68, 0x01,
			0x02, 0x65, (byte) 0xF3, 0x27, 0x16 };

	/** 数据标识 -- 水电表要用到的 */
	private String bz = "";

	public static int Action_Idle = -1;
	public static final int Action_Read_Adress_97 = 3;
	public static final int Action_Read_Adress_07 = 4;
	public static final int Action_ChaoBiao = 10;
	public static final int Action_Meter_Read = 5;

	/** 缓存数据 */
	private byte[] serialPortData_buffer = null;
	private byte[] sendBuffer;
	private byte[] one = null;
	private DeviceControl DevCtrl;
	private Context mContext;
	private SerialPort mSerialPort;
	private boolean isOpen = false;

	public static MeterController getInstance() {
		if (null == controll) {
			synchronized (MeterController.class) {
				if (null == controll) {
					controll = new MeterController();
				}
			}
		}
		return controll;
	}

	/**
	 * 回写--子类实现(将数据暴露给子类)
	 */
	public interface MeterCallBack {

		/**
		 * 红外
		 * @param buffer
		 * @param size
		 */
		public void Meter_Read(byte[] buffer, int size);

		/**
		 * 红外抄表结果返回
		 * @param result
		 */
		public void Meter_ChaoBiao(String result);

		/**
		 * 红外抄表结果返回
		 * @param result
		 */
		public void Meter_Adress(String result);

	}


	/**
	 * 打开--设备(本程序应该红外模块) -- 还要打开串口(同时设置了波特率)
	 *
	 * @param callBack   回写类--子类实现传过来 -- 将数据暴露给子类
	 * @param mContext
	 */
	public void Meter_Open(MeterCallBack callBack, Context mContext) {
		this.callBack = callBack;
		this.mContext = mContext;


		//Log.i("shen", "isOpen:" + isOpen);
		if(isOpen){
			Meter_Close();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		power_up();				// 为红外模块上电

		openSerialPort("/dev/ttyMT2", 1200, 8, 1, 1);		// 打开串口
		isOpen = true;
	}


	/**
	 * 关闭"红外模块"--对应的窗口也关掉
	 */
	public void Meter_Close() {
		closeSerialPort();
		//power_down();
		isOpen = false;
	}


	/**
	 * 关闭串口(对应的接收数据线程也关闭)
	 */
	public void closeSerialPort() {
		notifyReader();							// 如果"线程不为空"且"线程还在执行" -- 就中断线程
		isReceive = false;
		begin = false;

		if (mReadThread != null) {
			mReadThread = null;
		}

		if (mSerialPort != null) {
			int fd = mSerialPort.getFd();		// 获得文件描述符 (串口对应的)
			mSerialPort.CloseSerial(fd);		// 关闭文件描述符
		}
	}


	/**
	 * 打开串口 -- 开始接收数据的线程
	 *
	 * @param serial_path		要打开的"串口"(串口几？)
	 * @param baudrate			波特率
	 * @param databit			数据位
	 * @param stopbit			停止位
	 * @param crc				校验
	 */
	public void openSerialPort(String serial_path, int baudrate, int databit,int stopbit, int crc) {
		try {
			Log.d(Tag,"open_port:" + serial_path);

			if (mSerialPort != null) {
				if (mReadThread != null) {
					mReadThread.interrupt();			// 停止线程
				}
				closeSerialPort();						// 关闭串口(对应的接收数据线程也关闭)
			}
			mSerialPort = SerialPort.getInstance();			// 拿到操作"libserial_port.so"(操作串口)的类！

			mSerialPort.OpenSerial(serial_path, baudrate, 8, stopbit, crc);   // 打开串口

			begin = true;
			isReceive = true;
			mReadThread = new ReadThread();					// 开启接收数据的线程
			mReadThread.start();

		} catch (SecurityException e) {
			DisplayToast("open " + serial_path + " by  " + baudrate + " baurate failed");
			e.printStackTrace();

		} catch (IOException e) {
			DisplayToast("open " + serial_path + " by  " + baudrate + " baurate failed");
			e.printStackTrace();
		}
	}


	/**
	 * 为红外模块上电
	 */
	private void power_up() {
		try {
			DevCtrl = new DeviceControl(actual_path, mContext);    // 操作设备文件的类！

			DevCtrl.PowerOnDevice(power_on_write);					// 打开红外模块--上电

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}


	/**
	 * 关掉"模块"(宏红外模块) -- 去电
	 */
	private void power_down() {

		if (actual_path.equals("MT GPIO")) {
			Log.d(Tag,"MT GPIO off");
			DevCtrl.MTGpioOff();

		} else {

			try {
				DevCtrl.PowerOffDevice(power_off_write);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				LogUtils.i("e.getMessage()" + e.getMessage());
			}
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * 接收串口的数据的线程
	 */
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();

			while (!isInterrupted()) {     // true-->中断  --   ！isInterrupted()没有中断
				if (null == mSerialPort)
					return;

				int fd = mSerialPort.getFd();
				// Log.d(TAG, "read fd=" + fd);
				byte[] buffer;
				try {
					if (null == mSerialPort)
						return;

					buffer = mSerialPort.ReadSerial(fd, 1024); // 通过文件描述符 -- 读取串口的数据 -- 获取的是字节数组

					if (buffer != null) {

						a: while (begin) {
							Log.d(Tag,"buffer:" + Tools.bytesToHexString(buffer));

							if (null == serialPortData_buffer) {
								serialPortData_buffer = buffer;
							} else {
								serialPortData_buffer = Tools.arrayCopy(serialPortData_buffer, buffer);
							}

							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
							}

							if (null == mSerialPort)
								return;

							buffer = mSerialPort.ReadSerial(fd, 1024);
							if (buffer == null) {
								break a;
							}
						}

						if (null != serialPortData_buffer) {
							if (isMeter) {
								Log.d(Tag,"isMeter");
								System.out.println(Tools.bytesToHexString(serialPortData_buffer));

								SerialPortData serialPortData = new SerialPortData(
										serialPortData_buffer,
										serialPortData_buffer.length);

								if (Action_Idle == Action_ChaoBiao) {
									intervalDoRead(serialPortData);

								} else if (Action_Idle == Action_Read_Adress_97) {
									getAdress_97(serialPortData);

								} else if (Action_Idle == Action_Read_Adress_07) {
									getAdress_07(serialPortData);

								} else if (Action_Idle == Action_Meter_Read) {		// 直接将数据给子类：让子类解析
									if (null != callBack) {
										callBack.Meter_Read(serialPortData_buffer, serialPortData_buffer.length);
									}
								}
							}
						}
						serialPortData_buffer = null;

					} else {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}


	/**
	 * 根据情况拼接
	 *
	 * @param serialPortData
	 */
	private void intervalDoRead(SerialPortData serialPortData) {
		if (serialPortData.getSize() > 0) {
			switch (currentPro) {
				case Pro_Idle:

					break;
				case Pro_One:
					one = serialPortData.getDataByte();
					dealData(serialPortData);
					currentPro = Pro_Two;
					break;

				case Pro_Two:
					if (one != null) {
						byte[] temp = new byte[one.length + serialPortData.getSize()];
						System.arraycopy(one, 0, temp, 0, one.length);
						System.arraycopy(serialPortData.getDataByte(), 0, temp, one.length, serialPortData.getSize());

						byte[] availableData = new byte[temp.length - sendBuffer.length];
						System.arraycopy(temp, sendBuffer.length, availableData, 0, availableData.length);
						Log.d(Tag,"avaliable_data:" + Tools.bytesToHexString(availableData));

						SerialPortData data = new SerialPortData(availableData, availableData.length);

						dealData(data);
					}
					currentPro = Pro_Idle;

					break;
				default:
					break;
			}

		}
	}


	/**
	 * 根据对应的数据标识 -- 截取数据
	 *
	 * @param bytes
	 * @return
	 */
	public String getMsg(byte[] bytes) {

		// //接收数据帧
		String buf = "";
		try {
			byte[] recvBuffer = bytes;
			byte[] recvBuffer2 = new byte[256];// 2014/7.17

			int x = 0;
			int i = 0;

			String bzString = bz.toString().trim();

			// 2014/7/17判断接收帧中 第一个 68 的位置
			for (i = 0; i < recvBuffer.length; i++) {


				//region 定位"0x68"
				if (recvBuffer[i] == (byte) 0x68) {				// (前面的FE去掉)0x68
					x = i;										// 拿到"0x68"在"byte[]"中的位置

					// --68 + 6字节地址 + 68 + 1字节控制码-------
					if ((x + 8) < recvBuffer.length) {

						// 判断控制码
						// 97协议为 0x81
						// 07协议为 0x91

						if (recvBuffer[x + 8] == (byte) 0x81 || recvBuffer[x + 8] == (byte) 0x91) {

							Log.d("info", "接收到了 电表数据！");
							// 数据域长度：有数据时长度为 2+4；无数据时长度为：2
							// 97协议： 2 + 4 = 6
							// 07协议： 4 + 4 = 8

							// --68 + 6字节地址 + 68 + 1字节控制码 + 1字节数据域长度--------
							if ((x + 9) < recvBuffer.length) {
								int dataL = recvBuffer[x + 9];							// 数据域长度

								// --68 + 6字节地址 + 68 + 1字节控制码 + 1字节数据域长度 + n字节数据域--------
								if (x + 11 + dataL < recvBuffer.length) {

									// --68H + 6字节地址 + 68H + 1字节控制码 + 1字节数据域长度 + n字节数据域 + 1字节校验 + 16H--------
									if (recvBuffer[x + 11 + dataL] != (byte) 0x16) {
										Log.d("info", "---5----错误：没有接收到数据帧！");
										continue;
									}

									// 数据标识的长度  -- 97:2字节   07:4字节
									int iData = 0;
									if (recvBuffer[x + 8] == (byte) 0x81) {
										iData = 2;
									} else if (recvBuffer[x + 8] == (byte) 0x91) {
										iData = 4;
									} else {

										Log.d("info", "---6----错误：没有接收到数据帧！");
										continue;
									}

									// 保存"数据域"中对电表数据 -- "数据域" = "数据标识" + "对应的数据"
									byte[] dClearData = new byte[dataL - iData];
									Log.d("info", "对比数组长度 == " + dClearData.length);

									// 数据解析
									int mm = 0;
									// 数据域中的数据(电表返回数据会将其 + 0x33)
									for (mm = x + 10 + iData, i = 0; mm <= x + 9 + dataL; mm++, i++) {
										dClearData[i] = (byte) (recvBuffer[mm] - 0x33);
									}

									for (int j = dClearData.length - 1; j >= 0; j--) {
										// 将字节以16进制的形式弄成字符串 -- 然后接在一起
										buf += Tools.byteToString(dClearData[j]);
									}


									if (bz.length() == 4) {							// 97规约
										if (bz.substring(0, 1).equals("9")) {		// 9010 总正向有功电度
											try {
												buf = buf.substring(0, 6) + "." + buf.substring(6, 8);
												float f = Float.valueOf(buf);
												String bufer = String.valueOf(f);
												return bufer;
											} catch (NumberFormatException e) {
												return null;
											}

										} else if (bz.endsWith("C010") || bz.endsWith("c010")) {
											buf = buf.substring(0, 2) + "-"
													+ buf.substring(2, 4) + "-"
													+ buf.substring(4, 6);
											return buf;

										} else if (bz.substring(0, 1).endsWith("A")
												|| bz.substring(0, 1).endsWith("a")) {

											buf = buf.subSequence(0, 2) + "." + buf.subSequence(2, 6);
											float f = Float.valueOf(buf);
											String bufer = String.valueOf(f);
											return bufer;

										} else if (bz.equals("C011") || bz.equals("c011")) {
											buf = buf.substring(0, 2) + ":"
													+ buf.subSequence(2, 4)
													+ ":" + buf.substring(4, 6);
											return buf;

										} else if (bz.substring(0, 2).equals("c1")
												|| bz.substring(0, 2).equals("C1")) {
											buf = buf.substring(0, 2);
											return buf;
										}
									} else if (bzString.length() == 8) {

										if (bzString.substring(0, 2).equals("01")) {
											Log.d("info", "buf === " + buf);
											String str = buf.substring(10, 12)
													+ "."
													+ buf.substring(12,
													buf.length());

											float f = Float.valueOf(str);
											String bufer = String.valueOf(f)
													+ "\n" + "生成时间:"
													+ buf.substring(0, 2) + "-"
													+ buf.substring(2, 4) + "-"
													+ buf.substring(4, 6);
											return bufer;
										} else if (bzString.equals("04000101")) {
											buf = buf.substring(0, 2) + "-"
													+ buf.substring(2, 4) + "-"
													+ buf.substring(4, 6);
											return buf;
										} else if (bzString.equals("04000102")) {
											buf = buf.substring(0, 2) + ":"
													+ buf.subSequence(2, 4)
													+ ":" + buf.substring(4, 6);
											return buf;
										} else if (bzString.equals("04000103") || bzString.equals("04000104")) {
											buf = buf.substring(0, 2);
											return buf;
										} else if (bzString.substring(0, 2).equals("00")) {
											Log.d("info", "00kai");
											buf = buf.substring(0, 6) + "." + buf.substring(6, 8);
											float f = Float.valueOf(buf);
											String bufer = String.valueOf(f);
											return bufer;
										}
									}
									Log.d("info", "buf ---- " + buf);
									try {
										Thread.sleep(100);
									} catch (InterruptedException e) {
										// TODO 自动生成的 catch 块
										e.printStackTrace();
									}
								}
							} else {
								continue;
							}

						} else {
							Log.d("info", "---3----错误：没有接收到数据帧！");

							continue;
						}
					} else {
						continue;
					}
					// break;
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return buf.toString();
	}

	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	public static String bytesToHexString1234(byte src) {
		StringBuilder stringBuilder = new StringBuilder("");
		int v = src & 0xFF;
		String hv = Integer.toHexString(v);
		if (hv.length() < 2) {
			stringBuilder.append(0);
		}
		stringBuilder.append(hv);
		return stringBuilder.toString();
	}

	/**
	 * 获取表地址
	 *
	 * @param is97 是否是97表
	 */
	public void sendMscAdresstoDianbiao(boolean is97) {
		isMeter = true;
		if (is97) {
			Action_Idle = Action_Read_Adress_97;
			writeCommand(readAdress_97);
		} else {
			Action_Idle = Action_Read_Adress_07;
			writeCommand(readAdress_07);
		}
		// byte[] buffer = new byte[] { (byte) 0x68, (byte) 0xaa,
		// (byte) 0xaa, (byte) 0xaa, (byte) 0xaa, (byte) 0xaa,
		// (byte) 0xaa, (byte) 0x68, (byte) 0x01, (byte) 0x02,
		// (byte) 0x65, (byte) 0xf3, (byte) 0x27, (byte) 0x16,
		// (byte) 0x68, (byte) 0x63, (byte) 0x04, (byte) 0x11,
		// (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x68,
		// (byte) 0x81, (byte) 0x08, (byte) 0x65, (byte) 0xf3,
		// (byte) 0x96, (byte) 0x37, (byte) 0x44, (byte) 0x36,
		// (byte) 0x33, (byte) 0x33, (byte) 0xd9, (byte) 0x16 };
		// SerialPortData data = new SerialPortData(buffer,
		// buffer.length);
		// getAdress(data);
	}

	/**
	 * 抄表
	 *
	 * @param bdz 表地址
	 * @param bz 表
	 */
	public void sendMscToDianbiao(String bdz, String bz) {
		isMeter = true;
		Action_Idle = Action_ChaoBiao;

		int sendL = 0;
		int i = 0;

		String StrCortrol = "";
		String StrLength = "";
		this.bz = bz.trim();
		int bzLength = bz.length();
		if (bzLength == 4) // 97协议
		{
			sendL = 18;
			StrCortrol = "01";
			StrLength = "02";
		} else if (bzLength == 8) // 07协议
		{
			sendL = 20;
			StrCortrol = "11";
			StrLength = "04";
		} else {
			return;
		}
		sendBuffer = new byte[sendL];
		if (bdz.length() < 12) {
			bdz = (Tools.AddZero(12 - bdz.length()) + bdz).trim();
		} else if (bdz.length() > 12) {

		}
		sendBuffer[0] = (byte) 0xFE;
		sendBuffer[1] = (byte) 0xFE;
		sendBuffer[2] = (byte) 0xFE;
		sendBuffer[3] = (byte) 0xFE;
		sendBuffer[4] = 0x68;

		// 表地址
		sendBuffer[5] = Tools.hexString2Bytes(bdz.substring(10, 12))[0];
		sendBuffer[6] = Tools.hexString2Bytes(bdz.substring(8, 10))[0];
		sendBuffer[7] = Tools.hexString2Bytes(bdz.substring(6, 8))[0];
		sendBuffer[8] = Tools.hexString2Bytes(bdz.substring(4, 6))[0];
		sendBuffer[9] = Tools.hexString2Bytes(bdz.substring(2, 4))[0];
		sendBuffer[10] = Tools.hexString2Bytes(bdz.substring(0, 2))[0];
		Log.d("info", "表地址 == " + Tools.bytesToHexString1234(sendBuffer[5]) + "   "
						+ Tools.bytesToHexString1234(sendBuffer[6]) + "  "
						+ Tools.bytesToHexString1234(sendBuffer[7]) + "  "
						+ Tools.bytesToHexString1234(sendBuffer[8]) + "  "
						+ Tools.bytesToHexString1234(sendBuffer[9]) + "  "
						+ Tools.bytesToHexString1234(sendBuffer[10]));

		sendBuffer[11] = 0x68;

		// 控制码
		sendBuffer[12] = Tools.hexString2Bytes(StrCortrol)[0];

		// //数据域长度
		sendBuffer[13] = Tools.hexString2Bytes(StrLength)[0];

		for (i = 1; i <= Integer.parseInt(StrLength.substring(1, 2)); i++) {
			int j = 2 * (Integer.parseInt(StrLength.substring(1, 2)) - i);
			sendBuffer[13 + i] = (byte) (Tools.hexString2Bytes(bz.substring(j,
					j + 2))[0] + 0x33);
		}

		// 校验位
		int sumMod = 0;
		for (i = 4; i <= sendL - 3; i++) {
			sumMod += sendBuffer[i];
		}
		sendBuffer[sendL - 2] = (byte) (sumMod % 256);

		// 结束符
		sendBuffer[sendL - 1] = 0x16;
		// 发送数据 设置延迟10毫秒
		// try {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		Log.d("info", "sendBuffer === " + Tools.bytesToHexString(sendBuffer));
		setCurrentPro(Pro_One);
		if ((sendBuffer.length > 0)) {
			isReceive = true;
			writeCommand(sendBuffer);
		}
		// mOutputStream.write(sendBuffer, 0, sendBuffer.length);
		// mOutputStream.flush();
		// Thread.sleep(800);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
	}


	/**
	 * 解析的数据 -- 通过"回写"暴露给子类
	 * @param serialPortData
	 */
	private void dealData(SerialPortData serialPortData) {

		byte[] b = serialPortData.getDataByte();
		Log.d("info", "serialPortData == " + bytesToHexString(b));
		Log.v("onReadSerialPortData", "bytes=" + bytesToHexString(b));

		String result = getMsg(b).trim();
		if (null != callBack) {
			callBack.Meter_ChaoBiao(result);
		}
	}

	private void setCurrentPro(int pro) {
		this.currentPro = pro;
	}


	/**
	 * 存放串口接收到的数据！
	 */
	public class SerialPortData {
		private byte[] dataByte;
		private int size;

		public SerialPortData(byte[] _dataByte, int _size) {
			this.setDataByte(_dataByte);
			this.setSize(_size);
		}

		public int getSize() {
			return size;
		}

		public void setSize(int size) {
			this.size = size;
		}

		public byte[] getDataByte() {
			return dataByte;
		}

		public void setDataByte(byte[] dataByte) {
			this.dataByte = dataByte;
		}
	}




	private void getAdress_97(SerialPortData serialPortData) {
		// 68 AA AA AA AA AA AA 68 81 08 65 F3 DD DD DD DD DD DD 27 16
		boolean isStart = false;
		byte[] buffer = serialPortData.getDataByte();
		byte[] adress = new byte[6];
		StringBuffer Adress = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			if ((i + 7 < buffer.length) && buffer[i] == 0x68) {
				i = i + 7;
				isStart = true;
				Log.d(Tag,"has Adress return");
			}

			if (isStart) {
				if ((i + 12 < buffer.length) && buffer[i] == 0x68
						&& buffer[i + 1] == (byte) 0x81
						&& buffer[i + 2] == (byte) 0x08
						&& buffer[i + 3] == (byte) 0x65
						&& buffer[i + 4] == (byte) 0xF3) {
					adress[0] = buffer[i + 10];
					adress[1] = buffer[i + 9];
					adress[2] = buffer[i + 8];
					adress[3] = buffer[i + 7];
					adress[4] = buffer[i + 6];
					adress[5] = buffer[i + 5];
					Log.d(Tag,"adress[]:"
							+ Tools.bytesToHexString1234(adress[0])
							+ Tools.bytesToHexString1234(adress[1])
							+ Tools.bytesToHexString1234(adress[2])
							+ Tools.bytesToHexString1234(adress[3])
							+ Tools.bytesToHexString1234(adress[4])
							+ Tools.bytesToHexString1234(adress[5]));

					adress[0] = (byte) (adress[0] - 0x33);
					adress[1] = (byte) (adress[1] - 0x33);
					adress[2] = (byte) (adress[2] - 0x33);
					adress[3] = (byte) (adress[3] - 0x33);
					adress[4] = (byte) (adress[4] - 0x33);
					adress[5] = (byte) (adress[5] - 0x33);
					Adress.append(Tools.bytesToHexString1234(adress[0]));
					Adress.append(Tools.bytesToHexString1234(adress[1]));
					Adress.append(Tools.bytesToHexString1234(adress[2]));
					Adress.append(Tools.bytesToHexString1234(adress[3]));
					Adress.append(Tools.bytesToHexString1234(adress[4]));
					Adress.append(Tools.bytesToHexString1234(adress[5]));

					Log.d(Tag,"Adress:" + Adress);
					if (null != callBack) {
						callBack.Meter_Adress(Adress.toString());
					}
					break;
				}else {
					i = i - 7;
					isStart = false;
				}
			}
		}
	}

	private void getAdress_07(SerialPortData serialPortData) {
		// 68 91 0A 34 37 33 37 BC 46 33 33 43 A4 AC 16
		boolean isStart = false;
		byte[] buffer = serialPortData.getDataByte();
		byte[] adress = new byte[6];
		StringBuffer Adress = new StringBuffer();
		for (int i = 0; i < buffer.length; i++) {
			if ((i + 6 < buffer.length) && buffer[i] == 0x68
					&& buffer[i + 1] == (byte) 0x91
					&& buffer[i + 2] == (byte) 0x0A
					&& buffer[i + 3] == (byte) 0x34
					&& buffer[i + 4] == (byte) 0x37
					&& buffer[i + 5] == (byte) 0x33
					&& buffer[i + 6] == (byte) 0x37) {
				i = i + 6;
				isStart = true;
				Log.d(Tag,"has Adress return");
			}

			if (isStart) {
				if ((i + 8 < buffer.length)) {
					adress[0] = buffer[i + 6];
					adress[1] = buffer[i + 5];
					adress[2] = buffer[i + 4];
					adress[3] = buffer[i + 3];
					adress[4] = buffer[i + 2];
					adress[5] = buffer[i + 1];
					Log.d(Tag,"adress[]:"
							+ Tools.bytesToHexString1234(adress[0])
							+ Tools.bytesToHexString1234(adress[1])
							+ Tools.bytesToHexString1234(adress[2])
							+ Tools.bytesToHexString1234(adress[3])
							+ Tools.bytesToHexString1234(adress[4])
							+ Tools.bytesToHexString1234(adress[5]));

					adress[0] = (byte) (adress[0] - 0x33);
					adress[1] = (byte) (adress[1] - 0x33);
					adress[2] = (byte) (adress[2] - 0x33);
					adress[3] = (byte) (adress[3] - 0x33);
					adress[4] = (byte) (adress[4] - 0x33);
					adress[5] = (byte) (adress[5] - 0x33);
					Adress.append(Tools.bytesToHexString1234(adress[0]));
					Adress.append(Tools.bytesToHexString1234(adress[1]));
					Adress.append(Tools.bytesToHexString1234(adress[2]));
					Adress.append(Tools.bytesToHexString1234(adress[3]));
					Adress.append(Tools.bytesToHexString1234(adress[4]));
					Adress.append(Tools.bytesToHexString1234(adress[5]));

					Log.d(Tag,"Adress:" + Adress);
					if (null != callBack) {
						callBack.Meter_Adress(Adress.toString());
					}
					break;
				}
			}
		}
	}

	/**
	 * 如果"线程不为空"且"线程还在执行" -- 就中断线程
	 */
	private void notifyReader() {
		if (mReadThread != null && mReadThread.isAlive()) {
			mReadThread.interrupt();   	// 中断线程
		}
	}


	/**
	 * 通过串口类(调用串口的驱动文件)发送数据
	 *
	 * @param b	要发送的数据
	 */
	public void writeCommand(byte[] b) {
		if (b == null)
			return;

		Action_Idle = Action_Meter_Read;


		Log.d(Tag,"对串口写入：" + Tools.bytesToHexString(b));
		isReceive = true;
		notifyReader();					// 中断线程

		if (null != mSerialPort) {
			mSerialPort.WriteSerialByte(mSerialPort.getFd(), b);
		}

		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * Toast(吐司) -- 位置设置为"顶部"
	 * @param str
	 */
	public void DisplayToast(String str) {
		Toast toast = Toast.makeText(mContext, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}
}
