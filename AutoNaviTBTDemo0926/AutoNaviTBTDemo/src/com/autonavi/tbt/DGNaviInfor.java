package com.autonavi.tbt;

	/********************************************************************
	导航段转向图标定义如下：
	1			// 自车图标
	2			// 左转图标
	3			// 右转图标
	4			// 左前方图标
	5			// 右前方图标
	6			// 左后方图标
	7			// 右后方图标
	8			// 左转掉头图标
	9			// 直行图标
	10		    // 到达途经点图标
	11		    // 进入环岛图标
	12		    // 驶出环岛图标
	13		    // 到达服务区图标
	14		    // 到达收费站图标
	15		    // 到达目的地图标
	16		    // 进入隧道图标
	********************************************************************/
public class DGNaviInfor {
	public int m_Type;         		    // 更新类型,1 GPS导航更新,2 模拟导航更新
	public String m_CurRoadName;	    // 当前道路名称
	public String m_NextRoadName;	    // 下条道路名称
	public int m_SAPADist;				// 距离最近服务区的距离（单位米），若为-1则说明距离无效，没有服务区
	public int m_CameraDist;			// 距离最近电子眼距离（单位米），若为-1则说明距离无效，没有电子眼
	public int m_CameraType;			// 电子眼类型，0 测试摄像头，1为监控摄像头
	public int m_CameraSpeed;			// 电子眼限速，若无限速信息则为0
	public int m_Icon;         		    // 导航段转向图标，如上定义
	public int m_RouteRemainDis;	    // 路径剩余距离（单位米）
	public int m_RouteRemainTime;	    // 路径剩余时间（单位秒）
	public int m_SegRemainDis;		    // 当前导航段剩余距离（单位米）
	public int m_SegRemainTime;		    // 当前导航段剩余时间（单位秒）
	public int m_CarDirection;		    // 自车方向（单位度），以正北为基准，顺时针增加
	public double m_Longitude;		    // 自车经度
	public double m_Latitude;			// 自车纬度
	public int m_LimitedSpeed;		    // 当前道路速度限制（单位公里每小时）
	public int m_CurSegNum;   		    // 当前自车所在segment段，从0开始
	public int m_CurLinkNum;			// 当前自车所在Link，从0开始
	public int m_CurPointNum;			// 当前位置的前一个形状点号，从0开始
}