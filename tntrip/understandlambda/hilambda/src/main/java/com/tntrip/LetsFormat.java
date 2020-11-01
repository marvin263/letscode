package com.tntrip;

import java.text.ChoiceFormat;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Locale;

public class LetsFormat {

    /**
     * Because printf-style format strings are interpreted at runtime, rather than validated by the compiler, they can contain errors that result in the wrong strings being created.
     * This rule statically validates the correlation of printf-style format strings to their arguments when 
     * calling the format(...) methods of java.util.Formatter, java.lang.String, java.io.PrintStream, MessageFormat, and java.io.PrintWriter classes and
     * the printf(...) methods of java.io.PrintStream or java.io.PrintWriter classes.
     * 
     * @param args
     */
    public static void main(String[] args) {
     
        //%[argument_index$][flags][width][.precision]conversion
        
        // Width
        // The width is the minimum number of characters to be written to the output.
        // For the line separator conversion, width is not applicable; if it is provided, an exception will be thrown.
        
        //Precision
        //For general argument types, the precision is the maximum number of characters to be written to the output.
        String s = String.format("%1$s  %<s %2$s 中国 %<s. 然后， %3$-5.3s", "努力", "strong", "HelloWorld");
        System.out.println(s);
    }


}
