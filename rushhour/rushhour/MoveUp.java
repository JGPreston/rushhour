/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package rushhour;

import search.Action;
import search.State;

/**
 *
 * @author steven
 */
public class MoveUp implements Action{

    int moves;
    int carToMove;

    public int getCost() {
        return 1;
    }

    public MoveUp(int i,int j){
        this.carToMove = i;
        this.moves=j;
    }

    public int getCar(){
        return carToMove;
    }
    
    public String toString(){
        return carToMove+"-up-"+moves;
    }
    
}
