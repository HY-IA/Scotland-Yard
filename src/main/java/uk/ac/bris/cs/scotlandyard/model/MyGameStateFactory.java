package uk.ac.bris.cs.scotlandyard.model;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import uk.ac.bris.cs.scotlandyard.model.Board.GameState;

import uk.ac.bris.cs.scotlandyard.model.Move.*;
import uk.ac.bris.cs.scotlandyard.model.Piece.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.*;
import uk.ac.bris.cs.scotlandyard.model.ScotlandYard.Factory;
import java.util.*;


/**
 * cw-model
 * Stage 1: Complete this class
 */
public final class MyGameStateFactory implements Factory<GameState> {


    @Nonnull @Override
    public GameState build(
                            GameSetup setup,
                            Player mrX,
                            ImmutableList<Player> detectives) {

        return new MyGameState(setup, ImmutableSet.of(MrX.MRX), ImmutableList.of(), mrX, detectives);
        // return the new updated game state
    }

    // Use the private to defence the programming



    
    private static Set<SingleMove> makeSingleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {
        final var OnlyOneMove = new HashSet<SingleMove>();
        for (int destination : setup.graph.adjacentNodes(source)) {
            // find out if destination is occupied by a detective
            boolean occupiedDestination = false;
            for (Player de : detectives) {
                if (de.location() == destination) {
                    occupiedDestination = true;
                    break;}}
            if (occupiedDestination) {continue;}
            for (Transport t : setup.graph.edgeValueOrDefault(source, destination, ImmutableSet.of())) {
                if (player.has(t.requiredTicket()))
                    // if player has required ticket
                    OnlyOneMove.add(new SingleMove(player.piece(), source, t.requiredTicket(), destination));}
            if (player.has(Ticket.SECRET)) {
                OnlyOneMove.add(new SingleMove(player.piece(), source, Ticket.SECRET, destination));}}
        return Set.copyOf(OnlyOneMove);}




    private static Set<DoubleMove> makeDoubleMoves(GameSetup setup, List<Player> detectives, Player player, int source) {
        final var TwoMoves = new HashSet<DoubleMove>();
        if (player.has(Ticket.DOUBLE)){
            // check if the player(only mrX) has a double ticket
            for (int destination : setup.graph.adjacentNodes(source)) {
                // find out if destination is occupied by a detective
                boolean occupiedDestination = false;
                for(Player de : detectives) {
                    if (de.location() == destination){
                        occupiedDestination = true;
                        break;}}
                if (occupiedDestination){continue;}

            // making sure if the player is mrx, and it has the DOUBLE ticket.
            // first you need to make a single move then you can see what else can be done (Double moves)
            Set<SingleMove> single1 = makeSingleMoves(setup, detectives, player, source);
            for (SingleMove one : single1) {
                Set<SingleMove> single2 = makeSingleMoves(setup,detectives, player, one.destination);
                for (SingleMove two : single2) {
                    // if two tickets are the same and player at least has 2 of them then double move is possible.
                    if (((one.ticket == two.ticket && player.hasAtLeast(one.ticket, 2))) ||
                            (one.ticket != two.ticket && player.hasAtLeast(one.ticket,1) && player.hasAtLeast(two.ticket,1))){
                        TwoMoves.add(new DoubleMove(player.piece(), source, one.ticket, one.destination, two.ticket, two.destination));}

                    if (player.has(Ticket.SECRET)){ // if the player has the secret move ticket then add the secret move.
                        TwoMoves.add(new DoubleMove(player.piece(), source,Ticket.SECRET,one.destination,two.ticket,two.destination));}}}}}

        return ImmutableSet.copyOf(TwoMoves);}





    private static final class MyGameState implements GameState {

        private final GameSetup setup;
        private final ImmutableSet<Piece> remaining;
        private final ImmutableList<LogEntry> log;
        private Player mrX;
        private final List<Player> detectives;
        private ImmutableSet<Move> moves;
        private ImmutableSet<Piece> winner;



