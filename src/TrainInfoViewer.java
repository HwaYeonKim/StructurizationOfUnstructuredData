import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrainInfoViewer {
	
	static Vector<TrainInfo> trainInfoVec = new Vector<TrainInfo>();
	static InfoPatterns infoPatterns = new InfoPatterns();
	static DBconnector dbc = new DBconnector();
	
	public static void main(String args[]){
		
		dbc.ConnectionOpen();
		//철도정보 입력
		setTrainInfo();
		createTableData();
	//	makeTable();
		
	}
	
	static Vector<IndexStatus> checkTrainStatus(String str){
		Vector<IndexStatus> vec = new Vector<IndexStatus>();
		
		for(int i=0; i<trainInfoVec.size(); i++){
			for(int j=0; j<trainInfoVec.get(i).getKeywords().size(); j++){
				int index = str.indexOf(trainInfoVec.get(i).getKeywords().get(j));
				if(index != -1){
					IndexStatus is = new IndexStatus(index,trainInfoVec.get(i),trainInfoVec.get(i).getKeywords().get(j).length());
				    vec.add(is);
				}
			}
		}
		Collections.sort(vec, new IndexStatusCompare() );
		return vec;
	}
	
	static void createTableData(){
		try{
			
			BufferedReader in = new BufferedReader(new FileReader("./SNScontents.txt"));
		    String str;
		    
		    while ((str = in.readLine()) != null) {
		//    	System.out.println(str); //output
		//    	Map<String, TableData> tableDataMap = new LinkedHashMap<String, TableData>();
		    	Map<String, int[] > DateMap = new HashMap<String, int[]>();
		    	Map<String, String[] > strdirMap = new HashMap<String, String[]>();
		    	//check status
		    	Vector<IndexStatus> isvec = checkTrainStatus(str);
		    	
		    	//키워드를 기준으로 substring 생성.
		    	Vector<String> subStrings = new Vector<String>();
		    	IndexStatus is = null; int startIndex=0; 
		    	for(int i=0; i<isvec.size(); i++){
		    		if(!isvec.get(i).isSame(is)){
		    		if(startIndex > isvec.get(i).getFromIndex()){ //java.lang.StringIndexOutOfBoundsException 방지 
		    				isvec.remove(i);
		    				i--;
		    			}else{
		    			subStrings.add(str.substring(startIndex, isvec.get(i).getFromIndex()));
		    			startIndex = isvec.get(i).getToIndex();
		    			}
		    		}else{ // 키워드 겹치는 경우 삭제 
		    			isvec.remove(i);
		    			i--;
		    		}
		    		is=isvec.get(i);
		    	}
		    	
		    	//get DateTime
		    	
		    	int[] date = {0,0,0};
		    	Vector<int[]> timevec = new Vector<int[]>();
		    	
		    	for(int i=0; i<subStrings.size(); i++){
		    		date = getDateData(date, subStrings.get(i));
		    		int[] time = getTimeData(subStrings.get(i));
		    		if(time[0] == -1 && time[1] == -1 && !timevec.isEmpty()) timevec.add(timevec.lastElement());
		    		else timevec.add(time);
		    	}	
		    	
		    	for(int i=0 ; i< isvec.size(); i++){
		    		if(DateMap.containsKey(isvec.get(i).getStatus())){
		    			if(DateMap.get(isvec.get(i).getStatus())[0] == -1 && DateMap.get(isvec.get(i).getStatus())[1] == -1){
		    				DateMap.put(isvec.get(i).getStatus(), timevec.get(i)); // 겹치는 status 처리. 
		    			}
		    		}else{
		    			DateMap.put(isvec.get(i).getStatus(), timevec.get(i)); // 겹치는 status 처리. 
		    		}
		    		
		    	}
		    	
		    	//get station and direction
		    	
		    	//열차상태-역,방향정보,기관,호선 매칭 
		    	for(int i=0; i<subStrings.size(); i++){
		    		String[] st = getStnDirData(subStrings.get(i));
		    		if(!(st[0].equals("-") && st[1].equals("-"))){ //역정보존재 
		    			strdirMap.put(isvec.get(i).getStatus(), st);
		    		}
		    	}
		    	 
		    	//data 종합 
		    	if(DateMap.size() > strdirMap.size()){
		    		
		    	}
		    	String[] Dup = {"-","-","-","-"};
		    	for (Map.Entry<String, int[]> entry : DateMap.entrySet()) {
		    		TableData tb = new TableData();
		    		tb.setSNScontent(str);
	    			tb.setInfoType(entry.getKey());
	    		//	tb.setDateTime(date, entry.getValue());
	    			tb.setDateTimeFormat(date, entry.getValue());
	    			if(strdirMap.containsKey(entry.getKey())){ 
	    				Dup = strdirMap.get(entry.getKey());
	    	//			Dup[0] = strdirMap.get(entry.getKey())[0];
	    	//			Dup[1] = strdirMap.get(entry.getKey())[1];
	    	//			tb.setStDir(strdirMap.get(entry.getKey())[0], strdirMap.get(entry.getKey())[1]); 
	    				tb.setStDir(strdirMap.get(entry.getKey())); 
	    				}
	    			else{
	    				//TODO
	    				tb.setStDir(Dup);
	    	// 		tb.setStDir(Dup[0], Dup[1]); 
	    				}
	    			tb.print(); // output
	    			dbc.insertTrainInfo(tb);
		    	}
		    	
		    }
		    
		}catch (IOException e) {
	        System.err.println(e);
	        System.exit(1);
	    } 
	}
	
	
	static String[] getStnDirData(String str){
		String[] result = {"-","-","-","-"};
		
		try{
			for(int i=0; i<infoPatterns.getStnDirPattern().length; i++){
				Pattern pattern = Pattern.compile(infoPatterns.getStnDirPattern()[i]);
				Matcher matcher = pattern.matcher(str);
				if (matcher.matches()) {
					result[0] = matcher.group(1);
					result[1] = matcher.group(2);
					
					//'역'자 빼기
					Pattern st_pattern = Pattern.compile("(\\S*)역");
					Matcher m1 = st_pattern.matcher(result[0]);
					Matcher m2 = st_pattern.matcher(result[1]);
					
					String except = "대구역/동대구역/광주송정역/서울역";
					if(m1.matches()){
						if(!except.contains(result[0])) result[0] = m1.group(1);
					}
					if(m2.matches()){
						if(!except.contains(result[1]))  result[1] = m2.group(1);
					}
					
					//DB검사 후 역 이름 존재하면 break!
					String[] data = dbc.getTrainData(result[0], result[1]);
					if(data == null){
						//패턴매칭 
						Pattern train_pattern = Pattern.compile(".*\\s?([1-9])호선\\s?.*");
						Matcher m_t = train_pattern.matcher(str);
						if(m_t.matches()){
						   result[3] = m_t.group(1)+"호선";
						}
						return result;
					}else{
						result[2] = data[0];
						result[3] = data[1];
					}
					break;
				}
		}
		} catch (NumberFormatException nfe) {
			System.out.println("NumberFormatException");
		}
		
		return result;
	}
	
	static int[] getDateData(int[] r, String str) {

	int[] result = r;

	try {
			for (int i = 0; i < infoPatterns.getDatePattern().length; i++) {
				Pattern pattern = Pattern.compile(infoPatterns.getDatePattern()[i]);
				Matcher matcher = pattern.matcher(str);
				if (matcher.matches()) {
					int[] group = infoPatterns.getDatePosition(i);
					int group_cnt = 1;
					int[] date_g = { 0, 0, 0};
					for (int j = 0; j < group.length; j++) {
						if (group[j] == 1) {
							date_g[j] = Integer.parseInt(matcher.group(group_cnt++));
						}
					}

					for(int k=0; k<3; k++){
						result[k] = date_g[k];
					}
					
					break;
				}
			}
		
	} catch (NumberFormatException nfe) {
		System.out.println("NumberFormatException");
	}
	return result;

}
	static int[] getTimeData(String str) {

		int[] result = {-1,-1};
		try {
				for (int i = 0; i < infoPatterns.getTimePattern().length; i++) {
					Pattern pattern = Pattern.compile(infoPatterns.getTimePattern()[i]);
					Matcher matcher = pattern.matcher(str);
					if (matcher.matches()) {
						
						int[] group = infoPatterns.getTimePosition(i);
						int group_cnt = 1;
						int[] time_g = { -1,-1,-1 }; //TODO
						for (int j = 0; j < group.length; j++) {
							if (group[j] == 1) {
								if(group_cnt > matcher.groupCount()) break;
								if(matcher.group(group_cnt) == null){
									
								}else{
									time_g[j] = Integer.parseInt(matcher.group(group_cnt++));
								}
							} else if (group[j] == 12) {
								if (matcher.group(group_cnt).equalsIgnoreCase("오전")) {
									time_g[j] = 0;
								} else if(matcher.group(group_cnt).equalsIgnoreCase("오후")) {
									time_g[j] = 12;
								}
								group_cnt++;
							}
						}

						if(time_g[1] == -1 && time_g[2] == -1){ // 오전/오후만 설정한 경우.
							if(time_g[0] == 0){ //오전 
								result[0] = -10; result[1] = -10;
							}else{ //오후 
								result[0] = -20; result[1] = -20;
							}
							break;
						}else{
						//오전 12시 -> 0시(24시), 오후 12시 -> 12시 
						if(time_g[0] > 0 && time_g[1] == 12) { //오후 12시 
							result[0] = 12;
						}else if(time_g[0] == 0 && time_g[1] == 12){ // 오전 12시 
							result[0] = 0;
						}else{
							result[0] = time_g[1];
							if(time_g[0] >0) result[0] += time_g[0];
							}
						}
				//		if(time_g[2] == -1) time_g[2]++;
						result[1] = time_g[2];
						
						break;
					} 
				}
		} 
		catch (NumberFormatException nfe) {
			System.out.println("NumberFormatException");
		}
		return result;
	}
	
	static void setTrainInfo(){
		 try {
		      BufferedReader in = new BufferedReader(new FileReader("./train_info.txt"));
		      String str;

		      while ((str = in.readLine()) != null) {
		    	  
		    	  StringTokenizer st = new StringTokenizer(str,"**"); 
		    	  String status = st.nextToken();
		    	  String exp = st.nextToken();
		    	  String keys = st.nextToken();
		    	  StringTokenizer st_key = new StringTokenizer(keys, "/");
		    	  Vector<String> key_vec = new Vector<String>();
		    	  
		    	  while(st_key.hasMoreTokens()) {
		    		 key_vec.add(st_key.nextToken()); 
		    	  }
		    	  
		    	  TrainInfo ti = new TrainInfo(status, exp, key_vec);
		    	  trainInfoVec.add(ti);
		    	  }
		      
		      in.close();
		      	
		    } catch (IOException e) {
		        System.err.println(e); // 에러가 있다면 메시지 출력
		        System.exit(1);
		    }
	}
}

class IndexStatus{
	int from_index;
	int to_index;
	TrainInfo trainInfo;
	IndexStatus(int index, TrainInfo trainInfo, int str_size){
		this.from_index = index;
		this.to_index = index + str_size;
		this.trainInfo = trainInfo;
	}
	int getFromIndex(){
		return from_index;
	}
	
	int getToIndex(){
		return to_index;
	}
	
	String getStatus(){
		return trainInfo.getStatus();
	}
	
	boolean isSameStatus(String str){
		return trainInfo.isSameStatus(str);
	}
	
	boolean isSame(IndexStatus is){
		if(is == null) return false;
		if(this.from_index == is.from_index || this.to_index == is.to_index){
			return true;
		}else{
			return false;
		}
	}
}

class IndexStatusCompare implements Comparator<IndexStatus>{

	@Override
	public int compare(IndexStatus o1, IndexStatus o2) {
		if(o1.from_index == o2.from_index){
			return o2.to_index - o1.to_index;
		}
		return o1.from_index - o2.from_index;
	}
}
