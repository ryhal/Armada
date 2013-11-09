
/* BasicStroke, Color, Cursor, FontMetrics, Graphics, Graphics2D, Stroke */
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;

/**
  Grid paints, moves, stores, and keeps track of everything visual on the panel
  ie, ships, planets, everything below the frame/menus and above the background
*/
public class Grid extends ViewLayer {

    static final int GRID_WIDTH = 38400; // The width of the grid in pixels.
    static final int GRID_HEIGHT = 21600; // The height of the grid in pixels.
	
	private ArmadaEngine engine = new ArmadaEngine();
	
    private ArrayList<Element> elements;
    private ArrayList<DynamicElement> delements;
    private  ArrayList<Menu> menus;
    
    private PlayerManager pm;
    private  int mode = 0, turn = 1, index = 0;
    private  DynamicElement activeE;
    private ArmadaPanel ap;
    private BoundingRectangle viewRegion = new BoundingRectangle(0, 0, 500,500); //The entire grid is 2000 by 2000 pixels. This is the region that the user sees.
    
    // Mouse coordinate information
    private int currentX = 0;
    private int currentY = 0;
    
    // Information for computing time remaining for current turn
    private static final double TURN_TIME = 180000.0;
    private double mseconds = TURN_TIME;
    private long lastTime = 0;
    
    /**
        The only constructor
        @param ap The ArmadaPanel object that this grid will be inside of.
    */
    public Grid(ArmadaPanel ap) {
        super(new BoundingRectangle(0,0,10000,10000));
        
        this.ap = ap;
    	elements = new ArrayList<Element>();
    	delements = new ArrayList<DynamicElement>();
    	menus = new ArrayList<Menu>();
    	SoundEffect.init();
    	SoundEffect.volume=SoundEffect.Volume.LOW;
    	pm = new PlayerManager(this);
    	
    	// Add some ships
		delements.add(new NormalShip(750,330,1));
		delements.add(new NormalShip(160,330,1));
		delements.add(new NormalShip(260,330,2));
		delements.add(new NormalShip(60,330,2));
		delements.add(new NormalShip(220,330,2));
		delements.add(new NormalShip(300,330,2));
		Spawner.spawnPlanets(this, 40);
    }
    
    /**
        Moves the viewing region. Stops at boundaries.
        @param x The number of pixels to move in the x direction.
        @param y The number of pixels to move in the y direction.
    */
    public void moveViewRegion(int x, int y) {
        viewRegion.setX(viewRegion.getX()+x);
        viewRegion.setY(viewRegion.getY()+y);
        if (viewRegion.getX() < 0) viewRegion.setX(0);
        if (viewRegion.getY() < 0) viewRegion.setY(0);
        if (viewRegion.getX() + ap.getWidth() > GRID_WIDTH) {
            viewRegion.setX(GRID_WIDTH - ap.getWidth());
        }
        if (viewRegion.getY() + ap.getHeight() > GRID_HEIGHT) {
            viewRegion.setY(GRID_HEIGHT - ap.getHeight());
        }
    }
    
    /**
        @return The rectangular portion of the overall grid that is being displayed.
    */
    public BoundingRectangle getViewRegion() {
        return viewRegion;
    }
    
	public void cancelMove() {//does not deselect ship.  use setMode(0) to do that 
	    mode = 0;
	}
    
    /**
        Enables ships to move extremely far. Intended for debugging purposes.
    */
	public void moveCheat(){
		for(DynamicElement d: delements){
			d.setSpeed(99999999);
		}
	}
	
	/**
	    Set the selected dynamic element to the next allowed selection.
	*/
	public void selectNextDEThisTurn(){
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
		}while(temp.getAlliance()!=turn || !temp.isTargetable());
		
