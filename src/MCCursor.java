import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;

public class MCCursor{
	MCWindow w;
	public MCCursor( MCWindow w ){
		this.w = w;
	}
	public void render(){
		
		int width = w.getWidth();
		int height = w.getHeight();
		
		glMatrixMode(GL11.GL_PROJECTION);//切换到投影矩阵模式
		glLoadIdentity();//重置为单位矩阵
		glOrtho(0, width, 0, height, 1, -1);//设置正交投影(2D渲染)，覆盖整个窗口范围
		glMatrixMode(GL11.GL_MODELVIEW);//切换回模型视图矩阵模式
		
		glColor4d(1,1,1,.8f);
		
		int mx = width/2;
		int my = height/2;
		int lenShort = 1;
		int lenLong = 15;

		//禁用纹理和光照
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_LIGHTING);
		
		glBegin(GL_QUADS);
			//垂直部分(上下延伸)
			glVertex2f(mx-lenShort,my-lenLong);
			glVertex2f(mx-lenShort,my+lenLong);
			glVertex2f(mx+lenShort,my+lenLong);
			glVertex2f(mx+lenShort,my-lenLong);

			//水平部分(左右延伸)
			glVertex2f(mx-lenLong,my-lenShort);
			glVertex2f(mx-lenLong,my+lenShort);
			glVertex2f(mx+lenLong,my+lenShort);
			glVertex2f(mx+lenLong,my-lenShort);
		glEnd();

		//恢复OpenGL状态，以防影响其他3D物体的渲染
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_LIGHTING);
	}
}