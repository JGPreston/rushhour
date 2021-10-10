/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rushhour;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import search.Action;
import search.State;
/**
 *
 * @author steven
 */
public class GameState implements search.State {

    boolean[][] occupiedPositions;
    List<Car> cars; // target car is always the first one 
    int nrRows;
    int nrCols;
   
    public GameState(String fileName) throws Exception {
        BufferedReader in = new BufferedReader(new FileReader(fileName));
        nrRows = Integer.parseInt(in.readLine().split("\\s")[0]);
        nrCols = Integer.parseInt(in.readLine().split("\\s")[0]);
        String s = in.readLine();
        cars = new ArrayList();
        while (s != null) {
            cars.add(new Car(s));
            s = in.readLine();
        }
        initOccupied(); 
    }

    public GameState(int nrRows, int nrCols, List<Car> cars) {
        this.nrRows = nrRows;
        this.nrCols = nrCols;
        this.cars = cars;
        initOccupied();
    }

    public GameState(GameState gs) {
        nrRows = gs.nrRows;
        nrCols = gs.nrCols;
        occupiedPositions = new boolean[nrRows][nrCols];
        for (int i = 0; i < nrRows; i++) {
            for (int j = 0; j < nrCols; j++) {
                occupiedPositions[i][j] = gs.occupiedPositions[i][j];
            }
        }
        cars = new ArrayList();
        for (Car c : gs.cars) {
            cars.add(new Car(c));
        }
    }

