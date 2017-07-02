package com.epp1146.photogeotag;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;
	public static ArrayList<String> result1;

	// Constructor
	public ImageAdapter(Context c) {
		result1 = PreviewActivity.result2;
		mContext = c;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return result1.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return result1.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		Log.i("result value", ""+result1.get(position));
		
		BitmapFactory.Options options = new BitmapFactory.Options();

		options.inSampleSize = 16;
//		options.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(result1.get(position), options);		
		
		Bitmap bm1 = ThumbnailUtils.extractThumbnail(bm, 80, 80);
		ImageView imageView = new ImageView(mContext);		

		imageView.setImageBitmap(bm1);
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setLayoutParams(new GridView.LayoutParams(80, 80));

		return imageView;
	}
	
//	
//	final BitmapFactory.Options options = new BitmapFactory.Options();
//	options.inJustDecodeBounds = true;
//	BitmapFactory.decodeFile(res, options);
//
//	// Calculate inSampleSize
//	options.inSampleSize = calculateInSampleSize(options, reqWidth,
//			reqHeight);
//
//	// Decode bitmap with inSampleSize set
//	options.inJustDecodeBounds = false;
//	return BitmapFactory.decodeFile(res, options);

}
