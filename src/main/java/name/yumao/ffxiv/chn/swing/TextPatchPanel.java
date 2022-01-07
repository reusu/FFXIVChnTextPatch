package name.yumao.ffxiv.chn.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;
import java.util.logging.Logger;

// import java.util.ArrayList;
// import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
// import name.yumao.ffxiv.chn.model.TeemoUpdateVo;
import name.yumao.ffxiv.chn.thread.ReplaceThread;
import name.yumao.ffxiv.chn.thread.RollbackThread;
import name.yumao.ffxiv.chn.util.EXDFUtil;
import name.yumao.ffxiv.chn.util.FileUtil;
import name.yumao.ffxiv.chn.util.HexUtils;
// import name.yumao.ffxiv.chn.util.UpdateUtil;
import name.yumao.ffxiv.chn.util.res.Config;

public class TextPatchPanel extends JFrame implements ActionListener {
	
	// private List updates;
	private static Point origin = new Point();
	private static String title = "PatchTool";
	private JLabel title_lable = new JLabel(title);
	private Dimension dimension;
	private JButton bootButton = new JButton("快速啟動");
	private JButton configButton = new JButton("設置");
	private JButton closeButton = new JButton("x");
	private JPanel titlePanel = new JPanel();
	private JPanel bodyPanel = new JPanel();
	public JButton replaceButton = new JButton("漢化");
	public JButton rollbackButton = new JButton("還原");
	private JLabel menuText = new JLabel("by GPointChen",  SwingConstants.RIGHT);
	
