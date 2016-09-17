ReadMe


Attribute Class - handles the values for each attribute and stores when in arraylists for later access
        Utilizes a cleanvalues list for list of data without question marks


Cse353Hw4 - handles all the functionality of the program
        Contains main where the file is initially read in and where 5 fold cross validation is hard coded in
        naiveBayes method - overall architecture for calculations and calls several helper methods to assist in calculation and discrete value handling
        nbcTest method - where testing data is taken in to be tested
        countOccurrences - counts the occurrence of a value in a subset
        avgValues - averages the number of unique values per attribute 
        handleContinuous - handles the continuous values in the dataset and organizes it into subsets