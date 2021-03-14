package components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import main.Main;
import shapes.Picture;
import shapes.Shape;

public class Board extends JLabel{
	private static final long serialVersionUID = 1L;
	
	LinkedList<Shape> shapes = new LinkedList<Shape>();
	BufferedImage display;
	Color backgroundColor;
	int width;
	int height;
	public Board(Color backgroundColor, int width, int height) {
		super();
		this.backgroundColor = backgroundColor;
		this.width = width;
		this.height = height;
		final Board cur = this;
		this.display = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		MouseAdapter mouseListener = new MouseAdapter() {
			int firstX = 0;
			int firstY = 0;
			@Override
			public void mousePressed(MouseEvent e) {
				firstX = e.getXOnScreen();
				firstY = e.getYOnScreen();
				shapeInFocus = getShapeAt(e.getX(), e.getY());
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				firstX = 0;
				firstY = 0;
				shapeInFocus = null;
			}
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					Shape s = getShapeAt(e.getX(), e.getY());
					if (s != null) {
						Main.getPopupMenuForShape(s).show(cur, e.getX(), e.getY());
					}
				}
			}
			Shape shapeInFocus = null;
			@Override
			public void mouseDragged(MouseEvent e) {
				if (shapeInFocus != null) {
					shapeInFocus.setX(e.getXOnScreen() - firstX);
					shapeInFocus.setY(e.getYOnScreen() - firstY);
					Main.board.paintShapes();
				}
			}
		};
		this.addMouseListener(mouseListener);
		this.addMouseMotionListener(mouseListener);
		paintShapes();
	}
	public void setPaperSize(int width, int height) {
		this.width = width;
		this.height = height;
		Main.sizeLabel.setText(width + "x" + height);
		this.display = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		paintShapes();
	}
	public void paintShapes() {
		System.out.println("Board repainted");
		paintShapes(display.getGraphics());
	}
	public void paintShapes(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.get(i).isVisible()) {
				shapes.get(i).draw(g);
			}
		}
		this.setIcon(new ImageIcon(Picture.getScaledImage(display, (int)(width * Main.zoomSlider.getValue()/100.0), 
				(int)(height * Main.zoomSlider.getValue()/100.0))));
		repaint();
	}
	public LinkedList<Shape> getShapesList() {
		return shapes;
	}
	public void addShape(Shape shape) {
		System.out.println("Add " + shape.getName() + "to the board");
		shapes.add(shape);
		Main.updateShapeList();
	}
	public BufferedImage getDisplay() {
		paintShapes();
		return display;
	}
	@Override
	public int getWidth() {
		return this.width;
	}
	@Override
	public int getHeight(){
		return this.height;
	}
	public Board(BufferedImage display, Color backgroundColor, int width, int height) {
		super();
		this.display = display;
		this.backgroundColor = backgroundColor;
		this.width = width;
		this.height = height;
	}
	public Shape getShapeAt(int x, int y) {
		Shape s = null;
		for (int i = shapes.size() - 1; i > -1; i--) {
			s = shapes.get(i);
			if (s.getX() < x && s.getY() < y && s.getX() + s.getWidthOnBoard() > x && s.getY() + s.getHeightOnBoard() > y) {
				return s;
			}
		}
		return null;
	}
}