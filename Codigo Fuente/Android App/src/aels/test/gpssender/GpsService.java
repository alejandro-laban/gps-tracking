package aels.test.gpssender;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class GpsService extends Service implements LocationListener  {
	
	 IBinder binder = new LocalBinder();
	
	public	LocationManager locationManager;
	
	private double latitud;
	
	private double longitud;
	
	private boolean serviceStatus=false;
	
	private boolean gpsEnabled=false;

	private boolean netEnabled=false;
	
	public Context context;
	
	public String status;
	
	public static String BROAD_LAT="latitud";
	
	public static String BROAD_LON="longitud";
	
	public static String ACTION_LOCATION_BROADCAST = GpsService.class.getName()+"LocationBroadCast";
	
	
	public boolean isGpsEnabled() {
		return gpsEnabled;
	}

	public void setGpsEnabled(boolean gpsEnabled) {
		this.gpsEnabled = gpsEnabled;
	}

	public boolean isNetEnabled() {
		return netEnabled;
	}

	public void setNetEnabled(boolean netEnabled) {
		this.netEnabled = netEnabled;
	}

	
	public boolean isServiceStatus() {
		return serviceStatus;
	}

	public void setServiceStatus(boolean serviceStatus) {
		this.serviceStatus = serviceStatus;
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}
	
//	public GpsService(Context ct){
//		context=ct;
//	}
	
	public class LocalBinder extends Binder{
		
		GpsService getService(){
			return GpsService.this;
		}
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		status="OK";
		
		try {
			
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			netEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
		} catch (Exception e) {
			
			serviceStatus=false;
			gpsEnabled=false;
			netEnabled=false;
			status = "getSystemService";
			
			Toast.makeText(context,status,Toast.LENGTH_SHORT).show();
			
		}
		
		if(netEnabled){
		
			try {
			
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, GpsService.this);
			} catch (Exception e) {
			
				netEnabled = false;
				status = "NETWORK_PROVIDER";
				Toast.makeText(context,status,Toast.LENGTH_SHORT).show();
			}
		}
		
		if(gpsEnabled){
			
			try {
			
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, GpsService.this);
			} catch (Exception e) {
			
				gpsEnabled = false;
				status = "GPS_PROVIDER";
				Toast.makeText(context,status,Toast.LENGTH_SHORT).show();
			}
		}
		serviceStatus=true;
		//Toast.makeText(context,status,Toast.LENGTH_SHORT).show();
		
		return START_STICKY;
	}
	
	@Override
	public void onCreate() {
		
		context = getApplicationContext();
		
		status="OK";
		
		try {
			
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			
			gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			
			netEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			
		} catch (Exception e) {
			
			serviceStatus=false;
			gpsEnabled=false;
			netEnabled=false;
			status = "getSystemService";
			
			Toast.makeText(context,status,Toast.LENGTH_SHORT).show();
			
		}
		
		if(netEnabled){
		
			try {
			
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10, GpsService.this);
			} catch (Exception e) {
			
				netEnabled = false;
				status = "NETWORK_PROVIDER";
				Toast.makeText(context,status,Toast.LENGTH_SHORT).show();
			}
		}
		
		if(gpsEnabled){
			
			try {
			
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, GpsService.this);
			} catch (Exception e) {
			
				gpsEnabled = false;
				status = "GPS_PROVIDER";
				Toast.makeText(context,status,Toast.LENGTH_SHORT).show();
			}
		}
		serviceStatus=true;
		//Toast.makeText(context,status,Toast.LENGTH_SHORT).show();
		
	}

	
	private void setLocation(Location location) {
		
		latitud=location.getLatitude();
		longitud=location.getLongitude();
		
	}
	

	@Override
	public void onLocationChanged(Location location) {
		
		setLocation(location);
		Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(BROAD_LAT, location.getLatitude());
        intent.putExtra(BROAD_LON, location.getLongitude());
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        
	}

	@Override
	public void onProviderDisabled(String provider) {
		
		if(provider.equals(LocationManager.GPS_PROVIDER))
			gpsEnabled=false;
		
		if(provider.equals(LocationManager.NETWORK_PROVIDER))
			netEnabled=false;
	}

	@Override
	public void onProviderEnabled(String provider) {
		
		if(provider.equals(LocationManager.GPS_PROVIDER))
			gpsEnabled=true;
		
		if(provider.equals(LocationManager.NETWORK_PROVIDER))
			netEnabled=true;
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle arg2) {
		
		if(provider.equals(LocationManager.GPS_PROVIDER)){
			if(status == LocationProvider.AVAILABLE)
			gpsEnabled=true;
			else if(status ==LocationProvider.OUT_OF_SERVICE)
				gpsEnabled=false;
		}
		
		if(provider.equals(LocationManager.NETWORK_PROVIDER))
			if(status == LocationProvider.AVAILABLE)
				gpsEnabled=true;
			else if(status ==LocationProvider.OUT_OF_SERVICE)
					gpsEnabled=false;
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}
	
	@Override
	public void onDestroy() {
		
		locationManager.removeUpdates(GpsService.this);
		serviceStatus= false;
		Toast.makeText(context, "service done", Toast.LENGTH_SHORT).show();
		
	}

}
