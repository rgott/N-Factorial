package edu.towson.termproject;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

public class NumberValidation extends InputVerifier
{

	@Override
	public boolean verify(JComponent input)
	{
		String text = ((JTextField)input).getText();
		try
        {
    		Integer.parseInt(text);
    		return true;
        } catch (NumberFormatException e) 
        {
            return false;
        }
	}
	
}
