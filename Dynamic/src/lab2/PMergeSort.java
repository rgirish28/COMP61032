package lab2;
import java.util.Random;

public class PMergeSort implements Runnable
{
    /**
     * Global Shared Variables
     */
    public static int randArray[]; 
    public static int resultArray[];
    public static int flagArray[];
    public static int numThreads;
    public static double timeout,time;
    public static int itemsPerThread, arraySize;
    public static int[]  endIndexes;
    
    /**
     * Instance Variables
     */
    private int start, end, threadId;
    
    public PMergeSort(){}
    
    /**
     * 
     * @param start
     * @param end
     * @param threadId
     */
    public PMergeSort (int start, int end, int threadId)
    {
        this.start=start;
        this.end=end;
        this.threadId=threadId;
    }
   
    public static void main(String[] args) throws InterruptedException
    {
        int seed = 0;
        if (args.length < 4)
            System.out.println("Input should be integers in form: [number of threads][seed][arraysize][timeout (seconds)]");
        else if (args.length >= 1)
        try
        {
            numThreads = Integer.parseInt(args[0]);
            seed = Integer.parseInt(args[1]);
            arraySize = Integer.parseInt(args[2]);
            timeout = Double.parseDouble(args[3]);
            if (args.length > 4)
            {
                System.out.println("Input should be integers in form: [number of threads][seed][arraysize][timeout (seconds)]");
                System.out.println("Ignoring Extra Inputs.");
            }
        }
        catch (Exception e)
        {
            System.out.println("Input should be integers in form: [number of threads][seed][arraysize][timeout (seconds)]");
            System.out.println(e);
            System.out.println("Using defaults: " + numThreads + " threads.");
            System.out.println("Using defaults: " + seed + " seed.");
            System.out.println("Using defaults: " + arraySize + " arraySize.");
            System.out.println("Using defaults: " + timeout + " timeout.");
        }
        
        time=(double) (System.nanoTime()/1000000000.0);
                
        Random rg = new Random(seed);
        randArray = new int[arraySize];
        resultArray = new int [arraySize];
        
        for (int i=0;i<arraySize;i++)
            randArray[i]=rg.nextInt();
        
        if(numThreads>arraySize)
        {
            System.out.println("threads changed to "+arraySize);
            numThreads=arraySize;
        }
        
        itemsPerThread=arraySize/numThreads;
        Thread aThread;
        flagArray= new int[numThreads];
        endIndexes= new int[numThreads];
        
        for (int i = 0; i < numThreads-1; i ++)
        {
            aThread = new Thread(new PMergeSort(i*itemsPerThread,i*itemsPerThread+itemsPerThread-1,i));
            aThread.start();
        }
        double runningTime = 0.0;
        if(numThreads==1)
        {
            mergeSort(0,arraySize-1);
            flagArray[0]=1;
            runningTime = (double)(System.nanoTime()/1000000000.0)-time;
        }
        else
        {
            aThread = new Thread(new PMergeSort((numThreads-1)*itemsPerThread,arraySize-1,numThreads-1));
            aThread.start();
        }
            
        while(flagArray[0]==0)
        {
            Thread.sleep(1);
            runningTime=(double)(System.nanoTime()/1000000000.0)-time;
            if(runningTime > timeout)
            {
                System.out.print("Timeout exceeded");
                System.exit(-1);
            }
        }
        System.out.println("======================================");
        for (int i=0; i<arraySize-1;i++)
            if(randArray[i+1]<randArray[i])
            {
                System.out.print("Not ");
                break;
            }
        
        System.out.println("Ordered Array");
        for (int i=0; i<arraySize;i++)
            System.out.print(randArray[i]+" ");
        
        System.out.println("Number of Threads: " + numThreads);
        System.out.println("Array Size: " + arraySize);
        System.out.println("Random Seed: " + seed);
        System.out.println("Timeout: " + timeout);
        
        System.out.println("Execution Time: " + runningTime);
        System.out.println("======================================");
    }

    @Override
    public void run()
    {
        endIndexes[this.threadId]=end;
        mergeSort(start, end);
        
        int i=0;
        while(this.threadId+(Math.pow(2,i))<numThreads)
        {
            if(this.threadId % Math.pow(2, i+1) == 0)
            {
                while (flagArray[(int) (this.threadId+(Math.pow(2,i)))]==0) { }
                merge(start, end+1, endIndexes[(int) (this.threadId+(Math.pow(2,i)))]);
                end=endIndexes[(int) (this.threadId+(Math.pow(2,i)))];
                endIndexes[this.threadId]=end;    
            }
            i++;
        }
        flagArray[this.threadId] = 1;
    }
    
    private static void mergeSort(int start, int end)
    {
        int mid;
        if((end-start) == 1)
        {
            if(randArray[start]>randArray[end]){
                int temp=randArray[end];
                randArray[end]=randArray[start];
                randArray[start]=temp;
            }
        }
        else if((end-start)>1)
        {
            mid=start+(end-start)/2 ;
            mergeSort(start,mid);
            mergeSort(mid+1,end);
            merge(start,mid+1,end);
        } 
    }

    private static void merge(int s, int m, int e)
    {
        int i=s, j=m;
        for (int resultIndex = s; resultIndex <= e; resultIndex ++)
        {
            if(i < m)
            {
                if (j <= e)
                {
                    if (randArray[i] < randArray[j])
                    {
                        resultArray[resultIndex] = randArray[i];
                        i ++;
                    }
                    else
                    {
                        resultArray[resultIndex] = randArray[j];
                        j ++;
                    }
                }
                else
                {
                    resultArray[resultIndex] = randArray[i];
                    i ++;
                }
            }
            else
            {
                if (j <= e)
                {
                    resultArray[resultIndex] = randArray[j];
                    j ++;
                }
            }
        }
        // Write updated section of result array back to rand array
        for (int resultIndex = s; resultIndex <= e; resultIndex ++)
            randArray[resultIndex] = resultArray[resultIndex];
    }
}