package org.delcom.app.controllers;

import org.delcom.app.entities.Product;
import org.delcom.app.services.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/products") // URL aksesnya localhost:8080/products
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", service.findAll());
        return "product_index"; // Mengarah ke product_index.html
    }

    @GetMapping("/add")
    public String add(Model model) {
        model.addAttribute("product", new Product());
        return "product_form"; // Mengarah ke product_form.html
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product, @RequestParam("image") MultipartFile file) {
        try {
            service.saveProduct(product, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "redirect:/products";
    }
}