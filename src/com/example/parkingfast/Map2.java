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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class Map2 extends FragmentActivity
{
	private LocationManager mLocationManager; // �ŧi�w��޲z����
	private ArrayList<Poi> Pois = new ArrayList<Poi>(); // �إ�List�A�ݩʬ�Poi����
	private String bestProvider = LocationManager.NETWORK_PROVIDER; // �̨θ�T���Ѫ�
	static GoogleMap map;
	static double latitude; // �n��
	static double longitude; // �g��
	static double achieve_time;
	static double widthPixels;
	static double heightPixels;
	private boolean location_loaded = false;

	EditText goal_text;
	private TextView now_txt;
	private TextView addr_txt;
	// ImageButton spacepage_btn;
	// Button search_btn ;
	ImageButton another_btn;

	static String address;
	static LatLng Nowplace = new LatLng(latitude, longitude);
	static LatLng Another_place;
	static int markernum = 0;
	private boolean click_flag = false;

	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.map2);

		findView(); // ����ŧi
		checkInternet(); // �ˬd�������A
		testLocationProvider(); // �ˬd�w��A��

		// ��L�j�M
		another_btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (location_loaded)
				{
					if (goal_text.getText().toString().length() > 0) // �j�M�ؤ�����r�A���s�~������
					{
						Another_Search();
					}
				}
			}
		});

	}

	/* �U�Ӫ���ŧi �禡 */
	private void findView()
	{
		goal_text = (EditText) findViewById(R.id.goal_text);
		// spacepage_btn = (ImageButton) findViewById(R.id.spacepagebutton);
		// search_btn = (Button) findViewById(R.id.search);
		another_btn = (ImageButton) findViewById(R.id.another);

		now_txt = (TextView) findViewById(R.id.now2);
		ImageSpan mImageSpan = new ImageSpan(Map2.this, R.drawable.marker);
		SpannableString mSpannableString = new SpannableString(" �ثe��m�G ");
		mSpannableString.setSpan(mImageSpan, 0, 1, 0);
		now_txt.setText(mSpannableString);
		now_txt.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/showfone.ttf"));
		now_txt.setTextColor(Color.BLACK);
		TextPaint now_addr = now_txt.getPaint();
		now_addr.setFakeBoldText(true);

		addr_txt = (TextView) findViewById(R.id.addr2);
		addr_txt.setGravity(Gravity.CENTER);
		addr_txt.setTypeface(Typeface.createFromAsset(getAssets(),
				"fonts/showfone.ttf"));
		addr_txt.setTextColor(Color.BLACK);
		TextPaint addr = addr_txt.getPaint();
		addr.setFakeBoldText(true);
	}

	/* ��L�j�M �禡 */
	private void Another_Search()
	{
		Geocoder geocoder = new Geocoder(this);
		List<Address> addresses = null;
		Address geo_address = null;
		try
		{
			addresses = geocoder.getFromLocationName(goal_text.getText()
					.toString(), 1);
		} catch (IOException e)
		{
			Log.e("AddrToGP", e.toString());
		}
		if (addresses == null || addresses.isEmpty())
		{
			Toast.makeText(this, "addressNotFound", Toast.LENGTH_SHORT).show();
		} else
		{
			geo_address = addresses.get(0);
			double geo_Latitude = geo_address.getLatitude();
			double geo_Longitude = geo_address.getLongitude();
			Another_place = new LatLng(geo_Latitude, geo_Longitude);
			Toast.makeText(
					Map2.this,
					"�Z����"
							+ DistanceText(Distance(latitude, longitude,
									geo_Latitude, geo_Longitude))
							+ "\n"
							+ "�ɶ���"
							+ cal_time(Distance(latitude, longitude,
									geo_Latitude, geo_Longitude)),// ��ܩ��I�諸�ﶵ
					Toast.LENGTH_LONG).show();
		}
		// mapmarker�аO
		if (markernum > 1)
		{
			map.clear();
			map.addMarker(new MarkerOptions().position(Nowplace).title("�{�b��m")
					.snippet(address));
			markernum--;
		}
		map.addMarker(new MarkerOptions()
				.position(Another_place)
				.title(goal_text.getText().toString())
				.snippet(geo_address.getAddressLine(0))
				.icon(BitmapDescriptorFactory
						.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
		markernum++;

		// �ɯ�]�w
		String url = getDirectionsUrl(Nowplace, Another_place);
		DownloadTask downloadTask = new DownloadTask();
		downloadTask.execute(url);
	}

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

	/* ���թw��A�� */
	private void testLocationProvider()
	{
		mLocationManager = (LocationManager) (this
				.getSystemService(Context.LOCATION_SERVICE));
		// mLocationManager.requestLocationUpdates
		// (LocationManager.GPS_PROVIDER,10000,5,LocationChange); //�C10000ms��s�@��
		mLocationManager.requestLocationUpdates(
				LocationManager.NETWORK_PROVIDER, 10000, 5, LocationChange);
		if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
				|| mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
		{
			// �p�GGPS�κ����w��}�ҡA�I�slocationServiceInitial()��s��m
			// locationServiceInitial();
			Log.i("123", "123");
		} 
		else
		{
			// locationServiceInitial();
			Toast.makeText(this, "�ж}�ҩw��A��", Toast.LENGTH_LONG).show();
			// startActivity(new
			// Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); //�}�ҳ]�w����
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
		} else
		// �p��1�������⦨��
		{
			return String.valueOf(Math.round(achieve_time * 60)) + "��";
		}
		// TODO Auto-generated method stub

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
