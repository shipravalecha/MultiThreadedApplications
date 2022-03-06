/**
 * This class performs the reduce, scan on observations on the basis of timestamps.
 * In this example, I have taken 256 timestamps. Here I insert new observation and discard the oldest observation from HeatMap.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class HW6Threshold {
    private static final String REPLAY = "Replay";
    private static JFrame application;
	private static JButton button;
    private static ArrayList<HeatMap> Heatmaps;
    private static Color[][] grid = new Color[HeatmapParallel.DIM][HeatmapParallel.DIM];
    private static int current = 0;
    private static final Color COLD = new Color(0x0a, 0x37, 0x66), HOT = Color.RED;
	private static final double HOT_CALIB = 1.0;
    
    static class BHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (REPLAY.equals(e.getActionCommand())) {
				new Thread() {
			        public void run() {
			            try 
                        {
							animate();
						} catch (InterruptedException e) {
							System.exit(0);
						}
			        }
			    }.start();
			}
		}
	};

    public static void displayHeatMap() throws InterruptedException
    {
        int dim  = GeneralScan.DIM;
		grid = new Color[dim][dim];
		application = new JFrame();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fillGrid(grid);

		ColoredGrid gridPanel = new ColoredGrid(grid);
		application.add(gridPanel, BorderLayout.CENTER);

		button = new JButton(REPLAY);
		button.addActionListener(new BHandler());
		application.add(button, BorderLayout.PAGE_END);

		application.setSize(dim * 40, (int) (dim * 40.4));
		application.setVisible(true);
		application.repaint();
        animate();
    }

    private static void animate() throws InterruptedException 
    {
		button.setEnabled(false);
        for(current = 0; current<Heatmaps.size(); current++) {
           fillGrid(grid);
            application.repaint();
			Thread.sleep(20);
        }
		button.setEnabled(true);
		application.repaint();
	}

    private static void fillGrid(Color[][] grid) {
		for (int r = 0; r < grid.length; r++)
			for (int c = 0; c < grid[r].length; c++)
				grid[r][c] = interpolateColor(Heatmaps.get(current).data[r][c] / HOT_CALIB, COLD, HOT);
	}
    private static Color interpolateColor(double ratio, Color a, Color b) {
		ratio = Math.min(ratio, 1.0);
		int ax = a.getRed();
		int ay = a.getGreen();
		int az = a.getBlue();
		int cx = ax + (int) ((b.getRed() - ax) * ratio);
		int cy = ay + (int) ((b.getGreen() - ay) * ratio);
		int cz = az + (int) ((b.getBlue() - az) * ratio);
		return new Color(cx, cy, cz);
	}
    // Main method
    public static void main(String[] args) 
    {
        ArrayList<Observation> rawData = new ArrayList<Observation>();

        /*
        Write some arbitrary observations to a observation data file.
        */
            Random r = new Random();
            rawData.add(new Observation((long) 0,  0, 0));
            for (long t = 1; t < GeneralScan.TS ; t++) 
            {
				double x = r.nextGaussian() * 0.33;
				double y = r.nextGaussian() * 0.33;
				while ((x < -1.0 || x > 1.0) || (y < -1.0 || y > 1.0)) 
                {
                     x = r.nextGaussian() * 0.33;
				     y = r.nextGaussian() * 0.33;
                }
                rawData.add(new Observation(t, x, y));
			}

        // pass1 is the object of HeatmapParallel class
        HeatmapParallel pass1 = new HeatmapParallel(rawData);

        // forkjoin pool for subtasks t1, t2, t3
        ForkJoinPool reducePool = new ForkJoinPool(HeatmapParallel.N_THREADS);

        // reduce task t1
        ReduceTask t1 = new ReduceTask(pass1, 0);
        reducePool.invoke(t1);
        HeatMap[] pass1Results = pass1.interior.get(0);

        // start the parallel reduce scan by taking the pass1Results as the input
        // reduce task t2
        HeatMapParallelPass2 pass2 = new HeatMapParallelPass2(new ArrayList<HeatMap>(Arrays.asList(pass1Results)));
        ReduceTaskPass2 t2 = new ReduceTaskPass2(pass2, 0);
        reducePool.invoke(t2);

        // scan task t3
        ScanTaskPass2 t3 = new ScanTaskPass2(pass2, 0, pass2.init());
        reducePool.invoke(t3);

        // Lets do sampling
        Heatmaps = pass2.displayOutput;  

        // display heat map
        try {
            displayHeatMap();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } 
}
