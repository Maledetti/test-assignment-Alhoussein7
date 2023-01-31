
package com.example.ai_for_data_science.players.algorithms.svm;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class SupportVectorMachine {
    private RealMatrix x, y, α, w; // x - the feature; y- the label; α - soft margins; w - the hyperplane
    private double b;
    private final int MAX_ITERATIONS = 100;
    private final double EPSILON = 0.001; // defines the tolerable error of the regression model
    static final double MIN_ALPHA_OPT = 0.00001;
    static final double SOFT_PARAM_C = 1.0; // the regularization parameter - the error 'score'


    public SupportVectorMachine(RealMatrix x, RealMatrix y) {
        this.x = x;
        this.y = y;
        this.b = 0;
        double[] αList = new double[x.getData().length];
        IntStream.range(0, αList.length).forEach(i -> αList[i] = 0);
        α = MatrixUtils.createColumnRealMatrix(αList); //
        int iteration = 0;
        while (iteration < MAX_ITERATIONS) {
            iteration = SequentialMinimalOptimization() == 0 ? iteration + 1 : 0;
        }
        w = calcW();
    }

    // perform the SMO
    private int SequentialMinimalOptimization() {
        int αOptimizedPaired = 0;
        for (int i = 0; i < x.getData().length; i++) {
            RealMatrix firstAlphaError = multiplyTwoMatrices(y, α).transpose()
                    .multiply(x.multiply(x.getRowMatrix(i).transpose()))
                    .scalarAdd(b)
                    .subtract(y.getRowMatrix(i));
            // to move forward α has to violate the KKT (Karush-Kuhn Tucker) conditions:
            //      1] α = 0 -> correctly label example with room to spare
            //      2] α = c -> misclassified  label example
            //      3] 0 < α < c = 0 -> the example is a Support Vector
            if (αKKTValidation(α.getEntry(i, 0), firstAlphaError.getEntry(0, 0))) { //-> select the index of the second α to optimize
                int j = secondAlphaOptimizationIndex(i, x.getData().length);
                RealMatrix secondAlphaError = multiplyTwoMatrices(y, α).transpose().multiply(x.multiply(x.getRowMatrix(j).transpose())).scalarAdd(b).subtract(y.getRowMatrix(j));
                // keep copies of the old alphas
                double αi = α.getRowMatrix(i).getEntry(0, 0);
                double αj = α.getRowMatrix(j).getEntry(0, 0);
                // bound both α_s according to:
                // y1 != y2 -> following bound applies to  α1: L = max(0, (α2 - α1)); H = min(R_C, (R_C + α2 - α1))
                // y1 == y2 -> following bound applies to α2: L = max(0, (α2 + α1 - R_C)); H = min(R_C, (α2 + α1))
                double[] bounds = αBounds(α.getEntry(i, 0), α.getEntry(j, 0), y.getEntry(i, 0), y.getEntry(j, 0));
                // η - optimal amount to change the second α
                // η = 2x1 . x2 - x1 . x1 - x2 . x2
                // αj = -αj + y2(firstAlphaError - secondAlphaError)/η
                // αj_clipped = (H , if αj >= H) || (αj , if L < αj < H) || (L, if αj <= L)  --- clipping alphaJ
                // αi = αi + y1*y2*(αj - αj_clipped) --- use the clipped alphaJ to optimize alphaI
                double η = x.getRowMatrix(i).multiply(x.getRowMatrix(j)
                        .transpose())
                        .scalarMultiply(2.0)
                        .getEntry(0, 0) // 2x1 * x2
                        - x.getRowMatrix(i).multiply(x.getRowMatrix(i)
                        .transpose())
                        .getEntry(0, 0)  // - x1 * x1
                        - x.getRowMatrix(j).multiply(x.getRowMatrix(j).transpose()).getEntry(0, 0); // - x2 * x2

                if (bounds[0] != bounds[1] && η < 0) {
                    if (optimizeAlphaPair(i, j, firstAlphaError.getEntry(0, 0), secondAlphaError.getEntry(0, 0), η, bounds, αi, αj)) {
                        optimizeB(firstAlphaError.getEntry(0, 0), secondAlphaError.getEntry(0, 0), αi, αj, i, j);
                        αOptimizedPaired += 1;
                    }
                }
            }

        }
        return αOptimizedPaired;
    }

    private RealMatrix multiplyTwoMatrices(RealMatrix m1, RealMatrix m2) {
        double[][] product = new double[m1.getData().length][m1.getData()[0].length];
        IntStream.range(0, m1.getData().length).forEach(row -> IntStream.range(0, m1.getData()[0].length).forEach(col -> product[row][col] = m1.getEntry(row, col) * m2.getEntry(row, col)));
        return MatrixUtils.createRealMatrix(product);
    }

    private boolean αKKTValidation(double α, double error) {
        // to move forward α has to violate the KKT (Karush-Kuhn Tucker) conditions:
        //      1] α = 0 -> correctly label example with room to spare
        //      2] α = c -> misclassified  label example
        //      3] 0 < α < c = 0 -> the example is a Support Vector
        return ((α > 0 && Math.abs(error) < EPSILON) || (α < SOFT_PARAM_C && Math.abs(error) > EPSILON));
    }

    private int secondAlphaOptimizationIndex(int α1, int rows) {
        int α2 = α1;
        while (α1 == α2) {
            α2 = ThreadLocalRandom.current().nextInt(0, rows - 1);
        }
        return α2;
    }

    private double[] αBounds(double α1, double α2, double y1, double y2) {
        double[] bounds = new double[2];
        bounds[0] = (y1 != y2) ? Math.max(0, α2 - α1)
                : Math.max(0, α2 + α1 - SOFT_PARAM_C);

        bounds[1] = (y1 != y2) ? Math.min(SOFT_PARAM_C, α2 - α1 + SOFT_PARAM_C)
                : Math.min(SOFT_PARAM_C, α2 + α1);
        return bounds;
    }

    private boolean optimizeAlphaPair(int i, int j, double firstAlphaError, double secondAlphaError, double η, double[] bounds, double αi, double αj) {
        boolean flag = false;
        α.setEntry(j, 0, α.getEntry(j, 0) - y.getEntry(j, 0) * (firstAlphaError - secondAlphaError) / η);
        αjClipping(j, bounds[1], bounds[0]); // clipping the αj, to be use for the optimization of the αi
        if (Math.abs(α.getEntry(j, 0) - αj) >= MIN_ALPHA_OPT) { // optimization of the αi with same value as αj, but in opposite direction
            αiOptimization(i, j, αj);
            flag = true;
        }
        return flag;
    }

    /**
     * Optimization of the αi with same value as αj, but in opposite direction
     *
     * @param i
     * @param j
     * @param αj
     */
    private void αiOptimization(int i, int j, double αj) {
        α.setEntry(i, 0, (α.getEntry(i, 0) + y.getEntry(j, 0) * y.getEntry(i, 0) * (αj - α.getEntry(j, 0))));
    }

    private void optimizeB(double firstAlphaError, double secondAlphaError, double αi, double αj, int i, int j) {

        double b1 = computeB(firstAlphaError, αi, αj, i, j, x.getRowMatrix(i));
        double b2 = computeB(secondAlphaError, αi, αj, i, j, x.getRowMatrix(j));

        if (0 < α.getRowMatrix(i).getEntry(0, 0) && SOFT_PARAM_C > α.getRowMatrix(i).getEntry(0, 0)) b = b1;
        else if (0 < α.getRowMatrix(j).getEntry(0, 0) && SOFT_PARAM_C > α.getRowMatrix(j).getEntry(0, 0)) b = b2;
        else b = (b1 + b2) / 2.0;
    }

    private double computeB(double alphaError, double αi, double αj, int i, int j, RealMatrix rowMatrix) {
        return b - alphaError - multiplyTwoMatrices(y.getRowMatrix(i), α.getRowMatrix(i).scalarAdd(-αi)).
                multiply(x.getRowMatrix(i).multiply(rowMatrix.transpose())).getEntry(0, 0)
                - multiplyTwoMatrices(y.getRowMatrix(j), α.getRowMatrix(j).scalarAdd(-αj)).
                multiply(rowMatrix.multiply(x.getRowMatrix(j).transpose())).getEntry(0, 0);
    }