package sm;

import java.io.File;
import robocode.RobocodeFileOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import robocode.AdvancedRobot;

public class LUT implements LUTInterface {

	double [][] lookuptable;
	int [] stateActionSpace;
	int N;
	double alpha;
	double gamma;
	AdvancedRobot robo; 
	public LUT(AdvancedRobot robot) {
		System.out.println("Creating Lookup Table :) ");
		int N_actions = 7;
		int N_distances = 4;
		stateActionSpace =  new int [] {3,3,N_distances,N_actions};
		this.N = 3*3*N_distances*N_actions; //location,myenergy,distance,action
		this.lookuptable = new double [N][(4+1+1)];//index,location,myenery,distance,action,Q
		this.initialiseLUT();
		gamma = 0.1;
		alpha = 0.1;
		robo = robot;
	}
	@Override
	public double[] outputFor(double[] X) {
		// TODO Auto-generated method stub
		double [] output;
		int index = 0;
		double maxQ = 0;
		for(int i0 = 0; i0 < stateActionSpace[0]; i0++) { //iterates over the location
			for(int i1 = 0; i1 < stateActionSpace[1]; i1++) { //iterates over the energy
				for(int i2 = 0; i2 < stateActionSpace[2]; i2++) { //iterates over the distance
					if(i0 == X[0] && i1 == X[1] && i2 == X[2]) {
						System.out.println("My State is: "+i0+", "+i1+", "+i2+","+lookuptable[index][4]);

					}
					index = index + 1;
				}
			}
		}
		return null;
	}

	@Override
	public double train(double[] X, double[] argValue) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void save(File argFile) {
		// TODO Auto-generated method stub
		PrintStream w = null;
		try {
			w = new PrintStream(new RobocodeFileOutputStream(robo.getDataFile("count.dat")));

			int index = 0;
			w.println("index, i0, i1,i2, i3, Q");
			for(int i0 = 0; i0 < stateActionSpace[0]; i0++) { //iterates over the location
				for(int i1 = 0; i1 < stateActionSpace[1]; i1++) { //iterates over the energy
					for(int i2 = 0; i2 < stateActionSpace[2]; i2++) { //iterates over the distance
						for(int i3 = 0; i3 < stateActionSpace[3]; i3++) { //iterates over the action
							w.println(index +", "+i0+", "+i1+", "+i2+", "+i3+", "+lookuptable[index][5]);
							index = index + 1;
						}
					}
				}
			}
			// PrintStreams don't throw IOExceptions during prints, they simply set a flag.... so check it here.
			if (w.checkError()) {
				System.out.println("I could not write the count!");
			}
		} catch (IOException e) {
			System.out.println("IOException trying to write: ");

		} finally {
			if (w != null) {
				w.close();
			}
		}
	}

	@Override
	public void load(String argFileName) throws IOException {
		// TODO Auto-generated method stub
		try {
			BufferedReader reader = null;
			try {
				// Read file "count.dat" which contains 2 lines, a round count, and a battle count
				reader = new BufferedReader(new FileReader(robo.getDataFile("count.dat")));
				String line = reader.readLine();
				int index = 0;
				line = reader.readLine(); //skip first line
				while ((line = reader.readLine()) !=null) {
					String[] b = line.split(",");
					System.out.println(line);					
					for(int i = 0; i < b.length; i++) {
						lookuptable[index][i] = Double.parseDouble(b[i]);
					}
					
					System.out.println(lookuptable[index][0] +", "+lookuptable[index][1]+", "+lookuptable[index][2]+", "+lookuptable[index][3]+", "+lookuptable[index][4]+", "+lookuptable[index][5]);
					index = index +1;
				}
				// Try to get the counts
				//roundCount = Integer.parseInt(reader.readLine());
				//battleCount = Integer.parseInt(reader.readLine());

			} finally {
				if (reader != null) {
					reader.close();
				}
			}
		} catch (IOException e) {
			// Something went wrong reading the file, reset to 0.

		} catch (NumberFormatException e) {
			// Something went wrong converting to ints, reset to 0

		}
	}

