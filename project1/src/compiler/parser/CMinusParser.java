package compiler.parser;

import java.util.*;
import compiler.scanner.*;

public class CMinusParser implements Parser {

    private CMinusScanner_jflex scan;

    private Token nextToken;
    private Token.TokenType nextType;

    public CMinusParser(CMinusScanner_jflex scanner) {
        scan = scanner;
    }

    private Token advanceToken() {
        nextToken = scan.getNextToken();
        nextType = nextToken.getTokenType();
        return nextToken;
    }

    @Override
    public Program parse() throws ParserException {
        ArrayList<Declarations> declList = new ArrayList();
        advanceToken();
        declList.add(parseDecl());
        while (nextType == Token.TokenType.INT_TOKEN | nextType == Token.TokenType.VOID_TOKEN) {
            declList.add(parseDecl());
        }
        if (nextType != Token.TokenType.EOF_TOKEN) {
            throw new ParserException("There are no more declarations, but the end of file was not reached.");
        }
        return new Program(declList);
    }

    private Declarations parseDecl() throws ParserException {
        Declarations returnDecl;
        Token.TokenType typespec;
        String id;
        switch (nextType) {
            case INT_TOKEN:
                typespec = Token.TokenType.INT_TOKEN;
                break;
            case VOID_TOKEN:
                typespec = Token.TokenType.VOID_TOKEN;
                break;
            default:
                throw new ParserException("Parsing decl: Expected int or void, got " + nextType.toString());
        }
        advanceToken();
        if (nextType == Token.TokenType.ID_TOKEN) {
            id = nextToken.getTokenData().toString();
        } else {
            throw new ParserException("Parsing decl: Expected id, got " + nextType.toString());
        }
        advanceToken();
        switch (typespec) {
            case VOID_TOKEN:
                FunDeclaration fd = parseFunDecl(typespec, id);
                returnDecl = fd;
                break;
            case INT_TOKEN:
                Declarations dp = parseDeclPrime(id);
                returnDecl = dp;
                break;
            default:
                throw new ParserException(new Token.TokenType[]{
                		Token.TokenType.VOID_TOKEN, 
                		Token.TokenType.INT_TOKEN}, typespec);
        }
        return returnDecl;
    }

    private FunDeclaration parseFunDecl(Token.TokenType typespec, String id) throws ParserException {
        ArrayList<Param> ps;
        CompoundStatement cs;
        if (nextType != Token.TokenType.LEFTPAREN_TOKEN) {
            throw new ParserException("Parsing fun-decl: Expected (, got " + nextType.toString());
        }
        advanceToken();
        ps = parseParams();
        if (nextType != Token.TokenType.RIGHTPAREN_TOKEN) {
            throw new ParserException("Parsing fun-decl: Expected ), got " + nextType.toString());
        }
        advanceToken();

        cs = parseCompoundStmt();
        return new FunDeclaration(typespec, id, ps, cs);
    }

    private Declarations parseDeclPrime(String id) throws ParserException {
        if (nextType == Token.TokenType.LEFTSQBRACKET_TOKEN) {
            advanceToken();
            int num;
            if (nextType == Token.TokenType.NUM_TOKEN) {
                num = (int) nextToken.getTokenData();
            } else {
                throw new ParserException(Token.TokenType.NUM_TOKEN, nextType, Declarations.class);
            }
            advanceToken();
            if (nextType == Token.TokenType.RIGHTSQBRACKET_TOKEN) {
                advanceToken();
            } else {
                throw new ParserException("Parsing Decl': Expected ], got " + nextType.toString());
            }
            if (nextType == Token.TokenType.SEMICOLON) {
                advanceToken();
            } else {
                throw new ParseException("Parsing Decl': Expected ;, got " + nextType.toString());
            }
            return new VarDecl(id, num);
        } else if (nextType == Token.TokenType.SEMICOLON) {
            advanceToken();
            return new VarDecl(id);
        } else if (nextType == Token.TokenType.LEFTPAREN) {
            return parseFunDeclPrime(Token.TokenType.INT, id);
        } else {
            throw new ParseException("Parsing Decl': Expected [, ;, or (; got " + nextType.toString());
        }

    }

    private FunDecl parseFunDeclPrime(Token.TokenType typeSpec, String id) throws ParseException {
        if (nextType == Token.TokenType.LEFTPAREN) {
            advanceToken();
            ArrayList<Param> params = parseParams();
            if (nextType == Token.TokenType.RIGHTPAREN) {
                advanceToken();
                CompoundStatement cs = parseCompoundStmt();
                return new FunDecl(typeSpec, id, params, cs);
            } else {
                throw new ParseException("Parsing FunDecl': Expected ), got " + nextType.toString());
            }
        } else {
            throw new ParseException("Parsing FunDecl': Expected (, got " + nextType.toString());
        }
    }

