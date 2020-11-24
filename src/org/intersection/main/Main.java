package org.intersection.main;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Main {
	
	public static Shell shell;
	public static List<Point> list = new ArrayList<>();
	
	public static void main(String[] args) {
		Display display = Display.getDefault();
        shell = new Shell(display, SWT.SHELL_TRIM); // 创建窗口对象
        shell.setLocation(new Point(100, 100));
        shell.setText("Hello SWT");
        shell.setSize(500, 350); // 设置窗口大小
        shell.setForeground(display.getSystemColor(SWT.COLOR_RED));
        
        Button button = new Button(shell, SWT.PUSH);
        button.setBounds(0, 0, 100, 50);
		button.setText("button");
		button.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
				System.out.println("==============mouseUp");
				for (int i = 0; i < list.size(); i++) {
					GC gc = new GC(shell);
					gc.setLineWidth(3);
					Point beginPoint = list.get(i);
					Point endPoint = list.get((i + 1) % list.size());
					gc.drawLine(beginPoint.x, beginPoint.y, endPoint.x, endPoint.y);
					gc.dispose();
				}
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				
			}
		});
        
        shell.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
				GC gc = new GC(shell);
				gc.drawPoint(arg0.x, arg0.y);
				gc.drawOval(arg0.x, arg0.y, 3, 3);
				gc.fillOval(arg0.x, arg0.y, 3, 3);
				list.add(new Point(arg0.x, arg0.y));
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				
			}
		});
//        gc.drawPolygon(new int[] {0, 0, 200, 200, 100, 300});
//        shell.addPaintListener(new PaintListener(){
//        	public void paintControl(PaintEvent e){
//        		Rectangle clientArea = shell.getClientArea();
//        		e.gc.drawPolygon(new int[] {0, 0, 200, 200, 100, 300});
//        	}
//        });
//        shell.addPaintListener(new PaintListener() {
//			public void paintControl(PaintEvent e) {
//				System.out.println("=============PaintEvent");
//				if (Main.list.size() == 0) {
//					return;
//				}
//        		if (Main.list.size() == 1) {
//        			e.gc.drawPoint(Main.list.get(0).x, Main.list.get(0).y);
//					return;
//				}
//        		int[] polygon= new int[Main.list.size() * 2];
//        		for (int i = 0; i < Main.list.size(); i++) {
//        			Point pointBegin = Main.list.get(i);
//        			polygon[i * 2] = pointBegin.x;
//        			polygon[i * 2 + 1] = pointBegin.x;
////        			Point pointBegin = Main.list.get(i);
////        			Point pointEnd = Main.list.get((i + 1) % Main.list.size());
////        			e.gc.drawLine(pointBegin.x, pointBegin.y, pointEnd.x, pointEnd.y);
//				}
//        		e.gc.drawPolygon(polygon);
//			}
//		});
        
       
        shell.open(); // 打开窗口
        shell.layout();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
	}
}
