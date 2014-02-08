package se.lundakarnevalen.android;

import java.io.IOException;

import json.Notification;
import json.Response;
import se.lundakarnevalen.remote.GCMReceiver;
import se.lundakarnevalen.remote.LKRemote;
import se.lundakarnevalen.remote.LKRemote.RequestType;
import se.lundakarnevalen.remote.LKRemote.TextResultListener;
import se.lundakarnevalen.remote.LKSQLiteDB;
import se.lundakarnevalen.remote.SectionSQLiteDB;
import se.lundakarnevalen.widget.LKInboxArrayAdapter.LKMenuListItem;
import se.lundakarnevalen.widget.LKSectionsArrayAdapter.LKSectionsItem;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
//github.com/Lundakarnevalen/futural-intern-android.git

public class SplashscreenActivity extends Activity{

	RelativeLayout wrapper;
	Context context;
	private final int THREAD_DELAY = 2000; //Splashscreen shown in milliseconds 
	
	
	private static final String LOG_TAG = "splash";
	GoogleCloudMessaging gcm;
	String regId;
	SharedPreferences sp;
	boolean shown = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen_layout);
		this.context = this;
		
		// init some stuff
		populateSp();
		
		wrapper = (RelativeLayout) findViewById(R.id.splash);
		Log.d(LOG_TAG, (wrapper == null) ? "wrapper was null" : "wrapper was NOT null");
		//wrapper.setOnClickListener(cont);
		createMenuThread();

		sp = context.getSharedPreferences(LKFragment.SP_GCM_NAME, Context.MODE_PRIVATE);
		addDataSekBg(); 
		if(checkForGooglePlay()){
			// GCM registration
			Log.d("SplashScreen", "starting gcmRegistration()");
			gcmRegistration();
		}else{
			// What to do???
		}
		
		getMessages();
		//EasyTracker.getInstance().activityStart(this);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		checkForGooglePlay(); 
	}
	
	@Override
	public void onStop(){
		super.onStop();
		//EasyTracker.getInstance().activityStop(this);
	}
	
	public void getMessages() {
		Log.d("SplashScreen", "Starting getMessages()");
		LKRemote remote = new LKRemote(context, new LKRemote.TextResultListener() {
	
			@Override
			public void onResult(String result) {
				Log.d("SplashScreen", "onResult()");
				if(result == null) {
					Log.d("SplashScreen", "Result from server was null");
					return;
				}
				Gson gson = new Gson();
				Response.Notifications notifications = gson.fromJson(result, Response.Notifications.class);
				Notification[] messages = notifications.notifications;
				LKSQLiteDB db = new LKSQLiteDB(context);
				Log.d("SplashScreen", "Created db object. Starting loop. messages.length = "+messages.length);
				for(int i=0;i<messages.length;i++) {
					Log.d("SplashScreen", "loop counter i = "+i);
					if(!db.messageExistsInDb(messages[i].id)) {
						Log.d("SplashScreen", "Message not in db");
						addMessage(messages[i].title, messages[i].message, messages[i].created_at, messages[i].id);
					}
				}
				db.close();
				Log.d("SplashScreen", "Completed getMessages");
			}
		});
		remote.showProgressDialog(false);
		Log.d("SplashScreen", "Starting server request");
		remote.requestServerForText("notifications.json", "", RequestType.GET, false);
	}
	
	public void addMessage(String title, String message, String date, int id) {
		LKSQLiteDB db = new LKSQLiteDB(context);
		db.addItem(new LKMenuListItem(title, message, date, id, true, null));
		db.close();
		
		Log.d("SplashScreen", "Added message with id = "+id);
	}
	
	private String getRegId(){
		Log.d("SplashScreen", "Starting getRegId()");
		String regId = sp.getString(LKFragment.SP_GCM_REGID, "");
		if(regId.length() <= 0)
			return "";
		String regAppV = sp.getString(LKFragment.SP_GCM_REG_APP, "-1");
		String currentVersion = LKFragment.getAppVersion(this);
		if(!regAppV.equals(currentVersion)){
			Log.d(LOG_TAG, "New app version installed");
			return "";
		}
		Log.d("SplashScreen", "Finished getRegId()");
		return regId;
	}
	
	private void gcmRegistration(){
		gcm = GoogleCloudMessaging.getInstance(context);
		Log.d("SplashScreen", "Got gcm instance");
		regId = getRegId();
		if(regId.length() <= 0){
			Log.d(LOG_TAG, "Will try to register");
			// Register for gcm.
			regInBackground(context, gcm);
		}else{
			Log.d(LOG_TAG, "found regId");
		}
	}
	
	public static void storeAsRegId(String regId, Context context){
		SharedPreferences sp = context.getSharedPreferences(LKFragment.SP_GCM_NAME, MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(LKFragment.SP_GCM_REGID, regId);
		editor.putString(LKFragment.SP_GCM_REG_APP, LKFragment.getAppVersion(context));
		editor.commit();	
	}
	
	private void storeSekData(){
	     // Information about the sections
		SectionSQLiteDB db = new SectionSQLiteDB(this);
		db.dropEntiresInDatabase();
		
 		db.addItem(new LKSectionsItem(
 				"Barnevalen", 
 				R.drawable.barnevalen,
 				q1("Barnevalen") + "Barnevalen �r den delen av karnevalen som riktar sig till barn i alla �ldrar! Det �r det st�rsta av de stora n�jena och har, i skillnad fr�n de flesta andra, ett helt omräde! F�rutom en f�reställning kommer det även finnas sm� tält och bodar där barnen f�r delta i aktiviteter och hyss! Man ska ha riktigt kul när man g�r runt p� barnevalsomr�det oavsett hur gammal man är och det bygger givetvis p� en h�rlig gl�dje och gemenskap mellan de som �r engagerade!" + q2() + "Man kan g�ra massor av olika saker i Barnevalen! Laga mat till alla som �r med, sy kl�der och sminka de p� scen, ordna med fester, bygga upp n�jen p� omr�det, se till att allt flyter p�, st� i baren, och bygga dekor �r n�gra av de omr�den som det kommer beh�vas glada barnevalister p�!" + q3() + "Vi r�knar med att ta med 130 � 150 fantastiska barnevalister!" + q4("Barnevalen") + "Det �r en unik m�jlighet att p� ett studentikost s�tt f� roa barn, framtidens karnevalister, och deras familjer. Barnevalen �r som en minikarneval i karnevalen med utrymme f�r v�ldigt m�nga id�er och p�hitt, alla kommer kunna vara med och p�verka utformningen s� den blir perfekt! En extra bonus �r att vi, till skillnad fr�n de �vriga n�jena, kommer sluta v�ra f�rest�llningar tidigare om kv�llarna vilket g�r att vi kommer f� fler timmar �ver f�r partaj :-)",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Biljetteriet",
 				R.drawable.biljetteriet,
 				q1("Biljetteriet") + "Biljetteriets prim�ra uppgift �r att s�lja biljetter till karnevalsomr�det, dansen, t�ltn�jena, tombolan och inte minst till alla de 7 stora n�jena. Innan dess ska vi dessutom s�lja biljetter till Karneveljen och andra liknande �f�rfester� samt ta hand om gynnarna (de som gynnar Lundakarnevalen genom att l�mna garantier) genom att de f�r f�rk�pa biljetter. Biljetterna s�ljs f�rst genom internt och externt biljettsl�pp och sedan p� webben och i bodar runt om i Lund." + q2() + "Man kan s� klart f� jobba med att s�lja biljetter till alla de 140000 m�nniskor som ska bes�ka karnevalsomr�det, men det finns �ven bra m�jligheter att f� jobba med IT, ekonomi, matlagning och givetvis fester! Det viktigaste hos oss �r att man mest bara vill vara med i Futuralkarnevalen!" + q3() + "Vi �r en v�ldigt flexibel sektion s� det �terst� att se efter Uppropet, men troligtvis 150-250 karnevalister." + q4("Biljetteriet") + "Det finfina med Biljetteriet �r att man f�r m�jlighet att jobba under hela karnevalsv�ren, vilket g�r att man kommer l�ra k�nna massvis med sk�na karnevalister redan innan karnevalsdagarna. Man f�r dessutom tr�ffa m�nga glada bes�kare fr�n n�r och fj�rran som inte vill n�got annat �n att k�pa karnevalsbiljetter vilket de s�klart g�r helt r�tt i. Biljetteriet kommer vara en central del av hela karnevalskarusellen som man inte vill missa!<br><br>",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Bl�dderiet",
 				R.drawable.bladderiet,
 				q1("Blädderiet") + "Bl�dderiet �r en sektion som jobbar mest med tryckt material. Tillsammans ska vi skapa karnevalstidningen f�r att marknadsf�ra karnevalen och vi kommer �ven att g�ra programbladet, som kommer att vara den ultimata guiden till karnevalsdagarna i maj!" + q2() + "I Bl�dderiet kan man skriva, fotografera, jobba med layout, komma p� snitsiga saker eller vara allm�nt kreativ helt enkelt! H�r finns det massor av olika arbetsuppgifter f�r den som gillar att jobba med tryckt material i kreativa forum, och vi kommer att beh�va allt fr�n de som skapar tryckprodukter, de som jobbar mer administrativt eller de som skapar vi-k�nslan i sektionen!" + q3() + "Bl�dderiet �r en av de mindre sektionerna i Lundakarnevalen, och vi kommer att vara ca 65 personer. Ett litet men g�tt g�ng helt enkelt!" + q4("Bl�dderiet") + "Det b�sta med Bl�dderiet �r att du f�r m�jlighet att skapa n�gra av de saker som �r mest utm�rkande f�r Lundakarnevalen ut�t! En av Bl�dderiets styrkor �r �ven att vi kommer att vara en mindre sektion som jobbar intensivt under en kort period, vilket kommer att g�ra att vi kommer att f� ett riktigt sammansvetsat g�ng som kommer att ha kul tillsammans!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Cirkusen",
 				R.drawable.cirkusen,
 				q1("Cirkusen") + "Cirkusen �r ett av de stora n�jena och det �r precis vad det l�ter som� en cirkus! Vi kommer att ha ett riktigt cirkust�lt med manege och hela faderullan. Men det kommer inte vara en vanlig cirkus. Det kommer ist�llet vara en cirkus med karnevalistisk twist som kommer best� av nummer utav varierande art och kvalit�. Ofta brukar dessa driva lite med klassiska cirkusnummer och koncept. Cirkusen �r ocks� det �ldsta karnevalsn�jet! Klassiskt kul!" + q2() + "Det finns m�nga olika saker att g�ra i sektionen. Man kan bygga dekor, laga mat, sy, sminka, sk�ta tekniken, spela orkester och inte minst st� p� scen. Eller varf�r inte fixa aktiviteter f�r ensemblen eller g�ra ett snyggt programblad? Ensemblen kommer vara lik den i de traditionella spexen." + q3() + "Vi kommer vara ungef�r 90-100 glada karnevalister!" + q4("Cirkusen") + "Det b�sta med Cirkusen kommer att bli den h�rliga ensemblen! Vi kommer att bli en helt ny grupp m�nniskor som i en m�nad tillsammans ska umg�s, jobba och skapa n�got fantastiskt. Dessutom �r det ett v�ldigt unikt n�je! Hur ofta f�r man annars chansen att s�tta upp en cirkus? Det �r bara att l�ta fantasin fl�da!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Dansen",
 				R.drawable.dansen,
 				q1("Dansen") + "Danssektionen anordnar de stora festerna i AF-borgen p� kv�llarna under karnevalen. Varje kv�ll under de tre karnevalsdagarna sl�r AF-borgen upp sina portar f�r s�dra Sveriges st�rsta fest. Bes�kare kommer f� se borgen som man aldrig sett den f�rr." + q2() + "Under karnevalskv�llarna kommer AF-borgen vara full av partysugna m�nniskor, och vi ska se till att de f�r den b�sta kv�llen under deras tid i Lund. Man kommer f� arbeta inom alla de omr�den som m�jligg�r detta. Varje karnevalist kommer jobba en av kv�llarna, och d�rmed vara med och skapa sin unika karnevalsfest.<br><br>Alla karnevalister kommer �ven vara med i f�rarbetet inf�r festkv�llarna, vilket inkluderar att bygga dekor, g�ra skyltar och allt annat som g�r just det dansgolvet till det fetaste dansgolvet i hela Lund/Sk�ne/Sverige. Innan karnevalen kommer vi dessutom ha en himla massa andra roliga uppt�g, bland annat fotbollsturneringar, t�vlingar och fester som annordnas av sektionens interna vieri." + q3() + "Vi kommer vara runt 300 taggade karnevalister." + q4("Dansen") + "I danssektionen f�r man den unika m�jligheten att f� vara delaktig i skapandet av en av Sveriges st�rsta, ballaste och roligaste fester, som �r n�got helt annat �n en vanlig fest p� borgen. Detta f�r man g�ra tillsammans med ett hundratal andra lika taggade studenter.",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Ekonomi",
 				R.drawable.ekonomi,
 				q1("Ekonomisektionen") + "Det �r ekonomisektionen uppgift att se till att det h�r organiserade vansinnet g�r ihop rent ekonomiskt, inte minst f�r att framtida(!) generationer fortsatt kan ta del av denna fantastiska galenskap! Ekonomisektionen arbetar d�rf�r bland annat som st�ttande till karnevalens alla ekonomichefer. Dessutom ansvarar sektionen f�r all karnevalens sponsring, annonsering i karnevalsmedier, budgetering, karnevalens redovisning, kassor m.m. och inte minst de karnevalscentrala upphandlingar. Sektionen fungerar d�rf�r p� m�nga s�tt som en ryggrad �t �vriga sektioner, stabilt!" + q2() + "Det �r mer blandat �n vad man kan tro! Sektionens arbetsuppgifter som ofta involverar kontakt med m�nga av karnevalens sektioner erbjuder en s�rskild helhetsbild �ver karnevalen. Det sker mycket kontakt mellan sponsorerna och karnevalens redaktioner, n�jen och krogar,�f�r att se till s� att alla f�r de paket som best�llts.�Man kommer kunna jobba med logistik, s�kerhet, fundraising, och eftersom ekonomisektionen st�r f�r Karnevalens redovisning kommer man sj�lvklart kunna arbeta med redovisning och bokf�ring!�Sedan beh�vs s�klart snitsiga kommunikat�rer och vieriansvariga f�r att h�lla alla uppdaterade och hum�ret p� topp!" + q3() + "Ca 30-35 st!" + q4("Ekonomisektionen") + "Ekonomisektionen �r inte karnevalens st�rsta sektion, men v�l ett v�ldigt sammansvetsat team med den st�rsta utmaningen, som ger stora m�jligheter f�r karnevalisten att ha j�kligt kul och f� stort ansvar. Som del av ekonomisektionen ser man karnevalen med mer �verblick och �r hela tiden med och skapar en maximerad karnevalsistisk v�r!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Fabriken",
 				R.drawable.fabriken,
 				q1("Fabriken") + "Fabriken har tre huvudsakliga uppgifter, dels att bygga och sy �t andra sektioner och dels agera centralt materiallager f�r Karnevalen. Andra sektioner i Karnevalen kan antingen l�gga best�llningar p� saker som de vill ha som sedan tillverkas i Fabriken eller l�gga en best�llning p� material de beh�ver och sedan h�mta det fr�n Fabriken." + q2() + "I�Fabriken kan man bland annat jobba med lagerhantering och best�llningar, sy, snickra, m�la, laga mat, fixa fester och roliga aktiviteter f�r sektionen och mycket annat skoj." + q3() + "Vi kommer vara ungef�r 100 glada karnevalister!" + q4("Fabriken") + "Det b�sta med Fabriken �r att man f�r skapa v�ldigt mycket kreativa saker, det �r bara fantasin som s�tter gr�nser f�r vad som kan tillverkas i Fabriken. Man f�r uppleva en riktigt karnevalistisk gl�dje och ett g�tt h�ng p� Fabriken. Under karnevalsdagarna kommer det mesta arbetet vara klart vilket betyder att man f�r h�nga p� karnevalsomr�det och uppleva hela Karnevalen.",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Festm�steriet",
 				R.drawable.festmasteriet,
 				q1("Festm�steriet") + "Festm�sterietʊr en m�ngfacetterad sektion d� vi kommer syssla med flera olika delar men gemensamt �r att centralt, b�de organisatoriskt och fysiskt, ansvara f�r mat och dryck till hela Lundakarnevalen 2014. Mer specifikt �r v�r sektion uppdelad p� tre olika delar vilka �r; Karnevalslagret, Vippen och Bamban.<br>Lagret huserar i k�llaren p� AF-borgen och kommer pyssla med kontakt med leverant�rer, best�llningar, logistik och lagerh�llning av karnevalens 2000000 burkar Karnev�l, 12 ton mat och otaliga m�ngder f�rbrukningsmaterial.<br>Vippen �r ett mysigt gryt f�r gamla karnevalsr�var, sponsorer och samarbetspartners. I Festv�ningen p� AF kommer mat och dryck av h�g kvalit�t serveras p� vita linnedukar med kandelabrar f�r att visa uppskattning till n�gra av de personerna som g�r karnevalen m�jlig. Till sin hj�lp finns Tegn�rs k�k och cirka 100 eldsj�lar som i dagligt tal kallas karnevalister.<br>Bamban �r karnevalisternas eget vattenh�l, fas med mat. Som en klassisk skolmatsal i karnevalistisk tappning kommer K�llarsalen med tillh�rande k�k att vara Bambans hemvist d�r runt 800 portioner per dag ska m�tta vakter, poliser och ett g�ng sektioners karnevalister." + q2() + "Det mest kommer kretsa kring mat och dryck av olika slag. F�r att f� det att fungera bra �r det viktigaste att alla karnevalister har kul, och under tiden de har kul ska det dukas, k�ras truck, hacka, blanda drinkar, diska, garnera, baka, h�lla upp �l och mycket mycket mer!" + q3() + "Runt 220 glada karnevalister!" + q4("Festm�steriet") + "Vi kommer vara mitt i Lundakarnevalen 2014 d�r vi skapar f�ruts�ttningar f�r v�rldens b�sta karneval genom en h�rlig blandning av mat och dryck!/nVi kommer pyssla med ett brett spektra inom mat och dryck, fr�n en v�llagad pytt-i-panna till h�g gastronomisk niv� och logistik f�r m�nga ton mat!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Filmen",
 				R.drawable.filmen,
 				q1("Filmen") + "Vi ska g�ra Karnevalsfilmen! En lundensisk l�ngfilm med karnevalistisk snits." + q2() + "Allt fr�n att att bygga scenografi, laga mat, sk�despela, jobba med visuella effekter, klippa film, sy kl�der, ordna fester, filma, ljudl�gga, sminka, h�lla koll p� statister och mycket mycket mer." + q3() + "Vi kommer vara ca. 100 karnevalister och kanske 160 med statister." + q4("Filmen") + "Man f�r vara med och g�ra en l�ngfilm tillsammans med andra peppade m�nniskor. F� av oss har erfarenhet av filminspelning vilket g�r det �nnu h�ftigare att vi tillsammans skapar en filminspelning p� ett karnevalistiskt vis! Och inte nog med det, vi f�r b�rja tidigare �n alla andra! V�r Karnevalsv�r b�rjade i December!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Kabar�n",
 				R.drawable.kabaren,
 				q1("Kabar�n") + "Kabar�n �r ett av Lundakarnevalens stora n�jen och vi ska skapa en fantastisk f�rest�llning som karnevalens bes�kare kan uppleva under karnevalsdagarna. Detta spektakel med sketcher, s�ng, dans och gr�nsl�s humor kommer att s�ttas upp i ett stort t�lt i Lundag�rd, vilket g�r Kabar�n till ett n�je som befinner sig mitt i karnevalens hetluft!" + q2() + "I Kabar�n kommer man att kunna g�ra massor av olika saker: bygga dekor, laga mat, st�/dansa/sjunga p� scen, sy kostymer, b�ra �l, spela i orkester, m�la, bygga t�lt, sy kostymer, fixa med programblad, sminka, rigga teknik, och s� vidare! H�r kommer det helt enkelt att beh�vas alla typer av karnevalister." + q3() + "Kabar�n kommer att best� av mellan 70-90 snitsiga karnevalister." + q4("Kabar�n") + "Att skapa en Kabar� kommer att kr�va kreativitet, arbete och v�ldigt mycket gl�dje! I Kabar�n kommer det att vara h�gt i tak och finnas m�jlighet att ta ut de karnevalistiska sv�ngarna. Vi kommer att arbeta tillsammans veckorna innan karnevalen och bli en riktigt h�rlig, sammansvetsad ensemble. H�r kommer man att verkligen l�ra k�nna och arbeta med nya m�nniskor, eftersom vi �r en relativt liten sektion med en rolig uppgift. N�r sedan karnevalsdagarna�stundar i maj, kommer man som Kabar�-ist att uppleva resultatet: en fantastisk f�rest�llning, skratt och h�rligt h�ng under tr�dkronorna i Lundag�rd!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Klipperiet",
 				R.drawable.klipperiet,
 				q1("Klipperiet") + "Klipperiet �r en ny sektion som skall dokumentera och marknadsf�ra karnevalen i r�rlig bild. Det kommer inneb�ra ett kontinuerligt jobb med filmklipp inf�r, under och efter karnevalistiska h�ndelser samt stort utrymme f�r kreativa id�er, fantastiska produktioner och h�rligt h�ng under v�ren!" + q2() + "I Klipperiet kan man bland annat filma, klippa, komma p� grymma teknikl�sningar, fixa fester och h�ng, jobba med grafik och animering, klura ut och genomf�ra kreativa och karnevalistiska s�tt att marknadsf�ra och dokumentera karnevalen b�de internt och externt samt h�nga mycket p� sociala medier!" + q3() + "Vi kommer vara ungef�r 30 h�rliga karnevalister!" + q4("Klipperiet") + "Det b�sta med Klipperiet �r att det �r en helt ny sektion � det finns ett enormt utrymme f�r genomf�rande av fantastiska id�er och kreativt arbete med r�rlig bild. En f�rhoppning �r att vi skall arbeta med variation och i olika format, s�v�l l�ngre filmer som gifs.<br>Vi kommer i v�rt dokumentations- och marknadsf�ringsarbete ocks� ha n�ra kontakt med de andra sektionerna, och det �r g�tt.<br>Samt �ran att f� bevara Futuralkarnevalen f�r framtiden, s�klart!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Kommunikation",
 				R.drawable.kommunikation,
 				q1("Kommunikationssektionen") + "Kommunikationssektionen ansvarar f�r den interna och externa kommunikationen i Lundakarnevalen. Det inneb�r att vi ska se till att alla karnevalister f�r den information de beh�ver, och att alla bes�kare vet att de ska komma till Lundakarnevalen! Vi har �ven hand om mycket av karnevalens administration. Vi kommer bland annat att driva en karnevalsexp, ha hand om en bilpool, skapa appar, sk�ta hemsida och sociala medier och komma p� massa roliga och kreativa marknadsf�ringsid�er. Men framf�rallt ska vi ha riktigt skoj!" + q2() + "I sektionen kan man g�ra det mesta. Man kan laga mat till internfesterna, komma p� roliga aktiviteter f�r sektionen, jobba med administration, hj�lpa andra karnevalister genom att svara p� fr�gor p� expen, skapa grafiskt material, fotografera, skriva texter till hemsidan, bygga appar och mycket mycket mer." + q3() + "Vi kommer vara ungef�r 100 glada karnevalister!" + q4("Kommunikationssektionen") + "Det b�sta med kommunikationssektionen �r att det finns s� m�nga olika saker att g�ra! Det �r ocks� en sektion som kommer ha mycket kontakt med m�nga andra karnevalister vilket s�klart �r kul. Vi kommer vara v�ldigt kreativa, men ocks� allra b�st p� listor. En fantastisk kombination!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Krog Fine Dine",
 				R.drawable.krog,
 				q1("Krog Fine Dine") + "Krog Fine dine�kommer att liksom de andra krogarna att f�rse alla p� karnevalen med mat och dryck. Speciellt f�r Krog Fine dine �r att vi kommer h�lla till i AF-borgen och se till att behovet av fine dining p� karnevalen m�ttas." + q2() + "Man kan g�ra allt fr�n att laga god mat och st� i bar till att h�lla koll p� entr�er b�de till omr�det och till krogen med fokus p� att vi ska riktigt roligt n�r vi g�r allt det h�r!" + q3() + "F�rhoppningen �r att det ska bli ett h�rligt g�ng p� cirka 170 karnevalister!" + q4("Krog Fine Dine") + "Det b�sta �r att arbetet kommer att vara varierande s� att man inte tr�ttnar p� sin arbetsuppgift. Tillsammans kommer vi att skapa en krog d�r allt fr�n maten vi serverar till dekorationer i lokalen �r inspirerade av v�rt tema. Framf�rallt s� kommer vi skapa en krog d�r st�mningen alltid �r p� topp!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Krog l[a]unchingpad",
 				R.drawable.krog, 
 				q1("Krog l[a]unchingpad") + "Krogens absoluta centrum kommer att ligga kring servering av mat och dryck till karnevalens alla bes�kare! Till detta kommer en del kringuppgifter s�som ekonomi, s�kerhet och vieri." + q2() + "Hos oss kommer du att kunna laga mat, st� i baren, ing� i en vaktgrupp, hj�lpa till i entr�n, vissa kommer att vara gruppchef f�r ett g�ng karnevalister. Vi har ocks� ett eget vieri som du kommer att kunna vara aktiv i. Kort sagt kan du g�ra en massa snitsigt!" + q3() + "Vi kommer att vara ungef�r 250 karnevalister." + q4("Krog l[a]unchingpad") + "Vi kommer att skapa ett helt nytt krogomr�de uppe p� g:a kirurgen. Det kommer d�rf�r bli en helhetsupplevelse ut�ver det vanliga med allt som h�r en karneval till. Det kommer bli allm�nna galenskaper, underbara tokigheter och magiska dagar kv�llar och n�tter. H�ng med och skapa n�got helt nytt p� karnevalen och var med om att i framtiden g� till historien!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Krog Molnet",
 				R.drawable.krog,
 				q1("Krog Molnet") + "Krogarna har som sitt st�rsta ansvar att se till att ingen bes�kare beh�ver g� runt med tom mage och torr strupe, vilket vi g�r genom att servera litervis med dryck och en massa god mat. Vi ser �ven till att erbjuda det b�sta h�nget p� hela omr�det d� vi kommer att ha en stor och g�ttig uteservering bara n�gra steg fr�n font�nen och Stora scenen!" + q2() + "Hos oss kan man st� i k�ket och laga mat till 40 000 hungriga karnevalsbes�kare, servera �l och annat l�skande i baren, vara med och skapa dekoren till v�r krog samt ha koll p� s�kerheten. Sist men verkligen inte minst har vi sj�lvklart v�rt interna vieri som ska se till att alla v�ra karnevalister har det bra och �r glada och n�jda!" + q3() + "Vi kommer att vara 150-170 karnevalister." + q4("Krog Molnet") + "Vi kommer att satsa p� att ha den mest ambiti�sa och ljusslingesp�ckade inredningen i karnevalskrogarnas historia. Dessutom befinner vi oss ocks� mitt i smeten � vid font�nen och Stora scenen � vilket ju inte kan bli annat �n bra!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Krog Nangilima",
 				R.drawable.krog,
 				q1("Krog Nangilima") + "Det min krog har i huvudsaklig uppgift �r att leverera dryck, mat och en grym festst�mning! Kommer bli ett j�kla h�llig�ng." + q2() + "Det kommer finnas massa softa uppgifter, tanken �r att alla ska kunna hitta n�got som passar dem h�r. Mycket kommer s�klart kretsa kring bar- och restaurangarbete men det kommer �ven finnas m�jligheter att jobba med exempelvis musik, scen och s�kerhet. Vill man ta p� sig mycket ansvar finns det m�jlighet till det och skulle man vara lite ansvarsallergisk finns det f�rst�s s�dana poster ocks�. S� l�nge man �r riktigt, riktigt karnevalssugen kommer vi kunna hitta n�got som passar!" + q3() + "Det �r sv�rt att i dagsl�get s�ga hur sektionen kommer bli d� vi fortfarande dealar lite mellan krogarna, 200-300 personer �r v�l n�gon form av kvalificerad gissning. Alldeles, alldeles lagom blir det i alla fall!" + q4("Krog Nangilima") + "Det �r mycket som �r b�de bra och speciellt med oss! Vi kommer h�lla till med en annan krog uppe G:a Kirurgen, i norra delen av karnevalsomr�det. Vi kommer tillsammans med Krog l[a]unchingpad helt bossa �ver omr�det vilket betyder att vi kommer kunna hitta p� riktigt mycket sp�nnande hyss d�ruppe! Hur krogarna slutgiltigt kommer se ut �r �nnu inte klart men s�kert �r att min souschef Hugo och jag kommer satsa stenh�rt p� en upplevelse ut�ver mat och dryck. Uppt�g, musik, spektakel, scener, fester �- alla m�jliga �ventyr kommer kunna upplevas uppe p� Kirurgen i maj! S� har du ett genuint intresse av allt som har med mat, dryck och/eller events att g�ra look no further. F�rutom att ni kommer l�ra k�nna min fantastiska ledningsgrupp, Hugo och mig lovar jag er mycket galej och v�nner f�r livet. Vi kommer tillsammans skapa framtidens h�ftigaste, gladaste och futuralaste sektion!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Krog Undervatten",
 				R.drawable.krog,
 				q1("Krog Undervatten") + "Sektionen �r ansvarig f�r ett av karnevalens stora �l- och matt�lt. I det ska vi servera en massa h�rlig mat och dryck och se till att v�ra g�ster f�r en fantastiskt h�rligt karnevalistisk upplevelse n�r magen b�rjar kurra eller benen k�nns lite tr�tta!" + q2() + "Massor av skoj! Man kan laga mat, st� i baren, fixa dekorationer, ordna sektionens h�rliga fester, trixa med t�lt och teknik eller jobba med s�kerheten." + q3() + "Vi kommer att vara n�rmare 200 karnevalister i sektionen!" + q4("Krog Undervatten") + "I v�r sektion kommer man f� vara med och testa p� massor av roliga uppgifter, att driva krog �r sjukt kul! Att f� skapa gl�dje f�r v�ra g�ster samtidigt som vi sj�lva har kul kommer bli en riktig upplevelse!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Musik",
 				R.drawable.musiken,
 				q1("Musiksektionen") + "Musiksektionen ser till att det alltid str�mmar ljuv musik ifr�n olika platser p� Karnevalsomr�det och framf�rallt ifr�n Stora Scenen framf�r universitetshuset. Vi har hand om alla artister som kommer f�r att upptr�da under dagarna men �ven scenerna dem upptr�der ifr�n." + q2() + "I Musiksektionen kan du ta hand om artister, laga mat till alla i sektionen men �ven artister och deras crew, blanda drinkar, bygga scener och rigga teknik, administrerar backstagepass, biljetter m.m. k�ra folk fr�n och till omr�det, inreda loger och s� klart se till att alla har kul som �r med!" + q3() + "Vi kommer vara omkring 100 karnevalister." + q4("Musiksektionen") + "Det b�sta med musiksektionen �r att v�r insatts underh�ller tusentals m�nniskor �t g�ngen men �ven att det �r en h�rlig utmaning att f� allt att fungera samtidigt som vi upplever alla spelningar p� omv�nd f�rsta parkett (bakom scen).",
 				true));
 		db.addItem(new LKSectionsItem(
 				"N�jen",
 				R.drawable.nojen,
 				q1("N�jessektionen") + "N�jessektionen samordnar karnevalens st�rre scenproduktioner. Det inneb�r att vi jobbar med alla karnevalens stora N�jen: Kabar�n, Spexet, Showen, Barnevalen, Cirkusen, Filmen och Revyn. Vi kommer ocks� att jobba med de mindre n�jena som kallas Sm�n�jen!" + q2() + "N�jessektionen ordnar karnevalens scen- och orkesterinprovningar, showen p� Karneveljen, den storslagna invigningen och s�klart �ven avslutningen. Det inneb�r att det �r m�nga listor och personer att h�lla koll p�! Det kommer nog ocks� fikas och skrattas en hel del och h�llas m�ten s�klart." + q3() + "N�jessektionen �r den minsta sektionen i karnevalen och vi satsar p� att vara ungef�r tre personer. Tyv�rr kan man inte s�ka till v�r sektion p� uppropet, men man f�r g�rna komma och prata med oss vid disken d�r man anm�ler sig till inprovningar av olika slag!" + q4("N�jessektionen") + "Det b�sta med n�jessektionen �r att vi f�r jobba med s� m�nga olika sektioner och tr�ffa och hj�lpa s� m�nga m�nniskor! En annan bra grej �r att hela sektionen f�r plats i ett och samma badkar!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Omr�de",
 				R.drawable.omrade,
 				q1("Omr�de") + "Omr�dets uppgift �r att planera och bygga upp karnevalsomr�det med t�lt, entr�er,el, vatten, toaletter, internet och allt annat som kr�vs f�r att karnevalen ska fungera.�Sektionen �r �ven ansvarig f�r att dekorera omr�det och med utg�ng fr�n temat f� det att�andas karneval." + q2() + "Man kan jobba med byggnation, el, logistik, ljuss�ttning, dekoration, s�kerhet, matlagning och�planering av fester och andra roliga evenemang f�r sektionen." + q3() + "Ca 100." + q4("Omr�dessektionen") + "Att man fr�n ingenting bygger upp hela det magiska karnevalsomr�det med allt vad det inneb�r under�ett par intensiva veckor och att man f�r komma i kontakt med s� m�nga av de andra sektionerna i sitt arbete.",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Radio",
 				R.drawable.radio,
 				q1("Radion") + "Karnevalsradion kommer att s�nda live, via FM och webb och videostream, dygnet runt under karnevalsdagarna. Denna maratons�ndning kommer att ske fr�n en glasbur p� karnevalsomr�det och d�rifr�n fyller vi timmarna med prat, musik, intervjuer och allt vad vi vill! Under v�ren kommer vi �ven att s�nda ett program i veckan p� Radio AF d�r vi taggar inf�r karneval!" + q2() + "Framf�r allt f�r du s�nda och producera radio, det g�ller alla som engagerar sig i sektionen. Du f�r s�nda b�de under karnevalsdagarna och i programmet vi s�nder inf�r karnevalen! Vill du laga mat, st� i en bar, fixa fester, syssla med teknik, bygga saker eller l�gga musik finns det chans att g�ra �ven det. Du v�ljer sj�lv hur mycket du vill engagera dig, men radiopratandet st�r s�klart i fokus!" + q3() + "Vi kommer att vara ungef�r 70 pratglada karnevalister." + q4("Radion") + "Det absolut b�sta �r det som hela sektionen vilar p� � du f�r s�nda radio. Det �r extremt roligt! Dessutom f�r du s�nda radio som ocks� syns, b�de av folk som tittar in p� oss fr�n karnevalsomr�det och av folk som sitter hemma framf�r datorn. Och � som tidigare n�mnt � kan du hitta p� andra saker ut�ver det. Du kommer ocks� att f� l�ra dig en massa saker om radios�ndande och teknik under v�rens g�ng!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Revy",
 				R.drawable.revyn,
 				q1("Revyn") + "Vi i Revyn ska s�tta upp en humoristiskt, sketch-baserad 45-minuters f�rest�llning som ska visas l�pande i Universitetsaulan under karnevalsdagarna. Detta spektakel ska f�rberedas under cirka fyra veckor" + q2() + "Man kan:<br><br>- Laga mat och tillhandah�lla dryck under repveckorna<br><br>- Planera fester och aktiviteter som skapar vi-k�nsla i sektionen<br><br>- Arbeta med tekniken (ljud och ljus) kring f�rest�llningarna<br><br>- Arbeta med s�kerheten kring f�rest�llningarna<br><br>- Bygga dekor<br><br>- Sy scenkl�der<br><br>- Sminka sk�despelarna<br><br>- Spela i orkestern<br><br>- Skriva manus<br><br>- Sk�despela" + q3() + "Vi kommer vara ungef�r 85 karnevalister totalt." + q4("Revyn")+ "Det b�sta med Revyn kommer att vara sammanh�llningen, speciellt under repveckorna. Vi kommer arbeta v�ldigt n�ra och intensivt under denna period. Alla i sektionen kommer att ha en tydlig arbetsroll och alla kommer arbeta f�r samma m�l. N�r sedan produkten �r f�rdig kommer vi alla kunna njuta av denna och vara stolta tillsammans!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"S�kerhet",
 				R.drawable.sakerhet,
 				q1("S�kerhetssektionen") + "Vi i S�kerhetssektionen kommer att jobba fr�mst f�rebyggande med branddokumentation, sjukv�rd, folkfl�den, jourgrupper som hj�lper till att sl�cka br�nder i mer bildlig bem�rkelse och radiosamband f�r hela karnevalen.<br>Ut�ver det sk�ter vi kontakten med R�ddningstj�nsten, Polis, Lunds kommun och Universitet och andra f�retag i samtliga s�kerhetsfr�gor som ber�r Lundakarnevalens arbete." + q2() + "Massa! Det kommer att finnas de som hj�lper till som brandvakter, l�kare som sk�ter sjukv�rden under karnevalsdagarna, karnevalister som i jourgrupper hj�lper andra sektioner med precis det som beh�ver g�ras f�r stunden, personer som sk�ter ledningscentralen och mycket annat." + q3() + "Cirka 200 personer kommer vi att vara totalt!" + q4("S�kerhetssektionen") + "Dels �r det en blandning av spetskompetenser inom olika omr�den som blandas med ett stort g�ng mindre erfarna, men mist lika glada karnevalister, och dels �r det extra viktigt att vi har det roligt medan vi l�ser v�ra uppgifter d� det �r ett ansvarsfullt uppdrag som karnevalens alla bes�kare och medverkare verkligen �r beroende av fungerar.",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Shoppen",
 				R.drawable.shoppen,
 				q1("Shoppen") + "Vi ska f�r det f�rsta ha kul, l�ra k�nna varandra och leka massor, det �r f�r oss det viktigaste. F�r det andra s� �r det v�r uppgift att ta fram, producera och s�lja en m�ngd fantastiska ting att ta med sig hem fr�n karnevalen 2014. Allt fr�n on�diga manicker till praktiska manicker, till fina Karnevalskl�der. Allt med sin egen pr�gel fr�n Futuralkarnevalen 2014, f�r att b�de karnevalister och bes�kare ska kunna f� med sig ett v�rdefullt minne hem. Vi har �ven hand om �rets karnevalsspel." + q2() + "Hos oss kommer man att kunna s�lja futurala produkter, marknadsf�ra dem, bygga och designa v�ra fantastiska t�lt, laga mat och anordna internfester, komma p� roliga aktiviteter till sin grupp mm." + q3() + "Vi kommer vara runtomkring 300 personer." + q4("Shoppen") + "Det b�sta med v�r sektion �r att det �r den b�sta blandningen av kreativitet och seri�st. Det kommer att finnas m�nga olika typ av saker att g�ra men vi kommer alla att str�va mot en gemensam slutprodukt. Hos oss ska det vara fokus p� att ha kul och f� en gemenskap med gruppen, men ocks� att ens uppgifter �r meningsfulla och v�rdeskapande samtidigt som roliga.",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Show",
 				R.drawable.show,
 				q1("Show") + "Vi ska g�ra v�rldens b�sta karnevalsshow! Showen �r en av 6 stora n�jesf�rest�llningar i karnevalen, och kommer att spelas i Tegn�rs Matsalar. Med charm, god mat och musikalisk finess ska vi bjuda publiken p� en f�rest�llning ut�ver det vanliga � och ha en underbar v�r tillsammans p� v�gen! Of�rgl�mligt f�r alla!" + q2() + "P� m�nga s�tt �r Showen likt de andra n�jena vad g�ller undersektioner, n�mligen de m�nga olika grupper som kr�vs f�r att skapa en f�rest�llning � dekor, kostym, orkester, scen och teknik f�r att n�mna n�gra. Till det kan man till�gga de viktiga grupper som finns i alla sektioner, som vieri, sexm�steri och kommunikation. Det �r inte f�rhastat att s�ga att det finns v�ldigt stora m�jligheter att hitta n�got kreativt stimulerande i den fantastiska Showen!" + q3() + "Kanske mellan 70 och 90." + q4("Showen") + "Ja, var ska man b�rja�? Om du innehar de b�da egenskaperna: a) tycker att vara med och skapa en scenf�rest�llning verkar helball samt b) vill anamma den karnevalistiska andan och pr�va att g�ra n�got nytt och riktigt fr�scht, s� hade det varit falsk marknadsf�ring att inte rekommendera Showen. Det �r ett n�je som skiljer sig mycket fr�n �vriga n�jeslivet i studentv�rlden, till sin form och till sin karakt�r./nEn speciell och stor del av sj�lva f�rest�llningen �r konceptet krogshow, dvs att publiken blir serverade fantastisk mat under tiden. Det g�r t.ex. att sexm�steriet i Showen �r oerh�rt viktigt. En annan profil p� f�rest�llningen �r att den kommer att h�lla en h�g musikalisk niv�, och musiken kommer att spela en stor roll./nGenerellt kan man s�ga att det finns ingen b�ttre k�nsla �n att tillsammans skapa och utforska n�got nytt � och det �r precis det vi f�r chansen att g�ra i Showen. F�lj med in i v�r framtid vettja!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Sm�n�jen",
 				R.drawable.smanojen,
 				q1("Sm�n�jen") + "P� karnevalsomr�det kommer det att finnas tre stycken Sm�n�jen som ligger under N�jessektionen. Sm�n�jenas uppgift �r att skapa knasiga, h�rliga, tankev�ckande och underh�llande f�rest�llningar p� 30 minuter i t�lt som rymmer en publik p� cirka 100 personer." + q2() + "I ett sm�n�je finns det massor att g�ra och eftersom man �r en tajt grupp p� 50 personer s� f�r man ofta hj�lpa till med allt m�jligt!Allt fr�n att bygga dekor och sy kostymer till att spela i en orkester om man vill och agera p� scen. Ut�ver dessa uppgifter som �r direkt kopplade till f�rest�llningen s� kan man naturligtvis ocks� ordna kalas, laga mat, pyssla med kommunikation eller grotta ner sig i ekonomi och annat skoj!" + q3() + "I varje Sm�n�je kommer det att vara runt 50 personer." + q4("Sm�n�jen") + "Det lilla formatet ger massor av nya id�er och m�jligheter att r�ra sig utanf�r den vanliga boxen vad g�ller en f�rest�llning med mycket interaktion och publikkontakt. Den lilla ensemblen i kombination med den intensiva repetitionsperioden ger ocks� en fantastisk sammanh�llning som du kommer minnas f�r livet!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Snaxeriet",
 				R.drawable.snaxeriet,
 				q1("Snaxeriet") + "Snaxeriet �r en av Lundakarnevalens yngsta (och roligaste) sektioner! Syftet �r att tillfredsst�lla karnevalsbes�karnas sug genom att s�lja snacks, dryck och annan l�ttare f�rt�ring. Visionen �r inte bara att tillgodose tilltugg till bes�karna, utan �ven att ses som ett karnevalistiskt inslag med humoristiska produkter, f�rpackningar och f�rs�ljningsmetoder. Snaxeriet kommer att inte bara att finnas tillg�ngliga i f�rs�ljningsbodar p� omr�det, utan �ven genom kringg�ende f�rs�ljning och som t�gvagn i t�get. Under v�ren kommer �ven en del karnevalssnacks att b�rja s�ljas runt om i Lund." + q2() + "Oavsett om du gillar att skruva, spika eller fika s� �r Snaxeriet n�gonting f�r dig! Hos oss finns m�nga olika omr�det att arbete inom. F�rutom sj�lva snacksf�rs�ljningen kommer du och din grupp att f� vara med och skapa, p�verka och s�tta er pr�gel p� Snaxeriet. Om du �r ut�triktad och kreativ �r du som klippt och skuren f�r Snaxeriet. Vi beh�ver allt ifr�n serviceinriktade s�ljare och baristor till hemmasnickare och hobbyskr�ddare med sinne f�r f�rg och form. Hos oss f�r du testa p� allt!<br><br>Om du �r sugen p� att ta lite mer ansvar s� beh�ver vi ett tjugotal gruppchefer som kommer att leda grupper p� cirka 8 karnevalister-" + q3() + "Totalt kommer vi att bli mellan 180 till 200 glada karnevalister i sektionen." + q4("Snaxeriet") + "Som karnevalist i Snaxeriet �r du med och skriver karnevalshistoria n�r Lundakarnevalen f�r f�sta g�ngen g�r en fullfj�drad snackssattning! Under v�ren kommer vi tillsammans st�ta p� m�nga nya utmaningar som som kommer att beh�va bra id�er, kreativitet och humor f�r att ta sig an. Sedan skadar det ju inte att s�ga att den huvudsakliga f�dan f�r en snaxerist under karnevalsv�ren kommer att vara popcorn, sockervadd och karnev�l.",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Spexet",
 				R.drawable.spexet,
 				q1("Spexet") + "V�r sektion ska producera ett av de sju stora n�jena, n�rmare best�mt det anrika Karnevalsspexet. Under n�gra intensiva veckor i april-maj ska vi tillsammans skapa ett spex i AF-borgens Stora Sal samtidigt som vi ska f� m�nga nya v�nner och minnen f�r livet. Under karnevalshelgen i maj ska vi sammanlagt ge ca 20 f�rest�llningar � 45 minuter under tre dagar. Nyhet f�r i �r �r att det �r blandad ensemble p� scen." + q2() + "I Karnevalsspexet kan du g�ra allt fr�n att st� p� scen, spela instrument, sk�ta ljud och ljus till att snickra, m�la, sy, sminka, fixa med mat och dryck, planera interna events och fester samt designa grafiskt material s� som program och affisch. Och s�klart ha superroligt!" + q3() + "Ca 75." + q4("Spexet") + "Att vi f�r skapa ett spex tillsammans! Att f� arbeta ihop mot samma m�l och sedan k�nna den gemensamma gl�djen n�r vi f�r se slutresultatet �r en enast�ende upplevelse. Det �r �ven fantastiskt att f� visa upp v�rt arbete inf�r karnevalsbes�karna. Dessutom har vi den speciella f�rm�nen att vara det n�je som flest bes�kare kommer att ha m�jlighet att se.",
 				true));
 		db.addItem(new LKSectionsItem(
 				"T�get",
 				R.drawable.tag,
 				q1("T�get") + "Kort sagt: T�get har som huvuduppgift att under tv� tillf�llen sprida gl�dje och karnevalsanda genom Lunds gator och torg.<br><br>Lite mindre kort sagt: T�get kulminerar med tv� avg�ngar under l�rdagen och s�ndagen under karnevalsdagarna. Turen som g�r genom centrala Lund best�r av cirka 1000 karnevalister som bjuder de hundratusentals �sk�darna p� humoristiska och galna uppt�g de har i de olika vagnarna, mellanekipagen och orkestrarna. �Men v�gen till avf�rd best�r av en v�r av planering och fix. Det �r fr�mst m�naden innan avf�rd d� t�gomr�det invigs som verksamheten drar ig�ng n�r alla samlas f�r att bygga och sy upp sina ekipage.<br><br><b><i>Vad finns det f�r olika saker att g�ra i din sektion?�(de olika delarna av t�get)</i></b><br>Banverket � �ser till att det finns en t�gv�g att rulla p�! (R�lsen och S�kerheten)<br><br>Centralstationen � H�r �terfinns allt som beh�vs f�r att t�get ska kunna rulla; N�gra som h�ller koll p� biljettpengarna(ekonomin), n�gra som ber�ttar n�r avg�ngarna �r (kommunikationen), n�gon som ser till att det finns aktiviteter och fester f�r alla t�gkarnevalister(Vieriet), n�gra som ser till att alla blir riktigt snygga innan avf�rd(sminket) och n�gra som ansvarar f�r t�gomr�det/stationen d�r alla f�rberederer sker (Omr�det).<br><br>T�get(det som r�r sig) � H�r har vi alla vagnar, mellanekipage och orkestar som utg�r sj�lva den delen av t�get som r�r sig p� t�gv�gen." + q3() + "Med alla som jobbar med sj�lva t�get till alla p� vagnarna, mellanekipage och orkestrarna s� blir vi ett h�rligt h�ng p� cirka 1000 karnevalister." + q4("T�get") + "Att vi f�r tillsammans g� ut p� gatorna och visa varf�r Lundakarnevalen �r det roligaste man kan var med och se p�!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"T�ltn�je",
 				R.drawable.nojen,
 				q1("Tältn�je") + "T�ltn�jessektionen har som uppgift att planera, samordna och genomf�ra de 20-tal t�lt av olika typer och storlekar som fyller Karnevalsomr�det. I t�lten kommer det att finnas sm� shower, suspekta upplevelser, s�ng, skratt och sl�vitsande." + q2() + "I t�ltn�jessektionen kan du f� g�ra allt m�jligt kul. Du kommer att kunna syssla med att bygga dekor, sy, sminka, spela teater mm. Du kan ocks� vara med och laga mat, ordna med sektionens fester, jobba med ekonomi och administration, bygga upp t�lt, sk�ta teknik och massa annat roligt. Sjukt varierande allts�!" + q3() + "Vi kommer att vara n�gonstans mellan 450-700 h�rliga karnevalister." + q4("T�ltn�jena") + "Att du f�r chansen att jobba med din kreativitet i varierande uppgifter f�r att skapa roliga, sp�nnande och �verraskande upplevelser f�r karnevalsbes�karna. T�ltn�jessektionen �r dessutom en av karnevalens st�rsta sektioner vilket g�r att du f�r chansen att l�ra k�nna massor av nya h�rliga m�nniskor. Det �r ocks� en klassisk och traditionell sektion d�r du verkligen kan f� m�jlighet att vara med och s�tta pr�gel p� Futuralkarnevalen 2014!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Tombolan",
 				R.drawable.tombolan,
 				q1("Tombolan") + "Tombolan sprider karnevalsgl�dje genom de spel och t�vlingar som kommer att finnas p� omr�det. Vi skapar de mest fantastiska och kn�ppa spelbodar som Liseberg aldrig sett genom musik, lampor, satir och futurism." + q2() + "Alla bodar och id�er kring dessa kommer att utformas tillsammans vilket g�r att det finns m�jlighet att f� g�ra det mesta. Kanske besitter du en kompetens som g�r att man skulle kunna skapa ett helt nytt lyckohjul? I tombolan kommer man �ven att f� snickra, sy, spela spel, laga mat, skicka mail, g� p� fest, kl� ut sig, dricka karnev�l och fixa med blinkande lampor!" + q3() + "Vi siktar p� att vara ungef�r 150 sk�na tombolister." + q4("Tombolan") + "Hos oss kan man verkligen f� utlopp f�r sin kreativitet och utforma en id� fr�n grunden. Variationen �r ocks� n�got speciellt d� man kommer att f� �gna sig �t blandade uppgifter samtidigt som man l�r k�nna nya sk�na m�nniskor av alla dess sorter!",
 				true));
 		db.addItem(new LKSectionsItem(
 				"Vieriet",
 				R.drawable.vieriet,
 				q1("Vieriet") + "Vieriet �r karnevalens sprakande komet av gemenskap, pepp och vi-k�nsla, som kommer att lysa klart under hela v�ren! Vi �r de som f�r varenda karnevalist i varenda sektion att g� och l�gga sig med ett leende p� l�pparna. Vi �r de som ser till att du st�r med femtusen nya v�nner i slutet av maj. Vi �r de som ser till att det blir Upprop, Karnevelj, Karnebal, Strandfest och mycket annat, d�r alla karnevalister kan f�renas och smitta varandra med sin gl�dje och sitt engagemang. Om Karnevalshelgen �r m�let ser vi till att hela upploppet kantas av guld och gr�na skogar." + q2() + "Mest kommer det att handla om att skapa fantastiska event tillsammans! Planera, koordinera, dekorera, laga mat, st� i bar, st�da, dra vitsar och skratta �t sina egna sk�mt." + q3() + "Vi kommer att bli runt hundra stycken vierister, d�rav ungef�r sjuttio stycken tills�tts innan uppropet!" + q4("vieriet") + "Vieriet �r den gladaste platsen p� jorden! Till skillnad fr�n de flesta andra sektioner arbetar vieriet kontinuerligt under v�ren och kan med enorm stolthet avnjuta crescendot av gemenskap och vi-k�nsla den 16-18 maj, som vi alla d� tillsammans har byggt upp. Sedan �r det ju inte heller varje dag man har m�jlighet att f� vara med och skapa en fest f�r 3000 personer, slussa tusentals karnevalister till Lomma eller ordna Sveriges st�rsta fracksittning.",
 				true));
	}

	/* The questions*/
	public String q1(String sName) {
		return "<b><i>Vad ska " + sName + " g�ra? Var �r den �vergripande \"uppgiften?\"</i></b><br>";
	}
	public String q2() {
		return "<br><br><b><i>Vad finns det f�r olika saker att g�ra i din sektion?</i></b><br>";
	}
	public String q3() {
		return "<br><br><b><i>Hur m�nga kommer ni vara i sektionen (ungef�r)?</i></b><br>";
	}
	public String q4(String sName) {
		return "<br><br><b><i>Vad �r det b�sta med " + sName + "? Vad �r speciellt?</i></b><br>";
	}
	
	public void addDataSekBg(){
		AsyncTask<?, String, String> regInBackground = new AsyncTask<Object, String, String>(){

			@Override
			protected String doInBackground(Object... params) {
				storeSekData();
				Log.d(LOG_TAG, "added sek data!");
				return null;
			}
			
		}.execute();
	}
	
	private void createMenuThread() {
		/*start up the splash screen and main menu in a time delayed thread*/
		new Handler().postDelayed(new Thread() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashscreenActivity.this, ContentActivity.class);
				SplashscreenActivity.this.startActivity(intent);
				SplashscreenActivity.this.finish();
				overridePendingTransition(R.layout.fade_in, R.layout.fade_out);
			}
		}, THREAD_DELAY);
	}
	
	public static void regInBackground(final Context context, final GoogleCloudMessaging gcm){
		AsyncTask<?, String, String> regInBackground = new AsyncTask<Object, String, String>(){

			@Override
			protected String doInBackground(Object... params) {
				String regId = null;
				try{
					/*if(gcm == null){
						gcm = GoogleCloudMessaging.getInstance(context);
					}*/
					regId = gcm.register(GCMReceiver.SENDER_ID);
					Log.d(LOG_TAG, "Got regId: "+regId);
					storeAsRegId(regId, context);					
				}catch(IOException e){
					Log.e(LOG_TAG, "Exception thrown when trying to register"+e);
				}
				
				// POST reg id to server
				LKRemote remote = new LKRemote(context, new TextResultListener(){
					@Override
					public void onResult(String result) {
						Log.d(LOG_TAG, "regId result: "+result);
					}
				});
				remote.requestServerForText("phones.json", "{\"google_token\":\""+regId+"\"}", LKRemote.RequestType.POST, false);
				
				return regId;
			}
			
			@Override
			protected void onPostExecute(String message){
				
			}
			
		}.execute();
	}
	
	public boolean checkForGooglePlay(){
		int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(result != ConnectionResult.SUCCESS){
			if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
	            GooglePlayServicesUtil.getErrorDialog(result, this, GCMReceiver.PLAY_SERVICES_RESOLUTION_REQUEST).show();
	        } else {
	            Log.e(LOG_TAG, "This device is not supported.");
	            // TODO: What to do?? 
	        }
		}
		return true;
	}
	

