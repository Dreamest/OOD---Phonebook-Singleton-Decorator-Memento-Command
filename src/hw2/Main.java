package hw2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class Main extends Application implements AddressBookNew1Finals {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// just in case
		/*
		 * if (NUMBER_OF_OBJECTS <= 0) { System.out.println(NUMBER_OF_OBJECTS_LE0);
		 * System.exit(0); }
		 */
		Stage[] stages = new Stage[NUMBER_OF_OBJECTS];
		Scene[] scenes = new Scene[NUMBER_OF_OBJECTS];
		AddressBookPane[] panes = new AddressBookPane[NUMBER_OF_OBJECTS];
		try {
			for (int i = 0; i < 2 + NUMBER_OF_OBJECTS; i++) {
				if (i >= NUMBER_OF_OBJECTS)
					System.err.println(SINGLETON_MESSAGE);
				else {
					panes[i] = AddressBookPane.getInstance();
					scenes[i] = new Scene(panes[i]);
					stages[i] = new Stage();
					stages[i].setTitle(TITLE);
					stages[i].setScene(scenes[i]);
					stages[i].setResizable(true);
					stages[i].show();
					stages[i].setAlwaysOnTop(true);
					stages[i].setOnCloseRequest(event -> {
						AddressBookPane.reduceNumberOfObjects();
					});
				}
			}
		} catch (Exception e) {
			AddressBookPane.resetNumberOfObjects();
		}
	}
}

class AddressBookPane extends GridPane implements AddressBookNew1Finals, AddressBookEvent1 {
	private static int number_of_objects = 0;
	private RandomAccessFile raf;
	private TextField jtfName = new TextField();
	private TextField jtfStreet = new TextField();
	private TextField jtfCity = new TextField();
	private TextField jtfState = new TextField();
	private TextField jtfZip = new TextField();
	private UndoButton jbtUndo;
	private RedoButton jbtRedo;
	private AddButton jbtAdd;
	private FirstButton jbtFirst;
	private NextButton jbtNext;
	private PreviousButton jbtPrevious;
	private LastButton jbtLast;
	private FlowPane jpButton = new FlowPane();
	private ArrayList<CommandButton> cba;
	private Stack<CommandButton.Memento> stack;
	private EventHandler<ActionEvent> ae = e -> ((Command) e.getSource()).Execute();

