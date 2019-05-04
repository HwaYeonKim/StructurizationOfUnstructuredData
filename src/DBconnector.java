import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;



public class DBconnector {
	Connection connection = null;
//	Statement st = null;
	String url = "jdbc:mysql://localhost/TrainInfoProject";
	String username = "root";
	String password = "1234";
	String StationTable = "StationInfo";
	
	
	public void ConnectionOpen(){
		
		try {
			//STEP 2: Register JDBC driver
            Class.forName("com.mysql.jdbc.Driver");
            DriverManager.setLoginTimeout(10);
            //STEP 3: Open a connection
            System.out.println("Connecting to a selected database...");
            connection = DriverManager.getConnection(url,username, password);
            System.out.println("Connected database successfully...");

            //STEP 4: Execute a query
            System.out.println("Creating statement...");
    //        st = connection.createStatement();
           
			System.out.println("Connection has been established");
		} catch (SQLException sqex) {
			System.err.println("database connection: " + sqex.getMessage()+"\nSQLState: "+sqex.getSQLState());
		} catch (ClassNotFoundException e) {
		    e.printStackTrace();
		   }finally {
//			try {
//				if (connection != null)
//					connection.close();
//			} catch (SQLException se) {
//				se.printStackTrace();
//			}
		}
	}
	
	public void ConnectionClose(){
		try {
			connection.close();
		} catch (SQLException sqex) {
			System.out.println("SQLException: " + sqex.getMessage());
			System.out.println("SQLState: " + sqex.getSQLState());
		} finally {
//			try {
//				if (st != null)
//					st.close();
//			} catch (SQLException se2) {
//			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
	
	public String[] getTrainData(String from, String to) {
		
		Map<String, String[]> map = new HashMap<String,String[]>();
		
		Statement st = null;
		String sql1 = "SELECT * FROM StationInfo WHERE st_name = '"+from+"';";
		String sql2 = "SELECT * FROM StationInfo WHERE st_name = '"+to+"';";
		
		try {
			st=connection.createStatement();
	//		System.out.println("sql : " +sql1);
			ResultSet rs1 = st.executeQuery(sql1);
			
			while (rs1.next()) {
			String[] str = { rs1.getString("st_organization"), rs1.getString("st_train")};
			map.put(str[0]+"*"+str[1], str);
			}
			
			rs1.close();
			
			ResultSet rs2 = st.executeQuery(sql2);
			
			while (rs2.next()) {
			String str = rs2.getString("st_organization")+"*"+rs2.getString("st_train");
					if(map.containsKey(str)){
						return map.get(str);
				}
			
			}
			rs2.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	public void insertTrainInfo(TableData tb) {
		Statement st = null;
		String sql = "INSERT INTO `TrainInfoFromSNS` (`info_id`, `info_datetime`, `info_organization`, `info_trainNum`, `info_stationName`, `info_direction`, `info_type`, `info_sns`) "
				+ "VALUES (NULL, '"+tb.datetime_format+"', '"+tb.organization+"', '"+tb.trainNum+"', '"+tb.stationName+"', '"+tb.direction+"', '"+tb.infoType+"', '"+tb.SNScontent+"')";

		try {
			st = connection.createStatement();
			int rs = st.executeUpdate(sql);

			if( rs == 1 )
            {
                System.out.println("Success to Insert");
            }else{
                System.out.println("Fail");
            }
			st.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return null;
	}

	/*
	public boolean isStationsExist(String station){
		Statement st = null;
		String sql = "SELECT * FROM StationInfo WHERE st_name = '"+station+"';";
		
		try {
			st=connection.createStatement();
			System.out.println("sql : " +sql);
			ResultSet rs = st.executeQuery(sql);
			
			while (rs.next()) {
			System.out.print(rs.getString("st_organization")+"==");
			System.out.print(rs.getString("st_train")+"==");
			System.out.println(rs.getString("st_name"));
			}
			
			rs.close();
			st.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	*/
	
}


