package taxi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class SpecialCab extends Cab{
	/*@overview: This class is extends from Cab. According to passengers' requests to complete the task of pick up the passengers and send them to destination in shortest path.When there is no request,the cab can move at random.
	 * @invariant: map!=null && (\all int i,j;0<=i<=79,0<=j<=79;0<=map[i][j]<=3)  && map2!=null && (\all int i,j;0<=i<=79,0<=j<=79;0<=map2[i][j]<=3) && pmap!=null && (\all int i,j;0<=i<=79,0<=j<=79;pmap[i][j].repOK==true)  &&pmap2!=null && (\all int i,j;0<=i<=79,0<=j<=79;pmap2[i][j].repOK==true) && light.repOK()==true && id>=0 && id<100
	 */
	toFile f = new toFile();
	
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
	
	public SpecialCab(int id, int[][] map, Point[][] pmap, TaxiGUI taxigui, Light light, int[][] map2,
			Point[][] pmap2) {
		/*@MODIFIES:this.map; this.point; this.status; this.credit; this.id; this.map; this.pmap; this.req; this.time1; this.timer; this.time; this.signal; this.direction; this.map2; this.father; this.pmap2; this.reqlist;
		 * @EFFECTS: 任意情况 ==> super(id, map, pmap, taxigui, light, map2, pmap2);
		 * 			  任意情况 ==> this.father = false;
		 */
		super(id, map, pmap, taxigui, light, map2, pmap2);
		this.father = false;
	}

	public void getInformation(){
		/*@MODIFIES: 相应文件
		 * @EFFECTS: while it.hasNext() ==> Request req = it.next(), 将req中相应信息输出到相应文件;
		 */
		Iterator<Request> it = this.reqlist.iterator();
		while(it.hasNext()){
			Request req = it.next();
			f.toFile("请求" + req.id + "\r\n", "specialcab" + this.id + ".txt", true);
			f.toFile("请求产生时刻：" + req.time + "\r\n", "specialcab" + this.id + ".txt", true);
			f.toFile("请求发出位置：" + req.src + "\r\n", "specialcab" + this.id + ".txt", true);
			f.toFile("请求目的地位置：" + req.dst + "\r\n", "specialcab" + this.id + ".txt", true);
			f.toFile("抢到单时出租车所处位置：" + req.path2.get(0) + "\r\n", "specialcab" + this.id + ".txt", true);
			f.toFile("接客路径：" + "\r\n", "specialcab" + this.id + ".txt", true);
			for(int n = 0; n < req.path2.size(); n++){
				if(n != req.path2.size() - 1){
					//System.out.print(req.path.get(n) + "-->");
					f.toFile(req.path2.get(n) + "-->", "specialcab" + this.id + ".txt", true);
				}else{
					//System.out.print(req.path.get(n));
					f.toFile(req.path2.get(n) + "\r\n", "specialcab" + this.id + ".txt", true);
				}
			}
			f.toFile("送客路径：" + "\r\n", "specialcab" + this.id + ".txt", true);
			for(int n = req.spath2.size() - 1; n >= 0; n--){
				if(n != 0){
					//System.out.print(req.spath.get(n) + "-->");
					f.toFile(req.spath2.get(n) + "-->", "specialcab" + this.id + ".txt", true);
				}else{
					//System.out.print(req.spath.get(n));
					f.toFile(req.spath2.get(n) + "\r\n", "specialcab" + this.id + ".txt", true);
				}
			}
			f.toFile("\r\n" + "\r\n" + "*******************************************" + "\r\n", "specialcab" + this.id + ".txt", true);
			it.remove();
		}
	}
	
	public void shortPath(Point s){
		/*@REQUIRES: s != null; 	
		 *@MODIFIES: pmap2;
		 * @EFFECTS: (\all int i,j; 0 <= i <= 79, 0 <= j <= 79; pmap2[i][j].dis == -1) ==> pmap2中所有除s外的点都有唯一前驱点，都有一条到s点的最短路径且距离（dis）为该点到s点的最短路径长度
		 */
		 LinkedList<Point> queue = new LinkedList<Point>();
		 s.dis = 0;
		 queue.offer(s);
		 while(!queue.isEmpty()){
			 Point p = queue.poll();
			 if(map2[p.x][p.y] == 1){
				 if(pmap2[p.x][p.y+1].dis == -1){
					 pmap2[p.x][p.y+1].dis = p.dis + 1;
					 queue.offer(pmap2[p.x][p.y+1]);
					 pmap2[p.x][p.y+1].pre = p;
					 /*if(d.equels(pmap[p.x][p.y+1])){
						 break;
					 }*/
				 }else if(pmap2[p.x][p.y+1].dis == (pmap2[p.x][p.y].dis + 1)){
					 Point pp = pmap2[p.x][p.y+1].pre;
					 if(pp.x == p.x){
						 if(pmap2[p.x][p.y+1].eFlow > pmap2[p.x][p.y].eFlow){
							 pmap2[p.x][p.y+1].pre = p;
						 }
					 }else{
						 if(pp.x == (p.x-1)){
							 if(pmap2[p.x-1][p.y+1].sFlow > pmap2[p.x][p.y].eFlow){
								 pmap2[p.x][p.y+1].pre = p;
							 }
						 }else{
							 if(pmap2[p.x][p.y+1].sFlow > pmap2[p.x][p.y].eFlow){
								 pmap2[p.x][p.y+1].pre = p;
							 }
						 }
					 } 
				 }
			 }else if(map2[p.x][p.y] == 2){
				 if(pmap2[p.x+1][p.y].dis == -1){
					 pmap2[p.x+1][p.y].dis = p.dis + 1;
					 queue.offer(pmap2[p.x+1][p.y]);
					 pmap2[p.x+1][p.y].pre = p;
				 }else if(pmap2[p.x+1][p.y].dis == (pmap2[p.x][p.y].dis + 1)){
					 Point pp = pmap2[p.x+1][p.y].pre;
					 if(pp.y == (p.y)){
						 if(pmap2[p.x+1][p.y].sFlow > pmap2[p.x][p.y].sFlow){
							 pmap2[p.x+1][p.y].pre = p;
						 }
					 }else{
						 if(pp.y == (p.y-1)){
							 if(pmap2[p.x+1][p.y-1].eFlow > pmap2[p.x][p.y].sFlow){
								 pmap2[p.x+1][p.y].pre = p;
							 }
						 }else{
							 if(pmap2[p.x+1][p.y].eFlow > pmap2[p.x][p.y].sFlow){
								 pmap2[p.x+1][p.y].pre = p;
							 }
						 }
					 }
				 }
			 }else if(map2[p.x][p.y] == 3){
				 if(pmap2[p.x][p.y+1].dis == -1){
					 pmap2[p.x][p.y+1].dis = p.dis + 1;
					 queue.offer(pmap2[p.x][p.y+1]);
					 pmap2[p.x][p.y+1].pre = p;
					 /*if(d.equels(pmap[p.x][p.y+1])){
						 break;
					 }*/
				 }else if(pmap2[p.x][p.y+1].dis == (pmap2[p.x][p.y].dis + 1)){
					 Point pp = pmap2[p.x][p.y+1].pre;
					 if(pp.x == p.x){
						 if(pmap2[p.x][p.y+1].eFlow > pmap2[p.x][p.y].eFlow){
							 pmap2[p.x][p.y+1].pre = p;
						 }
					 }else{
						 if(pp.x == (p.x-1)){
							 if(pmap2[p.x-1][p.y+1].sFlow > pmap2[p.x][p.y].eFlow){
								 pmap2[p.x][p.y+1].pre = p;
							 }
						 }else{
							 if(pmap2[p.x][p.y+1].sFlow > pmap2[p.x][p.y].eFlow){
								 pmap2[p.x][p.y+1].pre = p;
							 }
						 }
					 }
				 }
				 if(pmap2[p.x+1][p.y].dis == -1){
					 pmap2[p.x+1][p.y].dis = p.dis + 1;
					 queue.offer(pmap2[p.x+1][p.y]);
					 pmap2[p.x+1][p.y].pre = p;
					 /*if(d.equels(pmap[p.x+1][p.y])){
						 break;
					 }*/
				 }else if(pmap2[p.x+1][p.y].dis == (pmap2[p.x][p.y].dis + 1)){
					 Point pp = pmap2[p.x+1][p.y].pre;
					 if(pp.y == (p.y)){
						 if(pmap2[p.x+1][p.y].sFlow > pmap2[p.x][p.y].sFlow){
							 pmap2[p.x+1][p.y].pre = p;
						 }
					 }else{
						 if(pp.y == (p.y-1)){
							 if(pmap2[p.x+1][p.y-1].eFlow > pmap2[p.x][p.y].sFlow){
								 pmap2[p.x+1][p.y].pre = p;
							 }
						 }else{
							 if(pmap2[p.x+1][p.y].eFlow > pmap2[p.x][p.y].sFlow){
								 pmap2[p.x+1][p.y].pre = p;
							 }
						 }
					 }
				 }
			 }
			 if(p.x > 0){
				 if(map2[p.x-1][p.y] >= 2 && pmap2[p.x-1][p.y].dis == -1){
					 pmap2[p.x-1][p.y].dis = p.dis + 1;
					 queue.offer(pmap2[p.x-1][p.y]);
					 pmap2[p.x-1][p.y].pre = p;
				 }else if(map2[p.x-1][p.y] >= 2 && pmap2[p.x-1][p.y].dis == (pmap2[p.x][p.y].dis + 1)){
					 Point pp = pmap2[p.x-1][p.y].pre;
					 if(pp.y == p.y){
						 if(pmap2[p.x-2][p.y].sFlow > pmap2[p.x-1][p.y].sFlow){
							 pmap2[p.x-1][p.y].pre = p;
						 }
					 }else{
						 if(pp.y == (p.y - 1)){
							 if(pmap2[p.x-1][p.y-1].eFlow > pmap2[p.x-1][p.y].sFlow){
								 pmap2[p.x-1][p.y].pre = p;
							 }
						 }else{
							 if(pmap2[p.x-1][p.y].eFlow > pmap2[p.x-1][p.y].sFlow){
								 pmap2[p.x-1][p.y].pre = p;
							 }
						 }
					 }
				 }
			 }
			 if(p.y > 0){
				 if((map2[p.x][p.y-1] == 1 || map2[p.x][p.y-1] == 3) && pmap2[p.x][p.y-1].dis == -1){
					 pmap2[p.x][p.y-1].dis = p.dis + 1;
					 queue.offer(pmap2[p.x][p.y-1]);
					 pmap2[p.x][p.y-1].pre = p;
				 }else if((map2[p.x][p.y-1] == 1 || map2[p.x][p.y-1] == 3) && pmap2[p.x][p.y-1].dis == (pmap2[p.x][p.y].dis + 1)){
					 Point pp = pmap2[p.x][p.y-1].pre;
					 if(pp.x == p.x){
						 if(pmap2[p.x][p.y-2].eFlow > pmap2[p.x][p.y-1].eFlow){
							 pmap2[p.x][p.y-1].pre = p;
						 }
					 }else{
						 if(pp.x == (p.x - 1)){
							 if(pmap2[p.x-1][p.y-1].sFlow > pmap2[p.x][p.y-1].eFlow){
								 pmap2[p.x][p.y-1].pre = p;
							 }
						 }else{
							 if(pmap2[p.x][p.y-1].sFlow > pmap2[p.x][p.y-1].eFlow){
								 pmap2[p.x][p.y-1].pre = p;
							 }
						 }
					 }
				 }
			 }
		 }
		//return pmap[d.x][d.y].dis; 
	}
	
}
