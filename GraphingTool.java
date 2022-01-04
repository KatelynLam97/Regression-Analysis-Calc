package regressionAnalysisCalc;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import java.lang.Math;
/**
 * Regression Analysis Calculator
 * 
 * <p>A program in Java Swing that finds the line or curve of best fit given a set of data and graphs it. The user uploads a formatted text file to input their data, and can choose form either 
 * linear, quadratic, power, and exponential models. An analysis of the indepedent and dependent variables are placed on the side. While using the program, the user can view
 * the original data values (by clicking the eye) and view instructions (using the question mark icon). The user can then save a copy of the regression model with the equation, coefficient
 * of determination and graph as a .png or .jpg. </p>
 *<p>Credits: The graphing portion is adapted from an original line graph by Rodrigo Castro, which can be accessed <a href = "https://gist.github.com/roooodcastro/6325153">here</a></p>
 *<p>The sources for the icons are: <ul><li>Open: <a href='https://iconpacks.net/?utm_source=link-attribution&utm_content=11625'>Iconpacks</a></li> <li>Save: 
 * <a href='https://iconpacks.net/?utm_source=link-attribution&utm_content=5341'>Iconpacks</a></li>  <li>View: <a href='https://iconpacks.net/?utm_source=link-attribution&utm_content=6444'>Iconpacks</a></li>
 * <li>Help: <a href='https://iconpacks.net/?utm_source=link-attribution&utm_content=11799'>Iconpacks</a></li></ul></p>
 *
 *<p>Additional sources used to calculate non-linear regression models can be accessed here:<ul><li>Charlotte Taylor (TutorMe Blog): <a href = "https://tutorme.com/blog/post/quadratic-regression/">Quadratic</a></li>
 *<li>Charles Zaiontz (Real Statistics Using Excel): <a href = "https://www.real-statistics.com/regression/power-regression/">Power (log-log method)</a></li>
 *<li>Stefan Waner (Zweig Media): <a href = "https://www.zweigmedia.com/RealWorld/calctopic1/regression.html">Exponential (log method)</a></li></ul>
 *@version Jan 2022
 *@author Katelyn Lam
 */
 public class GraphingTool extends JPanel implements ActionListener
 {
	private static JFrame frame; 
	private static final String workingDir = System.getProperty("user.dir"); //current directory program is running at
	
	//dimensions for the calculator
	private static final int screenWidth = 1000; //width of full screen
	private static final int width = 800; //width of the graphed section
    private static final int height = 600; //height of the displayed interface
	
	//padding constants
    private static final int padding = 25;
    private static final int labelPadding = 25;
	private static final int wordPadding = 22;
	private static final int vInfoPadding = 35;
	private static final int hInfoPadding = 10;
	
	//colour constants for the line/curve of best fit and indibidual points
    private static final Color lineColor = new Color(133, 228, 173, 180);
	private static final Color quadColor = new Color(175,243,248,180);
	private static final Color powColor = new Color(250,152,153,180);
	private static final Color expColor = new Color(249,200,127,180);
    private static final Color pointColor =new Color(44, 102, 230, 180); 
    private static final Color gridColor = new Color(200, 200, 200, 200);
    private static final Stroke GRAPH_STROKE = new BasicStroke(2f); //line thickness
    private static int pointWidth = 4; //diameter of a point on the graph
	
	//used for determining a reasonable scale of a a project on both axes
    private static int numberYDivisions = 10; //number of divisions on the y-axis
	private static int numberXDivisions = 21; //number of divisions on the x-axis
	private static double xScale = 0.0; //width of each section of scale (in pixels)
	private static double yScale = 0.0; //width of each section of scale (in pixels)
	private static double xSectionWidth = 0.0; //width of x-section on the scale
	private static double ySectionWidth = 0.0; //width of y-section on the scale
	
	//a set of fonts used throughout the program
	private static final Font titleFont = new Font("Dialog",Font.PLAIN,16);
	private static final Font defaultFont = new Font("Dialog",Font.PLAIN,12);
	private static final Font expFont = new Font("Dialog", Font.PLAIN, 10);
	private static final Font headerLabel = new Font("Sans-Serif",Font.PLAIN, 16);
	private static final Font subHeaderLabel = new Font("Sans-Serif", Font.PLAIN, 14);
	private static final Font bodyLabel = new Font("Sans-Serif", Font.PLAIN, 12);
	private static FontMetrics metrics;

	//initialization of each button, which will be displayed in the UI
	private static JButton openButton;
	private static JButton saveButton;
	private static JButton viewData;
	private static JButton infoButton;
	private static JDialog dataTable;
	private static JDialog helpWindow;
	private static JComboBox<String> regressionList; //a drop-down menu to select the proper regression model
	
	//labels used in the help dialog box
	private static JLabel introLabel;
	private static JLabel instructionsLabel;
	
	//initializes icons, or visual representations for each button
	private static final int iconWidth = 32;
	private static Icon openIcon = new ImageIcon(workingDir + "\\Open.png");
	private static Icon saveIcon = new ImageIcon(workingDir + "\\Save.png");
	private static Icon viewIcon = new ImageIcon(workingDir + "\\View.png");
	private static Icon infoIcon = new ImageIcon(workingDir + "\\Info.png");
	
	/*extrema for the maximum and minimum values of x and y.
	abs values are the min/max values that fit the scale. xBorder and yBorder is the constant added to the max value.*/
	private static double absLowestX = 0.0;
	private static double absLowestY = 0.0;
	private static double absMaxX = 0.0;
	private static double absMaxY = 0.0;
	private static double xBorder = 0.5;
	private static double yBorder = 0.5;
	private static double minXValue = 0.0;
	private static double maxXValue = 0.0;
	private static double minYValue = 0.0;
	private static double maxYValue = 0.0;
	
	private static int lastSelectedCB = 0; //the option last changed by the user on the regressionList
	
	private static boolean fileSelected = false; //state of whether a file has been selected to run the program
	 
	private static BufferedImage imageCopy; //a copy of the currently-displayed graph
	
	//variables for types of regression models -> boolean definitions is the state of whether a regression model is selected
	private static boolean isLinearRegression = false;
	private static boolean isQuadraticRegression = false;
	private static boolean isPowerRegression = false;
	private static boolean isExponentialRegression = false;
	private static boolean regressionSelected = false;
	private static String[] regressionTypes = {"None","Linear","Quadratic","Power","Exponential"}; //informal representation of the names of each regression model
	private static String[] regressionLabels = {"N/A","Linear Regression", "Quadratic Regression", "Power Regression", "Exponential Regression"}; //formal representation of the names of each regression model
	private static int accessorIndex = 0; //index of regressionLabels array (0 - 4)
	private static double[] regArgs; //arguments for a chosen regression model
	
	//data set of independent and dependent variable values
	private static List<Double> xData; //independent variable values
    private static List<Double> yData; //dependent variable values
	private static double[] xDataArray; //array form of xDataArray
	private static double[] yDataArray; //array form of y-DataArra
	private static String[] variableInfo; //names and units of independent and dependent variables
	
	//Points for the graphed set of data
	private static List<Double> graphX;
	private static List<Double> graphY;
	
	//maximum number of decimal places for a given amount
	private static int dpX;
	private static int dpY;
	
	/**
	 * Constructor for interface. Initializes all buttons, combo boxes, and data lists 
	 */
	public GraphingTool() 
	{
		/*initializes lists that hold all independent and dependent values. A value
		in xData corresponds to a value in yData with the same index*/
		xData = new LinkedList<Double>();
		yData = new LinkedList<Double>();
		
		//initializes lists for points displayed on the graph
		graphX = new LinkedList<Double>();
		graphY = new LinkedList<Double>();

		//creates open, save, and view data buttons and places on the window
	    openButton = new JButton(openIcon);
		saveButton = new JButton(saveIcon);
		viewData = new JButton(viewIcon);
		infoButton = new JButton(infoIcon);
		
		openButton.setBounds(width + hInfoPadding,height - iconWidth - 2,iconWidth,iconWidth);
		openButton.setActionCommand("open");
		
		saveButton.setBounds(width + hInfoPadding + iconWidth + 10,height - iconWidth - 2,iconWidth,iconWidth);
		saveButton.setActionCommand("save");

		viewData.setBounds(width + hInfoPadding + 2*iconWidth + 20, height - iconWidth - 2, iconWidth,iconWidth);
		viewData.setActionCommand("view");
		
		infoButton.setBounds(width + hInfoPadding + 3*iconWidth + 30, height - iconWidth - 2, iconWidth,iconWidth);
		infoButton.setActionCommand("help");
		
		//add events to each button
		openButton.addActionListener(this);
		saveButton.addActionListener(this);
		viewData.addActionListener(this);
		infoButton.addActionListener(this);
		
		//create combo box to select regression type
		regressionList = new JComboBox<>(regressionTypes);
		regressionList.setBounds(width + hInfoPadding + 10,vInfoPadding +14*wordPadding + 10,100,20);
		regressionList.setSelectedIndex(lastSelectedCB);
		regressionList.addActionListener(this);
		regressionList.setVisible(false);
		
		//creates introduction label and instructions
		String intro = "<html>Regression Analysis <p>Calculator</p></html>";
		String instructions = "<html> Please choose a text file to begin.<p></p><p>Author: Katelyn Lam</p><p>Date: Jan 2022</p><p>Version: 1.0</p></html>";
		introLabel = new JLabel(intro);
		instructionsLabel = new JLabel(instructions);
		introLabel.setBounds(width + 5, vInfoPadding,200, 50);
		introLabel.setFont(headerLabel);
		instructionsLabel.setBounds(width + 5, vInfoPadding, 200, 200);
		instructionsLabel.setFont(bodyLabel);
		
    }
	
	/**
	 * Draws the UI, mandatory method called when a JFrame() is initialized
	 * @param g A Java Graphics object that draws the UI on the JFRame
	 */
	protected void paintComponent(Graphics g) 
	{
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
		metrics = g2.getFontMetrics();
		
		introLabel.setVisible(false);
		instructionsLabel.setVisible(false);

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		//determines width of axes
        xScale = ((double) width - (2 * padding) - labelPadding) / (numberXDivisions - 1);
        yScale = ((double) getHeight() - 2 * padding - labelPadding) / numberYDivisions;
				
		// draw white background
        g2.setColor(Color.WHITE);
        g2.fillRect(padding + labelPadding, padding, width - (2 * padding) - labelPadding, getHeight() - 2 * padding - labelPadding);
        g2.setColor(Color.BLACK);
		
		
		if(fileSelected)
		{
			//initializes values and draws a scatterplot
			deepCopy(xData,graphX);
			deepCopy(yData, graphY);
			regressionList.setVisible(true);
			drawAxes(minXValue, maxXValue, minYValue, maxYValue,g2);
			drawAxesLabels(g2);
			
			List<Point> scatterPoints = determinePoints();
			List<Point> linePoints = new LinkedList<Point>();
			drawGraph(false,scatterPoints,g2);
		
			//creates a linear regression
			if(isLinearRegression)
			{	
				regArgs = AnalysisLibrary.linearRegression(xDataArray,yDataArray);
				createFunction(regArgs,1,20);
				linePoints = determinePoints();
				
			}
			
			//creates a quadratic regression
			else if(isQuadraticRegression)
			{
				regArgs = AnalysisLibrary.quadraticRegression(xDataArray,yDataArray);
				createFunction(regArgs,2,2000);
				linePoints = determinePoints();
			}
			
			//creates a power regression
			else if(isPowerRegression)
			{
				regArgs = AnalysisLibrary.powerRegression(xDataArray,yDataArray);
				createFunction(regArgs,3,2000);
				linePoints = determinePoints();
			}
			
			//creates an exponential regression
			else if(isExponentialRegression)
			{
				regArgs = AnalysisLibrary.exponentialRegression(xDataArray,yDataArray);
				createFunction(regArgs,4,2000);
				linePoints = determinePoints();
			}
			
			drawGraph(true,linePoints,g2); //draws a continuous function representing the selected regression model (none is drawn for no regression)
		
			//adds analysis of data on the right side (see AnalysisLibrary)
			g2.setColor(Color.BLACK);
			String fileName = "File: " + FileManager.getFileName();
			String meanLabel = "Mean";
			String xDataLabel = "x: ";
			String yDataLabel = "y: ";
			String medianLabel = "Median";
			String rangeLabel = "Range: ";
			String q1Label = "Q1: ";
			String q3Label = "Q3: ";
			String interquartileRangeLabel = "IQR: ";
			String varianceLabel = "Variance: ";
			String stDevLabel = "Standard Deviation: ";
			String covarianceLabel = "Covariance: ";
			String regressionLabel = "Regression type: ";
			String equationLabel = "Equation: ";
			String rFactorLabel = "r: "; 
			String rSquaredLabel = "R-squared: ";
			String corrTypeLabel = "Linear Correlation Type: ";
			
			//measures width of labels to determine spacing for value labels
			int rangeWidth = metrics.stringWidth(rangeLabel);
			int q1Width = metrics.stringWidth(q1Label);
			int q3Width = metrics.stringWidth(q3Label);
			int iqrWidth = metrics.stringWidth(interquartileRangeLabel);
			int covWidth = metrics.stringWidth(covarianceLabel);
			int regWidth = metrics.stringWidth(regressionLabel);
			int rWidth = metrics.stringWidth(rFactorLabel);
			int rSquaredWidth = metrics.stringWidth(rSquaredLabel);

			//draws labels and corresponding values
			g2.drawString(fileName, width + hInfoPadding, vInfoPadding);
			g2.drawString(meanLabel, width + hInfoPadding, vInfoPadding + wordPadding);
			g2.drawString(xDataLabel, width + hInfoPadding + 10, vInfoPadding + 2*wordPadding);
			int yMeanSpacing = drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.mean(xDataArray),dpX), width + hInfoPadding + 25,vInfoPadding +2*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(yDataLabel, width + 2*hInfoPadding + yMeanSpacing + 35, vInfoPadding + 2*wordPadding);
			drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.mean(yDataArray),dpY), width + 2*hInfoPadding + yMeanSpacing + 50,vInfoPadding +2*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(medianLabel, width + hInfoPadding, vInfoPadding + 3*wordPadding);
			g2.drawString(xDataLabel, width + hInfoPadding + 10, vInfoPadding + 4*wordPadding);
			int yMedianSpacing = drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.median(xDataArray,false),dpX), width + hInfoPadding + 25,vInfoPadding +4*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(yDataLabel, width + 2*hInfoPadding + yMedianSpacing + 35, vInfoPadding + 4*wordPadding);
			drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.median(yDataArray,false),dpY), width + 2*hInfoPadding + yMedianSpacing + 50,vInfoPadding +4*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(rangeLabel, width + 2*hInfoPadding + yMedianSpacing + 35, vInfoPadding + 5*wordPadding);
			drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.range(yDataArray),dpY), width + 2*hInfoPadding + yMedianSpacing + 35 + rangeWidth,vInfoPadding +5*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(q1Label, width + 2*hInfoPadding + yMedianSpacing + 35, vInfoPadding + 6*wordPadding);
			drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.interquartileRange(yDataArray)[0],dpY), width + 2*hInfoPadding + yMedianSpacing + 35 + q1Width,vInfoPadding +6*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(q3Label, width + 2*hInfoPadding + yMedianSpacing + 35, vInfoPadding + 7*wordPadding);
			drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.interquartileRange(yDataArray)[1],dpY), width + 2*hInfoPadding + yMedianSpacing + 35+ q3Width,vInfoPadding +7*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(interquartileRangeLabel, width + 2*hInfoPadding + yMedianSpacing + 35, vInfoPadding + 8*wordPadding);
			drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.interquartileRange(yDataArray)[2],dpY), width + 2*hInfoPadding + yMedianSpacing + 35 + iqrWidth,vInfoPadding +8*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(varianceLabel, width + hInfoPadding, vInfoPadding + 9*wordPadding);
			g2.drawString(xDataLabel, width + hInfoPadding + 10, vInfoPadding + 10*wordPadding);
			int yVarSpacing = drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.variance(xDataArray,true),dpX), width + hInfoPadding + 25,vInfoPadding +10*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(yDataLabel, width + 2*hInfoPadding + yVarSpacing + 35, vInfoPadding + 10*wordPadding);
			drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.variance(yDataArray,true),dpY), width + 2*hInfoPadding + yVarSpacing + 50,vInfoPadding +10*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(stDevLabel, width + hInfoPadding, vInfoPadding + 11*wordPadding);
			g2.drawString(xDataLabel, width + hInfoPadding + 10, vInfoPadding + 12*wordPadding);
			int yStDevSpacing = drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.stDev(xDataArray,true),dpX), width + hInfoPadding + 25,vInfoPadding +12*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(yDataLabel, width + 2*hInfoPadding + yStDevSpacing + 35, vInfoPadding + 12*wordPadding);
			drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.stDev(yDataArray,true),dpY), width + 2*hInfoPadding + yStDevSpacing + 50,vInfoPadding +12*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(covarianceLabel, width + hInfoPadding, vInfoPadding + 13*wordPadding);
			drawLabel(AnalysisLibrary.roundDecimal(AnalysisLibrary.covariance(xDataArray,yDataArray),dpY), width + hInfoPadding + covWidth,vInfoPadding +13*wordPadding + 1, Color.WHITE, Color.BLACK, g2);
			g2.drawString(regressionLabel, width + hInfoPadding, vInfoPadding + 14*wordPadding);
			
			//adds extra labels about the selected regression model (r-value, rSquared, equation)
			if(regressionSelected)
			{
				String equation = "";
				String rFactor = "N/A";
				String rSquared = "";
				double dRSquared = 0.0;
				String exp = "";
				int expLocation = 0;
			
				String terms[] = new String[regArgs.length];
					
				for(int i = 0; i < terms.length; i++)
				{
					if(regArgs[i] >= 0 && i > 0 && !isPowerRegression && !isExponentialRegression)
						terms[i] = "+ " + AnalysisLibrary.roundDecimal(regArgs[i],3);
					else 
						terms[i] = AnalysisLibrary.roundDecimal(regArgs[i],3);
				}
				
				g2.drawString(equationLabel, width + hInfoPadding + 20, vInfoPadding + 16*wordPadding + 10);
				g2.drawString(rFactorLabel, width + hInfoPadding + 20, vInfoPadding + 18*wordPadding + 10);
				g2.drawString(rSquaredLabel, width + hInfoPadding + 20, vInfoPadding + 19*wordPadding + 10);
				
				//initializes equations and formats labels for each regression model
				if(isLinearRegression)
				{
					equation = "y = " + terms[0]+"x " + terms[1];
					rFactor = AnalysisLibrary.roundDecimal(AnalysisLibrary.rFactor(xDataArray,yDataArray),3);
					String corrType = AnalysisLibrary.correlationType(Double.parseDouble(rFactor));
					dRSquared = AnalysisLibrary.rSquared(xDataArray,yDataArray,1);
					g2.drawString(corrTypeLabel, width + hInfoPadding + 20, vInfoPadding + 20*wordPadding + 10);
					drawLabel(corrType, width + hInfoPadding + 25,vInfoPadding +20*wordPadding + 30, Color.WHITE, Color.BLACK, g2);
					drawLabel(equation, width + hInfoPadding + 25,vInfoPadding +16*wordPadding + 30, Color.WHITE, Color.BLACK, g2);
				}
				
				else if(isQuadraticRegression)
				{
					equation = "y = " + terms[0];
					exp = "2";
					expLocation = equation.length();
					equation = equation + "x " + terms[1] + "x " + terms[2];
					drawLabel(equation,exp, width + 10,vInfoPadding +16*wordPadding + 30, Color.WHITE, Color.BLACK, expLocation,g2);
					dRSquared = AnalysisLibrary.rSquared(xDataArray,yDataArray,2);
				}
				
				else if(isPowerRegression)
				{
					exp = terms[1];
					equation = "y = " + terms[0] + "x      ";
				    expLocation = equation.length() - exp.length() - 2;
					drawLabel(equation,exp, width + hInfoPadding + 25,vInfoPadding +16*wordPadding + 30, Color.WHITE, Color.BLACK, expLocation,g2);
					rFactor = terms[2];
					dRSquared = AnalysisLibrary.rSquared(xDataArray,yDataArray,3);
				}
				
				else if(isExponentialRegression)
				{
					exp = "x";
					equation = "y = " + terms[0]+"("+ terms[1]+")";
					expLocation = equation.length() - 1;
					drawLabel(equation,exp, width + hInfoPadding + 25,vInfoPadding +16*wordPadding + 30, Color.WHITE, Color.BLACK, expLocation,g2);
					rFactor = terms[2];
					dRSquared = AnalysisLibrary.rSquared(xDataArray,yDataArray,4);
				}
				
				
				drawLabel(rFactor, width + hInfoPadding + 20 + rWidth,vInfoPadding +18*wordPadding + 10, Color.WHITE, Color.BLACK, g2);
				rSquared = AnalysisLibrary.roundDecimal(dRSquared,3);
				drawLabel(rSquared, width + hInfoPadding + 20 + rSquaredWidth,vInfoPadding +19*wordPadding + 10, Color.WHITE, Color.BLACK, g2);
				imageCopy = drawImageFile(scatterPoints,linePoints,equation,dRSquared,exp,expLocation); //creates a copy of the graph with regression model
			}
			else
				imageCopy = drawImageFile(scatterPoints); //saves an image of the graph without the regression model
		}
		else
		{
			//draws a blank axes and saves an image with the same axes. Shows label with title of program and creator
			drawAxes(0.5,20.0,0.5,99.5,g2);
			imageCopy = drawImageFile(null);
			introLabel.setVisible(true);
			instructionsLabel.setVisible(true);
		}
				
	}

	/**
	 * Resets the values of the independent and dependent variables in the data set
	 * @param dataX a list of independent variable values
	 * @param dataY a list of independent variable values
	 */
    public void initializeData(List<Double> dataX, List<Double> dataY) 
	{
		//ensures that the values of xData and yData are copies of dataX and dataY, not just the same reference
        deepCopy(dataX,xData);
		deepCopy(dataY,yData);
		
		//creates copies of xData and yData as arrays to perform calculations
		xDataArray = dataStructureConversion(dataX);
		yDataArray = dataStructureConversion(dataY);
		
		//determines max and min values for each data set
		minXValue = 0.0;
		maxXValue = 0.0;
		minYValue = 0.0;
		maxYValue = 0.0;
		determineExtrema();
		
		//resets the graph
        invalidate();
        this.repaint();
    }
	
	/**
	 * Determines the maximum and minimum values of the independent and dependent variables
	 */
    private static void determineExtrema() 
	{
		double xWidth; //range of x-axis
		double yWidth; //range of y-axis
		
		//determines max and min values for the data set
		for(int i = 0; i < xData.size(); i++)
		{
			if(xData.get(i) < minXValue || i == 0)
				minXValue = xData.get(i);
			
			else if(xData.get(i) > maxXValue || i == 0)
				maxXValue = xData.get(i);
			
			if(yData.get(i) < minYValue || i == 0)
				minYValue = yData.get(i);
			
			else if(yData.get(i) > maxYValue || i == 0)
				maxYValue = yData.get(i);
        }
		
		//determines buffer for the scale (extrema +/- 5%(minValue))
		yBorder = 0.05 * minYValue;
		xBorder = 0.05 * minXValue;
		
		//determines the maximum number of decimal places for each data set (to account for significant digits)
		dpX = AnalysisLibrary.maxNumDecimalPlaces(xDataArray);
		dpY = AnalysisLibrary.maxNumDecimalPlaces(yDataArray);
	}
	
	/**
	 * Determines a list of points drawn on the graph, expressed in terms of the scale of the interface.
	 * The list of points is determined using <code>graphX</code> and <code>graphY</code>, which does not
	 * necessarily have to equal the number of data points.
	 * @return List<Point> - list of all points on the graph
	 */
	 private static List<Point> determinePoints()
	 {
		 LinkedList<Point> graphPoints = new LinkedList<Point>();
		
		//creates a list of points to be graphed with coordinate (xData value, yData value)
        for (int i = 0; i < graphX.size(); i++) 
		{
            int x1 = Integer.parseInt(AnalysisLibrary.roundDecimal(((graphX.get(i)  - absLowestX)/xSectionWidth)* xScale + padding + labelPadding,0));
            int y1 = Integer.parseInt(AnalysisLibrary.roundDecimal(((absMaxY - graphY.get(i))/ySectionWidth) * yScale + padding,0));
            graphPoints.add(new Point(x1,y1));
        }
		return graphPoints;
	 }
	
	/**
	 * Draws a rectangular label with text and no superscripts, and determines the length of the label
	 * @param text - The text displayed on the label
	 * @param xPos - The x-coordinate of the label on the interface
	 * @param yPos - The y-coordinate of the label on the interface
	 * @param labelColour - The background colour of the label
	 * @param textColour - The colour of the label text
	 * @param g2 - An instance of Graphics2D that allows label to be drawn on an object
	 * @return int - Length of the label in pixels
	 */
	private static int drawLabel(String text, int xPos, int yPos, Color labelColour,Color textColour, Graphics2D g2)
	{
		int padding = 5; //default padding for label
		int textWidth = metrics.stringWidth(text);
		drawLabel(text, "", xPos, yPos, labelColour, textColour, 0, g2);
		return textWidth + padding;
	}
	
	/**
	 * Draws a rectangular label with text and superscripts.
	 * @param text - The text displayed on the label
	 * @param superscript - The value of the superscript
	 * @param xPos - The x-coordinate of the label on the interface
	 * @param yPos - The y-coordinate of the label on the interface
	 * @param labelColour - The background colour of the label
	 * @param textColour - The colour of the label text
	 * @param index - The location of <code>text</code> where the superscript is inserted (zero-based index)
	 * @param g2 - An instance of Graphics2D that allows label to be drawn on an object
	 * @return int - Length of the label in pixels
	 */
	private static void drawLabel(String text, String superscript, int xPos, int yPos, Color labelColour, Color textColour, int index, Graphics2D g2)
	{
		int padding = 5;
		String subText = text.substring(0,index + 1);
		int textWidth = metrics.stringWidth(text);
		int subTextWidth = metrics.stringWidth(subText);
		
		//draws label without superscript
		g2.setColor(labelColour);
		g2.fillRect(xPos, yPos - 12, textWidth + padding *2, 16);
		g2.setColor(textColour);
		
		//inserts superscript in a smaller font and lower yValue at indicated index location
		g2.drawString(text,xPos + padding, yPos);
		g2.setFont(expFont);
		g2.drawString(superscript,xPos + padding + subTextWidth, yPos - 6);
		g2.setFont(defaultFont); //resets font to default
	}
	
	/**
	 * Draws the axes of the graph given the max and min values for <i>x</i> and <i>y</i>
	 * @param lowerX - the minimum value of <i>x</i> (without border adjustment for display on the graph)
	 * @param upperX - the maximum value of <i>x</i> (without border adjustment for display on the graph)
	 * @param lowerY - the minimum value of <i>y</i> (without border adjustment for display on the graph)
	 * @param upperY - the minimum value of <i>y</i> (without border adjustment for display on the graph)
	 * @param g2 - An instance of Graphics2D that allows axes to be drawn on an object
	 */
	private static void drawAxes(double lowerX, double upperX, double lowerY, double upperY, Graphics2D g2)
	{	
		//determines bounds for x and y that are represented on the graph (max and min values +/- border size)
		if((lowerY - yBorder) > 0)
			absLowestY = lowerY - yBorder;
		else
			absLowestY = 0.0;
		
		if((lowerX - xBorder) > 0)
			absLowestX = lowerX - xBorder;
		else
			absLowestX = 0.0;
		
		absMaxX = upperX + xBorder;
		absMaxY = upperY + yBorder;
		
		// create hatch marks and grid lines for y axis.
        for (int i = 0; i < numberYDivisions + 1; i++) 
		{
            int x0 = padding + labelPadding;
            int x1 = pointWidth + padding + labelPadding;
            int y0 = height - ((i * (height - padding * 2 - labelPadding)) / numberYDivisions + padding + labelPadding);
            int y1 = y0;
			ySectionWidth = (absMaxY - absLowestY)/(double)numberYDivisions;
			
            g2.setColor(gridColor);
			g2.drawLine(padding + labelPadding + 1 + pointWidth, y0, width - padding, y1);
			g2.setColor(Color.BLACK);
			double currentLabel = absLowestY+ i * Double.parseDouble(AnalysisLibrary.roundDecimal(ySectionWidth,dpY));
			String yLabel = AnalysisLibrary.roundDecimal(currentLabel,dpY)+ " ";
			int labelWidth = metrics.stringWidth(yLabel);
			g2.drawString(yLabel, x0 - labelWidth - 5, y0 + (metrics.getHeight() / 2) - 3);
            
            g2.drawLine(x0, y0, x1, y1);
        }
		
		// create hatch marks and grid lines for x-axis.
        for (int i = 0; i < numberXDivisions; i++) 
		{
            int x0 = i * (width - padding * 2 - labelPadding) / (numberXDivisions - 1) + padding + labelPadding;
            int x1 = x0;
            int y0 = height - padding - labelPadding;
            int y1 = y0 - pointWidth;
            xSectionWidth = (absMaxX- absLowestX)/(double)(numberXDivisions - 1);
			
			g2.setColor(gridColor);
			g2.drawLine(x0, height - padding - labelPadding - 1 - pointWidth, x1, padding);
			g2.setColor(Color.BLACK);
			
			double currentLabel = (absLowestX)+ i * Double.parseDouble(AnalysisLibrary.roundDecimal(xSectionWidth,dpX));
			String xLabel = AnalysisLibrary.roundDecimal(currentLabel,dpX) + " ";

			int labelWidth = metrics.stringWidth(xLabel);
			g2.drawString(xLabel, x0 - labelWidth / 2, y0 + metrics.getHeight() + 3);
            g2.drawLine(x0, y0, x1, y1);
        }
		
		// create x and y axes 
        g2.drawLine(padding + labelPadding, height - padding - labelPadding, padding + labelPadding, padding);
        g2.drawLine(padding + labelPadding, height - padding - labelPadding, width - padding, height - padding - labelPadding);
	}
	
	/**
	 * Draws titles and axis labels (for <i>x</i> and <i>y</i>) on the graph. Called with <code>drawAxes()</code>
	 * @param g2 - An instance of Graphics2D that allows axis labels to be drawn on an object. Must be same reference if <code>drawAxes()</code> is called
	 */
	private static void drawAxesLabels(Graphics2D g2)
	{			
		//add title of graph
		String title = variableInfo[1] + " vs. " + variableInfo[0];
		int titleWidth = metrics.stringWidth(title);
		g2.setFont(titleFont);
		g2.drawString(title, (int)(width/2 - titleWidth/2 - 10), padding - 8); 
		g2.setFont(defaultFont);
		
		//adds label for y-axis
		String yAxisLabel = variableInfo[1] + " (" + variableInfo[3] + ")";
		g2.drawString(yAxisLabel, padding - 10, padding - 10);
		
		//adds label for x-axis
		String xAxisLabel = variableInfo[0] + " (" + variableInfo[2] + ")";
		int labelXWidth = metrics.stringWidth(xAxisLabel);
		g2.drawString(xAxisLabel, width - padding - labelXWidth, height - 10);
	}
	
	/**
	 * Draws the graph onto a pre-defined axes. The method <code>drawAxes()</code> must be called first.
	 * @param continuous - Represents state of whether graphed points are connected or not. <code>continuouts</code> is true if the graph is a smooth curve, false if it is a scatter plot
	 * @param points - points (independent variable x, dependent variable y) to be plotted on the graph
	 * @param g2 - An instance of Graphics2D that allows graph to be drawn on an object. Must be same reference as drawAxes()
	 */
    private static void drawGraph(boolean continuous, List<Point>points, Graphics2D g2)
	{
		//draws a continuous line graph/function by connecting each point with a line
		if(continuous)
		{
			Stroke oldStroke = g2.getStroke();
			
			if(isLinearRegression)
				g2.setColor(lineColor);
			else if(isQuadraticRegression)
				g2.setColor(quadColor);
			else if(isPowerRegression)
				g2.setColor(powColor);
			else if(isExponentialRegression)
				g2.setColor(expColor);
			
			g2.setStroke(GRAPH_STROKE);
			
			for (int i = 0; i < points.size() - 1; i++) 
			{
				int x1 = points.get(i).x;
				int y1 = points.get(i).y;
				int x2 = points.get(i + 1).x;
				int y2 = points.get(i + 1).y;
				g2.drawLine(x1, y1, x2, y2);
			}
		}
		
		//draws a scatter plot by plotting points with no connection
		else
		{
			g2.getStroke();
			g2.setColor(pointColor);
			for (int i = 0; i < points.size(); i++) 
			{
				int x = points.get(i).x - pointWidth;
				int y = points.get(i).y - pointWidth;
				int ovalW = pointWidth;
				int ovalH = pointWidth;
				g2.fillOval(x, y, ovalW, ovalH);
			}
		}
	}

	/**
	 * Draws a BufferedImage representing a scatterplot, to be used when the file is saved. <code>drawImageFile()</code> is called whenever a change to the displayed graph is made
	 * and no regression model is shown
	 * @param dataSet - a list of points that is plotted on the displayed graph
	 * @return BufferedImage - the BufferedImage with the scatterplot drawn on it
	 */
	private static BufferedImage drawImageFile(List<Point> dataSet)
	{
		BufferedImage newImage = new BufferedImage(screenWidth,height,BufferedImage.TYPE_INT_ARGB); //creates a new BuffferedImage to represent graph
		Graphics2D gBi = newImage.createGraphics(); //allows Graphics2D to draw on the BufferedImage
		
		//sets a white image background
		gBi.setColor(Color.WHITE); 
		gBi.fillRect(0,0,screenWidth,height);
		
		//draws the set of axes (and scatterplot if exists) that is displayed on the BufferedImage
		if(dataSet != null)
		{
			drawAxes(minXValue, maxXValue, minYValue, maxYValue,gBi);
			drawAxesLabels(gBi);
			drawGraph(false,dataSet,gBi);
		}
		else
			drawAxes(0.5, 19.5, 0.5, 99.5,gBi); //draws a blank set of axes if no data exists
		
		return newImage;
	}
	
	/**
	 * Draws a BufferedImage of the scatterplot with the selected regression model. The regression type, equation, and coefficient of determination (r-squared) is displayed on the pane
	 * to the right.
	 * @param dataSet - a list of points that is plotted on the displayed graph
	 * @param regressionSet - a list of points that corresponds to the equation of the regression model
	 * @param eq - the equation that represents the regression set
	 * @param rSquared - the coefficient of determination
	 * @param expArgument - the value of any present exponents. If there are no exponents/superscripts, <code>expArgument = ""</code>
	 * @param expIndex - location of the String where expArgument can be found. If there is no superscript, set to 0.
	 * @return BufferedImage - the BufferedImage with the scatterplot,regression model, equation and r-squared value
	 */ 
	private static BufferedImage drawImageFile(List<Point> dataSet, List<Point> regressionSet, String eq, double rSquared, String expArgument, int expIndex)
	{
		//intiializes a BufferedImage and Graphics2D tool to draw on it. Sets background to white
		BufferedImage newImage = new BufferedImage(screenWidth,height,BufferedImage.TYPE_INT_ARGB);
		Graphics2D gBi = newImage.createGraphics();
		gBi.setColor(Color.WHITE);
		gBi.fillRect(0,0,screenWidth,height);
		
		if(dataSet != null)
		{	
			//draws scatter plot of original data
			drawAxes(minXValue, maxXValue, minYValue, maxYValue,gBi);
			drawAxesLabels(gBi);
			drawGraph(false,dataSet,gBi);
			
			if(regressionSet != null)
			{
				//draws line or curve of best fit using the regression points
				drawGraph(true, regressionSet,gBi);
				gBi.setColor(Color.BLACK);
				gBi.drawString(regressionLabels[accessorIndex],width + hInfoPadding,vInfoPadding);
				
				//creates label describing the equation of the regression and r-squared value
				drawLabel(eq,expArgument, width + hInfoPadding,vInfoPadding + wordPadding, Color.WHITE, Color.BLACK, expIndex,gBi);
				String rSquaredString = "R-squared: " + AnalysisLibrary.roundDecimal(rSquared,3);
				gBi.drawString(rSquaredString, width + hInfoPadding, vInfoPadding + 2*wordPadding);
				
				//creates a label for the r-value if it exists (all regression models except for quadratic)
				if(accessorIndex != 2)
				{
					String rFactor = "r: " + AnalysisLibrary.roundDecimal(Math.sqrt(rSquared),3);
					gBi.drawString(rFactor, width + hInfoPadding, vInfoPadding + 3 * wordPadding);
				}
			}
		}
		
		else
			drawAxes(0.5, 19.5, 0.5, 99.5,gBi); //draws blank axes if no data exists
		
		return newImage;
	}
	
	/**
	 * Creates a message windpw with a table of the independent and dependent values. Appears when 'view' is clicked.
	 * @param ref - The main JFrame container of the UI
	 */
	private static void createDataTable(JFrame ref)
	{
		dataTable = new JDialog(ref, "Data: " + FileManager.getFileName()); //creates a new JDialog box to hold the table
		
		//stores table headers as a String[] and data as a Double[2][xData.length], which includes all independent and dependent values
		String[] columns = {variableInfo[0] + " (" + variableInfo[2] + ")",variableInfo[1] + " (" + variableInfo[3] + ")"};
		Double[][] data = new Double[xDataArray.length][2];	
		for(int i = 0; i < data.length; i++)
		{
			for(int j = 0; j < data[i].length; j++)
			{
				if(j == 0)
					data[i][j] = xDataArray[i];
				else
					data[i][j] = yDataArray[i];
			}
		}
		
		//creates a new JTable and puts it in the JDialog
		JTable table = new JTable(data,columns);
		JScrollPane tablePanel = new JScrollPane(table);
		dataTable.add(tablePanel);
		dataTable.setSize(200 + 2* padding, (data.length + 1) * 20  + wordPadding);
		dataTable.setVisible(true);
	}	
	
	/**
	 * Creates a message window that explains how the program works. Appears when 'help' is clicked.
	 * @param ref - The main JFrame container of the UI
	 */
	private static void createHelp(JFrame ref)
	{
		String helpSource = "HelpDescription.html";
		URL helpURL = GraphingTool.class.getResource(helpSource);
		JEditorPane helpPane = new JEditorPane(); //creates a JEditorPane that displays the HTML file
		JScrollPane scroller = new JScrollPane(helpPane);
		helpWindow = new JDialog(ref, "Help (?)");
		
		helpPane.setEditable(false);
		
		//opens HTML file and display on the JEditorPane
		if(helpURL == null)
			JOptionPane.showMessageDialog(ref, "ERROR: cannot access " + helpSource);
		else
		{
			try
			{
				helpPane.setPage(helpURL);
			}
			catch(IOException e)
			{
				JOptionPane.showMessageDialog(ref, "ERROR: Attempted to open bad URL.");
			}
			
			//adds help file to the scroller and display on JDialog
			scroller.setPreferredSize(new Dimension(500,500));
			scroller.setMinimumSize(new Dimension(500,200));
			helpWindow.add(scroller);
			helpWindow.setSize(new Dimension(500,500));
			helpWindow.setVisible(true);
		}

		//adds JPanel to the JDialog
		
	}
	
	/**
	 * Creates a list of points for a linear, quadratic, power, or exponential function within the bounds of the scale. Each value of <i>x</i> which ranges
	 * from the lowest value of <i>x</i> on the x-axis to its highest value, and is added to <code>graphX</code>. Its corresponding value of <i>y</i>
	 * is added to <code>graphY</code>.
	 * @param args - the arguments returned after a regression is performed (see Analysis Library)
	 * @param regIndex - an integer used to refer to the regression model. Ranges from 1 - 4 where 1: Linear, 2: Quadratic, 3: Power, 4: Exponential
	 * @param numPoints - the number of points used to represent the function. Recommended: 20 for linear, at least 1000 for non-linear to represent a smooth curve
	 */
	private static void createFunction(double[] args, int regIndex, int numPoints)
	{
		//reinitializes points plotted on the axes
		graphX.clear();
		graphY.clear();
		 
		//determines range of x-values
		double startXValue = absLowestX;
		double finalXValue = absMaxX;
		double xIncrement = (finalXValue - startXValue)/(double)numPoints; //amount x is increased by
		double currentXValue;
		double currentYValue;
		
		//adds each value of (x,y) as long as they are witbin the range of the set of axes. y is expressed as a function of x
		for(int i = 0; i <( numPoints + 1); i++)
		{	
			currentXValue = startXValue + i *xIncrement;
			currentYValue = AnalysisLibrary.computeFunction(args,regIndex,true,currentXValue);
			
			if(currentYValue > absLowestY && currentYValue < absMaxY)
			{
				graphX.add(currentXValue);
				graphY.add(currentYValue);
			}
		}
	 }

	
	/**
	 * converts a List <Double> to a primitive double[]
	 * @param list - list from which Double values are converted
	 * @return double[] - contents of list as a primitive type double
	 */
	private static double[] dataStructureConversion(List<Double> list)
   {
		 double[] converted = new double[list.size()];
		 for(int i = 0; i < list.size(); i++)
		 {
			converted[i] = list.get(i);
		 }
		 return converted;
	 }
	
	/**
	 * Copies all data from a source list to another list. This ensures that both the actual list and the source have 
	 * the same values but different object references (hence a deep copy)
	 * @param source - a list of type <code>Double</code> where the data is copied from
	 * @param ref - The list where the data from <code>source</code> is copied to. Will be the same length as <code>source</code>
	 */
	private static void deepCopy(List<Double>source, List<Double>ref)
	{
		ref.clear();
		for(int i = 0; i < source.size(); i++)
		{
			ref.add(i,source.get(i));
		}
	}
	
	/**
	 * Event-handler for if a <code>JButton</code> is pressed or <code>JComboBox</code> is selected
	 * @param e a recorded event when a user clicks on a <code>JComponent</code> with an <code>ActionListener</code>
	 */
	public void actionPerformed(ActionEvent e)
	{
		//opens a file if open is pressed, redraws graphs, reinitializes all data values, and resets table
		if((e.getActionCommand()).equals("open"))
		{
			do
			{
				FileManager.openFile();
			}
			while(FileManager.getFileName() == null);
			
			variableInfo = FileManager.getAxisInfo();
			this.initializeData(FileManager.getIndependentValues(),FileManager.getDependentValues());
			
			if(FileManager.isFileOpened())
				fileSelected = true;
			else
				FileManager.openFile();
			
			//closes the dialog for the table if it there is no data
			if(dataTable != null)
			{
				JDialog closeTable = (JDialog) SwingUtilities.getRoot(dataTable);
				closeTable.dispose();
			}
		}
		
		//saves a picture of the graph if the save button is pressed
		else if((e.getActionCommand()).equals("save"))
		{
			if(imageCopy != null)
				FileManager.saveFile(imageCopy);
		}
		
		//displays data table if it exists when view is pressed, otherwise displays error message
		else if((e.getActionCommand()).equals("view"))
		{
			if(FileManager.getFileName() != null)
				createDataTable(frame);
			else
				JOptionPane.showMessageDialog(frame, "Please open a .txt file to view data.");
		}
		
		//displays information about how to use the program if 'help' is pressed
		else if((e.getActionCommand()).equals("help"))
			createHelp(frame);
		
		accessorIndex = regressionList.getSelectedIndex(); //gets the currently selected index for the JComboBox

		/*sets the appropriate regression on the graph:
		 0 - None, 1 - Linear, 2 - Quadratic, 3 - Power, 4 - Exponential*/
		if(accessorIndex != lastSelectedCB)
		{
			if(accessorIndex== 0)
			{
				isLinearRegression = false;
				isQuadraticRegression = false;
				isPowerRegression = false;
				isExponentialRegression = false;
				regressionSelected = false;
			}
			else if(accessorIndex == 1)
			{
				isLinearRegression = true;
				isQuadraticRegression = false;
				isPowerRegression = false;
				isExponentialRegression = false;
				regressionSelected = true;
			}
			else if(accessorIndex == 2)
			{
				isQuadraticRegression = true;
				isLinearRegression = false;
				isPowerRegression = false;
				isExponentialRegression = false;
				regressionSelected = true;
			}
			else if(accessorIndex== 3)
			{
				isPowerRegression = true;
				isLinearRegression = false;
				isQuadraticRegression = false;
				isExponentialRegression = false;
				regressionSelected = true;
			}
			else if(accessorIndex == 4)
			{
				isExponentialRegression = true;
				isLinearRegression = false;
				isQuadraticRegression = false;
				isPowerRegression = false;
				regressionSelected = true;
			}
			
			this.repaint(); //re-initializes graph to display selected regression model
			lastSelectedCB = accessorIndex;
		}
	}

	/**
	 * Adds all graphic elements to the UI and displays it
	 */
	private static void createAndShowGui() 
	{
		//initializes a JFrame (the main container) for the UI
		frame = new JFrame("Regression Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
		//creates a new JPanel representing the UI and adds all JButtons and JLabsl
		GraphingTool mainPanel = new GraphingTool();
        mainPanel.setPreferredSize(new Dimension(1000, 600));
		mainPanel.setLayout(null);
		mainPanel.add(openButton);
		mainPanel.add(saveButton);
		mainPanel.add(viewData);
		mainPanel.add(infoButton);
		mainPanel.add(regressionList);
		mainPanel.add(introLabel);
		mainPanel.add(instructionsLabel);
		
		//formats JFrame and displays the graphics
		frame.getContentPane().add(mainPanel);
		frame.setSize(1000,600);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
		frame.setLayout(null);
    }

	/**
	 * Runs applet
	 */
	public static void main(String[] args) 
	{
		SwingUtilities.invokeLater(new Runnable() {
         public void run() {
            createAndShowGui();
         }});
    }
 }