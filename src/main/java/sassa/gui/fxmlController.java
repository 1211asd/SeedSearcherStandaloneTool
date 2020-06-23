package sassa.gui;


import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;
import kaptainwutax.biomeutils.Biome;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.seedutils.mc.MCVersion;
import sassa.searcher.SearchingThread;
import sassa.searcher.StructureSearcher;
import sassa.util.Singleton;
import sassa.util.StructureProvider;
import sassa.util.Structures;
import sassa.util.Util;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.UnaryOperator;

public class fxmlController implements Initializable {

    private static final int DELAY = 0;
    static Timer timer;
    public static boolean running;
    public static boolean paused;
    private static long pausedTime;
    @SuppressWarnings("unused")
    private static long startTime; // TODO use this in the future to tell user when they started
    private static long elapsedTime;


    static boolean allowThreadToSearch = true;

    @FXML
    private Text cRejSeedCount;

    @FXML
    private Text tRejSeedCount;

    @FXML
    private Button startBtn;

    @FXML
    private Button pauseBtn;

    @FXML
    private Button clearBtn;

    @FXML
    private Text timeElapsed;

    @FXML
    private ComboBox<String> mcVersions;


    @FXML
    private TextField seedsToFind;

    @FXML
    private TextField searchRadius;

    @FXML
    private CheckBox devMode;

    @FXML
    private CheckBox bedrockMode;

    @FXML
    private Text bedrockWarning;

    @FXML
    private CheckBox randomSeed;

    @FXML
    private Pane randomSeedPane;

    @FXML
    private Pane worldTypePane;

    @FXML
    private TextField minSeed;

    @FXML
    private TextField maxSeed;

    @FXML
    private TextArea console;

    @FXML
    private Text notificationLabel;

    @FXML
    private Tab biomesTab;

    @FXML
    private Tab structuresTab;

    @FXML
    private Text sequencedSeed;

    @FXML
    private ImageView paypalDonate;

    @FXML
    private Button saveConsole;

    @FXML
    private CheckBox autoSaveConsole;

    @FXML
    private Button directoryBrowser;

    @FXML
    private Label outputFileText;

    @FXML
    private ComboBox<String> worldType;

    //Get the grid in Biomes tab to dynamically build it.
    @FXML
    private GridPane biomesGrid;

    @FXML
    private GridPane structuresGrid;

    @FXML
    private GridPane biomeSetsGrid;

    @FXML
    private Slider amountOfCores;

    @FXML
    private Text coresAmount;

    String[] include_exclude_txt = {"", "Include", "Exclude"};
    Singleton singleton = Singleton.getInstance();
    MCVersion defaultVersion = MCVersion.v1_16;
    Util util;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        singleton.setBiomesGridPane(biomesGrid);
        singleton.setConsole(console);
        singleton.setMinecraftVersion(defaultVersion);
        singleton.setCRejSeed(cRejSeedCount);
        singleton.setTRejSeed(tRejSeedCount);
        singleton.setSeedCount(seedsToFind);
        singleton.setSequenceSeed(sequencedSeed);
        singleton.setStructureGridPane(structuresGrid);
        singleton.setBiomeSetsGridPane(biomeSetsGrid);
        singleton.setAutoSave(autoSaveConsole);
        singleton.setController(this);
        singleton.setWorldType(worldType);
        singleton.setAmountOfCores(amountOfCores);
        singleton.setCoresAmount(coresAmount);

        amountOfCores.setMax(Runtime.getRuntime().availableProcessors());
        coresAmount.textProperty().bind(
                Bindings.format(
                        "%.0f",
                        amountOfCores.valueProperty()
                )
        );

//        util = new Util();
//        guiCollector = new guiCollector();
        startBtn.setOnAction(buttonHandler);
        pauseBtn.setOnAction(buttonHandler);
        clearBtn.setOnAction(buttonHandler);
        bedrockMode.setOnAction(buttonHandler);
        randomSeed.setOnAction(buttonHandler);
        devMode.setOnAction(buttonHandler);
        mcVersions.setOnAction(buttonHandler);
        directoryBrowser.setOnAction(buttonHandler);
        saveConsole.setOnAction(buttonHandler);


        ArrayList<String> versions = new ArrayList<>();
        for(MCVersion v : MCVersion.values()){
            if(v.release > 12){
                versions.add(v.name);
            }
        }
        mcVersions.setItems(FXCollections
                .observableArrayList(versions));
        mcVersions.setValue(defaultVersion.name);

        //worldType.setItems(FXCollections.observableArrayList(worldTypes));
        singleton.getWorldType().setValue("DEFAULT");

        util = new Util();

