package jade.util.datatype;

import jade.util.Guard;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


/**
 * An immutable tuple of ({@code char}, {@code Color}). This is useful for storing screen display
 * information.
 */
public class ColoredChar
{
    private final char ch;
    private final Color color;
    
    //experiment pre hru typu Brogue, kde sa pouziva pozadie. Defaultne je null, t.j. cierne.
    private final Color background;
    
    public static ColoredChar blank = new ColoredChar(' ', Color.black);

    private static Map<ColoredChar, ColoredChar> interned = new HashMap<ColoredChar, ColoredChar>();

    /**
     * Gets the {@code ColoredChar} tuple with the given values. This will always return the
     * interned canonical version of the particular {@code ColoredChar}.
     * @param ch the {@code char} value of the {@code ColoredChar}
     * @param color the {@code Color} value of the {@code ColoredChar}
     * @return the interned {@code ColoredChar} with the specified values
     */
    public static ColoredChar create(char ch, Color color)
    {
        ColoredChar instance = new ColoredChar(ch, color);
        if(!interned.containsKey(instance))
            interned.put(instance, instance);
        return interned.get(instance);
    }

    public static ColoredChar create(char ch, Color color, Color background)
    {
        ColoredChar instance = new ColoredChar(ch, color, background);
        if(!interned.containsKey(instance))
            interned.put(instance, instance);
        return interned.get(instance);
    }
    
    /**
     * Constructs a new {@code ColoredChar} tuple with the given {@code char} value and {@code
     * Color.white} as the default {@code Color} value. This will always return the interned
     * canonical version of the particular {@code ColoredChar}.
     * @param ch the {@code char} value of the {@code ColoredChar}
     * @return the interned {@code ColoredChar} with the specified values
     */
    public static ColoredChar create(char ch)
    {
        return create(ch, Color.white);
    }

    private ColoredChar(char ch, Color color)
    {
        Guard.argumentIsNotNull(color);

        this.ch = ch;
        this.color = color;
        this.background = null;
    }

    private ColoredChar(char ch, Color color, Color background)
    {
        Guard.argumentIsNotNull(color);

        this.ch = ch;
        this.color = color;
        this.background = background;
    }
    
    /**
     * Returns a dimmer version of the colored char. This is useful for the tiles that were seen previously in the game,
     * but they are out of visible range now.
     * 
     * @return dimmed version of this ColoredChar.
     */
    public ColoredChar getDimmed(){
    	char ch = this.ch;
    	int b = Math.min(100, this.color.getBlue());
    	Color col = new Color(this.color.getRed()/10,this.color.getGreen()/10,b);
    			//this.color.darker();
    	Color back = null;
    	if (this.background!=null) {
    		b = Math.min(100, this.background.getBlue());
    		back = new Color(this.color.getRed()/10,this.color.getGreen()/10,b);
    	}
    			//this.background.darker();
    	return ColoredChar.create(ch, col, back);
    }
    
    
    public ColoredChar applyLight(Light light){
    	//FIXME flatten to range 0-250, keep distances
    	int red = Math.min(255, this.color.getRed() * light.getRed() / 200);
    	int green = Math.min(255, this.color.getGreen() * light.getGreen() / 200);
    	int blue = Math.min(255, this.color.getBlue() * light.getBlue() / 200);
    	if(red<50 && green<50 && blue<50){
    		red=50;green=50;blue=50;
    	}
    	int bred=0,bgreen=0,bblue=0;
    	if (this.background!=null){
    		bred = Math.min(255, this.background.getRed() * light.getRed() / 200);
        	bgreen = Math.min(255, this.background.getGreen() * light.getGreen() / 200);
        	bblue = Math.min(255, this.background.getBlue() * light.getBlue() / 200);
    	}
    	return ColoredChar.create(this.ch, new Color(red,green,blue), new Color(bred,bgreen,bblue));
    }
    
    /**
     * Returns the {@code char} value of the {@code ColoredChar}
     * @return the {@code char} value of the {@code ColoredChar}
     */
    public char ch()
    {
        return ch;
    }

    /**
     * Returns the {@code Color} value of the {@code ColoredChar}
     * @return the {@code Color} value of the {@code ColoredChar}
     */
    public Color color()
    {
        return color;
    }

    /**
     * Returns the {@code Color} value of the {@code ColoredChar}
     * @return the {@code Color} value of the {@code ColoredChar}
     */
    public Color background()
    {
        return background;
    }
    


    @Override
    public String toString()
    {
        return Character.toString(ch);
    }

    @Override
    public int hashCode()
    {
    	int backgroundhash = (background==null) ? 0 : background.hashCode();
        return (backgroundhash << 16) | (color.hashCode() << 8) | (ch & 0xFF);
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ColoredChar other = (ColoredChar) obj;
		if (background == null) {
			if (other.background != null)
				return false;
		} else if (!background.equals(other.background))
			return false;
		if (ch != other.ch)
			return false;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		return true;
	}
}
