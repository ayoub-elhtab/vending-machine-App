package com.ayoub;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

        int totalQuantity = underTest.getQuantity(1);
        assertEquals(25,totalQuantity );
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

        int totalQuantity = underTest.getQuantity(1);
        assertEquals(19,totalQuantity );
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

        double total = underTest.totalCoinInserted();
        assertEquals(6.0, total);
    }

    @Test
    public void totalCoinInserted_ShouldReturnZero_WhenNoCoinInserted(){
        double total = underTest.totalCoinInserted();
        assertEquals(0.0, total);
    }
    


}
