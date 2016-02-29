package com.example.baidumap.view;


import com.example.baidumap.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingItemViewEdit extends LinearLayout{

	TextView myLeftTextView;
	EditText myRightEditText;
	
	public SettingItemViewEdit(Context context, AttributeSet attrs) {
		super(context, attrs);
		initview();
	}

	private void initview() {
		LayoutInflater inflater=LayoutInflater.from(getContext());
		View view=inflater.inflate(R.layout.setting_view_edit, this);
		findView(view);
	}

	private void findView(View view) {
		myLeftTextView = (TextView) view.findViewById(R.id.setting_edit_LeftText);
		myRightEditText = (EditText) view.findViewById(R.id.setting_edit_RightEdit);
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
		return myRightEditText;
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
		myRightEditText.setText(txt);
	}
}
