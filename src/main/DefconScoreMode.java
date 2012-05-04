package main;

public enum DefconScoreMode {
		Default((byte)0x00),
		Survivor((byte)0x01),
		Genocide((byte)0x02);
		
		private byte value;
		DefconScoreMode(byte value){
			this.value=value;
		}
		public byte getValue() {
			return value;
		}
		public static DefconScoreMode Create(byte val){
			switch(val){
			default:
			case 0x00:
				return DefconScoreMode.Default;
			case 0x01:
				return DefconScoreMode.Survivor;
			case 0x02:
				return DefconScoreMode.Genocide;
			
			}
		}
}