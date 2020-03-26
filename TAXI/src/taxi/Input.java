package taxi;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Input extends Thread{
	/*@overview: Distinguish the types of the instructions the users input and respond it. 
	 * @invariant: (\all int i;0<=i<=99;cab[i].repOK==true)
	 */
	Timer timer;
	Queue queue;
	Cab[] cab;
	TaxiGUI taxigui;
	toFile f = new toFile();
	
	public boolean repOK(){
		/*@EFFECTS: \result == invariant(this)
		 */
		for(int i = 0; i < 100; i++){
			if(!cab[i].repOK()){
				return false;
			}
		}
		return true;
	}
	
	public Input(Queue queue, Cab[] cab, TaxiGUI taxigui){
		/*@MODIFIES: this.queue; this.cab;
		 * @EFFECTS: 任意情况 ==> this.queue = queue;
		 * 			  任意情况 ==> this.cab = cab;
		 */
		this.queue = queue;
		this.cab = cab;
		this.taxigui = taxigui;
	}
	
	public static boolean isNumeric(String str){
		//@EFFECTS: \result == (str是否是数字);
		if(str.equals(null) || str == "" || str.equals("")){
			return false;
		}
		  for (int i = 0; i < str.length(); i++){
		   if (!Character.isDigit(str.charAt(i))){
		    return false;
		   }
		  }
		  return true;
		 }
	
	public void run(){
		/*@MODIFIES: this.queue; this.cab;
		 * @EFFECTS: m1.matches ==> 如果请求合法，加入请求队列；否则进行相应报错;
		 * 			 m2.matches ==> 如果请求合法，关闭或打开相应道路，并判断是否需要重新计算正在服务状态或准备服务状态的车的服务路径，若需要则重新计算;
		 * 			 m3.matches ==> 如果请求合法， 调用特殊出租车的迭代器方法输出信息到相应文件;
		 * 			 输入为0-99内的数字 ==> 输出以该数字为id的出租车的相关信息;
		 * 			 输入为“stop” ==> 输出当前状态为0的所有出租车;
		 * 			 输入为“wait” ==> 输出当前状态为2的所有出租车;
		 * 			 输入为“serve” ==> 输出当前状态为1的所有出租车;
		 * 			 输入为“grab” ==> 输出当前状态为3的所有出租车; 
		 * 			 输入出现异常 ==> exceptional_behavior (Exception);
		 */
		String input = new String();
		double ct = 0;
		Scanner in = new Scanner(System.in);
		try{
			input = in.nextLine().replace(" ", "");
			ct = this.timer.getTime();
		}catch(Exception e){
			//System.exit(0);
		};
		int p = 1;
		while(!input.equals("run")){
			String[] str = input.split(";");
			int num = str.length;
			double time = ct;
			for(int i = 0; i < num; i++){
				Pattern pat1 = Pattern.compile("\\[CR,\\((\\+?\\d+),(\\+?\\d+)\\),\\((\\+?\\d+),(\\+?\\d+)\\)\\]");
				Pattern pat2 = Pattern.compile("\\[\\((\\+?\\d+),(\\+?\\d+)\\),\\((\\+?\\d+),(\\+?\\d+)\\),(\\+?\\d+)\\]");
				Pattern pat3 = Pattern.compile("special\\+?\\d+");
				Matcher m1 = pat1.matcher(str[i]);
				Matcher m2 = pat2.matcher(str[i]);
				Matcher m3 = pat3.matcher(str[i]);
				if(m1.matches()){
					try{
						int x1 = Integer.parseInt(m1.group(1));
						int y1 = Integer.parseInt(m1.group(2));
						int x2 = Integer.parseInt(m1.group(3));
						int y2 = Integer.parseInt(m1.group(4));
						if(x1 >= 0 && x1 < 80 && x2 >= 0 && x2 <80 && y1 >= 0 && y1 < 80 && y2 >= 0 && y2 < 80){
							Request req = new Request(new Point(x1, y1), new Point(x2, y2), time, p++);
							queue.add(req);
						}else{
							System.out.println(time + ": INVALID" + input);
						}
					}catch(Exception e){
						//this.fw.tofile("INVALID" + input.replace("(", "[").replace(")", "]"));
						System.out.println("INVALID: " + time + input);
					};
				}else if(m2.matches()){
					try{
						int x1 = Integer.parseInt(m2.group(1));
						int y1 = Integer.parseInt(m2.group(2));
						int x2 = Integer.parseInt(m2.group(3));
						int y2 = Integer.parseInt(m2.group(4));
						int n = Integer.parseInt(m2.group(5));
						int flag = 0;
						if(x1 == x2){
							if((y1 - y2) != 1 && (y2 - y1) != 1){
								flag = 1;
							}
						}else if(y1 == y2){
							if((x1 - x2) != 1 && (x2 - x1) != 1){
								flag = 1;
							}
						}else{
							flag = 1;
						}
						if(flag == 0){
							if(n != 0 && n != 1){
								System.out.println(time + ": INVALID" + input);
							}else{
								if(x1 >= 0 && x1 < 80 && x2 >= 0 && x2 <80 && y1 >= 0 && y1 < 80 && y2 >= 0 && y2 < 80){
									taxigui.SetRoadStatus(new Point(x1, y1), new Point(x2, y2), n);
									if(n == 0){
										if(x1 == x2){
											if(y1 > y2){
												if(cab[0].map[x2][y2] == 3){
													cab[0].map[x2][y2] = 2;
												}else{
													cab[0].map[x2][y2] = 0;
												}
											}else{
												if(cab[0].map[x1][y1] == 3){
													cab[0].map[x1][y1] = 2;
												}else{
													cab[0].map[x1][y1] = 0;
												}
											}
										}else{
											if(x1 > x2){
												if(cab[0].map[x2][y2] == 3){
													cab[0].map[x2][y2] = 1;
												}else{
													cab[0].map[x2][y2] = 0;
												}
											}else{
												if(cab[0].map[x1][y1] == 3){
													cab[0].map[x1][y1] = 1;
												}else{
													cab[0].map[x1][y1] = 0;
												}
											}
										}
										for(int j = 0; j < 100; j++){
											if(cab[j].status == 1){
												if(cab[j].req.spath.contains(cab[j].pmap[x1][y1]) && cab[j].req.spath.contains(cab[j].pmap[x2][y2])){
													synchronized(cab[j]){
														//@THREAD_EFFECTS: \locked(cab[j]);
														cab[j].signal = 1;
														cab[j].req.spath.clear();
														cab[j].shortPath(cab[j].point);
														cab[j].req.spath.add(cab[j].pmap[cab[j].req.dst.x][cab[j].req.dst.y]);
														Point pretp = cab[j].pmap[cab[j].req.dst.x][cab[j].req.dst.y].pre;
														while(pretp != null){
															cab[j].req.spath.add(pretp);
															pretp = pretp.pre;
														}
														f.toFile("修改后从该点开始送客路径： " + cab[j].pmap[cab[j].req.dst.x][cab[j].req.dst.y].dis + "\r\n", cab[j].req + ".txt", true);
														for(int m = cab[j].req.spath.size() - 1; m >= 0; m--){
															if(m != 0){
																//System.out.print(req.spath.get(n) + "-->");
																f.toFile(cab[j].req.spath.get(m) + "-->", cab[j].req + ".txt", true);
															}else{
																//System.out.print(req.spath.get(n));
																f.toFile(cab[j].req.spath.get(n) + "\r\n", cab[j].req + ".txt", true);
															}
														}
														f.toFile("\r\n" + "\r\n" + "*******************************************" + "\r\n", cab[j].req + ".txt", true);
														f.toFile("*******************************************" + "\r\n", cab[j].req + ".txt", true);
														cab[j].pointFree();
														cab[j].req.spath.remove(cab[j].req.spath.size()-1);
													}
												}
											}else if(cab[j].status == 3){
												if(cab[j].req.path.contains(cab[j].pmap[x1][y1]) && cab[j].req.path.contains(cab[j].pmap[x2][y2])){
													synchronized(cab[j]){
														//@THREAD_EFFECTS: \locked(cab[j]);
														cab[j].signal = 1;
														cab[j].req.path.clear();
														cab[j].shortPath(cab[j].pmap[cab[j].req.src.x][cab[j].req.src.y]);
														Point pretemp = cab[j].pmap[cab[j].point.x][cab[j].point.y].pre;
														while(pretemp != null){
															cab[j].req.path.add(pretemp);
															pretemp = pretemp.pre;
														}
														f.toFile("修改后从该点开始接客路径： " + cab[j].pmap[cab[j].point.x][cab[j].point.y].dis + "\r\n", cab[j].req + ".txt", true);
														for(int z = 0; z < cab[j].req.path.size(); z++){
															if(z != cab[j].req.path.size() - 1){
																//System.out.print(req.path.get(n) + "-->");
																f.toFile(cab[j].req.path.get(z) + "-->", cab[j].req + ".txt", true);
															}else{
																//System.out.print(req.path.get(n));
																f.toFile(cab[j].req.path.get(z) + "\r\n", cab[j].req + ".txt", true);
															}
														}
														cab[j].pointFree();
													}
												}
											}
										}
									}else{
										if(x1 == x2){
											if(y1 > y2){
												if(cab[0].map[x2][y2] == 2){
													cab[0].map[x2][y2] = 3;
													cab[0].map2[x2][y2] = 3;
												}else{
													cab[0].map[x2][y2] = 1;
													cab[0].map2[x2][y2] = 1;
												}
											}else{
												if(cab[0].map[x1][y1] == 2){
													cab[0].map[x1][y1] = 3;
													cab[0].map2[x1][y1] = 3;
												}else{
													cab[0].map[x1][y1] = 1;
													cab[0].map2[x1][y1] = 1;
												}
											}
										}else{
											if(x1 > x2){
												if(cab[0].map[x2][y2] == 1){
													cab[0].map[x2][y2] = 3;
													cab[0].map2[x2][y2] = 3;
												}else{
													cab[0].map[x2][y2] = 2;
													cab[0].map2[x2][y2] = 2;
												}
											}else{
												if(cab[0].map[x1][y1] == 1){
													cab[0].map[x1][y1] = 3;
													cab[0].map2[x1][y1] = 3;
												}else{
													cab[0].map[x1][y1] = 2;
													cab[0].map2[x1][y1] = 2;
												}
											}
										}
									}
								}else{
									System.out.println(time + ": INVALID" + input);
								}
							}
						}else{
							System.out.println(time + ": INVALID" + input);
						}
					}catch(Exception e){
						//this.fw.tofile("INVALID" + input.replace("(", "[").replace(")", "]"));
						System.out.println("INVALID: " + time + input);
					};
				}else if(m3.matches()){
					int id = Integer.parseInt(str[i].replaceAll("special", ""));
					if(0 <= id && id < 100){
						if(!cab[id].father){
							//SpecialCab scab = (SpecialCab)cab[id];
							//scab.getInformation();
							cab[id].getInformation();
						}else{
							System.out.println(time + ": INVALID" + input);
						}
					}else{
						System.out.println(time + ": INVALID" + input);
					}
				}else{
					if(isNumeric(str[i])){
						int id = Integer.parseInt(str[i]);
						if(id >= 0 && id <= 99){
							System.out.println(cab[id]);
						}
					}else if(str[i].equals("stop")){
						for(int j = 0; j < 100; j++){
							if(cab[j].status == 0){
								System.out.print(cab[j].id + " ");
							}
						}
						System.out.println();
					}else if(str[i].equals("serve")){
						for(int j = 0; j < 100; j++){
							if(cab[j].status == 1){
								System.out.print(cab[j].id + " ");
							}
						}
						System.out.println();
					}else if(str[i].equals("wait")){
						for(int j = 0; j < 100; j++){
							if(cab[j].status == 2){
								System.out.print(cab[j].id + " ");
							}
						}
						System.out.println();
					}else if(str[i].equals("grab")){
						for(int j = 0; j < 100; j++){
							if(cab[j].status == 3){
								System.out.print(cab[j].id + " ");
							}
						}
						System.out.println();
					}else{
						System.out.println(time + ": INVALID" + input);
					}
					//this.fw.tofile(System.currentTimeMillis() + ": INVALID" + "[" + input + "]");
				}
			}
			try{
				input = in.nextLine().replace(" ", "");
				ct = this.timer.getTime();
			}catch(Exception e){
				//System.exit(0);
			};
		}
	}
}
