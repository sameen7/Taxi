package taxi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public class Cab extends Thread{
	/*@overview: According to passengers' requests to complete the task of pick up the passengers and send them to destination in shortest path.When there is no request,the cab can move at random.
	 * @invariant: map!=null && (\all int i,j;0<=i<=79,0<=j<=79;0<=map[i][j]<=3)  && map2!=null && (\all int i,j;0<=i<=79,0<=j<=79;0<=map2[i][j]<=3) && pmap!=null && (\all int i,j;0<=i<=79,0<=j<=79;pmap[i][j].repOK==true)  &&pmap2!=null && (\all int i,j;0<=i<=79,0<=j<=79;pmap2[i][j].repOK==true) && light.repOK()==true && id>=0 && id<100
	 */
	int direction;
	int status;
	Point point;
	int credit;
	int id;
	int map[][];
	Point pmap[][];
	Request req;
	double time1;
	Timer timer;
	TaxiGUI taxigui; 
	double time;
	int signal;
	Light light;
	int map2[][];
	Point pmap2[][];
	boolean father;
	ArrayList<Request> reqlist;
	
	public boolean repOK(){
		/*@EFFECTS: \result == invariant(this)
		 */
		if(map == null){
			return false;
		}
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				if(map[i][j] < 0 || map[i][j] > 3){
					return false;
				}
			}
		}
		if(map2 == null){
			return false;
		}
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				if(map2[i][j] < 0 || map2[i][j] > 3){
					return false;
				}
			}
		}
		if(pmap == null){
			return false;
		}
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				if(!pmap[i][j].repOK()){
					return false;
				}
			}
		}
		if(pmap2 == null){
			return false;
		}
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				if(!pmap2[i][j].repOK()){
					return false;
				}
			}
		}
		if(!light.repOK()){
			return false;
		}
		if(id < 0 || id > 99){
			return false;
		}
		return true;
	}
	
	public Cab(int id, int map[][], Point pmap[][], TaxiGUI taxigui, Light light, int map2[][], Point pmap2[][]){
		/*@MODIFIES:this.map; this.point; this.status; this.credit; this.id; this.map; this.pmap; this.req; this.time1; this.timer; this.time; this.signal; this.direction; this.map2; this.father; this.pmap2; this.reqlist;
		 * @EFFECTS:任意情况==>this.point = new Point(rand.nextInt(80), rand.nextInt(80));
		 * 			任意情况==>this.status = 2;
		 * 			任意情况==>this.credit = 0;
		 * 			任意情况==>this.id = id;
		 * 			任意情况==>this.map = map;
		 * 			任意情况==>this.pmap = pmap;
		 * 			任意情况==>this.req = null;
		 * 			任意情况==>this.time1 = 0;
		 * 			任意情况==>this.timer = new Timer();
		 * 			任意情况==>this.time = 0;
		 * 			任意情况==>this.signal = 0;
		 * 			任意情况==>this.direction = -1;
		 * 			任意情况==>this.map2 = map2;
		 * 			任意情况==>this.father = true;
		 * 			任意情况==>this.pmap2 = pmap2;
		 * 			任意情况==>this.reqlist = new ArrayList<Request>();
		 */
		Random rand = new Random();
		point = new Point(rand.nextInt(80), rand.nextInt(80));
		status = 2;
		credit = 0;
		this.id = id;
		this.map = map;
		this.pmap = pmap;
		req = null;
		time1 = 0;
		timer = new Timer();
		this.taxigui = taxigui;
		this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
		time = 0;
		signal = 0;
		this.light = light;
		direction = -1;
		this.map2 = map2;
		this.father = true;
		this.pmap2 = pmap2;
		this.reqlist = new ArrayList<Request>();
	}
	
	public synchronized void Wait() throws InterruptedException{
		/*@MODIFIES:this.point;this.pmap
		 * @EFFECTS:当出租车选择好运动方向后==>出租车当前坐标改变;
		 * 									 ==>两点之间的道路流量加1;
		 * 			sleep(1)未正常执行 ==> exceptional_behavior (InterruptedException);
		 * @THREAD_EFFECTS: \locked();
		 */
		int[] p = new int[4];
		int j = 0;
		int flag = 0;
		for(int h = 0; h < 4; h++){
			p[h] = -1;
		}
		int i = map[point.x][point.y];
		if(point.x > 0){
			p[2] = map[point.x-1][point.y];
		}
		if(point.y > 0){
			p[3] = map[point.x][point.y - 1];
		}
		if(p[2] >= 2){
			p[2] = 1;
		}else{
			p[2] = -1;
		}
		if(p[3] == 1 || p[3] == 3){
			p[3] = 1;
		}else{
			p[3] = -1;
		}
		if(i == 1 || i == 3){
			p[1] = 1;
		}
		if(i >= 2){
			p[0] = 1;
		}
		int flow = 0;
		for(int h = 0; h < 4; h++){
			if(p[h] == 1 && h == 0){
				flow = pmap[point.x][point.y].sFlow;
				break;
			}else if(p[h] == 1 && h == 1){
				flow = pmap[point.x][point.y].eFlow;
				break;
			}else if(p[h] == 1 && h == 2){
				flow = pmap[point.x-1][point.y].sFlow;
				break;
			}else if(p[h] == 1 && h == 3){
				flow = pmap[point.x][point.y-1].eFlow;
				break;
			}
		}
		for(int h = 0; h < 4; h++){
			if(p[h] == 1 && h == 0){
				if(pmap[point.x][point.y].sFlow < flow){
					flow = pmap[point.x][point.y].sFlow;
				}
			}else if(p[h] == 1 && h == 1){
				if(pmap[point.x][point.y].eFlow < flow){
					flow = pmap[point.x][point.y].eFlow;
				}
			}else if(p[h] == 1 && h == 2){
				if(pmap[point.x-1][point.y].sFlow < flow){
					flow = pmap[point.x-1][point.y].sFlow;
				}
			}else if(p[h] == 1 && h == 3){
				if(pmap[point.x][point.y-1].eFlow < flow){
					flow = pmap[point.x][point.y-1].eFlow;
				}
			}
		}
		int[] pp = new int[4];
		for(int h = 0; h < 4; h++){
			pp[h] = p[h];
		}
		for(int h = 0; h < 4; h++){
			if(p[h] == 1 && h == 0){
				if(pmap[point.x][point.y].sFlow == flow){
					j++;
				}else{
					p[h] = 0;
				}
			}else if(p[h] == 1 && h == 1){
				if(pmap[point.x][point.y].eFlow == flow){
					j++;
				}else{
					p[h] = 0;
				}
			}else if(p[h] == 1 && h == 2){
				if(pmap[point.x-1][point.y].sFlow == flow){
					j++;
				}else{
					p[h] = 0;
				}
			}else if(p[h] == 1 && h == 3){
				if(pmap[point.x][point.y-1].eFlow == flow){
					j++;
				}else{
					p[h] = 0;
				}
			}
		}
		if(j == 0){
			for(int h = 0; h < 4; h++){
				if(pp[h] == 1){
					j++;
				}
			}
			flag = 1;
		}
		Random rand = new Random();
		int m = -3;
		if(j != 0){
			m = rand.nextInt(j);
		}
		int k = 0;
		if(flag == 0){
			for(int h = 0; h < 4; h++){
				if(p[h] == 1){
					k++;
					if(k == m + 1){
						if(h == 0){
							if(light.light[point.x][point.y] == 1){
								if(light.ston == 2 && direction == 2){
									while(light.ston == 2){
										sleep(1);
									}
								}else if(light.etow == 1 && direction == 1){
									while(light.etow == 1){
										sleep(1);
									}
								}
							}
							direction = 2;
							pmap[point.x][point.y].sFlow++;
							point.setPoint(point.x+1, point.y);
							this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
						}else if(h == 1){
							if(light.light[point.x][point.y] == 1){
								if(light.ston == 1 && direction == 2){
									while(light.ston == 1){
										sleep(1);
									}
								}else if(light.etow == 2 && direction == 3){
									while(light.etow == 2){
										sleep(1);
									}
								}
							}
							direction = 3;
							pmap[point.x][point.y].eFlow++;
							point.setPoint(point.x, point.y+1);
							this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
						}else if( h == 2){
							if(light.light[point.x][point.y] == 1){
								if(light.ston == 2 && direction == 0){
									while(light.ston == 2){
										sleep(1);
									}
								}else if(light.etow == 1 && direction == 3){
									while(light.etow == 1){
										sleep(1);
									}
								}
							}
							direction = 0;
							pmap[point.x-1][point.y].sFlow++;
							point.setPoint(point.x-1, point.y);
							this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
						}else{
							if(light.light[point.x][point.y] == 1){
								if(light.ston == 1 && direction == 0){
									while(light.ston == 1){
										sleep(1);
									}
								}else if(light.etow == 2 && direction == 1){
									while(light.etow == 2){
										sleep(1);
									}
								}
							}
							direction = 1;
							pmap[point.x][point.y-1].eFlow++;
							point.setPoint(point.x, point.y-1);
							this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
						}
					}
				}
			}
		}else{
			for(int h = 0; h < 4; h++){
				if(pp[h] == 1){
					k++;
					if(k == m + 1){
						if(h == 0){
							if(light.light[point.x][point.y] == 1){
								if(light.ston == 2 && direction == 2){
									while(light.ston == 2){
										sleep(1);
									}
								}else if(light.etow == 1 && direction == 1){
									while(light.etow == 1){
										sleep(1);
									}
								}
							}
							direction = 2;
							pmap[point.x][point.y].sFlow++;
							point.setPoint(point.x+1, point.y);
							this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
						}else if(h == 1){
							if(light.light[point.x][point.y] == 1){
								if(light.ston == 1 && direction == 2){
									while(light.ston == 1){
										sleep(1);
									}
								}else if(light.etow == 2 && direction == 3){
									while(light.etow == 2){
										sleep(1);
									}
								}
							}
							direction = 3;
							pmap[point.x][point.y].eFlow++;
							point.setPoint(point.x, point.y+1);
							this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
						}else if( h == 2){
							if(light.light[point.x][point.y] == 1){
								if(light.ston == 2 && direction == 0){
									while(light.ston == 2){
										sleep(1);
									}
								}else if(light.etow == 1 && direction == 3){
									while(light.etow == 1){
										sleep(1);
									}
								}
							}
							direction = 0;
							pmap[point.x-1][point.y].sFlow++;
							point.setPoint(point.x-1, point.y);
							this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
						}else{
							if(light.light[point.x][point.y] == 1){
								if(light.ston == 1 && direction == 0){
									while(light.ston == 1){
										sleep(1);
									}
								}else if(light.etow == 2 && direction == 1){
									while(light.etow == 2){
										sleep(1);
									}
								}
							}
							direction = 1;
							pmap[point.x][point.y-1].eFlow++;
							point.setPoint(point.x, point.y-1);
							this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
						}
					}
				}
			}
		}
	}
	
	public synchronized void Grab() throws InterruptedException{
		/*@MODIFIES:this.pmap;this.point;this.req;
		 * @EFFECTS:req.path.size!=0 ==> 出租车当前坐标改变,出租车走过边流量加1,将req.path中第一个元素删除;
		 * 			sleep(1)未正常执行 ==> exceptional_behavior (InterruptedException);
		 * @THREAD_EFFECTS: \locked();
		 */
		if(!req.path.isEmpty()){
			Point p = req.path.get(0);
			if(p.x == point.x){
				if(p.y > point.y){
					if(light.light[point.x][point.y] == 1){
						if(light.ston == 1 && direction == 2){
							while(light.ston == 1){
								sleep(1);
							}
						}else if(light.etow == 2 && direction == 3){
							while(light.etow == 2){
								sleep(1);
							}
						}
					}
					direction = 3;
					pmap[point.x][point.y].eFlow++;
				}else{
					if(light.light[point.x][point.y] == 1){
						if(light.ston == 1 && direction == 0){
							while(light.ston == 1){
								sleep(1);
							}
						}else if(light.etow == 2 && direction == 1){
							while(light.etow == 2){
								sleep(1);
							}
						}
					}
					direction = 1;
					pmap[p.x][p.y].eFlow++;
				}
			}else{
				if(p.x > point.x){
					if(light.light[point.x][point.y] == 1){
						if(light.ston == 2 && direction == 2){
							while(light.ston == 2){
								sleep(1);
							}
						}else if(light.etow == 1 && direction == 1){
							while(light.etow == 1){
								sleep(1);
							}
						}
					}
					direction = 2;
					pmap[point.x][point.y].sFlow++;
				}else{
					if(light.light[point.x][point.y] == 1){
						if(light.ston == 2 && direction == 0){
							while(light.ston == 2){
								sleep(1);
							}
						}else if(light.etow == 1 && direction == 3){
							while(light.etow == 1){
								sleep(1);
							}
						}
					}
					direction = 0;
					pmap[p.x][p.y].sFlow++;
				}
			}
			point.setPoint(p.x, p.y);
			this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
			req.path.remove(0);
		}
	}
	
	public synchronized void Service() throws InterruptedException{
		/*@MODIFIES:this.pmap;this.point;
		 * @EFFECTS:req.spath.size!=0 ==> 出租车当前坐标改变，出租车走过边流量加1，将req.spath中最后一个元素删除;
		 * 			sleep(1)未正常执行 ==> exceptional_behavior (InterruptedException);
		 * @THREAD_EFFECTS: \locked();
		 */
		if(!req.spath.isEmpty()){
			Point p = req.spath.get(req.spath.size()-1);
			if(p.x == point.x){
				if(p.y > point.y){
					if(light.light[point.x][point.y] == 1){
						if(light.ston == 1 && direction == 2){
							while(light.ston == 1){
								sleep(1);
							}
						}else if(light.etow == 2 && direction == 3){
							while(light.etow == 2){
								sleep(1);
							}
						}
					}
					direction = 3;
					pmap[point.x][point.y].eFlow++;
				}else{
					if(light.light[point.x][point.y] == 1){
						if(light.ston == 1 && direction == 0){
							while(light.ston == 1){
								sleep(1);						}
						}else if(light.etow == 2 && direction == 1){
							while(light.etow == 2){
								sleep(1);
							}
						}
					}
					direction = 1;
					pmap[p.x][p.y].eFlow++;
				}
			}else{
				if(p.x > point.x){
					if(light.light[point.x][point.y] == 1){
						if(light.ston == 2 && direction == 2){
							while(light.ston == 2){
								sleep(1);
							}
						}else if(light.etow == 1 && direction == 1){
							while(light.etow == 1){
								sleep(1);
							}
						}
					}
					direction = 2;
					pmap[point.x][point.y].sFlow++;
				}else{
					if(light.light[point.x][point.y] == 1){
						if(light.ston == 2 && direction == 0){
							while(light.ston == 2){
								sleep(1);
							}
						}else if(light.etow == 1 && direction == 3){
							while(light.etow == 1){
								sleep(1);
							}
						}
					}
					direction = 0;
					pmap[p.x][p.y].sFlow++;
				}
			}
			point.setPoint(p.x, p.y);
			this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
			req.spath.remove(req.spath.size()-1);
		}
	}
	
	public void run(){
		/*@MODIFIES:this.status; this.signal; this.credit; this.point;this.req;
		 *@EFFECTS:this.status == 2 ==> (20s内每200ms调用一次Wait方法，20s后this.status = 0);
		 * 		   this.status == 0 ==> (1s后this.status = 2);
		 *         this.status == 1 ==> (this.req != null ==> (this.req.spath不为空时 ==> 每200ms调用一次Service方法;否则this.status = 1,1s后，this.status = 2, this.credit += 3));
		 *         this.status == 3 ==> (this.req != null ==> (this.req.path不为空时 ==> 每200ms调用一次Grab方法;否则this.status = 1,1s后，this.status = 2));
		 *         Wait() || Grab() || Serve()异常 ==> exceptional_behavior (InterruptedException);
		 */
		while(true){
			time1 = timer.getTime();
			double time2 = time1;
			if(status == 2){
				while(status == 2){
					if(timer.getTime() - time1 < 200){
						/*if(timer.getTime() - time2 >= 2){
							Wait();
							time2 = timer.getTime();
						}*/
						try {
							sleep(200);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							Wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						status = 0;
					}
				}
			}else if(status == 0){
				this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				status = 2;
				this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
			}else if(status == 1){
				if(req != null){
					if(!req.spath.isEmpty()){
						req.spath.remove(req.spath.size()-1);
						while(!req.spath.isEmpty()){
							/*if(signal == 1){
								try {
									sleep(10);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								signal = 0;
							}*/
							/*if(timer.getTime() - time2 >= 2){
								Service();
								time2 = timer.getTime();
							}*/
							try {
								sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								Service();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						//System.out.println(this.toString());
					}
				}
				status = 0;
				this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				credit += 3;
				status = 2;
				this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
			}else if(status == 3){
				if(req != null){
					if(!req.path.isEmpty()){
						req.path.remove(0);
						while(!req.path.isEmpty()){
							/*if(signal == 1){
								try {
									sleep(10);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								signal = 0;
							}*/
							/*if(timer.getTime() - time2 >= 2){
								Grab();
								time2 = timer.getTime();
							}*/
							try {
								sleep(200);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								Grab();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						//System.out.println(this.toString());
					}
				}
				status = 0;
				this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				status = 1;
				this.taxigui.SetTaxiStatus(id, new Point(point.x, point.y), status);
			}
		}
	}
	
	public void shortPath(Point s){
		/*@REQUIRES: s != null; 	
		 *@MODIFIES: pmap;
		 * @EFFECTS: (\all int i,j; 0 <= i <= 79, 0 <= j <= 79; pmap[i][j].dis == -1) ==> pmap中所有除s外的点都有唯一前驱点，都有一条到s点的最短路径且距离（dis）为该点到s点的最短路径长度
		 */
		 LinkedList<Point> queue = new LinkedList<Point>();
		 s.dis = 0;
		 queue.offer(s);
		 while(!queue.isEmpty()){
			 Point p = queue.poll();
			 if(map[p.x][p.y] == 1){
				 if(pmap[p.x][p.y+1].dis == -1){
					 pmap[p.x][p.y+1].dis = p.dis + 1;
					 queue.offer(pmap[p.x][p.y+1]);
					 pmap[p.x][p.y+1].pre = p;
					 /*if(d.equels(pmap[p.x][p.y+1])){
						 break;
					 }*/
				 }else if(pmap[p.x][p.y+1].dis == (pmap[p.x][p.y].dis + 1)){
					 Point pp = pmap[p.x][p.y+1].pre;
					 if(pp.x == p.x){
						 if(pmap[p.x][p.y+1].eFlow > pmap[p.x][p.y].eFlow){
							 pmap[p.x][p.y+1].pre = p;
						 }
					 }else{
						 if(pp.x == (p.x-1)){
							 if(pmap[p.x-1][p.y+1].sFlow > pmap[p.x][p.y].eFlow){
								 pmap[p.x][p.y+1].pre = p;
							 }
						 }else{
							 if(pmap[p.x][p.y+1].sFlow > pmap[p.x][p.y].eFlow){
								 pmap[p.x][p.y+1].pre = p;
							 }
						 }
					 } 
				 }
			 }else if(map[p.x][p.y] == 2){
				 if(pmap[p.x+1][p.y].dis == -1){
					 pmap[p.x+1][p.y].dis = p.dis + 1;
					 queue.offer(pmap[p.x+1][p.y]);
					 pmap[p.x+1][p.y].pre = p;
				 }else if(pmap[p.x+1][p.y].dis == (pmap[p.x][p.y].dis + 1)){
					 Point pp = pmap[p.x+1][p.y].pre;
					 if(pp.y == (p.y)){
						 if(pmap[p.x+1][p.y].sFlow > pmap[p.x][p.y].sFlow){
							 pmap[p.x+1][p.y].pre = p;
						 }
					 }else{
						 if(pp.y == (p.y-1)){
							 if(pmap[p.x+1][p.y-1].eFlow > pmap[p.x][p.y].sFlow){
								 pmap[p.x+1][p.y].pre = p;
							 }
						 }else{
							 if(pmap[p.x+1][p.y].eFlow > pmap[p.x][p.y].sFlow){
								 pmap[p.x+1][p.y].pre = p;
							 }
						 }
					 }
				 }
			 }else if(map[p.x][p.y] == 3){
				 if(pmap[p.x][p.y+1].dis == -1){
					 pmap[p.x][p.y+1].dis = p.dis + 1;
					 queue.offer(pmap[p.x][p.y+1]);
					 pmap[p.x][p.y+1].pre = p;
					 /*if(d.equels(pmap[p.x][p.y+1])){
						 break;
					 }*/
				 }else if(pmap[p.x][p.y+1].dis == (pmap[p.x][p.y].dis + 1)){
					 Point pp = pmap[p.x][p.y+1].pre;
					 if(pp.x == p.x){
						 if(pmap[p.x][p.y+1].eFlow > pmap[p.x][p.y].eFlow){
							 pmap[p.x][p.y+1].pre = p;
						 }
					 }else{
						 if(pp.x == (p.x-1)){
							 if(pmap[p.x-1][p.y+1].sFlow > pmap[p.x][p.y].eFlow){
								 pmap[p.x][p.y+1].pre = p;
							 }
						 }else{
							 if(pmap[p.x][p.y+1].sFlow > pmap[p.x][p.y].eFlow){
								 pmap[p.x][p.y+1].pre = p;
							 }
						 }
					 }
				 }
				 if(pmap[p.x+1][p.y].dis == -1){
					 pmap[p.x+1][p.y].dis = p.dis + 1;
					 queue.offer(pmap[p.x+1][p.y]);
					 pmap[p.x+1][p.y].pre = p;
					 /*if(d.equels(pmap[p.x+1][p.y])){
						 break;
					 }*/
				 }else if(pmap[p.x+1][p.y].dis == (pmap[p.x][p.y].dis + 1)){
					 Point pp = pmap[p.x+1][p.y].pre;
					 if(pp.y == (p.y)){
						 if(pmap[p.x+1][p.y].sFlow > pmap[p.x][p.y].sFlow){
							 pmap[p.x+1][p.y].pre = p;
						 }
					 }else{
						 if(pp.y == (p.y-1)){
							 if(pmap[p.x+1][p.y-1].eFlow > pmap[p.x][p.y].sFlow){
								 pmap[p.x+1][p.y].pre = p;
							 }
						 }else{
							 if(pmap[p.x+1][p.y].eFlow > pmap[p.x][p.y].sFlow){
								 pmap[p.x+1][p.y].pre = p;
							 }
						 }
					 }
				 }
			 }
			 if(p.x > 0){
				 if(map[p.x-1][p.y] >= 2 && pmap[p.x-1][p.y].dis == -1){
					 pmap[p.x-1][p.y].dis = p.dis + 1;
					 queue.offer(pmap[p.x-1][p.y]);
					 pmap[p.x-1][p.y].pre = p;
				 }else if(map[p.x-1][p.y] >= 2 && pmap[p.x-1][p.y].dis == (pmap[p.x][p.y].dis + 1)){
					 Point pp = pmap[p.x-1][p.y].pre;
					 if(pp.y == p.y){
						 if(pmap[p.x-2][p.y].sFlow > pmap[p.x-1][p.y].sFlow){
							 pmap[p.x-1][p.y].pre = p;
						 }
					 }else{
						 if(pp.y == (p.y - 1)){
							 if(pmap[p.x-1][p.y-1].eFlow > pmap[p.x-1][p.y].sFlow){
								 pmap[p.x-1][p.y].pre = p;
							 }
						 }else{
							 if(pmap[p.x-1][p.y].eFlow > pmap[p.x-1][p.y].sFlow){
								 pmap[p.x-1][p.y].pre = p;
							 }
						 }
					 }
				 }
			 }
			 if(p.y > 0){
				 if((map[p.x][p.y-1] == 1 || map[p.x][p.y-1] == 3) && pmap[p.x][p.y-1].dis == -1){
					 pmap[p.x][p.y-1].dis = p.dis + 1;
					 queue.offer(pmap[p.x][p.y-1]);
					 pmap[p.x][p.y-1].pre = p;
				 }else if((map[p.x][p.y-1] == 1 || map[p.x][p.y-1] == 3) && pmap[p.x][p.y-1].dis == (pmap[p.x][p.y].dis + 1)){
					 Point pp = pmap[p.x][p.y-1].pre;
					 if(pp.x == p.x){
						 if(pmap[p.x][p.y-2].eFlow > pmap[p.x][p.y-1].eFlow){
							 pmap[p.x][p.y-1].pre = p;
						 }
					 }else{
						 if(pp.x == (p.x - 1)){
							 if(pmap[p.x-1][p.y-1].sFlow > pmap[p.x][p.y-1].eFlow){
								 pmap[p.x][p.y-1].pre = p;
							 }
						 }else{
							 if(pmap[p.x][p.y-1].sFlow > pmap[p.x][p.y-1].eFlow){
								 pmap[p.x][p.y-1].pre = p;
							 }
						 }
					 }
				 }
			 }
		 }
		//return pmap[d.x][d.y].dis; 
	}
	
	public void pointFree(){
		/*@MODIFIES:pmap;
		 * @EFFECTS:(\all int i,j; 0 <= i <= 79, 0 <= j <= 79) ==> pmap[i][j].dis == -1, pmap[i][j].pre = null,  pmap2[i][j].dis == -1, pmap2[i][j].pre = null;
		 */
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				pmap[i][j].pre = null;
				pmap[i][j].dis = -1; 
				pmap2[i][j].pre = null;
				pmap2[i][j].dis = -1; 
			}
		}
	}
	
	public void flowFree(){
		/*@MODIFIES:pmap;
		 * @EFFECTS:(\all int i,j; 0 <= i <= 79, 0 <= j <= 79) ==> pmap[i][j].eFlow == 0, pmap[i][j].sFlow = 0;
		 */
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				pmap[i][j].eFlow = 0;
				pmap[i][j].sFlow = 0; 
			}
		}
	}
	
	public void getInformation(){
		//@EFFECTS: System.out.println("not a special cab");
		System.out.println("not a special cab");
	}
	
	public String toString(){
		//@EFFECTS:\result == timer.getTime() + ": [cab" + id + "--> " + "position: " + point .toString() + ", status: " + status + ", credit: " + credit + "]";
		String str = timer.getTime() + ": [cab" + id + "--> " + "position: " + point .toString() + ", status: " + status + ", credit: " + credit + "]";
		return str;
	}
}
