package com.example.baidumap.entity;

import java.text.DecimalFormat;

/**
 * ��װ�Ĺ���λ�õ�ʵ��
 * @author Administrator
 *
 */
public class PositionEntity {
	DecimalFormat decfmt = new DecimalFormat("##0.000000");

	public static double latitue;

	public static double longitude;

	public static String address;

	public PositionEntity() {
	}

	public PositionEntity(double latitude, double longtitude, String address) {
		this.latitue = latitude;
		this.longitude = longtitude;
		this.address = address;
	}
}
