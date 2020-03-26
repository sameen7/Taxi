package taxi;

import java.util.ArrayList;

public class Scheduler extends Thread{
	/*@overview: Get the request from queue one by one and respond it.When there is a car which can grab it,assign it to the car.When there is no car respond, get rid of it.
	 * @invariant: (\all int i;0<=i<=99;cab[i].repOK==true) && pmap!=null && (\all int i,j;0<=i<=79,0<=j<=79;pmap[i][j].repOK==true) && pmap2!=null && (\all int i,j;0<=i<=79,0<=j<=79;pmap2[i][j].repOK==true)
	 */
	Timer timer;
	Queue queue;
	Cab[] cab;
	Point pmap[][];
	Point pmap2[][];
	toFile f = new toFile();
	
	public boolean repOK(){
		/*@EFFECTS: \result == invariant(this)
		 */
		for(int i = 0; i < 100; i++){
			if(!cab[i].repOK()){
				return false;
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
		return true;
	}
	
	public Scheduler(Queue queue, Cab[] cab, Point pmap[][], Point pmap2[][]){
		/*@MODIFIES: this.queue; this.cab; this.pmap;
		 * @EFFECTS: 任意情况 ==> this.queue = queue;
		 * 			  任意情况 ==> this.cab = cab;
		 * 			  任意情况 ==> this.pmap = pmap;
		 * 			  任意情况 ==> this.pmap2 = pmap2;
		 */
		this.queue = queue;
		this.cab = cab;
		this.pmap = pmap;
		this.pmap2 = pmap2;
	}
	public void run(){
		/*MODIFIES: this.queue; this.f; this.pmap; this.cab[j].credit(参与抢单的出租车的信用度);
		 * EFFECTS: queue.size != 0 ==> (将队列中每个请求发出时相应信息更新进文件，记录抢单出租车，
		 * 								抢单时间窗结束后若有抢单出租车则更新相应信息进文件并选择接单出租车，更新pmap/pmap2中元素相应信息，响应完该请求后将其从请求队列中删除); 
		 */
		while(true){
			if(!queue.isEmpty()){
				for(int i = 0; i < queue.getSize(); i++){
					Request req = queue.getRequest(i);
					for(int j = 0; j < 100; j++){
						synchronized(cab[j]){
							//@THREAD_EFFECTS: \locked(cab[j]);
							if(req.erea.inErea(cab[j].point) && !req.flag){
								//System.out.println("请求发出时方圆四里出租车信息：" + cab[j]);
								f.toFile("请求发出时方圆四里出租车信息：" + cab[j] + "\r\n", req + ".txt", true);
							}
							if(cab[j].status == 2 && req.erea.inErea(cab[j].point)){
								if(!req.pass(cab[j])){
									cab[j].credit++;
									req.cablist.add(cab[j]);
								}
							}
						}
					}
					req.flag = true;
					if(timer.getTime() - req.time >= 30){
						//synchronized(req){
							if(!req.cablist.isEmpty()){
								for(int m = 0; m < req.cablist.size(); m++){
									//System.out.println("cab" + req.cablist.get(m).id);
									f.toFile("抢单时间窗内所有抢单出租车：" + "cab" + req.cablist.get(m).id  + ": " + req.cablist.get(m) + "\r\n", req + ".txt", true);
								}
								choose(req);
								cab[0].pointFree();
							}else{
								System.out.println(req + "no cab in service!");
								f.toFile(req + "no cab in service!" + "\r\n", req + ".txt", true);
								f.toFile("\r\n" + "\r\n" + "*******************************************" + "\r\n", req + ".txt", true);
							}
							queue.remove(i);
							i--;
						//}
					}
				}
			}
		}
	}
	
	
	
	public void choose(Request req){
		/*@REQUIRES： req.cablist.size != 0;
		 *@MODIFIES: this.f; req; this.pmap; this.pmap2; this.cab[i](选择接单的出租车)；
		 * @EFFECTS: (\all int i; 0 <= i <= req.cablist.size(); req.cablist.get(i).status == 2) ==> 
		 * 			 (选择信用度最高的车，若信用度最高的有多辆则选择距乘客出发点最近的车，更新选择车的状态，更新相应信息进相应文件，
		 * 			  设置req的最短接单路径和最短服务路径，将req发送给选择的车，更新相应信息进相应文件；若没有可接单的车，更新相应信息进相应文件);
		 */
		int max = 0;
		ArrayList<Cab> list = new ArrayList<Cab>();
		for(int i = 0; i < req.cablist.size(); i++){
			if(req.cablist.get(i).credit >= max && req.cablist.get(i).status == 2){
				max = req.cablist.get(i).credit;
			}
		}
		for(int i = 0; i < req.cablist.size(); i++){
			if(req.cablist.get(i).credit == max){
				list.add(req.cablist.get(i));
			}
		}
		//System.out.println(req.cablist.size());
		//System.out.println(list.size());
		if(list.size() == 1){
			System.out.println(req + ": cab" + list.get(0).id + " is choosen");
			f.toFile("cab" + list.get(0).id + " is choosen" + "\r\n", req + ".txt", true);
			f.toFile("cab" + list.get(0).id + ": " + list.get(0) + "\r\n", req + ".txt", true);
			if(list.get(0).father){
				list.get(0).shortPath(pmap[req.src.x][req.src.y]);
				req.spath.add(pmap[req.dst.x][req.dst.y]);
				Point pretp = pmap[req.dst.x][req.dst.y].pre;
				while(pretp != null){
					req.spath.add(pretp);
					pretp = pretp.pre;
				}
				req.path.add(pmap[list.get(0).point.x][list.get(0).point.y]);
				Point pretemp = pmap[list.get(0).point.x][list.get(0).point.y].pre;
				while(pretemp != null){
					req.path.add(pretemp);
					pretemp = pretemp.pre;
				}
				list.get(0).req = req;
				list.get(0).status = 3;
				f.toFile("接客路径： " + pmap[list.get(0).point.x][list.get(0).point.y].dis + "\r\n", req + ".txt", true);
				for(int n = 0; n < req.path.size(); n++){
					if(n != req.path.size() - 1){
						//System.out.print(req.path.get(n) + "-->");
						f.toFile(req.path.get(n) + "-->", req + ".txt", true);
					}else{
						//System.out.print(req.path.get(n));
						f.toFile(req.path.get(n) + "\r\n", req + ".txt", true);
					}
				}
				//System.out.println();
				f.toFile("送客路径： " + pmap[req.dst.x][req.dst.y].dis + "\r\n", req + ".txt", true);
				for(int n = req.spath.size() - 1; n >= 0; n--){
					if(n != 0){
						//System.out.print(req.spath.get(n) + "-->");
						f.toFile(req.spath.get(n) + "-->", req + ".txt", true);
					}else{
						//System.out.print(req.spath.get(n));
						f.toFile(req.spath.get(n) + "\r\n", req + ".txt", true);
					}
				}
				f.toFile("\r\n" + "\r\n" + "*******************************************" + "\r\n", req + ".txt", true);
				//System.out.println();
			}else{
				list.get(0).shortPath(pmap2[req.src.x][req.src.y]);
				req.spath.add(pmap2[req.dst.x][req.dst.y]);
				Point pretp = pmap2[req.dst.x][req.dst.y].pre;
				while(pretp != null){
					req.spath.add(pretp);
					pretp = pretp.pre;
				}
				if(!req.spath.isEmpty()){
					for(int h = 0; h < req.spath.size(); h++){
						req.spath2.add(req.spath.get(h));
					}
				}
				
				req.path.add(pmap2[list.get(0).point.x][list.get(0).point.y]);
				Point pretemp = pmap2[list.get(0).point.x][list.get(0).point.y].pre;
				while(pretemp != null){
					req.path.add(pretemp);
					pretemp = pretemp.pre;
				}
				
				if(!req.path.isEmpty()){
					for(int h = 0; h < req.path.size(); h++){
						req.path2.add(req.path.get(h));
					}
				}
				list.get(0).req = req;
				list.get(0).reqlist.add(req);
				list.get(0).status = 3;
				f.toFile("接客路径： " + pmap2[list.get(0).point.x][list.get(0).point.y].dis + "\r\n", req + ".txt", true);
				for(int n = 0; n < req.path.size(); n++){
					if(n != req.path.size() - 1){
						//System.out.print(req.path.get(n) + "-->");
						f.toFile(req.path.get(n) + "-->", req + ".txt", true);
					}else{
						//System.out.print(req.path.get(n));
						f.toFile(req.path.get(n) + "\r\n", req + ".txt", true);
					}
				}
				//System.out.println();
				f.toFile("送客路径： " + pmap2[req.dst.x][req.dst.y].dis + "\r\n", req + ".txt", true);
				for(int n = req.spath.size() - 1; n >= 0; n--){
					if(n != 0){
						//System.out.print(req.spath.get(n) + "-->");
						f.toFile(req.spath.get(n) + "-->", req + ".txt", true);
					}else{
						//System.out.print(req.spath.get(n));
						f.toFile(req.spath.get(n) + "\r\n", req + ".txt", true);
					}
				}
				f.toFile("\r\n" + "\r\n" + "*******************************************" + "\r\n", req + ".txt", true);
				//System.out.println();
			}
			
		}else if(list.size() > 1){
			int dis = 1000000;
			list.get(0).shortPath(pmap[req.src.x][req.src.y]);
			for(int i = 0; i < list.size(); i++){
				if(!list.get(i).father){
					list.get(i).shortPath(pmap2[req.src.x][req.src.y]);
					break;
				}
			}
			
			for(int i = 0; i < list.size(); i++){
				if(list.get(i).father){
					if(pmap[list.get(i).point.x][list.get(i).point.y].dis < dis){
						dis = pmap[list.get(i).point.x][list.get(i).point.y].dis;
					}
				}else{
					if(pmap2[list.get(i).point.x][list.get(i).point.y].dis < dis){
						dis = pmap2[list.get(i).point.x][list.get(i).point.y].dis;
					}
				}
			}
			for(int i = 0; i < list.size(); i++){
				if(list.get(i).father){
					if(pmap[list.get(i).point.x][list.get(i).point.y].dis == dis){
						
						req.spath.add(pmap[req.dst.x][req.dst.y]);
						Point pretp = pmap[req.dst.x][req.dst.y].pre;
						while(pretp != null){
							req.spath.add(pretp);
							pretp = pretp.pre;
						}
						
						System.out.println(req + ": cab" + list.get(i).id + " is choosen");
						f.toFile("cab" + list.get(i).id + " is choosen" + "\r\n", req + ".txt", true);
						f.toFile("cab" + list.get(i).id + ": " + list.get(i) + "\r\n", req + ".txt", true);
						req.path.add(pmap[list.get(i).point.x][list.get(i).point.y]);
						Point pretemp = pmap[list.get(i).point.x][list.get(i).point.y].pre;
						while(pretemp != null){
							req.path.add(pretemp);
							pretemp = pretemp.pre;
						}
						list.get(i).req = req;
						list.get(i).reqlist.add(req);
						list.get(i).status = 3;
						f.toFile("接客路径： " + pmap[list.get(i).point.x][list.get(i).point.y].dis + "\r\n", req + ".txt", true);
						for(int n = 0; n < req.path.size(); n++){
							if(n != req.path.size() - 1){
								//System.out.print(req.path.get(n) + "-->");
								f.toFile(req.path.get(n) + "-->", req + ".txt", true);
							}else{
								//System.out.print(req.path.get(n));
								f.toFile(req.path.get(n) + "\r\n", req + ".txt", true);
							}
						}
						//System.out.println();
						f.toFile("送客路径： " + pmap[req.dst.x][req.dst.y].dis + "\r\n", req + ".txt", true);
						for(int n = req.spath.size() - 1; n >= 0; n--){
							if(n != 0){
								//System.out.print(req.spath.get(n) + "-->");
								f.toFile(req.spath.get(n) + "-->", req + ".txt", true);
							}else{
								//System.out.print(req.spath.get(n));
								f.toFile(req.spath.get(n) + "\r\n", req + ".txt", true);
							}
						}
						f.toFile("\r\n" + "\r\n" + "*******************************************" + "\r\n", req + ".txt", true);
						//System.out.println();
						break;
					}else{
						if(pmap2[list.get(i).point.x][list.get(i).point.y].dis == dis){
							
							req.spath.add(pmap2[req.dst.x][req.dst.y]);
							Point pretp = pmap2[req.dst.x][req.dst.y].pre;
							while(pretp != null){
								req.spath.add(pretp);
								pretp = pretp.pre;
							}
							if(!req.spath.isEmpty()){
								for(int h = 0; h < req.spath.size(); h++){
									req.spath2.add(req.spath.get(h));
								}
							}
							System.out.println(req + ": cab" + list.get(i).id + " is choosen");
							f.toFile("cab" + list.get(i).id + " is choosen" + "\r\n", req + ".txt", true);
							f.toFile("cab" + list.get(i).id + ": " + list.get(i) + "\r\n", req + ".txt", true);
							req.path.add(pmap2[list.get(i).point.x][list.get(i).point.y]);
							Point pretemp = pmap2[list.get(i).point.x][list.get(i).point.y].pre;
							while(pretemp != null){
								req.path.add(pretemp);
								pretemp = pretemp.pre;
							}
							if(!req.path.isEmpty()){
								for(int h = 0; h < req.path.size(); h++){
									req.path2.add(req.path.get(h));
								}
							}
							list.get(i).req = req;
							list.get(i).reqlist.add(req);
							list.get(i).status = 3;
							f.toFile("接客路径： " + pmap2[list.get(i).point.x][list.get(i).point.y].dis + "\r\n", req + ".txt", true);
							for(int n = 0; n < req.path.size(); n++){
								if(n != req.path.size() - 1){
									//System.out.print(req.path.get(n) + "-->");
									f.toFile(req.path.get(n) + "-->", req + ".txt", true);
								}else{
									//System.out.print(req.path.get(n));
									f.toFile(req.path.get(n) + "\r\n", req + ".txt", true);
								}
							}
							//System.out.println();
							f.toFile("送客路径： " + pmap2[req.dst.x][req.dst.y].dis + "\r\n", req + ".txt", true);
							for(int n = req.spath.size() - 1; n >= 0; n--){
								if(n != 0){
									//System.out.print(req.spath.get(n) + "-->");
									f.toFile(req.spath.get(n) + "-->", req + ".txt", true);
								}else{
									//System.out.print(req.spath.get(n));
									f.toFile(req.spath.get(n) + "\r\n", req + ".txt", true);
								}
							}
							f.toFile("\r\n" + "\r\n" + "*******************************************" + "\r\n", req + ".txt", true);
							//System.out.println();
							break;
						}
					}
				}
			}
		}else{
			System.out.println(req + "no cab in service!");
			f.toFile(req + "no cab in service!" + "\r\n", req + ".txt", true);
			f.toFile("\r\n" + "\r\n" + "*******************************************" + "\r\n", req + ".txt", true);
		}
	}
}
