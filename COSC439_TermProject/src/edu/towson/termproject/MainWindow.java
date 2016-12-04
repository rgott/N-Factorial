package edu.towson.termproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings("serial")
public class MainWindow extends JFrame
{
	private JPanel contentPane;
	static JLabel progressStatus;
	
	JTextField stripeSize;
	JTextField numthreads;
	JTextField factorialText;
	JTextField elementsTillCleaned;
	JTextField answerTotal;
	JTextField answerTotalLength;
	JTextField timeTaken;
	
	JButton btnCompute;
	JButton btnCancel;
	
	Thread numberFinderThread;
	
	SegmentedProgessBar startedProgress;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					MainWindow frame = new MainWindow();
					frame.setTitle("N Factorial finder");
					try
					{
						frame.setIconImage(ImageIO.read(new File("Resource/mainwindowicon.png")));
					}
					catch(Exception e)
					{
						System.out.println("Icon cannot be found");
						e.printStackTrace();
						// The window will just not have an icon
					}
					
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 310);
		setMinimumSize(new Dimension(350, 310));
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		
		JPanel fields = new JPanel();
		setFormLayout(fields, 7);
		
		// Add fields for options
		stripeSize = createField(fields, "Numbers per thread:");
		numthreads = createField(fields, "Threads:");
		elementsTillCleaned = createField(fields, "Elements till multiply set:");
		factorialText = createField(fields, "Enter Factorial to find:");
		answerTotal = createField(fields, "Total:");
		answerTotal.setEditable(false);
		answerTotal.setInputVerifier(null);
		
		answerTotalLength = createField(fields, "Answer num characters:");
		answerTotalLength.setEditable(false);
		answerTotalLength.setInputVerifier(null);
		
		timeTaken = createField(fields, "Time taken:");
		timeTaken.setEditable(false);
		timeTaken.setInputVerifier(null);
		
		centerPanel.add(fields,BorderLayout.CENTER);
		
		JPanel floatRight = new JPanel();
		floatRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				// only if factorial is being found can cancel happen
				if(numberFinderThread != null && numberFinderThread.isAlive())
				{
					numberFinderThread.interrupt();
					answerTotal.setText("");
					btnCompute.setEnabled(true);
					btnCancel.setEnabled(false);
					startedProgress.setValue(0);
				}
			}
		});
		btnCancel.setEnabled(false);
		floatRight.add(btnCancel);
		
		btnCompute = new JButton("Compute");
		btnCompute.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				// if any fields are empty then do not compute
				if(factorialText.getText().isEmpty() || numthreads.getText().isEmpty() || stripeSize.getText().isEmpty())
				{
					progressStatus.setText("ERROR: fill in all fields");
					return;
				}
				
				progressStatus.setText("");
				answerTotal.setText("");
				btnCompute.setEnabled(false);
				
				numberFinderThread = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						BigInteger factorial = new BigInteger(factorialText.getText());
						startedProgress.setMax(factorial);
						
						progressStatus.setText("Running factorials");
						
						long start = System.nanoTime();
						BigInteger retNum = StartFind(
								startedProgress,
								new BigInteger(stripeSize.getText()),
								Integer.parseInt(numthreads.getText()),
								factorial,
								Integer.parseInt(elementsTillCleaned.getText()));
						long end = System.nanoTime();
						
						end = end - start;
						if(end > 1000000)
						{
							timeTaken.setText(Long.toString(end / 1000000) + " milliseconds");
						}
						else if(end > 1000000000)
						{
							timeTaken.setText(Long.toString(end / 1000000000) + " seconds");
						}
						else if(end > 1000000000000L)
						{
							timeTaken.setText(Long.toString(end / 1000000000000L) + " minutes");
						}
						else
						{
							timeTaken.setText(Long.toString(end) + " nanoseconds");
						}
						
						
						if(retNum != null)
						{
							answerTotal.setText("Loading...");
							answerTotalLength.setText("Loading...");
							
							answerTotal.setText(retNum.toString());
						}
						else
							answerTotal.setText("");
						
						answerTotalLength.setText(Integer.toString(answerTotal.getText().length()));
						
						btnCompute.setEnabled(true);
						btnCancel.setEnabled(false);
					}
				});
				
				btnCompute.setEnabled(false);
				btnCancel.setEnabled(true);
				
				numberFinderThread.start();// start thread immediately
			}
		});
		floatRight.add(btnCompute);

		centerPanel.add(floatRight, BorderLayout.SOUTH);
		contentPane.add(centerPanel,BorderLayout.NORTH);
		
		// add progress bar and status
		JPanel panel = new JPanel();
		panel.setLayout(new FormLayout(
				new ColumnSpec[]{ColumnSpec.decode("150px:grow")},
				new RowSpec[]{ FormSpecs.DEFAULT_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC}));
		
		progressStatus = new JLabel("");
		panel.add(progressStatus,"1, 1");
		
		startedProgress = new SegmentedProgessBar(this,true);
		startedProgress.setForeground(Color.green);
		panel.add(startedProgress,"1, 2");
		
		contentPane.add(panel,BorderLayout.SOUTH);
		
		
		stripeSize.setText("100");
		numthreads.setText("10");
		factorialText.setText("123456");
		elementsTillCleaned.setText("1000");
	}
	
	public static BigInteger StartFind(SegmentedProgessBar startedProgess, BigInteger stripeSize,int threads, BigInteger factorial,int elementsTillClean)
	{
		BigInteger _total = new BigInteger("1"); // must be one because its multiplied 1*x = x
		ArrayList<BoolThread> list = new ArrayList<>(elementsTillClean);
		
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
				while(!signal.tryAcquire(750 ,TimeUnit.MILLISECONDS) || list.size() == elementsTillClean)
				{
					progressStatus.setText("Cleaning list");
					// clean list
					int counter = 0;
					for (int i = 0; i < list.size(); i++) // check all items in the list 
					{
						if(counter++ % 50 == 0)
						{
							progressStatus.setText("Cleaning list " + (list.size() - i) + " items remain.");
							try
							{
								if(signal.tryAcquire(10, TimeUnit.MILLISECONDS)) // used to see if the process was interupted
									signal.release();
								
							}catch(InterruptedException e)
							{
								progressStatus.setText("Cancelled");
								return null;
							}
						}
						if(!list.get(i).isAlive()) // if thread killed then add to toal and remove element
						{
							_total = _total.multiply(list.get(i).total);
							list.remove(i);
							i--; // since element is removed, item is i-1
						}
					}
					progressStatus.setText("Running factorials");
				}
			} catch (InterruptedException e)
			{
				e.printStackTrace();
				progressStatus.setText("Cancelled");
				return null; // Interruption is probably caused by user cancellation  
			}

			BigInteger staticCurrent = current;
			BigInteger staticNext = next;
			startedProgess.update(staticNext);
			
			BoolThread runThread = new BoolThread()
			{
				@Override
				public void run()
				{
//					System.out.println("Thread Start (" + staticCurrent + " - " + staticNext + ")");
					
					total = staticCurrent;
					BigInteger i = staticCurrent.subtract(BigInteger.ONE);
					while(i.compareTo(staticNext) == 1)
					{
						total = total.multiply(i);
						i = i.subtract(BigInteger.ONE);
					}
//					System.out.println("Release (" + staticCurrent + " - " + staticNext + ")");
					signal.release();
				}
			};
			runThread.start();
			list.add(runThread);
			
			current = staticNext; // move to next
		}

		progressStatus.setText("Joining threads");
		startedProgess.setMax(new BigInteger(Integer.toString(list.size())));
		startedProgess.setReversed(false);

		int counter = 1;
		for (int i = 0; i < list.size(); i++)
		{
			try
			{
				list.get(i).join();
				_total = _total.multiply(list.get(i).total);
				startedProgess.update(new BigInteger(Integer.toString(counter++)));
			} catch (InterruptedException e)
			{
				System.out.println("INTERUPTION EXCEPTION");
				progressStatus.setText("Cancelled1");
				e.printStackTrace();
				return null;
			}
		}
		startedProgess.setReversed(true);
		progressStatus.setText("Complete");
		return _total;
	}
	
	
	////////////////////// UI Functions //////////////////////
	private int currentRow = 0;
	private JTextField createField(JPanel comp, String data)
	{	
		int realRow = ((currentRow * 2) + 2);
		JLabel label = new JLabel(data);
		comp.add(label, "2, " + realRow + ", left, center");
		
		JTextField textField = new JTextField();
		textField.setInputVerifier(new NumberValidation());
		comp.add(textField, "4, " + realRow + ", fill, default");

		currentRow++;
		return textField;
	}
	
	public void setFormLayout(JPanel panel, int rows)
	{
		// create row specification
		LinkedList<RowSpec> rowSpec = new LinkedList<>();
		rowSpec.add(FormSpecs.LINE_GAP_ROWSPEC);
		rowSpec.add(RowSpec.decode("20px"));
		
		for (int i = 0; i < rows; i++)
		{
			rowSpec.add(FormSpecs.RELATED_GAP_ROWSPEC);
			rowSpec.add(FormSpecs.DEFAULT_ROWSPEC);
		}
		
		// create column specification
		LinkedList<ColumnSpec> colSpec = new LinkedList<>();
		colSpec.add(ColumnSpec.decode("20px"));
		colSpec.add(ColumnSpec.decode("150px"));
		colSpec.add(FormSpecs.DEFAULT_COLSPEC);
		colSpec.add(ColumnSpec.decode("150px:grow"));
		colSpec.add(FormSpecs.DEFAULT_COLSPEC);
		colSpec.add(ColumnSpec.decode("30px"));
		
		panel.setLayout(new FormLayout(colSpec.toArray(new ColumnSpec[colSpec.size()]),rowSpec.toArray(new RowSpec[rowSpec.size()])));
	}
	
}
