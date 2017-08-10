package com.zh.metermanagement.utils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 主要是通过BitmapFactory.Options 来实现。<br>
 * Options中有个属性inJustDecodeBounds。我们可以充分利用它，来避免大图片的溢出问题。他是什么原理呢？<br>
 * API这样说：如果该值设为true那么将不返回实际的bitmap，也不给其分配内存空间这样就避免内存溢出了(相当于占一个坑)。<br>
 * 但是允许我们查询图片的信息这其中就包括图片大小信息（options.outHeight (图片原始高度)和option.outWidth(图片原始宽度)）。<br>
 * Options中有个属性inSampleSize。我们可以充分利用它，实现缩放。<br>
 * 如果被设置为一个值> 1,要求解码器解码出原始图像的一个子样本,返回一个较小的bitmap,以节省存储空间。<br>
 * 例如,inSampleSize = = 2，则取出的缩略图的宽和高都是原始图片的1/2，图片大小就为原始大小的1/4。<br>
 * 对于任何值< = 1的同样处置为1。<br>
 * 那么相应的方法也就出来了，通过设置 inJustDecodeBounds为true，<br>
 * 获取到outHeight(图片原始高度)和 outWidth(图片的原始宽度)，然后计算一个inSampleSize(缩放值)，<br>
 * 然后就可以取图片了，这里要注意的是，inSampleSize 可能小于0，必须做判断。<br>
 */

/**
 * Image compress factory class
 * 图片压缩工厂类
 * 
 *
 */
public class ImageFactory {

    /**
     * 得到图片文件的大小
     * @param imgPath 图片文件的路径
     * @return
     */
    public static long getImageSize(String imgPath){
        File file = new File(imgPath);
        return file.length();
    }


    /**
     * 得到图片文件的实际宽高
     * @param imgPath 图片文件的路径
     * @return
     */
    public static int[] getImageWidthAndHeight(String imgPath){

        int[] wAndh = {0,0};

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边(只读参数)，不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Config.RGB_565;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath,newOpts);		// (根据原图)生成一个没有内容(只有参数)的"位图"

        int w = newOpts.outWidth;					// 获取图片的"真实宽度"
        int h = newOpts.outHeight;  				// 获取图片的"真实高度"

        wAndh[0] = w;
        wAndh[1] = h;

