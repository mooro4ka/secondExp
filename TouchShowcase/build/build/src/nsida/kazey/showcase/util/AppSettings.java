package nsida.kazey.showcase.util;

import java.io.File;
import java.util.Hashtable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


public class AppSettings {
	
	private AppSettings() {
		fHashMap = new Hashtable<String, String>();
	}
	
	private Hashtable<String, String> fHashMap;
	
	private static AppSettings SINGLETON;
	
	static {
		SINGLETON = new AppSettings();
	}
	
	
	public static String get(String key) {
		  return SINGLETON.fHashMap.get(key);
	}
	
	public static String get(String key, String deflt) {
		String value = SINGLETON.fHashMap.get(key);
		if (value == null) {
			return deflt;
		} else {
			return value;
		}
	}

	public static void put(String key, String data) {
		if (data == null) {
		    throw new IllegalArgumentException();
		} else {
		    SINGLETON.fHashMap.put(key, data);
		}
	}
	
	public static void loadSettings(File file) {
		try {
			
			JAXBContext context = JAXBContext
	                .newInstance(AppSettingsWrapper.class);
	        Unmarshaller um = context.createUnmarshaller();

	        AppSettingsWrapper wrapper = (AppSettingsWrapper) um.unmarshal(file);
			
			SINGLETON.fHashMap.clear();
			SINGLETON.fHashMap = wrapper.getHashtable();
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not load data from file:\n" + file.getPath());
		}
	}
	
	public static void saveSettings(File file) {
		try {
	        
			JAXBContext context = JAXBContext
	                .newInstance(AppSettingsWrapper.class);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        AppSettingsWrapper wrapper = new AppSettingsWrapper();
	        wrapper.setHashtable(SINGLETON.fHashMap);;

	        m.marshal(wrapper, file);

	    } catch (Exception e) { 
	    	e.printStackTrace();
	    	System.out.println("Could not save data to file:\n" + file.getPath());
	    }
	}
	
	@XmlRootElement
	public static class AppSettingsWrapper {

		private Hashtable<String, String> hashtable;
		
		@XmlElement
	    public Hashtable<String, String> getHashtable() {
	      return hashtable;
	    }
	
	    public void setHashtable(Hashtable<String, String> hashtable) {
	      this.hashtable = hashtable;
	    }

	}
}
