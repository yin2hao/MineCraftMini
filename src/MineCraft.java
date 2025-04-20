import org.lwjgl.*;
import java.nio.*;
import java.util.*;
import org.lwjgl.input.*;
import static org.lwjgl.input.Keyboard.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class MineCraft extends MCWindow {

	static MineCraft m;

	MCLight light;
	public MCGrid g;
	public MCWaterGrid trans;
	private MCPerson p;
	MCCursor cursor;
    MCMapGen gen;

	public MineCraft(){
		super(800, 500);//因为MineCraft extend MCWindows，所以super跳转到MCWindows方法
		m = this;
	}

	protected static final int LX = 120, LY = LX, LZ = 100;

	protected void init() {

		MCBlock.init();//方块破坏动画初始化

		glEnable(GL_TEXTURE_2D);//启用2D纹理
		glEnable(GL_DEPTH_TEST);//启用深度测试
        glDepthFunc(GL_LEQUAL); //设置深度测试函数
        float clear = 0f;
		glClearColor(clear, clear, clear, 1);// 设置清屏颜色为黑色
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST );// 设置透视修正质量
		//glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST );
		//glEnable( GL_POLYGON_SMOOTH );
		glEnable(GL_CULL_FACE);// 启用面剔除
		glFrontFace(GL_CW);// 设置顺时针为正面

		glEnable(GL_BLEND);// 启用混合
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);// 设置混合函数

		/*glFogi (GL_FOG_MODE, GL_LINEAR );
		FloatBuffer fb = BufferUtils.createFloatBuffer(4);
		fb.put(0).put(0).put(0).put(1).flip();
		glFog(GL_FOG_COLOR, fb);
		glFogf(GL_FOG_DENSITY, .1f);
		glHint(GL_FOG_HINT, GL_DONT_CARE);
		glFogf(GL_FOG_START, (float)MCBlock.SIDE * ((LZ-G)*2/3+G) );
		glFogf(GL_FOG_END, (float)MCBlock.SIDE * (LZ*1.33f));
		glEnable (GL_FOG);*/

		//MCMaterial.setMaterial( MCMaterial.NORM );
        
        gen = new MCMapGen(LX, LY, LZ);//初始化地图数据
        genMap();//生成地图
        
		light = new MCLight( LX/2 * MCBlock.SIDE, LY/2 * MCBlock.SIDE , LZ* MCBlock.SIDE , GL_LIGHT1);
		light.enable();

		cursor = new MCCursor(this);

		//p.hideMouse();
        
		update(1);
		display();
		//System.gc();
		//setFullscreen(true);

	}
    
    public void genMap(){
        
        /*double GL = 0.1 + 0.4*Math.random();
        double PE = 0.25 + 0.5*Math.random();
        int OCT = (int)(2+20*Math.random());
        
        System.out.println(GL + " " + PE + " " + OCT);*/
        
        //, (int)(LZ*GL), 7, 0.4);
		gen.genMap();
        g = gen.getGrid();
        trans = gen.getWater();
		trans.g2 = g;
        
        p = new MCPerson(LX/2.*MCBlock.SIDE,LY/2.*MCBlock.SIDE, (LZ)*MCBlock.SIDE - MCPerson.HEIGHT , 0,90, this);

    }

	//放置方块
	public static MCBlock loadBlock(char c, int x, int y, int z){
		switch(c){
			case 'g' : return new MCGrassBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
			case 'd' : return new MCDirtBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
			case 'w' : return new MCWaterBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
			case 'o' : return new MCWoodBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
			case 'l' : return new MCLeavesBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
			case 's' : return new MCSandBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
			case 't' : return new MCStoneBlock(x *MCBlock.SIDE, y*MCBlock.SIDE, z*MCBlock.SIDE);
		}
		return null;
	}

	long[] keyTime = new long[256];
	boolean mouse;

	protected void update(int delta){
		long time = MCTimer.getTime();//获取当前MCTime存储的相对时间

		if( isKeyDown( KEY_ESCAPE ) ){
			if(mouse)
				p.showMouse();
			mouse = false;
		}

		double moveConst = 5e-3 * MCBlock.SIDE;//移动速度基准值
		//移动的代码
		if( isKeyDown( KEY_W ) )
			p.move(delta * moveConst);
		if( isKeyDown( KEY_A ) )
			p.moveLeft(delta * moveConst);
		if( isKeyDown( KEY_S ) )
			p.moveBack(delta * moveConst);
		if( isKeyDown( KEY_D ) )
			p.moveRight(delta * moveConst);
		if( isKeyDown( KEY_SPACE )){
			if( p.inWater() ){
				p.jump( delta* moveConst);
			}else if (  time - keyTime[KEY_SPACE] >  100 ){
				p.jump(0 );
				keyTime[KEY_SPACE] = time;
			}
		}
        if( isKeyDown( KEY_M ) ){
            genMap();
        }
		if( isKeyDown( KEY_V ) )
			p.shadowJump();

		while( Mouse.next() ){
			int btn = Mouse.getEventButton();
			if(btn == 0){//鼠标左键
				if(!mouse){
					mouse = true;
					p.hideMouse();
				}
			}
			//鼠标右键按下
			if(btn == 1 && Mouse.getEventButtonState()){
				//放置方块
				p.place('d');
			}
		}

		while( Keyboard.next() ){
			if( Keyboard.getEventKey() == KEY_F && !Keyboard.getEventKeyState()){
				setFullscreen(!isFullscreen());
			}
		}

		if( Mouse.isButtonDown(0))
			p.work(delta * moveConst);
		else
			p.resetSelected();

		p.captureMovement();
		p.applyForces(delta);
		p.updateSelected();
	}

	public void display() {
		//清除颜色缓冲区和深度缓冲区，为新的帧做准备
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		//设置当前绘制颜色为绿色
		glColor4d(0,1,0,1);

		p.setCamera();//设置摄像头位置
		light.setPos(p.x,p.y, 2 * LZ * MCBlock.SIDE);//设置光源位置
		//light.update();
 		g.render(p);

		 //水体相关
 		trans.render(p);
		trans.floodWater();

		//光标相关
		cursor.render();
	}

	public static void main(String[] argv) {
		final MineCraft m = new MineCraft();
		m.start();

		/*new Thread(new Runnable(){public void run(){m.start();}}).start();
		System.out.println(m.getHeight() + " " + m.getWidth());
		m.setFullscreen(true);
		System.out.println(m.getHeight() + " " + m.getWidth());*/
	}
}