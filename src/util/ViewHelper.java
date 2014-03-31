package util;

import android.view.View;

public class ViewHelper {
	public static <T> T get(int id, View parent, Class<T> clz) {
        return clz.cast(parent.findViewById(id));
    }
}
