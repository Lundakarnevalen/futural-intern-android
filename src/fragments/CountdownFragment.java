package fragments;

import java.util.Date;
import java.util.GregorianCalendar;
import se.lundakarnevalen.android.R;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CountdownFragment extends LKFragment {

	TextView tvKarneval;
	TextView tvKarnelan;
	long diffKarneVad;
	private String antonsTestHAHAHAAHA;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Change the layout
		View rootView = inflater.inflate(R.layout.fr_layout_countdown, null);
		tvKarneval = (TextView) rootView.findViewById(R.id.tvKarneval);
		tvKarnelan = (TextView) rootView.findViewById(R.id.tvKarneLan);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setTitle("Countdown");
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),"fonts/Roboto-Bold.ttf");
		tvKarneval.setTypeface(tf);
		
		Date karneVal = new GregorianCalendar(2014, 4, 16, 14, 0, 0).getTime();
		Date karneLan = new GregorianCalendar(2014, 4, 16, 14, 0, 0).getTime();
		Date preKarneval = new GregorianCalendar(2014, 3, 15, 11, 0, 0).getTime();
		Date karneBeer = new GregorianCalendar(2014, 3, 15, 11, 0, 0).getTime();
		Date postKarneval = new GregorianCalendar(2014, 3, 15, 11, 0, 0).getTime();
		Date today = new Date();
		
		long diffKarneval = karneVal.getTime() - today.getTime();
		long diffKarneVad = karneLan.getTime() - today.getTime();
		
		CountDownTask countDownTask = new CountDownTask();
		countDownTask.execute(diffKarneval,diffKarneVad);
	}

	private void displayProgressKarneval(String countDownMessage) {
		tvKarneval.setText(countDownMessage);
	}
	private void displayProgressKarnelan(String countDownMessageVad) {
		tvKarnelan.setText(countDownMessageVad);
	}

	private class CountDownTask extends AsyncTask<Long, Long, Long> {

		@Override
		protected Long doInBackground(Long... params) {

			final long diffKarneval = params[0];
			final long diffKarneLan = params[1];
			publishProgress(diffKarneval,diffKarneLan);
			return diffKarneval;
		}
		@Override
		protected void onProgressUpdate(Long... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			long diffKarneval = values[0];
			long diffKarneLan = values[1];
			
			new CountDownTimer(diffKarneval, 1000) {
				@Override
				public void onTick(long diffKarneval) {
					// TODO Auto-generated method stub
					long seconds = diffKarneval / 1000;
					long minutes = seconds / 60;
					long hours = minutes / 60;
					long days = hours / 24;
					seconds = seconds % 60;
					minutes = minutes % 60;
					hours = hours % 24;
					String countDownMessage = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
					displayProgressKarneval(countDownMessage);
				}
				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					tvKarneval.setText("Done");
				}
			}.start();
			
			new CountDownTimer(diffKarneLan, 1000) {
				@Override
				public void onTick(long diffKarneLan) {
					// TODO Auto-generated method stub
					long seconds = diffKarneLan / 1000;
					long minutes = seconds / 60;
					long hours = minutes / 60;
					long days = hours / 24;
					seconds = seconds % 60;
					minutes = minutes % 60;
					hours = hours % 24;
					String countDownMessageVad = String.format("%02d:%02d:%02d:%02d", days, hours, minutes, seconds);
					displayProgressKarnelan(countDownMessageVad);
				}
				
				@Override
				public void onFinish() {
					// TODO Auto-generated method stub
					tvKarnelan.setText("Done");
				}
			}.start();
			
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
}
