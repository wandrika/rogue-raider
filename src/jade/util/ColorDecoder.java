package jade.util;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/*
 * We need Java color codes for monsters, this demo class is to make it easy. 
 * The color code is shown on "HSB" tab in a text field. It changes after clicking 
 * a color.
 */
@SuppressWarnings("serial")
public class ColorDecoder extends JPanel
	                              implements ChangeListener {

	    protected JColorChooser tcc;
	    protected JLabel banner;
	    protected JTextField tf;

	    public ColorDecoder() {
	        super(new BorderLayout());

	        //Set up the banner at the top of the window
	        banner = new JLabel("Color: ",
	                            JLabel.CENTER);
	        tf = new JTextField(20);
	        JPanel bannerPanel = new JPanel(new BorderLayout());
	        bannerPanel.add(banner, BorderLayout.CENTER);
	        bannerPanel.add(tf,BorderLayout.SOUTH);
	        bannerPanel.setBorder(BorderFactory.createTitledBorder("Banner"));

	        //Set up color chooser for setting text color
	        tcc = new JColorChooser(banner.getForeground());
	        tcc.getSelectionModel().addChangeListener(this);
	        tcc.setBorder(BorderFactory.createTitledBorder(
	                                             "Choose Color"));

	        add(bannerPanel, BorderLayout.CENTER);
	        add(tcc, BorderLayout.PAGE_END);
	    }

	    public void stateChanged(ChangeEvent e) {
	        Color newColor = tcc.getColor();
	        tf.setText(""+newColor.getRGB());
	    }

	    /**
	     * Create the GUI and show it.  For thread safety,
	     * this method should be invoked from the
	     * event-dispatching thread.
	     */
	    private static void createAndShowGUI() {
	        //Create and set up the window.
	        JFrame frame = new JFrame("ColorChooserDemo");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        //Create and set up the content pane.
	        JComponent newContentPane = new ColorDecoder();
	        newContentPane.setOpaque(true); //content panes must be opaque
	        frame.setContentPane(newContentPane);

	        //Display the window.
	        frame.pack();
	        frame.setVisible(true);
	    }

	    public static void main(String[] args) {
	        //Schedule a job for the event-dispatching thread:
	        //creating and showing this application's GUI.
	        javax.swing.SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	                createAndShowGUI();
	            }
	        });
	    }
	}

