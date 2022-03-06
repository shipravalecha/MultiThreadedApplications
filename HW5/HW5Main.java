import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class HW5Main {
    private static final String REPLAY = "Replay";
    private static JFrame application;
	private static JButton button;
    private static ArrayList<Color[][]> Heatmaps;
    private static Color[][] grid = new Color[HeatmapParallel.DIM][HeatmapParallel.DIM];
    
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

    public static void displayHeatMap() throws InterruptedException{
		application = new JFrame();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		fillGrid(Heatmaps.get(0));
		ColoredGrid gridPanel = new ColoredGrid(grid);
		application.add(gridPanel, BorderLayout.CENTER);
		
		button = new JButton(REPLAY);
		button.addActionListener(new BHandler());
		application.add(button, BorderLayout.PAGE_END);
		
		application.setSize(HeatmapParallel.DIM * 4, (int)(HeatmapParallel.DIM * 4.4));
		application.setVisible(true);
		application.repaint();
		animate();
    }

    private static void fillGrid(Color[][] newGrid) {
        int dim = HeatmapParallel.DIM;
        for (int i = 0; i< dim; i++) {
            for (int j = 0; j<dim; j++) {
                grid[i][j] = newGrid[i][j];
            }
        }
    }

    private static void animate() throws InterruptedException {
		button.setEnabled(false);
        for (int i = 1; i<Heatmaps.size(); i++) {
           fillGrid(Heatmaps.get(i));
            application.repaint();
			Thread.sleep(20);
        }
		button.setEnabled(true);
		application.repaint();
	}

    public static Color[][] getColors(Integer[][] output) {
        int dim = HeatmapParallel.DIM;
        Color[][] res = new Color[dim][dim];
        
        for (int i = 0; i < HeatmapParallel.DIM; i++) {
            for (int j = 0; j < HeatmapParallel.DIM; j++) {
                int val  = output[i][j];
                if (val == 0) res[i][j] = Color.BLACK;
                else if(val == 1) res[i][j] = Color.gray;
                else if (val > 1 && val <= 2) res[i][j] = Color.YELLOW;
                else if (val >= 3 && val <= 6) res[i][j] = Color.BLUE;
                else res[i][j] = Color.RED;
            }
        }
        return res;
    }
    public static void main(String[] args) {
        final String FILENAME = "observation_test.dat";
        ArrayList<Observation> rawData = new ArrayList<Observation>();

        /*
        Write some arbitrary observations to a observation data file.
         */
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILENAME));
            for (long t = 0; t < 8; t++)
                for (double x = -0.98; x < 1.0; x += 0.194)
                    for (double y = 0.1; y > -0.9; y -= 0.423) {
                        out.writeObject(new Observation(t, x, y));
                    }
            out.writeObject(new Observation());  // to mark EOF
            out.close();
        } catch (IOException e) {
            System.out.println("writing to " + FILENAME + "failed: " + e);
            e.printStackTrace();
            System.exit(1);
        }

        /*
         * Now read them from the data file and display them on the console.
         */
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILENAME));
            Observation obs = (Observation) in.readObject();
            while (!obs.isEOF()) 
            {
                obs = (Observation) in.readObject();
                if (rawData.size() < 256) {
                    rawData.add(obs);
                }
            }
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // hm is the object of HeatmapParallel class
        HeatmapParallel hm = new HeatmapParallel(rawData);

        // forkjoin pool for subtasks t1 and t2
        ForkJoinPool reducePool = new ForkJoinPool(hm.N_THREADS);

        // reduce task t1
        ReduceTask t1 = new ReduceTask(hm, 0);
        reducePool.invoke(t1);

        // scan task t2
        ScanTask t2 = new ScanTask(hm, 0, hm.init());
        reducePool.invoke(t2);
        Heatmaps = new ArrayList<Color[][]>();

        for (Integer[][] output : hm.scanOutput) {
            Heatmaps.add(getColors(output));
        }

        // display heat map
        try {
            displayHeatMap();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
    } 
}
