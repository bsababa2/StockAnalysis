package screens;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;
import utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ClustersScreen extends JFrame
{
	public ClustersScreen() throws CloneNotSupportedException
	{
		this.setTitle("Analysis Results");
		Utils.setSoftSize(this, new Dimension(700, 400));
		this.setLocationRelativeTo(null);

		JPanel clustersPanel = new JPanel();
		Utils.setLineLayout(clustersPanel);
		Utils.addSmallRigid(clustersPanel);
		for (int i = 0; i < 3; i++)
		{
			clustersPanel.add(getClusterPanelByIndex(i));
			Utils.addStandardRigid(clustersPanel);
		}

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

	private JPanel getClusterPanelByIndex(int index) throws CloneNotSupportedException
	{
		NumberAxis domainAxis = new NumberAxis();
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

		JLabel clusterLabel = new JLabel("Cluster" + " " + index);
		clusterLabel.setFont(new Font("Arial", Font.BOLD, 16));
		JPanel labelPanel = new JPanel();
		labelPanel.add(clusterLabel);

		JPanel clusterPanel = new JPanel();
		Utils.setPageLayout(clusterPanel);
		Utils.addSmallRigid(clusterPanel);
		clusterPanel.add(labelPanel);
		for (int i = 0; i < 10; i++)
		{
			plot.setDataset(getDataByStock());
			JFreeChart chart = new JFreeChart(null, JFreeChart.DEFAULT_TITLE_FONT, plot, false);
			ChartPanel chartPanel = new ChartPanel(chart);
			Utils.addStandardRigid(clusterPanel);
			clusterPanel.add(chartPanel);

			plot = (XYPlot) plot.clone();
		}

		return clusterPanel;
	}

	private XYSeriesCollection getDataByStock()
	{
		XYSeries series = new XYSeries("Stock Traffic");
		Random r = new Random();
		int x = 0;
		int y = 0;
		for(int i = 0; i < 50; i++)
		{
			x+= r.nextBoolean() ? -r.nextInt(10): r.nextInt(10);
			y+= r.nextBoolean() ? -r.nextInt(10): r.nextInt(10);
			series.add(x, y);
		}

		return new XYSeriesCollection(series);
	}

	public static void main(final String[] args) throws CloneNotSupportedException
	{
		ClustersScreen demo = new ClustersScreen();
		demo.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		demo.pack();
		RefineryUtilities.centerFrameOnScreen(demo);
		demo.setVisible(true);
	}
}