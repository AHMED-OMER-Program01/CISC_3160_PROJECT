package ciscProgLangProjectRewrite_1;


/*-----------------------------------------

Project

    The following defines a simple language, in which a program consists 
    of assignments and each variable is assumed to be of the integer type. 
    For the sake of simplicity, only operators that give integer values are 
    included. Write an interpreter for the language in a language of your 
    choice. Your interpreter should be able to do the following for a given 
    program: (1) detect syntax errors; (2) report uninitialized variables; 
    and (3) perform the assignments if there is no error and print out the 
    values of all the variables after all the assignments are done.

    Program: Assignment*
	    Assignment:
	    	Identifier = Exp;

		Exp: 
			Exp + Term | Exp - Term | Term

		Term:
			Term * Fact  | Fact

		Fact:
			( Exp ) | - Fact | + Fact | Literal | Identifier

		Identifier:
			Letter [Letter | Digit]*

		Letter:
			a|...|z|A|...|Z|_

		Literal:
			0 | NonZeroDigit Digit*

		NonZeroDigit:
			1|...|9

		Digit:
			0|1|...|9

		Sample inputs and outputs
			Input 1
				x = 001;
			Output 1
				error

			Input 2
				x_2 = 0;
			Output 2
				x_2 = 0

			Input 3
				x = 0
				y = x;
				z = ---(x+y);
			Output 3
				error

			Input 4
				x = 1;
				y = 2;
				z = ---(x+y)*(x+-y);
			Output 4	
				x = 1
				y = 2
				z = 3


 -----------------------------------------*/





public class num1
{

	public static void main(String[] args) 
	{
		Parser parser1 = new Parser();
		Parser parser2 = new Parser();
		String strTemp1;
		String strTemp2;
		//strTemp = "x = 001";//correctly errors

		//strTemp = "x_2 = 0;";//correctly is readable

		//strTemp = "x = 0\n"
		//		+ "y = x;\n"
		//		+ "z = ---(x+y);";//correctly errors

		strTemp1 = "x = 1;\n"
				+ "y = 2;\n"
				+ "z = ---(x+y)*(x+-y);";//correctly is readable
		
		parser1.str = strTemp1;
		parser1.curIndex  = 0;
		parser1.AssignmentSynCheck();
		System.out.print("------------\nvalid syntax\n------------\n");
		parser1.contentRead();
		
		/*---------Personal test parse example
		strTemp2 = "x = 3*(5*3);\n" 
				+ "y = x + 1;\n" 
				+ "z = -(y + 3)*(y + x);";
				
		parser2.str = strTemp2;
		parser2.curIndex  = 0;
		parser2.AssignmentSynCheck();
		System.out.print("\n------------\nvalid syntax\n------------\n");
		parser2.contentRead();
		*/
		
		
	}
}


class Parser {
	static public String str;
	static public int curIndex;
	/*
	AssignmentSynCheck:		Identifier = Exp;

	Exp:			Exp + Term | Exp - Term | Term

	Term:			Term * Fact  | Fact

	Fact:			( Exp ) | - Fact | + Fact | Literal | Identifier

	Identifier:		Letter [Letter | Digit]*

	Letter:			a|...|z|A|...|Z|_

	Literal:		0 | NonZeroDigit Digit*

	NonZeroDigit:	1|...|9

	Digit:			0|1|...|9
	 */

	static char currentToken()
	{
		if (curIndex >= str.length())
		{
			return '$';
		} 
		else 
		{
			//System.out.print("\n" + str.charAt(curIndex));
			return str.charAt(curIndex);
		}
	}

	static void readNextToken()
	{

		curIndex++;
	}

	void AssignmentSynCheck()
	{//Assignment:		Identifier = Exp;
		Identifier();

		char token = currentToken();

		while(token == ' ')
		{
			readNextToken();
			token = currentToken();
		}

		if(token == ';')
			errorUnitalizedVariable();

		if(token == '=')
		{
			readNextToken();
			token = currentToken();
		}
		else
			errorSyntax();

		while(token == ' ')
		{
			readNextToken();
			token = currentToken();
		}

		Exp();
		token = currentToken();

		if((token == '\n'))
		{
			readNextToken();
			token = currentToken();
		}

		if((token != '$') && (token == ';'))
		{
			readNextToken();
			token = currentToken();
			while(((token == ' ') || (token == '\n')) && (token != '$'))
			{
				readNextToken();
				token = currentToken();
			}
			if(token != '$')
				AssignmentSynCheck();

			return;
		}
		else
			errorSyntax();
	}




