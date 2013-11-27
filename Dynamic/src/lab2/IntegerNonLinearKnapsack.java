package lab2;

public class IntegerNonLinearKnapsack {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int[] weights = {100000,250000,150000,10000};
		int max = 2000000;	
		int c = 2500;
		String[] items = {"X-Ray CT Scanner","NMR Scanners","Radiation Therapy Machines","Beds"};
		int[][] valMatrix = new int[5][801];
		int[][] keep = new int[5][801];
		
		for(int i =0;i<801;i++)
			valMatrix[0][i] = 0;
		
		for(int i=1;i<=4;i++){
			for(int w=0;w<=max/c;w++){
				
				int limit = 0;
				
				if(i<=3)
					limit =3;
				else
					limit = 267;
				
				for(int k=1;k<=limit;k++){
					
					if ((weights[i-1]*k<=w*c)&&((val(i,k)+valMatrix[i-1][w-(weights[i-1]*k/c)])>valMatrix[i-1][w])){
						
						if(((val(i,k)+valMatrix[i-1][w-(weights[i-1]*k/c)])>valMatrix[i][w])){
					
							valMatrix[i][w] = val(i,k)+ valMatrix[i-1][w-(weights[i-1]*k/c)];
							keep[i][w] = k;
						
						}
											
					}
					
						
				}
				
				if(valMatrix[i][w]<valMatrix[i-1][w]){
					
					valMatrix[i][w] = valMatrix[i-1][w];
					keep[i][w]=0;
				
				}
			}
		}
		
			

		int tempMaxWt = max;
		int totalweight = 0;
		int totalvalue = 0;
		for (int i = 4 ; i >=1 ; i--) {
			
			if(keep[i][tempMaxWt/c] != 0){

				totalweight += weights[i-1]* keep[i][tempMaxWt/c];
				totalvalue += val(i,keep[i][tempMaxWt/c]);
				System.out.print(keep[i][tempMaxWt/c]+" ");
				System.out.print(items[i-1]);
				System.out.println("");
				tempMaxWt = tempMaxWt - (weights[i-1]* keep[i][tempMaxWt/c]);
				
			}
		}
		
		System.out.println("Value = " + totalvalue );
		System.out.println("Weight = " +totalweight );

	
		
	}

	private static int val(int index, int amount) {
		
		if(index==1){
			
			if(amount==1)
				return 1000;
			else if(amount==2)
				return 1850;
			else if(amount==3)
				return 2600;
		}
		else if(index==2){
			if(amount==1)
				return 5000;
			else if(amount==2)
				return 8500;
			else if(amount==3)
				return 11000;
		
		}
		else if(index==3){
			if(amount==1)
				return 7500;
			else if(amount==2)
				return 11500;
			else if(amount==3)
				return 14200;
		
		}
		else if(index==4){
			if(amount<=100)
				return 75*amount;
			else if(amount>100)
				return 7500+36*(amount-100);
	
		}
			return 0;
	}
			
		
	

					
		
	

}
