public class Customer implements Runnable {
    private final BarberShop shop;

    public Customer(BarberShop shop) {
        this.shop = shop;
    }

    @Override
    public void run() {
        try {
            shop.addCustomer();
            Thread.sleep(1000); // Simulate haircut time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
