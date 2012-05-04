package main.packets;
import java.io.IOException;

import main.DefconAuthEmu;

public final class ResponseMatch extends DefconPacket {
	public ResponseMatch(String address, int port, int ae) {
		super(address,port);
		this.ae=ae;
	}
	private int ae;
	@Override
	public byte[] getPacket() throws IOException {
		this.header="match";
		this.flag=0x09;
		
		this.set("ag",this.port);
		this.set("du",DefconPacket.HashToken());
		this.set("dd",this.port);
		this.set("dc",this.address);
		this.set("af",this.address);
		this.set("di",(String)DefconAuthEmu.config.get("LatestVersion"));
		this.set("dh","Defcon");
		this.set("ae",this.ae);
		this.set("c","ab");
		
		return this.Encode();
	}

}
