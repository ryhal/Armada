package element; 
import view.*;

import java.awt.*;

import animation.AnimationHelper;

/*
 * DynamicElement is a child class that is for Elements that have stats
 * There should be another child for extending a tree into the realm of visuals such as lasers to show an attack but these 
 * visuals cannot be selected
 */
public class Element {
    
	protected int x,y;
	protected int width, height;
	protected double angle; //in degrees, 0 to 360
	//orientation
	protected String image;	//image
	protected static int ID=0;//Element ID
	protected int id;
	protected boolean  targetable = false;
	protected AnimationHelper ah; 
	protected Element owner=null;

	public Element() {
		id=ID;
		ID++;
	}
	
	public Element(int a, int b, int w, int h, String img){
		x = a;
		y = b;
		width = w;
		height = h;
		angle=0;
		image=img;
		id=ID;
		ID++;
		targetable = false;
		ah = new AnimationHelper(this);
	}
	public Element(int a, int b, int w, int h,double an,String img){
		x = a;
		y = b;
		width = w;
		height = h;
		angle=an;
		image=img;
		id=ID;
		ID++;
		targetable = false;
		ah = new AnimationHelper(this);
	}
	
	/*
	 * this method is intended to be a script.  It will change the orientation of the Element and location of the Element over a few
	 * seconds in order to show it moving as an animation 
	 */
	public void move(int inX, int inY){
		ah.moveTo(inX,inY);
	}
	
	
	public void draw(Graphics g, BoundingRectangle viewRegion) {
		ah.draw(g, viewRegion);
		//g.fillRect(x+viewRegion.getX(),y+viewRegion.getY(), width, height);
	}
	
	public boolean isIn(int inX, int inY){
		return false;
	}
	
	public Menu getMenu(){
		return new Menu();
	}
	
	public void setLocation(int Xin, int Yin){
		x=Xin;
		y=Yin;
	}
	public boolean isTargetable() {
		return targetable;
	}

	public void setTargetable(boolean t) {
		targetable = t;
	}
	public AnimationHelper getAH(){
		return ah;
	}
	public Element getOwner(){
		return owner;
	}
	public void setOwner(Element e){
		owner=e;
	}
	public int getX() {
		return x;
	}

	public void setX(int a) {
	    x = a;
	}
	
	public int getY() {
		return y;
	}

	public void setY(int b) {
		y = b;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int w) {
		width = w;
		ah.setImageWidth(w);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int h) {
		height = h;
		ah.setImageHeight(h);
	}
	
	public double getAngle(){
		return angle;
	}
	
	public void setAngle(double a){
		angle=a;
	}
	
	public String getImage(){
		return image;
	}
	public void setImage(String img){
		image=img;
	}
	public int getID(){
		return id;
	}
	public void setID(int i){
		id=i;
	}

}
