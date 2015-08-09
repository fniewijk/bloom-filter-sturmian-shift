package com.sturmianshiftbloomfilter.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileReader {

	private BufferedInputStream in;
	private boolean open = false;
	private boolean hasData = true;
	
	public final void open(final String inputFile){
		try{
			  // Open the file that is the first 
			  // command line parameter
			  final FileInputStream fstream = new FileInputStream(inputFile);
			  // Get the object of DataInputStream
			  this.in = new BufferedInputStream(fstream);
			  
			  }catch (final Exception e){//Catch exception if any
				  System.err.println("Error: " + e.getMessage());
		}
		
		this.open = true;
	}
	
	public final int read(final byte[]array){
		//do something
		if(open){
			try {
				return in.read(array);
			} catch (final IOException e) {
				this.hasData = false;
				e.printStackTrace();
				System.out.println("Size of sample is bigger than set?");
			}
		}
		this.hasData = false;
		return -1;	
	}

	public final boolean hasData(){
		return (this.open && this.hasData);
	}
	
	public final void close(){
		 //Close the input stream
		  try {
			in.close();
			this.open = false;
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}
	
}
