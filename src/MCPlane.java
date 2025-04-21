public class MCPlane{

	//看不懂啊看不懂
	public double[] norm;
	public double x,y,z;
	
	public void setPlane(double x, double y, double z, double[] n){
		this.x = x;
		this.y = y;
		this.z = z;
		norm = n;
	}
	
	public boolean test(double xx, double yy, double zz){
		return  norm[0] * (xx-x) + 
				norm[1] * (yy-y) +
				norm[2] * (zz-z) >= 0;
	}
}
