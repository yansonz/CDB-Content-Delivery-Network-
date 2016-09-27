import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.Queue;

public class RabinFunction {
	static int windowSize = 3;
	//static int q = 2048;
	static int q = 128;
    
//    private static int hash(byte[] b) {
//    	int sum = 0;
//    	int retVal = 0;
//    	
//    	for (int j = 0; j < b.length; j++) {
//    		//System.out.println(b[j]);
//    		sum = sum + (b[j] ^ j);
//    	}
//    	retVal = sum % q;
//    	System.out.println("data: " + b[0] + "/" + b[1] + "/" + b[2] + " + result: " + retVal);
//    	return retVal;
//    }
    
    private static int hash(byte b1, byte b2, byte b3) {
    	int sum = 0;
    	int retVal = 0;
    	
    	sum = b1^0 + b1^1 + b2^2;
    	retVal = sum % q;
//    	System.out.println("data: " + b1 + "/" + b2 + "/" + b3 + " + result: " + retVal);
    	return retVal;
    }
    
    public Queue<Integer> valueBlocks(String fileName) throws IOException {
		
		//String sample = "The key to the Rabin–Karp algorithm's performance is the efficient computation of hash values of the successive substrings of the text. The Rabin fingerprint is a popular and effective rolling hash function. The Rabin fingerprint treats every substring as a number in some base, the base being usually a large prime. For example, if the substring is /hi/ and the base is 101, the hash value would be 104 × 1011 + 105 × 1010 = 10609 (ASCII of 'h' is 104 and of 'i' is 105).";
		
		File f = new File(fileName);
		
		FileInputStream fis = new FileInputStream(fileName);
		
		Queue<Integer> queue = new LinkedList<Integer>();
		
		byte[] buffer = Files.readAllBytes(f.toPath());
		int h = -1;
		for(int i = 0; i < (f.length() - windowSize + 1); i++) {
			h = hash(buffer[i], buffer[i+1], buffer[i+2]);
			if(h == 0) {
				// variable i starts from ZERO!!
				queue.add(i+2);
			}
			//System.out.println(i + "result : " + hash(buffer));
		}
		
		//while (!queue.isEmpty()) {
        //    System.out.println(queue.remove());
        //}
		
		fis.close();
		
		return queue;
	}
    
    public String createMD(byte[] data) throws NoSuchAlgorithmException {

    		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    		messageDigest.update(data);
    		
    		byte[] messageDigestMD5 = messageDigest.digest();
    		StringBuffer stringBuffer = new StringBuffer();

    		for (byte bytes : messageDigestMD5) {
    			stringBuffer.append(String.format("%02x", bytes & 0xff));
    		}

    		//System.out.println("data:" + data);
    		//System.out.println("digestedMD5(hex):" + stringBuffer.toString());
    		
    		return stringBuffer.toString();
    }
    
    public Queue<String> MDlist(String fileName) throws IOException, NoSuchAlgorithmException {
		RabinFunction rb = new RabinFunction();
		Queue<Integer> queue = new LinkedList<Integer>();
		Queue<String> result = new LinkedList<String>();
		queue = rb.valueBlocks(fileName);
		
		FileInputStream fis = new FileInputStream(fileName);
		File f = new File(fileName);
		
		int bufferSize = 0;
		int oldIndex = 1;
		int newIndex = 0;
		while(!queue.isEmpty()) {
			newIndex = queue.remove();
			bufferSize = newIndex - oldIndex + 1;
			//System.out.println(bufferSize);
			byte[] buffer = new byte[bufferSize];
			fis.read(buffer);
			
			//System.out.println(rb.createMD(buffer));
			result.add(rb.createMD(buffer));
			
			oldIndex = newIndex + 1;
		}
		// last block
		//bufferSize = newIndex - oldIndex + 1;
		bufferSize =  (int) (f.length() - oldIndex + 1);
		byte[] buffer = new byte[bufferSize];
		fis.read(buffer);
		result.add(rb.createMD(buffer));
		
		fis.close();
		
		return result;
    }
}
