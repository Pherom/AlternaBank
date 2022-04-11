package com.alternabank.ui.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public abstract class AbstractSingleChoiceForm<K> extends AbstractForm<K> implements SingleChoiceForm<K> {

    private final List<K> orderedChoices;

    protected AbstractSingleChoiceForm(String userInstructions, Set<K> choices) {
        super(userInstructions);
        orderedChoices = new ArrayList<>(choices);
    }

    @Override
    protected boolean validateUserResponse() {
        int choiceNumber;

        try {
            choiceNumber = Integer.parseInt(getUserResponse());
        } catch (NumberFormatException e) {
            return false;
        }

        return choiceNumber > 0 && choiceNumber <= orderedChoices.size();
    }

    @Override
    public void display() {
        if (orderedChoices.isEmpty())
            System.out.println("No available options to choose from!");

        else {
            while (true) {
                IntStream.range(1, orderedChoices.size() + 1).forEach(i -> System.out.printf("%n%d. %s", i, orderedChoices.get(i - 1)));
                System.out.println();
                System.out.println(getUserInstructions());
                readUserResponse();
                if (!validateUserResponse())
                    System.out.println("Invalid user response!");
                else break;
            }
        }
        System.out.println();
    }

    @Override
    public K getResults() {
        if(!orderedChoices.isEmpty())
            return orderedChoices.get(Integer.parseInt(getUserResponse()) - 1);
        else return null;
    }
}
