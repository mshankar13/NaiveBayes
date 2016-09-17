package cse353hw4;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author marleneshankar
 */
public class Attribute {

    public String name;
    public Set<String> values;
    public ArrayList<String> allvalues;
    public ArrayList<String> cleanvalues;
    public ArrayList<String> discreteVal;
    public Set<String> hashVal;
    public boolean continuous;
    public double avg;
    public double min;
    public double max;

    public Attribute(String name) {
        values = new HashSet<String>();
        allvalues = new ArrayList<String>();
        cleanvalues = new ArrayList<String>();
        discreteVal = new ArrayList<String>();
        hashVal = new HashSet<String>();
        continuous = false;
        this.name = name;
    }

    public void addUniqueValue(String value) {
        values.add(value);
        // check for "?"
        values.remove("?");
    }

    public void addValue(String value) {
        allvalues.add(value);
    }
    
    public void addCleanValue(String value) {
        cleanvalues.add(value);
    }

    public void isContinuous() {
        continuous = true;
    }

    public String getMode() {
        String maxValue = "";
        int maxCount = 0;
        for (String x : allvalues) {
            int counter = 0;
            for (String y : values) {
                if (x.equals(y)) {
                    ++counter;
                }
            }
            if (counter > maxCount) {
                maxCount = counter;
                maxValue = x;
            }
        }
        return maxValue;
    }

    public void calculateMinMax(int start, int end) {
        min = Double.parseDouble(cleanvalues.get(0));
        max = Double.parseDouble(cleanvalues.get(0));
        for (String x : cleanvalues.subList(start, end)) {
            if (Double.parseDouble(x) <= min) {
                min = Double.parseDouble(x);
            }

            if (Double.parseDouble(x) >= max) {
                max = Double.parseDouble(x);
            }
        }
    }

    
    public void addDiscreteList (String sub) {
        discreteVal.add(sub);
        hashVal.add(sub);
        hashVal.remove("?");
        // handle "?" here too
        // replace with mode
    }
}
