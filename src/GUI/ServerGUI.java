package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import server.Exit;
import server.Main;

/**
 * 界面
 * 
 */
public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 1L;

	// 主面板区
	private JPanel contentPane;

	// 文本显示区
	private JTextArea info;

	// 滚动面板
	private JScrollPane scrollPane;

	// "启动"菜单
	private JButton startButton;

	// "停止"菜单
	private JButton stopButton;

	// "配置"菜单
	private JButton settingButton;

	// "日志"菜单
	private JButton logButton;

	// "退出"菜单
	private JButton exitButton;

	// "关于"菜单
	private JButton aboutButton;

	// 浮出菜单栏
	private PopupMenu pop;

	// 托盘"打开"菜单
	private MenuItem open;

	// 托盘"启动"菜单
	private MenuItem startTray;

	// 托盘"停止"浮出菜单
	private MenuItem stopTray;

	// 托盘"退出"菜单
	private MenuItem exitTray;

	// 托盘图标
	private TrayIcon trayicon;

	// 图片
	private Image image;

	// 字体
	private Font font;

	// 屏幕大小
	private Dimension d;

	// 图标
	private ImageIcon icon;

	// 按钮面板
	private JScrollPane buttonJSP;

	/**
	 * 启动界面
	 */
	public static void openGUI() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ServerGUI frame = new ServerGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * 界面设计
	 */
	public ServerGUI() {
		// 字体及UI设计
		font = new Font(GUIConstants.font, Font.PLAIN, 13);
		Color co = new Color(242, 242, 242);
		UIManager.put("OptionPane.font", font);
		UIManager.put("OptionPane.messageFont", font);
		UIManager.put("OptionPane.buttonFont", font);
		UIManager.put("Button.background", co);
		UIManager.put("Button.font", font);

		// 设置用户在此窗体上发起 "close" 时默认执行的操作:隐藏该窗体
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		// 根据屏幕大小设置窗口打开位置
		d = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(480, 320);
		setLocation((int) (d.getWidth() - getWidth()) / 2,
				(int) (d.getHeight() - getHeight()) / 2);
		// 设置窗口不可调节大小
		setResizable(false);
		contentPane = new JPanel();
		// 设置组件边框
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// 设置容器布局管理器:空布局
		contentPane.setLayout(null);
		// 添加窗体
		setContentPane(contentPane);
		// 设置标题
		setTitle(GUIConstants.title);
		// 设置图标
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				GUIConstants.imgPath + "/icon.png"));

		// “开始”菜单UI，并监听
		icon = new ImageIcon(GUIConstants.imgPath + "/startup.png");
		startButton = new JButton(GUIConstants.startMenu);
		startButton.setLocation(10, 10);
		setButtonFrame(startButton, icon);
		startButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					startup();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// “停止”菜单UI，并监听
		icon = new ImageIcon(GUIConstants.imgPath + "/stop.png");
		stopButton = new JButton(GUIConstants.stopMenu);
		stopButton.setEnabled(false);
		stopButton.setLocation(80, 10);
		setButtonFrame(stopButton, icon);
		stopButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				stop();
			}
		});

		// “配置”菜单UI，并监听
		icon = new ImageIcon(GUIConstants.imgPath + "/setting.png");
		settingButton = new JButton(GUIConstants.settingMenu);
		settingButton.setLocation(150, 10);
		setButtonFrame(settingButton, icon);
		settingButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					new SetDialog(contentPane, GUIConstants.settingMenu);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		// “日志”菜单UI，并监听
		icon = new ImageIcon(GUIConstants.imgPath + "/log.png");
		logButton = new JButton(GUIConstants.logMenu);
		logButton.setLocation(220, 10);
		setButtonFrame(logButton, icon);
		logButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					java.awt.Desktop.getDesktop().open(
							new File(GUIConstants.openLogs));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// “退出”菜单UI，并监听
		icon = new ImageIcon(GUIConstants.imgPath + "/exit.png");
		exitButton = new JButton(GUIConstants.exitMenu);
		exitButton.setLocation(290, 10);
		setButtonFrame(exitButton, icon);
		exitButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ImageIcon ask = new ImageIcon(Toolkit.getDefaultToolkit()
						.getImage(GUIConstants.imgPath + "/ask.png"));
				ask.setImage(ask.getImage().getScaledInstance(30, 30,
						Image.SCALE_DEFAULT));
				int flag = JOptionPane.showConfirmDialog(contentPane,
						GUIConstants.exitMsg, GUIConstants.exitTitle,
						JOptionPane.YES_NO_OPTION, 0, ask);
				if (flag == 0)
					Exit.exitAll();
			}
		});

		// “关于”菜单UI，并监听
		icon = new ImageIcon(GUIConstants.imgPath + "/about.png");
		aboutButton = new JButton(GUIConstants.aboutMenu);
		aboutButton.setLocation(360, 10);
		setButtonFrame(aboutButton, icon);
		aboutButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ImageIcon icon = new ImageIcon(Toolkit.getDefaultToolkit()
						.getImage(GUIConstants.imgPath + "/aboutDialog.png"));
				icon.setImage(icon.getImage().getScaledInstance(35, 35,
						Image.SCALE_DEFAULT));
				JOptionPane.showMessageDialog(contentPane,
						GUIConstants.aboutMsg, GUIConstants.aboutTitle,
						JOptionPane.INFORMATION_MESSAGE, icon);
			}
		});

		buttonJSP = new JScrollPane();
		buttonJSP.setBounds(5, 5, 465, 90);
		buttonJSP.setOpaque(false);
		contentPane.add(buttonJSP);

		// 文本区
		info = new JTextArea();
		info.setEditable(false);
		info.setFont(new Font(GUIConstants.font, Font.PLAIN, 15));
		info.setText(GUIConstants.initText);
		contentPane.add(info, BorderLayout.CENTER);

		// 给文本区添加可滚动的面板
		scrollPane = new JScrollPane(info);
		scrollPane.setBounds(5, 105, 465, 180);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// 配置浮出菜单"打开"
		pop = new PopupMenu();
		open = new MenuItem(GUIConstants.openMenu);
		setTrayMenuFrame(open);
		open.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openFrame();
			}
		});
		// 配置浮出菜单"启动"
		startTray = new MenuItem(GUIConstants.startMenu);
		startTray.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					startup();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		setTrayMenuFrame(startTray);
		// 配置浮出菜单"停止"
		stopTray = new MenuItem(GUIConstants.stopMenu);
		stopTray.setEnabled(false);
		stopTray.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		setTrayMenuFrame(stopTray);
		// 配置浮出菜单"退出"
		exitTray = new MenuItem(GUIConstants.exitMenu);
		exitTray.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		setTrayMenuFrame(exitTray);

		// 添加托盘图标
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			image = Toolkit.getDefaultToolkit().getImage(
					GUIConstants.imgPath + "/icon.png");
			trayicon = new TrayIcon(image, GUIConstants.title, pop);
			// 自动设置托盘图标大小
			trayicon.setImageAutoSize(true);
			// 双击图标打开或关闭主面板
			trayicon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						if (getExtendedState() == Frame.ICONIFIED) {
							openFrame();
						} else {
							// 设置窗口状态(最小化至托盘)
							setExtendedState(Frame.ICONIFIED);
						}
					}
				}

			});
			try {
				tray.add(trayicon);
			} catch (AWTException e) {
				e.printStackTrace();
			}

			// 监听窗口事件
			addWindowListener(new WindowAdapter() {
				// 窗口从正常状态变为最小化状态时调用
				@Override
				public void windowIconified(WindowEvent e) {
					// setVisible(false);
					dispose();
				}
			});
		}
	}

	/**
	 * 显示主面板
	 */
	public void openFrame() {
		setVisible(true);
		setExtendedState(Frame.NORMAL);
	}

	/**
	 * 托盘浮出菜单通用配置
	 * 
	 * @param menuItem
	 */
	public void setTrayMenuFrame(MenuItem menuItem) {
		menuItem.setFont(new Font(GUIConstants.font, Font.PLAIN, 14));
		pop.add(menuItem);
	}

	/**
	 * 启动
	 * 
	 * @throws IOException
	 */
	public void startup() throws IOException {
		if (!startButton.isEnabled())
			return;
		if (!Main.start()) {
			info.append("\n"
					+ new SimpleDateFormat(GUIConstants.format).format(Calendar
							.getInstance().getTime())
					+ GUIConstants.startFailMsg);
		} else {
			startButton.setEnabled(false);
			startButton.setOpaque(false);
			startTray.setEnabled(false);
			stopButton.setEnabled(true);
			stopTray.setEnabled(true);
			info.append("\n"
					+ new SimpleDateFormat(GUIConstants.format).format(Calendar
							.getInstance().getTime())
					+ GUIConstants.startSucMsg);
		}
	}

	/**
	 * 停止
	 */
	public void stop() {
		if (!stopButton.isEnabled())
			return;
		if (!Main.shutdown()) {
			info.append("\n"
					+ new SimpleDateFormat(GUIConstants.format).format(Calendar
							.getInstance().getTime())
					+ GUIConstants.stopFailMsg);
		} else {
			startButton.setEnabled(true);
			startTray.setEnabled(true);
			stopButton.setEnabled(false);
			stopButton.setOpaque(false);
			stopTray.setEnabled(false);
			info.append("\n"
					+ new SimpleDateFormat(GUIConstants.format).format(Calendar
							.getInstance().getTime()) + GUIConstants.stopSucMsg);
		}
	}

	/**
	 * 菜单按钮通用配置
	 * 
	 * @param jb
	 * @param ii
	 */
	public void setButtonFrame(final JButton jb, ImageIcon ii) {
		jb.setSize(60, 80);
		ii.setImage(ii.getImage()
				.getScaledInstance(50, 50, Image.SCALE_DEFAULT));
		jb.setIcon(ii);
		jb.setHorizontalTextPosition(SwingConstants.CENTER);
		jb.setVerticalTextPosition(SwingConstants.BOTTOM);
		// 透明
		jb.setOpaque(false);
		// 设置背景色
		jb.setBackground(Color.lightGray);
		// 取消按钮边框
		jb.setBorderPainted(false);
		// 不绘制焦点
		jb.setFocusPainted(false);
		jb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				jb.setOpaque(true);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				jb.setOpaque(false);
			}
		});
		contentPane.add(jb);
	}
}
