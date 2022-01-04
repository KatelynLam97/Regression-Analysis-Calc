# Regression-Analysis-Calc
A graphing calculator created using Java Swing that performs a regression analysis in two variables. To execute the program in cmd on Windows, set the path to the path where the program is found. Compile with command javac regressionAnalysisCalc\GraphingTool.java and run with command java regressionAnalysisCalc/GraphingTool
When the program is executed, the user opens a correctly formatted text file with the following format to load the data:
(Let x represent the independent variable value, let y represent the dependent variable value)

xName@yName@xUnit@yUnit
x1 y1
x2 y2
x3 y3
.. ..

A scatter plot is created on the scaled axes, and the following is displayed on the right pane:
- mean (x and y)
- median (x and y) ->> For y only: range, Q1 (median of first half of data for y), Q3 (median of second half of data for y), interquartile range
- variance (x and y) [Usually expressed as sample]
- standard deviation (x and y) [Usually expressed as sample]
- covariance
- one of the following selectable regression types: linear, quadratic, power (log-log method), and exponential (log method). If a regression model is chosen, the equation, r-value (Pearson's correlation coefficient), R-squared (coefficient of determination) and if linear, the strength of correlation are displayed.

After the analysis is performed, by pressing on one of the icons on the bottom of the window,
- another correctly formatted file can be selected
- the image of the regression graph, R-squared and/or r-value can be saved as a .jpg or .png in a chosen directory. If no regression models are used, the original displayed scatter plot is saved.
- a window with the table of data when the eye, 'view' is clicked
- a set of instructions for how to use the program (the question mark, help) is clicked

Download the javadoc folder and see index.html for the documentation. A tutorial for the program can be seen by downloading RegressionAnalysisTool.webm
Sample1.txt, Sample2.txt, Sample3.txt, Sample4.txt, and Sample5.txt are all correctly-formatted data files that can be used in the program. Sample 2_Power is an example of a saved image file with a regression performed on it.