	static void Exp()
	{//Exp:			Exp + Term | Exp - Term | Term
		Term();
		char token = currentToken();
		if(token != '$')
		{
			if(token == ' ')
			{
				readNextToken();
				token = currentToken();
			}

			switch(token)
			{
			case '+':
			case '-':
				readNextToken();
				token = currentToken();
				if(token == ' ')
					readNextToken();
				Term();
				break;
			}
		}
		
		if(token == '=')
			errorSyntax();

		return;

	}



	static void Term()
	{//Term:			Term * Fact  | Fact
		Fact();
		char token = currentToken();

		if(token != '$')
		{
			if(token == ' ')
			{
				readNextToken();
				token = currentToken();
			}

			if(token == '*')
			{
				readNextToken();
				token = currentToken();
				if(token == ' ')
					readNextToken();
				Fact();
			}
		}

		return;
	}

	static void Fact()
	{//Fact:			( Exp ) | - Fact | + Fact | Literal | Identifier
		char token = currentToken();
		if(token == ' ')
			readNextToken();
		switch(token)
		{
		case '(':
			readNextToken();
			token = currentToken();
			if(token == ' ')
				readNextToken();
			Exp();
			token = currentToken();
			if( token != ')')
				errorSyntax();
			readNextToken();
			break;
		case '-':
		case '+':
			readNextToken();
			token = currentToken();
			if(token == ' ')
				readNextToken();
			Fact();
			break;
		default:
			if(Character.isDigit(token))
				Literal();
			else if(Character.isLetter(token))
				Identifier();
			break; 
		}
		return;
	}


	static void Literal()
	{
		char token = currentToken();
		if(token == '0')
		{
			readNextToken();
			token = currentToken();
			if(Character.isDigit(token))
				errorSyntax();
		}
		while(true)
		{	 
			if(Character.isDigit(token))
			{
				readNextToken();
				token = currentToken();
			}
			else
				return;
		}		 
	}


	static void Identifier()
	{
		char token = currentToken();
		if(Character.isLetter(token))
		{
			readNextToken();
			token = currentToken();
			while((Character.isLetter(token)) || 
					(Character.isDigit(token)) ||
					(token == '_'))
			{	 
				readNextToken();
				token = currentToken();
			}
			return;
		}
		else if ( token != '$')
		{
			//System.out.print("\n------------\n" + token + "\n------------\n");
			errorSyntax();
		}
	}
	
	
	static void error(String message)
	{	throw new RuntimeException("\nERROR:[" + message + "]\n");	}
	
	static void errorUknown()
	{	throw new RuntimeException("\nERROR:[UNKOWN ERROR]\n");	}
	
	static void errorSyntax()
	{
		System.out.print("\n" + currentToken() + "\n");
		throw new RuntimeException("\nERROR:[SYNTAX]\n");
	}

	static void errorUnitalizedVariable()
	{	throw new RuntimeException("\nERROR:[UNITNITALIZED VARIABLE]\n");	}
	
