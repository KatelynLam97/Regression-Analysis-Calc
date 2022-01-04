package regressionAnalysisCalc;
import java.lang.Math;
import java.text.DecimalFormat;
/**
 * Analysis Library provides a set of methods to perform statistical analysis, including mean, median, measures of spread, range, and regression models.
 *  A set of methods for rounding decimals is also included. All calculations will yield unrounded values. This class is not meant to be
 * instantiated.
 *@version Jan 2022
 *@author Katelyn Lam
 */
public class AnalysisLibrary
{
	/**
	 * Calculates the total sum of an array of data.
	 * @param data an array of type <code>double</code>
	 * @return double - the total sum of all doubles in array
	 */
	public static double summation(double[] data)
	{
		double summation = 0.0;
		for(int i = 0; i < data.length; i++)
		{
			summation = summation + data[i];
		}
		return summation;
	}
	
	/**
	 * Sorts a double array using bubble sort.
	 * @param unsorted an array of type <code>double</code>, not in order of increasing values
	 * @return double[] - a sorted array with the same values as <code>unsorted</code> in order of increasing values
	 */
	public static double[] sorted(double[] unsorted)
	{
		double data[] = new double[unsorted.length];
		double currentTest = 0;
		boolean mismatchFound = true;
		int numMismatch = 0;
		int i = 0;
		
		for(int j = 0; j < unsorted.length; j++)
		{
			data[j] = unsorted[j];
		}
		
		while(mismatchFound)
		{
			//if two numbers side by side are out of order, switch the numbers
			if(data[i] > data[i+1])
			{
				currentTest = data[i];
				data[i] = data[i+1];
				data[i+1] = currentTest;
				numMismatch++;
			}
			
			//continue looping through the array until there are no out of order elements
			if(i == (data.length - 2))
			{
				if(numMismatch > 0)
				{
					i = 0;
					numMismatch = 0;
				}
				else
					mismatchFound = false;
			}
			else
				i++;
		}
		return data;
	}
	
	/**
	 * Finds the arithmetic mean (average) of a set of data.
	 * @param data an array of type <code>double</code>
	 * @return double - the value of arithmetic mean
	 */
	public static double mean(double[] data)
	{
		double mean = 0.0;
		
		if(data.length > 0)
			mean = summation(data)/data.length;
		return mean;
	}
	
	/**
	 * Finds the median (the number for which half the values are 
	 * above and half are below) of a set of data.
	 * @param data an array of type <code>double</code>
	 * @return double -  the median, which is an element of the data set (or the average of 
	 * the two middle elements if the data set has an even length)
	 */
	public static double median(double[] data, boolean isSorted)
	{
		double median = 0.0;
		int length = data.length;
		if(!isSorted)
			data = sorted(data);
		
		//sorts the array and looks for the value at the middle index
		if(length % 2 == 0)
			median = (data[length/2 - 1] + data[length/2])/2.0;
		else
			median = data[length/2];

		return median;
	}
	
	/** 
	 * Finds the range (difference between highest and lowest values) of the data set.
	 * @param data an array of type <code>double</code>
	 * @return double - the range of the set of data
	 */
	public static double range(double[] data)
	{
		int length = data.length;
		double[] sortedDataArray = sorted(data);
		double range = sortedDataArray[length - 1] - sortedDataArray[0];
		return range;
	}
	
	/**
	 * Finds the quartiles and interquartile range of the set of data. The interquartile range (IQR)
	 * is the difference between Q1 and Q3, where Q1 is the median of the first half of the data
	 * set and Q3 is the median of the second half of the data set.
	 * @param data an array of type <code>double</code>
	 * @return double[] - an array where {Q1, Q3, IQR} is returned
	 */
	public static double[] interquartileRange(double[] data)
	{
		int length = data.length;
		double q1 = 0.0;
		double q3 = 0.0;
		double interquartileRange = 0.0;
		double[] sortedDataArray = sorted(data);
		double[] firstHalf = new double[length/2];
		double[] secondHalf = new double[length/2];
		
		//determines the index where the two arrays are split from the data array
		int firstSplitIndex = length/2 - 1;
		int secondSplitIndex = 0;
		
		if(length %2 == 0)
			secondSplitIndex = firstSplitIndex + 1;
		else
			secondSplitIndex = firstSplitIndex + 2;
		
		//cuts the data array in half and transfers to new arrays
		for(int i = 0; i <= firstSplitIndex; i++)
		{
			firstHalf[i] = sortedDataArray[i];
		}
		
		for(int j = secondSplitIndex; j < length; j++)
		{
			secondHalf[j - secondSplitIndex] = sortedDataArray[j];
		}
		
		//determines Q1, Q3, and IQR by finding the median of the split arrays and the difference
		q1 = median(firstHalf,true);
		q3 = median(secondHalf,true);
		interquartileRange = q3 - q1;
		
		double[] qData = {q1,q3,interquartileRange};
		return qData;
	}	
	
