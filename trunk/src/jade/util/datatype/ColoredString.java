package jade.util.datatype;

import jade.util.Guard;

import java.awt.Color;

/**
 * An immutable tuple of ({@code String}, {@code Color}). This is useful for storing screen display
 * information.
 */
public class ColoredString
{
    private final String s;
    private final Color color;
    

    public ColoredString(String s)
    {
        this(s, Color.white);
    }

    public ColoredString(String s, Color color)
    {
        Guard.argumentIsNotNull(color);

        this.s = s;
        this.color = color;
    }


    public Color color()
    {
        return color;
    }

    
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof ColoredString)
        {
            ColoredString other = (ColoredString)obj;
            return s == other.s && color.equals(other.color);
        }
        return false;
    }


    @Override
    public String toString()
    {
        return s;
    }
}
