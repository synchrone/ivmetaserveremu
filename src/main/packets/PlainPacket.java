package main.packets;

public class PlainPacket extends DefconPacket {

	private byte[] packet;
	@Override
	public byte[] getPacket() {
		return packet;
	}
	
	public PlainPacket(byte[] data){
		this.packet = data;
	}

}
