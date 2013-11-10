
public class NormalShip extends Ship{

	public NormalShip(int inX, int inY, int team) {
		super(inX,inY, 30, 30, "fighter",team);
		ah = new ShipAnimationHelper(this);
		baseMaxHull = 1000;
		maxHull=baseMaxHull;
		hull=maxHull;
		baseMaxEngine=1000;
		maxEngine=baseMaxEngine;
		engine=maxEngine;
		baseSpeed=3000;
		speed=baseSpeed;
		baseWeapons=300;
		weapons = baseWeapons;
		range = 300;
		//Some Examples of my testing
		//this.addItem(new Item(ItemList.ItemNames.WeaponsUpgrade));
		//this.addItem(new Item(ItemList.ItemNames.ScalingWeaponsUpgrade));
		//Item i = new Item(ItemList.ItemNames.HullPlate);
		//this.addItem(i);
		//this.removeItem(i);
	}

}
