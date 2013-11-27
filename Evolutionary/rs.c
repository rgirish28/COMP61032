
/* Random Search for the Assignment Problem 

DATA:
Staff offered projects, and each project has
a maximum number of participants

Students made 5 project choices in general
but some students made fewer. The choices 
were ranked. So choice 1 was their favourite
project, 2 their second favourite, etc.

These data are read in.

GOAL. Find a feasible assignment that 
minimizes the ranks of the choices

OBJECTIVE FUNCTION. We will penalize any 
infeasible assignment (one where a project
is allocated to more students than it is
allowed to be allocated to) with a penalty
of 1000 for each over-allocation of each project. 
We will additionally penalize giving a student
their second choice with a penalty of 10,
a third choice with a penalty of 20, etc.,
for every student.

REPRESENTATION
A solution vector (assignment) is an array
of integers
x[158]
where x[i] represents the choice of 
project allocated to 
student i (for i=1 to 157). x[i] is a number from {1,2,3,4,5}
and from this, and the information about
what choices students made, the actual project
ID this refers to can be looked up.

RANDOM SEARCH METHOD
Do MAXITERS times:
  -Set the assignments to each student to be
    a random number in the range 1-5.
  -Evaluate the solution according to the penalties defined above
  -Output the best value found
Return the last solution and its value


*/

#include<stdio.h>
#include<stdlib.h>
#include<math.h>
#include<string.h>

#define RN (rand()/(RAND_MAX+1.0))
#define M 801

FILE *fp;

long seed;
int MAXITERS;

typedef struct solution
{
  int x[158];  // the assignment  (also known as the solution string)
  int f;       // the overall fitness value
  int v;       // the number of hard constraint violations (if this is nonzero then the assignment is infeasible)
  int dist[6]; // the distribution: dist[1] is number of people who got 1st choice, dist[2] 2nd choice, etc
}sol;

sol *s;  // an instance of solution

struct project
{
  char ID[30];  // the ID number of the project
  int max_p;      // the maximum number of students that can be allocated this project
  int alloc;     // the number actually allocated
};

struct student
{
  int num;               // student ID: just a number in the range 1 to 157
  char choices[6][30];   // the (up to) five choices of project ID the student made. choices[1] holds the first
  int nc;                // the total number of choices the student made
};

struct project a[M+1];
struct student st[200];


// A hash table is used to store the projects so we can access them by their ID number
unsigned hash(char *v)
{
  int h;
  for(h=0; *v !='\0'; v++)
    h=(64*h+ *v)%M;
  return h;
}

void hashinitialize()
{
  int i;
  for(i=0;i<=M;i++)
    {
      //      a[i].ID = " "; a[i].max_p=-1;
      sprintf(a[i].ID," ") ; a[i].max_p=-1;

    }
}

void hashinsert(char *v, int max_p)
{
  int x = hash(v);
  while (strcmp(" ", a[x].ID))
    x=(x+1)%M;
  // printf("%d\n",x);
  sprintf(a[x].ID,"%s",v);
  a[x].max_p=max_p;
}

int hashsearch(char *v)
{
  int x = hash(v);
  int tries=0;
  while((strcmp(v, a[x].ID))&&(tries<2*M))
    {
      tries++;
      x=(x+1)%M;
    }
  if(tries==2*M)
    {
      printf("search for %s failed\n", v);
      return -1;
      // exit(1);
    }

  //  printf("found %d\n",x);
  //  return a[x].max_p;
  return x;
}




int evaluate_sol(sol *s)
{
  int i, ch, fitness;

  for(i=1;i<=M;i++)
    a[i].alloc=0;  // reset all the allocations to zero

  int proj;
  int pref_score=0;

  for(i=1;i<6;i++)
    s->dist[i]=0;

  
  for(i=1;i<=157;i++)
    {
      ch = s->x[i];  // ch is the choice (rank), s->x[i] this choice (rank) on the chromosome
      if (ch>st[i].nc)  // st[i].nc is the maximum choice number that student actually made (some did not make 5 choices)
	{
	  ch = st[i].nc; // repair it
	  s->x[i]=ch;  // repair it back on the chromosome (Lamarckian repair)
	}

      s->dist[ch]++;

      pref_score+=(ch-1)*10; // a linear penalty: 10 for second choice, 20 for third etc.
      
      proj=hashsearch(st[i].choices[ch]);
      if(proj==-1)
	{
	  printf("St=%d ch=%d max=%d\n", i, ch, st[i].nc);
	}
      (a[proj].alloc)++;
    }
  
  // next count the number of violations (projects over-allocated)
  int v=0;
  for(i=1;i<=M;i++)
    {
      if(a[i].max_p>0)  // if the project exists in the hash table
	{
	  if(a[i].alloc>a[i].max_p)  // if it is over-allocated
	    {
	      //	      printf("%s %d %d : %d Violations\n", a[i].ID, a[i].max_p, a[i].alloc, a[i].alloc-a[i].max_p);
	      v+=a[i].alloc-a[i].max_p;  // then add one to the violations counter
	    }
	}
    }

  

  fitness = 1000*v + pref_score;

  s->f = fitness;
  s->v = v;
  
  return fitness;

}



