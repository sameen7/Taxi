package taxi;

import java.util.ArrayList;

public class Request {
	/*@overview: Store the passengers' requests, has some properties to record the source, destination, the response erea of the request, the cars responded, the shortest path to pick up passenger and the shortest path to send the passenger.
	 * @invariant: src.repOK() && dst.repOK()
	 */
	int id;
	Point src;
	Point dst;
	double time;
	Erea erea;
	ArrayList<Cab> cablist;
	ArrayList<Point> path;
	ArrayList<Point> spath;
	ArrayList<Point> path2;
	ArrayList<Point> spath2;
	boolean flag;
	
	public boolean repOK(){
		/*@EFFECTS: \result == invariant(this)
		 */
		if(!src.repOK() || !dst.repOK()){
			return false;
		}
		return true;
	}
	
	public Request(Point src, Point dst, double time, int id){
		/*@MODIFIES: this.src; this.dst; this.time; this.erea; this.cablist; this.path; this.flag; this.id;
		 * @EFFECTS: 任意情况==>this.src = src;
					  任意情况==>this.dst = dst;
					  任意情况==>this.time = time;
					  任意情况==>setErea();
					  任意情况==>this.cablist = new ArrayList<Cab>();
					  任意情况==>this.path = new ArrayList<Point>();
					  任意情况==>this.spath = new ArrayList<Point>();
					  任意情况==>this.path2 = new ArrayList<Point>();
					  任意情况==>this.spath2 = new ArrayList<Point>();
					  任意情况==>flag = false;
					  任意情况==>this.id = id;	 
		 */
		this.src = src;
		this.dst = dst;
		this.time = time;
		setErea();
		this.cablist = new ArrayList<Cab>();
		this.path = new ArrayList<Point>();
		this.spath = new ArrayList<Point>();
		this.path2 = new ArrayList<Point>();
		this.spath2 = new ArrayList<Point>();
		flag = false;
		this.id = id;
	}
	public void setErea(){
		/*@MODIFIES: this.era;
		 * @EFFECTS: this.src != null ==> this.erea是以this.src为中点的4*4的区域;
		 */
		Point upL;
		Point downR;
		int upX, downX, upY, downY;
		if((src.x + 2) <= 79){
			upX = src.x + 2;
		}else{
			upX = 79;
		}
		if((src.x - 2) >= 0){
			downX = src.x - 2;
		}else{
			downX = 0;
		}
		if((src.y + 2) <= 79){
			upY = src.y + 2;
		}else{
			upY = 79;
		}
		if((src.y - 2) >= 0){
			downY = src.y - 2;
		}else{
			downY = 0;
		}
		upL = new Point(upX, upY);
		downR = new Point(downX, downY);
		erea = new Erea(upL, downR);
	}
	public boolean pass(Cab cab){
		//@EFFECTS: \result == (contain(cab).cablist);
		if(cablist.isEmpty()){
			return false;
		}else{
			for(int i = 0; i < cablist.size(); i++){
				if(cab.id == cablist.get(i).id){
					return true;
				}
			}
		}
		return false;
	}
	
	public String toString(){
		//@EFFECTS: \result == (id + "[CR, " + src + ", " + dst + "]");
		String str = id + "[CR, " + src + ", " + dst + "]";
		return str;
	}
}
