package com.example.parkingfast;

import java.util.Map;

import android.R.integer;
import android.R.string;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.renderscript.Font;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class parkingview extends View
{
	public parkingview(Context context) 
	{
		super(context);
	}

	public parkingview(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public parkingview(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}
	
	protected void onDraw(Canvas canvas) 
	{
		
		int width = this.getWidth();
        int height = this.getHeight();
        int per_height = (height-10)/7;
        int per_width = (width-10)/3;
        int x1=width-5-per_width ,y1=5 ,x2=width-5 ,y2=5+per_height;
        int space_num=12;
        int first_empty_space=0;
        
        
 
        
		Paint paint =new Paint(); //內框
		paint.setAntiAlias(true); //消除鋸齒
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(5);
		paint.setTextSize(60);
		
		Paint paint2 =new Paint();//外框
		paint2.setAntiAlias(true); //消除鋸齒
		paint2.setStyle(Style.STROKE);
		paint2.setStrokeWidth(15);
		paint2.setColor(Color.BLACK);
		
		Paint num_paint =new Paint();
		num_paint.setAntiAlias(true);
    	num_paint.setTextSize(60);//設定畫出的停車格號碼size
    
    	
    	
		Paint text_paint=new Paint(); 	
		text_paint.setColor(Color.BLACK);
		text_paint.setTextSize(60);	
		text_paint.setAntiAlias(true);
		//text_paint.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/showfone.ttf"));
    	//Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/showfone.ttf");
		//text_paint.setTypeface(tf);

		
		if(Map1.sendflag) //在parkingspace的class裡面，取得的字串和其餘動作已經OK後，sendflag=true
		{

			for(int i=11;i>=6;i--) //畫出停車位號碼12~7的車位
	        {
	        	Log.i("times", String.valueOf(i));
	        	if("0".equals(Map1.space[i])) //如果該停車位抓取字串為0，代表停車位是空
	        	{
	        		paint.setColor(Color.rgb(78, 238, 148));
	        		num_paint.setColor(Color.rgb(67, 205, 128));
	        		if(first_empty_space==0)
	        		{
	        		first_empty_space=i+1;
	        		}
	        	}
	        	else 
	        	{
	        		paint.setColor(Color.RED);
	        		num_paint.setColor(Color.RED);
				}
	        	canvas.drawRect(x1, y1+10, x2, y2, paint2); //左,上,右,下(x1,y1,x2,y2) 外框  	
	        	canvas.drawRect(x1, y1+10, x2, y2, paint); //左,上,右,下(x1,y1,x2,y2)   		
	        	canvas.drawText(String.valueOf(space_num), (x1+x2-25)/2, (y1+y2+65)/2, text_paint);//黑色文字陰影
	        	canvas.drawText(String.valueOf(space_num), (x1+x2-30)/2, (y1+y2+60)/2, num_paint);		
	        	y1+=per_height;
	    		y2+=per_height;
				space_num--;										
	        }
	        
	        x1=5;
	        y1=5;
	        x2=5+per_width;
	        y2=5+per_height;
	        
	        for(int i=5;i>=0;i--) //畫出停車位號碼7~12的車位
	        {	        	
	        	if("0".equals(Map1.space[i])) //如果該停車位抓取字串為0，代表停車位是空
	        	{
	        		paint.setColor(Color.rgb(78, 238, 148));
	        		num_paint.setColor(Color.rgb(67, 205, 128));
	        	}
	        	else 
	        	{
	        		paint.setColor(Color.RED);
	        		num_paint.setColor(Color.RED);
				}
				
	        	canvas.drawRect(x1, y1+10, x2, y2, paint2); //左,上,右,下(x1,y1,x2,y2)
	        	canvas.drawRect(x1, y1+10, x2, y2, paint); //左,上,右,下(x1,y1,x2,y2)
	        	canvas.drawText(String.valueOf(space_num), (x1+x2-45)/2, (y1+y2+65)/2, text_paint);
	        	canvas.drawText(String.valueOf(space_num), (x1+x2-50)/2, (y1+y2+60)/2, num_paint);
	        	y1+=per_height;
	    		y2+=per_height;
	    		space_num--;	    		
	        }
	        //畫出指引文字
	        
	        if(first_empty_space!=0)
	        {
	        	canvas.drawText("建議至"+String.valueOf(first_empty_space)+"號位置停車", (x1+x2-15)/2, (y1+y2+60)/2, text_paint);
	        }
	        else 
	        {
	        	canvas.drawText("抱歉，目前車位已滿", (x1+x2-15)/2, (y1+y2+60)/2, text_paint);
			}
	        
	        //灰色三角形
	        Path path =new Path();
	        path.moveTo(x2+(per_width/2),y1-per_height+70);// 多邊形起點  
	        path.lineTo(x2+(per_width/2)+50, y1-per_height+120);  
	        path.lineTo(x2+(per_width/2)-50, y1-per_height+120);  
	        path.close(); // 使這些點構成封閉的多邊形
	        text_paint.setColor(Color.GRAY);
	        canvas.drawPath(path, text_paint); 
	        //黑色三角形
	        Path path2 = new Path();
	        path2.moveTo(x2+(per_width/2) ,y1-per_height+80);// 多邊形起點  
	        path2.lineTo(x2+(per_width/2)+30, y1-per_height+110);  
	        path2.lineTo(x2+(per_width/2)-30, y1-per_height+110);  
	        path2.close(); // 使這些點構成封閉的多邊形  
	        text_paint.setColor(Color.BLACK);
	        canvas.drawPath(path2, text_paint); 
	        
	        //canvas.drawRect(left, top, right, bottom, text_paint)

	        
	        
		}
                  	
		invalidate(); //不斷更新畫面
	}
	
}
