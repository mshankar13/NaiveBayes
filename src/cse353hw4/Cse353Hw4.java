package cse353hw4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author marleneshankar
 */
public class Cse353Hw4 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ArrayList<String> dataSet = new ArrayList<String>();
        // Open the file and push the data into an Array List per row
        try {
            BufferedReader reader = new BufferedReader(new FileReader("./data/cse353-hw2-data.tsv"));
            String row = reader.readLine();
            int counter = 0;
            int numLines = 500;
            while (row != null) {
                dataSet.add(row);
                row = reader.readLine();
                counter++;
                if(counter > numLines)
                    break;
            }
            reader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Manually put in Attributes and types
        ArrayList<Attribute> attr = new ArrayList<Attribute>();

        Attribute age = new Attribute("age");
        age.isContinuous();
        attr.add(age);

        Attribute workclass = new Attribute("workclass");
        attr.add(workclass);

        Attribute fnlwgt = new Attribute("fnlwgt");
        fnlwgt.isContinuous();
        attr.add(fnlwgt);

        Attribute education = new Attribute("education");
        attr.add(education);

        Attribute education_num = new Attribute("education-num");
        education_num.isContinuous();
        attr.add(education_num);

        Attribute marital_status = new Attribute("marital-status");
        attr.add(marital_status);

        Attribute occupation = new Attribute("occupation");
        attr.add(occupation);

        Attribute relationship = new Attribute("relationship");
        attr.add(relationship);

        Attribute race = new Attribute("race");
        attr.add(race);

        Attribute sex = new Attribute("sex");
        attr.add(sex);

        Attribute capital_gain = new Attribute("capital-gain");
        capital_gain.isContinuous();
        attr.add(capital_gain);

        Attribute capital_loss = new Attribute("capital-loss");
        capital_loss.isContinuous();
        attr.add(capital_loss);

        Attribute hours_per_week = new Attribute("hours-per-week");
        hours_per_week.isContinuous();
        attr.add(hours_per_week);

        Attribute native_country = new Attribute("native-country");
        attr.add(native_country);

        // Do prob calculations
        // store calculations
        // <=50k - hash prob by value as key
        // >50k - hash prob by value as key
        int total = dataSet.size();
        int startTest = (total/5) * 4;
        int endTest = dataSet.size() - 1;
        int startTraining = 0;
        int endTraining = startTest - 1;
        naiveBayes(dataSet, attr, startTraining, endTraining, startTest, endTest);
    }

    public static void naiveBayes(ArrayList<String> dataSet, ArrayList<Attribute> attr, int startTraining, int endTraining, int startTest, int endTest) {
        HashMap greater = new HashMap();
        HashMap lessOrEqual = new HashMap();
        ArrayList<String> greaterFifty = new ArrayList<String>();
        ArrayList<Integer> gF = new ArrayList<Integer> ();
        ArrayList<String> lessOrEqualFifty = new ArrayList<String>();
        ArrayList<Integer> leF = new ArrayList<Integer> ();
        ArrayList<String> training = new ArrayList<String> ();
        training.addAll(dataSet.subList(startTraining, endTraining));
        // loop through dataSet and organize the data
        
        for (String x : dataSet) {
            String[] row = x.split("\t");
            // loop through and store values in each attribute array
            for (int i = 0; i < row.length - 1; i++) {
                attr.get(i).addUniqueValue(row[i]);
                attr.get(i).addValue(row[i]);
            }
        }
        
        // change all question marks to mode
        for (Attribute a: attr) {
            a.values.remove("?");
            String mode = a.getMode();
            for (String v: a.allvalues) {
                
                if(v.trim().equals("?")){
                    a.addCleanValue(mode);
                    
                }
                else{
                    a.addCleanValue(v);
                }
            }
        }
        
        
        for (String x : training) {
            String[] row = x.split("\t");
            // get the income marker
            String income = row[row.length - 1];
            if (income.equals("<=50K")) {
                lessOrEqualFifty.add(x);
                leF.add(training.indexOf(x));
            } else {
                greaterFifty.add(x);
                gF.add(training.indexOf(x));
            }
        }
   
        // 46510 total dataset
        // at this point all unique values are in attr
        // calculate number of subsets for continuous values
        int valueNum = avgValues(attr);
        // loop through the attributes to handle continuous values
        for (Attribute a: attr) {
            if (a.continuous) {
                handleContinuous(a, dataSet, valueNum, startTraining,endTraining);
            } else {
                // do nothing to discrete attributes
            }
        }
         
        for (Attribute a : attr) { 
                // loop through attribute values and count number of occurrences of each
            int attrIndex = attr.indexOf(a);
            Set<String> valList = new HashSet<String>();
            if (a.continuous) {  
                // use the discreteVal list NOT values
                valList.addAll(a.hashVal);
            }
            else {
                valList.addAll(a.values);
            }
                for (String v : valList) {

                    int occurrences = countOccurrences(a, v, lessOrEqualFifty, leF,attrIndex);
                    // calculate probability of that value and store in HashMap
                    double probability = ((double) occurrences) / ((double) lessOrEqualFifty.size());
                    lessOrEqual.put(v, probability);
                    
            }
        }
        
        for (Attribute a : attr) { 
                // loop through attribute values and count number of occurrences of each
            int attrIndex = attr.indexOf(a);
            Set<String> valList = new HashSet<String>();
            if (a.continuous) {  
                // use the discreteVal list NOT values
                valList.addAll(a.hashVal);
            }
            else {
                valList.addAll(a.values);
            }
                for (String v : valList) {
                    
                    int occurrences = countOccurrences(a, v, greaterFifty, gF,attrIndex);
                    // calculate probability of that value and store in HashMap
                    double probability = ((double) occurrences) / ((double) greaterFifty.size());
                    greater.put(v, probability);
            }
        }

        // now all the probabilities have been calculated in hashmaps
        //HashMap greater
        //HashMap lessOrEqual
        // conduct analysis on the last portion of the data individually
        ArrayList<String> subset = new ArrayList<String> ();
        subset.addAll(dataSet.subList(startTest, endTest));
        
        int count = 0;
        int match = 0;
        for(String x: subset) {
            String [] last = x.split("\t");
            String prediction = nbcTest(attr, dataSet.indexOf(x), x, greater, lessOrEqual);
            if(prediction.equals(last[last.length-1])){
                match++;
            }
            count++;
        }
        System.out.println((double)(match*1.0/count));
        
    }
    
    public static String nbcTest(ArrayList<Attribute> attr, int index, String x, HashMap greater, HashMap lessOrEqual ) {
            // for discrete values look at subset labels created
            // check for continuous column
            // if continuous, use attribute list with marked values
             
            double probG = 1;
            double probL = 1;
            String [] row = x.split("\t");
            // go through and get the probability of each value from both hashmaps and compare
            //for(String value: row) { 
                
            //    probG *= Double.parseDouble(greater.get(value).toString());
             //   probL *= Double.parseDouble(lessOrEqual.get(value).toString());
            //}
            for (int i = 0; i < row.length - 1; i ++) {
                String key = "";
                if (attr.get(i).continuous) {
                    // get the actual marker value and replace column value with for hashmap
                    key = attr.get(i).discreteVal.get(index);
                }
                else{
                    key = attr.get(i).cleanvalues.get(index);
                }
                //System.out.println(x);
                //System.out.println(key);
               // System.out.println(greater.get(key).toString());
                probG *= Double.parseDouble(greater.get(key).toString());
                probL *= Double.parseDouble(lessOrEqual.get(key).toString());
            }
            
            
            // now probG and probL are calculated
            if (probG > probL) {
                return ">50K";
            }
            else{
                return "<=50K";
            }
        
    }
    
    public static int countOccurrences (Attribute a, String v, ArrayList<String> subset, ArrayList<Integer> indicies, int attrIndex) {
        // loop through list and count the number of occurrences of the value
        int occurrences = 0;
                    //for (String x : subset) {
                    //    String[] row = x.split("\t");
                        // Get the desired column
                     //   String value = row[attrIndex];
                        
                        // check if the attr value = value in attr column
                    //    if (v.equals(value)) {
                    //        occurrences++;
                    //    }
                    //}
                    
                    for (int i : indicies) {
                        if (a.cleanvalues.get(i).equals(v)) {
                            ++occurrences;
                        }
                    }
        return occurrences;
    }
    
    public static int avgValues (ArrayList<Attribute> attr) {
        int valueAvg = 0;
        int discreteAttr = 0;
        for(Attribute a : attr) {
            if(!a.continuous){
                //System.out.println(a.values.size());
                valueAvg += a.values.size();
                discreteAttr++;
            }
        }
        valueAvg /= discreteAttr;
        // have the number of subsets
        return valueAvg;
    }
    
    public static void handleContinuous (Attribute a, ArrayList<String> dataSetSubset, int valueNum, int startTraining, int endTraining){
        // create discrete values for Continuous data
        // find min and max of discrete attr value
        // get the minimum and maximum of the values
        a.calculateMinMax(startTraining, endTraining); 
        // get the range and subsets
        double range = a.max - a.min;
        range /= (double) valueNum;
        
        // go through values and classify
        for (String x: a.cleanvalues) {
            //System.out.println(x);
            double value = Double.parseDouble(x);
            // loop through different classifiers for subset
            double start = a.min;
            int subsetnum = 0;
            while (start <= (a.max + range)) {
                if (value >= start && value < (start + range)) {
                    // replace value with range classifier string
                    // subx?
                    a.addDiscreteList(Integer.toString(subsetnum));
                }
                start += range;
                ++ subsetnum;
            }
        }
        // continuous made discrete :D 
    }

}
