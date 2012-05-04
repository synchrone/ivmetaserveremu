package main.packets;

import java.io.IOException;

public final class ResponseDemoLimits extends DefconPacket{
	@Override
	public byte[] getPacket() throws IOException {
		this.header="m";
		this.flag=0x09;
		
		this.set("DataTTL",-1);
		this.set("AllowDemoServers",true);
		this.set("MaxDemoPlayers",6);
		this.set("MaxDemoGameSize",6);
		this.set("du",DefconPacket.HashToken());
		this.set("dd",this.port);
		this.set("dc",this.address);
		this.set("DataType","DemoLimits");
		this.set("c","mf");		
		
		return this.Encode();
	}
	public ResponseDemoLimits(String address,Integer port){
		super(address,port);
	}

}
