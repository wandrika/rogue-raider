package jade.ui;

import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.List;
import java.util.Map;

//import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Implements a {@code Terminal} on a {@code JPanel}, which can then be embedded into any container
 * able to use a {@code JPanel}.
 */
public class TermPanel extends Terminal
{
	private GameScreen screen;
	private MessageScreen messageScreen;
	private StatusScreen statusScreen;
	public static final int numMessages = 5;

	/**
	 * Constructs a new {@code TermPanel} with the given dimensions. Note that the rows and columns
	 * can be changed by resizing the underlying JPanel, but font size is fixed.
	 * @param columns the default number of columns to display
	 * @param rows the default number of rows to display
	 * @param fontSize the size of each tile
	 */
	public TermPanel(int columns, int rows, int fontSize)
	{
		screen = new GameScreen(columns, rows, fontSize);
		messageScreen = new MessageScreen(columns, numMessages+1, fontSize);
		statusScreen = new StatusScreen(15, rows+numMessages+1, fontSize);
	}

	/**
	 * Constructs a new {@code TermPanel} with the default dimensions. There will be 80 columns, 24
	 * rows, and a font size of 14.
	 */
	public TermPanel()
	{
		this(80, 24, 14);
	}

	/**
	 * Constructs and returns a new {@code TermPanel} with default dimensions, which is placed
	 * inside a {@code JFrame}. The {@code TermPanel} will initially have 80 columns, 24 rows, and a
	 * font size of 12.
	 * @param title the title of the {@code JFrame}
	 * @return a new {@code TermPanel} with default dimensions.
	 */
	public static TermPanel getFramedTerminal(String title)
	{
		TermPanel term = new TermPanel();
		JFrame frame = new JFrame(title);
		Container panel = frame.getContentPane();
		panel.setLayout(new GridBagLayout());
		//frame.setContentPane(panel);
		
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.8;
		c.weighty = 0.2;
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		panel.add(term.messagePanel(), c);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.8;
		c.weighty = 0.8;
		c.gridwidth = 2;
		c.gridheight = 2;
		c.gridx = 0;
		c.gridy = 1;
		panel.add(term.panel(), c);
		
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.LINE_START;
		c.weightx = 0.2;
		c.weighty = 0.8;
		c.gridheight = 3;
		c.gridx = 2;
		c.gridy = 0;
		panel.add(term.statusScreen, c);
	
//		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
//		frame.setContentPane(panel);
//		panel.add(term.messagePanel());
//		panel.add(term.panel());
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		return term;
	}
	//ak chceme pridat dalsie oblasti okrem zakladneho hracieho pola (napriklad na vypisy, status bar a pod.), treba prislusne zmenit layout manager 
	//a prislusne nove panely pridat takto:
	//panel.add(novy)
	//Nove panely treba dodefinovat, idealne ako novu triedu.
	
	/**
	 * Returns the underlying {@code JPanel} display of the {@code TermPanel}. This {@code JPanel}
	 * can then be embedded in any other container like a normal {@code JPanel}.
	 * @return the underlying {@code JPanel} display of the {@code TermPanel}
	 */
	public JPanel panel()
	{
		return screen;
	}

	public JPanel messagePanel(){
		return messageScreen;
	}

	@Override
	public int getKey() throws InterruptedException
	{
		return screen.consumeKeyPress();
	}

	@Override
	protected void drawScreen(Map<Coordinate, ColoredChar> buffer)
	{
		screen.setBuffer(buffer);
		screen.repaint();
		messageScreen.repaint();
		statusScreen.repaint();
	}

	public void highlight(List<Coordinate> tiles)
	{
		if(tiles!=null){
			screen.setHighlighted(tiles);
			screen.repaint();
		}
	}

}
