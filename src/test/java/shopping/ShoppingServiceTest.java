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

    @BeforeEach
    void setUp() {
        productDao = Mockito.mock(ProductDao.class);
        shoppingService = new ShoppingServiceImpl(productDao);
        customer = new Customer(123L, "11-22-33-44");
        product = new Product("Продукт", 10);
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
     * Обязуем productDao возвращать список существующих продуктов
     * Проверяем, что результат совпадает со списком продуктов из productDao
     * Проверяем, что productDao.getAll() вызвался ровно 1 раз
     */
    @Test
    public void testGetAllProducts() {
        List<Product> products = List.of(product);
        Mockito.when(productDao.getAll()).thenReturn(products);

        List<Product> result = shoppingService.getAllProducts();

        Assertions.assertEquals(products, result);
        Mockito.verify(productDao, Mockito.times(1)).getAll();
    }

    /**
     * Проверяем получение продукта по имени
     * Обязуем productDao возвращать существующий product по имени "Продукт"
     * Проверяем, что результат совпадает с product из productDao
     * Проверяем, что productDao.getByName() с параметром "Продукт" вызвался ровно 1 раз
     */
    @Test
    public void testGetProductByName() {
        Mockito.when(productDao.getByName("Продукт")).thenReturn(product);

        Product result = shoppingService.getProductByName("Продукт");

        Assertions.assertEquals(product, result);
        Mockito.verify(productDao, Mockito.times(1)).getByName("Продукт");
    }

    /**
     * Проверяем успешность процесса покупки
     * Проверяем, что результат является true
     * Проверяем, что productDao сохранил product
     */
    @Test
    public void testBuySuccess() throws BuyException {
        Cart cart = shoppingService.getCart(customer);
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
        Cart cart = shoppingService.getCart(customer);

        boolean result = shoppingService.buy(cart);

        Assertions.assertFalse(result);
        Mockito.verify(productDao, Mockito.never()).save(Mockito.any());
    }

    /**
     * Тестирует возникновении ошибки, если при покупке недостаточно товара.
     * Нет смысла тестировать, т.к. при покупке количество товара не уменьшается,
     * а в Cart нельзя добавить товара больше, чем есть.
     * Является  логической ошибкой.
     */
    @Test
    public void testBuyException() {
    }

    /**
     * Неотносящееся к ShoppingService: в классе Cart метод validateCount сравнивает
     * количество товара и количество добавляемого в корзину, не учитывая,
     * что какая-то часть товара уже могла быть добавлена.
     * Является логичесской ошибкой
     */

    /**
     * Неотносящееся к ShoppingService: корзина не учитывает изменение количества продукта.
     * Является логичесской ошибкой
     */

    /**
     * Неотносящееся к ShoppingService: можно добавить null товар в Cart
     * Является логичесской ошибкой
     */
}
