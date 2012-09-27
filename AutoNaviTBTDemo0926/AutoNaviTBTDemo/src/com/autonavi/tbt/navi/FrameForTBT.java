package com.autonavi.tbt.navi;

import com.autonavi.tbt.CarLocation;
import com.autonavi.tbt.DGNaviInfor;
import com.autonavi.tbt.IFrameForTBT;
import com.autonavi.tbt.TBT;
import com.autonavi.tbt.demo.TBTNaviDemoMapView;

public class FrameForTBT implements IFrameForTBT{
	
	private TBT mTbt;
	private TBTNaviDemoMapView mTbtNavi;
	
	public FrameForTBT(TBTNaviDemoMapView tbtNavi, TBT tbt)
	{
		mTbtNavi = tbtNavi;
		mTbt = tbt;
	}
	
	/********************************************************************
	函数名称：netRequestHttp
	功能介绍：HTTP请求
	返 回 值：无
	说    明：此接口为异步接口，调用占用实现者的线程，函数调用必须立即返回
	********************************************************************/
	public void netRequestHttp(
		int moduleID,	
		int connectId,			// [I] 连接ID，Frame请求到数据后用此ID将数据传给TBT
		int type,				// [I] 0为Post方式，1为Get方式
		String url,				// [I] 请求的URL串
		String head,			// [I] HTTP头，默认为空
		byte[] data,			// [I] Post方式的Data数据，默认为空
		int dataLength			// [I] Data数据长度，默认为空
		) 
	{
		HttpRequest http = new HttpRequest(mTbt, connectId, type, url, head, data, dataLength);
		http.start();
	}


	/********************************************************************
	功能介绍：设置GPS是否有效
	返 回 值：无
	说    明：无
	********************************************************************/
	public void setGpsValid(
		int isValid				// [I] 0 无效，1有效
		)
	{
		
	}
	
	/********************************************************************
	函数名称：updateNaviInfor
	功能介绍：更新导航信息，参见DGNaviInfor定义
	返 回 值：无
	说    明：无
    ********************************************************************/
	public void updateNaviInfor(
		DGNaviInfor  dgNaviInfor		// [I] 导航信息
		) 
	{
		String text = "当前道路名称 = ";
		text += dgNaviInfor.m_CurRoadName;
		text += "\n下一路名 = ";
		text += dgNaviInfor.m_NextRoadName;
		text += "\n电子眼距离 = ";
		text += dgNaviInfor.m_CameraDist;
		text += "\n路径剩余距离 = ";
		text += dgNaviInfor.m_RouteRemainDis;
		text += "\n路径剩余时间 = ";
		text += dgNaviInfor.m_RouteRemainTime;
		text += "\n路段剩余距离 = ";
		text += dgNaviInfor.m_SegRemainDis;
		text += "\n路段剩余时间 = ";
		text += dgNaviInfor.m_SegRemainTime;
		text += "\n转向箭头 = ";
		text += dgNaviInfor.m_Icon;
		String s = "Loc " + dgNaviInfor.m_Longitude;
		s += " " + dgNaviInfor.m_Latitude;
		s += " " + dgNaviInfor.m_CarDirection;
//		System.out.println(s);
		
		System.out.printf("updateNaviInfor[CCP]: %.6f, %.6f\n", dgNaviInfor.m_Longitude, dgNaviInfor.m_Latitude);

		mTbtNavi.updateNaviInfor(dgNaviInfor);
	}



	@Override
	public void showCross(byte[] pCrossBackBuf, byte[] pCrossArrowBuf,
			int iBackSize, int iArrowSize) {
		
		mTbtNavi.showCross(pCrossBackBuf, pCrossArrowBuf, iBackSize, iArrowSize);
		
	}
	
	@Override
	public void hideCross() {
		// TODO Auto-generated method stub
		mTbtNavi.hideCross();
		
	}

	/********************************************************************
	函数名称：ShowLaneInfo
 	功能介绍：显示车道信息
	返 回 值：无
	说    明：无
    ********************************************************************/
	public void showLaneInfo(
		byte[] laneBackInfo,			// [I] 车道背景信息
		byte[] laneSelectInfo			// [I] 车道选择信息
		) {
	/*	System.out.printf("showLaneInfo: laneBackInfo[%d, %d, %d, %d, %d, %d, %d, %d], laneSelectInfo[%d, %d, %d, %d, %d, %d, %d, %d]\n",
				laneBackInfo[0],laneBackInfo[1],laneBackInfo[2],laneBackInfo[3],laneBackInfo[4],
				laneBackInfo[5],laneBackInfo[6],laneBackInfo[7], laneSelectInfo[0], laneSelectInfo[1]
				, laneSelectInfo[2], laneSelectInfo[3], laneSelectInfo[4], laneSelectInfo[5], laneSelectInfo[6], laneSelectInfo[7]);*/
		mTbtNavi.showLaneInfo(laneBackInfo, laneSelectInfo);
	}

	/********************************************************************
	函数名称：hideLaneInfo
	功能介绍：隐藏车道信息
	返 回 值：无
	说    明：无
    ********************************************************************/
	public void hideLaneInfo() {
		//System.out.println("hideLaneInfo");
		mTbtNavi.hideLaneInfo();
		
	}

