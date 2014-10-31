package jade.util.datatype;

import java.awt.Color;

/**
 * Class representing light with its red/green/blue parts and 
 * also random parts that allow some variability.
 * This is inspired by Brogue light model, but adapted
 * to Java objects.
 * Special thanks to Brian Walker!
 * @author wandrika
 *
 */
public class Light {

	private int red;
	private int green;
	private int blue;
	private int redRand;
	private int greenRand;
	private int blueRand;
	private int rand;
	private int numberOfComponents = 1;
	//TODO if necessary, add alpha channel

	public Light(Color color, int redRand, int greenRand, int blueRand, int rand) {
		super();
		this.red = color.getRed();
		this.green = color.getGreen();
		this.blue = color.getBlue();
		this.redRand = redRand;
		this.greenRand = greenRand;
		this.blueRand = blueRand;
		this.rand = rand;
		this.numberOfComponents = 1;
	}

	public void copyFrom(Light other){
		this.red = other.red;
		this.green = other.green;
		this.blue = other.blue;
		this.redRand = other.redRand;
		this.greenRand = other.greenRand;
		this.blueRand = other.blueRand;
		this.rand = other.rand;
		this.numberOfComponents = other.numberOfComponents;
	}

	public void addLight(int[] light){
		if(light.length == 3){
			this.red += light[0];
			this.green += light[1];
			this.blue += light[2];
			this.numberOfComponents++;
		}
	}

	public void setLights(int r, int g, int b){
		this.red = r;
		this.green = g;
		this.blue = b;
		this.numberOfComponents = 1;
	}

	public void setLights(Color c){
		this.red = c.getRed();
		this.green = c.getGreen();
		this.blue = c.getBlue();
		this.numberOfComponents = 1;
	}
	
	public void multiplyLight(int[] light, int multiplier){
		if(light.length == 3){
			this.red += light[0]*multiplier / 100;
			this.green += light[1]*multiplier / 100;
			this.blue += light[2]*multiplier / 100;
			this.numberOfComponents++;
		}
	}

	/**
	 * Set to average of all light components.
	 */
	public void normalize(){
		this.red /= this.numberOfComponents;
		this.green /= this.numberOfComponents;
		this.blue /= this.numberOfComponents;
		this.numberOfComponents = 1;
	}
	
	public Color getColor() {
		return new Color(red,green,blue);
	}

	public int getRedRand() {
		return redRand;
	}

	public int getGreenRand() {
		return greenRand;
	}

	public int getBlueRand() {
		return blueRand;
	}

	public void setRandoms(int red, int green, int blue){
		this.blueRand = blue;
		this.greenRand = green;
		this.redRand = red;
	}

	public int getRed(){
		return this.red;
	}
	public int getGreen(){
		return this.green;
	}
	public int getBlue(){
		return this.blue;
	}

	public int getRand() {
		return rand;
	}
	
	public void setRand(int r){
		this.rand = r;
	}

	public int getNumberOfComponents() {
		return numberOfComponents;
	}
	
}