        rebuildUI(defaultVersion);
    }

    EventHandler<ActionEvent> buttonHandler = new EventHandler<javafx.event.ActionEvent>() {
        @Override
        public void handle(javafx.event.ActionEvent e) {
            if (e.getSource() == devMode) {
//                Main.DEV_MODE = !Main.DEV_MODE;

            } else if (e.getSource() == randomSeed) {
                if(randomSeed.isSelected()){
                    randomSeedPane.setVisible(false);
                } else {
                    randomSeedPane.setVisible(true);
                }
                //RANDOM_SEEDS = !RANDOM_SEEDS;
            } else if (e.getSource() == bedrockMode){
                if(bedrockMode.isSelected()){
                    //BEDROCK = true;
                    bedrockWarning.setVisible(true);
                    structuresTab.setDisable(true);
                    singleton.getWorldType().setValue("DEFAULT");
                    worldTypePane.setDisable(true);
                } else {
                   // BEDROCK = false;
                    bedrockWarning.setVisible(false);
                    structuresTab.setDisable(false);
                    worldTypePane.setDisable(false);
                }
            } else if (e.getSource() == startBtn) {

                //GuiCollector.getBiomesFromUI(biomesGrid, "Include");
                GuiCollector.getStructures(structuresGrid, "Include");
                GuiCollector.getCategoryFromUI(biomeSetsGrid, "Include");
                try {
                    toggleRunning();
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            } else if (e.getSource() == pauseBtn) {
                togglePause();
            } else if (e.getSource() == clearBtn) {
                try {
                    reset();
                } catch (InterruptedException | IOException e1) {
                    e1.printStackTrace();
                }
            } else if (e.getSource() == mcVersions) {
                String selected = mcVersions.getSelectionModel().getSelectedItem();
                MCVersion version = MCVersion.fromString(selected);
                Singleton.getInstance().setMinecraftVersion(version);
                System.out.println("Version: "+selected+":"+mcVersions.getSelectionModel().getSelectedIndex());
                rebuildUI(version);

            } else if(e.getSource() == directoryBrowser){
                util.chooseDirectory(outputFileText);
            } else if(e.getSource() == saveConsole){
                util.appendToFile(Singleton.getInstance().getOutputFile(), console.getText());
            }
        }

    };
    public static final Village VILLAGE = new Village(MCVersion.v1_15);
    public static final BuriedTreasure BURIED_TREASURE = new BuriedTreasure(MCVersion.v1_15);
    public static final Igloo IGLOO = new Igloo(MCVersion.v1_15);
    public static final PillagerOutpost PILLAGER_OUTPOST = new PillagerOutpost(MCVersion.v1_15);
    public static final DesertPyramid DESERT_PYRAMID = new DesertPyramid(MCVersion.v1_15);
    public static final JunglePyramid JUNGLE_PYRAMID = new JunglePyramid(MCVersion.v1_15);
    public static final OceanRuin OCEAN_RUINS = new OceanRuin(MCVersion.v1_15);
    public static final Mansion MANSION = new Mansion(MCVersion.v1_15);
    public static final Mineshaft MINESHAFT = new Mineshaft(MCVersion.v1_15, Mineshaft.Type.EITHER);
    public static final Monument MONUMENT = new Monument(MCVersion.v1_15);
    public static final Shipwreck SHIPWRECK = new Shipwreck(MCVersion.v1_15);
    public static final SwampHut SWAMP_HUT = new SwampHut(MCVersion.v1_15);

    public static final BastionRemnant BASTION_REMNANT = new BastionRemnant(MCVersion.v1_16);
    public static final EndCity END_CITY = new EndCity(MCVersion.v1_15);

    //Fortress doesnt use regions pre 1.16
    public static final Fortress FORTRESS = new Fortress(MCVersion.v1_16);
    public static final NetherFossil NETHER_FOSSIL = new NetherFossil(MCVersion.v1_16);
    public static final RuinedPortal RUINED_PORTAL = new RuinedPortal(MCVersion.v1_16);

    //TODO: Will add strongholds later
    //public static final Stronghold STRONGHOLD = new Stronghold(MCVersion.v1_15);

    public void startSeedSearcher() throws IOException {

        long worldSeed = 4320562085990449695L;
        int searchRadius = 1000;
        int incrementer = 50;

        long startTime = System.nanoTime();

        ArrayList<Biome> biomesToFind = new ArrayList<>();
        biomesToFind.add(Biome.DEEP_WARM_OCEAN);
//        biomesToFind.add(Biome.OCEAN);
//        biomesToFind.add(Biome.FOREST);
//        biomesToFind.add(Biome.FLOWER_FOREST);
        //biomesToFind.add(Biome.MUSHROOM_FIELDS);

        ArrayList<Biome.Category> cat = new ArrayList<>();
        cat.add(Biome.Category.FOREST);
        cat.add(Biome.Category.ICY);

//        boolean b = false;
//        int count = 0;
//        do {
//            b = biomeSearcher.findBiomeFromCategory(searchRadius, new Random().nextLong(), cat, "OVERWORLD", incrementer);
//            //System.out.println(biomesToFind.size());
//            //System.out.println(count++);
//        } while(!b);


        ArrayList<RegionStructure<?, ?>> structuresToFind = new ArrayList<>();
        structuresToFind.add(VILLAGE);
        structuresToFind.add(MONUMENT);
        structuresToFind.add(DESERT_PYRAMID);
        structuresToFind.add(PILLAGER_OUTPOST);
//        structuresToFind.add(IGLOO);
        structuresToFind.add(SWAMP_HUT);
        structuresToFind.add(MANSION);

//        boolean b = false;
//        int count = 0;
//        do {
//            b = BiomeSearcher.findBiome(searchRadius, new Random().nextLong(), Biome.DEEP_WARM_OCEAN, "OVERWORLD", incrementer);
//            //System.out.println(biomesToFind.size());
//            System.out.println(count++);
//        } while(!b);

        //Searcher.searchRandomly(searchRadius, structuresToFind, biomesToFind, "OVERWORLD", incrementer, 16);
        //biomeSearcher.findBiome(searchRadius, worldSeed, Biome.PLAINS, "OVERWORLD", incrementer);
//
        //structureSearcher.findStructure(searchRadius, worldSeed, VILLAGE, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, MONUMENT, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, DESERT_PYRAMID, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, PILLAGER_OUTPOST, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, IGLOO, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, SWAMP_HUT, "OVERWORLD");
//        structureSearcher.findStructure(searchRadius, worldSeed, MANSION, "OVERWORLD");



        StructureSearcher.findStructureRandomly(searchRadius, structuresToFind, "OVERWORLD", 16);

        //structureSearcher.findStructure(searchRadius, worldSeed, FORTRESS, "NETHER");
        //structureSearcher.findStructure(searchRadius, worldSeed, END_CITY, "END");
        //structureSearcher.findMineshaft(searchRadius, worldSeed, MINESHAFT);
        long elapsedTime = System.nanoTime() - startTime;

        System.out.println(elapsedTime/1000000 + "/ms");


        //structureSearcher.findMineshaft(1024, 4320562085990449695L, MCVersion.v1_15, Mineshaft.Type.EITHER);
        updateDisplay();
        util.console("Welcome to SeedTool!");
        util.console("Please select at least one biome before searching!");
    }

    void createNewThreads() throws IOException {
        ArrayList<StructureProvider> structuresIN = GuiCollector.getStructures(structuresGrid, "Include");
        ArrayList<StructureProvider> structuresOUT = GuiCollector.getStructures(structuresGrid, "Exclude");
        ArrayList<Biome> biomesIN = GuiCollector.getBiomesFromUI(biomesGrid, "Include");
        ArrayList<Biome> biomesOUT = GuiCollector.getBiomesFromUI(biomesGrid, "Exclude");
        ArrayList<Biome.Category> categoriesIN = GuiCollector.getCategoryFromUI(biomeSetsGrid, "Include");
        ArrayList<Biome.Category> categoriesOUT = GuiCollector.getCategoryFromUI(biomeSetsGrid, "Exclude");
        Thread searchingT = new SearchingThread(structuresIN, structuresOUT, biomesIN, biomesOUT, categoriesIN, categoriesOUT);

        System.out.println(coresAmount.getText());
        //return r;
    }

    private void initTimer() {

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDisplay();
            }
        },DELAY,1);
    }

    private void updateDisplay() {
        Platform.runLater(() -> {
            if (!paused && running) {
                timeElapsed.setText(util.getElapsedTimeHoursMinutesFromMilliseconds(System.currentTimeMillis() - elapsedTime));
                notificationLabel.setText("Running");
            } else if (paused) {
                notificationLabel.setText("Paused");
            }
        });
    }

    private void toggleRunning() throws InterruptedException, IOException {
        allowThreadToSearch = true;
        if (running) {
            System.out.println("Shutting Down...");
            stop();
        } else {
            if (allowThreadToSearch) {
                start();
            } else {
                stop();
            }
        }

    }

    public boolean isRunning(){
        return running;
    }

    private void start() throws IOException {
        startBtn.setText("Stop");
        searchRadius.setEditable(false);
        seedsToFind.setEditable(false);
        startTime = System.currentTimeMillis();
        elapsedTime = System.currentTimeMillis();
        running = true;
        initTimer();
        createNewThreads();
        //t = new Thread(createNewThread());
//        for(int i = 0; i < singleton.getAmountOfCores().getValue(); i++) {
//            Thread t = new SearchingThread();
//            t.start();
//        }
//        t.start();
//        t2 = new Thread(createNewThread());
//        t2.start();
    }

    public void stop() throws InterruptedException, IOException{
        searchRadius.setEditable(true);
        seedsToFind.setEditable(true);
        startBtn.setText("Start");
        pauseBtn.setText("Pause");
        running = false;
        notificationLabel.setText("Stopped");
        sequencedSeed.setText("0");
        if(timer != null)
            timer.cancel();
        //if (t != null) t.interrupt();
    }

    private void togglePause() {
        if (!running) {
            util.console("Cannot pause when you aren't running!");
        } else {
            paused = !paused;
            String text = (paused) ? "Paused" : "Pause";

            if (paused) {
                pausedTime = System.currentTimeMillis();
                timer.cancel();
            } else {
                elapsedTime += System.currentTimeMillis() - pausedTime;
                initTimer();

                //startTime = timeAtPause;
            }
            pauseBtn.setText(text);
            updateDisplay();
        }
    }

    public boolean isPaused(){
        return paused;
    }
    private void reset() throws InterruptedException, IOException {
        if (paused) {
            togglePause();
        }
        stop();
        util.consoleWipe();
        timeElapsed.setText("00:00:00");
        startTime = System.currentTimeMillis();
        pausedTime = 0;
        elapsedTime = System.currentTimeMillis();
        cRejSeedCount.setText("0");
        tRejSeedCount.setText("0");
        notificationLabel.setText("Offline");

        updateDisplay();
    }

    private ArrayList<String> generateBiomesUI(MCVersion version){

        ArrayList<String> validBiomes = new ArrayList<>();

        Iterator regIt = Biome.REGISTRY.entrySet().iterator();
        while(regIt.hasNext()){
            Map.Entry mapElement = (Map.Entry)regIt.next();
            Biome b = (Biome) mapElement.getValue();
            if(b.getVersion().release <= version.release){
                validBiomes.add(b.getName());
            }
        }
        return validBiomes;
    }

    private ArrayList<String> generateCategoryUI(){

        ArrayList<String> validCategory = new ArrayList<>();

       for(Biome.Category c : Biome.Category.values()){
           validCategory.add(c.getName());
       }
        return validCategory;
    }

    private ArrayList<String> generateStructuresUI(MCVersion version){

        ArrayList<String> validStructures = new ArrayList<>();

        Iterator<Map.Entry<String, StructureProvider>> it = Structures.STRUCTURE.entrySet().iterator();

        while(it.hasNext()) {
            Map.Entry<String, StructureProvider> e = it.next();
            String name = e.getKey();
            StructureProvider struct = e.getValue();
            if(struct.getVersion().release <= version.release) {
                validStructures.add(name);
            }
        }
        return validStructures;
    }

    private void buildGridPane(GridPane grid, ArrayList<String> searchList, boolean textField){
        //ArrayList<String> searchingList = null;
           // searchingList = (ArrayList) util.createSearchLists(searchName);

        int k = 0;
        for (int i = 0; i < (searchList.size() / 3) + 1; i++) {
            for (int j = 0; j < 3; j++) {
                if (k < searchList.size()) {
                    VBox tempGrid = new VBox();
                    GridPane.setHgrow(tempGrid, Priority.ALWAYS);
                    GridPane.setVgrow(tempGrid, Priority.ALWAYS);
                    tempGrid.setAlignment(Pos.CENTER);
                    tempGrid.setSpacing(5);
                    grid.add(tempGrid, j, i);

                    Text tempText = new Text(searchList.get(k));
                    ComboBox<String> temp = new ComboBox<String>(FXCollections
                            .observableArrayList(include_exclude_txt));
                    tempGrid.getChildren().add(tempText);
                    tempGrid.getChildren().add(temp);
                    if(textField == true) {

                        TextField tempField = new TextField();
                        tempField.setMaxWidth(50);
                        tempField.setTooltip(new Tooltip("How many structures do you want to have? (Default if blank is 1 and its a minimum value)"));
                        tempGrid.getChildren().add(tempField);
                    }
                    k++;
                } else {
                    Pane empty = new Pane();
                    empty.setVisible(false);
                    grid.add(empty, j, i + 1);
                }
            }
        }
    }

    private void clearGridPane(GridPane pane){
        pane.getChildren().clear();
        pane.getColumnConstraints().clear();
        pane.getRowConstraints().clear();
    }

    private void rebuildUI(MCVersion version){
        clearGridPane(biomesGrid);
        clearGridPane(structuresGrid);
        clearGridPane(biomeSetsGrid);
        buildGridPane(biomesGrid, generateBiomesUI(version), false);
        buildGridPane(structuresGrid, generateStructuresUI(version),true);
        buildGridPane(biomeSetsGrid, generateCategoryUI(),false);
    }

    public void donate(){
        // util.openWebPage("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=W9E3YQAKQWC34&currency_code=CAD&source=url");
    }
}
