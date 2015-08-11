# ASM 3 #

In this repo you can find an simulated 16 bit microprocessor as a [LogiSim](http://www.cburch.com/logisim/) simulation.
A assembler for this processor is implemented in Java. It is implemented to teach my students programming in assembler. This is the third iteration therefore the name ASM 3.
The processor is a harvard single cycle cpu and its architecture is inspired by the MIPS architecture, but the assembler supports pseudo instructions like push, pop, enter, leave or call. So it is also possible to program it like a x86 processor.

### Usage ###

* In the folder src/main/logisim you can fine the processor.
* In the folder src/main/asm are some sample programs in assembler which can be assembled to a hex file for loading to the instruction memory of the processor.
* The assembler you can find in the folder src/main/java

### Set up? ###

* The Processor can directly opened with LogiSim
* The Assembler is a maven project. So you can build it calling "mvn build" on the command line
* Most Java-IDE's are able to import the pom.xml file. Best maven support you get using NetBeans.

### Who do I talk to? ###

* If you have questions you can contact the author using the BitBucket build in possibilities.