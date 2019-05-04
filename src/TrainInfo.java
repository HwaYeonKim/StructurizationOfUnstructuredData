import java.util.Vector;

public class TrainInfo {
	String status;
	String explanation;
	Vector<String> keywords;
	
	public TrainInfo(String status, String explanation,Vector<String> keywords){
		this.status = status;
		this.explanation = explanation;
		this.keywords = keywords;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public String getExplanation(){
		return this.explanation;
	}
	
	public Vector<String> getKeywords(){
		return this.keywords;
	}
	
	public boolean isSameStatus(String str){
		return this.status.equalsIgnoreCase(str)?true:false;
	}
}
