[![Build Status](https://travis-ci.org/hneemann/Assembler.svg?branch=master)](https://travis-ci.org/hneemann/Assembler)

[Download latest Release](https://github.com/hneemann/Assembler/releases/latest)

The most recent changes are listed in the [release notes](distribution/ReleaseNotes.txt).

# ASM 3 #

![screnshot](screenshot.png)

In this repo you can find an assembler for a simulated 16 bit microprocessor which is realized as a 
[Digital](https://github.com/hneemann/Digital/) simulation. 
You can find the processor itself as one of the examples in the [Digital](https://github.com/hneemann/Digital/) repository
or in the distributed zip file.
The processor is a 16 bit harvard single cycle cpu and it is inspired by the MIPS architecture.
Only the handling of long constants is a bit of a hack to avoid a 32 bit instruction memory.   
The assembler supports pseudo instructions like *push*, *pop*, *enter*, *leave* or *call* and *ret [n]*. 
So it is also possible to program more like for a x86 cisc processor.  
The assembler has a simple GUI and is able to control the [simulator](https://github.com/hneemann/Digital/) by using its TCP/IP 
interface. So it is easy to debug an assembly program within the simulator. 

### Usage ###

* In the folder *src/main/asm* are some sample programs in assembler. 
  The assembler creates a hex file to be loaded to the instruction memory of the simulated processor.
* You only need to start the simulator and load the example processor. Then you can control the simulator 
  by the assembler GUI.
* You can find the assembler in the folder *src/main/java*

### Set up? ###

* You can find the processor in the [Digital](https://github.com/hneemann/Digital/) repo or release ZIP as one of the examples.
* The assembler is implemented in Java and is build with maven. So you can build it calling `mvn install` on the command line.
* Most Java-IDE's are able to import the *pom.xml* file. Best maven support you can get using NetBeans.
