/*
 * listjobs.java
 * Unlocking Android
 * http://manning.com/ableson
 * Author: W. F. Ableson
 * fableson@msiservices.com
 */

package com.msi.manning.UnlockingAndroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class ManageJobs extends Activity implements OnItemClickListener 
{
	final int SHOWJOB = 1;
	Prefs myprefs = null;

	JobList _joblist = null;
	ListView jobListView;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.managejobs);

		// get our application prefs handle
		myprefs = new Prefs(this.getApplicationContext());


		TextView tv = (TextView) findViewById(R.id.statuslabel);
		
		_joblist = JobList.parse(this.getApplicationContext());
		if (_joblist == null) 
		{
			Log.d("CH12", "_joblist is null");

			
			
			
			// we need to do this to allow the list to have something to display! 
			// even though it is empty!
			_joblist = new JobList(this.getApplicationContext());
		}
		
		if (_joblist.getJobCount() == 0)
		{
			tv.setText("There are No Jobs Available");
		}
		else
		{
			tv.setText("There are " + _joblist.getJobCount() + " jobs.");
		}
		
		// get a reference to the list view
		jobListView = (ListView) findViewById(R.id.joblist);

		// setup data adapter
		ArrayAdapter<JobEntry> adapter = new ArrayAdapter<JobEntry>(this,
				android.R.layout.simple_list_item_1, _joblist.getAllJobs());

		// assign adapter to list view
		jobListView.setAdapter(adapter);

		// install handler
		jobListView.setOnItemClickListener(this);

		// hilight the first entry in the list...
		jobListView.setSelection(0);

	}

	public void onItemClick(AdapterView parent, View v, int position, long id) 
	{
		JobEntry je = _joblist.getJob(position);

		Log.i("CH12", "job clicked! [" + je.get_jobid() + "]");

		// a Job has been selected, let's get it ready to display
		Intent jobintent = new Intent(v.getContext(), ShowJob.class);

		// use the toBundle() helper method to assist in pushing
		// data across the "Activity" boundary
		Bundle b = je.toBundle();
		//jobintent.putExtra("android.intent.extra.INTENT", b);
		jobintent.putExtras(b);
		// we start this as a "sub" activity, because it may get updated
		// and we need to track that (in the method below OnActivityResult)
		startActivityForResult(jobintent, SHOWJOB);
	}


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	switch (requestCode)
    	{
    		case SHOWJOB:
    			if (resultCode == 1)
    			{
    				Log.d("CH12","Good Close, let's update our list");
    				// pull the JobEntry out of the bundle
    				Bundle bundle = data.getExtras();
    				JobEntry je = JobEntry.fromBundle(bundle);
    				// update our list of jobs
    				_joblist.replace(je);
    			}
    			break;
    	}
    	
    }
    
}