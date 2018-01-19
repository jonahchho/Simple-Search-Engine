/*

uni-gram for English words & Number

tri-gram for Chinese words

tokens produced by segmenter

*/

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;

public class Processor {
	
	public static void process() {
		
		PreparedStatement pst = null;
		
		HashMap<String, Integer> en, num, zhTri, zh;
		
		Stemmer stemmer = new Stemmer();
		
		int total = 0, totalTerm = 0;
	
		String path = "./Seg", path2 = "./Data", token = "", text = "";  //String path = "./Seg/1.seg";
		
		StringBuilder temp;
			
		Scanner scanner, scanner2;
				
		try {
			
			
			DB wordSet = new DB();
	        Connection con = wordSet.getConnection();

	        wordSet.createTable(con, "EnUniGram");
	        wordSet.createTable(con, "NumUniGram");
	        wordSet.createTable(con, "ZhTriGram");
	        wordSet.createTable(con, "SegGram");
	        wordSet.createTable2(con, "TermNumber");
	        
	        System.out.println( "Start Process!!" );
			
			for (int i = 0; i < 31043; i++) {  // 31043
				
				total = 0;
				
				en = new HashMap<String, Integer>();
				
				num = new HashMap<String, Integer>();
				
				zhTri = new HashMap<String, Integer>();
				
				zh = new HashMap<String, Integer>();
			
				scanner = new Scanner(new InputStreamReader(new FileInputStream(path+"/"+i+".seg"), "UTF-8"));
				
				while ( scanner.hasNext() ) {
					token = scanner.next();
	
					if ( token.matches( "[\\p{Alpha}]+" ) ) {  // Handle English
						
						char[] charAry = token.toLowerCase().toCharArray();    // Stemming
						stemmer.add(charAry, charAry.length);
						stemmer.stem();
						token = stemmer.toString();
					
						if ( en.containsKey( token ) ) en.put( token, en.get(token)+1 );
						else en.put(token, 1);
					
						total++;
					}
					
					else if ( token.matches( "[\\p{Digit}]+" ) ) {  // Handle Number
						
						if ( num.containsKey( token ) ) num.put( token, num.get(token)+1 );
						else num.put(token, 1);
					
						total++;						
					}
					
					else if ( token.matches( "[^\\p{XDigit}\\p{Graph}\\p{P}\\p{Blank}\\p{Space}\\℃|°|�|-|□|■|∕|°。/+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]+" ) ) { // Handle Chinese
						
						if ( zh.containsKey( token ) ) zh.put( token, zh.get(token)+1 );
						else zh.put(token, 1);
				
						total++;
					}

				}
				
				//**************************************  Handle ZhTriGram  *************************************************
				
				scanner2 = new Scanner(new InputStreamReader(new FileInputStream(path2+"/"+i+".txt"), "UTF-8"));
				
				temp = new StringBuilder();
				
				while ( scanner2.hasNext() ) {
					
					token = scanner2.next();

					token = token.replaceAll( "[\\p{XDigit}\\p{Graph}\\p{P}\\p{Blank}\\p{Space}\\℃|°|�|-|□|■|∕|°。/+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]", "" );

					if ( !token.matches("") ) temp.append(token);
			
				}
				
				text = temp.toString();
			
				for ( int start = 0, end = start + 3; start < text.length() && end <= text.length(); start++, end++ ) {  
					token = text.substring( start, end );
					
					if ( zhTri.containsKey( token ) ) zhTri.put( token, zhTri.get(token)+1 );
					else zhTri.put(token, 1);
				
					total++;
				}
				
				//***********************************************************************************************************
				
				pst = con.prepareStatement("insert into EnUniGram (docID,word,TF) values(?,?,?) ;");
			
				con.setAutoCommit(false);
			
				for (Entry<String, Integer> entry : en.entrySet()) {
					String key = entry.getKey();
					int value = entry.getValue();
			    
					pst.setInt(1, i);
					pst.setString(2, key);
					pst.setFloat(3, (float)value/total);
					pst.addBatch();

				}
				
				pst.executeBatch();

				pst = con.prepareStatement("insert into NumUniGram (docID,word,TF) values(?,?,?) ;");

				for (Entry<String, Integer> entry : num.entrySet()) {
					String key = entry.getKey();
					int value = entry.getValue();
			    
					pst.setInt(1, i);
					pst.setString(2, key);
					pst.setFloat(3, (float)value/total);
					pst.addBatch();

				}
				
				pst.executeBatch();

				pst = con.prepareStatement("insert into ZhTriGram (docID,word,TF) values(?,?,?) ;");
			
				for (Entry<String, Integer> entry : zhTri.entrySet()) {
					String key = entry.getKey();
					int value = entry.getValue();
			    
					pst.setInt(1, i);
					pst.setString(2, key);
					pst.setFloat(3, (float)value/total);
					pst.addBatch();
				
				}
				
				pst.executeBatch();
				
				pst = con.prepareStatement("insert into SegGram (docID,word,TF) values(?,?,?) ;");
			
				for (Entry<String, Integer> entry : zh.entrySet()) {
					String key = entry.getKey();
					int value = entry.getValue();
			    
					pst.setInt(1, i);
					pst.setString(2, key);
					pst.setFloat(3, (float)value/total);
					pst.addBatch();

				}
				
				pst.executeBatch();
				
				pst = con.prepareStatement("insert into TermNumber (docID,termNum) values(?,?) ;");
				pst.setInt(1, i);
				pst.setInt(2, total);
				pst.executeUpdate();
				
				con.commit();
				pst.close();
				
				totalTerm = totalTerm + total;
				
				System.out.println( "doc:" + i + "		total: " + total );
			}
			
			con.close();
			
			System.out.println( "Process complete!!     total: " + totalTerm );

		} 
			
		catch (UnsupportedEncodingException | FileNotFoundException | SQLException  e) {
			e.printStackTrace();
		}	
		
	}

}