	/**
	 * Finds the variance of a set of data. The variance is a measure of spread of a set of data about the arithmetic mean. It is calculated by taking 
	 * the summation of the square of the deviation, or difference between a data value and the arithmetic mean, then divided by the number of points, <i>n</i> (for a census)
	 * and <i>n - 1</i> (for a sample). The sample variance has a lower denominator because the deviation tends to be lower than the actual value.
	 * @param data an array of type <code>double</code>
	 * @param isSample true if data represents a sample, false if data represents a census. For this regression analysis calculator, all data is assumed to be a sample.
	 * @return double - the variance of the data set
	 */
	public static double variance(double[] data, boolean isSample)
	{
		double variance = 0.0;
		double mean = mean(data);

		double[] deviations = new double[data.length];
		
		for(int i = 0; i < deviations.length; i++)
		{
			deviations[i] = Math.pow(data[i] - mean,2);
		}
		
		if(isSample)
			variance = summation(deviations)/(deviations.length - 1);
		else
			variance = mean(deviations);
		return variance;
	}
	
	/**
	 * Finds the standard deviation of a set of data. The standard deviation is the average deviation for a given data point from the arithmetic mean.
	 * @param data an array of type <code>double</code>
	 * @param isSample <code>true</code> if data represents a sample, <code>false</code> if data represents a census. For this regression analysis calculator, all data is assumed to be a sample. Must
	 * be same state if <code>variance()</code> is called.
	 * @return double - the standard deviation of the data set
	 */
	public static double stDev(double[] data, boolean isSample)
	{
		double stDev = Math.sqrt(variance(data,isSample));
		return stDev;
	}
	
	/**
	 * Finds the covariance of a set of data. The covariance represents the average spread of the independent variable <i>x</i> and dependent variable <i>y</i>.
	 * @param dataX an array of type <code>double</code> representing the values of the independent variable
	 * @param dataY an array of type <code>double</code> representing the values of the dependent variable. Must be the same length as <code>dataX</code>.
	 * @return double - the covariance of <code>dataX</code> and <code>dataY</code>
	 */
	public static double covariance(double[] dataX, double[] dataY)
	{
		double covariance = 0.0;
		double summationMultipliedValues = 0.0;
		double meanX = mean(dataX);
		double meanY = mean(dataY);
		double[] multipliedValues = new double[dataX.length];
		
		for(int i = 0; i < multipliedValues.length; i++)
		{
			multipliedValues[i] = (dataX[i] - meanX) * (dataY[i] - meanY);
		}
		
		summationMultipliedValues = summation(multipliedValues);
		covariance = summationMultipliedValues/(double)(dataX.length - 1);
		return covariance;
	}
	
	/** 
	 * Finds the Pearson Correlation Coefficient (PCC or <i>r</i>) of a linear regression. It is a quantitative measure of how well the line of best fit represents 
	 * a two-variable data set.
	 * @param dataX an array of type <code>double</code> representing the values of the independent variable.
	 * @param dataY an array of type <code>double</code> representing the values of the dependent variable. Must be the same length as <code>dataX</code>.
	 * @return double - the PCC of <code>dataX</code> and <code>dataY</code>, where 0 <= PCC <= 1
	 */
	public static double rFactor(double[] dataX, double[] dataY)
	{
		double covariance = covariance(dataX, dataY);
		double stDevX = stDev(dataX, true);
		double stDevY = stDev(dataY, true);
		double rFactor = covariance/(stDevX * stDevY);
		return rFactor;
	}
	
