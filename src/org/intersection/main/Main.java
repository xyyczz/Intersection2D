package org.intersection.main;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 主界面
 * @author zhaijz
 */
public class Main {
	public static Main mainView;
	public Display display;
	public Shell shell;
	public Composite left;
	public List<Point> list = new ArrayList<>();

	public static void main(String[] args) {
		mainView = new Main();
		Display display = Display.getDefault();
		mainView.display = display;
		mainView.shell = new Shell(display, SWT.SHELL_TRIM); // 创建窗口对象
		mainView.shell.setLocation(new Point(100, 100));
		mainView.shell.setText("Intersection");
		mainView.shell.setSize(700, 500); // 设置窗口大小
		mainView.shell.setForeground(display.getSystemColor(SWT.COLOR_RED));
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
	
	private void createViews() {
		createLeftView();
		createRightView();
	}
	
	private void createLeftView() {
		Rectangle clientArea = this.shell.getClientArea();
		Composite composite = new Composite(this.shell, SWT.BORDER);
		this.left = composite;
		composite.setLayoutData(new RowData((int)(clientArea.width * 0.7), clientArea.height));
//		composite.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
		
		composite.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {

			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				GC gc = new GC(composite);
				gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
				gc.drawPoint(arg0.x, arg0.y);
				gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
//				gc.drawOval(arg0.x, arg0.y, 30, 30);
				gc.fillOval(arg0.x, arg0.y, 3, 3);
				Main.this.list.add(new Point(arg0.x, arg0.y));
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
		buttonA.setText("A区域选点");
		Button buttonB = new Button(composite, SWT.PUSH);
		buttonB.setText("B区域选点");
		
		Button button1 = new Button(composite, SWT.PUSH);
		button1.setSize(100, 50);
		button1.setText("计算凸包");
		button1.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				for (int i = 0; i < mainView.list.size(); i++) {
					GC gc = new GC(mainView.left);
					gc.setLineWidth(2);
					Point beginPoint = mainView.list.get(i);
					Point endPoint = mainView.list.get((i + 1) % mainView.list.size());
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
		Button button2 = new Button(composite, SWT.PUSH);
		button2.setText("相交计算");
		Button button3 = new Button(composite, SWT.PUSH);
		button3.setText("清空");
	}
}
