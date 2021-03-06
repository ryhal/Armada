package element; 
import view.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;

import element.ship.Ship;
import animation.AnimationHelper;

/**
	Creates a teleportation animation.
*/

public class Teleporter extends Element{
	/** Establishes the string that will represent the teleporter image. */
	private static final String IMAGE_NAME="teleporter";
	private static final int NUM_FRAME=8;
	/** Establishes the width of the teleporter animation. */
	private static final int WIDTH=140;
	/** Establishes the height of the teleporter animation. */
	private static final int HEIGHT=140;
	
	private int imgIndex=0;
	private Ship s;
	private int destX,destY,w,h,mode;
	private BufferedImage backup;
	/**
	 * Creates Teleporter
	 * @param d Ship that is getting teleported
	 * @param dX x destination 
	 * @param dY y destination
	 */
	public Teleporter(Ship d, int dX,int dY){
		super(d.getX(),d.getY(),WIDTH*NUM_FRAME,HEIGHT,d.getAngle(),IMAGE_NAME);
		mode=0;
		s=d;
		s.setTargetable(false);
		destX=dX;
		destY=dY;
		w=(int)(d.getWidth()*1.5);
		h=(int)(d.getHeight()*1.5);
		backup=s.getAH().getImage();
	}
	/**
	 * Creates Teleporter
	 * @param d Ship that is getting teleported
	 * @param dX x destination 
	 * @param dY y destination
	 * @param mode mode for teleportation animation (0 for disappearing and appearing, 1 for just appearing)
	 */
	public Teleporter(Ship d, int dX,int dY,int mode){
		super(d.getX(),d.getY(),WIDTH*NUM_FRAME,HEIGHT,d.getAngle(),IMAGE_NAME);
		this.mode=mode;
		s=d;
		destX=dX;
		destY=dY;
		s.setTargetable(false);
		if(mode==1)
			imgIndex=NUM_FRAME-1;
		w=(int)(d.getWidth()*1.5);
		h=(int)(d.getHeight()*1.5);
	}
	/**
	 * does teleportation animation
	 */
	public void draw(Graphics g, BoundingRectangle viewRegion){
		BufferedImage temp= ah.getImage().getSubimage(WIDTH*(imgIndex/2), 0, WIDTH, HEIGHT);
		RescaleOp rop=null;
		if(mode==0){
			float[] scales = { 1f, 1f, 1f, 1f-1f/(NUM_FRAME)*(imgIndex+1)/2 };
			float[] offsets = new float[4];
			 rop= new RescaleOp(scales, offsets, null);
			imgIndex++;
		}
			
		else if(mode ==1){
			float[] scales = { 1f, 1f, 1f, 1f/(NUM_FRAME)*(NUM_FRAME-(imgIndex/2.0f)) };
			float[] offsets = new float[4];
			 rop = new RescaleOp(scales, offsets, null);
			imgIndex--;
		}
		if(rop==null)
			System.out.println("transparency fail!!");
		else
			s.getAH().setImage(rop.filter(backup, null));
		AnimationHelper.draw(x, y, w, h, angle, temp, g, viewRegion);
	}
	/**
	 * checks if teleportation is done
	 * @return <code>true</code> if teleportation is done
	 * 			<code>false</code> otherwise
	 */
	public boolean isDone(){
		if(mode==0&&imgIndex>=NUM_FRAME*2){
			mode=1;
			imgIndex=2*(NUM_FRAME-1);
			s.setX(destX);
			s.setY(destY);
			x=destX;
			y=destY;
			return false;
		}
		else if(mode==1&&imgIndex<0){
			s.setTargetable(true);
			return true;
		}
		else
			return false;
			
	}
}