        private MyGameState(
                final GameSetup setup,
                final ImmutableSet<Piece> remaining,
                final ImmutableList<LogEntry> log,
                final Player mrX,
                final List<Player> detectives) {


            this.setup = setup;
            this.remaining = remaining;
            this.log = log;
            this.mrX = mrX;
            this.detectives = detectives;



            if (setup.moves.isEmpty()){throw new IllegalArgumentException("Error");}
            // if the set-up is empty then error(not setup).
            if (setup.graph.nodes().isEmpty()){ throw new IllegalArgumentException("Error");}
            // if there is no node on the graph then error.
            if (detectives.isEmpty()){ throw new NullPointerException("Error");}
            // If there is no detectives then game is done.
            if (mrX.piece() == null){ throw new NullPointerException("Error");}
            // If mrX is not given then error.
            if (remaining.isEmpty()){ throw new IllegalArgumentException("Error");}

            for (Player de : detectives) {
                if (de.has(Ticket.DOUBLE)){ throw new IllegalArgumentException("Error");}
                //(de (detectives) has DOUBLE OR SECRET TICKET is strange so error)
                if (de.has(Ticket.SECRET)) {throw new IllegalArgumentException("Error");}}

            for (int i = 0; i < detectives.size(); i++) {
                for (int j = i + 1; j < detectives.size(); j++) {
                    if (detectives.get(i).location() == detectives.get(j).location()){
                        // same location is not allowed, so error.
                        throw new IllegalArgumentException("Error");}}}}



        @Nonnull @Override
        public GameSetup getSetup() {
            return setup;}
            //return current game setup




        @Nonnull @Override
        public ImmutableList<LogEntry> getMrXTravelLog() {
            return log;}




        @Nonnull @Override
        public ImmutableSet<Piece> getPlayers() {
            Set<Piece> sp = new HashSet<>();
            for (Player de : detectives) {
                sp.add(de.piece());
                sp.add(mrX.piece());}


            return ImmutableSet.copyOf(sp);}
        //return all the player in the game.



        @Nonnull @Override
        public Optional<Integer> getDetectiveLocation(Detective detective) {
            for (Player de : detectives) {
                if (de.piece() == detective) {
                    return Optional.of(de.location());}}
            // returning the location of the all the detectives


            return Optional.empty();
            // return the empty, if there are no detectives in the game
        }



        @Nonnull @Override
        public Optional<TicketBoard> getPlayerTickets(Piece piece) {
            for (Player de : detectives) {
                if (de.piece() == piece) {
                    return Optional.of(ticket -> de.tickets().get(ticket));}}


            if (mrX.piece() == piece) {
                return Optional.of(ticket -> mrX.tickets().get(ticket));}

            //getting ticket for detectives and mrx too.
            //return the ticket board of the given player and if it is empty if the player is not in the game.
            return Optional.empty();}




        @Nonnull @Override
        public ImmutableSet<Move> getAvailableMoves() {

            Set<Move> allPossibleMoves = new HashSet<>();
            Set<Piece> newWinner = new HashSet<>();
            Set<Piece> detectWin = new HashSet<>();

            for (Player de : detectives){
                detectWin.add(de.piece());}
            // add the all the detectives in the game.


            for (Player de : detectives){
                // if the detective and mrX is at the same location then mrX is caught.
                if(de.location() == mrX.location()){
                    newWinner.addAll(detectWin);}}


            if(setup.moves.size() == log.size()){
                // if mrX is only remain then mrX is winner of the game.
                if(remaining.contains(mrX.piece())){
                newWinner.add(mrX.piece());}}


            if (setup.moves.size() == log.size()) {
            for (Player  de : detectives){
                // if de is only remain then de is the winner of the game.
                if (remaining.contains(de.piece())){
                    detectWin.add(de.piece());}}}


            Set<Move> DetectivesAllMove = new HashSet<>();
            // all the moves for the detectives.

            for(Player de : detectives){
                DetectivesAllMove.addAll(makeSingleMoves(setup,detectives,de,de.location()));}
            // adding the all the available moves of the detectives

            if(DetectivesAllMove.isEmpty()){
                // if detective can not make move then stuck.
                newWinner.add(mrX.piece());}



            if(newWinner.isEmpty()){
            if(remaining.contains(mrX.piece())){ // if there is still remaining left of the mrX piece.
                // you can do the one move
                    allPossibleMoves.addAll(makeSingleMoves(setup, detectives, mrX, mrX.location()));

                if (setup.moves.size() >= 2){
                    // if the setup round is left more than 2 round then two moves can be done.
                    allPossibleMoves.addAll(makeDoubleMoves(setup, detectives, mrX, mrX.location()));}

                if(allPossibleMoves.isEmpty()){
                    newWinner.addAll(detectWin);}}

            if (!remaining.contains(mrX.piece())){
                // if there is no mrX piece in the remaining and detective is in the remaining then they can make single move.
                for (Player de : detectives){
                    if ( remaining.contains(de.piece())){
                        allPossibleMoves.addAll(makeSingleMoves(setup,detectives,de,de.location()));}}}}
                     // only oneMove because detective is allowed to do one move only.


            winner = ImmutableSet.copyOf(newWinner);
            moves  = ImmutableSet.copyOf(allPossibleMoves);

            return moves;}