	/**
	 * Determines the strength of a linear correlation given a value of the Pearson Correlation Coefficient (PCC), (or <i>r</i>). The linear correlation is described as follows:
	 * <ul><li> if <i>r</i>= 0, there is no correlation.</li><li>if <i>r</i> = 1, there is a perfect correlation.
	 * <li>If <i>r</i> < 0, there is a negative (-) correlation. If <i>r</i> > 0, there is a positive (+) correlation.</li>
	 * <li>If 0 < |<i>r</i>| <= 1/3 there is a weak correlation. If 1/3 < |<i>r</i>| <= 2/3 there is a moderate correlation, and a strong correlation is where 2/3 < |<i>r</i>| < 1.</ul>
	 * @param rFactor - the PCC of a linear relationship. This function can only be called when an rFactor exists in a linear correlation.
	 * @return String - the type of correlation, outputted as "Positive/Negative  Weak/Moderate/Strong  Correlation"
	 */
	public static String correlationType(double rFactor)
	{
		String correlationType = "";
		
		//determines if correlation is positive or negative
		if(rFactor < 0)
			correlationType = correlationType + "Negative ";
		else if(rFactor > 0)
			correlationType = correlationType + "Positive ";
		else
			correlationType = "No correlation.";
		
		rFactor = Math.abs(rFactor);
		
		//determines the strength of correlation based on the abs. value of PCC
		if(rFactor > 0 && rFactor <= (1.0/3.0))
			correlationType = correlationType + "Weak Correlation.";
		else if(rFactor > (1.0/3.0) && rFactor <= (2.0/3.0))
			correlationType = correlationType + "Moderate Correlation.";
		else if(rFactor > (2.0/3.0) && rFactor < 1)
			correlationType = correlationType + "Strong Correlation.";
		else if(rFactor > 0.9999999 && rFactor < 1.000001)
			correlationType = "Perfect Correlation";
		else
			correlationType = "Error occured in r-value";
		return correlationType;
	}
	
	/**
	 * Determines the coefficient of determination, or R<sup>2</sup> for any regression model. R<sup>2</sup> is a quantitative measure that shows the proportion
	 * of the actual value that can be explained by the regression mode. If a PCC or (<i>r</i>) exists, then R<sup>2</sup> is the square of <i>r</i>. The supported regression models
	 * are: linear, quadratic, power (log-log method) and exponential (log method).
	 * @param dataX  an array of type <code>double</code> representing the values of the independent variable
	 * @param dataY  an array of type <code>double</code> representing the values of the dependent variable, which must be the same length as <code>dataX</code>
	 * @param regressionType the regression model used to compute R<sup>2</sup>. Must be 1 - linear, 2 - quadratic, 3 - power, 4 - exponential
	 * @return double - the value of R<sup>2</sup> where 0 <= R<sup>2</sup> < 1.
	 */
	public static double rSquared(double[] dataX, double[] dataY, int regressionType)
	{
		double yMean = mean(dataY);
		double rSquared = 0;
		double[] arguments;
		
		//determines r-squared by taking the square of the r-value
		if(regressionType == 1)
		{
			arguments = linearRegression(dataX, dataY);
			double slope = arguments[0];
			double yIntercept = arguments[1];
			rSquared = Math.pow(rFactor(dataX,dataY),2);
		}
		
		/*determines r-squared by taking the summation of the squares of the estimated deviations (using the regression model)
		 divided by the summation of the squares of the actual deviations*/
		else if(regressionType == 2)
		{
			arguments = quadraticRegression(dataX,dataY);
			double a = arguments[0];
			double b = arguments[1];
			double c = arguments [2];
			double estimatedY;
			double[] numerator = new double[dataY.length];
			double[] denominator = new double[dataY.length];
			
			for(int i = 0; i < dataY.length; i++)
			{
				estimatedY = a * Math.pow(dataX[i],2) + b * dataX[i] + c;
				numerator[i] = Math.pow(estimatedY - yMean, 2);
				denominator[i] = Math.pow(dataY[i] - yMean, 2);
			}
			rSquared = summation(numerator)/summation(denominator);
		}
		
		//determines r-squared by taking the square of the r-value (power uses transformed linear model)
		else if(regressionType == 3)
		{
			arguments = powerRegression(dataX,dataY);
			rSquared = Math.pow(arguments[2],2);
		}
		
		//determines r-squared by taking the square of the r-value (exponential uses transformed linear model)
		else if(regressionType == 4)
		{
			arguments = exponentialRegression(dataX,dataY);
			rSquared = Math.pow(arguments[2],2);
		}

		return rSquared;
	}
	
