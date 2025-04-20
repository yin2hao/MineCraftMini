import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class MCCamera{
	
	public static void setCamera(  int width, int height, 
							double degx, double degz, 
							double x, double y, double z){
		glMatrixMode(GL_PROJECTION);//切换到投影矩阵模式
		glLoadIdentity();//重置为单位矩阵

		//计算宽高比
		float widthHeightRatio = (float) width / height;

		//使用GLU设置透视投影：
		//45度垂直视野
		//根据窗口尺寸保持正确的宽高比
		//近裁剪面0.1单位
		//远裁剪面1e3 * MCBlock.SIDE(1000倍方块尺寸)
		//到底咋用的不知道，以后学
		gluPerspective(45, widthHeightRatio, 0.1f, (float)(1e3 * MCBlock.SIDE));

		glRotated(-degz ,1,0,0);// 绕X轴旋转(俯仰角)
		glRotated(-degx +90 ,0,0,1);// 绕Z轴旋转(偏航角)，+90度调整
		glTranslated(-x,-y,-z);// 移动到摄像机位置

		glMatrixMode(GL_MODELVIEW);//切换回模型视图矩阵模式
		glLoadIdentity();//重置为单位矩阵(为后续物体渲染做准备)
	}
}