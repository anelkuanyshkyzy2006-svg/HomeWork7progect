import java.util.*;

interface SortStrategy {
    void sort();
}

class BubbleSort implements SortStrategy {
    public void sort() { System.out.println("Сортировка пузырьком выполнена."); }
}

class SelectionSort implements SortStrategy {
    public void sort() { System.out.println("Сортировка выбором выполнена."); }
}

class SortContext {
    private SortStrategy strategy;
    public void setStrategy(SortStrategy strategy) { this.strategy = strategy; }
    public void execute() { strategy.sort(); }
}

interface Observer {
    void update(String msg);
}

class ConcreteObserver implements Observer {
    private String name;
    public ConcreteObserver(String name) { this.name = name; }
    public void update(String msg) { System.out.println(name + " получил сообщение: " + msg); }
}

class Subject {
    private List<Observer> observers = new ArrayList<>();
    public void add(Observer o) { observers.add(o); }
    public void remove(Observer o) { observers.remove(o); }
    public void notifyObservers(String msg) {
        for (Observer o : observers) o.update(msg);
    }
}

interface Command {
    void execute();
}

class Light {
    public void on() { System.out.println("Light is ON"); }
    public void off() { System.out.println("Light is OFF"); }
}

class LightOnCommand implements Command {
    private Light light;
    public LightOnCommand(Light light) { this.light = light; }
    public void execute() { light.on(); }
}

class LightOffCommand implements Command {
    private Light light;
    public LightOffCommand(Light light) { this.light = light; }
    public void execute() { light.off(); }
}

class RemoteControl {
    private Command command;
    public void setCommand(Command command) { this.command = command; }
    public void pressButton() { command.execute(); }
}

interface State {
    void handle(Player player);
}

class StandingState implements State {
    public void handle(Player player) {
        System.out.println("Player is standing.");
        player.setState(new JumpingState());
    }
}

class JumpingState implements State {
    public void handle(Player player) {
        System.out.println("Player is jumping.");
        player.setState(new StandingState());
    }
}

class Player {
    private State state;
    public Player(State state) { this.state = state; }
    public void setState(State state) { this.state = state; }
    public void action() { state.handle(this); }
}

abstract class Handler {
    protected Handler next;
    public void setNext(Handler next) { this.next = next; }
    public abstract void handle(int request);
}

class ConcreteHandler1 extends Handler {
    public void handle(int request) {
        if (request < 3) System.out.println("Request " + request + " handled by Handler 1");
        else if (next != null) next.handle(request);
    }
}

class ConcreteHandler2 extends Handler {
    public void handle(int request) {
        if (request < 10) System.out.println("Request " + request + " handled by Handler 2");
        else if (next != null) next.handle(request);
    }
}

class ConcreteHandler3 extends Handler {
    public void handle(int request) {
        System.out.println("Request " + request + " handled by Handler 3");
    }
}

class IteratorExample implements Iterator<String> {
    private List<String> items;
    private int index = 0;
    public IteratorExample(List<String> items) { this.items = items; }
    public boolean hasNext() { return index < items.size(); }
    public String next() { return items.get(index++); }
}

interface Mediator {
    void send(String msg, Colleague sender);
}

abstract class Colleague {
    protected Mediator mediator;
    public Colleague(Mediator mediator) { this.mediator = mediator; }
}

class ConcreteColleague extends Colleague {
    private String name;
    public ConcreteColleague(Mediator mediator, String name) {
        super(mediator);
        this.name = name;
    }
    public void send(String msg) {
        System.out.println(name + " sends: " + msg);
        mediator.send(msg, this);
    }
    public void receive(String msg) {
        System.out.println(name + " receives: " + msg);
    }
}

class ConcreteMediator implements Mediator {
    private List<ConcreteColleague> colleagues = new ArrayList<>();
    public void addColleague(ConcreteColleague c) { colleagues.add(c); }
    public void send(String msg, Colleague sender) {
        for (ConcreteColleague c : colleagues)
            if (c != sender) c.receive(msg);
    }
}

public class Main {
    public static void main(String[] args) {
        SortContext context = new SortContext();
        context.setStrategy(new BubbleSort()); context.execute();
        context.setStrategy(new SelectionSort()); context.execute();

        Subject subject = new Subject();
        ConcreteObserver o1 = new ConcreteObserver("Observer1");
        ConcreteObserver o2 = new ConcreteObserver("Observer2");
        subject.add(o1); subject.add(o2);
        subject.notifyObservers("Событие №1");
        subject.remove(o1);
        subject.notifyObservers("Событие №2");

        Light light = new Light();
        RemoteControl remote = new RemoteControl();
        remote.setCommand(new LightOnCommand(light)); remote.pressButton();
        remote.setCommand(new LightOffCommand(light)); remote.pressButton();

        Player player = new Player(new StandingState());
        player.action(); player.action(); player.action();

        Handler h1 = new ConcreteHandler1();
        Handler h2 = new ConcreteHandler2();
        Handler h3 = new ConcreteHandler3();
        h1.setNext(h2); h2.setNext(h3);
        int[] requests = {2, 5, 14};
        for (int r : requests) h1.handle(r);

        List<String> items = Arrays.asList("A", "B", "C");
        IteratorExample it = new IteratorExample(items);
        while (it.hasNext()) System.out.println("Item: " + it.next());

        ConcreteMediator mediator = new ConcreteMediator();
        ConcreteColleague alice = new ConcreteColleague(mediator, "Alice");
        ConcreteColleague bob = new ConcreteColleague(mediator, "Bob");
        mediator.addColleague(alice); mediator.addColleague(bob);
        alice.send("Hi Bob");
        bob.send("Hello Alice");
    }
}





