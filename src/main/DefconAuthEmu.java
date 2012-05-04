package main;

import java.net.*;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DefconAuthEmu
{
	public static Properties config;
	public static ServerList servers = new ServerList();
	private static Logger logger = Logger.getLogger("main.DefconAuthEmu");
    private static Connection dbconn;
    
    public static Connection getDB(){
    	if(dbconn == null){
    		try{
    			Class.forName("com.mysql.jdbc.Driver").newInstance();
    			String url = (String) config.get("DatabaseConnection");
    			String user = (String) config.get("DatabaseUser");
    			String pass = (String) config.get("DatabasePass");
    			dbconn = DriverManager.getConnection(url,user,pass);
    			if(dbconn==null){
    				throw new Exception();
    			}
    			
    		}catch(Exception e){
    			logger.severe("Couldn't connect to DB "+e.getMessage());
    			System.exit(0);
    		}
    	}
    	return dbconn;
    }
    public static byte[] Int32ToByteArr(int v){
		return new byte[]{
			(byte) ((v >>> 0) & 0xFF),
			(byte) ((v >>> 8) & 0xFF),
			(byte) ((v >>> 16) & 0xFF),
			(byte) ((v >>> 24) & 0xFF)
		};
	}
    public static int ByteArrToInt32(byte[] intByte) {
		int fromByte = 0;
		for (int i = 0; i < 4; i++)
		{
			int n = (intByte[i] < 0 ? (int)intByte[i] + 256 : (int)intByte[i]) << (8 * i);
			fromByte += n;
		}
		return fromByte;
	}
	public static void main(String[] args)
	{	
		config=new Properties();
		try {
			config.load(new FileReader("config"));
		} catch (FileNotFoundException e) {
			logger.severe("Fatal error: Couldn't find config file.");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		logger.info("Defcon Metaserver Emulator started.");
		DefconAuthEmu emu = new DefconAuthEmu(5008);
		emu.serve();
	}
	
    byte[] buf = new byte[1024];
    private DatagramSocket server;
    
    private Timer cleanUpTimer = new Timer();
    
    public DefconAuthEmu(int portnum){
		logger.setLevel(Level.ALL);
		this.cleanUpTimer.schedule(new TimerTask(){
			public void run() {
				if(!DefconAuthEmu.config.get("PurgeDatabase").equals("1")){return;}
				try {
					Statement cleanStmt = DefconAuthEmu.getDB().createStatement();
					cleanStmt.execute("DELETE FROM `servers` WHERE `lastUpdate` <  NOW() - INTERVAL 1 MINUTE");
					int affected = cleanStmt.getUpdateCount();
					if(affected>0){
						logger.info("Database Cleanup succeeded. "+affected+" servers deleted");
					}
				} catch (SQLException e) {
					logger.severe("Database Cleanup Error :"+e.getMessage());
				}
			}
		}, 0,60000);
		
		try {
			servers.add(new DefconServer(
					"Myserver",
					"127.0.0.1",5010,
					"127.0.0.1",5010,
					1,1,6,2,4,
					DefconGameType.Default,DefconScoreMode.Default,
					0,false,"AAAAAA-BBBBBB-CCCCCC-DDDDDD-EEE")
			);
		} catch (SQLException e) {
		}
        try
		{
			server = new DatagramSocket(portnum);
		}
		catch (Exception err){
			logger.severe(err.getMessage());
			System.exit(0);
		}
	}

	public void serve(){
		
		try
		{
			RequestFetcher fetcher = new RequestFetcher();
			
			logger.info("Awaiting incoming connections. Press Ctrl+C to exit.");
			while (true)
			{
                DatagramPacket request = new DatagramPacket(buf, buf.length);
                server.receive(request);
                buf=new byte[1024];
                
                String address=request.getAddress().toString().substring(1); //there's "/" sign suddenly
                
                fetcher.setData(request.getData());
                fetcher.setAddress(address);
                fetcher.setPort(request.getPort());
                DatagramPacket response = fetcher.getResponse();
                
	            if(response!=null){
	                server.send(response);
                }
			}
		}
		catch (Exception err){
			logger.severe(err.getMessage());
			err.printStackTrace();
		}
	}


}