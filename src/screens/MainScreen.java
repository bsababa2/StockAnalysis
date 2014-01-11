package screens;

import com.sun.deploy.panel.NumberDocument;
import org.apache.commons.io.FileUtils;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * Created by Barak on 06/01/14.
 */
public class MainScreen extends JFrame
{
	private static String stockChartUrl = "http://ichart.yahoo.com/table.csv?s=STOCK&a=FM&b=FD&c=FY&d=TM&e=TD&f=TY&g=d&ignore=.csv";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private JTextField daysToAnalyzeField = new JTextField(new NumberDocument(), "20", 4);
	private JTextField numOfClustersField = new JTextField(new NumberDocument(), "3", 3);
	private JCheckBox openCheckBox = new JCheckBox("Open", true);
	private JCheckBox highCheckBox = new JCheckBox("High");
	private JCheckBox lowCheckBox = new JCheckBox("Low", true);
	private JCheckBox closeCheckBox = new JCheckBox("Close", true);
	private JButton analyzeButton = new JButton("Analyze");

	private Map<String, TimeSeries> stockToData = new HashMap<String, TimeSeries>();
	private String tempChartUtl = stockChartUrl;

	private JTextField numStockField = new JTextField(new NumberDocument(), "100", 5);

	public MainScreen() throws HeadlessException
	{
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setTitle("Stock Analysis");
		Utils.setSoftSize(this, new Dimension(400, 200));
		this.setLocationRelativeTo(null);

		Utils.setHardSize(numStockField, new Dimension(50, 25));
		Utils.setHardSize(daysToAnalyzeField, new Dimension(50, 25));
		Utils.setHardSize(numOfClustersField, new Dimension(50, 25));

		Calendar calendar = Calendar.getInstance();
		stockChartUrl = stockChartUrl.replace("TM", calendar.get(Calendar.MONTH) + "");
		stockChartUrl = stockChartUrl.replace("TD", calendar.get(Calendar.DAY_OF_MONTH) + "");
		stockChartUrl = stockChartUrl.replace("TY", calendar.get(Calendar.YEAR) + "");

		JPanel numStockPanel = new JPanel();
		Utils.setLineLayout(numStockPanel);
		Utils.addStandardRigid(numStockPanel);
		numStockPanel.add(new JLabel("Num of Stocks:"));
		Utils.addStandardRigid(numStockPanel);
		numStockPanel.add(numStockField);
		numStockPanel.add(Box.createHorizontalGlue());

		JPanel daysPanel = new JPanel();
		Utils.setLineLayout(daysPanel);
		Utils.addStandardRigid(daysPanel);
		daysPanel.add(new JLabel("Analyze:"));
		Utils.addStandardRigid(daysPanel);
		daysPanel.add(daysToAnalyzeField);
		Utils.addStandardRigid(daysPanel);
		daysPanel.add(new JLabel("Days from today backward"));
		daysPanel.add(Box.createHorizontalGlue());

		JPanel featuresPanel = new JPanel();
		Utils.setLineLayout(featuresPanel);
		Utils.addStandardRigid(featuresPanel);
		featuresPanel.add(new JLabel("Features for analysis:"));
		Utils.addStandardRigid(featuresPanel);
		featuresPanel.add(openCheckBox);
		Utils.addStandardRigid(featuresPanel);
		featuresPanel.add(highCheckBox);
		Utils.addStandardRigid(featuresPanel);
		featuresPanel.add(lowCheckBox);
		Utils.addStandardRigid(featuresPanel);
		featuresPanel.add(closeCheckBox);
		Utils.addStandardRigid(featuresPanel);
		featuresPanel.add(Box.createHorizontalGlue());

		JPanel clustersPanel = new JPanel();
		Utils.setLineLayout(clustersPanel);
		Utils.addStandardRigid(clustersPanel);
		clustersPanel.add(new JLabel("Clusters:"));
		Utils.addStandardRigid(clustersPanel);
		clustersPanel.add(numOfClustersField);
		clustersPanel.add(Box.createHorizontalGlue());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		buttonPanel.add(analyzeButton);
		analyzeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				analyze();
			}
		});

		JPanel mainPanel = new JPanel();
		Utils.setPageLayout(mainPanel);
		Utils.addSmallRigid(mainPanel);
		mainPanel.add(numStockPanel);
		Utils.addSmallRigid(mainPanel);
		mainPanel.add(daysPanel);
		Utils.addSmallRigid(mainPanel);
		mainPanel.add(featuresPanel);
		Utils.addSmallRigid(mainPanel);
		mainPanel.add(clustersPanel);
		Utils.addSmallRigid(mainPanel);
		mainPanel.add(buttonPanel);

		this.getContentPane().add(mainPanel);
	}

	private void analyze()
	{
		if (numStockField.getText().isEmpty() || daysToAnalyzeField.getText().isEmpty() || numOfClustersField.getText().isEmpty() ||
			(!openCheckBox.isSelected() && !highCheckBox.isSelected() && !lowCheckBox.isSelected() && !closeCheckBox.isSelected()))
		{
			Utils.showErrorMsg(this, "You must fill all the fields before performing the analysis!");
			return;
		}

		int daysToAnalyze = Integer.parseInt(daysToAnalyzeField.getText());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1 * daysToAnalyze);
		tempChartUtl = stockChartUrl.replace("FM", calendar.get(Calendar.MONTH) + "");
		tempChartUtl = stockChartUrl.replace("FD", calendar.get(Calendar.DAY_OF_MONTH) + "");
		tempChartUtl = stockChartUrl.replace("FY", calendar.get(Calendar.YEAR) + "");

		try
		{
			// Creates the directory in which the stock files will be stored
			File dir = new File("StockFiles");
			if (dir.exists()) FileUtils.deleteDirectory(dir);
			Thread.sleep(1000);
			dir.mkdir();

			List<String> stocks = getStockList(Integer.parseInt(numStockField.getText()));
			for (String stock : stocks)
			{
				createFilesForStock(daysToAnalyze, stock);
			}

			new ClustersScreen(this, stockToData).setVisible(true);
		}
		catch (Exception e)
		{
			Utils.showExceptionMsg(this, e);
			e.printStackTrace();
		}
	}

	private List<String> getStockList(int numOfStocks) throws IOException
	{
		List<String> stocks = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader("stocksList.txt"));

		String line = br.readLine();
		for (int i = 0; i < numOfStocks && line != null; i++)
		{
			stocks.add(line.substring(0, line.indexOf("|")));
			line = br.readLine();
		}

		return stocks;
	}

	private void createFilesForStock(int numOfDays, String stockName) throws IOException, ParseException
	{
		// Creates the url
		String stockUrl = tempChartUtl.replace("STOCK", stockName);
		URL url = new URL(stockUrl);
		URLConnection urlConnection = url.openConnection(Proxy.NO_PROXY);
		BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

		// Create a new entry for the stock
		stockToData.put(stockName, new TimeSeries(stockName));

		// Ignoring the table headers
		String line = br.readLine();
		for (int i = 0; i < numOfDays / 10 && line != null; i++)
		{
			File generatedFile = new File("StockFiles\\"+ stockName + "_" + i +".csv");
			generatedFile.createNewFile();

			PrintWriter writer = new PrintWriter(generatedFile);
			line = br.readLine();
			for (int j = 0; j < 10 && line != null; j++)
			{
				// Gets the stock values from the current line and writes them to the generated file
				String values[] = line.split(",");
				StringBuilder lineToWrite = new StringBuilder(stockName).append(",").append(values[0]);
				if (openCheckBox.isSelected()) lineToWrite.append(",").append(values[1]);
				if (highCheckBox.isSelected()) lineToWrite.append(",").append(values[2]);
				if (lowCheckBox.isSelected()) lineToWrite.append(",").append(values[3]);
				if (closeCheckBox.isSelected()) lineToWrite.append(",").append(values[4]);
				writer.println(lineToWrite);

				// Adds the stock values from the current line to the map (Date, Open value)
				stockToData.get(stockName).add(new Day(dateFormat.parse(values[0])),
					Double.parseDouble(values[1]));

				line = br.readLine();
			}
			writer.close();
		}

		br.close();
	}

	public static void main(String args[])
	{
		new MainScreen().setVisible(true);
	}
}
