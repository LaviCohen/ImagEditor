package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Main;
import shapes.Picture;
import shapes.Shape;

public class Board extends JPanel{
	private static final long serialVersionUID = 1L;
	
	LinkedList<Shape> shapes = new LinkedList<Shape>();
	BufferedImage image;
	JLabel display;
	Color backgroundColor;
	int width;
	int height;
	public Board(Color backgroundColor, int width, int height) {
		super(new BorderLayout());
		this.backgroundColor = backgroundColor;
		this.width = width;
		this.height = height;
		this.display = new JLabel();
		this.add(display, BorderLayout.CENTER);
		final Board cur = this;
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		MouseAdapter mouseListener = new MouseAdapter() {
			int firstX = 0;
			int firstY = 0;
			int firstShapeX = 0;
			int firstShapeY = 0;
			double movementInX = 0;
			double movementInY = 0;
			@Override
			public void mousePressed(MouseEvent e) {
				firstX = e.getXOnScreen();
				firstY = e.getYOnScreen();
				System.out.println(e.getX() + ", " + e.getY() + ":" + getLeftGap() + ", " + getUpGap());
				shapeInFocus = getShapeAt(
						(int)((e.getX() - getLeftGap()) / getZoom()),
						(int)((e.getY() -   getUpGap()) / getZoom()));
				if (shapeInFocus != null) {
					firstShapeX = shapeInFocus.getX();
					firstShapeY = shapeInFocus.getY();
				}
			}
			private double getUpGap() {
				return ((Main.boardScrollPane.getHeight() - (height * getZoom()))/2);
			}
			private double getLeftGap() {
				return ((Main.boardScrollPane.getWidth()  - (width  * getZoom()))/2);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				firstX = 0;
				firstY = 0;
				firstShapeX = 0;
				firstShapeY = 0;
				movementInX = 0;
				movementInY = 0;
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
					movementInX += (e.getXOnScreen() - firstX) * 100.0 / Main.zoomSlider.getValue();
					movementInY += (e.getYOnScreen() - firstY) * 100.0 / Main.zoomSlider.getValue();
					shapeInFocus.setX(firstShapeX + (int)movementInX);
					shapeInFocus.setY(firstShapeY + (int)movementInY);
					firstX = e.getXOnScreen();
					firstY = e.getYOnScreen();
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
		this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		paintShapes();
	}
	public void paintShapes() {
		System.out.println("Board repainted");
		paintShapes(image.getGraphics());
	}
	public void paintShapes(Graphics g) {
		g.setColor(backgroundColor);
		g.fillRect(0, 0, width, height);
		for (int i = 0; i < shapes.size(); i++) {
			if (shapes.get(i).isVisible()) {
				shapes.get(i).draw(g);
			}
		}
		this.remove(display);
		this.display = new JLabel(new ImageIcon(
						Picture.getScaledImage(image, 
								(int)(width  * getZoom()), 
								(int)(height * getZoom()))));
		this.add(display);
		this.revalidate();
		this.repaint();
		if (Main.boardScrollPane != null) {
			Main.boardScrollPane.revalidate();
			Main.boardScrollPane.repaint();
		}
	}
	public LinkedList<Shape> getShapesList() {
		return shapes;
	}
	public void addShape(Shape shape) {
		System.out.println("Add " + shape.getName() + " to the board");
		shapes.add(shape);
		Main.updateShapeList();
	}
	public BufferedImage getDisplay() {
		paintShapes();
		return image;
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
		this.image = display;
		this.backgroundColor = backgroundColor;
		this.width = width;
		this.height = height;
	}
	public static double getZoom() {
		return Main.zoomSlider.getValue()/100.0;
	}
	public Shape getShapeAt(int x, int y) {
		System.out.println(x + ", " + y);
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