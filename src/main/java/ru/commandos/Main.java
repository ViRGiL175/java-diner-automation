package ru.commandos;

import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.tinylog.Logger;
import ru.virgil.OuterWorld;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    private static Terminal terminal;
    private static Screen screen;
    private static MultiWindowTextGUI multiWindowTextGUI;
    private static BasicWindow upWindow;
    private static BasicWindow downWindow;
    private static BasicWindow loadScreenWindow;
    private static BasicWindow economicsWindow;
    private static BasicWindow feedbackWindow;
    private static final Calendar calendar = new GregorianCalendar();
    private static ArrayList<Label> loadingLabel;
    private static Label loading;
    private static Panel economicPanel;
    private static Panel feedbackPanel;
    private static Label budget;
    private static Label economicBudget;
    private static Label feedbackBudget;
    public static Panel driveThruPanelList;
    public static Panel counterPanelList;
    public static Panel canteenPanelList;
    public static Panel kitchenPanelList;
    public static Panel restRoomPanelList;
    public static Label barmenPlace;
    public static Label cleanerPlace;
    public static Label date;
    public static Button economics;
    public static Button feedback;
    public static ArrayList<Label> cookPlaces;
    public static ArrayList<Label> feedbackLabels;
    public static ArrayList<Label> economicLabels;
    public static ArrayList<Label> driveThruPlaces;
    public static ArrayList<Label> counterPlaces;
    public static ArrayList<Label> canteenPlaces;
    public static ArrayList<Label> kitchenPlaces;
    public static ArrayList<Label> restRoomPlaces;

    public static void main(String[] args) throws IOException {

        Diner.slowdown = 300;

        Observable.just(1).subscribeOn(Schedulers.newThread()).subscribe(v -> {

            OuterWorld outerWorld = OuterWorld.singleton(20 * Diner.slowdown, TimeUnit.MILLISECONDS);

            Diner diner = new Diner(outerWorld.getClientsSource(), outerWorld.getAutoClientsSource(), outerWorld.getDateSource());

            outerWorld.run();
        });

        loadConfig();
        loadLoadingScreen();

        screen.startScreen();
        multiWindowTextGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(new TextColor.RGB(223, 196, 104)));
        Observable.interval(90 * Diner.slowdown, TimeUnit.MICROSECONDS).takeWhile(s -> loadingLabel.get(0).getText().length() > 0).subscribe(v -> {
            for (Label label : loadingLabel) {
                label.setText(new StringBuilder(label.getText()).deleteCharAt(0).toString());
            }
            multiWindowTextGUI.updateScreen();
            if (loadingLabel.get(0).getText().length() == 1) {
                loading.setText("Starting...");
                Observable.timer(2000, TimeUnit.MILLISECONDS).subscribe(q -> {
                    multiWindowTextGUI.removeWindow(loadScreenWindow);
                    multiWindowTextGUI.updateScreen();
                });
            }
        });
        Observable.interval(700, TimeUnit.MILLISECONDS).takeWhile(s -> loadingLabel.get(0).getText().length() > 0).subscribe(v -> {
            loading();
        });
        multiWindowTextGUI.addWindowAndWait(loadScreenWindow);
        multiWindowTextGUI.addWindow(downWindow);
        multiWindowTextGUI.addWindowAndWait(upWindow);
    }

    private static void loading(){
        loading.setText(loading.getText() + ".");
        if (loading.getText().length() > 10 && !loading.getText().equals("Starting...")) {
            loading.setText(new StringBuilder(loading.getText()).delete(7, 11).toString());
        }
    }

    private static void loadLoadingScreen() throws IOException {

        Panel loadScreenPanel = new Panel();
        loadScreenPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        loadScreenPanel.addComponent(new EmptySpace(new TerminalSize(0, terminal.getTerminalSize().getRows() / 4 + 1)));

        loadingLabel = new ArrayList<>();
        loadingLabel.add(new Label("                                                                            ██████╗ ██╗███╗   ██╗███████╗██████╗ ██╗███╗   ██╗████████╗███████╗██████╗     ██████╗ ██████╗ ███╗   ███╗").addTo(loadScreenPanel));
        loadingLabel.add(new Label("                                                                            ██╔══██╗██║████╗  ██║██╔════╝██╔══██╗██║████╗  ██║╚══██╔══╝██╔════╝██╔══██╗   ██╔════╝██╔═══██╗████╗ ████║").addTo(loadScreenPanel));
        loadingLabel.add(new Label("                                                                            ██║  ██║██║██╔██╗ ██║█████╗  ██████╔╝██║██╔██╗ ██║   ██║   █████╗  ██████╔╝   ██║     ██║   ██║██╔████╔██║").addTo(loadScreenPanel));
        loadingLabel.add(new Label("                                                                            ██║  ██║██║██║╚██╗██║██╔══╝  ██╔══██╗██║██║╚██╗██║   ██║   ██╔══╝  ██╔══██╗   ██║     ██║   ██║██║╚██╔╝██║").addTo(loadScreenPanel));
        loadingLabel.add(new Label("                                                                            ██████╔╝██║██║ ╚████║███████╗██║  ██║██║██║ ╚████║   ██║   ███████╗██║  ██║██╗╚██████╗╚██████╔╝██║ ╚═╝ ██║").addTo(loadScreenPanel));
        loadingLabel.add(new Label("                                                                            ╚═════╝ ╚═╝╚═╝  ╚═══╝╚══════╝╚═╝  ╚═╝╚═╝╚═╝  ╚═══╝   ╚═╝   ╚══════╝╚═╝  ╚═╝╚═╝ ╚═════╝ ╚═════╝ ╚═╝     ╚═╝").addTo(loadScreenPanel));

        loadScreenPanel.addComponent(new EmptySpace(new TerminalSize(0, 3)));
        Panel loadingPanel = new Panel().addTo(loadScreenPanel);
        loadingPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        loadingPanel.addComponent(new EmptySpace(new TerminalSize(terminal.getTerminalSize().getColumns() / 2 - 7, 1)));
        loading = new Label("Loading").addTo(loadingPanel);

        loadScreenWindow = new BasicWindow();
        loadScreenWindow.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        loadScreenWindow.setComponent(loadScreenPanel);
        loadScreenWindow.setTheme(new SimpleTheme(new TextColor.RGB(188, 111, 95), new TextColor.RGB(223, 196, 104), SGR.BOLD));
    }

    private static void onPressEconomics() {

        try {
            multiWindowTextGUI.addWindow(economicsWindow);
            multiWindowTextGUI.updateScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void onPressFeedback() {

        try {
            multiWindowTextGUI.addWindow(feedbackWindow);
            multiWindowTextGUI.updateScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void onPressBack() {

        try {
            if(multiWindowTextGUI.getWindows().contains(economicsWindow)) {
                multiWindowTextGUI.removeWindow(economicsWindow);
            }
            else {
                multiWindowTextGUI.removeWindow(feedbackWindow);
            }
            multiWindowTextGUI.updateScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadConfig() throws IOException {

        terminal = new DefaultTerminalFactory().setInitialTerminalSize(new TerminalSize(80, 24)).createTerminal();

        screen = new TerminalScreen(terminal);

        Panel mainPanel = new Panel();
        mainPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

        Panel datePanel = new Panel().addTo(mainPanel);
        datePanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        String s = calendar.get(Calendar.DAY_OF_MONTH) + " " + (calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en"))) + " " + calendar.get(Calendar.YEAR);
        date = new Label(s);
        datePanel.addComponent(new EmptySpace(new TerminalSize(30, 1)));
        datePanel.addComponent(date);

        Panel buttonPanel = new Panel().addTo(mainPanel);
        buttonPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        economics = new Button("Economics", Main::onPressEconomics);
        buttonPanel.addComponent(economics);
        buttonPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
        feedback = new Button("Feedback", Main::onPressFeedback);
        buttonPanel.addComponent(feedback);
        buttonPanel.addComponent(new EmptySpace(new TerminalSize(27, 1)));
        budget = new Label("Budget: $1000");
        buttonPanel.addComponent(budget);

        mainPanel.addComponent(new EmptySpace());

        Panel dinerPanel = new Panel().addTo(mainPanel);
        dinerPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        driveThruPanelList = new Panel();
        dinerPanel.addComponent(driveThruPanelList.withBorder(Borders.singleLine("D-Thru")));
        driveThruPanelList.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        driveThruPlaces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            driveThruPlaces.add(new Label((i + 1) + ".        "));
            driveThruPanelList.addComponent(driveThruPlaces.get(i));
        }
        cleanerPlace = new Label("         ").addTo(driveThruPanelList);

        counterPanelList = new Panel();
        dinerPanel.addComponent(counterPanelList.withBorder(Borders.singleLine("Counter")));
        counterPanelList.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        barmenPlace = new Label("Barmen     ").addTo(counterPanelList);
        counterPlaces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            counterPlaces.add(new Label((i + 1) + ".        "));
            counterPanelList.addComponent(counterPlaces.get(i));
        }

        canteenPanelList = new Panel();
        dinerPanel.addComponent(canteenPanelList.withBorder(Borders.singleLine("Canteen")));
        canteenPanelList.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        Panel canteenPanelListFirst = new Panel().addTo(canteenPanelList);
        canteenPanelListFirst.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        canteenPlaces = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            canteenPlaces.add(new Label((i + 1) + ".         "));
            canteenPanelListFirst.addComponent(canteenPlaces.get(i));
        }
        Panel canteenPanelListSecond = new Panel().addTo(canteenPanelList);
        for (int i = 5; i < 9; i++) {
            canteenPlaces.add(new Label(" " + (i + 1) + ".         "));
            canteenPanelListSecond.addComponent(canteenPlaces.get(i));
        }
        canteenPlaces.add(new Label(10 + ".         "));
        canteenPanelListSecond.addComponent(canteenPlaces.get(9));

        kitchenPanelList = new Panel();
        dinerPanel.addComponent(kitchenPanelList.withBorder(Borders.singleLine("Kitchen")));
        kitchenPanelList.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        cookPlaces = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            cookPlaces.add(new Label("Cook   "));
            kitchenPanelList.addComponent(cookPlaces.get(i));
        }
        kitchenPlaces = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            kitchenPlaces.add(new Label("       "));
            kitchenPanelList.addComponent(kitchenPlaces.get(i));
        }

        restRoomPanelList = new Panel();
        dinerPanel.addComponent(restRoomPanelList.withBorder(Borders.singleLine("Restroom")));
        restRoomPanelList.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        restRoomPlaces = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            restRoomPlaces.add(new Label((i + 1) + ".        "));
            restRoomPanelList.addComponent(restRoomPlaces.get(i));
        }

        Panel legendPanel = new Panel().addTo(mainPanel);
        legendPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        Label legendLabel = new Label("Legend: C - cooking, W - waiting, O - ordering").addTo(legendPanel);
        legendPanel.addComponent(new EmptySpace(new TerminalSize(16,1)));
        Label link = new Label("DinerInter.com").addTo(legendPanel);

        upWindow = new BasicWindow();
        upWindow.setPosition(new TerminalPosition(0, 0));
        upWindow.setSize(new TerminalSize(terminal.getTerminalSize().getColumns() - 2, terminal.getTerminalSize().getRows() / 2 - 1));
        upWindow.setHints(Arrays.asList(Window.Hint.FIXED_POSITION, Window.Hint.FIXED_SIZE));
        upWindow.setComponent(mainPanel);
        upWindow.setTheme(new SimpleTheme(new TextColor.RGB(188, 111, 95), new TextColor.RGB(223, 196, 104), SGR.BOLD));

        Panel cmd = new Panel();
        cmd.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        Label output = new Label("").addTo(cmd);

        downWindow = new BasicWindow();
        downWindow.setPosition(new TerminalPosition(0, terminal.getTerminalSize().getRows() / 2 + 1));
        downWindow.setSize(new TerminalSize(terminal.getTerminalSize().getColumns() - 2, terminal.getTerminalSize().getRows() / 2 - 3));
        downWindow.setHints(Arrays.asList(Window.Hint.FIXED_POSITION, Window.Hint.FIXED_SIZE));
        downWindow.setComponent(cmd);
        downWindow.setTheme(new SimpleTheme(new TextColor.RGB(188, 111, 95), new TextColor.RGB(51, 49, 49), SGR.BOLD));

        economicPanel = new Panel();
        economicPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        Panel economicDatePanel = new Panel().addTo(economicPanel);
        economicDatePanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        economicDatePanel.addComponent(new EmptySpace(new TerminalSize(30, 1)));
        economicDatePanel.addComponent(new Label(s));
        Panel economicButtonPanel = new Panel().addTo(economicPanel);
        economicButtonPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        Button economicBack = new Button("Back", Main::onPressBack).addTo(economicButtonPanel);
        economicButtonPanel.addComponent(new EmptySpace(new TerminalSize(43, 1)));
        economicBudget = new Label("Budget: $1000").addTo(economicButtonPanel);
        economicLabels = new ArrayList<>();

        economicsWindow = new BasicWindow();
        economicsWindow.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        economicsWindow.setComponent(economicPanel);
        economicsWindow.setTheme(new SimpleTheme(new TextColor.RGB(188, 111, 95), new TextColor.RGB(223, 196, 104), SGR.BOLD));

        feedbackPanel = new Panel();
        feedbackPanel.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        Panel feedbakDatePanel = new Panel().addTo(feedbackPanel);
        feedbakDatePanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        feedbakDatePanel.addComponent(new EmptySpace(new TerminalSize(30, 1)));
        feedbakDatePanel.addComponent(new Label(s));
        Panel feedbackButtonPanel = new Panel().addTo(feedbackPanel);
        feedbackButtonPanel.setLayoutManager(new LinearLayout(Direction.HORIZONTAL));
        Button feedBack = new Button("Back", Main::onPressBack).addTo(feedbackButtonPanel);
        feedbackButtonPanel.addComponent(new EmptySpace(new TerminalSize(43, 1)));
        feedbackBudget = new Label("Budget: $1000").addTo(feedbackButtonPanel);
        feedbackLabels = new ArrayList<>();

        feedbackWindow = new BasicWindow();
        feedbackWindow.setHints(Arrays.asList(Window.Hint.FULL_SCREEN));
        feedbackWindow.setComponent(feedbackPanel);
        feedbackWindow.setTheme(new SimpleTheme(new TextColor.RGB(188, 111, 95), new TextColor.RGB(223, 196, 104), SGR.BOLD));
    }

    public static void addToEconomicLabels(String s) {
        Label label = new Label(s);
        economicLabels.add(label);
        if (economicLabels.size() == 1) {
            economicPanel.addComponent(label);
        }
        else {
            economicPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
            economicPanel.addComponent(label);
        }
    }

    public static void addToFeedbackLabels(String s) {
        Label label = new Label(s);
        feedbackLabels.add(label);
        if (feedbackLabels.size() == 1) {
            feedbackPanel.addComponent(label);
        }
        else {
            feedbackPanel.addComponent(new EmptySpace(new TerminalSize(1, 1)));
            feedbackPanel.addComponent(label);
        }
    }

    public static void updateScreen() {
        try {
            multiWindowTextGUI.updateScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateDate(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        Main.date.setText(calendar.get(Calendar.DAY_OF_MONTH) + " " + (calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("en"))) + " " + calendar.get(Calendar.YEAR));
        updateScreen();
    }

    public static void updateBudget(Double money, Double dinamic) {
        if (dinamic == null) {
            budget.setText("Budget: $" + String.format("%.2f", money));
            economicBudget.setText(budget.getText());
            feedbackBudget.setText(budget.getText());
        }
        else {
            budget.setText("Budget: $" + String.format("%.2f", money) + ", $" + String.format("%.2f", dinamic) + "/mo.");
            economicBudget.setText(budget.getText());
            feedbackBudget.setText(budget.getText());
        }
        updateScreen();
    }
}
