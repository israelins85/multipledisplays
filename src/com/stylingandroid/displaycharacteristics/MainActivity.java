package com.stylingandroid.displaycharacteristics;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.media.MediaRouter.SimpleCallback;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private MyPresentation mPresentation = null;
	private MyCallback mCallback = null;

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
		MediaRouter mr = (MediaRouter) getSystemService(MEDIA_ROUTER_SERVICE);
		if (mr != null)
		{
			mCallback = new MyCallback();
			mr.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mCallback);
		}
		RouteInfo info = mr.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
		if (info != null && info.isEnabled() && info.getPresentationDisplay() != null)
		{
			mPresentation = new MyPresentation(this,
					info.getPresentationDisplay(),
					android.R.style.Theme_Holo_Light_NoActionBar);
			mPresentation.show();
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void multiDestroy()
	{
		if (mCallback != null)
		{
			MediaRouter mr = (MediaRouter) getSystemService(MEDIA_ROUTER_SERVICE);
			mr.removeCallback(mCallback);
			mCallback = null;
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
	private class MyCallback extends SimpleCallback
	{
		@Override
		public void onRoutePresentationDisplayChanged(MediaRouter router,
				RouteInfo info)
		{
			if (info != null && info.isEnabled() && mPresentation == null)
			{
				mPresentation = new MyPresentation(MainActivity.this,
						info.getPresentationDisplay(),
						android.R.style.Theme_Holo_Light_NoActionBar);
				mPresentation.show();
			}
			else
			{
				mPresentation = null;
			}
		}
	}

}
