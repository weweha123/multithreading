import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BarberShop {
    private final int chairs;
    private int waiting;
    private boolean sleeping;
    private final Lock lock = new ReentrantLock();
    private final Condition barberSleep = lock.newCondition();
    private final Condition customerWait = lock.newCondition();
    private static long currentTime = 28800000;

    public BarberShop(int chairs) {
        this.chairs = chairs;
        this.waiting = 0;
        this.sleeping = true;
    }

    public void addCustomer() throws InterruptedException {
        lock.lock();
        try {
            if (waiting < chairs) {
                waiting++;
                System.out.println("Клієнт увійшов. Очікування: " + waiting);
                if (sleeping) {
                    System.out.println("Розбудив перукаря.");
                    barberSleep.signal();
                }
                customerWait.await();
            } else {
                System.out.println("Клієнт пішов, стільців немає.");
            }
        } finally {
            lock.unlock();
        }
    }

    public void startHaircut() throws InterruptedException {
        lock.lock();
        try {
            if (waiting > 0) {
                waiting--;
                System.out.println("Перукар починає стрижку. " + "Очікування: " + waiting);
                customerWait.signal();
            } else {
                System.out.println("Перукар засинає.");
                sleeping = true;
                barberSleep.await();
                sleeping = false;
                System.out.println("Перукар прокидається.");
            }
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        System.out.println("Барбер відчиняється");
        Thread timeThread = new Thread(() -> {
            while (currentTime < 64800000) {
                try {
                    System.out.println(getCurrentTime());
                    Thread.sleep(8000);
                    currentTime += 7200000;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        timeThread.start();
        BarberShop shop = new BarberShop(3);
        Thread barberThread = new Thread(new Barber(shop));
        barberThread.start();



        while (currentTime < 64800000) {
            Thread customerThread = new Thread(new Customer(shop));
            customerThread.start();
            try {
                Thread.sleep(new Random().nextInt(7500) + 700);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println(getCurrentTime());
        timeThread.interrupt();
        barberThread.interrupt();

        System.out.println("Барбер зачиняється");
    }

    public static synchronized String getCurrentTime() {
        long hours = (currentTime / (1000 * 60 * 60)) % 24;
        long minutes = (currentTime / (1000 * 60)) % 60;
        long seconds = (currentTime / 1000) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
