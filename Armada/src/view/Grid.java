package view;
import av.audio.SoundEffect;





/* BasicStroke, Color, Cursor, FontMetrics, Graphics, Graphics2D, Stroke */
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.util.*;
import java.io.*;

import javax.imageio.ImageIO;

import view.controller.GameController;
import element.DynamicElement;
import element.Element;
import element.Explosion;
import element.Teleporter;
import element.planet.HomePlanet;
import element.planet.Planet;
import element.ship.CargoShip;
import element.ship.Ship;
import game.ArmadaEngine;
import game.ArmadaEngine.AttackStatus;
import game.ArmadaEngine.MovementStatus;
import game.player.Player;
import game.player.PlayerManager;

/**
  Grid paints, moves, stores, and keeps track of everything visual on the panel
  ie, ships, planets, everything below the frame/menus and above the background
*/
public class Grid extends ViewLayer {

   // static final int GRID_WIDTH = 38400; // The width of the grid in pixels.
    //static final int GRID_HEIGHT = 21600; // The height of the grid in pixels.
	private static final int GRID_WIDTH = ArmadaEngine.GRID_WIDTH;
    private static final int GRID_HEIGHT = ArmadaEngine.GRID_HEIGHT;
	private ArmadaEngine engine;
	
    private ArrayList<Element> elements;
    private ArrayList<DynamicElement> delements;
    
    private long startTime = -1;
    private float fade = 1.0f;
    private int mode = 1, spawnPrice;
    private int index = 0;
    private DynamicElement activeE, toSpawn;
    private GameController gc = null;
    private DynamicSize dsb;
    private BoundingRectangle viewRegion = null; //The entire grid is 2000 by 2000 pixels. This is the region that the user sees.
    
    // Mouse coordinate information
    private int currentX = 0;
    private int currentY = 0;
    
    /**
        The only constructor
        @param ap The ArmadaPanel object that this grid will be inside of.
    */
    public Grid(GameController gc) {
        super(new BoundingRectangle(0,0,10000,10000));
        this.gc = gc;
        engine = gc.getEngine();
        this.dsb = gc.getViewSize();
        viewRegion = gc.getViewRegion();
    	elements = new ArrayList<Element>();
    	delements = engine.getDynamicElements();
    	SoundEffect.init();
    	SoundEffect.volume=SoundEffect.Volume.LOW;
    	
    }
    
    /**
        Moves the viewing region. Stops at boundaries.
        @param x The number of pixels to move in the x direction.
        @param y The number of pixels to move in the y direction.
    */
    public void moveViewRegion(int x, int y) {
        gc.moveViewRegion(x, y);
    }
    
    /**
        @return The rectangular portion of the overall grid that is being displayed.
    */
    public BoundingRectangle getViewRegion() {
        return gc.getViewRegion();
    }
    
    public void moveViewRegionToPoint(int x, int y) {
        gc.moveViewRegionToPoint(x, y);
    }
    
    /**
        Enables ships to move extremely far. Intended for debugging purposes.
    */
	public void moveCheat() { 
	    engine.enableDebugSpeed(); 
	    gc.showInfo("Move Cheat Enabled");
	}
	
	//does not deselect ship.  use setMode(0) to do that 
	public void cancelMove() { setMode(0);}
	
	/**
	    Set the selected dynamic element to the next allowed selection.
	*/
	public void cycleActiveDE() {
		if(delements == null)return;
		if(delements.size() <=0) return;
		if(index >= delements.size()) index=0;
		int initialIndex = index;
		DynamicElement temp = delements.get(index);
		do{
			index++;
			if(index==initialIndex){//if came full circle
				return;
			}
			if(delements.size() <= index){//if hit end of list
				index=0;
			}
			temp=delements.get(index);
			//if(temp.getAlliance() == turn)break;
		}while(temp.getAlliance()!=getTurn() || !temp.isTargetable());
		
		activeE=temp;
		viewRegion.setCenter(activeE.getX(), activeE.getY());
	}
	
	/**
	    Change the action mode to the next mode.
	*/
	public void nextMode(){
		int temp = mode;
		temp++;
		if(temp > 5){
			temp=1;
		}
		this.setMode(temp);
	}
	
	/**
	    @return The current x coordinate of the mouse cursor.
	*/
	public int getCurrentX(){
		return currentX;
	}
	
	/**
	    @return The current y coordinate of the mouse cursor. 
	*/
	public int getCurrentY(){
		return currentY;
	}
	
