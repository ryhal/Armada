package view.hud; 
import view.*;
import view.controller.GameController;
import view.hud.HUD.Position;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import element.DynamicElement;
import element.ship.Ship;


public class MapHUD extends HUD{

    public enum Scale {
        SMALL, LARGE;
    }
    
    private Scale mapScale = Scale.SMALL;
	
	private ArrayList<DynamicElement> des;
	static final double DEFAULT_SCALE = .2;
	private double scale = DEFAULT_SCALE;
	private boolean expanding = false;
	private boolean shrinking = false;
	public static final int SCALING_TIME = 3000; // in milliseconds
	private int inputLocation=4;
	
	private Grid grid = null;
	
	public MapHUD(Grid gr, GameController gc, Position p){
		super(new BoundingRectangle(0,0,250,125),gc);
		grid = gr;
		//inputLocation=l;
	    //location = l;
	    this.position = p;
		des = grid.getDelements();
		setName("Map Layer");
	}
	
	/**
	 * Moves the location of what is displayed, determined by where on the MapHUD the click is.  Returns false if the click is off the map
	 */
    public boolean click(int inX, int inY){
		if(r.isIn(inX, inY)){
		    //System.out.println("You clicked the map");
			moveMap(inX,inY);
			return true;
		}
		return false;
	}
	
	public void draw(Graphics g){
	    r.setWidth((int)(grid.getAp().getWidth() * scale));
	    r.setHeight((int)(grid.getAp().getHeight() * scale));
	    r.setX(grid.getAp().getWidth() - r.getWidth() - 10);
	    r.setY(grid.getAp().getHeight() - r.getHeight() - 10);
	    updateLocation();
	    int x = r.getX();
	    int y = r.getY();
	    int width = r.getWidth();
	    int height = r.getHeight();
		g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
		g.fillRect(x, y, width, height);
		int insetWidth = (int)((grid.getViewRegion().getWidth() / (float)grid.getWidth())*(width*1.0));
		int insetHeight = (int)((grid.getViewRegion().getHeight() / (float)grid.getHeight())*(height*1.0));
		double dxf = (width*1.0)*(grid.getViewRegion().getX()/(grid.getWidth()*1.0));
		double dyf = (height*1.0)*(grid.getViewRegion().getY()/(grid.getHeight()*1.0));
		des=grid.getDelements();
		if(des == null)return;
		DynamicElement temp;
		synchronized(des){
			for(int i=0; i<des.size(); i++){
				temp=des.get(i);
				if(temp.getAlliance() == 1){
					g.setColor(Color.RED);
				}
				else if(temp.getAlliance() == 2){
					g.setColor(Color.BLUE);
				}
				else{
					g.setColor(Color.GREEN);
				}
				if (temp instanceof Ship) {
				    g.fillRect(x + (int)(((temp.getX()-temp.getWidth()/2)/ (float)grid.getWidth())*(width*1.0)), y + (int)(((temp.getY()-temp.getHeight()/2)/ (float)grid.getHeight())*(height*1.0)), 2+(int)((temp.getWidth()/ (float)grid.getWidth())*(width*1.0)),2+ (int)((temp.getHeight()/ (float)grid.getHeight())*(height*1.0)));
				    //drawRange(g);//abandoned until I can find a way to make it so that if the range is outside the map it doesn't show
				} else {
				    g.fillOval(x + (int)(((temp.getX()-temp.getWidth()/2)/ (float)grid.getWidth())*(width*1.0)), y + (int)(((temp.getY()-temp.getHeight()/2)/ (float)grid.getHeight())*(height*1.0)), 2+(int)((temp.getWidth()/ (float)grid.getWidth())*(width*1.0)),2+ (int)((temp.getHeight()/ (float)grid.getHeight())*(height*1.0)));
				}
			}
		}
		
		int dx = (int)dxf; int dy = (int)dyf;
		g.setColor(Color.WHITE);
		g.drawRect(x+dx, y+dy, insetWidth, insetHeight);
		g.drawRect(x-1, y-1, width+1, height+1);
		updateLocation();
		if (scale > 0.79) {
		    g.drawLine(x + (int)(width / 2.0f), y, x + (int)(width / 2.0f), y + height);
		    g.drawLine(x, y + (int)(height / 2.0f), x + width, y + (int)(height / 2.0f));
		    int SCALE_LENGTH = 75;
		    int SCALE_HEIGHT = 16;
		    g.drawLine(x + width - SCALE_LENGTH - 30, y + height - 30, x + width - 30, y + height - 30);
		    g.drawLine(x + width - SCALE_LENGTH - 30, y + height - 30 - (int)(SCALE_HEIGHT / 2.0f), x + width - SCALE_LENGTH - 30, y + height - 30 + (int)(SCALE_HEIGHT / 2.0f));
		    g.drawLine(x + width - 30, y + height - 30 - (int)(SCALE_HEIGHT / 2.0f), x + width - 30, y + height - 30 + (int)(SCALE_HEIGHT / 2.0f));
		    g.drawString("1000 km", x + width - SCALE_LENGTH - 25, y + height - 40);//I don't think we should have a unit, the scale of our game is ridiculous
		    g.drawString("II", x + 10, y + 20);
		    g.drawString("I", x + (int)(width / 2.0f) + 10, y + 20);
		    g.drawString("III", x + 10, y + (int)(height / 2.0f) + 20);
		    g.drawString("IV", x + (int)(width / 2.0f) + 10, y + (int)(height / 2.0f)+20);
		}
		
	}
	
