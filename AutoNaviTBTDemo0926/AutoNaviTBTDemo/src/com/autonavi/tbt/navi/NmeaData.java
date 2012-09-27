package com.autonavi.tbt.navi;

public class NmeaData {
	public double m_Longitude;		// 纬度, 单位度 (正值为北纬, 负值为南纬)
	public double m_Latitude;		// 经度, 单位度 (正值为东经, 负值为西经)
	public double m_Altitude;		// 海拔, 单位米
	public double m_Speed;			// 速度, 单位千米/时
	public double m_Track;			// 方向角, 单位度
	public double m_MagVariation;	// 地磁变化, 单位度
	public double m_Pdop;			// 位置精度参数
	public double m_Hdop;			// 水平精度参数
	public double m_Vdop;			// 垂直精度参数
	public int m_NumSats;			// 星空图卫星个数
	public int m_FixedMode;			// GPS定位质量
	public int m_BJYear;			// GPS(BJ)时间－－年
	public int m_BJMonth;			// GPS(BJ)时间－－月
	public int m_BJDay;				// GPS(BJ)时间－－日
	public int m_BJHour;			// GPS(BJ)时间－－时
	public int m_BJMinute;			// GPS(BJ)时间－－分
	public int m_BJSecond;			// GPS(BJ)时间－－秒
	public char m_ValidChar;		//定位成功与否的标志
	public int m_LastFixQuality; 	// 前一次定位质量，0 无效坐标，1 有效坐标，2 DGPS更正坐标
	public boolean m_HasCoordEverBeenValid;	//是否曾经定位成功过
}