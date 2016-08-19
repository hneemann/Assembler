# ASM 3 #

In this repo you can find a assembler for a simulated 16 bit microprocessor as a [Digital](https://github.com/hneemann/Digital/) 
simulation. You can find the processor itself as one of the examples in the [Digital](https://github.com/hneemann/Digital/) repo.
The assembler is implemented in Java. 
The processor is a harvard single cycle cpu and its architecture is inspired by the MIPS architecture.
The handling of long constants is a bit of a hack to avoid a 32 bit instruction memory.   
The assembler supports pseudo instructions like *push*, *pop*, *enter*, *leave* or *call* and *ret [n]*. 
So it is also possible to program more like for a x86 cisc processor. So on can explain the different techniques to 
create and remove the stack frame of a function call on a x86 processor. 
The assembler has a simple GUI and is able to control the simulator using its TCP/IP interface. 
So it is easy to debug an assembly program within the simulator. 

### Usage ###

* In the folder *src/main/asm* are some sample programs in assembler. 
  The assembler creates a hex file to be loaded to the instruction memory of the simulated processor.
* You only need to start the simulator and load the example processor. Then you can control the simulator 
  by the assembler GUI.
* You can find the assembler in the folder *src/main/java*

### Set up? ###

* You can find the processor in the [Digital](https://github.com/hneemann/Digital/) repo or release ZIP as one of the examples.
* The assembler is a maven project. So you can build it calling `mvn install` on the command line
* Most Java-IDE's are able to import the *pom.xml* file. Best maven support you can get using NetBeans.
