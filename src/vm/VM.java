package vm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VM {
    ArrayList<String> quadruple;
    ArrayList<Integer> dataSegment;
    BufferedReader br;
    PrintWriter writer1;
    
    public void InitCodeSegment()
    {
        try {
            br = new BufferedReader(new FileReader("machine-code.txt"));
            quadruple=new ArrayList<String>();
            String str=br.readLine();
            while(str!=null)
            {
                quadruple.add(str);
                str=br.readLine();
            }
        } catch (Exception ex) {
            Logger.getLogger(VM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void InitDataSegment()
    {
        try {
            
            br=new BufferedReader(new FileReader("parser-symboltable.txt"));
            dataSegment=new ArrayList<Integer>();
            String str=br.readLine();
            //Initializing all integers with 0
            while(str!=null)
            {
                String tokens[]=str.split("\t");
                if(tokens[0].charAt(0)>=48 && tokens[0].charAt(0)<=57)
                    dataSegment.add(Integer.parseInt(tokens[0]));
                else 
                    dataSegment.add(0);
                str=br.readLine();
            }
           
            
        } catch (Exception ex) {
            Logger.getLogger(VM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void Input(String operand)
    {
        Scanner myObj = new Scanner(System.in);  // Create a Scanner object
        System.out.print("Enter integer: ");
        int index=Integer.parseInt(operand)/4;
        dataSegment.set(index, myObj.nextInt());  // Read user input into the data segment
    }
    public int GreaterThan(String op1,String op2, String lineNum,int x)
    {
       int index1=Integer.parseInt(op1)/4;
       int index2=Integer.parseInt(op2)/4;
       if(dataSegment.get(index1)>dataSegment.get(index2))
           return Integer.parseInt(lineNum)-2;
       else
       {
           return x;
       }
    }
    public int GreaterThanEqual(String op1,String op2, String lineNum,int x)
    {
       int index1=Integer.parseInt(op1)/4;
       int index2=Integer.parseInt(op2)/4;
       if(dataSegment.get(index1)>=dataSegment.get(index2))
           return Integer.parseInt(lineNum)-2;
       else
           return x;
    }
    public int LessThan(String op1,String op2, String lineNum,int x)
    {
       int index1=Integer.parseInt(op1)/4;
       int index2=Integer.parseInt(op2)/4;
       if(dataSegment.get(index1)<dataSegment.get(index2))
           return Integer.parseInt(lineNum)-2;
       else
           return x;
    }
    public int LessThanEqual(String op1,String op2, String lineNum,int x)
    {
       int index1=Integer.parseInt(op1)/4;
       int index2=Integer.parseInt(op2)/4;
       if(dataSegment.get(index1)<=dataSegment.get(index2))
           return Integer.parseInt(lineNum)-2;
       else
           return x;
    }
    public int Equals(String op1,String op2, String lineNum,int x)
    {
       int index1=Integer.parseInt(op1)/4;
       int index2=Integer.parseInt(op2)/4;
       if(dataSegment.get(index1).equals(dataSegment.get(index2)))
           return Integer.parseInt(lineNum)-2;
       else
           return x;
    }
    public void Assignment(String val, String var)
    {
        int indexVar=Integer.parseInt(var)/4;
        int indexVa1=Integer.parseInt(val)/4;
        dataSegment.set(indexVar,dataSegment.get(indexVa1));
        
    }
    public void Add(String op1,String op2,String result)
    {
        int index1=Integer.parseInt(op1)/4;
        int index2=Integer.parseInt(op2)/4;
        int index3=Integer.parseInt(result)/4;
        dataSegment.set(index3,dataSegment.get(index2)+dataSegment.get(index1));
    }
    public void Subtract(String op1,String op2,String result)
    {
        int index1=Integer.parseInt(op1)/4;
        int index2=Integer.parseInt(op2)/4;
        int index3=Integer.parseInt(result)/4;
        dataSegment.set(index3,dataSegment.get(index1)-dataSegment.get(index2));
    }
    public void Multiply(String op1,String op2,String result)
    {
        int index1=Integer.parseInt(op1)/4;
        int index2=Integer.parseInt(op2)/4;
        int index3=Integer.parseInt(result)/4;
        dataSegment.set(index3,dataSegment.get(index1)*dataSegment.get(index2));
    }
    public void Divide(String op1,String op2,String result)
    {
        int index1=Integer.parseInt(op1)/4;
        int index2=Integer.parseInt(op2)/4;
        int index3=Integer.parseInt(result)/4;
        dataSegment.set(index3,dataSegment.get(index2)/dataSegment.get(index1));
    }
    public void ExecuteQuadruple()
    {
        for(int x=0;x<quadruple.size();x++)
        {
            
            String instruction = quadruple.get(x);
            //System.out.println("Instruction: "+instruction);
            String[] tokens=instruction.split("\t");
            int opcode=Integer.parseInt(tokens[0]);
            switch (opcode){
                case 1:
                    Add(tokens[1],tokens[2],tokens[3]);
                    break;
                case 2:
                    Subtract(tokens[1],tokens[2],tokens[3]);
                    break;
                case 3:
                    Multiply(tokens[1],tokens[2],tokens[3]);
                    break;
                case 4:
                    Divide(tokens[1],tokens[2],tokens[3]);
                    break;
                case 5:
                    Assignment(tokens[1],tokens[2]);
                    break;
                case 6:
                    x=Equals(tokens[1],tokens[2],tokens[3],x);
                    break;
                case 7:
                    x=LessThan(tokens[1],tokens[2],tokens[3],x);
                    break;
                case 8:
                    x=GreaterThan(tokens[1],tokens[2],tokens[3],x);
                    break; 
                case 9:
                    x=LessThanEqual(tokens[1],tokens[2],tokens[3],x);
                    break;
                case 10:
                    x=GreaterThan(tokens[1],tokens[2],tokens[3],x);
                    break;
                case 12:
                    x=Integer.parseInt(tokens[1])-2;
                    break; 
                case 13:
                    Input(tokens[1]);
                    break;
                case 14:
                    System.out.print(dataSegment.get(Integer.parseInt(tokens[1])/4));
                    break; 
                case 15:
                    System.out.println(dataSegment.get(Integer.parseInt(tokens[1])/4));
                    break;
                case 16:
                    System.out.println(dataSegment.get(Integer.parseInt(tokens[1])/4));
                    break;
                  
            }
//                       System.out.println("Data Segment");
//            for(int i=0;i<dataSegment.size();i++)
//            {
//                System.out.println(dataSegment.get(i));
//            }  
        }
    }
    public void runVM()
    {
        //Loading the Quadruple
        InitCodeSegment();
        
        //Initializing data segment using symboltable
        InitDataSegment();
        
        //Executing Instructions
        ExecuteQuadruple();
    }

    public static void main(String[] args) {
        
        // interface that obtains absolute path of a file including its name
        Scanner myObj = new Scanner(System.in); 
        System.out.println("Enter the complete file path terminating with the .go file");
        String filePath = myObj.next(); 
        System.out.println("File Path is: " + filePath);
         
        //checks for .go extension
        String extension = filePath.substring(filePath.length() - 3);
        if(!extension.equals(".go"))
        {
            System.out.println("File path must end in source file name which should have the .go extension");
            System.exit(-1);
        }
        
        //Compilation Phase 1# Lexical Analyzer,generates words.txt
        LexicalAnalyzer lex = new LexicalAnalyzer();
        if(lex.runLex(filePath)==false)
        {
            System.out.println("Lexical Analyzer detected an error so further compilation halted");
            System.exit(1);
        }
        
        //Compilation Phases 2,3: Generates parse tree,symbol table and machine code 
        Parser parser= new Parser();
        parser.runParser();
        
        //Virtual Machine
        VM vm =new VM();
        vm.runVM();
    }
    
}