	static void errorCalculation()
	{	throw new RuntimeException("\nERROR:[IN EVALUATION OF EXPRESSION]\n");	}

	
	
	
	void contentRead()
	{
		String tempString = str;
		tempString = tempString.replace(" ", "");
		tempString = tempString.replace("\n", "");
		String linesArray[] = tempString.split(";");
		String variableArray[][] = new String[(linesArray.length)][2];
		
		for(int i = 0; i < linesArray.length ; i++ )
		{
			System.out.print(linesArray[i] + ";\n");
		}
		System.out.print("------------\nTotal lines seen: [" 
				+ linesArray.length + "]");

		
		for(int i = 0; i < variableArray.length ; i++ )
		{
			variableArray[i] = linesArray[i].split("=");
			//System.out.print("\n" + variableArray[i][0] + "\t" + variableArray[i][1]);
		}
		
		//System.out.print("\n------------");
		//for(int i = 0; i < variableArray.length ; i++ )
		//{
		//	System.out.print("\n" + variableArray[i][0] + "\t" + variableArray[i][1]);
		//}
		System.out.print("\n------------");
		for(int i = 0; i < variableArray.length ; i++ )
		{
			//System.out.print("\n" + i + "\t" + variableArray[i][1]);
			variableArray[i][1] = String.valueOf(Parser.stackCalc((variableArray[i][1])));
			tempString = (variableArray[i][1]);
			
			//replacelength = (variableArray[i][0]).length();
			for(int j = i; j < variableArray.length; j++)
			{
				variableArray[j][1] = (variableArray[j][1]).replace((variableArray[i][0]).trim(), tempString);
			}
		}
		//System.out.print("\n------------");
		for(int i = 0; i < variableArray.length ; i++ )
		{
			System.out.print("\n" + variableArray[i][0] + " = " + variableArray[i][1]);
		}
		System.out.print("\n------------");
		
		
		//use linesArray.length as an absolute worst case?
		//Utterly rancid, but Vectors won't work for some reason?
		//Machine issue?? Java Gods just hate me?
		//I dunno, meh. Regardless; Arrays. Hurray.
		//Ugh.
		//int 
		
		//System.out.print("\n\n\n@@@@@@@@@@@@@@@@@\n\n\n");
		//int gfrejf = stackCalc((variableArray[0][1]));
		//System.out.print("\n\n@@@@@@@@@@@@@@@@@\n\n" + (variableArray[0][1]) + "\n\n@@@@@@@@@@@@@@@@@\n\n");
		//System.out.print("\n\n@@@@@@@@@@@@@@@@@\n\n" + gfrejf + "\n\n@@@@@@@@@@@@@@@@@\n\n");
		
		
	}
	
	static int stackCalc(String equation)
	{
		return(stackCalc(equation, 0));
	}
	
