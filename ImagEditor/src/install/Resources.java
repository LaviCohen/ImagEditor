package install;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Resources {
	public static ImageIcon editIcon;
	public static ImageIcon removeIcon;
	public static ImageIcon up_layerIcon;
	public static ImageIcon down_layerIcon;
	public static ImageIcon showIcon;
	public static ImageIcon hideIcon;
	static {
		try {
			editIcon = new ImageIcon(ImageIO.read(
					Resources.class.getResourceAsStream("images/edit.jpg")));
			removeIcon = new ImageIcon(ImageIO.read(
					Resources.class.getResourceAsStream("images/remove.jpg")));
			up_layerIcon = new ImageIcon(ImageIO.read(
					Resources.class.getResourceAsStream("images/up_layer.png")));
			down_layerIcon = new ImageIcon(ImageIO.read(
					Resources.class.getResourceAsStream("images/down_layer.png")));
			showIcon = new ImageIcon(ImageIO.read(
					Resources.class.getResourceAsStream("images/show.png")));
			hideIcon = new ImageIcon(ImageIO.read(
					Resources.class.getResourceAsStream("images/hide.png")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
