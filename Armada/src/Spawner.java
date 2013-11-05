
public class Spawner {
	
	public static void spawnPlanets(Grid grid, int numPlanets){
		long time = System.currentTimeMillis();
		for(int i = 0; i < numPlanets; i++){
			Planet temp = new Planet();
			Planet temp2 = new Planet();
			if(grid.getDelements() != null && grid.getDelements().size()>0 ){
				for(int h = 0;  h<grid.getDelements().size(); h++){
					while(!Spawner.awayFromEdge(grid, temp) || temp.distanceFrom(grid.getDelements().get(h)) < 200){
						if(System.currentTimeMillis() - time > (50*Grid.GRID_HEIGHT/3000)){
							System.out.println("PLANET SPAWNER TIMEOUT ---- DONT TRY SPAWNING SO MANY PLANETS");
							return;
						}
						temp=new Planet();
						h=0;
					}
				}
				
			}
			temp2.setWidth(temp.getWidth());
			temp2.setHeight(temp.getHeight());
			temp2.setY(Grid.GRID_HEIGHT - temp.getY());
			temp2.setX(Grid.GRID_WIDTH - temp.getX());
			System.out.println(temp.getX() + " " + temp.getY() + " " + temp2.getX() + " " + temp2.getY());
			grid.add(temp);
			grid.add(temp2);
		}
	}
	
	public static boolean awayFromEdge(Grid grid, DynamicElement de){
		if(de.getX() > 200 && Math.abs(de.getY()- Grid.GRID_HEIGHT) > 200 && de.getY()> 200 && Math.abs(Grid.GRID_WIDTH - de.getX()) > 200){
			return true;
		}
		else return false;
	}
	
}