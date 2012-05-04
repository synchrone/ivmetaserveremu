package main;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class DefconVocabulary {
	private LinkedHashMap<String, Object> data = new LinkedHashMap<String, Object>();
	public String header;
	public byte flag;
	
	private int workingOffset=0;
	private byte[] packet;
	
	@SuppressWarnings("unused")
	private void savePacket(byte[] data,String name) throws IOException{
		File fl = new File(name);
		fl.delete();
		java.io.FileWriter wr = new java.io.FileWriter(name);
		char[] toWrite = new char[data.length];
		Charset.forName("CP1251").newDecoder().decode(ByteBuffer.wrap(data), CharBuffer.wrap(toWrite), true);
		wr.write(toWrite);
		wr.close();
	}
	
	public DefconVocabulary(){
		
	}
	
	public DefconVocabulary(byte[] data) throws Exception{
		this.packet = data;
		//this.savePacket(data, "lastpacket.hex");
		if(data[0]!=0x3C){throw new Exception("Not starts with '<', but should!");}
		this.workingOffset=1;
		
		this.header = GetString();
		this.flag = GetNextByte(); //some flag, dunno what for yet
		
		while(this.HasMoreData()){
			String name = GetString();
			Object value = null;
			if(name.equals("<")){
				return; //nested vocabulary, without providing length. creepy =\
				//this.workingOffset--;
				//value=GetVocabulary();
			}
			
			byte dataType = GetNextByte();
			switch (dataType){
				case 0x01:
					value = GetInt32();
					break;
				case 0x03:
					value = GetNextByte();
					break;
				case 0x04:
					value = GetString();
					break;
				case 0x05:
					value = GetNextByte() == 0x01 ? true : false;
				case 0x06:
					value = GetVocabulary();
					break;
				default:
					System.out.println("Unknown datatype "+dataType+" at offset "+this.workingOffset);
					value=-1;
					break;
			}
			this.data.put(name, value);
		}
	}
	public byte[] Encode() throws IOException{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bao.write(0x3C); //<
		bao.write((int)this.header.length());
		bao.write(this.header.getBytes());
		bao.write(this.flag);
		
		for(Entry<String, Object> val : this.data.entrySet()){
			String key = val.getKey();
			bao.write(key.length());
			bao.write(key.getBytes());
			
			Object value = val.getValue();
			
			if(value.getClass().getName() == "java.lang.Integer"){
				bao.write(0x01);
				bao.write(DefconVocabulary.Int32ToByteArr((Integer)value));
				
			}else if(value.getClass().getName() == "java.lang.Byte"){
				bao.write(0x03);
				bao.write(((Byte)value).byteValue());
				
			}else if(value.getClass().getName() == "java.lang.String"){
				bao.write(0x04);
				byte[] str = ((String)value).getBytes();
				bao.write(str.length);
				bao.write(str);

			}else if(value.getClass().getName() == "java.lang.Boolean"){
				bao.write(0x05);
				bao.write(((Boolean)value).booleanValue() ? 0x01 : 0x00);
				
			}else if(value.getClass().getName() == "DefconVocabulary"){
				bao.write(0x06);
				byte[] rawData = ((DefconVocabulary)value).Encode();
				bao.write(DefconVocabulary.Int32ToByteArr(rawData.length));
				bao.write(rawData);
				
			}else{
				System.out.println("Unknown datatype: "+value.getClass().getName());
			}
		}
		bao.write(new byte[]{0x00,0x3E});
		return bao.toByteArray();
	}
	
	public Object get(String key){
		return this.data.get(key);
	}
	public void set(String key, Object value){
		this.data.put(key, value);
	}
	public boolean isset(String key){
		return this.data.containsKey(key);
	}
	
	private boolean HasMoreData(){
		return this.packet[this.workingOffset]!=0x00;
	}
	private byte GetNextByte(){
		return this.packet[this.workingOffset++];
	}
	private int GetInt32(){
		int intLen = 4;
		byte[] intAsByte = new byte[intLen];
		System.arraycopy(this.packet, this.workingOffset, intAsByte, 0, intLen);
		int intVal = DefconVocabulary.ByteArrToInt32(intAsByte);
		this.workingOffset+=intLen;
		return intVal;
	}
	/*
	private int GetInt8(){
		int intVal = (int)this.packet[this.workingOffset++];
		return intVal;
	}*/
	
	private String GetString() throws Exception{
		String myString = null;
		int length = this.packet[this.workingOffset++];
		if(length == 0){
			throw new Exception("No more data in packet");
		}
		byte[] myStringAsByte = new byte[length];
		System.arraycopy(this.packet, this.workingOffset, myStringAsByte, 0, length);
		myString = new String(myStringAsByte);
		this.workingOffset+=length;
		return myString;
	}
	private DefconVocabulary GetVocabulary() throws Exception{
		int length = this.GetInt32();
		byte[] nestedData = new byte[length];
		System.arraycopy(this.packet,this.workingOffset, nestedData, 0, length);
		this.workingOffset+=length;
		return new DefconVocabulary(nestedData);
	}
	
	public void Print(){
		Print(0);
	}
	public void Print(int nestLevel){
		for(int i=0;i<nestLevel;i++){
			System.out.print(" ");
		}
		System.out.println(this.header + ", flag="+((int)this.flag));
		for(Entry<String, Object> val : this.data.entrySet()){
			for(int i=0;i<nestLevel;i++){
				System.out.print(" ");
			}
			Object value = val.getValue();
			System.out.print(val.getKey()+" = ");
			
			if(value.getClass().getName() == "java.lang.String"){
				System.out.println("(string) "+(String)value);
				
			}else if(value.getClass().getName() == "java.lang.Integer"){
				System.out.println("(int) "+(Integer)value);
			}else if(value.getClass().getName() == "java.lang.Byte"){
				System.out.println("(byte) "+(Integer)value);
				
			}else if(value.getClass().getName() == "DefconVocabulary"){
				System.out.println("(vocabulary)");
				DefconVocabulary voc = (DefconVocabulary)value;
				voc.Print(nestLevel+1);
			}else{
				System.out.println("("+value.getClass().getName()+") "+value.toString());
			}
		}
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
}
