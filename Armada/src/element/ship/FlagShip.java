package element.ship; 
import item.Item;
import item.ItemList;
import item.ItemList.ItemNames;
import animation.ShipAnimationHelper;
import view.*;
public class FlagShip extends Ship {
	
	//private double hullBonus = .1, engineBonus = .1;

	public FlagShip(int inX, int inY, int team) {
		super(inX,inY, 195, 79, "flagship",team);// 89 71
		ah = new ShipAnimationHelper(this);
		baseMaxHull=5000;
		maxHull=baseMaxHull;
		hull=maxHull;
		baseMaxEngine=5000;
		maxEngine=baseMaxEngine;
		engine=maxEngine;
		baseSpeed=100;
		maxSpeed=baseSpeed;
		speed=baseSpeed;
		baseWeapons=600;
		weapons=baseWeapons;
		range=600;
		maxCargo=50;
		board=.8f;

		//this.addItem(new Item(ItemList.ItemNames.Flagship));
		update();	
	}
	
	
}
