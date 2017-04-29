package edu.towson.termproject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.io.File;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;

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
    static JLabel queueStatus;
	
    JButton btnCompute;
	
	Thread numberFinderThread;
	
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
		setBounds(100, 100, 450, 340);
		setMinimumSize(new Dimension(350, 340));
		
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		
		JPanel fields = new JPanel();
		setFormLayout(fields, 7);
		
		// Add fields for options
		JTextField stripeSize = createField(fields, "Numbers per thread:");
		JTextField numthreads = createField(fields, "Threads:");
		JTextField elementsTillCleaned = createField(fields, "Elements in queue:");
		JTextField factorialText = createField(fields, "Enter num to Factorialize:");
		
		JTextField answerTotal = createField(fields, "Factorial Found:");
		answerTotal.setEditable(false);
		answerTotal.setInputVerifier(null);
		
		JTextField answerTotalLength = createField(fields, "Factorial Length:");
		answerTotalLength.setEditable(false);
		answerTotalLength.setInputVerifier(null);
		
		JTextField timeTaken = createField(fields, "Time taken:");
		timeTaken.setEditable(false);
		timeTaken.setInputVerifier(null);
		
		centerPanel.add(fields,BorderLayout.CENTER);
		
		JPanel floatRight = new JPanel();
		floatRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		JButton showSysInfo = new JButton("System Info");
		showSysInfo.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					SystemInfo frame = new SystemInfo();
					frame.setTitle("System Information");
					try
					{
						frame.setIconImage(ImageIO.read(new File("Resource/mainwindowicon.png")));
					}
					catch(Exception ex)
					{
						System.out.println("Icon cannot be found");
						ex.printStackTrace();
						// The window will just not have an icon
					}
					
					frame.setVisible(true);
				} catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
		});
		floatRight.add(showSysInfo);
		
		JButton copyBtn = new JButton("Copy");
		copyBtn.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				StringSelection stringSelection = new StringSelection (answerTotal.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
			}
		});
		copyBtn.setEnabled(false);
		floatRight.add(copyBtn);
		
		
		BigIntProgessBar startedProgress = new BigIntProgessBar(this,true);
		startedProgress.setForeground(Color.green);
		
		JButton btnCancel = new JButton("Cancel");
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
		
		JProgressBar listSizeProgressBar = new JProgressBar();
		listSizeProgressBar.setForeground(Color.yellow);
		
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
				queueStatus.setText("");
				answerTotal.setText("");
				btnCompute.setEnabled(false);
				
				numberFinderThread = new Thread(new Runnable() // keeps ui running
				{
					@Override
					public void run()
					{
						BigInteger factorial = new BigInteger(factorialText.getText());
						
						
						progressStatus.setText("Running factorials");
						
						long start = System.nanoTime();
						BigInteger retNum = StartFind(
								startedProgress,
								listSizeProgressBar,
								new BigInteger(stripeSize.getText()),
								Integer.parseInt(numthreads.getText()),
								factorial,
								Integer.parseInt(elementsTillCleaned.getText()));
						long end = System.nanoTime();
						
						end = end - start;
						timeTaken.setText(Long.toString(end / 1000000) + " milliseconds");
						
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
				copyBtn.setEnabled(true);
				
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
				new RowSpec[]{ FormSpecs.DEFAULT_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC,FormSpecs.DEFAULT_ROWSPEC}));
		queueStatus = new JLabel("");
        panel.add(queueStatus,"1, 1");
                
		panel.add(listSizeProgressBar,"1,2");
		
                progressStatus = new JLabel("");
		panel.add(progressStatus,"1, 3");
		
		panel.add(startedProgress,"1, 4");
		
		contentPane.add(panel,BorderLayout.SOUTH);
		
		stripeSize.setText("100");
		numthreads.setText("10");
		factorialText.setText("123456");
		elementsTillCleaned.setText("1000");
	}
	
	public static BigInteger StartFind(BigIntProgessBar startedProgess, JProgressBar listSizeProgressBar, BigInteger stripeSize,int threads, BigInteger factorial, int elementsTillClean)
	{
		listSizeProgressBar.setMaximum(elementsTillClean);
		listSizeProgressBar.setMinimum(0);
		listSizeProgressBar.setValue(0);
		
		startedProgess.setMax(factorial);
		
		ArrayBlockingQueue<BigInteger> list = new ArrayBlockingQueue<>(elementsTillClean);
		
		BigIntThread masterCount = new BigIntThread()
		{
			@Override
			public void run()
			{
				try
				{
					total = new BigInteger("1");// must be one because its multiplied 1*x = x
					
					BigInteger[] result = factorial.divideAndRemainder(stripeSize);
					BigInteger i = result[0].add((result[1].compareTo(BigInteger.ZERO) == 0)?BigInteger.ZERO:BigInteger.ONE);
                                        result = null; // make it easy for garbage collector
					
//					System.out.println(i); // number of thread that will run
					while(i.compareTo(BigInteger.ZERO) == 1)
					{
						total = total.multiply(list.take());
						listSizeProgressBar.setValue(listSizeProgressBar.getValue() - 1);
                                                queueStatus.setText("Threads Appending");
						i = i.subtract(BigInteger.ONE);
					}
				} catch (InterruptedException e)
				{
					startedProgess.setValue(startedProgess.getMinimum());
					progressStatus.setText("Cancelled");
                                        queueStatus.setText("Cancelled");
					e.printStackTrace();
				}
			}
		};
		masterCount.start();
		
		BigInteger current = factorial;
		BigInteger next = current.subtract(stripeSize);

		Semaphore signal = new Semaphore(threads);
		boolean check = true;
		while(check)
		{
			if(next.compareTo(BigInteger.ONE) != 1)
			{
				next = BigInteger.ONE;
				check = false;
			}
			
			try
			{
				signal.acquire();
			} catch (InterruptedException e)
			{
				e.printStackTrace();
				startedProgess.setValue(startedProgess.getMinimum());
				progressStatus.setText("Cancelled");
                                queueStatus.setText("Cancelled");
				listSizeProgressBar.setValue(0);
				return null; // Interruption is probably caused by user cancellation  
			}
				
			listSizeProgressBar.setValue(listSizeProgressBar.getValue() + 1); // update progress bar
			{ // code block to create a thread and advance the pointers
				// create thread
				BigIntThread runThread = createThread(current,next,signal,list);
				runThread.start();
				
				// advance pointers
				startedProgess.update(current);
				current = next; // move to next
				next = current.subtract(stripeSize);
			}
		}
		
		try
		{
			masterCount.join();
			startedProgess.setValue(startedProgess.getMaximum());
			progressStatus.setText("Complete");
                        queueStatus.setText("");
			listSizeProgressBar.setValue(0);
			return masterCount.total;
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		progressStatus.setText("Cancelled");
		startedProgess.setValue(startedProgess.getMinimum());
        queueStatus.setText("Cancelled");
		listSizeProgressBar.setValue(0);
		return null;
	}
	
	private static BigIntThread createThread(BigInteger staticCurrent,BigInteger staticNext, Semaphore signal,ArrayBlockingQueue<BigInteger> concurrencyList)
	{
		return new BigIntThread()
		{
			@Override
			public void run()
			{
				total = staticCurrent;
				BigInteger i = staticCurrent.subtract(BigInteger.ONE);
				while(i.compareTo(staticNext) == 1)
				{
					total = total.multiply(i);
					i = i.subtract(BigInteger.ONE);
				}
				
				try
				{
					concurrencyList.put(total);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				signal.release();
			}
		};
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
	
	public static void setFormLayout(JPanel panel, int rows)
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
