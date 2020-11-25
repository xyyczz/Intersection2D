package org.intersection.support;

/**
 * @author zhaijz
 * 2维向量
 */
public class Vector2D {
	private static double EPSILON = 1.0e-6;
	
	/**  横坐标 */
	public final double x;
	/**  纵坐标 */
	public final double y;
	
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * 向量减法
	 * @param other
	 * @return
	 */
	public Vector2D sub(Vector2D other) {
		return new Vector2D(x - other.x, y - other.y);
	}
	
	/**
	 * 向量加法
	 * @param other
	 * @return
	 */
	public Vector2D sum(Vector2D other) {
		return new Vector2D(x + other.x, y + other.y);
	}
	
	/**
	 * 向量乘以一个数
	 * @param multi
	 * @return
	 */
	public Vector2D mul(double multi) {
		return new Vector2D(x * multi, y * multi);
	}
	
	/**
	 * 向量除以一个数
	 * @param div
	 * @return
	 */
	public Vector2D div(double div) {
		return new Vector2D(x / div, y / div);
	}
	
	/**
	 * 向量归一化
	 * @return
	 */
	public Vector2D normalize() {
		double dis = Math.sqrt(this.x * this.x + this.y* this.y);
		return new Vector2D(x / dis, y / dis);
	}
	
	/**
	 * 向量点乘
	 * @param other
	 * @return
	 */
	public double dot(Vector2D other) {
		return this.x * other.x + this.y * other.y;
	}
	
	/**
	 * 向量叉乘
	 * @param other
	 * @return
	 */
	public double cross(Vector2D other) {
		return this.x * other.y - this.y * other.x;
	}
	
	/**
	 * 求法向量
	 * @return
	 */
	public Vector2D skew() {
		return new Vector2D(-y, x);
	}
	
	/**
	 * 反向
	 * @return
	 */
	public Vector2D reverse() {
		return new Vector2D(-x, -y);
	}
	
	/**
	 * 两点之间的距离
	 * @param other
	 * @return
	 */
	public double distance(Vector2D other) {
		double dx = other.x - this.x;
		double dy = other.y - this.y;
		
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	/**
	 * 两点之间距离的平方
	 * @param other
	 * @return
	 */
	public double distanceSquared(Vector2D other) {
		double dx = other.x - this.x;
		double dy = other.y - this.y;
		
		return dx * dx + dy * dy;
	}
	
	/**
	 * 计算向量的长度
	 * @return
	 */
	public double length() {
		return Math.sqrt(x * x + y * y);
	}
	
	/**
	 * 从start指向end的方向移动dis的距离
	 * @param start
	 * @param end
	 * @param dis 负表示反方向
	 * @return
	 */
	public static Vector2D lookAtPos(Vector2D start, Vector2D end, double dis) {
		if (end.equals(start)) {
			return start;
		}
		
		double diffx = end.x - start.x;
		double diffy = end.y - start.y;
		double diffdis = Math.sqrt(diffx * diffx + diffy * diffy);
		double multipler = dis / diffdis;
		
		return new Vector2D(start.x + diffx * multipler, start.y + diffy * multipler);
	}
	
	/**
	 * 从start指向dir的方向移动dis的距离
	 * @param start
	 * @param dir
	 * @param dis 负表示反方向
	 * @return
	 */
	public static Vector2D lookAtDir(Vector2D start, Vector2D dir, double dis) {
		if (dir.isZero()) {
			return start;
		}
		
		double dirdis = Math.sqrt(dir.x * dir.x + dir.y * dir.y);
		double multipler = dis / dirdis;
		
		return new Vector2D(start.x + dir.x * multipler, start.y + dir.y * multipler);
	}
	
	/**
	 * 计算向量v1逆时针转到v2的角度
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static double angle(Vector2D v1, Vector2D v2) {
		double angle;
		// 归一化
		v1 = v1.normalize();
		v2 = v2.normalize();
		// 点乘算夹角
		double dot = v1.dot(v2);
		if (floatEqual(dot, 1.0)) {
			angle = 0.0;
		} else if (floatEqual(dot, -1.0)) {
			angle = Math.PI;
		} else {
			angle = Math.acos(dot);
			// 叉乘确定夹角是在第一二象限还是在第三四象限
			double cross = v1.cross(v2);
			// 夹角在第三四象限
			if (cross < 0) { 
				angle = 2 * Math.PI - angle;
			}
		}
		
		double degree = angle * 180.0 / Math.PI;
		return degree;
	}
	
	/**
	 * 浮点数相等比较
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static boolean floatEqual(float f1, float f2) {
		return Math.abs(f1 - f2) < EPSILON;
	}
	
	public static boolean floatEqual(double d1, double d2) {
		return Math.abs(d1 - d2) < EPSILON;
	}
	
	/**
	 * 向量沿逆时针旋转指定角度后得到的新向量
	 * @param v
	 * @param degree
	 * @return
	 */
	public static Vector2D rotate(Vector2D v, double degree) {
		double radians = degree * Math.PI / 180;
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);
		return new Vector2D(v.x * cos - v.y*sin, v.x * sin + v.y * cos);
	}
	
	/**
	 * 计算两个点的中点
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static Vector2D middle(Vector2D v1, Vector2D v2) {
		double x = (v1.x + v2.x) / 2;
		double y = (v1.y + v2.y) / 2;
		return new Vector2D(x, y);
	}
	
	/**
	 * 零向量(向量的x,y都为零)
	 * @return
	 */
	public boolean isZero() {
		return (x > -EPSILON && x < EPSILON) && (y > -EPSILON && y < EPSILON);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		
		if (!(other instanceof Vector2D)) {
			return false;
		}
		
		Vector2D castOther = (Vector2D) other;
		return this.x == castOther.x && this.y == castOther.y;
	}
	
	@Override
	public int hashCode() {
		return Double.hashCode(x + y);
	}

	@Override
	public String toString() {
		return new StringBuilder().append("[").append(x).append(",").append(y).append("]").toString();
	}
}
