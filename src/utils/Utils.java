package utils;

import javax.swing.*;
import java.awt.*;

/**
 * Created with IntelliJ IDEA.
 * User: Barak
 * Date: 02/10/13
 * Time: 18:45
 * To change this template use File | Settings | File Templates.
 */
public class Utils
{
	public static void showInfoMsg(Component parent, String msg)
	{
		JOptionPane.showMessageDialog(parent, msg, "מידע", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void showErrorMsg(Component parent, String errorMsg)
	{
		JOptionPane.showMessageDialog(parent, errorMsg, "שגיאה", JOptionPane.ERROR_MESSAGE);
	}

	public static void showExceptionMsg(Component parent, Exception e)
	{
		showErrorMsg(parent, e.getMessage());
	}

	public static String showInputMsg(Component component, String msg, String title)
	{
		return JOptionPane.showInputDialog(component, msg, title, JOptionPane.PLAIN_MESSAGE);
	}

	public static void setLineLayout(JPanel panel)
	{
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
	}

	public static void setPageLayout(JPanel panel)
	{
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
	}

	public static void addTinyRigid(JPanel panel)
	{
		panel.add(Box.createRigidArea(new Dimension(2, 2)));
	}

	public static void addSmallRigid(JPanel panel)
	{
		panel.add(Box.createRigidArea(new Dimension(5, 5)));
	}

	public static void addStandardRigid(JPanel panel)
	{
		panel.add(Box.createRigidArea(new Dimension(10, 10)));
	}

	// Set both minimum and preferred sizes
	public static void setSoftSize(Component component, Dimension boxSize)
	{
		component.setPreferredSize(boxSize);
		component.setMinimumSize(boxSize);
	}

	// Set both minimum and preferred sizes
	public static void setHardSize(Component component, Dimension boxSize)
	{
		component.setPreferredSize(boxSize);
		component.setMaximumSize(boxSize);
		component.setMinimumSize(boxSize);
	}
}
