package test;

public class MyDto {
	String stringValue;
	int intValue;
	boolean booleanValue;
	public MyDto() {
		stringValue = "";
		intValue = 0;
		booleanValue = false;
	}
	public MyDto(String x, int y, boolean z) {
		stringValue = x;
		intValue = y;
		booleanValue = z;
	}
	public String getStringValue() {
		return stringValue;
	}
	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}
	public int getIntValue() {
		return intValue;
	}
	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}
	public boolean isBooleanValue() {
		return booleanValue;
	}
	public void setBooleanValue(boolean booleanValue) {
		this.booleanValue = booleanValue;
	}
	
}
