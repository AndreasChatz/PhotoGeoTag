package com.epp1146.photogeotag;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class FullPreview extends Activity {
	
	private DBHandler dbHandlerObject;
	ImageView imageView1;
	Bitmap bm;
	public String fullPreviewImageUri;
	private	static String TAG = "GeoTag";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_preview);

		// get intent data
		Intent i = getIntent();

		// Selected image id
		int position = i.getExtras().getInt("id");
		// ImageAdapter imageAdapter = new ImageAdapter(this);

		imageView1 = (ImageView) findViewById(R.id.full_image_view);

		Integer[] dimensions = FaceDetection.screenDimensions(this);
		fullPreviewImageUri =ImageAdapter.result1.get(position);
		bm = FaceDetection.decodeSampledBitmapFromUri(
				fullPreviewImageUri, dimensions[0],
				dimensions[1]);

//		imageView1.setImageBitmap(bm);		
		
//		Log.i("ImageAdapter.result1.get(position)", ""+ImageAdapter.result1.get(position));

		dbHandlerObject = new DBHandler(this);
		ArrayList<ArrayList<String>> coords = dbHandlerObject.getFacesCoordinates(ImageAdapter.result1.get(position));
		Log.i("coords megethos", ""+coords.size());
		drawRectanglesAndNames(coords);
	}
	
	private void drawRectanglesAndNames (ArrayList<ArrayList<String>> coords){
		
		Bitmap bitmap565 = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Config.RGB_565);
		Paint ditherPaint = new Paint();
		Paint drawPaint = new Paint();

		ditherPaint.setDither(true);
		drawPaint.setColor(Color.GREEN);
		drawPaint.setStrokeWidth(2);
		drawPaint.setTextSize(24);

		Canvas canvas = new Canvas();
		canvas.setBitmap(bitmap565);
		canvas.drawBitmap(bm, 0, 0, ditherPaint);
		
		for(int i=0; i<coords.size();i++){
			drawPaint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(Float.parseFloat(coords.get(i).get(1)),
					Float.parseFloat(coords.get(i).get(3)),
					Float.parseFloat(coords.get(i).get(2)),
					Float.parseFloat(coords.get(i).get(4)), drawPaint);
			
			drawPaint.setStyle(Paint.Style.FILL);
			canvas.drawText(coords.get(i).get(0), Float.parseFloat(coords.get(i).get(1)), Float.parseFloat(coords.get(i).get(4)) + 25, drawPaint);
			
		}
		
		imageView1.setImageBitmap(bitmap565);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.full_preview, menu);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (MainActivity.isWifiConnected(this)) {
			Intent intent = new Intent(this, MapPreviewActivity.class);
			intent.putExtra("path", fullPreviewImageUri);
			Log.i(TAG, "onOptionsItemSelected: path " +fullPreviewImageUri);
			startActivity(intent);
		}
//		Toast.makeText(this, "pati8ike", Toast.LENGTH_LONG).show();
		return super.onOptionsItemSelected(item);
	}

}
