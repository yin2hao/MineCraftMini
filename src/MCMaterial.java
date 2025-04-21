import org.lwjgl.*;
import static org.lwjgl.opengl.GL11.*;
import java.nio.*;
import java.util.*;

public class MCMaterial{

	protected static int PRECISION = 20;//材质强度的精度级别（预设为20）
	public static FloatBuffer[] cache = new FloatBuffer[PRECISION];//用于普通材质
	public static FloatBuffer[] wcache = new FloatBuffer[PRECISION];// 用于水材质
	protected static double[] vals = new double[PRECISION];//存储强度值的数组
	protected static double NORM = 1;//归一化常量
	
	public static FloatBuffer cursorBuf = create(1,1,1);

	static{
		double d = NORM/ PRECISION;
		double x = 0;
		for(int i = 0; i<PRECISION; i++){
			float s = (float)x;
			
			vals[i] = x;
			cache[i] = create(s,s,s);
			wcache[i] = create(s/2,s/2,s);
			x+=d;
		}
	}

	//用于创建材质颜色缓冲区
	public static FloatBuffer create(float f1, float f2, float f3){
		FloatBuffer buf = BufferUtils.createFloatBuffer(4);
		buf.put(f1).put(f2).put(f3).put(1).flip();
		return buf;
	}

	//根据给定的强度(str)和是否为水材质(water)，查找合适的颜色缓冲区
	public static FloatBuffer findBuf( double str, boolean water ){
		int x = Arrays.binarySearch( vals , str );
		if( x < 0)
			x = -x-1;
		if(x >= cache.length)
			x = cache.length-1;
		if( water )
			return wcache[x];
		else
			return cache[x];

	}

	//设置普通材质的属性
	public static void setMaterial( double str ){
		setBuf(findBuf(str, false));
	}

	//实际设置OpenGL材质属性的方法
	public static void setBuf( FloatBuffer buf ){
		glMaterial(GL_FRONT, GL_SPECULAR, buf);
		glMaterial(GL_FRONT, GL_AMBIENT, buf);
		glMaterialf(GL_FRONT, GL_SHININESS, 0.1f);
	}

}