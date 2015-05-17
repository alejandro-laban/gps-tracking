package aels.test.gpssender;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class Sender {
	
	final JSONParser jsonParser = new JSONParser();
	
	public void send(String Fname,String Lname,String Id,String LAT,String LON){

	// Building Parameters
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("fName", Fname));
    params.add(new BasicNameValuePair("lName", Lname));
    params.add(new BasicNameValuePair("id", Id));
    params.add(new BasicNameValuePair("lat", LAT));
    params.add(new BasicNameValuePair("lon", LON ));

    // getting JSON Object
    // Note that create product url accepts POST method
    JSONObject json = jsonParser.makeHttpRequest("http://192.168.0.14/insert.php",
            "POST", params);

    // check for success tag
    /*try {
        int success = json.getInt("code");

        if (success == 1) {
            // closing this screen
            //finish();
        } else {
            // failed to create product
        }
    } catch (JSONException e) {
        e.printStackTrace();
    }*/
	
	}
}
