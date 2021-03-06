package element.planet; 
import view.*;

import java.awt.Graphics;
import java.util.ArrayList;

import element.DynamicElement;
import element.ship.Ship;
import element.status.HealthBar;
import game.ArmadaEngine;
import animation.AnimationHelper;
import animation.DynamicAnimationHelper;


public class Planet extends DynamicElement{
	
	protected ArrayList<Ship> dockedList;
	protected int upgradeLevel, worth = 250;
	protected int imgNum = 1;
	private int[][] storeList = new int[3][10]; // The store should be handled by another class
	//private Teleporter tp; // teleport is a simple change in X and Y, there is no need for it to be an entire class
	//private HealthBar hb;//health bars don't account for the PLanet's state yet, so it isnt ready to add to the Planet yet.  Also, you forgot to add the hb to the draw method
    
    /*
	public Planet(int a, int b, int w, int h, String img, int r,
			int maxH, int maxE, int s, int all, int t, int weap) {
		//super(a, b, w, h, img, r, maxH, maxE, s, all, t, weap);
		
	}
    */
    
    
	public String upgradeDescription() { return "Upgrade Description"; }
	public int upgradePrice() { return 100; }
	
	public Planet() {
		super();
		initialize();
		setRandomImage();
		
		ah=new DynamicAnimationHelper(this);
	}
	
	public Planet(int image) {
	    super();
	    System.out.println("Building planet with image number: " + image);
	    initialize();
	    if (image < 1 || image > 11) image = 1;
	    imgNum = image;
	    setImage("planet/planet_" + imgNum);
	    ah=new DynamicAnimationHelper(this);
	}
	
	private void initialize() {
	    targetable = true;
		angle=0;
		speed=0; maxEngine =0; engine=0;
		maxHull=6000; hull=maxHull;
		canAttack=false; canMove= false;
		x = (int)((double)Grid.getGridWidth()* Math.random());
		y = (int)((double)Grid.getGridHeight()* Math.random());
		width = 100 +(int)(Math.random() * 150.0);
		height = width;
		range = (int)((double)width * 1.5);
		
		
		alliance = 0;
		dockedList = new ArrayList<Ship>();
		upgradeLevel = 0;
		hb = new HealthBar(this);
	}
	
	public int getImageNum() {
	    return imgNum;
	}
	
	private void setRandomImage(){		
		int i = (int)(1 + (Math.random() *11) );
		imgNum = i;
		setImage("planet/planet_" + i);
	}
	
	public void dock(Ship s){
		if(alliance == 0 || alliance == s.getAlliance()){
			s.setPlanetDocked(this);
			dockedList.add(s);
			System.out.println(s + " docked at " + this);
		}
	}
	
	public void startOfTurn(Grid g){
		for(Ship s: dockedList){
			if(s.isDead()){
				dockedList.remove(s);
			}
		}
		if(alliance==0 && onlyOneAllianceDocked() && g.getTurn()==dockedList.get(0).getAlliance()){
			alliance = dockedList.get(0).getAlliance();
		}
		if(alliance!=0 && alliance==g.getTurn()){
			g.getPlayerManager().payPlayerMoney(alliance, worth);
			System.out.println("Pay player " + alliance);
		}
		
	}
	
	public void startOfTurn(ArmadaEngine engine){
		ArrayList<Ship> newList = new ArrayList<Ship>();
		for(Ship s: dockedList){
			if(s.isDead()){
				continue;
			}
			newList.add(s);
		}
		dockedList=newList;
		if(alliance==0 && onlyOneAllianceDocked() && engine.getTurn()==dockedList.get(0).getAlliance()){
			alliance = dockedList.get(0).getAlliance();
		}
		if(alliance!=0 && alliance==engine.getTurn()){
			engine.getPlayerManager().payPlayerMoney(alliance, worth);
			System.out.println("Pay player " + alliance);
		}
		
	}
	
	public void unDock(Ship s){
		dockedList.remove(s);
		
	}
	
	private boolean onlyOneAllianceDocked(){
		if(dockedList == null) return false;
		if(dockedList.size()==0)return false;
		int al = dockedList.get(0).getAlliance();
		for(DynamicElement d: dockedList){
			if(d.getAlliance()!=al) return false;
		}
		return true;
	}
	
	public ArrayList<Ship> getDocked(){
		for(Ship s: dockedList){
			if(s.isDead()){
				dockedList.remove(s);
			}
		}
		return dockedList;
	}
	public void draw(Graphics g, BoundingRectangle viewRect){
		super.draw(g,viewRect);
		if(alliance !=0){
			AnimationHelper.draw(x, y, width/2, height/2, "fort", g,viewRect);
		}
	}
	/*
	 * There are far too few situations accounted for in this code and needs to be removed.
	public void upgrade(){
		if (alliance == 0) {
			if (dockedList.size() == 1) {  // What if I have more than one ship docked of the same alliance?
				alliance = dockedList.get(0).alliance; //NEVER directly access an object's variables.  Use .getAlliance()
				upgradeLevel++;
			}
			else if (dockedList.size() == 0) System.out.println("Cannot upgrade unless ship is docked.");//I can't think of any situation in which this would happen.  If alliance==0, then the Planet shouldnt even have an upgrade option
			else System.out.println("Planet can only be upgraded if one ship is docked.");//When did we decide that?
		}
		else if (upgradeLevel == 1) {
			if (dockedList.size() == 1 && dockedList.get(0).alliance != alliance) alliance = dockedList.get(0).alliance;//again, NEVER access variable directly.  Also, why is alliance being set here? It should already have an alliance. Also, this if statement doesn't make sense in general.  Why would an enemy ship be docked? Why would upgrade be called in this situation?  why, again, is there only one ship? I already have a method that you could use to check if every ship is the same alliance, so you could use that instead of assuming that there is one ship and setting it to that alliance.  Use onlyOneAllianceDocked() 
			else {
				upgradeLevel++;
				
			}
		}
		//at this point, I stopped commenting on why I removed this method
		else if (upgradeLevel == 2) {
			if (dockedList.size() == 1 && dockedList.get(0).alliance != alliance){
				alliance = dockedList.get(0).alliance;
				upgradeLevel = 0;
			}
			else {
				upgradeLevel++;
				storeList[0][0]++;
				storeList[0][1]++;
				storeList[0][2]++;
				hb = new HealthBar(this);
			}
		}
		else if (upgradeLevel == 3) {
			if (dockedList.size() == 1 && dockedList.get(0).alliance != alliance){
				alliance = dockedList.get(0).alliance;
				upgradeLevel = 0;
			}
			else {
				upgradeLevel++;
				//tp = new Teleporter(x, y);
			}
		}
	}
	
	public int getUpgradeLevel(){
		return upgradeLevel;
	}
	public void purchaseStore(){
	}*/
}
