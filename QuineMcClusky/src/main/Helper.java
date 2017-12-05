package main;

import java.util.*;

public class Helper {
    public static int findMax(List<Integer> inputList) {
        if (inputList == null || inputList.size() == 0) {
            throw new NullPointerException();
        }
        int max = inputList.get(0);
        for (int num : inputList) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }

    // to check if the minterms have one bit difference (input is oldmatch, output is newmatch: both are list of list of minterms)
    public static List<List<MinTerm>> pair(List<List<MinTerm>> minTermList) {
        List<List<MinTerm>> match = new ArrayList<>(); // creates the outer empty list
        for (int i = 0; i < minTermList.size() - 1; i++) {
            List<MinTerm> w1MinTerms = minTermList.get(i);
            List<MinTerm> w2MinTerms = minTermList.get(i + 1);
            match.add(new ArrayList<>()); //creates the inner empty list
            for (MinTerm w1Term : w1MinTerms) {
                for (MinTerm w2Term : w2MinTerms) {
                    if (areMinTermsPair(w1Term, w2Term)) {
                        Set<Integer> pairSet = new HashSet<>();
                        pairSet.addAll(w1Term.values);
                        pairSet.addAll(w2Term.values);
                        //to store paired minterms ie,{2,4}
                        MinTerm minTerm = new MinTerm(pairSet);
                        //to check if the pair already exists(while pairing for matchII and further)
                        if (match.get(i).contains(minTerm)) {
                            continue; //recently changed check once
                        }
                        minTerm.binaryRepresentation = getAdjString(w1Term.binaryRepresentation, w2Term.binaryRepresentation);
                        match.get(i).add(minTerm);
                    }
                }
            }
        }
        return match;
    }

    private static boolean areMinTermsPair(MinTerm m1, MinTerm m2) {
        if (getDiffCount(m1.binaryRepresentation, m2.binaryRepresentation) == 1) {
            return true;
        }
        return false;
    }

    private static int getDiffCount(String b1, String b2) {
        int diffCount = 0;
        for (int i = 0; i < b1.length(); i++) {
            if (b1.charAt(i) != b2.charAt(i)) {
                diffCount++;
            }
        }
        return diffCount;
    }

    private static String getAdjString(String b1, String b2) {
        String adjString = "";
        char ch;
        for (int i = 0; i < b1.length(); i++) {
            if (b1.charAt(i) != b2.charAt(i)) {
                ch = 'd';
            } else {
                ch = b1.charAt(i);
            }
            adjString = adjString + ch;
        }
        return adjString;
    }

    public static List<MinTerm> findPrimeImplicants(List<List<MinTerm>> oldMatch, List<List<MinTerm>> newMatch) {
        List<MinTerm> primeImplicants = new ArrayList<>();
        for (int i = 0; i < oldMatch.size(); i++) {
            List<MinTerm> oldMinTermList = oldMatch.get(i);
            for (MinTerm oldMinTerm : oldMinTermList) {
                boolean paired = false;
                //int limit = i < newMatch.size() - 1 ? i : newMatch.size() - 1; // no of lists we need to iterate over in th new match
                for (int j = 0; j < newMatch.size(); j++) {
                    List<MinTerm> newMinTermList = newMatch.get(j);
                    for (MinTerm newMinTerm : newMinTermList) {
                        if (newMinTerm.values.containsAll(oldMinTerm.values)) {
                            paired = true;
                            break;
                        }
                    }
                    if (paired == true) {
                        break;
                    }
                }
                if (paired == false) {
                    primeImplicants.add(oldMinTerm);
                }
            }
        }
        return primeImplicants;
    }

    public static void printList (Collection<MinTerm> minTerms, String heading){
        //println internally calls the objects toString method (in MinTerm class)
        System.out.println(heading);
        for(MinTerm minTerm: minTerms){
            System.out.println(minTerm);
        }
        System.out.println();
    }

}