	public void refresh(long previousTime, long currentTime) {
	    if (startTime < 0) startTime = currentTime;
	    if ((currentTime - startTime) < 1000) {
	        fade = 1-((currentTime - startTime) / 1000.0f);
	    }
		viewRegion.setWidth(dsb.getWidth());
		viewRegion.setHeight(dsb.getHeight());
		if(activeE!=null && activeE.isDead()){
			activeE=null;
		}
		
		int vel = 50;
		if (!gc.mouseIsOnScreen()) return;
		if (!(currentX == 0) || !(currentY == 0)) {
			if(dsb.getWidth() * .03 > currentX){
				if(viewRegion.getX() >=25){
					viewRegion.setX(viewRegion.getX() - vel);	
				}
		    }
			if(dsb.getWidth() * .97 < currentX){
		    	if(Grid.getGridWidth() - (viewRegion.getX()+viewRegion.getWidth()) >=25){
					viewRegion.setX(viewRegion.getX() + vel);	
				}
		    }
			if(dsb.getHeight() * .03 > currentY){
		    	if(viewRegion.getY() >=25){
					viewRegion.setY(viewRegion.getY() - vel);	
				}
		    }
			if(dsb.getHeight() * .97 < currentY){
		    	if(Grid.getGridHeight() - (viewRegion.getY() + viewRegion.getHeight()) >=25){
					viewRegion.setY(viewRegion.getY() + vel);	
				}
		    }	
		}
	}
	
	/**
	 * @param e1 DynamicElement who's range is used for calculation
	 * @param e2 DynamicElement which is calculated to be within range of DynamicElement e1
	 */
	public boolean inRange(DynamicElement e1, DynamicElement e2){
		return e1.getRange() > e1.distance(e2);
	}
	
	/**
	    Deselect the selected dynamic element.
	*/
	public void unselect(){
		activeE=null;
		//mode=1;
	}
	
