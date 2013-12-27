package se.lundakarnevalen.remote;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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
	private String remoteAdr = "http://eee.esek.se/";
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
     * Do a HTTP POST to the server for a plain/text response, use TextResultListener to get response.  
     * @param file The file to do the http POST on
     * @param data The POST data.
     */
    public void requestServerForText(String file, String data){
    	if(hasInternetConnection(context)){
    		AsyncTask<String, Void, String> task = new ServerTextTask();
    		task.execute(file, data);
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
			String file, data;
			try{
				file = params[0];
				data = params[1];
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
				con.setRequestMethod("POST");
			} catch (ProtocolException e) {
				Log.wtf(LOG_TAG, "Should not happen - No such post method");
				return null;
			}
			con.setDoInput(true);
			con.setDoOutput(true);
			//
			
			con.setRequestProperty("Content-type", "text/plain");
			con.setRequestProperty("charset", "UTF-8");
			
			// Write post data. 
			DataOutputStream dos;
			try {
				
				dos = new DataOutputStream(con.getOutputStream());
			} catch (IOException e) {
				Log.e(LOG_TAG, "Could not init. output stream.");
				return null;
			}
			try {
				dos.writeBytes(data);
				dos.flush();
				dos.close();
			} catch (IOException e) {
				Log.e(LOG_TAG, "Could not write data to server.");
				return null;
			}
			// Get data input stream
			InputStreamReader isr;
			try {
				Log.d(LOG_TAG, "Response: "+con.getResponseCode());
				isr = new InputStreamReader((InputStream) con.getContent());
			} catch (IOException e) {
				Log.e(LOG_TAG, "Could not open input stream to " + url.getPath() + ((con == null) ? " con was null" : "con was NOT null"));
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
		public void onResult(String result);
	}
}
