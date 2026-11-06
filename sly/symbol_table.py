from dataclasses import dataclass

class SymbolTable:

    @dataclass
    class Item:
        name: str
        type: str

    def __init__(self):
        self.symbols: dict[str, SymbolTable.Item] = {}

    def add(self, name: str, typ: str):
        if name in self.symbols:
            raise ValueError(f"Повторное объявление переменной с именем {name}")
        self.symbols[name] = SymbolTable.Item(name, typ)

    def lookup(self, name:str):
        return self.symbols.get(name, None)

    # Показать таблицу
    def show(self):
        for _, sym in self.symbols.items():
            print(f"  {sym}")