	private AddressBookPane() {
		try {
			raf = new RandomAccessFile(FILE_NAME, FILE_MODE);
		} catch (IOException ex) {
			System.out.println(ex);
			System.exit(0);
		}

		jtfState.setAlignment(Pos.CENTER_LEFT);
		jtfState.setPrefWidth(25);
		jtfZip.setPrefWidth(60);

		cba = new ArrayList<>();
		stack = new Stack<>();
		cba.add(jbtFirst = new FirstButton(this, false, raf));
		cba.add(jbtNext = new NextButton(this, false, raf));
		cba.add(jbtPrevious = new PreviousButton(this, false, raf));
		cba.add(jbtLast = new LastButton(this, false, raf));
		cba.add(jbtAdd = new AddButton(this, true, raf));
		cba.add(jbtRedo = new RedoButton(this, true, raf, stack));
		cba.add(jbtUndo = new UndoButton(this, true, raf, stack));

		Label state = new Label(STATE);
		Label zp = new Label(ZIP);
		Label name = new Label(NAME);
		Label street = new Label(STREET);
		Label city = new Label(CITY);
		GridPane p1 = new GridPane();
		p1.add(name, 0, 0);
		p1.add(street, 0, 1);
		p1.add(city, 0, 2);
		p1.setAlignment(Pos.CENTER_LEFT);
		p1.setVgap(8);
		p1.setPadding(new Insets(0, 2, 0, 2));
		GridPane.setVgrow(name, Priority.ALWAYS);
		GridPane.setVgrow(street, Priority.ALWAYS);
		GridPane.setVgrow(city, Priority.ALWAYS);
		GridPane adP = new GridPane();
		adP.add(jtfCity, 0, 0);
		adP.add(state, 1, 0);
		adP.add(jtfState, 2, 0);
		adP.add(zp, 3, 0);
		adP.add(jtfZip, 4, 0);
		adP.setAlignment(Pos.CENTER_LEFT);
		GridPane.setHgrow(jtfCity, Priority.ALWAYS);
		GridPane.setVgrow(jtfCity, Priority.ALWAYS);
		GridPane.setVgrow(jtfState, Priority.ALWAYS);
		GridPane.setVgrow(jtfZip, Priority.ALWAYS);
		GridPane.setVgrow(state, Priority.ALWAYS);
		GridPane.setVgrow(zp, Priority.ALWAYS);
		GridPane p4 = new GridPane();
		p4.add(jtfName, 0, 0);
		p4.add(jtfStreet, 0, 1);
		p4.add(adP, 0, 2);
		p4.setVgap(1);
		GridPane.setHgrow(jtfName, Priority.ALWAYS);
		GridPane.setHgrow(jtfStreet, Priority.ALWAYS);
		GridPane.setHgrow(adP, Priority.ALWAYS);
		GridPane.setVgrow(jtfName, Priority.ALWAYS);
		GridPane.setVgrow(jtfStreet, Priority.ALWAYS);
		GridPane.setVgrow(adP, Priority.ALWAYS);
		GridPane jpAddress = new GridPane();
		jpAddress.add(p1, 0, 0);
		jpAddress.add(p4, 1, 0);
		GridPane.setHgrow(p1, Priority.NEVER);
		GridPane.setHgrow(p4, Priority.ALWAYS);
		GridPane.setVgrow(p1, Priority.ALWAYS);
		GridPane.setVgrow(p4, Priority.ALWAYS);
		jpAddress.setStyle(STYLE_COMMAND);
		jpButton.setHgap(5);

		jpButton.setAlignment(Pos.CENTER);
		GridPane.setVgrow(jpButton, Priority.NEVER);
		GridPane.setVgrow(jpAddress, Priority.ALWAYS);
		GridPane.setHgrow(jpButton, Priority.ALWAYS);
		GridPane.setHgrow(jpAddress, Priority.ALWAYS);
		this.setVgap(5);
		this.add(jpAddress, 0, 0);
		this.add(jpButton, 0, 1);

		jbtFirst.setOnAction(ae);
		jbtNext.setOnAction(ae);
		jbtPrevious.setOnAction(ae);
		jbtLast.setOnAction(ae);

		jbtAdd.setOnAction(ae);
		jbtRedo.setOnAction(ae);
		jbtUndo.setOnAction(ae);

		jbtFirst.Execute();
	}

	public void SetName(String text) {
		jtfName.setText(text);
	}

	public void SetStreet(String text) {
		jtfStreet.setText(text);
	}

	public void SetCity(String text) {
		jtfCity.setText(text);
	}

	public void SetState(String text) {
		jtfState.setText(text);
	}

	public void SetZip(String text) {
		jtfZip.setText(text);
	}

	public String GetName() {
		return jtfName.getText();
	}

	public String GetStreet() {
		return jtfStreet.getText();
	}

	public String GetCity() {
		return jtfCity.getText();
	}

	public String GetState() {
		return jtfState.getText();
	}

	public String GetZip() {
		return jtfZip.getText();
	}

	public void clearTextFields() {
		jtfName.setText("");
		jtfStreet.setText("");
		jtfCity.setText("");
		jtfState.setText("");
		jtfZip.setText("");
	}

	public static AddressBookPane getInstance() {
		AddressBookPane ap = new AddressBookPane();
		if (number_of_objects > NUMBER_OF_OBJECTS)
			return null;
		else if (number_of_objects == NUMBER_OF_OBJECTS - NUMBER_OF_SPECIALS) {
			// could be just== 0 but I wanted the top window to be the special one.
			number_of_objects++;
			Decorator.decorate(ap.jpButton, true, ap.cba);
			return ap;
		} else {
			number_of_objects++;
			Decorator.decorate(ap.jpButton, false, ap.cba);
			return ap;
		}
	}

