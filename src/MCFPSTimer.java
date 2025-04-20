public class MCFPSTimer extends MCTimer{

	private int fps;
	private int lastFps;
	
	public int getFPS(){
		return lastFps;
	}

	//这个方法用来计算每秒的帧数，只包含计数逻辑，不包含触发逻辑
	public void frame(){
		//getTime获得上次记录相对时间，curTime获得当前相对时间
		if (getTime() - curTime() > 1000) {//当时间间隔大于一秒时
			lastFps = fps;
			fps = 0;//重置帧计数器
			time += 1000;
		}
		fps++;
	}
}