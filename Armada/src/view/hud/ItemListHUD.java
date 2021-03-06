package view.hud; 
import view.*;
import view.button.ButtonPage;
import view.button.ItemButton;
import view.controller.GameController;
import view.hud.HUD.Position;
import item.ItemList;
import item.ItemList.ItemNames;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import element.DynamicElement;
import element.planet.Planet;
import element.ship.Ship;

/**
 * This displays the items of the selected Ship
 * @author Aaron
 *
 */
public class ItemListHUD extends HUD {

	//private Planet p;
	private Ship s;
	private DynamicElement de;
	//private ArrayList<ItemButton> buttons;
	//private ItemButton activeB;
	private HUDmanager hm;
	private DockHUD dh;
	private ButtonPage buttons;
	
	public ItemListHUD(GameController gc, Position position, HUDmanager h){
		super(new BoundingRectangle(0,0,250,200),gc, position);
		//location = l;
		de =gc.getActiveE();
		hm=h;
		if(de != null && de instanceof Ship){
			s=(Ship)de;
		}
		buttons = new ButtonPage(this);
		fillButtons();
	}
	
	/**
	 * Makes/fills the ButtonPage with ItemButtons cooresponding to the items of the selected Ship s;
	 */
	public void fillButtons(){
	    int x = r.getX();
	    int y = r.getY();
	    int width = r.getWidth();
	    int height = r.getHeight();
		updateLocation();
		if(de==null){
			return;
		}
		if(gc.getActiveE() != de || de==null){
			de =gc.getActiveE();
			buttons=new ButtonPage(this);
			fillButtons();
		}
		if(de != null && de instanceof Ship){
			s=(Ship)de;
		}
		
		if(de instanceof Ship){
			buttons.fillShip((Ship)de);
		}
		
	}
	
	/**
	 * Determines if this needs to be displayed.
	 */
	public void refresh(long previousTime, long currentTime) {
		super.refresh(previousTime, currentTime);
		if(gc.getGrid().getActiveE() == null){
			displaying=false;
		}
		else{
			displaying=true;
		}
	}
	
	/**
	 * Displays this HUD at the set location
	 */
	public void draw(Graphics g){
		if(gc.getActiveE() != de || de==null){
			de =gc.getActiveE();
			buttons=new ButtonPage(this);
			fillButtons();
		}
		if(de != null && de instanceof Ship){
			s=(Ship)de;
		}
		else{
			return;
		}
		
		updateLocation();
		buttons.setDimensions(r.x,r.y,r.width,r.height);
		//buttons.();
		r.setHeight(buttons.getSuggestedHeight());
		buttons.draw(g);
		if(dh !=null){
			dh.draw(g);
		}
	}
	
	/**
	 * returns true if a button is clicked
	 */
	public boolean click(int inX, int inY){
	    if (!this.drawingEnabled()) return false;		
		return buttons.click(inX, inY);
	}
	
	/**
	 * Returns selected ItemButton's Item ID.
	 * @return
	 */
	public ItemList.ItemNames getItemId(){
		if(buttons.getActiveB()!=null){
			return buttons.getActiveB().getId();
		}
		return null;
	}
	
}
