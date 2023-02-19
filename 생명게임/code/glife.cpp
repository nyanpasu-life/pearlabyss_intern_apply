/****************************************/
/*                                      */
/*                                      */
/*  Game of Life with Pthread and CUDA  */
/*                                      */
/*  CSE561/SCE412 Project #2            */
/*  @ Ajou University                   */
/*                                      */
/*                                      */
/****************************************/

#include "glife.h"
using namespace std;

int gameOfLife(int argc, char *argv[]);
void singleThread(int, int, int);
void* workerThread(void *);
void* confirmThread(void *);
int nprocs;
GameOfLifeGrid* g_GameOfLifeGrid;

uint64_t dtime_usec(uint64_t start)
{
  timeval tv;
  gettimeofday(&tv, 0);
  return ((tv.tv_sec*USECPSEC)+tv.tv_usec)-start;
}

GameOfLifeGrid::GameOfLifeGrid(int rows, int cols, int gen)
{
  m_Generations = gen;
  m_Rows = rows;
  m_Cols = cols;

  m_Grid = (int**)malloc(sizeof(int*) * rows);
  if (m_Grid == NULL)
    cout << "1 Memory allocation error " << endl;

  m_Temp = (int**)malloc(sizeof(int*) * rows);
  if (m_Temp == NULL)
    cout << "2 Memory allocation error " << endl;

  m_Grid[0] = (int*)malloc(sizeof(int) * (cols*rows));
  m_1DGrid = m_Grid[0];
  if (m_Grid[0] == NULL)
    cout << "3 Memory allocation error " << endl;

  m_Temp[0] = (int*)malloc(sizeof(int) * (cols*rows));
  if (m_Temp[0] == NULL)
    cout << "4 Memory allocation error " << endl;

  for (int i = 1; i < rows; i++) {
    m_Grid[i] = m_Grid[i-1] + cols;
    m_Temp[i] = m_Temp[i-1] + cols;
  }

  for (int i = 0; i < rows; i++) {
    for (int j = 0; j < cols; j++) {
      m_Grid[i][j] = m_Temp[i][j] = 0;
    }
  }
}

// Entry point
int main(int argc, char* argv[])
{
  if (argc != 7) {
    cout <<"Usage: " << argv[0] << " <input file> <display> <nprocs>"
           " <# of generation> <width> <height>" << endl;
    cout <<"\n\tnprocs = 0: Enable GPU" << endl;
    cout <<"\tnprocs > 0: Run on CPU" << endl;
    cout <<"\tdisplay = 1: Dump results" << endl;
    return 1;
  }

  return gameOfLife(argc, argv);
}

int gameOfLife(int argc, char* argv[])
{
  int cols, rows, gen;
  ifstream inputFile;
  int input_row, input_col, display;
  uint64_t difft, cuda_difft;
  pthread_t *threadID;

  inputFile.open(argv[1], ifstream::in);

  if (inputFile.is_open() == false) {
    cout << "The "<< argv[1] << " file can not be opend" << endl;
    return 1;
  }

  display = atoi(argv[2]);
  nprocs = atoi(argv[3]);
  gen = atoi(argv[4]);
  cols = atoi(argv[5]);
  rows = atoi(argv[6]);

  g_GameOfLifeGrid = new GameOfLifeGrid(rows, cols, gen);

  while (inputFile.good()) {
    inputFile >> input_row >> input_col;
    if (input_row >= rows || input_col >= cols) {
      cout << "Invalid grid number" << endl;
      return 1;
    } else
      g_GameOfLifeGrid->setCell(input_row, input_col);
  }

  // Start measuring execution time
  difft = dtime_usec(0);

  // TODO: YOU NEED TO IMPLMENT THE SINGLE THREAD, PTHREAD, AND CUDA
  if (nprocs == 0) {
    // Running on GPU
    cuda_difft = runCUDA(rows, cols, gen, g_GameOfLifeGrid, display);
  } else if (nprocs == 1) {
    // Running a single thread
    singleThread(rows, cols, gen);
  } else {
    // Running multiple threads (pthread)
    threadID = (pthread_t*) malloc (sizeof(pthread_t)*nprocs);
    for(int g=0; g<gen; g++){
      int width = rows*cols / nprocs;
      int margin = rows*cols % nprocs;
      int from, to;
      for(int i=0;i<nprocs;i++){
        if(i==0) { from = 0;}
        else    { from = to+1;}
        to = from + width -1;
        if(i<margin) { to +=1;}

        int * threadArg = (int*)malloc(sizeof(int)*2);
        threadArg[0] = from;
        threadArg[1] = to;

        pthread_create(&threadID[i], NULL, workerThread,(void*)threadArg);
      }

      for(int i=0;i<nprocs;i++){
        pthread_join(threadID[i], NULL);
      }

      g_GameOfLifeGrid->flushGrid();
    }
  }

  difft = dtime_usec(difft);

  // Print indices only for running on CPU(host).
  if (display && nprocs) {
    g_GameOfLifeGrid->dump();
    g_GameOfLifeGrid->dumpIndex();
  }

  if (nprocs) {
    // Single or multi-thread execution time
    cout << "Execution time(seconds): " << difft/(float)USECPSEC << endl;
  } else {
    // CUDA execution time
    cout << "Execution time(seconds): " << cuda_difft/(float)USECPSEC << endl;
  }
  inputFile.close();
  cout << "Program end... " << endl;
  return 0;
}

