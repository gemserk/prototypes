package com.gemserk.prototypes.launcher;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.gemserk.prototypes.Launcher;

public class MultipleCanvasApplication {

	protected static final Logger logger = LoggerFactory.getLogger(MultipleCanvasApplication.class);
	
	public static void main(String[] argv) {
		
		Canvas canvas = new Canvas() {
			private LwjglApplication application;

			public final void addNotify() {
				super.addNotify();

				Launcher launcher = new Launcher() {
					@Override
					public void create() {
						Gdx.graphics.setVSync(true);
						super.create();
					};
				};

				application = new LwjglApplication(new Launcher(), false, this);
			}

			public final void removeNotify() {
				application.stop();
				super.removeNotify();
			}
			
			{
//				addPropertyChangeListener(new PropertyChangeListener() {
//					
//					@Override
//					public void propertyChange(PropertyChangeEvent evt) {
//						System.out.println(evt.getPropertyName() + " changed!");
//					}
//				});
				
				addComponentListener(new ComponentAdapter() {
					@Override
					public void componentResized(ComponentEvent e) {
						
					}
				});
			}
			
		};
		
		canvas.setSize(640, 480);
		
		Canvas canvas2 = new Canvas() { 
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				g.setColor(Color.red);
				g.fillOval(10, 10, 40, 40);
				
			}
		};
		canvas2.setSize(320, 240);
		canvas2.setLocation(40, 40);
		
		JFrame jFrame = new JFrame();
		jFrame.setMinimumSize(new Dimension(320, 240));
		jFrame.setMaximumSize(new Dimension(800, 600));
		jFrame.setSize(640, 480);
		jFrame.setLayout(new BorderLayout());
		jFrame.add(canvas);
//		jFrame.add(canvas2);
		
		jFrame.validate();
		
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jFrame.setVisible(true);
		
		jFrame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				
			}
		});
		
//		new LwjglApplication(new MyApp(), false, canvas);

	}

}
