import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

public class Algorithm {
    public static List<Action> minimax(State state) {
        int max = Integer.MIN_VALUE;
        List<Action> result = null;
        for (List<Action> a : state.applicableActions()) {
            int minValue = minValue(state.result(a));
            if (minValue >= max) {
                max = minValue;
                result = a;
            }

        }
        return result;
    }

    private static int maxValue(State state) {
        if (state.isTerminal())
            return state.utility();
        int val = Integer.MIN_VALUE;
        for (List<Action> a : state.applicableActions())
            val = Math.max(val, minValue(state.result(a)));
        return val;
    }

    private static int minValue(State state) {
        if (state.isTerminal())
            return state.utility();
        int val = Integer.MAX_VALUE;
        for (List<Action> a : state.applicableActions())
            val = Math.min(val, maxValue(state.result(a)));
        return val;
    }

    public static List<Action> minimax_H(State state, int depthCutoff) {
        int max = Integer.MIN_VALUE;
        List<Action> result = null;
        for (List<Action> a : state.applicableActions()) {
            int minValue = minValueH(state.result(a), depthCutoff - 1);
            if (minValue >= max) {
                max = minValue;
                result = a;
            }
        }
        return result;
    }

    private static int maxValueH(State state, int depth) {
        if (state.isTerminal())
            return state.utility();
        else if (depth == 0)
            return state.boardMajority();
        int val = Integer.MIN_VALUE;
        for (List<Action> a : state.applicableActions())
            val = Math.max(val, minValueH(state.result(a), depth - 1));
        return val;
    }

    private static int minValueH(State state, int depth) {
        if (state.isTerminal())
            return state.utility();
        else if ( depth == 0)
            return state.boardMajority();
        int val = Integer.MAX_VALUE;
        for (List<Action> a : state.applicableActions())
            val = Math.min(val, maxValueH(state.result(a), depth - 1));
        return val;
    }

    public static List<Action> heuristic_minimax_w_alpha_beta_pruning(State state, int depthCutoff) {
        Pair<Integer, List<Action>> val = maxValueAB(state, new LinkedList<>(), Integer.MIN_VALUE, Integer.MAX_VALUE, depthCutoff);
        return val.getValue();
    }

    private static Pair<Integer, List<Action>> maxValueAB(State previousState, List<Action> toNewState, int alpha, int beta, int depth) {
        State state = previousState.result(toNewState);
        state.setPlayer(state.getPlayer().otherPlayer());
        if (state.isTerminal())
            return new Pair(state.utility(), toNewState);
        else if (depth == 0) {
            return new Pair(state.boardMajority(), toNewState);
        }

        Pair<Integer, List<Action>> val = new Pair<>(Integer.MIN_VALUE, new LinkedList<>());
        for (List<Action> a : state.applicableActions()) {
            int valTemp = minValueAB(state, a, alpha, beta, depth - 1).getKey();
            if (valTemp > val.getKey()) {
                val = new Pair(valTemp, a);
            }
            if (val.getKey() >= beta)
                return val;
            alpha = Math.max(alpha, val.getKey());
        }
        return val;
    }

    private static Pair<Integer, List<Action>> minValueAB(State previousState, List<Action> toNewState, int alpha, int beta, int depth) {
        State state = previousState.result(toNewState);
        state.setPlayer(state.getPlayer().otherPlayer());
        if (state.isTerminal())
            return new Pair(state.utility(), toNewState);
        else if (depth == 0) {
            return new Pair(state.boardMajority(), toNewState);
        }
        Pair<Integer, List<Action>> val = new Pair<>(Integer.MAX_VALUE, new LinkedList<>());
        for (List<Action> a : state.applicableActions()) {
            int valTemp = maxValueAB(state, a, alpha, beta, depth - 1).getKey();
            if (valTemp < val.getKey()) {
                val = new Pair(valTemp, a);
            }
            if (val.getKey() <= alpha)
                return val;
            beta = Math.min(beta, val.getKey());
        }
        return val;
    }
}