package org.intersection.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.intersection.support.Vector2D;

/**
 * 分离轴算法凸包相交计算
 * 参考引用：https://en.wikipedia.org/wiki/Hyperplane_separation_theorem
 * https://www.jianshu.com/p/4000a301c32a
 * @author zhaijz
 *
 */
public class Sat {
	
	public static boolean intersection(List<Vector2D> p, List<Vector2D> q) {
		if (p.isEmpty() || q.isEmpty()) {
			return false;
		}
		List<Vector2D> min = p;
		if (q.size() < p.size()) {
			min = q;
		}
		
		for (int i = 0; i < min.size(); i++) {
			Vector2D dir = min.get(i).sub(min.get((i + 1) % min.size()));
			Vector2D skew = dir.skew();
			double[] sectionP = satProjection(skew, p);
			double[] sectionQ = satProjection(skew, q);
			// 只要有一个轴能分离，说明不相交
			if (!sectionIntersection(sectionP, sectionQ)) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean sectionIntersection(double[] p, double[] q) {
		if (p[0] >= q[0] && p[0] <= q[1]) {
			return true;
		}
		if (p[1] >= q[0] && p[1] <= q[1]) {
			return true;
		}
		if (q[0] >= p[0] && q[0] <= p[1]) {
			return true;
		}
		if (q[1] >= p[0] && q[1] <= p[1]) {
			return true;
		}
		return false;
	}
	
	private static double[] satProjection(Vector2D skew, List<Vector2D> p) {
		skew = skew.normalize();
		double min = Double.MAX_VALUE;
		double max = -Double.MAX_VALUE;
		double value;
		for (int i = 0; i < p.size(); i++) {
			value = skew.dot(p.get(i));
			if (value < min) {
				min = value;
			}
			if (value > max) {
				max = value;
			}
		}
		return new double[] {min, max};
	}

	public static void main(String[] args) {
		List<Vector2D> p = new ArrayList<>();
		p.add(new Vector2D(0, 0));
		p.add(new Vector2D(0, 2));
		p.add(new Vector2D(2, 2));
		p.add(new Vector2D(2, 0));
		List<Vector2D> q = new ArrayList<>();
//		q.add(new Vector2D(1, -1));
//		q.add(new Vector2D(1, 1));
//		q.add(new Vector2D(3, 1));
		
//		q.add(new Vector2D(1.999, -1));
//		q.add(new Vector2D(1.999, 1));
//		q.add(new Vector2D(2.999, 1));
		
//		q.add(new Vector2D(2.001, -1));
//		q.add(new Vector2D(2.001, 1));
//		q.add(new Vector2D(3.001, 1));
		
		q.add(new Vector2D(2.000001, 1));
		q.add(new Vector2D(3, 2));
		q.add(new Vector2D(4, 1));
		q.add(new Vector2D(3, 0));
		int num = 1000000;
		boolean result = false;
		long time1 = System.currentTimeMillis();
		for (int i = 0; i < num; i++) {
			result = intersection(p, q);
		}
		long time2 = System.currentTimeMillis();
		System.out.println(time2 - time1);
		if (result) {
			System.out.println("A B 相交");
		} else {
			System.out.println("A B 不相交");
		}
	}
}
