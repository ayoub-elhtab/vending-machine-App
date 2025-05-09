package com.ayoub;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class VendingMachineTest {

    private VendingMachine underTest;
    private Product product;
    private Coin coin;
    
    @BeforeEach
    public void setUp() {
        underTest = new VendingMachine();
        product = new Product(1, "Test Product", 10.0);
        coin = Coin.one;
    }

    @Test
    public void addProduct_ShouldAddNewProductSuccessfully(){
        int quantity = 20;
        underTest.addProduct(product, quantity);
        assertNotNull(product);
    }

    @Test
    public void getProduct_ShouldReturnProduct_WhenExists(){
        int quantity = 20;
        underTest.addProduct(product, quantity);

        Product foundProduct = underTest.getProduct(1);
        assertNotNull(product);
        assertEquals(product, foundProduct);
    }

    @Test
    public void getProduct_ShouldReturnNull_WhenProductDoesNotExists(){
        Product nonExistingProduct = underTest.getProduct(99);
        assertNull(nonExistingProduct);
    }

    @Test
    public void restockProduct_ShouldIncreaseQuantity_WhenProductExists(){
        int quantity = 20;
        underTest.addProduct(product, quantity);
        underTest.restockProduct(1, 5);

        int actual = underTest.getQuantity(1);
        assertEquals(25, actual);
    }

    @Test
    public void restockProduct_ShouldThrowException_WhenProductDoesNotExists(){
        Exception exception = assertThrows(IllegalArgumentException.class, ()->{
            underTest.restockProduct(99, 5);
        });
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    public void reduceProduct_ShouldDecreaseQuantity_WhenProductExists(){
        int quantity = 20;
        underTest.addProduct(product, quantity);
        underTest.reduceProduct(1);

        int actual = underTest.getQuantity(1);
        assertEquals(19, actual);
    }

    @Test
    public void reduceProduct_ShouldThrowException_WhenProductIsOutOfStock(){
        int quantity = 0;
        underTest.addProduct(product, quantity);

        Exception exception = assertThrows(IllegalStateException.class, ()->{
            underTest.reduceProduct(1);
        });
        assertEquals("Out of stock", exception.getMessage());
    }

    @Test
    public void insertCoin_ShouldInsertCoinSuccessfully(){
        underTest.insertCoin(coin);
        assertNotNull(coin);
    }

    @Test
    public void insertCoin_ShouldReturnNull_WhenNoCoinInserted(){
        underTest.insertCoin(null);
        assertNull(null);
    }

    @Test
    public void totalCoinInserted_ShouldReturnTotal(){
        underTest.insertCoin(coin);
        underTest.insertCoin(Coin.five);

        double actual = underTest.totalCoinInserted();
        assertEquals(6.0, actual);
    }

    @Test
    public void totalCoinInserted_ShouldReturnZero_WhenNoCoinInserted(){
        double actual = underTest.totalCoinInserted();
        assertEquals(0.0, actual);
    }

    @Test
    public void totalCoinsInTheMachine_ShouldReturnTotal(){
        underTest.refillCoins(coin, 2);
        underTest.refillCoins(Coin.two, 1);
        underTest.refillCoins(Coin.half, 4);

        // Expected total = ( 2 * 1.0) + 2.0 + (0.5 * 4)
        double actual = underTest.totalCoinsInTheMachine();
        assertEquals(6.0, actual);
    }

    @Test
    public void refillCoins_(){
        underTest.refillCoins(Coin.two, 5);

        // Check total coin value in machine
        double expected = 5 * 2.0; // = 10.0
        double actual = underTest.totalCoinsInTheMachine();
        assertEquals(expected, actual);
    }


    @Test
    public void purchaseProduct_ShouldReturnChangeAndReduceProduct(){
        // add one product
        Product product1 = new Product(2, "Cola", 4.0);
        underTest.addProduct(product1, 1);

        // refill coins in the machine
        underTest.refillCoins(coin, 1);
        underTest.refillCoins(Coin.five, 2);

        // user insert Coin.ten to buy the product that cost 4.0
        underTest.insertCoin(Coin.ten);

        // purchase the product
        List<Coin> change = underTest.purchaseProduct(2);

        // assert change
        double totalChange = change.stream().mapToDouble(Coin::getValue).sum(); // calculate the sum of changes
        assertEquals(6.0, totalChange);

        // product should now be out of stock (admin insert quantity 1 - user buy 1 = 0)
        assertThrows(IllegalStateException.class, ()-> underTest.reduceProduct(2));


        // Verify machine coin stock is now 15.0 (user insert 10.0 + the machine has 2 * 5.0 and 1 * 1.0 - change 6.0)
        double totalCoins = underTest.totalCoinsInTheMachine();
        assertEquals(15.0, totalCoins);
    }

    @Test
    public void purchaseProduct_ShouldThrowException_WhenPurchaseInvalidProduct(){
        underTest.insertCoin(coin);

        Exception exception = assertThrows(IllegalArgumentException.class, ()->{
            underTest.purchaseProduct(99); // this id does not exist
        });
        assertEquals("Invalid product", exception.getMessage());
    }

    @Test
    public void purchaseProduct_ShouldThrowException_WhenProductIsOutOfStock(){
        Product product1 = new Product(2, "Cola", 4.0);

        // quantity = 0
        underTest.addProduct(product1, 0);
        // user insert money
        underTest.insertCoin(Coin.five);

        Exception exception = assertThrows(IllegalStateException.class, () ->{
            // purchase a product that is out of stock
            underTest.purchaseProduct(2);
        });
        assertEquals("Out of stock", exception.getMessage());
    }

    @Test
    public void purchaseProduct_ShouldThrowException_WhenInsufficientFundsInserted(){
        Product product1 = new Product(2, "Cola", 4.0);

        underTest.addProduct(product1, 1);
        // user insert 1.0 to buy a product that cost 4.0
        underTest.insertCoin(coin);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            underTest.purchaseProduct(2);
        });
        assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    public void purchaseProduct_ShouldThrowException_WhenCannotProvideChange() {
        Product product1 = new Product(2, "Cola", 4.0);
        underTest.addProduct(product1, 1);

        // machine has only 1.0 in change
        underTest.refillCoins(coin, 1);
        // user inserts 10.0
        underTest.insertCoin(Coin.ten);

        // Price is 4.0 → needs 6.0 change, but machine can't provide it
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            underTest.purchaseProduct(2);
        });
        assertEquals("Cannot provide exact change", exception.getMessage());
    }
}

