import java.util.ArrayList;


public class PlayerManager {
	
	private int numPlayers;
	private ArrayList<Player> players;
	
	public PlayerManager(Grid g){
		players=new ArrayList<Player>();
		Player p1 = new Player(1);
		players.add(p1);
		g.add(p1.getHome());
		Player p2 = new Player(2);
		players.add(p2);
		g.add(p2.getHome());
	}
	
	public void payPlayerMoney(int alliance, int money){
		for(Player p: players){
			if(p.getAlliance() == alliance){
				p.incMoney(money);
			}
		}
	}
	
	public boolean canPay(int alliance, int pay){
		for(Player p: players){
			if(p.getAlliance() == alliance){
				if(p.getMoney() >= pay){
					return true;
				}
				else{
					return false;	
				}
			}
		}
		return false;
	}
	
	public int getPlayerMoney(int alliance){
		for(Player p: players){
			if(p.getAlliance() == alliance){
				return p.getMoney();
			}
		}
		return -1;
	}
	
	public void playerPays(int alliance, int pay){
		for(Player p: players){
			if(p.getAlliance() == alliance){
				p.incMoney(-pay);
			}
		}
	}
	
	public Player getWinner(){
		for(Player p: players){
			if(p.getHome().isDead()){
				p.setDead(true);
			}
		}
		for(Player p: players){
			if(p.isDead()){
				for(Player pp: players){
					if(!pp.isDead()){
						return pp;
					}
				}
			}
		}
		return null;
	}

}