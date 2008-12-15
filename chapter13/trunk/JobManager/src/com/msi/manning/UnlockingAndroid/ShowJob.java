/*
 * showjob.java
 * Unlocking Android
 * http://manning.com/ableson
 * Author: W. F. Ableson
 * fableson@msiservices.com
 */


package com.msi.manning.UnlockingAndroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.view.View;
import android.util.Log;

public class ShowJob extends Activity 
{
	Prefs myprefs = null;
	JobEntry je = null;
	final int CLOSEJOBTASK = 1;
	
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        setContentView(R.layout.showjob);
        
        
        myprefs = new Prefs(this.getApplicationContext());
        
        StringBuilder sb = new StringBuilder();
        
        String details = null;
        
        Intent startingIntent = getIntent();
        
        if (startingIntent != null)
        {
        	Log.i("CH12::ShowJob","starting intent not null");
        	Bundle b = startingIntent.getExtras();
        	if (b == null)
        	{
        		Log.i("CH12::ShowJob","bad bundle");
        		details = "bad bundle?";
        	}
        	else
    		{
        		je = JobEntry.fromBundle(b);
        		sb.append("Job Id: " + je.get_jobid() + " (" + je.get_status()+ ")\n\n");
        		sb.append(je.get_customer() + "\n\n");
        		sb.append(je.get_address() + "\n" + je.get_city() + "," + je.get_state() + "\n" );
        		sb.append("Product : "+ je.get_product() + "\n\n");
        		sb.append("Comments: " + je.get_comments() + "\n\n");
        		details = sb.toString();
    		}
        }
        else
        {
        	details = "Job Information Not Found.";
            TextView tv = (TextView) findViewById(R.id.details);
            tv.setText(details);
            return;
        }
        
        TextView tv = (TextView) findViewById(R.id.details);
        tv.setText(details);

        
        Button bmap = (Button) findViewById(R.id.mapjob);
        
        bmap.setOnClickListener(new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	// clean up data for use in GEO query
            	String address = je.get_address() + " " + je.get_city() + " " + je.get_zip();
            	String cleanAddress = address.replace(",", "");
            	cleanAddress = cleanAddress.replace(' ','+');
            	
            	try
            	{
            		Intent geoIntent = new Intent("android.intent.action.VIEW",android.net.Uri.parse("geo:0,0?q=" + cleanAddress));
           		
            		startActivity(geoIntent);
            	}
            	catch (Exception ee)
            	{
            		Log.d("CH12","error launching map? " + ee.getMessage());
            	}

            }
        });        
        
        Button bproductinfo = (Button) findViewById(R.id.productinfo);
        bproductinfo.setOnClickListener(new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	try
            	{
            		Intent productInfoIntent = new Intent("android.intent.action.VIEW",android.net.Uri.parse(je.get_producturl()));
           		
            		startActivity(productInfoIntent);
            	}
            	catch (Exception ee)
            	{
            		Log.d("CH12","error launching product info? " + ee.getMessage());
            	}
            }
        });
        
        Button bclose = (Button) findViewById(R.id.closejob);
        if (je.get_status().equals("CLOSED"))
        {
        	bclose.setText("Job is Closed. View Signature");
        }
        bclose.setOnClickListener(new Button.OnClickListener() 
        {
            public void onClick(View v) 
            {
            	if (je.get_status().equals("CLOSED"))
            	{
            		Intent signatureIntent = new Intent("android.intent.action.VIEW",android.net.Uri.parse(myprefs.getServer() + "sigs/" + je.get_jobid() + ".jpg"));
               		
            		startActivity(signatureIntent);
            		
            	}
            	else
            	{
	            	Intent closeJobIntent = new Intent(ShowJob.this,CloseJob.class);
	           		Bundle b = je.toBundle();
	           		closeJobIntent.putExtras(b);
	           		//closeJobIntent.putExtra("android.intent.extra.INTENT", b);
	        		startActivityForResult(closeJobIntent,CLOSEJOBTASK);
            	}
            }
        });
        
        
        
        
        Log.d("CH12","Job status is :" + je.get_status());
        
        
        
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	switch (requestCode)
    	{
    		case CLOSEJOBTASK:
    			if (resultCode == 1)
    			{
    				Log.d("CH12","Good Close!");
    				// propagate this up to the list activity
    				Intent resultIntent = new Intent();
    				resultIntent.putExtras(data.getExtras());
    				this.setResult(1, resultIntent);
    				// leave this activity
    				finish();
    			}
    			break;
    	}
    	
    }
    
	
	
}
