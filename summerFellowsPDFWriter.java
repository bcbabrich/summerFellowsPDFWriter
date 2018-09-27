import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class Main {
	
private static ArrayList<String> remediationList = new ArrayList<String>();
private static int rushingErrors=0;
private static int accidents=0;
private static int studentID = 0;

//CSV file header
private static final String FILE_HEADER = "id,firstName,lastName,gender,age";
private static final String COMMA_DELIMITER = ",";
private static final String NEW_LINE_SEPARATOR = "\n";

	///WRITE CSV FILE FOR LISA METHOD
	///Takes in an array full of answers given by single student
	///writes this info to a set CSV file
	///By the program end, CSV file will contain info Lisa wanted
	public static void writeCSV(String[] questionKey)
	{
		System.out.println("writeCSV ran");
		//let's try just writing to the same file every time
		FileWriter fileWriter = null;
		String outputFile = "CSVFile.csv";
		
		// before we open the file check to see if it already exists
		boolean alreadyExists = new File(outputFile).exists();
			
		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');
			
			// if the file didn't already exist then we need to write out the header line
			if (!alreadyExists)
			{
				csvOutput.write("#id");
				csvOutput.write("MathSAT");
				csvOutput.write("CalcYN");
				csvOutput.write("Highest Grade");
				csvOutput.endRecord();
			}
			// else assume that the file already has the correct header line
			
			// write out a few records
			csvOutput.write("#"+Integer.toString(studentID));
			csvOutput.write(questionKey[4]);//math SAT score
			csvOutput.write(questionKey[6]);//took calc in high school
			csvOutput.write(questionKey[10]);//highest grade in high school math
			csvOutput.write(questionKey[39]);//algebra score
			csvOutput.write(questionKey[40]);//calculus score 
			csvOutput.endRecord();
			
			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		studentID++;
	}

	
	///WRITE PDF METHOD
	//this method will copy the needed resources from remediationGuide.txt to 
	//a student's custom PDF.
	//IN: takes in a remedText arrayList full of text (remediation resources) and 
	//the corresponding student's first and last name.
	//IMPORTANT: THIS PROGRAM WILL NOT CATCH DUPLICATE NAMES
	//OUT: will write a PDF file in the source folder with the given names and text in it.
	public static void writePDF(ArrayList<String> remedText, String name1, String name2)
	{
		int x = 0;//trailing int to follow i in the for loop down below
		String temp = "xxxxxxx";//trailing string as well for same loop
//      System.out.println("print of remedText: \n");
//      for(int i = 0; i < remedText.size(); i++){
//     	 System.out.println(remedText.get(i));
//      }
        
        String text = null; 
        String fileName1 = name1+name2+".pdf";// name of our file
        
        try{
             PDDocument doc = new PDDocument();
             
             //This loop writes the PDF for each student.
             //Theoretically, it works like this:
             //-- Each time a string of length 2 with ":" following it is met, a new page is added
             //-- Until a new string of the same as above is met, lines are written to this page
             //----to account for null lines, if a line is of length < 3, temp is set to "xxxx"
             //			so that we're never checking for a null string 
             for(int i = 0; i < remedText.size(); i++){
            	 //System.out.println("i: "+i+", remedText.size(): "+remedText.size());
            	 if(i >= remedText.size()){
            		 break;
            	 } else if( i > 0){
            		 i = i - 1;
            	 }
            	 
            	 //System.out.println("remedText.get(i): "+remedText.get(i));
            	 if(remedText.get(i).substring(2,3).equals(":")){
            		 PDPage page = new PDPage();
	            	 PDPageContentStream content = new PDPageContentStream(doc, page);

	            	 content.beginText();
	            	 content.moveTextPositionByAmount(35, 750);
	            	 
	            	 x=i;
	            	 //loop
	            	 //System.out.println("remedText size: "+remedText.size());
	            	 do{
	            		 x++;
	            		 
	            		 if(x >= remedText.size()){
	            			 break;
	            		 }
	            		 
			             content.setFont(PDType1Font.HELVETICA, 16);
		            	 
	            		 //System.out.println("x: "+x);
		            	 text = remedText.get(x);
		            	 //System.out.println("TEXT : "+text);
		            	 
		            	 content.drawString(text);//stuff is being written to PDF right here
		            	 content.moveTextPositionByAmount(0, -25);
		            	 
		            	 if(text.length() < 3){
		            		 temp = "xxxxx";
		            	 } else {
		            		 temp = text;
		            	 }
		            	 
	            	 }while(!temp.substring(2,3).equals(":"));
	            	 i=x;
	            	 content.endText();
		             content.close();
	            	 doc.addPage(page);
            	 }
             }
             doc.save(fileName1);
             doc.close();
             System.out.println("your file created in : "+ System.getProperty("user.dir"));
        
        }
        catch(IOException e){
        System.out.println(e.getMessage());
        }
	}

	//READ REMEDIATION FILE METHOD
	//this method does the job of creating a list of text representing a student's
	//"remediation" resources, which will be written to a PDF for them.
	//IN: takes in a questionKey array full of the student's ANSWERS (NOT questions)
	//OUT: based on if the student got a CMU grade for a given question (index) i,
	//the corresponding text will be grabbed from the remediationGuide.txt file at the ith spot
	public static void readRemediationFile(String[] questionKey){
		System.out.println("reading remediation file for "+questionKey[0]+", "+questionKey[1]);
		System.out.println("quick print of questionKey[]:");
		for(int i = 0; i < questionKey.length; i++){
			System.out.print(questionKey[i]+ ", ");
		}
		String fileName = "remediationGuide.txt";
		ArrayList<String> remedText = new ArrayList<String>();
		
		String line = null;
		String temp = null;//temp to convert int to string for fileReading below
		String temp2 = null;//second string to mark beginning/end of file scan
		boolean read = false;
        
		for(int i = 0; i < questionKey.length; i++){
         			if(questionKey[i].equals("CMU")){
						try {
							 // FileReader reads text files in the default encoding.
							 FileReader fileReader = 
								 new FileReader(fileName);

							 // Always wrap FileReader in BufferedReader.
							 BufferedReader bufferedReader = 
								 new BufferedReader(fileReader);
								
							 //System.out.println("GOT HERE");
							 temp = Integer.toString(i+2)+":";//string to terminate scan/add
							 temp2 = Integer.toString(i+1)+":";//string to begin scan/add
							//System.out.println("term: "+temp+", begin: "+temp2);
							
								do{
									line = bufferedReader.readLine();
									//System.out.println("line: "+line);
									if(line == null || line.equals(temp)){
										break;
									}
									if(line.equals(temp2)){
										//System.out.println("start scanning");
										read = true;
									}
									if(read){
										remedText.add(line);//text of remediation sources is added here
									}
								}while(line != null);
							

							 // Always close files.
							 bufferedReader.close();         
						 }
						 catch(FileNotFoundException ex) {
							 System.out.println(
								 "Unable to open file '" + 
								 fileName + "'");                
						 }
						 catch(IOException ex) {
							 System.out.println(
								 "Error reading file '" 
								 + fileName + "'");                  
							 // Or we could just do this: 
							 // ex.printStackTrace();
						 }
         			}
        read = false;
        }
        writePDF(remedText, questionKey[0], questionKey[1]);
	}
	
	//QUESTION DECISISON TREE	
	//a large case switch statement which maps answers in a student line to "grades"
	//these grades being CMU or A
	//IN: takes in an answer (part of a "student" line being processed in readIndividualStudent)
	//OUT: returns the student's "grade" based on their answer (using case switch statement)
	public static String questionDecisionTree(int c, String text){
		c = c + 1;//c is thrown off by +1 because of arrays
		
		switch (c) {
	        case 1:  System.out.println("first name: "+text);
	        		 remediationList.add(text);
	        		 //duplicate nameID catcher would go here
	                 return text;
	                 
	        case 2:  System.out.println("last name: "+text);
	        		 remediationList.add(text);
	                 return text;
	                 
	        case 3:  //System.out.println("is this your first name case");
	                 break;
	        case 4:  //System.out.println("what is your intended major");
	                 break;
	        case 5:  //System.out.println("best SAT score"+ c);
	        		 return text;
	        case 6:  //System.out.println("best ACT score"+ c);
	                 break;
	        case 7:  //System.out.println("did you take calculus? "+ c);
	        		 return text;
	        case 8:  //System.out.println("which of these topics did you study in highschool? "+ c);
	                 break;
	        case 9:  //System.out.println("did you take statistics?"+ c);
	                 break;
	        case 10: //System.out.println("which of these topis did you study in highschool?"+ c);
	                 break;
	        case 11: //System.out.println("what grade did you earn your highest level high school math  "+ c);
	                 return text;
	        case 12: //System.out.println("which of the following is equal to 8(n-5)^2 "+ c);
	        		if(text.equals("B.")||text.equals("C.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if (text.equals("E.")||text.equals("D.")||text.equals("A.")){
	        			System.out.println("A on #"+c + ", chose "+text);
	        			accidents++;
	        			return "A";
	        		} else if(text.equals("F.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		}
	                 break;
	        case 13: //If z is a nonzero, which of the following is equal to;
	        		 System.out.println("grading #"+c+" now");
	        		 if(text.equals("A.")||text.equals("B.")||text.equals("D.")){
	        			 System.out.println("CMU on #"+ c + ", chose "+text);
	        			 return "CMU";
	        		 } else if (text.equals("C.")){
	        			 System.out.println("RE on #"+ c + ", chose "+text);
	        			 rushingErrors++;
	        			 return "RE";
	        		 } else if (text.equals("E. none of these")){
	        			 System.out.println("C on #"+ c + ", chose "+text);
	        			 //return "C";
	        		 }
	        		 break;
	        case 14: //suppose x is a positive number. Choose all of the following which
	        	//gonna need a helpler method to pick out individual answers
	        	    text = text.replace("\"", "");//get rid of quotation marks
	        	    String temp;//following string
	        	    System.out.println("grading #"+c+" now");
	        	    for(int i = 0; i < text.length(); i++){
	        	    	temp = text.substring(i,i+1);
	        	    	//System.out.println("temperature: "+temp);
	        	    	if(temp.equals("A")||temp.equals("B")||temp.equals("C")
	        	    			||temp.equals("D")||temp.equals("E")||temp.equals("F")){
			        	    
				       		 if(temp.equals("D")||temp.equals("F")){
				       			 System.out.println("CMU on #"+ c + ", chose "+temp);
				       			 return "CMU";
				       		 } else if (temp.equals("A")||temp.equals("B")||temp.equals("C")){
				       			 System.out.println("A on #"+ c + ", chose "+temp);
				       			 accidents++;
				       		 } else if (temp.equals("E")){
				       			 System.out.println("C on #"+c + ", chose "+temp);
				       		 }
	        	    	}
	        	    }
	        		 break;
	        case 15: //if z is a nonzero, which of the following is equal to
			        System.out.println("grading #"+c+" now");
		       		 if(text.equals("A.")||text.equals("E. none of these")||text.equals("B.")){
		       			 System.out.println("CMU on #"+ c + ", chose "+text);
		       			 return "CMU";
		       		 } else if (text.equals("D.")){
		       			 System.out.println("A on #"+ c + ", chose "+text);
		       			 accidents++;
		       		 } else if (text.equals("C.")){
		       			 System.out.println("C on #"+ c + ", chose "+text);
		       		 }
			 		 break;
	        case 16: //simultaneously solve the equations
		        	System.out.println("grading #"+c+" now");
		       		 if(text.equals("E. no solutions")||text.equals("F. infinitely many solutions")||text.equals("G. none of these")){
		       			 System.out.println("CMU on #"+ c + ", chose "+text);
		       			 return "CMU";
		       		 } else if (text.equals("A.")||text.equals("C.")||text.equals("D.")){
		       			 System.out.println("RE on #"+ c + ", chose "+text);
		       			 accidents++;
		       		 } else if (text.equals("B.")){
		       			 System.out.println("C on #"+ c + ", chose "+text);
		       		 }
		       		 break;
	        case 17: //absolute value functions and inequalities
	        		System.out.println("grading #"+c+" now");
		        	if(text.equals("C.")||text.equals("F.")||text.equals("G. none of these")
		        			||text.equals("A.")||text.equals("B.")||text.equals("E.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("D.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		}
		        	break;
	        case 18: //squaring binomials with radicals
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("B.")||text.equals("E.")||text.equals("F. none of these")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if (text.equals("A.")||text.equals("D.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			accidents++;
	        			return "A";
	        		} else if(text.equals("C. 9")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		}
			 	break;
	        case 19: //Function notation
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("C. 60")||text.equals("F. none of these")||text.equals("D. -8")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if (text.equals("A. 0")||text.equals("B. 4")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			accidents++;
	        			return "A";
	        		} else if(text.equals("E. 14")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 20: //function notation with variables
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("B.")||text.equals("D.")||text.equals("E.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if (text.equals("A.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			accidents++;
	        			return "A";
	        		} else if(text.equals("C.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 21: //finding the roots of a quadratic
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("C.")||text.equals("A.")||text.equals("B.")||text.equals("D.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("E. none of these")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
			 
	        case 22: //Equation of a circle
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("B.")||text.equals("C.")||text.equals("E.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if (text.equals("D.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			accidents++;
	        			return "A";
	        		} else if(text.equals("A.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 23: //equation of a parabola
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A.")||text.equals("B.")||text.equals("D.")||text.equals("F. none of these")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if (text.equals("E.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			accidents++;
	        			return "A";
	        		} else if(text.equals("C.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 24: //equation of a trig function
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A.")||text.equals("C.")||text.equals("D.")
		        			||text.equals("F.")||text.equals("H. none of these")
		        			||text.equals("E.")||text.equals("G.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("B.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 25: //equation of a line given two points
		        	text = text.replace("\"", "");//get rid of quotation marks
	        	    String temp1;//following string
	        	    System.out.println("grading #"+c+" now");
	        	    for(int i = 0; i < text.length(); i++){
	        	    	temp1 = text.substring(i,i+1);
	        	    	//System.out.println("temperature: "+temp);
	        	    	if(temp1.equals("A")||temp1.equals("B")||temp1.equals("C")
	        	    			||temp1.equals("D")||temp1.equals("E")||temp1.equals("F")||temp1.equals("G")){
			        	    
				       		 if(temp1.equals("B")||temp1.equals("D")||temp1.equals("G")){
				       			 System.out.println("CMU on #"+ c + ", chose "+temp1);
				       			 return "CMU";
				       		 } else if (temp1.equals("E")){
				       			 System.out.println("A on #"+ c + ", chose "+temp1);
				       			 accidents++;
				       		 } else if (temp1.equals("A")||temp1.equals("C")||temp1.equals("F")){
				       			 System.out.println("C on #"+c + ", chose "+temp1);
				       		 }
	        	    	}
	        	    }
				 	break;
	        case 26: //Definition of a function
		        	text = text.replace("\"", "");//get rid of quotation marks
	        	    String temp11;//following string
	        	    System.out.println("grading #"+c+" now");
	        	    for(int i = 0; i < text.length(); i++){
	        	    	temp11 = text.substring(i,i+1);
	        	    	//System.out.println("temperature: "+temp);
	        	    	if(temp11.equals("A")||temp11.equals("B")||temp11.equals("C")
	        	    			||temp11.equals("D")||temp11.equals("E")||temp11.equals("F")||temp11.equals("G")){
			        	    
				       		 if(temp11.equals("B")||temp11.equals("C")||temp11.equals("D")||temp11.equals("G")){
				       			 System.out.println("CMU on #"+ c + ", chose "+temp11);
				       			 return "CMU";
				       		 } else if (temp11.equals("E")||temp11.equals("F")||temp11.equals("A")){
				       			 System.out.println("C on #"+c + ", chose "+temp11);
				       		 }
	        	    	}
	        	    }
				 	break;
	        case 27: //evaluating limits at infinity
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A.")||text.equals("B. 2")||text.equals("E. 0")
		        			||text.equals("G.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if (text.equals("A.")||text.equals("F.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			rushingErrors++;
	        			return "RE";
	        		} else if(text.equals("D.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 28: //Evaluating limits at zero
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A.")||text.equals("B. 216")||text.equals("C. 0")
		        			||text.equals("F. 6")||text.equals("H. none of these")||text.equals("E.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if (text.equals("D. does not exist")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			accidents++;
	        			return "A";
	        		} else if(text.equals("G. 108")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 29: //Interpreting properties of a function from a graph 1
		        	text = text.replace("\"", "");//get rid of quotation marks
	        	    String temp111;//following string
	        	    System.out.println("grading #"+c+" now");
	        	    for(int i = 0; i < text.length(); i++){
	        	    	temp111 = text.substring(i,i+1);
	        	    	//System.out.println("temperature: "+temp);
	        	    	if(temp111.equals("A")||temp111.equals("B")||temp111.equals("C")
	        	    			||temp111.equals("D")||temp111.equals("E")||temp111.equals("F")||temp111.equals("G")
	        	    			||temp111.equals("H")||temp111.equals("I")){
			        	    
				       		 if(temp111.equals("C")||temp111.equals("G")||temp111.equals("H")
					        			||temp111.equals("I")){
				       			 System.out.println("CMU on #"+ c + ", chose "+temp111);
				       			 return "CMU";
				       		 } else if (temp111.equals("B")){
				       			 System.out.println("A on #"+c + ", chose "+temp111);
				       		 } else if (temp111.equals("A")||temp111.equals("D")||temp111.equals("E")||temp111.equals("F")){
				       			 System.out.println("C on #"+c + ", chose "+temp111);
				       		 } else {
				       			 System.out.println("text!: "+temp111);
				       		 }
	        	    	}
	        	    }
				 	break;
	        case 30: //Interpreting Properties of a graph 2
		        	text = text.replace("\"", "");//get rid of quotation marks
	        	    String temp1111;//following string
	        	    System.out.println("grading #"+c+" now");
	        	    for(int i = 0; i < text.length(); i++){
	        	    	temp1111 = text.substring(i,i+1);
	        	    	//System.out.println("temperature: "+temp);
	        	    	if(temp1111.equals("A")||temp1111.equals("B")||temp1111.equals("C")
	        	    			||temp1111.equals("D")||temp1111.equals("E")||temp1111.equals("F")||temp1111.equals("G")
	        	    			||temp1111.equals("H")||temp1111.equals("I")){
			        	    
				       		 if(temp1111.equals("A")||temp1111.equals("B")||temp1111.equals("G")
					        			||temp1111.equals("I")){
				       			 System.out.println("CMU on #"+ c + ", chose "+temp1111);
				       			 return "CMU";
				       		 } else if (temp1111.equals("C")||temp1111.equals("D")
				       				 ||temp1111.equals("E")||temp1111.equals("F")||temp1111.equals("H")){
				       			 System.out.println("C on #"+c + ", chose "+temp1111);
				       		 } else {
				       			 System.out.println("text!: "+temp1111);
				       		 }
	        	    	}
	        	    }
				 	break;
	        case 31: //Chain Rule (Derivatives)
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A.")||text.equals("B.")||text.equals("D.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if (text.equals("C.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			accidents++;
	        			return "A";
	        		} else if(text.equals("E.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 32: //Derivatives of exponential functions
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A.")||text.equals("C.")||text.equals("D.")
		        			||text.equals("E.")||text.equals("F.")||text.equals("G.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("B.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 33: // Chain Rule Involving Trig Functions
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("C.")||text.equals("F.")||text.equals("G.")
		        			||text.equals("H.")||text.equals("I.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("D.")||text.equals("E.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			return "A";
	        		} else if(text.equals("B.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 34: //Question Twenty-Three - Relating a graph to its derivative
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A. Graph 1")||text.equals("B. Graph 2")||text.equals("D. Graph 4")
		        			||text.equals("H.")||text.equals("I.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("C. Graph 3")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 35: //Question Twenty-Four - Derivatives Involving the quotient rule
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A.")||text.equals("C.")||text.equals("E.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("B.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			return "A";
	        		} else if(text.equals("D.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 36: //Question Twenty-Five - Equation of a tangent line
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("C.")||text.equals("D.")||text.equals("F.")
		        			||text.equals("G. none of these")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("A.")||text.equals("B.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			return "A";
	        		} else if(text.equals("E.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 37: //Question Twenty-Six - Power Rule Integration
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("B.")||text.equals("C.")||text.equals("D.")
		        			||text.equals("E.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("A.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			return "A";
	        		} else if(text.equals("F.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 38: //Question Twenty-Seven - Acceleration/Velocity Derivative word problem
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A.")||text.equals("C.")||text.equals("D.")
		        			||text.equals("F.")||text.equals("G.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("E.")){
	        			System.out.println("A on #"+ c + ", chose "+text);
	        			return "A";
	        		} else if(text.equals("B.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 39: //Question Twenty-Eight - Acceleration/Velocity/Position 
		        	 //Word Problem Involving Integration
		        	System.out.println("grading #"+c+" now");
		        	if(text.equals("A.")||text.equals("B.")||text.equals("C.")
		        			||text.equals("D.")||text.equals("F.")){
	        			System.out.println("CMU on #"+ c + ", chose "+text);
	        			return "CMU";
	        		} else if(text.equals("E.")){
	        			System.out.println("C on #"+ c + ", chose "+text);
	        			return "C";
	        		} else {
	        			System.out.println("print text: "+text);
	        		}
				 	break;
	        case 40: 
	        	return text;
	        case 41: 
	        	return text;
	        default: System.out.println("error,"+c);
	                 break;
		}
		return "C";
	}
	
	//READ INDIVIDUAL STUDENT METHOD
	//IN: each line in the CSV file represents one student's test, and this is what's inputted.
	//OUT:none.
	//this method takes each student's line, and breaks it up into sections based on the commas in it.
	//each section is passes along to the decisionTree method to be analyzed.
	public static void readIndividualStudent(String line){
		System.out.println("");
		int x = 0;//following variable
		int y = 0;//comma(question) counter
		int z = 0;//keeps track of place in line
		String[] questionKey = new String[41];
		for(int i = 0; i < questionKey.length; i++){
			questionKey[i] = "C";
		}
		
		line = line.substring(21, line.length());//get rid of time code immediately
		
		if(!line.equals(",,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,0,0")){//takes care of "dud" tests
			//System.out.println(line);
			
			for(int i=0; i < line.length(); i++){
				if(line.substring(i,i+1).equals("\"")){
					x=i+1;
					while(!line.substring(x,x+1).equals("\"")){
						x++;
					}
					i=x+1;
				}//skip over any commas that are inside quotation marks (multiple answer questions)
				
				if(line.substring(i, i+1).equals(",")){//comma is found, comma # and corresponding substring are sent to the decisionTree
					questionKey[y] = questionDecisionTree(y,line.substring(z,i));
					y++;
					z=i+1;
				}
			}
			//takes care of filling final question (score on calc part)
			questionKey[y] = questionDecisionTree(y,line.substring(z,line.length()));
		}
		//print to check what the questionKey looks like
//		for(int i = 0; i < questionKey.length; i++){
//			System.out.print(questionKey[i]+", ");
//		}
		//System.out.println("y (number of commas): "+y);
		readRemediationFile(questionKey);
		writeCSV(questionKey);
		remediationList.clear();
	}
	
	//FILE READING METHOD
	//IN: fileName. Just click "rename" on the exported CSV file and copypaste that string 
	//into where this method is called from (i.e., main)
	//OUT: void (nothing). This method has a while loop which "reads" the answers
	//of each student, and each iteration calls another method to will start the process
	//of writing that student's PDF remediation packet.
	public static void readCSVFile(String fileName)
	{
		int x = 8;
		int y = 0;
		BufferedReader fileReader = null; 
		try {
			fileReader = new BufferedReader(new FileReader(fileName));
		    String line = "";
		    
		    //int x is here so that the first 8 lines aren't read (they're the questions)
		    //int y is here so I can control the output of the sysoutprintln. 
		    while ((line = fileReader.readLine()) != null /*&& y < 50*/) {
		    	if(x < 0){
		    		readIndividualStudent(line);
		    	}
		    	x--;
		    	y++;
		    }
		
		} catch (Exception e) {
			System.out.println("Error in CsvFileReader !!!");
			e.printStackTrace();
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				System.out.println("Error while closing fileReader !!!");
				e.printStackTrace();
		    }
		}
	}

	///MAIN
	//not much happens here besides the qualtrics CSV being inputted.
    public static void main(String[] args) {
        readCSVFile("Math Placement 2016_June 14, 2016_10.46.csv");
    }
    
}