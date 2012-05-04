package main.packets;

import main.DefconVocabulary;

public class ResponseHolePunch  extends DefconPacket{
	public ResponseHolePunch(DefconVocabulary data){
		this.header="match";
		this.flag=0x0A;
		this.set("dh",data.get("dh")); //gamename
		this.set("di",data.get("di")); //version
		this.set("c",data.get("c"));   //=ac
		
		this.set("ah",data.get("ah")); //you gonna be connected from ip
		this.set("ai",data.get("ai")); //and port
		
		this.set("da",data.get("da")); //original 
		this.set("db",data.get("db"));
		
		this.set("dc",data.get("da"));
		
		this.set("dd",2806); //need to figure out, wtf
		this.set("du",DefconPacket.HashToken()); //rly
	}
	@Override
	public byte[] getPacket() throws Exception {
		return this.Encode();
	}

}
