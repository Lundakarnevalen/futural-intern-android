package util;

import android.view.View;

public class ViewHelper {
	public static View lastCache;
	public static int lastId;
	public static long lastAccess;
	
	public static <T> T get(int id, View parent, Class<T> clz) {
		long t = System.currentTimeMillis();
		if(lastCache != null && lastAccess > t - 300 && id == lastId){
			return clz.cast(lastCache);
		}
		lastId = id;
		lastAccess = t;
		return clz.cast(lastCache = parent.findViewById(id));
    }
}