    private ArrayList<Param> parseParams() throws ParseException {
        ArrayList<Param> alp = new ArrayList();
        //advanceToken();
        if (nextType == Token.TokenType.VOID) {
            advanceToken();
            return alp;
        }
        alp.add(parseParam());
        while (nextType != Token.TokenType.RIGHTPAREN) {
            alp.add(parseParam());
        }
        return alp;
    }

    private Param parseParam() throws ParseException {
        Param p;
        if (nextType == Token.TokenType.INT) {
            advanceToken();
            if (nextType == Token.TokenType.IDENT) {
                String id = nextToken.getTokenData().toString();
                advanceToken();
                if (nextType == Token.TokenType.LEFTSQBRACKET) {
                    advanceToken();
                    if (nextType == Token.TokenType.RIGHTSQBRACKET) {
                        advanceToken();
                        return new Param(id, true);
                    } else {
                        throw new ParseException("Parsing Param: Expected ], got " + nextType.toString());
                    }
                } else if (nextType == Token.TokenType.COMMA
                        || nextType == Token.TokenType.RIGHTPAREN) {
                    if (nextType == Token.TokenType.COMMA) {
                        advanceToken();
                    }
                    return new Param(id, false);
                } else {
                    throw new ParseException("Parsing Param: Expected , or ), got " + nextType.toString());
                }
            } else {
                throw new ParseException("Parsing Param: Expected IDENT, got " + nextType.toString());
            }
        } else {
            throw new ParseException("Parsing Param: Expected INT, got " + nextType.toString());
        }

    }

    private CompoundStatement parseCompoundStmt() throws ParseException {
        ArrayList<VarDecl> localDecls = new ArrayList<>();
        ArrayList<Statement> stmtList = new ArrayList<>();
        if (nextType == Token.TokenType.LEFTBRACE) {
            advanceToken();
        } else {
            throw new ParseException("Parsing CompoundStatement: Expected {, got " + nextType.toString());
        }
        if (nextType == Token.TokenType.INT
                || nextType == Token.TokenType.VOID) {
            localDecls = parseLocalDecls();
        }
        if (nextType == Token.TokenType.SEMICOLON
                || nextType == Token.TokenType.LEFTPAREN
                || nextType == Token.TokenType.NUM
                || nextType == Token.TokenType.IDENT
                || nextType == Token.TokenType.LEFTBRACE
                || nextType == Token.TokenType.IF
                || nextType == Token.TokenType.WHILE
                || nextType == Token.TokenType.RETURN) {
            stmtList = parseStmtList();
            if (nextType == Token.TokenType.RIGHTBRACE) {
                advanceToken();
                return new CompoundStatement(localDecls, stmtList);
            } else {
                throw new ParseException("Parsing CompoundStatement: Expected }, got " + nextType.toString());
            }
        } else if (nextType == Token.TokenType.RIGHTBRACE) {
            advanceToken();
            return new CompoundStatement(localDecls, stmtList);
        } else {
            throw new ParseException("Parsing CompoundStatement: Expected {, got " + nextType.toString());
        }
    }

    private ArrayList<VarDecl> parseLocalDecls() throws ParseException {
        ArrayList<VarDecl> returnList = new ArrayList();
        while (nextType != Token.TokenType.IF
                && nextType != Token.TokenType.IDENT
                && nextType != Token.TokenType.NUM
                && nextType != Token.TokenType.WHILE
                && nextType != Token.TokenType.RETURN
                && nextType != Token.TokenType.LEFTPAREN
                && nextType != Token.TokenType.LEFTBRACE
                && nextType != Token.TokenType.SEMICOLON
                && nextType != Token.TokenType.RIGHTBRACE) {
            returnList.add(parseVarDecl());
        }
        return returnList;
    }