        return wAndh;
    }

	/**
	 * Get bitmap from specified image path
	 * 根据"指定的图片路径"得到"位图"
	 *
	 * @param imgPath	指定的图片路径
	 * @return	Bitmap 	位图		
	 */
	public static Bitmap getBitmap(String imgPath) {
		// Get bitmap through image path
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = false;
		newOpts.inPurgeable = true;
		newOpts.inInputShareable = true;
		
		newOpts.inSampleSize = 1;			                     // inSampleSize=1：表示不压缩
		newOpts.inPreferredConfig = Config.RGB_565;
		return BitmapFactory.decodeFile(imgPath, newOpts);      // 根据"图片文件"生成"位图"
	}
	
	/**
	 * Store bitmap into specified image path<br>
	 * "图像位图"存储到"指定的路径"<br>
	 * 	 
	 * @param bitmap					要生成固定文件的位图(bitmap是在"内存中")
	 * @param outPath					存储的路径(File file = new File(outPath); if(!file.exists()) file.mkdir() )
	 * @throws FileNotFoundException    执行这个过程，一般都要检查错误
	 */
	public static void storeImage(Bitmap bitmap, String outPath) throws FileNotFoundException {
		FileOutputStream os = new FileOutputStream(outPath);
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);			// 100==>不压缩
	}
	
	/**
     * 压缩到指定宽高(传进来的是"图片文件路径")<br>
     * 将传进来的"图片文件路径"拿到"位图的真实参数"，设置"Bitmap参数(根据自己需要"压缩")",再将这个"图片文件"和参数，生成"位图"<p>
     *
	 * Compress image by pixel, this will modify image width/height. <br>
	 * 压缩图像的"像素",这将修改图像宽度/高度。<br>
	 * Used to get thumbnail<br>
	 * 用于获取缩略图<br>
	 * 
	 * @param imgPath 	存储图片的路径
	 * @param pixelW 	目标像素的宽度
	 * @param pixelH 	目标像素的高度
	 * @return			位图
	 */
	public static Bitmap ratio(String imgPath, float pixelW, float pixelH) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true，即只读边(只读参数)，不读内容
        newOpts.inJustDecodeBounds = true;
        newOpts.inPreferredConfig = Config.RGB_565;

        Bitmap bitmap = BitmapFactory.decodeFile(imgPath,newOpts);		// (根据原图)生成一个没有内容(只有参数)的"位图"
          
        newOpts.inJustDecodeBounds = false;  		// 设为"false"，说明下面要塞图片内容了
        int w = newOpts.outWidth;					// 获取图片的"真实宽度"  
        int h = newOpts.outHeight;  				// 获取图片的"真实高度" 
		
        // 想要缩放的目标尺寸
        float hh = pixelH;							// 设置高度为240f时，可以明显看到图片缩小了
	    float ww = pixelW;							// 设置宽度为120f，可以明显看到图片缩小了
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
        int be = 1;									//be=1表示不缩放  
        if (w > h && w > ww) {						// 如果"真实宽>真实高"且"真实宽>缩放宽"——(一句话：宽大)
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {				// 如果"真实高>真实宽"且"真实高>缩放高"——(一句话：高大)  
            be = (int) (newOpts.outHeight / hh);  
        }  
		
        if (be <= 0) be = 1;  						// 小于0，必须弄成1
//        System.out.println("be:"+be);
        newOpts.inSampleSize = be;					//设置缩放比例
        // 开始压缩图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(imgPath, newOpts);			// 此时生成的"位图"(解析文件)，就有内容了
        // 压缩好比例大小后再进行质量压缩
        // return compress(bitmap, maxSize); // 这里再进行质量压缩的意义不大，反而耗资源，删除
        return bitmap;
	}
	
	/**
     * 压缩到指定宽高(传进来的是"Bitmap")<br>
     * 将传进来的"Bitmap"压缩后放到"流"中，设置"Bitmap参数(根据自己需要"压缩")",再将这个"流"和参数，生成"位图"<p>
     *
	 * Compress image by size, this will modify image width/height. <br>
	 * 压缩图像的"大小",这将修改图像宽度/高度。<br>
	 * Used to get thumbnail<br>
	 * 用于获取缩略图<br>
	 * 
	 * @param image	Bitmap 位图
	 * @param pixelW 	目标像素的宽度
	 * @param pixelH 	目标像素的高度
	 * @return			位图
	 */
	public static Bitmap ratio(Bitmap image, float pixelW, float pixelH) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();			        // byte数组输出流
	    image.compress(Bitmap.CompressFormat.JPEG, 100, os);			        // 将"位图"数据压缩后放到流中
        //判断如果"图片大于1M",进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
	    if( (os.toByteArray().length / 1024) > 1024) {
	        os.reset();                                                         // 重置baos即"清空baos"
	        image.compress(Bitmap.CompressFormat.JPEG, 50, os);                 // 这里压缩50%，把压缩后的数据存放到baos中
	    }  
	    ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());  // byte数组输入流
	    BitmapFactory.Options newOpts = new BitmapFactory.Options();           // 新建一个位图的参数类
	    //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
	    newOpts.inJustDecodeBounds = true;
	    newOpts.inPreferredConfig = Config.RGB_565;
	    Bitmap bitmap = BitmapFactory.decodeStream(is, null, newOpts);          // 解析流，生成"位图"

	    newOpts.inJustDecodeBounds = false;                                    // 此时的"位图"，只能拿到参数，不能读取内容
	    int w = newOpts.outWidth;                                               // 位图的真实宽
	    int h = newOpts.outHeight;                                              // 位图的真实高
	    float hh = pixelH;                                                      // 设置高度为240f时，可以明显看到图片缩小了
	    float ww = pixelW;                                                      // 设置宽度为120f，可以明显看到图片缩小了
	    //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可  
	    int be = 1;                                                             //be=1表示不缩放
	    if (w > h && w > ww) {                                                  // 如果"真实宽>真实高"且"真实宽>缩放宽"——(一句话：宽大)
	        be = (int) (newOpts.outWidth / ww);  
	    } else if (w < h && h > hh) {                                           // 如果"真实高>真实宽"且"真实高>缩放高"——(一句话：高大)
	        be = (int) (newOpts.outHeight / hh);  
	    }  
	    if (be <= 0) be = 1;                                                    // 小于0，必须弄成1

	    newOpts.inSampleSize = be;                                              //设置缩放比例
	    //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
	    is = new ByteArrayInputStream(os.toByteArray());
	    bitmap = BitmapFactory.decodeStream(is, null, newOpts);                 // 解析流(输入流is)，生成"位图"
	    //压缩好比例大小后再进行质量压缩
        // return compress(bitmap, maxSize);                            // 这里再进行质量压缩的意义不大，反而耗资源，删除
	    return bitmap;
	}
	
	/**
     * 压缩到"指定大小"(传进来的是"Bitmap")<p>
     *
	 * Compress by quality,  and generate image to the path specified<br>
     * 压缩的"质量",并生成图像到指定的路径
	 *
	 * @param image             Bitmap 位图
	 * @param outPath           图形生成指定的路径
	 * @param maxSize           目标将被压缩"比"maxSize小。(kb)
	 * @throws IOException       流的操作，一般检查io异常
	 */
	public static void compressAndGenImage(Bitmap image, String outPath, int maxSize) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();                 	// byte数组输出流
		int options = 100;                                                          // scale  比例
        image.compress(Bitmap.CompressFormat.JPEG, options, os);  		            // 将"位图"按一定比例压缩后，放入输出流中

        // 压缩比例，由 100，每次循环递减10，直到压缩"所要的大小"——照这样算，比例会变成"负数"(有问题吗)
        while ( os.toByteArray().length / 1024 > maxSize) {
        	os.reset();                                                             // 重置baos即"清空baos"
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);                // 将"位图"按照"比例"压缩，放到"输出流"中
        }

        // 生成压缩文件——将"输出流"写到指定的文件中
        FileOutputStream fos = new FileOutputStream(outPath);
        fos.write(os.toByteArray());  
        fos.flush();  
        fos.close();
	}
	
	/**
     * 压缩到"指定大小"(传进来的是"图片文件路径")——压缩图片质量——是否"删除"原图片<p>
     *
	 * Compress by quality,  and generate image to the path specified<br>
	 * 压缩的质量,并生成图像到指定的路径<br>
     *
	 * @param imgPath           原图路径
	 * @param outPath           图形生成指定的路径
	 * @param maxSize           目标将被压缩"比"maxSize小。(kb)
	 * @param needsDelete       压缩后是否"删除"原始文件
	 * @throws IOException        流的操作，一般检查io异常
     *
	 */
	public static void compressAndGenImage(String imgPath, String outPath, int maxSize, boolean needsDelete) throws IOException {
		compressAndGenImage(getBitmap(imgPath), outPath, maxSize);
		
		// 删除文件
		if (needsDelete) {
			File file = new File(imgPath);
			if (file.exists()) {                // 这个文件存在，就删除
				file.delete();
			}
		}
	}
	
	/**
     * 将"位图"压缩到"指定宽高"(压缩图片大小)，存放到"指定文件"<p>
     *
	 * Ratio and generate thumb to the path specified
     *
	 * 
	 * @param image                     Bitmap 位图
	 * @param outPath                   "图片"指定存放的路径
     * @param pixelW 	                 目标像素的宽度
     * @param pixelH 	                 目标像素的高度
	 * @throws FileNotFoundException    "文件是否存在"的异常
	 */
	public static void ratioAndGenThumb(Bitmap image, String outPath, float pixelW, float pixelH) throws FileNotFoundException {
		Bitmap bitmap = ratio(image, pixelW, pixelH);
		storeImage( bitmap, outPath);
	}
	
	/**
     * 将"图片文件"压缩到"指定宽高"(压缩图片大小)，存放到"指定文件"——是否"删除"原图片<p>
	 * Ratio and generate thumb to the path specified
	 * 
	 * @param imgPath                   图片文件路径
	 * @param outPath                   "图片"指定存放的路径
     * @param pixelW                     目标像素的宽度
     * @param pixelH                     目标像素的高度
     * @param needsDelete               压缩后是否"删除"原始文件
     * @throws FileNotFoundException    "文件是否存在"的异常
	 */
	public static void ratioAndGenThumb(String imgPath, String outPath, float pixelW, float pixelH, boolean needsDelete) throws FileNotFoundException {
		Bitmap bitmap = ratio(imgPath, pixelW, pixelH);
		storeImage( bitmap, outPath);
		
		// Delete original file
				if (needsDelete) {
					File file = new File(imgPath);
					if (file.exists()) {
						file.delete();
					}
				}
	}
}