import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

public class Search {

	public static void main(String[] args) {

		List<Tuple> container, answer;		
		
		int total = 0;
		
		String line = "", input = "";
		
		Stemmer stemmer = new Stemmer();
		
		Scanner scanner = new Scanner(System.in), scanner2;
		
		HashMap<Integer, Tuple> handler;
		
		PreparedStatement pst = null;
		
		ResultSet rs = null;
		
		DB wordSet = new DB();
		
		System.out.print( "Please enter a query: " );

        try {
        	
			Connection con = wordSet.getConnection();
			
			while( scanner.hasNextLine() ) {
				
				long StartTime = System.currentTimeMillis();
				
				line = scanner.nextLine();
				
				line = line.replaceAll("[\\p{P}\\℃|°|�|-|□|■|∕|°。/+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", " ");
				
				line = splitAlphaNumeric( line );
				
				//System.out.println( line );
				
				scanner2 = new Scanner(line);
				
				handler = new HashMap<Integer, Tuple>();
				
				while( scanner2.hasNext() ) {
				
					input = scanner2.next();
					
					container = new ArrayList<>();
					
					total = 0;
					
					//System.out.println( input );

					if ( input.matches( "[^\\p{XDigit}\\p{Graph}\\p{P}\\p{Blank}\\p{Space}\\℃|°|�|-|□|■|∕|°。/+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]+" ) ) {
						
						
						if ( input.length() == 3 ) {
							pst = con.prepareStatement("select docID, word, TF from ZhTriGram where word = ? ;");
							pst.setString(1, input );
  
							rs = pst.executeQuery();
    				
							while (rs.next()) {
							
								Tuple tuple = new Tuple();
								tuple.docID = rs.getInt("docID");
								tuple.word = rs.getString("word");
								tuple.TF = rs.getFloat("TF");
								
								container.add(tuple);
							
								total++;
							
							}
						}
						
						
						if ( total == 0 ) {
						
							pst = con.prepareStatement("select docID, AVG(TF) from SegGram where word like ? group by docID ;");
							pst.setString(1, "%" + input + "%" );

							rs = pst.executeQuery();
							
							while ( rs.next() ) {
								
								Tuple tuple = new Tuple();
								tuple.docID = rs.getInt("docID");
								tuple.word = input;
								tuple.TF = rs.getFloat("AVG(TF)");

								container.add(tuple);
	
								total++;
			
							}
						}
		
						for ( int i = 0; i < container.size(); i++ ) container.get(i).TFIDF = (float)(container.get(i).TF * Math.log10(31043/total)); 
						
						Collections.sort( container, new TFIDFComparator() );
						
						// Collections.sort( container, new TFComparator() );
						
						for  ( int i = 0; i < container.size(); i++ ) {
							
							if ( handler.containsKey( container.get(i).docID ) ) { 
								container.get(i).TF = container.get(i).TF + handler.get(container.get(i).docID).TF;
								container.get(i).word = input;
								container.get(i).TFIDF = container.get(i).TFIDF + handler.get(container.get(i).docID).TFIDF;
							}
							
							handler.put(container.get(i).docID, container.get(i) );
							
						}

						//for ( int i = 0; i < 20 && i < container.size(); i++ ) System.out.println( container.get(i).docID + ".txt  "  );

					}
				
					else if ( input.matches("[\\p{Alpha}]+") ) {
					
						char[] charAry = input.toLowerCase().toCharArray();    // Stemming
						stemmer.add(charAry, charAry.length);
						stemmer.stem();
						input = stemmer.toString();
					
					
						pst = con.prepareStatement("select * from EnUniGram where word = ? ;");
						pst.setString(1, input );
  
						rs = pst.executeQuery();
    				
						while (rs.next()) {
							
							Tuple tuple = new Tuple();
							tuple.docID = rs.getInt("docID");
							tuple.word = rs.getString("word");
							tuple.TF = rs.getFloat("TF");
							
							container.add(tuple);
						
							total++;
							
						}	
						
						for ( int i = 0; i < container.size(); i++ ) container.get(i).TFIDF = (float)(container.get(i).TF * Math.log10(31043/total)); 
						
						Collections.sort( container, new TFIDFComparator() );
						
						//Collections.sort( container, new TFComparator() );
						
						for  ( int i = 0; i < container.size(); i++ ) {
							
							if ( handler.containsKey( container.get(i).docID ) ) { 
								container.get(i).TF = container.get(i).TF + handler.get(container.get(i).docID).TF;
								container.get(i).word = input;
								container.get(i).TFIDF = container.get(i).TFIDF + handler.get(container.get(i).docID).TFIDF;
							}
							
							handler.put(container.get(i).docID, container.get(i) );
							
						}
						
						
						//for ( int i = 0; i < 20; i++ ) System.out.println( container.get(i).docID + ".txt  "  );
					
					}
					
					else if ( input.matches( "[\\p{Digit}]+" ) ) {
						
						pst = con.prepareStatement("select * from NumUniGram where word = ? ;");
						pst.setString(1, input );
  
						rs = pst.executeQuery();
    				
						while (rs.next()) {
							
							Tuple tuple = new Tuple();
							tuple.docID = rs.getInt("docID");
							tuple.word = rs.getString("word");
							tuple.TF = rs.getFloat("TF");
							
							container.add(tuple);
						
							total++;
						}		
						
						for ( int i = 0; i < container.size(); i++ ) container.get(i).TFIDF = (float)(container.get(i).TF * Math.log10(31043/total)); 
						
						Collections.sort( container, new TFIDFComparator() );
						
						//Collections.sort( container, new TFComparator() );
						
						for  ( int i = 0; i < container.size(); i++ ) {
							
							if ( handler.containsKey( container.get(i).docID ) ) { 
								container.get(i).TF = container.get(i).TF + handler.get(container.get(i).docID).TF;
								container.get(i).word = input;
								container.get(i).TFIDF = container.get(i).TFIDF + handler.get(container.get(i).docID).TFIDF;
							}
							
							handler.put(container.get(i).docID, container.get(i) );
							
						}
						
						//for ( int i = 0; i < 20 && i < container.size(); i++ ) System.out.println( container.get(i).docID + ".txt  " );
						
					}
					
					System.out.println();
    		
				}
				
				System.out.println( "Answer" );
				
				if ( !handler.isEmpty() ) {
				
					answer = new ArrayList<>();
				
					for (Entry<Integer, Tuple> entry : handler.entrySet()) {
						Tuple value = entry.getValue();
					
						answer.add(value);

					}
				
					Collections.sort( answer, new TFIDFComparator() );
					
					//Collections.sort( answer, new TFComparator() );
				
					for ( int i = 0; i < 20 && i < answer.size(); i++ ) System.out.println( answer.get(i).docID + ".txt\t\t" + "TFIDF:\t"  + String.format( "%.10f", answer.get(i).TFIDF ) );
				}
				
				else {
					
					System.out.println( "Not Found" );
					System.out.println( "Please insert some spaces and try again" );
				}
				
				long ProcessTime = System.currentTimeMillis() - StartTime;
				System.out.println("Process time：" + Double.parseDouble(String.valueOf(ProcessTime))/1000 + " sec\r\n");
				
				System.out.print( "Please enter a query: " );
			}
			
		} 
        
        catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}
	
