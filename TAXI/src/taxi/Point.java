package taxi;

public class Point {
	/*@overview: Use the x and y postion to set a coordinate point.And the point has some properties like the distance from source,prepoint and flow.
	 * @invariant: 0 <= x && x < 80 && 0 <= y && y < 80
	 */
	int x;
	int y;
	int dis;
	Point pre;
	int eFlow;
	int sFlow;
	double flowTime;
	public boolean repOK(){
		/*@EFFECTS: \result == invariant(this)
		 */
		if(0 <= x && x < 80 && 0 <= y && y < 80){
			return true;
		}
		return false;
	}
	public Point(int x, int y){
		/*@MODIFIES: this.x; this.y; this.dis; this.pre; this.eFlow; this.sFlow; this.flowTime;
		 * @EFFECTS: 任何情况 ==> this.x = x;
		 * 			  任何情况 ==> this.y = y;
		 * 			  任何情况 ==> this.dis = -1;
		 *  		  任何情况 ==> this.pre = null;
		 * 			  任何情况 ==> this.eFlow = 0;
		 *  		  任何情况 ==> this.sFlow = 0;
		 * 			  任何情况 ==> this.flowTime = System.currentTimeMillis() / 100;
		 */
		this.x = x;
		this.y = y;
		this.dis = -1;
		this.pre = null;
		this.eFlow = 0;
		this.sFlow = 0;
		this.flowTime = System.currentTimeMillis() / 100;
	}
	
	public void setPoint(int x, int y){
		/*@REQUIRES: 0 <= x <= 79, 0 <= y <= 79;
		 *@MODIFIES: this.x; this.y;
		 * @EFFECTS: 任意情况 ==> this.x = x, this.y = y;
		 */
		this.x = x;
		this.y = y;
	}
	
	public boolean equels(Point p){
		/*@REQUIRES: p != null;
		 * @EFFECTS: \result == (x == p.x && y == p.y);
		 * 
		 */
		if(x == p.x && y == p.y){
			return true;
		}
		return false;
	}
	
	public String toString(){
		//@EFFECTS: \result == ("(" + this.x + ", " + this.y + ")");
		String str = "(" + this.x + ", " + this.y + ")";
		return str;
	}
}
