package taxi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class toFile {
	/*@overview: Get the information written into the file as the format of UTF-8. 
	 */
	public boolean repOK(){
		/*@EFFECTS: \result == true
		 */
		return true;
	}
	public static void toFile(String str, String path, boolean flag){
		/*@REQUIRES: path为合法路径
		 *@MODIFIES: 以path为路径的文件
		 * @EFFECTS: (以path为路径的文件存在 && flag == true) ==> 将str追加进文本;
		 * 			 (以path为路径的文件不存在 && (flag == true || flag == false) ==> 创建以path为路径的文件并将str加进文本;
		 * 			 (以path为路径的文件存在 && flag == false) ==> 丢弃文本中原有信息， 将str加进文本;
		 */
		Charset charset = Charset.forName("UTF-8");
		try{
			FileOutputStream out = new FileOutputStream(path, flag); 
			out.write(str.getBytes(charset)); 
			out.close();    
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
}
