Fredrick Kofi Tam, UNI:fkt2105
Teresa Choe, UNI:tc2716

Project 2 for COMSW4112
=======================

Folder contains:
----------------

branch_mispred.c: c code to test optimal plans generated

config.txt: file which contains configurations of  system code is being run

DbQuery.java: java implementation of algoritm 4.11, Finds optimal plans.

Makefile: compiles java code

query.txt: contains selectivities of functions to optimize for

stage2.sh: shell script to execute/run java optimizer program

SubsetRecord.java: java class that contains subset information

output.txt: contains optimal plans produced by java program

Implementation:
---------------
We basically followed steps in paper by implementing algorithm 4.11.
We decided to make a SubsetRecord class which contained all the necessary 
information for each subset that is put in the array A of size 2^k-1. 
Java program accepts config.txt and query.txt as parameters that it processes
based on the algorithm in paper to produce optimal plans which are printed
to standard output. This output is then piped into output.txt



