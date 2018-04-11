package com.example.parkingfast;

import android.R.string;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class finish extends Activity
{
	private TextView finish_time_txt;
	private TextView finish_dollar_txt;
	private TextView payway_txt;
	private int hour;
	private int dollar;   
    
	public void onCreate(Bundle savedInstanceState)
	{
	       super.onCreate(savedInstanceState);
	       requestWindowFeature(Window.FEATURE_NO_TITLE); 
	       getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	       WindowManager.LayoutParams.FLAG_FULLSCREEN);
	       setContentView(R.layout.finish);
	       	    
	       
	       hour= Integer.valueOf(parkingspace.s.substring(0, 2)).intValue();
		   dollar=Integer.valueOf(Map1.Pois.get(Map1.poi_num).getPay()).intValue();
		   if(hour==0)
		   {
			   dollar=dollar*(hour+1);
		   }
		   else 
		   {
			   dollar=dollar*(hour);
			   
		   }
	       findView(); //物件宣告
	}
	
	private void findView() //物件宣告
	{
		finish_time_txt=(TextView)findViewById(R.id.finish_time);
		finish_time_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/showfone.ttf"));
		finish_time_txt.setTextColor(Color.BLUE);
		finish_time_txt.setText("總停車時間為 "+parkingspace.s);
		TextPaint finish_time = finish_time_txt.getPaint(); 
		finish_time.setFakeBoldText(true);
		
		
		finish_dollar_txt=(TextView)findViewById(R.id.finish_dollar);
		finish_dollar_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/showfone.ttf"));
		finish_dollar_txt.setTextColor(Color.BLUE);
		finish_dollar_txt.setText("總共金額為 "+String.valueOf(dollar)+"元");
		TextPaint finish_dollar = finish_dollar_txt.getPaint(); 
		finish_dollar.setFakeBoldText(true);
		
		payway_txt=(TextView)findViewById(R.id.pay_way);
		payway_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/showfone.ttf"));
		payway_txt.setTextColor(Color.BLACK);
		TextPaint payway = payway_txt.getPaint(); 
		payway.setFakeBoldText(true);
	}

	

    public void onPause() 
    {
        super.onPause(); 
    }
    
    public void onResume() 
    {
        super.onResume();
    }
    
}