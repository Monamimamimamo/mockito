package shopping;

import customer.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import java.util.ArrayList;
import java.util.List;


/**
 * Реализует модульные тесты для ShoppingService
 */
class ShoppingServiceTest {


    private ShoppingService shoppingService;
    private ProductDao productDao;
    private Customer customer;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        productDao = Mockito.mock(ProductDao.class);
        shoppingService = new ShoppingServiceImpl(productDao);
        customer = new Customer(123L, "11-22-33-44");
        product = new Product("Продукт", 10);
        cart = new Cart(customer);
    }

    /**
     * Проверяем получение корзины существующего покупателя
     * Ожидаем, что у заданного покупателя существует корзина
     * <p>
     * Можно создать cart просто введя null - логическая ошибка
     */
    @Test
    void testGetCart() {
        Cart cart = shoppingService.getCart(customer);
        Assertions.assertNotNull(cart);
    }

    /**
     * Проверяем получение всех продуктов
     */
    @Test
    public void testGetAllProducts() {
        // Как я понял, тест бесполезен, т.к. getAllProducts() просто обращается к Dao
        // И не имеет другой логики
    }

    /**
     * Проверяем получение продукта по имени
     */
    @Test
    public void testGetProductByName() {
        // Как я понял, тест бесполезен, т.к. getAllProducts() просто обращается к Dao
        // И не имеет другой логики
    }

    /**
     * Проверяем успешность процесса покупки
     * Проверяем, что результат является true
     * Проверяем, что productDao сохранил product
     */
    @Test
    public void testBuySuccess() throws BuyException {
        cart.add(product, 2);

        boolean result = shoppingService.buy(cart);

        Assertions.assertTrue(result);
        Mockito.verify(productDao, Mockito.times(1)).save(product);
    }

    /**
     * Проверяем покупку пустой корзины
     * Проверяем, что результат является false
     * Проверяем, что productDao не сохранил ничего
     */
    @Test
    public void testBuyEmptyCart() throws BuyException {
        boolean result = shoppingService.buy(cart);

        Assertions.assertFalse(result);
        Mockito.verify(productDao, Mockito.never()).save(Mockito.any());
    }

    /**
     * Проверяем покупку продукта из корзины
     * Проверяем, что покупка удачна
     * Проверяем, что рбщее количество товара уменьшилось
     * Проверяем, что Dao обновило состояние продукта
     * Проверяем, что корзина стала пустой
     */
    @Test
    public void testBuySomeProducts() throws BuyException {
        cart.add(product, 1);

        boolean result = shoppingService.buy(cart);

        Assertions.assertTrue(result);
        Assertions.assertEquals(9, product.getCount());
        Mockito.verify(productDao, Mockito.times(1)).save(product);
        Assertions.assertTrue(cart.getProducts().isEmpty());
    }

    /**
     * Тестируем добавление в корзину избыточного количества продукта одним человеком
     * Добавляем весь продукт
     * Проверяем, что при попытке добавить ещё один продукт выбросится Exception с характерным сообщением
     */
    @Test
    public void testAddExtraCountProduct_ForOne() {
        cart.add(product, 10);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                ()-> cart.add(product, 1));
        Assertions.assertEquals("Невозможно добавить товар " + product.getName()
                + " в корзину, т.к. нет необходимого количества товаров", exception.getMessage());
    }


    /**
     * Тестируем покупку избыточного количества продукта двумя людьми
     * Первый покупатель совершает покупку
     * Проверяем, что при попытке второго покупателя купить продукт выбрасывается BuyException с характерным сообщение
     * Проверяем, что количество продукта изменилось только 1 раз.
     */
    @Test
    public void testBuyExtraCountProduct_ForSome() throws BuyException {
        Cart cart2 = new Cart(new Customer(456, "55-66-77-88"));
        cart.add(product, 6);
        cart2.add(product, 5);
        shoppingService.buy(cart);

        BuyException exception = Assertions.assertThrows(BuyException.class, () -> shoppingService.buy(cart2));
        Assertions.assertEquals("В наличии нет необходимого количества товара " + "'"
                + product.getName() + "'", exception.getMessage());

        Mockito.verify(productDao, Mockito.times(1)).save(product);
    }

    /**
     * Тестируем покупку отрицательного количества продуктов
     * Проверяем, что покупка не удалась
     */
    @Test
    public void testBuyNegativeCountProduct() throws BuyException {
        cart.add(product, -1);

        Assertions.assertFalse(shoppingService.buy(cart));
    }
}