    private VarDecl parseVarDecl() throws ParseException {
        String returnID;
        int returnNum;
        VarDecl returnVarDecl = null;
        if (nextType == Token.TokenType.INT) {
            advanceToken();
            if (nextType == Token.TokenType.IDENT) {
                returnID = nextToken.getTokenData().toString();
                advanceToken();
                switch (nextType) {
                    case SEMICOLON:
                        returnVarDecl = new VarDecl(returnID);
                        advanceToken();
                        break;
                    case LEFTSQBRACKET:
                        advanceToken();
                        if (nextType == Token.TokenType.NUM) {
                            returnNum = (int) nextToken.getTokenData();
                            advanceToken();
                            if (nextType != Token.TokenType.RIGHTSQBRACKET) {
                                throw new ParseException("Parsing VarDecl: Expected ], got " + nextType.toString());
                            }
                            advanceToken();
                            returnVarDecl = new VarDecl(returnID, returnNum);
                            break;
                        } else {
                            throw new ParseException("Parsing VarDecl: Expected NUM, got " + nextType.toString());
                        }
                    default:
                        throw new ParseException("Parsing VarDecl: Expected [ or ;, got " + nextType.toString());
                }
            } else {
                throw new ParseException("Parsing VarDecl: Expected ID, got " + nextType.toString());
            }
        } else {
            throw new ParseException("Parsing VarDecl: Expected INT, got " + nextType.toString());
        }
        return returnVarDecl;
    }

    private ArrayList<Statement> parseStmtList() throws ParseException {
        ArrayList<Statement> returnList = new ArrayList();
        while (nextType != Token.TokenType.RIGHTBRACE) {
            returnList.add(parseStatement());
            //advanceToken();
        }
        return returnList;
    }

    private Statement parseStatement() throws ParseException {
        Statement returnStatement = null;
        switch (nextType) {
            case IF:
                advanceToken();
                returnStatement = parseIf();
                break;
            case WHILE:
                advanceToken();
                returnStatement = parseWhile();
                break;
            case RETURN:
                advanceToken();
                returnStatement = parseReturn();
                break;
            case NUM:
            case IDENT:
            case LEFTPAREN:
            case SEMICOLON:
                returnStatement = parseExprStmt();
                break;
            case LEFTBRACE:
                returnStatement = parseCompoundStmt();
                break;
            default:
                throw new ParseException("Parsing Statement: Expected IF, WHILE,"
                        + " NUM, ID, RETURN, (, ;, or {, got " + nextType.toString());
        }
        return returnStatement;
    }

    private IfStatement parseIf() throws ParseException {
        if (nextType != Token.TokenType.LEFTPAREN) {
            throw new ParseException("Parsing IfStmt: Expected (, got " + nextType.toString());
        }
        advanceToken();
        Expression e = parseExpression();
        if (nextType != Token.TokenType.RIGHTPAREN) {
            throw new ParseException("Parsing IfStmt: Expected ), got " + nextType.toString());
        }
        advanceToken();
        Statement s = parseStatement();
        Statement elseStatement = null;
        if (nextType == Token.TokenType.ELSE) {
            advanceToken();
            elseStatement = parseStatement();
        }
        return new IfStatement(e, s, elseStatement);
    }

    private WhileStatement parseWhile() throws ParseException {
        if (nextType != Token.TokenType.LEFTPAREN) {
            throw new ParseException("Parsing WhileStmt: Expected (, got " + nextType.toString());
        }
        advanceToken();
        Expression e = parseExpression();
        if (nextType != Token.TokenType.RIGHTPAREN) {
            throw new ParseException("Parsing WhileStmt: Expected ), got " + nextType.toString());
        }
        advanceToken();
        Statement s = parseStatement();
        return new WhileStatement(e, s);
    }

    private ReturnStatement parseReturn() throws ParseException {
        Expression e = null;
        switch (nextType) {
            case LEFTPAREN:
            case IDENT:
            case NUM:
                e = parseExpression();
                advanceToken();
                break;
            case SEMICOLON:
                advanceToken();
                break;
            default:
                throw new ParseException("Parsing ReturnStmt: Expected (, ID, NUM, or ;, got " + nextType.toString());
        }
        return new ReturnStatement(e);
    }

    private ExpressionStatement parseExprStmt() throws ParseException {
        Expression e = null;
        switch (nextType) {
            case LEFTPAREN:
            case IDENT:
            case NUM:
                e = parseExpression();
                if (nextType != Token.TokenType.SEMICOLON) {
                    throw new ParseException("Parsing ExprStmt: Expected ;, got " + nextType.toString());
                }
                advanceToken();
                break;
            case SEMICOLON:
                advanceToken();
                break;
            default:
                throw new ParseException("Parsing ExprStmt: Expected (, ID, NUM, or ;, got " + nextType.toString());
        }
        return new ExpressionStatement(e);
    }

