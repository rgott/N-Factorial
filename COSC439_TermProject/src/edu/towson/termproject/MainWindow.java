package edu.towson.termproject;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.Scrollbar;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;

public class MainWindow extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea ta = new JTextArea();
	private JPanel contentPane;
	private final JList<String> textList = new JList<String>();
	JTextField stripeSize;
	JTextField threads;
	JTextField combination;
	JButton btnCompute;
	JButton btnCancel;
	Thread numberFinderThread;
	JTextField answerTotal;
	DefaultListModel<String> mTextList;
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
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout());
		
		JPanel fields = new JPanel();
		setFormLayout(fields, 4);
		
		stripeSize = createField(fields, "Numbers per thread:");
		threads = createField(fields, "Threads:");
		combination = createField(fields, "Enter Factorial to find:");
		answerTotal = createField(fields, "Total:");
		answerTotal.setEditable(false);
		answerTotal.setInputVerifier(null);
		
		
		
		centerPanel.add(fields,BorderLayout.CENTER);
		
		mTextList = new DefaultListModel<>();
		textList.setModel(mTextList);
		
		JPanel floatRight = new JPanel();
		floatRight.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if(numberFinderThread != null && numberFinderThread.isAlive())
				{
					numberFinderThread.interrupt();
					btnCompute.setEnabled(true);
					btnCancel.setEnabled(false);
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
				btnCompute.setEnabled(false);
				
				numberFinderThread = new Thread(new Runnable()
				{
					@Override
					public void run()
					{
						answerTotal.setText(StartFind(
								new BigInteger(stripeSize.getText()),
								Integer.parseInt(threads.getText()),
								new BigInteger(combination.getText()), 1000).toString());
						btnCompute.setEnabled(true);
						btnCancel.setEnabled(false);
					}
				});
				btnCompute.setEnabled(false);
				btnCancel.setEnabled(true);
				numberFinderThread.start();
			}
		});
		floatRight.add(btnCompute);
		centerPanel.add(floatRight, BorderLayout.SOUTH);
		contentPane.add(centerPanel,BorderLayout.NORTH);
		
		JScrollPane p = new JScrollPane(textList);
		
		contentPane.add(p, BorderLayout.SOUTH);
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
	
	public static BigInteger StartFind(BigInteger stripeSize,int threads, BigInteger factorial,int elementsTillClean)
	{
		BigInteger _total = new BigInteger("1");
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
				while(!signal.tryAcquire(5 ,TimeUnit.MILLISECONDS) || list.size() == elementsTillClean)
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
//					mTextList.addElement("Thread Start (" + staticCurrent + " - " + staticNext + ")");
//					System.out.println("Thread Start (" + staticCurrent + " - " + staticNext + ")");
					
					total = staticCurrent;
					BigInteger i = staticCurrent.subtract(BigInteger.ONE);
					while(i.compareTo(staticNext) == 1)
					{
						total = total.multiply(i);
						i = i.subtract(BigInteger.ONE);
//						System.out.println(total);
					}
					
//					mTextList.addElement("Release (" + staticCurrent + " - " + staticNext + ")");
//					System.out.println("Release (" + staticCurrent + " - " + staticNext + ")");
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
				System.out.println("INTERUPTION EXCEPTION");
				e.printStackTrace();
				return null;
			}
		}
		System.out.println("merging: " + list.size());
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
	
	public int currentRow = 0;
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
	
	
}
