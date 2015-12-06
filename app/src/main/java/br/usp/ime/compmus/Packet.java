package br.usp.ime.compmus;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Packet {

	private Random random = new Random();
	private int extraObjects = 0;
	
	private ArrayList<Object> objectsList = new ArrayList<Object>();
	
	/**
	 * Create a packet to be sent during the tests.
	 * @param extraObjects
	 */
	public Packet(int extraObjects) {
		this.extraObjects = extraObjects;
		
		generateObjectsList();
	}

    /**
     * Create a packet from arguments
     * @param values in any type
     */
	public Packet(Object... values) {

		objectsList.clear();
		for(Object value: values) {

			objectsList.add(value);
		}
	}

	/**
	 * Generate a list of floats
	 */
	private void generateObjectsList() {
		
		if (extraObjects <= 0) {
			
			objectsList.clear();
		} else if (extraObjects > objectsList.size()) {
			
			for (int i = objectsList.size(); i < extraObjects; i++) {
				objectsList.add(random.nextFloat());
			}
		} else {			
			for (int i = objectsList.size()-1; i >= extraObjects; i--) {
				
				objectsList.remove(i);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Object> getListContents() {
		
		List<Object> packet = new ArrayList<Object>();


		try {
			//packet.addAll((List<Object>) floatsList.clone());
			for (Object obj : objectsList) {
				packet.add(obj);
			}
		} catch (ClassCastException e) {
			packet = new ArrayList<Object>();
		}

//		packet.add(MobileDevice.getId());
		return packet;
	}
	
	public String getJsonContents() {
		
		StringBuilder packet = new StringBuilder();
		String objectsString = objectsList.toString();
		
		packet.append("{");
		packet.append(MobileDevice.getId());
		
		if (extraObjects > 0) {
			packet.append(",");
			packet.append(objectsString.substring(1, objectsString.length()-1));
		}
		
		packet.append("}");
		
		return packet.toString();
	}
	
}
