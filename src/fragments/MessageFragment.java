package fragments;

import se.lundakarnevalen.android.R;
import se.lundakarnevalen.widget.LKInboxArrayAdapter;
import se.lundakarnevalen.widget.LKTextView;
import se.lundakarnevalen.widget.LKTextViewBold;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MessageFragment extends LKFragment{
	
	LKInboxArrayAdapter.LKMenuListItem listItem;
	
	public void setListItem(LKInboxArrayAdapter.LKMenuListItem listItem){
		this.listItem = listItem;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View root = inflater.inflate(R.layout.message_layout, null);
		LKTextViewBold titleTextView = (LKTextViewBold) root.findViewById(R.id.message_title);
		LKTextViewBold dateTextView = (LKTextViewBold) root.findViewById(R.id.message_date);
		LKTextView messageTextView = (LKTextView) root.findViewById(R.id.message_message_text);
		LKTextViewBold sectionTextView = (LKTextViewBold) root.findViewById(R.id.message_image_text);
		ImageView thumbnailImageView = (ImageView) root.findViewById(R.id.message_thumbnail);
		
		titleTextView.setText(listItem.title.toUpperCase());
		dateTextView.setText(listItem.date);
		messageTextView.setText(listItem.message);
		thumbnailImageView.setImageBitmap(listItem.image);
		
		switch (listItem.recipients) {
		case 0:
			thumbnailImageView.setImageResource(R.drawable.icon);
			sectionTextView.setText("Karnevalen");
			break;
		case 1:
			thumbnailImageView.setImageResource(R.drawable.barnevalen);
			sectionTextView.setText(R.string.sektion_1);
			break;
		case 2:
			thumbnailImageView.setImageResource(R.drawable.biljetteriet);
			sectionTextView.setText(R.string.sektion_2);
			break;
		case 3:
			thumbnailImageView.setImageResource(R.drawable.bladderiet);
			sectionTextView.setText(R.string.sektion_3);
			break;
		case 4:
			thumbnailImageView.setImageResource(R.drawable.cirkusen);
			sectionTextView.setText(R.string.sektion_4);
			break;
		case 5:
			thumbnailImageView.setImageResource(R.drawable.dansen);
			sectionTextView.setText(R.string.sektion_5);
			break;
		case 6:
			thumbnailImageView.setImageResource(R.drawable.ekonomi);
			sectionTextView.setText(R.string.sektion_6);
			break;
		case 7:
			thumbnailImageView.setImageResource(R.drawable.kabaren);
			sectionTextView.setText(R.string.sektion_7);
			break;
		case 8:
			thumbnailImageView.setImageResource(R.drawable.fabriken);
			sectionTextView.setText(R.string.sektion_8);
			break;
		case 9:
			thumbnailImageView.setImageResource(R.drawable.klipperiet);
			sectionTextView.setText(R.string.sektion_9);
			break;
		case 10:
			thumbnailImageView.setImageResource(R.drawable.kommunikation);
			sectionTextView.setText(R.string.sektion_10);
			break;
		case 11:
			thumbnailImageView.setImageResource(R.drawable.krog);
			sectionTextView.setText(R.string.sektion_11);
			break;
		case 12:
			thumbnailImageView.setImageResource(R.drawable.krog);
			sectionTextView.setText(R.string.sektion_12);
			break;
		case 13:
			thumbnailImageView.setImageResource(R.drawable.krog);
			sectionTextView.setText(R.string.sektion_13);
			break;
		case 14:
			thumbnailImageView.setImageResource(R.drawable.krog);
			sectionTextView.setText(R.string.sektion_14);
			break;
		case 15:
			thumbnailImageView.setImageResource(R.drawable.krog);
			sectionTextView.setText(R.string.sektion_15);
			break;
		case 16:
			thumbnailImageView.setImageResource(R.drawable.omrade);
			sectionTextView.setText(R.string.sektion_16);
			break;
		case 17:
			thumbnailImageView.setImageResource(R.drawable.musiken);
			sectionTextView.setText(R.string.sektion_17);
			break;
		case 18:
			thumbnailImageView.setImageResource(R.drawable.radio);
			sectionTextView.setText(R.string.sektion_18);
			break;
		case 19:
			thumbnailImageView.setImageResource(R.drawable.revyn);
			sectionTextView.setText(R.string.sektion_19);
			break;
		case 20:
			thumbnailImageView.setImageResource(R.drawable.shoppen);
			sectionTextView.setText(R.string.sektion_20);
			break;
		case 21:
			thumbnailImageView.setImageResource(R.drawable.show);
			sectionTextView.setText(R.string.sektion_21);
			break;
		case 22:
			thumbnailImageView.setImageResource(R.drawable.snaxeriet);
			sectionTextView.setText(R.string.sektion_22);
			break;
		case 23:
			thumbnailImageView.setImageResource(R.drawable.spexet);
			sectionTextView.setText(R.string.sektion_23);
			break;
		case 24:
			thumbnailImageView.setImageResource(R.drawable.sakerhet);
			sectionTextView.setText(R.string.sektion_24);
			break;
		case 25:
			thumbnailImageView.setImageResource(R.drawable.tombolan);
			sectionTextView.setText(R.string.sektion_25);
			break;
		case 26:
			thumbnailImageView.setImageResource(R.drawable.vieriet);
			sectionTextView.setText(R.string.sektion_26);
			break;
		case 100:
			thumbnailImageView.setImageResource(R.drawable.festmasteriet);
			sectionTextView.setText(R.string.sektion_100);
			break;
		case 101:
			thumbnailImageView.setImageResource(R.drawable.festmasteriet);
			sectionTextView.setText(R.string.sektion_101);
			break;
		case 102:
			thumbnailImageView.setImageResource(R.drawable.festmasteriet);
			sectionTextView.setText(R.string.sektion_102);
			break;
		case 199:
			thumbnailImageView.setImageResource(R.drawable.festmasteriet);
			sectionTextView.setText(R.string.sektion_199);
			break;
		case 202:
			thumbnailImageView.setImageResource(R.drawable.smanojen);
			sectionTextView.setText(R.string.sektion_202);
			break;
		case 203:
			thumbnailImageView.setImageResource(R.drawable.smanojen);
			sectionTextView.setText(R.string.sektion_203);
			break;
		case 204:
			thumbnailImageView.setImageResource(R.drawable.smanojen);
			sectionTextView.setText(R.string.sektion_204);
			break;
		case 300:
			thumbnailImageView.setImageResource(R.drawable.tag);
			sectionTextView.setText(R.string.sektion_300);
			break;
		case 399:
			thumbnailImageView.setImageResource(R.drawable.tag);
			sectionTextView.setText(R.string.sektion_399);
			break;
		case 400:
			thumbnailImageView.setImageResource(R.drawable.nojen);
			sectionTextView.setText(R.string.sektion_400);
			break;
		case 499:
			thumbnailImageView.setImageResource(R.drawable.nojen);
			sectionTextView.setText(R.string.sektion_499);
			break;
		}
		
		//TODO:	Update the inbox counter
		this.setInboxCount();
		
		return root;
	}
}
