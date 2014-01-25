package se.lundakarnevalen.android;

import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import android.os.Bundle;

public class AboutFragment extends LKFragment{
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		setTitle("Om appen");
		
		/**
		 * Exempel för att använda remote klassen
		 * Ni behöver inte köra detta i en egen tråd, det sker automagiskt i LKRemote.
		 * För att ändra URL:en till servern, öppna klassen LKRemote och ändra värdet på variabeln remoteAdr till något annat. 
		 */
		LKRemote remote = new LKRemote(getContext()); // Skapa ett remote-objekt och skicka in context.
		remote.showProgressDialog(true); // Gör att en popup med en spinner visas (false som standard), raden måste alltså inte finnas med 
		remote.setTextResultListener(new TextResultListener(){
			@Override
			public void onResult(String result) {
				// Metod som körs på UI tråden när server returnerar ett resultat
				// Det som servern sporttar ut fås alltså som en String i result.
				// Skriv alltså kod här som tar hand om datan. 
			}
		});
		remote.requestServerForText("login.php", "name=alex&pass=abc"); // Gör en request till servern. Första parametern är FILEN som ska kallas, den andra är datan som skickas (skicka data som vanlig http post data).
		
	}
}
