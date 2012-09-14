package com.autonavi.tbt;

public class CarLocation {
	public double m_Longitude;  // 自车经度
	public double m_Latitude;   // 自车纬度
	public int m_CarDir;		// 当前车辆的方向（单位度），以正北为基准，顺时针增加
	public int m_Speed;		    // 当前自车速度，单位公里每小时
	public int m_MatchStatus;	// 匹配状态，0 未匹配到路径上，1 匹配到路径上
}