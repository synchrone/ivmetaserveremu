package main.packets;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.logging.Logger;

import main.DefconAuthEmu;

public final class ResponseAuth extends DefconPacket {
	private String key;
	private int keyId;
	private int authState;
	
	private static Logger logger = Logger.getLogger("main.packets.DefconPacket");

	@Override
	public byte[] getPacket() throws IOException {
		this.header="m";
		this.flag=0x05;
		
		this.set("c","me");
		this.set("dr",keyId);
		this.set("dq",key);
		this.set("me",authState);
		this.set("ds",DefconPacket.HashToken());
		
		return this.Encode();
	}
	
	public ResponseAuth(String key) throws SQLException{
		this.key=key;
		int keyid=0;
		
		if((boolean)DefconAuthEmu.config.get("DBCheckKey").equals("1")){
			logger.info("Checking key with DB...");
			Statement stmt = DefconAuthEmu.getDB().createStatement();
			ResultSet rs = stmt.executeQuery("SELECT `keyid` FROM `keys` WHERE `key`='"+key+"';");
			if(rs.next()){
				keyid = rs.getInt("keyid");
				logger.info("Key #"+keyid+" valid");
				this.keyId=keyid;
				this.authState=1;
			}else if(key.startsWith("hsh")){
				logger.info("HashKey valid "+key);
				this.keyId=1;
				this.authState=1;
			}else{
				logger.info("Key not valid "+key);
				this.keyId = -1;
				this.authState=-1;
			}
			
			stmt.close();
		}else{
			byte[] keyBytes = key.getBytes();
			int calcKeyId=0;
			for(byte keyChar : keyBytes){
				calcKeyId+=(int)keyChar;
			}
			this.keyId=calcKeyId;
			this.authState=1;
			logger.info("Generated Key #"+keyid);
		}
	}

}
