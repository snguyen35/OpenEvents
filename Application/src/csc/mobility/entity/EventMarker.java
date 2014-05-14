package csc.mobility.entity;

import csc.mobility.openevents.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;

public class EventMarker {
	Context context;
	Paint mPaint;
	public EventMarker(Context context){
		this.context=context;
	}
	/*public Bitmap getMarker(String name){
		
		// paint defines the text color,
				// stroke width, size
				Paint color = new Paint();
				color.setTextSize(10);
				color.setTextAlign(Paint.Align.CENTER);
				color.setColor(Color.BLACK);
				//color.setShadowLayer(3, 0,0, Color.BLUE);
				color.setTypeface(Typeface.DEFAULT_BOLD);
		
				int textWidth=Math.round(color.measureText(name));
				int textSize=Math.round(color.measureText(name));
		
				Bitmap.Config conf = Bitmap.Config.ARGB_8888;
				Bitmap bmp = Bitmap.createBitmap(textWidth, textSize, conf);
				Canvas canvas1 = new Canvas(bmp);
			

				mPaint= new Paint();
				mPaint.setColor(Color.BLACK);
				mPaint.setStyle(Style.STROKE);
       
		
		
		//modify canvas
		//canvas1.drawRect(100-color.measureText(name)/2, 10 - color.getTextSize(), 100 + color.measureText(name)/2, 12, mPaint);
		canvas1.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.currentlocation), color.measureText(name)/2,10, color);
		canvas1.drawText(name, color.measureText(name)/2, 10, color);
		return bmp;
	}*/
	public Bitmap getMarker(String name){
		
		//Context context = null;

		mPaint= new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Style.STROKE);
       
       
		// paint defines the text color,
		// stroke width, size
		Paint color = new Paint();
		color.setTextSize(12);
		color.setTextAlign(Paint.Align.CENTER);
		color.setColor(Color.BLACK);
		color.setShadowLayer(3, 0,0, Color.BLUE);
		color.setTypeface(Typeface.DEFAULT_BOLD);
		
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bmp = Bitmap.createBitmap(Math.round(color.measureText(name)), 60, conf);
		Canvas canvas1 = new Canvas(bmp);
		
		//modify canvas
		canvas1.drawRect(0, color.getTextSize(), 30 + color.measureText(name), 0, mPaint);
		canvas1.drawBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.currentlocation), color.measureText(name)/2,10, color);
		canvas1.drawText(name, color.measureText(name)/2, 10, color);
		return bmp;
	}
}