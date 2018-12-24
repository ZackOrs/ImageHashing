package PackageA;
import PackageA.ImagePHash;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
a
//By Zachary Orsoli
//November 2018

//This class will search the file HashCodes.txt for hashes
//similar to the inputed image
//Takes seconds to run.

public class ImageSearch {
	
	private String hash;
	private String location;
	
	public String gethash() {
		return this.hash;
	}
	
	public String getlocation() {
		return this.location;
	}

	public void set(String h, String l) {
		
		this.hash = h;
		this.location = l;
	}
	
	
	//hamming distance formula
	//Compares two hashes and if enough binary chars are similar
	//images are considered similar
    private static Boolean HammingDistance(String key, String Two)
    {

    	if (key.length() != Two.length())
    		return false;
    	int distance = 0;
    	for (int i = 0; i < key.length(); i++)
    	{
    		if (key.charAt(i) != Two.charAt(i))
    			distance++;
    	}
    	if (distance < 9) {
    		System.out.printf("Hamming distance: %-7d ", distance);
    		return true;
    	}

    	return false;
    }

	public static void main(String[] args) throws IOException {

		BufferedReader br = null;	
		
		br = new BufferedReader (new FileReader("HashCodes.txt"));
		String hashcomp = "";
		File fileA = new File("Project_1_datasets/2/40694.jpg");
		System.out.println("The reference file is: "+ fileA.getPath());
		System.out.println();
		try {
			hashcomp = ImagePHash.getHash(fileA);
		
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		System.out.println("Searching for hashes similar to: " + hashcomp);
		System.out.println();
		System.out.println();
		int count = 0;
		String line;
		String[] splits= new String[2];
		Driver fileObject= null;

		
		
		System.out.println("Matching files");
		System.out.println("---------------------------------------------------");
		System.out.println();
		while((line = br.readLine()) != null)
        {
			fileObject = new Driver();
			splits = line.split(" ");
			fileObject.set(splits[0],splits[1]);
			
			if (HammingDistance(hashcomp,fileObject.gethash()) == true )
				{
					System.out.printf("%-37s %-43s\n",fileObject.getlocation()+"  ", fileObject.gethash());

					count++;
				}

        }
		System.out.println();
		System.out.println("Number of matching files: "+ count);
		
		if (br  != null) {
            br.close();
        }
	}

}
