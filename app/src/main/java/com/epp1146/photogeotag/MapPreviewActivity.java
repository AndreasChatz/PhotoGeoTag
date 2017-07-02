package com.epp1146.photogeotag;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapPreviewActivity extends FragmentActivity implements OnMapReadyCallback {
	
	String path;
	CameraUpdate update;
	LatLng latlng;
	public static String TAG = "GeoTag";
	
	@RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_preview);
		
		Bundle extras;
		extras = getIntent().getExtras();
		if(extras!=null){
			path = extras.getString("path");
			Log.i(TAG, "onCreate: " + path);
		}

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		DBHandler dbHandlerObject = new DBHandler(this);

		String [] dedomena = dbHandlerObject.getCoordsAndPlace("file://"+path);
		latlng = new LatLng(Double.parseDouble(dedomena[0]), Double
				.parseDouble(dedomena[1]));
		update = CameraUpdateFactory.newLatLngZoom(latlng, 17);

//		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
////		googleMap = map.getMap();
//		googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//		googleMap.clear();
		


//		googleMap.animateCamera(update);
//		googleMap.addMarker(new MarkerOptions()
//				.position(latlng)
//				.title("Τραβήχτηκε εδώ").snippet("Περιοχή : " + dedomena[2]));
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		googleMap.addMarker(new MarkerOptions()
				.position(latlng)
				.title(getString(R.string.takenHere)));
		googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		googleMap.animateCamera(update);
	}
}
