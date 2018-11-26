package GUI;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import server.Constants;
import server.Main;

/**
 * 配置对话框
 * 
 */
public class SetDialog extends JFrame {

	private static final long serialVersionUID = 1L;

	// 容器
	private JFrame frame;

	// 主面板
	private JPanel setPane;

	// 端口标签
	private JLabel portLabel;

	// 字符集标签
	private JLabel charsetLabel;

	// 默认项目标签
	private JLabel defaultProjectLabel;

	// 是否缓存标签
	private JLabel isCachedLable;

	// 字体
	private Font font;

	// 端口输入区
	private JTextField portText;

	// 字符集下拉输入框
	@SuppressWarnings("rawtypes")
	private JComboBox charsetBox;

	// 默认项目输入框
	private JTextField defaultProjectText;

	// 单选按钮组
	private ButtonGroup bg;

	// 按钮“是”
	private JRadioButton yes;

	// 按钮“否”
	private JRadioButton no;

	// “打开配置文件”
	private JButton openConfFile;

	// “保存”按钮
	private JButton save;

	// “取消”按钮
	private JButton cancel;

	// 窗口在屏幕上的水平位置
	private int locateX;

	// 窗口在屏幕上的垂直位置
	private int locateY;

	// 窗口的宽度
	private int dialogWidth;

	// 窗口的高度
	private int dialogHeight;

	// 内容距离窗体左侧的水平距离
	private int contentX;

	// 内容距离窗体顶部的垂直距离
	private int contentY;

	// 每行内容的高度
	private int contentHeight;

	// 标签的宽度
	private int labelWidth;

	// 输入区的宽度
	private int inputWidth;

	// 行距
	private int marginTop;

