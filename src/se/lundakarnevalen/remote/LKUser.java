package se.lundakarnevalen.remote;

import se.lundakarnevalen.android.LKFragment;
import json.Response;
import json.User;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

/**
 * Class representing a user. 
 * @author alexander
 *
 */
public class LKUser {
	private static final String LOG_TAG = "LKUser";
	private static final String SHARED_PREFS_NAME = "LKUserStorage";
	private static final String SHARED_PREFS_JSON = "LKUserToken";
	
	Context context;
	public String personnummer, fornamn, efternamn, gatuadress, postnr, postort, email, telnr, matpref, engageradKar, engageradNation, engageradStudentikos, engageradEtc, ovrigt;
	public int kon, nation, storlek, terminer, korkort, snallaIntresse, snallaSektion;
	public int[] intresse, sektioner;
	public boolean jobbatHeltid, jobbatStyrelse, jobbatForman, jobbatAktiv, karnevalist2010, villAnsvara, medlemAf, medlemKar, medlemNation, karneveljsbiljett;
	SharedPreferences sp;
	
	public LKUser(Context context){
		this.context = context;
		sp = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
	}
	
	/**
	 * Check if there is a user stored localy on this device .
	 * @param context The application context
	 * @return True if there is a user, false if there is not. 
	 */
	public static boolean localUserStored(Context context){
		SharedPreferences sp = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		String json = sp.getString(SHARED_PREFS_JSON, null);
		return json != null;
	}
	
	/**
	 * Store instance of user in SP. 
	 */
	public void storeUserLocaly(){
		Editor editor = sp.edit();
		editor.putString(SHARED_PREFS_JSON, getJson());
		editor.commit();
	}
	
	public String getJson(){
		Gson gson = new Gson();
		User karnevalist = new User();
		karnevalist.fornamn = this.fornamn;
		karnevalist.efternamn = this.efternamn;
		karnevalist.gatuadress = this.gatuadress;
		karnevalist.postnr = this.postnr;
		karnevalist.postort = this.postort;
		karnevalist.email = this.email;
		karnevalist.telnr = this.telnr;
		karnevalist.matpref = this.matpref;
		karnevalist.engagerad_kar = this.engageradKar;
		karnevalist.engagerad_nation = this.engageradNation;
		karnevalist.engagerad_studentikos = this.engageradStudentikos;
		karnevalist.engagerad_etc = this.engageradEtc;
		karnevalist.ovrigt = this.ovrigt;
		karnevalist.kon_id = this.kon;
		karnevalist.nation_id = this.nation;
		karnevalist.storlek_id = this.storlek;
		karnevalist.terminer = this.terminer;
		karnevalist.korkort_id = this.korkort;
		karnevalist.snalla_intresse = this.snallaIntresse;
		karnevalist.snalla_sektion = this.snallaSektion;
		karnevalist.intresse_ids = this.intresse;
		karnevalist.sektion_ids = this.sektioner;
		karnevalist.jobbat_heltid = this.jobbatHeltid;
		karnevalist.jobbat_aktiv = this.jobbatAktiv;
		karnevalist.jobbat_forman = this.jobbatForman;
		karnevalist.jobbat_styrelse = this.jobbatStyrelse;
		karnevalist.karnevalist_2010 = this.karnevalist2010;
		karnevalist.vill_ansvara = this.villAnsvara;
		karnevalist.medlem_af = this.medlemAf;
		karnevalist.medlem_kar = this.medlemKar;
		karnevalist.medlem_nation = this.medlemNation;
		karnevalist.karneveljsbiljett = this.karneveljsbiljett;
		return gson.toJson(karnevalist);
	}
	
	public void parseJson(String json){
		Gson gson = new Gson();
		Response.GetKarnevalist user = gson.fromJson(json, Response.GetKarnevalist.class);
		this.personnummer = user.karnevalist.personnummer;
		this.fornamn = user.karnevalist.fornamn;
		this.efternamn = user.karnevalist.efternamn;
		this.gatuadress = user.karnevalist.gatuadress;
		this.postnr = user.karnevalist.postnr;
		this.postort = user.karnevalist.postort;
		this.email = user.karnevalist.email;
		this.telnr = user.karnevalist.telnr;
		this.matpref = user.karnevalist.matpref;
		this.engageradKar = user.karnevalist.engagerad_kar;
		this.engageradNation = user.karnevalist.engagerad_nation;
		this.engageradStudentikos = user.karnevalist.engagerad_studentikos;
		this.engageradEtc = user.karnevalist.engagerad_etc;
		this.ovrigt = user.karnevalist.ovrigt;
		this.kon = user.karnevalist.kon_id;
		this.nation = user.karnevalist.nation_id;
		this.storlek = user.karnevalist.storlek_id;
		this.terminer = user.karnevalist.terminer;
		this.korkort = user.karnevalist.korkort_id;
		this.snallaIntresse = user.karnevalist.snalla_intresse;
		this.snallaSektion = user.karnevalist.snalla_sektion;
		this.intresse = user.karnevalist.intresse_ids;
		this.sektioner = user.karnevalist.sektion_ids;
		this.jobbatHeltid = user.karnevalist.jobbat_heltid;
		this.jobbatAktiv = user.karnevalist.jobbat_aktiv;
		this.jobbatForman = user.karnevalist.jobbat_forman;
		this.jobbatStyrelse = user.karnevalist.jobbat_styrelse;
		this.karnevalist2010 = user.karnevalist.karnevalist_2010;
		this.villAnsvara = user.karnevalist.vill_ansvara;
		this.medlemAf = user.karnevalist.medlem_af;
		this.medlemKar = user.karnevalist.medlem_kar;
		this.medlemNation = user.karnevalist.medlem_nation;
		this.karneveljsbiljett = user.karnevalist.karneveljsbiljett;
	}
}
