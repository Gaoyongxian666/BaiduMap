package com.example.baidumap.view;


import com.example.baidumap.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingItemViewBtn extends LinearLayout{

	TextView myLeftTextView;
	TextView myRightTextView;
	
	public SettingItemViewBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		initview();
	}

	private void initview() {
		LayoutInflater inflater=LayoutInflater.from(getContext());
		View view=inflater.inflate(R.layout.setting_view_btn, this);
		findView(view);
	}

	private void findView(View view) {
		myLeftTextView = (TextView) view.findViewById(R.id.setting_btn_LeftText);
		myRightTextView = (TextView) view.findViewById(R.id.setting_btn_RightText);
	}
    /**
     * ������ߵĿؼ�
     * @return
     */
	public TextView getMyLeftTextView() {
		return myLeftTextView;
	}
	/**
	 * �����ұߵĿؼ�
	 * @return
	 */
	public TextView getMyRightTextView() {
		return myRightTextView;
	}
	
	
	/**
	 * ������ߵ�����
	 * @param txt
	 */
	public void setLeftText(String txt){
		myLeftTextView.setText(txt);
	}
	/**
	 * �����ұߵ�����
	 * @param txt
	 */
	public void setRightText(String txt){
		myRightTextView.setText(txt);
	}
	/**
	 * �����ұߵ�ͼƬ
	 * @param resId
	 */
	public void setRightBitMap(int resId) {
		myRightTextView.setBackgroundResource(resId);
	}
}
