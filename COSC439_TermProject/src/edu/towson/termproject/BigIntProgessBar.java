package edu.towson.termproject;

import java.math.BigInteger;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

@SuppressWarnings("serial")
public class BigIntProgessBar extends JProgressBar
{
	BigInteger max;

	// must be set
	public void setMax(BigInteger max)
	{
		this.max = max;
	}

	JFrame frame;
	boolean reversed;
	static final int HEIGHT = 23;
	static final int MAXPROGRESS = 900;
	public BigIntProgessBar(JFrame frame, boolean reversed)
	{
		super(0,MAXPROGRESS);
		this.frame = frame;
		this.reversed = reversed;
	}
	
	public void update(BigInteger end)
	{
		setBounds(0, 0,  0, HEIGHT);
		if(reversed)
		{
			super.setValue(1000 - map(end,BigInteger.ZERO,max,0,MAXPROGRESS));
		}
		else
		{
			super.setValue(map(end,BigInteger.ZERO,max,0,MAXPROGRESS));
		}
		frame.revalidate();
	}
	
	private int map(BigInteger x, BigInteger in_min, BigInteger in_max, int out_min, int out_max)
	{
	  return (x.subtract(in_min)).multiply(new BigInteger(Integer.toString(out_max - out_min))).divide(in_max.subtract(in_min)).add(new BigInteger(Integer.toString(out_min))).intValue();
	}

	public void setReversed(boolean b)
	{
		reversed = b;
	}
}