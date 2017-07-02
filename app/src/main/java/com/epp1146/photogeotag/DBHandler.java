package com.epp1146.photogeotag;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

public class DBHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MyDB.db";
    private static final String TABLE_PERSONS_IN_PHOTO = "PersonsInPhoto";
    private static final String TABLE_PHOTO_LOCATION = "PhotoLocation";
    private static final String TAG = "GeoTag";

    // Ονόματα στηλών του πίνακα PersonsInPhoto
    public static final String KEY_ID_PIP = "_id";
    public static final String KEY_PERSON_IMAGE_PATH = "ImageName";
    public static final String KEY_NAME = "Name";
    public static final String KEY_XMIN = "xMin";
    public static final String KEY_XMAX = "xMax";
    public static final String KEY_YMIN = "yMin";
    public static final String KEY_YMAX = "yMax";

    // Ονόματα στηλών του πίνακα PhotoLocation
    public static final String KEY_ID_PL = "_id";
    public static final String KEY_LOCATION_IMAGE_PATH1 = "ImageName1";
    public static final String KEY_GEO_LAT = "latitude";
    public static final String KEY_GEO_LONG = "longitude";
    public static final String KEY_PLACE = "Place";

    public DBHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        final String CREATE_PERSONS_IN_PHOTO_TABLE = "CREATE TABLE " + TABLE_PERSONS_IN_PHOTO + "("
                + KEY_ID_PIP + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_PERSON_IMAGE_PATH + " TEXT,"
                + KEY_NAME + " TEXT,"
                + KEY_XMIN + " REAL,"
                + KEY_XMAX + " REAL,"
                + KEY_YMIN + " REAL,"
                + KEY_YMAX + " REAL"
                + ")";

        final String CREATE_TABLE_PHOTO_LOCATION_TABLE = "CREATE TABLE " + TABLE_PHOTO_LOCATION + "("
                + KEY_ID_PL + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_LOCATION_IMAGE_PATH1 + " TEXT,"
                + KEY_GEO_LAT + " REAL,"
                + KEY_GEO_LONG + " REAL,"
                + KEY_PLACE + " TEXT"
                + ")";

        db.execSQL(CREATE_PERSONS_IN_PHOTO_TABLE);
        db.execSQL(CREATE_TABLE_PHOTO_LOCATION_TABLE);
    }

    public void fakeCoord(String ImPath, String loc) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(KEY_LOCATION_IMAGE_PATH1, ImPath);
        cv.put(KEY_GEO_LAT, "41.264187");
        cv.put(KEY_GEO_LONG, "23.250105");
        cv.put(KEY_PLACE, loc);
        db.insert(TABLE_PHOTO_LOCATION, null, cv);
        db.close();
    }

    public void setPersonsInPhotoRow(String imagePath, String name, float xMin, float xMax, float yMin, float yMax) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_PERSON_IMAGE_PATH, imagePath);
        values.put(KEY_NAME, name);
        values.put(KEY_XMIN, xMin);
        values.put(KEY_XMAX, xMax);
        values.put(KEY_YMIN, yMin);
        values.put(KEY_YMAX, yMax);

        db.insert(TABLE_PERSONS_IN_PHOTO, null, values);
        db.close();
    }

    public PersonsInPhotoTable[] isImageNameExist(String imageName) {
        String select = "select _id,Name,xMin,yMax from " + TABLE_PERSONS_IN_PHOTO + " where ImageName = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(select, new String[]{imageName});
        PersonsInPhotoTable[] persons = new PersonsInPhotoTable[cursor.getCount()];
        if (cursor.moveToFirst() && cursor != null) {
            do {
                PersonsInPhotoTable person = new PersonsInPhotoTable();
                person.setId(cursor.getInt(0));
                person.setName(cursor.getString(1));
                person.setXMin(cursor.getFloat(2));
                person.setYMax(cursor.getFloat(3));
                persons[cursor.getPosition()] = person;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return persons;
    }

    public void getAllPersons() {
        String selectAllRaws = "select * from " + TABLE_PERSONS_IN_PHOTO;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_PERSONS_IN_PHOTO, null);

        if (cursor.moveToFirst()) {
            do {
//				Log.i("Onoma", "onoma : "+ cursor.getInt(0));
//				Log.i("Onoma", "onoma : "+ cursor.getString(1));
//              Log.i("Onoma", "onoma : " + cursor.getString(2));
//				Log.i("Onoma", "onoma : "+ cursor.getFloat(3));
//				Log.i("Onoma", "onoma : "+ cursor.getFloat(4));
//				Log.i("Onoma", "onoma : "+ cursor.getFloat(5));
//				Log.i("Onoma", "onoma : "+ cursor.getFloat(6));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public List<PersonsInPhotoTable> getPersonInPhotoRow(String imageName) {

        List<PersonsInPhotoTable> row = new ArrayList<PersonsInPhotoTable>();

        String selectRaw = "SELECT * FROM " + TABLE_PERSONS_IN_PHOTO + " WHERE ImageName = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectRaw, new String[]{imageName});

        if (cursor.moveToFirst()) {
            do {
                PersonsInPhotoTable person = new PersonsInPhotoTable();
                person.setImagePath(imageName);
                person.setName(cursor.getString(1));
                person.setXMin(cursor.getFloat(2));
                person.setXMax(cursor.getFloat(3));
                person.setYMin(cursor.getFloat(4));
                person.setYMax(cursor.getFloat(5));

                row.add(person);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return row;

    }

    public void updateNameEntry(int id, String name) {// Den einai apolita sosti, den epitrepei tag dio prosopon se diaforetikes xronikes stigmes
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);

        int a = db.update(TABLE_PERSONS_IN_PHOTO, cv, KEY_ID_PIP + " = " + id, null);
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //An yparxei palioteros pinakas sviston
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PERSONS_IN_PHOTO);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO_LOCATION);

        //Dimiourgia ksana tou pinaka
        onCreate(db);
    }

    public String[] getAllPhoneNamesFromDB() {
        SQLiteDatabase db = this.getReadableDatabase();

//		String select = "select DISTINCT "+KEY_NAME+" from "+TABLE_PERSONS_IN_PHOTO;

//		Cursor cursor = db.rawQuery(select,null);
        Cursor cursor = db.query(true, TABLE_PERSONS_IN_PHOTO, new String[]{KEY_NAME}, null, null, null, null, null, null);
        String items[] = new String[cursor.getCount()];
        if (cursor.moveToFirst()) {
            do {
                items[cursor.getPosition()] = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    public ArrayList<String> getAllLocationsFromDB() {
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "select DISTINCT " + KEY_PLACE + " from " + TABLE_PHOTO_LOCATION;

        Cursor cursor = db.rawQuery(select, null);
//		String items[] = new String[cursor.getCount()];
        ArrayList<String> items = new ArrayList<String>();

        if (cursor.moveToFirst()) {
            do {
                if (!cursor.getString(0).equals(".")) {
                    items.add(cursor.getString(0));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    public ArrayList<String> getFilteredImages(String Location, String[] names) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectByLoc = "SELECT " + KEY_LOCATION_IMAGE_PATH1 + " FROM "
                + TABLE_PHOTO_LOCATION + " WHERE " + KEY_PLACE + " =?";
        Cursor cursor = db.rawQuery(selectByLoc, new String[]{Location});
        String[] list = new String[cursor.getCount()];
        if (cursor.moveToFirst()) {
            do {
                list[cursor.getPosition()] = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();

        String select = "SELECT " + KEY_PERSON_IMAGE_PATH + " FROM "
                + TABLE_PERSONS_IN_PHOTO + " WHERE " + KEY_NAME + " =?";

        ArrayList<ArrayList<String>> s = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < names.length; i++) {
            Cursor cursor1 = db.rawQuery(select, new String[]{names[i]});
            ArrayList<String> row = new ArrayList<String>();
            row.add(names[i]);
            if (cursor1.moveToFirst()) {
                do {
                    row.add(cursor1.getString(0));
                } while (cursor1.moveToNext());
            }
            s.add(row);
            cursor1.close();
        }
        db.close();

        int minLength = 1000;
        int minPosition = -1;
        int counter = 0;
        for (ArrayList<String> s1 : s) {
            if (s1.size() < minLength) {
                minLength = s1.size();
                minPosition = counter;
                counter++;
            }
        }

        int matchCount;
        ArrayList<Integer> position = new ArrayList<Integer>();

        for (int i = 1; i < minLength; i++) {
            matchCount = 0;
            for (int z = 0; z < s.size(); z++) {
                if (s.size() != 1 && z == minPosition) {
                    z++;
                }
                if (z < s.size()) {
                    for (int j = 1; j < s.get(z).size(); j++) {
                        if ((s.get(minPosition).get(i)).equals(s.get(z).get(j))) {
                            matchCount++;
                            break;
                        }
                    }
                }
            }

            if ((matchCount == s.size() - 1) || (s.size() == 1)) {
                position.add(i);
            }
        }
        ArrayList<String> matchResults = new ArrayList<String>();

        String sImagePath = "", listImagePath = "", sImageName = "", listImageName = "";
        for (int i = 0; i < position.size(); i++) {
            for (int j = 0; j < list.length; j++) {
                sImagePath = s.get(minPosition).get(position.get(i));
                listImagePath = list[j];
                sImageName = sImagePath.substring(sImagePath.indexOf("IMG"));
                listImageName = listImagePath.substring(listImagePath.indexOf("IMG"));
                if (sImageName.equals(listImageName)) {
                    matchResults.add(sImagePath);
//                    Log.i(TAG, "getFilteredImages: matchResults " + matchResults);
                    break;
                }
            }
        }
        return matchResults;
    }

    // Epistrefei tis sintetagmens ton prosopon mazi me ta onomata auton
    public ArrayList<ArrayList<String>> getFacesCoordinates(String path) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + KEY_NAME + "," + KEY_XMIN + "," + KEY_XMAX + "," + KEY_YMIN + "," + KEY_YMAX + " FROM " + TABLE_PERSONS_IN_PHOTO + " WHERE " + KEY_PERSON_IMAGE_PATH + "=?";

        Cursor cursor = db.rawQuery(query, new String[]{path});
        ArrayList<ArrayList<String>> coordinations = new ArrayList<ArrayList<String>>();

        if (cursor.moveToFirst()) {
            ArrayList<String> row = null;
            do {
                row = new ArrayList<String>();
                row.add(cursor.getString(0));
                row.add(cursor.getString(1));
                row.add(cursor.getString(2));
                row.add(cursor.getString(3));
                row.add(cursor.getString(4));

                coordinations.add(row);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return coordinations;
    }

    public void setPhotoLocationAttributes(String path, float latitude, float longitude, String place) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_LOCATION_IMAGE_PATH1, path);
        values.put(KEY_GEO_LAT, latitude);
        values.put(KEY_GEO_LONG, longitude);
        values.put(KEY_PLACE, place);

        db.insert(TABLE_PHOTO_LOCATION, null, values);
        db.close();
    }

    //	public ArrayList<String> getNullPlaceCoordinates(){
//		SQLiteDatabase db = this.getReadableDatabase();
//		String select = "SELECT "+KEY_ID_PL+","+KEY_GEO_LAT+","+KEY_GEO_LONG+" FROM "+TABLE_PHOTO_LOCATION+" WHERE "+KEY_PLACE+" =?";
//		Cursor cursor = db.rawQuery(select, new String[]{"."});
//		return null;		
//	}
    public void updateNullPicturePlace(Context context) {
        Location loc = new Location("");
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + KEY_ID_PL + "," + KEY_GEO_LAT + "," + KEY_GEO_LONG + " FROM " + TABLE_PHOTO_LOCATION + " WHERE " + KEY_PLACE + " =?";
        Cursor cursor = db.rawQuery(select, new String[]{"."});
        if (cursor.moveToFirst()) {
            do {
                loc.setLatitude(cursor.getDouble(1));
                loc.setLongitude(cursor.getDouble(2));
                String place = MainActivity.returnGeoPlace(context, loc);
                ContentValues cv = new ContentValues();
                cv.put(KEY_PLACE, place);
                db.update(TABLE_PHOTO_LOCATION, cv, KEY_ID_PL + " = " + cursor.getString(0), null);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    public String[] getCoordsAndPlace(String path) {
        String[] dedomena = new String[3];
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT " + KEY_GEO_LAT + "," + KEY_GEO_LONG + "," + KEY_PLACE + " FROM " + TABLE_PHOTO_LOCATION + " WHERE " + KEY_LOCATION_IMAGE_PATH1 + " =?";
        Cursor cursor = db.rawQuery(select, new String[]{path});
        if (cursor.moveToFirst()) {
            dedomena[0] = cursor.getString(0);
            dedomena[1] = cursor.getString(1);
            dedomena[2] = cursor.getString(2);
        }
        cursor.close();
        db.close();
        return dedomena;
    }
}