	@Override
	public void initialiseLUT() {
		// TODO Auto-generated method stub
		int index = 0;
		for(int i0 = 0; i0 < stateActionSpace[0]; i0++) { //iterates over the location
			for(int i1 = 0; i1 < stateActionSpace[1]; i1++) { //iterates over the energy
				for(int i2 = 0; i2 < stateActionSpace[2]; i2++) { //iterates over the distance
					for(int i3 = 0; i3 < stateActionSpace[3]; i3++) { //iterates over the action
						lookuptable[index][0] = index;
						lookuptable[index][1] = i0;
						lookuptable[index][2] = i1;
						lookuptable[index][3] = i2;
						lookuptable[index][4] = i3;
						lookuptable[index][5] = 0; //Math.random();
						//lookuptable[index][4] = rand.nextInt((2 - 0) + 1) + 0;
						//System.out.println("index " + index +","+i0+","+i1+","+i2+","+i3+","+lookuptable[index][4]);
						System.out.println(lookuptable[index][0] +", "+lookuptable[index][1]+", "+lookuptable[index][2]+", "+lookuptable[index][3]+", "+lookuptable[index][4]+", "+lookuptable[index][5]);
						index = index + 1;
					}
				}
			}
		}
	}

	@Override
	public int indexFor(double[] X) { //returns index of max Q
		// TODO Auto-generated method stub
		int index = 0;
		int indexToReturn = 0;
		double maxQ = -1e6;
		//System.out.println("I am looking for this state "+X[0]+", "+X[1]+", "+X[2]);
		for(int i0 = 0; i0 < stateActionSpace[0]; i0++) { //iterates over the location
			for(int i1 = 0; i1 < stateActionSpace[1]; i1++) { //iterates over the energy
				for(int i2 = 0; i2 < stateActionSpace[2]; i2++) { //iterates over the distance
					
					if(i0 == (int) X[0] && i1 == (int) X[1] && i2 == (int) X[2]) {//The correct state has been found. Now search for the max Q.
						for(int i3 = 0; i3 < stateActionSpace[3]; i3++) { //iterates over the action
							if (maxQ == -1e6) { //If it is the first iteration then set to first q
								maxQ = lookuptable[index][4];
								indexToReturn = index;
							} else if (lookuptable[index][4] > maxQ) { //if there is a bitter q. then select that action.
								maxQ = lookuptable[index][4];
								indexToReturn = index;
							}
							index = index + 1;
						}
						//System.out.println("Index for " + indexToReturn +", "+i0+", "+i1+", "+i2+","+lookuptable[indexToReturn][4]);
						return indexToReturn;
					}
					
					index = index + stateActionSpace[3];
				}
			}
		}
		System.out.println("COULDN'T FIND INDEX");
		return 0;
	}
	
	public void updateQ(int maxQindex, int prevIndex, double r) {
		// TODO Auto-generated method stub
		double dQ;
		
		double prevQ = this.lookuptable[prevIndex][5];
		double nextQ = this.lookuptable[maxQindex][5];
		
		dQ = this.alpha*(r + this.gamma*nextQ-prevQ);
		this.lookuptable[prevIndex][5] = prevQ + dQ;
		System.out.println("Updating Q");
		return;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello world");

	}

	public void printLUT() {
		// TODO Auto-generated method stub
		int index = 0;
		System.out.println("Printing Lookup table");
		for(int i0 = 0; i0 < stateActionSpace[0]; i0++) { //iterates over the location
			for(int i1 = 0; i1 < stateActionSpace[1]; i1++) { //iterates over the energy
				for(int i2 = 0; i2 < stateActionSpace[2]; i2++) { //iterates over the distance
					for(int i3 = 0; i3 < stateActionSpace[3]; i3++) { //iterates over the action
						//System.out.println("index " + index +", "+i0+", "+i1+", "+i2+", "+i3+", "+lookuptable[index][4]);
						System.out.println(lookuptable[index][0] +", "+lookuptable[index][1]+", "+lookuptable[index][2]+", "+lookuptable[index][3]+", "+lookuptable[index][4]+", "+lookuptable[index][5]);
						index = index + 1;
					}
				}
			}
		}
	}
	
}
