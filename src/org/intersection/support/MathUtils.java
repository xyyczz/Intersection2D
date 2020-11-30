package org.intersection.support;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MathUtils {
	public static double EPSILON = 1.0e-6;
	public static double COLLSION_ANGLE = 42.5;
	
	public final static int MINUS_ONE = -1;
	public final static int ZERO = 0;
	public final static int ONE = 1;
	public final static int TWO = 2;
	public final static int THREE = 3;
	public final static int FOUR = 4;
	public final static int FIVE = 5;
	public final static int SIX = 6;
	public final static int SEVEN = 7;
	public final static int EIGHT = 8;
	public final static int NINE = 9;
	public final static int TEN = 10;
	public final static int HUNDRED = 100;
	public final static int THOUSAND = 1000;
	public final static int TEN_THOUSAND = 10000;
	public final static int MILLION = 1000000;
	
	/**
	 * 计算 从起始点 到目标点是否产生碰撞
	 * @param originPos 起始点
	 * @param originDir 起始方向
	 * @param targetPos 目标点
	 * @param radius 碰撞体积
	 * @param angleArr
	 * @return
	 */
	public static boolean collisionDetect(Vector2D originPos, Vector2D originDir, Vector2D targetPos, double radius, List<Double> angleArr) {
		boolean result = false;
		//如果 AB 距离小于距离 并且角度
		double dis = originPos.distance(targetPos);
		if (dis < radius) {
			double angle = MathUtils.getRotateAngle(targetPos.x - originPos.x, targetPos.y - originPos.y,
					originDir.x, originDir.y
					);
//			Log.temp.info("碰撞产生angle{}:", angle);
			if (angle < COLLSION_ANGLE && angle >=0) {
//				Log.temp.info("碰撞产生angle{}:", angle);
				if (angleArr != null) {
					angleArr.add(angle);
				}
				return true;
			} else if (angle > (360 - COLLSION_ANGLE) && angle <=360) {
//				Log.temp.info("碰撞产生angle{}:", angle);
				if (angleArr != null) {
					angleArr.add(angle);
				}
				return true;
			}
		}
		
		return result;
	}
	
	/**
	 * 计算 从起始点 到目标点是否产生碰撞
	 * @param originPos 起始点
	 * @param originDir 起始方向
	 * @param targetPos 目标点
	 * @param radius 碰撞体积
	 * @param angleArr 返回起点到目标的角度
	 * @return
	 */
	public static boolean collisionDetectAll(Vector2D originPos, Vector2D originDir, Vector2D targetPos, double radius, List<Double> angleArr) {
		boolean result = false;
		//如果 AB 距离小于距离 并且角度
		double dis = originPos.distance(targetPos);
		if (dis < radius) {
			double angle = MathUtils.getRotateAngle(targetPos.x - originPos.x, targetPos.y - originPos.y,
					originDir.x, originDir.y
					);
			
			if (angle < COLLSION_ANGLE && angle >=0) {
				result =  true;
			} else if (angle > (360 - COLLSION_ANGLE) && angle <=360) {
				result = true;
			}
			
			if (angleArr != null) {
				if (angle > 180) {
					angle = angle - 360;
				}
				angleArr.add(angle);
			}
		}
		
		return result;
	}

	/**
	 * 获取向量p2到p1的逆时针旋转角，其中向量p1表示为x1，y1；向量p2表示为x2，y2
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double getRotateAngle(double x1, double y1, double x2, double y2) {
		double dist, dot, degree, angle;
		if (x1 == x2 && y1 == y2) {
			return 0;
		}

		// normalize，标准化
		dist = Math.sqrt(x1 * x1 + y1 * y1);
		x1 /= dist;
		y1 /= dist;

		dist = Math.sqrt(x2 * x2 + y2 * y2);
		x2 /= dist;
		y2 /= dist;

		// dot product 向量点积
		dot = x1 * x2 + y1 * y2;
		if (Math.abs(dot - 1.0) <= EPSILON) {
			angle = 0.0;
		} else if (Math.abs(dot + 10) <= EPSILON) {
			angle = Math.PI;
		} else {
			double cross;
			angle = Math.acos(dot);

			// cross product 向量x乘
			cross = x1 * y2 - x2 * y1;

			// vector p2 is clockwise from vector p1
			// with respect to the origin (0.0)
			if (cross > 0) {
				angle = 2 * Math.PI - angle;
			}
		}

		degree = angle * 180.0 / Math.PI;
		return degree;
	}
	
	/**
	 * 将value箝位到min和max之间
	 * @param min
	 * @param max
	 * @param value
	 * @return
	 */
	public static double clamp(double min, double max, double value) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}
	
	public static float clamp(float min, float max, float value) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}
	
	public static int clamp(int min, int max, int value) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
	}
	
	public static long clamp(long min, long max, long value) {
		if (value < min) {
			return min;
		} else if (value > max) {
			return max;
		} else {
			return value;
		}
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
	 * 两小数相乘，先将积取两位小数且向下取，然后转int且向上取整
	 * 
	 * @warn 货币计算转用
	 * @return
	 */
	public static int multiplyFloat(double x, double y) {
		BigDecimal a = new BigDecimal(String.valueOf(x));
		BigDecimal b = new BigDecimal(String.valueOf(y));
		return a.multiply(b).setScale(2, RoundingMode.DOWN).setScale(0, RoundingMode.UP).intValue();
	}
	
	/** 美分到美元 向下取两位小数 */
	public static float centToDoller(Number x) {
		BigDecimal a = new BigDecimal(String.valueOf(x));
		BigDecimal b = new BigDecimal("100");
		return a.divide(b).setScale(2, RoundingMode.DOWN).floatValue();
	}

	/**
	 * number 在指定区间 [min,max]
	 * @param min
	 * @param number
	 * @param max
	 * @return
	 */
	public static boolean isInTheSection(int min, int number, int max) {
		return min <= number && number <= max;
	}
	
	public static boolean isInTheSection(long min, long number, long max) {
		return min <= number && number <= max;
	}
}
