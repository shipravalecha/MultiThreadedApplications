class HeatMap 
{
    Double[][] data;
    int dim;
    public HeatMap(int DIM) {
        this.dim  = DIM;
        data = new Double[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                data[i][j] = 0.0;
            }
        }
    }

    public void put(int x, int y, double val) {
        data[x][y] = val;
    }

    public static HeatMap add(HeatMap h1, HeatMap h2) {
        int dim = HeatmapParallel.DIM;
        HeatMap res = new HeatMap(dim);
        for(int i = 0 ; i < dim; i++) {
            for(int j = 0; j < dim; j++) {
                res.put(i, j, h1.data[i][j] + h2.data[i][j]);
            }
        }
        return res;
    }

    public HeatMap remove(HeatMap toRemove) {
        HeatMap res = new HeatMap(this.dim);
        int dim = HeatmapParallel.DIM;
        for (int i = 0;i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                res.data[i][j] = this.data[i][j] - toRemove.data[i][j];
            }   
        }
        return res;
    }
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("");
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                s.append("(" + data[i][j] + ")");
            }
            s.append("\n");
        }
        return s.toString();
    }

    public HeatMap applyWeight(double weight) {
        int dim = HeatmapParallel.DIM;
        HeatMap res = new HeatMap(dim);
        for (int i = 0; i < dim; i++) {
            for (int j  = 0; j < dim; j++) {
                res.data[i][j] = this.data[i][j] * weight;
            }
        }
        return res;
    }
}