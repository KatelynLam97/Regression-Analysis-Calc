package regressionAnalysisCalc;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.StringIndexOutOfBoundsException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.InputMismatchException;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

/** 
 * A class for opening and reading (.txt) text files using <code>Scanner</code> for the Regression Analysis Calculator, following a specified format (see README.txt), and writing image files (.png) or (.jpg).
 * This class is not meant to be instantiated.
 * @version Jan 2021
 * @author Katelyn Lam
 */

public class FileManager
{
	//initializes Scanner for reading files
	private static Scanner inputReader = new Scanner(System.in); 
	private static Scanner fileReader;
	
	private static String fileName; //name of the file
	private static int lineCount = 0; //the number of lines in the file, used for tracking locations of errors
	private static boolean fileOpened = false; //state of whether a file has been opened successfully (meaning data has been recorded and heading is correctly formatted)
	private static File selectedFile; //file selected by JFileChooser
	
	/*creates two ArrayLists to represent the independent and dependent values. An ArrayList is used because the size of the data set is 
	unknown, but calculations done in the AnalysisLibrary uses double[]*/
	private static List<Double> independentValues = new ArrayList<Double>();
	private static List<Double> dependentValues = new ArrayList<Double>();
	
	//values of the names of the independent and dependent variable and their units
	private static String independentVariable = "";
	private static String independentUnit = "N/A";
	private static String dependentVariable = "";
	private static String dependentUnit = "N/A";

	/**
	 * Opens a correctly formatted text file (see README.txt), and records data for independent and dependent values.
	 * A message dialog is outputted if file cannot be found.
	 */
	public static void openFile()
	{
		JFrame openFrame = new JFrame();
		JFileChooser fileOpener = new JFileChooser();
		FileNameExtensionFilter options = new FileNameExtensionFilter("Normal text file (*.txt)","txt");
        fileOpener.setFileFilter(options); //restricts user to only selecting text files
		setAppropriateDirectory(fileOpener); //sets the directory file is chosen from as the last opened directory
        int result = fileOpener.showOpenDialog(openFrame); //shows user text box to open file
        
        //attempts to open a selected text file. If file cannot be accessed, displays an error message
        if (result == JFileChooser.APPROVE_OPTION)
        {
            selectedFile = fileOpener.getSelectedFile();
			
			try
			{
				fileReader = new Scanner(selectedFile);
			}
			catch(FileNotFoundException e)
			{
				JOptionPane.showMessageDialog(openFrame, "ERROR: Cannot find file.");
			}
			fileName = selectedFile.getName();
			fileReader();
            saveDirectory(selectedFile.getParent()); //saves most recent directory
        } 
	}
	
	/**
	 * Reads from a text file and sets the file name, values and units of independent and dependent variable. Each value of the 
	 * independent and dependent variables are stored as data points.
	 */
	public static void fileReader()
	{
		JFrame errorDialog = new JFrame();
		boolean isValid = false;
		boolean endOfFileReached = false;
		String categories = " ";
		double currentElement = 0.0;
		lineCount = 0;
		
		//re-initialize data set
		independentValues.clear();
		dependentValues.clear();
		
		//reads every line of the file until end is reached
		while(!endOfFileReached)
		{
			try
			{
				/*On the first line, looks for "@" character and stores values of variables before it.
				Attempts to initialize values and units of independent and dependent variable. */
				if(lineCount < 1)
				{
					categories = fileReader.nextLine();
					int[] sentinelLocations = new int[3];
					int index = 0;
					
					for(int i = 0; i < categories.length(); i++)
					{
						if(categories.substring(i,i+1).equals("@"))
						{
							sentinelLocations[index] = i;
							index++;
						}
					}
					
					//Displays error message if header is not formatted correctly with 3 "@" characters separating the values
					try
					{
						independentVariable = categories.substring(0,sentinelLocations[0]);
						dependentVariable = categories.substring(sentinelLocations[0] + 1, sentinelLocations[1]);
						independentUnit =categories.substring(sentinelLocations[1] + 1, sentinelLocations[2]);
						dependentUnit = categories.substring(sentinelLocations[2] + 1, categories.length());
					}
					catch(StringIndexOutOfBoundsException e)
					{
						JOptionPane.showMessageDialog(errorDialog, "ERROR: Cannot format title. Please reformat. See Help(?) for details.");
						endOfFileReached = true;
					}
				}
				
				//attempts to store values of independent and dependent variables for subsequent lines
				else
					readData();
					
				lineCount++;
			}
			catch(NoSuchElementException e) //stops reading the file once it reaches the end, returns to caller
			{
				endOfFileReached = true;
				
				if(independentValues.size() > 0)
					fileOpened = true;
				else
					JOptionPane.showMessageDialog(errorDialog, "ERROR: File is formatted incorrectly. Please see Help(?) for details.");
			}
		}
	}
	
