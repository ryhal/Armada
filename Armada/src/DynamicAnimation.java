import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


import javax.imageio.ImageIO;


public class DynamicAnimation {
	
	private DynamicElement e;
	BufferedImage image;
	
	public DynamicAnimation(DynamicElement el){
		e=el;
		loadImage(e.getImage());
	}
	
	private void loadImage(String img) {
		
		try{
			File f= new File("image/"+img+".png");
			image= ImageIO.read(f);
		}
		catch(IOException e){
			e.printStackTrace();
			System.out.println("IMAGE COULD NOT BE FOUND");
		}
	}

	public DynamicElement getDE() {
		return e;
	}

	public void setDE(DynamicElement e) {
		this.e = e;
	}

	public static void main(String[] args) {
		int x=200;
		int y=200;
		int w=50;
		int h=50;
		double an=45;
		String img="boarding";
		int s=100;
		int all = 1;
		DynamicElement de = new DynamicElement(x,y,w,h,an,img,0,0,0,s,all,0,0);
		DynamicAnimation a = new DynamicAnimation(de);
		System.out.println("Set up Complete! Image = "+de.getImage());

	}

	
}
