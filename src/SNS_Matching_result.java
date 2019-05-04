import java.awt.dnd.DragGestureEvent;
import java.util.Date;

public class SNS_Matching_result {

	Date datetime; // 일
	String organization; // 철도운영기
	int trainNum; // 호
	String stationName; // 역
	String direction; // 방향
	TrainInfo infoType; // 정보유형
	String SNScontent; // sns 내용 원문

	public SNS_Matching_result(Date datetime, String organization, int trainNum, String stationName, String direction,
			TrainInfo infoType, String SNScontent) {
		this.datetime = datetime;
		this.organization = organization;
		this.trainNum = trainNum;
		this.stationName = stationName;
		this.direction = direction;
		this.infoType = infoType;
		this.SNScontent = SNScontent;
	}
	
	//get
}
