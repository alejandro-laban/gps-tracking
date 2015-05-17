package aels.test.gpssender;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import aels.test.gpssender.GpsService.LocalBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GpsSenderActivity extends ActionBarActivity {
	
	
	static final LatLng UTP = new LatLng (-12.064557, -77.036653);
	LatLng currentPos ;
	private GoogleMap map;
	
	Intent intentGpsService;
	
	boolean boundService=false;
	
	GpsService service;
	
	Sender send;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps_sender);
		
		intentGpsService = new Intent(GpsSenderActivity.this, GpsService.class);
		startService(intentGpsService);
		
		send = new Sender();
		
		final TextView lat = (TextView) findViewById(R.id.latitudT);
		 
			      
		final TextView lon = (TextView) findViewById(R.id.longitudT);
		
		map = ((SupportMapFragment) getSupportFragmentManager ()
				.findFragmentById(R.id.map))
				.getMap();
		    
		
		if (map!=null){
			Marker utp = map.addMarker(new MarkerOptions()
			.position(UTP)
		    .title("UTP")
		    .snippet("universidad")
		    .icon(BitmapDescriptorFactory
		    .fromResource(R.drawable.ic_launcher)));
		    	
		    map.moveCamera(CameraUpdateFactory.newLatLngZoom(UTP, 17));
		    utp.setPosition(UTP);
		    }
		
		LocalBroadcastManager.getInstance(this).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        
                        double latitude = intent.getDoubleExtra(GpsService.BROAD_LAT, 0);
                        double longitude = intent.getDoubleExtra(GpsService.BROAD_LON, 0);
                        
                        lat.setText("Latitud: "+String.valueOf(latitude));
                        lon.setText("Longitud: "+String.valueOf(longitude));
                        
                        currentPos = new LatLng (latitude,longitude);
                        
                        map.addMarker(new MarkerOptions().position(currentPos));

               		 	send.send("FNAME","LNAME","ID001",String.valueOf(latitude),String.valueOf(longitude));
               		 	
                        
                    }
                }, new IntentFilter(GpsService.ACTION_LOCATION_BROADCAST)
        );
		    
		    
	}

	public void gps(View view){
		
		ToggleButton toggle = (ToggleButton) view.findViewById(R.id.toggleButton1);
		
		String status;

		if (toggle.isChecked()){
			
			if( isMyServiceRunning(GpsService.class) )
				status = "Service already started";
			else
				status = "Service already stopped";
			
			
			Toast.makeText(GpsSenderActivity.this, status, Toast.LENGTH_SHORT).show();
			
			if(!service.isGpsEnabled()){
				showSettingsAlert();
				service.onCreate();
				if(!service.isGpsEnabled())
					toggle.setChecked(false);
			}
			else{
				map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 17));
			}
			
			//startService(intentGpsService);

		}
		
		else {
			
			if( isMyServiceRunning(GpsService.class) )
				status = "service was running";
			else
				status = "service was already stopped";
			
			Toast.makeText(GpsSenderActivity.this, status, Toast.LENGTH_SHORT).show();
			
			
			//stopService(intentGpsService);
		}
	}
	
	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GpsSenderActivity.this);
      
        alertDialog.setTitle("GPS settings");
  
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
       
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                GpsSenderActivity.this.startActivity(intent);
            }
        });
  
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
            }
        });
        
        alertDialog.show();
        
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.gps_sender, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, GpsService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
	
	@Override
    protected void onStop() {
        super.onStop();
        
        if (boundService) {
            unbindService(mConnection);
            boundService = false;
        }
    }
	
	private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder iService) {

            LocalBinder binder = (LocalBinder) iService;
            GpsSenderActivity.this.service = (GpsService) binder.getService();
            boundService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        	boundService = false;
        }
    };

}
