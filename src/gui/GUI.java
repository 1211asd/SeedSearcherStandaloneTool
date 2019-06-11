package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
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

public class GUI {
	
	private JFrame frmSeedTool;
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	/**
	 * Launch the application.
	 */

	private static final int DELAY = 0;
	static Timer timer;
	public static boolean running;
	public static boolean paused;
	private static long startTime;
	static JButton btnClear;
	static JButton btnStart;
	static JButton btnPause;
	public static JCheckBox excludeBiome;

	public static JPanel includeCB;
	public static JPanel excludeCB;

	public static String[] biomeSelected;

	ButtonListener listener = new ButtonListener();

	public static JLabel seedCount;
	public static JLabel totalSeedCount;
	public static JLabel timeElapsed;
	public static JTextArea console;

	static Thread t;
	static boolean allowThreadToSearch = true;
	static BiomeSearcher r;
	private static JTextField widthSearch;
	private static JTextField heightSearch;
	private static JTextField maxSeeds;

	
	private static int searchQuadrantWidth = 2048;
	private static int searchQuadrantHeight = 2048;
	private static int maximumMatchingWorldsCount = 10;
	public static String minecraftVersion = "1.14.2";
	

	static BiomeSearcher createNewThread() throws IOException, FormatException, MinecraftInterfaceCreationException {
		BiomeSearcher.SearchCenterKind searchCenterKind = BiomeSearcher.SearchCenterKind.ORIGIN;


		r = new BiomeSearcher(
				minecraftVersion,
				searchCenterKind,
				Integer.parseInt(widthSearch.getText()),
				Integer.parseInt(heightSearch.getText()),
				Integer.parseInt(maxSeeds.getText()));
		// t = new Thread(r);
		return r;
	}

	public void startSeedSearcher() throws IOException, FormatException, MinecraftInterfaceCreationException {
		initTimer();
		Util.console("Please select Biomes first!");
		// Execute.
	}

