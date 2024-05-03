public class Barber implements Runnable {
    private final BarberShop shop;

    public Barber(BarberShop shop) {
        this.shop = shop;
    }

    @Override
    public void run() {
        try {
            while (true) {
                shop.startHaircut();
                Thread.sleep(4000); // Simulate haircut time
                System.out.println("Перукар закінчив стрижку!");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
