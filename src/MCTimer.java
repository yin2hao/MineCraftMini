import org.lwjgl.*;

public class MCTimer{

	protected long time;

	public MCTimer(){
		time = getTime();
	}

	//获得当前相对时间与上一次存储的相对时间的差值
	public int getDelta(){
		return (int)(getTime() - time);
	}

	//更新存储的时间
	public void update(){
		time = getTime();
	}

	//返回现在time存储的时间
	public long curTime(){
		return time;
	}

	//返回程序相对时间
	public static long getTime() {
	    return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}

}