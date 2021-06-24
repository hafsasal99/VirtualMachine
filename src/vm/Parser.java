package vm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import vm.LexicalAnalyzer;
import java.util.*;


public class Parser {
    String look;
    boolean error=false;
    BufferedReader br;
    PrintWriter writer1;
    PrintWriter writer2;
    PrintWriter writer3;
    PrintWriter writer4;
    int count =0; //to keep track of number of times newTmp is invoked
    String identifierName;
    String dataType;
    static int tab = 0;
    String lexeme=null;
    int n=0;
    int address;
    int vTrue;
    int whileTrue;
    
    String currDir = System.getProperty("user.dir");
    String p = currDir + "\\parser-symboltable.txt";
    //DONE
    int getAddress(String id) throws FileNotFoundException, IOException
    {
        File file=new File(p);    //creates a new file instance  
        FileReader fr=new FileReader(file);   //reads the file  
        BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream  
        StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters  
        
        String line;  
        while((line=br.readLine())!=null)  
        {  
            String[] words = line.split("\t");
            if (words[0].equals(id))
            {
                fr.close(); 
                return Integer.parseInt(words[2]);  
            }
        }  
        
        System.out.println(id + " has not been defined.");
        System.exit(0);
        return -1;
    }
    //DONE
    void backPatch(int lineNo, int add) throws FileNotFoundException, IOException
    {
        String currDir = System.getProperty("user.dir");
        String p = currDir + "\\tac.txt";
        Path path = Paths.get(p);
        
        List <String> lines;
        lines = Files.readAllLines(path, StandardCharsets.UTF_8);
       
        
        String content = lines.get(lineNo);
        content += add + 1 ;
        lines.set(lineNo, content);
        
        PrintWriter writer3 = new PrintWriter(p);
        for(String str: lines)
            writer3.write(str + System.lineSeparator());
        writer3.close();
    }
    //DONE
    void backPatchMC() throws IOException
    {
        String currDir = System.getProperty("user.dir");
        String mc = currDir + "\\machine-code.txt";
        Path pathMC = Paths.get(mc);
        
        String tac = currDir + "\\tac.txt";
        Path pathTAC = Paths.get(tac);
        
        List <String> linesTAC;
        linesTAC = Files.readAllLines(pathTAC, StandardCharsets.UTF_8);
        
        List <String> linesMC;
        linesMC = Files.readAllLines(pathMC, StandardCharsets.UTF_8);
        
        int line = 0;
        for(String str: linesTAC)
        {
            if (str.charAt(0)== 'g' && str.charAt(1)== 'o')
            {
                String content = linesTAC.get(line);
                String [] words = content.split(" ");
                String add = words[1];
               
                String content1 = "12\t" + add;
                linesMC.set(line, content1);
                line++;
            }
            else if (str.charAt(0)== 'i' && str.charAt(1)== 'f' || str.charAt(0)== 'e' && str.charAt(1)== 'l' || str.charAt(0)== 'w' && str.charAt(1)== 'h')
            {
                String content = linesTAC.get(line);
                String [] words = content.split(" ");
                String add = words[5];
               
                String content1 = linesMC.get(line);
                content1.trim();
                content1 += add;
                linesMC.set(line, content1);
                line++;
            }
            else
                line++;
        }
        PrintWriter writer4 = new PrintWriter(mc);
        for(String str: linesMC)
            writer4.write(str + System.lineSeparator());
        writer4.close();
    }
    
    void emit(String args)
    {
        writer3.append(args);
        writer3.append("\n");
        writer3.flush();
        n++;
    }
    
    String newTemp()
    {
        count++;
        String t = "temp"+count;
        //every temp has datatype int because for char assignment we do not need extra variable in TAC
        writer1.append(t + "\tInteger\t" + address + "\n");
        writer1.flush();
        address += 4;
        return t;
    }
    
    String ExpressionS() throws IOException
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
            
