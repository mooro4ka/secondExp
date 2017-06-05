package nsida.kazey.showcase.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class GoodsLoader {
	
	private Timer myTimer;
	private ArrayList<String> imgFilesList;
	private String ftpDir;
	private String localDir;
	private String localProgramDir;
	
	public GoodsLoader() {
		myTimer = new Timer();
		imgFilesList = new ArrayList<String>();
		ftpDir = AppSettings.get("ftpDir");
		localDir = AppSettings.get("localGoodsImageDir");
		localProgramDir = AppSettings.get("localProgramDir");
		
		if (ftpDir.substring(ftpDir.length()-1, ftpDir.length()) != "/")
			ftpDir += "/";
		
		if (localDir.substring(localDir.length()-1, localDir.length()) != "/")
			localDir += "/";
		
		if (localProgramDir.substring(localProgramDir.length()-1, localProgramDir.length()) != "/")
			localProgramDir += "/";		
	}
	
	public void Start() {
		myTimer.schedule(new MyTimerTask(), 5000, 10000);
	}
	
	private boolean updateMe() {
		try {
			
			String updatingFile = ftpDir + "updating.flg";
			
			if(!Files.exists(Paths.get(updatingFile)))
				Files.createFile(Paths.get(updatingFile));			
						
			boolean wasUpdate = transferUpdateFile();
			
			Files.deleteIfExists(Paths.get(updatingFile));
			
			if (wasUpdate) return true;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}		
	
	private boolean transferUpdateFile() {
		
		try {
			
			ArrayList<Path> p = findFilePathsWhithEnd(ftpDir, "TouchShowcase.upd");
			
			for (Path sourcePath : p) {
				try {
					Path targetPath = Paths.get(localProgramDir + "updTouchShowcase.jar");
					Files.deleteIfExists(targetPath);
					Files.move(sourcePath, targetPath);
					return true;
				} catch (Exception e) { 
					e.printStackTrace();
				} 		        		
	        }
			
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return false;		
	}

	private void loadGoods() {
		try {
			
			String uploadingFile = ftpDir + "uploading.flg";
			
			if(!Files.exists(Paths.get(uploadingFile)))
				Files.createFile(Paths.get(uploadingFile));			
			
			ObservableList<FileGoods> listFileGoods = FXCollections.observableArrayList();
			ObservableList<FileGroups> listFileGroups = FXCollections.observableArrayList();
			
			fillListFromFileByItems(listFileGoods);
			fillListFromFileByGroups(listFileGroups);
			
			writeListFromFileByGoods2DB(listFileGoods);
			writeListFromFileByGroups2DB(listFileGroups);
			
			transferImages();
			
			Files.deleteIfExists(Paths.get(uploadingFile));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void transferImages() {
		
		try {
			
			for (String currentImgFileName : imgFilesList) {
				ArrayList<Path> p = findFilePathsWhithEnd(ftpDir, currentImgFileName);
				
				for (Path sourcePath : p) {
					try {
						Path targetPath = Paths.get(localDir + currentImgFileName);
						Files.deleteIfExists(targetPath);
						Files.move(sourcePath, targetPath);
					} catch (Exception e) { 
						e.printStackTrace();
					}		        		
		        }
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void fillListFromFileByGroups(ObservableList<FileGroups> fileGroupsList) {
		try {
			
			ArrayList<Path> p = findFilePathsWhithEnd(ftpDir, "layout.xml");
			
			for (Path currentPath : p) {

				DocumentBuilder documentBuilder = DocumentBuilderFactory
													.newInstance().newDocumentBuilder();
				Document document = documentBuilder.parse(currentPath.toString());			
				Node root = document.getDocumentElement();			
				NodeList groups = root.getChildNodes();
				
	            for (int i = 0; i < groups.getLength(); i++) {
	            	Node currentGroup = groups.item(i);
	            	
	            	if (currentGroup.getNodeType() != Node.TEXT_NODE) {
	               		NodeList buttons = currentGroup.getChildNodes();
	            		NamedNodeMap attributes = currentGroup.getAttributes();       		            		
	            		
	            		String groupName = attributes.getNamedItem("name").getNodeValue();
	            		String groupImg = "";
	            		try { groupImg = attributes.getNamedItem("img").getNodeValue(); }
	            			catch (Exception e) {}
	             		
	            		for (int j = 0; j < buttons.getLength(); j++) {
	            			Node currentButton = buttons.item(j);
	            			
	            			if (currentButton.getNodeType() != Node.TEXT_NODE) {
			            		attributes = currentButton.getAttributes();
			            		FileGroups newFG = new FileGroups();
			            		
			            		newFG.name = groupName;
			            		newFG.img = groupImg;
			            		newFG.button_name = attributes.getNamedItem("name").getNodeValue();
			            		newFG.button_img = attributes.getNamedItem("img").getNodeValue();
			            		newFG.goods = Integer.parseInt(attributes.getNamedItem("PLU").getNodeValue());
	            			
			            		fileGroupsList.add(newFG);
			            		
			            		imgFilesList.add(newFG.button_img);
	            			}
	            		}           		
		            }
	            }
	            
	            Files.deleteIfExists(currentPath);
	            
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void fillListFromFileByItems(ObservableList<FileGoods> listFileGoods) {
		try {

			ArrayList<Path> p = findFilePathsWhithEnd(ftpDir, "plu.xml");
			
			for (Path currentPath : p) {
	
				DocumentBuilder documentBuilder = DocumentBuilderFactory
													.newInstance().newDocumentBuilder();
				Document document = documentBuilder.parse(currentPath.toString());			
				Node root = document.getDocumentElement();			
				NodeList items = root.getChildNodes();
				
	            for (int i = 0; i < items.getLength(); i++) {
	            	Node currentItem = items.item(i);
	            	
	            	if (currentItem.getNodeType() != Node.TEXT_NODE) {
	            		NamedNodeMap attributes = items.item(i).getAttributes();
	            		FileGoods newFG = new FileGoods();
	            		
	            		newFG.code = Integer.parseInt(attributes.getNamedItem("plunumber").getNodeValue());
	            		newFG.price = Float.parseFloat(attributes.getNamedItem("price").getNodeValue())/100;
	            		newFG.barcode = attributes.getNamedItem("groupcode").getNodeValue();
	            		newFG.barcode += attributes.getNamedItem("itemcode").getNodeValue();
	            		newFG.name = attributes.getNamedItem("namefirst").getNodeValue();
	            		newFG.name += attributes.getNamedItem("namesecond").getNodeValue();
	            		
	            		listFileGoods.add(newFG);
	            	}
	            }
	            
	            Files.deleteIfExists(currentPath);
	            
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
	}

	private void writeListFromFileByGroups2DB(ObservableList<FileGroups> listFileGroups) {
		H2Provider.writeGroups(listFileGroups);		
	}

	private void writeListFromFileByGoods2DB(ObservableList<FileGoods> listFileGoods) {
		H2Provider.writeGoods(listFileGoods);
	}


	private ArrayList<Path> findFilePathsWhithEnd(String dir, String currentImgFileName) throws IOException {
		
		Path path = Paths.get(dir);
		ArrayList<Path> ap = new ArrayList<>();
		
		 Files.find(path, 1, (target,attrs) -> attrs.isRegularFile()
						&& target.toString().endsWith(currentImgFileName))
				.forEach(currentPath -> ap.add(currentPath));
		 
		 return ap;
	}

	
	class MyTimerTask extends TimerTask {

		@Override
		public void run() {

			Path readyUpdFilePath = Paths.get(ftpDir + "readyUpd.flg");
			
			if(Files.exists(readyUpdFilePath)) {
				boolean wasUpdate = updateMe();
				
				try { Files.deleteIfExists(readyUpdFilePath); } 
					catch (IOException e) { e.printStackTrace(); }
				
				if (wasUpdate) System.exit(0);
			}
			
			Path readyFilePath = Paths.get(ftpDir + "ready.flg");
			
			if(Files.exists(readyFilePath)) {
				loadGoods();
				try { Files.deleteIfExists(readyFilePath); } 
					catch (IOException e) { e.printStackTrace(); }
			}
		}

	}
	
	class FileGoods {
		 
		public int code;
		public String name;
		public String barcode;
		public float price;

	}
	
	class FileGroups {
		
		public int code;
		public String name;
		public String img;
		public String button_name;
		public String button_img;
		public int goods;
		
	}

}
