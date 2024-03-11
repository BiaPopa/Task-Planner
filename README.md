Popa Bianca, 331CA

Tema #2 APD
Planificarea de task-uri intr-un datacenter

Pentru rezolvarea temei, au fost implementate clasele MyDispatcher si
MyHost astfel:

1. Clasa MyDispatcher

La apelul metodei addTask, se verifica ce politica de planificare foloseste
dispatcherul, folosindu-ne de enum-ul SchedulingAlgorithm, si se apeleaza
metoda specifica acesteia.

- metoda RR (politica Round Robin):

Pentru a determina carui host ii este asignat taskul, se foloseste formula
(lastID + 1) % numarul de hosturi. Variabila lastID este initializata cu
valoarea -1 pentru ca hostul 0 sa primeasca primul task, iar apoi este
incrementata la fiecare apel. Numarul de hosturi se afla calculand lungimea
listei de hosturi.

- metoda SQ (politica Shortest Queue):

Pentru a determina carui host ii este asignat taskul, se parcurge intreaga
lista de hosturi si se selecteaza hostul cu cea mai scurta coada de taskuri,
folosind metoda getQueueSize. Aceasta este implementata in clasa MyHost si
verifica daca hostul are un task care ruleaza folosind variabila isRunning. 
Daca da, atunci se adauga 1 la lungimea cozii de prioritati. Daca nu, atunci 
se returneaza doar lungimea acesteia.

- metoda SITA (politica Size Interval Task Assignment):

Pentru a determina carui host ii este asignat taskul, se verifica din ce 
categorie face parte acesta, folosind enum-ul TaskType. Astfel, taskurile
SHORT vor merge pe hostul 0, MEDIUM pe hostul 1 si LONG pe hostul 2.

- metoda LWL (politica Least Work Left):

Pentru a determina carui host ii este asignat taskul, se parcurge intreaga
lista de hosturi si se selecteaza hostul cu cel mai mic timp de executie,
folosind metoda getWorkLeft. Aceasta este implementata in clasa MyHost si
calculeaza suma timpilor de executie a taskurilor acestuia. Daca hostul are un
task care ruleaza, atunci se calculeaza timpul ramas din executia acestuia si se
adauga la suma finala. Timpul ramas se calculeaza scazand din timpul total de 
executie (getLeft), timpul scurs de la pornirea executiei taskului pana la momentul
curent (time - startTime). Intrucat Timerul este calculat in secunde, se va inmulti
cu 1000 timpul scurs. In final, se adauga si restul timpilor de executie ai
taskurilor din coada de prioritati. Metoda getWorkLeft este sincronizata
pentru a ne asigura ca taskul care ruleaza nu este preemptat in timp ce se
calculeaza suma.

Toate cele patru metode trimit taskul catre hostul selectat folosind metoda
addTask a acestuia.

2. Clasa MyHost

Clasa MyHost are o coada de prioritati tasks in care se retin taskurile ce trebuie
executate, in ordinea prioritatii lor. Clasa mai contine si variabilele isShutdown
(devine true in cazul in care se inchide hostul), task (retine taskul care ruleaza),
start_time (retine timpul la care se porneste executia taskului) si isRunning (devine
true in cazul in care hostul are un task care ruleaza).

Clasa MyHost implementeaza si urmatoarele metode:

- metoda run - asteapta folosind wait pana cand hostul primeste un task nou de executat
sau pana cand executia sa este oprita. Daca hostul se inchide, atunci se va opri
executia metodei. Altfel, se extrage taskul cu cea mai mare prioritate din coada, se
calculeaza timpul sau de start si se seteaza variabila isRunning pe true. Se va porni
executia taskului timp de task.getLeft() milisecunde folosind metoda sleep. Daca executia
sa este intrerupta de un task cu prioritate mai mare, atunci se va opri executia taskului
curent, se va calcula timpul ramas din executia sa (ca la getWorkLeft), se va readauga in
coada de prioritati si se va seta variabila isRunning pe false. Daca nu este intrerupt, 
atunci se seteaza timpul de executie ramas la 0. In final, daca taskul si-a terminat 
executia, este apelata metoda finish si se seteaza variabila isRunning pe false.

- metoda addTask - verifica daca taskul primit are prioritate mai mare decat taskul curent
si daca acesta este preemptibil. Daca da, atunci se apeleaza interrupt pentru a opri
executia taskului care ruleaza. Taskul primit este adaugat apoi in coada de prioritati, iar
metoda va apela notifyAll pentru a anunta ca s-a adaugat un task nou in coada.

- metoda getQueueSize - explicata anterior

- metoda getWorkLeft - explicata anterior

- metoda shutdown - seteaza variabila isShutdown pe true si apeleaza notifyAll pentru
a anunta ca hostul s-a inchis
