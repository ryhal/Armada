import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JPanel;
/* @Author: Yun Suk Chang
 * @Version: 100613
 * 
 * Made for Creating && Testing animation
 * 
 * This class have basics that are needed for animation of moving from one place to another.
 * Implement this to the main panel and change the part that I marked "---default background repaint---"
 * 
 * Please do not alter DynamicAnimationHelper (DAH) except for commenting since the order matters.
 * 
 * Contains: move(int index, int x, int y, int movingTime) - index: DE's id for DAH
 *           overriden paintComponent(Graphics g)
 *           
 */
public class TestPanel extends JPanel {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		
		private int mvTime;//total moving time (includes rotation time) <<very inaccurate
		private int status;//0=draw DE on its coord. 1=rotate DE. 2= move DE to new coord.
		private int moveX;
		private int moveY;
		private double rotationAngle;

		private int deltaX;
		private int deltaY;

		private int index;
		private ArrayList<DynamicAnimationHelper> da;
		
		public TestPanel(){
			da=new ArrayList<DynamicAnimationHelper>();
			mvTime=0;
			status=0;
			moveX=0;
			moveY=0;
			rotationAngle=0;

			deltaX=0;
			deltaY=0;

			index=-1;
		}
		public TestPanel(DynamicElement de){
			da=new ArrayList<DynamicAnimationHelper>();
			da.add( new DynamicAnimationHelper(de));
			de.setIndex(0);
			mvTime=0;
			status=0;
			moveX=0;
			moveY=0;
			rotationAngle=0;

			deltaX=0;
			deltaY=0;

			index=-1;
		}
		public void addDE(DynamicElement de){
			da.add(new DynamicAnimationHelper(de));
			de.setIndex(da.size()-1);
		}
		public void move(int i, int x, int y, int t){
			mvTime=t;
			moveX=x;
			moveY=y;
			status=1;
			rotationAngle=da.get(i).calcRotationAngle(moveX, moveY);
			deltaX=moveX-da.get(i).getDE().getX();
			deltaY=moveY-da.get(i).getDE().getY();
			da.get(i).setAngleLeft(rotationAngle);
			da.get(i).setMoveXLeft(deltaX);
			da.get(i).setMoveYLeft(deltaY);
			index=i;
			while(status!=0)
			{
	        	repaint();
	        	try {
	        		Thread.sleep(10);
	        	} catch (InterruptedException e) {
	        		e.printStackTrace();
	        	}
			}
			double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
			if(angle<0)
				angle=360+angle;
			da.get(i).getDE().setAngle(angle);
			da.get(i).getDE().setX(moveX);
			da.get(i).getDE().setY(moveY);
			da.get(i).setAngleLeft(0);
			da.get(i).setMoveXLeft(0);
			da.get(i).setMoveYLeft(0);
			repaint();
		}
		public void paintComponent(Graphics g){
			//-------default background repaint--------\\
			Graphics2D g2 = (Graphics2D) g;
			Color c= g2.getColor();
			g2.setColor(getBackground());
			g2.fillRect(0,0,this.getWidth(),this.getHeight());
			g2.setColor(c);
			
			//g2.draw((Shape)new Rectangle(600-100, 500-100, 200, 200));
			//------------------------------------------\\
			
			//--------code needed for move()----------------\\
			switch(status){
			case 0:/*
				
				System.out.println("a= "+da.get(index).getAngleLeft()+" deltaA= "+rotationAngle+" angle=" +da.get(index).getDE().getAngle());
				System.out.println("xl= "+da.get(index).getMoveXLeft()+" dx= "+deltaX+" x= "+da.get(index).getDE().getX());
				System.out.println("yl= "+da.get(index).getMoveYLeft()+" dy= "+deltaY+" y= "+da.get(index).getDE().getY());
			*/	break;
			case 1:				
//				System.out.println("a= "+da.get(index).getAngleLeft()+" deltaA= "+rotationAngle+" angle=" +da.get(index).getDE().getAngle());
				if(!da.get(index).rotate(rotationAngle, mvTime, g))
					status=2;
				break;
			case 2:
	//			System.out.println("xl= "+da.get(index).getMoveXLeft()+" dx= "+deltaX+" x= "+da.get(index).getDE().getX());
	//			System.out.println("yl= "+da.get(index).getMoveYLeft()+" dy= "+deltaY+" y= "+da.get(index).getDE().getY());
				if(!da.get(index).moveHelper(deltaX, deltaY, mvTime, g))
					status=0;
				break;
			}
			for(int i=0;i<da.size();i++){
				da.get(i).draw(g);
			}
			
			//------------------------------------------\\
		}
		
		
		public static void main(String[] args) {
			//---Testing---
			
			int x=200;
			int y=200;
			int w=50;
			int h=50;
			double an=45;
			String img="fighter";
			int s=0;
			int all = 1;
			
			DynamicElement de1 = new DynamicElement(x,y,w,h,an,img,0,0,0,s,all,0,0);
			DynamicElement de2 = new DynamicElement(300,100,w,h,90,img,0,0,0,0,2,0,0);
			DynamicElement de3 = new DynamicElement(600,500,w*4,h*4,0,"flagship",0,0,0,0,1,0,0);
			DynamicElement de4 = new DynamicElement(700,400,w*2,h*2,0,"juggernaut",0,0,0,0,1,0,0);
			TestPanel tp = new TestPanel(de1);
			tp.addDE(de2);
			tp.addDE(de3);
			tp.addDE(de4);
			
			JFrame frame = new JFrame("Animation Test");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			int frameSize=800;
			
			frame.getContentPane().add( BorderLayout.CENTER, tp );
			frame.setSize( frameSize, frameSize );
	        frame.setVisible(true);
	        
	        tp.move(0,700,50,500);//<-change this values to test
	        tp.move(1,500,600,300);
	        tp.move(2,100,100,1000);
	        tp.move(3,200,700,800);
	        
		}
}
