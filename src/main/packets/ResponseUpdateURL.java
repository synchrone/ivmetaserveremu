package main.packets;

import java.io.IOException;

import main.DefconAuthEmu;

public final class ResponseUpdateURL extends DefconPacket {


	@Override
	public byte[] getPacket() throws IOException {
		this.header="m";
		this.flag=0x07;
		
		this.set("c","mf");
		this.set("DataType", "UpdateURL");
		this.set("dc",this.address);
		this.set("dd",this.port);
		this.set("du",DefconPacket.HashToken());
		this.set("UpdateURL",(String)DefconAuthEmu.config.get("UpdateURL"));
		this.set("DataTTL",-1);
		
		return this.Encode();
	}
	
	public ResponseUpdateURL(String address,Integer port){
		super(address,port);
	}

}
