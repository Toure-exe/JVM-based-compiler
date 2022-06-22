import java.io.*;

public class Parser{
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br){
        lex = l;
        pbr = br;
        move();
    }

	//move: muove il br al prossimo carattere del file e lo stampa
    void move(){
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

	//output errore
    void error(String s){
		throw new Error("near line " + lex.line + ": " + s);
    }
	
	//match: controlla se siamo alla fine(EOF), se sì si ritorna in start per uscire e tornare in main
    void match(int t){
		if (look.tag == t){
			if (look.tag != Tag.EOF) 
				move();
		}else error("syntax error");
    }
	
	//start: inizio 
    public void start(){
		//Condizioni per entrare effettivamente nella funzione start, ricavate da First(start), sugli appunti First(prog)
		if(look.tag == Token.assign.tag || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.COND || look.tag == Tag.WHILE || look.tag == Token.lpg.tag){
			statlist();
			match(Tag.EOF);
		}else
			error("Error in START. Expected ID, PRINT, READ, COND, WHILE OR '}'");
    }
	
	public void statlist(){
		if(look.tag == Token.assign.tag || look.tag == Tag.PRINT || look.tag == Tag.READ || look.tag == Tag.COND || look.tag == Tag.WHILE || look.tag == Token.lpg.tag){
			stat();
			statlistp();
		}else
			error("Error in STATLIST. Expected ID, PRINT, READ, COND, WHILE OR '}'");
	}
	
	public void statlistp(){
		switch(look.tag){
			case ';':
				match(';');
				stat();
				statlistp();
				break;
				
			case Tag.EOF:
			case '}':
				break;
				
			default:
				error("Error in STATLISTP. Expected ; or $ or }");
		}
	}
	
	public void stat(){
		switch(look.tag){
			case '=':
				match('=');
				match(Tag.ID);
				expr();
				break;
				
			case Tag.PRINT:
				match(Tag.PRINT);
				if(look.tag != '(')
					error("Error in STAT. Expected '('");
				match('(');
				exprlist();
				if(look.tag != ')')
					error("Error in STAT. Expected ')'");
				else 
					match(')');
				break;
				
			case Tag.READ:
				match(Tag.READ);
				if(look.tag != '(')
					error("Error in STAT. Expected '('");
				match('(');
				if(look.tag != Tag.ID)
					error("Error in STAT. Expected ID");
				match(Tag.ID);
				if(look.tag != ')')
					error("Error in STAT. Expected ')'");
				else 
					match(')');
				break;
				
			case Tag.COND:
				match(Tag.COND);
				whenlist();
				if(look.tag != Tag.ELSE)
					error("Error in STAT. Expected ELSE");
				match(Tag.ELSE);
				stat();
				break;
				
			case Tag.WHILE:
				match(Tag.WHILE);
				if(look.tag != '(')
					error("Error in STAT. Expected '('");
				match('(');
				bexpr();
				if(look.tag != ')')
					error("Error in STAT. Expected ')'");
				else{ 
					match(')');
					stat();
				}
				break;
				
			case '{':
				match('{');
				statlist();
				if(look.tag != '}')
					error("Error in STAT. Expected '}'");
				else
					match('}');
				break;
		}
	}
		
	public void whenlist(){
		if(look.tag == Tag.WHEN){
			whenitem();
			whenlistp();
		}else 
			error("Error in WHENITEM. Expected WHEN");
	}
		
	public void whenlistp(){
		switch(look.tag){
			case Tag.WHEN:
				whenitem();
				whenlistp();
				break;
				
			case Tag.ELSE:
			case ')':
				break;
				
			default:
				error("Error in WHENLISTP. Expected WHEN or )");
		}
	}
	
	public void whenitem(){
		if(look.tag == Tag.WHEN){
			match(Tag.WHEN);
			if(look.tag != '(')
				error("Error in WHENITEM. Expected '('");
			match('(');
			bexpr();
			if(look.tag != ')')
				error("Error in WHENITEM. Expected ')'");
			else{
				match(')');
				if(look.tag != Tag.DO)
					error("Error in WHENITEM. Expected DO");
				match(Tag.DO);
				stat();
			}
		}else
			error("Error in WHENITEM. Expected WHEN");
	}
	
	public void bexpr(){
		if(look.tag == Word.eq.tag || look.tag == Word.ne.tag || look.tag == Word.le.tag || look.tag == Word.ge.tag || look.tag == Word.lt.tag ||look.tag == Word.gt.tag){
			switch(look.tag){
				case Tag.RELOP:
					match(Tag.RELOP);
					expr();
					expr();
			}
		}else
			error("Error in BEXPR. Expected ==, <>, <=, >=");
	}
	
	public void expr(){
		switch(look.tag){
			case '+':
				match('+');
				if(look.tag != '(')
					error("Error in EXPR. Expected '('");
				match('(');
				exprlist();
				if(look.tag != ')')
					error("Error in EXPR. Expected ')'");
				else 
					match(')');
				break;
				
			case '*':
				match('*');
				if(look.tag != '(')
					error("Error in EXPR. Expected '('");
				match('(');
				exprlist();
				if(look.tag != ')')
					error("Error in EXPR. Expected ')'");
				else 
					match(')');
				break;
				
			case '-':
				match('-');
				expr();
				expr();
				break;
			
			case '/':
				match('/');
				expr();
				expr();
				break;
				
			case Tag.NUM:
				match(Tag.NUM);
				break;
				
			case Tag.ID:
				match(Tag.ID);
				break;
				
		}
	}
	
	public void exprlist(){
		if(look.tag == Token.plus.tag || look.tag == Token.mult.tag || look.tag == Token.minus.tag || look.tag == Token.div.tag || look.tag == Tag.NUM || look.tag == Tag.ID){
			expr();
			exprlistp();
		}else
			error("Error in EXPRLIST. Expected +, -, *, /, NUM or ID");
	}
	
	public void exprlistp(){
		switch(look.tag){
			case '+':
			case '-':
			case '*':
			case '/':
			case Tag.NUM:
			case Tag.ID:
				expr();
				exprlistp();
				break;
			
			case ')':
				break;
			default:
				error("Error in EXPRLISTP. Expected + or - or * or / or NUM or ID or )");
		}
	}
	


		
    public static void main(String[] args){
        Lexer lex = new Lexer();
        String path = "File.txt"; // il percorso del file da leggere
        try{
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e){
			e.printStackTrace();
			}
    }
}