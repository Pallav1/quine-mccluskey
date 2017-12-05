package main;

import bitWise.BitWiseOperations;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<Integer> input = new ArrayList<>();
        /*input.add(0);
        input.add(2);*/

        //To input minterms
        System.out.println("Quine - McCluskey: SOP");
        System.out.println("\nFirst enter Minterms then you'll be asked for don't cares");
        System.out.println("\nEnter Minterms and when you are done enter -1");
        Scanner inputScanner = new Scanner(System.in);      //scanner class is used to read from standard input
        while (inputScanner.hasNext()) {
            int minterm = inputScanner.nextInt(); // minterm integer
            if (minterm < 0) {
                break;
            }
            input.add(minterm);
        }

        /*if(input.isEmpty()){
            return;
        } to check if the input is empty*/

        //To filter out duplicates
        Set<Integer> uniqueMinterms = new HashSet<>(input);

        //To input Don't cares
        System.out.println("\nEnter don't cares and when you are done enter -1");
        inputScanner = new Scanner(System.in);
        Set<Integer> dontCares = new HashSet<>();
        while (inputScanner.hasNext()) {
            int dontCare = inputScanner.nextInt();
            if (dontCare < 0) {
                break;
            }
            //if the number entered for dont care already entered before
            if (uniqueMinterms.contains(dontCare)){
                System.out.println(dontCare + " is already part of Minterms. Please enter unique minterm. ");
                continue;
            }
            dontCares.add(dontCare);
        }

        input.clear();
        input.addAll(uniqueMinterms);
        input.addAll(dontCares); //input contains the unique minterms with don't cares

        //To find the Maximum Number of bits required
        int max = Helper.findMax(input);
        int numOfBits = (int) Math.ceil(Math.log(max) / Math.log(2));

        //Key->weight; value-> List of MinTerms corresponding to that weight
        Map<Integer, List<MinTerm>> table = new TreeMap<>();
        for (int value : input) {
            int weight = BitWiseOperations.getBinaryWeight(value);
            // The below if checks if the weight is already in the keyset
            if (!table.containsKey(weight)) {
                table.put(weight, new ArrayList<>()); // add new weight and the corresponding list to map
            }
            Set<Integer> intSet = new HashSet<>(); // stores the values of MinTerm object
            intSet.add(value);
            List<MinTerm> minTerms = table.get(weight); // gets the existing minterm list corresponding tho the weight
            MinTerm minTermObj = new MinTerm(intSet);
            minTermObj.binaryRepresentation = BitWiseOperations.getBinaryStringRepresentation(value, numOfBits);
            minTerms.add(minTermObj);
        } //Now the input is arranged according to the weights

        //Pairing starts here
        List<List<MinTerm>> oldMatch = new ArrayList<>(table.values());
        List<List<MinTerm>> newMatch; // to find out new match
        // To find the list of prime implicants
        List<MinTerm> primeImplicants = new ArrayList<>();
        do {
            newMatch = Helper.pair(oldMatch);
            // to check if newMatch contains prime implicants
            primeImplicants.addAll(Helper.findPrimeImplicants(oldMatch, newMatch));
            oldMatch = newMatch;
        } while (newMatch.size() > 0); // until we can make pairs

        Helper.printList(primeImplicants,"Prime Implicants:");

        //Now we have found prime implicants
        //Next we'll find essential prime implicants
        Set<MinTerm> essentialPrimeImplicantsList = new HashSet<>();
        for (int element : input) {
            int count = 0;
            if (dontCares.contains(element)) { // to ignore don't cares
                continue;
            }
            MinTerm essentialPrimeImplicant = null;
            for (MinTerm minTerm : primeImplicants) {
                if (minTerm.values.contains(element)) {
                    count++;
                    essentialPrimeImplicant = minTerm;
                }
            }
            if (count == 1) {
                essentialPrimeImplicantsList.add(essentialPrimeImplicant);
            }
        }

        Helper.printList(essentialPrimeImplicantsList,"Essential Prime Implicants:");

        //To find Non Essential Prime Implicants

        Set<MinTerm> nonEssentialPrimeImplicantsList = new HashSet(primeImplicants);
        nonEssentialPrimeImplicantsList.removeAll(essentialPrimeImplicantsList);//removeall internally calls minterms equals method.

        Helper.printList(nonEssentialPrimeImplicantsList,"Non Essential Prime Implicants:");

        //To find the numbers covered under EPIs

        Set<Integer> numsCoveredUnderEPIs = new HashSet<>();
        for (MinTerm minTerm : essentialPrimeImplicantsList) {
            numsCoveredUnderEPIs.addAll(minTerm.values);
        }

        //To find the numbers not covered under EPIs

        Set<Integer> nonCoveredNumsUnderEPIS = new HashSet(input);
        nonCoveredNumsUnderEPIS.removeAll(numsCoveredUnderEPIs);
        nonCoveredNumsUnderEPIS.removeAll(dontCares);

        //To sort the Non Essential Prime Implicant(alpha, beta, gama) in decreasing order (how many numbers it covers)
        List<MinTerm> rankedNonEssentialPrimeImplicants = new ArrayList<>(nonEssentialPrimeImplicantsList);
        Collections.sort(rankedNonEssentialPrimeImplicants); // uses compareTo function defined in MinTerm Class
        //Ex: alpha=4, gama=3, beta=3

        //non covered numbers to Non Essential prime implicants mapping based on how may elements they cover
        //Ex: 0 = alpha,beta; 1= gama;
        Map<Integer, List<MinTerm>> rankMap = new HashMap<>();
        //non covered numbers --> key
        for (int element : nonCoveredNumsUnderEPIS) {
            //
            List<MinTerm> rankedMinterms = new ArrayList<>();
            for (MinTerm minTerm : rankedNonEssentialPrimeImplicants) {
                if (minTerm.values.contains(element)) {
                    rankedMinterms.add(minTerm);
                }
            }
            rankMap.put(element, rankedMinterms);
        }

        Set<MinTerm> selectedNonEssentialPrimeImplicants = new HashSet<>();
        for (int element : rankMap.keySet()) {
            selectedNonEssentialPrimeImplicants.add(rankMap.get(element).get(0));
            //to get the high ranked non essential prime implicant (first in the list) for the element
        }

        Helper.printList(selectedNonEssentialPrimeImplicants,"Selected Non Essential Prime Implicants:");

        List<MinTerm> finalExpressionTerms = new ArrayList<>();
        finalExpressionTerms.addAll(essentialPrimeImplicantsList);
        finalExpressionTerms.addAll(selectedNonEssentialPrimeImplicants);

        String finalExpression = "";
        for (MinTerm minTerm : finalExpressionTerms) {
            String term = minTerm.getExpressionTerm();
            // to avoid getting + before the first term
            if (!finalExpression.isEmpty()) {
                finalExpression = finalExpression + " + ";
            }
            finalExpression = finalExpression + term;
        }

        System.out.print("\nFinal Expression: ");
        System.out.println(finalExpression);

    }
}
