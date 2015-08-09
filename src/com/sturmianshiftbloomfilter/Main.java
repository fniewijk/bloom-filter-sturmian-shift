package com.sturmianshiftbloomfilter;

	import java.util.concurrent.LinkedBlockingQueue;
	import java.util.concurrent.ArrayBlockingQueue;
	import java.util.concurrent.ThreadPoolExecutor;
	import java.util.concurrent.TimeUnit;
	
	import com.sturmianshiftbloomfilter.data.FileReader;
	import com.sturmianshiftbloomfilter.log.Logger;

	public class Main {

		private static String path = new String("data/normalsturmian.txt");
		
		public static final int WORD_LENGTH = 256;
		public static final int BUCKET_BYTE_SIZE = 64*1024*1024;
		public static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
		//update time
		public static final int UPDATE_TIME=1000;
		public static final int MAX_QUEUE_SIZE=500000;
		
		private int bufferOffset = 0;
		private int numberOfBytesRead = 0;
		
		private final BloomFilter bucket = new BloomFilter(Main.BUCKET_BYTE_SIZE);
		
		private final LinkedBlockingQueue<Runnable> blockingQueue = new LinkedBlockingQueue<Runnable>(Main.MAX_QUEUE_SIZE);
		
		private final Logger logger = new Logger(path,bucket,blockingQueue);
		
		public static void main(final String[] args) {
		   
			final Main main = new Main();
			
			main.analyze(path, Main.WORD_LENGTH);
		}
		
		private final void analyze(final String inputFile, final int wordLength){
			
			this.logger.init();
			int time = Main.UPDATE_TIME;
			
			byte[] word = new byte[wordLength];
			final byte[] buffer = new byte[wordLength];
			
			//initialize the thread pool
			final ThreadPoolExecutor pool = new ThreadPoolExecutor(Main.MAX_THREADS,Main.MAX_THREADS*2,0,TimeUnit.MILLISECONDS, blockingQueue );
			pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
			
			//open the file and read the first word in the buffer.
			FileReader fileReader = new FileReader();
			fileReader.open(inputFile);
			fileReader.read(buffer);
			
			//loop until end of file.
			while(numberOfBytesRead != -1){
			    
				//reset the offset
				bufferOffset = 0;
				//clone the buffer, cause we dont want to change the original buffer, cause it might be used still in a thread.
				word = buffer.clone();
				numberOfBytesRead = fileReader.read(buffer);
				
				//make new words specific for this set of data.
				byte[] loopWord = null;
				byte[] loopBuffer = null;
				
				loopWord = word.clone();
				loopBuffer = buffer.clone();

				for(int i=0; i < numberOfBytesRead ;i++){
					pool.execute(new HashRunnable(bucket,loopWord,loopBuffer,bufferOffset));
				    
				    bufferOffset++;
			
				    //ugly way of keeping the pool filled correctly.
				    if(blockingQueue.size() > Main.MAX_QUEUE_SIZE-1){
				    	
						this.waitForAWhile(time);
						logger.loopUpdate();
						
						if(blockingQueue.size() <= Main.MAX_QUEUE_SIZE/1.5f && time > 200){
							time -= 200;
							if(time < 200){
								time = 200;
							}
						}else {
							time += 200;
						}
					}
				}
			}
			
			//wait until all the threads have been processed.
			while(blockingQueue.size() > 0){
				this.waitForAWhile(100);
			}
			
			pool.shutdown();
			fileReader.close();
			logger.loopEnd();
		}
		
		private synchronized void waitForAWhile(final int mSec){
			try {
				this.wait(mSec);
			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}