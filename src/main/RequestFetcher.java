package main;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.logging.*;
import main.packets.*;

public class RequestFetcher {

	private byte[] request;
	private String address;
	private int port;
	
	Logger logger = Logger.getLogger("main.RequestFetcher");
	
	public void setData(byte[] request){
		this.request=request;
	}
	public void setAddress(String address){
		this.address=address;
	}
	public void setPort(int port) {
		this.port=port;
	}
	
	public DatagramPacket getResponse() throws Exception{
		DefconVocabulary voc = new DefconVocabulary(this.request);
		DefconPacket response = null;
		DatagramPacket responsepacket = null;
		
		try{
			if(voc.header.equals("m")){
				if(voc.isset("DataType")){
					if(voc.get("DataType").equals("MOTD")){
						logger.info("Got MOTD Request");
						response = new ResponseMOTD(this.address,this.port);
						
					}else if(voc.get("DataType").equals("UpdateURL")){
						logger.info("Got UpdateURL Request");
						response = new ResponseUpdateURL(this.address,this.port);
						
					}else if(voc.get("DataType").equals("LatestVersion")){
						logger.info("Got Latest Version Request");
						response = new ResponseLatestVersion(this.address,this.port);
						
					}else if(voc.get("DataType").equals("DemoLimits")){
						logger.info("Got DemoLimits Request");
						response = new ResponseDemoLimits(this.address,this.port);
						
					}else if(voc.get("DataType").equals("ServerTTL")){
						logger.info("Got ServerTTL Request");
						response=new ResponseServerTTL(this.address,this.port);//not yet works
						
					}else if(voc.get("DataType").equals("DemoPrices")){
						logger.info("Got DemoPrice Request");
						
					}else if(voc.get("DataType").equals("BuyURL")){
						logger.info("Got BuyURL Request");
						
					}else{
						logger.warning("Unknown packet:");
						voc.Print();
						
					}
				}else if(voc.isset("c") && voc.get("c").equals("md")){
					logger.info("Got Auth Request");
					response = new ResponseAuth((String)voc.get("dq"));
					
				}else if(voc.isset("dg")){
					logger.info("Got NewServer Request");
					DefconServer going2add = new DefconServer(voc,this.address);
						//new DefconServer(this.request,this.address);
					DefconAuthEmu.servers.add(going2add);
					response=null; //we don't need to answer here
					
				}else if(voc.isset("c") && voc.get("c").equals("mb")){
					logger.info("Got GetServers Request");
					response=DefconAuthEmu.servers.ComposeGetLobbyPacket();
					
				}else if(voc.isset("c") && voc.get("c").equals("mh")){
					logger.info("Got GetExactServer Request");
					
				}else{
					logger.warning("Unknown packet:");
					voc.Print();
				}
				
			}else if(voc.header.equals("match")){
				if(voc.get("c").equals("ac")){
					logger.info("Got HolePunch Server Request");
					/*response = new ResponseHolePunch(voc);
					this.address = (String) voc.get("ah");
					this.port = (Integer) voc.get("ai");*/
					response=null;
				}else{
					logger.info("Got Match Request");
					response=new ResponseMatch(this.address,this.port,(Integer)voc.get("ae"));
					
				}
			}else{
				logger.warning("Unknown packet:");
				voc.Print();
			}
			
			if(response==null){
				return null;
			}
			
	        byte[] responseData = response.getPacket();
	        responsepacket = new DatagramPacket(responseData ,responseData.length);
	        responsepacket.setAddress(InetAddress.getByName(this.address));
	        responsepacket.setPort(this.port);
		
		}catch(Exception e){
			/*String fname = String.valueOf(new Date().getTime());
			java.io.FileWriter wr = new java.io.FileWriter(fname);
			wr.write(txtRequest);
			wr.close();*/
			logger.severe("Exception "+e.getClass().getName());
			e.printStackTrace();
			logger.severe("trying to handle the packet: "+e.getMessage()
					//+". Packet wrote to: "+fname);
					);
		}
		return responsepacket;
	}
	
	
}
