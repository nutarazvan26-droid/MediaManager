package ui;

public final class Menu {

    private static Menu instance;
    public Menu GetInstance()
    {
        if(instance == null)
        {
            instance = new Menu();
        }
        return instance;
    }
}
