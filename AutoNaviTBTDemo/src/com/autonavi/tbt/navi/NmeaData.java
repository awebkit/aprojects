package com.autonavi.tbt.navi;

public class NmeaData {
	public double m_Longitude;		// γ��, ��λ�� (��ֵΪ��γ, ��ֵΪ��γ)
	public double m_Latitude;		// ����, ��λ�� (��ֵΪ����, ��ֵΪ����)
	public double m_Altitude;		// ����, ��λ��
	public double m_Speed;			// �ٶ�, ��λǧ��/ʱ
	public double m_Track;			// �����, ��λ��
	public double m_MagVariation;	// �شű仯, ��λ��
	public double m_Pdop;			// λ�þ��Ȳ���
	public double m_Hdop;			// ˮƽ���Ȳ���
	public double m_Vdop;			// ��ֱ���Ȳ���
	public int m_NumSats;			// �ǿ�ͼ���Ǹ���
	public int m_FixedMode;			// GPS��λ����
	public int m_BJYear;			// GPS(BJ)ʱ�䣭����
	public int m_BJMonth;			// GPS(BJ)ʱ�䣭����
	public int m_BJDay;				// GPS(BJ)ʱ�䣭����
	public int m_BJHour;			// GPS(BJ)ʱ�䣭��ʱ
	public int m_BJMinute;			// GPS(BJ)ʱ�䣭����
	public int m_BJSecond;			// GPS(BJ)ʱ�䣭����
	public char m_ValidChar;		//��λ�ɹ����ı�־
	public int m_LastFixQuality; 	// ǰһ�ζ�λ������0 ��Ч��꣬1 ��Ч��꣬2 DGPS�������
	public boolean m_HasCoordEverBeenValid;	//�Ƿ���λ�ɹ���
}