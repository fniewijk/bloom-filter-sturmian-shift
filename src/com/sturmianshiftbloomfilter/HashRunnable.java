package com.sturmianshiftbloomfilter;

import java.util.Arrays;

import com.sturmianshiftbloomfilter.hash.murmurhash.*;
import com.sturmianshiftbloomfilter.hash.g414.*;

public class HashRunnable implements Runnable{

	private byte[] word;
	private final BloomFilter bucket;
	private byte[] buffer;
	private final int bufferOffset;
	private final int[] seed = {124235,46788,86456,-7,-2346346,66666};

	HashRunnable(final BloomFilter bucketObject,final byte[] word,final byte[]buffer,final int bufferOffset) {
	    this.word = word;
	    this.bucket = bucketObject;
	    
	    this.buffer = buffer;
	    this.bufferOffset = bufferOffset;
	    
	  }
	
	@Override
	public final void run(){
		
		//make a new array so we dont mess with the original which the other threads use. shift the contents to the beginning dependent
		//on the bufferoffset
		
		word = Arrays.copyOfRange(word,bufferOffset,bufferOffset+word.length);
		
		//byte[] word = new byte[this.word.length];
		//System.arraycopy(this.word,bufferOffset,word,0,this.word.length-bufferOffset);
		
		//copy from the buffer to the end of the array.
		System.arraycopy(buffer,0,word,buffer.length-bufferOffset,bufferOffset);

		//run a number of hashes for the bloom filter.
		//final int[] results = new int[seed.length];

		
		//final int[] results = MurmurHash.hash64_2(word,word.length);
		/*
		for(int i=0;i<seed.length;i++){
			
			//results[i] = Math.abs(MurmurHash.hash32(word, word.length,seed[i]));	
			//results[i] = Math.abs(MurmurHash3.murmurhash3x8632(word, 0, word.length,seed[i]));	
			
			//actually also murmurhash3
			//results[i] = Math.abs(MurmurHash4.MurmurHash3_x64_32_4(word,seed[i]));
			
			
			//results[i] = Math.abs(CWowHash.computeCWowIntHash(word,seed[i]));
			
			//faulty
			//results[i] = Math.abs(HsiehSuperFastHash.computeHsiehIntHash(word,seed[i]));
			
		}
		*/
		
		final int[] results = MurmurHash4.MurmurHash3_x64_32_4(word,1234500);
		int[] total = new int[results.length*2];
		System.arraycopy(results,0,total,0,results.length);
		
		//we need to push all of the results the same time to the bucket to prevent race issues.
		bucket.pushToBucket(results);
		
		//help the garbage collector.
		this.word = null;
		this.buffer = null;
	}
	
	
	
	
	
}
