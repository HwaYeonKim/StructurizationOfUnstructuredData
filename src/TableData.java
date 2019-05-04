import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

public class TableData implements Cloneable {
	int datetime[] = {0,0,0,0,-1,-1}; // year, month, date, day, hour, min
    Calendar datetime_; // 일
    String datetime_format;
	String organization; // 철도운영기관 
	String trainNum; // 호
	String stationName; // 역
	String direction; // 방향
	String infoType; // 정보유형
	String SNScontent; // sns 내용 원문
	
	public TableData(){
//		this.organization = "서울교통공사";
	};
	
	 public TableData clone() throws CloneNotSupportedException {
	     TableData tb = (TableData) super.clone();
	 //    tb.datetime = (int[]) this.datetime;
	     return tb;
	   }

	public void printDatetime(){
		System.out.print("printDatetime(): ");
		for(int i=0; i<6; i++){
			System.out.print(datetime[i]+".");
		}
		System.out.println();
	}
	
	public void setDateTimeFormat(int[] date, int[] time){
		StringBuilder sb = new StringBuilder();
		//int datetime[] = {0,0,0,0,-1,-1}; // year, month, date, day, hour, min
		for(int i=0; i< date.length; i++){
			if(date[i] == 0){
				sb.append("-");
			}else{
				//format
				NumberFormat nf = new DecimalFormat("00");
				sb.append(nf.format(date[i]));
			}
		}
		if(time[0] == -1 && time[1] == -1) sb.append("----");
		else if(time[0] == -10) sb.append("오전");
		else if(time[0] == -20) sb.append("오후");
		else{
			NumberFormat nf = new DecimalFormat("00");
			sb.append(nf.format(time[0]));
			sb.append(nf.format(time[1]));
		}
		this.datetime_format = sb.toString();
	}
	
	public void setStDir(String st, String dir){
		this.stationName = st;
		this.direction = dir;
	}
	
	public void setStDir(String[] arr){
		this.stationName = arr[0];
		this.direction = arr[1];
		this.organization = arr[2];
		this.trainNum = arr[3];
	}
	
	
	public void setDateTime(int[] date, int[] time){
		//TODO
		//없는 날짜 또는 시간을 어떻게 처리할지 
		//	this.datetime_.set(, month, date, hourOfDay, minute);
		for(int i=0; i<date.length; i++){
			this.datetime[i] = date[i];
		}
		for(int i=0; i<time.length; i++){
			this.datetime[date.length+i] = time[i];
		}
		
	}
	public int[] getDateTime(){
		return this.datetime;
	}
	public void setSNScontent(String sns){
		this.SNScontent = sns;
	}
	
	public void setInfoType(String infoType){
		this.infoType = infoType;
	}
	
	public void setDate(int[] date){
		
		for(int i=0; i<4; i++){
			this.datetime[i] = date[i];
		}
	}
	
	public void setTime(int[] time){
		//오전 12시 -> 0시(24시), 오후 12시 -> 12시 
		if(time[0] > 0 && time[1] == 12) { //오후 12시 
			this.datetime[4] = 12;
		}else if(time[0] == 0 && time[1] == 12){ // 오전 12시 
			this.datetime[4] = 0;
		}else{
		this.datetime[4] = time[0]+ time[1];
		}
		this.datetime[5] = time[2];
	}
	
	public boolean checkDate(){
		for(int i=0; i<4; i++){
			if(this.datetime[i] > 0) return true;
		}
		return false;
	}
	
	public boolean checkTime(){
		for(int i=4; i<6; i++){
			if(this.datetime[i] > -1) return true;
		}
		return false;
	}
	
	public void print(){
		System.out.println("---------");
		System.out.println("sns : " + SNScontent);
		System.out.print("date : " + datetime_format +"  ");
		/*
		for(int i=0; i<6; i++){
			System.out.print(this.datetime[i]+".");
		}
		*/
		System.out.println("station : " + stationName + "  direction : " + direction);
		System.out.println("organization : " + organization + "  trainNum : " + trainNum);
		System.out.println("infoType : " + infoType);
//		String organization; // 철도운영기관 
//		int trainNum; // 호
//		String stationName; // 역
//		String direction; // 방향
//		System.out.println();
	}

}
