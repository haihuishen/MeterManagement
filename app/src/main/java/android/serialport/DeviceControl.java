package android.serialport;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * 操作设备文件的类！
 */
public class DeviceControl {

	/** 要操作的 -- 设备文件 的文件写入流 */
	private BufferedWriter mCtrlFile;
	private Context mContext;


	/**
	 * 构造函数
	 * @param path			要操作的 -- 设备文件
	 * @param context
	 * @throws IOException
	 */
	public DeviceControl(String path, Context context) throws IOException {
		File DeviceName = new File(path);
		mContext = context;
		mCtrlFile = new BufferedWriter(new FileWriter(DeviceName, false));
	}


	/**
	 * 给设备文件写入数据 -- 虽然这个文件是on但是这是看我们写入的数据
	 *
	 * @param power_on		要写到"设备文件"的数据
	 * @throws IOException
	 */
	public void PowerOnDevice(String power_on) throws IOException{
		mCtrlFile.write(power_on);
		mCtrlFile.flush();
	}


	/**
	 * 给设备文件写入数据 -- 虽然这个文件是off但是这是看我们写入的数据
	 *
	 * @param power_off		要写到"设备文件"的数据
	 * @throws IOException
	 */
	public void PowerOffDevice(String power_off) throws IOException{
		mCtrlFile.write(power_off);
		mCtrlFile.flush();
	}







	public void TriggerOnDevice() throws IOException // make barcode begin to
														// scan
	{
		mCtrlFile.write("trig");
		mCtrlFile.flush();
	}

	public void TriggerOffDevice() throws IOException // make barcode stop scan
	{
		mCtrlFile.write("trigoff");
		mCtrlFile.flush();
	}

	public void DeviceClose() throws IOException // close file
	{
		mCtrlFile.close();
	}

	public void MTGpioOn() {
		try {
			mCtrlFile.write("-wdout64 1");
			mCtrlFile.flush();
			Toast.makeText(mContext, "open mtgpio driver success",
					Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void MTGpioOff() {
		try {
			mCtrlFile.write("-wdout64 0");
			mCtrlFile.flush();
			Toast.makeText(mContext, "close mtgpio driver success", Toast.LENGTH_SHORT).show();
			//Log.i("shen","close mtgpio driver success");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}