	static int stackCalc(String equation, int recursion)
	{
		
		int sum = 0;
		int currentNumber = 0;
		char currentChar;
		
		int numberInNumberStack = 0;
		int numberInOperationStack = 0;
		int numberStack[] = new int[(equation.length())];//size is worst-case
		char operationStack[] = new char[(equation.length())];//size is worst-case
		//System.out.print("\neeeeeeeeeeeeeeee\n" +  equation + "\neeeeeeeeeeeeeeee\n");
		for(int i = 0; i < equation.length(); i++)
		{
			currentChar = equation.charAt(i);

			if (Character.isDigit(currentChar)) 
            {
            	currentNumber = Integer.parseInt(String.valueOf(currentChar));
            	if( (i+1) < equation.length() )
            	{
            		i++;
            		//currentChar = equation.charAt(i);
	                while((i < equation.length()) && (Character.isDigit(equation.charAt(i))))
	                {
	                	//currentChar = equation.charAt(i);
	                	currentNumber = currentNumber * 10 + Integer.parseInt(String.valueOf(equation.charAt(i)));
	                    i++;
	                }
	                i--;
            	}
                currentChar = equation.charAt(i);
                numberStack[numberInNumberStack] = currentNumber;
                numberInNumberStack++;
				//for(int g = 0; g <numberInNumberStack; g++)
				//{	System.out.print("\n" +  numberStack[g] + "\tn");	}
				//System.out.print("\n\n");
				//System.out.print("\n" + numberInNumberStack + "\n\n");
            }
			else if(currentChar == '(')
			{//OKAY SO;
				//WHILE THIS *TECHNICALLY* WORKS
				//IT'S SCUFFED TO ALL HELL
				//TLDR;
				//Issues correcting placement of [i] after recursion;
				//	Am thinking of just biting the bullet and making
				//the assignment call a function that's int-less,
				//and making this a 'helper' function, with a passing
				//index variable, and a multi-faceted return,
				//or something egregous like that ( lol ).
				
				//DOES calc all examples well; but handles multiple
				//nesting parenthesises like doo-doo
				//Which is the most confusing thing so far
				//since it should be one of the easiest bits??
				//Just pass it???
				
				//GAH, whatever; this is only the first night;
				//I have weeks.
				
				//int temp = (equation.substring(i + 1));
				//System.out.print("\n\n\nggggggggg\n" +  equation.substring(i + 1) + "\nggggggggg\n\n\n");
				String strTemp = equation.substring((i+1));
				//strTemp = (strTemp.substring(1, (strTemp).indexOf(")") + 1));
				//System.out.print("\n\n\nddddddddddd\n" +  strTemp + "\nddddddddddd\n\n\n");
				//System.out.print("\n\n\nsssss\n" +  strTemp + "\nsssss\n\n\n");
				
				currentNumber = stackCalc(strTemp,( recursion + 1 ));
				equation = equation.substring((strTemp.indexOf(')') + 1));
				if( equation.isEmpty())
					break;
				//else
				//	i = i + 1;
				
				
				//System.out.print("\n\n\ncalccalc\n" +  currentNumber + "\ncalccalc\n");
				numberStack[numberInNumberStack] = currentNumber;
				//System.out.print("\n????\n" +  numberStack[numberInNumberStack] 
				//		+ "\n" + numberInNumberStack
				//		+ "\n????\n\n\n");
				numberInNumberStack++;
				//for(int g = 0; g <numberInNumberStack; g++)
				//{	System.out.print("\n" +  numberStack[g] + "\ty");	}
			}
			else if(currentChar == ')')
			{
				//System.out.print("\n\n\nzzzzz\n" +  equation 
				//		+"\n" + equation.charAt((i-1)) + "\nzzzzz\n\n\n");
				break;
			}
            else 
            {
            	switch(currentChar)
            	{//PEMDAS //Parenthesis case is handled in the number section above
            		//case '^':
            		case '*':
            		//case '/':
            		case '+':
            		case '-':
            			operationStack[numberInOperationStack] = currentChar;
            			numberInOperationStack++;
            			//System.out.print("\nopopop\n" +  currentChar + "\nopopop\n");
            			break;
            		default:
            			break;
            	}
            }
		}
		
		/*
		for(int i = 0; i < numberInNumberStack; i++)
		{
			System.out.print("\nh\n" + numberStack[i] + "\nh\n");
		}
		*/
		
		
		while((numberInOperationStack > 0) && (numberInNumberStack > 0))
		{
			if((numberInNumberStack >= 2) && (numberInOperationStack >= 1))
			{
				//System.out.print("\n1hmhmhmhhmh\n" + sum + "\nmmhmhmmmhmhmmmm\n");
				//System.out.print("\n" +(numberStack[(numberInNumberStack-2)]) 
				//		+ "\n" + (numberStack[(numberInNumberStack-1)])
				//		+ "\n" + (operationStack[(numberInOperationStack-1)])
				//		+ "\n\n");
				sum += calcMath((numberStack[(numberInNumberStack-2)]), 
						(numberStack[(numberInNumberStack-1)]),
						(operationStack[(numberInOperationStack-1)]));
				//System.out.print("\n2hmhmhmhhmh\n" + sum + "\nmmhmhmmmhmhmmmm\n");
				if((numberStack[(numberInNumberStack-2)]) == 0)
				{
					//System.out.print("\nfeufhu4e4g\n" + sum + "\nehjfhewg\n");
					//error("fe");
				}
				numberInNumberStack -= 2;
				numberInOperationStack--;
			}
		}
		if((numberInOperationStack >= 1) && (numberInNumberStack == 0))
		{
			while((numberInOperationStack >= 1) && 
					(operationStack[(numberInOperationStack-1)] == '-'))
			{
				sum = calcMath(-1, sum, '*');
				numberInOperationStack--;
			}
		}
		if(numberInNumberStack == 1)
		{
			sum += numberStack[(numberInNumberStack-1)];
			numberInNumberStack--;
		}
		//System.out.print("\nffffffffff\n" + sum + "\nffffffffff\n");
		return(sum);
		
	}
	
	
	static int calcMath( int num1, int num2, char operation)
	{
		///*
		///System.out.print("\n\n\naaaaaaaaaaaaaaaaa\n" 
		///		+ num1 
		///		+ " " + operation + " "
		///		+ num2
		///		+"\naaaaaaaaaaaaaaaaa\n\n\n");
		//*/
		switch(operation)
		{
			//PEMDAS //Parenthesis case is handled in the number section above
			case '*':
				return (num1 * num2);
			case '+':
				return (num1 + num2);
			case '-':
				//System.out.print("\nyoooooooooooooooooooooooo\n");
				return ((num1 - num2));
			default:
				errorCalculation();
		}
		/*--------------------NO DIVISON!
		 * OR EXPONENTIALS!
		 * YEAAAAAAAAAAAHHHHH!!!!!!!!!!!!
		 	case '^':
				return (num1^num2);
			case '/':
				if( num2 != 0)
					return (num1/num2);
				else 
				{
					error("THERE WAS AN ATTEMPT TO DIVIDE BY ZERO");
					return 0;//Paranoia
				}
		 */
		errorCalculation();
		return 0;//Paranoia

	}

}

