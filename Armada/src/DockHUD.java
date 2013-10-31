import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class DockHUD extends HUD{
	
	private Planet p;
	private ArrayList<Button> buttons;

	public DockHUD(Grid gr, int l){
		super(0,0,300,30,gr);
		location = l;
		buttons=new ArrayList<Button>();
		fillButtons();
	}
	
	public void draw(Graphics g){
		if(grid.getActiveE() instanceof Planet) p = (Planet)grid.getActiveE();
		else return;
		if(p==null){
			return;
		}
		updateLocation();
		if(p.getDocked().size() < 1){
			g.setColor(Color.BLACK);
			width= 150;
			g.fillRect(x, y, width, 30);
			g.setColor(Color.WHITE);
			g.drawString("There are no docked ships", x+width/2 - g.getFontMetrics().stringWidth("There are no docked ships")/2, y+20);
			return;
		}
		
		
		
		fillButtons();
		updateHeight();
		g.setColor(new Color(50,100,210, 75));
		g.fillRect(x, y, width, height);
		g.setColor(new Color(25,125,175, 75));
		g.fillRect(x+5, y+5, width-10, height-10);
		drawButtons(g);
	}
	
	public void updateHeight(){
		this.setHeight( 12 + (buttons.size() * 24));
	}
	
	public void updateButtons(){
		this.setHeight(buttons.size() * 35);
		for(int i =0; i < buttons.size(); i++){
			Button b = buttons.get(i);
			b.setX(x+3);
			b.setY(y+3+(i* 22));
		}
	}
	
	public void drawButtons(Graphics g){
		for(Button b: buttons){
			b.draw(g);
		}
	}
	
	public void fillButtons(){
		buttons=new ArrayList<Button>();
		updateLocation();
		if(p==null)return;
		if(p.getDocked().size()<1)return;
		for(int i =0; i < p.getDocked().size(); i++){
			buttons.add(new Button(x+8, y+8+(i*24), width-16, 20, grid, p.getDocked().get(i).toString()));	
		}
		
	}

	
	
}