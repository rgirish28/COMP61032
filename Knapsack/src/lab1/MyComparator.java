package lab1;

import java.util.Comparator;

public class MyComparator implements Comparator<KnapsackElement>{

	public int compare(KnapsackElement arg0, KnapsackElement arg1) {
		// TODO Auto-generated method stub
		if(arg0.bound>arg1.bound)
			return -1;
		if(arg0.bound==arg1.bound)
			return 0;
		
			return 1;
		
	}
}