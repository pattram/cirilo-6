package org.emile.client.ptif;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;

public class PTIFF {

	
	public static void main(String[] args) {
		try {
	
			 String fname = "2";
			 
			 System.out.println("PTIFF 1.0.0.2");
			 byte[] stream = FileUtils.readFileToByteArray(new File("/Users/yoda/tmp/etc/"+fname+".tif"));
			 PyramidalTiff pt = new PyramidalTiff();
			 stream = pt.build(stream);
			 File outputFile = new File("/Users/yoda/tmp/etc/"+fname+"-ppt.tif");
			 try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
				 outputStream.write(stream);
			 }
			 
			 		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			  System.out.println("PTIFF terminated normally");
		}
	}

}