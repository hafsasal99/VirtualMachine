
package vm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class LexicalAnalyzer {
    String token;
    String lexeme;
    boolean isMComment=false;
    ArrayList<String> keywords;
    int index=0;
    StringBuilder comment;
    LexicalAnalyzer()
    {
        keywords=new ArrayList<String>();
        keywords.add("if");
        keywords.add("elif");
        keywords.add("else");
        keywords.add("while");
        keywords.add("Integer");
        keywords.add("char");
        keywords.add("In");
        keywords.add("func");
        keywords.add("print");
        keywords.add("write");
        keywords.add("println");
        keywords.add("println");
        keywords.add("ret");
        
    }
    
    boolean isKeyword(String s)
    {
        int endingIndex=s.length();
        for(int x=index;x<s.length();x++)
        {
            if(!isLetter(s.charAt(x)))
            {
                endingIndex=x;
                break;
            }
        }
        for(int x=0;x<keywords.size();x++) //no case sensitivity for keywords
        {
            if(keywords.get(x).equalsIgnoreCase(s.substring(index,endingIndex)))
            {
                     token=s.substring(index,endingIndex);
            lexeme="^";
            index=endingIndex;
            return true;
            }
        }
//        if(keywords.contains(s.substring(index,endingIndex)))
//        {   
//            token=s.substring(index,endingIndex);
//            lexeme="^";
//            index=endingIndex;
//            return true;
//        }
        return false;
    }
    
    boolean isSpace(String s)
    {
        for(int x=index;x<s.length();x++)
        {
            if(s.charAt(index)!=' ')
                return false;
            else
                index++;
        }
        return true;
    }
    
    boolean isLetter(char c)
    {
        if((c>=65 && c<=90) || (c>=97 && c<=122))
            return true;
        else 
            return false;
    }
    
    boolean isIdentifier(String s)
    {
        int state=1;
        for(int x=index;x<s.length();x++)
        {
            switch(state)
            {
                case 1:
                    if(isLetter(s.charAt(x)))
                    {
                        state=2;
                    }
                    else 
                    {
                        return false;
                    }
                    break;  
                case 2:
                    if(!(isLetter(s.charAt(x))) && !isDigit(s.charAt(x)))
                    {
                        token="ID";
                        s=s.substring(index,x);
                        index=x;
                        lexeme="\""+s+"\"";
                        return true;
                    }
                    break;
            }

        }
        if(state==2)
        {
            token="ID";
            String str=s.substring(index);
            index=s.length();
            lexeme="\""+str+"\"";
            return true;
        }
        return false;
    }
    
    boolean isRO(String s)
    {
        int state=1;
        for(int x=index;x<=s.length();x++)
        {
            switch(state)
            {
                case 1:
                    if(s.charAt(x)=='>')
                        state = 2;
                    else if (s.charAt(x)=='<')
                        state = 5;
                    else if (s.charAt(x)=='/')
                        state = 8;
                     else if (s.charAt(x)=='=')
                     {
                         state=9;
                     }
                    else
                        return false;
                    break;  
                case 2:
                    if(s.charAt(x)=='=')
                    {
                        token="RO";
                        lexeme="GE";
                        index+=2;
                    }
                    else
                    {
                        token="RO";
                        lexeme="GT";
                        index+=1;
                    }
                    return true;
                case 5:
                   if(s.charAt(x)=='=')
                    {
                        token="RO";
                        lexeme="LE";
                        index+=2;
                    }
                    else
                    {
                        token="RO";
                        lexeme="LT";
                        index+=1;
                    }
                    return true;
                case 8:
                    if(s.charAt(x)=='=')
                    {
                        token="RO";
                        lexeme="NE";
                        index+=2;
                        return true;
                    }
                    else
                        return false;
                    
                    case 9:
                    if(s.charAt(x)=='=')
                    {
                        token="RO";
                        lexeme="EQ";
                        index+=2;
                        return true;
                    }
                    else
                        return false;
            }
        }
        return false;
    }
    
     boolean isAO(String s)
    {
        int state=1;
        for(int x=index;x<=s.length();x++)
        {
            switch(state)
            {
                case 1:
                    if(s.charAt(x)=='+')
                        state=2;
                    else if (s.charAt(x)=='-')
                        state = 4;
                    else if (s.charAt(x)=='*')
                        state = 6;
                     else if (s.charAt(x)=='/')
                        state = 8;
                    else
                        return false;
                    break;
                case 2:
                    token="'+'";
                    lexeme="^";
                    index++;
                    return true;
                case 4:
                    token="'-'";
                    lexeme="^";
                    index++;
                    return true;
                case 6:
                    token="'*'";
                    lexeme="^";
                    index++;
                    return true;
                case 8:
                    token="'/'";
                    lexeme="^";
                    index++;
                    return true;
                    
            }
        }
        return false;
    }  
     
    boolean isComment(String s)
    {
        int state=1;     
        for(int x=index;x<=s.length();x++)
        {
            switch(state)
            {
                case 1:
                    if(s.charAt(x) == '/')
                        state=2;
                    else if(isMComment==true)
                        state=3;
                    else 
                        return false;
                    break;  
                case 2:
                    if(s.charAt(x) == '*')
                        state=3;
                    else 
                        return false;
                    break;  
                case 3:
                    if(x==s.length())
                        break;
                    if(s.charAt(x) == '*')
                        state = 4;
                    break;
                case 4:
                    if(x==s.length())
                        break;
                    if(s.charAt(x) == '/')
                        state=5;
                    else 
                        state = 3;
                    break;
                case 5:
                    token="COM";
                    s=s.substring(index,x);
                    index=x;
                    if(isMComment==true)
                    {   
                        lexeme="\""+comment+s+"\"";
                        comment=null;
                    }
                    else
                        lexeme="\""+s+"\"";
                    isMComment=false;
                    return true;
            }
        }
        if(comment==null)
            comment=new StringBuilder(s.substring(index)+"\n");
        else
            comment.append(s.substring(index)+"\n");
        isMComment=true;
        return false;
    }
    
    boolean isNumConst(String s)
    {
        int state=1;     
        
        for(int x=index;x<=s.length();x++)
        {
            switch(state)
            {
                case 1:
                    if (isDigit(s.charAt(x)))
                        state=2;
                    else if (s.charAt(x) == '+' || s.charAt(x) == '-' )
                        state=3;
                    else
                        return false;
                    break;  
                case 2:
                    if(x==s.length())
                    {
                        token="NUM";
                        s=s.substring(index,x);
                        lexeme=s;
                        index=x;
                        return true;
                    }
                    if ( !(isDigit(s.charAt(x))))
                        state=4;
                    break;  
                case 3:
                    if (x==s.length() || isDigit(s.charAt(x)))
                        state = 2;
                    else
                        return false;
                    break;
                case 4:
                    token="NUM";
                    s=s.substring(index,x-1);
                    index=x-1;
                    lexeme=s;
                    return true;
            }
        }
        return false;
    }
    
    boolean isLitConst(String s)
    {
        int state=1;     
        
        for(int x=index;x<=s.length();x++)
        {
            switch(state)
            {
                case 1:
                    if (s.charAt(x) == '\'')
                    {
                        state=2;
                    }
                    else
                        return false;
                    break;  
                case 2:
                    if (isLetter(s.charAt(x)))
                    {
                        state=3;
                    }
                    else
                        return false;
                    break;  
                case 3:
                    if (s.charAt(x) == '\'')
                        state = 4;
                    else
                        return false;
                    break;
                case 4:
                    token="LC";
                    lexeme="\'" + s.charAt(index+1) + "\'";
                    index = x;
                    return true;
            }
        }
        return false;
    }
    
    boolean isBracket(String s)
    {
       if(s.charAt(index)=='(' ||s.charAt(index)==')' ||s.charAt(index)=='{' || s.charAt(index)=='}' ||s.charAt(index)=='[' ||s.charAt(index)==']' )
        {
            token="'"+s.charAt(index)+"'";
            lexeme="^";
            index++;
            return true;
        }
        return false;
    }
    
    boolean isString(String s)
    {
        int state=1;
        for(int x=index;x<=s.length();x++)
        {
            switch(state)
            {
                case 1:
                    if(s.charAt(x)=='"')
                        state=2;
                    else
                        return false;
                    break;
                case 2:
                    if(s.charAt(x)=='"')
                    {
                        token="STR";
                        lexeme=s.substring(index,x+1);
                        index=x+1;
                        return true;
                    }
                    break;
                    
            }
        }
        return false;
    }
    
    boolean isAssignmentOperator(String s)
    {
        if(index+1<s.length())
                if(s.charAt(index)==':' && s.charAt(index+1)=='=')
                {
            token="':='";
            lexeme="^";
            index+=2;
            return true;
            }
        
        return false;
    }
    
    boolean isInputOp(String s)
    {
        if(index+1 < s.length())
                if(s.charAt(index)=='>' && s.charAt(index+1)=='>')
                {
                    token=">>";
                    lexeme="^";
                    index+=2;
                    return true;
                }
        
        return false;
    }
    
    boolean isVarDeclaration(String s)
    {
        if(s.charAt(index)==':')
        {
            token="':'";
            lexeme="^";
            index++;
            return true;
        }
        return false;
    }
    
    boolean isDigit(char c)
    {
        if(c>=48 && c<=57)
            return true;
        else 
            return false;
    }
    
    boolean isComma_Colon(String s)
    {
        if(s.charAt(index)==':' ||s.charAt(index)==';' ||s.charAt(index)==',' )
        {
            token="'"+s.charAt(index)+"'";
            lexeme="^";
            index++;
            return true;
        }
        return false;
    }
    
    String returnTokenLexemePair()
    {
        return "("+token+" , "+lexeme+")";
    }
    
    void init()
    {
        token=null;
        lexeme=null;
    }
    public boolean runLex(String filePath)
    {
        // file reading code
        try{
           BufferedReader br = new BufferedReader(new FileReader(filePath));
           PrintWriter writer1 = new PrintWriter(new FileWriter("words.txt",true));  
	   String contentLine = br.readLine();
	   while (contentLine != null) {
              index=0;
              while(index<contentLine.length()){
              isSpace(contentLine);
              char first=contentLine.charAt(index);
              boolean error=false;
              
              if(isLetter(first))
              {
                  if(!isKeyword(contentLine))
                  {   
                     if(!isIdentifier(contentLine))
                     {
                         error=true;
                         System.out.println("Invalid character encountered at " +contentLine.charAt(index));
                         break;
                     }
               
                  }
                      
              }  
              else if(isDigit(first))
              {
                  if(!isNumConst(contentLine))
                      {
                          error = true;
                          System.out.println("Invalid character encountered: " +contentLine.charAt(index));
                          break;
                      }
              }
              else 
              {
                  boolean input=false;
                  if(first=='>' ||first=='<'||first=='=')
                  {
                      if(first=='>')
                      {
                          if(isInputOp(contentLine)){
//                              returnTokenLexemePair();
//                                init();
//                                isSpace(contentLine); 
                                input=true;
                          }
                      }
                      if(input==false){
                      if(!isRO(contentLine))
                      {
                          error = true;
                          System.out.println("Invalid character encountered: " +contentLine.charAt(index));
                          
                      }}
                  }
                  else if(first=='+'|| first=='-' || first=='*' )
                  {
                      if(!isAO(contentLine))
                      {
                          error = true;
                          System.out.println("Invalid character encountered: " +contentLine.charAt(index));
                          
                      }
                  }
                  else if(first=='('|| first==')' || first=='{' || first=='}'|| first=='[' || first==']' )
                  {
                      if(!isBracket(contentLine))
                      {
                          error = true;
                          System.out.println("Invalid character encountered: " +contentLine.charAt(index));
                          
                      }
                  }
                  else if (first == '/')
                  {
                      if(!isRO(contentLine))
                      {
                          if(!isComment(contentLine))
                          {
                                if(!isAO(contentLine)){
                                error = true;
                                System.out.println("Invalid character encountered: " +contentLine.charAt(index));
                                
                            }
                          }
                      }
                  }
                  else if(first=='\'')
                  {
                      
                      if(!isLitConst(contentLine))
                      {
                                error = true;
                                System.out.println("Invalid character encountered: " +contentLine.charAt(index));
                                
                      }
                  }
                  else if(first=='"')
                  {
                      if(!isString(contentLine))
                      {
                          error = true;
                          System.out.println("Invalid character encountered: " +contentLine.charAt(index));
                          
                      }
                  }
                  else if(first==':')
                  {
                      if(!isAssignmentOperator(contentLine))
                      {
                          if(!isVarDeclaration(contentLine) && !isComma_Colon(contentLine)){
                          error = true;
                          System.out.println("Invalid character encountered: " +contentLine.charAt(index));
                          
                          }
                      }
                  }
                  else
                  {
                      if(!isComma_Colon(contentLine))
                      {
                          error = true;
                          System.out.println("Invalid character encountered: " +contentLine.charAt(index));
                          
                      }
                  }
              }
                  
               if(isMComment==true)
               {
                   contentLine = br.readLine();
                   while(isMComment==true && contentLine!=null)
                   {
                       isComment(contentLine);
                       index=0;
                       contentLine = br.readLine();
                   }
                   if(contentLine==null)
                       System.out.println("Error!Comment started but never closed.");
               }
               if(error==false && isMComment==false)
               {
                   
                   writer1.append(returnTokenLexemePair()+"\n");
                   writer1.flush();
                   init();
  
               }
               if(error==true)
                   return false;
               isSpace(contentLine);
              } 
              //next line
	      contentLine = br.readLine();
	   } 
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.exit(-1);
        }
       return true;
    }
    
}