	private void initTimer() {
		Action updateLabelAction = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 3920770968451095353L;

			@Override
			public void actionPerformed(ActionEvent e) {
				updateDisplay();
			}
		};
		timer = new Timer(DELAY, updateLabelAction);
	}

	private static void updateDisplay() {
		if (!paused) {
			// String text = String.format("%02d:%02d:%02d:%02d",
			// this.hours, this.minutes, this.seconds, this.hundredths);
			// this.timeLabel.setText(text);

			timeElapsed.setText("Time Elapsed: " + Util.getElapsedTimeHoursMinutesFromMilliseconds(System.currentTimeMillis() - startTime));
		}
	}

	private static void toggleRunning() throws InterruptedException, IOException, FormatException,
			MinecraftInterfaceCreationException, UnknownBiomeIndexException {
		allowThreadToSearch = true;
		if (running) {
			System.out.println("Shutting Down...");
			stop();
		} else {
			manageCheckedCheckboxes();
			if (allowThreadToSearch) {
				start();
			} else {
				stop();
			}
		}

	}

	private static void start() throws IOException, FormatException, MinecraftInterfaceCreationException {
		t = new Thread(createNewThread());
		startTime = System.currentTimeMillis();
		running = true;
		t.start();
		timer.restart();
		btnStart.setText("Stop");
		BiomeSearcher.totalRejectedSeedCount = 0;

	}

	public static void stop() throws InterruptedException, IOException, FormatException, MinecraftInterfaceCreationException {
		btnStart.setText("Start");
		btnPause.setText("Pause");
		running = false;
		timer.stop();
		if (t != null) {
			t.interrupt();
			t = new Thread(createNewThread());
		}
	}

	private static void togglePause() {
		paused = !paused;
		String text = (paused) ? "Click To Unpause" : "Click to Pause";
		 
		if(paused) {
			timer.stop();
		} else {
			timer.start();
			
			//startTime = timeAtPause;
		}
		btnPause.setText(text);
		updateDisplay();
	}

	private static void reset() throws InterruptedException, IOException, FormatException,
			MinecraftInterfaceCreationException, UnknownBiomeIndexException {
		if (paused) {
			togglePause();
		}
		stop();
		Util.consoleWipe();
		timeElapsed.setText("Time Elapsed: 00:00:00");
		startTime = System.currentTimeMillis();
		seedCount.setText("Rejected Seed Count: 0");
		totalSeedCount.setText("Total Rejected Seed Count: 0");
		BiomeSearcher.totalRejectedSeedCount = 0;

		updateDisplay();
	}

	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnStart) {
				try {
					toggleRunning();
				} catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException |
						UnknownBiomeIndexException e1) {
					e1.printStackTrace();
				}

			} else if (e.getSource() == btnPause) {
				togglePause();
			} else if (e.getSource() == btnClear) {
				try {
					reset();
				} catch (InterruptedException | IOException | FormatException | MinecraftInterfaceCreationException |
						UnknownBiomeIndexException e1) {
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
	private void initialize() {
		frmSeedTool = new JFrame();
		frmSeedTool.setTitle("Seed Tool");
		frmSeedTool.setResizable(false);
		frmSeedTool.setBounds(100, 100, Main.BACK_FRAME_WIDTH, Main.BACK_FRAME_HEIGHT);
		frmSeedTool.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmSeedTool.setVisible(true);
		frmSeedTool.getContentPane().setLayout(null);

		JPanel panel_console = new JPanel();
		panel_console.setBounds(0, 0, Main.CONSOLE_WIDTH, 25);
		frmSeedTool.getContentPane().add(panel_console);
		panel_console.setLayout(new BorderLayout(0, 0));

		JLabel consoleTxt = new JLabel("Console Output");
		consoleTxt.setHorizontalAlignment(SwingConstants.CENTER);
		panel_console.add(consoleTxt, BorderLayout.CENTER);
		
		JScrollPane consoleScrollBar = new JScrollPane();
		consoleScrollBar.setBounds(0, 25, Main.CONSOLE_WIDTH, Main.CONSOLE_HEIGHT);
		frmSeedTool.getContentPane().add(consoleScrollBar);

		console = new JTextArea();
		console.setLineWrap(true);
		consoleScrollBar.setViewportView(console);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBounds(Main.CONSOLE_WIDTH, 0, Main.FRAME_WITHOUT_CONSOLE_WIDTH-14, Main.BACK_FRAME_HEIGHT-36);
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
		btnStart.setBounds(10, 350, 150, 25);
		panel.add(btnStart);

		btnPause = new JButton("Pause");
		btnPause.addActionListener(listener);
		btnPause.setBounds(10, 385, 150, 25);
		panel.add(btnPause);

		btnClear = new JButton("Clear");
		btnClear.addActionListener(listener);
		btnClear.setBounds(10, 420, 150, 25);
		panel.add(btnClear);
		
		
		String[] versions = {"1.14.2", "1.13.2"};
		JComboBox<String> versionBox = new JComboBox<String>();
		for (String version : versions) versionBox.addItem(version);
		versionBox.setSelectedIndex(0);
		
		versionBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				JComboBox<String> combo = versionBox;
				String selected = (String) combo.getSelectedItem();
				minecraftVersion = selected;
			}
		});
		
		Container cp = new Container();
		JLabel versionLabel = new JLabel("Minecraft Version:");
		cp.setLayout(new FlowLayout());
		cp.add(versionLabel);
		cp.add(versionBox);
		cp.setBounds(385, 350, 140, 100);
		panel.add(cp);
		
		JLabel lblExcludeBiomes = new JLabel("Exclude biomes?");
		lblExcludeBiomes.setBounds(385, 410, 125, 20);
		panel.add(lblExcludeBiomes);
		
		excludeBiome = new JCheckBox("");
		excludeBiome.setBounds(500, 410, 20, 20);
		excludeBiome.addActionListener(listener);
		panel.add(excludeBiome);
		
		JLabel lblSearchWidth = new JLabel("Search Width (x):");
		lblSearchWidth.setBounds(175, 350, 200, 20);
		panel.add(lblSearchWidth);
		
		widthSearch = new JTextField();
		widthSearch.setText(""+searchQuadrantWidth);
		widthSearch.setBounds(285, 350, 85, 20);
		panel.add(widthSearch);
		widthSearch.setColumns(10);
		
		JLabel lblSearchHeight = new JLabel("Search Height (z):");
		lblSearchHeight.setBounds(175, 385, 200, 20);
		panel.add(lblSearchHeight);
		
		heightSearch = new JTextField();
		heightSearch.setText(""+searchQuadrantHeight);
		heightSearch.setBounds(285, 385, 85, 20);
		panel.add(heightSearch);
		heightSearch.setColumns(10);
		
		JLabel maxSeedsLabel = new JLabel("Seeds to Find:");
		maxSeedsLabel.setBounds(175, 420, 150, 20);
		panel.add(maxSeedsLabel);
		
		maxSeeds = new JTextField();
		maxSeeds.setText(""+maximumMatchingWorldsCount);
		maxSeeds.setBounds(285, 420, 85, 20);
		panel.add(maxSeeds);
		maxSeeds.setColumns(10);
		
		// Panel 1: Inclusion
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Inclusion", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel lblBiomeInclusionSelectionTxt = new JLabel("Biome Inclusion");
		Util.setFontSize(lblBiomeInclusionSelectionTxt, 24);
		lblBiomeInclusionSelectionTxt.setBounds(0, 0, (Main.BACK_FRAME_WIDTH-Main.CONSOLE_WIDTH), 33);
		lblBiomeInclusionSelectionTxt.setHorizontalAlignment(SwingConstants.CENTER);
		panel_1.add(lblBiomeInclusionSelectionTxt);
		
		JScrollPane lblBiomeInclusionSelectionScroll = new JScrollPane();
		lblBiomeInclusionSelectionScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		lblBiomeInclusionSelectionScroll.setBounds(0, 33, Main.FRAME_SCROLL_BAR_WIDTH, Main.FRAME_SCROLL_BAR_HEIGHT);
		lblBiomeInclusionSelectionScroll.getVerticalScrollBar().setUnitIncrement(10);
		panel_1.add(lblBiomeInclusionSelectionScroll);

		includeCB = new JPanel();
		lblBiomeInclusionSelectionScroll.setViewportView(includeCB);
		includeCB.setLayout(
				new FormLayout(
						new ColumnSpec[] {
								FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, // Col 1
								FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, // Col 2
								FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, // Col 3
								},
						new RowSpec[] {
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 2
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 4
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 6
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 8
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 10
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 12
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 14
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 16
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 18
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 20
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 22
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 24
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 26
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 28
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 30
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 32
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 34
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 36
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 38
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 40
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 42
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 44
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 46
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 48
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 50
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 52
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 54
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 56
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 58
								FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		/*
		 FORMAT:
		 
		JCheckBox inb__ = new JCheckBox("");
		includeCB.add(inb__, "2, ");
		
		JCheckBox inb__ = new JCheckBox("");
		includeCB.add(inb__, "4, ");
		
		JCheckBox inb__ = new JCheckBox("");
		includeCB.add(inb__, "6, ");
		 */
		
		JLabel in_hotBiomesTxt = new JLabel("Hot Biomes");
		in_hotBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(in_hotBiomesTxt, 18);
		Util.Underline(in_hotBiomesTxt);
		includeCB.add(in_hotBiomesTxt, "4, 2");
		
		JCheckBox inb_des_1 = new JCheckBox("Desert");
		includeCB.add(inb_des_1, "2, 4");
		
		JCheckBox inb_des_2 = new JCheckBox("Desert Hills");
		includeCB.add(inb_des_2, "4, 4");
		
		JCheckBox inb_des_3 = new JCheckBox("Desert M");
		includeCB.add(inb_des_3, "6, 4");
		
		JCheckBox inb_sav_1 = new JCheckBox("Savanna");
		includeCB.add(inb_sav_1, "2, 6");
		
		JCheckBox inb_sav_2 = new JCheckBox("Savanna Plateau");
		includeCB.add(inb_sav_2, "4, 6");
		
		JCheckBox inb_sav_3 = new JCheckBox("Savanna M");
		includeCB.add(inb_sav_3, "6, 6");
		
		JCheckBox inb_sav_4 = new JCheckBox("Savanna Plateau M");
		includeCB.add(inb_sav_4, "2, 8");
		
		JCheckBox inb_mes_1 = new JCheckBox("Mesa");
		includeCB.add(inb_mes_1, "4, 8");
		
		JCheckBox inb_mes_2 = new JCheckBox("Mesa Plateau F");
		includeCB.add(inb_mes_2, "6, 8");
		
		JCheckBox inb_mes_3 = new JCheckBox("Mesa Plateau");
		includeCB.add(inb_mes_3, "2, 10");
		
		JCheckBox inb_mes_4 = new JCheckBox("Mesa (Bryce)");
		includeCB.add(inb_mes_4, "4, 10");
		
		JCheckBox inb_mes_5 = new JCheckBox("Mesa Plateau F M");
		includeCB.add(inb_mes_5, "6, 10");
		
		JCheckBox inb_mes_6= new JCheckBox("Mesa Plateau F M");
		includeCB.add(inb_mes_6, "2, 12");
		
		JLabel in_lushBiomesTxt = new JLabel("Lush Biomes");
		in_lushBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(in_lushBiomesTxt, 18);
		Util.Underline(in_lushBiomesTxt);
		includeCB.add(in_lushBiomesTxt, "4, 14");
		
		JCheckBox inb_pla_1 = new JCheckBox("Plains");
		includeCB.add(inb_pla_1, "2, 16");
		
		JCheckBox inb_pla_2 = new JCheckBox("Sunflower Plains");
		includeCB.add(inb_pla_2, "4, 16");
		
		JCheckBox inb_for_1 = new JCheckBox("Forest");
		includeCB.add(inb_for_1, "6, 16");
		
		JCheckBox inb_for_2 = new JCheckBox("Forest Hills");
		includeCB.add(inb_for_2, "2, 18");
		
		JCheckBox inb_for_3 = new JCheckBox("Flower Forest");
		includeCB.add(inb_for_3, "4, 18");
		
		JCheckBox inb_bir_1 = new JCheckBox("Birch Forest");
		includeCB.add(inb_bir_1, "6, 18");
		
		JCheckBox inb_bir_2 = new JCheckBox("Birch Forest Hills");
		includeCB.add(inb_bir_2, "2, 20");
		
		JCheckBox inb_bir_3 = new JCheckBox("Birch Forest M");
		includeCB.add(inb_bir_3, "4, 20");
		
		JCheckBox inb_bir_4 = new JCheckBox("Birch Forest Hills M");
		includeCB.add(inb_bir_4, "6, 20");
		
		JCheckBox inb_roo_1 = new JCheckBox("Roofed Forest");
		includeCB.add(inb_roo_1, "2, 22");
		
		JCheckBox inb_roo_2 = new JCheckBox("Roofed Forest M");
		includeCB.add(inb_roo_2, "4, 22");
		
		JCheckBox inb_swa_1 = new JCheckBox("Swampland");
		includeCB.add(inb_swa_1, "6, 22");
		
		JCheckBox inb_swa_2 = new JCheckBox("Swampland M");
		includeCB.add(inb_swa_2, "2, 24");
		
		JCheckBox inb_jun_1 = new JCheckBox("Jungle");
		includeCB.add(inb_jun_1, "4, 24");
		
		JCheckBox inb_jun_2 = new JCheckBox("Jungle Hills");
		includeCB.add(inb_jun_2, "6, 24");
		
		JCheckBox inb_jun_3 = new JCheckBox("Jungle Edge");
		includeCB.add(inb_jun_3, "2, 26");
		
		JCheckBox inb_jun_4 = new JCheckBox("Jungle M");
		includeCB.add(inb_jun_4, "4, 26");
		
		JCheckBox inb_jun_5 = new JCheckBox("Jungle Edge M");
		includeCB.add(inb_jun_5, "6, 26");
		
//		if (Version.isOrGreaterThanSelectedVersion(Version.V1_14_2)) {
			JCheckBox inb_jun_6 = new JCheckBox("Bamboo Jungle");
			includeCB.add(inb_jun_6, "2, 28");
			
			JCheckBox inb_jun_7 = new JCheckBox("Bamboo Jungle Hills");
			includeCB.add(inb_jun_7, "4, 28");
//		}
		
		JLabel in_coldBiomesTxt = new JLabel("Cold Biomes");
		in_coldBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(in_coldBiomesTxt, 18);
		Util.Underline(in_coldBiomesTxt);
		includeCB.add(in_coldBiomesTxt, "4, 30");
		
		JCheckBox inb_ext_1 = new JCheckBox("Extreme Hills");
		includeCB.add(inb_ext_1, "2, 32");
		
		JCheckBox inb_ext_2 = new JCheckBox("Extreme Hills Edge");
		includeCB.add(inb_ext_2, "4, 32");
		
		JCheckBox inb_ext_3 = new JCheckBox("Extreme Hills+");
		includeCB.add(inb_ext_3, "6, 32");
		
		JCheckBox inb_ext_4 = new JCheckBox("Extreme Hills M");
		includeCB.add(inb_ext_4, "2, 34");
		
		JCheckBox inb_ext_5 = new JCheckBox("Extreme Hills+ M");
		includeCB.add(inb_ext_5, "4, 34");
		
		JCheckBox inb_tai_1 = new JCheckBox("Taiga");
		includeCB.add(inb_tai_1, "6, 34");
		
		JCheckBox inb_tai_2 = new JCheckBox("Taiga Hills");
		includeCB.add(inb_tai_2, "2, 36");
		
		JCheckBox inb_tai_3 = new JCheckBox("Mega Taiga");
		includeCB.add(inb_tai_3, "4, 36");
		
		JCheckBox inb_tai_4 = new JCheckBox("Mega Taiga Hills");
		includeCB.add(inb_tai_4, "6, 36");
		
		JCheckBox inb_tai_5 = new JCheckBox("Taiga M");
		includeCB.add(inb_tai_5, "2, 38");
		
		JCheckBox inb_tai_6 = new JCheckBox("Mega Spruce Taiga");
		includeCB.add(inb_tai_6, "4, 38");
		
		JCheckBox inb_tai_7 = new JCheckBox("Mega Spruce Taiga (Hills)");
		includeCB.add(inb_tai_7, "6, 38");
		
		JLabel in_snowyBiomesTxt = new JLabel("Snowy Biomes");
		in_snowyBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(in_snowyBiomesTxt, 18);
		Util.Underline(in_snowyBiomesTxt);
		includeCB.add(in_snowyBiomesTxt, "4, 40");
		
		JCheckBox inb_col_1 = new JCheckBox("Cold Taiga");
		includeCB.add(inb_col_1, "2, 42");
		
		JCheckBox inb_col_2 = new JCheckBox("Cold Taiga Hills");
		includeCB.add(inb_col_2, "4, 42");
		
		JCheckBox inb_col_3 = new JCheckBox("Cold Taiga M");
		includeCB.add(inb_col_3, "6, 42");
		
		JCheckBox inb_ice_1 = new JCheckBox("Ice Plains");
		includeCB.add(inb_ice_1, "2, 44");
		
		JCheckBox inb_ice_2 = new JCheckBox("Ice Mountains");
		includeCB.add(inb_ice_2, "4, 44");
		
		JCheckBox inb_ice_3 = new JCheckBox("Ice Plains Spikes");
		includeCB.add(inb_ice_3, "6, 44");
		
		JLabel in_waterBiomesTxt = new JLabel("Water Biomes");
		in_waterBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(in_waterBiomesTxt, 18);
		Util.Underline(in_waterBiomesTxt);
		includeCB.add(in_waterBiomesTxt, "4, 46");
		
		JCheckBox inb_bea_1 = new JCheckBox("Beach");
		includeCB.add(inb_bea_1, "2, 48");
		
		JCheckBox inb_bea_2 = new JCheckBox("Stone Beach");
		includeCB.add(inb_bea_2, "4, 48");
		
		JCheckBox inb_bea_3 = new JCheckBox("Cold Beach");
		includeCB.add(inb_bea_3, "6, 48");
		
		JCheckBox inb_oce_1 = new JCheckBox("River");
		includeCB.add(inb_oce_1, "2, 50");
		
		JCheckBox inb_oce_2 = new JCheckBox("Ocean");
		includeCB.add(inb_oce_2, "4, 50");
		
		JCheckBox inb_oce_3 = new JCheckBox("Deep Ocean");
		includeCB.add(inb_oce_3, "6, 50");
		
		JCheckBox inb_fro_1 = new JCheckBox("Frozen River");
		includeCB.add(inb_fro_1, "2, 52");
		
		JCheckBox inb_fro_2 = new JCheckBox("Frozen Ocean");
		includeCB.add(inb_fro_2, "4, 52");
		
		JCheckBox inb_fro_3 = new JCheckBox("Frozen Deep Ocean");
		includeCB.add(inb_fro_3, "4, 52");
		
		JCheckBox inb_mus_1 = new JCheckBox("Mushroom Island");
		includeCB.add(inb_mus_1, "2, 54");
		
		JCheckBox inb_mus_2 = new JCheckBox("Mushroom Island Shore");
		includeCB.add(inb_mus_2, "4, 54");
		
		JCheckBox inb_oce_4 = new JCheckBox("Warm Ocean");
		includeCB.add(inb_oce_4, "6, 54");
		
		JCheckBox inb_oce_5 = new JCheckBox("Warm Deep Ocean");
		includeCB.add(inb_oce_5, "2, 56");
		
		JCheckBox inb_oce_6 = new JCheckBox("Lukewarm Ocean");
		includeCB.add(inb_oce_6, "4, 56");
		
		JCheckBox inb_oce_7 = new JCheckBox("Lukewarm Deep Ocean");
		includeCB.add(inb_oce_7, "6, 56");
		
		JCheckBox inb_oce_8 = new JCheckBox("Cold Ocean");
		includeCB.add(inb_oce_8, "2, 58");
		
		JCheckBox inb_oce_9 = new JCheckBox("Cold Deep Ocean");
		includeCB.add(inb_oce_9, "4, 58");
		
		
		
		// Panel 2: Exclusion
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Exclusion", null, panel_2, null);
		panel_2.setLayout(null);
		tabbedPane.setEnabledAt(2, false);
		JLabel lblExclusionBiomeSelectionTxt = new JLabel("Biome Exclusion");
		Util.setFontSize(lblExclusionBiomeSelectionTxt, 24);
		lblExclusionBiomeSelectionTxt.setBounds(0, 0, (Main.BACK_FRAME_WIDTH-Main.CONSOLE_WIDTH), 33);
		lblExclusionBiomeSelectionTxt.setHorizontalAlignment(SwingConstants.CENTER);
		panel_2.add(lblExclusionBiomeSelectionTxt);
		
		JScrollPane lblExclusionBiomeSelectionScroll = new JScrollPane();
		lblExclusionBiomeSelectionScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		lblExclusionBiomeSelectionScroll.setBounds(0, 33, Main.FRAME_SCROLL_BAR_WIDTH, Main.FRAME_SCROLL_BAR_HEIGHT);
		lblExclusionBiomeSelectionScroll.getVerticalScrollBar().setUnitIncrement(10);
		panel_2.add(lblExclusionBiomeSelectionScroll);
		
		excludeCB = new JPanel();
		lblExclusionBiomeSelectionScroll.setViewportView(excludeCB);
		excludeCB.setLayout(
				new FormLayout(
						new ColumnSpec[] {
								FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, // Col 1
								FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, // Col 2
								FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, // Col 3
								},
						new RowSpec[] {
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 2
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 4
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 6
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 8
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 10
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 12
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 14
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 16
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 18
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 20
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 22
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 24
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 26
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 28
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 30
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 32
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 34
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 36
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 38
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 40
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 42
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 44
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 46
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 48
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 50
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 52
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 54
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 56
								FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, // Row 58
								FormSpecs.RELATED_GAP_ROWSPEC,}));
		
		/*
		 FORMAT:
		 
		JCheckBox exb__ = new JCheckBox("");
		excludeCB.add(exb__, "2, ");
		
		JCheckBox exb__ = new JCheckBox("");
		excludeCB.add(exb__, "4, ");
		
		JCheckBox exb__ = new JCheckBox("");
		excludeCB.add(exb__, "6, ");
		 */
		
		JLabel ex_hotBiomesTxt = new JLabel("Hot Biomes");
		ex_hotBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(ex_hotBiomesTxt, 18);
		Util.Underline(ex_hotBiomesTxt);
		excludeCB.add(ex_hotBiomesTxt, "4, 2");
		
		JCheckBox exb_des_1 = new JCheckBox("Desert");
		excludeCB.add(exb_des_1, "2, 4");
		
		JCheckBox exb_des_2 = new JCheckBox("Desert Hills");
		excludeCB.add(exb_des_2, "4, 4");
		
		JCheckBox exb_des_3 = new JCheckBox("Desert M");
		excludeCB.add(exb_des_3, "6, 4");
		
		JCheckBox exb_sav_1 = new JCheckBox("Savanna");
		excludeCB.add(exb_sav_1, "2, 6");
		
		JCheckBox exb_sav_2 = new JCheckBox("Savanna Plateau");
		excludeCB.add(exb_sav_2, "4, 6");
		
		JCheckBox exb_sav_3 = new JCheckBox("Savanna M");
		excludeCB.add(exb_sav_3, "6, 6");
		
		JCheckBox exb_sav_4 = new JCheckBox("Savanna Plateau M");
		excludeCB.add(exb_sav_4, "2, 8");
		
		JCheckBox exb_mes_1 = new JCheckBox("Mesa");
		excludeCB.add(exb_mes_1, "4, 8");
		
		JCheckBox exb_mes_2 = new JCheckBox("Mesa Plateau F");
		excludeCB.add(exb_mes_2, "6, 8");
		
		JCheckBox exb_mes_3 = new JCheckBox("Mesa Plateau");
		excludeCB.add(exb_mes_3, "2, 10");
		
		JCheckBox exb_mes_4 = new JCheckBox("Mesa (Bryce)");
		excludeCB.add(exb_mes_4, "4, 10");
		
		JCheckBox exb_mes_5 = new JCheckBox("Mesa Plateau F M");
		excludeCB.add(exb_mes_5, "6, 10");
		
		JCheckBox exb_mes_6= new JCheckBox("Mesa Plateau F M");
		excludeCB.add(exb_mes_6, "2, 12");
		
		JLabel ex_lushBiomesTxt = new JLabel("Lush Biomes");
		ex_lushBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(ex_lushBiomesTxt, 18);
		Util.Underline(ex_lushBiomesTxt);
		excludeCB.add(ex_lushBiomesTxt, "4, 14");
		
		JCheckBox exb_pla_1 = new JCheckBox("Plains");
		excludeCB.add(exb_pla_1, "2, 16");
		
		JCheckBox exb_pla_2 = new JCheckBox("Sunflower Plains");
		excludeCB.add(exb_pla_2, "4, 16");
		
		JCheckBox exb_for_1 = new JCheckBox("Forest");
		excludeCB.add(exb_for_1, "6, 16");
		
		JCheckBox exb_for_2 = new JCheckBox("Forest Hills");
		excludeCB.add(exb_for_2, "2, 18");
		
		JCheckBox exb_for_3 = new JCheckBox("Flower Forest");
		excludeCB.add(exb_for_3, "4, 18");
		
		JCheckBox exb_bir_1 = new JCheckBox("Birch Forest");
		excludeCB.add(exb_bir_1, "6, 18");
		
		JCheckBox exb_bir_2 = new JCheckBox("Birch Forest Hills");
		excludeCB.add(exb_bir_2, "2, 20");
		
		JCheckBox exb_bir_3 = new JCheckBox("Birch Forest M");
		excludeCB.add(exb_bir_3, "4, 20");
		
		JCheckBox exb_bir_4 = new JCheckBox("Birch Forest Hills M");
		excludeCB.add(exb_bir_4, "6, 20");
		
		JCheckBox exb_roo_1 = new JCheckBox("Roofed Forest");
		excludeCB.add(exb_roo_1, "2, 22");
		
		JCheckBox exb_roo_2 = new JCheckBox("Roofed Forest M");
		excludeCB.add(exb_roo_2, "4, 22");
		
		JCheckBox exb_swa_1 = new JCheckBox("Swampland");
		excludeCB.add(exb_swa_1, "6, 22");
		
		JCheckBox exb_swa_2 = new JCheckBox("Swampland M");
		excludeCB.add(exb_swa_2, "2, 24");
		
		JCheckBox exb_jun_1 = new JCheckBox("Jungle");
		excludeCB.add(exb_jun_1, "4, 24");
		
		JCheckBox exb_jun_2 = new JCheckBox("Jungle Hills");
		excludeCB.add(exb_jun_2, "6, 24");
		
		JCheckBox exb_jun_3 = new JCheckBox("Jungle Edge");
		excludeCB.add(exb_jun_3, "2, 26");
		
		JCheckBox exb_jun_4 = new JCheckBox("Jungle M");
		excludeCB.add(exb_jun_4, "4, 26");
		
		JCheckBox exb_jun_5 = new JCheckBox("Jungle Edge M");
		excludeCB.add(exb_jun_5, "6, 26");
		
//		if (Version.isOrGreaterThanSelectedVersion(Version.V1_14_2)) {
			JCheckBox exb_jun_6 = new JCheckBox("Bamboo Jungle");
			excludeCB.add(exb_jun_6, "2, 28");
			
			JCheckBox exb_jun_7 = new JCheckBox("Bamboo Jungle Hills");
			excludeCB.add(exb_jun_7, "4, 28");
//		}
		
		JLabel ex_coldBiomesTxt = new JLabel("Cold Biomes");
		ex_coldBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(ex_coldBiomesTxt, 18);
		Util.Underline(ex_coldBiomesTxt);
		excludeCB.add(ex_coldBiomesTxt, "4, 30");
		
		JCheckBox exb_ext_1 = new JCheckBox("Extreme Hills");
		excludeCB.add(exb_ext_1, "2, 32");
		
		JCheckBox exb_ext_2 = new JCheckBox("Extreme Hills Edge");
		excludeCB.add(exb_ext_2, "4, 32");
		
		JCheckBox exb_ext_3 = new JCheckBox("Extreme Hills+");
		excludeCB.add(exb_ext_3, "6, 32");
		
		JCheckBox exb_ext_4 = new JCheckBox("Extreme Hills M");
		excludeCB.add(exb_ext_4, "2, 34");
		
		JCheckBox exb_ext_5 = new JCheckBox("Extreme Hills+ M");
		excludeCB.add(exb_ext_5, "4, 34");
		
		JCheckBox exb_tai_1 = new JCheckBox("Taiga");
		excludeCB.add(exb_tai_1, "6, 34");
		
		JCheckBox exb_tai_2 = new JCheckBox("Taiga Hills");
		excludeCB.add(exb_tai_2, "2, 36");
		
		JCheckBox exb_tai_3 = new JCheckBox("Mega Taiga");
		excludeCB.add(exb_tai_3, "4, 36");
		
		JCheckBox exb_tai_4 = new JCheckBox("Mega Taiga Hills");
		excludeCB.add(exb_tai_4, "6, 36");
		
		JCheckBox exb_tai_5 = new JCheckBox("Taiga M");
		excludeCB.add(exb_tai_5, "2, 38");
		
		JCheckBox exb_tai_6 = new JCheckBox("Mega Spruce Taiga");
		excludeCB.add(exb_tai_6, "4, 38");
		
		JCheckBox exb_tai_7 = new JCheckBox("Mega Spruce Taiga (Hills)");
		excludeCB.add(exb_tai_7, "6, 38");
		
		JLabel ex_snowyBiomesTxt = new JLabel("Snowy Biomes");
		ex_snowyBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(ex_snowyBiomesTxt, 18);
		Util.Underline(ex_snowyBiomesTxt);
		excludeCB.add(ex_snowyBiomesTxt, "4, 40");
		
		JCheckBox exb_col_1 = new JCheckBox("Cold Taiga");
		excludeCB.add(exb_col_1, "2, 42");
		
		JCheckBox exb_col_2 = new JCheckBox("Cold Taiga Hills");
		excludeCB.add(exb_col_2, "4, 42");
		
		JCheckBox exb_col_3 = new JCheckBox("Cold Taiga M");
		excludeCB.add(exb_col_3, "6, 42");
		
		JCheckBox exb_ice_1 = new JCheckBox("Ice Plains");
		excludeCB.add(exb_ice_1, "2, 44");
		
		JCheckBox exb_ice_2 = new JCheckBox("Ice Mountains");
		excludeCB.add(exb_ice_2, "4, 44");
		
		JCheckBox exb_ice_3 = new JCheckBox("Ice Plains Spikes");
		excludeCB.add(exb_ice_3, "6, 44");
		
		JLabel ex_waterBiomesTxt = new JLabel("Water Biomes");
		ex_waterBiomesTxt.setHorizontalAlignment(SwingConstants.CENTER);
		Util.setFontSize(ex_waterBiomesTxt, 18);
		Util.Underline(ex_waterBiomesTxt);
		excludeCB.add(ex_waterBiomesTxt, "4, 46");
		
		JCheckBox exb_bea_1 = new JCheckBox("Beach");
		excludeCB.add(exb_bea_1, "2, 48");
		
		JCheckBox exb_bea_2 = new JCheckBox("Stone Beach");
		excludeCB.add(exb_bea_2, "4, 48");
		
		JCheckBox exb_bea_3 = new JCheckBox("Cold Beach");
		excludeCB.add(exb_bea_3, "6, 48");
		
		JCheckBox exb_oce_1 = new JCheckBox("River");
		excludeCB.add(exb_oce_1, "2, 50");
		
		JCheckBox exb_oce_2 = new JCheckBox("Ocean");
		excludeCB.add(exb_oce_2, "4, 50");
		
		JCheckBox exb_oce_3 = new JCheckBox("Deep Ocean");
		excludeCB.add(exb_oce_3, "6, 50");
		
		JCheckBox exb_fro_1 = new JCheckBox("Frozen River");
		excludeCB.add(exb_fro_1, "2, 52");
		
		JCheckBox exb_fro_2 = new JCheckBox("Frozen Ocean");
		excludeCB.add(exb_fro_2, "4, 52");
		
		JCheckBox exb_fro_3 = new JCheckBox("Frozen Deep Ocean");
		excludeCB.add(exb_fro_3, "4, 52");
		
		JCheckBox exb_mus_1 = new JCheckBox("Mushroom Island");
		excludeCB.add(exb_mus_1, "2, 54");
		
		JCheckBox exb_mus_2 = new JCheckBox("Mushroom Island Shore");
		excludeCB.add(exb_mus_2, "4, 54");
		
		JCheckBox exb_oce_4 = new JCheckBox("Warm Ocean");
		excludeCB.add(exb_oce_4, "6, 54");
		
		JCheckBox exb_oce_5 = new JCheckBox("Warm Deep Ocean");
		excludeCB.add(exb_oce_5, "2, 56");
		
		JCheckBox exb_oce_6 = new JCheckBox("Lukewarm Ocean");
		excludeCB.add(exb_oce_6, "4, 56");
		
		JCheckBox exb_oce_7 = new JCheckBox("Lukewarm Deep Ocean");
		excludeCB.add(exb_oce_7, "6, 56");
		
		JCheckBox exb_oce_8 = new JCheckBox("Cold Ocean");
		excludeCB.add(exb_oce_8, "2, 58");
		
		JCheckBox exb_oce_9 = new JCheckBox("Cold Deep Ocean");
		excludeCB.add(exb_oce_9, "4, 58");
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
	public static Biome[] manageCheckedCheckboxes() throws UnknownBiomeIndexException, InterruptedException,
			IOException, FormatException, MinecraftInterfaceCreationException {
		Component[] comps = includeCB.getComponents();
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
			allowThreadToSearch = false;
			Util.console("Please select Biomes!\nSearch has cancelled.\nRecommend you clear console!");
			stop();
		}
		return biomes;
		
	}
	
	public static Biome[] manageCheckedCheckboxesRejected()
			throws UnknownBiomeIndexException,
			InterruptedException,
			IOException,
			FormatException,
			MinecraftInterfaceCreationException {
		Component[] comps = excludeCB.getComponents();
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
					"Please select Biomes!\nPlease click start a few times to start again (Working on a fix)\nRecommend you clear console!");
			stop();
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
