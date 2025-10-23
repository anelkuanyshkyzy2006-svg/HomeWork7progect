import java.util.*;

public class PatternsModule07 {
    interface ICommand {
        void execute();
        void undo();
    }

    static class Light {
        private boolean isOn = false;
        public void on() { isOn = true; System.out.println("Light: ON"); }
        public void off() { isOn = false; System.out.println("Light: OFF"); }
    }

    static class Door {
        private boolean isOpen = false;
        public void open() { isOpen = true; System.out.println("Door: OPEN"); }
        public void close() { isOpen = false; System.out.println("Door: CLOSED"); }
    }

    static class Thermostat {
        private int temperature = 22;
        public void increase(int delta) { temperature += delta; System.out.println("Thermostat: " + temperature + "°C"); }
        public void decrease(int delta) { temperature -= delta; System.out.println("Thermostat: " + temperature + "°C"); }
    }

    static class TV {
        private boolean isOn = false;
        public void on() { isOn = true; System.out.println("TV: ON"); }
        public void off() { isOn = false; System.out.println("TV: OFF"); }
    }

    static class LightOnCommand implements ICommand {
        private Light light;
        public LightOnCommand(Light light) { this.light = light; }
        public void execute() { light.on(); }
        public void undo() { light.off(); }
    }

    static class LightOffCommand implements ICommand {
        private Light light;
        public LightOffCommand(Light light) { this.light = light; }
        public void execute() { light.off(); }
        public void undo() { light.on(); }
    }

    static class DoorOpenCommand implements ICommand {
        private Door door;
        public DoorOpenCommand(Door door) { this.door = door; }
        public void execute() { door.open(); }
        public void undo() { door.close(); }
    }

    static class DoorCloseCommand implements ICommand {
        private Door door;
        public DoorCloseCommand(Door door) { this.door = door; }
        public void execute() { door.close(); }
        public void undo() { door.open(); }
    }

    static class TempIncreaseCommand implements ICommand {
        private Thermostat thermostat;
        private int delta;
        public TempIncreaseCommand(Thermostat t, int delta) { this.thermostat = t; this.delta = delta; }
        public void execute() { thermostat.increase(delta); }
        public void undo() { thermostat.decrease(delta); }
    }

    static class TempDecreaseCommand implements ICommand {
        private Thermostat thermostat;
        private int delta;
        public TempDecreaseCommand(Thermostat t, int delta) { this.thermostat = t; this.delta = delta; }
        public void execute() { thermostat.decrease(delta); }
        public void undo() { thermostat.increase(delta); }
    }

    static class TVOnCommand implements ICommand {
        private TV tv;
        public TVOnCommand(TV tv) { this.tv = tv; }
        public void execute() { tv.on(); }
        public void undo() { tv.off(); }
    }

    static class TVOffCommand implements ICommand {
        private TV tv;
        public TVOffCommand(TV tv) { this.tv = tv; }
        public void execute() { tv.off(); }
        public void undo() { tv.on(); }
    }

    static class Invoker {
        private final Deque<ICommand> history = new ArrayDeque<>();
        private final int historyLimit;
        public Invoker(int historyLimit) { this.historyLimit = Math.max(1, historyLimit); }
        public void executeCommand(ICommand command) {
            if (command == null) return;
            command.execute();
            history.push(command);
            while (history.size() > historyLimit) history.removeLast();
        }
        public void undoLast() {
            if (history.isEmpty()) { System.out.println("Undo error: нет выполненных команд для отмены."); return; }
            ICommand cmd = history.pop();
            cmd.undo();
        }
        public void undoMultiple(int count) {
            if (count <= 0) return;
            for (int i = 0; i < count; i++) {
                if (history.isEmpty()) { System.out.println("Undo: больше нет команд."); break; }
                undoLast();
            }
        }
    }

    static abstract class Beverage {
        public final void prepareRecipe() {
            boilWater();
            brewOrSteep();
            pourInCup();
            if (customerWantsCondiments()) addCondiments();
            else System.out.println("No condiments added.");
        }
        protected void boilWater() { System.out.println("Boiling water"); }
        protected abstract void brewOrSteep();
        protected void pourInCup() { System.out.println("Pouring into cup"); }
        protected boolean customerWantsCondiments() { return true; }
        protected abstract void addCondiments();
    }

    static class Tea extends Beverage {
        protected void brewOrSteep() { System.out.println("Steeping the tea"); }
        protected void addCondiments() { System.out.println("Adding lemon"); }
    }

