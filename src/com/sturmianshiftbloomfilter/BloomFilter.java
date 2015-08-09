package com.sturmianshiftbloomfilter;

import java.util.Arrays;


public class BloomFilter {

	private byte[] bucket;
	private int bucketLengthBits;
	private int counter;
	private boolean foundDifferentOne;
	private int unique;
	
	BloomFilter(final int bucketLength){
		this.bucket = new byte[bucketLength];
		//bucket length in bits
		this.bucketLengthBits = bucketLength * 8;
		this.counter = 0;
		this.unique = 0;
	}
	
	public final byte[] getByteArray(){
		return bucket;
	}
	
	public final int getCounter(){
		return counter;
	}
	
	public final int getUnique(){
		return unique;
	}
	
	public final int getLength(){
		return bucketLengthBits;
	}
	
	public final byte[] getBucket(){
		return this.bucket;
	}
	
	//make sure when you check if the hash is in the bucket that its not being read or written to.
	//same with writing to the bucket, nobody else should do it.
	
	public final synchronized void pushToBucket(final int[] hashes){
		
		
		int hash;
		for(int i=0;i < hashes.length; i++){
			
			hash = Math.abs(hashes[i] % bucketLengthBits);
			
			//if that hash bit is not found in the bucket then there is a new result
			if((bucket[hash >> 3] & (1 << hash % 8)) == 0){
				foundDifferentOne = true; 
				//add the result to the bucket
				bucket[hash >> 3] |= (1 << (hash % 8));
			}
		}
		
		if(foundDifferentOne){
			this.unique++;
			foundDifferentOne = false;
		}
		
		this.counter++;
	}
	
	public final boolean test(int hash){

		//we can only check positive hashes, that are not bigger than size of the bucket.
		hash = Math.abs(hash % bucketLengthBits);
		return (bucket[hash >> 3] & (1 << hash % 8)) == 1;
	}
	
}
