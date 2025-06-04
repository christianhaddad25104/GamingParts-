public class Product {
    public String productId;
    public String name;
    public double price;
    public int quantity;
    public String company;
    public String description;

    public Product() {}  // חובה עבור Firebase

    public Product(String productId, String name, double price, int quantity, String company, String description) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.company = company;
        this.description = description;
    }
}
