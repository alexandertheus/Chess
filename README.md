# Chess Engine with corresponding Chess AI
The program is implemented in Java and allows the user to play chess against the computer with three available levels of difficulty: 1, 2 and 3. <br />

## How to Start the Game
When running the game, it will inform you about the notation used for the chess moves (see next paragraph for further explanation) and asks you to choose a level of difficulty. Entering the number "1" will select the easy level of difficulty. Number "2" the medium level of difficulty, etc. Once you hit enter the game starts. <br />

## How to Make a Move
The chess game uses the algebraic notation for chess moves: https://en.wikipedia.org/wiki/Algebraic_notation_(chess).<br />
If an erroneous notation is entered (f. ex. you put the enemy king into a checkmate without adding "++" to the end of your input) it will ask you to enter your move again.

## How the AI is implemented
The logic of the AI is based on the minimax algorithm. Given a color, arraylist and a depth (which corresponds to the chosen difficulty), the class TreeNode will create a tree-like structure with all possible moves. The root will create children which correspond to all the possible moves of the initial state the board is in. The children of those children will do the same, but with their own adjusted board. The height of the tree corresponds to: "chosen level of difficulty" + 1. The leaves of the final tree are assigned with a score which corresponds to the relative constitution of the board at the leave:<br />
Pawn:               1 Point<br />
King:               MAXVALUE<br />
Queen:              5 Points<br />
Rest of the pieces: 2 Points<br />

The computer then selects the best possible move (child with the highest score) of all the possible moves.

## Structure of the Program
For a high-level understanding of the program's structure please have a look at the class diagram.
