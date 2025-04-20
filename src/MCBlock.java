import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.newdawn.slick.opengl.*;
import static org.lwjgl.opengl.GL11.*;

public class MCBlock{

	protected MCTexturedQuad [] q = new MCTexturedQuad[6];//方块的六个面
	public  double x, y,z;

	//Texture数组为org.newdawn.slick.opengl.*包中的特殊数组，但该项目疑似2015年就死了
	//可以使用lwjgl的STBImage作为代替
	public static Texture[] breaks = new Texture[10];//方块破坏的10个纹理

	//方块破坏动画
	public static void init(){
		for(int i = 1; i<=10; i++)
			breaks[i-1] = MCTextureLoader.getTexture("break" + i +".png");
	}
	
	protected double work;//方块被挖掘的进度
	protected Texture curBreak;//当前挖掘阶段（方便播动画）

	public static final double SIDE = 16;//方块边长(但似乎并没有用)

	//分别代表方块的六个面
	public static final int BOT = 0;
	public static final int LEFT = 1;
	public static final int RIGHT = 2;
	public static final int FRONT = 3;
	public static final int BACK = 4;
	public static final int TOP = 5;

	public MCBlock(double x, double y, double z, String texture){
		this.x = x; this.y = y; this.z = z;
		//使用单一纹理创建方块，6个面都使用相同的纹理
		q[BOT] = 	new MCTexturedQuad(this, BOT, texture);
		q[LEFT] = 	new MCTexturedQuad(this, LEFT, texture);
		q[RIGHT] = 	new MCTexturedQuad(this, RIGHT, texture);
		q[FRONT] = 	new MCTexturedQuad(this, FRONT, texture);
		q[BACK] = 	new MCTexturedQuad(this, BACK, texture);
		q[TOP] = 	new MCTexturedQuad(this, TOP, texture);
	}

	public MCBlock(double x, double y, double z, String[] t){
		this.x = x; this.y = y; this.z = z;
		int i = 0;
		//使用纹理数组创建方块，可以为每个面指定不同的纹理
		q[BOT] = 	new MCTexturedQuad(this, BOT, t[i++]);
		q[LEFT] = 	new MCTexturedQuad(this, LEFT, t[i++]);
		q[RIGHT] = 	new MCTexturedQuad(this, RIGHT, t[i++]);
		q[FRONT] = 	new MCTexturedQuad(this, FRONT, t[i++]);
		q[BACK] = 	new MCTexturedQuad(this, BACK, t[i++]);
		q[TOP] = 	new MCTexturedQuad(this, TOP, t[i]);
	}

	//设置方块是否发光(高亮显示)，如果取消发光，同时重置工作进度
	public void glow(boolean glow){
		for(MCTexturedQuad qq : q)
			qq.glow = glow;
		if( !glow )
			setWork(0);
	}

	/*public void render(){
		for( MCTexturedQuad qq : q )
			qq.render();
	}*/

	//一个很小的偏移量(0.01)，用于渲染破坏纹理时避免z-fighting
	//暂时不懂
	public static final double E = 1e-2f;

