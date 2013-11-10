import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;


public class ModeHUD extends HUD{

    private String[] modes = {"None", "Move (1)", "Hull (2)", "Eng (3)", "Dock(4)", "Move F.(5)"};
	
	public ModeHUD(int x, int y, int width, int height) {
		super(x, y, width, height);
		// TODO Auto-generated constructor stub
	}
	
	public ModeHUD(Grid gr, int t){
		super(5,45,500,40,gr, t);
	}
	
	public void draw(Graphics g){
	    int x = r.getX();
	    int y = r.getY();
	    int width = r.getWidth();
	    int height = r.getHeight();
	    
	    
		if(grid == null)return;
		this.updateLocation();
		//System.out.println("x, y, width, height: " + x + ", " + y + ", " + width + ", " + height);
		g.setColor(new Color(0.0f, 0.1f, 0.1f, 0.5f));
		//g.fillRect(x, y, width, height);
		int selection = grid.getMode();
		g.setColor(Color.WHITE);
		int selectionWidth = (int)(width / 6.0f);
		for (int i = 0; i < 6; i++) {
		    g.drawString(modes[i], i*selectionWidth+5, y+height-15);
		}
		
		
		int startX = (selection) * selectionWidth;
		g.drawRect(startX, y+1, selectionWidth, height-1);
		
		if (grid.getActiveE()!=null && grid.getActiveE() instanceof Ship) {
			Graphics2D g2d = (Graphics2D)g;
		    
				    
		    int shipX = grid.getActiveE().getX();
		    int shipY = grid.getActiveE().getY();
		    Stroke s = g2d.getStroke();
		    float array[] = {10.0f};
		    g2d.setStroke(new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, array, 0.0f));
		    g2d.setColor(Color.WHITE);
		    if(grid.getMode()==1 && grid.getActiveE().canMovePath2(grid.getCurrentX() + grid.getViewRegion().getX(),grid.getCurrentY() + grid.getViewRegion().getY(),grid.getDelements()) && grid.getActiveE().canMove()){
		    	g.setColor(Color.GREEN);
		    }
		    else if(grid.getMode() == 1){
		    	g.setColor(Color.RED);
		    }
		    else if((grid.getMode() == 2 ) && grid.getActiveE().withinRange(grid.getCurrentX() + grid.getViewRegion().getX(),grid.getCurrentY() + grid.getViewRegion().getY()) && grid.getActiveE().canAttack()){
		    	g.setColor(new Color(250,100,0));
		    }
		    else if((grid.getMode() == 2 )){
		    	g.setColor(Color.RED);
		    }
		    else if(( grid.getMode() == 3) && grid.getActiveE().withinRange(grid.getCurrentX() + grid.getViewRegion().getX(),grid.getCurrentY() + grid.getViewRegion().getY()) && grid.getActiveE().canAttack()){
		    	g.setColor(Color.YELLOW);
		    }
		    else if(( grid.getMode() == 3)){
		    	g.setColor(Color.RED);
		    }else if(( grid.getMode() == 4) && grid.getActiveE().distance(grid.getCurrentX() + grid.getViewRegion().getX(), (grid.getCurrentY()) + grid.getViewRegion().getY()) < 100){
		    	g.setColor(Color.MAGENTA);
		    }
		    else if(( grid.getMode() == 4)){
		    	g.setColor(Color.RED);
		    	g.drawString("Out of docking range or already docked", x, y+g.getFontMetrics().getHeight()*2);
		    }
		    else if (( grid.getMode() == 5)) {
		        g.setColor(new Color(0.0f, 0.9f, 1.0f));
		    }
		    
		    if(grid.getCurrentX()!=0||grid.getCurrentY()!=0){
		        if (grid.getMode() != 5) {
			        g.drawLine(shipX-grid.getViewRegion().getX(), shipY-grid.getViewRegion().getY(), grid.getCurrentX(), grid.getCurrentY());
			        int radius = 20;
			        g.drawOval(grid.getCurrentX()-radius, grid.getCurrentY()-radius, radius*2, radius*2);
			    } else {
			        int counter = 0;
			        for (DynamicElement d : grid.getDelements()) {
			            if (d instanceof Ship && d.getAlliance()==grid.getTurn() && d.withinMovement(grid.getCurrentX(),grid.getCurrentY()) && d.isTargetable()) {
			                shipX = d.getX();
			                shipY = d.getY();
			                g.drawLine(shipX-grid.getViewRegion().getX(), shipY-grid.getViewRegion().getY(), grid.getCurrentX()-counter*50, grid.getCurrentY()-counter*50);
			                int radius = 20;
			                g.drawOval(grid.getCurrentX()-counter*50-radius, grid.getCurrentY()-counter*50-radius, radius*2, radius*2);
			                counter++;
			            }
			            
			        }
			    }
		    }
		    g2d.setStroke(s);
		    /*
		    switch(grid.getMode()){
		    case 0:
		    	g.drawString("Mode: No Move Selected", x, y+g.getFontMetrics().getHeight());
		    	break;
		    case 1:
		    	g.drawString("Mode: Move", x, y+g.getFontMetrics().getHeight());
		    	break;
		    case 2:
		    	g.drawString("Mode: Attack Hull", x, y+g.getFontMetrics().getHeight());
		    	break;
		    case 3:
		    	g.drawString("Mode: Attack Engine", x, y+g.getFontMetrics().getHeight());
		    	break;
		    case 4:
		    	g.drawString("Mode: Docking", x, y+g.getFontMetrics().getHeight());
		    	break;
		    case 5:
		        g.drawString("Mode: Fleet Move", x, y+g.getFontMetrics().getHeight());
		        break;
		    }  
		    */
		}
		else if(grid.getActiveE() != null && grid.getActiveE() instanceof Planet){
			Planet p = (Planet)grid.getActiveE();
			switch(grid.getActiveE().getAlliance()){
				case 1:
					g.setColor(Color.RED);
					break;
				case 2:
					g.setColor(Color.BLUE);
					break;
				default:
					g.setColor(Color.WHITE);
					break;
			}
			g.drawOval(p.getX()-p.getWidth()/2 - grid.getViewRegion().getX(), p.getY()-p.getHeight()/2 - grid.getViewRegion().getY(), p.getWidth(), p.getHeight());
		}
		else{
			g.setColor(Color.WHITE);
			//g.drawString("Nothing Selected", x, y+g.getFontMetrics().getHeight());
		}
	}

}

/*
grid.distance(grid.getActiveE(), grid.getCurrentX() + grid.getViewRegion().getX(), (grid.getCurrentY()) + grid.getViewRegion().getY()) < 100)
*/