	public static void reduceNumberOfObjects() {
		number_of_objects--;
	}

	public static int getNumberOfObjects() {
		return number_of_objects;
	}

	public static void resetNumberOfObjects() {
		number_of_objects = 0;
	}
}

interface Command {
	public void Execute();
}

class CommandButton extends Button implements Command, AddressBookNew1Finals {
	private boolean update;
	private AddressBookPane p;
	private RandomAccessFile raf;
	private String entry;

	public CommandButton(AddressBookPane pane, boolean update, RandomAccessFile r) {
		super();
		p = pane;
		raf = r;
		this.update = update;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}

	public Memento createMemento() {
		return new Memento(entry);
	}

	public String restoreMemento(Memento m) {
		return m.getEntry();
	}

	public boolean update() {
		return update;
	}

	public AddressBookPane getPane() {
		return p;
	}

	public RandomAccessFile getFile() {
		return raf;
	}

	public void setPane(AddressBookPane p) {
		this.p = p;
	}

	@Override
	public void Execute() {
	}

	public void writeAddress(long position) {
		try {
			getFile().seek(position);
			FixedLengthStringIO1.writeFixedLengthString(getPane().GetName(), NAME_SIZE, getFile());
			FixedLengthStringIO1.writeFixedLengthString(getPane().GetStreet(), STREET_SIZE, getFile());
			FixedLengthStringIO1.writeFixedLengthString(getPane().GetCity(), CITY_SIZE, getFile());
			FixedLengthStringIO1.writeFixedLengthString(getPane().GetState(), STATE_SIZE, getFile());
			FixedLengthStringIO1.writeFixedLengthString(getPane().GetZip(), ZIP_SIZE, getFile());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void readAddress(long position) throws IOException {
		getFile().seek(position);
		String name = FixedLengthStringIO1.readFixedLengthString(NAME_SIZE, getFile());
		String street = FixedLengthStringIO1.readFixedLengthString(STREET_SIZE, getFile());
		String city = FixedLengthStringIO1.readFixedLengthString(CITY_SIZE, getFile());
		String state = FixedLengthStringIO1.readFixedLengthString(STATE_SIZE, getFile());
		String zip = FixedLengthStringIO1.readFixedLengthString(ZIP_SIZE, getFile());
		getPane().SetName(name);
		getPane().SetStreet(street);
		getPane().SetCity(city);
		getPane().SetState(state);
		getPane().SetZip(zip);
	}

	public String getAddress(long position) throws IOException {
		getFile().seek(position);
		String name = FixedLengthStringIO1.readFixedLengthString(NAME_SIZE, getFile());
		String street = FixedLengthStringIO1.readFixedLengthString(STREET_SIZE, getFile());
		String city = FixedLengthStringIO1.readFixedLengthString(CITY_SIZE, getFile());
		String state = FixedLengthStringIO1.readFixedLengthString(STATE_SIZE, getFile());
		String zip = FixedLengthStringIO1.readFixedLengthString(ZIP_SIZE, getFile());
		return name + street + city + state + zip;
	}

	public void setAddress(long position, String address) throws IOException {
		String name = address.substring(0, NAME_SIZE);
		String street = address.substring(NAME_SIZE, STREET_SIZE + NAME_SIZE);
		String city = address.substring(STREET_SIZE + NAME_SIZE, CITY_SIZE + STREET_SIZE + NAME_SIZE);
		String state = address.substring(CITY_SIZE + STREET_SIZE + NAME_SIZE,
				STATE_SIZE + CITY_SIZE + STREET_SIZE + NAME_SIZE);
		String zip = address.substring(STATE_SIZE + CITY_SIZE + STREET_SIZE + NAME_SIZE,
				ZIP_SIZE + STATE_SIZE + CITY_SIZE + STREET_SIZE + NAME_SIZE);

		getPane().SetName(name);
		getPane().SetStreet(street);
		getPane().SetCity(city);
		getPane().SetState(state);
		getPane().SetZip(zip);

		writeAddress(position);

	}

	public class Memento {
		private String entry;

		public Memento(String entry) {
			this.entry = entry;
		}

		private String getEntry() {
			return entry;
		}
	}
}

class UndoButton extends CommandButton {
	private Stack<Memento> stack;

	public UndoButton(AddressBookPane pane, boolean update, RandomAccessFile r, Stack<Memento> stack) {
		super(pane, update, r);
		this.setText(UNDO);
		this.stack = stack;
	}

	@Override
	public void Execute() {
		try {
			if (getFile().length() > 0) {
				long lastEntryPos = getFile().length() - RECORD_SIZE * 2;
				setEntry(getAddress(lastEntryPos));
				stack.push(createMemento());
				getFile().setLength(getFile().length() - RECORD_SIZE * 2);
				if (getFile().length() > 0) {
					readAddress(0);
				} else {
					getPane().clearTextFields();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class RedoButton extends CommandButton {
	private Stack<Memento> stack;

	public RedoButton(AddressBookPane pane, boolean update, RandomAccessFile r, Stack<Memento> stack) {
		super(pane, update, r);
		this.setText(REDO);
		this.stack = stack;
	}

	@Override
	public void Execute() {
		try {
			if (!stack.isEmpty()) {
				String address = restoreMemento(stack.pop());
				setAddress(getFile().length(), address);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class AddButton extends CommandButton {
	public AddButton(AddressBookPane pane, boolean update, RandomAccessFile r) {
		super(pane, update, r);
		this.setText(ADD);
	}

	@Override
	public void Execute() {
		try {
			writeAddress(getFile().length());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class NextButton extends CommandButton {
	public NextButton(AddressBookPane pane, boolean update, RandomAccessFile r) {
		super(pane, update, r);
		this.setText(NEXT);
	}

	@Override
	public void Execute() {
		try {
			long currentPosition = getFile().getFilePointer();
			if (currentPosition < getFile().length())
				readAddress(currentPosition);
			else if (getFile().length() == 0)
				getPane().clearTextFields();
			else if (currentPosition - 2 * RECORD_SIZE == getFile().length()) {
				
				System.out.println("test");
				
				
				readAddress(currentPosition - 2 * 2 * RECORD_SIZE);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

class PreviousButton extends CommandButton {
	public PreviousButton(AddressBookPane pane, boolean update, RandomAccessFile r) {
		super(pane, update, r);
		this.setText(PREVIOUS);
	}

	@Override
	public void Execute() {
		try {
			long currentPosition = getFile().getFilePointer();
			if (getFile().length() == 0)
				getPane().clearTextFields();
			else if (currentPosition - 2 * 2 * RECORD_SIZE >= 0)
				readAddress(currentPosition - 2 * 2 * RECORD_SIZE);

		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

class LastButton extends CommandButton {
	public LastButton(AddressBookPane pane, boolean update, RandomAccessFile r) {
		super(pane, update, r);
		this.setText(LAST);
	}

	@Override
	public void Execute() {
		try {
			long lastPosition = getFile().length();
			if (lastPosition > 0)
				readAddress(lastPosition - 2 * RECORD_SIZE);
			else if (getFile().length() == 0)
				getPane().clearTextFields();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}

class FirstButton extends CommandButton {
	public FirstButton(AddressBookPane pane, boolean update, RandomAccessFile r) {
		super(pane, update, r);
		this.setText(FIRST);
	}

	@Override
	public void Execute() {
		try {
			if (getFile().length() > 0)
				readAddress(0);
			else if (getFile().length() == 0)
				getPane().clearTextFields();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
