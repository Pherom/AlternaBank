package com.alternabank.client.util;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class DoubleCellFactory<CellData> implements Callback<TableColumn<CellData, Double>, TableCell<CellData, Double>> {
    @Override
    public TableCell<CellData, Double> call(TableColumn<CellData, Double> param) {
        return new TableCell<CellData, Double>() {
            @Override
            protected void updateItem(Double d, boolean empty) {
                super.updateItem(d, empty);
                if (!empty)
                    setText(String.format("%.2f", d));
                else setText(null);
            }
        };
    }
}
