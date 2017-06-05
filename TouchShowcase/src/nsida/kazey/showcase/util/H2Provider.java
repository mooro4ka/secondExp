package nsida.kazey.showcase.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.h2.jdbcx.JdbcConnectionPool;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nsida.kazey.showcase.model.Goods;
import nsida.kazey.showcase.model.Items;
import nsida.kazey.showcase.util.GoodsLoader.FileGoods;
import nsida.kazey.showcase.util.GoodsLoader.FileGroups;

public class H2Provider {
	
	private static String DB_CONNECTION;
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static H2Provider SINGLETONE;
    private JdbcConnectionPool connectionPool = null;
    
    private H2Provider() {
    	DB_CONNECTION = "jdbc:h2:"+AppSettings.get("bd") + ";mode=MySQL";
		//Server.createTcpServer().start();
		connectionPool = JdbcConnectionPool.create(DB_CONNECTION, DB_USER, DB_PASSWORD);
		connectionPool.setMaxConnections(10);
    }
    
    static {
    	SINGLETONE = new H2Provider();
    }
    
    private static Connection getConnection() throws SQLException  {				
    	return SINGLETONE.connectionPool.getConnection();
    }
	
	public static ObservableList<Items> getItemsForShowcase(Items parent) {
		
		StringBuilder queryString = new StringBuilder();
		
		ObservableList<Items> itemsList = FXCollections.observableArrayList();
		
		try {

			Connection connection = getConnection();	
			Statement stmt = connection.createStatement();
							
			if (parent == null) {
				queryString.append("SELECT DISTINCT ");
				queryString.append("NAME,");
				queryString.append("IMG ");
				queryString.append("FROM SHOWCASE.GROUPS ");
				queryString.append("ORDER BY NAME");
				
				ResultSet rs = stmt.executeQuery(queryString.toString());
				
				while (rs.next()) {
					itemsList.add(new Goods(
							true, 
							"", 							
							rs.getString("NAME"), 
							rs.getString("IMG"), 
							"", 
							0F, 
							0));
				}				
			} else {
				
				queryString.append("SELECT DISTINCT ");
				queryString.append("GD.CODE, ");
				queryString.append("GD.NAME, ");
				queryString.append("GD.BARCODE, ");
				queryString.append("GD.PRICE, ");
				queryString.append("GR.BUTTON_NAME, ");
				queryString.append("GR.BUTTON_IMG ");
				queryString.append("FROM SHOWCASE.GROUPS GR ");
				queryString.append("LEFT JOIN SHOWCASE.GOODS GD ");
				queryString.append("ON GR.GOODS = GD.CODE ");
				queryString.append("WHERE GR.NAME = '" 
									+ parent.getButtonDescription() + "'");
				queryString.append("AND NOT GD.NAME IS NULL ");
				queryString.append("ORDER BY BUTTON_NAME");
				
				ResultSet rs = stmt.executeQuery(queryString.toString());
	
				while (rs.next()) {
					itemsList.add(new Goods(
							false, 
							rs.getString("NAME"),
							rs.getString("BUTTON_NAME"),
							rs.getString("BUTTON_IMG"), 
							rs.getString("BARCODE"), 
							rs.getFloat("PRICE"), 
							rs.getInt("CODE"))
					);
				}
			}
			
			connection.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return itemsList;		
	}
	
	public static ObservableList<Items> getItemsForPacking(Integer code) {
		
		StringBuilder queryString = new StringBuilder();
		ObservableList<Items> itemsList = FXCollections.observableArrayList();
		String strFilter = (code == null 
				? "" : "WHERE CODE = " + Integer.toString(code)) + " ";
		
		try {

			Connection connection = getConnection();	
			Statement stmt = connection.createStatement();
							
			queryString.append("SELECT DISTINCT ");
			queryString.append("CODE, ");
			queryString.append("NAME, ");
			queryString.append("BARCODE, ");
			queryString.append("PRICE ");
			queryString.append("FROM SHOWCASE.GOODS ");
			queryString.append(strFilter);
			queryString.append("ORDER BY NAME");
			
			ResultSet rs = stmt.executeQuery(queryString.toString());

			while (rs.next()) {
				itemsList.add(new Goods(
						false, 
						rs.getString("NAME"),
						"",
						"", 
						rs.getString("BARCODE"), 
						rs.getFloat("PRICE"), 
						rs.getInt("CODE"))
				);
			}
			
			connection.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return itemsList;		
	}

	public static void writeGroups(ObservableList<FileGroups> listFileGroups) {
		try {
			
			if (listFileGroups.size() == 0)
				return;

			Connection connection = getConnection();
			connection.setAutoCommit(false);
			
			Statement stmt = connection.createStatement();
			
			stmt.addBatch("TRUNCATE TABLE SHOWCASE.GROUPS");
			
			for (FileGroups tempGroup : listFileGroups) {
				stmt.addBatch(
						"INSERT INTO SHOWCASE.GROUPS(" + 
						"NAME, IMG, BUTTON_NAME, BUTTON_IMG, GOODS) " +
						"VALUES (" + 
						"'" + tempGroup.name.replace(",", ".") + 
						"', '" + tempGroup.img + 
						"', '" + tempGroup.button_name.replace(",", ".") + 
						"', '" + tempGroup.button_img.replace(",", ".") +
						"', " + Integer.toString(tempGroup.goods) + 
						")");
			}
			
			stmt.executeBatch();
			
			connection.commit();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		} 				
	}

	public static void writeGoods(ObservableList<FileGoods> listFileGoods) {
		try {
			
			if (listFileGoods.size() == 0)
				return;
			
			Connection connection = getConnection();
			connection.setAutoCommit(false);
			
			Statement stmt = connection.createStatement();
			
			for (FileGoods tempGoods : listFileGoods) {
				stmt.addBatch(
						"INSERT INTO SHOWCASE.GOODS " +  
						"(CODE, NAME, BARCODE, PRICE) " + 
						"VALUES " + 
						"(" + tempGoods.code + 
						", '" + tempGoods.name.replace(",", ".") + 
						"', '" + tempGoods.barcode.replace(",", ".") + 
						"', " + String.format("%.2f", tempGoods.price).replace(",", ".") +") " + 
						"ON DUPLICATE KEY UPDATE " +
						"NAME = '" + tempGoods.name.replace(",", ".") + "'," +  
						"BARCODE = '" + tempGoods.barcode.replace(",", ".") + "'," +  
						"PRICE = " + String.format("%.2f", tempGoods.price).replace(",", "."));
			}
			
			stmt.executeBatch();
			
			connection.commit();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		} 				
	}

}
