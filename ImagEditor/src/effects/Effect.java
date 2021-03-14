package effects;

import java.awt.image.BufferedImage;

public abstract class Effect {
	public abstract BufferedImage getImage(BufferedImage bf);
	public abstract void edit();
}
