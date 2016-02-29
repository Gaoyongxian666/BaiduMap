package com.example.baidumap;

import com.baidu.location.Address;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import android.R.anim;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

public class UpDataLocationActivity extends Activity implements
		OnClickListener, OnMapLoadedCallback,OnGetGeoCoderResultListener {
	
	private static String mTAG = "UpDataLocationActivity";

	public MapView mMapView = null;
	public BaiduMap mBaiduMap = null;

	ImageView location_back;
	Button location_ok_Btn;
	ImageView mylocation_btn;
	
	
	public LatLng mCenterLatLng;
	private double myCentureLatitude;
	private double myCentureLongitude;
	private String myCentureAddress;
	
	private GeoCoder Search;
	private UiSettings mUiSettings;//��ͼ����

	// ��λ���
	public BDLocation currlocation = null; // �洢��ǰ��λ��Ϣ
	public String mAddress;                //�洢��ǰ��ַ
	private GeoCoder mySearch;
	public LocationClient mLocationClient = null;
	public MyLocationListener listener = new MyLocationListener();
	private boolean isFirstIn = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);// ���ر�����
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_updata_location);

		findView();
		initView();
		initLocation();
		//����Ļ�м仭��ͼ��
		CenterIcon centerIcon = new CenterIcon(this, mMapView);
		getWindow().addContentView(
				centerIcon,
				new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT));
		
		// �ٶȵ�ͼ״̬�ı��������
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			@Override
			public void onMapStatusChangeStart(MapStatus status) {
				// updateMapState(status);
			}

			@Override
			public void onMapStatusChangeFinish(MapStatus status) {
				updateMapState(status);
			}

			@Override
			public void onMapStatusChange(MapStatus status) {
				// updateMapState(status);
			}
        });  
	}
	
	/**
	 * ��ȡ�ƶ�����Ļ�м侭γ��
	 * @param status
	 */
    protected void updateMapState(MapStatus status) {
    	mCenterLatLng = status.target;
    	/** ��ȡ��γ�� */  
        myCentureLatitude = mCenterLatLng.latitude;  
        myCentureLongitude = mCenterLatLng.longitude;
        LatLng ptCenter = new LatLng(myCentureLatitude, myCentureLongitude);
        
    	// ��ʼ������ģ�飬ע���¼�����
    	Search = GeoCoder.newInstance();
    	Search.setOnGetGeoCodeResultListener(this);
        Search.reverseGeoCode(new ReverseGeoCodeOption().location(ptCenter));
	}
    
    /**
	 * ���������ص�����
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
            return;  
        }else{
        	myCentureAddress = result.getAddress();
        	Toast.makeText(this, result.getAddress(),Toast.LENGTH_SHORT).show();
        	Log.e("aa", result.getAddress());
        }
        
	}
	/**
     * �ҿؼ�
     */
	private void findView() {
		location_back = (ImageView) findViewById(R.id.location_back);
		location_back.setOnClickListener(this);
		location_ok_Btn = (Button) findViewById(R.id.location_ok);
		location_ok_Btn.setOnClickListener(this);
		mylocation_btn = (ImageView) findViewById(R.id.id_mylocation_btn);
		mylocation_btn.setOnClickListener(this);
	}

	/**
	 * ��ʼ����λ
	 */
	private void initLocation() {
		// ʵ�����ٶȵ�ͼ��λ������
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(listener);

		// mLocationMode = LocationMode.NORMAL;
		// // ��ͼ״̬��ť
		// img_myLocation = (ImageView) findViewById(R.id.id_mylocation_img);
		// img_myLocation.setImageResource(R.drawable.main_icon_location);

		// ���õ�ͼ����
		LocationClientOption option = new LocationClientOption();
		// ��������ϵ
		option.setCoorType("bd09ll");
		// ����λ����Ϣ
		option.setIsNeedAddress(true);
		// ���ø߾��ȶ�λ
		option.setOpenGps(true);
		// ����ˢ�¼��1��
		option.setScanSpan(2000);
		mLocationClient.setLocOption(option);

		// ������ͼ��λ
		if (!mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		mLocationClient.start();
	}

	/**
	 * ��ʼ����ͼ
	 */
	private void initView() {
		mMapView = (MapView) findViewById(R.id.mapView);
		mBaiduMap = mMapView.getMap();
		mUiSettings = mBaiduMap.getUiSettings();
		mUiSettings.setRotateGesturesEnabled(false);//������ת����
		mUiSettings.setOverlookingGesturesEnabled(false);//���ø���
        mMapView.showScaleControl(false);// ����ʾ��ͼ�ϱ�����  
        mMapView.showZoomControls(false);// ����ʾ��ͼ���ſؼ�����ť��������  
		// ��ʼ�������ߵ�100��
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(18.0f);
		mBaiduMap.setMapStatus(msu);
	}

	/**
	 * ��λ������
	 * 
	 * @author Administrator
	 * 
	 */
	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				return;
			}
			currlocation = location;
            mAddress = location.getAddrStr();
			// ��װ����
			MyLocationData data = new MyLocationData.Builder()//
					.accuracy(location.getRadius())//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			// �󶨵�ͼ����
			mBaiduMap.setMyLocationData(data);

			// ��һ�δ�
			if (isFirstIn) {
				// ��ȡ�����
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(update);
				isFirstIn = false;
				myCentureLatitude = currlocation.getLatitude();
		        myCentureLongitude = currlocation.getLongitude();
		        myCentureAddress = mAddress;
				// Toast.makeText(context, location.getAddrStr(),
				// Toast.LENGTH_SHORT).show();
			}

		}

	}

	/**
	 * ��ͼ������ɺ�
	 */
	@Override
	public void onMapLoaded() {
		// BitmapDescriptor bitmap;
		// bitmap =
		// BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);
		//
		// MarkerOptions options = new MarkerOptions().position(new LatLng(0,
		// 0))
		// .icon(bitmap).zIndex(9).anchor(0.5f, 0.5f);
		// marker = (Marker) (mBaiduMap.addOverlay(options));
		// marker.setFlat(true);
		
	}

	/**
	 * ����Ļ�м�ʵ��һ��View
	 * 
	 * @author Administrator
	 * 
	 */
	class CenterIcon extends View {

		public int w;
		public int h;
		public Bitmap mBitmap;
		public MapView mMapView;

		public CenterIcon(Context context, MapView mMapView) {

			super(context);
			// ������Ļ���ĵ�ͼ��
			mBitmap = BitmapFactory.decodeResource(getResources(),
					R.drawable.icon_marker_location);
			this.mMapView = mMapView;
		}

		@Override
		protected void onDraw(Canvas canvas) {

			super.onDraw(canvas);
			// ��ȡ��Ļ���ĵ�����
				
		    w = mMapView.getWidth() / 2 - mBitmap.getWidth() / 2;
			h = mMapView.getHeight() / 2 - mBitmap.getHeight();
			canvas.drawBitmap(mBitmap, w, h, null);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.location_back:
			Intent intent = new Intent(UpDataLocationActivity.this,
					UpDataActivity.class);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.location_ok:
			PositionEntity.latitue = myCentureLatitude;
	        PositionEntity.longitude = myCentureLongitude;
	        PositionEntity.address = myCentureAddress;
	        Log.e(mTAG, PositionEntity.latitue+"-"+PositionEntity.longitude+"-"+PositionEntity.address);
	        Intent intent2 = new Intent(UpDataLocationActivity.this,
					UpDataActivity.class);
			setResult(RESULT_OK, intent2);
			finish();
			break;
		case R.id.id_mylocation_btn:
			LatLng ll = new LatLng(currlocation.getLatitude(), currlocation.getLongitude());
			MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(update);
			break;
		}
	}

	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		mMapView = null;

	}

	protected void onResume() {
		super.onRestart();
		mMapView.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// ������λ
		mBaiduMap.setMyLocationEnabled(true);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
	}

	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// ֹͣ��λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		
	}

    /**
     * �������ص�����
     */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
		
	}
	

}
