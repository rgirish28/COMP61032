package lab1;

import java.io.*;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.regex.*;


public class Knapsack {
	
	private static int n;
	private static int[] values,weights,indices;
	private static int c;
	
	static final Comparator<KnapsackElement> 
	Queue_ORDER =
	    new Comparator<KnapsackElement>() {
	public int compare(KnapsackElement e1, KnapsackElement e2) {
		
	    if ( e1.bound > e2.bound)
	    	return -1;
	    else if ( e1.bound == e2.bound)
	    	  return 0;
	    	else
	    		return 1;
	}
	};
	
	
	public static void main(String[] args) {
		BufferedReader fin = null;
		String s;
		int ctr;
		values = null;
		weights = null;
		
		try {
			fin = new BufferedReader(new FileReader(new File("hard.200.txt")));
		} catch (FileNotFoundException e) {
			System.out.println("Invalid File name");
		}
		
		try {
			s = fin.readLine();
			//System.out.println(s);
			n = Integer.parseInt(s);
			
			values = new int[n];
			weights = new int[n];
			indices = new int[n];
			int i=0;
			
			while((s= fin.readLine() )!=null){
				ctr = 1;
				Pattern p = Pattern.compile("\\d+");//to choose the digit using regular expressions
				Matcher m = p.matcher(s);

				while (m.find()) {
					
					if (ctr==1)
						c=Integer.parseInt(m.group());
					else if(ctr==2){
						values[i]=Integer.parseInt(m.group());
						indices[i]=i+1;
					}
					else if (ctr==3)
						weights[i++] = Integer.parseInt(m.group());
						
					
					ctr++;
					
				}
				
				
				}
			
			
			
		} catch (IOException e) {
			System.out.println("IO Exception");
		}
		
		System.out.println("File contains the following data:");
		System.out.println(n);
		for(int i=0;i<n;i++){
			System.out.println(indices[i]+" "+values[i]+" "+weights[i]);
																																								System.out.print("");
		}
		
		System.out.println(c);
		//solveByBnB();
		solveByGreedy();
		//solveByEnum();
}
 
 public static void solveByEnum(){
	 
	 String outString = "";
	 String binary,combination="";
	 int smallestIndex=0,currentWeight,highestValue,currentValue,reqdWeight;
	 highestValue = 0;
	 reqdWeight =0;
	 
	 for(int i=0;i<n;i++)
		 outString+="0";
	 
	 System.out.println(outString);
	 
	 for(int i=0;i<(int)(Math.pow(2, n));i++)
	 {	 
		 currentWeight = 0;
	 	 currentValue = 0;
		 binary = Integer.toBinaryString(i);
		 combination = outString.substring(0,n-binary.length())+binary;
		 for(int j=0;j<binary.length();j++)
		 {
			 if (binary.charAt(j)=='1'){
				 currentValue += values[n-binary.length()+j];
			 	 currentWeight += weights[n-binary.length()+j];
			 }
		 }
		 
		 System.out.println(combination+" Value = "+currentValue+" Weight = "+currentWeight);
		 
		 if (currentValue > highestValue&&currentWeight<=c){
			 smallestIndex = i;
			 highestValue = currentValue;
			 reqdWeight = currentWeight;
		 }
	 }
	 if(smallestIndex==0)
		 System.out.println("No possible combination within given weight value");
	 else
	 {
		 binary = Integer.toBinaryString(smallestIndex);
		 combination = outString.substring(0,n-binary.length())+ binary;
		 System.out.println("Best feasible solution found: Value = "+highestValue+" Weight = "+reqdWeight);
		 for(int i=0;i<combination.length();i++){
			 if(combination.charAt(i)=='1')
				 System.out.print("<"+(i+1)+"> ");
		 }
		 System.out.println();
	 }
 }
 
public static void solveByGreedy(){
	 
	 int currentWeight=0,currentValue=0;
	 String outString = "";
	 
	 sortArray();
	 
	 for(int i=0;i<n;i++){
		
		if((currentWeight+weights[i])<=c)
		{
			 currentWeight+=weights[i];
			 currentValue+=values[i];
			 outString +="<"+indices[i]+"> ";
		}

	}
	
	 System.out.println("Greedy solution (not necessarily optimal): "+currentValue+" "+currentWeight);
	 System.out.println(outString);
	 
		 
 }

