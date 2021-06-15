package com.stylingandroid.displaycharacteristics;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
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

public class PresentationHelper {
    private final Context m_context;
    private PresentationHelper.MyPresentation m_presentation = null;
    private PresentationHelper.MyCallback m_callback = null;

    public PresentationHelper(Context outerContext) {
        m_context = outerContext;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            multiInit();
        }
    }

    public void release() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            multiDestroy();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void multiInit() {
        MediaRouter mr = (MediaRouter) m_context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
        if (mr == null) {
            return;
        }
        m_callback = new PresentationHelper.MyCallback();
        mr.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, m_callback);
        RouteInfo info = mr.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        if ((info != null) && info.isEnabled() && (info.getPresentationDisplay() != null)) {
            m_presentation = new MyPresentation(m_context,
                    info.getPresentationDisplay(),
                    android.R.style.Theme_DeviceDefault_Light_NoActionBar);
            m_presentation.show();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void multiDestroy() {
        if (m_callback != null) {
            MediaRouter mr = (MediaRouter) m_context.getSystemService(Context.MEDIA_ROUTER_SERVICE);
            mr.removeCallback(m_callback);
            m_callback = null;
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static class MyPresentation extends Presentation {
        public MyPresentation(Context outerContext, Display display, int theme) {
            super(outerContext, display, theme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            populate(findViewById(R.id.main), getDisplay());
        }

        @SuppressLint("DefaultLocale")
        private void populate(View v, Display display) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            float density = metrics.density;
            TextView actual = (TextView) v.findViewById(R.id.actual);
            if (actual != null) {
                actual.setText(String.format("%dx%d", metrics.widthPixels,
                        metrics.heightPixels));
            }
            TextView df = (TextView) v.findViewById(R.id.density_factor);
            if (df != null) {
                df.setText(String.format("%f", density));
            }
            TextView dp = (TextView) v.findViewById(R.id.device_pixels);
            if (dp != null) {
                dp.setText(String.format("%dx%d",
                        ((int) (((float) metrics.widthPixels) / density)),
                        ((int) (((float) metrics.heightPixels) / density))));
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private class MyCallback extends SimpleCallback {
        @Override
        public void onRoutePresentationDisplayChanged(MediaRouter router,
                                                      RouteInfo info) {
            if (info != null && info.isEnabled() && m_presentation == null) {
                m_presentation = new MyPresentation(PresentationHelper.this.m_context,
                        info.getPresentationDisplay(),
                        android.R.style.Theme_DeviceDefault_Light_NoActionBar);
                m_presentation.show();
            } else {
                m_presentation = null;
            }
        }
    }
}
