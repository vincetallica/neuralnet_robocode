
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class NeuralNet implements NeuralNetInterface {

	public int NUM_OUTPUTS, NUM_HIDDEN;
	public static int NUM_INPUTS, NUM_PATTERNS, NUM_EPOCH;
	public double errThisPat, LR, MT, LB, UB;
	public double errSig, weightChange = 0.0;

	// Outputs 
    public double outputNeuron[] = new double[NUM_OUTPUTS];
    public double hiddenNeuron[] = new double[NUM_HIDDEN];    // u = Hidden node outputs.
    public double weightsIH[][] =  new double[NUM_INPUTS][NUM_HIDDEN]; // Input to Hidden weights.
    public double weightsHO[] = new double[NUM_HIDDEN];    // Hidden to Output weights.
    public double weightsBO[] =  new double[NUM_OUTPUTS]; // Input to Hidden weights.
    public double weightsBH[] = new double[NUM_HIDDEN];
    public double prev_deltaWIH[][] = new double[NUM_INPUTS][NUM_HIDDEN];
    public double prev_deltaWHO[] = new double[NUM_HIDDEN];
 
    // Inputs
    public static double beta = 1.0;
    public boolean BF;

	public NeuralNet (
			int argNumInputs,
			int argNumHidden,
			double argLearningRate,
			double argMomentumTerm,
			double argA,
			double argB,
			boolean bipolar_flag,
			boolean NW)
	{
		NUM_OUTPUTS = 1;
		NUM_INPUTS = argNumInputs+1; //argNumInputs [3 inputs in input vector X] with bias
		NUM_HIDDEN = argNumHidden+1; // argNumHidden [4 hidden neurons in layer, 1 bias]
		NUM_PATTERNS = 4;

		BF = bipolar_flag;
	    LR = argLearningRate;      //argLearningRate, Learning rate, input to hidden weights
	    MT = argMomentumTerm;	//argMomentumTerm
	    LB = argA; //argA,lowerbound of sigmoid by output neuron only
	    UB = argB; //argB,upperbound of custom sigmoid by output neuron only
	    hiddenNeuron = new double[NUM_HIDDEN];    // u = Hidden node outputs.
	    outputNeuron = new double[NUM_OUTPUTS];
	    weightsIH =  new double[NUM_INPUTS][NUM_HIDDEN]; // Input to Hidden weights.
	    weightsHO = new double[NUM_HIDDEN];    // Hidden to Output weights.
	    weightsBO = new double[NUM_OUTPUTS]; //hidden bias to output
	    weightsBH = new double[NUM_HIDDEN]; //input bias X[3] to hidden
	    prev_deltaWIH = new double[NUM_INPUTS][NUM_HIDDEN];
	    prev_deltaWHO = new double[NUM_HIDDEN];

	    if (NW == true) {
	    	beta = (0.7 * Math.pow(NUM_HIDDEN, (1.0 / NUM_INPUTS)));
	    }
	}

	// Internal functions
	public double bipolar_sig (double x) {
		return ((2.0/(1 + Math.pow(Math.E, (-1) * x))) - 1) ; //[-1, 1]
	}
	public double sigmoid (double x) {
		return (1.0/(1 + Math.pow(Math.E, (-1) * x))); //[0, 1]
	}
	public double d_bipolar_sig(double x) {
		return (1-Math.pow(x, 2));
	}
	public double d_sig(double x) {
		return (x * (1 - x));
	}
	public double customSigmoid (double x) {
		return ((UB-LB)/(1 + Math.pow(Math.E, (-1) * x)) - LB);
	}

	public void initializeWeights() {
		// input vector X [0]=-1/1, [1]=1/-1, [2]=1 
		double InputsNorm=0.0;
		for(int j=0; j < NUM_HIDDEN; j++ )
		{
			weightsHO[j] = (new Random().nextDouble() - 0.5); // [-0.5, 0.5]
			System.out.println("Weight = " + weightsHO[j]);
			for (int i = 0; i < NUM_INPUTS; i++) {
				weightsIH[i][j] = (new Random().nextDouble() - 0.5); // [-0.5, 0.5]
				System.out.println("IH Weight = " + weightsIH[i][j]);
				InputsNorm += Math.pow(weightsIH[i][j], 2);
			}
		}
		//Nguyen Widrow Adjustment
        InputsNorm = Math.sqrt(InputsNorm);
        if (beta != 1.0) {
        	for(int j=0; j < NUM_HIDDEN; j++ )
    		{
        		for (int i = 0; i < NUM_INPUTS; i++) {
        			weightsIH[i][j] = (beta*weightsIH[i][j])/InputsNorm;
        		}
    		}
        }
		return;
	}
	public void zeroWeights() {
		/*for(int j=0; j < NUM_HIDDEN; j++ )
		{
			hiddenNeuron [j] = 0;
			if (j == NUM_HIDDEN-1) {
				hiddenNeuron [j] = 1; //bias neuron
			}
		}*/
		return;
	}

	@Override
	public double outputFor (double[] X) {
		// Calculate hidden nerons' activations (Forward propagation)
		for (int i = 0; i< NUM_HIDDEN; i++) {
			hiddenNeuron[i] = 0.0;
			if (i==NUM_HIDDEN-1)
				hiddenNeuron[i] = 1.0; //bias hidden=1.0
			else {
				for (int j = 0; j<NUM_INPUTS; j++) {//size of input vector
					hiddenNeuron[i] += (X[j]*weightsIH[j][i]);
				}
				hiddenNeuron[i] = BF ? bipolar_sig(hiddenNeuron[i]):sigmoid(hiddenNeuron[i]);
			}
		}
		double outPred = 0.0;
		// Calculate output neuron value (Forward propagation)
     	for (int i = 0; i < NUM_HIDDEN; i++) 
     	{//size of input vector
     		outPred += (hiddenNeuron[i] * weightsHO[i]);
		}
     	outPred = (BF ? bipolar_sig(outPred) : sigmoid(outPred));
     	outputNeuron[0] = outPred;
		return outputNeuron[0]; //if outputFor() will be iterated across # of neurons
	}

	public double train (double[] X, double argValue) {
		// Calculate weightHO changes based on errOutput/fprime_err
		/* for (int i=0;i<NUM_HIDDEN; i++)
		{ 	
			for (int j = 0; j<NUM_OUTPUTS; j++) {
				BPweightSum[j] += outputNeuron[j]*errOutput[j];
				//update HO weights 
				weightsHO[j][i] += (LR*BPweightSum[j]);
			}
		} */
		outputFor(X);
		errThisPat = argValue - outputNeuron[0];

		for (int k=0;k<NUM_HIDDEN; k++)
		{ 
			errSig = BF ? d_bipolar_sig(outputNeuron[0]): d_sig(outputNeuron[0]);
			errSig = errThisPat*errSig;
			double weightChange = LR *errSig*hiddenNeuron[k];
			weightChange = weightChange + prev_deltaWHO[k]*MT;
			weightsHO[k] += weightChange;
			prev_deltaWHO[k] = weightChange;//store previous weight change

            // Regularization of the output weights.
            /*if(weightsHO[k] < -5){
                weightsHO[k] = -5;
            }else if(weightsHO[k] > 5){
                weightsHO[k] = 5;
            }*/
		}

		// Calculate weightIH changes based on weightsHO
		for (int i=0;i<NUM_HIDDEN; i++)
		{
			for (int j = 0; j<NUM_INPUTS; j++) {
				double x = BF ? d_bipolar_sig(hiddenNeuron[i]): d_sig(hiddenNeuron[i]);
				x = LR*x*(weightsHO[i])*errSig;
				x = x*X[j];
				double weightChange = x;
				/*for(int k =0; k<NUM_OUTPUTS; k++) {
					BPweightSumHI[j] = LR*d_sig(hiddenNeuron[i])*errOutput[k]*(weightsHO[k][i]);
					//might not be multiplying input X[j] for S'(x)
				}*/
				//update IH weights 
				//weightsIH[j][i] += (BPweightSumHI[j]);
				weightChange = weightChange + prev_deltaWIH[j][i]*MT;
				weightsIH[j][i] += weightChange;
				prev_deltaWIH[j][i] = weightChange; //store prev weightchange
			}
			
		}
		outputNeuron[0] =  outputFor(X); //recalculate u0
		// Threshold output (regularization)
		/*if (!BF) {
			if (outputNeuron[0] >= 0.8) {
				outputNeuron[0] = 1.0;
			} 
			else if (outputNeuron[0] <= 0.2) {
				outputNeuron[0] = 0.0;
			}
		}*/
		errThisPat = argValue - outputNeuron[0];
		return (errThisPat); //return error for training pattern
	}

	@Override
	public void save(File argFile) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void load(String argFileName) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
