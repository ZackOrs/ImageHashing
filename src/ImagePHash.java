package PackageA;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Stream;

import javax.imageio.ImageIO;




//NOTE: When this class is run, it was take a few minutes to compute all the hashes.
//For the search, use the other class, Drive.java
//hash codes are already written to the HashCodes.txt file

public class ImagePHash {

	private static int size = 16;
	private static int smallerSize = 8;
	
	public ImagePHash() {
		initCoefficients();
	}
	
	public ImagePHash(int size, int smallerSize) {
		ImagePHash.size = size;
		ImagePHash.smallerSize = smallerSize;
		
		initCoefficients();
	}
	
	
	// Returns a 'binary string' (like. 001010111011100010) which is easy to do a hamming distance on. 
	//takes an arraylist as an argument to be later to write to a file
	public static String getHash(File is , ArrayList<String> hashes) throws Exception {
		BufferedImage img = ImageIO.read(is);
		
		
		//reduce the size of the image to 16x16
		//this will simplify the DCT calculations

		img = resize(img, size, size);
		
		double[][] vals = new double[size][size];
		
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				vals[x][y] = getBlue(img, x, y);
			}
		}

		//get the DCT, this is a calculation used to find the frequencies and scalars
		
		
		double[][] dctVals = applyDCT(vals);

		
		//reduce the DCT to a smaller size, 8x8 size, it will take the top left 8x8
		double total = 0;
		
		for (int x = 0; x < smallerSize; x++) {
			for (int y = 0; y < smallerSize; y++) {
				total += dctVals[x][y];
			}
		}
		total -= dctVals[0][0];
		
		//get the average greyscale value of the top left  8x8 of the image
		
		double avg = total / (double) ((smallerSize * smallerSize) - 1);

		
		//reduce the DCT by making the hash read each of the 8x8 pixels
		//if the greyscale value of the current pixel is above the avg add 1 to the string
		//if the greyscale value of the current pixel is below the avg add a 0 to the string
		//this will output a binary string of 64 chars made of 1's and 0's
		String hash = "";
		
		for (int x = 0; x < smallerSize; x++) {
			for (int y = 0; y < smallerSize; y++) {
				if (x != 0 || y != 0) {
					hash += (dctVals[x][y] > avg?"1":"0");
				}
			}
		}
		
		//add the hash to the arraylist
		hashes.add(hash + " " + is.getPath());
		return hash;
	}
	
	//the same function without the arraylist
	//to be used to get the hash of the key file
	public static String getHash(File is) throws Exception {
		BufferedImage img = ImageIO.read(is);
		
		
		img = resize(img, size, size);

		double[][] vals = new double[size][size];
		
		for (int x = 0; x < img.getWidth(); x++) {
			for (int y = 0; y < img.getHeight(); y++) {
				vals[x][y] = getBlue(img, x, y);
			}
		}
		
		

		double[][] dctVals = applyDCT(vals);

	
		double total = 0;
		
		for (int x = 0; x < smallerSize; x++) {
			for (int y = 0; y < smallerSize; y++) {
				total += dctVals[x][y];
			}
		}
		total -= dctVals[0][0];
		
		double avg = total / (double) ((smallerSize * smallerSize) - 1);
	
		
		String hash = "";
		
		for (int x = 0; x < smallerSize; x++) {
			for (int y = 0; y < smallerSize; y++) {
				if (x != 0 || y != 0) {
					hash += (dctVals[x][y] > avg?"1":"0");
				}
			}
		}
		return hash;
	}
	
	
	//function to resize the image
	private static BufferedImage resize(BufferedImage image, int width,	int height) {
		BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(image, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}
	

	//since the image is in greyscale , r=b=g so get one of the three
	private static int getBlue(BufferedImage img, int x, int y) {
		return (img.getRGB(x, y)) & 0xff;
	}
	

	//main function, the DCT function to give the hash before it is modified
	private static double[] c;
	private static void initCoefficients() {
		c = new double[size];
        for (int i=1;i<size;i++) {
            c[i]=1;
        }
        c[0]=1/Math.sqrt(2.0);
    }
	
	//Note the DCT function was found here:
	//https://stackoverflow.com/questions/4240490/problems-with-dct-and-idct-algorithm-in-java
	private static double[][] applyDCT(double[][] f) {
		int N = size;
		initCoefficients();
		
        double[][] F = new double[N][N];
        for (int u=0;u<N;u++) {
        
          for (int v=0;v<N;v++) {
            double sum = 0.0;
            for (int i=0;i<N;i++) {
              for (int j=0;j<N;j++) {
            	 
                sum+=Math.cos(((2*i+1)/(2.0*N))*u*Math.PI)*Math.cos(((2*j+1)/(2.0*N))*v*Math.PI)*(f[i][j]);
          
              }
            }


            sum*=((c[u]*c[v])/4.0);

            F[u][v] = sum;

            
          }
        }
        return F;
	}
	
	
	 static void printArray (ArrayList<String> Hashes){
	    	for (int i = 0; i < Hashes.size(); i++) {
	            System.out.println(Hashes.get(i).toString());
	    	}
	 }
	
	
	
	
	public static void main(String[] args) throws IOException {
		

		ArrayList <String> hashes = new ArrayList<String>();

		
		System.out.println();

		//walk function will go through every file inside the folder specified
		try (Stream<Path> filePathStream=Files.walk(Paths.get("Project_1_datasets"))) {
			filePathStream.forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					File fileB = new File(filePath.toString());
					try {
						getHash(fileB, hashes);
						System.out.println(fileB.getPath());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
	}

		PrintWriter outputStream = new PrintWriter(new FileWriter("HashCodes.txt"));
		for (Object cur : hashes) {
            outputStream.println(cur);
        }
		
		outputStream.close();
		System.out.println("Done hashing");
		
		
	}
}