	/**
	 * Reads the data values (as doubles) for the independent and dependent variable, and adds them to their corresponding list
	 */
	private static void readData()
	{
		//reads the next double input on each line, assigns it either as a dependent (if is even element) or independent (if is odd element)
		try
		{
			double currentElement = fileReader.nextDouble();
						
			if(lineCount % 2 == 0)
				dependentValues.add(currentElement);
			else
				independentValues.add(currentElement);
		}
		catch(InputMismatchException e) //if input is not a double, the line is skipped and an error message is displayed
		{
			JFrame errorFrame = new JFrame();
			JOptionPane.showMessageDialog(errorFrame, "ERROR: Bad data on line " + (lineCount + 3)/2 + " See Help(?) for details.");
			fileReader.nextLine();
			
			if(lineCount % 2 == 0)
				independentValues.remove(independentValues.size() - 1);
			else
				lineCount++;
		}
	}
	
	/**
	 * Returns state of whether a file has been opened successfully. A successful open operation means that the headings are recorded
	 * and there is a at least one set of data values.
	 * @return boolean - <code>true</code> if a file has been opened successfully, otherwise <code>false</code>
	 */
	public static boolean isFileOpened()
	{
		return fileOpened;
	}
	
	/**
     * Saves an image file as a .png or a .jpg to a chosen directory. 
	 * @param bi A Java <code>BufferedImage</code> image that will be saved
     */
    public static void saveFile(BufferedImage bi) 
    {
        JFrame frame = new JFrame();
        String path;
        boolean correctType = false;
        
        try
        {             
            // Create a JFileSaver, a file chooser box included with Java 
            JFileChooser fileSaver = new JFileChooser();
            setAppropriateDirectory(fileSaver);//accesses most recent directory
            
            //Allows user to save photo only as a png or a jpg, open save window
            FileNameExtensionFilter acceptablePNG = new FileNameExtensionFilter("PNG (*.png)","png");
            FileNameExtensionFilter acceptableJPG = new FileNameExtensionFilter("JPG (*.jpg)","jpg");
            
            fileSaver.addChoosableFileFilter(acceptablePNG);
            fileSaver.addChoosableFileFilter(acceptableJPG);
            int result = fileSaver.showSaveDialog(frame);
        
            //records file path if 'Save' is pressed
            if (result == fileSaver.APPROVE_OPTION)
            {  
                saveDirectory(fileSaver.getSelectedFile().getParent());//saves recent directory
                
                //saves image file as a png to selected path
                if(fileSaver.getFileFilter().equals(acceptablePNG))
                {
                    path = fileSaver.getSelectedFile().getAbsolutePath() + ".png";
                    File f = new File (path);
                    ImageIO.write(bi, "png", f);
                }
                //saves image file as a jpg to selected path
                else if(fileSaver.getFileFilter().equals(acceptableJPG))
                {
                    path = fileSaver.getSelectedFile().getAbsolutePath() + ".jpg";
                    
                    //converts current image to a BufferedImage without alpha
                    BufferedImage newImage = convertPNGtoJPG(bi);
                    File f = new File (path);
                    ImageIO.write(newImage, "jpg", f);
                }
                else
                {
                    JOptionPane.showMessageDialog(frame, "Please select appropriate image file type");
					saveFile(bi);
                }
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(frame, "ERROR: Cannot save file.");
        }
    }
	
   /**
     * Sets directory file is opened to to the directory a file has last been saved to, which
     * is recorded in 'lastOpenedPath.txt' If there is no directory that has been recorded, 
     * go to the home directory.
     * @param fileChooser - an instance of JFileChooser, that can pick a specific file directory
     */
    private static void setAppropriateDirectory(JFileChooser fileChooser)
    {
        String path = "ERROR"; //directory where file is read
        Scanner reader; //reads from file
        boolean endOfFileReached = false; //state of whether end of file has been reached
        
        /* Opens and reads from lastOpenedPath.txt to get recent directory*/
        try
        {
            reader = new Scanner(new File("lastOpenedPath.txt"));
            while(!endOfFileReached)
            {
                try
                {
                    path = reader.nextLine();
                }
                catch(NoSuchElementException e)
                {
                    endOfFileReached = true;
                }
            }
        }
        catch(FileNotFoundException e)
        {
            endOfFileReached = true;
        }
        
        //sets path to the last recorded directory if it exists, otherwise set to home directory
        if(path.equals("ERROR"))
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        else
            fileChooser.setCurrentDirectory(new File(path));
    }
	
	/**
     * Writes last accessed path to a text file, which is the default directory
     * the next time an image file is opened or saved. 
     * @param path - the most recent directory accessed by the user
     */
    private static void saveDirectory(String path)
    {   
        //writes path that is currently accessed to the file lastOpenedPath.txt
        try
        {
            File savedPaths = new File("lastOpenedPath.txt");
            FileWriter writer = new FileWriter("lastOpenedPath.txt");
            writer.write(path);
            writer.close();
        }
        catch (IOException e) 
        {
            System.out.println("Cannot write to file.");
            e.printStackTrace();
        }
    }
	
	/**
     * Converts a <code>BufferedImage</code> of type .png to a .jpg.
     * @param bi Original <code>BufferedImage</code> with alpha (can be exported as .png)
     * @param BufferedImage New <code>BufferedImage</code> without alpha (can be exported as .jpg)
     */
    public static BufferedImage convertPNGtoJPG(BufferedImage bi)
    {
        BufferedImage jpgImage = new BufferedImage(bi.getWidth(),bi.getHeight(),BufferedImage.TYPE_INT_RGB);
        jpgImage.createGraphics().drawImage(bi,0,0, null); //creates a copy of the original image using jpg colour constraints
        return jpgImage;
    }
	
	/**
	 * Returns the values of the independent variables of the data set
	 * @return List<Double> a list of the independent values of the set of data read from the file
	 */
	public static List<Double> getIndependentValues()
	{
		return independentValues;
	}
	
	
	/**
	 * Returns the values of the dependent variables of the data set
	 * @return List<Double> a list of the dependent values of the set of data read from the file
	 */
	public static List<Double> getDependentValues()
	{
		return dependentValues;
	}
	
	/**
	 * Returns the information about the variable names and units in line 1 read from the file if formatted correctly.
	 * This information is used as labels  for the axes of the graph displayed in <code>GraphingTool</code>
	 * @return String[] An array of the information for the axis labels, represented as:
	 * {independent variable name, dependent variable names, unit of independent variable, unit of dependent variable
	 */
	public static String[] getAxisInfo()
	{
		String[] variableInfo = {independentVariable,dependentVariable,independentUnit,dependentUnit};
		return variableInfo;
	}
	
	/**
	 * Returns the name of a currently opened file
	 * @return String - the name of the file that is opened in the main program
	 */
	public static String getFileName()
	{
		return fileName;
	}

}
				
				