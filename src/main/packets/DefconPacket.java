package main.packets;

import java.util.Date;

import main.DefconVocabulary;

public abstract class DefconPacket extends DefconVocabulary{
	public abstract byte[] getPacket() throws Exception;
	
	public static int HashToken(){
		return new Long(new Date().getTime()/1000).intValue();
	}
	
	protected String address;
	protected Integer port;
	public DefconPacket(String address, Integer port){
		this.address=address;
		this.port = port;
	}
	
	public DefconPacket(){}
}
