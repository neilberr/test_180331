package pkg;

import org.rosuda.JRI.Rengine;

public class Temp {

    public static void main(String a[]) {

        // Create an R vector in the form of a string.
        String javaVector = "c(1,2,3,4,5)";
        
        System.out.println("in: ");
        System.out.println("hello: " + System.getProperty("java.library.path"));
        System.getProperty("java.library.path");
        
        System.out.println("mid: ");
        

//        System.setProperty("java.library.path", "C:\\Users\\MyLatop\\Documents\\R\\win-library\\3.4\\rJava\\libs\\x64");  

        System.out.println("hello: " + System.getProperty("java.library.path"));

        System.out.println("out: ");
        
        // Start Rengine.
        Rengine engine = new Rengine(new String[] { "--no-save" }, false, null);

        // The vector that was created in JAVA context is stored in 'rVector' which is a variable in R context.
        engine.eval("rVector=" + javaVector);
        
        //Calculate MEAN of vector using R syntax.
        engine.eval("meanVal=mean(rVector)");
        
        //Retrieve MEAN value
        double mean = engine.eval("meanVal").asDouble();
        
        //Print output values
        System.out.println("Mean of given vector is=" + mean);

    }
}