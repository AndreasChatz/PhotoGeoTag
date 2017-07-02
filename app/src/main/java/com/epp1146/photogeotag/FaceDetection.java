package com.epp1146.photogeotag;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class FaceDetection extends Activity {

	private Uri ImagePath;
	private ImageView iv;
	private Bitmap myBitmap, bitmap565;
	private int facesFound, pointer;
	private static int screenWidth = 0;
	private static int screenHeight = 0;
	private int MAX_FACES = 10;
	private PointF midPoint;
	private float eyeDistance = 0.0f;
	private boolean exist = false;
	static int inSampleSize;
	DBHandler DBHandlerObject;
	String trueImagePath;

	private String contactName;

	PersonInPhoto[] persons;
	PersonsInPhotoTable[] personsTable;

	private int PICK_CONTACT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_face_detection);

		Bundle extras = getIntent().getExtras();
//		Log.i("prin ton orismo tou iv", "pige");
		iv = (ImageView) findViewById(R.id.photo);
//		Log.i("prin ton listener tou iv", "pige");
		iv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				float x = event.getX();
				float y = event.getY();

				switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN:
					for (int index = 0; index < facesFound; index++) {
						persons[index].setPersonsCoordinates();
						if (persons[index].isPersonClicked(x, y)) {
							Intent i = new Intent(
									Intent.ACTION_PICK,
									android.provider.ContactsContract.Contacts.CONTENT_URI);
							pointer = index;
							startActivityForResult(i, PICK_CONTACT);

							break;
						}
					}

				}
				return false;
			}
		});
		if (extras != null) {
//			Log.i("extras", "!null");
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			ImagePath = extras.getParcelable("path");
			// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			// ImagePath = extras.getString("path");
//			Log.i("image path receved", ""+ImagePath);
			screenDimensions(this);
			trueImagePath = getRealPathFromURI(ImagePath);
			myBitmap = decodeSampledBitmapFromUri(trueImagePath, screenWidth,
					screenHeight);
//			Log.i("true path", ""+trueImagePath);

			detectFaces(myBitmap);
		} else {
//			Log.i("My App", "skata");
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
			Uri contactData = data.getData();
			Cursor c = this.getContentResolver().query(contactData, null, null,
					null, null);// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			if (c.moveToFirst()) {

				contactName = c
						.getString(c
								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				persons[pointer].setName(contactName);
//				c.close();

				float xmin = persons[pointer].getRealXMin();
				float ymax = persons[pointer].getRealYMax();

				Canvas canvas = new Canvas(bitmap565);
				Paint paint = new Paint();
				paint.setColor(Color.GREEN);
				paint.setAntiAlias(true);
				paint.setStyle(Paint.Style.FILL);
				paint.setTextSize(24);
				canvas.drawText(contactName, xmin, ymax + 25, paint);
				iv.setImageBitmap(bitmap565);	
				Log.i("True image name", ""+trueImagePath);
			}
			c.close();

			Log.i("minima", "telos tis startActivityForResult");
			Toast.makeText(this,
					getString(R.string.arthro) + contactName + getString(R.string.vrisketeStinPhoto),
					Toast.LENGTH_SHORT).show();
		}

	}

	public static Bitmap decodeSampledBitmapFromUri(String res, int reqWidth,
			int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(res, options);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(res, options);
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;

		inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			// Calculate ratios of height and width to requested height and
			// width
			int heightRatio = Math.round((float) height / (float) reqHeight);
			int widthRatio = Math.round((float) width / (float) reqWidth);

			// Choose the smallest ratio as inSampleSize value, this will
			// guarantee
			// a final image with both dimensions larger than or equal to
			// the
			// requested height and width.
			inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;

		}
		Log.i("inSapleSize", ""+inSampleSize);
		return inSampleSize;
	}

	@SuppressLint("NewApi")
	static Integer [] screenDimensions(Context context) {

		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2) {
			screenWidth = display.getWidth(); // deprecated
			screenHeight = display.getHeight(); // deprecated
		} else {
			Point size = new Point();
			display.getSize(size);
			screenWidth = size.x;
			screenHeight = size.y;
		}
		Integer [] dimensions = new Integer[2];
		dimensions[0]=screenWidth;
		dimensions[1]=screenHeight;
		return dimensions;
	}

	private void detectFaces(Bitmap bitmap) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		FaceDetector detector = new FaceDetector(width, height, MAX_FACES);
		FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];

		bitmap565 = Bitmap.createBitmap(width, height, Config.RGB_565);
		Paint ditherPaint = new Paint();
		Paint drawPaint = new Paint();

		ditherPaint.setDither(true);
		drawPaint.setColor(Color.GREEN);
		drawPaint.setStyle(Paint.Style.STROKE);
		drawPaint.setStrokeWidth(2);

		Canvas canvas = new Canvas();
		canvas.setBitmap(bitmap565);
		canvas.drawBitmap(bitmap, 0, 0, ditherPaint);

		facesFound = detector.findFaces(bitmap565, faces);
		midPoint = new PointF();

		float confidence = 0.0f;

		persons = new PersonInPhoto[facesFound];

		if (facesFound > 0) {
			for (int index = 0; index < facesFound; ++index) {
				faces[index].getMidPoint(midPoint);
				eyeDistance = faces[index].eyesDistance();
				confidence = faces[index].confidence();

				persons[index] = new PersonInPhoto();

				persons[index].setPersonsBasicCoordinates(midPoint.x,
						midPoint.y, eyeDistance);

				// Log.i("FaceDetector", "Confidence: " + confidence
				// + ", Eye distance: " + eyeDistance + ", Mid Point: ("
				// + midPoint.x + ", " + midPoint.y + ")");

				canvas.drawRect((int) midPoint.x - eyeDistance,
						(int) midPoint.y - eyeDistance, (int) midPoint.x
								+ eyeDistance, (int) midPoint.y + eyeDistance,
						drawPaint);
			}
		}
		iv.setImageBitmap(bitmap565);
		DBHandlerObject = new DBHandler(this);
		personsTable = DBHandlerObject.isImageNameExist(trueImagePath);// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		// Log.i("personsTable.length", "" + personsTable.length);
		if (personsTable.length != 0) {
			// Log.i("personsTable.length", "" + personsTable.length);
			exist = true;
			Toast.makeText(this, R.string.idiProstetheiOnomata, Toast.LENGTH_SHORT).show();
		}

		float ratio = (float) ((double) screenWidth / (double) myBitmap
				.getWidth());
		int hightImageScreen = (int) (ratio * myBitmap.getHeight());
		float yShift = (screenHeight - hightImageScreen) / 2;

		PersonInPhoto.setRatioYshift(ratio, yShift);
	}

	public String getRealPathFromURI(Uri contentURI) {
		String realPath = null;
		String[] proj = { MediaStore.Images.ImageColumns.DATA };
		Cursor cursor = getContentResolver().query(contentURI, proj, null,
				null, null);
		if (cursor == null) {
			realPath = contentURI.getPath();
		} else {
			if (cursor.moveToFirst()) {
				int idx = cursor
						.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
				realPath = cursor.getString(idx);
			}
		}
		cursor.close();
		return realPath;
	}

	private void isTheSamePerson(String name, float xmin, float ymax) {
		// Log.i("isTheSamePerson", "mpike");
		// Log.i("personsTable.length", "" + personsTable.length);

		for (int i = 0; i < personsTable.length; i++) {

			if (!name.equals(personsTable[i].getName())) {
				if (xmin == personsTable[i].getXMin()
						&& ymax == personsTable[i].getYMax()) {
					DBHandlerObject.updateNameEntry(personsTable[i].getId(),
							name);
				}

				// Log.i("personsTable[i].getName()",
				// "" + personsTable[i].getName());

			}
		}
		exist = false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.face_detection, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();

		DBHandlerObject = new DBHandler(this);
		if (!exist) {
			for (int i = 0; i < persons.length; i++) {
				if (persons[i].getName()!=null) { //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					DBHandlerObject.setPersonsInPhotoRow(trueImagePath,
							persons[i].getName(), persons[i].getRealXMin(),
							persons[i].getRealXMax(), persons[i].getRealYMin(),
							persons[i].getRealYMax());
					Log.i("proste8ike?", "nai" + persons[i]);
				}
			}
		} else {
			Log.i("proste8ike?", "oxi");
			Log.i("contactName?", "" + contactName);

			for (int i = 0; i < persons.length; i++) {
				float xmin = persons[i].getRealXMin();
				float ymax = persons[i].getRealYMax();
				String name = persons[i].getName();
				if (name != null) {
					isTheSamePerson(name, xmin, ymax);
				}
			}
			Log.i("persons.length?", "" + persons.length);
		}
		DBHandlerObject.getAllPersons();
		persons = null;
	}
}