	/**
	    Set the action mode.
	    @param i the mode. Could be a value from 1 to 4.
	*/
	public void setMode(int i){
		mode=i;
		if(mode==0){
			activeE=null;
		}
		if (mode == 0) {
	        //ap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    } else {
	        //ap.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	    }
	    gc.modeChanged();
	}
	
	public void mouseMoved(int x, int y) {
	    currentX = x;
	    currentY = y;
	}
	
	public void update() {
		
		
		if(delements != null) {
			ArrayList<DynamicElement> removeDE = new ArrayList<DynamicElement>();
			for (int i=0;i<delements.size();i++) {
				if(delements.get(i).isDead()){
					SoundEffect.EXPLODE.play();
					if((double)Math.random() >= (double)0.9){//10% chance of playing the scream
					    SoundEffect.SCREAM.play();
					}   
					elements.add(new Explosion(delements.get(i)));
					removeDE.add(delements.get(i));
				}
				
			}
			delements.removeAll(removeDE);
			removeDE.clear();
			ArrayList<Element> removeList = new ArrayList<Element>();
			for(int i=0; i<elements.size(); i++){
				if(elements.get(i) instanceof Explosion){
					if(((Explosion)(elements.get(i))).isDone()){
					    removeList.add(elements.get(i));
					}
				}
				if(elements.get(i) instanceof Teleporter){
					if(((Teleporter)(elements.get(i))).isDone()){
					    removeList.add(elements.get(i));
					}
				}
			}
			elements.removeAll(removeList);
			removeList.clear();
		}
		
	}
	
	public boolean click(int inX, int inY){
		
	    if((mode == 0 || activeE==null) && delements != null && delements.size() != 0) { //selecting a ship
			inX += viewRegion.getX(); inY += viewRegion.getY();
			for (DynamicElement d : delements) {
			    if(d.isIn(inX,inY) && d.isTargetable()) {
				    //mode = 1;
				    activeE=d;
				    activeE.update();
			        return true;
			    }
			}
		}
	      
		if(activeE==null || !activeE.isTargetable()) {
			return true;
		}
		if(activeE.getAlliance()!=getTurn()){
			gc.invalidMoveAttempt("Can only command units under your control");
			return true;
		}	
		if(mode == 1) { //move
			inX += viewRegion.getX(); inY += viewRegion.getY();
			handleMovementStatus(engine.moveDynamicElement(activeE, inX, inY));
			return true;
		}
		if(mode == 2) { //attack hull
			if(delements != null && delements.size() != 0 && activeE.canAttack()){
				inX += viewRegion.getX(); inY += viewRegion.getY();
				handleAttackStatus(engine.attackHull(activeE, inX, inY));
		    }
			else if (!activeE.canAttack()){
				gc.invalidMoveAttempt("Cannot attack again this turn");
			}
		    return true;
		}
		if(mode == 3) { //attack engine
			if(delements != null && delements.size() != 0 && activeE.canAttack()){
				inX += viewRegion.getX(); inY += viewRegion.getY();
				handleAttackStatus(engine.attackEngine(activeE, inX, inY));
			}
			else if (!activeE.canAttack()){
				gc.invalidMoveAttempt("Cannot attack again this turn");
			}
		}
		if(mode == 4) {
			//docking
			if(delements != null && delements.size() != 0){
				inX += viewRegion.getX(); inY += viewRegion.getY();
				engine.dock(activeE, inX, inY);
			}
			return true;
		}
		
		if (mode == 5) {
		    if(activeE instanceof HomePlanet && activeE.getAlliance()==this.getTurn()){
		    	if(toSpawn!= null){
		    		toSpawn.setX(inX+this.viewRegion.getX());
		    		toSpawn.setY(inY+this.viewRegion.getY());
		    		if(toSpawn.distanceFrom(activeE) > (toSpawn.getWidth()/2 + activeE.getWidth()/2) && toSpawn.distanceFrom(activeE) < activeE.getWidth()/2 + Ship.getDockRange()){
		    			for(DynamicElement d: delements){
		    				if(d.distanceFrom(toSpawn) < d.getWidth()/2+toSpawn.getWidth()/2){
		    					gc.userError("Cannot spawn: Something is in the way.");
		    					return false;
		    				}
		    			}
		    			if(!engine.getPlayerManager().canPay(activeE.getAlliance(), spawnPrice)){
		    				gc.userError("Cannot spawn: Insufficient Funds");
		    				return false;
		    			}
		    			engine.getPlayerManager().playerPays(activeE.getAlliance(), spawnPrice);
		    			engine.add(toSpawn);
		    			System.out.println(delements.size());
		    			//System.out.println(toSpawn.getX() + " " + toSpawn.getY() + " " + toSpawn.getWidth() + " " + toSpawn.getHeight());
		    			toSpawn=null;
		    			spawnPrice=0;
		    			mode=1;
		    		}
		    	}
		    }
		    return true;
		}
		/*
		//teleporting
		if(mode ==5){
			if(delements != null && delements.size() != 0){
				inX += viewRegion.getX(); inY += viewRegion.getY();
				if(activeE instanceof Ship){
					((Ship)(activeE)).setTeleporting(true);
					elements.add(new Teleporter((Ship)(activeE), inX,inY));
				}
				
			}
			return true;
		}
		*/
		return false;
	}
	
	public void prepareSpawn(DynamicElement de, int price){
		toSpawn=de;
		mode=5;
		spawnPrice=price;
	}
	
	private void handleMovementStatus(ArmadaEngine.MovementStatus status) {
	    
	    if (status == ArmadaEngine.MovementStatus.SUCCESS) {
	        if (mode != 5) gc.showInfo("Moving Ship");
	        else gc.showInfo("Moving Fleet");
	    }
	    if (status == ArmadaEngine.MovementStatus.RANGE) gc.invalidMoveAttempt("Out of Range!");
	    if (status == ArmadaEngine.MovementStatus.OBJECT_IN_PATH) gc.invalidMoveAttempt("Path obstructed!");
	    if (status == ArmadaEngine.MovementStatus.CANNOT_MOVE_PLANET) gc.invalidMoveAttempt("Planets cannot move!");
	    if (status == ArmadaEngine.MovementStatus.UNKNOWN_FAILURE) gc.invalidMoveAttempt("Invalid Move!");
	    if (status == ArmadaEngine.MovementStatus.NOT_TURN) gc.invalidMoveAttempt("It is not your turn");
	}
	
	private void handleAttackStatus(ArmadaEngine.AttackStatus status) {
	    if (status == ArmadaEngine.AttackStatus.SUCCESS) gc.showInfo("Attacking Target");
	    if (status == ArmadaEngine.AttackStatus.RANGE) gc.invalidMoveAttempt("Target out of range!");
	    if (status == ArmadaEngine.AttackStatus.BAD_TARGET) gc.invalidMoveAttempt("Target out of range!");
	    if (status == ArmadaEngine.AttackStatus.UNKNOWN_FAILURE) gc.invalidMoveAttempt("Cannot attack this!");
	}
	
	public void turnTimedOut() {
	    activeE = null;
	    mode = 1;
	}
	
	public void toggleTurn() {
	    gc.newTurn();
	    //setMode(0);
	    engine.toggleTurn();
	    activeE = null;
	    mode = 1;		
	}
	
	/* draws everything on the Grid */
	public void draw(Graphics g){
	    drawAllDelements(g); 
	    Color c = g.getColor();
	    if (fade < 0.0001) fade = 0.0f;
	    if (fade > 0.9999) fade = 1.0f;
	    g.setColor(new Color(0.0f, 0.0f, 0.0f, fade));
	    g.fillRect(0, 0, dsb.getWidth(), dsb.getHeight());
	    
	    g.setColor(Color.GRAY);
	    if (activeE != null)
	        //g.drawOval(activeE.getX(), activeE.getY(), activeE.getWidth(), activeE.getHeight());
	    g.setColor(c);
	    
	} 
	
	private void drawAllDelements(Graphics g){
		if(delements != null){
			
			
			for(Element e:elements){
			    e.draw(g, viewRegion);
			}
			
			//draws DE's in order
			for (DynamicElement de : delements) {
				if(de instanceof Planet){
					de.draw(g, viewRegion);
				}
			}
			for (DynamicElement de : delements) {
				if(de instanceof Ship && de.getAlliance() != engine.getTurn()){
					de.draw(g, viewRegion);
				}
			}
			for (DynamicElement de : delements) {
				if(de instanceof Ship && de.getAlliance() == engine.getTurn()){
					de.draw(g, viewRegion);
				}
			}
			//End drawing DE's
			
			
		}
			
			
	}
	
	public PlayerManager getPlayerManager(){
		return engine.getPlayerManager();
	}
	
	public Player getWinner(){
		return getPlayerManager().getWinner();
	}
	
	public Player getLoser(){
		return getPlayerManager().getLoser();
	}
    
    public double secondsRemainingForTurn() {
        return engine.secondsRemainingForTurn();
    }
    
    public double maxSecondsForTurn() {
        return engine.maxSecondsForTurn();
    }
	
	public void add(DynamicElement inDE){
		delements.add(inDE);
	}
	
	public DynamicElement getActiveE() {
		return activeE;
	}
	
	/**
	Get the current mode as an int.
	*/
	public int getMode() {
		return mode;
	}
	
	/**
	Get a string that describes the current mode.
	*/
	public String getModeString() {
	    if (mode == 0) return "No Selection";
	    if (mode == 1) return "Move";
	    if (mode == 2) return "Attack Hull";
	    if (mode == 3) return "Attack Engine";
	    if (mode == 4) return "Dock";
	    if (mode == 5) return "Move Fleet";
	    return "Unknown Mode";
	}
	
	public int getTurn() {
		return engine.getTurn();
	}
	public ArrayList<DynamicElement> getDelements() {
		return delements;
	}
	/*
	public ArmadaPanel getAp() {
		return ap;
	}
	*/
	public DynamicSize getAp() {
		return this.dsb;
	}
	
	public int getWidth(){
		return getGridWidth();
	}
	public int getHeight(){
		return getGridHeight();
	}

	public void setCurrentX(int currentX) {
		this.currentX = currentX;
	}

	public void setCurrentY(int currentY) {
		this.currentY = currentY;
	}

	public static int getGridHeight() {
		return GRID_HEIGHT;
	}

	public static int getGridWidth() {
		return GRID_WIDTH;
	}

}
//this.ap = ap;
//private ArmadaPanel ap;
//delements = new ArrayList<DynamicElement>();

/*
        viewRegion.setX(viewRegion.getX()+x);
        viewRegion.setY(viewRegion.getY()+y);
        if (viewRegion.getX() < 0) viewRegion.setX(0);
        if (viewRegion.getY() < 0) viewRegion.setY(0);
        if (viewRegion.getX() + dsb.getWidth() > GRID_WIDTH) {
            viewRegion.setX(GRID_WIDTH - dsb.getWidth());
        }
        if (viewRegion.getY() + dsb.getHeight() > GRID_HEIGHT) {
            viewRegion.setY(GRID_HEIGHT - dsb.getHeight());
        }
        
        */

