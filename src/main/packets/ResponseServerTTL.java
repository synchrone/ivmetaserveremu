package main.packets;

import java.io.IOException;

public final class ResponseServerTTL extends DefconPacket {
	@Override
	public byte[] getPacket() throws IOException {
		
		this.header="m";
		this.flag=0x07;

		this.set("c","mf");
		this.set("DataType","ServerTTL");
		this.set("dc",this.address);
		this.set("dd",this.port);
		this.set("du",DefconPacket.HashToken());
		this.set("ServerTTL","60");
		this.set("DataTTL",60);
		
		return this.Encode();
	}
	public ResponseServerTTL(String address, Integer port){
		super(address,port);
	}
}