	public static void solveByBnB(){
	
	double bestSolutionValue;
	KnapsackElement right,left;
	String bestSolution = "",temp = "";
	long time;
	bestSolutionValue =0.0;
	sortArray();
	
	
	PriorityQueue<KnapsackElement> queue = new PriorityQueue<KnapsackElement>(100, Queue_ORDER);
	
	KnapsackElement current = new KnapsackElement();
	
	current.solution = "";
	for(int i=0;i<n;i++)
		current.solution += "X";
	
	current.bound = fractionalUpperBound(current.solution,1);
	
	queue.add(current);
	
	time = System.currentTimeMillis();
	
	while (!queue.isEmpty() && bestSolutionValue<current.bound){
		current = queue.poll();
		temp = current.solution;
		if	(current.solution.contains("X")){
		
			right = new KnapsackElement();
			left = new KnapsackElement();
			left.solution = current.solution.replaceFirst("X", "0");
			left.bound = fractionalUpperBound(left.solution,1);
			right.solution = temp.replaceFirst("X", "1");
			right.bound = fractionalUpperBound(right.solution,1);
			queue.add(left);
			
			
			if(isFeasible(right)){
				queue.add(right);
				temp = right.solution;
				temp = temp.replaceAll("X", "0");
				
				if(fractionalUpperBound(temp,1)>bestSolutionValue){
					
					bestSolutionValue = fractionalUpperBound(temp,1);
					bestSolution = temp;
				}
		
			}
			
			
		}
		if(System.currentTimeMillis()-time>50000)
			break;
	}
	System.out.println("Value = "+(int)bestSolutionValue+" Weight = "+(int)fractionalUpperBound(bestSolution, 2));
	
	for(int i=0;i<bestSolution.length();i++){
		if(bestSolution.charAt(i)=='1'){
			System.out.print("<"+indices[i]+"> ");
			

		}
	}
	
	}
		 
	


private static boolean isFeasible(KnapsackElement right) {
		int currentWeight = 0;	
	
	 	for(int i=0;i<right.solution.length();i++){
		 
		 if(right.solution.charAt(i)!='X'){
			 currentWeight+=weights[i]*Integer.parseInt(right.solution.charAt(i)+""); 
		 }
	 	}
	 	if(currentWeight<=c)
	 		return true;
	 	else
	 		return false;
	 	
	}

private static double fractionalUpperBound(String partialSolution,int choice){
	
	int currentWeight=0;
	double currentValue=0.0;
	
	
	 for(int i=0;i<partialSolution.length();i++){
		 
		 if(partialSolution.charAt(i)!='X'){
			 currentValue+=values[i]*Integer.parseInt(partialSolution.charAt(i)+"");
			 currentWeight+=weights[i]*Integer.parseInt(partialSolution.charAt(i)+"");
		 }
		 
		 else
		 {	 
			 if((currentWeight+weights[i])<=c)
			 {
			 currentWeight+=weights[i];
			 currentValue+=(double)(values[i]);
			// System.out.println(currentValue);
			 }
			 else
			 {
			 currentValue += (((double)c-(double)currentWeight)/((double)weights[i])*(double)values[i]);
			 currentWeight += c-currentWeight;
			// System.out.println(currentValue);
			 break;
			 }
		
		 }
		 
	
	 } 
		 if(choice ==1)
			 return currentValue;
		 else
			 return currentWeight;
	 	 
	}

	private static void sortArray() {

		int temp=0,index =0;
		double highestValue;
	 
		for(int i=0;i<n;i++){
			highestValue =((double)values[i]/(double)weights[i]);
			index=i;
			for(int j=i+1;j<n;j++){
				if (highestValue<  ((double)values[j]/(double)weights[j])){
					highestValue =  ((double)values[j]/(double)weights[j]);
					index = j;
				}
				else if(highestValue ==((double)values[j]/(double)weights[j])&&weights[j]<weights[index]){
				
					highestValue =  ((double)values[j]/(double)weights[j]);
					index = j;
				}
			}
			temp =values[i];
			values[i] =values[index];
			values[index] = temp;
			temp =weights[i];
			weights[i] =weights[index];
			weights[index] = temp;	
			temp =indices[i];
			indices[i] =indices[index];
			indices[index] = temp;
		}
	
	
	}
	
		
}




