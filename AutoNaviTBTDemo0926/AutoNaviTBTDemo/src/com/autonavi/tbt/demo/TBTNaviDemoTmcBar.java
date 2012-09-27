package com.autonavi.tbt.demo;

import android.graphics.Bitmap;

import com.autonavi.tbt.TmcBar;
import com.autonavi.tbt.TmcBarItem;

public class TBTNaviDemoTmcBar {
	
	// TMC bar 宽 像素值
	static public final int TMC_BAR_WIDTH = 10;
	// TMC bar 高 像素值
	static public final int TMC_BAR_HEIGHT = 80;
	
	// TMC bar 空白图谱
	static public final int[] COLOR_PATTERN_EMPTY = {0XFF404041	,0XFF474646	,0XFF4E4D4D	,0XFF555454	,0XFF585757	
		,0XFF585757	,0XFF555454	,0XFF4E4D4D	,0XFF474646,0XFF404041};
	
	// TMC bar 灰色图谱
	static public final int[] COLOR_PATTERN_GRAY = {0XFF818181,0XFF9B9B9B,0XFFB4B4B4,0XFFCECECE,0XFFE0E0E0
		,0XFFE0E0E0,0XFFCECECE,0XFFB4B4B4,0XFF9B9B9B,0XFF818181};
	// TMC bar 绿色图谱
	static public final int[] COLOR_PATTERN_GREEN = {0XFF256706,0XFF348705,0XFF4FB508,0XFF65D82E,0XFFA8E777
		,0XFFA8E777,0XFF65D82E,0XFF4FB508,0XFF348705,0XFF256706};
	// TMC bar 黄色图谱
	static public final int[] COLOR_PATTERN_YELLOW = {0XFF8E5D04,0XFFB57704,0XFFE89805,0XFFFEB735,0XFFFECE76
		,0XFFFECE76,0XFFFEB735,0XFFE89805,0XFFB57704, 0XFF8E5D04};
	// TMC bar 红色图谱
	static public final int[] COLOR_PATTERN_RED = {0XFF800407, 0XFFA5060A,0XFFD5060A, 0XFFF74E36, 0XFFFE7678
		,0XFFFE7678,0XFFF74E36,0XFFD5060A,0XFFA5060A,0XFF800407 };
	
	static public Bitmap createTmcBarBitmap(TmcBar tmcBar) {
		return createTmcBarBitmap(tmcBar, TMC_BAR_WIDTH, TMC_BAR_HEIGHT);
	}
	
	static public Bitmap createTmcBarBitmap(TmcBar tmcBar, int width, int height){	
		int []colors = new int[width * height];
		
		int length = 0;
		int emptyLength = 0;
		for (int i=0; i < tmcBar.m_Num; i++){
			TmcBarItem item = tmcBar.m_TmcBar[i];
			length += item.m_iLength;
		}
		if (length < 2000)
		{
			emptyLength = 2000 - length;
			length = 2000;
		}
		int temp = 0;
		if (emptyLength != 0) {
			int h = emptyLength * height / length;
			for (int j = 0; j < h; j++) {
				for (int k = 0; k < COLOR_PATTERN_EMPTY.length; k++)
				{
					colors[(j + temp)*TMC_BAR_WIDTH + k] = COLOR_PATTERN_EMPTY[k];
				}
			}
			temp += h;			
		}
		for (int i=tmcBar.m_Num - 1; i >= 0; i--){
			TmcBarItem item = tmcBar.m_TmcBar[i];
			int h = item.m_iLength * height / length;	
			if (i == 0) {
				h = height - temp;
			}
			
			int [] pattern;
			switch(item.m_iStatus) {
			case 0: // gray
				pattern = COLOR_PATTERN_GRAY;
				break;
			case 1: // green
				pattern = COLOR_PATTERN_GREEN;
				break;
			case 2: // yellow
				pattern = COLOR_PATTERN_YELLOW;
				break;
			case 3: // red
				pattern = COLOR_PATTERN_RED;
				break;
			default:
				pattern = COLOR_PATTERN_GRAY;				
			}
			
			for (int j = 0; j < h; j++) {
				for (int k = 0; k < pattern.length; k++)
				{
					colors[(j + temp)* TMC_BAR_WIDTH + k] = pattern[k];
				}
			}
			temp += h;
		}
			
		Bitmap bmp = Bitmap.createBitmap(colors, width, height, 
											Bitmap.Config.ARGB_8888);						
		return bmp;
	}
	
	private static int calcColorByStatus(int status) {
		int color;
		
		switch(status) {
		case 0: // gray
			color = 0xFF545454;
			break;
		case 1: // green
			color = 0xFF00FF00;
			break;
		case 2: // yellow
			color = 0xFFFFFF00;
			break;
		case 3: // red
			color = 0xFFFF0000;
			break;
		default:
			color = 0xFF545454;				
		}
		
		return color;
	}
	
	
	// 将一个byte数转成int
	// 实现这个函数的目的是为了将byte数当成无符号的变量去转化成int
	public static int convertByteToInt(byte data){
		
		int heightBit = (int) ((data>>4) & 0x0F);
		int lowBit = (int) (0x0F & data);

		return heightBit * 16 + lowBit;
	}
	
	
	// 将纯RGB数据数组转化成int像素数组
	public static int[] convertByteToColor(byte[] data){
		int size = data.length;
		if (size == 0){
			return null;
		}
		
		int arg = 0;
		if (size % 3 != 0){
			arg = 1;
		}
		
		// 一般情况下data数组的长度应该是3的倍数，这里做个兼容，多余的RGB数据用黑色0XFF000000填充		
		int []color = new int[size / 3 + arg];
		int red, green, blue;
		
		if (arg == 0){
			for(int i = 0; i < color.length; ++i){
				red = convertByteToInt(data[i * 3 + 2]);
				green = convertByteToInt(data[i * 3 + 1]);
				blue = convertByteToInt(data[i * 3]);	
				
				// 获取RGB分量值通过按位或生成int的像素值						
				//color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;
				color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;
			}
		}else{
			for(int i = 0; i < color.length - 1; ++i){
				red = convertByteToInt(data[i * 3 + 2]);
				green = convertByteToInt(data[i * 3 + 1]);
				blue = convertByteToInt(data[i * 3]);	
				color[i] = (red << 16) | (green << 8) | blue | 0xFF000000;	
			}
			
			color[color.length] = 0xFF000000;
		}
	
		return color;
	}
}
