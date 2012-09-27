package com.autonavi.tbt;

public class NaviGuideItem {
	/********************************************************************
	导航段转向图标m_iIcon说明如下：
		0	无定义
		1	自车图标
		2	左转图标
		3	右转图标
		4	左前方图标
		5	右前方图标
		6	左后方图标
		7	右后方图标
		8	左转掉头图标
		9	直行图标
		10	到达途经点图标
		11	进入环岛图标
		12	驶出环岛图标
		13	到达服务区图标
		14	到达收费站图标
		15	到达目的地图标
		16	到达隧道图标
    ********************************************************************/
	public int m_Length;		// 导航段长度
	public int m_UseTime;	    // 导航段行驶时间
	public int m_Icon;			// 导航段转向图标，如上定义
	public String m_Name;		// 名称
	public double m_Longitude; 	// 经度
	public double m_Latitude;  	// 纬度
}