void print_allocation(sol *s)
{
  int i, proj;
  char id[30];
  evaluate_sol(s);
  printf("Complete List of Project Allocations Made:\n");
  printf("STUDENT\tCHOICE\tPRJ_ID\tN_ALLOC\tMAX\n");
  for(i=1;i<=157;i++)
    {
      sprintf(id,"%s",st[i].choices[s->x[i]]);
      proj=hashsearch(id);
      printf("%d\t%d\t%s\t%d\t%d\n", i, s->x[i], id, a[proj].alloc, a[proj].max_p);
    }
  

}

void print_solution_details(sol *s)
{
  int i;

  print_allocation(s);
  printf("Summary details:\n");
  printf("#violations= %d\n", s->v);
  printf("fitness= %d\n", s->f);
  for(i=1;i<6;i++)
    printf("%d students got their choice %d project\n", s->dist[i], i);
  
  
}


int main(int argc, char **argv)
{
  if(argc!=3)
    {
      fprintf(stderr,"Need to provide two arguments: seed MAXITERS\n");
      exit(1);
    }

  seed=atol(argv[1]);
  srand(seed);
  MAXITERS=atoi(argv[2]);
 

  s = (sol *)malloc(sizeof(sol));  // this holds the solution, which is randomized each step

  int best=1000000;  // fitness of the best ever solution
  char proj[30];
  int i,j,f,g,r;
 
  if(fp=fopen("Project-Choices3.txt", "r"))
    {
      fscanf(fp, "%*s %*s %*s\n");
      while(!feof(fp))
	{
	  fscanf(fp, "%d %s %d\n", &i, proj, &r);
	  // printf("%d\n", i);
	  sprintf(st[i].choices[r],"%s",proj);
	  st[i].nc=r;
	}
      fclose(fp);
    }
  else
    {
      fprintf(stderr,"Couldn't open project choices file\n");
      exit(1);
    }

  // student choices read in.
  // Next set up hash table and read in project IDs and numbers of participants
  // and store in hash as well

  hashinitialize();
  int n;
  if(fp=fopen("Project-Participants.txt", "r"))
    {
      fscanf(fp, "%*s %*s \n");
      while(!feof(fp))
	{
	  fscanf(fp, "%s %d \n", proj, &n);
	  hashinsert(proj, n);
	}
      
      fclose(fp);
    }
  else
    {
      fprintf(stderr,"Couldn't open project participants file\n");
      exit(1);
    }
 

  // Random Search Begins
  for(j=0;j<MAXITERS;j++)  // MAXITERS iterations
    {
      for(i=1;i<=157;i++)    // for every student
	s->x[i]=1+(int)(RN*5);   // give them a project choice between 1 and 5

      f=evaluate_sol(s);  // calculate the fitness

      if(j%10000==1)  // print out every 1000th one (for speed)
	printf("%d Fitness= %d Best= %d\n",j,f, best);
      if(f<best)
	best=f;
    }
  // optimization ended

  printf("%d Fitness= %d Best= %d\n",MAXITERS,f, best);

  // print out over-allocated projects (i.e. violations) first
  printf("\n\n=== RESULTS (last random solution) ===\n\nOver-allocated projects (violations) :\n");
  printf("PRJ_ID\tN_ALLOC\tMAX\n");
  for(i=1;i<=M;i++)
    {
      
      if(a[i].max_p>0)
	if(a[i].alloc>a[i].max_p)
	  printf("%s\t%d\t%d\n", a[i].ID, a[i].alloc, a[i].max_p);
    }
  print_solution_details(s);
  
}
