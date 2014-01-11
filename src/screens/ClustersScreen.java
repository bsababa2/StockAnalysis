package screens;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.DateTickUnitType;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClustersScreen extends JDialog
{
	private static Dimension STOCK_NAME_LABEL = new Dimension(40, 25);

	private Map<String, TimeSeries> stockToData = new HashMap<String, TimeSeries>();

	public ClustersScreen(JFrame owner, Map<String, TimeSeries> stockToData) throws CloneNotSupportedException, IOException
	{
		super(owner, true);
		this.stockToData = stockToData;
		this.setTitle("Analysis Results");
		Utils.setSoftSize(this, new Dimension(700, 600));
		this.setLocationRelativeTo(null);

		JPanel clustersPanel = createClusterPanel();

		JLabel mainLabel = new JLabel("Analysis Results");
		mainLabel.setFont(new Font("Arial", Font.BOLD, 24));
		JPanel labelPanel = new JPanel();
		labelPanel.add(mainLabel);

		JPanel mainPanel = new JPanel();
		Utils.setPageLayout(mainPanel);
		Utils.addSmallRigid(mainPanel);
		mainPanel.add(labelPanel);
		Utils.addStandardRigid(mainPanel);
		mainPanel.add(clustersPanel);

		setContentPane(mainPanel);
	}

	private JPanel createClusterPanel() throws IOException, CloneNotSupportedException
	{
		JPanel clustersPanel = new JPanel();
		Utils.setLineLayout(clustersPanel);
		Utils.addSmallRigid(clustersPanel);

		Map<Character, List<String>> clusterToStocks  = getClusterMapFromFile();

		for (Character cluster : clusterToStocks.keySet())
		{
			clustersPanel.add(createClusterPanel(cluster, clusterToStocks.get(cluster)));
			Utils.addStandardRigid(clustersPanel);
		}

		return clustersPanel;
	}

	private JPanel createClusterPanel(Character cluster, List<String> stocks) throws CloneNotSupportedException
	{
		DateAxis domainAxis = new DateAxis();
		domainAxis.setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
		domainAxis.setTickLabelsVisible(false);
		domainAxis.setTickMarksVisible(false);
		domainAxis.setAxisLineVisible(false);
		domainAxis.setNegativeArrowVisible(false);
		domainAxis.setPositiveArrowVisible(false);
		domainAxis.setVisible(false);

		NumberAxis rangeAxis = new NumberAxis();
		rangeAxis.setTickLabelsVisible(false);
		rangeAxis.setTickMarksVisible(false);
		rangeAxis.setAxisLineVisible(false);
		rangeAxis.setNegativeArrowVisible(false);
		rangeAxis.setPositiveArrowVisible(false);
		rangeAxis.setVisible(false);

		XYPlot plot = new XYPlot();
		plot.setDomainAxis(domainAxis);
		plot.setDomainGridlinesVisible(false);
		plot.setDomainCrosshairVisible(false);
		plot.setRangeGridlinesVisible(false);
		plot.setRangeCrosshairVisible(false);
		plot.setRangeAxis(rangeAxis);
		plot.setRenderer(new StandardXYItemRenderer(StandardXYItemRenderer.LINES));

		JLabel clusterLabel = new JLabel("Cluster" + " " + cluster);
		clusterLabel.setFont(new Font("Arial", Font.BOLD, 16));
		JPanel labelPanel = new JPanel();
		labelPanel.add(clusterLabel);

		JPanel clusterPanel = new JPanel();
		Utils.setPageLayout(clusterPanel);
		Utils.addSmallRigid(clusterPanel);
		clusterPanel.add(labelPanel);
		for (String stock : stocks)
		{
			JPanel stockPanel = new JPanel();
			Utils.setLineLayout(stockPanel);

			plot.setDataset(new TimeSeriesCollection(stockToData.get(stock)));
			JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
			ChartPanel chartPanel = new ChartPanel(chart);

			JLabel stockNameLabel = new JLabel(stock);
			Utils.setHardSize(stockNameLabel, STOCK_NAME_LABEL);
			stockPanel.add(stockNameLabel);
			Utils.addSmallRigid(stockPanel);
			stockPanel.add(chartPanel);

			Utils.addStandardRigid(clusterPanel);
			clusterPanel.add(stockPanel);

			plot = (XYPlot) plot.clone();
		}

		return clusterPanel;
	}

	private Map<Character, List<String>> getClusterMapFromFile() throws IOException
	{
		Map<Character, List<String>> clusterToStocksMap = new HashMap<Character, List<String>>();
		BufferedReader reader = new BufferedReader(new FileReader("clusters.txt"));

		String line = reader.readLine();
		for (int i = 0; line != null && !line.isEmpty(); i++, line = reader.readLine())
		{
			String valuesListString = line.substring(line.indexOf("[") + 1, line.indexOf("]"));
			String[] values = valuesListString.split(",");

			char cluster = (char) ('A' + i);
			clusterToStocksMap.put(cluster, new ArrayList<String>());
			for (String value : values)
			{
				clusterToStocksMap.get(cluster).add(value);
			}
		}

		return clusterToStocksMap;
	}
}