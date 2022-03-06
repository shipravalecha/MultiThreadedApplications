/**
 * This GeneralScan is the abstract class that has the methods
 * init(), prepare(), combine(), size(), value(), parent(),
 * left(), right(), isLeaf()
 * Methods init(), prepare(), combine() will be overriden in HeatmapParallel class
 */

import java.lang.Math;
import java.util.*;

// ElemType -> Represents Raw Data ie.e observations
// TallyType -> Represents Tally Data
public abstract class GeneralScan<ElemType, TallyType> {
	
	public int ROOT = 0;
	public boolean reduced;
	public int n;
	public ArrayList<ElemType> data;
	public ArrayList<TallyType> interior;					// contains interior nodes values
	public ArrayList<TallyType> scanOutput;					// contains prefixes from scan
	public ArrayList<TallyType> accumulatedData;			// contains accumulated results from reduce
	public int height;
	public static int N_THREADS = 16;						// No. of threads used in the program
	public static int DIM = 150;


	public GeneralScan(ArrayList<ElemType> rawData) 
	{ 
		this.data = rawData;
		this.n = rawData.size();
		this.interior = new ArrayList<TallyType>(Collections.nCopies(N_THREADS - 1, init()));
		this.accumulatedData = new ArrayList<TallyType>(Collections.nCopies(N_THREADS, init()));
		this.scanOutput =  new ArrayList<TallyType>(Collections.nCopies(rawData.size(), init()));
		this.reduced = false;
		this.height = (int) Math.ceil(Math.log(n) / Math.log(2));
		if(1<<height != n)
		throw new IllegalArgumentException("data size must be power of 2 for now");
	}

	public abstract TallyType init();

	public abstract TallyType prepare(ElemType datum);

	public abstract TallyType combine(TallyType left, TallyType right);
	
	int size() {
		return (N_THREADS-1) + N_THREADS;
	}

	TallyType value(int i) {
		if (i < N_THREADS-1)
			return interior.get(i);
		else
			return accumulatedData.get(i - (N_THREADS-1));
	}

	int parent(int i) { 
		return (i-1)/2; 
	}

	int left(int i) { 
		return i*2+1; 
	}

	int right(int i) { 
		return left(i)+1; 
	}
	
	boolean isLeaf(int i) {
		return right(i) >= size();
	}
}
