package main;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import main.packets.DefconPacket;
import main.packets.PlainPacket;


@SuppressWarnings("serial")
public class ServerList extends Hashtable<String, DefconServer> {
	Logger logger = Logger.getLogger("main.ServerList");
	public DefconPacket ComposeGetLobbyPacket() throws SQLException, IOException{
		
		Collection<DefconServer> servers = this.getServers();
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		
		bao.write(new byte[]{0x3C, 0x01, 0x6D,
				(byte)(servers.size()+1),
				0x01, 0x63, 0x04, 0x02, 0x6D, 0x67
		});
		int i=0;
		
		for(DefconServer server : servers){
			i++;
			byte[] serverbody=server.GetResponse();
			byte[] curAnswer=new byte[7+serverbody.length];
			//header
			curAnswer[0]=(byte)0x01;
			curAnswer[1]=(byte)(48+i); //servernum
			curAnswer[2]=0x06;
			System.arraycopy(DefconAuthEmu.Int32ToByteArr(serverbody.length), 0, curAnswer, 3, 4); //packet size
			
			//the server itself
			System.arraycopy(serverbody,0,curAnswer,7,serverbody.length);
			
			bao.write(curAnswer);
		}		
		bao.write(new byte[]{0x00, 0x3E});
		
		return i==0 ? null : new PlainPacket(bao.toByteArray());
	}
	
	public Collection<DefconServer> getServers() throws SQLException{
		if(!DefconAuthEmu.config.get("StoreServersInDB").equals("1")){return this.values();}
		Statement selector = DefconAuthEmu.getDB().createStatement();
		ResultSet serversResult = selector.executeQuery("SELECT * FROM `servers`");
		
		Collection<DefconServer> servers = new Vector<DefconServer>();
		while(serversResult.next()){
			servers.add(new DefconServer(serversResult));
		}
		
		selector.close();
		return servers;
		
	}
	public void add(DefconServer serv) throws SQLException {
		if(!DefconAuthEmu.config.get("StoreServersInDB").equals("1")){this.put(serv.uniqueId, serv); return;}
		
		Statement exStmt = DefconAuthEmu.getDB().createStatement();
		ResultSet exists = exStmt.executeQuery("SELECT `localPort` FROM `servers` WHERE `uniqueId` = '"+serv.uniqueId+"'");
		if(exists.next()){
			Statement insert = DefconAuthEmu.getDB().createStatement();
			logger.info("Such server exists - updating info");
			insert.execute("UPDATE `servers` SET "+
			
					"`name` = '"+serv.name.replace("'", "").replace("\"","") +"' , "+
					"`game` = '"+serv.game +"' , "+
					"`version` = '"+serv.version +"' , "+
					"`localip` = '"+serv.localIp +"' , "+
					"`localport` = '"+serv.localPort +"' , "+
					"`playersOnline` = '"+serv.playersOnline +"' , "+
					"`dk` = '"+new Integer(serv.dk).toString() +"' , "+
					"`maxPlayers` = '"+serv.maxPlayers +"' , "+
					"`spectatorsOnline` = '"+serv.spectatorsOnline +"' , "+
					"`maxSpectators` = '"+serv.maxSpectators +"' , "+
					"`gameType` = '"+new Integer(serv.gameType.getValue()).toString() +"' , "+
					"`scoreMode` = '"+new Integer(serv.scoreMode.getValue()).toString() +"' , "+
					"`timePlaying` = '"+serv.timePlaying +"' , "+
					"`gameStarted` = '"+(serv.gameStarted ? '1': '0') +"' , "+
					"`teamCount` = '"+serv.teamCount +"' , "+
					"`globalip` = '"+serv.globalIp +"' , "+
					"`globalPort` = '"+serv.globalPort +"'  "+
					
			"WHERE `uniqueId`='"+serv.uniqueId+"'");
		}else{
			Statement create = DefconAuthEmu.getDB().createStatement();
			logger.info("A really new server - inserting to db");
			create.execute("INSERT INTO `servers` ("+
					"`name` ,"+
					"`game` ,"+
					"`version` ,"+
					"`localip` ,"+
					"`localport` ,"+
					"`playersOnline` ,"+
					"`dk` ,"+
					"`maxPlayers` ,"+
					"`spectatorsOnline` ,"+
					"`maxSpectators` ,"+
					"`gameType` ,"+
					"`scoreMode` ,"+
					"`timePlaying` ,"+
					"`gameStarted` ,"+
					"`teamCount` ,"+
					"`globalip` , "+
					"`globalPort` ,"+
					"`uniqueId` )VALUES ('"+ 
					serv.name.replace("'", "").replace("\"","") +"',  '"+ 
					serv.game +"',  '"+ 
					serv.version +"',  '"+ 
					serv.localIp +"',  '"+ 
					serv.localPort +"',  '"+ 
					serv.playersOnline +"', '"+ 
					new Integer(serv.dk).toString() +"',  '"+ 
					serv.maxPlayers +"',  '"+ 
					serv.spectatorsOnline +"',  '"+ 
					serv.maxSpectators +"',  '"+ 
					new Integer(serv.gameType.getValue()).toString()+"',  '"+ 
					new Integer(serv.scoreMode.getValue()).toString() +"', '"+ 
					serv.timePlaying +"',  '"+ 
					(serv.gameStarted ? '1': '0') +"',  '"+ 
					serv.teamCount +"',  '"+ 
					serv.globalIp +"',  '"+ 
					serv.globalPort +"',  '"+ 
					serv.uniqueId +"')"
				);
			create.close();
		}
		exStmt.close();
		
	}
}
