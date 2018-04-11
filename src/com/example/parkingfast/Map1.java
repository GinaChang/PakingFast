package com.example.parkingfast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Text;
import com.google.android.gms.drive.internal.r;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.R.integer;
import android.R.string;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class Map1 extends FragmentActivity
{
	private LocationManager mLocationManager; // 宣告定位管理控制
	static ArrayList<Poi> Pois = new ArrayList<Poi>(); // 建立List，屬性為Poi物件
	private String bestProvider = LocationManager.NETWORK_PROVIDER; // 最佳資訊提供者
	static GoogleMap map;
	static double latitude, dest_latitude; // 緯度
	static double longitude, dest_longitude; // 經度
	static double achieve_time;
	static double widthPixels;
	static double heightPixels;
	static boolean run_one_time = true;
	ImageButton search_btn;
	TextView now_addr_txt;
	TextView addr_txt;
	TextView parking_space_txt;
	static String address;
	static LatLng Nowplace = new LatLng(latitude, longitude);
	static LatLng destination = new LatLng(dest_latitude, dest_longitude);
	static LatLng Another_place;
	static int markernum = 0;
	static int poi_num;
	Boolean getplace = false;
	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;
	private IntentFilter[] mFilters;
	private String[][] mTechLists;
	private NdefMessage[] mMessage;
	static String NFCtext;
	private boolean click_flag = false;
	private boolean location_loaded = false;
	public static String[] space;
	public static int strlen;
	public static boolean sendflag = false;
	private int emptyplace_num = 0;
	private String url = "http://print.php"; //請填入欲讀取的網頁內容

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.map1);

		// 建立物件，並放入List裡 (建立物件需帶入名稱、緯度、經度)
		if (run_one_time)
		{
			Pois.add(new Poi("圓環立體停車場", 23.715417, 120.565108, "雲林縣斗六市文化路6號",
					20));
			Pois.add(new Poi("雲林溪立體停車場", 23.707276, 120.542055, "雲林縣斗六市府前街32號",
					25));
			Pois.add(new Poi("斗六市公所停車場", 23.697372, 120.527115, "雲林縣斗六市府文路38號",
					30));
			Pois.add(new Poi("聯通停車場", 23.702376, 120.536325, "雲林縣斗六市南昌街105號",
					25));
			Pois.add(new Poi("家樂福停車場", 23.701666, 120.530591, "雲林縣雲林路二段297號", 0));
			Pois.add(new Poi("雲科大停車場", 23.695490, 120.532686, "雲林縣大學路三段123號", 0));
			run_one_time = false;
		}
		findView(); // 物件宣告
		checkInternet(); // 檢查網路狀態
		testLocationProvider(); // 檢查定位服務
		// locationServiceInitial();
		Setdistance(); // 計算目前位置與每個停車場的距離
		DistanceSort(Pois); // 照距離排序

		mAdapter = NfcAdapter.getDefaultAdapter(this);

		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

		IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		try
		{
			ndef.addDataType("text/plain");
		} catch (MalformedMimeTypeException e)
		{
			throw new RuntimeException("fail", e);
		}
		mFilters = new IntentFilter[] { ndef, };

		mTechLists = new String[][] { new String[] { NfcF.class.getName() } };

		// 自動搜尋
		search_btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (location_loaded)
				{
					SearchDialog();
					for (int i = 0; i < Pois.size(); i++)
					{
						Log.d("TAG", "地點 : " + Pois.get(i).getName()
								+ "  , 距離為 : "
								+ DistanceText(Pois.get(i).getDistance()));
					}
				}
			}
		});

		Timer timer01 = new Timer();
		// 設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
		timer01.schedule(task, 0, 1000);

	}

	/* 各個物件宣告 函式 */
	private void findView()
	{
		search_btn = (ImageButton) findViewById(R.id.search);

		now_addr_txt = (TextView) findViewById(R.id.now_addr);
		ImageSpan mImageSpan = new ImageSpan(Map1.this, R.drawable.marker);
		SpannableString mSpannableString = new SpannableString(" 目前位置： ");
		mSpannableString.setSpan(mImageSpan, 0, 1, 0);
		now_addr_txt.setText(mSpannableString);
		now_addr_txt.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/showfone.ttf"));
		now_addr_txt.setTextColor(Color.BLACK);
		TextPaint now_addr = now_addr_txt.getPaint();
		now_addr.setFakeBoldText(true);

		addr_txt = (TextView) findViewById(R.id.addr);
		addr_txt.setGravity(Gravity.CENTER);
		addr_txt.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/showfone.ttf"));
		addr_txt.setTextColor(Color.BLACK);
		TextPaint addr = addr_txt.getPaint();
		addr.setFakeBoldText(true);

		parking_space_txt = (TextView) findViewById(R.id.parking_place);
		parking_space_txt.setGravity(Gravity.CENTER);
		parking_space_txt.setTextColor(Color.RED);
		parking_space_txt.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/showfone.ttf"));
		TextPaint parking_space = parking_space_txt.getPaint();
		parking_space.setFakeBoldText(true);
	}

	private TimerTask task = new TimerTask()
	{
		public void run()
		{
			// TODO Auto-generated method stub
			Message msg = new Message();
			Bundle data = new Bundle();
			msg.setData(data);

			Log.i("runnable", "while");
			try
			{
				Log.i("runnable11", "while");
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost method = new HttpPost(url);// 連線到 url網址
				HttpResponse response = httpclient.execute(method);
				HttpEntity entity = response.getEntity();

				if (entity != null)
				{
					data.putString("key", EntityUtils.toString(entity));// 如果成功將網頁內容存入key
					handler_Success.sendMessage(msg);
				} else
				{
					data.putString("key", "無資料");
					handler_Nodata.sendMessage(msg);
				}
				Thread.sleep(1000);
			} catch (Exception e)
			{
				data.putString("key", "連線失敗");
				handler_Error.sendMessage(msg);

			}
		}
	};

	/* 檢查網路狀態 */
	private void checkInternet()
	{
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		// Log.i("1111Internet", ""+mNetworkInfo);
		if (mNetworkInfo == null)
		{
			// Log.i("2222Internet", "Internet");
			new AlertDialog.Builder(this)
					.setTitle("請檢查網路狀態")
					.setMessage("關閉程式")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int whichButton)
								{
									System.exit(0);
								}
							}).show();
		}

	}

	/* 自動搜尋後跳出AlertDialog視窗 */
	private void SearchDialog()
	{
		final String[] ListStr = {
				Pois.get(0).getName() + "\r\n" + Pois.get(0).getPay() + "元/小時"
						+ "\r\n" + DistanceText(Pois.get(0).getDistance())
						+ "\r\n" + cal_time(Pois.get(0).getDistance()) + "\r\n"
						+ "剩餘" + String.valueOf(emptyplace_num) + "個停車位",
				Pois.get(1).getName() + "\r\n" + Pois.get(1).getPay() + "元/小時"
						+ "\r\n" + DistanceText(Pois.get(1).getDistance())
						+ "\r\n" + cal_time(Pois.get(1).getDistance()) + "\r\n"
						+ "剩餘" + String.valueOf(emptyplace_num) + "個停車位",
				Pois.get(2).getName() + "\r\n" + Pois.get(2).getPay() + "元/小時"
						+ "\r\n" + DistanceText(Pois.get(2).getDistance())
						+ "\r\n" + cal_time(Pois.get(2).getDistance()) + "\r\n"
						+ "剩餘" + String.valueOf(emptyplace_num) + "個停車位" };
		AlertDialog.Builder MyListAlertDialog = new AlertDialog.Builder(this);
		MyListAlertDialog.setTitle("最近的三個停車場");
		// 建立ListClick事件
		DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Toast.makeText(Map1.this, ListStr[which],// 顯示所點選的選項
						Toast.LENGTH_LONG).show();
				dest_latitude = Pois.get(which).getLatitude();
				dest_longitude = Pois.get(which).getLongitude();
				destination = new LatLng(dest_latitude, dest_longitude);
				// mapmarker標記
				if (markernum > 1)
				{
					map.clear();
					map.addMarker(new MarkerOptions().position(Nowplace)
							.title("現在位置").snippet(address));
					markernum--;
				}
				map.addMarker(new MarkerOptions()
						.position(destination)
						.title(Pois.get(which).getName())
						.snippet(Pois.get(which).getAddress())
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				markernum++;

				parking_space_txt.setText("您選擇了『" + Pois.get(which).getName()
						+ "』");
				click_flag = true;
				poi_num = which;
				// 導航設定
				String url = getDirectionsUrl(Nowplace, destination);
				DownloadTask downloadTask = new DownloadTask();
				downloadTask.execute(url);
			}
		};
		// 建立按下取消什麼事情都不做的事件
		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// 什麼事情都不做
			}
		};
		MyListAlertDialog.setItems(ListStr, ListClick);
		MyListAlertDialog.setNeutralButton("取消", OkClick);
		MyListAlertDialog.show();
	}

	/* 起點與終點 導航 */
	private String getDirectionsUrl(LatLng origin, LatLng dest)
	{
		// Origin of route
		String str_origin = "origin=" + origin.latitude + ","
				+ origin.longitude;
		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
		// Sensor enabled
		String sensor = "sensor=false";
		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + sensor;
		// Output format
		String output = "json";
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"
				+ output + "?" + parameters;
		return url;
	}

	/* 從URL下載JSON資料的方法 */
	private String downloadUrl(String strUrl) throws IOException
	{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try
		{
			URL url = new URL(strUrl);
			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();
			// Connecting to url
			urlConnection.connect();
			// Reading data from url
			iStream = urlConnection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));
			StringBuffer sb = new StringBuffer();
			String line = "";
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}
			data = sb.toString();
			br.close();
		} catch (Exception e)
		{
			Log.d("Exception while downloading url", e.toString());
		} finally
		{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	// Fetches data from url passed
	private class DownloadTask extends AsyncTask<String, Void, String>
	{

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url)
		{
			// For storing data from web service
			String data = "";
			try
			{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			} catch (Exception e)
			{
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		protected void onPostExecute(String result)
		{
			super.onPostExecute(result);
			ParserTask parserTask = new ParserTask();
			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	/* 解析JSON格式 */
	private class ParserTask extends
			AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
	{
		// Parsing the data in non-ui thread
		@Override
		protected List<List<HashMap<String, String>>> doInBackground(
				String... jsonData)
		{
			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;
			try
			{
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();
				// Starts parsing data
				routes = parser.parse(jObject);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return routes;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(List<List<HashMap<String, String>>> result)
		{
			ArrayList<LatLng> points = null;
			PolylineOptions lineOptions = null;
			MarkerOptions markerOptions = new MarkerOptions();
			// Traversing through all the routes
			for (int i = 0; i < result.size(); i++)
			{
				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				// Fetching i-th route
				List<HashMap<String, String>> path = result.get(i);
				// Fetching all the points in i-th route
				for (int j = 0; j < path.size(); j++)
				{
					HashMap<String, String> point = path.get(j);
					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					LatLng position = new LatLng(lat, lng);
					points.add(position);
				}
				// Adding all the points in the route to LineOptions
				lineOptions.addAll(points);
				lineOptions.width(5); // 導航路徑寬度
				lineOptions.color(Color.BLUE); // 導航路徑顏色
			}
			// Drawing polyline in the Google Map for the i-th route
			map.addPolyline(lineOptions);
		}
	}

	public void cal_empty_place(String val)
	{
		int b = 0;
		String a;
		strlen = val.length();
		for (int j = 0; j < val.length(); j++) // 將每個字元丟進space陣列
		{
			a = val.substring(j, j + 1);
			if ("0".equals(a))
			{
				b++;
			}
		}
		emptyplace_num = b;
	}

	/* PHP資料讀取 */
	Handler handler_Success = new Handler()
	{
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String val = data.getString("key");// 取出key中的字串存入val

			val = val.trim(); // 取得的字串去掉前尾空白
			cal_empty_place(val);
			Log.i("cal_empty_place", "1");

			// Toast.makeText(getApplicationContext(), val,
			// Toast.LENGTH_LONG).show();
		}
	};

	Handler handler_Error = new Handler()
	{
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String val = data.getString("key");
			Toast.makeText(getApplicationContext(), val, Toast.LENGTH_LONG)
					.show();
		}
	};

	Handler handler_Nodata = new Handler()
	{
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String val = data.getString("key");
			Toast.makeText(getApplicationContext(), val, Toast.LENGTH_LONG)
					.show();
		}
	};

	/* 測試定位服務 */
	private void testLocationProvider()
	{
		mLocationManager = (LocationManager) (this
				.getSystemService(Context.LOCATION_SERVICE));
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 10000, 5, LocationChange);//網路定位，每10000ms或5公尺更新一次

		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| mLocationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{

		} 
		else
		{			
			Toast.makeText(this, "請開啟定位服務", Toast.LENGTH_LONG).show();			
		}
		

		// 直接顯示之前紀錄的經緯度點
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.addMarker(new MarkerOptions().position(Nowplace).title("現在位置")
				.snippet(address));
		markernum++;
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Nowplace, 16));
		addr_txt.setText(address);
	}

	/* 定位服務初始化 */
	private void locationServiceInitial()
	{
		Criteria criteria = new Criteria(); // 資訊提供者選取標準
		bestProvider = mLocationManager.getBestProvider(criteria, true); // 選擇精準度最高的提供者
		Location location = mLocationManager.getLastKnownLocation(bestProvider);
		getLocation(location);
	}

	/* 定位目前位置 */
	private void getLocation(Location location)
	{
		if (location != null)
		{
			longitude = location.getLongitude(); // 取得經度
			latitude = location.getLatitude(); // 取得緯度

			Nowplace = new LatLng(latitude, longitude); // 將經緯度帶入地圖記錄點
			address = (String) getAddressByLocation(location); // address 存取
																// 目前地址
			address = address.substring(5);
			// 載入文字顯示位址
			addr_txt.setText(address);
			location_loaded = true;

			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map.addMarker(new MarkerOptions().position(Nowplace).title("現在位置")
					.snippet(address));
			markernum++;
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(Nowplace, 16));

			if (markernum > 1)
			{
				map.clear();
				map.addMarker(new MarkerOptions().position(Nowplace)
						.title("現在位置").snippet(address));
				markernum--;
			}
		} else
		{
			Toast.makeText(this, "無法定位座標，請開啟網路或GPS", Toast.LENGTH_LONG).show();
		}
	}

	/* 依地點解析地址 */
	private CharSequence getAddressByLocation(Location location)
	{
		String returnAddress = "";
		try
		{
			if (location != null)
			{
				Double longitude = location.getLongitude(); // 取得經度
				Double latitude = location.getLatitude(); // 取得緯度

				Geocoder gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE); // 地區:台灣
				List<Address> lstAddress = gc.getFromLocation(latitude,
						longitude, 1); // 經緯度轉地址，取第一個後放進lstAddress
				returnAddress = lstAddress.get(0).getAddressLine(0); // 回傳lstAddress裡面的第一筆資料
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return returnAddress;
	}

	/* 更新定位Listener */
	public LocationListener LocationChange = new LocationListener()
	{
		public void onLocationChanged(Location mLocation)
		{
			if (!click_flag)
			{
				getLocation(mLocation);
				Setdistance();
				DistanceSort(Pois); // 依照距離遠近進行List重新排列
				// for迴圈，印出景點店家名稱及距離，並依照距離由近至遠排列
				// 第一筆為最近的景點店家，最後一筆為最遠的景點店家
				for (int i = 0; i < Pois.size(); i++)
				{
					Log.d("TAG", "地點 : " + Pois.get(i).getName() + "  , 距離為 : "
							+ DistanceText(Pois.get(i).getDistance()));
				}
			}
		}

		public void onProviderDisabled(String provider)
		{
		}

		public void onProviderEnabled(String provider)
		{
		}

		public void onStatusChanged(String provider, int status, Bundle extras)
		{
		}
	};

	public void putvalue(String val)
	{
		space = new String[val.length()]; // 動態配置長度，陣列長度符合取得字串長度
		strlen = val.length();
		for (int j = 0; j < val.length(); j++) // 將每個字元丟進space陣列
		{
			space[j] = val.substring(j, j + 1);
		}
	}

	public void onNewIntent(Intent intent)
	{
		Parcelable[] rawMsgs = intent
				.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawMsgs != null)
		{
			mMessage = new NdefMessage[rawMsgs.length];
			for (int i = 0; i < rawMsgs.length; i++)
			{
				mMessage[i] = (NdefMessage) rawMsgs[i];
			}
		}
		NdefMessage msg = mMessage[0];
		try
		{
			byte[] payload = msg.getRecords()[0].getPayload();
			// Get the Text Encoding
			String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8"
					: "UTF-16";
			// Get the Language Code
			int languageCodeLength = payload[0] & 0077;
			// Get the Text
			NFCtext = new String(payload, languageCodeLength + 1,
					payload.length - languageCodeLength - 1, textEncoding);
			NFCtext = NFCtext.trim(); // 取得的字串去掉前尾空白
			putvalue(NFCtext);
			sendflag = true;

			task.cancel();
			Intent helloIntent = new Intent();
			helloIntent.setClass(Map1.this, parkingspace.class);
			startActivity(helloIntent);
		} catch (Exception e)
		{

		}
	}

	public void onPause()
	{
		super.onPause();
		if (mAdapter != null)
			mAdapter.disableForegroundDispatch(this);
	}

	protected void onDestroy()
	{
		super.onDestroy();
		mLocationManager.removeUpdates(LocationChange); // 程式結束時停止定位更新
	}

	protected void onRestart()
	{
		super.onRestart();
	}

	protected void onResume()
	{
		super.onResume();
		if (mAdapter != null)
			mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
					mTechLists);
	}

	/* 將距離帶入至Poi物件 */
	protected void Setdistance()
	{
		for (Poi mPoi : Pois)
		{
			// for迴圈將距離帶入，判斷距離為Distance function
			// 需帶入使用者取得定位後的緯度、經度、景點店家緯度、經度。
			mPoi.setDistance(Distance(Nowplace.latitude, Nowplace.longitude,
					mPoi.getLatitude(), mPoi.getLongitude()));
		}
	}

	/* 帶入距離回傳字串 (距離小於一公里以公尺呈現，距離大於一公里以公里呈現並取小數點兩位) */
	private String DistanceText(double distance)
	{
		cal_time(distance); // 計算行駛大約時間
		if (distance < 1000)
		{
			return String.valueOf((int) distance) + "m";
		} else
		{
			return new DecimalFormat("#.00").format(distance / 1000) + "km";
		}

	}

	/* 計算大約行駛時間 */
	private String cal_time(double distance)
	{
		achieve_time = distance / 500;
		if (achieve_time >= 1) // 大於1分鐘
		{
			return String.valueOf(Math.round(achieve_time)) + "分鐘";
		} 
		else
		// 小於1分鐘換算成秒
		{
			return String.valueOf(Math.round(achieve_time * 60)) + "秒";
		}
		// TODO Auto-generated method stub

	}

	/* List排序，依照距離由近開始排列，第一筆為最近，最後一筆為最遠 */
	private void DistanceSort(ArrayList<Poi> poi)
	{
		Collections.sort(poi, new Comparator<Poi>()
		{
			public int compare(Poi poi1, Poi poi2)
			{
				return poi1.getDistance() < poi2.getDistance() ? -1 : 1;
			}
		});
	}

	/* 帶入使用者及景點店家經緯度可計算出距離 */
	public double Distance(double longitude1, double latitude1,
			double longitude2, double latitude2)
	{
		double radLatitude1 = latitude1 * Math.PI / 180;
		double radLatitude2 = latitude2 * Math.PI / 180;
		double l = radLatitude1 - radLatitude2;
		double p = longitude1 * Math.PI / 180 - longitude2 * Math.PI / 180;
		double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(l / 2), 2)
				+ Math.cos(radLatitude1) * Math.cos(radLatitude2)
				* Math.pow(Math.sin(p / 2), 2)));
		distance = distance * 6378137.0;
		distance = Math.round(distance * 10000) / 10000;
		return distance;
	}

}
