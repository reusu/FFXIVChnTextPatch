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
// import java.util.ArrayList;
// import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import name.yumao.ffxiv.chn.model.Language;
// import name.yumao.ffxiv.chn.model.TeemoUpdateVo;
import name.yumao.ffxiv.chn.util.HexUtils;
import name.yumao.ffxiv.chn.util.res.Config;

public class ConfigApplicationPanel extends JFrame implements ActionListener {
	
	// private List<TeemoUpdateVo> updates;
	private static Point origin = new Point();
	private static String title = "漢化設置";
	private JLabel title_lable = new JLabel(title);
	private Dimension dimension;
	private JButton closeButton = new JButton("x");
	private JPanel titlePanel = new JPanel();
	private JPanel bodyPanel = new JPanel();
	
	private JLabel pathLable = new JLabel("遊戲路徑");
	private JTextField pathField = new JTextField();
	private JButton pathButton = new JButton(".");
	
	private JLabel sLangLable = new JLabel("原始語言");
	private JComboBox<String> sLangLableVal;
	
	private JLabel dLangLable = new JLabel("目標語言");
	private JComboBox<String> dLangLableVal;
	
	// added
	private JLabel fLangLable = new JLabel("檔案語言");
	private JComboBox<String> fLangLableVal;
	
	/*
	private JLabel transModeLable = new JLabel("资源版本");
	private JComboBox<String> transModeVal;
	*/
	
	private JButton configButton = new JButton("確認");
	private JButton exitButton = new JButton("退出");
	
