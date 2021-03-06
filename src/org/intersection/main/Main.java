package org.intersection.main;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.intersection.algorithm.Gjk;
import org.intersection.algorithm.GrahamScan;
import org.intersection.algorithm.Sat;
import org.intersection.support.Vector2D;

/**
 * 主界面，左区域画板，右区域，控制按钮
 * @author zhaijz
 */
public class Main {
	public static Main mainView;
	public Display display;
	public Shell shell;
	
	public Canvas left;
	/** 选择的区域按钮 */
	public Button areaButton;
	/** A区域按钮 */
	public Button areaAButton;
	/** A区域点列表 */
	public List<Point> listA = new ArrayList<>();
	/** B区域点列表 */
	public List<Point> listB = new ArrayList<>();
	/** A区域点凸包列表 */
	public List<Point> listAConvexHull = new ArrayList<>();
	/** B区域点凸包列表 */
	public List<Point> listBConvexHull = new ArrayList<>();
	/** 耗时 */
	public Label labetCostValue;
	/** 算法 */
	public Combo comboAlgorithm;

	public static void main(String[] args) {
		mainView = new Main();
		Display display = Display.getDefault();
		mainView.display = display;
		mainView.shell = new Shell(display, SWT.SHELL_TRIM); // 创建窗口对象
		mainView.shell.setText("Intersection");
		mainView.shell.setSize(700, 500); // 设置窗口大小
		mainView.shell.setForeground(display.getSystemColor(SWT.COLOR_RED));
		mainView.setShellScreenCenter();
		RowLayout rowLayout = new RowLayout();
		mainView.shell.setLayout(rowLayout);
		
		mainView.createViews();
		mainView.shell.open(); // 打开窗口
		mainView.shell.layout();

		while (!mainView.shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
	
	private void setShellScreenCenter() {
		Rectangle clientArea = shell.getMonitor().getClientArea();
		Rectangle shellArea = shell.getClientArea();
		int x = clientArea.width / 2 - shellArea.width / 2;
		int y = clientArea.height / 2 - shellArea.height / 2;
		shell.setLocation(x, y);
	}
	
	private void createViews() {
		createLeftView();
		createRightView();
	}
	
	private void createLeftView() {
		Rectangle clientArea = this.shell.getClientArea();
		Canvas canvas = new Canvas(this.shell, SWT.BORDER);
		this.left = canvas;
		canvas.setLayoutData(new RowData((int)(clientArea.width * 0.7), clientArea.height));
//		composite.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		
		canvas.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				if (Main.this.areaButton == null) {
					Main.this.messageBox("请先选择A/B区域");
					return;
				}
				// 点去重
				Point point = new Point(arg0.x, arg0.y);
				if (areaContainsPoint(point)) {
					return;
				}
				List<Point> list = getAreaPointList();
				GC gc = new GC(canvas);
				gc.setForeground(getAreaPointColor());
				gc.drawPoint(arg0.x, arg0.y);
//				gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
//				gc.fillOval(arg0.x, arg0.y, 3, 3);
				list.add(new Point(arg0.x, arg0.y));
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {

			}
		});
	}
	
