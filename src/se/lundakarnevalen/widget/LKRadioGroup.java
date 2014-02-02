package se.lundakarnevalen.widget;

import java.util.ArrayList;
import java.util.List;

import se.lundakarnevalen.android.R;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class LKRadioGroup {

    ArrayList<RadioButton> radios = new ArrayList<RadioButton>();
    ArrayList<Integer> radioids = new ArrayList<Integer>();
    Context context;
    /**
     * Constructor, which allows you to pass number of RadioButtons 
     * represented by resource IDs, making a group.
     * 
     * @param activity
     *            Current View (or Activity) to which those RadioButtons 
     *            belong.
     * @param radiosIDs
     *            One RadioButton or more.
     */
    public LKRadioGroup(Context context, View activity, int... radiosIDs) {
        super();
        this.context = context;
        for (int radioButtonID : radiosIDs) {
            RadioButton rb = (RadioButton)activity.findViewById(radioButtonID);
            if (rb != null) {
                this.radios.add(rb);
                this.radioids.add(radioButtonID);
                rb.setOnClickListener(onClick);
            }
        }
    }
    public int getActiveRB(){
    	for(int i = 0; i<radios.size(); i++) {
    		if(radios.get(i).isChecked()) {
    			return radioids.get(i);
    		}
    	}
    	return 0;
    }

    /**
     * This occurs everytime when one of RadioButtons is clicked, 
     * and deselects all others in the group.
     */
    OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
        	if(v.getId() == R.id.radio_dd_15) { //OBS SJUK HRDKODAT MSTE FIXAS OM NGOT €NDRAS
        		AlertDialog.Builder builder = new AlertDialog.Builder(context);
    			builder.setTitle("Superviktigt!");
    			builder.setMessage("Fšr dessa funktioner sŒ mŒste du komma pŒ audition! Auditionstid bokas hos Nšjessektionen.");
    			builder.setPositiveButton("Ok", null);
    			builder.show();
        	}
            // let's deselect all radios in group
            for (RadioButton rb : radios) {

                ViewParent p = rb.getParent();
                if (p.getClass().equals(RadioGroup.class)) {
                    // if RadioButton belongs to RadioGroup, 
                    // then deselect all radios in it 
                    RadioGroup rg = (RadioGroup) p;
                    rg.clearCheck();
                } else {
                    // if RadioButton DOES NOT belong to RadioGroup, 
                    // just deselect it
                    rb.setChecked(false);
                }
            }

            // now let's select currently clicked RadioButton
            if (v.getClass().equals(RadioButton.class)) {
                RadioButton rb = (RadioButton) v;
                rb.setChecked(true);
            }

        }
    };

}