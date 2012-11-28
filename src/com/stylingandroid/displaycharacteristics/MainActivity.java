package com.stylingandroid.displaycharacteristics;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private MyPresentation mPresentation = null;
	private MyDisplayListener mListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		populate(findViewById(R.id.main), getWindowManager()
				.getDefaultDisplay());
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
		{
			multiInit();
		}
	}

	@Override
	protected void onDestroy()
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
		{
			multiDestroy();
		}
		super.onDestroy();
	}

	private static void populate(View v, Display display)
	{
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		float density = metrics.density;
		TextView actual = (TextView) v.findViewById(R.id.actual);
		if (actual != null)
		{
			actual.setText(String.format("%dx%d", metrics.widthPixels,
					metrics.heightPixels));
		}
		TextView df = (TextView) v.findViewById(R.id.density_factor);
		if (df != null)
		{
			df.setText(String.format("%f", density));
		}
		TextView dp = (TextView) v.findViewById(R.id.device_pixels);
		if (dp != null)
		{
			dp.setText(String.format("%dx%d",
					((int) ((float) metrics.widthPixels / density)),
					((int) ((float) metrics.heightPixels / density))));
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void multiInit()
	{
		DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
		if (dm != null)
		{
			mListener = new MyDisplayListener();
			dm.registerDisplayListener(mListener, null);
			Display[] displays = dm
					.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
			for (Display display : displays)
			{
				mPresentation = new MyPresentation(this, display,
						android.R.style.Theme_Holo_Light_NoActionBar);
				mPresentation.show();
			}
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void multiDestroy()
	{
		if (mListener != null)
		{
			DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
			dm.unregisterDisplayListener(mListener);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	public class MyPresentation extends Presentation
	{

		public MyPresentation(Context outerContext, Display display, int theme)
		{
			super(outerContext, display, theme);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			populate(findViewById(R.id.main), getDisplay());
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private class MyDisplayListener implements DisplayListener
	{
		@Override
		public void onDisplayAdded(int displayId)
		{
			DisplayManager dm = (DisplayManager) getSystemService(DISPLAY_SERVICE);
			Display disp = dm.getDisplay(displayId);
			if (disp != null)
			{
				mPresentation = new MyPresentation(MainActivity.this, disp,
						android.R.style.Theme_Holo_Light_NoActionBar);
				mPresentation.show();
			}
		}

		@Override
		public void onDisplayChanged(int displayId)
		{
		}

		@Override
		public void onDisplayRemoved(int displayId)
		{
			if (mPresentation != null
					&& mPresentation.getDisplay().getDisplayId() == displayId)
			{
				mPresentation = null;
			}
		}
	}

}