    static class Coffee extends Beverage {
        private String userInput = "yes";
        public void setUserInput(String input) { this.userInput = input; }
        protected void brewOrSteep() { System.out.println("Brewing the coffee"); }
        protected void addCondiments() { System.out.println("Adding milk and sugar"); }
        protected boolean customerWantsCondiments() {
            if (userInput == null) return false;
            String normalized = userInput.trim().toLowerCase();
            if ("yes".equals(normalized) || "y".equals(normalized)) return true;
            if ("no".equals(normalized) || "n".equals(normalized)) return false;
            System.out.println("Warning: некорректный ввод '" + userInput + "'. По умолчанию — без добавок.");
            return false;
        }
    }

    static class HotChocolate extends Beverage {
        protected void brewOrSteep() { System.out.println("Mixing chocolate powder"); }
        protected void addCondiments() { System.out.println("Adding marshmallows"); }
        private boolean wantCondiments = true;
        public void setWantCondiments(boolean v) { wantCondiments = v; }
        protected boolean customerWantsCondiments() { return wantCondiments; }
    }

    interface IMediator {
        void register(User user);
        void unregister(User user);
        void send(String message, User from, String toUser);
    }

    static class ChatRoom implements IMediator {
        private final Map<String, User> users = new HashMap<>();
        public void register(User user) {
            if (user == null) return;
            if (!users.containsKey(user.getName())) {
                users.put(user.getName(), user);
                user.setMediator(this);
                broadcastSystemMessage(user.getName() + " присоединился к чату.");
            }
        }
        public void unregister(User user) {
            if (user == null) return;
            if (users.remove(user.getName()) != null) broadcastSystemMessage(user.getName() + " покинул чат.");
        }
        public void send(String message, User from, String toUser) {
            if (from == null) return;
            if (!users.containsKey(from.getName())) { from.receive("Ошибка: вы не в чате."); return; }
            if (toUser == null || toUser.isEmpty()) {
                for (User u : users.values())
                    if (!u.getName().equals(from.getName()))
                        u.receive(from.getName() + ": " + message);
            } else {
                User recipient = users.get(toUser);
                if (recipient != null) recipient.receive("(личное) " + from.getName() + ": " + message);
                else from.receive("Ошибка: пользователь '" + toUser + "' не найден.");
            }
        }
        private void broadcastSystemMessage(String msg) {
            for (User u : users.values()) u.receive("[Система]: " + msg);
        }
    }

    static class User {
        private final String name;
        private IMediator mediator;
        public User(String name) { this.name = name; }
        public String getName() { return name; }
        public void setMediator(IMediator mediator) { this.mediator = mediator; }
        public void send(String message) { send(message, null); }
        public void send(String message, String toUser) {
            if (mediator == null) { System.out.println(name + " не подключен к чату."); return; }
            mediator.send(message, this, toUser);
        }
        public void receive(String message) { System.out.println(name + " получает: " + message); }
    }

    public static void main(String[] args) {
        System.out.println("=== Command Pattern ===");
        Light light = new Light();
        Door door = new Door();
        Thermostat thermostat = new Thermostat();
        TV tv = new TV();
        Invoker invoker = new Invoker(10);
        invoker.executeCommand(new LightOnCommand(light));
        invoker.executeCommand(new DoorOpenCommand(door));
        invoker.executeCommand(new TempIncreaseCommand(thermostat, 3));
        invoker.executeCommand(new TVOnCommand(tv));
        invoker.undoLast();
        invoker.undoLast();
        invoker.undoMultiple(5);

        System.out.println("\n=== Template Method ===");
        Tea tea = new Tea();
        System.out.println("Preparing tea:");
        tea.prepareRecipe();
        Coffee coffee = new Coffee();
        coffee.setUserInput("no");
        System.out.println("\nPreparing coffee:");
        coffee.prepareRecipe();
        HotChocolate choc = new HotChocolate();
        choc.setWantCondiments(true);
        System.out.println("\nPreparing hot chocolate:");
        choc.prepareRecipe();

        System.out.println("\n=== Mediator Pattern ===");
        ChatRoom chat = new ChatRoom();
        User alice = new User("Alice");
        User bob = new User("Bob");
        User cathy = new User("Cathy");
        chat.register(alice);
        chat.register(bob);
        chat.register(cathy);
        alice.send("Привет всем!");
        bob.send("Привет, Alice!", "Alice");
        User dave = new User("Dave");
        dave.send("Я не в чате");
        chat.unregister(cathy);
        System.out.println("\nDemo finished.");
    }
}
