package taxi;

public class Timer {
	/*@overview: To get the  current time. 
	 */
	public boolean repOK(){
		/*@EFFECTS: \result == true
		 */
		return true;
	}
	public static double getTime(){
		//@EFFECTS: \result == System.currentTimeMillis() / 100;
		return System.currentTimeMillis() / 100;
	}
}
