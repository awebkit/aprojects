package com.autonavi.tbt;

public class Camera {
	
	public int m_iCameraType;		// 电子眼类型，0 为测速摄像头，1为监控摄像头
	public int m_iCameraSpeed;		// 电子眼限速，若无限速信息则为0
	public double m_dLongitude; 	// 经度
	public double m_dLatitude;  	// 纬度
}