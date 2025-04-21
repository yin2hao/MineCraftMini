import org.newdawn.slick.opengl.*;
import org.newdawn.slick.util.*;
import java.util.*;

//纹理加载工具类
//使用Slick2D库的OpenGL纹理功能
public class MCTextureLoader{
	private static HashMap<String, Texture> map;
	
	static {
		map = new HashMap<String, Texture>();
	}
	
	public static Texture getTexture(String s){
        s = "res/"+s;//自动在传入的纹理名称前加上 "res/" 路径
		//首先检查纹理是否已加载过（在 map 中）
		if(map.containsKey(s)) return map.get(s);
		try{
			//如果已加载，直接返回缓存中的纹理
			map.put(s, TextureLoader.getTexture(s.split("\\.")[s.split("\\.").length-1], ResourceLoader.getResourceAsStream(s)));
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		return map.get(s);
	}
}