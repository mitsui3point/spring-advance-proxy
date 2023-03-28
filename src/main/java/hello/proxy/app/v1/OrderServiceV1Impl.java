package hello.proxy.app.v1;

import org.springframework.stereotype.Service;

public class OrderServiceV1Impl implements OrderServiceV1 {
    private final OrderRepositoryV1 repository;

    public OrderServiceV1Impl(OrderRepositoryV1 repository) {
        this.repository = repository;
    }

    @Override
    public void orderItem(String itemId) {
        repository.save(itemId);
    }
}
