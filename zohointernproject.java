//import com.intellij.internal.statistic.eventLog.EventLogSystemEvents;
//import com.intellij.psi.PsiYieldStatement;

import java.sql.*;
import  java.util.*;
public class demomain {
    static int co = 1;
    static int medicinecount = 1;
    static String delete = "DELETE FROM userdetails;";
    static String medtabledelete = "DELETE FROM medtable";
    static String refreshthird = "DELETE FROM patmedtable";
    static String refresher = "DELETE FROM password";
    static String ref="DELETE FROM priority";
    public static void main(String args[]) throws SQLException, ClassNotFoundException {
        //  Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zohoproject", "root", "password");
        Statement statement = conn.createStatement();
        Scanner in = new Scanner(System.in);
        String admin="admin";
        String starter="select role from userdetails where role='"+admin+"';";
        ResultSet rs=statement.executeQuery(starter);
        if(!rs.next())
        {
            delete();
            starter();
        }
        while (true) {
            System.out.println("                     Login page");
            System.out.println("What role do you want to perform");
            String str="select distinct(role) from userdetails";
            ResultSet res=statement.executeQuery(str);
            while (res.next()){
                System.out.println(res.getString("role"));
            }
            String roletoperform = in.next().trim().toLowerCase();
            int co=0;
            String string="select role from userdetails where role='"+roletoperform+"';";
            ResultSet res1=statement.executeQuery(string);
            if(res1.next())
            {
                co=1;
            }
            if(co==0)
            {
                System.out.println("No such role present");
                continue;
            }
            System.out.println("Enter email id");
            String roletoperformmailid = in.next().trim();
            String forlogin = "SELECT * FROM password where mailid = '" + roletoperformmailid + "';";
            ResultSet resforlogin = statement.executeQuery(forlogin);
            if (!resforlogin.next()) {
                System.out.println("INVALID LOGIN");
                continue;
            }
            System.out.println("Enter password");
            String passwordforlogin = in.next();
            resforlogin = statement.executeQuery(forlogin);
            String passwordstoredindatabase = "";
            if (resforlogin.next()) {
                passwordstoredindatabase = resforlogin.getString(1);
            }
            if (passwordstoredindatabase.equals(passwordforlogin)) {
                System.out.println("INVALID login");
                continue;
            }
            int option=0;
            while(option!=4) {
                if (!roletoperform.equals("patient")) {
                    System.out.print("Whether u want to view or add a role \nPress 1 to view\nPress 2 to add a role\nPress 3 to skip\n");
                }
                System.out.println("Press 4 to log out");
                option = in.nextInt();
                if (option == 1) {
                    print(roletoperform);
                } else if (option == 2) {
                    System.out.println("Enter who you want to add");
                    String adder = in.next().toLowerCase();
                    addrole(roletoperform, adder);
                } else if (option == 4) {
break;
                }
                if (roletoperform.equals("doctor") || roletoperform.equals("assistant")) {
                    System.out.println("Do you want to add any medicines\nPress yes to add\nPress no to skip");
                    String checker = in.next();
                    if (checker.equals("yes")) {
                        System.out.println("Enter the medicines u want to add");
                        ArrayList<String> templist = new ArrayList<>();
                        System.out.println("Enter finish so that u have added the medicines list");
                        String currmedicine = in.next().toLowerCase().trim();
                        while (!currmedicine.equals("finish")) {
                            templist.add(currmedicine);
                            currmedicine = in.next();
                        }
                        System.out.println("Enter patient user id");
                        int userid = in.nextInt();
                        String exitstornot = "SELECT * from userdetails WHERE (userid='" + userid + "' and role='patient')";
                        ResultSet inornot = statement.executeQuery(exitstornot);
                        if (inornot.next()) {
                            for (String curr : templist) {
                                System.out.println("medicine approval type 1.yes to confirm\n 2.no to skip it:" + curr);
                                String flag = in.next().toLowerCase();
                                if (flag.equals("yes")) {
                                    String medpresentornot = "SELECT * from medtable where name='" + curr + "'";
                                    ResultSet medexecute = statement.executeQuery(medpresentornot);
                                    if (medexecute.next()) {
                                        int getmedmid = 0;
                                        String sk = "SELECT medno FROM medtable where name='" + curr + "';";
                                        ResultSet result = statement.executeQuery(sk);
                                        while (result.next()) {
                                            getmedmid = result.getInt(1);
                                        }
                                        String presentquery = "select medicineid from patmedtable where userid=" + userid;
                                        String r = "select " + getmedmid + " from medtable where "+getmedmid+" in (" + presentquery+")";
                                        ResultSet rs1 = statement.executeQuery(r);
                                        if (rs1.next()==false) {
                                            String newinsert = "INSERT INTO patmedtable(userid,medicineid) VALUES ('" + userid + "','" + getmedmid + "');";
                                            statement.executeUpdate(newinsert);
                                        }
                                    } else {
                                        //System.out.println(curr + " " + medicinecount);
                                        String insertmed = "INSERT INTO medtable (name,medno) VALUES ('" + curr + "','" + medicinecount + "');";
                                        statement.executeUpdate(insertmed);
                                        String newinsert = "INSERT INTO patmedtable(userid,medicineid) VALUES ('" + userid + "','" + medicinecount + "');";
                                        statement.executeUpdate(newinsert);
                                        medicinecount++;
                                    }
                                }
                            }
                        } else {
                            System.out.println("No such patient present in database");
                        }
                    }
                    System.out.println("Do you want to view the patients medications\n1.type yes to view\n2.no to skip it");
                    String type = in.next().trim().toLowerCase();
                    if (type.equals("yes")) {
                        printmedicines();
                    }
                } else if (roletoperform.equals("patient")) {
                    System.out.println("Do you want to view your medicines type yes or no");
                    String yesorno = in.next();
                    if (yesorno.equals("yes")) {
                        printmedications(roletoperformmailid);
                    }
                    continue;
                }
            }
            System.out.println("if you have completed your process \n1.press END to terminate \n2.press skip to continue");
            String checker = in.next().toLowerCase().trim();
            if (checker.equals("end")) {
                System.out.println("Thank you!");
                break;
            }
        }
    }
    public  static  void  printmedications(String email) throws SQLException, ClassNotFoundException {

        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zohoproject", "root", "password");
        Statement statement = conn.createStatement();
        String sub1="select name from medtable where medno in (select medicineid from patmedtable where userid in (select userid from userdetails where email='"+email+"'))";
        // System.out.println(sub1);
        ResultSet set=statement.executeQuery(sub1);

        while (set.next())
        {
            System.out.println(set.getString("name"));
        }

    }
    public  static  void printmedicines() throws SQLException {
        Scanner in=new Scanner(System.in);
        System.out.println("FOR WHICH PATIENT You want to print the medications");
        System.out.println("Enter his mailid");
        String email=in.next().trim();
        String query="select * from userdetails where email='"+email+"';";
        //  System.out.println(query);
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zohoproject", "root", "password");
        Statement statement=conn.createStatement();
        ResultSet res=statement.executeQuery(query);
        boolean flag= res.next();
        res.close();
        // System.out.println(flag);
        if(flag)
        {
            String sub1="select name from medtable where medno in (select medicineid from patmedtable where userid in (select userid from userdetails where email='"+email+"'))";
            // System.out.println(sub1);
            ResultSet set=statement.executeQuery(sub1);

            while (set.next())
            {
                System.out.println(set.getString("name"));
            }
        }
        else
        {
            System.out.println("Sorry! No such patient is present");
        }
    }
    public  static  void print(String curr) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zohoproject", "root", "password");
        Statement statement=conn.createStatement();
        String query="SELECT priority from priority where role='"+curr+"'"+";";
        ResultSet res=statement.executeQuery(query);
        int currprior=0;
        if(res.next())
        {
            currprior=res.getInt("priority");
        }
        String str="SELECT role from priority where priority < "+currprior;
        //  ResultSet res1=statement.executeQuery(str);
        String query1="select * from userdetails where role in ("+str+")";
        ResultSet restart =statement.executeQuery(query1);
        int checker=0;
        while (restart.next())
        {
            checker++;
            System.out.println(restart.getString("role")+" "+restart.getString("name"));
        }
        if(checker==0)
            System.out.println("No results found");
        //  System.out.println(currprior);
    }
    public static void  addrole(String roletoperform,String adder) throws ClassNotFoundException, SQLException {
        Scanner sc=new Scanner(System.in);
        // System.out.println("Enter who you want to add");
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zohoproject", "root", "password");
        Statement statement=conn.createStatement();
        String s1="SELECT priority from priority where role='"+roletoperform+"'";
        String s2="SELECT priority from priority where role='"+adder+"'";
        ResultSet r1=statement.executeQuery(s1);
        int a=0;
        int b=0;
        boolean a1=r1.next();
        if(a1)
        {
            a=r1.getInt("priority");
        }
        r1.close();
        ResultSet r2=statement.executeQuery(s2);
        boolean a2=r2.next();
        if(a2)
        {
            b=r2.getInt("priority");
        }
        if(a>b)
        {
            System.out.println("Enter his mail id");
            String hisemail = sc.next().trim();
            String exitstornot="SELECT * from userdetails WHERE email='"+hisemail+"';";
            ResultSet res=statement.executeQuery(exitstornot);
            if (!res.next())
            {
                System.out.println("Enter his name now");
                String username=sc.next().trim();
                System.out.println("Set a password for this role");
                String newpassword=sc.next();
                while(!Passwordvalidator.checker(newpassword))
                {
                    passwordrequirements();
                    newpassword=sc.next().trim();
                }
                String str="INSERT INTO userdetails (userid,role,name,email) VALUES ('"+co+"','"+adder+"','"+username+"','"+hisemail+"');";
                statement.executeUpdate(str);
                co++;
                String adminquery="INSERT INTO password (mailid,password) VALUES ('"+hisemail+"','"+newpassword+"');";
                statement.executeUpdate(adminquery);
                System.out.println("Added Successfully");
            } else {
                System.out.println("Already exisiing mail check whether email is correct or not or he may be present already no need to add again");
            }
        } else {
            System.out.println("Access denied");
        }
    }
    public  static  void  passwordrequirements()
    {
        System.out.println("Your password should contain");
        System.out.println("Atleast One upper case letter");
        System.out.println("Atleast One lower case letter");
        System.out.println("Atleast One Digit");
        System.out.println("Atleast one Special Character");
        System.out.println("And no space");
        System.out.println("Type your password again");
    }
    public  static  void delete() throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zohoproject", "root", "password");
        Statement statement = conn.createStatement();
        statement.executeUpdate(medtabledelete);
        statement.executeUpdate(refreshthird);
        statement.executeUpdate(delete);
        statement.executeUpdate(refresher);
        statement.executeUpdate(ref);
    }
    public  static  void starter() throws SQLException {
        delete();
        System.out.println("WELCOME ADMIN");
        Scanner in=new Scanner(System.in);
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/zohoproject", "root", "password");
        Statement statement = conn.createStatement();
        System.out.println("Enter your name Admin");
        String adminname = in.next();
        System.out.println("Enter your mail");
        String email = in.next();
        String role = "Admin";
        co = 1;
        medicinecount = 1;
        String str = "INSERT INTO userdetails (userid,role,name,email) VALUES ('" + co + "','" + role + "','" + adminname + "','" + email + "');";
        ++co;
        statement.executeUpdate(str);
        System.out.println("Enter password");
        String password = in.next();
        while (!Passwordvalidator.checker(password)) {
            passwordrequirements();
            password = in.next();
        }
        String adminquery = "INSERT INTO password (mailid,password) VALUES ('" + email + "','" + password + "');";
        statement.executeUpdate(adminquery);
        System.out.println("Enter the no of roles");
        int noofroles = in.nextInt();
        System.out.println("Enter the roles 'Admin'");
        for (int i = 0; i < noofroles; i++) {
            System.out.println("Enter the role name and his priority:");
            String curr = in.next();
            int prior=in.nextInt();
            String insert="INSERT INTO priority (role,priority) VALUES ('" + curr + "',"+prior + ");";
            statement.executeUpdate(insert);
        }
    }
}