	//核心渲染方法，根据指定的面(side)渲染方块
	//使用GL_QUADS绘制四边形
	//如果当前有破坏动画(curBreak不为null)，会在主纹理上叠加破坏纹理
	//每个面的顶点顺序和纹理坐标都经过精心排列
	//对于水面(MCWaterBlock)，顶部面会稍微下沉以产生视觉效果
	public void render( int side ){
		switch(side){
			case BOT:
				glNormal3d(0,0,-1);
				glTexCoord2f(0,0);
				glVertex3d(x+SIDE,y+SIDE,z);
				glTexCoord2f(1,0);
				glVertex3d(x,y+SIDE,z);
				glTexCoord2f(1,1);
				glVertex3d(x,y,z);
				glTexCoord2f(0,1);
				glVertex3d(x+SIDE,y,z);
				if( curBreak != null ){
					glEnd();
					curBreak.bind();
					glBegin(GL_QUADS);
					
					glNormal3d(0,0,-1);
					glTexCoord2f(0,0);
					glVertex3d(x+SIDE,y+SIDE,z-E);
					glTexCoord2f(1,0);
					glVertex3d(x,y+SIDE,z-E);
					glTexCoord2f(1,1);
					glVertex3d(x,y,z-E);
					glTexCoord2f(0,1);
					glVertex3d(x+SIDE,y,z-E);
					
					glEnd();
					q[side].bind();
					glBegin(GL_QUADS);
					
				}
				break;
			case LEFT:
				glNormal3d(-1,0,0);
				glTexCoord2f(0,0);
				glVertex3d(x,y+SIDE,z+SIDE);
				glTexCoord2f(1,0);
				glVertex3d(x,y,z+SIDE);
				glTexCoord2f(1,1);
				glVertex3d(x,y,z);
				glTexCoord2f(0,1);
				glVertex3d(x,y+SIDE,z);
				if( curBreak != null ){
					glEnd();
					curBreak.bind();
					glBegin(GL_QUADS);
					
					glNormal3d(-1,0,0);
					glTexCoord2f(0,0);
					glVertex3d(x-E,y+SIDE,z+SIDE);
					glTexCoord2f(1,0);
					glVertex3d(x-E,y,z+SIDE);
					glTexCoord2f(1,1);
					glVertex3d(x-E,y,z);
					glTexCoord2f(0,1);
					glVertex3d(x-E,y+SIDE,z);
					
					glEnd();
					q[side].bind();
					glBegin(GL_QUADS);
					
				}
				break;
			case RIGHT:
				glNormal3d(1,0,0);
				glTexCoord2f(0,0);
				glVertex3d(x+SIDE,y,z+SIDE);
				glTexCoord2f(1,0);
				glVertex3d(x+SIDE,y+SIDE,z+SIDE);
				glTexCoord2f(1,1);
				glVertex3d(x+SIDE,y+SIDE,z);
				glTexCoord2f(0,1);
				glVertex3d(x+SIDE,y,z);
				if( curBreak != null ){
					glEnd();
					curBreak.bind();
					glBegin(GL_QUADS);
					
					glNormal3d(1,0,0);
					glTexCoord2f(0,0);
					glVertex3d(x+SIDE+E,y,z+SIDE);
					glTexCoord2f(1,0);
					glVertex3d(x+SIDE+E,y+SIDE,z+SIDE);
					glTexCoord2f(1,1);
					glVertex3d(x+SIDE+E,y+SIDE,z);
					glTexCoord2f(0,1);
					glVertex3d(x+SIDE+E,y,z);
					
					glEnd();
					q[side].bind();
					glBegin(GL_QUADS);
					
				}
				break;
			case FRONT:
				glNormal3d(0,-1,0);
				glTexCoord2f(0,0);
				glVertex3d(x,y,z+SIDE);
				glTexCoord2f(1,0);
				glVertex3d(x+SIDE,y,z+SIDE);
				glTexCoord2f(1,1);
				glVertex3d(x+SIDE,y,z);
				glTexCoord2f(0,1);
				glVertex3d(x,y,z);
				if( curBreak != null ){
					glEnd();
					curBreak.bind();
					glBegin(GL_QUADS);
					
					glNormal3d(0,-1,0);
					glTexCoord2f(0,0);
					glVertex3d(x,y-E,z+SIDE);
					glTexCoord2f(1,0);
					glVertex3d(x+SIDE,y-E,z+SIDE);
					glTexCoord2f(1,1);
					glVertex3d(x+SIDE,y-E,z);
					glTexCoord2f(0,1);
					glVertex3d(x,y-E,z);
					
					glEnd();
					q[side].bind();
					glBegin(GL_QUADS);
					
				}
				break;
			case BACK:
				glNormal3d(0,1,0);
				glTexCoord2f(0,0);
				glVertex3d(x+SIDE,y+SIDE,z+SIDE);
				glTexCoord2f(1,0);
				glVertex3d(x,y+SIDE,z+SIDE);
				glTexCoord2f(1,1);
				glVertex3d(x,y+SIDE,z);
				glTexCoord2f(0,1);
				glVertex3d(x+SIDE,y+SIDE,z);
				if( curBreak != null ){
					glEnd();
					curBreak.bind();
					glBegin(GL_QUADS);
					
					glNormal3d(0,1,0);
					glTexCoord2f(0,0);
					glVertex3d(x+SIDE,y+SIDE+E,z+SIDE);
					glTexCoord2f(1,0);
					glVertex3d(x,y+SIDE+E,z+SIDE);
					glTexCoord2f(1,1);
					glVertex3d(x,y+SIDE+E,z);
					glTexCoord2f(0,1);
					glVertex3d(x+SIDE,y+SIDE+E,z);
					
					glEnd();
					q[side].bind();
					glBegin(GL_QUADS);
					
				}
				break;
			case TOP:
                double zz = (this instanceof MCWaterBlock) ? -SIDE * 0.1:0;
				glNormal3d(0,0,1);
				glTexCoord2f(0,0);
				glVertex3d(x,y+SIDE,z+SIDE+zz);
				glTexCoord2f(1,0);
				glVertex3d(x+SIDE,y+SIDE,z+SIDE+zz);
				glTexCoord2f(1,1);
				glVertex3d(x+SIDE,y,z+SIDE+zz);
				glTexCoord2f(0,1);
				glVertex3d(x,y,z+SIDE+zz);
				if( curBreak != null ){
					glEnd();
					curBreak.bind();
					glBegin(GL_QUADS);
					
					glNormal3d(0,0,1);
					glTexCoord2f(0,0);
					glVertex3d(x,y+SIDE,z+SIDE+E);
					glTexCoord2f(1,0);
					glVertex3d(x+SIDE,y+SIDE,z+SIDE+E);
					glTexCoord2f(1,1);
					glVertex3d(x+SIDE,y,z+SIDE+E);
					glTexCoord2f(0,1);
					glVertex3d(x,y,z+SIDE+E);
					
					glEnd();
					q[side].bind();
					glBegin(GL_QUADS);
					
				}
				break;
		}
	}


	boolean[] water = new boolean[6];
	boolean[] back = new boolean[6];

	public void setWater( int side, boolean water ){
		this.water[side] = water;
	}
	public void renderBack( int side, boolean back ){
		this.back[side] = back;
	}

	public boolean getWater( int side ){
		return water[side];
	}
	public boolean getBack( int side ){
		return back[side];
	}

	public MCTexturedQuad[] getQuads(){
		return q;
	}
	
	public static final double REQ_WORK = 40;
	
	public double reqWork(){
		return REQ_WORK;
	}
	
	public double getWork(){
		return work;
	}

	//设置当前挖掘进度
	//根据进度计算应该显示哪个破坏阶段纹理
	//当进度为0时清除破坏纹理
	public void setWork( double work ){
		this.work = work;
		
		int per = ( (int)Math.ceil(work/ reqWork() * breaks.length) );
		if( per == 0 ){
			curBreak = null;
			return;
		}
		if( per > breaks.length) per = breaks.length;
		
		curBreak = breaks[per-1];
	}

	//判断方块是否已经被完全破坏(工作进度 >= 所需工作量)
	public boolean dead(){
		return work >= reqWork();
	}
}