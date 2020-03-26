package taxi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	/*@overview: start the program.And initialize the cabs.
	 */
	public boolean repOK(){
		/*@EFFECTS: \result == true
		 */
		return true;
	}
	public static void  main(String[] args) throws FileNotFoundException, IOException, InterruptedException{
		/*@EFFECTS: map对象创建成功 && Thread.sleep(200)正常执行 ==> 启动程序;
		 * 			map对象创建不成功 ==> exceptional_behavior (IOException, FileNotFoundException);
		 * 			Thread.sleep(200)未正常执行 ==> exceptional_behavior (InterruptedException);
		 */
		TaxiGUI taxigui = new TaxiGUI();
		mapInfo mi=new mapInfo();
		mi.readmap("map.txt");//在这里设置地图文件路径
		taxigui.LoadMap(mi.map, 80);
		Light light = new Light(taxigui);
		Map map = new Map();
		Queue queue = new Queue(taxigui);
		Cab[] cab = new Cab[100];
		/*for(int i = 0; i < 100; i++){
			cab[i] = new Cab(i, map.map, map.pmap, taxigui, light, map.map2, map.pmap2);
		}*/
		
		
		//调用init_taxi方法
		cab = init_taxi(map, taxigui, light);
		
		
		Input input = new Input(queue, cab, taxigui);
		Scheduler sche = new Scheduler(queue, cab, map.pmap, map.pmap2);
		light.start();
		input.start();
		sche.start();
		for(int i = 0; i < 100; i++){
			cab[i].start();
		}
		while(true){
			Thread.sleep(200);
			cab[0].flowFree();
		}
	}
	public static Cab[] init_taxi(Map map, TaxiGUI taxigui, Light light){
		/*@MODIFIES: cab;
		 * @EFFECTS: 初始化100辆出租车， 普通出租车:cab[i] = new Cab(i, map.map, map.pmap, taxigui, light, map.map2, map.pmap2);特殊出租车:cab[i] = new SpecialCab(i, map.map, map.pmap, taxigui, light, map.map2, map.pmap2);
		 * 			 \result == cab;
		 */
		Cab[] cab = new Cab[100];
		int i = 0;
		for(i = 0; i < 70; i++){
			cab[i] = new Cab(i, map.map, map.pmap, taxigui, light, map.map2, map.pmap2);
			taxigui.SetTaxiType(i, 0);
		}
		for(i = 70; i < 100; i++){
			cab[i] = new SpecialCab(i, map.map, map.pmap, taxigui, light, map.map2, map.pmap2);
			taxigui.SetTaxiType(i, 1);
		}
		return cab;
	}
}
