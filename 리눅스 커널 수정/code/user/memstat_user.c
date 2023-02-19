#include <stdio.h>
#include <unistd.h>
#include <sys/syscall.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/time.h>
#include <time.h>
#define NR_my_syscall 548

#define ALLOC_ITERATION 70000
#define CALL_ITERATION 999
#define TEST_ALLOC_SIZE 1048576 //bytes


int file_act, file_inact, anon_act, anon_inact, ref_act, \
		ref_inact, promote_count, demote_count, evict_count;

void get_mem_stats() {
    long ret;
    int *mem_stats = (int*)malloc(sizeof(int)*9);

	struct timeval  tv1, tv2;
	gettimeofday(&tv1, NULL);
    ret = syscall(NR_my_syscall, mem_stats);
	if (ret != 0) {
		fprintf(stderr, "ERR[%ld]: syscall %d is failed\n", ret, NR_my_syscall);
        return;
	}
	gettimeofday(&tv2, NULL);

    file_act = mem_stats[0];
	file_inact = mem_stats[1];
	anon_act = mem_stats[2];
	anon_inact = mem_stats[3];
	ref_act = mem_stats[4];
	ref_inact = mem_stats[5];
	promote_count = mem_stats[6];
	demote_count = mem_stats[7];
	evict_count = mem_stats[8];
    printf("Active File page\t\t: %d\n", file_act); 
	printf("Inactive File page\t\t: %d\n", file_inact); 
	printf("Active Anon page\t\t: %d\n", anon_act); 
	printf("Inactive Anon page\t\t: %d\n", anon_inact); 
	printf("\n");
	printf("Referenced Active page\t\t: %d\n", ref_act);
	printf("Referenced Inactive page\t: %d\n", ref_inact);
	printf("\n");
	printf("Cumulated promoted page\t\t: %d\n", promote_count);
	printf("Cumulated demoted page\t\t: %d\n", demote_count);
	printf("\n");
	printf("Cumulated evicted page\t\t: %d\n", evict_count);
	printf ("Total time = %.f microseconds\n",
			(double) (tv2.tv_usec - tv1.tv_usec) +
			(double) (tv2.tv_sec - tv1.tv_sec) * 1000000);
	printf("\n\n");
	free(mem_stats);
}

int main(int argc, char** argv) {
	srand(time(NULL));
	int TEST_MODE=0;
	int HALF_ALLOC_ITERATION = ALLOC_ITERATION /2;

	if(argc ==2)
		TEST_MODE = atoi(argv[1]);

	struct timeval  t1, t2;

    int * alloc_list[ALLOC_ITERATION];

    get_mem_stats();

	gettimeofday(&t1, NULL);
    //alloc
    for(int i=0;i<ALLOC_ITERATION;i++){
        alloc_list[i] = (int*)malloc(TEST_ALLOC_SIZE);
    }

	//call test
	if(TEST_MODE ==0){
		for(int i=0;i<CALL_ITERATION;i++){
        	for(int j=0;j<ALLOC_ITERATION;j++){
            	alloc_list[j][0] = i;
			}
        }
    }
	if(TEST_MODE ==1){
		for(int i=0;i<CALL_ITERATION;i++){
        	for(int j=0;j<HALF_ALLOC_ITERATION;j++){
            	alloc_list[j][0] = i;
			}
			for(int j=0;j<HALF_ALLOC_ITERATION;j++){
            	alloc_list[j][0] = i;
			}
        }
    }
	if(TEST_MODE ==2){
		for(int i=0;i<CALL_ITERATION;i++){
        	for(int j=0;j<ALLOC_ITERATION;j++){
				int k = (int)(((double)((rand()<<15) | rand())) / (((RAND_MAX<<15) | RAND_MAX) + 1) * (ALLOC_ITERATION));
            	alloc_list[k][0] = i;
			}
        }
    }
		if(TEST_MODE ==3){
		for(int i=0;i<CALL_ITERATION;i++){
        	for(int j=0;j<HALF_ALLOC_ITERATION;j++){
				int k = (int)(((double)((rand()<<15) | rand())) / (((RAND_MAX<<15) | RAND_MAX) + 1) * (ALLOC_ITERATION));
            	alloc_list[k][0] = i;
			}
			for(int j=0;j<HALF_ALLOC_ITERATION;j++){
				int k = (int)(((double)((rand()<<15) | rand())) / (((RAND_MAX<<15) | RAND_MAX) + 1) * (ALLOC_ITERATION));
            	alloc_list[k][0] = i;
			}
        }
    }

    //free
    for(int i=0;i<ALLOC_ITERATION;i++){
        free(alloc_list[i]);
    }
	gettimeofday(&t2, NULL);
	printf ("TEST TIME is %.f microseconds\n",
			(double) (t2.tv_usec - t1.tv_usec) +
			(double) (t2.tv_sec - t1.tv_sec) * 1000000);

    get_mem_stats();
	return 0;
}
