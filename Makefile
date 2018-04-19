# File: Makefile
# Simple make file to build the executable

JC = javac
.SUFFIXES: .java .class
.java.class: 
	$(JC) $(JFLAGS) $*.java
CLASSES= SubsetRecord.java DbQuery.java 
default: classes
classes: $(CLASSES:.java=.class)
clean: 
	$(RM) *.class
# all: $(TARGETS)

# branch_mispred: branch_mispred.o
# 	$(CC) $(CFLAGS) $(LIB_INCLUDE) -o $@ branch_mispred.o $(LIBS)
#.c.o:
#	$(CC) $(CFLAGS) $(INCLUDE) -c -o  $*.o $<
#clean:
#	rm -f *.o *~ $(TARGETS)
