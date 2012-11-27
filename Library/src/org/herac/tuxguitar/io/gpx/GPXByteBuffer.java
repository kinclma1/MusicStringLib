package org.herac.tuxguitar.io.gpx;

public class GPXByteBuffer {
	
	private static final int BUFFER_TYPE_BITS = 8;
	
	private int position;
	private byte[] buffer;
	
	public GPXByteBuffer( byte[] buffer ){
		this.buffer = buffer;
        position = 0;
	}
	
	public int length(){
		return (buffer.length );
	}
	
	public int offset(){
		return (position / BUFFER_TYPE_BITS );
	}
	
	public boolean end(){
		return (offset() >= length() );
	}
	
	public int readBit(){
		int bit = -1;
		int byteIndex = (position / BUFFER_TYPE_BITS );
		int byteOffset = ( (BUFFER_TYPE_BITS - 1) - (position % BUFFER_TYPE_BITS ) );
		if( byteIndex >= 0 && byteIndex < buffer.length ){
			bit = ( ((buffer[ byteIndex ] & 0xff) >> byteOffset ) & 0x01 );
            position++;
		}
		return bit;
	}
	
	public int readBits(int count){
		int bits = 0;
		for( int i = (count - 1) ; i >= 0 ; i -- ){
			bits |= ( readBit() << i  );
		}
		return bits;
	}
	
	public int readBitsReversed(int count){
		int bits = 0;
		for( int i = 0 ; i < count ; i ++ ){
			bits |= ( readBit() << i  );
		}
		return bits;
	}
	
	public byte[] readBytes(int count){
		byte[] bytes = new byte[count];
		for( int i = 0 ; i < count ; i++ ){
			bytes[i] = (byte)readBits(8);
		}
		return bytes;
	}
}
