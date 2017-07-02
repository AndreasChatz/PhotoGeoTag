package com.epp1146.photogeotag;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class SelectFilters extends Activity implements OnClickListener {

    String[] items = null;
    ArrayList<String> itemsLocation;
    private String location = "";
    String[] names;
    int epilogi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_filters);

        findViewById(R.id.btn4).setOnClickListener(this);
        findViewById(R.id.btn5).setOnClickListener(this);
        findViewById(R.id.btn6).setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.select_filters, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.btn4:
                createDialog(0);
                break;
            case R.id.btn5:
                createDialog(1);
                break;
            case R.id.btn6:
                DBHandler dbHandler = new DBHandler(this);

                if (!(location.equals("") || (names == null))) {
                    ArrayList<String> result = new ArrayList<String>();
                    result = dbHandler.getFilteredImages(location, names);
                    if (!result.isEmpty()) {
                        Intent intent = new Intent(this, PreviewActivity.class);
                        intent.putExtra("result", result);
                        startActivity(intent);
                    } else {
                        Toast.makeText(
                                this,
                                R.string.denYparxounPhotoMeAutaTaStixeia,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, R.string.epelekseOnomaKaiTopothesia,
                            Toast.LENGTH_SHORT).show();
                }
        }
    }

    private Dialog createDialog(int i) {
        DBHandler dbHandler = new DBHandler(this);
        dbHandler.updateNullPicturePlace(this);

        switch (i) {
            case 0:
                items = dbHandler.getAllPhoneNamesFromDB();
                Log.i("items length", "" + items.length);

                final boolean[] checkedItems = new boolean[items.length];
                Log.i("boolean ok?", "nai");
                return new AlertDialog.Builder(this)
                        .setTitle("Επέλεξε ονόματα")
                        .setIcon(android.R.drawable.stat_notify_more)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        int j = 0;
                                        for (int i = 0; i < checkedItems.length; i++) {
                                            if (checkedItems[i]) {
                                                j++;
                                            }
                                        }
                                        names = new String[j];
                                        int k = 0;
                                        for (int z = 0; z < j; z++) {
                                            for (int i = k; i < items.length; i++) {
                                                if (checkedItems[i]) {
                                                    names[z] = items[i];// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
                                                    k = i + 1;
                                                    Log.i("name", "" + names[z]);
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                })
                        .setNegativeButton("Ακύρωση", null)
                        .setMultiChoiceItems(items, checkedItems,
                                new DialogInterface.OnMultiChoiceClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which, boolean isChecked) {
                                        Toast.makeText(
                                                getBaseContext(),
                                                items[which]
                                                        + (isChecked ? getString(R.string.epilexthike)
                                                        : " Unchecked"),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
            case 1:
                itemsLocation = dbHandler.getAllLocationsFromDB();

                Log.i("items length", "" + itemsLocation.size());
                String[] item = new String[itemsLocation.size()];
                String[] it = itemsLocation.toArray(item);
                // int j=0; !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!metatropi
                // ArrayList se String [], i etsi i opos stis proigoumenes
                // grammes!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // String [] item = new String[itemsLocation.size()];
                // for(String n:itemsLocation){
                // item[j]=n;
                // j++;
                // Log.i("items", ""+n);
                // }!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                location = "";
                epilogi = -1;
                return new AlertDialog.Builder(this)
                        .setTitle(R.string.epelekseTopothesia)
                        .setIcon(android.R.drawable.stat_notify_more)
                        .setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (epilogi != -1) {
                                            Log.i("location pou epilextike", ""
                                                    + which);
                                            location = itemsLocation.get(epilogi);// items[epilogi];
                                            Log.i("location pou epilextike", ""
                                                    + location);
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.akirosi, null)
                        .setSingleChoiceItems(it, -1,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub
                                        epilogi = which;
                                        Toast.makeText(
                                                getBaseContext(),
                                                itemsLocation.get(which)
                                                        + getString(R.string.epilexthike),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }).show();
        }
        return null;
    }
}
