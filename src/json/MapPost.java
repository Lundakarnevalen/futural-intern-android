package json;

public class MapPost {
	public GPSpos cluster;
	public String token;
	public MapPost(float lat, float lng,String token) {
		this.token = token;
		cluster = new GPSpos(lat,lng);
	}
	private class GPSpos {
		private float lat;
		private float lng;
		public GPSpos(float lat, float lng) {
			this.lat = lat;
			this.lng = lng;
		}
	}
}
