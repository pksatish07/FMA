package com.gsysk.asynctasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.gsysk.fma.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CloudParserAsyncTask extends AsyncTask<Void, Void, Void> {

    // Override this method to do custom remote calls
    int j = 0;
    String queryType = "";
    Activity curActivity;
    List<ParseObject> parseObjectList;
    ProgressDialog progressDialog;
    public CloudParserAsyncTask(String type,Activity activity,List<ParseObject> parseObjectList)
    {
        queryType = type;
        curActivity = activity;
        this.parseObjectList = parseObjectList;
    }
    protected Void doInBackground(Void... params)
    {
        //Initiate processing and next activity launch

        // Gets the current list of parseObjectsList.gatewaylist in sorted order
        ParseQuery<ParseObject> query = null;
        if(queryType.equals("Drivers"))
        {
            query = new ParseQuery<ParseObject>("Driver");
        }
        else if(queryType.equals("Users"))
        {
            query = new ParseQuery<ParseObject>("user");
        }



        try {
            parseObjectList = query.find();
				
				/*for(int i=0;i<parseObjectList.size();i++)
				{
					System.out.println(parseObjectList.get(i).get("Name"));
				}*/
        } catch (ParseException e) {

        }
        return null;
    }

    @Override
    protected void onPreExecute() {

        if(queryType.equals("Drivers"))
        {
            progressDialog = ProgressDialog.show(curActivity, "",
                    "Fetching driver details...", true);
        }
        else if(queryType.equals("Users"))
        {
            progressDialog = ProgressDialog.show(curActivity, "",
                    "Fetching contact details...", true);
        }

        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void result) {
        // Put the list of parseObjectsList.gatewaylist into the list view


        progressDialog.dismiss();
        String titleMessage = "";
        if(queryType.equals("Drivers"))
        {
            titleMessage = "List of Drivers : ";



        }
        else if(queryType.equals("Users"))
        {
            titleMessage = "List of Contacts : ";
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(curActivity);
        builder.setTitle(titleMessage);
        Dialog dialog = null;
        ListView modeList = new ListView(curActivity);
        String[] nameArray = new String[parseObjectList.size()];
        final String[] phoneNumArray = new String[parseObjectList.size()];


        for(int ind=0;ind<parseObjectList.size();ind++)
        {
            nameArray[ind] = " "+parseObjectList.get(ind).getString("Name");
            phoneNumArray[ind] = "  Number :"+parseObjectList.get(ind).getString("PhoneNumber");
        }

        // Each row in the list stores country name, currency and flag
        List<HashMap<String,String>> aList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<parseObjectList.size();i++){
            HashMap<String, String> hm = new HashMap<String,String>();
            hm.put("mainTxt", nameArray[i]);
            hm.put("subTxt",phoneNumArray[i]);

            aList.add(hm);
        }

        // Keys used in Hashmap
        String[] from = { "mainTxt","subTxt" };

        // Ids of views in listview_layout
        int[] to = { R.id.mainTxt,R.id.subTxt};
        // Instantiating an adapter to store each items
        // R.layout.listview_layout defines the layout of each item
        SimpleAdapter modeAdapter = new SimpleAdapter(curActivity, aList, R.layout.listview_dialog_layout, from, to);

        int[] colors = {0, 0xFFFF0000, 0}; // red for the example
        modeList.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        modeList.setDividerHeight(2);
        modeList.setAdapter(modeAdapter);

        // Item Click Listener for the listview
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View container, int position, long id) {
                // Getting the Container Layout of the ListView

                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumArray[position].split(":")[1]));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                curActivity.startActivity(callIntent);
            }
        };

        // Setting the item click listener for the listview
        modeList.setOnItemClickListener(itemClickListener);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                if(dialog!=null)
                {
                    dialog.dismiss();
                }
            }
        });
        builder.setView(modeList);
        dialog = builder.create();


        dialog.show();


    }

}