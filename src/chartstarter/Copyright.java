/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chartstarter;

/**
 *
 * @author veckardt
 */
public class Copyright {

    public static final String COPYRIGHT = "(c)";
    public static String copyright = "Copyright " + COPYRIGHT + " 2015 PTC Inc.";
    public static String copyrightHtml = "Copyright &copy; 2015 PTC Inc.";
    public static String programName = "Integrity Chart Starter";
    public static String programVersion = "1.0";
    public static String title = programName + " - v"+ programVersion;
    public static String author = "Author: Volker Eckardt";
    public static String email = "emails: veckardt@ptc.com";

    public static String getCopyright () {
        String copy = (" " + programName + " - Version " + programVersion+"\n");
        copy = copy + ("\n This utility displays a report or chart for a single item");
        copy = copy + ("\n Tested with PTC Integrity 10.4 and 10.6");
        copy = copy + ("\n");
        copy = copy + ("\n " + copyright);
        copy = copy + ("\n " + author);
        copy = copy + ("\n " + email);   
        return copy;
    }
    
    public static void write() {
        System.out.println(getCopyright());
    }

    public static void usage() {
        System.out.println("*");
        System.out.println("* Usage: ");
        System.out.println("*   <path-to-javaw>\\javaw -jar <path-to-jar>\\IntegrityReportStarter.jar");
        System.out.println("* Example:");
        System.out.println("*   C:\\Program Files\\Java\\jdk1.7.0_40\\bin\\javaw -jar C:\\IntegrityClient10\\IntegrityReportStarter.jar");
        System.out.println("* Additional Notes:");
        System.out.println("*   - a configuration file 'IntegrityTraceAnalyser.properties' can be used to specify default values");
        System.out.println("*   - a log file is created in directory '%temp%', the filename is 'IntegrityReportStarter_YYYY-MM-DD.log'");
        System.out.println("*");
    }
}
