import java.io.*;
import java.util.*;
public class Lexer {
	public static int line = 1;
	private char peek = ' ';
	private void readch(BufferedReader br){
		try{
			peek = (char) br.read();
		} catch (IOException exc){
			peek = (char) -1; // ERROR OR EOF
		}
	}
	public Token lexical_scan(BufferedReader br){
		while(peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r'){
			if (peek == '\n') 
				line++;
			readch(br);
		}
		switch(peek){
			case '!':
				peek = ' ';
				return Token.not;
			// ... gestire i casi di (, ), {, }, +, -, *, /, ; ... //
			case '(':
				peek = ' ';
				return Token.lpt;
			case ')':
				peek = ' ';
				return Token.rpt;
			case '{':
				peek = ' ';
				return Token.lpg;
			case '}':
				peek = ' ';
				return Token.rpg;
			case '+':
				peek = ' ';
				return Token.plus;
			case '-':
				peek = ' ';
				return Token.minus;
			case '*':
				peek = ' ';
				return Token.mult;
			case '/':
				readch(br);
				if(peek == '*'){
					readch(br);
					int state = 2;
					while(state != 4){
						while (peek == ' ' || peek == '\t' || peek == '\n' || peek == '\r'){
							if (peek == '\n') 
								line++;
							readch(br);
						}
						if(peek ==((char)-1)){
							System.err.println("Comment not closed");
							return null;
						}
						
						switch (state){
							case 2:
								if(peek == '*')
									state = 3;
							break;

							case 3:
								if(peek == '/')
									state = 4;
								else if(peek == '*')
									state = 3;
							break;
						}
					readch(br);
					}
					return lexical_scan(br); //faccio una ricorsione per far ritornare i prossimi token (siccome non devo restituire token con i commenti)
				}else if(peek == '/'){
					while((peek !=((char)-1))&&(peek != '\n'))
						readch(br);
					if(peek ==((char)-1))
						return new Token(Tag.EOF);
					else
						return lexical_scan(br);
				}
				else{
					return Token.div;
				}
			case ';':
				peek = ' ';
				return Token.semicolon;
			case '&':
				readch(br);
				if (peek == '&'){
					peek = ' ';
				return Word.and;
				}else{
					System.err.println("Erroneous character" + " after & : " + peek );
					return null;
				}
			// ... gestire i casi di ||, <, >, <=, >=, ==, <>, = ... //
			case '|':
				readch(br);
				if(peek == '|'){
					peek = ' ';
					return Word.or;
				}else{
					System.err.println("Erroneous character" + " after | : " + peek );
					return null;
				}
			case '<':
				readch(br);
				if(peek == '='){
					peek = ' ';
					return Word.le;
				}else if(peek == '>'){
					peek = ' ';
					return Word.ne;
				}else{
					return Word.lt;
				}	
			case '>':
				readch(br);
				if(peek == '='){
					peek = ' ';
					return Word.ge;
				}else{
					return Word.gt;
				}
			case '=':
				readch(br);
				if(peek == '='){
					peek = ' ';
					return Word.eq;
				}else{
					return Token.assign;
				}
				
			case (char)-1:
				return new Token(Tag.EOF);
				
			default:
				if (Character.isLetter(peek) || peek == '_' || Character.isDigit(peek)){ 
					String s = "";			
					do{
						s+= peek;
						readch(br);
					}while(Character.isDigit(peek) || Character.isLetter(peek) || peek == '_'); /*continuo a comporre la stringa s finchè trovo una lettera o un numero o _ */
		
					switch(s){
						case "cond":
							return Word.cond;
						case "when":
							return Word.when;
						case "then":
							return Word.then;
						case "else":
							return Word.elsetok;
						case "while":
							return Word.whiletok;
						case "do":
							return Word.dotok;
						case "seq":
							return Word.seq;
						case "print":
							return Word.print;
						case "read":
							return Word.read;
							
						default: /* altrimenti ho trovato un nuovo identificatore*/
							int state = 0;
							int i = 0;
							
							while(state >= 0 && i < s.length()){
								final char ch = s.charAt(i);
								
								 switch(state){
									 case 0:
										if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
											state = 1;
										else if(ch >= '0' && ch <= '9')
											state = 3;
										else if(ch == '_')
											state = 2;
									 break;
									 
									 case 1:
										if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
											state = 1;
										else if(ch >= '0' && ch <= '9')
											state = 1;
										else if(ch == '_')
											state = 1;
									 break;
									 
									 case 2:
									 	if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'))
											state = 1;
										else if(ch >= '0' && ch <= '9')
											state = 1;
										else if(ch == '_')
											state = 2;
									 break;
									 
									 case 3:
										if(ch >= '0' && ch <= '9')
											state = 3;
										else if((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch == '_'))
											state = -1;
									 break;
								 }
								 i++;
							}
						if(state == 1){ //è un identificatore
							Word wID = new Word(Tag.ID, s);
							return wID;
						}else if(state == 3){ //è un numero
							NumberTok nNUM = new NumberTok(Tag.NUM, s);	/* crea un nuovo oggetto numero e lo ritorna */
							return nNUM;
						}else{
							System.err.println("Erroneous identifier/number " + s);
							return null;
						}	
					}
				}else{
					System.err.println("General Erroneous identifier: " + peek );
					return null;
				}
		}
	}
	public static void main(String[] args){
		Lexer lex = new Lexer();
		String path = "File.txt"; // il percorso del file da leggere
		try{
			BufferedReader br = new BufferedReader(new FileReader(path));
			Token tok;
			do{
				tok = lex.lexical_scan(br);
				System.out.println("Scan: " + tok);
			}while (tok.tag != Tag.EOF);
			br.close();
		}catch (IOException e){
			e.printStackTrace();
			}
	}
}
