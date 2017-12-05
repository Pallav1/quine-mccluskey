package main;

import java.util.Set;

//implementing Comparable interface and compareTo function is defined below
public class MinTerm implements Comparable<MinTerm> {
    Set<Integer> values;    // MinTerm values
    String binaryRepresentation;

//constructor
    public MinTerm(Set<Integer> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        MinTerm minTerm = (MinTerm) o;
        //type casting to Minterm object

        return this.values.equals(minTerm.values);
    }

    @Override
    public int hashCode() {
        return values != null ? values.hashCode() : 0;
    }

    @Override
    //descending order sorting
    //a.compareTo(b) --> this operator ll point to a, o will point to b
    public int compareTo(MinTerm o) {
        if (o.values.size() >= this.values.size()) {
            return 1;
        } else {
            return -1;
        }
    }

    public String getExpressionTerm() {
        Character ch = 'A';
        Character bar = '\'';
        String expressionTerm = "";
        for (Character c : this.binaryRepresentation.toCharArray()) {
            if (c == '0') {
                expressionTerm = expressionTerm + ch + bar;
            } else if (c == '1') {
                expressionTerm = expressionTerm + ch;
            } //'d' gets ignored.
            ch++;
        }
        return expressionTerm;
    }

    @Override
    public String toString() {
        return values.toString() + " : " + binaryRepresentation;
    }
}
