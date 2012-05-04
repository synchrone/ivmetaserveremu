package main;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public class DefconServer {
	String name; //dg
	String game="Defcon"; //dh
	String version="1.43"; //di
	String localIp; //de

	int localPort; //df
	//00 00 00
	int playersOnline; //dj
	byte dk; ///dk... unknown
	int maxPlayers; //dl
	int spectatorsOnline; //dm
	int maxSpectators; //dn
	DefconGameType gameType; //dw
	DefconScoreMode scoreMode; //dx
	int timePlaying; //dy
	boolean gameStarted; //dv
	int teamCount; //ea
	int globalPort; //db - 2byte
	String globalIp; //da but there's global IP
	String uniqueId;
	/*byte dd; //dd
	byte du;
	
	byte dO; //do
	byte dp;*/
	
	int readerOffset=8;
	byte[] packet;
	List<DefconPlayer> players;
	
	public DefconServer(DefconVocabulary data,String globalIp){
		this.globalIp=globalIp;
		this.name = (String) data.get("dg");
		this.game = (String) data.get("dh");
		this.version = (String) data.get("di");
		this.localIp = (String) data.get("de");
		this.localPort = (Integer)data.get("df");
		this.playersOnline = ((Byte)data.get("dj")).intValue();
		this.dk = (Byte)data.get("dk");
		this.maxPlayers = ((Byte)data.get("dl")).intValue();
		this.spectatorsOnline = ((Byte)data.get("dm")).intValue();
		this.maxSpectators = ((Byte)data.get("dn")).intValue();
		this.gameType = DefconGameType.Create((Byte)data.get("dw"));
		this.scoreMode = DefconScoreMode.Create((Byte)data.get("dx"));
		if(data.isset("dy")){
			this.timePlaying = (Integer)data.get("dy");
		}
		this.gameStarted = (((Byte)data.get("dv")).byteValue() == 0x01 ? true: false);
		this.teamCount = ((Byte)data.get("ea")).intValue();
		this.globalPort = (Integer)data.get("db");
		this.uniqueId = (String)data.get("dq");
		
	}
	
	public DefconServer(ResultSet sqlRow) throws SQLException{
		
		this.name=sqlRow.getString("name");
		this.localIp=sqlRow.getString("localIp");
		this.localPort=sqlRow.getInt("localPort");
		this.globalIp=sqlRow.getString("globalIp");
		this.globalPort=sqlRow.getInt("globalPort");
		this.teamCount=sqlRow.getInt("teamCount");
		this.playersOnline=sqlRow.getInt("playersOnline");
		this.maxPlayers=sqlRow.getInt("maxPlayers");
		this.spectatorsOnline=sqlRow.getInt("spectatorsOnline");
		this.maxSpectators=sqlRow.getInt("maxSpectators");
		this.gameType=DefconGameType.Create((byte)sqlRow.getInt("gameType"));
		this.scoreMode=DefconScoreMode.Create((byte)sqlRow.getInt("scoreMode"));
		this.timePlaying=sqlRow.getInt("timePlaying");
		this.gameStarted=(sqlRow.getInt("gameStarted")==1 ? true : false);
		this.playersOnline=sqlRow.getInt("playersOnline");
		this.uniqueId = sqlRow.getString("uniqueId");
	}
	
	public DefconServer(String name,
						String localIp,
						int localPort,
						String globalIp,
						int globalPort,
						
						int teamCount,
						int playersOnline,
						int maxPlayers,
						int spectatorsOnline,
						int maxSpectators,
						DefconGameType gameType,
						DefconScoreMode scoreMode,
						int timePlaying,
						boolean gameStarted,
						String uniqueId
	){
		this.name=name;
		this.localIp=localIp;
		this.localPort=localPort;
		this.globalIp=globalIp;
		this.globalPort=globalPort;
		this.teamCount=teamCount;
		this.playersOnline=playersOnline;
		this.maxPlayers=maxPlayers;
		this.spectatorsOnline=spectatorsOnline;
		this.maxSpectators=maxSpectators;
		this.gameType=gameType;
		this.scoreMode=scoreMode;
		this.timePlaying=timePlaying;
		this.gameStarted=gameStarted;
		this.playersOnline=playersOnline;
		this.uniqueId = uniqueId;
	}
	

	public byte[] GetResponse(){
		int offset=4;
		byte[] output = new byte[500];
		output[0]=0x3C;
		output[1]=0x01;
		output[2]=0x6D; 
		output[3]=0x17; 
		
		System.arraycopy(new byte[]{0x02, 0x64, 0x67, 0x04},0,output,offset,4);
		output[8]=(byte)name.length();
		System.arraycopy(name.getBytes(), 0, output, offset+5, name.length());
		offset+=5+name.length();
		
		System.arraycopy(new byte[]{0x02, 0x64, 0x68, 0x04},0,output,offset,4);
		output[offset+4]=(byte)game.length();
		System.arraycopy(game.getBytes(), 0, output, offset+5, game.length());
		offset+=5+game.length();
		
		System.arraycopy(new byte[]{0x02, 0x64, 0x69, 0x04},0,output,offset,4);
		output[offset+4]=(byte)version.length();
		System.arraycopy(version.getBytes(),0,output,offset+5,version.length());
		offset+=5+version.length();
		
		System.arraycopy(new byte[]{0x02, 0x64, 0x65, 0x04},0,output,offset,4);
		output[offset+4]=(byte)localIp.length();
		System.arraycopy(localIp.getBytes(),0,output,offset+5,localIp.length());
		offset+=5+localIp.length();
		
		
		System.arraycopy(new byte[]{0x02, 0x64, 0x66, 0x01},0,output,offset,4);
		System.arraycopy(main.DefconAuthEmu.Int32ToByteArr(localPort),0,output,offset+4,4);
		offset+=8;
			byte[] params = new byte[]{
				0x02, 0x64, 0x6A, 0x03, /*val */ 0x00, //teams
				0x02, 0x64, 0x6B, 0x03, /*val */ 0x00, //dk
				0x02, 0x64, 0x6C, 0x03,	/*val */ 0x06, //maxplayers
				0x02, 0x64, 0x6D, 0x03, /*val */ 0x00, //specs online
				0x02, 0x64, 0x6E, 0x03, /*val */ 0x06, //max specs
				0x02, 0x64, 0x77, 0x03, /*val */ 0x00, //gametype
				0x02, 0x64, 0x78, 0x03, /*val */ 0x00, //scoremode
				0x02, 0x64, 0x79, 0x01, /*val */ 0x00, 0x00, 0x00, 0x00, //time playing /dy
				0x02, 0x64, 0x76, 0x03, /*val */ 0x00, //game started //dv
				0x02, 0x65, 0x61, 0x03, /*val */ 0x02, //players online //ea
				0x02, 0x64, 0x62, 0x01, /*val */ (byte)0x92, 0x13, 0x00, 0x00, //globalPort //db
				0x01, 0x63, 0x04, 0x02, 0x6D, 0x63
			};
			params[4]=(byte) playersOnline;
			params[9]=dk;
			params[14]=(byte) maxPlayers;
			params[19]=(byte) spectatorsOnline;
			params[24]=(byte) maxSpectators;
			params[29]=gameType.getValue();
			params[34]=scoreMode.getValue();
			System.arraycopy(main.DefconAuthEmu.Int32ToByteArr(timePlaying),0,params,39,4);
			System.arraycopy(main.DefconAuthEmu.Int32ToByteArr(localPort),0,params,57,4);
			params[47]=(byte) (gameStarted ? 0x01 : 0x00);
			params[52]=(byte) teamCount;
	
		System.arraycopy(params,0,output,offset,params.length);
		offset+=params.length;
		
		System.arraycopy(new byte[]{0x02, 0x64, 0x63, 0x04},0,output,offset,4); //dc
		output[offset+4]=(byte)globalIp.length();
		System.arraycopy(globalIp.getBytes(),0,output,offset+5,globalIp.length());
		offset+=5+globalIp.length();
		
		byte[] dddu = new byte[] {
			0x02, 0x64, 0x64, 0x01, /*val */(byte)0x00, 0x00, 0x00, 0x00, //dd
			0x02, 0x64, 0x75, 0x01, /*val */(byte)0xF3, (byte)0xBC, (byte)0x85, 0x49 //du
		};
		System.arraycopy(dddu ,0,output,offset,dddu.length);
		offset+=dddu.length;
		
		System.arraycopy(new byte[]{0x02, 0x64, 0x61, 0x04},0,output,offset,4); //da
		output[offset+4]=(byte)globalIp.length();
		System.arraycopy(globalIp.getBytes(),0,output,offset+5,globalIp.length());
		offset+=5+globalIp.length();
		
		byte[] footer = new byte[]{
			0x02, 0x64, 0x6F, 0x01, /*val */0x00, 0x00, 0x00, 0x00, //dO
			0x02, 0x64, 0x70, 0x01, /*val */0x01, 0x00, 0x00, 0x00, //dp //if 0x00 -not-compatible
			0x00, 0x3E
		};
		System.arraycopy(footer ,0,output,offset,footer.length);
		offset+=footer.length;
		
		byte[] response = new byte[offset];
		System.arraycopy(output,0,response,0,offset);
		return response;
	}
}