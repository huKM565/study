import sys
from sly import Lexer


class KotlinLexer(Lexer):
    tokens = {
        PACKAGE,
        PRINTLN,
        FUN,
        VAR,
        ID,
        IF,
        ELSE_IF,
        ELSE,
        FOR,
        IN,
        DOUBLE_DOTS,
        DOT,

        INT_LITERAL,
        FLOAT_LITERAL,
        STRING_LITERAL,

        ASSIGN,
        PLUS,
        MINUS,
        TIMES,
        DIV,

        EQUALS,
        NOT_EQUALS,
        LTE,
        GTE,
        LT,
        GT,
        AND,
        OR,
        NOT,

        LPAREN,
        RPAREN,
        LBREACE,
        RBREACE,
    }

    ignore = ' \t'

    EQUALS = r'=='
    ASSIGN = r'='
    PLUS = r'\+'
    MINUS = r'-'
    TIMES = r'\*'
    DIV = r'/'

    NOT_EQUALS = r'!='
    LTE = r'<='
    GTE = r'>='
    LT = r'<'
    GT = r'>'

    AND = r'&&'
    OR = r'\|\|'
    NOT = r'!'

    DOUBLE_DOTS = r'\.\.'
    DOT = r'\.'

    LPAREN = r'\('
    RPAREN = r'\)'
    LBREACE = r'\{'
    RBREACE = r'\}'

    keywords = {
        'package': PACKAGE,
        'fun': FUN,
        'var': VAR,
        'if': IF,
        'else': ELSE,
        'for': FOR,
        'in': IN,
        'println': PRINTLN
    }

    @_(r'[a-zA-Z_][a-zA-Z0-9_]*')
    def ID(self, t):
        t.type = self.keywords.get(t.value, 'ID')
        return t

    @_(r'\d+\.\d+')
    def FLOAT_LITERAL(self, t):
        return t

    @_(r'\d+')
    def INT_LITERAL(self, t):
        return t

    @_(r'\"([^\\\"]|\\.)*\"')
    def STRING_LITERAL(self, t):
        return t
 
    @_(r'\n+')
    def ignore_newline(self, t):
        self.lineno += len(t.value)

    def error(self, t):
        print(f"Строка {self.lineno}: Недопустимый символ '{t.value}'")
        self.index += 1


if __name__ == '__main__':
    filename = sys.argv[1]

    try:
        with open(filename, 'r', encoding='utf-8') as f:
            code = f.read()

        lexer = KotlinLexer()
        for tok in lexer.tokenize(code):
            print(tok)

    except FileNotFoundError:
        print(f"Ошибка: файл '{filename}' не найден")
