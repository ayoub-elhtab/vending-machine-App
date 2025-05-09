package com.ayoub;

import java.util.*;

public class VendingMachine {
    private final Map<Integer, Product> productStock = new HashMap<>();
    private final Map<Integer, Integer> productQuantities = new HashMap<>();

    private final Map<Coin, Integer> coinStock = new EnumMap<>(Coin.class);
    private final List<Coin> insertedCoins = new ArrayList<>();

    public VendingMachine() {
        // initialize coin stock with zero for each type
        for (Coin c : Coin.values()) {
            coinStock.put(c, 0);
        }
    }

    // Product Management
    public void addProduct(Product product, int quantity) {
        productStock.put(product.getId(), product);
        productQuantities.put(product.getId(), productQuantities.getOrDefault(product.getId(), 0) + quantity);
    }

    public Product getProduct(int id) {
        return productStock.get(id);
    }

    public int getQuantity(int id) {
        return productQuantities.getOrDefault(id, 0);
    }

    public void restockProduct(int id, int quantity) {
        if (!productStock.containsKey(id)) throw new IllegalArgumentException("Product not found");
        productQuantities.put(id, productQuantities.get(id) + quantity);
    }

    public void reduceProduct(int id) {
        int current = productQuantities.getOrDefault(id, 0);
        if (current == 0) throw new IllegalStateException("Out of stock");
        productQuantities.put(id, current - 1);
    }

    // Coin Management
    public void insertCoin(Coin coin) {
        insertedCoins.add(coin);
    }

    public double totalCoinInserted() {
        return insertedCoins.stream().mapToDouble(Coin::getValue).sum();
    }

    public double totalCoinsInTheMachine() {
        return coinStock.entrySet().stream()
                .mapToDouble(e -> e.getKey().getValue() * e.getValue())
                .sum();
    }

    public void refillCoins(Coin coin, int quantity) {
        coinStock.put(coin, coinStock.getOrDefault(coin, 0) + quantity);
    }


    // Purchase Flow
    public List<Coin> purchaseProduct(int productId) {
        Product p = getProduct(productId);
        if (p == null) throw new IllegalArgumentException("Invalid product");

        int currentQty = productQuantities.getOrDefault(productId, 0);
        if (currentQty <= 0) throw new IllegalStateException("Out of stock");

        double price = p.getPrice();
        double paid = totalCoinInserted();
        if (paid < price) throw new IllegalStateException("Insufficient funds");

        // Add inserted coins to machine stock
        for (Coin c : insertedCoins) {
            coinStock.put(c, coinStock.getOrDefault(c, 0) + 1);
        }

        // Update product inventory
        reduceProduct(productId);

        // Compute change and deduct coins from machine
        double changeAmount = paid - price;
        List<Coin> change = new ArrayList<>();
        int remaining = (int) Math.round(changeAmount * 100);

        for (Coin c : Coin.sortedDescending()) {
            int coinValue = (int) Math.round(c.getValue() * 100);
            int available = coinStock.getOrDefault(c, 0);
            while (remaining >= coinValue && available > 0) {
                remaining -= coinValue;
                available--;
                change.add(c);
            }
            coinStock.put(c, available);
        }

        if (remaining != 0) throw new IllegalStateException("Cannot provide exact change");

        insertedCoins.clear();
        return change;
    }

    
}
