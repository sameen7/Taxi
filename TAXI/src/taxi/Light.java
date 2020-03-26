package taxi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

public class Light extends Thread{
	/*@overview: Load the information of the traffic lights from file.And simulate the change of the traffic lights.
	 * @invariant: light!=null && (\all int i,j;0<=i<=79,0<=j<=79;0<=light[i][j]<=1)
	 */
	int[][] light = new int[80][80];
	int change;
	int ston;
	int etow;
	TaxiGUI taxigui;
	
	public boolean repOK(){
		/*@EFFECTS: \result == invariant(this)
		 */
		if(light == null){
			return false;
		}
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				if(light[i][j] != 0 && light[i][j] != 1){
					return false;
				}
			}
		}
		return true;
	}
	
	public Light(TaxiGUI taxigui) throws IOException{
		/*@MODIFIES: this.light; this.change; this.ston; this.etow;
		 * @EFFECTS: 任何情况 ==> change = (int)(50 * Math.random() + 50);
		 * 			 setLight函数正常执行 ==> light初始化;
		 * 			 setLight函数不正常执行 ==> exceptional_behavior (IOException);
		 * 			 任何情况 ==> ston = rand.nextInt(2) + 1;
		 * 			 ston == 1 ==> etow = 2;
		 * 			 ston == 2 ==> etow = 1;
		 */
		this.taxigui = taxigui;
		setLight();
		change = (int)(200 * Math.random() + 300);
		Random rand = new Random();
		ston = rand.nextInt(2) + 1;
		if(ston == 1){
			etow = 2;
		}else{
			etow = 1;
		}
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				if(light[i][j] == 1){
					taxigui.SetLightStatus(new Point(i, j), etow);
				}else{
					taxigui.SetLightStatus(new Point(i, j), 0);
				}
			}
		}
	}
	public void setLight()throws FileNotFoundException, IOException{
		/*@MODIFIES: this.light;
		 * @EFFECTS: 文件存在 && IO正常工作 ==> 文件内容加载进this.light;
		 * 			  文件不存在 || IO未正常工作 ==> exceptional_behavior (FileNotFoundException | IOException);
		 */
		FileReader fr = new FileReader("lights.txt");
		BufferedReader br = new BufferedReader(fr);
		int n = 0;
		String str = null;
		while((str = br.readLine()) != null){
			str = str.replace(" ", "").replace("\t", "");
			if(str.length() == 80){
				for(int i = 0; i < 80; i++){
					if(str.charAt(i) == '0' || str.charAt(i) == '1'){
						light[n][i] = str.charAt(i) - '0';
					}else{
						System.out.println("wrong input");
					}
				}
				if(n < 80){
					n++;
				}else{
					System.out.println("wrong input");
				}
			}else{
				System.out.println("wrong input");
			}
		}
		br.close();
	}
	public void run(){
		/*@MODIFIES: this.etow; this.ston;
		 * @EFFECTS: 每隔change的时间，ston == 1 ==> etow = 1, ston = 2;
		 * 			  每隔change的时间，ston == 2 ==> etow = 2, ston = 1;
		 */
		while(true){
			try {
				sleep(change);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(ston == 1){
				etow = 1;
				ston = 2;
				for(int i = 0; i < 80; i++){
					for(int j = 0; j < 80; j++){
						if(light[i][j] == 1){
							taxigui.SetLightStatus(new Point(i, j), etow);
						}
					}
				}
			}else{
				etow = 2;
				ston = 1;
				for(int i = 0; i < 80; i++){
					for(int j = 0; j < 80; j++){
						if(light[i][j] == 1){
							taxigui.SetLightStatus(new Point(i, j), etow);
						}
					}
				}
			}
		}
	}
}
