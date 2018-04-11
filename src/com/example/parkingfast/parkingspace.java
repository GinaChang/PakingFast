package com.example.parkingfast;

import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.R.string;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class parkingspace extends Activity
{
	public static String s;	
	private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private TextView timer;
    private TextView parkingtime_txt;
    private int mCount = 1;
    private int tsec=0,csec=0,cmin=0,chour=0;  
    private NdefMessage[] mMessage;
    Tag mytag; 
    
    
	public void onCreate(Bundle savedInstanceState)
	{
	       super.onCreate(savedInstanceState);
	       requestWindowFeature(Window.FEATURE_NO_TITLE); 
	       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	       WindowManager.LayoutParams.FLAG_FULLSCREEN);
	       setContentView(R.layout.parkingspace);
	       findView(); //物件宣告
	       	  
	       Timer timer01 =new Timer();
	       //設定Timer(task為執行內容，0代表立刻開始,間格1秒執行一次)
	       timer01.schedule(task, 0,1000);
	       
	       
	       mAdapter = NfcAdapter.getDefaultAdapter(this);
	        // Create a generic PendingIntent that will be deliver to this activity. The NFC stack
	        // will fill in the intent with the details of the discovered tag before delivering to
	        // this activity.
	       
	        mPendingIntent = PendingIntent.getActivity(this, 0,
	                new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);	        
	        // Setup an intent filter for all MIME based dispatches
	        
	        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	        try 
	        {
	            ndef.addDataType("text/plain");
	        } 
	        catch (MalformedMimeTypeException e) 
	        {
	           throw new RuntimeException("fail", e);
	        }
	        mFilters = new IntentFilter[] 
	        {
	               ndef,
	        };

	        //Setup a tech list for all NfcF tags
	        mTechLists = new String[][] { new String[] { NfcF.class.getName() } };	    	       	      
	}
	
	private void findView() //物件宣告
	{
		parkingtime_txt = (TextView)findViewById(R.id.pakingtime);
		//parkingtime_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/showfone.ttf"));
		parkingtime_txt.setTextColor(Color.BLUE);
		//TextPaint parkingtime = parkingtime_txt.getPaint(); 
		//parkingtime.setFakeBoldText(true);
		
		timer = (TextView) findViewById(R.id.timer);
		timer.setTextColor(Color.BLUE);
	}
		      
    /*NFC receive程式開始*/
	private TimerTask task = new TimerTask()
	{
        public void run() 
        {
        	if(mCount%2==1)   //當mCount是第一次感應時，每秒tsec+1
            {        		
                tsec++;
                Message message = new Message();               
                //傳送訊息1
                message.what =1;
                handler.sendMessage(message);
            }
        	if(mCount==2) 
        	{
        		task.cancel();
        		Intent finishIntent = new Intent();
        		finishIntent.setClass(parkingspace.this, finish.class);
        		startActivity(finishIntent);	
			}
        	        		       	     	
        }      
    };
	
  //TimerTask無法直接改變元件因此要透過Handler來當橋樑
  	private Handler handler = new Handler()
  	{
  		 public  void  handleMessage(Message msg) 
  		 {
  			 super.handleMessage(msg);
  			 switch(msg.what)
  			 {
  			 	case 1:
  				csec=tsec%60;
  				cmin=tsec/60;
                s="";
                
  		 		if(chour<10)
  		 		{
  		 			s="0"+chour;  		 			   
  		 		}
  		 		else 
  		 		{
  		 			s=""+chour;
  		 		}
  		 		
                if(cmin <10)
                {
                    s=s+":0"+cmin;
                }
                else if(cmin==60)
                { 
                	cmin=0;
                	chour++;
                	s=s+":0"+cmin;
                }
                else
                {
                    s=s+":"+cmin;
                }                  
                if(csec < 10)
                {
                    s=s+":0"+csec;
                }
                else
                {
                    s=s+":"+csec;
                }                  
                //s字串為00:00:00格式
                timer.setText(s);
                break;
             }
          }
      };
	

    public void onResume() 
    {
        super.onResume();
        if (mAdapter != null) mAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    public void onNewIntent(Intent intent) 
    {    	
    	Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);    
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
        //Get the Text Encoding
        String textEncoding = ((payload[0] & 0200) == 0) ? "UTF-8" : "UTF-16";
        //Get the Language Code
        int languageCodeLength = payload[0] & 0077;
        //Get the Text
        String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        mCount++;   
        }
        catch(Exception e)
        {
        	
        }
    }

    public void onPause() 
    {
        super.onPause(); 
        if (mAdapter != null) mAdapter.disableForegroundDispatch(this);
    }	
    
}