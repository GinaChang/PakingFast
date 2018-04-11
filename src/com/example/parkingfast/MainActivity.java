package com.example.parkingfast;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class MainActivity extends Activity
{
	ImageButton choice1_btn, choice2_btn;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);

		findView();
		
		
		// ¹Ï¤ù«ö¶s¤Á´«¤À­¶
		choice1_btn.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Map1.class);
				startActivity(intent);
			}
		});
		
		choice2_btn.setOnClickListener(new OnClickListener() 
		{
			public void onClick(View v)
			{
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Map2.class);
				startActivity(intent);
			}
		});

	}

	private void findView()
	{
		choice1_btn = (ImageButton) findViewById(R.id.choice1);
		choice2_btn = (ImageButton) findViewById(R.id.choice2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
