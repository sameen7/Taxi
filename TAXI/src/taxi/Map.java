package taxi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Map {
	/*@overview: Load the information of the map from file.And initialize the point map(pmap).
	 * @invariant: map!=null && (\all int i,j;0<=i<=79,0<=j<=79;0<=map[i][j]<=3) && map2!=null && (\all int i,j;0<=i<=79,0<=j<=79;0<=map2[i][j]<=3) && pmap!=null && (\all int i,j;0<=i<=79,0<=j<=79;pmap[i][j].repOK==true && pmap2!=null && (\all int i,j;0<=i<=79,0<=j<=79;pmap2[i][j].repOK==true)
	 */
	int map[][] = new int[80][80];
	Point pmap[][] = new Point[80][80];
	int map2[][] = new int[80][80];
	Point pmap2[][] = new Point[80][80];
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
		return true;
	}
	public Map() throws IOException{
		/*@MODIFIES: this.map; this.pmap, this.map2;
		 * @EFFECTS: 任何情况 ==> pmap初始化;
		 * 			 setMap函数正常执行 ==> map初始化;
		 * 			 setMap函数不正常执行 ==> exceptional_behavior (IOException);
		 */
		setMap();
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				map2[i][j] = map[i][j];
			}
		}
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				pmap[i][j] = new Point(i, j);
			}
		}
		for(int i = 0; i < 80; i++){
			for(int j = 0; j < 80; j++){
				pmap2[i][j] = new Point(i, j);
			}
		}
	}
	public void setMap() throws IOException, FileNotFoundException{
		/*@MODIFIES: this.map;
		 * @EFFECTS: 文件存在 && IO正常工作 ==> 文件内容加载进this.map;
		 * 			  文件不存在 || IO未正常工作 ==> exceptional_behavior (FileNotFoundException | IOException);
		 */
		FileReader fr = new FileReader("map.txt");
		BufferedReader br = new BufferedReader(fr);
		int n = 0;
		String str = null;
		while((str = br.readLine()) != null){
			str = str.replace(" ", "").replace("\t", "");
			if(str.length() == 80){
				for(int i = 0; i < 80; i++){
					if(str.charAt(i) >= '0' && str.charAt(i) <= '3'){
						map[n][i] = str.charAt(i) - '0';
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
}
