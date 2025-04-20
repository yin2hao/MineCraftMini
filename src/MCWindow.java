import org.lwjgl.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;


public class MCWindow{

	private int height;
	private int width;
	//fheight和fwidth是全屏窗口大小
	private int fheight;
	private int fwidth;

	private static final int MAXFPS = 60;//定义最大帧率

	//定义了俩，后面要new对象
	private MCTimer timer;
	private MCFPSTimer fpsTimer;

	private boolean fullscreen;//疑似检查是否全屏

	private int fps;//一个似乎是用不到的帧率

	public MCWindow (int w, int h){
		//创建新窗口前的参数赋值
		width = w;
		height = h;

		//new对象
		timer = new MCTimer();
		fpsTimer = new MCFPSTimer();//计算实时帧率
	}

	public int getWidth(){return fullscreen ? fwidth : width;}

	public int getHeight(){
		return fullscreen ? fheight : height;
	}

	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
			Display.setVSyncEnabled(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		init();
		timer.update();
		fpsTimer.update();

		//setFullscreen(true);

		while (!Display.isCloseRequested()) {
            
			int delta = timer.getDelta();
			timer.update();

			update(delta);
			display();
			updateFPS();
            
			Display.update(true);
			Display.sync(MAXFPS);
		}

		Display.destroy();
	}

	protected void init(){
	}

	protected void display(){
	}

	protected void update(int delta) {
	}

	public void setFullscreen(boolean f) {
		if(f == fullscreen) return;

		fullscreen = f;
		try {
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				int freq = 0;
				long area = 0;

				for (DisplayMode current : Display.getAvailableDisplayModes()){
					if ( current.getWidth() * current.getHeight()  > area &&
						(targetDisplayMode == null || current.getFrequency() >= freq &&
								current.getBitsPerPixel() >= targetDisplayMode.getBitsPerPixel()))
						{
							targetDisplayMode = current;
							freq = current.getFrequency();
							area = current.getWidth() * current.getHeight();
						}
					//System.out.println(current.getWidth()  + " " +  current.getHeight());
				}

				if(targetDisplayMode != null){
					fwidth = targetDisplayMode.getWidth();
					fheight = targetDisplayMode.getHeight();
				}

			} else
				targetDisplayMode = new DisplayMode(width, height);
			//System.out.println(targetDisplayMode.getWidth()  + " " +  targetDisplayMode.getHeight());

			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
				System.exit(0);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);
			
			glViewport(0,0, getWidth(), getHeight());

		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
			System.exit(0);
		}
	}

	public boolean isFullscreen(){
		return fullscreen;
	}


	public void updateFPS() {
		fpsTimer.frame();
		Display.setTitle("FPS: " + fpsTimer.getFPS());
	}

	public static void main(String[] args){
		new MCWindow(800,500).start();
	}

}