        @Nonnull @Override
        public ImmutableSet<Piece> getWinner() {
            getAvailableMoves();
            return winner;}




        @Nonnull @Override
        public GameState advance(Move move) {
            moves = getAvailableMoves();
            if (!moves.contains(move)) throw new IllegalArgumentException("Illegal move: " + move);


            List<LogEntry> newLogs = new ArrayList<>(log);
            Set<Piece> newRemaining = new HashSet<>(remaining);
            List<Player> newDetectives = new ArrayList<>();

            newRemaining.remove(move.commencedBy());
            // Remove the piece which made the move.


            int newDestination = move.accept(new Visitor<>() {


                @Override
                public Integer visit(SingleMove singleMove) {
                    return singleMove.destination;}

                @Override
                public Integer visit(DoubleMove doubleMove) {
                    return doubleMove.destination2;}
            });

            // if it is the MRX turn
            if(move.commencedBy().isMrX()){
                mrX = mrX.use(move.tickets());
                mrX = mrX.at(newDestination);

                newDetectives.addAll(detectives);
                // adding the current game state of the detectives


                for(Player de : detectives){
                    newRemaining.add(de.piece());}
                // then add the detectives to the newRemaining, since switching from mrX turn

                for (Ticket ticket : move.tickets()){
                    if(ticket != Ticket.DOUBLE) { // checking the right ticket (Double is a signature)
                        if(setup.moves.get(newLogs.size())){
                            // checking if this is revealing round
                            newLogs.add(LogEntry.reveal(ticket, mrX.location()));}
                        // ticket is used in this Entry and location is you reveal

                        else{newLogs.add(LogEntry.hidden(ticket));}}}}



            // if it is the detectives turn
            if (move.commencedBy().isDetective()){
                for(Player de : detectives){
                    if (move.commencedBy() == de.piece()){

                        de = de.use(move.tickets());
                        de = de.at(newDestination);

                        newDetectives.add(de);

                        mrX = mrX.give(move.tickets());}
                    // detective ticket is given to mrX, only after detective use it.

                    if (move.commencedBy() != de.piece()){
                        newDetectives.add(de);}}



                Set<Move> stillRemain = new HashSet<>();
                // making sure if there is any piece is still in the new Remaining, Because they are stuck.

                    for (Player de : detectives){
                        if(newRemaining.contains(de.piece())){
                            stillRemain.addAll(makeSingleMoves(setup,detectives,de,de.location()));}}
                    // add the moves if there is still pieces remaining

                    if (stillRemain.isEmpty()){
                        // if this is empty then it is not stuck, so add the mrx to start the new round.
                        newRemaining.add(mrX.piece());}

                    if (newRemaining.isEmpty()){
                        // add mrx to start new round.
                        newRemaining.add(mrX.piece());}}



            return(new MyGameState(setup,ImmutableSet.copyOf(newRemaining), ImmutableList.copyOf(newLogs), mrX, ImmutableList.copyOf(newDetectives)));}}}





//-------------------------------------------------------------- ALL THE TESTS PASSED ------------------------------------------------------//


