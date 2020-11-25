package org.intersection.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.eclipse.swt.graphics.Point;
import org.intersection.support.Vector2D;

/**
 * 格雷厄姆扫描算法
 * @author zhaijz
 */
public class GrahamScan {
	
	public static List<Point> grahamScan(List<Point> list) {
		List<Vector2D> temp = list.stream().map(a -> new Vector2D(a.x, a.y)).collect(Collectors.toList());
		Optional<Vector2D> min = temp.stream().min((a, b) -> Double.compare(a.y, b.y));
		if (min.isEmpty()) {
			return Collections.emptyList();
		}
		temp.remove(min.get());
		Vector2D bottom = min.get();
		Vector2D v0 = new Vector2D(1, 0);
		
		temp.sort((a, b) -> Double.compare(Vector2D.angle(v0, a.sub(bottom)), Vector2D.angle(v0, b.sub(bottom))));
		List<Vector2D> result = new ArrayList<Vector2D>();
		
		result.add(bottom);
		result.add(temp.remove(0));
		for (Vector2D vector : temp) {
			Vector2D vBase = result.get(result.size() - 1).sub(result.get(result.size() - 2));
			Vector2D v = vector.sub(result.get(result.size() - 2));
			double angle = Vector2D.angle(vBase, v);
			while (angle > 180.0D) {
				result.remove(result.size() - 1);
				vBase = result.get(result.size() - 1).sub(result.get(result.size() - 2));
				v = vector.sub(result.get(result.size() - 2));
				angle = Vector2D.angle(vBase, v);
			}
			result.add(vector);
		}
		
		return result.stream().map(a -> new Point((int)a.x, (int)a.y)).collect(Collectors.toList());
	}
}
