package com.chamelaeon.dicebot.random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.BitSet;

import org.apache.commons.math3.special.Erf;
import org.apache.commons.math3.special.Gamma;
import org.jtransforms.fft.DoubleFFT_1D;
import org.junit.Test;

import com.chamelaeon.dicebot.statistics.ChiSquaredStatistics;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class MersenneTwisterRandomTest {
    // Flags for matrix operations in one of the NIST tests.
    int MATRIX_FORWARD_ELIMINATION = 0;
    int MATRIX_BACKWARD_ELIMINATION = 1;
    
    MersenneTwisterRandom random = new MersenneTwisterRandom();
    
    @Test
    public void testGetRollInt() {
        // Just verify this works.
        int roll = random.getRoll(100);
        assertTrue(roll >= 1);
        assertTrue(roll <= 100);
    }

    @Test
    public void testGetRollIntStatistics() {
        ChiSquaredStatistics statistics = new ChiSquaredStatistics();
        
        for (int i = 0; i < 1_000_000; i++) {
            random.getRoll(100, statistics);
        }
        
        assertEquals(1000000, statistics.getDice());
        assertEquals(50.50, statistics.getAverage(100), 0.05);
    }
    
    @Test
    public void testSequenceFromInitialSeedVsReference() {
        random.initializeGenerator(5489);
        
        for (int i = 0; i < referenceResults.length; i++) {
            assertEquals(referenceResults[i], random.nextInt());
        }
    }
    
    // All these tests are specified as the NIST recommendations for PRNGs.
    // Documentation available at: http://csrc.nist.gov/groups/ST/toolkit/rng/documents/SP800-22rev1a.pdf 
    
    @Test
    public void frequencyMonobitTest() {
        // Bit string length. 
        int n = 10000;
        
        int[] epsilon = generateBitArray(random, n);
        
        int runningTotal = 0;
        for (int i = 0; i < n; i++) {
            runningTotal += (2 * epsilon[i]) - 1;
        }
        
        double avg = Math.abs((double) runningTotal) / Math.sqrt(n);
        double p = 1 - Erf.erf(avg / Math.sqrt(2)); 
        assertTrue(p > 0.01);
    }
    
    @Test
    public void frequencyTestWithinABlock() {
        // Bit string length. 
        int n = 1000000;
        // Block length.
        int M = 200;
        // number of blocks.
        int N = n / M;
        
        // Get the data.
        int[] epsilon = generateBitArray(random, n);
        
        // For each block...
        double[] piForEachBlock = new double[N];
        for (int i = 0; i < N; i++) {
            double onesTotal = 0;
            
            // For each bit in that block...
            for (int j = 0; j < M; j++) {
                // Find the proportion of 1s in the block.
                if (epsilon[(i * M) + j] == 1) {
                    onesTotal++;
                }
            }
            
            piForEachBlock[i] = onesTotal / M;
        }
        
        // Calculate chi-squared.
        double rollingSum = 0;
        for (int i = 0; i < piForEachBlock.length; i++) {
            rollingSum += Math.pow((piForEachBlock[i] - 0.5), 2); 
        }
        
        double chiSquared = 4 * M * rollingSum;
        double p = Gamma.regularizedGammaQ(((double) N)/2.0, chiSquared / 2.0);
        assertTrue(p > 0.01);
    }
    
    @Test
    public void runsTest() {
        // Bit string length.
        int n = 100;
        // Threshold value.
        double tau = 2.0 / Math.sqrt(n);
        
        // Bit string.
        int[] epsilon = generateBitArray(random, n);
        
        // Calculate the number of 1s.
        double pi = 0;
        for (int i = 0; i < epsilon.length; i++) {
            pi += epsilon[i];
        }
        pi = pi / (double) n;
        
        
        if (Math.abs(pi - 0.5) >= tau) {
            fail("Failed the frequency test, will not run the runs test.");
        }
        
        int v = 1;
        // Do the actual runs test.
        for (int i = 0; i < epsilon.length - 1; i++) {
            int r = epsilon[i] == epsilon[i + 1] ? 0 : 1;
            v += r;
        }
        
        double p = 1- Erf.erf(
                    (Math.abs(v - (2 * n * pi * (1 - pi)))) /
                    (2 * Math.sqrt(2 * n) * pi * (1 - pi)) 
                );
        
        assertTrue(p > 0.01);
    }
    
    @Test
    public void longestRunOfOnesInABlockTest() {
        // Bit string length.
        int n = 256;
        // Set M to 8 because of the length of n.
        int M = 8;
        // Set K to 3 because of the value of M.
        int K = 3;
        // The number of blocks.
        int N = n / M;
        
        int[] epsilon = generateBitArray(random, n);
        
        // Calculate the length of the longest consecutive run of 1s for each block.
        int v[] = new int[K + 1];
        for (int i = 0; i < N; i++) {
            int maxRun = 0;
            int currentRun = 0;
            int lastValue = 1;
            for (int j = 0; j < M; j++) {
                int newValue = epsilon[(i * M) + j];
                if (1 == newValue && 1 == lastValue) {
                    // Run continues, increase the count.
                    currentRun++;
                } else if (1 == newValue && 0 == lastValue) {
                    // New run, reset the count.
                    currentRun = 1;
                }
                
                if (currentRun > maxRun) {
                    maxRun++;
                }
                lastValue = newValue;
            }
            // Increment the count in the correct class.
            // TODO: Rangemap?
            if (maxRun <= 1) {
                v[0]++;
            } else if (maxRun == 2) {
                v[1]++;
            } else if (maxRun == 3) {
                v[2]++;
            } else {
                v[3]++;
            }
        }
        
        /* The expected probabilities of a run of a certain length. Each cell in the array corresponds to a length
         * value of the longest run: pi[0] = a run <=4, pi[1] = 5, pi[2] = 6, pi[3] = 7, pi[4] = 8, pi[5] >= 9.
         * THIS ARRAY IS TIED TO THE VALUES CHOSEN FOR M AND K. If you change those you'll have to go back to the 
         * paper to get different values. */
        double[] piForEachBlock = new double[] { 0.1174, 0.2430, 0.2493, 0.1752, 0.1027, 0.1124 };
        
        // Calculate the chi-squared of actual vs expected.
        double chiSquared = 0;
        for (int i = 0; i < K + 1; i++) {
            double nTimesPi = (double) N * piForEachBlock[i];
            
            chiSquared += Math.pow(v[i] - nTimesPi, 2) / nTimesPi;
        }
        
        double p = Gamma.regularizedGammaQ(((double) K) / 2.0, chiSquared / 2);
        assertTrue(p > 0.01);
    }

    @Test
    public void binaryMatrixRankTest() throws IOException {
        // Bit string length.
        int n = 100_000;
        // Number of rows. Defined by the NIST paper.
        int M = 32;
        // Number of the columns. Defined by NIST paper.
        int Q = 32;
        // Number of blocks.
        int N = n / (M * Q);
        int[] epsilon = generateBitArray(random, n); 
        
        // Build the matrices.
        int epsilonIndex = 0;
        int[][][] matrices = new int[N][M][Q];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                for (int k = 0; k < Q; k++) {
                    matrices[i][j][k] = epsilon[epsilonIndex];
                    epsilonIndex++;
                }
            }
        }
        
        // Determine binary rank for each matrix.
        int[] R = new int[N];
        int maxRankCount = 0;
        int oneUnderMaxRankCount = 0;
        for (int i = 0; i < N; i++) {

            R[i] = determineBinaryRank(M, Q, matrices[i]);
            if (R[i] == M) {
                maxRankCount++;
            } else if (R[i] == M - 1) {
                oneUnderMaxRankCount++;
            }
        }
        int remainder = N - maxRankCount - oneUnderMaxRankCount;
        
        // Compute chi-squared.
        double chiSquared = (Math.pow(maxRankCount - (0.2888 * N), 2) / (0.2888 * N)) 
                          + (Math.pow(oneUnderMaxRankCount - (0.5776 * N), 2) / (0.5776 * N)) 
                          + (Math.pow(remainder - (0.1336 * N), 2) / (0.1336 * N));
        
        double p = Gamma.regularizedGammaQ(1, chiSquared / 2);
        assertTrue(p > 0.01);
    }
    
    @Test
    public void discreteFourierTransformTest() {
        // Bit string length.
        int n = 100_000;
        
        int[] epsilon = generateBitArray(random, n);
        
        // Threshold for peak detection.
        double T = Math.sqrt(n * Math.log(1/0.05));
        // Theoretical number of peaks less than T.
        double N0 = (.95 * n) / 2;
        
        double[] X = new double[n];
        for (int i = 0; i < n; i++) {
            X[i] = (2 * epsilon[i]) - 1;
        }
        
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(X);
        
        // Check how many peaks are > T.
        /* NOTE: I firmly believe the NIST C code is wrong in their implementation and their example.
         * For the life of me, I cannot get their example epsilon to come out with the same N1 - I get 47
         * where they get 46. Based on the actual description of methods, this should be right... */
        double N1 = 0;
        for (int i = 0; i < n / 2; i++) {
           double magnitude = Math.sqrt(Math.pow(X[2 * i], 2) + Math.pow(X[2 * i + 1], 2));
           if (magnitude < T) {
               N1++;
           }
        }
        
        double d = (N1 - N0) / Math.sqrt(n * 0.95 * 0.05 / 4);
        double p = Erf.erfc(Math.abs(d) / Math.sqrt(2));
        
        assertTrue(p > 0.01);
    }
    
    @Test
    public void nonOverlappingTemplateMatchingTest() throws IOException {
    	// The length of the template in bits. Fixed at 9 in my test.
    	int m = 10; 
    	// The length of the bitstring.
    	int n = 1_000_000;
    	// Number of blocks. Fixed to the same value as the NIST code.
    	int N = 8;
    	// Size of the blocks.
    	int M = n / N;
    	
    	random.initializeGenerator(9770116);
    	int[] epsilon = generateBitArray(random, n);
    	
    	// Theoretical mean
    	double mu = (M - m + 1) / Math.pow(2, m);
    	// Theoretical variance
    	double sigmaSquared = M * ((1 / Math.pow(2, m)) - ((2 * m -1) / Math.pow(2, 2 * m)));
    	
    	// The templates.
    	int[][] B = readTemplatesFromFile();
    	
    	int[][] W = new int[B.length][N];
    	// For each template....
    	for (int jj = 0; jj < B.length; jj++) {
    		int[] template = B[jj];
    		
    		for (int i = 0; i < N; i++) {
    		    int Wobs = 0;
    		    for (int j = 0; j < M - m + 1; j++) {
    		        boolean match = true;
    		        for (int k = 0; k < m; k++) {
                        if (template[k] != epsilon[i * M + j + k]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        Wobs++;
                        j += m - 1;
                    }
                }
    		    W[jj][i] = Wobs;
    		}
    	}
    	
    	// Compute chi squared and the p-value for each of the templates.
    	for (int i = 0; i < W.length; i++) {
			int[] results = W[i];
			double chiSquared = 0;
			for (int j = 0; j < results.length; j++) {
				chiSquared += Math.pow(results[j] - mu, 2) / sigmaSquared;
			}
			
			double p = Gamma.regularizedGammaQ(N / 2, chiSquared / 2);
			assertTrue(p > 0.01);
		}
    }
    
    /* All methods were sourced from the test code from NIST. I converted them from C to Java and cleaned them up,
        sometimes breaking a single method into two and removing a boolean flag. Otherwise they are largely unchanged. */
    private int determineBinaryRank(int M, int Q, int[][] matrix) {
        int m = Math.min(M, Q);
        
        /* FORWARD APPLICATION OF ELEMENTARY ROW OPERATIONS */ 
        for (int i = 0; i < m - 1; i++) {
            if (matrix[i][i] == 1) 
                performForwardElementaryElimination(i, M, Q, matrix);
            else {  
                if (findUnitElementAndSwapForward(i, M, Q, matrix)) { 
                    performForwardElementaryElimination(i, M, Q, matrix);
                }
            }
        }

        /* BACKWARD APPLICATION OF ELEMENTARY ROW OPERATIONS */ 
        for (int i = m - 1; i > 0; i--) {
            if (matrix[i][i] == 1)
                performBackwardElementaryElimination(i, M, Q, matrix);
            else {  /* matrix[i][i] = 0 */
                if (findUnitElementAndSwapBackward(i, M, Q, matrix)) {
                    performBackwardElementaryElimination(i, M, Q, matrix);
                }
            }
        } 

        return determineRank(m, M, Q, matrix);
    }
    
    private void performForwardElementaryElimination(int i, int M, int Q, int[][] A) {
        for (int j = i + 1; j < M;  j++) {
            if (A[j][i] == 1) {
                for (int k = i; k < Q; k++) { 
                    A[j][k] = (A[j][k] + A[i][k]) % 2; 
                }
            }
        }
    }
    
    private void performBackwardElementaryElimination(int i, int M, int Q, int[][] A) {
        for (int j = i - 1; j >= 0; j--) {
            if (A[j][i] == 1) {
                for (int k = 0; k < Q; k++) {
                    A[j][k] = (A[j][k] + A[i][k]) % 2;
                }
            }
        }
    }

    private boolean findUnitElementAndSwapForward(int i, int M, int Q, int[][] A) { 
        int index = i + 1;
        while ((index < M) && (A[index][i] == 0)) {
            index++;
        }
        if ( index < M ) {
            swapRows(i, index, Q, A);
            return true;
        }
                
        return false;
    }
    
    private boolean findUnitElementAndSwapBackward(int i, int M, int Q, int[][] A) {
        int index = i - 1;
        while ((index >= 0) && (A[index][i] == 0)) {
            index--;
        }
        if ( index >= 0 ) {
            swapRows(i, index, Q, A);
            return true;
        }
    
        return false;
    }
    
    private void swapRows(int i, int index, int Q, int[][] A) {
        for (int p = 0; p < Q; p++) {
            int temp = A[i][p];
            A[i][p] = A[index][p];
            A[index][p] = temp;
        }
    }

    private int determineRank(int m, int M, int Q, int[][] A) {
        /* DETERMINE RANK, THAT IS, COUNT THE NUMBER OF NONZERO ROWS */
        int rank = m;
        for (int i = 0; i < M; i++) {
            boolean allZeroes = true;
            for (int j = 0; j < Q; j++)  {
                if (A[i][j] == 1) {
                    allZeroes = false;
                    break;
                }
            }
            
            if (allZeroes) {
                rank--;
            }
        } 
                
        return rank;
    }
    
    private static int[] generateBitArray(Random random, int length) {
        int[] retVal = new int[length];
        for (int i = 0; i < retVal.length; i++) {
            retVal[i] = random.getRoll(2) - 1;
        }
        return retVal;
    }
    
    public static int[] generateBitArrayFromBinaryExpansionOfE(int length) throws IOException {
        BufferedReader reader = null;
        try{
            int[] bits = new int[length];
            int i = 0;
            reader = new BufferedReader(new InputStreamReader(MersenneTwisterRandomTest.class.getResourceAsStream("data.e")));
            while (reader.ready() && i < length) {
                char ch = (char) reader.read();
                if (ch == '0') {
                    bits[i++] = 0;
                } else if (ch == '1') {
                    bits[i++] = 1;
                }
            }
            
            return bits;
        } finally {
            Closeables.closeQuietly(reader);
        }
    }
    
    private static int[][] readTemplatesFromFile() throws IOException {
        BufferedReader reader = null;
        try{
            int[][] bits = new int[124][10];
            reader = new BufferedReader(new InputStreamReader(MersenneTwisterRandomTest.class.getResourceAsStream("template10")));
            int i = 0;
            while (reader.ready() && i < bits.length) {
                char[] chars = reader.readLine().toCharArray();
                int bitTracker = 0;
                for (int j = 0; j < chars.length; j++) {
                    if (chars[j] == '0') {
                        bits[i][bitTracker++] = 0;
                    } else if (chars[j] == '1') {
                        bits[i][bitTracker++] = 1;
                    }
                }
                i++;
            }
            
            return bits;
        } finally {
            Closeables.closeQuietly(reader);
        }
    }
    
    private static final long[] referenceResults = new long[] {-795755684, 581869302, -404620562, -708632711, 545404204, 
        -133711905, -372047867, 949333985, -1579004998, 1323567403, 418932835, -1944672731, 1196140740, 809094426, -1946129057, 
        -30574576, -182506777, -15198492, -150802599, -138749190, 676943009, -1177512687, -126303053, -81133257, -183966550, 
        471852626, 2084672536, -867128743, -857788836, 1275731771, 609397212, 20544909, 1811450929, 483031418, -361913170, 
        -1547204601, -892462743, -522136403, -173978709, -2131752568, -1478582452, -867889990, 153380495, 1551745920, -647984699, 
        910208076, -283496851, -1368550362, -1379821989, 1712568902, -1040498238, -1113911603, -1103237636, 2039073006, 1684602222, 
        1812852786, -1479711180, 746745227, 735241234, 1296707006, -1262522457, -870676135, 136721026, 1359573808, 1189375152, 
        -547914046, 198304612, 640439652, 417177801, -25475623, -758242871, -764919654, -1310701087, 537655879, 1361931891, 
        -1014685970, -213794687, 2107063880, 147944788, -1444803288, 1884392678, 540721923, 1638781099, 902841100, -1007097710, 
        219972873, -879609714, 156513983, 802611720, 1755486969, 2103522059, 1967048444, 1913778154, 2094092595, -1519074049, 
        -884870760, -1248268554, -339840185, -1053612696, -826647952};
}

