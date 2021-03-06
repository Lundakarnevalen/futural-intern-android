package json;

/**
 * Parsing a user in json.
 * @author alexander
 */
public class UserWrite {
	public String personnummer, fornamn, efternamn, gatuadress, postnr, postort, email, telnr, matpref, engagerad_kar, engagerad_nation;
	public String google_token, engagerad_studentikos, engagerad_etc, created_at, updated_at, ovrigt;
	public int avklarat_steg, kon_id, nation_id, storlek_id, terminer, korkort_id, snalla_intresse, snalla_sektion, tilldelad_sektion;
	public boolean jobbat_heltid, jobbat_styrelse, jobbat_forman, jobbat_aktiv, karnevalist_2010, vill_ansvara, medlem_af, medlem_kar, medlem_nation, karneveljsbiljett;
	public int[] intresse_ids, sektion_ids;
	public Foto foto;
	
	public UserWrite(){
		foto = new Foto();
	}
}
