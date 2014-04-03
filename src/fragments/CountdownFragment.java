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
		Typeface tf = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/Roboto-Bold.ttf");
		tvKarneval.setTypeface(tf);

		Date karneVal = new GregorianCalendar(2014, 4, 16, 14, 0, 0).getTime();
		Date karneLan = new GregorianCalendar(2014, 4, 16, 14, 0, 0).getTime();
		Date preKarneval = new GregorianCalendar(2014, 3, 15, 11, 0, 0)
				.getTime();
		Date karneBeer = new GregorianCalendar(2014, 3, 15, 11, 0, 0).getTime();
		Date postKarneval = new GregorianCalendar(2014, 3, 15, 11, 0, 0)
				.getTime();
		Date today = new Date();

		long diffKarneval = karneVal.getTime() - today.getTime();
		long diffKarnelan = karneLan.getTime() - today.getTime();
		long diffPreKarneval = preKarneval.getTime() - today.getTime();
		long diffKarnebeer = karneBeer.getTime() - today.getTime();
		long diffPostKarneval = postKarneval.getTime() - today.getTime();

		CountDownTask countDownTask = new CountDownTask();
		countDownTask.execute(diffKarneval, diffKarnelan, diffPreKarneval, diffKarnebeer, diffPostKarneval);
	}

	private class CountDownTask extends AsyncTask<Long, Long, Long> {

		@Override
		protected Long doInBackground(Long... params) {

			final long diffKarneval = params[0];
			final long diffKarneLan = params[1];
			final long diffPreKarneval = params[2];
			final long diffKarnebeer = params[3];
			final long diffPostKarneval = params[4];
			
			publishProgress(diffKarneval, diffKarneLan, diffPreKarneval, diffKarnebeer, diffPostKarneval);
			return (long) 0;
		}

		@Override
		protected void onProgressUpdate(Long... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			long diffKarneval = values[0];
			long diffKarneLan = values[1];
			long diffPreKarneval = values[2];
			long diffKarnebeer = values[3];
			long diffPostKarneval = values[4];

			new CountDownTimer(diffKarneval, 1000){
				@Override
				public void onFinish() {
				}

				@Override
				public void onTick(long millisUntilFinished) {
					tvKarneval.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();
			
			new CountDownTimer(diffKarneLan, 1000){
				@Override
				public void onFinish() {
					//Change into checked
				}
				@Override
				public void onTick(long millisUntilFinished) {
					tvKarnelan.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();
			
			new CountDownTimer(diffPreKarneval, 1000){
				@Override
				public void onFinish() {
					//Change into checked
				}
				@Override
				public void onTick(long millisUntilFinished) {
//					tvKarnelan.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();
			
			new CountDownTimer(diffKarnebeer, 1000){
				@Override
				public void onFinish() {
					//Change into checked
				}
				@Override
				public void onTick(long millisUntilFinished) {
//					tvKarnelan.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();
			
			new CountDownTimer(diffPostKarneval, 1000){
				@Override
				public void onFinish() {
					//Change into checked
				}
				@Override
				public void onTick(long millisUntilFinished) {
//					tvKarnelan.setText(getCountdownMessage(millisUntilFinished));
				}
			}.start();
		}

		@Override
		protected void onPostExecute(Long result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
	private String getCountdownMessage(long diffKarneval){
		long seconds = diffKarneval / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;
		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 24;
		String countDownMessage = String.format("%02d:%02d:%02d:%02d",
				days, hours, minutes, seconds);
		return countDownMessage;
	}
}