	/**
	 * Determines a line of best fit for the graph <i>y</i> vs <i>x</i> where <i>y</i> is the dependent variable and <i>x</i> is the independent variable.
	 * The line of best fit can be represented by the equation <i>y = mx + b</i>.
	 * @param dataX an array of type <code>double</code> representing the values of the independent variable
	 * @param dataY an array of type <code>double</code> representing the values of the dependent variable
	 * @return double[] - an array of arguments for the line of best fit, represented as {slope(<i>m</i>), y-intercept(<i>b</i>)}
	 */
	public static double[] linearRegression(double[] dataX, double[] dataY)
	{
		double yMean = mean(dataY);
		double xMean = mean(dataX);
		double slope = covariance(dataX, dataY)/variance(dataX, true);
		double yIntercept = yMean - slope * xMean;
		double[] linearArguments = {slope,yIntercept};
		return linearArguments;
	}
	
	/**
	 * Determines a curve of best fit for the graph <i>y</i> vs <i>x</i> where <i>y</i> is the dependent variable and <i>x</i> is the independent variable.
	 * The curve of best fit can be represented by the equation <i>y = ax<sup>2</sup>+ bx + c</i>. Reference <a href = "https://tutorme.com/blog/post/quadratic-regression/">this source</a>
	 * to see how the quadratic regression is performed.
	 * @param dataX an array of type <code>double</code> representing the values of the independent variable
	 * @param dataY an array of type <code>double</code> representing the values of the dependent variable
	 * @return double[] - an array of arguments for the curve of best fit, represented as {<i>a</i>,<i>b</i>,<i>c</i>}
	 */
	public static double[] quadraticRegression(double[] dataX, double[] dataY)
	{
		double[]x4 = new double[dataX.length];
		double[]x3 = new double[dataX.length];
		double[]x2 = new double[dataX.length];
		double[]x2y = new double[dataX.length];
		double[]xy = new double[dataX.length];
		
		for(int i = 0; i < dataX.length; i++)
		{
			x4[i] = Math.pow(dataX[i],4);
			x3[i] = Math.pow(dataX[i],3);
			x2[i] = Math.pow(dataX[i],2);
			x2y[i] = x2[i]*dataY[i];
			xy[i] = dataX[i]*dataY[i];
		}
		
		//determines a system of three linear equations and solves them for the values a,b, and c
		double[] arg1 = {summation(x4),summation(x3),summation(x2),summation(x2y)};
		double[] arg2 = {summation(x3),summation(x2),summation(dataX),summation(xy)};
		double[] arg3 = {summation(x2),summation(dataX),dataX.length, summation(dataY)};
		double[] quadraticArguments = threeLinearEqSolver(arg1,arg2,arg3);
		return quadraticArguments;
	}
	
	/**
	 * Determines a curve of best fit for the graph <i>y</i> vs <i>x</i> where <i>y</i> is the dependent variable and <i>x</i> is the independent variable.
	 * The curve of best fit can be represented by the equation <i>y = ax<sup>b</sup></i>. Reference <a href = "https://www.real-statistics.com/regression/power-regression/">this source</a>
	 * to see how the power regression is performed. Note: this uses the log-log method which performs a linear regression on ln<i>y</i> vs. ln<i>x</i> before expressing
	 * <i>y</i> in terms of a power function of <i>x</i>. This is not the most accurate power regression model.
	 * @param dataX an array of type <code>double</code> representing the values of the independent variable
	 * @param dataY an array of type <code>double</code> representing the values of the dependent variable
	 * @return double[] - an array of arguments for the curve of best fit, represented as {<i>a</i>,<i>b</i>,<i>PCC</i>}. The PCC is for the linear regression of ln<i>y</i> vs ln<i>x</i>
	 */
	public static double[] powerRegression(double[] dataX, double[]dataY)
	{
		double[] lnX = new double[dataX.length];
		double[] lnY = new double[dataY.length];
		
		for(int i = 0; i < lnY.length; i++)
		{
			lnX[i] = Math.log(dataX[i]);
			lnY[i] = Math.log(dataY[i]);
		}
		double[] regLine = linearRegression(lnX,lnY);
		double[] powArgs = {Math.pow(Math.E, regLine[1]),regLine[0],rFactor(lnX,lnY)};
		return powArgs;
	}

