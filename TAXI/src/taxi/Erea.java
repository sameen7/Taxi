package taxi;

public class Erea {
	/*@overview: an rectangular erea defined by diagonal points. And it can see if a specific point is in this erea.
	 * @invariant: upL.repOK() && downR.repOK()
	 */
	Point upL;
	Point downR;
	
	public boolean repOK(){
		/*@EFFECTS: \result == invariant(this)
		 */
		if(!upL.repOK()){
			return false;
		}
		if(!downR.repOK()){
			return false;
		}
		return true;
	}
	
	public Erea(Point upL, Point downR){
		/*@MODIFIES: this.upL; this.downR;
		 * @EFFECTS: 任何情况 ==> this.upL = upL;
		 * 			  任何情况 ==> this.downR = downR;
		 */
		this.upL = upL;
		this.downR = downR;
	}
	
	public boolean inErea(Point p){
		/*@REQUIRES: p != null;
		 * @EFFECTS: \result == (p.x >= this.downR.x && p.x <= this.upL.x && p.y >= this.downR.y && p.y <= this.upL.y)
		 */
		if(p.x >= downR.x && p.x <= upL.x && p.y >= downR.y && p.y <= upL.y){
			return true;
		}
		return false;
	}
}