	/********************************************************************
	函数名称：playNaviSound
	功能介绍：使用TTS播报一个字符串
	返 回 值：无
	说    明：函数占用Frame的线程，此函数调用后马上返回
    ********************************************************************/
	public void playNaviSound(
		int iSoundType,			   // [I] 语音类型：1 导航播报，2 前方路况播报
		String soundStr				// [I] 要播报的字符串Unicode编码
		) 
	{
		System.out.println(soundStr);
		if (mTbtNavi != null) {
	//		if (Tts.JniIsPlaying() == 2) {
				((TBTNaviDemoMapView) mTbtNavi).playSound(soundStr);
	//		}
		}

		mTbtNavi.PostText(soundStr, 1);

	}
	/********************************************************************
	函数名称：endEmulatorNavi
	功能介绍：停止模拟导航
	返 回 值：无
	说    明：指引模拟导航结束后通知Frame以便更新UI界面等
    ********************************************************************/
	public void endEmulatorNavi() 
	{
		if (mTbt != null) {
			mTbt.stopEmulatorNavi();
			mTbtNavi.onEmulatorNaviEnd();
		}

	}

	/********************************************************************
	函数名称：arriveWay
	功能介绍：通知到达途经点，如果到达目的地iWayID为0
	返 回 值：无
	说    明：无
	********************************************************************/
	public void arriveWay(
		int wayId		// [I] 到达途径点的编号，标号从1开始，如果到达目的地iWayID为0
		) 
	{
		if (wayId == 0)
		{
			mTbtNavi.onGPSNaviEnd();
		}
	}

	/****************************************************************
	函数名称：reroute
	功能介绍：通知Frame需要重新规划路径
	返 回 值：无
	说    明：无
	****************************************************************/
	public void reroute()
	{
		if (mTbtNavi != null) {
	//		if (Tts.JniIsPlaying() == 2) {
				((TBTNaviDemoMapView) mTbtNavi).playSound("偏离路径，正在重新规划路线");
	//		}
		}
		mTbtNavi.reroute();
	}
	
	public void rerouteForTMC() {
		if (mTbtNavi != null) {
	//		if (Tts.JniIsPlaying() == 2) {
				((TBTNaviDemoMapView) mTbtNavi).playSound("发生静默算路，重新规划路线");
	//		}
		}
		mTbtNavi.tmcReroute();
	}
	/********************************************************************
	函数名称：routeDestroy
	功能介绍：通知当前路径销毁
	返 回 值：无
	说    明：无
    ********************************************************************/
	public void routeDestroy() 
	{
		int i = 0;
		i++;
	}
//	CarLocation loc;
	/********************************************************************
	函数名称：carLocationChange
	功能介绍：通知当前匹配后的GPS位置点
	返 回 值：无
	说    明：
    ********************************************************************/
	public void carLocationChange(
		CarLocation  carLocation			// [I] 当前匹配后的点，参见VPLocation结构
		) 
	{
		//System.out.printf("carLocationChange: %.6f, %.6f\n", carLocation.m_Longitude, carLocation.m_Latitude);
	 	mTbtNavi.onCarLocationChanged(carLocation);
		//loc= carLocation;

	}
 
	/********************************************************************
	函数名称：setRequestRouteState
	功能介绍：通知路径计算状态
	返 回 值：无
	说    明：requestRouteState含义如下
			1	路径计算成功
			2	网络超时
			3	网络失败
			4	请求参数错误
			5	返回数据格式错误
			6	起点周围没有道路
			7	起点在步行街
			8	终点周围没有道路
			9	终点在步行街
			10	途经点周围没有道路
			11	途经点在步行街
    ********************************************************************/
	public void setRequestRouteState(
		int requestRouteState
		) {
		
		String str = "setRequestRouteState = ";
		str += requestRouteState;
		System.out.println(str);
		
		mTbtNavi.notifyRouteResult(requestRouteState);
		mTbtNavi.setRoutePrepared(true);
		
/*		String rpHint;

		switch(requestRouteState){
		case 1:
			
			mTbtNavi.setRoutePrepared(true);
			rpHint = "路径计算成功";
	//		mTbtNavi.createDialog();
	//		mTbtNavi.createNaviChoiceDialog();
			break;
		case 2:
			rpHint = "网络超时";
			break;
		case 3:
			rpHint = "网络失败";
			break;
		case 4:
			rpHint = "请求参数错误";
			break;
		case 5:
			rpHint = "返回数据格式错误";
			break;
		case 6:
			rpHint = "起点周围没有道路";
			break;
		case 7:
			rpHint = "起点在步行街";
			break;
		case 8:
			rpHint = "终点周围没有道路";
			break;
		case 9:
			rpHint = "终点在步行街";
			break;
		case 10:
			rpHint = "途经点周围没有道路";
			break;
		case 11:
			rpHint = "途经点在步行街";
			break;
			
		default:
			rpHint = "未知错误";
		}
		
		mTbtNavi.PostText(rpHint, 1);
*/		
	}

	/********************************************************************
	函数名称：tmcUpdate
	功能介绍：通知动态信息改变
	返 回 值：无
	说    明：动态更新后，需要更新光柱等
    ********************************************************************/
	public  void tmcUpdate() 
	{
		int n = 0;
		n++;
	}

	/********************************************************************
	函数名称：showTrafficPanel
	功能介绍：显示情报板
	返 回 值：无
	说    明：无
	作    者：余新彦	xinyan.yu@autonavi.com
    ********************************************************************/
	public  void showTrafficPanel(
		int iSize,
		byte[] panelBuf			// [I] 情报板数据
		) 
	{
		String s = "Traffic Panel size" + iSize;
		System.out.println(s);
		
		mTbtNavi.showTrafficPanel(iSize, panelBuf);
		
		int i=0;
		i++;
	}

	/********************************************************************
	函数名称：hideTrafficPanel
	功能介绍：关闭情报板
	返 回 值：无
	说    明：无
	作    者：余新彦	xinyan.yu@autonavi.com
    ********************************************************************/
	public void hideTrafficPanel() {
		mTbtNavi.hideTrafficPanel();
	}
}
