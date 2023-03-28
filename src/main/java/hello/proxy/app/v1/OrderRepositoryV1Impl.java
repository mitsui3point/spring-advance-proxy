package hello.proxy.app.v1;

import org.springframework.stereotype.Repository;

public class OrderRepositoryV1Impl implements OrderRepositoryV1 {
    @Override
    public void save(String itemId) {
        if ("ex".equals(itemId)) {
            throw new IllegalArgumentException("예외 발생");
        }
        sleep(1000);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
