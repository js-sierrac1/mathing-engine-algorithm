package org.example.oneD;

import org.example.Order;
import org.example.OrderBookI;
import org.example.Side;

import java.util.*;

public class OrderBookUsingList implements OrderBookI {
    private final LinkedList<Order> bids = new LinkedList<>(); // Buy Orders
    private final LinkedList<Order> asks = new LinkedList<>(); // Sell Orders
    private final Map<Integer, Order> orderCache = new HashMap<>();

    @Override
    public Map<Integer, Order> getOrderCache() {
        return Map.copyOf(orderCache);
    }

    @Override
    public void addOrder(Order order) {
        if((order == null) || orderCache.containsKey(order.getId())){
            throw  new IllegalArgumentException("Order is null or already exists");
        }

        matchOrders(order);

        if(order.getQuantity() > 0){
            addOrderToOrderBook(order);
        }
    }

    private void addOrderToOrderBook(final Order order){
        orderCache.put(order.getId(), order);
        if (order.getSide() == Side.BUY){
            bids.addLast(order);
            bids.sort(Comparator.comparingDouble(Order::getPrice)
                    .reversed()
                    .thenComparing(Order::getEntryTime));
        } else {
            asks.addLast(order);
            asks.sort(Comparator.comparingDouble(Order::getPrice)
                    .thenComparing(Order::getEntryTime));
        }
    }

    private void matchOrders(final Order order) {
        final LinkedList<Order> oppositeOrders = (order.getSide() == Side.BUY) ? asks : bids;

        while ((order.getQuantity() > 0)
                && !oppositeOrders.isEmpty()
                && isPriceMatch(order, oppositeOrders.getFirst())) {
            System.out.printf("Aggressive %s order, matching with %s orders%n",
                    order.getSide() == Side.BUY ? "buy" : "sell",
                    order.getSide() == Side.BUY ? "sell" : "buy");

            final Order oppositeOrder = oppositeOrders.getFirst(); // O(1)
            final int fillQuantity = Math.min(order.getQuantity(), oppositeOrder.getQuantity());

            System.out.printf("Matched Order: %d @ %.2f%n", fillQuantity, oppositeOrder.getPrice());

            order.setQuantity(order.getQuantity() - fillQuantity);
            oppositeOrder.setQuantity(oppositeOrder.getQuantity() - fillQuantity);

            if (oppositeOrder.getQuantity() == 0) {
                oppositeOrders.removeFirst(); // O(1)
            }
        }
    }

    private boolean isPriceMatch(final Order order, final Order oppositeOrder) {
        return (order.getSide() == Side.BUY && order.getPrice() >= oppositeOrder.getPrice())
                || (order.getSide() == Side.SELL && order.getPrice() <= oppositeOrder.getPrice());
    }

    @Override
    public void printLevel1() {
        System.out.println("--------------------------");

        System.out.println("BEST ASK~>");
        if (!asks.isEmpty()) {
            System.out.printf("%s%n", asks.getFirst()); // O(1)
        }

        System.out.println("--------------------------");

        System.out.println("BEST BID~>");
        if (!bids.isEmpty()) {
            System.out.printf("%s%n", bids.getFirst()); // O(1)
        }

        System.out.println("--------------------------");
    }

    @Override
    public void printOrderBook() {
        System.out.println("--------------------------");
        System.out.println("SELL Orders:");
        reverseList(asks).forEach(System.out::println); // O(n)
        System.out.println("\nBUY Orders:");
        bids.forEach(System.out::println); // O(n)
        System.out.println("--------------------------");
    }

    private List<Order> reverseList(final List<Order> orders) {
        final List<Order> result = new ArrayList<>(orders);
        Collections.reverse(result);
        return result;
    }

    @Override
    public void cancelOrder(int orderId) {
        if (orderCache.containsKey(orderId)) {
            final Order order = orderCache.get(orderId);
            removeOrder(order);
            System.out.printf("Canceled: %s%n", order);
        } else {
            System.err.printf("Order ID not found: %d%n", orderId);
        }
    }

    @Override
    public void amendOrder(int orderId, int qtyNew) {
        // Pre-conditions
        if (!orderCache.containsKey(orderId) || (qtyNew < 1)) {
            throw new IllegalArgumentException("Order does not exists or incorrect qty");
        }

        final Order order = orderCache.get(orderId);
        order.setQuantity(qtyNew); // O(1)

        removeAndAddOrder(order);
        System.out.printf("Order amended: %s%n", order);
    }

    @Override
    public void amendOrder(int orderId, double pxNew) {
        // Pre-conditions
        if (!orderCache.containsKey(orderId) || (pxNew < 1)) {
            throw new IllegalArgumentException("Order does not exists or incorrect price");
        }

        final Order order = orderCache.get(orderId);
        order.setPrice(pxNew); // O(1)

        removeAndAddOrder(order);
        System.out.printf("Order amended: %s%n", order);
    }

    @Override
    public void amendOrder(int orderId, int qtyNew, double pxNew) {
        // Pre-conditions
        if (!orderCache.containsKey(orderId) || (qtyNew < 1) || (pxNew < 1)) {
            throw new IllegalArgumentException("Order does not exists or incorrect qty / price");
        }

        final Order order = orderCache.get(orderId);
        order.setQuantity(qtyNew); // O(1)
        order.setPrice(pxNew); // O(1)

        removeAndAddOrder(order);
        System.out.printf("Order amended: %s%n", order);
    }

    private void removeAndAddOrder(final Order order) {
        removeOrder(order); // O(n)
        addOrder(order); // O(n * log(n))
    }

    private void removeOrder(final Order order) {
        orderCache.remove(order.getId()); // O(1)
        if (order.getSide() == Side.BUY) {
            bids.remove(order); // O(n)
        } else {
            asks.remove(order); // O(n)
        }
    }
}
