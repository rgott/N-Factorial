package edu.towson.termproject;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

public class SystemInfo extends JFrame
{

	private JPanel contentPane;

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
					SystemInfo frame = new SystemInfo();
					
					frame.setVisible(true);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	com.sun.management.OperatingSystemMXBean os = 
			(com.sun.management.OperatingSystemMXBean)java.lang.management.ManagementFactory.getOperatingSystemMXBean();
	
	/**
	 * Create the frame.
	 */
	public SystemInfo()
	{
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		
		setBounds(200, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setFormLayout(contentPane, 7);
		
		
		JLabel version = createField(contentPane, "Windows Version:",os.getVersion());
		JLabel processors = createField(contentPane, "Processors:",Integer.toString(os.getAvailableProcessors()));
		JLabel cvms = createField(contentPane, "Commited Virtual memory size:",Long.toString(os.getCommittedVirtualMemorySize()));
		JLabel physical = createField(contentPane, "Physical memory(mb)", "Loading...");
		JLabel cpuLoad = createField(contentPane, "CPU load","Loading...");
		
		
		setContentPane(contentPane);
		
		
		
		Timer SimpleTimer = new Timer(500, new ActionListener(){
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	physical.setText(Long.toString(os.getFreePhysicalMemorySize()/1024/1024) + " / " + Long.toString(os.getTotalPhysicalMemorySize()/1024/1024));
				cpuLoad.setText(Double.toString(os.getProcessCpuLoad()));
		    }
		});
		SimpleTimer.start();
		
	}
	
	
	
	////////////////////// UI Functions //////////////////////
	private int currentRow = 0;

	private JLabel createField(JPanel comp, String data,String value)
	{
		int realRow = ((currentRow * 2) + 2);
		JLabel dataField = new JLabel(data);
		comp.add(dataField, "2, " + realRow + ", left, center");

		JLabel valueField = new JLabel(value);
		comp.add(valueField, "4, " + realRow + ", fill, default");

		currentRow++;
		return valueField;
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
		colSpec.add(ColumnSpec.decode("180px"));
		colSpec.add(FormSpecs.DEFAULT_COLSPEC);
		colSpec.add(ColumnSpec.decode("150px:grow"));
		colSpec.add(FormSpecs.DEFAULT_COLSPEC);
		colSpec.add(ColumnSpec.decode("30px"));

		panel.setLayout(new FormLayout(colSpec.toArray(new ColumnSpec[colSpec.size()]),
				rowSpec.toArray(new RowSpec[rowSpec.size()])));
	}

}
