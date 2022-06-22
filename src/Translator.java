import java.io.*;
/*Per eseguire Jasmin (con il file Output.j come input a jasmin): */
/*java -jar jasmin.jar Output.j */
public class Translator{
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count = 0;

    public Translator(Lexer l, BufferedReader br){
        lex = l;
        pbr = br;
        move();
    }

    void move(){
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s){
        throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t){
        if(look.tag == t){
            if (look.tag != Tag.EOF){
                move();
            }

        } else
            error("syntax error");
    }

    public void prog(){
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                int lnext_prog = code.newLabel();
                statlist(lnext_prog);
                code.emitLabel(lnext_prog);
                match(Tag.EOF);
                try {
                    code.toJasmin();
                } catch (java.io.IOException e) {
                    System.out.println("IO error\n");
                }
                break;
            default:
                error("Error in grammar (prog). Wrong token: " + look);
        }
    }

    public void statlist(int lnext_statlist){
        switch(look.tag){
            case '=':
            case Tag.PRINT:
            case Tag.READ:
            case Tag.COND:
            case Tag.WHILE:
            case '{':
                stat(lnext_statlist);
                statlistp(lnext_statlist);
                break;

            default:
                error("Error in grammar (statlist). Wrong token: " + look);
        }
    }

    public void statlistp(int lnext_statlistp){
        switch(look.tag){
            case ';':
                match(';');
                int lnext_stat = code.newLabel(); //creo una nuova label per il nuovo stat (che può usare se usa while o cond)
                code.emitLabel(lnext_stat); //stampo la nuova label
				stat(lnext_stat); 
				statlistp(lnext_statlistp);//invoco statlistp con la vecchia lable per finire il metodo
                break;

            case Tag.EOF:
            case '}':
                break;

            default:
                error("Error in grammar (statlistp). Wrong token: " + look);
        }
    }

    public void stat(int lnext){
        switch(look.tag){
            case '=':
                match('=');
                if(look.tag == Tag.ID){
                    int id_addr = st.lookupAddress(((Word) look).lexeme);
                    if(id_addr == -1){
                        count++;
						id_addr = count;
                        st.insert(((Word)look).lexeme,count);
                    }
                    match(Tag.ID);
                    expr(OpCode.noOp); //Aggiunta di una nuova istruzione JVM noOp
                    code.emit(OpCode.istore, id_addr);
                }else
                    error("Error in grammar (stat) after = with " + look);
                break;

            case Tag.PRINT:
                match(Tag.PRINT);
                match('(');
                exprlist(OpCode.invokestatic);
                match(')');
                break;

            case Tag.READ:
                match(Tag.READ);
                match('(');
                if(look.tag == Tag.ID){
                    int id_addr = st.lookupAddress(((Word) look).lexeme);
                    if (id_addr == -1){
                        count++;
						id_addr = count;
						st.insert(((Word)look).lexeme,count);
                    }
                    match(Tag.ID);
                    match(')');
                    code.emit(OpCode.invokestatic, 0);
                    code.emit(OpCode.istore, id_addr);
                }else
                    error("Error in grammar (stat) after read with " + look);
                break;

            case Tag.COND:
                int true_label = code.newLabel();
				int false_label = code.newLabel();
				int exit_label = code.newLabel();
				
				match(Tag.COND);
                whenlist(true_label, false_label, exit_label);
                match(Tag.ELSE);
                stat(lnext);
                code.emitLabel(exit_label);
                break;

            case Tag.WHILE:
				true_label = code.newLabel();
				exit_label = code.newLabel();
				int loop_label = code.newLabel();
			
                match(Tag.WHILE);
                match('(');
                code.emitLabel(loop_label); 
                bexpr(true_label, exit_label);
				code.emitLabel(true_label);
                match(')'); 
                stat(lnext);
                code.emit(OpCode.GOto, loop_label);
                code.emitLabel(exit_label);
                break;

            case '{':
                match('{');
                statlist(lnext);
                match('}');
                break;

            default:
                error("Error in grammar (stat). Wrong token: " + look);
        }
    }

    public void whenlist(int label_true, int label_false, int exit_label){
        switch(look.tag){
            case Tag.WHEN:
                whenitem(label_true, label_false, exit_label);
				whenlistp(label_true, label_false, exit_label);
                break;

            default:
                error("Error in grammar (whenlist). Wrong token: " + look);
        }

    }

    public void whenlistp(int label_true, int label_false, int exit_label){
        switch(look.tag){
            case Tag.WHEN:
                label_true = code.newLabel();
				label_false = code.newLabel();
				whenitem(label_true, label_false, exit_label);
				label_true = code.newLabel();
				label_false = code.newLabel();
				whenlistp(label_true, label_false, exit_label);
                break;
				
            case Tag.ELSE:
            case ')':
                break;

            default:
                error("Error in grammar (whenlistp). Wrong token: " + look);
        }
    }

    public void whenitem(int label_true, int label_false, int exit_label){
        switch(look.tag) {
            case Tag.WHEN:
                match(Tag.WHEN);
                match('(');
                bexpr(label_true,label_false);
				
                code.emitLabel(label_true); //stampa nel file Output.j l'etichetta corrente -> L1:
                match(')');
                match(Tag.DO);
                int lnext = label_false; 
                stat(lnext);
				code.emit(OpCode.GOto, exit_label);
                code.emitLabel(label_false);
                break;

            default:
                error("Error in grammar (whenitem). Wrong token: " + look);
        }
    }

    public void bexpr(int label_true, int label_false){
        switch(look.tag) {
            case Tag.RELOP:
                String cond = (((Word)look).lexeme);
                match(Tag.RELOP);
                switch(cond){
                    case "==":
						expr(OpCode.noOp);
						expr(OpCode.noOp);
						code.emit(OpCode.if_icmpeq, label_true);
						code.emit(OpCode.GOto, label_false);
						break;
						
					case "<=":
						expr(OpCode.noOp);
						expr(OpCode.noOp);
						code.emit(OpCode.if_icmple, label_true);
						code.emit(OpCode.GOto, label_false);
						break;
						
					case ">=":
						expr(OpCode.noOp);
						expr(OpCode.noOp);
						code.emit(OpCode.if_icmpge, label_true);
						code.emit(OpCode.GOto, label_false);
						break;
						
					case "<>":
						expr(OpCode.noOp);
						expr(OpCode.noOp);
						code.emit(OpCode.if_icmpne, label_true);
						code.emit(OpCode.GOto, label_false);
						break;
						
					case "<":
						expr(OpCode.noOp);
						expr(OpCode.noOp);
						code.emit(OpCode.if_icmplt, label_true);
						code.emit(OpCode.GOto, label_false);
						break;
					
					case ">":
						expr(OpCode.noOp);
						expr(OpCode.noOp);
						code.emit(OpCode.if_icmpgt, label_true);
						code.emit(OpCode.GOto, label_false);
						break;
					
					default:
						error("Error in grammar (bexpr). Wrong Relop token: " + look);
                }
                break;

            default:
                error("Error in grammar (bexpr). Wrong token: " + look);
        }
    }

    public void expr(OpCode nextCode){
        switch(look.tag){
            case '+':
                match('+');
                match('(');
                exprlist(OpCode.iadd);
                match(')');
                if(nextCode == OpCode.invokestatic){
                    code.emit(OpCode.invokestatic, 1);
                }
                break;

            case '-':
                match('-');
                expr(OpCode.noOp);
                expr(OpCode.noOp);
                code.emit(OpCode.isub);
                if(nextCode == OpCode.invokestatic){
                    code.emit(OpCode.invokestatic, 1);
                }
                break;

            case '*':
                match('*');
                match('(');
                exprlist(OpCode.imul);
                match(')');
                if(nextCode == OpCode.invokestatic){
                    code.emit(OpCode.invokestatic, 1);
                }
                break;

            case '/':
                match('/');
                expr(OpCode.noOp);
                expr(OpCode.noOp);
                code.emit(OpCode.idiv);
                if(nextCode == OpCode.invokestatic){
                    code.emit(OpCode.invokestatic, 1);
                }
                break;

            case Tag.NUM:
				code.emit(OpCode.ldc, Integer.parseInt(((NumberTok)look).lexeme));
                if(nextCode == OpCode.invokestatic){
                    code.emit(OpCode.invokestatic, 1);
                }
                match(Tag.NUM);
				break;

            case Tag.ID:
                if(look.tag == Tag.ID){
                    int id_addr = st.lookupAddress(((Word) look).lexeme);
                    if (id_addr == -1) {
                        count++;
						id_addr = count;
						st.insert(((Word)look).lexeme,count);
                    }
					match(Tag.ID);
                    code.emit(OpCode.iload, id_addr);
                    if (nextCode == OpCode.invokestatic){
                        code.emit(OpCode.invokestatic, 1);
                    }
                }else{
                    error("syntax error");
                }
                break;

            default:
                error("Error in grammar (expr). Wrong token: " + look);
        }
    }

    public void exprlist(OpCode nextCode){
        switch(look.tag){
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr(nextCode);
                exprlistp(nextCode);
                break;
				
            default:
                error("Error in grammar (exprlist). Wrong token: " + look);
        }
    }

    public void exprlistp(OpCode nextCode){
        switch(look.tag){
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr(nextCode);
                exprlistp(nextCode);
                if(nextCode != OpCode.invokestatic){
                    code.emit(nextCode);
                }
                break;

            case ')':
                break;

            default:
                error("Error in grammar (exprlistp). Wrong token: " + look);
        }
    }

	public static void main(String[] args){
			Lexer lex = new Lexer();

			String path = "program.lft"; // il percorso del file da leggere
			try{
				BufferedReader br = new BufferedReader(new FileReader(path));
				Translator translator = new Translator(lex, br);
				translator.prog();
				System.out.println("Input OK");
				br.close();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
}
