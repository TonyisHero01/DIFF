Příkaz diff je jedním z nejužitečnějších příkazu v Linuxu. Úkolem je nasimulovat diff v jazyce Java. Program má více režimů, které může uživatel zapínat pro různé zobrazení výstupu.

Režim standard: zapíná se bez přepínačů, například: diff file1 file2

`	`Příklad výstupu: 

`		`25c22

< o

\---

\> q

První číslo (25) je číslo řádku v prvním souboru, c je značka operátoru CHANGE(další operátory jsou: d je DELETE, a je ADD) a druhé číslo (22) je číslo řádku v druhém souboru. Dále jsou příslušné řádky ve dvou souborech. 

Režim context: zapíná se přepínačem -C, například: -C file1 file2

`	`Příklad výstupu:

`		`\*\*\* soubor1.txt 2023-03-07 19:30:21:771 +0100

--- soubor2.txt 2023-03-07 19:30:13:874 +0100

\*\*\* 16 \*\*\*\*

! x

--- 9 ----

! y

První dva řádky jsou data posledních uprav. Dále řádek obsahující \* s číslem je číslo řádku z prvního souboru a poté je kontext a příslušné řádky. Řádek obsahující „-“ s číslem je číslo řádku z druhého souboru a dále jsou kontext a příslušné řádky. Operátory jsou před řádkem, který má nějakou změnu a operátory jsou: - je DELETE, + je ADD a ! je CHANGE.

Lze nastavit délku kontextu. Za přepínačem -C může uživatel zadat velikost kontext, například: -C 1 file1 file2

`	`Příklad výstupu:

`		`\*\*\* 15,18 \*\*\*\*

`  `c

! x

`  `c

`  `d

--- 8,12 ----

`  `c

! y

`  `c

\+ z

`  `D

`		`Řádky, které nejsou operátory, jsou kontexty. 

Režim Side-By-Side: zapíná se přepínačem -y, například: -y file1 file2

`	`Příklad výstupu:

`		`a`                                                               `<                                                                

`		`c`                                                                 `c                                                              

`		`c`                                                                 `c                                                              

`		`c`                                                                 `c                                                              

`		`c`                                                                 `c

nalevo jsou řádky z prvního souboru a napravo jsou řádky z druhého souboru. Operátory jsou na pravé straně, kde < je DELETE, > je ADD a | je CHANGE. Může Side-By-Side režim nastavit šířku kontextu s přepínačem -W a velikost šířky, například: -y -W 10 file1 file2

`	`Příklad výstupu:

`		`a`   `<    

`		`c`     `c  

`		`c`     `c  

`		`c`     `c  

`		`c`     `c

Režim Interactive Side-By-Side: se zapíná přepínačem -i, například: -i file1 file2

`	`Příklad výstupu:

`		`a`                                                               `<                                                                

`		`Do you want to choose from the first or second file? (1/2)

`		`1

`		`c`                                                                 `c                                                              

`		`c`                                                                 `c                                                              

`		`c`                                                                 `c                                                              

`		`c`                                                                 `c                                                              

`		`x`                                                               `<                                                                

Do you want to choose from the first or second file? (1/2)

A nakonec výstupu program vytiskne obsah, který uživatel interaktivně vybral ze dvou souborů. Může se výstup programu zapsat do souboru s přepínačem -o outputFile, například: -i -o outputFile file1 file2. Lze také nastavit velikost kontextu se přepínačem -W, například: -i -W 5 -o outputFile file1 file2.