	/**
	 * Determines a curve of best fit for the graph <i>y</i> vs <i>x</i> where <i>y</i> is the dependent variable and <i>x</i> is the independent variable.
	 * The curve of best fit can be represented by the equation <i>y = ab<sup>x</sup></i>. Reference <a href = "https://www.real-statistics.com/regression/power-regression/">this source</a>
	 * to see how the exponential regression is performed. Note: this uses the log method which performs a linear regression on ln<i>y</i> vs. <i>x</i> before expressing <i>y</i> in terms
	 * of an exponential function of <i>x</i>. This is not the most accurate exponential regression model.
	 * @param dataX an array of type <code>double</code> representing the values of the independent variable
	 * @param dataY an array of type <code>double</code> representing the values of the dependent variable
	 * @return double[] - an array of arguments for the curve of best fit, represented as {<i>a</i>,<i>b</i>,<i>PCC</i>}. The PCC is for the linear regression of ln<i>y</i> vs <i>x</i>
	 */
	public static double[] exponentialRegression(double[] dataX, double[]dataY)
	{
		double[]logY = new double[dataY.length];
		
		for(int i = 0; i < logY.length; i++)
		{
			logY[i] = Math.log(dataY[i]);
		}
		double[] regLine = linearRegression(dataX,logY);
		double[] expArgs = {Math.pow(Math.E,regLine[1]), Math.pow(Math.E,regLine[0]),rFactor(dataX,logY)};
		return expArgs;
	}	
	/**
	 * Solves for a value of <i>x</i> or <i>y</i> given a function and the value of either one of those variables. Can only solve for linear, quadratic, power,
	 * and exponential equations.
	 * @param args the arguments returned after a regression is performed, representing constants in the equation
	 * @param regIndex an integer used to refer to the regression model. Ranges from 1 - 4 where 1: Linear, 2: Quadratic, 3: Power, 4: Exponential
	 * @param isY state of if the equation is solving for <i>y</i>. <code>true</code> if solving for <i>y</i> and <code>false</code> if solving for <i>x</i>
	 * @param variable value of the given variable (<i>x</i> if solving for <i>y</i>, <i>y</i> if solving for <i>x</i>)
	 * @return double the solved value
	 */
	public static double computeFunction(double[] args, int regIndex, boolean isY, double variable)
	{
		//solves for y
		if(isY)
		{
			if(regIndex == 1) //linear
				return (args[0] * variable + args[1]);
			else if(regIndex == 2) //quadratic
				return (args[0] * Math.pow(variable,2) + args[1] * variable + args[2]);
			else if(regIndex == 3) //power
				return (args[0] * Math.pow(variable,args[1]));
			else if(regIndex == 4) //exponential
				return (args[0] * Math.pow(args[1],variable));
		}
		
		//solves for x
		else
		{
			if(regIndex == 1) //linear
				return (variable - args[1])/args[0];
			else if(regIndex == 2) //quadratic: only takes the greater x-value to ensure it is greater than the last x-value graphed
				return (-1*args[1] + Math.sqrt(Math.pow(args[1],2) - 4*args[0]*(args[2]- (variable + 0.5))))/(2*args[0]);
			else if(regIndex == 3) //power
				return (Math.pow(Math.E,Math.log(variable/args[0])/args[1]));
			else if(regIndex == 4) //exponential
				return (Math.log((variable/args[0])/Math.log(args[1])));
		}
		return 0.0;
	}
	
