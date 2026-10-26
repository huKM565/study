import sys
from pprint import pprint
from sly import Parser
from main import KotlinLexer


class KotlinParser(Parser):
    tokens = KotlinLexer.tokens

    precedence = (
        ('left', OR),
        ('left', AND),
        ('left', EQUALS, NOT_EQUALS),
        ('left', LT, GT, LTE, GTE),
        ('left', PLUS, MINUS),
        ('left', TIMES, DIV),
        ('right', NOT, UMINUS),
    )

    @_('declaration_list')
    def program(self, p):
        return ('program', p.declaration_list) 

    @_('declaration_list declaration')
    def declaration_list(self, p):
        return p.declaration_list + [p.declaration]

    @_('declaration')
    def declaration_list(self, p):
        return [p.declaration]

    @_('function', 'var_decl')
    def declaration(self, p):
        return p[0]

    @_('FUN ID LPAREN RPAREN block')
    def function(self, p):
        return ('fun', p.ID, p.block)

    @_('LBREACE statement_list RBREACE')
    def block(self, p):
        return p.statement_list

    @_('statement_list statement')
    def statement_list(self, p):
        return p.statement_list + [p.statement]

    @_('')
    def statement_list(self, p):
        return []

    @_('var_decl',
       'assignment',
       'if_stmt',
       'for_stmt',
       'println_stmt')
    def statement(self, p):
        return p[0]

    @_('VAR ID ASSIGN expression')
    def var_decl(self, p):
        return ('var_decl', p.ID, p.expression)

    @_('ID ASSIGN expression')
    def assignment(self, p):
        return ('assign', p.ID, p.expression)

    @_('IF LPAREN expression RPAREN block else_part')
    def if_stmt(self, p):
        return ('if', p.expression, p.block, p.else_part)

    @_('ELSE if_stmt')
    def else_part(self, p):
        return ('else_if', p.if_stmt)

    @_('ELSE block')
    def else_part(self, p):
        return ('else', p.block)

    @_('')
    def else_part(self, p):
        return None

    @_('FOR LPAREN ID IN expression DOUBLE_DOTS expression RPAREN block')
    def for_stmt(self, p):
        return ('for', p.ID, p.expression0, p.expression1, p.block)

    @_('PRINTLN LPAREN expression RPAREN')
    def println_stmt(self, p):
        return ('println', p.expression)

    @_('expression PLUS expression',
       'expression MINUS expression',
       'expression TIMES expression',
       'expression DIV expression')
    def expression(self, p):
        return ('binop', p[1], p.expression0, p.expression1)

    @_('expression EQUALS expression',
       'expression NOT_EQUALS expression',
       'expression LT expression',
       'expression GT expression',
       'expression LTE expression',
       'expression GTE expression')
    def expression(self, p):
        return ('cmp', p[1], p.expression0, p.expression1)

    @_('expression AND expression',
       'expression OR expression')
    def expression(self, p):
        return ('logic', p[1], p.expression0, p.expression1)

    @_('NOT expression')
    def expression(self, p):
        return ('not', p.expression)

    @_('MINUS expression %prec UMINUS')
    def expression(self, p):
        return ('neg', p.expression)

    @_('LPAREN expression RPAREN')
    def expression(self, p):
        return p.expression

    @_('INT_LITERAL')
    def expression(self, p):
        return ('int', int(p.INT_LITERAL))

    @_('FLOAT_LITERAL')
    def expression(self, p):
        return ('float', float(p.FLOAT_LITERAL))

    @_('STRING_LITERAL')
    def expression(self, p):
        return ('string', p.STRING_LITERAL)

    @_('ID')
    def expression(self, p):
        return ('id', p.ID)

    def __init__(self):
        super().__init__()
        self.has_errors = False

    def error(self, p):
        self.has_errors = True
        if p:
            print(f"Синтаксическая ошибка на строке {p.lineno}: "
                  f"неожиданный токен {p.type} '{p.value}'")
        else:
            print("Синтаксическая ошибка: неожиданный конец файла")


if __name__ == '__main__':
    filename = sys.argv[1]

    try:
        with open(filename, 'r', encoding='utf-8') as f:
            code = f.read()

        lexer = KotlinLexer()
        parser = KotlinParser()
        tree = parser.parse(lexer.tokenize(code))

        if lexer.has_errors or parser.has_errors:
            print('Разбор завершился с ошибками.')
            sys.exit(1)
        elif tree is not None:
            print('Разбор выполнен успешно. Дерево:')
            pprint(tree, width=100)

    except FileNotFoundError:
        print(f"Ошибка: файл '{filename}' не найден")
