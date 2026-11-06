import sys

from main import KotlinLexer
from parser import KotlinParser
from symbol_table import SymbolTable


class KotlinSemanticAnalyzer:
    NUMERIC = {'Int', 'Double'}

    def __init__(self):
        self.symbol_table = SymbolTable()

    def check_statement(self, node):
        kind = node[0]

        if kind == "var_decl":
            _, name, value = node

            self.symbol_table.add(name, self.check_expr(value))

        elif kind == "assign":
            _, name, value = node

            sym = self.symbol_table.lookup(name)
            if sym is None:
                raise ValueError(f"Присваивание необъявленной переменной '{name}'")

            value_t = self.check_expr(value)
            if value_t != sym.type:
                raise ValueError(f"Нельзя присвоить {value_t} переменной '{name}' типа {sym.type}")

        elif kind == "if":
            _, cond, block, else_part = node

            cond_t = self.check_expr(cond)
            if cond_t != 'Boolean':
                raise ValueError(f"Условие if должно быть Boolean, получено {cond_t}")

            for st in block:
                self.check_statement(st)

            if else_part is not None:
                else_kind, else_body = else_part
                if else_kind == "else":
                    for st in else_body:
                        self.check_statement(st)
                else:
                    self.check_statement(else_body)

        elif kind == "for":
            _, var, start, end, block = node

            start_t = self.check_expr(start)
            end_t = self.check_expr(end)
            if start_t != 'Int' or end_t != 'Int':
                raise ValueError(f"Границы диапазона for должны быть Int, получено {start_t} и {end_t}")

            self.symbol_table.add(var, 'Int')

            for st in block:
                self.check_statement(st)

        elif kind == "println":
            self.check_expr(node[1])

        else:
            raise ValueError(f"Неизвестный statement: {kind}")

    def check_expr(self, node):
        kind = node[0]

        if kind == 'int':
            return 'Int'

        if kind == 'double':
            return 'Double'

        if kind == 'string':
            return 'String'

        if kind == 'id':
            name = node[1]
            sym = self.symbol_table.lookup(name)
            if sym is None:
                raise ValueError(f"Использование необъявленной переменной '{name}'")
            return sym.type

        if kind == 'neg':
            t = self.check_expr(node[1])
            if t not in self.NUMERIC:
                raise ValueError(f"Унарный минус применим только к числам, получено {t}")
            return t

        if kind == 'not':
            t = self.check_expr(node[1])
            if t != 'Boolean':
                raise ValueError(f"'!' применим только к Boolean, получено {t}")
            return 'Boolean'

        if kind == 'binop':
            _, op, lhs, rhs = node
            lt = self.check_expr(lhs)
            rt = self.check_expr(rhs)
            if lt not in self.NUMERIC or rt not in self.NUMERIC:
                raise ValueError(f"Операция '{op}' требует чисел, получено {lt} и {rt}")
            return 'Double' if 'Double' in (lt, rt) else 'Int'

        if kind == 'cmp':
            _, op, lhs, rhs = node
            lt = self.check_expr(lhs)
            rt = self.check_expr(rhs)
            if op in ('==', '!='):
                if lt != rt:
                    raise ValueError(f"Сравнение '{op}' между {lt} и {rt} некорректно")
            elif lt != rt and not (lt in self.NUMERIC and rt in self.NUMERIC):
                raise ValueError(f"Сравнение '{op}' между {lt} и {rt} некорректно")
            return 'Boolean'

        if kind == 'logic':
            _, op, lhs, rhs = node
            lt = self.check_expr(lhs)
            rt = self.check_expr(rhs)
            if lt != 'Boolean' or rt != 'Boolean':
                raise ValueError(f"'{op}' требует Boolean с обеих сторон, получено {lt} и {rt}")
            return 'Boolean'

        raise ValueError(f"Неизвестное выражение: {kind}")

    def check_function(self, node):
        _, name, statements = node
        self.symbol_table.add(name, "fun")

        for st in statements:
            self.check_statement(st)

    def analyze(self, ast):
        tag, functions = ast

        if tag != "program":
            raise ValueError("Программа должна начинаться с program")

        for fn in functions:
            self.check_function(fn)

        return self.symbol_table


if __name__ == '__main__':
    filename = sys.argv[1]

    try:
        with open(filename, 'r', encoding='utf-8') as f:
            code = f.read()

        lexer = KotlinLexer()
        parser = KotlinParser()

        ast = parser.parse(lexer.tokenize(code))

        semantic_analyzer = KotlinSemanticAnalyzer()
        table = semantic_analyzer.analyze(ast)

        print('Разбор выполнен успешно. Таблица:')

    except FileNotFoundError:
        print(f"Ошибка: файл '{filename}' не найден")
    except (ValueError, SyntaxError) as e:
        print(f'Разбор завершился с ошибками: {e}')
        sys.exit(1)