#ifdef __cplusplus
extern "C++" {
#include "glife.h"
}
#include <cuda.h>

#define BLOCK_NUM 10
#define THREAD_PER_B_NUM 10

// HINT: YOU CAN USE THIS METHOD FOR ERROR CHECKING
// Print error message on CUDA API or kernel launch
#define cudaCheckErrors(msg) \
    do { \
        cudaError_t __err = cudaGetLastError(); \
        if (__err != cudaSuccess) { \
            fprintf(stderr, "Fatal error: %s (%s at %s:%d)\n", \
                    msg, cudaGetErrorString(__err), \
                    __FILE__, __LINE__); \
            fprintf(stderr, "*** FAILED - ABORTING\n"); \
        } \
    } while (0)

__device__ int isLive_cu(int*grid, int index){
    return (grid[index] ? 1 : 0);
}

// TODO: YOU MAY NEED TO USE IT OR CREATE MORE
__device__ int getNeighbors(int* grid, int tot_rows, int tot_cols, int row, int col) {
    int numOfNeighbors = 0;
    int index = row*tot_cols + col;

    if(row>=1          && col>=1          && isLive_cu(grid,index -tot_cols -1)){numOfNeighbors ++;}
    if(row>=1          &&                    isLive_cu(grid,index -tot_cols   )){numOfNeighbors ++;}
    if(row>=1          && col<=tot_cols-2 && isLive_cu(grid,index -tot_cols +1)){numOfNeighbors ++;}
    if(                   col>=1          && isLive_cu(grid,index           -1)){numOfNeighbors ++;}
    if(                   col<=tot_cols-2 && isLive_cu(grid,index           +1)){numOfNeighbors ++;}
    if(row<=tot_rows-2 && col>=1          && isLive_cu(grid,index +tot_cols -1)){numOfNeighbors ++;}
    if(row<=tot_rows-2 &&                    isLive_cu(grid,index +tot_cols   )){numOfNeighbors ++;}
    if(row<=tot_rows-2 && col<=tot_cols-2 && isLive_cu(grid,index +tot_cols +1)){numOfNeighbors ++;}

    return numOfNeighbors;
}

// TODO: YOU NEED TO IMPLEMENT KERNEL TO RUN ON GPU DEVICE
__global__ void kernel(int * readGrid, int * writeGrid, int tot_rows, int tot_cols, int BN, int TpBN)
{
    int width; int margin;
    if(tot_rows*tot_cols > BN*TpBN){
        width = (tot_rows*tot_cols) / (BN*TpBN);
        margin = (tot_rows*tot_cols) % (BN*TpBN);
    }
    else{
        width = 1;
        margin = 0;
    }




    int from = blockIdx.x*TpBN*width + (threadIdx.x * width);
    int to = from + width -1;
    int max = tot_rows * tot_cols;

    int tindex = blockIdx.x * TpBN + threadIdx.x;
    if(BN*TpBN -1 -tindex < margin and BN*TpBN -1 -tindex >=0){
        int i = margin -BN*TpBN +1 +tindex;
        from = from + i;
        to = to + i;
    }

    while(from <=to && from<max){
        int i = from / tot_cols;
        int j = from % tot_cols;

        int nei = getNeighbors(readGrid, tot_rows, tot_cols, i, j);
        if( (isLive_cu(readGrid,from)&&nei==2) || nei==3) { writeGrid[from] = 1;}
        else                                              { writeGrid[from] = 0;}

        from ++;
    }
}




// TODO: YOU NEED TO IMPLEMENT TO PRINT THE INDEX RESULTS
void cuda_dump(GameOfLifeGrid * golg)
{
    golg->dump();
}

// TODO: YOU NEED TO IMPLEMENT TO PRINT THE INDEX RESULTS
void cuda_dump_index(GameOfLifeGrid * golg)
{
    golg->dumpIndex();
}

// TODO: YOU NEED TO IMPLEMENT ON CUDA VERSION
uint64_t runCUDA(int rows, int cols, int gen,
                 GameOfLifeGrid* g_GameOfLifeGrid, int display)
{
    cudaSetDevice(0); // DO NOT CHANGE THIS LINE

    uint64_t difft;

    // ---------- TODO: CALL CUDA API HERE ----------
    int * Grid1;
    int * Grid2;
    cudaMalloc((void **)&Grid1, sizeof(int) * rows * cols);
    cudaMalloc((void **)&Grid2, sizeof(int) * rows * cols);

    int *cpuArr = g_GameOfLifeGrid->get1DGrid();

    cudaMemcpy(Grid1, cpuArr, sizeof(int)*rows*cols, cudaMemcpyHostToDevice);

    // Start timer for CUDA kernel execution
    difft = dtime_usec(0);
    // ----------  TODO: CALL KERNEL HERE  ----------
    int g= 0;
    while(g<gen){
        if(g%2==1) {kernel<<<BLOCK_NUM,THREAD_PER_B_NUM>>>(Grid2, Grid1, rows, cols,
                        BLOCK_NUM, THREAD_PER_B_NUM);} //odd num
        else    {kernel<<<BLOCK_NUM,THREAD_PER_B_NUM>>>(Grid1, Grid2, rows, cols,
                        BLOCK_NUM, THREAD_PER_B_NUM);} //even num
        g++;

    }

    // Finish timer for CUDA kernel execution
    int *newcpuArr;
    newcpuArr = (int*)malloc(sizeof(int)*rows*cols);
    if(g%2==1) {cudaMemcpy(newcpuArr, Grid2, sizeof(int)*rows*cols, cudaMemcpyDeviceToHost);}
    else    {cudaMemcpy(newcpuArr, Grid1, sizeof(int)*rows*cols, cudaMemcpyDeviceToHost);}


    g_GameOfLifeGrid->updateGrid(newcpuArr);

    difft = dtime_usec(difft);

   // Print the results
    if (display) {
        cuda_dump(g_GameOfLifeGrid);
        cuda_dump_index(g_GameOfLifeGrid);
    }

    cudaFree(Grid1);
    cudaFree(Grid2);
    return difft;
}
#endif