	public static String splitAlphaNumeric(String str) {
		
		String[] temp;
		
		StringBuilder builder = new StringBuilder();
		
	    temp = str.split("(?i)((?<=[a-zA-Z])(?=\\d))|"
	    				+"((?<=\\d)(?=[a-zA-Z]))|"
	    		
	    				+"((?<=[^\\p{XDigit}\\p{Graph}])(?=\\d))|"
	    				+"((?<=\\d)(?=[^\\p{XDigit}\\p{Graph}]))|"
	    				
	    				+"((?<=[^\\p{XDigit}\\p{Graph}])(?=[a-zA-Z]))|"
	    				+"((?<=[a-zA-Z])(?=[^\\p{XDigit}\\p{Graph}]))"
	    				);
	    
	    for ( int i = 0; i < temp.length; i++ ) {
	    	builder.append(temp[i]);
	    	builder.append(" ");
	    }
	    
	    return builder.toString();

	    
	}

}
/*
class Tuple implements Comparable<Tuple> {
	
	public int docID;
	public String word;
    public float TF;
    public float TFIDF;
    
    public int compareTo(Tuple other) {
        return Float.compare( other.TFIDF, TFIDF );
   } 
    
	
}
*/

class Tuple {
	
	public int docID;
	public String word;
    public float TF;
    public float TFIDF;
	
}

class TFIDFComparator implements Comparator<Tuple> {

    @Override
    public int compare(Tuple o1, Tuple o2) {
        return Float.compare(o2.TFIDF, o1.TFIDF);
    }

}

class TFComparator implements Comparator<Tuple> {

    @Override
    public int compare(Tuple o1, Tuple o2) {
        return Float.compare(o2.TF, o1.TF);
    }

}