		activeE=temp;
		viewRegion.setCenter(activeE.getX(), activeE.getY());
	}
	
	/**
	    Change the action mode to the next mode.
	*/
	public void nextMode(){
		mode++;
		if(mode > 4){
			mode=1;
		}
		System.out.println("mode: " + mode);
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
	
	public void updateViewRegion(){
		viewRegion.setWidth(ap.getWidth());
		viewRegion.setHeight(ap.getHeight());
		if (lastTime == 0) {
		    lastTime = new GregorianCalendar().getTimeInMillis();
		} else {
		    long newTime = new GregorianCalendar().getTimeInMillis();
		    double delta = (double)(newTime - lastTime);
		    mseconds -= delta;
		    lastTime = newTime;
		}
		if (mseconds < 0.0) toggleTurn();
	}
	
	/**
	 * @param e1 DynamicElement who's range is used for calculation
	 * @param e2 DynamicElement which is calculated to be within range of DynamicElement e1
	 */
	public boolean inRange(DynamicElement e1, DynamicElement e2){
		return e1.getRange() > distance(e1,e2);
	}
	
	/**
	    Deselect the selected dynamic element.
	*/
	public void unselect(){
		activeE=null;
		mode=1;
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
	        ap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	    } else {
	        ap.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	    }
	}
	
	public void mouseMoved(int x, int y) {
	    currentX = x;
	    currentY = y;
	}
	
	public void update() {
		int vel = 50;
		//if(currentX == 0 && currentY == 0) {}
		if (!(currentX == 0) || !(currentY == 0)) {
			if(ap.getWidth() * .03 > currentX){
				if(viewRegion.getX() >=25){
					viewRegion.setX(viewRegion.getX() - vel);	
				}
		    }
			if(ap.getWidth() * .97 < currentX){
		    	if(Grid.GRID_WIDTH - (viewRegion.getX()+viewRegion.getWidth()) >=25){
					viewRegion.setX(viewRegion.getX() + vel);	
				}
		    }
			if(ap.getHeight() * .03 > currentY){
		    	if(viewRegion.getY() >=25){
					viewRegion.setY(viewRegion.getY() - vel);	
				}
		    }
			if(ap.getHeight() * .97 < currentY){
		    	if(Grid.GRID_HEIGHT - (viewRegion.getY() + viewRegion.getHeight()) >=25){
					viewRegion.setY(viewRegion.getY() + vel);	
				}
		    }	
		}
	}
	
	/*
	 * received upon any click on ArmadaPanel
	 */
	public boolean click(int inX, int inY){
		System.out.println("Clicked: ("+inX+", "+inY+")");
		
		if(mode == 0 || activeE==null){//selecting a menu
			if(menus != null && menus.size() != 0){
				for (Menu m : menus) {
					//if(m.isIn(inX,inY)){
					//	m.click();
					//}
				}
			}
			if(delements != null && delements.size() != 0){//selecting a ship
				inX += viewRegion.getX(); inY += viewRegion.getY();
				//System.out.println("x, y:" + inX + ", " + inY);
				for (DynamicElement d : delements) {
					if(d.isIn(inX,inY) && d.isTargetable()){
					    mode = 1;
						activeE=d;
						activeE.update();
						//menus.add(d.getMenu());
						return true;
					}
				}
			}
			
		}
		if(activeE==null || activeE.getAlliance()!=turn || !activeE.isTargetable()) {
			return true;
		}
		if(mode == 1) { //move
			inX += viewRegion.getX(); inY += viewRegion.getY();
			engine.moveDynamicElement(activeE, inX, inY, delements);
			return true;
		}
		if(mode == 2) { //attack hull
			if(delements != null && delements.size() != 0 && activeE.canAttack()){
				inX += viewRegion.getX(); inY += viewRegion.getY();
				engine.attackHull(activeE, inX, inY, delements);
		    }
		    return true;
		}
		if(mode == 3) { //attack engine
			if(delements != null && delements.size() != 0 && activeE.canAttack()){
				inX += viewRegion.getX(); inY += viewRegion.getY();
				engine.attackEngine(activeE, inX, inY, delements);
			}
		}
		if(mode == 4){
			//docking
			if(delements != null && delements.size() != 0){
				inX += viewRegion.getX(); inY += viewRegion.getY();
				engine.dock(activeE, inX, inY, delements);
			}
			return true;
		}
		return false;
	}
	
	/*
	 * Calculates distance between the two inputs, order does not matter
	 */
	public int distance(DynamicElement e1, DynamicElement e2){
		return (int)Math.sqrt(Math.pow(Math.abs((double)e1.getY()-(double)e2.getY()),2) + Math.pow(Math.abs((double)e1.getX()-(double)e2.getX()),2));
	}
	
	public int distance(DynamicElement e1, int inX, int inY){
		return (int)Math.sqrt(Math.pow(Math.abs((double)e1.getY()-(double)inY),2) + Math.pow(Math.abs((double)e1.getX()-(double)inX),2));
	}
	
	public void toggleTurn(){
		if(turn==1){
			turn=2;
		}
		else{
			turn=1;	
		}
		setMode(1);
		startTurn();		
	}
	
	public void startTurn(){
	    mseconds = TURN_TIME;
		activeE=null;
		mode=1;
		if(delements != null && delements.size() != 0){//selecting a ship
			for (DynamicElement d : delements) {
				//System.out.println("looking for ship 1");
				d.startOfTurn(this);
			}
		}
	}
	
	/*
	 * draws everything on the Grid
	 */
	public void draw(Graphics g){
	    drawAllDelements(g);
	} 
	
	private void drawAllDelements(Graphics g){
		
		if(delements != null && delements.size() != 0){
			for (int i=0;i<delements.size();i++) {
				if(delements.get(i).isDead()){
					SoundEffect.EXPLODE.play();
					if((double)Math.random() >= (double)0.9){//25% chance of playing the scream
						SoundEffect.SCREAM.play();
					}
					elements.add(new Explosion(delements.get(i)));
					
					delements.remove(i);
					i--;
				}
			}
			for (int i=0;i<elements.size();i++){
				if(elements.get(i) instanceof Explosion){
					if(((Explosion)(elements.get(i))).isDone()){
						elements.remove(i);
						i--;
					}
				}
			}
			for(Element e:elements){
				e.draw(g, viewRegion);
			}
			for (DynamicElement de : delements) {
				if(de instanceof Planet){
					de.draw(g, viewRegion);
				}
			}
			for (DynamicElement de : delements) {
				if(de instanceof Ship){
					if(de.getAlliance() !=turn){
						de.draw(g, viewRegion);
					}
					
				}
			}
			for (DynamicElement de : delements) {
				if(de instanceof Ship){
					if(de.getAlliance() ==turn){
						de.draw(g, viewRegion);
					}
				}
			}
		}
	}
	
	public PlayerManager getPlayerManager(){
		return pm;
	}
	
	public Player getWinner(){
		return pm.getWinner();
	}
	
	public Player getLoser(){
		return pm.getLoser();
	}
	
	private static BufferedImage loadImage(File f) {
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(f);
        } catch (IOException e) {
            System.out.println("Failed to load background image");
        }
        return bi;
        
    }
    
    public double secondsRemainingForTurn() {
        return mseconds;
    }
    
    public double maxSecondsForTurn() {
        return TURN_TIME;
    }
	
	public void add(DynamicElement inDE){
		delements.add(inDE);
	}
	
	public DynamicElement getActiveE() {
		return activeE;
	}
	public int getMode() {
		return mode;
	}
	public int getTurn() {
		return turn;
	}
	public ArrayList<DynamicElement> getDelements() {
		return delements;
	}
	public ArmadaPanel getAp() {
		return ap;
	}
	public int getWidth(){
		return GRID_WIDTH;
	}
	public int getHeight(){
		return GRID_HEIGHT;
	}

	public void setCurrentX(int currentX) {
		this.currentX = currentX;
	}

	public void setCurrentY(int currentY) {
		this.currentY = currentY;
	}

}

