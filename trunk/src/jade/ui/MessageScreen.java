package jade.ui;

import jade.util.datatype.ColoredString;
import jade.util.datatype.MessageQueue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class MessageScreen extends JPanel {
	private int tileWidth;
	private int tileHeight;

	public MessageScreen(int columns, int rows, int fontSize)
	{
		tileWidth = fontSize * 3 / 4;
		tileHeight = fontSize;
		setPreferredSize(new Dimension(columns * tileWidth, rows * tileHeight));
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, fontSize));
		setBackground(Color.black);
		setFocusable(false);
	}

	@Override
	protected void paintComponent(Graphics page) 
	{
		super.paintComponent(page);
		ColoredString[] messages = MessageQueue.getItems(TermPanel.numMessages);

		int y=0;
		for(ColoredString message: messages)
		{
			int x = 0;
			y += tileHeight;
			page.setColor(message.color());
			page.drawString(message.toString(), x, y);
		}
	}
}
