package edu.towson.termproject;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.swing.DefaultListModel;

public class FactorialFinder
{
	
	public static BigInteger StartFind(DefaultListModel<String> mTextList,BigInteger stripeSize,int threads, BigInteger factorial)
	{
		BigInteger _total = new BigInteger("1");
		LinkedList<BoolThread> list = new LinkedList<>();
		
		BigInteger current = factorial;

		boolean check = true;
		Semaphore signal = new Semaphore(threads);
		while(check)
		{
			BigInteger next = current.subtract(stripeSize);
			if(next.compareTo(BigInteger.ONE) != 1)
			{
				next = BigInteger.ONE;
				check = false;
			}
			
			try
			{
				// if takes time to acquire clean list
				while(!signal.tryAcquire(2 ,TimeUnit.SECONDS))
				{
					int size = list.size();
					// clean list
					for (int i = 0; i < list.size(); i++)
					{
						if(!list.get(i).isAlive())
						{
							_total = _total.multiply(list.get(i).total);
							list.remove(i);
							i--;
						}
					}
					System.out.println("CLEANED LIST REMOVED: " + (size - list.size()));
				}
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			BigInteger staticCurrent = current;
			BigInteger staticNext = next;

			BoolThread runThread = new BoolThread()
			{
				@Override
				public void run()
				{
					mTextList.addElement("Thread Start (" + staticCurrent + " - " + staticNext + ")");
					System.out.println("Thread Start (" + staticCurrent + " - " + staticNext + ")");
					
					total = staticCurrent;
					BigInteger i = staticCurrent.subtract(BigInteger.ONE);
					while(i.compareTo(staticNext) == 1)
					{
						total = total.multiply(i);
						i = i.subtract(BigInteger.ONE);
//						System.out.println(total);
					}
					
					mTextList.addElement("Release (" + staticCurrent + " - " + staticNext + ")");
					System.out.println("Release (" + staticCurrent + " - " + staticNext + ")");
					signal.release();
				}
			};
			runThread.start();
//			System.out.println(signal.availablePermits());
			list.add(runThread);
			
			current = staticNext;
		}
		
		for (BoolThread item : list)
		{
			try
			{
				item.join();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < list.size(); i++)
		{
			if(!list.get(i).isAlive())
			{
				_total = _total.multiply(list.get(i).total);
				list.remove(i);
				i--;
			}
		}
		
		System.out.println(_total);
		
		return _total;
	}
	
	public static abstract class BoolThread extends Thread
	{
		BigInteger total;
	}
}