	/**
	 * Moves the displayed location depending on input click
	 * @param inX X position of click
	 * @param inY Y position of click
	 */
	public void moveMap(int inX, int inY){
	/*
	    inX += 10;
	    inY += 10;
	    */
		int newX = inX-r.x;
		int newY = inY-r.y;
	    double wPerc = (double)r.width/Grid.getGridWidth();
	    double hPerc = (double)r.height/Grid.getGridHeight();
		int xx=(int)(((double)newX)/wPerc)-grid.getAp().getWidth()/2;
		int yy=(int)(((double)newY)/hPerc) -grid.getAp().getHeight()/2;
		if(xx < 0) xx = 0;
		if(yy < 0) yy = 0;
		if(xx > Grid.getGridWidth()-grid.getAp().getWidth()) xx = Grid.getGridWidth()-grid.getAp().getWidth();
		if(yy > Grid.getGridHeight()-grid.getAp().getHeight()) yy = Grid.getGridHeight()-grid.getAp().getHeight();
		grid.moveViewRegionToPoint(xx, yy);
		//grid.getViewRegion().setX(xx);
		//grid.getViewRegion().setY(yy);
	}
	
	/**
	 * Returns true if the input position is within this HUD
	 * @param inX X position of click
	 * @param inY Y position of click
	 */
	public boolean isIn(int inX, int inY){
		return r.isIn(inX, inY);
	}
	
	
	/**
	 * Handles the changing size of the MapHUD if it is expanding or shrinking
	 */
	public void refresh(long previousTime, long currentTime) {
	    
	    super.refresh(previousTime, currentTime);
	    int delta = (int)(currentTime - previousTime);
	    float step = ( delta / (SCALING_TIME * 1.0f) ) * 8.0f;
	    if (shrinking) {
	    	position = HUD.Position.BOTTOM_RIGHT;
	    	//System.out.println("1: " + location);
	        scale -= step;
	        if (scale < DEFAULT_SCALE) {
	            scale = DEFAULT_SCALE;
	            shrinking = false;    
	        }
	        
	    }
	    if (expanding) {
	        position = HUD.Position.CENTERED;
	        scale += step;
	        if (scale > .8) {
	            scale = .8;
	            expanding = false;
	        }
	    }
	}

	/**
	 * Switches between shrinking/expanding
	 */
	public void toggleScale(){
	    if (shrinking || expanding) return;
		if(scale > 0.5){
		    
		    shrinking = true;
		    mapScale = Scale.SMALL;
			//scale = MapHUD.DEFAULT_SCALE;
		} else {
		    expanding = true;
		    mapScale = Scale.LARGE;
		    //scale = 1;
		}
	}
	
	/**
	 * returns current map scale
	 * @return mapScale
	 */
	public Scale getScaleType() {
	    return mapScale;
	}
	
	/**
	 * unused
	 * @param g input Graphics
	 */
	public void drawRange(Graphics g){//abandoned until I can find a way to make it so that if the range is outside the map it doesn't show
		if(grid.getActiveE()==null)return;
		Ship temp;
		if(grid.getActiveE() instanceof Ship){
			temp=(Ship)grid.getActiveE();
		}
		else return;
		int x = r.getX();
	    int y = r.getY();
	    int width = r.getWidth();
	    int height = r.getHeight();
		Color old = g.getColor();
		
		g.setColor(new Color(255,255,255,20));
		if(grid.getMode()==1){
			   g.fillOval(x + (int)(((temp.getX()-temp.getSpeed())/ (float)grid.getWidth())*(width*1.0)), y + (int)(((temp.getY()-temp.getSpeed())/ (float)grid.getHeight())*(height*1.0)), 2+(int)((temp.getSpeed()*2/ (float)grid.getWidth())*(width*1.0)),2+ (int)((temp.getSpeed()*2/ (float)grid.getHeight())*(height*1.0)));
			//g.fillOval(x + (int)(((s.getX()-s.getRange())/ (float)grid.getWidth())*(width*1.0)), y + (int)(((s.getY()-s.getRange())/ (float)grid.getHeight())*(height*1.0)), 2+(int)(((s.getRange()*2)/ (float)grid.getWidth())*(width*1.0)),(int)(((s.getRange()*4)/ (float)grid.getHeight())*(height*1.0)));
		}
		g.setColor(old);
		
	}
	
	/***
	 * returns scale
	 * @return scale
	 */
	public double getScale() {
		return scale;
	}
	/**
	 * sets scale
	 * @param scale The double to set scale to
	 */
	public void setScale(double scale) {
		this.scale = scale;
	}
}
