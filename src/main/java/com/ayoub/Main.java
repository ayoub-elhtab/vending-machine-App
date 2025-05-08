package com.ayoub;

import java.util.List;

public class Main {
    public static void main(String[] args) {

        VendingMachine vm = new VendingMachine();
        // stock products
        vm.addProduct(new Product(1, "Water", 5.0), 10);
        vm.addProduct(new Product(2, "Wafers", 2.0), 15);
        // refill coins using user enum
        vm.refillCoins(Coin.ten, 2);    // $20 total
        vm.refillCoins(Coin.five, 4);   // $20 total
        vm.refillCoins(Coin.two, 5);    // $10 total
        vm.refillCoins(Coin.one, 10);   // $10 total
        vm.refillCoins(Coin.half, 20);  // $10 total

        // user inserts coins: $3.50 (one + two + half)
        vm.insertCoin(Coin.one);
        vm.insertCoin(Coin.two);
        vm.insertCoin(Coin.half);
        // user buys Water ($5.00) -> insufficient funds example
        try {
            vm.purchaseProduct(1);
        } catch (Exception e) {
            System.out.println(e.getMessage()); // Insufficient funds
        }
        // now insert more cash
        vm.insertCoin(Coin.five);
        // purchase Water
        List<Coin> change = vm.purchaseProduct(1);
        System.out.println("Change returned: " + change);
        // expected: 0.50 + 0.5 coins if stock allows
    }

}