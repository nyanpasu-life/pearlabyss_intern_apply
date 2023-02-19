#Project 3. New Page Replacement Policy

###About
This project consists of two parts:
1. Memory management statistics system call
2. Change page replacement policy from second chance LRU-approximation algorithm to counter-based clock algorithm.

The user-level test program is included at ./user directory.

The kernel module for new counter-based page replacement policy is included at ./lruCounter directory. You should change the path of 'KDIR' in Makefile to your linux source directory.

The memory statistics system call program is included at ./memstat_syscall directory.

You should add the path of ./memstat_syscall to your linux kernel's Makefile(at core-y).

To execute 'memstat_user', you should insert test case number using command option. Default option is test case 1.
ex) ./memstat_user 1
* Test case 1 : Total sequential access
* Test case 2 : Half sequential access
* Test case 3 : Random sequential access
* Test case 4 : Random half sequential access

###Command
* to build
```
# in ./user
make

# in ./lruCounter
make
```
* to execute (in virtual machine)
```
# in ./lruCounter
insmod lruCounter.ko

# in ./user
make    # if not built
./memstat_user [test_case_number]
```
* to clean
```
# in ./user, ./lruCounter
make clean
```
###Info
201521032 Han Taehui
201720733 Shin Seungheon