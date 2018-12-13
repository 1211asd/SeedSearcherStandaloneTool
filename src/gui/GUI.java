package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import Util.Util;
import amidst.mojangapi.minecraftinterface.MinecraftInterfaceCreationException;
import amidst.mojangapi.world.biome.Biome;
import amidst.mojangapi.world.biome.UnknownBiomeIndexException;
import amidst.parsing.FormatException;
import main.BiomeSearcher;
import main.Main;

import javax.swing.JTextField;

public class GUI {

	private JFrame frmSeedTool;
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	/**
	 * Launch the application.
	 */

	
	public  JButton btnClear;
	public static JButton btnStart;
	public static JButton btnPause;
	public static JCheckBox excludeBiome;

	public static JPanel checkBoxes;
	public static JPanel checkBoxes1;

	public static String[] biomeSelected;

	ButtonListener listener = new ButtonListener();

	public static JLabel seedCount;
	public static JLabel totalSeedCount;
	public static JLabel timeElapsed;
	public static JTextArea console;


	public static JTextField widthSearch;
	public static JTextField heightSearch;
	public static JTextField maxSeeds;
	public static JTextField versionId;

	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnStart) {
				try {
					Main.toggleRunning();
				} catch (
						InterruptedException
						| IOException
						| FormatException
						| MinecraftInterfaceCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else if (e.getSource() == btnPause) {
				Main.togglePause();
			} else if (e.getSource() == btnClear) {

				try {
					Main.reset();
				} catch (
						InterruptedException
						| IOException
						| FormatException
						| MinecraftInterfaceCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			} else if(e.getSource() == excludeBiome) {
				if(excludeBiome.isSelected()) {
					tabbedPane.setEnabledAt(2, true);
				} else {
					tabbedPane.setEnabledAt(2, false);
				}
			}
		}
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	public void initialize() {
		frmSeedTool = new JFrame();
		frmSeedTool.setTitle("Seed Tool");
		frmSeedTool.setResizable(false);
		frmSeedTool.setBounds(100, 100, 644, 515);
		frmSeedTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSeedTool.setVisible(true);
		frmSeedTool.getContentPane().setLayout(null);

		JPanel panel_3 = new JPanel();
		panel_3.setBounds(0, 0, 238, 26);
		frmSeedTool.getContentPane().add(panel_3);
		panel_3.setLayout(new BorderLayout(0, 0));

		JLabel consoleLog = new JLabel("Console Output");
		consoleLog.setHorizontalAlignment(SwingConstants.CENTER);
		panel_3.add(consoleLog, BorderLayout.CENTER);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 25, 238, 461);
		frmSeedTool.getContentPane().add(scrollPane);

		console = new JTextArea();
		console.setLineWrap(true);
		scrollPane.setViewportView(console);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBounds(237, 0, 401, 486);
		frmSeedTool.getContentPane().add(tabbedPane);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Data", null, panel, null);
		panel.setLayout(null);

		seedCount = new JLabel("Rejected Seed Count: " + 0);
		seedCount.setBounds(10, 11, 212, 14);
		panel.add(seedCount);

		timeElapsed = new JLabel("Time Elapsed: 00:00:00");
		timeElapsed.setBounds(10, 331, 212, 14);
		panel.add(timeElapsed);

		totalSeedCount = new JLabel("Total Rejected Seed Count: 0");
		totalSeedCount.setBounds(10, 36, 212, 14);
		panel.add(totalSeedCount);

		btnStart = new JButton("Start");
		btnStart.addActionListener(listener);
		btnStart.setBounds(10, 356, 141, 23);
		panel.add(btnStart);

		btnPause = new JButton("Pause");
		btnPause.addActionListener(listener);
		btnPause.setBounds(10, 390, 141, 23);
		panel.add(btnPause);

		btnClear = new JButton("Clear");
		btnClear.addActionListener(listener);
		btnClear.setBounds(10, 424, 141, 23);
		panel.add(btnClear);
		
		JLabel versionIdLabel = new JLabel("Minecraft Version:");
		versionIdLabel.setBounds(174, 331, 141, 14);
		panel.add(versionIdLabel);
		
		versionId = new JTextField();
		versionId.setText(""+Main.minecraftVersionId);
		versionId.setBounds(300, 331, 86, 20);
		panel.add(versionId);
		versionId.setColumns(10);
		
		JLabel lblSearchSize = new JLabel("Search Size: (Width, Height) D: 2048");
		lblSearchSize.setBounds(174, 360, 212, 14);
		panel.add(lblSearchSize);
		
		widthSearch = new JTextField();
		widthSearch.setText(""+Main.searchQuadrantWidth);
		widthSearch.setBounds(170, 391, 86, 20);
		panel.add(widthSearch);
		widthSearch.setColumns(10);
		
		heightSearch = new JTextField();
		heightSearch.setText(""+Main.searchQuadrantHeight);
		heightSearch.setBounds(300, 391, 86, 20);
		panel.add(heightSearch);
		heightSearch.setColumns(10);
		
		JLabel maxSeedsLabel = new JLabel("Max # Seeds to Find:");
		maxSeedsLabel.setBounds(174, 428, 151, 14);
		panel.add(maxSeedsLabel);	
		
		maxSeeds = new JTextField();
		maxSeeds.setText(""+Main.maximumMatchingWorldsCount);
		maxSeeds.setBounds(300, 427, 86, 20);
		panel.add(maxSeeds);
		maxSeeds.setColumns(10);
		
		JLabel lblExcludeBiomes = new JLabel("Exclude biomes?");
		lblExcludeBiomes.setBounds(174, 306, 127, 14);
		panel.add(lblExcludeBiomes);
		
		excludeBiome = new JCheckBox("");
		excludeBiome.setBounds(331, 301, 21, 23);
		excludeBiome.addActionListener(listener);
		panel.add(excludeBiome);

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Selection", null, panel_1, null);
		panel_1.setLayout(null);

		JLabel lblNewLabel = new JLabel("Biome Selection");
		lblNewLabel.setBounds(0, 0, 396, 34);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblNewLabel);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setBounds(0, 33, 396, 427);
		panel_1.add(scrollPane_1);

		checkBoxes = new JPanel();
		scrollPane_1.setViewportView(checkBoxes);
		checkBoxes.setLayout(
				new FormLayout(
						new ColumnSpec[] {
								FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.DEFAULT_COLSPEC,
								FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.DEFAULT_COLSPEC, },
						new RowSpec[] {
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,}));

		JCheckBox chckbxNewCheckBox = new JCheckBox("Ocean (Always true)");
		checkBoxes.add(chckbxNewCheckBox, "2, 2");
		//chckbxNewCheckBox.setEnabled(false);

		JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Plains");
		checkBoxes.add(chckbxNewCheckBox_1, "4, 2");

		JCheckBox chckbxNewCheckBox_3 = new JCheckBox("Desert");
		checkBoxes.add(chckbxNewCheckBox_3, "2, 4");

		JCheckBox chckbxNewCheckBox_4 = new JCheckBox("Extreme Hills");
		checkBoxes.add(chckbxNewCheckBox_4, "4, 4");

		JCheckBox chckbxNewCheckBox_5 = new JCheckBox("Forest");
		checkBoxes.add(chckbxNewCheckBox_5, "2, 6");

		JCheckBox chckbxNewCheckBox_6 = new JCheckBox("Taiga");
		checkBoxes.add(chckbxNewCheckBox_6, "4, 6");

		JCheckBox chckbxNewCheckBox_7 = new JCheckBox("Swampland");
		checkBoxes.add(chckbxNewCheckBox_7, "2, 8");

		JCheckBox chckbxNewCheckBox_8 = new JCheckBox("River");
		checkBoxes.add(chckbxNewCheckBox_8, "4, 8");

		JCheckBox chckbxNewCheckBox_9 = new JCheckBox("Hell");
		checkBoxes.add(chckbxNewCheckBox_9, "2, 10");
		chckbxNewCheckBox_9.setEnabled(false);

		JCheckBox chckbxNewCheckBox_10 = new JCheckBox("The End");
		checkBoxes.add(chckbxNewCheckBox_10, "4, 10");
		chckbxNewCheckBox_10.setEnabled(false);

		JCheckBox chckbxNewCheckBox_11 = new JCheckBox("Frozen Ocean");
		checkBoxes.add(chckbxNewCheckBox_11, "2, 12");

		JCheckBox chckbxNewCheckBox_12 = new JCheckBox("Frozen River");
		checkBoxes.add(chckbxNewCheckBox_12, "4, 12");

		JCheckBox chckbxNewCheckBox_13 = new JCheckBox("Ice Plains");
		checkBoxes.add(chckbxNewCheckBox_13, "2, 14");

		JCheckBox chckbxNewCheckBox_14 = new JCheckBox("Ice Mountains");
		checkBoxes.add(chckbxNewCheckBox_14, "4, 14");

		JCheckBox chckbxNewCheckBox_15 = new JCheckBox("Mushroom Island");
		checkBoxes.add(chckbxNewCheckBox_15, "2, 16");

		JCheckBox chckbxNewCheckBox_16 = new JCheckBox("Mushroom Island Shore");
		checkBoxes.add(chckbxNewCheckBox_16, "4, 16");

		JCheckBox chckbxNewCheckBox_17 = new JCheckBox("Beach");
		checkBoxes.add(chckbxNewCheckBox_17, "2, 18");

		JCheckBox chckbxNewCheckBox_18 = new JCheckBox("Desert Hills");
		checkBoxes.add(chckbxNewCheckBox_18, "4, 18");

		JCheckBox chckbxNewCheckBox_19 = new JCheckBox("Forest Hills");
		checkBoxes.add(chckbxNewCheckBox_19, "2, 20");

		JCheckBox chckbxNewCheckBox_20 = new JCheckBox("Extreme Hills Edge");
		checkBoxes.add(chckbxNewCheckBox_20, "4, 20");

		JCheckBox chckbxNewCheckBox_21 = new JCheckBox("Jungle");
		checkBoxes.add(chckbxNewCheckBox_21, "2, 22");

		JCheckBox chckbxNewCheckBox_22 = new JCheckBox("Jungle Hills");
		checkBoxes.add(chckbxNewCheckBox_22, "4, 22");

		JCheckBox chckbxNewCheckBox_23 = new JCheckBox("Jungle Edge");
		checkBoxes.add(chckbxNewCheckBox_23, "2, 24");

		JCheckBox chckbxNewCheckBox_24 = new JCheckBox("Deep Ocean");
		checkBoxes.add(chckbxNewCheckBox_24, "4, 24");

		JCheckBox chckbxNewCheckBox_25 = new JCheckBox("Stone Beach");
		checkBoxes.add(chckbxNewCheckBox_25, "2, 26");

		JCheckBox chckbxNewCheckBox_26 = new JCheckBox("Cold Beach");
		checkBoxes.add(chckbxNewCheckBox_26, "4, 26");

		JCheckBox chckbxNewCheckBox_27 = new JCheckBox("Birch Forest");
		checkBoxes.add(chckbxNewCheckBox_27, "2, 28");

		JCheckBox chckbxNewCheckBox_28 = new JCheckBox("Birch Forest Hills");
		checkBoxes.add(chckbxNewCheckBox_28, "4, 28");

		JCheckBox chckbxNewCheckBox_29 = new JCheckBox("Roofed Forest");
		checkBoxes.add(chckbxNewCheckBox_29, "2, 30");

		JCheckBox chckbxNewCheckBox_30 = new JCheckBox("Cold Taiga");
		checkBoxes.add(chckbxNewCheckBox_30, "4, 30");

		JCheckBox chckbxNewCheckBox_31 = new JCheckBox("Cold Taiga Hills");
		checkBoxes.add(chckbxNewCheckBox_31, "2, 30");

		JCheckBox chckbxNewCheckBox_2 = new JCheckBox("Mega Taiga");
		checkBoxes.add(chckbxNewCheckBox_2, "2, 32");

		JCheckBox chckbxNewCheckBox_32 = new JCheckBox("Mega Taiga Hills");
		checkBoxes.add(chckbxNewCheckBox_32, "4, 32");

		JCheckBox chckbxNewCheckBox_33 = new JCheckBox("Extreme Hills+");
		checkBoxes.add(chckbxNewCheckBox_33, "2, 34");

		JCheckBox chckbxNewCheckBox_34 = new JCheckBox("Savanna");
		checkBoxes.add(chckbxNewCheckBox_34, "4, 34");

		JCheckBox chckbxNewCheckBox_35 = new JCheckBox("Savanna Plateau");
		checkBoxes.add(chckbxNewCheckBox_35, "2, 36");

		JCheckBox chckbxNewCheckBox_36 = new JCheckBox("Mesa");
		checkBoxes.add(chckbxNewCheckBox_36, "4, 36");

		JCheckBox chckbxNewCheckBox_37 = new JCheckBox("Mesa Plateau F");
		checkBoxes.add(chckbxNewCheckBox_37, "2, 38");

		JCheckBox chckbxNewCheckBox_38 = new JCheckBox("Mesa Plateau");
		checkBoxes.add(chckbxNewCheckBox_38, "4, 38");

		JCheckBox chckbxNewCheckBox_39 = new JCheckBox("The End - Floating islands");
		checkBoxes.add(chckbxNewCheckBox_39, "2, 40");
		chckbxNewCheckBox_39.setEnabled(false);

		JCheckBox chckbxNewCheckBox_40 = new JCheckBox("The End - Medium island");
		checkBoxes.add(chckbxNewCheckBox_40, "4, 40");
		chckbxNewCheckBox_40.setEnabled(false);

		JCheckBox chckbxNewCheckBox_41 = new JCheckBox("The End - High island");
		checkBoxes.add(chckbxNewCheckBox_41, "2, 42");
		chckbxNewCheckBox_41.setEnabled(false);

		JCheckBox chckbxNewCheckBox_42 = new JCheckBox("The End - Barren island");
		checkBoxes.add(chckbxNewCheckBox_42, "4, 42");
		chckbxNewCheckBox_42.setEnabled(false);

		JCheckBox chckbxNewCheckBox_43 = new JCheckBox("Warm Ocean");
		checkBoxes.add(chckbxNewCheckBox_43, "2, 44");

		JCheckBox chckbxNewCheckBox_44 = new JCheckBox("Lukewarm Ocean");
		checkBoxes.add(chckbxNewCheckBox_44, "4, 44");

		JCheckBox chckbxNewCheckBox_45 = new JCheckBox("Cold Ocean");
		checkBoxes.add(chckbxNewCheckBox_45, "2, 46");

		JCheckBox chckbxNewCheckBox_46 = new JCheckBox("Warm Deep Ocean");
		checkBoxes.add(chckbxNewCheckBox_46, "4, 46");
		chckbxNewCheckBox_46.setEnabled(false);

		JCheckBox chckbxNewCheckBox_47 = new JCheckBox("Lukewarm Deep Ocean");
		checkBoxes.add(chckbxNewCheckBox_47, "2, 48");

		JCheckBox chckbxNewCheckBox_48 = new JCheckBox("Cold Deep Ocean");
		checkBoxes.add(chckbxNewCheckBox_48, "4, 48");
		
		JCheckBox chckbxNewCheckBox_49 = new JCheckBox("Frozen Deep Ocean");
		checkBoxes.add(chckbxNewCheckBox_49, "2, 50");

		JCheckBox chckbxNewCheckBox_50 = new JCheckBox("The Void");
		checkBoxes.add(chckbxNewCheckBox_50, "4, 50");
		chckbxNewCheckBox_50.setEnabled(false);
		
		JCheckBox chckbxNewCheckBox_51 = new JCheckBox("Sunflower Plains");
		checkBoxes.add(chckbxNewCheckBox_51, "2, 52");
		
		JCheckBox chckbxNewCheckBox_52 = new JCheckBox("Desert M");
		checkBoxes.add(chckbxNewCheckBox_52, "4, 52");
		
		JCheckBox chckbxNewCheckBox_53 = new JCheckBox("Extreme Hills M");
		checkBoxes.add(chckbxNewCheckBox_53, "2, 54");
		
		JCheckBox chckbxNewCheckBox_54 = new JCheckBox("Flower Forest");
		checkBoxes.add(chckbxNewCheckBox_54, "4, 54");
		
		JCheckBox chckbxNewCheckBox_55 = new JCheckBox("Taiga M");
		checkBoxes.add(chckbxNewCheckBox_55, "2, 56");
		
		JCheckBox chckbxNewCheckBox_56 = new JCheckBox("Swampland M");
		checkBoxes.add(chckbxNewCheckBox_56, "4, 56");
		
		JCheckBox chckbxNewCheckBox_57 = new JCheckBox("Ice Plains Spikes");
		checkBoxes.add(chckbxNewCheckBox_57, "2, 58");
		
		JCheckBox chckbxNewCheckBox_58 = new JCheckBox("Jungle M");
		checkBoxes.add(chckbxNewCheckBox_58, "4, 58");
		
		JCheckBox chckbxNewCheckBox_59 = new JCheckBox("Jungle Edge M");
		checkBoxes.add(chckbxNewCheckBox_59, "2, 60");
		
		JCheckBox chckbxNewCheckBox_60 = new JCheckBox("Birch Forest M");
		checkBoxes.add(chckbxNewCheckBox_60, "4, 60");
		
		JCheckBox chckbxNewCheckBox_61 = new JCheckBox("Birch Forest Hills M");
		checkBoxes.add(chckbxNewCheckBox_61, "2, 62");
		
		JCheckBox chckbxNewCheckBox_62 = new JCheckBox("Roofed Forest M");
		checkBoxes.add(chckbxNewCheckBox_62, "4, 62");
		
		JCheckBox chckbxNewCheckBox_63 = new JCheckBox("Cold Taiga M");
		checkBoxes.add(chckbxNewCheckBox_63, "2, 64");
		
		JCheckBox chckbxNewCheckBox_64 = new JCheckBox("Mega Spruce Taiga");
		checkBoxes.add(chckbxNewCheckBox_64, "4, 64");
		
		JCheckBox chckbxNewCheckBox_65 = new JCheckBox("Mega Spruce Taiga (Hills)");
		checkBoxes.add(chckbxNewCheckBox_65, "2, 66");
		
		JCheckBox chckbxNewCheckBox_66 = new JCheckBox("Extreme Hills+ M");
		checkBoxes.add(chckbxNewCheckBox_66, "4, 66");
		
		JCheckBox chckbxNewCheckBox_67 = new JCheckBox("Savanna M");
		checkBoxes.add(chckbxNewCheckBox_67, "2, 68");
		
		JCheckBox chckbxNewCheckBox_68 = new JCheckBox("Savanna Plateau M");
		checkBoxes.add(chckbxNewCheckBox_68, "4, 68");
		
		JCheckBox chckbxNewCheckBox_69 = new JCheckBox("Mesa (Bryce)");
		checkBoxes.add(chckbxNewCheckBox_69, "2, 70");
		
		JCheckBox chckbxNewCheckBox_70 = new JCheckBox("Mesa Plateau F M");
		checkBoxes.add(chckbxNewCheckBox_70, "4, 70");
		
		JCheckBox chckbxNewCheckBox_71 = new JCheckBox("Mesa Plateau M");
		checkBoxes.add(chckbxNewCheckBox_71, "2, 72");
		

		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Exclusion", null, panel_2, null);
		panel_2.setLayout(null);
		tabbedPane.setEnabledAt(2, false);
		JLabel lblNewLabel1 = new JLabel("Biome Exclusion");
		lblNewLabel1.setBounds(0, 0, 396, 34);
		lblNewLabel1.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblNewLabel1);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_2.setBounds(0, 33, 396, 427);
		panel_2.add(scrollPane_2);

		checkBoxes1 = new JPanel();
		scrollPane_2.setViewportView(checkBoxes1);
		checkBoxes1.setLayout(
				new FormLayout(
						new ColumnSpec[] {
								FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.DEFAULT_COLSPEC,
								FormSpecs.RELATED_GAP_COLSPEC,
								FormSpecs.DEFAULT_COLSPEC, },
						new RowSpec[] {
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,
								FormSpecs.RELATED_GAP_ROWSPEC,
								FormSpecs.DEFAULT_ROWSPEC,}));

		JCheckBox chckbxNewCheckBox_73 = new JCheckBox("Ocean");
		checkBoxes1.add(chckbxNewCheckBox_73, "2, 2");
		//chckbxNewCheckBox_73.setEnabled(false);

		JCheckBox chckbxNewCheckBox_74 = new JCheckBox("Plains");
		checkBoxes1.add(chckbxNewCheckBox_74, "4, 2");

		JCheckBox chckbxNewCheckBox_75 = new JCheckBox("Desert");
		checkBoxes1.add(chckbxNewCheckBox_75, "2, 4");

		JCheckBox chckbxNewCheckBox_76 = new JCheckBox("Extreme Hills");
		checkBoxes1.add(chckbxNewCheckBox_76, "4, 4");

		JCheckBox chckbxNewCheckBox_77 = new JCheckBox("Forest");
		checkBoxes1.add(chckbxNewCheckBox_77, "2, 6");

		JCheckBox chckbxNewCheckBox_78 = new JCheckBox("Taiga");
		checkBoxes1.add(chckbxNewCheckBox_78, "4, 6");

		JCheckBox chckbxNewCheckBox_79 = new JCheckBox("Swampland");
		checkBoxes1.add(chckbxNewCheckBox_79, "2, 8");

		JCheckBox chckbxNewCheckBox_80 = new JCheckBox("River");
		checkBoxes1.add(chckbxNewCheckBox_80, "4, 8");

		JCheckBox chckbxNewCheckBox_81 = new JCheckBox("Hell");
		checkBoxes1.add(chckbxNewCheckBox_81, "2, 10");
		chckbxNewCheckBox_81.setEnabled(false);

		JCheckBox chckbxNewCheckBox_82 = new JCheckBox("The End");
		checkBoxes1.add(chckbxNewCheckBox_82, "4, 10");
		chckbxNewCheckBox_82.setEnabled(false);

		JCheckBox chckbxNewCheckBox_83 = new JCheckBox("Frozen Ocean");
		checkBoxes1.add(chckbxNewCheckBox_83, "2, 12");

		JCheckBox chckbxNewCheckBox_84 = new JCheckBox("Frozen River");
		checkBoxes1.add(chckbxNewCheckBox_84, "4, 12");

		JCheckBox chckbxNewCheckBox_840 = new JCheckBox("Ice Plains");
		checkBoxes1.add(chckbxNewCheckBox_840, "2, 14");

		JCheckBox chckbxNewCheckBox_85 = new JCheckBox("Ice Mountains");
		checkBoxes1.add(chckbxNewCheckBox_85, "4, 14");

		JCheckBox chckbxNewCheckBox_86 = new JCheckBox("Mushroom Island");
		checkBoxes1.add(chckbxNewCheckBox_86, "2, 16");

		JCheckBox chckbxNewCheckBox_87 = new JCheckBox("Mushroom Island Shore");
		checkBoxes1.add(chckbxNewCheckBox_87, "4, 16");

		JCheckBox chckbxNewCheckBox_88 = new JCheckBox("Beach");
		checkBoxes1.add(chckbxNewCheckBox_88, "2, 18");

		JCheckBox chckbxNewCheckBox_89 = new JCheckBox("Desert Hills");
		checkBoxes1.add(chckbxNewCheckBox_89, "4, 18");

		JCheckBox chckbxNewCheckBox_90 = new JCheckBox("Forest Hills");
		checkBoxes1.add(chckbxNewCheckBox_90, "2, 20");

		JCheckBox chckbxNewCheckBox_91 = new JCheckBox("Extreme Hills Edge");
		checkBoxes1.add(chckbxNewCheckBox_91, "4, 20");

		JCheckBox chckbxNewCheckBox_92 = new JCheckBox("Jungle");
		checkBoxes1.add(chckbxNewCheckBox_92, "2, 22");

		JCheckBox chckbxNewCheckBox_93 = new JCheckBox("Jungle Hills");
		checkBoxes1.add(chckbxNewCheckBox_93, "4, 22");

		JCheckBox chckbxNewCheckBox_94 = new JCheckBox("Jungle Edge");
		checkBoxes1.add(chckbxNewCheckBox_94, "2, 24");

		JCheckBox chckbxNewCheckBox_95 = new JCheckBox("Deep Ocean");
		checkBoxes1.add(chckbxNewCheckBox_95, "4, 24");

		JCheckBox chckbxNewCheckBox_96 = new JCheckBox("Stone Beach");
		checkBoxes1.add(chckbxNewCheckBox_96, "2, 26");

		JCheckBox chckbxNewCheckBox_97 = new JCheckBox("Cold Beach");
		checkBoxes1.add(chckbxNewCheckBox_97, "4, 26");

		JCheckBox chckbxNewCheckBox_98 = new JCheckBox("Birch Forest");
		checkBoxes1.add(chckbxNewCheckBox_98, "2, 28");

		JCheckBox chckbxNewCheckBox_99 = new JCheckBox("Birch Forest Hills");
		checkBoxes1.add(chckbxNewCheckBox_99, "4, 28");

		JCheckBox chckbxNewCheckBox_100 = new JCheckBox("Roofed Forest");
		checkBoxes1.add(chckbxNewCheckBox_100, "2, 30");

		JCheckBox chckbxNewCheckBox_101 = new JCheckBox("Cold Taiga");
		checkBoxes1.add(chckbxNewCheckBox_101, "4, 30");

		JCheckBox chckbxNewCheckBox_102 = new JCheckBox("Cold Taiga Hills");
		checkBoxes1.add(chckbxNewCheckBox_102, "2, 30");

		JCheckBox chckbxNewCheckBox_103 = new JCheckBox("Mega Taiga");
		checkBoxes1.add(chckbxNewCheckBox_103, "2, 32");

		JCheckBox chckbxNewCheckBox_104 = new JCheckBox("Mega Taiga Hills");
		checkBoxes1.add(chckbxNewCheckBox_104, "4, 32");

		JCheckBox chckbxNewCheckBox_105 = new JCheckBox("Extreme Hills+");
		checkBoxes1.add(chckbxNewCheckBox_105, "2, 34");

		JCheckBox chckbxNewCheckBox_106 = new JCheckBox("Savanna");
		checkBoxes1.add(chckbxNewCheckBox_106, "4, 34");

		JCheckBox chckbxNewCheckBox_107 = new JCheckBox("Savanna Plateau");
		checkBoxes1.add(chckbxNewCheckBox_107, "2, 36");

		JCheckBox chckbxNewCheckBox_108 = new JCheckBox("Mesa");
		checkBoxes1.add(chckbxNewCheckBox_108, "4, 36");

		JCheckBox chckbxNewCheckBox_109 = new JCheckBox("Mesa Plateau F");
		checkBoxes1.add(chckbxNewCheckBox_109, "2, 38");

		JCheckBox chckbxNewCheckBox_110 = new JCheckBox("Mesa Plateau");
		checkBoxes1.add(chckbxNewCheckBox_110, "4, 38");

		JCheckBox chckbxNewCheckBox_111 = new JCheckBox("The End - Floating islands");
		checkBoxes1.add(chckbxNewCheckBox_111, "2, 40");
		chckbxNewCheckBox_111.setEnabled(false);

		JCheckBox chckbxNewCheckBox_112 = new JCheckBox("The End - Medium island");
		checkBoxes1.add(chckbxNewCheckBox_112, "4, 40");
		chckbxNewCheckBox_112.setEnabled(false);

		JCheckBox chckbxNewCheckBox_113 = new JCheckBox("The End - High island");
		checkBoxes1.add(chckbxNewCheckBox_113, "2, 42");
		chckbxNewCheckBox_113.setEnabled(false);

		JCheckBox chckbxNewCheckBox_114 = new JCheckBox("The End - Barren island");
		checkBoxes1.add(chckbxNewCheckBox_114, "4, 42");
		chckbxNewCheckBox_114.setEnabled(false);

		JCheckBox chckbxNewCheckBox_115 = new JCheckBox("Warm Ocean");
		checkBoxes1.add(chckbxNewCheckBox_115, "2, 44");

		JCheckBox chckbxNewCheckBox_116 = new JCheckBox("Lukewarm Ocean");
		checkBoxes1.add(chckbxNewCheckBox_116, "4, 44");

		JCheckBox chckbxNewCheckBox_117 = new JCheckBox("Cold Ocean");
		checkBoxes1.add(chckbxNewCheckBox_117, "2, 46");

		JCheckBox chckbxNewCheckBox_118 = new JCheckBox("Warm Deep Ocean");
		checkBoxes1.add(chckbxNewCheckBox_118, "4, 46");
		chckbxNewCheckBox_118.setEnabled(false);

		JCheckBox chckbxNewCheckBox_119 = new JCheckBox("Lukewarm Deep Ocean");
		checkBoxes1.add(chckbxNewCheckBox_119, "2, 48");

		JCheckBox chckbxNewCheckBox_120 = new JCheckBox("Cold Deep Ocean");
		checkBoxes1.add(chckbxNewCheckBox_120, "4, 48");
		
		JCheckBox chckbxNewCheckBox_121 = new JCheckBox("Frozen Deep Ocean");
		checkBoxes1.add(chckbxNewCheckBox_121, "2, 50");

		JCheckBox chckbxNewCheckBox_122 = new JCheckBox("The Void");
		checkBoxes1.add(chckbxNewCheckBox_122, "4, 50");
		chckbxNewCheckBox_122.setEnabled(false);
		
		JCheckBox chckbxNewCheckBox_123 = new JCheckBox("Sunflower Plains");
		checkBoxes1.add(chckbxNewCheckBox_123, "2, 52");
		
		JCheckBox chckbxNewCheckBox_124 = new JCheckBox("Desert M");
		checkBoxes1.add(chckbxNewCheckBox_124, "4, 52");
		
		JCheckBox chckbxNewCheckBox_125 = new JCheckBox("Extreme Hills M");
		checkBoxes1.add(chckbxNewCheckBox_125, "2, 54");
		
		JCheckBox chckbxNewCheckBox_126 = new JCheckBox("Flower Forest");
		checkBoxes1.add(chckbxNewCheckBox_126, "4, 54");
		
		JCheckBox chckbxNewCheckBox_127 = new JCheckBox("Taiga M");
		checkBoxes1.add(chckbxNewCheckBox_127, "2, 56");
		
		JCheckBox chckbxNewCheckBox_128 = new JCheckBox("Swampland M");
		checkBoxes1.add(chckbxNewCheckBox_128, "4, 56");
		
		JCheckBox chckbxNewCheckBox_129 = new JCheckBox("Ice Plains Spikes");
		checkBoxes1.add(chckbxNewCheckBox_129, "2, 58");
		
		JCheckBox chckbxNewCheckBox_130 = new JCheckBox("Jungle M");
		checkBoxes1.add(chckbxNewCheckBox_130, "4, 58");
		
		JCheckBox chckbxNewCheckBox_131 = new JCheckBox("Jungle Edge M");
		checkBoxes1.add(chckbxNewCheckBox_131, "2, 60");
		
		JCheckBox chckbxNewCheckBox_132 = new JCheckBox("Birch Forest M");
		checkBoxes1.add(chckbxNewCheckBox_132, "4, 60");
		
		JCheckBox chckbxNewCheckBox_133 = new JCheckBox("Birch Forest Hills M");
		checkBoxes1.add(chckbxNewCheckBox_133, "2, 62");
		
		JCheckBox chckbxNewCheckBox_134 = new JCheckBox("Roofed Forest M");
		checkBoxes1.add(chckbxNewCheckBox_134, "4, 62");
		
		JCheckBox chckbxNewCheckBox_135 = new JCheckBox("Cold Taiga M");
		checkBoxes1.add(chckbxNewCheckBox_135, "2, 64");
		
		JCheckBox chckbxNewCheckBox_136 = new JCheckBox("Mega Spruce Taiga");
		checkBoxes1.add(chckbxNewCheckBox_136, "4, 64");
		
		JCheckBox chckbxNewCheckBox_137 = new JCheckBox("Mega Spruce Taiga (Hills)");
		checkBoxes1.add(chckbxNewCheckBox_137, "2, 66");
		
		JCheckBox chckbxNewCheckBox_138 = new JCheckBox("Extreme Hills+ M");
		checkBoxes1.add(chckbxNewCheckBox_138, "4, 66");
		
		JCheckBox chckbxNewCheckBox_139 = new JCheckBox("Savanna M");
		checkBoxes1.add(chckbxNewCheckBox_139, "2, 68");
		
		JCheckBox chckbxNewCheckBox_140 = new JCheckBox("Savanna Plateau M");
		checkBoxes1.add(chckbxNewCheckBox_140, "4, 68");
		
		JCheckBox chckbxNewCheckBox_141 = new JCheckBox("Mesa (Bryce)");
		checkBoxes1.add(chckbxNewCheckBox_141, "2, 70");
		
		JCheckBox chckbxNewCheckBox_142 = new JCheckBox("Mesa Plateau F M");
		checkBoxes1.add(chckbxNewCheckBox_142, "4, 70");
		
		JCheckBox chckbxNewCheckBox_143 = new JCheckBox("Mesa Plateau M");
		checkBoxes1.add(chckbxNewCheckBox_143, "2, 72");
	}

	/**
	 * Some Biomes come back as null. No idea. The Names match each other so it
	 * should work (Apparently it works like 1 in 10 times...)
	 * 
	 * @param biomeCodesCount
	 * @param biomeCodes
	 * @return
	 * @throws UnknownBiomeIndexException
	 * @throws InterruptedException
	 * @throws IOException
	 * @throws FormatException
	 * @throws MinecraftInterfaceCreationException
	 */
	public static Biome[] manageCheckedCheckboxes()
			throws UnknownBiomeIndexException,
			InterruptedException,
			IOException,
			FormatException,
			MinecraftInterfaceCreationException {
		Component[] comps = checkBoxes.getComponents();
		List<String> checkedTexts = new ArrayList<String>();

		for (Component comp : comps) {

			if (comp instanceof JCheckBox) {
				JCheckBox box = (JCheckBox) comp;
				if (box.isSelected()) {

					String text = box.getText();
					// System.out.println(box.getText());
					checkedTexts.add(text);
				}
			}
		}

		Biome[] biomes = new Biome[checkedTexts.size()];

		for (int i = 0; i < checkedTexts.size(); i++) {

			biomes[i] = Biome.getByName(checkedTexts.get(i));

		}

		if (biomes.length == 0) {
			Util.console(
					"Please select Biomes!\nPlease click start a few times to start again (Working on a fix)\nRecommend you clear also!");
			Main.stop();
		}
		return biomes;

	}
	
	public static Biome[] manageCheckedCheckboxesRejected()
			throws UnknownBiomeIndexException,
			InterruptedException,
			IOException,
			FormatException,
			MinecraftInterfaceCreationException {
		Component[] comps = checkBoxes1.getComponents();
		List<String> checkedTexts = new ArrayList<String>();

		for (Component comp : comps) {

			if (comp instanceof JCheckBox) {
				JCheckBox box = (JCheckBox) comp;
				if (box.isSelected()) {

					String text = box.getText();
					// System.out.println(box.getText());
					checkedTexts.add(text);
				}
			}
		}

		Biome[] biomes = new Biome[checkedTexts.size()];

		for (int i = 0; i < checkedTexts.size(); i++) {

			biomes[i] = Biome.getByName(checkedTexts.get(i));

		}

		if (biomes.length == 0) {
			Util.console(
					"Please select Biomes!\nPlease click start a few times to start again (Working on a fix)\nRecommend you clear also!");
			Main.stop();
		}
		return biomes;

	}

	/**
	 * Seeing biomes to add to the search array!
	 * 
	 * @param checkbox change state
	 */
	public void itemStateChanged(ItemEvent e) {
		Object source = e.getItemSelectable();

		if (source == "Biomes") {
			// Remove Biome from arrayList
		} else if (source == "Biomes") {
			// Remove Biome from arrayList
		}

		if (e.getStateChange() == ItemEvent.DESELECTED) {
			// Remove Biome from arrayList
		}
	}

}