	public TextPatchPanel() {
		super(title);
		setUndecorated(true);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		setIconImage(toolkit.createImage(HexUtils.hexStringToBytes("89504E470D0A1A0A0000000D4948445200000040000000400806000000AA6971DE000004EC4944415478DAED5B69485451141EB50DB3C56AA821A329C7596CD459A2FA57467B513F0209F147CB0F2B82C20ACA928ACA3F2544605909051651D962641B914542942456602B6DE492AD56A29665DF9137F27ACD7BB3F4EECCBB830F0EF7CD9B33EFDEEFDC73CE3DF79C3B3A5D082EB7DB3D283535D5EE703816B85CAE5CA7D3791C7407F74FF1DD1BB4F5681BD0D6815E821EE1FB4AB487D1AE453B2B2525C58A77F4D7F174198DC67E003D07200A00A20AD40EA09D81127EF71BEF20C194A05D6AB3D90C5AC6DD0B839C8FC19E007D0F06B01F02F905BA857EB2939393E334811A031982016DC6C0DEB2002D47E8F32BFA2C84798C090B70D8663C06918F417C535261B27750332B41E0DD3F4007ED76FBA850618F06F015E8FC83C2C07E92C3831F28C1FD975068044D04FA5B47A6C80C39A46C232FAD34E3684FA5A5A5CD236F1F4A9310994635CCC2A23A78805B056A5300FF009D4F062D24FB0C0778B1366012B254016E32990602D04985CEDAA17A5B70DF1B7C79821674869B842574DB7F81872A25E045F7153A790E1E1758A3D0D93E2D00F76212FB697C0183870A8DA3684DE1C56514E9D1CBA10107B4085E34D68280C163769B141C1DBD309A786929D432780F619256FAABF6168ACFE5C0E345EB3DBC00BF4C2B36EF874F68C378C72B82B75AAD43C9AE159C4A8E484B9C78DECA037811861AD93881BC38186E2AA8D00E116F2C789FF104DEA72928D932BE3B26F6A4F8BC9B47F002BD008498BFC0432A53F045875C6445DB5B11AF438E97235398290E74FAE2C11319E616D8BA5912115EE619BC4005E2D9CF5590D41AA9A6F0E2F57D68C0450FA0C1F8F05986B15A6A2B78561E01B34F02B8D195AFC3CD2605C674C9EC275146264204709A6C792A25226518AE7B592576450278C1B1E791000AE518E0F86648F047853AE5C5729748A13E09A05686E9B174F7841F4C8894D907EE339EE5EC938C7A6CF0A2FE791102BEBE3BB5EE2D9949EAE12DCB8AE7D722007C1D1569C4B3FAD10BE37B3C9F6DB7DB1345D15F14CBAC6E889C5E39300D97CE6A8D2F67016A9111142FF490CA72729B9F9C48716CD2F09D7298A0698AA9302A6C60862F45C2B246C517002EA28C744085548BC532003FBAC01160AA02D5824A29130D9AFB8F6D0773619DCF10CAD2E1DCE874600C8D68A9BE705528916FA74228804E27C74C491BA6352F746444C74B4045E8AC02ED6B7FD35E42E5B6992246A1CE7F97964F9A2DDC1753124528A25281258BCAE76827510E12CFF59E24AB262F2A4593AA2188180DB089341B142F4073469ACDE6617ABD3E2EA8DC7BCFD573755F06832196340C9A3591D66E98C57298C8D6AE8D0BAF179DEF0198C594592230A03D007604748E9212E4F0845D66AB42167723B70250A36A04019DE75900875488E31BB9150066AF4C8D1801ABD0082E0580C1DF5343005E3254DC6840934A51E26AEEC053F0A456680D3FB097C725D0AD66428347FBCF54B9ACCD9DFDEF545100EF788C01AEA8B955E66EB3E5E32469C044C7F578527F93DA0913DA7AF3A4FED96A0B206CA7C283D4805206677B9278C11FC3A29680B8622C17E885F3469D0C4C208117F52F642100AA63F010FFF7517BF9F3649C75D2A36D1AF5FE8B18D50C1A7851FFDB8CCA5F559A078F40259D61FDEF2C0FA16F25C3FA7EBED6673F83F101E74C2D273EE285FF00331300937F81A9E8F98B19D7FF9B349DF7A39A3D63011CD5B2F9D31FA62B581C9F174E7EBCA2E3F92C06FE07D4147657975EC1240000000049454E44AE426082")));
		this.dimension = toolkit.getScreenSize();
		// this.updates = updateVoList;
		setBounds((this.dimension.width - 290) / 2, (this.dimension.height - 130) / 2, 290, 120);
		
		setResizable(false);
		setLayout((LayoutManager)null);
		// 標題欄
		this.titlePanel.setBounds(0, 0, 290, 30);
		this.titlePanel.setBackground(new Color(110, 110, 110));
		this.titlePanel.setBorder(new MatteBorder(0, 0, 0, 0, new Color(110, 110, 110)));
		add(this.titlePanel);
		this.bodyPanel.setBounds(0, 0, 290, 120);
		this.bodyPanel.setBackground(new Color(255, 255, 255));
		this.bodyPanel.setBorder(new MatteBorder(0, 1, 1, 1, new Color(110, 110, 110)));
		add(this.bodyPanel);
		this.title_lable.setBounds(10, 0, 150, 30);
		this.title_lable.setFont(new Font("Microsoft Yahei", 1, 13));
		this.title_lable.setForeground(new Color(255, 255, 255));
		add(this.title_lable, 0);
		/*
		if (this.updates.size() != 0 && !Config.getProperty("SLanguage").equals("CHS")) {
			this.bootButton.setBounds(200, 0, 25, 30);
			this.bootButton.setFont(new Font("Microsoft Yahei", 1, 12));
			this.bootButton.setForeground(new Color(255, 255, 255));
			this.bootButton.setMargin(new Insets(0, 0, 0, 0));
			this.bootButton.setBorder((Border)null);
			this.bootButton.setOpaque(false);
			this.bootButton.setIconTextGap(0);
			this.bootButton.setContentAreaFilled(false);
			this.bootButton.setFocusable(false);
			this.bootButton.addActionListener(this);
			add(this.bootButton, 0);
		} 
		*/
		// 設置按鈕
		this.configButton.setBounds(230, 0, 25, 30);
		this.configButton.setFont(new Font("Microsoft Yahei", 1, 12));
		this.configButton.setForeground(new Color(255, 255, 255));
		this.configButton.setMargin(new Insets(0, 0, 0, 0));
		this.configButton.setBorder((Border)null);
		this.configButton.setOpaque(false);
		this.configButton.setIconTextGap(0);
		this.configButton.setContentAreaFilled(false);
		this.configButton.setFocusable(false);
		this.configButton.addActionListener(this);
		add(this.configButton, 0);
		// 最小化及關閉
		this.closeButton.setBounds(260, 0, 20, 30);
		this.closeButton.setFont(new Font("Microsoft Yahei", 1, 12));
		this.closeButton.setForeground(new Color(255, 255, 255));
		this.closeButton.setMargin(new Insets(0, 0, 0, 0));
		this.closeButton.setBorder((Border)null);
		this.closeButton.setOpaque(false);
		this.closeButton.setIconTextGap(0);
		this.closeButton.setContentAreaFilled(false);
		this.closeButton.setFocusable(false);
		this.closeButton.addActionListener(this);
		add(this.closeButton, 0);
		// 拖曳功能
		addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						TextPatchPanel.origin.x = e.getX();
						TextPatchPanel.origin.y = e.getY();
					}
				});
		addMouseMotionListener(new MouseMotionAdapter() {
					public void mouseDragged(MouseEvent e) {
						Point p = TextPatchPanel.this.getLocation();
						TextPatchPanel.this.setLocation(p.x + e.getX() - TextPatchPanel.origin.x, p.y + e.getY() - TextPatchPanel.origin.y);
					}
				});
		// 主要面板
		this.replaceButton.setBounds(30, 40, 80, 60);
		this.replaceButton.setFont(new Font("Microsoft Yahei", 0, 25));
		this.replaceButton.setForeground(new Color(110, 110, 110));
		this.replaceButton.setMargin(new Insets(0, 0, 0, 0));
		this.replaceButton.setOpaque(false);
		this.replaceButton.setIconTextGap(0);
		this.replaceButton.setContentAreaFilled(false);
		this.replaceButton.setFocusable(false);
		this.replaceButton.addActionListener(this);
		this.replaceButton.setEnabled(true);
		add(this.replaceButton, 0);
		
		this.rollbackButton.setBounds(180, 40, 80, 60);
		this.rollbackButton.setFont(new Font("Microsoft Yahei", 0, 25));
		this.rollbackButton.setForeground(new Color(110, 110, 110));
		this.rollbackButton.setMargin(new Insets(0, 0, 0, 0));
		this.rollbackButton.setOpaque(false);
		this.rollbackButton.setIconTextGap(0);
		this.rollbackButton.setContentAreaFilled(false);
		this.rollbackButton.setFocusable(false);
		this.rollbackButton.addActionListener(this);
		this.rollbackButton.setEnabled(true);
		add(this.rollbackButton, 0);
		
		// this.menuText.setBounds(30, 80, 230, 60);
		// this.menuText.setFont(new Font("Callibri", 0, 12));
		// add(this.menuText, 0);
		
		setVisible(false);
		setVisible(true);
		
		/*
		try {
			String path = Config.getProperty("GamePath");
			if (isFFXIVFloder(path)) {
				String resourceFolder = path + File.separator + "game" + File.separator + "sqpack" + File.separator + "ffxiv";
				String pathToIndex = resourceFolder + File.separator + "0a0000.win32.index";
				EXDFUtil exdfUtil = new EXDFUtil(pathToIndex);
				if (exdfUtil.isTransDat()) {
					this.replaceButton.setEnabled(false);
					List<TeemoUpdateVo> diffFiles = UpdateUtil.diffChnFile(this.updates);
					if (diffFiles.size() > 0 && 
						JOptionPane.showConfirmDialog(null, "检测到新汉化版本，是否使用？", "提示", 0) == 0) {
						RollbackThread rollbackThread = new RollbackThread(resourceFolder, this, this.updates, false);
						Thread rollbackFileThread = new Thread((Runnable)rollbackThread);
						rollbackFileThread.run();
						ReplaceThread replaceThread = new ReplaceThread(resourceFolder, this, this.updates);
						Thread replaceFileThread = new Thread((Runnable)replaceThread);
						replaceFileThread.start();
					} 
					
				} 
			} 
		} catch (Exception exception) {}
		*/
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Logger log = Logger.getLogger("GPLogger");
		if (e.getSource() == this.replaceButton) {
			String path = Config.getProperty("GamePath");
			String slang = Config.getProperty("SLanguage");
			String dlang = Config.getProperty("DLanguage");
			// added
			String flang = Config.getProperty("FLanguage");
			
			System.out.println("遊戲路徑：" + path);
			System.out.println("原始語言：" + slang);
			System.out.println("目標語言：" + dlang);
			//added
			System.out.println("檔案語言：" + flang);
			log.config("遊戲路徑：" + path);
			log.config("原始語言：" + slang);
			log.config("目標語言：" + dlang);
			log.config("檔案語言：" + flang);
			
			if (isFFXIVFloder(path)) {
				// 備份原檔案
				String resourceFolder = path + File.separator + "game" + File.separator + "sqpack" + File.separator + "ffxiv";
				String[] resourceNames = { "000000.win32.dat0", "000000.win32.index", "000000.win32.index2", "0a0000.win32.dat0", "0a0000.win32.index", "0a0000.win32.index2" };
				for (String resourceName : resourceNames) {
					File resourceFile = new File(resourceFolder + File.separator + resourceName);
					if (resourceFile.exists() && resourceFile.isFile())
						FileUtil.copyTo(resourceFile, "backup" + File.separator + resourceFile.getName()); 
				} 
				ReplaceThread replaceThread = new ReplaceThread(resourceFolder, this/*, this.updates*/);
				Thread replaceFileThread = new Thread((Runnable)replaceThread);
				replaceFileThread.start();
			} else {
				JOptionPane.showMessageDialog(null, "<html><body>請選擇正確的遊戲根目錄<br />目錄預設名為：<br />FINAL FANTASY XIV ONLINE</body></html>", "路徑錯誤", 0);
				log.severe("Game path error!");
				dispose();
				new ConfigApplicationPanel();
			} 
		} 
		if (e.getSource() == this.rollbackButton) {
			String path = Config.getProperty("GamePath");
			if (isFFXIVFloder(path)) {
				// 還原備份的檔案
				String resourceFolder = path + File.separator + "game" + File.separator + "sqpack" + File.separator + "ffxiv";
				RollbackThread rollbackThread = new RollbackThread(resourceFolder, this,/*this.updates,*/ true);
				Thread rollbackFileThread = new Thread((Runnable)rollbackThread);
				rollbackFileThread.start();
			} else {
				JOptionPane.showMessageDialog(null, "<html><body>請選擇正確的遊戲根目錄<br />目錄預設名為：<br />FINAL FANTASY XIV ONLINE</body></html>", "路徑錯誤", 0);
				log.severe("Game path error!");
				dispose();
				new ConfigApplicationPanel();
			} 
		} 
		if (e.getSource() == this.bootButton) {
			dispose();
			new ClientLauncherPanel();
		} 
		if (e.getSource() == this.configButton) {
			dispose();
			new ConfigApplicationPanel();
		} 
		if (e.getSource() == this.closeButton)
			System.exit(0); 
	}
	
	private boolean isFFXIVFloder(String path) {
		if (path == null)
			return false; 
		if ((new File(path + File.separator + "game" + File.separator + "ffxiv.exe")).exists())
			return true; 
		return false;
	}
	
	public static void main(String[] args) throws Exception {
		new TextPatchPanel();
	}
}
