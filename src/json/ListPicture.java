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
		index = convertIndex(index);
		return photos.get(index).url;
	}

	public String getCation(int position) {
		return photos.get(position).caption;
	}
	
	public String getName(int position) {
		return photos.get(position).name;
	}
	
	
	private int convertIndex(int index) {
		return photos.size() - index - 1;
	}
}
