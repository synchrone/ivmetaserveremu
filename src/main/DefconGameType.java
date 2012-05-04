package main;
public enum DefconGameType{
	Default((byte)0x00),
	OfficeMode((byte)0x01),
	SpeedDefcon((byte)0x02),
	Diplomacy((byte)0x03),
	BigWorld((byte)0x04),
	Tournament((byte)0x05);
	
	private byte value;
	DefconGameType(byte value){
		this.value=value;
	}
	public byte getValue() {
		return value;
	}
	public static DefconGameType Create(byte val){
		switch(val){
			default:
			case 0x00:
				return DefconGameType.Default;
			case 0x01:
				return DefconGameType.OfficeMode;
			case 0x02:
				return DefconGameType.SpeedDefcon;
			case 0x03:
				return DefconGameType.Diplomacy;
			case 0x04:
				return DefconGameType.BigWorld;
			case 0x05:
				return DefconGameType.Tournament;
		}
	}
}
