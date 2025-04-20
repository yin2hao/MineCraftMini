import org.lwjgl.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.*;

//使用LWJGL库实现的OpenGL光照
public class MCLight{
	
	private float x,y,z;//光源的3D坐标位置
	//光源强度,为了便于理解将变量名修改（str-->lightSourceIntensity）
	public float lightSourceIntensity;
	
	private FloatBuffer pos;//存储光源位置
	private FloatBuffer lit;//存储光源颜色/强度

	//GLOW发光状态光强，NORM普通状态光强
	public static final float GLOW = 1f, NORM = .45f;
	public static MCLight light;

	//静态环境光强度缓冲区，初始化为0.1的灰度值
	//又是一个看不懂的东西
	private static FloatBuffer amb;
	
	public int lightNum;//表示此光源对应的OpenGL光源编号

	//初始化环境光参数，设置为较暗的灰色(RGB均为0.1)
	static {
		amb = BufferUtils.createFloatBuffer(4);
		float ambStr = .1f;
		amb.put(ambStr).put(ambStr).put(ambStr).put(0).flip();
	}


	public MCLight(double x, double y, double z, int num){
		lightNum = num;
		light = this;
		setPos(x,y,z);
		setLightSourceIntensity(NORM);
	}

	public void setLightSourceIntensity(double s){
		lightSourceIntensity = (float)s;//设置光源强度

		//创建包含新强度的FloatBuffer
		lit = BufferUtils.createFloatBuffer(4);
		lit.put(lightSourceIntensity).put(lightSourceIntensity).put(lightSourceIntensity).put(1).flip();

		//设置光源的镜面反射光和环境光参数
		glLight(lightNum, GL_SPECULAR, lit);
		glLight(lightNum, GL_AMBIENT, amb);
	}

	//好像没用
	public void update(){
		glLight(lightNum, GL_POSITION, pos);
	}

	public void setPos(double x, double y, double z){
		//设置新位置坐标
		this.x = (float)x;
		this.y = (float)y;
		this.z = (float)z;

		//创建包含新位置的FloatBuffer
		pos = BufferUtils.createFloatBuffer(4);
		pos.put(this.x).put(this.y).put(this.z).put(1).flip();

		//更新OpenGL中的光源位置
		glLight(lightNum, GL_POSITION, pos);
	}

	public void enable(){
		glEnable(GL_LIGHTING);//启用光照系统
		glEnable(lightNum);//启用特定编号的光源
		glLightf(lightNum, GL_CONSTANT_ATTENUATION, 1f);//设置光源的衰减参数(目前只设置了常数衰减)
		//glLightf(lightNum, GL_LINEAR_ATTENUATION, 1e-4f);
		//glLightf(lightNum, GL_QUADRATIC_ATTENUATION, 1e-6f);
	}
}