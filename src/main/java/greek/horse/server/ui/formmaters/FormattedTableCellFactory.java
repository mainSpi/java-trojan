package greek.horse.server.ui.formmaters;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.text.TextAlignment;
import javafx.util.Callback;

public class FormattedTableCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

    private final TextAlignment textAlignment;

    public FormattedTableCellFactory(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
    }

    public FormattedTableCellFactory() {
        textAlignment = TextAlignment.CENTER;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TableCell<S, T> call(TableColumn<S, T> p) {
        TableCell<S, T> cell = new TableCell<S, T>() {

            @Override
            public void updateItem(Object item, boolean empty) {
                if (item == getItem()) {
                    return;
                }
                super.updateItem((T) item, empty);
                if (item == null) {
                    super.setText(null);
                    super.setGraphic(null);
                } else if (item instanceof Node) {
                    super.setText(null);
                    super.setGraphic((Node) item);
                } else {
                    super.setText((String) item);
                    super.setGraphic(null);
                }
            }
        };
        cell.setTextAlignment(this.textAlignment);
        cell.setAlignment(this.textAlignment.equals(TextAlignment.CENTER)?Pos.CENTER:Pos.CENTER_LEFT);

        return cell;
    }


}