    private Expression parseExpression() throws ParseException {
        Expression e;
        Object data;
        switch (nextType) {
            case LEFTPAREN:
                advanceToken();
                e = parseExpression();
                advanceToken();
                if (nextType != Token.TokenType.RIGHTPAREN) {
                    throw new ParseException("Parsing Expr: Expected ), got " + nextType.toString());
                }
                return parseSimExpr(e);
            case NUM:
                data = nextToken.getTokenData();
                advanceToken();
                return parseSimExpr(new NumExpr((int) data));
            case IDENT:
                data = nextToken.getTokenData();
                advanceToken();
                return parseExprPrime(new VarExpr((String) data));
            default:
                throw new ParseException("Parsing Expr: Expected (, NUM, or ID, got " + nextType.toString());
        }
    }

    private Expression parseExprPrime(Expression e) throws ParseException {
        switch (nextType) {
            case ASSIGN:
                advanceToken();
                return new AssignExpr((VarExpr) e, parseExpression());
            case LEFTPAREN:
                advanceToken();
                CallExpr ce = new CallExpr(((VarExpr) e).getVar(), parseArgs());
                if (nextType != Token.TokenType.RIGHTPAREN) {
                    throw new ParseException("Parsing Expr': Expected ), got " + nextType.toString());
                }
                advanceToken();
                return parseSimExpr(ce);
            case LEFTSQBRACKET:
                advanceToken();
                Expression inBrackets = parseExpression();
                advanceToken();
                if (nextType != Token.TokenType.RIGHTSQBRACKET) {
                    throw new ParseException("Parsing Expr': Expected ], got " + nextType.toString());
                }
                return parseExprDP(new VarExpr(((VarExpr) e).getVar(), inBrackets));
            case STAR:
            case SLASH:
            case PLUS:
            case MINUS:
            case LESSTHANEQUALTO:
            case LESSTHAN:
            case GREATERTHAN:
            case GREATERTHANEQUALTO:
            case COMPARE:
            case NOTEQUAL:
                return parseSimExpr(e);
            default:
                return e;
        }
    }

    private Expression parseExprDP(VarExpr e) throws ParseException {
        switch (nextType) {
            case ASSIGN:
                advanceToken();
                return new AssignExpr(e, parseExpression());
            case STAR:
            case SLASH:
            case PLUS:
            case MINUS:
            case LESSTHANEQUALTO:
            case LESSTHAN:
            case GREATERTHAN:
            case GREATERTHANEQUALTO:
            case COMPARE:
            case NOTEQUAL:
                return parseSimExpr(e);
            default:
                return e;
        }
    }

    private Expression parseSimExpr(Expression e) throws ParseException {
        Expression addExprPrime = parseAddExprPrime(e);
        BinExp.operation o;
        switch (nextType) {
            case LESSTHAN:
                o = BinExp.operation.LT;
                break;
            case GREATERTHAN:
                o = BinExp.operation.GT;
                break;
            case COMPARE:
                o = BinExp.operation.COMPARE;
                break;
            case NOTEQUAL:
                o = BinExp.operation.NOTEQ;
                break;
            case LESSTHANEQUALTO:
                o = BinExp.operation.LTE;
                break;
            case GREATERTHANEQUALTO:
                o = BinExp.operation.GTE;
                break;
            default:
                return addExprPrime;
        }
        advanceToken();
        return new BinExp(addExprPrime, parseAddExpr(), o);
    }

    private Expression parseAddExprPrime(Expression e) throws ParseException {
        BinExp.operation o = null;
        Expression tp;
        if (nextType == Token.TokenType.STAR
                || nextType == Token.TokenType.SLASH) {
            //advanceToken();
            tp = parseTermPrime(e);
            Expression be = tp;
            //advanceToken();
            while (nextType == Token.TokenType.PLUS
                    || nextType == Token.TokenType.MINUS) {
                if (nextType == Token.TokenType.PLUS) {
                    o = BinExp.operation.PLUS;
                } else if (nextType == Token.TokenType.MINUS) {
                    o = BinExp.operation.MINUS;
                }
                advanceToken();
                be = new BinExp(be, parseTerm(), o);
                //advanceToken();
            }
            return be;
        } else if (nextType == Token.TokenType.PLUS
                || nextType == Token.TokenType.MINUS) {
            if (nextType == Token.TokenType.PLUS) {
                o = BinExp.operation.PLUS;
            } else if (nextType == Token.TokenType.MINUS) {
                o = BinExp.operation.MINUS;
            }
            advanceToken();
            BinExp be = new BinExp(e, parseTerm(), o);
            //advanceToken();
            while (nextType == Token.TokenType.PLUS
                    || nextType == Token.TokenType.MINUS) {
                if (nextType == Token.TokenType.PLUS) {
                    o = BinExp.operation.PLUS;
                } else if (nextType == Token.TokenType.MINUS) {
                    o = BinExp.operation.MINUS;
                }
                be = new BinExp(be, parseTerm(), o);
                advanceToken();
            }
            return be;
        } else {
            return e;
        }
    }

