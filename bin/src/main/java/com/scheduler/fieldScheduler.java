package com.scheduler;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.*;

import java.time.*;
import java.time.temporal.*;
import java.time.format.DateTimeFormatter;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class fieldScheduler extends JPanel {
    
    //Variable declarations 
    int weekIndex = 0;
    JSplitPane splitPane;
    ListSelectionModel teamListSelectionModel,fieldListSelectionModel,
                        dayListSelectionModel,timeListSelectionModel;
    GridBagConstraints gbc = new GridBagConstraints();
    mongoDB mongo;

    //Declare actionable items
    private JButton btnAdd  = new JButton("Add");
    private JButton btnUpdate = new JButton("Update");
    private JButton btnDelete = new JButton("Delete");
    private JButton btnImport = new JButton("Import");
    private JButton btnExit = new JButton("Exit");

    private JLabel lblA = new JLabel("Team:");
    private JLabel lblB = new JLabel("Field:");
    private JLabel lblC = new JLabel("Date:");
    private JLabel lblD = new JLabel("Time:");

    private JTextField txtA = new JTextField();
    private JTextField txtB = new JTextField();
    private JTextField txtC = new JTextField();
    private JTextField txtD = new JTextField();

    private JButton btnPrev  = new JButton("<< Previous");
    private JLabel lblWeek = new JLabel("",SwingConstants.CENTER ); 
    private JButton btnNext = new JButton("Next >>");


    public fieldScheduler(JFrame frame) {
        super(new BorderLayout());  

        //Set up database
        initDatabase();     
        
        //Set up layout and set content
        setOpaque(true);
        JSplitPane splitPane = initLayout();
        frame.setContentPane(splitPane);

        //Set event listeners
        initEvent(frame);

    }
 
    public void resetLayout(JFrame frame) {

        //Reseet layout and set content
        JSplitPane splitPane = initLayout();
        frame.setContentPane(splitPane);
        frame.validate();
        frame.repaint();

    }

    /*
     * Create the GUI and show it
     */
    private static void createAndShowGUI() {

        //Create and set up the window.
        JFrame frame = new JFrame("Oakridge Field Scheduler");
        frame.setMinimumSize(new Dimension(400, 200));
        frame.setPreferredSize(new Dimension(1200, 600));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        fieldScheduler runScheduler = new fieldScheduler(frame);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    public void initDatabase(){

        //Create connection to mongoDB
        mongo = new mongoDB();
        mongo.setConnection("test", "Employee");
/*

TO DO:


>>jscrollpanes to stretch over empty space
>>unit tests

   
*/
    }


    public JSplitPane initLayout(){

        //-------CREATE THE LAYOUT-------//

        //First split the layour into two
        //Options on top and calendar on the bottom
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(splitPane);
 
        //Design top half first
        JPanel topHalf = new JPanel(new GridBagLayout());
        topHalf.setBorder(BorderFactory.createTitledBorder( "Scheduling Options:"));
        topHalf.setPreferredSize(new Dimension(300, 100));

        //------Make input container
        JPanel inputContainer = new JPanel();
        inputContainer.setLayout(new GridLayout(1,2));

            JPanel textContainer = new JPanel(new GridBagLayout());
            textContainer.setBorder(BorderFactory.createTitledBorder( "Options:"));
            //textContainer.setLayout(new GridLayout(3,2));
            gbc = getGbc(0, 0, 0.05, 1);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            textContainer.add(lblA,gbc);
            gbc = getGbc(1, 0, 0.95, 1);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            textContainer.add(txtA,gbc);
            gbc = getGbc(0, 1, 0.05, 1); 
            gbc.fill = GridBagConstraints.HORIZONTAL;
            textContainer.add(lblB,gbc);
            gbc = getGbc(1, 1, 0.95, 1); 
            gbc.fill = GridBagConstraints.HORIZONTAL;
            textContainer.add(txtB,gbc);
            gbc = getGbc(0, 2, 0.05, 1); 
            gbc.fill = GridBagConstraints.HORIZONTAL;
            textContainer.add(lblC,gbc);
            gbc = getGbc(1, 2, 0.95, 1); 
            gbc.fill = GridBagConstraints.HORIZONTAL;
            textContainer.add(txtC,gbc);
            gbc = getGbc(0, 3, 0.05, 1); 
            gbc.fill = GridBagConstraints.HORIZONTAL;
            textContainer.add(lblD,gbc);
            gbc = getGbc(1, 3, 0.95, 1); 
            gbc.fill = GridBagConstraints.HORIZONTAL;
            textContainer.add(txtD,gbc);

            JPanel buttonContainer = new JPanel();
            buttonContainer.setLayout(new GridLayout(5,1));
            buttonContainer.setBorder(BorderFactory.createTitledBorder( "Actions:"));
            buttonContainer.add(btnAdd);
            buttonContainer.add(btnUpdate);
            buttonContainer.add(btnDelete);
            buttonContainer.add(btnImport);
            buttonContainer.add(btnExit);

        //Add to both inputContainer
        inputContainer.add(textContainer);
        inputContainer.add(buttonContainer);

        gbc = getGbc(0, 0, 0.25, 0.15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topHalf.add(inputContainer,gbc);
        //------

        //------Make list panes
        JPanel listContainer = new JPanel();
        List allPanes = createListPanes();
        //listContainer.setLayout(new GridBagLayout());
        //listContainer.setLayout(new GridLayout(1,allPanes.size()));
        for (int i = 0; i < allPanes.size(); i++){
            gbc = getGbc(i, 0, 1, 1);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            listContainer.add((JScrollPane)allPanes.get(i),gbc);
        }

        gbc = getGbc(0, 1, 0.25, 0.85);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        topHalf.add(listContainer,gbc);
        splitPane.add(topHalf);
        //------
        



        //------
        //Design bottom half 
        JPanel bottomHalf = new JPanel();
        bottomHalf.setLayout(new GridBagLayout());
        //bottomHalf.setLayout(new BoxLayout(bottomHalf, BoxLayout.LINE_AXIS));
        bottomHalf.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        bottomHalf.setPreferredSize(new Dimension(500, 100));


        //------Make arrow panel
        JPanel arrowContainer = new JPanel();
        arrowContainer.setLayout(new GridBagLayout());
        lblWeek.setText(new ThisLocalizedWeek(Locale.US).getFirstDay().plusWeeks(weekIndex) +
                " --- to --- " + new ThisLocalizedWeek(Locale.US).getLastDay().plusWeeks(weekIndex) );
        lblWeek.setFont(lblWeek.getFont().deriveFont (14.0f));
        gbc = getGbc(0, 0, 0.025, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        arrowContainer.add(btnPrev,gbc);
        gbc = getGbc(1, 0, 0.95, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        arrowContainer.add(lblWeek,gbc);
        gbc = getGbc(2, 0, 0.025, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        arrowContainer.add(btnNext,gbc);

        //------Make Calendar panel
        JPanel calendarContainer = new JPanel();
        calendarContainer.setLayout(new GridLayout(1,7));

        // Define the lists of data for each day
        String[] dayTitles = { "Sunday", "Monday", "Tuesday", "Wednesday", 
                            "Thursday", "Friday", "Saturday" };
        ThisLocalizedWeek week = new ThisLocalizedWeek(Locale.US);
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<List<String>> gameLists = new ArrayList<List<String>>();
        for (int i = 0; i < dayTitles.length; i++){
            String textDate =  
                        week.getFirstDay().plusWeeks(weekIndex).plusDays(i).format(formatter);
            String[] gameDayList = getDayResult(textDate);
            List<String> gameDayArray = Arrays.asList(gameDayList);
            gameLists.add(gameDayArray);
        }
        
        // Add each day pane to the calendar container
        for (int i = 0; i < dayTitles.length; i++){
            String textDate = 
                        week.getFirstDay().plusWeeks(weekIndex).plusDays(i).format(formatter);
            JScrollPane jScrollPane = createScrollPane(gameLists.get(i), textDate );
            jScrollPane.setBorder(
                        BorderFactory.createTitledBorder( dayTitles[i]+ ": " + textDate ));
            calendarContainer.add(jScrollPane);
        }

        //Add components to bottom half
        gbc = getGbc(0, 0, 1, 0.1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomHalf.add(arrowContainer, gbc);
        gbc = getGbc(0, 1, 1, 0.9);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        bottomHalf.add(calendarContainer, gbc);
        //bottomHalf.add(calendarContainer, BorderLayout.CENTER);
        splitPane.add(bottomHalf);

        return splitPane;
    }


    public List createListPanes(){

        //-------Generate lists
        String[] teamListData = mongo.queryFieldAll(mongo.collection, "Team");
        Arrays.sort(teamListData);
        JList teamList = new JList(teamListData); 
        teamListSelectionModel = teamList.getSelectionModel();
        SharedListSelectionHandler listenTeam = new SharedListSelectionHandler();
        listenTeam.type = "Team";
        listenTeam.listdata = teamListData;
        teamListSelectionModel.addListSelectionListener(listenTeam);
        JScrollPane teamListPane = new JScrollPane(teamList);
        teamListPane.setPreferredSize(new Dimension(275, 125));
        teamListPane.setBorder(BorderFactory.createTitledBorder( "Teams:" ));
        teamListSelectionModel.setSelectionMode(
                        ListSelectionModel.SINGLE_SELECTION);

        String[] fieldListData = mongo.queryFieldAll(mongo.collection, "Field");
        Arrays.sort(fieldListData);
        JList fieldList = new JList(fieldListData);
        fieldListSelectionModel = fieldList.getSelectionModel();
        SharedListSelectionHandler listenField = new SharedListSelectionHandler();
        listenField.type = "Field";
        listenField.listdata = fieldListData;
        fieldListSelectionModel.addListSelectionListener(listenField);
        JScrollPane fieldListPane = new JScrollPane(fieldList);
        fieldListPane.setPreferredSize(new Dimension(275, 125));
        fieldListPane.setBorder(BorderFactory.createTitledBorder( "Fields:" ));
        fieldListSelectionModel.setSelectionMode(
                        ListSelectionModel.SINGLE_SELECTION);
        
        String[] dayListData = mongo.queryFieldAll(mongo.collection, "Date");
        Arrays.sort(dayListData);
        JList dayList = new JList(dayListData);
        dayListSelectionModel = dayList.getSelectionModel();
        SharedListSelectionHandler listenDate = new SharedListSelectionHandler();
        listenDate.type = "Date";
        listenDate.listdata = dayListData;
        dayListSelectionModel.addListSelectionListener(listenDate);
        JScrollPane dayListPane = new JScrollPane(dayList);
        dayListPane.setPreferredSize(new Dimension(275, 125));
        dayListPane.setBorder(BorderFactory.createTitledBorder( "Days:" ));
        dayListSelectionModel.setSelectionMode(
                        ListSelectionModel.SINGLE_SELECTION);

        String[] timeListData = mongo.queryFieldAll(mongo.collection, "Time");
        JList timeList = new JList(timeListData);
        Arrays.sort(timeListData);
        timeListSelectionModel = timeList.getSelectionModel();
        SharedListSelectionHandler listenTime = new SharedListSelectionHandler();
        listenTime.type = "Time";
        listenTime.listdata = timeListData;
        timeListSelectionModel.addListSelectionListener(listenTime);
        JScrollPane timeListPane = new JScrollPane(timeList);
        timeListPane.setPreferredSize(new Dimension(275, 125));
        timeListPane.setBorder(BorderFactory.createTitledBorder( "Times:" ));
        timeListSelectionModel.setSelectionMode(
                        ListSelectionModel.SINGLE_SELECTION);

        //Incorporate all list panes into one list
        List<JScrollPane> allPanes = new ArrayList<JScrollPane>();
        allPanes.add(teamListPane);
        allPanes.add(fieldListPane);
        allPanes.add(dayListPane);
        allPanes.add(timeListPane);

        return allPanes;
    }

    private JScrollPane createScrollPane(List list, String day){ 
        //Create scroll pane with selection listener
        String[] stringArray = (String[])list.toArray(new String[0]);
        JList listData = new JList(stringArray);   
        JScrollPane listPane = new JScrollPane(listData);
        listData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listData.addListSelectionListener(new ListSelectionListener() {                
                public void valueChanged(ListSelectionEvent e) { 

                    //Get selected index then split string of indexed array
                    int selectedIndex = listData.getMinSelectionIndex();
                    String[] splitStr = stringArray[selectedIndex].split("=>");

                    //Change text of corresponding field
                    txtA.setText( splitStr[2].replaceAll("(^ )|( $)", "") );
                    txtB.setText( splitStr[0].replaceAll("(^ )|( $)", "") );
                    txtC.setText( day );
                    txtD.setText( splitStr[1].replaceAll("(^ )|( $)", "") );

                    //listData.getSelectionModel().clearSelection();
                    listData.clearSelection();
                }
            });


        return listPane;
    }

    private String[] getDayResult(String date){

        //Search for date inputted and return results in string array
        List res = mongo.searchDate(mongo.collection, date);

        //Results store
        String[] data;

        //Number of results
        int numRes = res.size()/3;

        //To ensure empty result is taken into account
        if(res.isEmpty()){
            data = new String[1];
            data[0] = "";

        }
        //Otherwise fill out based on num of results
        else{
            data = new String[numRes];
            for (int i=0; i<numRes; i++){
                data[i] = res.get(3*i+1) + " => " + res.get(3*i+2) + " => " + res.get(3*i);
            }
        }

        return data;
    }




    //-------Initiate action listeners
    private void initEvent(JFrame frame){

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnAddClick(e, frame);
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnUpdateClick(e, frame);
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnDeleteClick(e, frame);
            }
        });

        btnImport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnImportClick(e, frame);
            }
        });

        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnExitClick(e);
            }
        });

        btnPrev.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnPrevClick(e, frame);
            }
        });

        btnNext.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnNextClick(e, frame);
            }
        });
    }



    private void btnAddClick(ActionEvent evt, JFrame frame){        

        if ( (txtA.getText().equals("") || txtB.getText().equals("") ||
                    txtC.getText().equals("") || txtD.getText().equals("") ) ){
            assert true;
        }
        else {
            mongo.addGame(mongo.collection, txtA.getText(), txtB.getText(), 
                        txtC.getText(), txtD.getText());
            resetLayout(frame);
        }
    }

    private void btnUpdateClick(ActionEvent evt, JFrame frame){     

        //Create new window for updating an entry
        JButton _btnUpdate = new JButton("Update");
        JButton _btnClose = new JButton("Close");

        JLabel _lblA = new JLabel("Team:");
        JLabel _lblB = new JLabel("Field:");
        JLabel _lblC = new JLabel("Date:");
        JLabel _lblD = new JLabel("Time:");

        JTextField _txtA = new JTextField(txtA.getText());
        JTextField _txtB = new JTextField(txtB.getText());
        JTextField _txtC = new JTextField(txtC.getText());
        JTextField _txtD = new JTextField(txtD.getText());

        //Create and set up the frame
        JFrame _frame = new JFrame("Update Game");


        //------Make input container
        JPanel _inputContainer = new JPanel();
        _inputContainer.setPreferredSize(new Dimension(350, 175));
        _inputContainer.setLayout(new GridBagLayout());

        JPanel _textContainer = new JPanel(new GridBagLayout());
        _textContainer.setBorder(BorderFactory.createTitledBorder( "Options:"));
        gbc = getGbc(0, 0, 0.05, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_lblA,gbc);
        gbc = getGbc(1, 0, 0.95, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_txtA,gbc);
        gbc = getGbc(0, 1, 0.05, 1); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_lblB,gbc);
        gbc = getGbc(1, 1, 0.95, 1); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_txtB,gbc);
        gbc = getGbc(0, 2, 0.05, 1); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_lblC,gbc);
        gbc = getGbc(1, 2, 0.95, 1); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_txtC,gbc);
        gbc = getGbc(0, 3, 0.05, 1); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_lblD,gbc);
        gbc = getGbc(1, 3, 0.95, 1); 
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_txtD,gbc);

        JPanel _buttonContainer = new JPanel();
        _buttonContainer.setBorder(BorderFactory.createTitledBorder( "Actions:"));
        _buttonContainer.add(_btnUpdate);
        _buttonContainer.add(_btnClose);

        //Add to both inputContainer
        gbc = getGbc(0, 0, 1, 0.6);
        //gbc.fill = GridBagConstraints.VERTICAL;
        _inputContainer.add(_textContainer,gbc);
        gbc = getGbc(0, 1, 1, 0.4);
        ///gbc.fill = GridBagConstraints.VERTICAL;
        _inputContainer.add(_buttonContainer,gbc);


        //Set and display the frame
        _frame.setContentPane(_inputContainer);
        _frame.pack();
        _frame.setVisible(true);


        //If update button pressed, add game to database then close frame
        _btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mongo.updateGame(mongo.collection, txtA.getText(), txtB.getText(), 
                            txtC.getText(), txtD.getText(), _txtA.getText(), _txtB.getText(), 
                            _txtC.getText(), _txtD.getText());
                _frame.setVisible(false); 
                _frame.dispose();
                resetLayout(frame);
            }
        });


        //If button pressed, add game to database then close frame
        _btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _frame.setVisible(false); 
                _frame.dispose();
            }
        });

    }

    private void btnDeleteClick(ActionEvent evt, JFrame frame){        
        mongo.removeGame(mongo.collection, txtA.getText(), txtB.getText(), 
                    txtC.getText(), txtD.getText());
        resetLayout(frame);
    }

    private void btnPrevClick(ActionEvent evt, JFrame frame){           
        weekIndex--;
        resetLayout(frame);
    }    

    private void btnNextClick(ActionEvent evt, JFrame frame){        
        weekIndex++;
        resetLayout(frame);
    }

    private void btnImportClick(ActionEvent evt, JFrame frame){  

        //Create new window for importing a file
        JButton _btnBrowse = new JButton("Browse");
        JButton _btnImport = new JButton("Import");
        JButton _btnClose = new JButton("Close");

        JLabel _lblA = new JLabel("Directory:");
        JLabel _lblB = new JLabel("File:");
        JTextField _txtA = new JTextField();
        JTextField _txtB = new JTextField();

        //Create and set up the frame
        JFrame _frame = new JFrame("Import Game File");

        //------Make input container
        JPanel _inputContainer = new JPanel();
        _inputContainer.setPreferredSize(new Dimension(350, 175));
        _inputContainer.setLayout(new GridBagLayout());

        JPanel _textContainer = new JPanel(new GridBagLayout());
        _textContainer.setBorder(BorderFactory.createTitledBorder( "Options:"));
        gbc = getGbc(0, 0, 0.05, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_lblA,gbc);
        gbc = getGbc(1, 0, 0.95, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_txtA,gbc);
        gbc = getGbc(0, 1, 0.05, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_lblB,gbc);
        gbc = getGbc(1, 1, 0.95, 1);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        _textContainer.add(_txtB,gbc);

        JPanel _buttonContainer = new JPanel();
        _buttonContainer.setBorder(BorderFactory.createTitledBorder( "Actions:"));
        _buttonContainer.add(_btnBrowse);
        _buttonContainer.add(_btnImport);
        _buttonContainer.add(_btnClose);

        //Add to both inputContainer
        gbc = getGbc(0, 0, 1, 0.6);
        _inputContainer.add(_textContainer,gbc);
        gbc = getGbc(0, 1, 1, 0.4);
        _inputContainer.add(_buttonContainer,gbc);

        //Set and display the frame
        _frame.setContentPane(_inputContainer);
        _frame.pack();
        _frame.setVisible(true);

        //If browse button pressed, search for fiel to import
        _btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser c = new JFileChooser();
                // Demonstrate "Open" dialog:
                int rVal = c.showOpenDialog(null);
                if (rVal == JFileChooser.APPROVE_OPTION) {
                    _txtA.setText(c.getCurrentDirectory().toString());
                    _txtB.setText(c.getSelectedFile().getName());
                }
                if (rVal == JFileChooser.CANCEL_OPTION) {
                    _txtA.setText("You pressed cancel");
                    _txtB.setText("");
                }
            }
        });

        //If import button pressed, read games and add game to database 
        //then close frame
        _btnImport.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                //File to import
                String filename = _txtA.getText() + "\\" + _txtB.getText();
                File fid = new File(filename);

                //Check if csv file
                if ( _txtB.getText().endsWith(".csv") && (fid.exists() && !fid.isDirectory()) ){


                    try{
                        //Scan file and split by newline
                        String[] vals;
                        Scanner scanner = new Scanner(fid);
                        scanner.useDelimiter("\n");
                        int cnt = 0;

                        //Go thru all scanned lines
                        while(scanner.hasNext()){
                            //Add lines split by commas to string array
                            vals = scanner.next().split(",");

                            //Skip first header line
                            if (cnt>0){                                
                                //Add vals as a game in database
                                mongo.addGame(mongo.collection, vals[0], vals[1],
                                                vals[2], vals[3]);
                            }
                            //Increase counter
                            cnt++;

                        }

                        //Close scanner
                        scanner.close();

                        //After adding all entries, close window and reset initial layout
                        _frame.setVisible(false); 
                        _frame.dispose();
                        resetLayout(frame);

                    }catch (FileNotFoundException except){

                        except.printStackTrace();
                    }

                }
                else{

                    //Create pop-up window saying incorrect file format
                    JLabel _lblA = new JLabel("Selected file is not a proper csv file...");

                    //Create and set up the frame
                    JFrame _frame = new JFrame("File Issue");

                    //------Make input container
                    JPanel _inputContainer = new JPanel();
                    _inputContainer.setPreferredSize(new Dimension(300, 75));
                    _inputContainer.setLayout(new GridBagLayout());
                    _inputContainer.add(_lblA);

                    //Set and display the frame
                    _frame.setContentPane(_inputContainer);
                    _frame.pack();
                    _frame.setVisible(true);

                }

            }
        });


        //If button pressed, add game to database then close frame
        _btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _frame.setVisible(false); 
                _frame.dispose();
            }
        });


    }

    private void btnExitClick(ActionEvent evt){
        System.exit(0);
    }

    class SharedListSelectionHandler implements ListSelectionListener {
        int selectedIndex;
        String type;
        String[] listdata;

        public void valueChanged(ListSelectionEvent e) { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            
            // Find out which indexes are selected.
            this.selectedIndex = lsm.getMinSelectionIndex();

            //Change text of corresponding field
            if (type=="Team"){
                txtA.setText(  listdata[this.selectedIndex] );
            }

            else if (type=="Field"){
                txtB.setText(  listdata[this.selectedIndex] );
            }

            else if (type=="Date"){
                txtC.setText(  listdata[this.selectedIndex] );
            }

            else if (type=="Time"){
                txtD.setText(  listdata[this.selectedIndex] );
            }

            lsm.clearSelection();
        }
    }

    private GridBagConstraints getGbc(int x, int y, double weightX, double weightY) {
        gbc.gridx = x;
        gbc.gridy = y;
        //gbc.gridheight = height;
        //gbc.gridwidth = width;
        gbc.weightx = weightX;
        gbc.weighty = weightY;

        return gbc;
    }

    public class ThisLocalizedWeek {
        // Try and always specify the time zone you're working with
        ZoneId TZ = ZoneId.of("Canada/Eastern");

        private Locale locale;
        private DayOfWeek firstDayOfWeek;
        private DayOfWeek lastDayOfWeek;

        public ThisLocalizedWeek(Locale locale) {
            this.locale = locale;
            this.firstDayOfWeek = WeekFields.of(locale).getFirstDayOfWeek();
            this.lastDayOfWeek = DayOfWeek.of(((this.firstDayOfWeek.getValue() + 5) % DayOfWeek.values().length) + 1);
        }
        public LocalDate getFirstDay() {
            return LocalDate.now(TZ).with(TemporalAdjusters.previousOrSame(this.firstDayOfWeek));
        }
        public LocalDate getLastDay() {
            return LocalDate.now(TZ).with(TemporalAdjusters.nextOrSame(this.lastDayOfWeek));
        }
    }

}