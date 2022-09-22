# Scotland-Yard

In this first task we will ask you to implement Java classes, which model the game mechanics of "Scotland Yard" within a given software framework.

Note that you will implement the full version of the game (not the beginners version), but with the following alterations/clarifications:

Police or Bobbies will not be modelled.
The Ferry will be modelled.
Mr X and the detectives will be given variable (user-specified) amounts of tickets at the start, the normal rules for tickets follow:
When a detective moves, the ticket used will be given to Mr X.
Used Mr X tickets are discarded.
The number of rounds in a game is variable (>0) specified by an initial setup rather than fixed to 22 rounds as in the board game.
In the manual, the round count is defined as the number of transitions between Mr X and the detectives as a whole, this number is different from the number of slots on Mr X's Travel Log because Mr X can use double moves which occupies two slots (e.g. a 22 round game with two double move tickets means Mr X can have up to 24 moves).
For practical reasons, we've simplified this rule so the game can be set up with a variable max number of moves for Mr X (i.e. the slot count in Mr X's travel log), such that the game is over when Mr X's travel log is completely full, instead of some arbitrary number of rounds.
Mr X cannot move into a detective location.
Mr X loses if it is his turn and he cannot make any move himself anymore.
Detectives lose if it is their turn and none of them can move, if some can move the others are just skipped.
Ticket.SECRET represents a black ticket, this is used for Mr X's secret moves
Pay special attention to rules for double moves and secret moves, since these are particularly complex.