    private Expression parseAddExpr() throws ParseException {
        Expression term = parseTerm();
        //advanceToken();
        BinExp.operation o = null;
        Expression be = term;
        if (nextType == Token.TokenType.PLUS
                || nextType == Token.TokenType.MINUS) {
            while (nextType == Token.TokenType.PLUS
                    || nextType == Token.TokenType.MINUS) {
                if (nextType == Token.TokenType.PLUS) {
                    o = BinExp.operation.PLUS;
                } else if (nextType == Token.TokenType.MINUS) {
                    o = BinExp.operation.MINUS;
                }
                be = new BinExp(be, parseTerm(), o);
                advanceToken();
            }
            return be;
        }
        return term;
    }

    private Expression parseTerm() throws ParseException {
        //term -> factor { mulop factor }
        Expression lhs = parseFactor();

        BinExp.operation o = null;
        BinExp be;
        if (nextType == Token.TokenType.STAR
                || nextType == Token.TokenType.SLASH) {
            if (nextType == Token.TokenType.STAR) {
                o = BinExp.operation.MULT;
            } else if (nextType == Token.TokenType.SLASH) {
                o = BinExp.operation.DIV;
            }
            advanceToken();
            be = new BinExp(lhs, parseFactor(), o);
            while (nextType == Token.TokenType.STAR
                    || nextType == Token.TokenType.SLASH) {
                if (nextType == Token.TokenType.STAR) {
                    o = BinExp.operation.MULT;
                } else if (nextType == Token.TokenType.SLASH) {
                    o = BinExp.operation.DIV;
                }
                advanceToken();
                be = new BinExp(lhs, parseFactor(), o);
                advanceToken();
            }
            return be;
        } else {
            return lhs;
        }
    }

    private Expression parseTermPrime(Expression e) throws ParseException {

        //term -> factor { mulop factor }
        BinExp.operation o = null;
        BinExp be = null;
        if (nextType == Token.TokenType.STAR
                || nextType == Token.TokenType.SLASH) {
            while (nextType == Token.TokenType.STAR
                    || nextType == Token.TokenType.SLASH) {
                if (nextType == Token.TokenType.STAR) {
                    o = BinExp.operation.MULT;
                } else if (nextType == Token.TokenType.SLASH) {
                    o = BinExp.operation.DIV;
                }
                advanceToken();
                be = new BinExp(e, parseFactor(), o);
                //advanceToken();
            }
            return be;
        } else {
            return e;
        }
    }

    private Expression parseFactor() throws ParseException {
        switch (nextType) {
            case LEFTPAREN:
                advanceToken();
                Expression e = parseExpression();
                advanceToken();
                return e;
            case IDENT:
                String id = (String) nextToken.getTokenData();
                advanceToken();
                return parseVarcall(id);
            case NUM:
                int num = (int) nextToken.getTokenData();
                advanceToken();
                return new NumExpr(num);
            default:
                throw new ParseException("Parsing Factor: Expected (, ID, or NUM, got " + nextType.toString());
        }
    }

    private Expression parseVarcall(String id) throws ParseException {
        switch (nextType) {
            case LEFTPAREN:
                advanceToken();
                CallExpr e = new CallExpr(id, parseArgs());
                advanceToken();
                if (nextType != Token.TokenType.RIGHTPAREN) {
                    throw new ParseException("Parsing Varcall: Expected ), got " + nextType.toString());
                }
                return e;
            default:
                return parseVarPrime(id);
        }
    }

    private Expression parseVarPrime(String id) throws ParseException {
        if (nextType == Token.TokenType.LEFTSQBRACKET) {
            advanceToken();
            Expression e = parseExpression();
            advanceToken();
            return new VarExpr(id, e);
        } else {
            return new VarExpr(id);
        }
    }

    private ArrayList<Expression> parseArgs() throws ParseException {
        if (nextType == Token.TokenType.LEFTPAREN
                || nextType == Token.TokenType.NUM
                || nextType == Token.TokenType.IDENT) {
            return parseArgList();
        } else {
            return new ArrayList();
        }
    }

    private ArrayList<Expression> parseArgList() throws ParseException {
        ArrayList<Expression> eList = new ArrayList<>();
        eList.add(parseExpression());
        while (nextType == Token.TokenType.COMMA) {
            advanceToken();
            eList.add(parseExpression());
        }
        return eList;
    }
}
