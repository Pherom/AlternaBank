package com.alternabank.ui.form;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public abstract class AbstractMultipleChoiceForm<K> extends AbstractForm<Set<K>> implements MultipleChoiceForm<K>{

    private final List<K> orderedChoices;
    private final Set<K> userSelections = new HashSet<>();
    private boolean confirmed = false;

    protected AbstractMultipleChoiceForm(String userInstructions, Set<K> choices) {
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

        return choiceNumber > 0 && choiceNumber <= orderedChoices.size() + 3;
    }

    @Override
    public void display() {
        if (orderedChoices.isEmpty())
            System.out.println("No available options to choose from!");
        else {
            while (true) {
                System.out.printf("%n%d. SELECT ALL%n%d. SELECT NONE%n%d. CONFIRM%n", 1, 2, 3);
                IntStream.range(4, orderedChoices.size() + 4).forEach(i -> System.out.printf("%n%d. %s", i, orderedChoices.get(i - 4)));
                System.out.printf("%n%nSELECTED:%n");
                userSelections.forEach(this::displayUserSelection);
                System.out.println();
                System.out.println(getUserInstructions());
                readUserResponse();

                if (!validateUserResponse())
                    System.out.println("Invalid user response!");

                else {
                    int choice = Integer.parseInt(getUserResponse());
                    if (choice == 1) {
                        userSelections.addAll(orderedChoices);
                        orderedChoices.clear();
                    } else if (choice == 2) {
                        orderedChoices.addAll(userSelections);
                        userSelections.clear();
                    } else if (choice == 3)
                        confirmed = true;
                    else {
                        userSelections.add(orderedChoices.remove(choice - 4));
                    }

                    if (confirmed) {
                        if (userSelections.isEmpty()) {
                            confirmed = false;
                            System.out.println("At least one item must be selected before confirmation!");
                        } else break;
                    }
                }
            }
        }
        System.out.println();
    }

    @Override
    public Set<K> getResults() {
        return userSelections;
    }
}
