#!/bin/bash
# javac DbQuery.java
# java DbQuery query.txt config.txt

if [ $# -eq 2 ]
then
	javac DbQuery.java
	java DbQuery $1 $2
	java DbQuery $1 $2 &> output.txt
else
	javac DbQuery.java
	java DbQuery query.txt config.txt
	java DbQuery query.txt config.txt &> output.txt
fi
