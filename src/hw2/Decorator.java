package hw2;

import java.util.ArrayList;

import javafx.scene.layout.FlowPane;

public abstract class Decorator {
	public static void decorate(FlowPane jpButton, boolean update, ArrayList<CommandButton> cba) {
		if (update) {
			for (int i = 0; i < cba.size(); i++) {
				jpButton.getChildren().add(cba.get(i));
			}
		} else {
			for (int i = 0; i < cba.size(); i++) {
				if (!cba.get(i).update()) {
					jpButton.getChildren().add(cba.get(i));
					cba.get(i);
				}
			}
		}
	}

}
