package com.sturmianshiftbloomfilter.log;

import java.io.File;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

import com.sturmianshiftbloomfilter.BloomFilter;
import com.sturmianshiftbloomfilter.Main;

public class Logger {

	private BloomFilter bucket;
	private BlockingQueue<Runnable> blockingQueue;
	private long fileLength = 0;
	private int wordLength = Main.WORD_LENGTH;
	private int seedLength = 8;
	private int wordsProcessed = 0;
	private final long startTime = System.currentTimeMillis();
	private long loopTime = System.currentTimeMillis();
	
	public Logger(String filePath,BloomFilter bucket, BlockingQueue<Runnable> blockingQueue){
		fileLength = this.getFileLength(filePath);
		this.bucket = bucket;
		this.blockingQueue = blockingQueue;
	}
	
	public final void init(){
		System.out.println("Starting the program");
		System.out.println("Running in " + System.getProperty("sun.arch.data.model") + "bit mode");
	}
	
	public final void loopUpdate( ){
		final int counter = bucket.getCounter();
		final int queue = blockingQueue.size();
		
		final double progress = Double.valueOf(counter)/(fileLength-wordLength);
		
		
		System.out.println("-----------------------");
		System.out.println("counter at : " + counter + " loops of a sample of " + wordLength  + " byte(s) long");
		System.out.println("amount in queue : " + queue + " loops of a sample of " + wordLength  + " byte(s) long");
		System.out.println("processed : " + progress*100.0f + " percent");
		System.out.println(String.valueOf( Double.valueOf((counter-wordsProcessed))/((System.currentTimeMillis() - loopTime)/1000)) + " words per second." );
		System.out.println("different patterns indication: " + bucket.getUnique());
		System.out.println(String.valueOf(Thread.activeCount()-1) + " threads used. Plus one for the file loading." );
		System.out.println(Double.valueOf(bucket.getUnique() / (bucket.getLength()*1.0f) * 100) + " percent bucket filled");
    	System.out.println(" ");
		System.out.println("ran for: " +  String.valueOf((System.currentTimeMillis() - startTime)/1000.0f) + " seconds." );
		System.out.println("estimated time remaining: " +  String.valueOf((1/progress-1)*(System.currentTimeMillis() - startTime )/1000.0f) + " seconds." );
		System.out.println(String.valueOf( Double.valueOf((counter-wordsProcessed)*(wordLength/1073741824.0f)*this.seedLength)/((System.currentTimeMillis() - loopTime)/1000.0f)) + " GB per second." );
		
		this.loopTime = System.currentTimeMillis();
		wordsProcessed = counter;
		
		//System.out.println(Arrays.toString(bucket.getBucket() ));
		
	}
	
	public final void loopEnd(){
		long loopTime = System.currentTimeMillis();
		final int counter = bucket.getCounter();
		
		System.out.println("-----------------------");
		System.out.println("wordLength = " + wordLength);
		System.out.println("Finished");
		System.out.println(String.valueOf( Double.valueOf(counter*this.seedLength)/((System.currentTimeMillis() - startTime)/1000)/1000000) + " million ops per second." );
		System.out.println(Double.valueOf(bucket.getUnique() / (bucket.getLength()*1.0f) * 100) + " percent bucket filled");
		System.out.println("ran for: " +  String.valueOf((System.currentTimeMillis() - startTime)/1000.0f) + " seconds." );
		System.out.println(counter + " loops of a sample of " + wordLength  + " byte(s) long");
		System.out.println("different patterns: " + bucket.getUnique());
		//System.out.println(Double.valueOf((Math.pow((bucket.getUnique() / (bucket.getLength()*1.0f) / 2),seedLength)) * counter) + " chance of a collision with a completely random distribution");
	}
	
	public final long getFileLength(final String path){
		final File f = new File(path);
		return f.length();
	}
}
