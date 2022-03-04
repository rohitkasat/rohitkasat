package com.kgetech.exceldump;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.StringJoiner;

import static com.kgetech.exceldump.Data.*;
import static com.kgetech.exceldump.First.main_panel;
import static java.lang.System.exit;


public class First extends JFrame {


    public static String getCSVString(ArrayList<String> list) throws IOException {

        // TODO - Add some validations here
        StringJoiner joiner = new StringJoiner(",");
        for (String item : list) joiner.add(item);
        String data = joiner.toString();
        data = data + "\n";
        return data;
    }

    public static String getCSVStringForMultipleData(ArrayList<ArrayList<String>> listOfList) throws IOException {

        final String lineSeparator = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();

        String res = "";
        for (ArrayList<String> data : listOfList) {
            res += getCSVString(data);
        }

        return res;
    }

    public static BufferedWriter out;
    public static JPanel main_panel;
    private JButton add;

    public static String S;

    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public static ArrayList<ArrayList<String>> listOfData = new ArrayList<ArrayList<String>>();
    public static JButton submit_button;
    public static Boolean isMultiple = false;

    public First() {


        new Execute();

        if (S == "single data") {
            System.out.println(S);
        } else if (S == "multiple data") {
            System.out.println(S);
        } else{
            exit(0);
        }


        // Setting properties of main panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setBounds(350, 100, 1014, 597);
        setSize(800, 700);
        setLayout(new FlowLayout());
        setTitle("KGE_TECH");

        main_panel = new JPanel();
        main_panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(main_panel);
        main_panel.setLayout(null);


        new Data();


        add = new JButton("ADD");
        add.setBounds(450, 600, 175, 25);
        main_panel.add(add);
        if (S == "single data") {

            add.setEnabled(false);
        } else if (S == "multiple data") {
            add.setEnabled(true);
        }

        add.addActionListener(e -> {


            String emailTxt = emailId.getText();
            if (!emailTxt.matches(EMAIL_PATTERN)) {

                JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid email address");
            } else {

                ArrayList<String> data = new ArrayList<String>();
                data.add(ben_name_field.getText());
                data.add(ben_account_number_field.getText());
                data.add(ifsc.getText());
                data.add(transaction_type.getSelectedItem().toString());
                data.add(debit_account_number.getText());
                String date = Data.dateFormat.format(dc.getDateEditor().getDate());
                data.add(date);
                data.add(amount.getText());
                data.add(currency.getText());
                data.add(emailId.getText());
                data.add(remarks.getText());
                listOfData.add(data);

                for (String s : data) {
                    System.out.print(s + " ");
                }
                System.out.println();
                isMultiple = true;
            }
            ben_name_field.setText("");
            ben_account_number_field.setText("");
            ifsc.setText("");
            transaction_type.setSelectedIndex(0);
            debit_account_number.setText("");

            amount.setText("");

            emailId.setText("");
            remarks.setText("(optional)");
        });

        submit_button = new JButton("SUBMIT");
        submit_button.addActionListener(e -> {


            ArrayList<String> data = new ArrayList<String>();
            String data_for_file = null;
            if (S == "single data") {
                String emailTxt = emailId.getText();
                if (!emailTxt.matches(EMAIL_PATTERN)) {

                    JOptionPane.showMessageDialog(new JFrame(), "Please enter a valid email address");
                } else {

                    data.add(ben_name_field.getText());
                    data.add(ben_account_number_field.getText());
                    data.add(ifsc.getText());
                    data.add(transaction_type.getSelectedItem().toString());
                    data.add(debit_account_number.getText());
                    String date = Data.dateFormat.format(dc.getDateEditor().getDate());
                    data.add(date);
                    data.add(amount.getText());
                    data.add(currency.getText());
                    data.add(emailId.getText());
                    data.add(remarks.getText());

                }


                try {
                    data_for_file = getCSVString(data);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                try {
                    data_for_file = getCSVStringForMultipleData(listOfData);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }

            FileWriter stream;
            try {
                String Currentdir = System.getProperty("user.dir");
                System.out.println(Currentdir);

                String csvFileAddress = Currentdir + "/BeneficieryName_currentdate.csv";
                File f = new File(csvFileAddress);

                if (!f.isFile()) {
                    stream = new FileWriter(csvFileAddress, true);
                    out = new BufferedWriter(stream);
                    System.out.println("pls work");
                    String[] entries = {"BENEFICIARY NAME", "BENEFICIARY ACCOUNT NUMBER", "IFSC", "TRANSACTION TYPE", "DEBIT CARD NUMBER", "TRANSACTION DATE", "AMOUNT", "CURRENCY", "EMAIL ID", "REMARKS"};
                    ArrayList<String> headers = new ArrayList<String>();
                    Collections.addAll(headers, entries);

                    String headers_csv = getCSVString(headers);
                    out.write(headers_csv);
                } else {
                    stream = new FileWriter(csvFileAddress, true);
                    out = new BufferedWriter(stream);
                }

                out.write(data_for_file);
                out.close();

                String xlsxFileAddress = Currentdir + "/BeneficieryName_currentdate.xlsx";
                XSSFWorkbook workBook = new XSSFWorkbook();
                XSSFSheet sheet = workBook.createSheet("sheet1");
                String currentLine="";
                int RowNum=0;
                BufferedReader br = new BufferedReader(new FileReader(csvFileAddress));
                while ((currentLine = br.readLine()) != null) {
                    String str[] = currentLine.split(",");
                    XSSFRow currentRow=sheet.createRow(RowNum);
                    for(int i=0;i<str.length;i++){
                        currentRow.createCell(i).setCellValue(str[i]);
                    }
                    RowNum++;
                }

                FileOutputStream fileOutputStream =  new FileOutputStream(xlsxFileAddress);
                workBook.write(fileOutputStream);
                fileOutputStream.close();
                System.out.println("Done");

            } catch (IOException e1) {
                System.out.println("Unable to add to file. Encountered error - " + e1.getMessage());
            }

            exit(0);



        });


        submit_button.setBounds(200, 600, 175, 25);
        main_panel.add(submit_button);


    }


    public static void main (String[]args){



        First gui = new First();
        gui.setVisible(true);
    }
}


class Data
{



    private JLabel name_label;
    private JLabel label2;
    private JLabel label3;
    private JLabel label4;
    private JLabel label5;
    private JLabel label6;
    private JLabel label7;
    private JLabel label8;
    private JLabel label9;
    private JLabel lb;
    public static JTextField ben_name_field, ben_account_number_field, ifsc, debit_account_number, transaction_date, amount, currency, emailId, remarks;
    public static JComboBox transaction_type;
    public static SimpleDateFormat dateFormat = new SimpleDateFormat(
            "dd-MM-yyyy");
    public static JDateChooser dc = new JDateChooser();

    public Data()
    {

        // Create Labels for the fields where data has to be added
        name_label = new JLabel("Beneficiary Name");

        // This functions takes in x-coordinate, y-coordinate, width and height. So you can make adjustments to other fields based on this.
        name_label.setBounds(50, 100, 175, 25);
        main_panel.add(name_label);

        label2 = new JLabel("Beneficiary Acc. No.");
        label2.setBounds(400, 100, 175, 25);
        main_panel.add(label2);


        label3 = new JLabel("IFSC");
        label3.setBounds(50, 200, 175, 25);
        main_panel.add(label3);

        label4 = new JLabel("Transaction Type");
        label4.setBounds(400, 200, 175, 25);
        main_panel.add(label4);

        label5 = new JLabel("Debit Card Number");
        label5.setBounds(50, 300, 175, 25);
        main_panel.add(label5);

        label6 = new JLabel("Transaction Date");
        label6.setBounds(400, 300, 175, 25);
        main_panel.add(label6);

        label7 = new JLabel("Amount");
        label7.setBounds(50, 400, 175, 25);
        main_panel.add(label7);

        label8 = new JLabel("Currency");
        label8.setBounds(400, 400, 175, 25);
        main_panel.add(label8);

        label9 = new JLabel("Email ID");
        label9.setBounds(50, 500, 175, 25);
        main_panel.add(label9);

        lb = new JLabel("Remarks");
        lb.setBounds(400, 500, 175, 25);
        main_panel.add(lb);



        // Create input fields to allow user to enter data.
        ben_name_field = new JTextField();
        ben_name_field.setBounds(175, 100, 175, 25);
        main_panel.add(ben_name_field);
        ben_name_field.setColumns(8);

        ben_account_number_field = new JTextField();
        ben_account_number_field.setBounds(525, 100, 175, 25);
        main_panel.add(ben_account_number_field);
        ben_account_number_field.setColumns(18);
        ben_account_number_field.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                String value = ben_account_number_field.getText();
                int l = value.length();
                JLabel ben_acc_lab = new JLabel();
                ben_acc_lab.setVisible(true);
                ben_acc_lab.setBounds(525, 120, 175, 25);
                if ((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyChar() == '\b') {
                    ben_account_number_field.setEditable(true);
                    ben_acc_lab.setText("");
                } else {
                    ben_account_number_field.setEditable(false);
                    ben_acc_lab.setForeground(Color.red);
                    main_panel.add(ben_acc_lab);
                    ben_acc_lab.setText("* Enter only numeric digits");

                }
            }
        });

        ifsc = new JTextField();
        ifsc.setBounds(175, 200, 175, 25);
        main_panel.add(ifsc);
        ifsc.setColumns(18);

        String s1[] = {"SELECT", "IFT", "NEFT", "RTGS"};
        transaction_type = new JComboBox(s1);

        transaction_type.setBounds(525, 200, 175, 25);
        main_panel.add(transaction_type);

        debit_account_number = new JTextField();
        debit_account_number.setBounds(175, 300, 175, 25);
        debit_account_number.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                String value = debit_account_number.getText();
                int l = value.length();
                JLabel DCno = new JLabel();
                DCno.setVisible(true);
                DCno.setBounds(175, 320, 175, 25);
                if ((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyChar() == '\b') {
                    debit_account_number.setEditable(true);
                    DCno.setText("");
                } else {
                    debit_account_number.setEditable(false);
                    DCno.setForeground(Color.red);
                    main_panel.add(DCno);
                    DCno.setText("*Enter only numeric digits");

                }
            }
        });
        main_panel.add(debit_account_number);
        debit_account_number.setColumns(18);


        LocalDateTime now = LocalDateTime.now();
        dc.setDate(new Date());

        dc.setBounds(525,300,175,25);
        main_panel.add(dc);

        amount = new JTextField();
        amount.setBounds(175, 400, 175, 25);
        amount.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent ke) {
                String value = amount.getText();
                int l = value.length();
                JLabel amt = new JLabel();
                amt.setVisible(true);
                amt.setBounds(175, 420, 175, 25);
                if ((ke.getKeyChar() >= '0' && ke.getKeyChar() <= '9') || ke.getKeyChar() == '\b') {
                    amount.setEditable(true);
                    amt.setText("");
                } else {
                    amount.setEditable(false);
                    amt.setForeground(Color.red);
                    main_panel.add(amt);
                    amt.setText("* Enter only numeric digits");

                }
            }
        });
        main_panel.add(amount);
        amount.setColumns(18);

        currency = new JTextField();
        currency.setText("INR");
        currency.setBounds(525, 400, 175, 25);
        main_panel.add(currency);
        currency.setColumns(18);
        currency.setEditable(false);

        emailId = new JTextField();
        emailId.setBounds(175, 500, 175, 25);
        emailId.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                emailId.setText("");
            }
        });
        JLabel ema = new JLabel();
        ema.setVisible(true);
        ema.setBounds(175, 520, 175, 25);
        main_panel.add(emailId);
        emailId.setColumns(30);

        remarks = new JTextField();
        remarks.setText("(optional)");
        remarks.setBounds(525, 500, 175, 25);
        remarks.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                remarks.setText("");
            }
        });
        main_panel.add(remarks);
        remarks.setColumns(18);

    }
}

class Execute
{

    public Execute()
    {

        System.out.println("executed!");
        Object[] options = {"single data", "multiple data"};
        int n = JOptionPane.showOptionDialog(new JFrame() , "select an option?", "TYPE",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

        if(n==0){
            First.S="single data";
        }else if(n==1){
            First.S="multiple data";
        }
    }

}
