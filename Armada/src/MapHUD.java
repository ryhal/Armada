import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;


public class MapHUD extends HUD{
	
	private ArrayList<DynamicElement> des;
	private double scale = .2;
	static final double DEFAULT_SCALE = .2;
	
	public MapHUD(Grid gr, int l){
		super(0,0,250,125,gr);
		location = l;
		des = grid.getDelements();
		setName("Map Layer");
	}
	
	public MapHUD(Grid gr, int l, BoundingRectangle r){
		super(0,0,250,125,gr);
		location = l;
		des = grid.getDelements();
		setName("Map Layer");
	}

    public boolean click(int inX, int inY){
		if(r.isIn(inX, inY)){
			moveMap(inX,inY);
			return true;
		}
		return false;
	}
	
	public void draw(Graphics g){
	    int x = r.getX();
	    int y = r.getY();
	    r.setWidth((int)(grid.getAp().getWidth() * scale));
	    r.setHeight((int)(grid.getAp().getHeight() * scale));
	    int width = r.getWidth();
	    int height = r.getHeight();
		updateLocation();
		g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.8f));
		g.fillRect(x, y, width, height);
		int insetWidth = (int)((grid.getViewRegion().getWidth() / (float)grid.getWidth())*(width*1.0));
		int insetHeight = (int)((grid.getViewRegion().getHeight() / (float)grid.getHeight())*(height*1.0));
		double dxf = (width*1.0)*(grid.getViewRegion().getX()/(grid.getWidth()*1.0));
		double dyf = (height*1.0)*(grid.getViewRegion().getY()/(grid.getHeight()*1.0));
		
		des=grid.getDelements();
		if(des == null)return;
		DynamicElement temp;
		synchronized(des){
			for(int i=0; i<des.size(); i++){
				temp=des.get(i);
				if(temp.getAlliance() == 1){
					g.setColor(Color.RED);
				}
				else if(temp.getAlliance() == 2){
					g.setColor(Color.BLUE);
				}
				else{
					g.setColor(Color.GREEN);
				}
				g.fillOval(x + (int)((temp.getX()/ (float)grid.getWidth())*(width*1.0)), y + (int)((temp.getY()/ (float)grid.getHeight())*(height*1.0)), 2+(int)((temp.getWidth()/ (float)grid.getWidth())*(width*1.0)),2+ (int)((temp.getHeight()/ (float)grid.getHeight())*(height*1.0)));
			}
		}
		
		int dx = (int)dxf; int dy = (int)dyf;
		g.setColor(Color.WHITE);
		g.drawRect(x+dx, y+dy, insetWidth, insetHeight);
		
	}

	public void moveMap(int inX, int inY){
		int newX = inX-r.x;
		int newY = inY-r.y;
	    double wPerc = (double)r.width/Grid.GRID_WIDTH;
	    double hPerc = (double)r.height/Grid.GRID_HEIGHT;
		int xx=(int)(((double)newX)/wPerc)-grid.getAp().getWidth()/2;
		int yy=(int)(((double)newY)/hPerc) -grid.getAp().getHeight()/2;
		if(xx < 0) xx = 0;
		if(yy < 0) yy = 0;
		if(xx > Grid.GRID_WIDTH-grid.getAp().getWidth()) xx = Grid.GRID_WIDTH-grid.getAp().getWidth();
		if(yy > Grid.GRID_HEIGHT-grid.getAp().getHeight()) yy = Grid.GRID_HEIGHT-grid.getAp().getHeight();
		grid.getViewRegion().setX(xx);
		grid.getViewRegion().setY(yy);
	}
	
	public boolean isIn(int inX, int inY){
		return r.isIn(inX, inY);
	}

	public void toggleScale(){
		if(scale == 1){
			scale = MapHUD.DEFAULT_SCALE;
		}
		else scale = 1;
	}
	
	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}
}
