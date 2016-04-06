package asm;

import absyn.*;
import symb.*;
import java.io.*;
import java.util.*;

abstract public class Asm {
  static private StringBuilder asm = new StringBuilder(); //string output to .tm file
  static private int address = 0;

  public enum Operations {
    HALT, IN, OUT, ADD, SUB, MUL, DIV, LD, ST,
    LDA, LDC, JLT, JLE, JGT, JGE, JEQ, JNE;
  }

  static private List<Operations> registerOnly = Arrays.asList(
    Operations.HALT, Operations.IN, Operations.OUT, 
    Operations.ADD, Operations.SUB, Operations.MUL, 
    Operations.DIV, Operations.LDC
  );

  static private void addLine(int addr, Operations oper, int r, int s, int t) {
    if(registerOnly.contains(oper)) {
      asm.append(addr + ":  " + r + "," + s + "," + t + "\n");
    }
    else {
      asm.append(addr + ":  " + r + "," + s + "(" + t + ")" + "\n");
    }
  }

  static private void header(String filename) {
    asm.append("* C-Minus Compilation to TM Code\n");
    String out = filename.substring(0, filename.lastIndexOf(".")) + ".tm";
    asm.append("* File: " + out + "\n");
  }

  static private void prelude() {
    asm.append("* Standard prelude:\n");
    asm.append("0:     LD  6,0(0)\n");
    asm.append("1:    LDA  5,0(6)\n");
    asm.append("2:     ST  0,0(0)\n");
    asm.append("* Jump around i/o routines here\n");
    asm.append("* code for input routine\n");
    asm.append("4:     ST  0,-1(5)\n");
    asm.append("5:     IN  0,0,0\n");
    asm.append("6:     LD  7,-1(5)\n");
    asm.append("* code for input routine\n");
    asm.append("7:     ST  0,-1(5)\n");
    asm.append("8:     LD  0,-2(5)\n");
    asm.append("9:    OUT  0,0,0\n");
    asm.append("10:    LD  7,-1(5)\n");
    asm.append("3:    LDA  7,7(7)\n");
    asm.append("* End of standard prelude.\n");
    address += 11;
  }

  static public void generateAssembly(String filename) {
    header(filename);
    prelude();

    /* output file name with path and .tm file type */
    String write = filename.substring(0, filename.lastIndexOf('.')) + ".tm";
    /* export assembly to external file */
    try {
      File file = new File(write);
      if(!file.exists()) 
        file.createNewFile();

      FileWriter fw = new FileWriter(file.getAbsoluteFile());
      BufferedWriter bw = new BufferedWriter(fw);
      bw.write(asm.toString());
      bw.close();
      System.out.println("Compile complete, saved to " + write);
    } catch(IOException e) {
      System.out.println("Error: failed to write to " + write);
    }
  }
}