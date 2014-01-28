package se.lundakarnevalen.remote;

import json.Response;
import json.User;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

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
	public int id = Integer.MIN_VALUE;
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
	
	/**
	 * Get user from storage
	 * @return
	 */
	public void getUserLocaly(){
		String json = sp.getString(SHARED_PREFS_JSON, null);
		if(json != null)
			parseJson(json);
	}
	
	/**
	 * Updates user from remote. 
	 */
	public void updateFromRemote(){
		if(id != Integer.MIN_VALUE){
			LKRemote remote = new LKRemote(context, new LKRemote.TextResultListener(){
				@Override
				public void onResult(String result) {
					Log.d(LOG_TAG, "server: "+result);
					// Update user with data
					Gson gson = new Gson();
					Response.GetKarnevalist data = gson.fromJson(result, Response.GetKarnevalist.class);
					
					if(data.status.equals("success")){
						getDataFromUser(data.karnevalist);
						storeUserLocaly();
					}else{
						Log.e(LOG_TAG, "Non successfull request for id="+id+", status="+data.status);
					}
				}
			});
			remote.requestServerForText("karnevalister/"+id+".json", null, LKRemote.RequestType.GET);
			Log.d(LOG_TAG, "requested server for the user with id:"+id);
		}else{
			// No user downloaded.
			Log.e(LOG_TAG, "Found no user ID for user");
		}
	}
	
	public String getJson(){
		Gson gson = new Gson();
		User karnevalist = new User();
		karnevalist.fornamn = this.fornamn;
		karnevalist.id = this.id;
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
		Log.d(LOG_TAG, "Will now parse: "+json);
		Gson gson = new Gson();
		User user = gson.fromJson(json, User.class);
		this.personnummer = user.personnummer;
		this.fornamn = user.fornamn;
		this.id = user.id;
		this.efternamn = user.efternamn;
		this.gatuadress = user.gatuadress;
		this.postnr = user.postnr;
		this.postort = user.postort;
		this.email = user.email;
		this.telnr = user.telnr;
		this.matpref = user.matpref;
		this.engageradKar = user.engagerad_kar;
		this.engageradNation = user.engagerad_nation;
		this.engageradStudentikos = user.engagerad_studentikos;
		this.engageradEtc = user.engagerad_etc;
		this.ovrigt = user.ovrigt;
		this.kon = user.kon_id;
		this.nation = user.nation_id;
		this.storlek = user.storlek_id;
		this.terminer = user.terminer;
		this.korkort = user.korkort_id;
		this.snallaIntresse = user.snalla_intresse;
		this.snallaSektion = user.snalla_sektion;
		this.intresse = user.intresse_ids;
		this.sektioner = user.sektion_ids;
		this.jobbatHeltid = user.jobbat_heltid;
		this.jobbatAktiv = user.jobbat_aktiv;
		this.jobbatForman = user.jobbat_forman;
		this.jobbatStyrelse = user.jobbat_styrelse;
		this.karnevalist2010 = user.karnevalist_2010;
		this.villAnsvara = user.vill_ansvara;
		this.medlemAf = user.medlem_af;
		this.medlemKar = user.medlem_kar;
		this.medlemNation = user.medlem_nation;
		this.karneveljsbiljett = user.karneveljsbiljett;
	}
	
	public void getDataFromUser(User user){
		Log.d(LOG_TAG, "Will use data from user");
		this.personnummer = user.personnummer;
		this.fornamn = user.fornamn;
		this.id = user.id;
		this.efternamn = user.efternamn;
		this.gatuadress = user.gatuadress;
		this.postnr = user.postnr;
		this.postort = user.postort;
		this.email = user.email;
		this.telnr = user.telnr;
		this.matpref = user.matpref;
		this.engageradKar = user.engagerad_kar;
		this.engageradNation = user.engagerad_nation;
		this.engageradStudentikos = user.engagerad_studentikos;
		this.engageradEtc = user.engagerad_etc;
		this.ovrigt = user.ovrigt;
		this.kon = user.kon_id;
		this.nation = user.nation_id;
		this.storlek = user.storlek_id;
		this.terminer = user.terminer;
		this.korkort = user.korkort_id;
		this.snallaIntresse = user.snalla_intresse;
		this.snallaSektion = user.snalla_sektion;
		this.intresse = user.intresse_ids;
		this.sektioner = user.sektion_ids;
		this.jobbatHeltid = user.jobbat_heltid;
		this.jobbatAktiv = user.jobbat_aktiv;
		this.jobbatForman = user.jobbat_forman;
		this.jobbatStyrelse = user.jobbat_styrelse;
		this.karnevalist2010 = user.karnevalist_2010;
		this.villAnsvara = user.vill_ansvara;
		this.medlemAf = user.medlem_af;
		this.medlemKar = user.medlem_kar;
		this.medlemNation = user.medlem_nation;
		this.karneveljsbiljett = user.karneveljsbiljett;
	}
}
