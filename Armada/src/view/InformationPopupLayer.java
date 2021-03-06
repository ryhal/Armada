package view; 
import view.*;
import java.awt.*;
public class InformationPopupLayer extends ViewLayer {
    private String text;
    private boolean fadingIn = false;
    private boolean fadingOut = true;
    private float phase = 1.0f;
    static final int PHASE_TIME = 3000;
    static final float DEFAULT_RED = 0.0f;
    static final float DEFAULT_GREEN = 0.1f;
    static final float DEFAULT_BLUE = 0.1f;
    private float red = DEFAULT_RED;
    private float green = DEFAULT_GREEN;
    private float blue = DEFAULT_BLUE;
    //public static InformationPopupLayer ipl;
    /*
    public static InformationPopupLayer getInstance(){
    	if(ipl == null){
    		ipl = new InformationPopupLayer(new BoundingRectangle(0, 45, 200, 35));
    	}
    	return ipl;
    }
    */
    
    /**
    Creates a new information layer instance at the specified position and size.
    */
    public InformationPopupLayer(BoundingRectangle b) {
        super(b);
    }
    /**
    Show a message.
    */
    public void showPopup(String text) {
        showPopup(text, null);
    }
    /**
    Shows a message with the specified background color.
    */
    public void showPopup(String text, Color backgroundColor) {
        if (backgroundColor == null) {
            red = DEFAULT_RED;
            green = DEFAULT_GREEN;
            blue = DEFAULT_BLUE;
        } else {
            red = backgroundColor.getRed() / 255.0f;
            green = backgroundColor.getGreen() / 255.0f;
            blue = backgroundColor.getBlue() / 255.0f;
        }
        this.text = text;
        phase = 0.0f;
    }
    
    /**
    Updates the stage of the animation.
    */
    public void refresh(long previousTime, long currentTime) {
        //System.out.println("Refresh");
        int delta = (int)(currentTime - previousTime);
        float step = delta / (PHASE_TIME * 1.0f);
        if (phase < 1.0f) phase += step;
    }
    
    /**
    Returns false to indicate that this layer does not capture clicks.
    */
    public boolean click(int x, int y) { return false; }
    
    /**
    Draws the popup in its current stage into the Graphics object. Nothing should should
    appear if no message was added in a while.
    */
    public void draw(Graphics g) {
    	if(text != null){
    		r.width=g.getFontMetrics().stringWidth(text) + 50;
    	}
        if (phase > 0.99f) return;
        float alpha = 1.0f;
        if (phase < 0.2f) {
            alpha = phase * 5.0f;
        } else if (phase > 0.8f) {
            alpha = (1 - phase) * 5.0f;
        }
        g.setColor(new Color(red, green, blue, alpha));
        g.fillRect(this.r.getX(), this.r.getY(), this.r.getWidth(), this.r.getHeight());
        g.setColor(new Color(1.0f, 1.0f, 1.0f, alpha));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        int startX = this.r.getX() + (int)((this.r.getWidth() - textWidth) / 2.0);
        int startY = this.r.getY() + (int)((this.r.getHeight() - ((this.r.getHeight() - textHeight) / 2.0)));
        g.drawString(text, startX, startY);
    }
}