    public void printState() {
        int[][] state = new int[nrRows][nrCols];

        for (int i = 0; i < cars.size(); i++) {
            List<Position> l = cars.get(i).getOccupyingPositions();
            for (Position pos : l) {
                state[pos.getRow()][pos.getCol()] = i + 1;
            }
        }

        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                if (state[i][j] == 0) {
                    System.out.print(".");
                } else {
                    System.out.print(state[i][j] - 1);
                }
            }
            System.out.println();
        }
    }

    private void initOccupied() {
        occupiedPositions = new boolean[nrRows][nrCols];
        for (Car c : cars) {
            List<Position> l = c.getOccupyingPositions();
            for (Position pos : l) {
                occupiedPositions[pos.getRow()][pos.getCol()] = true;
            }
        }
    }

    public boolean isGoal() {
        Car goalCar = cars.get(0);
        return goalCar.getCol() + goalCar.getLength() == nrCols;
    }

    public boolean equals(Object o) {
        if (!(o instanceof GameState)) {
            return false;
        } else {
            GameState gs = (GameState) o;
            return nrRows == gs.nrRows && nrCols == gs.nrCols && cars.equals(gs.cars); // note that we don't need to check equality of occupiedPositions since that follows from the equality of cars
        }
    }

    public int hashCode() {
        return cars.hashCode();
    }

    public void printToFile(String fn) {
        try {
            PrintWriter out = new PrintWriter(new FileWriter(fn));
            out.println(nrRows);
            out.println(nrCols);
            for (Car c : cars) {
                out.println(c.getRow() + " " + c.getCol() + " " + c.getLength() + " " + (c.isVertical() ? "V" : "H"));
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    //Coursework part
    //c1737263

    public List<Action> getLegalActions() {
        ArrayList<Action> gla = new ArrayList<Action>();

        for (int movingCar = 0; movingCar < cars.size(); movingCar++) { //iterate through the size of cars list
            
            /*
              For each car, check how far it can move in any direction.
              If it can move n amount of spaces in a direction, add it
              to a list of possible moves which holds information on the 
              car, the direction, and the amount of moves in that direction
            */
    
            int moveTotal=1;                                        //Sets the move count to 1 before starting
            MoveLeft moveLeft = new MoveLeft(movingCar,moveTotal);  //Makes a new moveleft memory instance with the car and moves number
            while(isLegal(moveLeft)){                               //Checks whether or not the move can be made
                gla.add(moveLeft);                                  //If returned as true, this move will be added to the list of possible moves
                moveTotal++;                                        //Add 1 to the moves number (check if the car can move in a direction more than 1 space at a time)
                moveLeft = new MoveLeft(movingCar, moveTotal);      //New move but with the added move number total ^
            }

            //Repeated but with different directions
            moveTotal=1;
            MoveRight moveRight = new MoveRight(movingCar,moveTotal);
            while(isLegal(moveRight)){
                gla.add(moveRight);
                moveTotal++;
                moveRight = new MoveRight(movingCar,moveTotal);
            }
            
            moveTotal=1;
            MoveUp moveUp = new MoveUp(movingCar,moveTotal);
            while(isLegal(moveUp)){
                gla.add(moveUp);
                moveTotal++;
                moveUp = new MoveUp(movingCar,moveTotal);
            }
            
            moveTotal=1;
            MoveDown moveDown = new MoveDown(movingCar,moveTotal);
            while(isLegal(moveDown)){
                gla.add(moveDown);
                moveTotal++;
                moveDown = new MoveDown(movingCar,moveTotal);
            }
        }
        return gla;                                                 //Once the list of cars has been iterated through, return the list of possible moves
    }

    public boolean isLegal(Action action) {

        /*
            Variables are setup here to reduce the amount of duplicated lines
        */
        int carNum = Integer.parseInt(action.toString().split("-")[0]);     //Get the car number that has been set in the (action).java file
        int moveNum = Integer.parseInt(action.toString().split("-")[2]);    //Get the moves total number that has been set in the (action).java file

        Car currentCar = cars.get(carNum);       //Set the car being checked from carNum setup above
        int row = currentCar.getRow();           //Set the row number to be the current cars one
        int col = currentCar.getCol();           //Set the column number to be the current cars one
        int length = currentCar.getLength();     //Set the length number to be the current cars one
        boolean vert = currentCar.isVertical();  //Set if the current car is vertical or not
       
        if(action instanceof MoveLeft){                                                            
            /*
                1. If the car is vertical, return false
                2. If the column number - the moves total number is less than 0 (check if the move makes the car go out of boundary)
                3. If the move collides into another car on the grid
            */                                       
            if (vert == true || col - moveNum < 0 || occupiedPositions[row][col-moveNum] == true){
                return false;
            }
            return true; //Returns true if all of the above passes
        }

        /*  
            Changed the boundary being checked to be the total number of columns instead of 0 as the car is moving right
        */
        if(action instanceof MoveRight){
            if (vert == true || col + length + moveNum > nrCols || occupiedPositions[row][col + length + moveNum -1] == true){
                return false;
            }
            return true;
            
        }

        /*
            For up and down movements, check if the car isn't vertical 
        */
        if(action instanceof MoveUp){
            if (vert == false || row - moveNum < 0 || occupiedPositions[row - moveNum][col] == true){
                return false;
            }
            return true;
        }

        /*  
            Changed the boundary being checked to be the total number of columns instead of 0 as the car is moving down
        */
        if(action instanceof MoveDown){
            if (vert == false || row + length + moveNum > nrRows || occupiedPositions[row + length + moveNum -1][col] == true){
                return false;
            }
            return true;
        }

        return false;
    }

    public int getEstimatedDistanceToGoal() {
        int escape = cars.get(0).getRow();  //Get the row of the goal car
        int blockingTotal = 1;              //For counting the amount of cars that block the goal cars path

        if(isGoal()){
            return 0;
        }
        for (int i=1; i < cars.size(); i++) {                                                               //Iterate through the size of cars list read in
            if (cars.get(i).isVertical()){                                                                  //If the car is vertical
                for (int j=0; j < cars.get(i).getLength(); j++){                                            //Iterate through the length number from 0 to the total of the cars length
                    /*
                        If the current cars row plus its length equals the escape row and the car selected is 
                        infront of the goal car then add it to the blocked counter total
                        and
                        If the current cars column is infront of the goal cars column
                    */
                    if (cars.get(i).getRow() + j == escape && cars.get(i).getCol() > cars.get(0).getCol()){ 
                        blockingTotal++;                                                                    //Add one to the number of blocking cars
                    }
                }
            }
        }
        return blockingTotal;
    }

    public State doAction(Action action){
        GameState gs = new GameState(this);                                 //Makes a new gamestate for the move being made

        int carNum = Integer.parseInt(action.toString().split("-")[0]);     //Gets the car number from the (action).java car number
        int moveNum = Integer.parseInt(action.toString().split("-")[2]);    //Gets the move number from the (action).java move number
        int col = cars.get(carNum).getCol();                                //Sets the column number to be the current cars
        int row = cars.get(carNum).getRow();                                //Sets the row number to be the current cars


        if(action instanceof MoveLeft){
            gs.moveCar(carNum, row, (col - moveNum));       //Use the current game state. Pass info into the moveCar method for a move to be made
        }
        else if(action instanceof MoveRight){
            gs.moveCar(carNum, row, (col + moveNum));
        }
        else if(action instanceof MoveDown){
            gs.moveCar(carNum, (row + moveNum), col);
        }
        else if(action instanceof MoveUp){
            gs.moveCar(carNum, (row - moveNum), col);
        }
        return gs;                                          //Once this has been done, return the gamestate 
    }

    public void moveCar(int carToMove, int row, int col){
        Car car = this.cars.get(carToMove);                 //Sets a car to be the current car 
        car.setRow(row);                                    //Sets the cars row to the the row number passed through
        car.setCol(col);                                    //Sets the cars column to the column number passed through
        this.cars.set(carToMove, car);                      //Replaces the information on the current car with the new car
        initOccupied();                                     //Calls initOccupied() to update the cars list for later use when a new move goes to be made
    }
}