            writer2.append("----");
            writer2.flush();
        }
        
        writer2.append("Expression\n");
        writer2.flush();
       
        --tab;
        String v=Expression();
        return v;
    }
    
    String Expression() throws IOException {
        String Ev="";
        if(look.equals("ID")||look.equals("NUM")||look.equals("'('"))
        {
            ++tab;
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
           
            writer2.append("Term\n");
            writer2.flush();
            String Tn=Term();
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("EPrime\n");
            writer2.flush();
            Ev=EPrime(Tn);
            --tab;
            
        }
        else
        {
            error=true;
            System.out.print("Syntax Error in what seemed like an assignment expression.");
            System.exit(1);
        }
       
        return Ev;
    }

    String Term() throws IOException {
	String retValue;
        ++tab;
        for (int i = 0; i < tab; i++) {
            
            writer2.append("----");
            writer2.flush();
        }
        
        writer2.append("F\n");
        writer2.flush();
        String Fn=F();
        
        for (int i = 0; i < tab; i++) {
            
            writer2.append("----");
            writer2.flush();
        }
        
        writer2.append("TPrime\n");
        writer2.flush();
        retValue=TPrime(Fn);  
        --tab;
        return retValue;
    }  

    String EPrime(String str) throws IOException {
        String retValue;
	if (look.equals("'+'")) {
            ++tab;
            match("'+'"); 
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("+\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("Term\n");
            writer2.flush();
            String Tn = Term();
            
            String EprimeI;
            String tmp = newTemp();
            emit(tmp + " = " + str + " + " + Tn);
            
            //use opcode 1 for +
            int quad1 = getAddress(str);
            int quad2 = getAddress(Tn);
            int quad3 = getAddress(tmp);
            
            writer4.append("1\t" + quad1 + "\t" + quad2 + "\t" + quad3 + "\n");
            writer4.flush();
            
            EprimeI = tmp;
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("EPrime\n");
            writer2.flush();
            retValue=EPrime(EprimeI);
            --tab;
            
	}
	else if (look.equals("'-'")) {
            ++tab;
            match("'-'"); 
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("-\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("Term\n");
            writer2.flush();
            
            String Tn=Term();
            String EprimeI;
            String tmp=newTemp();
            emit(tmp+"="+str+"-"+Tn);
            
            int quad1 = getAddress(str);
            int quad2 = getAddress(Tn);
            int quad3 = getAddress(tmp);
            
            //use opcode 2 for -
            writer4.append("2\t" + quad1 + "\t" + quad2 + "\t" + quad3 + "\n");
            writer4.flush();
            
            EprimeI = tmp;
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("EPrime\n");
            writer2.flush();
            retValue=EPrime(EprimeI);
            --tab;
            
	}
	else
            return str;
        return retValue;
    }

    String F() throws IOException {
        ++tab;
        String retValue="";
	if (look.equals("ID"))
        {
                for (int i = 0; i < tab; i++) {
                    
                    writer2.append("----");
                    writer2.flush();
                }
                
                writer2.append(lexeme+"\n");
                writer2.flush();
                StringBuilder lex=new StringBuilder(lexeme);
                if(lex.charAt(0)=='"')
                {
                    lex.deleteCharAt(0);
                    lex.deleteCharAt(lex.length()-1);
                }
                retValue=lex.toString();
                match("ID");
                --tab;
        }
	else if (look.equals("NUM"))
        {
                for (int i = 0; i < tab; i++) {
                    
                    writer2.append("----");
                    writer2.flush();
                }
                
                writer2.append(lexeme+"\n");
                writer2.flush();
                StringBuilder lex=new StringBuilder(lexeme);
                if(lex.charAt(0)=='"')
                {
                    lex.deleteCharAt(0);
                    lex.deleteCharAt(lex.length()-1);
                }
                retValue=lex.toString();
                match("NUM");
                --tab;
                
                writer1.append(lex.toString() + "\tInteger\t" + address + "\n");
                writer1.flush();
                address += 4;
        }
	else if (look.equals("'('")) {
		match("'('");
                for (int i = 0; i < tab; i++) {
                    
                    writer2.append("----");
                    writer2.flush();
                }
               
                writer2.append("(\n");
                writer2.flush();
            
                for (int i = 0; i < tab; i++) {
                    
                    writer2.append("----");
                    writer2.flush();
                }
                
                writer2.append("Expression\n");
                writer2.flush();
                retValue=ExpressionS();
               
                match("')'");
                for (int i = 0; i < tab; i++) {
                    
                    writer2.append("----");
                    writer2.flush();
                }
               
                writer2.append(")\n");
                writer2.flush();
                --tab;
                
	}
	else
        {
            error=true;
            System.out.print("Bad token in what seemed like an arithemtic expression...");
            System.exit(1);
            --tab;
            
        }
        return retValue;
    }

    String TPrime(String str) throws IOException{
        String retValue="";
	if (look.equals("'*'")) {
            ++tab;
            match("'*'"); 
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("*\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
               
                writer2.append("----");
                writer2.flush();
            }
           
            writer2.append("F\n");
            writer2.flush();
            
            String Fn=F();
            String TprimeI;
            String tmp=newTemp();
            emit(tmp+"="+str+"*"+Fn);
            
            int quad1 = getAddress(str);
            int quad2 = getAddress(Fn);
            int quad3 = getAddress(tmp);
            
            //use opcode 3 for *
            writer4.append("3\t" + quad1 + "\t" + quad2 + "\t" + quad3 + "\n");
            writer4.flush();
            
            TprimeI = tmp;
          
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("TPrime\n");
            writer2.flush();
            retValue=TPrime(TprimeI);
            --tab;
            
	}
	else if (look.equals("'/'")) {
            ++tab;
            match("'/'"); 
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("/\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("F\n");
            writer2.flush();
            String Fn=F();
            String TprimeI;
            String tmp=newTemp();
            emit(tmp+"="+str+"/"+Fn);
            
            int quad1 = getAddress(str);
            int quad2 = getAddress(Fn);
            int quad3 = getAddress(tmp);
            
            //use opcode 4 for /
            writer4.append("4\t" + quad1 + "\t" + quad2 + "\t" + quad3 + "\n");
            writer4.flush();
            
            TprimeI = tmp;
          
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("TPrime\n");
            writer2.flush();
            retValue=TPrime(TprimeI);
            --tab;
	}
        else
            return str;
        return retValue;
    }
    
    void DeclarationS()
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
            
            writer2.append("----");
            writer2.flush();
        }
       
        writer2.append("Declaration\n");
        writer2.flush();
        Declaration();
        --tab;
    }
    
    void Declaration()
    {
        int inc=0;
        if(look.equals("char"))
        {
            ++tab;
            inc=1;
            match("char");dataType="char";
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("char\n");
            writer2.flush();
            
            match("':'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(":\n");
            writer2.flush();
            
            if(look.equals("ID"))
            {
                   StringBuilder id=new StringBuilder(identifierName);
                   id.deleteCharAt(0);id.deleteCharAt(id.length()-1);
                   identifierName=id.toString().trim();
                   writer1.append(identifierName+"\t"+dataType+"\t"+address+"\n");
                   writer1.flush();
                   identifierName=null;
                   address+=inc;
            }
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(lexeme+"\n");
            writer2.flush();
            match("ID");
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("G\n");
            writer2.flush();
            G(inc);
            --tab;
        }
        else if(look.equals("Integer"))
        {
            ++tab;
            inc=4;
            match("Integer");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("Integer\n");
            writer2.flush();
            dataType="Integer";
            
            match("':'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(":\n");
            writer2.flush();
            
            if(look.equals("ID"))
            {
                   StringBuilder id=new StringBuilder(identifierName);
                   id.deleteCharAt(0);id.deleteCharAt(id.length()-1);
                   identifierName=id.toString().trim();
                   writer1.append(identifierName+"\t"+dataType+"\t"+address+"\n");
                   writer1.flush();
                   identifierName=null;
                   address+=inc;
            }
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(lexeme+"\n");
            writer2.flush();
            match("ID");
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("G\n");
            writer2.flush();
            G(inc);
            --tab;
        }
        
    }
    
    void G(int inc)
    {
        ++tab;
        if(look.equals("','"))
        {
            match("','");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
           
            writer2.append(",\n");
            writer2.flush();
            
            if(look.equals("ID"))
            {
                   StringBuilder id=new StringBuilder(identifierName);
                   id.deleteCharAt(0);id.deleteCharAt(id.length()-1);
                   identifierName=id.toString();
                   writer1.append(identifierName+"\t"+dataType+"\t"+address+"\n");
                   writer1.flush();
                   identifierName=null;
                   address+=inc;
                   //dataType=null;
            }
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(lexeme+"\n");
            writer2.flush();
            match("ID");
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("G\n");
            writer2.flush();
            G(inc);
        }
        else if(look.equals("';'"))
        {    
            match("';'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
           
            writer2.append(";\n");
            writer2.flush();
            dataType=null;
        }
        else 
        {
            error=true;
            System.out.print("Bad token in what seemed like a declaration statement...");
            System.exit(1);
        }
       --tab;
    }
    //DONE
    void AssignmentS() throws IOException
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
        }
        
        writer2.append("Assignment\n");
        writer2.flush();
        Assignment();
        --tab;
    }
    //DONE
    void Assignment() throws IOException
    {
        ++tab;
        StringBuilder lex;
        if(look.equals("ID"))
        {
           lex=new StringBuilder(lexeme);
                if(lex.charAt(0)=='"')
                {
                    lex.deleteCharAt(0);
                    lex.deleteCharAt(lex.length()-1);
                }
                
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            
            writer2.append(lexeme+"\n");
            writer2.flush();
            match("ID");
            
            match("':='");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(":=\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            
            writer2.append("B\n");
            writer2.flush();
            
            String bVal = B();
            emit(lex.toString() + " = " + bVal);
            
            int quad1 = getAddress(lex.toString());
            int quad2 = getAddress(bVal);
            
            //use opcode 5 for =
            writer4.append("5\t" + quad2 + "\t" + quad1 + "\n");
            writer4.flush();
            
            match("';'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(";\n");
            writer2.flush();
        }
        --tab;
    }
    //DONE
    String B() throws IOException
    {
        ++tab;
        String retValue;
        if(look.equals("LC"))
        {
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(lexeme+"\n");
            writer2.flush();
            
            StringBuilder lex=new StringBuilder(lexeme);
            retValue=lex.toString().trim();
            match("LC");
            
            writer1.append(retValue+ "\tChar\t" + address + "\n");
            writer1.flush();
            address += 4;
        }
        else if(look.equals("NUM"))
        {
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(lexeme+"\n");
            writer2.flush();
            
            StringBuilder lex=new StringBuilder(lexeme);
            if(lex.charAt(0)=='"')
            {
                lex.deleteCharAt(0);
                lex.deleteCharAt(lex.length()-1);
            }
            retValue=lex.toString();
            match("NUM");
            --tab;
                
            writer1.append(lex.toString()+ "\tInteger\t" + address + "\n");
            writer1.flush();
            address += 4;
        }
        else if(look.equals("ID")||look.equals("'('"))
        {
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("Expression\n");
            writer2.flush();  
            retValue=ExpressionS();
            }
        else 
        {    
            error=true;
            System.out.print("Bad token in what seemed like assignment...");
            System.exit(1);
            retValue="error";
        }
        --tab;
        return retValue;
    }
    //DONE
    void ConditionalS() throws IOException
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
        }
        
        writer2.append("Conditional\n");
        writer2.flush();
        Conditional();
        --tab;
    }
    //DONE
    void Conditional() throws IOException
    {
        ++tab;
        if(look.equals("if"))
        {
            match("if");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("if\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("V\n");
            writer2.flush();
         
            int ifFalse = V("if ");
            backPatch(vTrue, n);
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(":\n");
            writer2.flush();
            match("':'");
            
            match("'{'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("{\n");
            writer2.flush();
           
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("Statement\n");
            writer2.flush();
            
            Statement();
            int ifNext = n;
            emit("goto ");
            writer4.append("12\t\n");
            writer4.flush();
            
            backPatch(ifFalse, n);
            
            match("'}'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("}\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("CPrime\n");
            writer2.flush();
            Cprime(ifNext, -1);
        }
        --tab;
    }
    //DONE
    int V(String keyword) throws IOException
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
        }
        writer2.append("O\n");
        writer2.flush();
        
        vTrue = n;
        String output = keyword;
        String op1 = O();
        output += op1;
        output += " ";
        
        for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
        }
        writer2.append(lexeme+"\n");
        writer2.flush();
        
        String RO = lexeme;
        output += RO;
        output += " ";
        match("RO");
            
        for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
        }
        writer2.append("O\n");
        writer2.flush();
        
        String op2 = O();
        output += op2;
        output += " goto " ;
        
        emit (output);
        int opcode = 0;
        
        if (RO.equals("EQ"))
            opcode = 6;
        else if (RO.equals("LT"))
            opcode = 7;
        else if (RO.equals("GT"))
            opcode = 8;
        else if (RO.equals("LE"))
            opcode = 9;
        else if (RO.equals("GE"))
            opcode = 10;
        else
        {
            System.out.print("Wrong relational operator given.");
            System.exit(0);
        }
        
        int quad1 = getAddress(op1);
        int quad2 = getAddress(op2);
        
        writer4.append(opcode + "\t" + quad1 + "\t" + quad2 + "\t" + "\n");
        writer4.flush();
            
        int retVal = n;
        emit ("goto ");
        writer4.append("12\t\n");
        writer4.flush();
        
        --tab;
        return retVal;
    }
    //DONE
    String O()
    {
        ++tab;
        if(look.equals("ID"))
        {
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(lexeme+"\n");
            writer2.flush();
            
            StringBuilder lex=new StringBuilder(lexeme);
            if(lex.charAt(0)=='"')
            {
                lex.deleteCharAt(0);
                lex.deleteCharAt(lex.length()-1);
            }
            String retVal=lex.toString();
            match("ID");
            return retVal;
        }
        else if(look.equals("LC"))
        {
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(lexeme+"\n");
            writer2.flush();
            
            StringBuilder lex=new StringBuilder(lexeme);
            if(lex.charAt(0)=='"')
            {
                lex.deleteCharAt(0);
                lex.deleteCharAt(lex.length()-1);
            }
            String retVal=lex.toString();
            
            match("LC");
            writer1.append(retVal+ "\tChar\t" + address + "\n");
            writer1.flush();
            address += 4;
            return retVal;
        }
        else if (look.equals("NUM"))
        {
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(lexeme+"\n");
            writer2.flush();
            
            StringBuilder lex=new StringBuilder(lexeme);
            if(lex.charAt(0)=='"')
            {
                lex.deleteCharAt(0);
                lex.deleteCharAt(lex.length()-1);
            }
            String retVal=lex.toString();
            
            match("NUM");
            writer1.append(retVal+ "\tInteger\t" + address + "\n");
            writer1.flush();
            address += 4;
            return retVal;
        }
        else
        {
            error=true;
            System.out.print("Bad token... at "+look+" in what seemed like a conditional statement");
            System.exit(1);
        }
        --tab;
        return null;
    }
    //Done
    void Cprime(int ifNext, int elifNext) throws IOException
    {
        ++tab;
        if(look.equals("elif"))
        {
            match("elif");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("elif\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("V\n");
            writer2.flush();
            
            int elifFalse = V("elif ");
            backPatch(vTrue, n);
            
            match("':'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(":\n");
            writer2.flush();
            
            match("'{'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("{\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("Statement\n");
            writer2.flush();
            
            Statement();
            elifNext = n;
            emit("goto ");
            
            writer4.append("12\t\n");
            writer4.flush();
            
            backPatch(elifFalse, n);
            
            match("'}'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("}\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("CPrime\n");
            writer2.flush();
            Cprime(ifNext, elifNext);
        }
        else if(look.equals("else"))
        {
            match("else");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("else\n");
            writer2.flush();
            
            match("'{'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("{\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("Statement\n");
            writer2.flush();
            
            Statement();
            backPatch(ifNext, n);
            if (elifNext > -1)
                backPatch(elifNext, n);
            
            match("'}'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("}\n");
            writer2.flush();
        }
        --tab;
    }
    
    void FunctionS() throws IOException
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
        }
        
        writer2.append("Function\n");
        writer2.flush();
        Function();
        --tab;
    }
    
    void Function() throws IOException
    {
        ++tab;
        if(look.equals("func"))
        {
            match("func");
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("Z\n");
            writer2.flush();
            Z();
            
            match("':'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(":\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(lexeme+"\n");
            writer2.flush();
            match("ID");
            
            match("'('");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("(\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("X\n");
            writer2.flush();
            X();
            
            match("')'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(")\n");
            writer2.flush();
            
            match("'{'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("{\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("Statement\n");
            writer2.flush();
            Statement();
            
            if(look.equals("ret"))
            {
                match("ret");
                for (int i = 0; i < tab; i++) {
                    
                writer2.append("----");
                writer2.flush();
                }
                
                writer2.append("ret\n");
                writer2.flush();
            
                for (int i = 0; i < tab; i++) {
                    
                writer2.append("----");
                writer2.flush();
                }
                
                writer2.append(lexeme+"\n");
                writer2.flush();
                match("ID");
                
                match("';'");
                for (int i = 0; i < tab; i++) {
                    
                writer2.append("----");
                writer2.flush();
                }
                
                writer2.append(";\n");
                writer2.flush();
            }
            
            match("'}'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("}\n");
            writer2.flush();
        }
        --tab;
    }
   
    void Z()
    {
        ++tab;
        if(look.equals("Integer"))
        {
            match("Integer");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
           
            writer2.append("Integer\n");
            writer2.flush();
        }
        else if(look.equals("char"))
        {
            match("char");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("char\n");
            writer2.flush();
        }
        else
        {
            error=true;
            System.out.print("Bad token at "+look+" ...in what seemed like a function header");
            System.exit(1);
        }
        --tab;
    }
    
    void X() //Parameter List
    {
        ++tab;
        if(!look.equals("')'"))
        {
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("Z\n");
            writer2.flush();
            Z();
            
            match("':'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(":\n");
            writer2.flush();
                
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(lexeme+"\n");
            writer2.flush();
            match("ID");
            
            if(look.equals("','"))
            {    
                match("','");
                for (int i = 0; i < tab; i++) {
                    
                writer2.append("----");
                writer2.flush();
                }
                
                writer2.append(",\n");
                writer2.flush();
                
                for (int i = 0; i < tab; i++) {
                   
                writer2.append("----");
                writer2.flush();
                }
                
                writer2.append("X\n");
                writer2.flush();
                X();
            }
        }
        --tab;
    }
    //DONE
    void OutputS() throws IOException
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
        }
        writer2.append("Output\n");
        writer2.flush();
        Output();
        --tab;
    }
    
    void Output() throws IOException
    {
        ++tab;
        if(look.equals("print"))
        {
            match("print");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("print\n");
            writer2.flush();
                    
            match("'('");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("(\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(lexeme+"\n");
            writer2.flush();
            
            StringBuilder lex=new StringBuilder(lexeme);
            if(lex.charAt(0)=='"')
            {
                lex.deleteCharAt(0);
                lex.deleteCharAt(lex.length()-1);
            }
            String retVal=lex.toString();
            
            emit ("print(" + retVal + ")");     
            writer4.append("14\t" + getAddress(retVal) + "\n");
            writer4.flush();
            
            match("ID");
            
            match("')'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(")\n");
            writer2.flush();
            
            match("';'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(";\n");
            writer2.flush();
        }
        else if(look.equals("println"))
        {
            match("println");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("println\n");
            writer2.flush();
            
            match("'('");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("(\n");
            writer2.flush();
            
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(lexeme+"\n");
            writer2.flush();
            //emit ("println(" + lexeme + ")");
            
            StringBuilder lex=new StringBuilder(lexeme);
            if(lex.charAt(0)=='"')
            {
                lex.deleteCharAt(0);
                lex.deleteCharAt(lex.length()-1);
            }
            String retVal=lex.toString();
            
            emit ("println(" + retVal + ")");     
            writer4.append("15\t" + getAddress(retVal) + "\n");
            writer4.flush();
            
            match("ID");
            
            match("')'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(")\n");
            writer2.flush();
            
            match("';'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(";\n");
            writer2.flush();
        }
        else if(look.equals("write"))
        {
            match("write");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("write\n");
            writer2.flush();
            
            match("'('");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append("(\n");
            writer2.flush();
            
            
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(lexeme+"\n");
            writer2.flush();
            //emit ("print(" + lexeme + ")");
            
            StringBuilder lex=new StringBuilder(lexeme);
            if(lex.charAt(0)=='"')
            {
                lex.deleteCharAt(0);
                lex.deleteCharAt(lex.length()-1);
            }
            String retVal=lex.toString();
            
            emit ("write(" + retVal + ")");     
            writer4.append("16\t" + getAddress(retVal) + "\n");
            writer4.flush();
            
            match("ID");
            
            match("')'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(")\n");
            writer2.flush();
            
            match("';'");
            for (int i = 0; i < tab; i++) {
                
                writer2.append("----");
                writer2.flush();
            }
            
            writer2.append(";\n");
            writer2.flush();
        }
        --tab;
    }
    //DONE
    void LoopS() throws IOException
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
        }
        writer2.append("loop\n");
        writer2.flush();
        Loop();
        --tab;
    }
    //DONE
    void Loop() throws IOException
    {
        ++tab;
        if(look.equals("while"))
        {
            match("while");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("while\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("V\n");
            writer2.flush();
            
            int whileStart = n+1;
            int whileFalse = V("while ");
            backPatch(vTrue, n);
            
            match("':'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(":\n");
            writer2.flush();
            
            match("'{'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("{\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("Statement\n");
            writer2.flush();
            
            Statement();
            emit("goto " + whileStart);
            writer4.append("12\t" + whileStart + "\n");
            writer4.flush();
            backPatch(whileFalse,n);
            
            match("'}'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("}\n");
            writer2.flush();
        }
        --tab;
    }
    //DONE
    void InputS() throws IOException
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
        }
        writer2.append("Input\n");
        writer2.flush();
        Input();
        --tab;
    }
    //DONE   
    void Input() throws IOException
    {
        ++tab;
        if(look.equals("In"))
        {
            match("In");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("In\n");
            writer2.flush();
            
            match(">>");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(">>\n");
            writer2.flush();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(lexeme+"\n");
            writer2.flush();
            
            StringBuilder lex=new StringBuilder(lexeme);
            if(lex.charAt(0)=='"')
            {
                lex.deleteCharAt(0);
                lex.deleteCharAt(lex.length()-1);
            }
            String retVal=lex.toString();
            
            emit ("In >> " + retVal);
            writer4.append("13\t" + getAddress(retVal) + "\n");
            writer4.flush();
            
            match("ID");
            
            match("';'");
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(";\n");
            writer2.flush();
        }
        --tab;
    }
      
    void CommentS()
    {
        ++tab;
        for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
        }
        writer2.append("Comment\n");
        writer2.flush();
        Comment();
        --tab;
    }
    
    void Comment()
    {
        ++tab;
        if(look.equals("COM"))
        {
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append(lexeme+"\n");
            writer2.flush();
            match("COM");
        }
        --tab;
    }
    
    void Statement() throws IOException
    {
        ++tab;
        if(look!=null && !look.equals("'}'") && !look.equals("ret"))
        {
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("Q\n");
            writer2.flush();
            Q();
            
            for (int i = 0; i < tab; i++) {
                writer2.append("----");
                writer2.flush();
            }
            writer2.append("Statement\n");
            writer2.flush();
            Statement();
        }    
        --tab;
    }
    
    void Q() throws IOException
    {
        if(look==null)
        {
            System.out.print("Bad token!Last statement is incomplete");
            System.exit(1);
        }
        if(look.equals("Integer")||look.equals("char"))
        {
            DeclarationS();
        }
        else if(look.equals("ID"))
        {
            AssignmentS();
        }
        else if(look.equals("func"))
        {
            FunctionS();
        }
        else if(look.equals("if"))
        {
            ConditionalS();
        }
        else if(look.equals("print")||look.equals("println")||look.equals("write")) 
        {
            OutputS();
        }
        else if(look.equals("while"))
        {
            LoopS();
        }
        else if(look.equals("In"))
        {
            InputS();
        }
        else if(look.equals("COM"))
        {
            CommentS();
        }
        else
        {
            error=true;
            System.out.print("Syntax Error!Unrecognized token..."+look);
            System.exit(1);
        }
    }
    
    void match(String tok) {
        if(look==null)
        {
            error=true;
            System.out.print("EOF ecountered when "+tok+" expected");
            System.exit(1);
        }
	if (look.equals(tok))
        {
            look = nextTok();
        }
	else
        {
            error=true;
            System.out.print("EOF ecountered when "+tok+" expected");
            System.exit(1);
        }
    }
    
    void runParser()
    {
        try {
            br = new BufferedReader(new FileReader("words.txt"));
            writer1 = new PrintWriter(new FileWriter("parser-symboltable.txt",true));
            writer2 = new PrintWriter(new FileWriter("parseTree.txt",true));
            writer3 = new PrintWriter(new FileWriter("tac.txt",true));
            writer4 = new PrintWriter(new FileWriter("machine-code.txt",true));
            
            look=nextTok();
            
            writer2.append("Statement\n");
            writer2.flush();
            Statement();
            
            backPatchMC();

        } catch (Exception ex) {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    String nextTok()
    {
        try{
            String str=br.readLine();
            boolean multilineComment=false;
            //Check to make sure the line read isn't an error statement 
            
            if(str!=null){
            //For Multiline comments
            lexeme="";
            while((str.charAt(str.length()-1)!=')'))
            {
                if(multilineComment==false){
                    String tokens[]=str.split(" , ");
                    StringBuffer sb2=new StringBuffer(tokens[1]);
                    lexeme=lexeme+sb2.toString().trim()+"\n";}
                else
                    lexeme=lexeme+str+"\n";
                str=br.readLine();
                multilineComment=true;
            }
            if (multilineComment==true)
            {
                StringBuffer sb2=new StringBuffer(str);
                sb2.deleteCharAt(sb2.length()-1);
                lexeme=lexeme+sb2.toString().trim();
                return("COM");
            }
            //Extracting the token from the line read
            String tokens[]=str.split(" , ");
            StringBuilder sb = new StringBuilder(tokens[0]);
            sb.deleteCharAt(0);
            
            if(sb.toString().trim().equals("ID"))
            {
                StringBuffer sb2=new StringBuffer(tokens[1]);
                sb2.deleteCharAt(sb2.length()-1);
                identifierName=sb2.toString().trim();
                lexeme=sb2.toString().trim();
            }
            else if(sb.toString().trim().equals("COM") || sb.toString().trim().equals("LC") || sb.toString().trim().equals("NUM") || sb.toString().trim().equals("LC") || sb.toString().trim().equals("STR") || sb.toString().trim().equals("RO"))
            {
                StringBuffer sb2=new StringBuffer(tokens[1]);
                sb2.deleteCharAt(sb2.length()-1);
                lexeme=sb2.toString().trim();
       
            }
            return sb.toString().trim(); 
            }
        }
        catch(Exception e) 
        {
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, e);
        }
        return null;
    }
    
}