//	View.OnClickListener cont = new View.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			launchApp();
//		}
//	};
//	
//	private void launchApp(){
//		Intent intent = new Intent(context, ContentActivity.class);
//		startActivity(intent);
//		finish();
//	}
	
	/** 
	 * Populate SP with some basic data if there is none.
	 */
	private void populateSp(){
		// Init with registration data if none exists. 
		SharedPreferences sp = this.getSharedPreferences(LKFragment.SP_NAME, MODE_PRIVATE);
		int registrationStep = sp.getInt(LKFragment.SP_KEY_REGISTRATION_STEP, -1);
		
		// Set values
		Editor editor = sp.edit();
		if(registrationStep == -1){
			editor.putInt(LKFragment.SP_KEY_REGISTRATION_STEP, 0);
		}
		
		editor.commit();
		
//		LKSQLiteDB db = new LKSQLiteDB(context);
//		int heigestLocalId = db.heighestMessageId();
//		db.close();
		
//		LKRemote remote = new LKRemote(this,new LKRemote.TextResultListener() {
//			@Override
//			public void onResult(String result) {
//				LKSQLiteDB db = new LKSQLiteDB(context);
//				Log.d(LOG_TAG, result);
//				Gson gson = new Gson();
//				Response.Message resp = gson.fromJson(result, Response.Message.class);
//				if(resp.status.equals("success")){
//					Log.d("SplashScreenActivity", resp.notifications);
//				}else{
//					Log.d("SplashScreenActivity", "Something went horribly wrong...");
//				}
//				db.close();
//			}
//		});
		
		
	}
}
