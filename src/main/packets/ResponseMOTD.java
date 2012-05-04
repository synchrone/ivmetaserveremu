package main.packets;

import java.io.IOException;

import main.DefconAuthEmu;

public final class ResponseMOTD extends DefconPacket {
	
	@Override
	public byte[] getPacket() throws IOException {

		this.header="m";
		this.flag = 0x07;

		this.set("DataType","MOTD");
		this.set("dc",this.address);
		this.set("dd",this.port);
		this.set("du",DefconPacket.HashToken());
		this.set("MOTD",(String)DefconAuthEmu.config.get("MOTD"));
		this.set("DataTTL",-1);
		this.set("c","mf");
		
		return this.Encode();
	}
	
	public ResponseMOTD(String address, Integer port){
		super(address,port);
	}

}
