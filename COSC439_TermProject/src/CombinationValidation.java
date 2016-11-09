import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CombinationValidation
{
	int combination;
	int stripeSize;
	int threads;
	public CombinationValidation(int stripeSize,int threads, int combination)
	{
		this.combination = combination;
		this.stripeSize = stripeSize;
		this.threads = threads;
	}
	
	LinkedList<BoolThread> list = new LinkedList<>();
	public int StartFind()
	{
		int current;
		int next = 0;
		boolean notFound = true;
		
		Semaphore signal = new Semaphore(threads);
		
		while(notFound)
		{
			current = next;
			next += stripeSize;
			
			try
			{
				// if takes time to acquire clean list
				signal.tryAcquire(1 ,TimeUnit.SECONDS);
				
				// clean list
				for (int i = 0; i < list.size(); i++)
				{
					if(!list.get(i).isAlive())
					{
						if(list.get(i).found)
						{
							System.out.println("FOUND IT KILLING THREADS");
							
							return list.get(i).foundInt; // found
						}
						list.remove(i);
						i--;
					}
				}
				
				// start acquire again
				signal.acquire();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int staticCurrent = current;
			int staticNext = next;
			
			BoolThread runThread = new BoolThread()
			{
				@Override
				public void run()
				{
//					System.out.println("Thread Start (" + staticCurrent + " - " + staticNext + ")");
					for (int i = staticCurrent; i < staticNext; i++)
					{
						if(i == combination)
						{
							foundInt = i;
							found = true;
						}
					}
//					System.out.println("Release (" + staticCurrent + " - " + staticNext + ")");
					signal.release(2);
				}
			};
			runThread.start();
//			System.out.println(signal.availablePermits());
			list.add(runThread);
		}
		return -1;
	}
	
	public abstract class BoolThread extends Thread
	{
		public boolean found = false;
		public int foundInt = 0;
		
	}
}