	public ConfigApplicationPanel(/*List<TeemoUpdateVo> updateVoList*/) {
		super(title);
		setUndecorated(true);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		setIconImage(toolkit.createImage(HexUtils.hexStringToBytes("89504E470D0A1A0A0000000D4948445200000040000000400806000000AA6971DE000004EC4944415478DAED5B69485451141EB50DB3C56AA821A329C7596CD459A2FA57467B513F0209F147CB0F2B82C20ACA928ACA3F2544605909051651D962641B914542942456602B6DE492AD56A29665DF9137F27ACD7BB3F4EECCBB830F0EF7CD9B33EFDEEFDC73CE3DF79C3B3A5D082EB7DB3D283535D5EE703816B85CAE5CA7D3791C7407F74FF1DD1BB4F5681BD0D6815E821EE1FB4AB487D1AE453B2B2525C58A77F4D7F174198DC67E003D07200A00A20AD40EA09D81127EF71BEF20C194A05D6AB3D90C5AC6DD0B839C8FC19E007D0F06B01F02F905BA857EB2939393E334811A031982016DC6C0DEB2002D47E8F32BFA2C84798C090B70D8663C06918F417C535261B27750332B41E0DD3F4007ED76FBA850618F06F015E8FC83C2C07E92C3831F28C1FD975068044D04FA5B47A6C80C39A46C232FAD34E3684FA5A5A5CD236F1F4A9310994635CCC2A23A78805B056A5300FF009D4F062D24FB0C0778B1366012B254016E32990602D04985CEDAA17A5B70DF1B7C79821674869B842574DB7F81872A25E045F7153A790E1E1758A3D0D93E2D00F76212FB697C0183870A8DA3684DE1C56514E9D1CBA10107B4085E34D68280C163769B141C1DBD309A786929D432780F619256FAABF6168ACFE5C0E345EB3DBC00BF4C2B36EF874F68C378C72B82B75AAD43C9AE159C4A8E484B9C78DECA037811861AD93881BC38186E2AA8D00E116F2C789FF104DEA72928D932BE3B26F6A4F8BC9B47F002BD008498BFC0432A53F045875C6445DB5B11AF438E97235398290E74FAE2C11319E616D8BA5912115EE619BC4005E2D9CF5590D41AA9A6F0E2F57D68C0450FA0C1F8F05986B15A6A2B78561E01B34F02B8D195AFC3CD2605C674C9EC275146264204709A6C792A25226518AE7B592576450278C1B1E791000AE518E0F86648F047853AE5C5729748A13E09A05686E9B174F7841F4C8894D907EE339EE5EC938C7A6CF0A2FE791102BEBE3BB5EE2D9949EAE12DCB8AE7D722007C1D1569C4B3FAD10BE37B3C9F6DB7DB1345D15F14CBAC6E889C5E39300D97CE6A8D2F67016A9111142FF490CA72729B9F9C48716CD2F09D7298A0698AA9302A6C60862F45C2B246C517002EA28C744085548BC532003FBAC01160AA02D5824A29130D9AFB8F6D0773619DCF10CAD2E1DCE874600C8D68A9BE705528916FA74228804E27C74C491BA6352F746444C74B4045E8AC02ED6B7FD35E42E5B6992246A1CE7F97964F9A2DDC1753124528A25281258BCAE76827510E12CFF59E24AB262F2A4593AA2188180DB089341B142F4073469ACDE6617ABD3E2EA8DC7BCFD573755F06832196340C9A3591D66E98C57298C8D6AE8D0BAF179DEF0198C594592230A03D007604748E9212E4F0845D66AB42167723B70250A36A04019DE75900875488E31BB9150066AF4C8D1801ABD0082E0580C1DF5343005E3254DC6840934A51E26AEEC053F0A456680D3FB097C725D0AD66428347FBCF54B9ACCD9DFDEF545100EF788C01AEA8B955E66EB3E5E32469C044C7F578527F93DA0913DA7AF3A4FED96A0B206CA7C283D4805206677B9278C11FC3A29680B8622C17E885F3469D0C4C208117F52F642100AA63F010FFF7517BF9F3649C75D2A36D1AF5FE8B18D50C1A7851FFDB8CCA5F559A078F40259D61FDEF2C0FA16F25C3FA7EBED6673F83F101E74C2D273EE285FF00331300937F81A9E8F98B19D7FF9B349DF7A39A3D63011CD5B2F9D31FA62B581C9F174E7EBCA2E3F92C06FE07D4147657975EC1240000000049454E44AE426082")));
		this.dimension = toolkit.getScreenSize();
		// this.updates = updateVoList;
		
		// edited 外框尺寸
		/* 
		if (this.updates.size() == 0) {
			setBounds((this.dimension.width - 290) / 2, (this.dimension.height - 190) / 2, 290, 190);
		} else {
			setBounds((this.dimension.width - 290) / 2, (this.dimension.height - 220) / 2, 290, 220);
		} 
		*/
		setBounds((this.dimension.width - 290) / 2, (this.dimension.height - 190) / 2, 290, 190);
		setResizable(false);
		setLayout((LayoutManager)null);
		// 標題欄
		this.titlePanel.setBounds(0, 0, 290, 30);
		this.titlePanel.setBackground(new Color(110, 110, 110));
		this.titlePanel.setBorder(new MatteBorder(0, 0, 0, 0, new Color(110, 110, 110)));
		add(this.titlePanel);
		// edited 外框尺寸
		/*
		if (this.updates.size() == 0) {
			this.bodyPanel.setBounds(0, 0, 290, 190);
		} else {
			this.bodyPanel.setBounds(0, 0, 290, 220);
		} 
		*/
		this.bodyPanel.setBounds(0, 0, 290, 190);
		this.bodyPanel.setBackground(new Color(255, 255, 255));
		this.bodyPanel.setBorder(new MatteBorder(0, 1, 1, 1, new Color(110, 110, 110)));
		add(this.bodyPanel);
		this.title_lable.setBounds(10, 0, 150, 30);
		this.title_lable.setFont(new Font("Microsoft Yahei", 1, 13));
		this.title_lable.setForeground(new Color(255, 255, 255));
		add(this.title_lable, 0);
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
						ConfigApplicationPanel.origin.x = e.getX();
						ConfigApplicationPanel.origin.y = e.getY();
					}
				});
		addMouseMotionListener(new MouseMotionAdapter() {
					public void mouseDragged(MouseEvent e) {
						Point p = ConfigApplicationPanel.this.getLocation();
						ConfigApplicationPanel.this.setLocation(p.x + e.getX() - ConfigApplicationPanel.origin.x, p.y + e.getY() - ConfigApplicationPanel.origin.y);
					}
				});
		// 主要面板
		this.pathLable.setBounds(30, 40, 100, 25);
		this.pathLable.setFont(new Font("Microsoft Yahei", 1, 13));
		this.pathLable.setForeground(new Color(110, 110, 110));
		add(this.pathLable, 0);
		this.pathField.setBounds(100, 40, 150, 25);
		this.pathField.setFont(new Font("Microsoft Yahei", 1, 13));
		this.pathField.setForeground(new Color(110, 110, 110));
		this.pathField.setEditable(false);
		add(this.pathField, 0);
		this.pathButton.setBounds(250, 40, 10, 25);
		this.pathButton.setFont(new Font("Microsoft Yahei", 1, 13));
		this.pathButton.setForeground(new Color(110, 110, 110));
		this.pathButton.setMargin(new Insets(0, 0, 0, 0));
		this.pathButton.setOpaque(false);
		this.pathButton.setIconTextGap(0);
		this.pathButton.setContentAreaFilled(false);
		this.pathButton.setFocusable(false);
		this.pathButton.addActionListener(this);
		add(this.pathButton, 0);
		
		// added
		// 因為新增了這一段，以下各物件的setBounds的第二項都要增加30（往下移）。
		// 為了因應CSV，新增CSV選項。
		this.fLangLable.setBounds(30, 70, 100, 25);
		this.fLangLable.setFont(new Font("Microsoft Yahei", 1, 13));
		this.fLangLable.setForeground(new Color(110, 110, 110));
		add(this.fLangLable, 0);
		this.fLangLableVal = new JComboBox<>();
		this.fLangLableVal.addItem("CSV");
		this.fLangLableVal.addItem("日文");
		this.fLangLableVal.addItem("英文");
		this.fLangLableVal.addItem("德文");
		this.fLangLableVal.addItem("法文");
		this.fLangLableVal.addItem("簡體中文");
		this.fLangLableVal.setBounds(100, 70, 160, 23);
		this.fLangLableVal.setFont(new Font("Microsoft Yahei", 1, 13));
		this.fLangLableVal.setForeground(new Color(110, 110, 110));
		this.fLangLableVal.setOpaque(false);
		this.fLangLableVal.setFocusable(false);
		this.fLangLableVal.addActionListener(this);
		add(this.fLangLableVal, 0);
		
		this.sLangLable.setBounds(30, 100, 100, 25);
		this.sLangLable.setFont(new Font("Microsoft Yahei", 1, 13));
		this.sLangLable.setForeground(new Color(110, 110, 110));
		add(this.sLangLable, 0);
		this.sLangLableVal = new JComboBox<>();
		this.sLangLableVal.addItem("日文");
		this.sLangLableVal.addItem("英文");
		this.sLangLableVal.addItem("德文");
		this.sLangLableVal.addItem("法文");
		this.sLangLableVal.addItem("簡體中文");
		this.sLangLableVal.setBounds(100, 100, 160, 23);
		this.sLangLableVal.setFont(new Font("Microsoft Yahei", 1, 13));
		this.sLangLableVal.setForeground(new Color(110, 110, 110));
		this.sLangLableVal.setOpaque(false);
		this.sLangLableVal.setFocusable(false);
		add(this.sLangLableVal, 0);

		this.dLangLable.setBounds(30, 130, 100, 25);
		this.dLangLable.setFont(new Font("Microsoft Yahei", 1, 13));
		this.dLangLable.setForeground(new Color(110, 110, 110));
		add(this.dLangLable, 0);
		this.dLangLableVal = new JComboBox<>();
		this.dLangLableVal.addItem("簡體中文");
		this.dLangLableVal.addItem("正體中文");
		this.dLangLableVal.setBounds(100, 130, 160, 23);
		this.dLangLableVal.setFont(new Font("Microsoft Yahei", 1, 13));
		this.dLangLableVal.setForeground(new Color(110, 110, 110));
		this.dLangLableVal.setOpaque(false);
		this.dLangLableVal.setFocusable(false);
		add(this.dLangLableVal, 0);
		
		this.dLangLable.setVisible(false);
		this.dLangLableVal.setVisible(false);
		
		/*
		if (this.updates.size() == 0) {
			this.configButton.setBounds(30, 160, 80, 20);
			this.configButton.setFont(new Font("Microsoft Yahei", 1, 12));
			this.configButton.setForeground(new Color(110, 110, 110));
			this.configButton.setMargin(new Insets(0, 0, 0, 0));
			this.configButton.setOpaque(false);
			this.configButton.setIconTextGap(0);
			this.configButton.setContentAreaFilled(false);
			this.configButton.setFocusable(false);
			this.configButton.addActionListener(this);
			add(this.configButton, 0);
			this.exitButton.setBounds(180, 160, 80, 20);
			this.exitButton.setFont(new Font("Microsoft Yahei", 1, 12));
			this.exitButton.setForeground(new Color(110, 110, 110));
			this.exitButton.setMargin(new Insets(0, 0, 0, 0));
			this.exitButton.setOpaque(false);
			this.exitButton.setIconTextGap(0);
			this.exitButton.setContentAreaFilled(false);
			this.exitButton.setFocusable(false);
			this.exitButton.addActionListener(this);
			add(this.exitButton, 0);
		} else {
			this.transModeLable.setBounds(30, 160, 100, 25);
			this.transModeLable.setFont(new Font("Microsoft Yahei", 1, 13));
			this.transModeLable.setForeground(new Color(110, 110, 110));
			add(this.transModeLable, 0);
			this.transModeVal = new JComboBox<>();
			this.transModeVal.addItem("官方版");
			this.transModeVal.addItem("提莫版");
			this.transModeVal.addItem("暖暖版");
			this.transModeVal.addItem("暖提版");
			this.transModeVal.setBounds(100, 160, 160, 23);
			this.transModeVal.setFont(new Font("Microsoft Yahei", 1, 13));
			this.transModeVal.setForeground(new Color(110, 110, 110));
			this.transModeVal.setOpaque(false);
			this.transModeVal.setFocusable(false);
			add(this.transModeVal, 0);
			this.configButton.setBounds(30, 190, 80, 20);
			this.configButton.setFont(new Font("Microsoft Yahei", 1, 12));
			this.configButton.setForeground(new Color(110, 110, 110));
			this.configButton.setMargin(new Insets(0, 0, 0, 0));
			this.configButton.setOpaque(false);
			this.configButton.setIconTextGap(0);
			this.configButton.setContentAreaFilled(false);
			this.configButton.setFocusable(false);
			this.configButton.addActionListener(this);
			add(this.configButton, 0);
			this.exitButton.setBounds(180, 190, 80, 20);
			this.exitButton.setFont(new Font("Microsoft Yahei", 1, 12));
			this.exitButton.setForeground(new Color(110, 110, 110));
			this.exitButton.setMargin(new Insets(0, 0, 0, 0));
			this.exitButton.setOpaque(false);
			this.exitButton.setIconTextGap(0);
			this.exitButton.setContentAreaFilled(false);
			this.exitButton.setFocusable(false);
			this.exitButton.addActionListener(this);
			add(this.exitButton, 0);
		} 
		*/
		this.configButton.setBounds(30, 160, 80, 20);
		this.configButton.setFont(new Font("Microsoft Yahei", 1, 12));
		this.configButton.setForeground(new Color(110, 110, 110));
		this.configButton.setMargin(new Insets(0, 0, 0, 0));
		this.configButton.setOpaque(false);
		this.configButton.setIconTextGap(0);
		this.configButton.setContentAreaFilled(false);
		this.configButton.setFocusable(false);
		this.configButton.addActionListener(this);
		add(this.configButton, 0);
		this.exitButton.setBounds(180, 160, 80, 20);
		this.exitButton.setFont(new Font("Microsoft Yahei", 1, 12));
		this.exitButton.setForeground(new Color(110, 110, 110));
		this.exitButton.setMargin(new Insets(0, 0, 0, 0));
		this.exitButton.setOpaque(false);
		this.exitButton.setIconTextGap(0);
		this.exitButton.setContentAreaFilled(false);
		this.exitButton.setFocusable(false);
		this.exitButton.addActionListener(this);
		add(this.exitButton, 0);
		setVisible(false);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == this.pathButton) {
			JFileChooser pathChooser = new JFileChooser();
			pathChooser.setDialogTitle("請選擇遊戲根目錄");
			pathChooser.setFileSelectionMode(1);
			int returnVal = pathChooser.showOpenDialog(null);
			if (returnVal == 0) {
				String filePath = pathChooser.getSelectedFile().getPath();
				if (isFFXIVFloder(filePath)) {
					this.pathField.setText(filePath);
					this.pathField.setCaretPosition(0);
				} else {
					JOptionPane.showMessageDialog(null, "<html><body>請選擇正確的遊戲根目錄<br />目錄預設名為：<br />FINAL FANTASY XIV ONLINE</body></html>", "路徑錯誤", 0);
				} 
			} 
		} 
		if (e.getSource() == this.fLangLableVal) {
			String selected = (String)this.fLangLableVal.getSelectedItem();
			// System.out.println(selected);
			if (selected == "CSV") {
				this.dLangLable.setVisible(false);
				this.dLangLableVal.setVisible(false);
			} else {
				this.dLangLable.setVisible(true);
				this.dLangLableVal.setVisible(true);
			}
		}
		if (e.getSource() == this.configButton) {
			String path = this.pathField.getText();
			String srcLang = (String)this.sLangLableVal.getSelectedItem();
			String dstLang = (String)this.dLangLableVal.getSelectedItem();
			// added
			String filLang = (String)this.fLangLableVal.getSelectedItem();
			
			String transMode = "0";
			/*
			if (this.updates.size() != 0)
				transMode = String.valueOf(this.transModeVal.getSelectedIndex()); 
			*/
			if (isFFXIVFloder(path)) {
				Config.setProperty("GamePath", path);
				Config.setProperty("SLanguage", Language.toLang(srcLang));
				Config.setProperty("DLanguage", Language.toLang(dstLang));
				// added
				Config.setProperty("FLanguage", Language.toLang(filLang));
				
				Config.setProperty("TransMode", transMode);
				Config.saveProperty();
				dispose();
				new TextPatchPanel(/*this.updates*/);
			} else {
				JOptionPane.showMessageDialog(null, "<html><body>請選擇正確的遊戲根目錄<br />目錄預設名為：<br />FINAL FANTASY XIV ONLINE</body></html>", "路徑錯誤", 0);
			} 
		} 
		if (e.getSource() == this.closeButton || e.getSource() == this.exitButton)
			System.exit(0); 
	}
	
	private boolean isFFXIVFloder(String path) {
		if (path == null)
			return false; 
		return (new File(path + File.separator + "game" + File.separator + "ffxiv.exe")).exists();
	}
	
	public static void main(String[] args) {
		/*
		TeemoUpdateVo vo = new TeemoUpdateVo();
		List<TeemoUpdateVo> updates = new ArrayList<>();
		updates.add(vo);
		*/
		new ConfigApplicationPanel(/*updates*/);
	}
}
