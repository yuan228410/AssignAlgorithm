package yuan;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
public class WindowAssign extends JFrame implements ActionListener {
	JTable table, table1;
	JButton 设置矩阵大小, 计算;
	JTextField inputNumber;
	JMenuBar menubar;
	JMenu menu1, menu2;
	JMenuItem item_question, item_solution, item_author;
	int rows = 3;
	JPanel p, p1, p3, p4;
	WindowAssign() {
		init();
		setSize(600, 550); // 设置窗体初始大小
		setVisible(true);
		setTitle("Java求解运筹学中的指派问题"); // 设置窗体标题
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	void init() {
		setLayout(new FlowLayout()); // 设置窗体布局为线性布局
		计算 = new JButton("计算");
		计算.setBackground(Color.PINK);//设置计算按钮背景色
		计算.setForeground(Color.GREEN);//设置计算按钮前景色
		设置矩阵大小 = new JButton("确定");
		设置矩阵大小.setBackground(Color.PINK);//设置设置矩阵大小按钮背景色
		设置矩阵大小.setForeground(Color.GREEN);//设置设置矩阵大小按钮前景色
		inputNumber = new JTextField(10);
		设置矩阵大小.addActionListener(this); // 为设置矩阵大小按钮绑定事件监听器
		计算.addActionListener(this); // 为计算按钮绑定事件监听器
		table = new JTable(3, 3);
		p1 = new JPanel();
		p = new JPanel();
		p1.setPreferredSize(new Dimension(2000, 30));
		p.setPreferredSize(new Dimension(2000, 30));
		p1.add(new JLabel("在下列表格中输入指派问题费用矩阵")); //
		p.add(new JLabel("输入矩阵大小"));
		p.add(inputNumber);
		p.add(设置矩阵大小);
		p.add(计算);
		add(p1);
		JScrollPane cz = new JScrollPane(table);
		cz.setPreferredSize(new Dimension(400, 150));
		add(cz);
		add(p);
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == 设置矩阵大小) { // 当点击设置矩阵大小按钮时设置表格的行列
			rows = Integer.parseInt(inputNumber.getText());
			table = new JTable(rows, rows);
			getContentPane().removeAll();
			add(p1);
			JScrollPane cz = new JScrollPane(table);
			cz.setPreferredSize(new Dimension(400, 150));
			add(cz);
			add(p);
			validate();
		} else if (e.getSource() == 计算) { // 当点击计算按钮时输出结果
			Object a[][] = new Object[rows][rows];//接受从表格传入的数据
			int price[][] = new int[rows][rows];//存储向指派方法中输入的数据
			//#begin将Object型数组转化为int型数组
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < rows; j++) {
					a[i][j] = table.getValueAt(i, j);
					price[i][j] = Integer.parseInt(a[i][j].toString());
				}
			}
			 //#endbegin
			p4 = new JPanel();
			p4.add(new JLabel("用0-1变量表示结果,o表示不 指派第i人做第j件事，1表示指派第i人做第j件事，结果如下:"));
			p4.setPreferredSize(new Dimension(2000, 30)); // 设置JPanel面板的初始大小
			this.add(p4);
			table1 = new JTable(rows, rows);//显示输出结果的表格
			JScrollPane cz = new JScrollPane(table1);//将表格放在浮动窗格中
			cz.setPreferredSize(new Dimension(400, 150)); // 设置浮动窗格的初始大小
			add(cz);//向窗体添加表格
			int b[] = AssignProblem.process(price, rows, rows).clone();//接受指派问题的输出结果
			int d[][] = new int[rows][rows];
			int sum = 0;
			for (int i = 0; i < b.length; i += 2) {
				d[b[i]][b[i + 1]] = 1;
			}
			//#begin将指派问题的输出结果存储到表格中
			for (int i = 0; i < rows; i++)
				for (int j = 0; j < rows; j++) {
					sum += d[i][j] * price[i][j];
					a[i][j] = (Object) (new Integer(d[i][j]));
					table1.setValueAt(a[i][j], i, j);
					table1.repaint();
				}
			//#endbegin
			p3 = new JPanel();
			p3.add(new JLabel("所以此指派问题最优解为:" + sum));//将指派问题的最终结果输出
			p3.setPreferredSize(new Dimension(2000, 30));//设置p3的大小
			this.add(p3);//向窗体添加p3
			this.paintAll(getGraphics().create());// 重绘窗体，以显示表格
		}
	}
}