/*
From click method
if(activeE.withinMovement(inX,inY) && activeE.canMovePath2(inX,inY, delements) && activeE instanceof Ship){
				activeE.moveTo(inX, inY);
				Ship temp = (Ship) activeE;
				temp.setPlanetDocked(null);
}
//System.out.println("x, y:" + inX + ", " + inY);
for (DynamicElement d : delements) {
						//System.out.println("looking for ship 1");
						if(d.isIn(inX,inY) && d.getAlliance()!=activeE.getAlliance() && activeE.withinRange(inX,inY) && d.isTargetable()){
							//System.out.println("looking for ship 2");
							d.hullTakeDamage(activeE);
							activeE.setCanAttack(false);
							//setMode(0);
							return true;
						}
					}
					
//System.out.println("x, y:" + inX + ", " + inY);
				for (DynamicElement d : delements) {
					//System.out.println("looking for ship 1");
					if(d.isIn(inX,inY) && d.getAlliance()!=activeE.getAlliance() && activeE.withinRange(inX,inY) && d.isTargetable() && d.getEngine()>0){
						//System.out.println("looking for ship 2");
						d.engineTakeDamage(activeE);
						System.out.println("Engines now at: "+d.getEngine());
						activeE.setCanAttack(false);
						//setMode(0);
						return true;
					}
				}
				
				for (DynamicElement d : delements) {
					if(d.isIn(inX,inY) && (d.getAlliance()==0 || d.getAlliance() == activeE.getAlliance()) && activeE.distanceFrom(inX, inY) < 100 && d.isTargetable() && d instanceof Planet && activeE instanceof Ship){
						System.out.println("docking attempted");
						Planet p = (Planet)d;
						Ship s = (Ship) activeE;
						p.dock(s);
						return true;
					}
					
					if(d.isIn(inX,inY) && d.getAlliance()!= activeE.getAlliance() && activeE.distanceFrom(inX, inY) < 100 && d.isTargetable() && d instanceof Ship && activeE instanceof Ship){
						System.out.println("Boarding attempted");
						Ship s = (Ship) activeE;
						Ship t = (Ship) d;
						s.board(t);
						return true;
					}
				}

*/

