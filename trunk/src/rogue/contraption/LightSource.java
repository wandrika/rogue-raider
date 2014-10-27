package rogue.contraption;

import java.util.Collection;

import jade.core.Actor;
import jade.fov.ModifiedRayCaster;
import jade.ui.ColorConstants;
import jade.util.Dice;
import jade.util.datatype.ColoredChar;
import jade.util.datatype.Coordinate;
import jade.util.datatype.Light;


public class LightSource extends Actor {
	private Light lightColor;
	private int radialFadeToPercent;
	private int rangeMin, rangeMax;
	protected ModifiedRayCaster lightView = new ModifiedRayCaster();

	public LightSource(char face, String colorName, int rangeMin, int rangeMax, int fadeToPercent){
		super(ColoredChar.create(face, ColorConstants.getLight(colorName).getColor()));
		this.lightColor = ColorConstants.getLight(colorName);
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
		this.radialFadeToPercent = fadeToPercent;
	}

	public Light getLightColor() {
		return lightColor;
	}

	public int getRadialFadeToPercent() {
		return radialFadeToPercent;
	}

	public int getRangeMin() {
		return rangeMin;
	}

	public int getRangeMax() {
		return rangeMax;
	}

	@Override
	public void act() {

		int[] colorComponents = new int[3];
		int randComponent, lightMultiplier;
		int fadeToPercent;
		float radius;

		radius = Dice.global.nextInt(rangeMin, rangeMax);
		//radius /= 100;

		randComponent = Dice.global.nextInt(0, lightColor.getRand());
		colorComponents[0] = randComponent + lightColor.getRed() + Dice.global.nextInt(0, lightColor.getRedRand());
		colorComponents[1] = randComponent + lightColor.getGreen() + Dice.global.nextInt(0, lightColor.getGreenRand());
		colorComponents[2] = randComponent + lightColor.getBlue() + Dice.global.nextInt(0, lightColor.getBlueRand());

		fadeToPercent = radialFadeToPercent;

		Collection<Coordinate> fieldView = lightView.getViewField(world, this.x(), this.y(), (int)radius);
		//FIXME pass param for !isMinersLight;
		for (Coordinate coord: fieldView){
			lightMultiplier = (int) (100 - (100 - fadeToPercent) * 
					(Math.sqrt((coord.x()- this.x()) * (coord.x()-this.x()) + (coord.y()-this.y()) * (coord.y()-this.y())) / (radius)));
			world.lightAt(coord.x(), coord.y()).multiplyLight(colorComponents,lightMultiplier);
		}

		world.lightAt(this.x(), this.y()).addLight(colorComponents);

	}
}
