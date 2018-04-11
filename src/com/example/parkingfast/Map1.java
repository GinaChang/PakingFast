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
	private LocationManager mLocationManager; // �ŧi�w��޲z����
	static ArrayList<Poi> Pois = new ArrayList<Poi>(); // �إ�List�A�ݩʬ�Poi����
	private String bestProvider = LocationManager.NETWORK_PROVIDER; // �̨θ�T���Ѫ�
	static GoogleMap map;
	static double latitude, dest_latitude; // �n��
	static double longitude, dest_longitude; // �g��
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
	private String url = "http://140.125.32.238/print.php";

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.map1);

		// �إߪ���A�é�JList�� (�إߪ���ݱa�J�W�١B�n�סB�g��)
		if (run_one_time)
		{
			Pois.add(new Poi("�������鰱����", 23.715417, 120.565108, "���L���椻����Ƹ�6��",
					20));
			Pois.add(new Poi("���L�˥��鰱����", 23.707276, 120.542055, "���L���椻�����e��32��",
					25));
			Pois.add(new Poi("�椻�����Ұ�����", 23.697372, 120.527115, "���L���椻�������38��",
					30));
			Pois.add(new Poi("�p�q������", 23.702376, 120.536325, "���L���椻���n����105��",
					25));
			Pois.add(new Poi("�a�ְֺ�����", 23.701666, 120.530591, "���L�����L���G�q297��", 0));
			Pois.add(new Poi("����j������", 23.695490, 120.532686, "���L���j�Ǹ��T�q123��", 0));
			run_one_time = false;
		}
		findView(); // ����ŧi
		checkInternet(); // �ˬd�������A
		testLocationProvider(); // �ˬd�w��A��
		// locationServiceInitial();
		Setdistance(); // �p��ثe��m�P�C�Ӱ��������Z��
		DistanceSort(Pois); // �ӶZ���Ƨ�

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

		// �۰ʷj�M
		search_btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (location_loaded)
				{
					SearchDialog();
					for (int i = 0; i < Pois.size(); i++)
					{
						Log.d("TAG", "�a�I : " + Pois.get(i).getName()
								+ "  , �Z���� : "
								+ DistanceText(Pois.get(i).getDistance()));
					}
				}
			}
		});

		Timer timer01 = new Timer();
		// �]�wTimer(task�����椺�e�A0�N��ߨ�}�l,����1�����@��)
		timer01.schedule(task, 0, 1000);

	}

	/* �U�Ӫ���ŧi �禡 */
	private void findView()
	{
		search_btn = (ImageButton) findViewById(R.id.search);

		now_addr_txt = (TextView) findViewById(R.id.now_addr);
		ImageSpan mImageSpan = new ImageSpan(Map1.this, R.drawable.marker);
		SpannableString mSpannableString = new SpannableString(" �ثe��m�G ");
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
				HttpPost method = new HttpPost(url);// �s�u�� url���}
				HttpResponse response = httpclient.execute(method);
				HttpEntity entity = response.getEntity();

				if (entity != null)
				{
					data.putString("key", EntityUtils.toString(entity));// �p�G���\�N�������e�s�Jkey
					handler_Success.sendMessage(msg);
				} else
				{
					data.putString("key", "�L���");
					handler_Nodata.sendMessage(msg);
				}
				Thread.sleep(1000);
			} catch (Exception e)
			{
				data.putString("key", "�s�u����");
				handler_Error.sendMessage(msg);

			}
		}
	};

	/* �ˬd�������A */
	private void checkInternet()
	{
		ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
		// Log.i("1111Internet", ""+mNetworkInfo);
		if (mNetworkInfo == null)
		{
			// Log.i("2222Internet", "Internet");
			new AlertDialog.Builder(this)
					.setTitle("���ˬd�������A")
					.setMessage("�����{��")
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

	/* �۰ʷj�M����XAlertDialog���� */
	private void SearchDialog()
	{
		final String[] ListStr = {
				Pois.get(0).getName() + "\r\n" + Pois.get(0).getPay() + "��/�p��"
						+ "\r\n" + DistanceText(Pois.get(0).getDistance())
						+ "\r\n" + cal_time(Pois.get(0).getDistance()) + "\r\n"
						+ "�Ѿl" + String.valueOf(emptyplace_num) + "�Ӱ�����",
				Pois.get(1).getName() + "\r\n" + Pois.get(1).getPay() + "��/�p��"
						+ "\r\n" + DistanceText(Pois.get(1).getDistance())
						+ "\r\n" + cal_time(Pois.get(1).getDistance()) + "\r\n"
						+ "�Ѿl" + String.valueOf(emptyplace_num) + "�Ӱ�����",
				Pois.get(2).getName() + "\r\n" + Pois.get(2).getPay() + "��/�p��"
						+ "\r\n" + DistanceText(Pois.get(2).getDistance())
						+ "\r\n" + cal_time(Pois.get(2).getDistance()) + "\r\n"
						+ "�Ѿl" + String.valueOf(emptyplace_num) + "�Ӱ�����" };
		AlertDialog.Builder MyListAlertDialog = new AlertDialog.Builder(this);
		MyListAlertDialog.setTitle("�̪񪺤T�Ӱ�����");
		// �إ�ListClick�ƥ�
		DialogInterface.OnClickListener ListClick = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				Toast.makeText(Map1.this, ListStr[which],// ��ܩ��I�諸�ﶵ
						Toast.LENGTH_LONG).show();
				dest_latitude = Pois.get(which).getLatitude();
				dest_longitude = Pois.get(which).getLongitude();
				destination = new LatLng(dest_latitude, dest_longitude);
				// mapmarker�аO
				if (markernum > 1)
				{
					map.clear();
					map.addMarker(new MarkerOptions().position(Nowplace)
							.title("�{�b��m").snippet(address));
					markernum--;
				}
				map.addMarker(new MarkerOptions()
						.position(destination)
						.title(Pois.get(which).getName())
						.snippet(Pois.get(which).getAddress())
						.icon(BitmapDescriptorFactory
								.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
				markernum++;

				parking_space_txt.setText("�z��ܤF�y" + Pois.get(which).getName()
						+ "�z");
				click_flag = true;
				poi_num = which;
				// �ɯ�]�w
				String url = getDirectionsUrl(Nowplace, destination);
				DownloadTask downloadTask = new DownloadTask();
				downloadTask.execute(url);
			}
		};
		// �إ߫��U��������Ʊ����������ƥ�
		DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int which)
			{
				// ����Ʊ�������
			}
		};
		MyListAlertDialog.setItems(ListStr, ListClick);
		MyListAlertDialog.setNeutralButton("����", OkClick);
		MyListAlertDialog.show();
	}

	/* �_�I�P���I �ɯ� */
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

	/* �qURL�U��JSON��ƪ���k */
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

	/* �ѪRJSON�榡 */
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
				lineOptions.width(5); // �ɯ���|�e��
				lineOptions.color(Color.BLUE); // �ɯ���|�C��
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
		for (int j = 0; j < val.length(); j++) // �N�C�Ӧr����ispace�}�C
		{
			a = val.substring(j, j + 1);
			if ("0".equals(a))
			{
				b++;
			}
		}
		emptyplace_num = b;
	}

	/* PHP���Ū�� */
	Handler handler_Success = new Handler()
	{
		public void handleMessage(Message msg)
		{
			super.handleMessage(msg);
			Bundle data = msg.getData();
			String val = data.getString("key");// ���Xkey�����r��s�Jval

			val = val.trim(); // ���o���r��h���e���ť�
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

	/* ���թw��A�� */
	private void testLocationProvider()
	{
		mLocationManager = (LocationManager) (this
				.getSystemService(Context.LOCATION_SERVICE));
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 10000, 5, LocationChange);//�����w��A�C10000ms��5���ا�s�@��

		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| mLocationManager
						.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{

		} 
		else
		{			
			Toast.makeText(this, "�ж}�ҩw��A��", Toast.LENGTH_LONG).show();			
		}
		

		// ������ܤ��e�������g�n���I
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		map.addMarker(new MarkerOptions().position(Nowplace).title("�{�b��m")
				.snippet(address));
		markernum++;
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(Nowplace, 16));
		addr_txt.setText(address);
	}

	/* �w��A�Ȫ�l�� */
	private void locationServiceInitial()
	{
		Criteria criteria = new Criteria(); // ��T���Ѫ̿���з�
		bestProvider = mLocationManager.getBestProvider(criteria, true); // ��ܺ�ǫ׳̰������Ѫ�
		Location location = mLocationManager.getLastKnownLocation(bestProvider);
		getLocation(location);
	}

	/* �w��ثe��m */
	private void getLocation(Location location)
	{
		if (location != null)
		{
			longitude = location.getLongitude(); // ���o�g��
			latitude = location.getLatitude(); // ���o�n��

			Nowplace = new LatLng(latitude, longitude); // �N�g�n�ױa�J�a�ϰO���I
			address = (String) getAddressByLocation(location); // address �s��
																// �ثe�a�}
			address = address.substring(5);
			// ���J��r��ܦ�}
			addr_txt.setText(address);
			location_loaded = true;

			map = ((MapFragment) getFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			map.addMarker(new MarkerOptions().position(Nowplace).title("�{�b��m")
					.snippet(address));
			markernum++;
			map.moveCamera(CameraUpdateFactory.newLatLngZoom(Nowplace, 16));

			if (markernum > 1)
			{
				map.clear();
				map.addMarker(new MarkerOptions().position(Nowplace)
						.title("�{�b��m").snippet(address));
				markernum--;
			}
		} else
		{
			Toast.makeText(this, "�L�k�w��y�СA�ж}�Һ�����GPS", Toast.LENGTH_LONG).show();
		}
	}

	/* �̦a�I�ѪR�a�} */
	private CharSequence getAddressByLocation(Location location)
	{
		String returnAddress = "";
		try
		{
			if (location != null)
			{
				Double longitude = location.getLongitude(); // ���o�g��
				Double latitude = location.getLatitude(); // ���o�n��

				Geocoder gc = new Geocoder(this, Locale.TRADITIONAL_CHINESE); // �a��:�x�W
				List<Address> lstAddress = gc.getFromLocation(latitude,
						longitude, 1); // �g�n����a�}�A���Ĥ@�ӫ��ilstAddress
				returnAddress = lstAddress.get(0).getAddressLine(0); // �^��lstAddress�̭����Ĥ@�����
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return returnAddress;
	}

	/* ��s�w��Listener */
	public LocationListener LocationChange = new LocationListener()
	{
		public void onLocationChanged(Location mLocation)
		{
			if (!click_flag)
			{
				getLocation(mLocation);
				Setdistance();
				DistanceSort(Pois); // �̷ӶZ������i��List���s�ƦC
				// for�j��A�L�X���I���a�W�٤ζZ���A�è̷ӶZ���Ѫ�ܻ��ƦC
				// �Ĥ@�����̪񪺴��I���a�A�̫�@�����̻������I���a
				for (int i = 0; i < Pois.size(); i++)
				{
					Log.d("TAG", "�a�I : " + Pois.get(i).getName() + "  , �Z���� : "
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
		space = new String[val.length()]; // �ʺA�t�m���סA�}�C���ײŦX���o�r�����
		strlen = val.length();
		for (int j = 0; j < val.length(); j++) // �N�C�Ӧr����ispace�}�C
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
			NFCtext = NFCtext.trim(); // ���o���r��h���e���ť�
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
		mLocationManager.removeUpdates(LocationChange); // �{�������ɰ���w���s
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

	/* �N�Z���a�J��Poi���� */
	protected void Setdistance()
	{
		for (Poi mPoi : Pois)
		{
			// for�j��N�Z���a�J�A�P�_�Z����Distance function
			// �ݱa�J�ϥΪ̨��o�w��᪺�n�סB�g�סB���I���a�n�סB�g�סC
			mPoi.setDistance(Distance(Nowplace.latitude, Nowplace.longitude,
					mPoi.getLatitude(), mPoi.getLongitude()));
		}
	}

	/* �a�J�Z���^�Ǧr�� (�Z���p��@�����H���اe�{�A�Z���j��@�����H�����e�{�è��p���I���) */
	private String DistanceText(double distance)
	{
		cal_time(distance); // �p���p�j���ɶ�
		if (distance < 1000)
		{
			return String.valueOf((int) distance) + "m";
		} else
		{
			return new DecimalFormat("#.00").format(distance / 1000) + "km";
		}

	}

	/* �p��j����p�ɶ� */
	private String cal_time(double distance)
	{
		achieve_time = distance / 500;
		if (achieve_time >= 1) // �j��1����
		{
			return String.valueOf(Math.round(achieve_time)) + "����";
		} 
		else
		// �p��1�������⦨��
		{
			return String.valueOf(Math.round(achieve_time * 60)) + "��";
		}
		// TODO Auto-generated method stub

	}

	/* List�ƧǡA�̷ӶZ���Ѫ�}�l�ƦC�A�Ĥ@�����̪�A�̫�@�����̻� */
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

	/* �a�J�ϥΪ̤δ��I���a�g�n�ץi�p��X�Z�� */
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