	/**
	 * Solves a system of three linear equations (used in the quadratic regression), each of the form Ax + By + C = 0 where A,B, and C are the unknowns.
	 * @param eq1 - the first equation in the system, arguments are listed as {<i>x<sub>1</sub></i>,<i>y<sub>1</sub></i>}
	 * @param eq2 - the second equation in the system, arguments are listed as {<i>x<sub>2</sub></i>,<i>y<sub>2</sub></i>}
	 * @param eq3 - the third equation in the system, arguments are listed as {<i>x<sub>3</sub></i>,<i>y<sub>3</sub></i>}
	 * @return double[] - an array represented as {A,B,C}
	 */
	private static double[] threeLinearEqSolver(double[] eq1, double[] eq2, double[] eq3)
	{
		double a = 0.0;
		double b = 0.0;
		double c = 0.0;
		double[] subEq1 = new double[eq1.length - 1];
		
		//isolates a value for B in terms of C using elimination of A with eq. 1 and 2, and then rearranging
		for(int j = 1; j < eq1.length; j++)
		{
			subEq1[j-1] = eq2[0]* eq1[j] - eq1[0] * eq2[j];
		}
		double[] bSol = {subEq1[1] * -1/subEq1[0], subEq1[2]/subEq1[0]};
		
		//substituting the value of B, using eq.1 and 3, eliminate A and solve for C
		double[] subEq2 = {eq1[0],bSol[0]*eq1[1] + eq1[2],eq1[3]-bSol[1]*eq1[1]};
		double[] subEq3 = {eq3[0],bSol[0]*eq3[1] + eq3[2],eq3[3]-bSol[1]*eq3[1]};
		double[] cSol = {subEq2[1] * subEq3[0] - subEq3[1] * subEq2[0], subEq2[2] * subEq3[0] - subEq3[2] * subEq2[0]};
		c = cSol[1]/cSol[0];
		
		b = bSol[0] * c + bSol[1]; //solve for B by substituting the value of C
		a = (eq1[3] - eq1[2]*c- eq1[1]*b)/eq1[0]; //solve for A by substituting the value of B and C
		double[] finalSol = {a,b,c};
		return finalSol;
	}
	
	/**
	 * Rounds a decimal number to a preferred number of places and returns it as a <code>String</code>. The function rounds down for a number that is less than 5, and rounds
	 * up for a number greater or equal to 5.
	 * @param num unrounded decimal value
	 * @param places the number of decimal places a value is rounded to, which must be greater or equal to 0. If the number is smaller than
	 * the <code>places</code> (for.ex rounding 0.0000412 to 2 decimal places, the number is rounded to 1 significant digit (so 0.0000412 would be rounded to 0.00004).
	 * @return String - the rounded decimal
	 */
	public static String roundDecimal(double num, int places)
	{
		//determines the number of whole digits of the number
		int numWholeDigits = (int)Math.log10(num) + 1;
		String wholeDigits = formatString(numWholeDigits -1, "#");
		wholeDigits = wholeDigits+"0";
		String dp = "0";
		double numLog;
		
		/*if the 0 < abs.value of num < 1, then the log10 of the num is taken. If this value is greater
		than the number of places, then the num is rounded to this value, to ensure that no zero values are
		shown*/
		if(num < 0)
			numLog = Math.log10(num * -1);
		else
			numLog = Math.log10(num);
		
		if(numLog < (places * -1) && num < 0)
			dp = formatString((int)(-1*numLog + 1),"0");
		else
			dp = formatString(places,"0");
		
		//sets a rounding pattern for how the decimal is rounded and formats it as a String
		String roundedPattern = wholeDigits + "." + dp;
		DecimalFormat roundPattern = new DecimalFormat(roundedPattern);
		roundPattern.setRoundingMode(java.math.RoundingMode.HALF_UP);
		String decFormat = roundPattern.format(num);
		
		if(places == 0)
			return decFormat.substring(0,decFormat.length() -1);
		return decFormat;
	}
	
	/**
	 * Takes a <code>String</code> and creates a new <code>String</code> that is equivalent to the first <code>String</code> multiplied by a number of times. 
	 * (E.g. formatString("Hi",2) -> HiHi)
	 * @param num - number of times a given <code>String</code> is multiplied
	 * @param string - The given <code>String</code> to be formatted
	 * @return String - a new String where <code>string</code> is multiplied <code>num</code> times
	 */
	private static String formatString(int num, String string)
	{
		String newString = "";
		for(int i = 0; i < num; i++)
		{
			newString = newString + string;
		}
		return newString;
	}

	/**
	 * Determines the greatest number of decimal places of a number in a set of data.
	 * @param data an array of type <code>double</code>
	 * @return int - the maximum number of decimal places of any number in the data set
	 */
	public static int maxNumDecimalPlaces(double[] data)
	{
		int maxDP = 0;
		
		//converts each number in the data set to a String
		for(int i = 0; i < data.length; i++)
		{
			String sample  = Double.toString(data[i]);
			int j = 0;
			
			/*counts the number of digits after the decimal point in the data set
			if this number is greater than the maximum number before, update the maximum number
			of decimal places to this new value */
			while(j < sample.length() && !sample.substring(j,j+1).equals("."))
			{
				j++;
			}
			if((sample.length() - 1 - j) > maxDP)
				maxDP = sample.length() - 1 - j;
		}
		return maxDP;
	}	
}
	
		
		
	