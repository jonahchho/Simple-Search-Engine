import java.io.*;

public class Segmenter {

	public static void main(String[] args) {
		String path = "./Data";
	     
		File folder = new File( path );
			
		String[] list = folder.list();
		
		for (int i = 0; i < list.length; i++) {
	    
			Runtime run = Runtime.getRuntime();   
	     
	     
			try {   

				Process process = run.exec("cmd /c" + " java -jar ./jar/segmenter.jar -8" + " ./Data/" + i + ".txt" );
				process.waitFor();
	        
			} 
	     
			catch (Exception e) {            
				e.printStackTrace();   
			} 
	     
		}
			
			folder = new File( path );
			
			list = folder.list();	
			
			for (int i = 0; i < list.length; i++) {
				
				if ( list[i].indexOf( ".seg" ) != -1 ) {
				
					File file = new File("./Data/"+ list[i] );
				
					file.renameTo( new File( "./seg/" + list[i].substring(0, list[i].indexOf(".") ) + ".seg" ) );
				}
			}
	}

}
