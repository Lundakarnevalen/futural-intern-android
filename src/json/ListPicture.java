package json;

import java.util.List;

public class ListPicture {

	private String success;
	private List<Picture> photos;

	public String getThumb(int index) {
		return photos.get(index).thumb;
	}
	
	public boolean successful() {
		return success.equals("true");
	}
	
	public boolean isEmpty() {
		return photos.isEmpty();
	}
	
	public int lastIndex() {
		return photos.size() - 1;
	}
	
	public String getUrl(int index){
		int newPosition = convertIndex(index);
		return photos.get(newPosition).url;
	}

	public String getCation(int position) {
		int newPosition = convertIndex(position);
		return photos.get(newPosition).caption;
	}
	
	public String getName(int position) {
		int newPosition = convertIndex(position);
		return photos.get(newPosition).name;
	}
	
	private int convertIndex(int index) {
		return photos.size() - index - 1;
	}
}
