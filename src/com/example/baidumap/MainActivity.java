package com.example.baidumap;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MarkerOptions.MarkerAnimateType;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.example.baidumap.MyOrientationListener.OnOrientationListener;
import com.example.baidumap.api.LBSSearch;

public class MainActivity extends Activity implements OnClickListener {

	public MapView mMapView = null;
	public BaiduMap mBaiduMap = null;

	private Bitmap mBitmap;
	private Infos info;
	private ImageView image;
	private TextView zan;
	private TextView name;
	private TextView distance;

	private Context context;
	private Button btn_myLocation;
	private Button btn_overlays;
	private Button btn_uploaddata;
	private ImageView img_myLocation;

	// ��λ���
	public BDLocation currlocation = null; // �洢��ǰ��λ��Ϣ
	public LocationClient mLocationClient = null;
	public MyLocationListener listener = new MyLocationListener();
	private boolean isFirstIn = true;
	public double mLatitude;
	public double mLongitude;
	// ģʽ�л�
	private LocationMode mLocationMode;

	// �Զ��嶨λͼ��
	private BitmapDescriptor mIconLocation;
	private MyOrientationListener myOrientationListener;
	private float mCurrentX;

	// ���������
	private BitmapDescriptor bitmap;
	private Marker marker;
	private RelativeLayout mMarkerLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ���ر�����
		// ��ʹ��SDK�����֮ǰ��ʼ��context��Ϣ������ApplicationContext
		// ע��÷���Ҫ��setContentView����֮ǰʵ��
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);

		this.context = this;
		findViewById();

		initView();
		initLocation();
		initMarker();

		search();

		/**
		 * ��ǵ���¼�
		 */
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				Bundle extraInfo = marker.getExtraInfo();
				info = (Infos) extraInfo.getSerializable("info");
				image = (ImageView) mMarkerLayout
						.findViewById(R.id.id_info_image);
				distance = (TextView) mMarkerLayout
						.findViewById(R.id.id_info_distance);
				zan = (TextView) mMarkerLayout.findViewById(R.id.id_info_zan);
				name = (TextView) mMarkerLayout.findViewById(R.id.id_info_name);

				String string = info.getImgurl();
				Log.e("main", string);
				if (string.length() < 5) {
					image.setImageResource(R.drawable.a01);
				} else {
					new Thread(runnable).start();
				}
				distance.setText(info.getDistance());
				zan.setText(info.getZan() + "");
				name.setText(info.getName());

				InfoWindow infoWindow;
				TextView tv = new TextView(context);
				tv.setBackgroundResource(R.drawable.location_tips);
				tv.setPadding(30, 20, 30, 50);
				tv.setText(info.getName());
				tv.setTextColor(Color.parseColor("#fff5eb"));

				LatLng latLng = marker.getPosition();
				OnInfoWindowClickListener listener = null;
				listener = new OnInfoWindowClickListener() {
					@Override
					public void onInfoWindowClick() {
						mBaiduMap.hideInfoWindow();
					}
				};

				infoWindow = new InfoWindow(BitmapDescriptorFactory
						.fromView(tv), latLng, -47, listener);
				mBaiduMap.showInfoWindow(infoWindow);
				mMarkerLayout.setVisibility(View.VISIBLE);

				return true;
			}
		});

		/**
		 * ��ͼ����¼�
		 */
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				mMarkerLayout.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();
			}
		});

	}

	/**
	 * �ҿؼ�
	 */
	private void findViewById() {
		btn_overlays = (Button) findViewById(R.id.id_overlays);
		btn_overlays.setOnClickListener(this);

		btn_myLocation = (Button) findViewById(R.id.id_mylocation);
		btn_myLocation.setOnClickListener(this);

		btn_uploaddata = (Button) findViewById(R.id.id_upload_data);
		btn_uploaddata.setOnClickListener(this);
		
		mMarkerLayout = (RelativeLayout) findViewById(R.id.id_marker_layout);
	}

	/**
	 * ��ȡ����ͼƬ
	 * 
	 * @param imgurl
	 * @return
	 */
	protected Bitmap getBitmapFromUrl(String imgurl) {
		URL url;
		Bitmap bitmap = null;
		try {
			url = new URL(imgurl);
			InputStream is = url.openConnection().getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bitmap = BitmapFactory.decodeStream(bis);
			bis.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	// �̴߳�������ͼƬ����
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			mBitmap = getBitmapFromUrl(info.getImgurl());
			mHandler.sendMessage(msg);
		}
	};

	/**
	 * ��������
	 */
	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (msg.obj == null) {
					Log.e("bb", "��������Ϊ��");
				} else {
					String result = msg.obj.toString();
					try {
						JSONObject json = new JSONObject(result);
						parser(json);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				image.setImageBitmap(mBitmap);
				break;
			}
		}
	};

	/**
	 * ������������
	 * 
	 * @param json
	 */
	protected void parser(JSONObject json) {
		Infos infos = new Infos();
		List<Infos> list = infos.getInfos();
		try {
			JSONArray jsonArray = json.getJSONArray("contents");
			if (jsonArray != null && jsonArray.length() <= 0) {
				Toast.makeText(this, "û�з���Ҫ�������", Toast.LENGTH_SHORT).show();
			} else {
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = (JSONObject) jsonArray.opt(i);
					Infos info = new Infos();

					info.setName(jsonObject2.getString("title"));
					info.setAddr(jsonObject2.getString("address"));
					info.setDistance("����" + jsonObject2.getString("distance")
							+ "��");

					JSONArray locArray = jsonObject2.getJSONArray("location");
					double longitude = locArray.getDouble(0);
					double latitude = locArray.getDouble(1);
					info.setLatitude(latitude);
					info.setLongitude(longitude);

					float results[] = new float[1];
					if (currlocation != null) {
						Location.distanceBetween(currlocation.getLatitude(),
								currlocation.getLongitude(), latitude,
								longitude, results);
					}
					info.setDistance("����" + (int) results[0] + "��");

					info.setImgurl(jsonObject2.getString("image"));
					info.setZan(jsonObject2.getInt("zan"));

					list.add(info);
				}
			}
		} catch (Exception e) {
			Log.e("mainactivity", "parser����");
		}
	}

	/**
	 * �����Ƽ���
	 */
	private void search() {
		Infos infos = new Infos();
		infos.getInfos().clear();
		LBSSearch.request(getRequestParams(), mHandler);
	}

	/**
	 * �趨�Ƽ�������
	 * 
	 * @return
	 */
	private HashMap<String, String> getRequestParams() {
		HashMap<String, String> map = new HashMap<String, String>();

		try {
			map.put("radius", "2000");
			if (currlocation != null) {
				map.put("location", currlocation.getLongitude() + ","
						+ currlocation.getLatitude());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ��ʼ��������
	 */
	private void initMarker() {
		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);
	}

	/**
	 * ��Ӹ�����
	 * 
	 * @param infos
	 */
	private void addOverlays(List<Infos> infos) {
		mBaiduMap.clear();
		LatLng latlng = null;

		for (Infos info : infos) {
			latlng = new LatLng(info.getLatitude(), info.getLongitude());

			MarkerOptions options = new MarkerOptions().position(latlng)
					.icon(bitmap).zIndex(9).draggable(true);

			options.animateType(MarkerAnimateType.grow);
			marker = (Marker) (mBaiduMap.addOverlay(options));
			Bundle arg0 = new Bundle();
			arg0.putSerializable("info", info);
			marker.setExtraInfo(arg0);
		}
//		MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latlng);
//		mBaiduMap.animateMapStatus(msu);

	}

	/**
	 * ��ʼ����ͼ
	 */
	private void initView() {
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();// ��ʼ���ٶȵ�ͼ ���ܶԵ�ͼ����
		mMapView.showScaleControl(false);// ����ʾ��ͼ�ϱ�����  
        mMapView.showZoomControls(false);// ����ʾ��ͼ���ſؼ�����ť��������
		// ��ʼ�������ߵ�100��
		MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(17.0f);
		mBaiduMap.setMapStatus(msu);

	}

	/**
	 * ��ʼ����λ
	 */
	private void initLocation() {

		// ʵ�����ٶȵ�ͼ��λ������
		mLocationClient = new LocationClient(this);
		mLocationClient.registerLocationListener(listener);

		mLocationMode = LocationMode.NORMAL;
		// ��ͼ״̬��ť
		img_myLocation = (ImageView) findViewById(R.id.id_mylocation_img);
		img_myLocation.setImageResource(R.drawable.main_icon_location);

		// ���õ�ͼ����
		LocationClientOption option = new LocationClientOption();
		// ��������ϵ
		option.setCoorType("bd09ll");
		// ����λ����Ϣ
		option.setIsNeedAddress(true);
		// ���ø߾��ȶ�λ
		option.setOpenGps(true);
		// ����ˢ�¼��1��
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);

		// ������ͼ��λ
		if (!mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		mLocationClient.start();

		// ��ʼ��ͼ��
		mIconLocation = BitmapDescriptorFactory
				.fromResource(R.drawable.navi_map_gps_locked);

		myOrientationListener = new MyOrientationListener(context);
		myOrientationListener
				.setOnOrientationListener(new OnOrientationListener() {
					@Override
					public void onOrientationChanged(float x) {
						mCurrentX = x;
					}
				});

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

			// ��װ����
			MyLocationData data = new MyLocationData.Builder()//
					.accuracy(location.getRadius())//
					.direction(mCurrentX)//
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			// �󶨵�ͼ����
			mBaiduMap.setMyLocationData(data);

			// �����Զ���ͼ��
			MyLocationConfiguration config = new MyLocationConfiguration(
					mLocationMode, true, mIconLocation);
			mBaiduMap.setMyLocationConfigeration(config);

			// ���¾�γ��
			mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();

			// ��һ�δ�
			if (isFirstIn) {
				// ��ȡ�����
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
				mBaiduMap.animateMapStatus(update);
				isFirstIn = false;

				// Toast.makeText(context, location.getAddrStr(),
				// Toast.LENGTH_SHORT).show();
			}

		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// �ҵ�λ�ð�ť
		case R.id.id_mylocation:
			switch (mLocationMode) {
			case NORMAL:
				mLocationMode = LocationMode.FOLLOWING;
				img_myLocation.setImageResource(R.drawable.main_icon_follow);
				break;
			case FOLLOWING:
				mLocationMode = LocationMode.COMPASS;
				img_myLocation.setImageResource(R.drawable.main_icon_compass);
				mIconLocation = BitmapDescriptorFactory
						.fromResource(R.drawable.navi_map_gps_locked_compass);
				break;
			case COMPASS:
				mLocationMode = LocationMode.NORMAL;
				img_myLocation.setImageResource(R.drawable.main_icon_location);
				mIconLocation = BitmapDescriptorFactory
						.fromResource(R.drawable.navi_map_gps_locked);
				break;
			}
			break;
		// �����ﰴť
		case R.id.id_overlays:
			addOverlays(Infos.infos);
			search();
			break;
		case R.id.id_upload_data:
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, UpDataActivity.class);
			startActivity(intent);
			break;
		}
	}

	/**
	 * ��λ���ҵ�λ��
	 */
	private void centerToMyLocation() {
		LatLng ll = new LatLng(mLatitude, mLongitude);
		MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
		mBaiduMap.animateMapStatus(update);
	}

	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		mMapView = null;
		// ֹͣ��λ
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		// ֹͣ���򴫸���
		myOrientationListener.stop();
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
		// �������򴫸���
		myOrientationListener.start();

	}

	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
