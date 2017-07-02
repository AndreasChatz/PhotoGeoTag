package com.epp1146.photogeotag;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

//http://developer.android.com/reference/android/media/ThumbnailUtils.html

public class PreviewActivity extends Activity {
	
	ImageView im;
	static ArrayList<String> result2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preview);
		result2 = null;
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			result2 = extras.getStringArrayList("result");
		}
		

        GridView gridView = (GridView) findViewById(R.id.grid_view);
 
        // Instance of ImageAdapter Class
        gridView.setAdapter(new ImageAdapter(this));
        
        gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                    int position, long id) {
 
                // Sending image id to FullScreenActivity
                Intent i = new Intent(getApplicationContext(), FullPreview.class);
                // passing array index
                i.putExtra("id", position);
                startActivity(i);
            }
        });
    }
		
//		Bundle extras = getIntent().getExtras();
//		if(extras!=null){
//			ArrayList<String> result = extras.getStringArrayList("result");	
//			Log.i("result count", ""+result.size());
//			im = (ImageView) findViewById(R.id.imageView1);
//			im.setImageURI(Uri.parse(result.get(0)));
//		}
//	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.preview, menu);
		return true;
	}

}
