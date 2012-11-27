package com.stylingandroid.displaycharacteristics;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		populate(findViewById(R.id.main), getWindowManager()
				.getDefaultDisplay());
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
}
