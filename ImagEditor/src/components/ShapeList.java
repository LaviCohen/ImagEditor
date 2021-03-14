package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import layouts.ListLayout;
import main.Main;
import shapes.Picture;
import shapes.Shape;

public class ShapeList extends JPanel{
	private static final long serialVersionUID = 1L;
	public LinkedList<ShapePanel> shapePanels = new LinkedList<ShapeList.ShapePanel>();
	public static class ShapePanel extends JPanel{
		private static final long serialVersionUID = 1L;
		public Shape shape;
		public ShapePanel(Shape shape, ShapeList shapeList) {
			super(new BorderLayout());
			this.shape = shape;
			final ShapePanel cur = this;
			JPopupMenu popup = Main.getPopupMenuForShape(shape);
			this.setBackground(Color.WHITE);
			JButton showNhide = new JButton("Hide");
			showNhide.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (shape.isVisible()) {
						shape.setVisible(false);
						showNhide.setText("Show");
					}else {
						shape.setVisible(true);
						showNhide.setText("Hide");
					}
					Main.board.paintShapes();
					showNhide.repaint();
				}
			});
			this.add(showNhide, BorderLayout.EAST);
			this.add(new JLabel(shape.getName()));
			this.addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				@Override
				public void mouseEntered(MouseEvent e) {
					
				}
				@Override
				public void mouseClicked(MouseEvent e) {
					shapeList.setSelection(cur);
					if (e.getButton() == MouseEvent.BUTTON3) {
						popup.show(cur, e.getX(), e.getY());
					}
				}
			});
			this.add(getSmallImage(shape, 30, 30), BorderLayout.WEST);
		}
		public static JPanel getSmallImage(Shape s, int width, int height) {
			BufferedImage bf = new BufferedImage(s.getWidthOnBoard(), s.getHeightOnBoard(), BufferedImage.TYPE_INT_ARGB);
			s.draw(bf.getGraphics());
			BufferedImage display = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = display.getGraphics();
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
			int realWidth;
			int realHeight;
			double ratio;
			if (bf.getWidth() > bf.getHeight()) {
				ratio = ((double)width)/bf.getWidth();
			}else {
				ratio = ((double)height)/bf.getHeight();
			}
			realWidth = (int)(width/ratio);
			realHeight = (int)(height/ratio);
			g.drawImage(Picture.getScaledImage(bf, realWidth, realHeight),
					(width - realWidth)/2 - s.getX(), (height - realHeight)/2 - s.getY(), null);
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel(new ImageIcon(display)));
			return panel;
		}
	}
	public ShapePanel selected = null;
	public ShapeList(Shape[] shapes) {
		super(new ListLayout(0, 3));
		for (int i = shapes.length - 1; i >= 0; i--) {
			ShapePanel sp = (ShapePanel) getGUIforShape(shapes[i]);
			shapePanels.add(sp);
			this.add(sp);
		}
	}
	public JComponent getGUIforShape(Shape shape) {
		ShapePanel shapePanel = new ShapePanel(shape, this);
		return shapePanel;
	}
	public void setSelection(Shape s) {
		for (int i = 0; i < shapePanels.size(); i++) {
			if (s == shapePanels.get(i).shape) {
				setSelection(shapePanels.get(i));
				return;
			}
		}
	}
	public void setSelection(ShapePanel shapePanel) {
		if (selected != null) {
			selected.setBackground(Color.WHITE);
		}
		selected = shapePanel;
		selected.setBackground(Color.CYAN);
		selected.revalidate();
		selected.repaint();
	}
	public Shape getSelectedShape() {
		if (selected == null) {
			return null;
		}
		return selected.shape;
	}
}