// TODO: YOU NEED TO IMPLMENT SINGLE THREAD
void singleThread(int rows, int cols, int gen)
{
  for(int g=0;g<gen;g++){
    g_GameOfLifeGrid->next();
    g_GameOfLifeGrid->flushGrid();
  }

}

// TODO: YOU NEED TO IMPLMENT PTHREAD
void* workerThread(void *arg)
{
  //int cols = g_GameOfLifeGrid->getCols();  int rows = g_GameOfLifeGrid->getRows();  int max  = cols * rows;
  int * Arr = (int*)arg;
  int from = Arr[0];
  int to = Arr[1];

  g_GameOfLifeGrid->next(from, to);
}

// HINT: YOU MAY NEED TO FILL OUT BELOW FUNCTIONS OR CREATE NEW FUNCTIONS
void GameOfLifeGrid::next(const int from, const int to)
{
  int i = from / m_Cols; int j = from % m_Cols;
  int f = from;
  while(f<=to){
    int nei = getNumOfNeighbors(i,j);
    if( (isLive(i,j)&&nei==2) || nei==3 ){ live(i, j); }
    else                                 { dead(i, j); }

    f++;
    j++;
    if(j==m_Cols) { j=0; i++; }
  }

}

void GameOfLifeGrid::next()
{
  for(int i=0;i<getRows(); i++){
    for(int j=0;j<getCols();j++){
      int nei = getNumOfNeighbors(i,j);
      if( (isLive(i,j)&&nei==2) || nei==3 ){ live(i, j); }
      else                                 { dead(i, j); }
    }
  }
}

// TODO: YOU MAY NEED TO IMPLMENT IT TO GET NUMBER OF NEIGHBORS
int GameOfLifeGrid::getNumOfNeighbors(int rows, int cols)
{
  int numOfNeighbors = 0;


  if(rows>=1        && cols>=1        && isLive(rows-1, cols-1)){numOfNeighbors ++;}
  if(rows>=1        &&                   isLive(rows-1, cols  )){numOfNeighbors ++;}
  if(rows>=1        && cols<=m_Cols-2 && isLive(rows-1, cols+1)){numOfNeighbors ++;}
  if(                  cols>=1        && isLive(rows, cols-1))  {numOfNeighbors ++;}
  if(                  cols<=m_Cols-2 && isLive(rows, cols+1))  {numOfNeighbors ++;}
  if(rows<=m_Rows-2 && cols>=1        && isLive(rows+1, cols-1)){numOfNeighbors ++;}
  if(rows<=m_Rows-2 &&                   isLive(rows+1, cols))  {numOfNeighbors ++;}
  if(rows<=m_Rows-2 && cols<=m_Cols-2 && isLive(rows+1, cols+1)){numOfNeighbors ++;}

  return numOfNeighbors;
}

void GameOfLifeGrid::dump()
{
  cout << "===============================" << endl;

  for (int i = 0; i < m_Rows; i++) {
    cout << "[" << i << "] ";
    for (int j = 0; j < m_Cols; j++) {
      if (m_Grid[i][j] == 1)
        cout << "*";
      else
        cout << "o";
    }
    cout << endl;
  }
  cout << "===============================\n" << endl;
}

void GameOfLifeGrid::dumpIndex()
{
  cout << ":: Dump Row Column indices" << endl;
  for (int i=0; i < m_Rows; i++) {
    for (int j=0; j < m_Cols; j++) {
      if (m_Grid[i][j]) cout << i << " " << j << endl;
    }
  }
}