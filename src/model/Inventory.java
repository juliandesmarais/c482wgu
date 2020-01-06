package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Inventory {
    private ObservableList<Part> allParts = FXCollections.observableArrayList();
    private ObservableList<Product> allProducts = FXCollections.observableArrayList();

    public void addPart(Part newPart) {
        allParts.add(newPart);
    }

    public void addProduct(Product newProduct) {
        allProducts.add(newProduct);
    }

    public Part lookupPart(int partId) {
        for (Part part: allParts) {
            if (part.getId() == partId) {
                return part;
            }
        }

        return null;
    }

    public Product lookupProduct(int productId) {
        for (Product product: allProducts) {
            if (product.getId() == productId) {
                return product;
            }
        }

        return null;
    }

    public ObservableList<Part> lookupPart(String partName) {
        ObservableList<Part> matchingParts = FXCollections.observableArrayList();

        for (Part part: allParts) {
            if (part.getName().toLowerCase().contains(partName.toLowerCase())) {
                matchingParts.add(part);
            }
        }

        return matchingParts;
    }

    public ObservableList<Product> lookupProduct(String productName) {
        ObservableList<Product> matchingProducts = FXCollections.observableArrayList();

        for (Product product: allProducts) {
            if (product.getName().toLowerCase().contains(productName.toLowerCase())) {
                matchingProducts.add(product);
            }
        }

        return matchingProducts;
    }

    public void updatePart(int index, Part selectedPart) {
        allParts.set(index, selectedPart);
    }

    public void updateProduct(int index, Product newProduct) {
        allProducts.set(index, newProduct);
    }

    public boolean deletePart(Part selectedPart) {
        if (!allParts.contains(selectedPart)) {
            return false;
        }

        allParts.remove(selectedPart);
        return true;
    }

    public boolean deleteProduct(Product selectedProduct) {
        if (!allProducts.contains(selectedProduct)) {
            return false;
        }

        allProducts.remove(selectedProduct);
        return true;
    }

    public ObservableList<Part> getAllParts() {
        return allParts;
    }

    public ObservableList<Product> getAllProducts() {
        return allProducts;
    }

    public Integer getLastPartId() {
        return getAllParts().size();
    }

    public Integer getLastProductId() {
        return getAllProducts().size();
    }

}
