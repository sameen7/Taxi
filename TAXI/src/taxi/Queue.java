package taxi;

import java.util.ArrayList;

public class Queue {
	/*@overview: Simulate the queue of requests.
	 * @invariant: list.isEmpty || (!list.isEmpty && \all int i;0<=i<=list.size();list.get(i).repOK() && list.get(i)!=null)
	 */
	private ArrayList<Request> list;
	TaxiGUI taxigui; 
	
	public boolean repOK(){
		/*@EFFECTS: \result == invariant(this)
		 */
		if(!list.isEmpty()){
			for(int i = 0; i < list.size(); i++){
				if(!list.get(i).repOK() || list.get(i) == null){
					return false;
				}
			}
		}
		return true;
	}
	
	public Queue(TaxiGUI taxigui) {
		/*@MODIFIES: this.list;
		 * @EFFECTS: 任意情况 ==> this.list = new ArrayList<Request>();
		 */
		this.list = new ArrayList<Request>();
		this.taxigui = taxigui;
	}
	
	public synchronized Request getRequest(int n) {
		/*@REQUIRES: 0 <= n <= list.size();
		 * @EFFECTS: \result == list.get(n);
		 * @THREAD_EFFECTS: \locked();
		 */
		return list.get(n);
	}
	public synchronized int getSize() {
		/* @EFFECTS: \result == list.size();
		 * @THREAD_EFFECTS: \locked();
		 */
		return list.size();
	}
	
	public synchronized void add(Request req) {
		/*@REQUIRES: req != null;
		 *@MODIFIES: this.list;
		 * @EFFECTS: !req.src.equels(req.dst) ==> (如果req不是相同请求，则加入数组中);
		 * @THREAD_EFFECTS: \locked();
		 */
		if(list.isEmpty() && !req.src.equels(req.dst)){
			list.add(req);
			taxigui.RequestTaxi(req.src, req.dst);
		}else{
			if(!req.src.equels(req.dst)){
				int flag = 0;
				for(int i = 0; i < list.size(); i++){
					if(req.src.equels(list.get(i).src) && req.dst.equels(list.get(i).dst) && req.time == list.get(i).time){
						flag = 1;
						break;
					}
				}
				if(flag == 0){
					list.add(req);
					taxigui.RequestTaxi(req.src, req.dst);
				}
			}
		}
	}
	public synchronized void remove(int i) {
		/*@REQUIRES: 0 <= i <= list.size();
		 * @EFFECTS: 任意情况 ==> 删除list中下表为i的元素
		 * @THREAD_EFFECTS: \locked();
		 */
		list.remove(i);
	}
	
	public synchronized boolean isEmpty(){
		/* @EFFECTS: \result == list.isEmpty();
		 * @THREAD_EFFECTS: \locked();
		 */
		return list.isEmpty();
	}
}