	/**
	 * 配置对话框构造函数
	 * 
	 * @param parentComponent
	 * @param title
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public SetDialog(Component parentComponent, String title) {
		// 参数
		Map<String, String> map = Constants.getUserCfg();

		// 字体及UI设计
		font = new Font(GUIConstants.font, Font.PLAIN, 13);
		UIManager.put("Label.font", font);
		UIManager.put("RadioButton.font", font);
		UIManager.put("Button.font", font);
		UIManager.put("ComboBox.font", font);
		UIManager.put("TextArea.font", font);

		// 构造frame并设置位置
		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		dialogWidth = 280;
		dialogHeight = 230;
		frame.setSize(dialogWidth, dialogHeight);
		Point p = parentComponent.getLocationOnScreen();
		locateX = (int) (p.getX() + parentComponent.getWidth() / 2);
		locateY = (int) (p.getY() + parentComponent.getHeight() / 2);
		frame.setLocation(locateX - frame.getWidth() / 2,
				locateY - frame.getHeight() / 2);
		// 设置标题
		frame.setTitle(title);
		// 设置图标
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(
				GUIConstants.imgPath + "/setting.png"));

		// 构造setPane
		setPane = new JPanel();
		setPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		// 空布局
		setPane.setLayout(null);
		frame.setContentPane(setPane);
		frame.setVisible(true);
		// 设置窗口不可调节大小
		frame.setResizable(false);

		contentHeight = 20;
		marginTop = 9;
		labelWidth = 60;
		inputWidth = 90;
		contentX = (dialogWidth - labelWidth - inputWidth) / 2;
		contentY = 13;

		// “端口”标签
		portLabel = new JLabel(GUIConstants.portLabl);
		portLabel.setHorizontalAlignment(SwingConstants.LEFT);
		portLabel.setBounds(contentX, contentY, labelWidth, contentHeight);
		frame.add(portLabel);
		// “端口”输入框
		portText = new JTextField(map.get("port"));
		portText.setBounds(contentX + portLabel.getWidth(), contentY,
				inputWidth, contentHeight);
		frame.add(portText);

		// “字符集”标签
		charsetLabel = new JLabel(GUIConstants.charsetLabel);
		charsetLabel.setHorizontalAlignment(SwingConstants.LEFT);
		charsetLabel.setBounds(contentX,
				portLabel.getY() + portLabel.getHeight() + marginTop,
				labelWidth, contentHeight);
		frame.add(charsetLabel);
		// “字符集”输入
		charsetBox = new JComboBox(GUIConstants.charsetItem);
		charsetBox.setEditable(true);
		charsetBox.setSelectedItem(map.get("charset"));
		charsetBox.setBounds(contentX + charsetLabel.getWidth(),
				charsetLabel.getY(), inputWidth, contentHeight);
		frame.add(charsetBox);

		// “默认项目目录”标签
		defaultProjectLabel = new JLabel(GUIConstants.defaultProjectLabel);
		defaultProjectLabel.setHorizontalAlignment(SwingConstants.LEFT);
		defaultProjectLabel.setBounds(contentX, charsetLabel.getY()
				+ charsetLabel.getHeight() + marginTop, labelWidth,
				contentHeight);
		frame.add(defaultProjectLabel);
		// “默认项目目录”输入
		defaultProjectText = new JTextField(map.get("default_project"));
		defaultProjectText.setBounds(contentX + defaultProjectLabel.getWidth(),
				defaultProjectLabel.getY(), inputWidth, contentHeight);
		frame.add(defaultProjectText);

		// “是否缓存”标签
		isCachedLable = new JLabel(GUIConstants.isCachedLable);
		isCachedLable.setHorizontalAlignment(SwingConstants.LEFT);
		isCachedLable.setBounds(contentX, defaultProjectLabel.getY()
				+ defaultProjectLabel.getHeight() + marginTop, labelWidth,
				contentHeight);
		frame.add(isCachedLable);
		// “是否缓存”单选按钮
		bg = new ButtonGroup();
		yes = new JRadioButton(GUIConstants.yes);
		no = new JRadioButton(GUIConstants.no);
		yes.setSelected(Boolean.valueOf(map.get("is_cached")));
		no.setSelected(!Boolean.valueOf(map.get("is_cached")));
		bg.add(yes);
		bg.add(no);
		yes.setBounds(contentX + isCachedLable.getWidth(),
				isCachedLable.getY(), inputWidth / 2, contentHeight);
		no.setBounds(yes.getX() + yes.getWidth(), yes.getY(), inputWidth / 2,
				contentHeight);
		frame.add(yes);
		frame.add(no);

		// “保存”按钮
		save = new JButton(GUIConstants.save);
		save.setBounds(contentX,
				isCachedLable.getY() + isCachedLable.getHeight() + marginTop,
				60, 30);
		frame.add(save);
		// 监听“保存”按钮
		save.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {				
				ImageIcon ask = new ImageIcon(Toolkit.getDefaultToolkit()
						.getImage(GUIConstants.imgPath + "/ask.png"));
				ask.setImage(ask.getImage().getScaledInstance(30, 30,
						Image.SCALE_DEFAULT));
				int flag = JOptionPane.showConfirmDialog(setPane,
						GUIConstants.saveMsg, GUIConstants.saveTitle,
						JOptionPane.YES_NO_OPTION, 0, ask);
				if (flag != 0){return;}				
				// 设置端口
				Constants.setPort(Integer.parseInt(portText.getText()));
				// 设置charset
				Constants.setCharset((String) charsetBox.getSelectedItem());
				// 设置default_page
				Constants.setDeaultProject(defaultProjectText.getText());
				// 设置isCached
				Constants.setIsCached(yes.isSelected());
				if (!Main.state) {
					Constants.writeUserDocument();
				}
				frame.dispose();
			}
		});

		// “取消”按钮
		cancel = new JButton(GUIConstants.cancel);
		cancel.setBounds(contentX + save.getWidth() + 30, save.getY(), 60, 30);
		frame.getContentPane().add(cancel);
		// 监听“取消”按钮
		cancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.dispose();
			}
		});

		// “打开配置文件”标签
		openConfFile = new JButton(GUIConstants.openConfFile);
		openConfFile.setOpaque(false);
		openConfFile.setBounds(contentX, save.getY() + save.getHeight()
				+ marginTop, 150, contentHeight);
		frame.add(openConfFile);
		openConfFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					java.awt.Desktop.getDesktop().open(
							new File(GUIConstants.openConf));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		// 监听窗口事件
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
			}
		});

	}
}