	private void createRightView() {
		Composite composite = new Composite(this.shell, SWT.BORDER);
		Rectangle clientArea = this.shell.getClientArea();
		composite.setLayoutData(new RowData((int)(clientArea.width * 0.27), clientArea.height));
		
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.marginHeight = 15;
		fillLayout.marginWidth = 15;
		fillLayout.spacing = 20;
		composite.setLayout(fillLayout);
		
		Button buttonA = new Button(composite, SWT.PUSH);
		this.areaAButton = buttonA;
		buttonA.setText("A区域选点");
		buttonA.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {

			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				Main.this.areaButton = buttonA;
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {

			}
		});
		Button buttonB = new Button(composite, SWT.PUSH);
		buttonB.setText("B区域选点");
		buttonB.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {

			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				Main.this.areaButton = buttonB;
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {

			}
		});
		
		Button button1 = new Button(composite, SWT.PUSH);
		button1.setSize(100, 50);
		button1.setText("计算凸包");
		button1.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				List<Point> list = getAreaPointList();
				if (list == null) {
					Main.this.messageBox("请选择A/B区域");
					return;
				}
				if (list.size() < 3) {
					Main.this.messageBox("区域选点数不能少于三个");
					return;
				}
				list = GrahamScan.grahamScan(list);
				cacheConvexHullPoints(list);
				for (int i = 0; i < list.size(); i++) {
					GC gc = new GC(mainView.left);
//					gc.setLineWidth(2);
					Point beginPoint = list.get(i);
					Point endPoint = list.get((i + 1) % list.size());
					gc.setForeground(getAreaPointColor());
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
		
		Composite compositeTimes = new Composite(composite, SWT.BORDER);
		compositeTimes.setLayout(new FillLayout());
		Label label = new Label(compositeTimes, SWT.NONE);
		label.setText("次数:");
		Combo combo = new Combo(compositeTimes, SWT.ABORT);
		combo.add("1");
		combo.add("1000");
		combo.add("10000");
		combo.add("100000");
		combo.select(0);
		
		Composite compositeAlgorithm = new Composite(composite, SWT.BORDER);
		compositeAlgorithm.setLayout(new FillLayout());
		Label labelAlgorithm = new Label(compositeAlgorithm, SWT.NONE);
		labelAlgorithm.setText("算法:");
		this.comboAlgorithm = new Combo(compositeAlgorithm, SWT.ABORT);
		comboAlgorithm.add("SAT");
		comboAlgorithm.add("GKJ");
		comboAlgorithm.select(0);
		
		Button button2 = new Button(composite, SWT.PUSH);
		button2.setText("相交计算");
		button2.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
				int index = combo.getSelectionIndex();
				String item = combo.getItem(index);
				int num = Integer.valueOf(item);
				intersection(num);
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				
			}
		});
		
		Composite compositeTime = new Composite(composite, SWT.BORDER);
		compositeTime.setLayout(new FillLayout());
		Label labelCost = new Label(compositeTime, SWT.NONE);
		labelCost.setText("耗时:");
		this.labetCostValue = new Label(compositeTime, SWT.NONE);
		Label labelMs = new Label(compositeTime, SWT.NONE);
		labelMs.setText("ms");
		
		Button button3 = new Button(composite, SWT.PUSH);
		button3.setText("清空");
		button3.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseUp(MouseEvent arg0) {
				
			}
			
			@Override
			public void mouseDown(MouseEvent arg0) {
				left.redraw();
				listA.clear();
				listB.clear();
				listAConvexHull.clear();
				listBConvexHull.clear();
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				
			}
		});
	}
	
	private void intersection(int num) {
		if (this.listA == null || this.listB == null) {
			messageBox("请先区域选点");
			return;
		}
		if (this.listAConvexHull == null || this.listBConvexHull == null) {
			messageBox("请先凸包计算");
			return;
		}
		List<Vector2D> listA = this.listAConvexHull.stream().map(point -> new Vector2D(point.x, point.y)).collect(Collectors.toList());
		List<Vector2D> listB = this.listBConvexHull.stream().map(point -> new Vector2D(point.x, point.y)).collect(Collectors.toList());
		boolean intersection = false;
		int index = this.comboAlgorithm.getSelectionIndex();
		boolean sat = "SAT".equals(this.comboAlgorithm.getItem(index));
		long start = System.currentTimeMillis();
		for (int i = 0; i < num; i++) {
			if (sat) {
				intersection = Sat.intersection(listA, listB);
			} else {
				intersection = Gjk.intersection(listA, listB);
			}
		}
		long end = System.currentTimeMillis();
		this.labetCostValue.setText(Long.toString(end - start));
		if (intersection) {
			GC gc = new GC(left);
			gc.setXORMode(true);
			gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
			gc.fillPolygon(pointListToArray(this.listAConvexHull));
			gc.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
			gc.fillPolygon(pointListToArray(this.listBConvexHull));
			
			messageBox("相交");
		} else {
			messageBox("不相交");
		}
	}
	
	private int[] pointListToArray(List<Point> list) {
		int[] points = new int[list.size() * 2];
		for (int i = 0; i < list.size(); i++) {
			Point point = list.get(i);
			points[i * 2] = point.x;
			points[i * 2 + 1] = point.y;
		}
		return points;
	}
	
	private void cacheConvexHullPoints(List<Point> list) {
		if (this.areaButton == this.areaAButton) {
			this.listAConvexHull = list;
		} else {
			this.listBConvexHull = list;
		}
	}
	
	private boolean areaContainsPoint(Point point) {
		if (this.areaButton == this.areaAButton) {
			return this.listA.contains(point);
		} else {
			return this.listB.contains(point);
		}
	}
	
	private Color getAreaPointColor() {
		if (this.areaButton == this.areaAButton) {
			return display.getSystemColor(SWT.COLOR_RED);
		} else {
			return display.getSystemColor(SWT.COLOR_BLUE);
		}
	}
	
	private List<Point> getAreaPointList() {
		if (this.areaButton == null) {
			return null;
		}
		if (this.areaButton == this.areaAButton) {
			return this.listA;
		} else {
			return this.listB;
		}
	}
	
	private void messageBox(String message) {
		MessageBox mb = new MessageBox(shell, SWT.ICON_SEARCH);
        mb.setText("Error");
        mb.setMessage(message);
        mb.open();
	}
}
