package element.ship; 
import item.Item;
import item.ItemList;
import item.ItemList.ItemNames;
import animation.ShipAnimationHelper;
import view.*;
public class CargoShip extends Ship {
	
	//private double hullBonus = .1, engineBonus = .1;

	public CargoShip(int inX, int inY, int team) {
		super(inX,inY, 60, 50, "cargoship",team);
		ah = new ShipAnimationHelper(this);
		baseMaxHull=500;
		maxHull=baseMaxHull;
		hull=maxHull;
		baseMaxEngine=500;
		maxEngine=baseMaxEngine;
		engine=maxEngine;
		baseSpeed=250;
		maxSpeed=baseSpeed;
		speed=baseSpeed;
		baseWeapons=300;
		weapons=baseWeapons;
		range=300;
		maxCargo=25;
		board=.1f;


		this.addItem(new Item(ItemList.ItemNames.Cargo));
		update();	
	}
	
	
}
