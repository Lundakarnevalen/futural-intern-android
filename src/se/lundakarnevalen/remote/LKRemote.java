package se.lundakarnevalen.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.ProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Class used to talk to the server API. 
 * @author alexander najafi
 *
 */

public class LKRemote {
	
	private final String LOG_TAG = "API call";
	
	Context context;
	private String remoteAdr = "http://www.karnevalist.se/";
	//private String remoteAdr = "http://httpbin.org/put";
	private boolean showProgressDialog = false;
	TextResultListener textResultListener;
	
	/**
	 * Creates remote object and sets a listener for the result
	 * @param context Application context
	 */
	public LKRemote(Context context){
		this.context = context;
	}
	
	/**
	 * Creates remote object and sets a listener for the result
	 * @param context Application context
	 * @param textResultListener Listener called when result is returned by server.
	 */
	public LKRemote(Context context, TextResultListener textResultListener){
		this.context = context; 
		this.textResultListener = textResultListener;
	}
	
	/**
	 * Sets a text result listener
	 * @param textResultListener the listener.
	 */
	public void setTextResultListener(TextResultListener textResulListener){
		this.textResultListener = textResultListener;
	}
	
	/**
	 * Show a progressbar when performing the post. 
	 * @param showProgressBar
	 */
	public void showProgressDialog(boolean showProgressDialog){
		this.showProgressDialog = showProgressDialog;
	}
	
	/**
     * Checks if device is connected to internet. 
     * @return True if internet is available, otherwise false. 
     */
    public static boolean hasInternetConnection(Context context){
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivity != null){
                    NetworkInfo[] info = connectivity.getAllNetworkInfo();
                    if(info != null)
                            for(int n = 0; n < info.length; n++){
                                    if(info[n].getState() == NetworkInfo.State.CONNECTED){
                                            return true;
                                    }
                            }
            }
            return false;
    }
    
    /**
     * Returns a string for the request type based on the Enum. 
     * @param type The request type
     * @return The string for the request type. 
     */
    private String getRequestTypeString(RequestType type){
    	switch(type){
    	case POST:
    		return "POST";
    	case GET:
    		return "GET";
    	case PUT:
    		return "PUT";
    	default:
    		return "GET";
    	}
    }
    
    /**
     * Do a HTTP POST to the server for a plain/text response, use TextResultListener to get response.  
     * @param file The file
     * @param data The data
     */
    public void requestServerForText(String file, String data, RequestType type){
    	String requestType = getRequestTypeString(type);
    	
    	if(hasInternetConnection(context)){
    		AsyncTask<String, Void, String> task = new ServerTextTask();
    		task.execute(file, data, requestType);
    	}else{
    		Log.e(LOG_TAG, "no internet connection");
    	}
    }

	
	/**
	 * Inner class used to perform http calls with a textresponse. 
	 * @author Alexander Najafi
	 */
	class ServerTextTask extends AsyncTask<String, Void, String>{
		
		ProgressDialog progressDialog;
		boolean progressDialogVisible;
		
		@Override
		protected void onPreExecute(){
			if(showProgressDialog){
				// Create and show progress dialog.
				buildProgressDialog();
			}
		}
		
		protected void buildProgressDialog(){
			progressDialog = new ProgressDialog(context);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		}
		
		protected void hideProgressDialog(){
			progressDialog.cancel();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String file, data, requestType;
			boolean write = true;
			try{
				file = params[0];
				data = params[1];
				requestType = params[2];
				if(requestType.equals(getRequestTypeString(RequestType.GET))){
					write = false;
				}
			}catch(ArrayIndexOutOfBoundsException e){
				Log.e(LOG_TAG, "No parameters, post data or file is missing. usage: [file, data]");
				return null;
			}
			
			URL url;
			try {
				url = new URL(remoteAdr+file);
			} catch (MalformedURLException e) {
				Log.e(LOG_TAG, "Malformed URL");
				return null;
			}
			
			HttpURLConnection con;
			try {
				con = (HttpURLConnection) url.openConnection();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Could not open connection to: "+url.getPath());
				return null;
			}
			if(con == null){
			Log.e(LOG_TAG, "con was null");
				return null;
			}
			try {
				con.setRequestMethod(requestType);
				Log.d(LOG_TAG, "Set request method");
			} catch (java.net.ProtocolException e) {
				Log.e(LOG_TAG, "no such protocol");
			}
			con.setRequestProperty("Content-Type", "application/json; charset=utf-8");   
			con.setRequestProperty("Charset", "UTF-8");

			con.setUseCaches(false);
			con.setDoInput(true);
			con.setDoOutput(true);
			
			/*Log.i(LOG_TAG, "Will now open stream for writing with:");
			for (String header : con.getRequestProperties().keySet()) {
				   if (header != null) {
				     for (String value : con.getRequestProperties().get(header)) {
				        Log.i(header, value);
				      }
				   }
			}*/
			
			try{
				con.connect();
			}catch(IOException e){ 
				Log.e(LOG_TAG, "Could not connect.");
				return null;
			}
			// Write data. 
			if(write){
				OutputStreamWriter dos;
				try {
					dos = new OutputStreamWriter(con.getOutputStream());
				} catch (IOException e) {
					Log.e(LOG_TAG, "Could not init. output stream.");
					return null;
				}
				try {
					Log.d(LOG_TAG, "data:"+data);
					dos.write(data); // should write data. 
					dos.flush();
					Log.d(LOG_TAG, "Flushed");
					dos.close();
					Log.d(LOG_TAG, "dos closed");
					Log.d(LOG_TAG, "wrote: "+data);
				} catch (IOException e) {
					Log.e(LOG_TAG, "Could not write data to server.");
					return null;
				}
			}
			
			InputStreamReader isr;
			try {
				//Log.d(LOG_TAG, "Response: "+con.getResponseCode());
				isr = new InputStreamReader((InputStream) con.getContent());
			} catch (IOException e) {
				Log.e(LOG_TAG, "Could not open input stream to " + url.getPath() + ((con == null) ? " con was null" : " con was NOT null"+" "+	e));
				return null;
			}
			// Read data from stream
			BufferedReader br = new BufferedReader(isr);
			StringBuilder result = new StringBuilder();
			String line;
			try {
				while((line = br.readLine()) != null){
					result.append(line);
				}
			} catch (IOException e) {
				Log.e(LOG_TAG, "Failed when reading from stream");
				return null;
			}
			
			try {
				br.close();
				isr.close();
				con.disconnect();
			} catch (IOException e) {
				Log.w(LOG_TAG, "Could not close connection");
			}
			
			return result.toString();
		}
		
		@Override
		protected void onPostExecute(String result){
			if(showProgressDialog){
				hideProgressDialog();
			}
			
			if(result == null)
				Log.e(LOG_TAG, "Warning result was null");
			
			if(textResultListener != null){
				textResultListener.onResult(result);
			}			
		}
		
		@Override
		protected void onCancelled(String result){
			if(showProgressDialog){
				hideProgressDialog();
			}
			
			if(result == null)
				Log.e(LOG_TAG, "WARNING - THE CALL WAS CANCELED");
			
			if(textResultListener != null){
				textResultListener.onResult(result);
			}	
		}
		
	}
	
	/**
	 * Interface for callback on text result from server call. 
	 * @author Alexander Najafi
	 *
	 */
	public interface TextResultListener{
		public static final String LOG_TAG = "Result listener";
		public void onResult(String result);
	}
	
	public enum RequestType{
		POST, GET, PUT;
	}
}
