package org.intersection.algorithm;

import java.util.ArrayList;
import java.util.List;

import org.intersection.support.MathUtils;
import org.intersection.support.TwoTuple;
import org.intersection.support.Vector2D;

/**
 * GJK(Gilbert–Johnson–Keerthi distance algorithm)算法凸包相交计算
 * 参考引用：https://en.wikipedia.org/wiki/Gilbert%E2%80%93Johnson%E2%80%93Keerthi_distance_algorithm
 * https://zhuanlan.zhihu.com/p/34344829
 * @author zhaijz
 */
public class Gjk {
	/**
	 * 计算凸多边形是否相交，p,q为多边形点集
	 * @param p
	 * @param q
	 * @return
	 */
	public static boolean intersection(List<Vector2D> p, List<Vector2D> q) {
		if (p.isEmpty() || q.isEmpty()) {
			return false;
		}
		int result = 0;
		Vector2D dir = p.get(0).sub(q.get(0));
		List<Vector2D> simplex = new ArrayList<>();
		TwoTuple<Integer, Vector2D> twoTuple;
		for (int i = 0; i < 500; i++) {
			twoTuple = gjkIntersection(p, q, simplex, dir);
			result = twoTuple.first;
			dir = twoTuple.second;
			if (result != -1) {
				break;
			}
		}
		return result > 0;
	}
	
	private static TwoTuple<Integer, Vector2D> gjkIntersection(List<Vector2D> p, List<Vector2D> q, List<Vector2D> simplex, Vector2D dir) {
		Vector2D A;
		if (simplex.isEmpty()) {
			// initialize
			A = support(p, dir).sub(support(q, dir.reverse()));
			dir = A.reverse();
			simplex.add(A);
		}
		
		A = support(p, dir).sub(support(q, dir.reverse()));
		double dotProduct = A.dot(dir);
		if (dotProduct < 0.0D) {
			return new TwoTuple<Integer, Vector2D>(0, null);
		}
		// 更新单纯形
		simplex.add(A);
		
		Vector2D P;
		switch (simplex.size()) {
		case 0:
			// 不应该发生
			break;
		case 1:
			dir = A.reverse();
			break;
		case 2: {
			A = simplex.get(0);
			Vector2D B = simplex.get(1);
			Vector2D lineSeg = B.sub(A);
			double t = A.reverse().dot(B.sub(A));
			t = MathUtils.clamp(0.0D, 1.0D, t / lineSeg.dot(lineSeg));
			if (1.0D - t < MathUtils.EPSILON) {
				P = B;
				simplex.clear();
				simplex.add(P);
			} else {
				P = A.sum(B.sub(A).mul(t));
			}
			dir = P.reverse();
			break;
		}
		case 3: 
			TwoTuple<Double, Double> result = nearestPointInTriangleToPoint(simplex, new Vector2D(0.0D, 0.0D));
			double s = result.first;
			double t = result.second;
			A = simplex.get(0);
			Vector2D B = simplex.get(1);
			Vector2D C = simplex.get(2);
            if (s < MathUtils.EPSILON) {
                // P is on edge 1 (AC) so B can be removed
                simplex.clear();
                simplex.add(A);
                simplex.add(C);
            }

            if (t < MathUtils.EPSILON) {
                // P is on edge 0 (AB) so C can be removed
                simplex.clear();
                simplex.add(A);
                simplex.add(B);
            }

            if (Math.abs(1.0D - (s + t)) < MathUtils.EPSILON) {
                // P is on edge 3 (BC) so A can be removed
                simplex.clear();
                simplex.add(B);
                simplex.add(C);
            }

            P = A.sum(B.sub(A).mul(s)).sum(C.sub(A).mul(t));

            dir = P.reverse();
			break;
		default:
			break;
		}
//		System.err.println(dir.length());
		// 逼近(0,0)
		if (dir.length() < MathUtils.EPSILON) {
			return new TwoTuple<Integer, Vector2D>(1, dir);
		}
//		System.err.println("simplex.size()=" + simplex.size());
		return new TwoTuple<Integer, Vector2D>(-1, dir);
	}
	
	private static TwoTuple<Double, Double> nearestPointInTriangleToPoint(List<Vector2D> vertices, Vector2D point) {
		double s, t;
		Vector2D A = vertices.get(0);
		Vector2D B = vertices.get(1);
		Vector2D C = vertices.get(2);
		
		Vector2D edge0 = B.sub(A);
		Vector2D edge1 = C.sub(A);
		Vector2D v0 = A.sub(point);
		
		double a, b, c, d, e;
		a = edge0.dot(edge0);
		b = edge0.dot(edge1);
		c = edge1.dot(edge1);
		d = edge0.dot(v0);
		e = edge1.dot(v0);
		
		double det = a * c - b * b;
	    s = b * e - c * d;
	    t = b * d - a * e;
	    
	    if (s + t < det) {
	        if (s < 0.0D) {
	            if (t < 0.0D) {
	                if (d < 0.0D) {
	                    s = MathUtils.clamp(0.0D, 1.0D, -d / a);
	                    t = 0.0D;
	                } else {
	                    s = 0.0D;
	                    t = MathUtils.clamp(0.0D, 1.0D, -e / c);
	                }
	            } else {
	                s = 0.0D;
	                t = MathUtils.clamp(0.0D, 1.0D, -e / c);
	            }
	        } else if (t < 0.0D) {
	            s = MathUtils.clamp(0.0D, 1.0D, -d / a);
	            t = 0.0D;
	        } else {
	        	double invDet = 1.0D / det;
	            s *= invDet;
	            t *= invDet;
	        }
	    } else {
	        if (s < 0.0D) {
	        	double tmp0 = b + d;
	        	double tmp1 = c + e;
	            if (tmp1 > tmp0) {
	            	double numer = tmp1 - tmp0;
	            	double denom = a - 2.0D * b + c;
	                s = MathUtils.clamp(0.0D, 1.0D, numer / denom);
	                t = 1.0D - s;
	            } else {
	                t = MathUtils.clamp(0.0D, 1.0D, -e / c);
	                s = 0.0D;
	            }
	        } else if (t < 0.0D) {
	            if (a + d > b + e) {
	            	double numer = c + e - b - d;
	            	double denom = a - 2.0D * b + c;
	                s = MathUtils.clamp(0.0D, 1.0D, numer / denom);
	                t = 1.0D - s;
	            } else {
	                s = MathUtils.clamp(0.0D, 1.0D, -e / c);
	                t = 0.0D;
	            }
	        } else {
	        	double numer = c + e - b - d;
	        	double denom = a - 2.0D * b + c;
	            s = MathUtils.clamp(0.0D, 1.0D, numer / denom);
	            t = 1.0D - s;
	        }
	    }
	    return new TwoTuple<Double, Double>(s, t);
	}
	
	/**
	 * GJK support函数实现
	 * @param shap
	 * @param dir
	 * @return
	 */
	private static Vector2D support(List<Vector2D> shap, Vector2D dir) {
		double dot = -Double.MAX_VALUE;
		Vector2D result = null;
		for (Vector2D point : shap) {
			double d = point.dot(dir);
			if (d > dot) {
				dot = d;
				result = point;
			}
		}
		return result;
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
